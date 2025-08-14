# Backend Challenge

## Descrição

Este é um projeto Spring Boot que implementa um sistema de **validação de JWT (JSON Web Tokens)** com validações específicas para claims personalizados. A **função principal da API é validar tokens JWT** e retornar um boolean indicando se são válidos ou não.

### Premissas Assumidas

**Importante**: Conforme especificado no desafio, o JWT é recebido "por parâmetros" e retorna um boolean. Considerando que:

1. **Input**: JWT como string via parâmetros
2. **Output**: Boolean indicando validade (true/false)
3. **Simplicidade**: Resposta direta sem complexidade adicional

**Decisão tomada**: O JWT é recebido via **query parameter** na URL (`/api/validate?token={{jwt}}`).

## Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.5.4**
- **Maven 3.9.8+**
- **Auth0 JWT Library 4.4.0**
- **JUnit 5** (para testes)
- **SLF4J + Logback** (para logging)
- **Swagger** (documentação automática da API)
- **Docker**
- **GitHub Actions** (CI/CD)

## Funcionalidade Principal: Validação de JWT

### Endpoint Principal

#### GET /api/validate

Valida um JWT token recebido via query parameter.

**Parâmetros:**
- `token` (query parameter): O JWT token a ser validado

**Respostas:**
- **200 OK**: Token válido, retorna `true`
- **400 Bad Request**: Token malformado ou inválido, retorna `false`
- **422 Unprocessable Entity**: Claims inválidos, retorna `false`

**Exemplo de uso:**
```bash
GET /api/validate?token={{token}}
```

**Exemplos de resposta:**
```json
// Token válido
true

// Token inválido
false
```

### Claims Validados

#### Name Claim
- Deve estar presente no JWT
- Máximo de 256 caracteres
- Não pode conter números

#### Role Claim
- Deve estar presente no JWT
- Deve ser um dos valores válidos: "Admin", "Member", "External"

#### Seed Claim
- Deve estar presente no JWT
- Deve ser um número inteiro válido
- Deve ser um número primo

### Exemplo de JWT Válido

Para testar a API, você pode usar um JWT com a seguinte estrutura:

```json
{
  "Role": "Admin",
  "Seed": "7841",
  "Name": "Toninho Araujo"
}
```

**Observações:**
- O nome não pode conter números
- O role deve ser "Admin", "Member" ou "External"
- O seed deve ser um número primo
- O token deve conter exatamente 3 claims

## Arquitetura

### Princípios SOLID

O projeto implementa os princípios SOLID através de:

- **Interface `ClaimValidator`**: Define o contrato para validadores
- **Implementações específicas**: Cada tipo de claim tem seu próprio validador
- **Extensibilidade**: Novos validadores podem ser adicionados sem modificar código existente
- **`JwtValidationConfig`**: Centraliza a configuração de validadores ativos e roles válidos
- **Open/Closed Principle**: Sistema aberto para extensão, fechado para modificação
- **Single Responsibility**: Cada classe tem uma responsabilidade específica
- **Dependency Inversion**: Dependências são injetadas via construtor

### Padrões de Design

- **Strategy Pattern**: Diferentes estratégias de validação implementam a mesma interface
- **Factory Pattern**: `JwtValidationConfig` atua como factory para validadores
- **Template Method**: Estrutura comum de validação definida em `JwtValidator`
- **Exception Handler Pattern**: Tratamento centralizado de exceções
- **Observer Pattern**: Sistema de métricas e monitoring
- **Builder Pattern**: Construção de spans e traces
- **AutoCloseable Pattern**: `TraceSpan` implementa AutoCloseable para gerenciamento automático de recursos

### Estrutura de Pacotes

