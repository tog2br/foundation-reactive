# Foundation - Comparação de Performance: Undertow vs Netty

Este projeto compara a performance entre duas abordagens diferentes para servidores Kotlin com corrotinas:

1. **foundation-undertow-bloqueante**: Servidor Undertow com Feign Client e Spring Data JPA
2. **foundation-netty-reativo**: Servidor Netty com WebClient reativo e R2DBC

## Arquitetura

### Servidor Undertow (Bloqueante)
- **Servidor**: Undertow
- **Cliente HTTP**: Feign Client
- **Banco de Dados**: Spring Data JPA + H2
- **Padrão**: Bloqueante com corrotinas

### Servidor Netty (Reativo)
- **Servidor**: Netty (via Spring WebFlux)
- **Cliente HTTP**: WebClient reativo
- **Banco de Dados**: R2DBC + H2
- **Padrão**: Reativo não-bloqueante

## Endpoints

Ambos os servidores expõem os mesmos endpoints:

### GET /api/planets
- **Undertow**: Busca dados de https://swapi.dev/api/planets/1/
- **Netty**: Busca dados de https://swapi.dev/api/planets/1/

### PUT /api/film
- **Undertow**: Insere filme no banco H2 via JPA
- **Netty**: Insere filme no banco H2 via R2DBC

### GET /api/metrics
- Retorna métricas de performance do servidor

## Métricas Coletadas

- **RPS**: Requisições por segundo
- **Latência Média**: Tempo médio de resposta em ms
- **P95 Latência**: Percentil 95 de latência em ms
- **Memória**: Uso de memória em MB
- **CPU Médio**: Percentual médio de uso de CPU
- **Threads**: Número de threads ativas

## Como Executar

### Pré-requisitos
- Java 17+
- Gradle 7+

### 1. Compilar os Projetos

```bash
# Compilar servidor Undertow
cd foundation-undertow-bloqueante
./gradlew build

# Compilar servidor Netty
cd ../foundation-netty-reativo
./gradlew build

# Compilar teste de carga
cd ../load-test
./gradlew build
```

### 2. Executar os Servidores

**Terminal 1 - Servidor Undertow (porta 8080):**
```bash
cd foundation-undertow-bloqueante
./gradlew bootRun
```

**Terminal 2 - Servidor Netty (porta 8081):**
```bash
cd foundation-netty-reativo
./gradlew bootRun
```

### 3. Executar Teste de Carga

**Terminal 3 - Teste de Carga:**
```bash
cd load-test
./gradlew run
```

O teste de carga irá:
- Fazer 200 requisições por segundo
- Durante 30 segundos
- Testar ambos os endpoints (/planets e /film) em cada servidor
- Alternando entre os dois servidores
- Coletar e exibir métricas comparativas em formato de tabela (incluindo threads)

## Scripts de Execução

### Executar Tudo (Linux/Mac)
```bash
chmod +x run-all.sh
./run-all.sh
```

### Executar Tudo (Windows)
```cmd
run-all.bat
```

## Estrutura do Projeto

```
foundation/
├── foundation-undertow-bloqueante/    # Servidor Undertow
│   ├── src/main/kotlin/
│   │   └── com/foundation/undertow/
│   │       ├── controller/            # Controllers REST
│   │       ├── service/               # Lógica de negócio
│   │       ├── repository/            # Repositórios JPA
│   │       ├── entity/                # Entidades JPA
│   │       ├── dto/                   # DTOs
│   │       └── client/                # Feign Client
│   └── build.gradle.kts
├── foundation-netty-reativo/          # Servidor Netty
│   ├── src/main/kotlin/
│   │   └── com/foundation/netty/
│   │       ├── controller/            # Controllers REST
│   │       ├── service/               # Lógica de negócio
│   │       ├── repository/            # Repositórios R2DBC
│   │       ├── entity/                # Entidades R2DBC
│   │       └── dto/                   # DTOs
│   └── build.gradle.kts
├── load-test/                         # Teste de Carga
│   ├── src/main/kotlin/
│   │   └── com/foundation/loadtest/
│   │       ├── client/                # Cliente HTTP
│   │       ├── service/               # Serviços de teste
│   │       └── model/                 # Modelos
│   └── build.gradle.kts
└── README.md
```

## Análise de Performance

O teste de carga irá gerar métricas comparativas entre os dois servidores, permitindo analisar:

1. **Throughput**: Qual servidor processa mais requisições por segundo
2. **Latência**: Qual servidor responde mais rapidamente
3. **Uso de Recursos**: Consumo de memória, CPU e threads
4. **Escalabilidade**: Como cada servidor se comporta sob carga

## Configurações

### Undertow
- Porta: 8080
- Threads Worker: 200
- Threads IO: 4

### Netty
- Porta: 8081
- Configuração padrão do Spring WebFlux

### Teste de Carga
- Target RPS: 200
- Duração: 30 segundos
- Endpoints: /planets e /film (alternando)
- Intervalo entre requisições: 5ms
- Saída: Tabela formatada com métricas comparativas (incluindo threads)

## Troubleshooting

### Porta já em uso
```bash
# Verificar processos usando as portas
lsof -i :8080
lsof -i :8081

# Matar processos se necessário
kill -9 <PID>
```

### Erro de compilação
```bash
# Limpar e recompilar
./gradlew clean build
```

### Servidor não responde
- Verificar se os servidores estão rodando nas portas corretas
- Verificar logs de erro
- Aguardar inicialização completa (5-10 segundos)
