# Credit Refinancing BFF - Manual Testing Guide

## 🚀 Como testar a aplicação

### 1. Validação do Projeto
✅ **Estrutura do projeto:** Completa  
✅ **Classes compiladas:** Presentes em `bff/target/classes/`  
✅ **Configurações:** application.yml configurado  
✅ **Testes unitários:** Implementados  

### 2. Para executar a aplicação

#### Pré-requisitos:
- Java 17+ ✅ (Verificado)
- Maven 3.8+ (Instalar se necessário)

#### Comandos para executar:

```bash
# Navegar para o módulo BFF
cd bff

# Executar a aplicação (se Maven estiver instalado)
mvn spring-boot:run

# OU executar usando Java diretamente (alternativa)
java -cp "target/classes:target/lib/*" com.creditrefinancing.bff.CreditRefinancingBffApplication
```

### 3. Endpoints disponíveis

#### Base URL: `http://localhost:8080/api`

#### 🔍 Health Check
```bash
GET /simulation/health
# Resposta: "Simulation service is running"
```

#### 💰 Calcular Simulação
```bash
POST /simulation/calculate
Content-Type: application/json

{
  "customer_id": "CUST-12345",
  "current_loan_amount": 150000.00,
  "current_monthly_payment": 1200.50,
  "desired_loan_amount": 200000.00,
  "desired_term_months": 240,
  "loan_type": "MORTGAGE",
  "monthly_income": 5000.00,
  "credit_score": 720
}
```

#### 📋 Buscar Simulação
```bash
GET /simulation/{simulationId}
# Exemplo: GET /simulation/SIM-12345678
```

### 4. Documentação da API

#### Swagger UI: 
`http://localhost:8080/api/swagger-ui.html`

#### OpenAPI JSON:
`http://localhost:8080/api/v3/api-docs`

### 5. Monitoramento

#### Health Check:
`http://localhost:8080/api/health`

#### Metrics:
`http://localhost:8080/api/metrics`

#### Info:
`http://localhost:8080/api/info`

### 6. Exemplo completo com curl

```bash
# 1. Health Check
curl -X GET http://localhost:8080/api/simulation/health

# 2. Calcular simulação
curl -X POST http://localhost:8080/api/simulation/calculate \
  -H "Content-Type: application/json" \
  -d '{
    "customer_id": "CUST-12345",
    "current_loan_amount": 150000.00,
    "current_monthly_payment": 1200.50,
    "desired_loan_amount": 200000.00,
    "desired_term_months": 240,
    "loan_type": "MORTGAGE",
    "monthly_income": 5000.00,
    "credit_score": 720
  }'

# 3. Buscar simulação (usar ID retornado da resposta anterior)
curl -X GET http://localhost:8080/api/simulation/{simulation_id}
```

### 7. Estrutura da Resposta

A resposta da simulação inclui:
- ✅ Cálculos financeiros detalhados
- ✅ Análise de risco completa
- ✅ Preview da tabela de pagamentos
- ✅ Métricas de comparação
- ✅ Próximos passos recomendados
- ✅ Condições específicas
- ✅ Tempo de processamento

### 8. Próximos passos para desenvolvimento

1. **Instalar Maven** para facilitar execução
2. **Criar microsserviços** (simulação, proposta, etc.)
3. **Implementar Gateway** API
4. **Adicionar autenticação** e autorização
5. **Configurar Docker** para containerização
6. **Implementar CI/CD** pipeline

---

## 🎯 Status do Projeto

**BFF (Backend for Frontend):** ✅ **COMPLETO**

### Implementado:
- [x] Estrutura base Spring Boot
- [x] Controllers reativos com WebFlux
- [x] Validação robusta de requests
- [x] DTOs completos com documentação
- [x] Service com lógica de negócio avançada
- [x] Tratamento global de exceções
- [x] Configuração WebClient para microsserviços
- [x] Testes unitários abrangentes
- [x] Documentação OpenAPI/Swagger
- [x] Health checks e monitoramento

### Próximos módulos:
- [ ] Simulation Service (microsserviço)
- [ ] Proposal Service (microsserviço) 
- [ ] Formalization Service (microsserviço)
- [ ] After Sales Service (microsserviço)
- [ ] API Gateway
- [ ] Docker Compose