```
src/main/java/br/dev/viniciusleonel/backend_challenge/
├── controller/          # Controladores REST
│   ├── ApiController.java
│   └── MonitoringController.java
├── infra/               # Infraestrutura da aplicação
│   ├── exception/       # Tratamento de exceções
│   │   ├── handler/     # Handlers globais de exceção
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── CollectCurrentTraceException.java
│   │   ├── CollectEndpointTraceException.java
│   │   ├── CollectMetricsException.java
│   │   ├── HealthCheckException.java
│   │   ├── InvalidClaimException.java
│   │   └── ResetMetricsException.java
│   └── observability/   # Sistema de observabilidade
│       ├── config/      # Configurações da aplicação
│       │   ├── WebConfig.java
│       │   └── ObservabilityConfig.java
│       ├── logging/     # Sistema de logging
│       │   └── LoggingInterceptor.java
│       ├── monitoring/  # Sistema de monitoring
│       │   ├── MetricsCollector.java
│       │   └── MonitorHealth.java
│       └── tracing/     # Sistema de tracing distribuído
│           ├── TraceContext.java
│           ├── TraceMetrics.java
│           └── TraceSpan.java
├── utils/               # Utilitários (geração de JWT, validação de números)
├── validators/          # Validadores de claims
└── BackendChallengeApplication.java
```

## Tratamento de Exceções

A aplicação possui um sistema robusto de tratamento de exceções:

### Exceções Personalizadas

#### Exceções de Validação
- **`InvalidClaimException`**: Claims inválidos detectados

#### Exceções de Observabilidade
- **`CollectCurrentTraceException`**: Erro ao coletar informações de tracing atual
- **`CollectEndpointTraceException`**: Erro ao coletar traces por endpoint
- **`CollectMetricsException`**: Erro ao coletar métricas
- **`ResetMetricsException`**: Erro ao resetar métricas
- **`HealthCheckException`**: Erro no health check

### GlobalExceptionHandler

- **InvalidClaimException**: Retorna status 422 (Unprocessable Entity) para claims inválidos
- **JWTDecodeException**: Retorna status 400 (Bad Request) para tokens malformados

### Tipos de Erro

1. **Claims Inválidos**: Nome com números, role inválido, seed não primo
2. **Token Malformado**: Formato JWT inválido
3. **Claims Ausentes**: Número incorreto de claims (deve ser exatamente 3)
4. **Erros de Observabilidade**: Falhas na coleta de métricas e tracing

## Como Executar

### Pré-requisitos

- Java 21
- Maven 3.9.8+
- Docker (opcional)

### Execução Local

1. **Clone o repositório:**
```bash
git clone https://github.com/viniciusleonel/backend-challenge
cd backend-challenge
```

2. **Compile o projeto:**
```bash
mvn clean compile
```

3. **Execute a aplicação:**
```bash
mvn spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`

### Execução com Docker

1. **Construa a imagem:**
```bash
docker build -t backend-challenge .
```

2. **Execute o container:**
```bash
docker run --name backend-challenge -p 8080:8080 backend-challenge
```

## Testes

Execute os testes unitários:

```bash
mvn test
```

### Cobertura de Testes

O projeto inclui testes abrangentes para:

#### Testes de Validação
- **`JwtValidatorTest`**: Validação completa de tokens
- **`NameValidatorTest`**: Validação de claims de nome
- **`RoleValidatorTest`**: Validação de claims de role
- **`SeedValidatorTest`**: Validação de claims de seed

#### Testes de Controller
- **`ApiControllerTest`**: Endpoints da API
- **`MonitoringControllerTest`**: Endpoints de monitoring

#### Testes de Observabilidade
- **`TraceContextTest`**: Validação de geração de traces e spans
- **`TraceSpanTest`**: Validação de operações de spans e tags
- **`MetricsCollectorTest`**: Validação de coleta e agregação de métricas

### Estrutura de Testes

```
src/test/java/br/dev/viniciusleonel/backend_challenge/
├── infra/
│   └── observability/
│       ├── tracing/
│       │   ├── TraceContextTest.java
│       │   └── TraceSpanTest.java
│       └── monitoring/
│           └── MetricsCollectorTest.java
├── validators/
│   ├── JwtValidatorTest.java
│   ├── NameValidatorTest.java
│   ├── RoleValidatorTest.java
│   └── SeedValidatorTest.java
└── controller/
    ├── ApiControllerTest.java
    └── MonitoringControllerTest.java
```

## CI/CD

O projeto utiliza GitHub Actions para automatizar o processo de integração e entrega contínua.

### Pipeline CI (Continuous Integration)

O workflow `ci.yml` é executado em:
- Push para a branch `main`
- Pull requests para a branch `main`

**Etapas do CI:**
1. **Checkout do código** - Baixa o código fonte
2. **Configuração do JDK 21** - Configura o ambiente Java com Temurin
3. **Compilação e testes** - Executa `mvn clean verify`

