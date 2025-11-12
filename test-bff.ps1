# Script para testar o BFF
Write-Host "=== TESTE DO BFF ===" -ForegroundColor Green

# 1. Verificar se Java está disponível
Write-Host "1. Verificando Java..." -ForegroundColor Yellow
java -version
if ($LASTEXITCODE -ne 0) {
    Write-Host "ERRO: Java não encontrado!" -ForegroundColor Red
    exit 1
}

# 2. Configurar JAVA_HOME
$env:JAVA_HOME = "C:\Program Files\Java\jdk-24"
Write-Host "JAVA_HOME configurado: $env:JAVA_HOME" -ForegroundColor Cyan

# 3. Verificar se o BFF está rodando
Write-Host "2. Verificando se BFF está rodando..." -ForegroundColor Yellow
$javaProcess = Get-Process -Name "java" -ErrorAction SilentlyContinue
if ($javaProcess) {
    Write-Host "✓ Processo Java encontrado (PID: $($javaProcess.Id))" -ForegroundColor Green
} else {
    Write-Host "⚠ Nenhum processo Java rodando" -ForegroundColor Yellow
}

# 4. Verificar porta 8080
Write-Host "3. Verificando porta 8080..." -ForegroundColor Yellow
$port8080 = netstat -ano | findstr :8080
if ($port8080) {
    Write-Host "✓ Porta 8080 em uso:" -ForegroundColor Green
    Write-Host $port8080
} else {
    Write-Host "⚠ Porta 8080 não está em uso" -ForegroundColor Yellow
}

# 5. Testar Health Check
Write-Host "4. Testando Health Check..." -ForegroundColor Yellow
try {
    $healthResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/simulations/health" -Method GET -TimeoutSec 5
    Write-Host "✓ Health Check OK:" -ForegroundColor Green
    Write-Host ($healthResponse | ConvertTo-Json -Compress)
} catch {
    Write-Host "⚠ Health Check falhou: $($_.Exception.Message)" -ForegroundColor Yellow
}

# 6. Testar Swagger UI
Write-Host "5. Testando Swagger UI..." -ForegroundColor Yellow
try {
    $swaggerResponse = Invoke-WebRequest -Uri "http://localhost:8080/api/swagger-ui.html" -Method GET -TimeoutSec 5
    if ($swaggerResponse.StatusCode -eq 200) {
        Write-Host "✓ Swagger UI disponível" -ForegroundColor Green
    }
} catch {
    Write-Host "⚠ Swagger UI não disponível: $($_.Exception.Message)" -ForegroundColor Yellow
}

# 7. Testar endpoint de simulação
Write-Host "6. Testando endpoint de simulação..." -ForegroundColor Yellow
$testPayload = @{
    personalInfo = @{
        cpf = "123.456.789-00"
        fullName = "João Silva"
        birthDate = "1985-05-15"
        email = "joao.silva@email.com"
        phone = "+5511999999999"
    }
    financialInfo = @{
        monthlyIncome = 8000.00
        existingDebts = 2500.00
        currentLoanBalance = 150000.00
        currentMonthlyPayment = 1800.00
        propertyValue = 400000.00
    }
    loanRequest = @{
        requestedAmount = 100000.00
        termInMonths = 240
        loanType = "REFINANCING"
    }
} | ConvertTo-Json -Depth 3

try {
    $simulationResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/simulations/calculate" -Method POST -Body $testPayload -ContentType "application/json" -TimeoutSec 10
    Write-Host "✓ Simulação executada com sucesso:" -ForegroundColor Green
    Write-Host ($simulationResponse | ConvertTo-Json -Compress)
} catch {
    Write-Host "⚠ Simulação falhou: $($_.Exception.Message)" -ForegroundColor Yellow
}

Write-Host "=== TESTE CONCLUÍDO ===" -ForegroundColor Green
