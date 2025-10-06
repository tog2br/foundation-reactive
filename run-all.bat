@echo off
echo === FOUNDATION - COMPARACAO DE PERFORMANCE ===
echo Iniciando servidores e teste de carga...
echo.

REM Função para verificar se uma porta está em uso
:check_port
set port=%1
netstat -an | find ":%port% " | find "LISTENING" >nul
if %errorlevel% == 0 (
    echo Porta %port% ja esta em uso. Matando processos...
    for /f "tokens=5" %%a in ('netstat -ano ^| find ":%port% " ^| find "LISTENING"') do taskkill /F /PID %%a >nul 2>&1
    timeout /t 2 >nul
)
goto :eof

REM Função para aguardar servidor estar pronto
:wait_for_server
set port=%1
set name=%2
set max_attempts=30
set attempt=0

echo Aguardando %name% estar pronto na porta %port%...
:wait_loop
if %attempt% geq %max_attempts% (
    echo Erro: %name% nao ficou pronto em 30 segundos
    exit /b 1
)

curl -s http://localhost:%port%/api/metrics >nul 2>&1
if %errorlevel% == 0 (
    echo %name% esta pronto!
    exit /b 0
)

timeout /t 1 >nul
set /a attempt+=1
goto wait_loop

REM Limpar portas se necessário
call :check_port 8080
call :check_port 8081

REM Compilar projetos
echo Compilando projetos...
cd foundation-undertow-bloqueante
call gradlew build -q
if %errorlevel% neq 0 (
    echo Erro ao compilar foundation-undertow-bloqueante
    exit /b 1
)

cd ..\foundation-netty-reativo
call gradlew build -q
if %errorlevel% neq 0 (
    echo Erro ao compilar foundation-netty-reativo
    exit /b 1
)

cd ..\load-test
call gradlew build -q
if %errorlevel% neq 0 (
    echo Erro ao compilar load-test
    exit /b 1
)

cd ..

REM Iniciar servidor Undertow em background
echo Iniciando servidor Undertow...
cd foundation-undertow-bloqueante
start /B gradlew bootRun > ..\undertow.log 2>&1
cd ..

REM Iniciar servidor Netty em background
echo Iniciando servidor Netty...
cd foundation-netty-reativo
start /B gradlew bootRun > ..\netty.log 2>&1
cd ..

REM Aguardar servidores estarem prontos
call :wait_for_server 8080 "Undertow"
if %errorlevel% neq 0 exit /b 1

call :wait_for_server 8081 "Netty"
if %errorlevel% neq 0 exit /b 1

echo.
echo Ambos os servidores estao prontos!
echo Iniciando teste de carga em 5 segundos...
timeout /t 5 >nul

REM Executar teste de carga
echo === EXECUTANDO TESTE DE CARGA ===
cd load-test
call gradlew run
set LOAD_TEST_EXIT_CODE=%errorlevel%
cd ..

echo.
echo === FINALIZANDO SERVIDORES ===

REM Parar servidores (Windows não tem kill direto, então vamos usar taskkill)
for /f "tokens=2" %%a in ('tasklist /FI "IMAGENAME eq java.exe" /FO CSV ^| find "java.exe"') do (
    taskkill /F /PID %%a >nul 2>&1
)

echo.
echo === TESTE CONCLUIDO ===
echo Logs do Undertow: undertow.log
echo Logs do Netty: netty.log
echo.

if %LOAD_TEST_EXIT_CODE% == 0 (
    echo ✅ Teste executado com sucesso!
) else (
    echo ❌ Teste falhou com codigo de saida: %LOAD_TEST_EXIT_CODE%
)

exit /b %LOAD_TEST_EXIT_CODE%