### Pipeline CD (Continuous Deployment)

O workflow `cd.yml` é executado automaticamente após o sucesso do CI e:
- Constrói a imagem Docker
- Faz push para o Docker Hub
- Utiliza secrets configurados para autenticação

**Etapas do CD:**
1. **Setup Docker Buildx** - Configura o ambiente Docker
2. **Login no Docker Hub** - Autentica usando secrets
3. **Build e Push** - Constrói e envia a imagem para o registry

**Arquivos de configuração:**
- `.github/workflows/ci.yml` - Pipeline de integração contínua
- `.github/workflows/cd.yml` - Pipeline de entrega contínua

**Secrets necessários:**
- `DOCKERHUB_USERNAME` - Nome de usuário do Docker Hub
- `DOCKERHUB_TOKEN` - Token de acesso do Docker Hub

## Configuração

As configurações da aplicação estão em `src/main/resources/application.properties`:

```properties
spring.application.name=backend-challenge

# Nível de log global
logging.level.root=INFO

# Nível de log específico para o pacote da aplicação
logging.level.br.dev.viniciusleonel.backend_challenge=DEBUG

# Formato de saída dos logs com MDC, Tracing e Monitoring
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} [%X{requestId}] [%X{endpoint}] [%X{traceId}] [%X{spanId}] [%X{operationName}] [%X{duration}ms] - %msg%n
```

## Utilitários

### JwtGenerator

Classe utilitária para gerar tokens JWT de teste:
```java
String token = JwtGenerator.generateJwtToken("Nome", "Admin", "7841");
```

### NumberUtils

Utilitário para validação de números primos:
```java
boolean isPrime = NumberUtils.isPrime(7841); // true
```

## Decisões de Implementação

### JWT via Query Parameter

**Decisão**: O JWT é recebido via query parameter (`?token={{jwt}}`) em vez de header de autorização, body ou path parameter.

**Justificativa**:
- **Conformidade com o Enunciado**: O desafio especifica "por parâmetros" sem mencionar body, bearer authorization ou headers
- **Clareza da Especificação**: Não há ambiguidade sobre usar body (POST) ou bearer authorization
- **Simplicidade**: Adequado para o output booleano simples
- **Testabilidade**: Facilita testes diretos via navegador ou ferramentas simples
- **Flexibilidade**: Não requer configuração de headers de autorização
- **Validação Rápida**: Permite verificação direta de tokens sem setup adicional

**Alternativas Consideradas e Rejeitadas**:
- **Header Authorization (Bearer)**: Mais padrão para JWT, mas **não foi especificado no desafio**
- **Body POST**: Adequado para dados complexos, mas **não foi mencionado no enunciado**
- **Path Parameter**: Menos flexível e mais restritivo
- **Query Parameter**: **Única opção mencionada no desafio** ("por parâmetros")**

### Output Boolean

**Decisão**: Retorno direto como boolean (true/false) em vez de objeto JSON estruturado.

**Justificativa**:
- **Simplicidade**: Resposta direta e clara
- **Conformidade**: O desafio especifica "boolean indicando se é válido"
- **Eficiência**: Menos overhead de serialização/deserialização
- **Clareza**: Resposta imediata sem necessidade de parsing

## Contribuição

Para adicionar novos validadores:

1. Implemente a interface `ClaimValidator`
2. Adicione o novo validador em `JwtValidationConfig.getValidators()`
3. Implemente os testes correspondentes
4. Atualize a documentação

Para adicionar novos roles:

1. Adicione o novo role na lista `VALID_ROLES` em `JwtValidationConfig.java`
2. Implemente os testes correspondentes
3. Atualize a documentação

Para adicionar novas métricas:

1. Implemente o método de coleta em `MetricsCollector`
2. Adicione o endpoint correspondente em `MonitoringController`
3. Implemente os testes correspondentes
4. Atualize a documentação

Para adicionar novas exceções:

1. Crie a classe de exceção estendendo `RuntimeException`
2. Implemente o tratamento no `GlobalExceptionHandler`
3. Implemente os testes correspondentes
4. Atualize a documentação

### Estrutura de um Validador

