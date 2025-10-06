#!/bin/bash

echo "=== FOUNDATION - COMPARAÇÃO DE PERFORMANCE ==="
echo "Iniciando servidores e teste de carga..."
echo "Teste: 50 requisições concorrentes por 30 segundos (planets + film)"
echo ""

# Função para verificar se uma porta está em uso
check_port() {
    local port=$1
    if lsof -Pi :$port -sTCP:LISTEN -t >/dev/null ; then
        echo "Porta $port já está em uso. Matando processo..."
        lsof -ti:$port | xargs kill -9 2>/dev/null || true
        sleep 2
    fi
}

# Função para aguardar servidor estar pronto
wait_for_server() {
    local port=$1
    local name=$2
    local max_attempts=60
    local attempt=0
    
    echo "Aguardando $name estar pronto na porta $port..."
    while [ $attempt -lt $max_attempts ]; do
        if curl -s http://localhost:$port/api/planets >/dev/null 2>&1; then
            echo "$name está pronto!"
            return 0
        fi
        sleep 1
        attempt=$((attempt + 1))
    done
    
    echo "Erro: $name não ficou pronto em 60 segundos"
    return 1
}

# Limpar portas se necessário
check_port 8080
check_port 8081

# Compilar projetos
echo "Compilando projetos..."
cd foundation-undertow-bloqueante
./gradlew build -q
if [ $? -ne 0 ]; then
    echo "Erro ao compilar foundation-undertow-bloqueante"
    exit 1
fi

cd ../foundation-netty-reativo
./gradlew build -q
if [ $? -ne 0 ]; then
    echo "Erro ao compilar foundation-netty-reativo"
    exit 1
fi

cd ../load-test
./gradlew build -q
if [ $? -ne 0 ]; then
    echo "Erro ao compilar load-test"
    exit 1
fi

cd ..

# Iniciar servidor Undertow em background
echo "Iniciando servidor Undertow..."
cd foundation-undertow-bloqueante
./gradlew bootRun > ../undertow.log 2>&1 &
UNDERPOW_PID=$!
cd ..

# Iniciar servidor Netty em background
echo "Iniciando servidor Netty..."
cd foundation-netty-reativo
./gradlew bootRun > ../netty.log 2>&1 &
NETTY_PID=$!
cd ..

# Aguardar servidores estarem prontos
wait_for_server 8080 "Undertow" || exit 1
wait_for_server 8081 "Netty" || exit 1

echo ""
echo "Ambos os servidores estão prontos!"
echo "Iniciando teste de carga em 5 segundos..."
sleep 5

# Executar teste de carga
echo "=== EXECUTANDO TESTE DE CARGA ==="
cd load-test
./gradlew run
LOAD_TEST_EXIT_CODE=$?
cd ..

echo ""
echo "=== FINALIZANDO SERVIDORES ==="

# Parar servidores
kill $UNDERPOW_PID 2>/dev/null || true
kill $NETTY_PID 2>/dev/null || true

# Aguardar processos terminarem
sleep 3

# Verificar se ainda estão rodando e forçar parada se necessário
if kill -0 $UNDERPOW_PID 2>/dev/null; then
    echo "Forçando parada do Undertow..."
    kill -9 $UNDERPOW_PID 2>/dev/null || true
fi

if kill -0 $NETTY_PID 2>/dev/null; then
    echo "Forçando parada do Netty..."
    kill -9 $NETTY_PID 2>/dev/null || true
fi

echo ""
echo "=== TESTE CONCLUÍDO ==="
echo "Logs do Undertow: undertow.log"
echo "Logs do Netty: netty.log"
echo ""

if [ $LOAD_TEST_EXIT_CODE -eq 0 ]; then
    echo "✅ Teste executado com sucesso!"
else
    echo "❌ Teste falhou com código de saída: $LOAD_TEST_EXIT_CODE"
fi

exit $LOAD_TEST_EXIT_CODE