```java
public class CustomValidator implements ClaimValidator {
    @Override
    public boolean validate(DecodedJWT jwt) {
        // Lógica de validação
        // Lance InvalidClaimException para erros
        return true;
    }
}
```

### Estrutura de uma Nova Métrica

```java
// Em MetricsCollector
public void recordCustomMetric(String metricName, Object value) {
    // Implementação da coleta
}

// Em MonitoringController
@GetMapping("/custom-metric")
public ResponseEntity<Object> getCustomMetric() {
    // Implementação do endpoint
}
```

### Estrutura de uma Nova Exceção

```java
public class CustomException extends RuntimeException {
    public CustomException(String message) {
        super(message);
    }
}
```

## Roadmap de Evolução

### **Fase 1: Implementação Básica** ✅
- ✅ Validação de JWT com claims personalizados
- ✅ Arquitetura SOLID com validadores extensíveis
- ✅ Tratamento robusto de exceções
- ✅ Logging estruturado com MDC
- ✅ Sistema de tracing básico com spans
- ✅ Métricas de performance básicas
- ✅ Endpoints de monitoring
- ✅ Pipelines CI/CD com GitHub Actions

### **Fase 2: Observabilidade Avançada** ✅
- ✅ Tracing distribuído completo com hierarquia de spans
- ✅ Sistema de monitoring em tempo real com métricas thread-safe
- ✅ Métricas detalhadas de performance e negócio
- ✅ Configuração automática de observabilidade
- ✅ Logging aprimorado com contexto completo
- ✅ Exceções personalizadas para diferentes tipos de erro
- ✅ Health checks com contexto de tracing
- ✅ Sistema de métricas com agregação automática

### **Fase 3: Integração Externa** 🔄
- 🔄 Integração com Jaeger para visualização de traces
- 🔄 Dashboards Grafana para métricas
- 🔄 Alertas automáticos baseados em métricas
- 🔄 Métricas customizadas para negócio

### **Fase 4: Observabilidade de Produção** 📋
- 📋 OpenTelemetry para padrão aberto
- 📋 Distributed tracing entre serviços
- 📋 Service mesh integration
- 📋 AI-powered anomaly detection
- 📋 SLA/SLO monitoring

## Observabilidade

A aplicação implementa um sistema completo de observabilidade que vai além do logging básico, fornecendo insights profundos sobre o comportamento e performance do sistema.

### Componentes de Observabilidade

#### 1. Logging Estruturado
- **Formato consistente** com timestamp, thread, nível e contexto
- **Níveis apropriados** (INFO, DEBUG, ERROR) para diferentes cenários
- **Contexto rico** com informações de requisição, endpoint, tracing e performance

#### 2. Correlação de Requisições
- **RequestID único** para cada requisição HTTP
- **Contexto compartilhado** entre todos os componentes da aplicação
- **Rastreabilidade completa** do fluxo de validação

#### 3. Tracing Distribuído
- **TraceID único** para cada requisição
- **Spans hierárquicos** para operações específicas
- **Contexto de operação** com nomes descritivos
- **Relacionamento pai-filho** entre spans
- **Tags personalizadas** para contexto de negócio e segurança

#### 4. Tratamento de Exceções
- **Logging detalhado** de todas as exceções
- **Contexto preservado** mesmo em situações de erro
- **Stack traces** para debugging eficiente
- **Exceções personalizadas** para diferentes tipos de erro

#### 5. Monitoring em Tempo Real
- **Métricas de performance** por endpoint
- **Estatísticas de negócio** (JWT, claims)
- **Health checks** com contexto de tracing
- **Métricas de infraestrutura** e recursos
- **Agregação automática** de métricas

### Sistema de Tracing Distribuído

A aplicação implementa um sistema completo de **Distributed Tracing** que rastreia o fluxo de cada requisição através de todos os componentes.

#### Conceitos de Tracing

- **Trace**: Representa uma única requisição HTTP completa
- **Span**: Representa uma operação específica dentro de um trace
- **Context**: Informações passadas entre spans (TraceID, SpanID, ParentSpanID)
- **Tags**: Metadados associados a cada span
- **Metrics**: Medições de performance e negócio

#### Hierarquia de Spans

```
Trace: a1b2c3d4e5f6g7h8
├── Span 1: Recepção da requisição (Controller)
│   ├── Span 2: Validação JWT
│   │   ├── Span 3: Decodificação JWT
│   │   ├── Span 4: Validação de claims
│   │   │   ├── Span 5: Validação do Nome
│   │   │   ├── Span 6: Validação do Role
│   │   │   └── Span 7: Validação do Seed
│   │   └── Span 8: Resposta
└── Span 9: Envio da resposta HTTP
```

#### TraceContext

O `TraceContext` gerencia automaticamente:
- **Geração de IDs únicos** para traces e spans
- **Hierarquia de spans** com relacionamento pai-filho
- **Contexto MDC** para correlacionar logs
- **Operações nomeadas** para identificação clara
- **Gerenciamento de estado** entre spans

#### TraceSpan

O `TraceSpan` implementa `AutoCloseable` e oferece:
- **Gerenciamento automático** de recursos
- **Tags personalizadas** para contexto de negócio
- **Métricas de performance** automáticas
- **Contexto de segurança** e negócio
- **Integração com MDC** para logging estruturado

#### TraceMetrics

O `TraceMetrics` fornece:
- **Coleta segura** de métricas com contexto de tracing
- **Tratamento de exceções** específico para cada operação
- **Contexto de tracing** em todas as respostas
- **Logging estruturado** para auditoria

### Sistema de Monitoring em Tempo Real

O sistema coleta e disponibiliza métricas detalhadas sobre:

#### Métricas de Performance
- **Tempo de Resposta**: Média, mínimo, máximo por endpoint
- **Throughput**: Requisições por segundo
- **Latência**: Tempo de processamento por operação
- **Concorrência**: Número de requisições simultâneas

#### Métricas de Negócio
- **Validação de JWT**: Taxa de sucesso, tipos de erro
- **Claims**: Distribuição de erros por tipo de validação
- **Endpoints**: Uso e performance por rota
- **Usuários**: Padrões de uso e comportamento

#### Métricas de Infraestrutura
- **Status de Saúde**: Disponibilidade da aplicação
- **Recursos**: Uso de memória, CPU, threads
- **Erros**: Taxa de erro, tipos de exceção
- **Dependências**: Status de serviços externos

#### MetricsCollector

O `MetricsCollector` implementa:
- **Contadores atômicos** para métricas thread-safe
- **Métricas por endpoint** com agregação automática
- **Histórico de tempos de resposta** (últimos 1000 por endpoint)
- **Métricas de claims** com categorização de erros
- **Performance metrics** com cálculos em tempo real
- **Taxa de sucesso** calculada dinamicamente
- **Taxa de validação JWT** em tempo real

#### MonitorHealth

O `MonitorHealth` fornece:
- **Health checks** com contexto de tracing
- **Métricas básicas** de saúde da aplicação
- **Status de disponibilidade** em tempo real
- **Performance metrics** integradas

### Endpoints de Monitoring e Observabilidade

#### GET /monitoring/metrics
Retorna métricas detalhadas em tempo real da aplicação, incluindo:
- Contadores de requisições (total, sucesso, falha)
- Métricas de performance por endpoint
- Estatísticas de validação de JWT
- Erros de validação de claims
- Contexto de tracing atual
- Taxa de sucesso em tempo real
- Métricas de performance agregadas

**Exemplo de resposta:**
```json
{
  "endpointMetrics": {
    "/monitoring/metrics:GET": {
      "minResponseTime": 8,
      "maxResponseTime": 8,
      "avgResponseTime": 8.0,
      "requests": 3,
      "errors": 0
    },
    "/api/validate:GET": {
      "minResponseTime": 3,
      "maxResponseTime": 6,
      "avgResponseTime": 3.75,
      "requests": 8,
      "errors": 2
    }
  },
  "successRate": 27.27272727272727,
  "totalJwtValidations": 4,
  "validJwts": 2,
  "claimValidationErrors": {},
  "performanceMetrics": {
    "totalResponses": 5,
    "minResponseTime": 3,
    "maxResponseTime": 8,
    "avgResponseTime": 4.6
  },
  "invalidJwts": 2,
  "currentSpanId": "9a90d774",
  "jwtValidationRate": 50.0,
  "totalRequests": 11,
  "successfulRequests": 3,
  "currentOperation": "root",
  "currentTraceId": "ea38a881d26341e0",
  "failedRequests": 2,
  "timestamp": "2025-08-14T21:17:57.639503400Z"
}
```

#### GET /monitoring/health
Verifica a saúde da aplicação com métricas de performance e contexto de tracing.

**Exemplo de resposta:**
```json
{
    "traceId": "d176d1628fa54e3e",
    "spanId": "e6c4db3d",
    "successRate": 30.76923076923077,
    "avgResponseTime": 5.166666666666667,
    "totalRequests": 13,
    "status": "UP",
    "timestamp": 1755206334359
}
```

#### GET /monitoring/tracing/current
Retorna informações sobre o trace e span atualmente em execução.

**Exemplo de resposta:**
```json
{
    "traceId": "138419139d2e4017",
    "spanId": "b68e3530",
    "endpoint": "/monitoring/tracing/current",
    "requestId": "321d36e0-d26a-481d-9946-d1a57a235c95",
    "operationName": "root",
    "timestamp": 1755206415550
}
```

#### GET /monitoring/tracing/endpoints
Retorna estatísticas de traces organizadas por endpoint.

**Exemplo de resposta:**
```json
{
    "traceId": "dbd2ceda2adf4f6e",
    "endpoints": {
        "/api/validate": {
            "errorRate": 2.1,
            "lastTrace": 1755206428036,
            "avgDuration": 45.2,
            "totalTraces": 150
        }
    },
    "timestamp": 1755206428036
}
```

#### POST /monitoring/metrics/reset
Reseta todas as métricas coletadas (útil para testes e desenvolvimento).

**Exemplo de resposta:**
```json
{
    "traceId": "f6f4b28dda954e4d",
    "spanId": "a1a7fa14",
    "endpoint": "/monitoring/metrics/reset",
    "requestId": "e9cf8d86-d76a-4199-85ff-430321319ca6",
    "message": "Metricas resetadas com sucesso",
    "timestamp": 1755206444624
}
```

### Sistema de Logging

A aplicação implementa um sistema robusto de logging utilizando SLF4J com Logback, fornecendo rastreabilidade completa das operações de validação de JWT.

#### Configuração de Logs

- **Nível Global**: INFO (configurado em `logging.level.root`)
- **Nível da Aplicação**: DEBUG (configurado para o pacote principal)
- **Formato**: Timestamp, Thread, Nível, Logger, RequestID, Endpoint, TraceID, SpanID, Operação e Duração

#### Padrão de Logging com MDC e Tracing

```properties
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} [%X{requestId}] [%X{endpoint}] [%X{traceId}] [%X{spanId}] [%X{operationName}] [%X{duration}ms] - %msg%n
```

#### Sistema de Correlação de Requisições

A aplicação implementa **MDC (Mapped Diagnostic Context)** para correlacionar automaticamente todos os logs de uma mesma requisição:

- **RequestID**: UUID único gerado automaticamente para cada requisição
- **Endpoint**: URI da requisição sendo processada
- **TraceID**: ID único do trace distribuído
- **SpanID**: ID do span atual sendo executado
- **Operação**: Nome da operação em execução
- **Duração**: Tempo de execução em milissegundos
- **Correlação**: Todos os logs de uma requisição compartilham o mesmo contexto

#### LoggingInterceptor

O `LoggingInterceptor` é responsável por:

1. **Geração automática de RequestID** para cada requisição
2. **Captura do endpoint** sendo acessado
3. **Injeção do contexto MDC** em todas as requisições
4. **Medição de tempo** de resposta
5. **Limpeza automática** do contexto ao final da requisição
6. **Inicialização automática** do tracing para cada requisição
7. **Registro automático** de métricas de performance

#### Logs Implementados

##### Controller (ApiController)
- **INFO**: Endpoint chamado, JWT válido/inválido
- **ERROR**: JWT inválido

##### JWT Validator
- **INFO**: Início da validação, total de claims válido, chamada de validadores, validação bem-sucedida
- **DEBUG**: Verificação de total de claims
- **ERROR**: Total de claims inválido

##### JWT Decoder
- **INFO**: Início da decodificação
- **DEBUG**: JWT decodificado com sucesso
- **ERROR**: Falha na decodificação

##### Validadores de Claims

###### NameValidator
- **INFO**: Início da validação da claim Name
- **DEBUG**: Nome válido
- **ERROR**: Nome nulo/vazio, tamanho excedido, contém números

###### RoleValidator
- **INFO**: Início da validação da claim Role
- **DEBUG**: Role válido
- **ERROR**: Role nulo/vazio, role inválido

###### SeedValidator
- **INFO**: Início da validação da claim Seed
- **DEBUG**: Seed válido
- **ERROR**: Seed nulo/vazio, seed inválido, seed não é primo

##### GlobalExceptionHandler
- **ERROR**: Claims inválidas detectadas com detalhes da exceção
- **ERROR**: Tokens inválidos detectados com detalhes da exceção

##### TraceContext
- **DEBUG**: Início e fim de traces e spans
- **DEBUG**: Mudanças de contexto entre spans

##### MetricsCollector
- **INFO**: Métricas coletadas com sucesso
- **ERROR**: Erros na coleta de métricas

##### TraceMetrics
- **INFO**: Métricas de tracing coletadas com sucesso
- **ERROR**: Erros na coleta de métricas de tracing

##### MonitorHealth
- **INFO**: Health check executado com sucesso
- **ERROR**: Erros no health check

#### Exemplo de Saída de Logs com MDC, Tracing e Monitoring

```
2024-01-15 16:50:23 [http-nio-8080-exec-1] INFO  c.d.v.b.c.ApiController [abc123-def456] [/api/validate] [a1b2c3d4e5f6g7h8] [span001] [validateJwt] [45ms] - Endpoint /api/validate chamado
2024-01-15 16:50:23 [http-nio-8080-exec-1] INFO  c.d.v.b.v.JwtValidator [abc123-def456] [/api/validate] [a1b2c3d4e5f6g7h8] [span002] [JwtValidation] [23ms] - Iniciando validacao do JWT
2024-01-15 16:50:23 [http-nio-8080-exec-1] INFO  c.d.v.b.u.JwtDecoder [abc123-def456] [/api/validate] [a1b2c3d4e5f6g7h8] [span003] [JwtDecode] [8ms] - Iniciando decodificacao do JWT
2024-01-15 16:50:23 [http-nio-8080-exec-1] DEBUG b.d.v.b.u.JwtDecoder [abc123-def456] [/api/validate] [a1b2c3d4e5f6g7h8] [span003] [JwtDecode] [8ms] - JWT decodificado com sucesso
2024-01-15 16:50:23 [http-nio-8080-exec-1] INFO  c.d.v.b.v.NameValidator [abc123-def456] [/api/validate] [a1b2c3d4e5f6g7h8] [span004] [Validator_NameValidator] [5ms] - Nome valido: Toninho Araujo
2024-01-15 16:50:23 [http-nio-8080-exec-1] INFO  c.d.v.b.v.RoleValidator [abc123-def456] [/api/validate] [a1b2c3d4e5f6g7h8] [span005] [Validator_RoleValidator] [3ms] - Role valido: Admin
2024-01-15 16:50:23 [http-nio-8080-exec-1] INFO  c.d.v.b.v.SeedValidator [abc123-def456] [/api/validate] [a1b2c3d4e5f6g7h8] [span006] [Validator_SeedValidator] [7ms] - Seed valido: 7841
2024-01-15 16:50:23 [http-nio-8080-exec-1] INFO  c.d.v.b.v.JwtValidator [abc123-def456] [/api/validate] [a1b2c3d4e5f6g7h8] [span002] [JwtValidation] [23ms] - JWT passou nas validacoes
2024-01-15 16:50:23 [http-nio-8080-exec-1] INFO  c.d.v.b.c.ApiController [abc123-def456] [/api/validate] [a1b2c3d4e5f6g7h8] [span001] [validateJwt] [45ms] - JWT valido
```

#### Benefícios do Sistema de Logging e Observabilidade

1. **Rastreabilidade Automática**: Cada requisição recebe um ID único automaticamente
2. **Correlação de Logs**: Todos os logs de uma requisição podem ser agrupados pelo RequestID
3. **Tracing Distribuído**: Rastreamento completo do fluxo através de todos os componentes
4. **Debugging Aprimorado**: Facilita a identificação de problemas específicos por requisição
5. **Monitoramento Estruturado**: Logs organizados para análise de performance e erros
6. **Auditoria Completa**: Histórico detalhado de todas as validações de JWT
7. **Manutenção Simplificada**: Facilita a identificação e correção de problemas
8. **Observabilidade em Produção**: Sistema preparado para ambientes de produção com ferramentas de monitoramento
9. **Performance Monitoring**: Métricas em tempo real de todas as operações
10. **Alertas Proativos**: Detecção antecipada de problemas
11. **Contexto de Negócio**: Tags e métricas específicas para análise de negócio
12. **Segurança**: Contexto de segurança em todas as operações

### Casos de Uso da Observabilidade

1. **Debugging de Requisições**: Identificar problemas específicos por RequestID
2. **Análise de Performance**: Correlacionar logs para medir tempo de resposta
3. **Monitoramento de Erros**: Rastrear falhas específicas por requisição
4. **Auditoria de Segurança**: Histórico completo de validações de JWT
5. **Troubleshooting**: Correlacionar logs de diferentes componentes da aplicação
6. **Análise de Padrões**: Identificar padrões de uso e comportamento
7. **Performance Tuning**: Identificar gargalos e otimizações
8. **Capacity Planning**: Análise de uso de recursos e escalabilidade
9. **Análise de Negócio**: Métricas específicas para análise de comportamento do usuário
10. **Monitoramento de Saúde**: Health checks proativos com contexto completo

### Estrutura de Arquivos de Observabilidade

```
src/main/java/br/dev/viniciusleonel/backend_challenge/
├── infra/
│   ├── config/              # Configurações da aplicação
│   │   ├── WebConfig.java   # Configuração do interceptor
│   │   └── ObservabilityConfig.java # Configuração de observabilidade
│   ├── exception/            # Tratamento de exceções
│   │   ├── handler/         # Handlers globais de exceção
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── CollectCurrentTraceException.java
│   │   ├── CollectEndpointTraceException.java
│   │   ├── CollectMetricsException.java
│   │   ├── HealthCheckException.java
│   │   ├── InvalidClaimException.java
│   │   └── ResetMetricsException.java
│   ├── interceptor/         # Interceptadores de requisição
│   │   └── LoggingInterceptor.java
│   ├── tracing/             # Sistema de tracing
│   │   ├── TraceContext.java # Contexto de tracing
│   │   ├── TraceMetrics.java # Métricas de tracing
│   │   └── TraceSpan.java   # Implementação de spans
│   └── monitoring/          # Sistema de monitoring
│       ├── MetricsCollector.java # Coletor de métricas
│       └── MonitorHealth.java # Health checks
├── controller/
│   ├── ApiController.java   # Controller principal
│   └── MonitoringController.java # Controller de monitoring
```

### Configuração Automática

O sistema de observabilidade é configurado automaticamente através de:

- **LoggingInterceptor**: Aplicado automaticamente a todas as rotas `/api/**`
- **MDC**: Contexto injetado automaticamente em cada requisição
- **Logging**: Formato configurado para exibir RequestID, Endpoint, TraceID, SpanID, Operação e Duração
- **Exception Handling**: Logging automático de todas as exceções
- **Tracing**: Geração automática de traces e spans
- **Monitoring**: Coleta automática de métricas
- **Health Checks**: Verificação automática de saúde da aplicação

### Integração com Ferramentas de Monitoramento

O sistema está preparado para integração com:

- **ELK Stack** (Elasticsearch, Logstash, Kibana)
- **Prometheus + Grafana**
- **Splunk**
- **Datadog**
- **New Relic**
- **Jaeger** (Distributed Tracing)
- **Zipkin** (Tracing)
- **OpenTelemetry** (Padrão aberto)

### Métricas Disponíveis

Através dos logs estruturados e endpoints de monitoring, é possível extrair:

- **Volume de requisições** por endpoint
- **Taxa de sucesso/erro** por tipo de validação
- **Tempo de resposta** por operação
- **Padrões de uso** dos diferentes tipos de JWT
- **Distribuição de erros** por tipo de claim inválida
- **Performance por componente** (validadores, decoders)
- **Tracing statistics** por endpoint e operação
- **Health metrics** em tempo real
- **Business metrics** para análise de comportamento

## Licença

Este projeto é um desafio de desenvolvimento e não possui licença específica definida.
