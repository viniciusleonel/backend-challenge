# Backend Challenge

---

## Descrição

Este é um projeto Spring Boot que implementa um sistema de **validação de JWT (JSON Web Tokens)** com validações específicas para claims personalizados. A **função principal da API é validar tokens JWT** e retornar um boolean indicando se são válidos ou não.

**Importante**: Conforme especificado no desafio, o JWT é recebido por parâmetros e retorna um boolean. Considerando que:

1. **Input**: JWT como string via parâmetros
2. **Output**: Boolean indicando validade (true/false)
3. **Simplicidade**: Resposta direta sem complexidade adicional

## Funcionalidade Principal: Validação de JWT

### Endpoint Principal

### GET /api/validate?token={{token}}

Valida um JWT token recebido via query parameter.

**Parâmetros:**
- `token` (query parameter): O JWT token a ser validado

**Respostas:**
- **200 OK**: Token válido, retorna `true`
- **400 Bad Request**: Token malformado ou inválido, retorna `false`
- **422 Unprocessable Entity**: Claims inválidos, retorna `false`

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

```
eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiNzg0MSIsIk5hbWUiOiJUb25pbmhvIEFyYXVqbyJ9.QY05sIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg
```
```json
{
  "Role": "Admin",
  "Seed": "7841",
  "Name": "Toninho Araujo"
}
```
### Resposta (Status 200)
```json
true
```


**Observações:**
- O nome não pode conter números
- O role deve ser "Admin", "Member" ou "External"
- O seed deve ser um número primo
- O token deve conter exatamente 3 claims

### Exemplo de JWT Inválido

### Caso 1 - JWT com formato inválido:
```
eyJhbGciOiJzI1NiJ9.dfsdfsfryJSr2xrIjoiQWRtaW4iLCJTZrkIjoiNzg0MSIsIk5hbrUiOiJUb25pbmhvIEFyYXVqbyJ9.QY05fsdfsIjtrcJnP533kQNk8QXcaleJ1Q01jWY_ZzIZuAg
```
```json

```
### Resposta (Status 400)
```json
false
```

### Caso 2 - Claim `Name` com número:
```
eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiRXh0ZXJuYWwiLCJTZWVkIjoiODgwMzciLCJOYW1lIjoiTTRyaWEgT2xpdmlhIn0.6YD73XWZYQSSMDf6H0i3-kylz1-TY_Yt6h1cV2Ku-Qs
```

```json
{
  "Role": "External",
  "Seed": "72341",
  "Name": "M4ria Olivia"
}
```

### Resposta (Status 422)
```json
false
```

### Caso 3 - Mais de 3 Claims válidas:
```
eyJhbGciOiJIUzI1NiJ9.eyJSb2xlIjoiTWVtYmVyIiwiT3JnIjoiQlIiLCJTZWVkIjoiMTQ2MjciLCJOYW1lIjoiVmFsZGlyIEFyYW5oYSJ9.cmrXV_Flm5mfdpfNUVopY_I2zeJUy4EZ4i3Fea98zvY
```
```json
{
  "Role": "Member",
  "Org": "BR",
  "Seed": "14627",
  "Name": "Valdir Aranha"
}
```
### Resposta (Status 400)
```json
false
```

### Caso 4 - Claim `Seed` não contém um número primo:
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJOYW1lIjoiVG9uaW5obyBBcmF1am8iLCJSb2xlIjoiQWRtaW4iLCJTZWVkIjoiNCJ9.0B_WTINUHoRiHQqwcQJncELG4m85I2n7iQ35ENHuvA8
```
```json
{
  "Role": "Admin",
  "Seed": "4",
  "Name": "Toninho Araujo"
}
```
### Resposta (Status 422)
```json
false
```

> **Nota:** A geração de tokens JWT para testes agora está disponível na classe `GenerateJwtToken`. Com ela, é possível criar tokens contendo diferentes combinações de claims, permitindo simular diversos cenários durante o desenvolvimento e validação da aplicação.  
> Para utilizá-la, basta alterar os valores das claims diretamente na classe `GenerateJwtToken` e executar o método `main`. Por exemplo, modifique os campos desejados no método `main` e rode a aplicação. O token gerado será exibido no console, pronto para ser utilizado nos testes.

---

## Validadores de Claims JWT

O sistema de validação de JWT foi projetado para ser modular e extensível, utilizando uma arquitetura baseada em interfaces e implementações específicas para cada tipo de claim. A seguir, estão descritos os principais validadores utilizados:

- **JwtValidator**: Classe principal responsável por orquestrar a validação do token JWT. Ela utiliza uma lista de instâncias de `ClaimValidator` para validar cada claim presente no token, além de verificar se o token está bem formado e assinado corretamente.

- **ClaimValidator**: Interface que define o contrato para todos os validadores de claims. Cada implementação dessa interface é responsável por validar um tipo específico de claim, garantindo assim o princípio da responsabilidade única (SRP).

- **RoleValidator**: Implementação de `ClaimValidator` responsável por validar a claim `Role`. Verifica se o valor da role está entre os papéis permitidos e configurados no sistema, garantindo que apenas roles válidas sejam aceitas.

- **NameValidator**: Implementação de `ClaimValidator` que valida a claim `Name`. Pode, por exemplo, garantir que o nome não esteja vazio, siga um padrão específico ou atenda a regras de negócio definidas.

- **SeedValidator**: Implementação de `ClaimValidator` dedicada à validação da claim `Seed`. No contexto deste projeto, verifica se o valor da seed é um número primo, conforme a regra de negócio estabelecida.

Esses validadores são configurados e gerenciados pela classe `JwtValidationConfig`, que centraliza a definição de quais validadores estão ativos e quais roles são consideradas válidas. Novos validadores podem ser facilmente adicionados ao sistema, bastando implementar a interface `ClaimValidator` e registrá-los na configuração, sem a necessidade de alterar o código existente dos demais validadores ou do `JwtValidator`.

Essa abordagem garante que o sistema seja aberto para extensão e fechado para modificação (Princípio Open/Closed), além de promover a reutilização e a manutenção facilitada do código.


---

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
│   ├── observability/   # Sistema de observabilidade
│   │   ├── config/      # Configurações da aplicação
│   │   │   ├── WebConfig.java
│   │   │   └── ObservabilityConfig.java
│   │   ├── logging/     # Sistema de logging
│   │   │   └── LoggingInterceptor.java
│   │   ├── monitoring/  # Sistema de monitoring
│   │   │   ├── MetricsCollector.java
│   │   │   └── MonitorHealth.java
│   │   └── tracing/     # Sistema de tracing distribuído
│   │       ├── TraceContext.java
│   │       ├── TraceMetrics.java
│   │       └── TraceSpan.java
│   └── terraform/       # Infraestrutura como código
│       ├── main.tf      # Configuração principal AWS
│       ├── user_data.sh # Script de inicialização
│       └── ssh/         # Chaves SSH
├── utils/               # Utilitários (geração de JWT, validação de números)
├── validators/          # Validadores de claims
└── BackendChallengeApplication.java
```

---

## Tratamento de Exceções

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

O `GlobalExceptionHandler` agora trata as seguintes exceções:

- **InvalidClaimException**: Retorna status 422 (Unprocessable Entity) para claims inválidos
- **JWTDecodeException**: Retorna status 400 (Bad Request) para tokens malformados
- **CollectCurrentTraceException**: Retorna status 500 (Internal Server Error) para falhas ao coletar o trace atual
- **CollectEndpointTraceException**: Retorna status 500 (Internal Server Error) para falhas ao coletar traces por endpoint
- **CollectMetricsException**: Retorna status 500 (Internal Server Error) para falhas ao coletar métricas
- **ResetMetricsException**: Retorna status 500 (Internal Server Error) para falhas ao resetar métricas
- **HealthCheckException**: Retorna status 500 (Internal Server Error) para falhas no health check

### Tipos de Erro

1. **Claims Inválidos**: Nome com números, role inválido, seed não primo
2. **Token Malformado**: Formato JWT inválido
3. **Claims Ausentes**: Número incorreto de claims (deve ser exatamente 3)
4. **Erros de Observabilidade**: Falhas na coleta de métricas e tracing

---

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

---

## Documentação Interativa com Swagger

A API possui documentação interativa gerada automaticamente com **Swagger** (OpenAPI 3), facilitando a exploração e o teste dos endpoints diretamente pelo navegador.

- **Acesse a documentação Swagger UI:**  
  [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  
  ou  
  [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

**Swagger AWS:**  
  [http://ec2-54-173-114-50.compute-1.amazonaws.com/swagger-ui.html](http://ec2-54-173-114-50.compute-1.amazonaws.com/swagger-ui.html)  
  ou  
  [http://ec2-54-173-114-50.compute-1.amazonaws.com/swagger-ui/index.html](http://ec2-54-173-114-50.compute-1.amazonaws.com/swagger-ui/index.html)

> **Dica:** O Swagger permite enviar requisições reais para os endpoints, visualizar exemplos de payloads, respostas, códigos de status e detalhes das validações.

A especificação OpenAPI também pode ser acessada em:  
[http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

**OpenAPI AWS:**  
[http://ec2-54-173-114-50.compute-1.amazonaws.com/v3/api-docs](http://ec2-54-173-114-50.compute-1.amazonaws.com/v3/api-docs)

**Principais vantagens do Swagger:**
- Visualização clara dos endpoints disponíveis
- Testes rápidos sem necessidade de ferramentas externas (ex: Postman)
- Exemplos de requisições e respostas
- Detalhamento dos parâmetros e possíveis códigos de resposta

A configuração do Swagger já está pronta no projeto, basta rodar a aplicação e acessar os links acima.

> **Coleção Postman:**  
Um arquivo de coleção do Postman está disponível em `src/postman`. Você pode importar esse arquivo no Postman para testar rapidamente todos os endpoints da API, com exemplos de requisições e respostas já configurados.

---

## Como Executar

### URL Base AWS - http://ec2-54-173-114-50.compute-1.amazonaws.com/


### Pré-requisitos

- Java 21
- Maven 3.9.8+
- Docker (opcional)
- Terraform (opcional)
- AWS CLI (opcional)

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

**API AWS:** `http://ec2-54-173-114-50.compute-1.amazonaws.com`

### Execução com Docker

1. **Construa a imagem:**
```bash

docker build -t backend-challenge .
```

2. **Execute o container:**
```bash

docker run --name backend-challenge -p 8080:8080 backend-challenge
```

---

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

---

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

---

## Observabilidade

A aplicação implementa um sistema completo de observabilidade com logging, tracing e monitoring, fornecendo insights profundos sobre o comportamento e performance do sistema.

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
        "/api/validate:GET": {
            "minResponseTime": 2,
            "maxResponseTime": 2,
            "avgResponseTime": 2.0,
            "requests": 2,
            "errors": 0
        }
    },
    "successRate": 50.0,
    "totalJwtValidations": 1,
    "validJwts": 1,
    "claimValidationErrors": {},
    "performanceMetrics": {
        "totalResponses": 3,
        "minResponseTime": 2,
        "maxResponseTime": 2,
        "avgResponseTime": 2.0
    },
    "invalidJwts": 0,
    "currentSpanId": "0c93a92c",
    "jwtValidationRate": 100.0,
    "totalRequests": 6,
    "successfulRequests": 3,
    "currentOperation": "root",
    "currentTraceId": "c83c43a942da48ec",
    "failedRequests": 0,
    "timestamp": "2025-08-14T22:37:47.732610800Z"
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
2025-08-14 21:06:19 [http-nio-8080-exec-3] DEBUG b.d.v.b.i.o.tracing.TraceContext [82459d58-a8cd-429c-9970-945efe7136a7] [/api/validate] [480c8f6e38474f35] [42da1a61] [root] [ms] - Trace iniciado: traceId=480c8f6e38474f35, spanId=42da1a61
2025-08-14 21:06:19 [http-nio-8080-exec-3] DEBUG b.d.v.b.i.o.tracing.TraceContext [82459d58-a8cd-429c-9970-945efe7136a7] [/api/validate] [480c8f6e38474f35] [42da1a61] [root] [ms] - getCurrentTraceId() retornou: 480c8f6e38474f35
2025-08-14 21:06:19 [http-nio-8080-exec-3] DEBUG b.d.v.b.i.o.l.LoggingInterceptor [82459d58-a8cd-429c-9970-945efe7136a7] [/api/validate] [480c8f6e38474f35] [42da1a61] [root] [ms] - Tracing iniciado para: GET /api/validate [requestId: 82459d58-a8cd-429c-9970-945efe7136a7, traceId: 480c8f6e38474f35]
2025-08-14 21:06:19 [http-nio-8080-exec-3] DEBUG b.d.v.b.i.o.tracing.TraceContext [82459d58-a8cd-429c-9970-945efe7136a7] [/api/validate] [480c8f6e38474f35] [e34462fc] [validateJwt] [ms] - Span iniciado: operationName=validateJwt, spanId=e34462fc, parentSpanId=42da1a61
2025-08-14 21:06:19 [http-nio-8080-exec-3] DEBUG b.d.v.b.i.o.tracing.TraceSpan [82459d58-a8cd-429c-9970-945efe7136a7] [/api/validate] [480c8f6e38474f35] [e34462fc] [validateJwt] [ms] - Span iniciado: validateJwt [spanId: 42da1a61, parentSpanId: ]
2025-08-14 21:06:19 [http-nio-8080-exec-3] INFO  b.d.v.b.validators.JwtValidator [82459d58-a8cd-429c-9970-945efe7136a7] [/api/validate] [480c8f6e38474f35] [e34462fc] [validateJwt] [ms] - Iniciando validacao do JWT
2025-08-14 21:06:19 [http-nio-8080-exec-3] INFO  b.d.v.b.utils.JwtDecoder [82459d58-a8cd-429c-9970-945efe7136a7] [/api/validate] [480c8f6e38474f35] [e34462fc] [validateJwt] [ms] - Iniciando decodificacao do JWT
2025-08-14 21:06:19 [http-nio-8080-exec-3] DEBUG b.d.v.b.utils.JwtDecoder [82459d58-a8cd-429c-9970-945efe7136a7] [/api/validate] [480c8f6e38474f35] [e34462fc] [validateJwt] [ms] - JWT decodificado com sucesso
2025-08-14 21:06:19 [http-nio-8080-exec-3] INFO  b.d.v.b.validators.JwtValidator [82459d58-a8cd-429c-9970-945efe7136a7] [/api/validate] [480c8f6e38474f35] [e34462fc] [validateJwt] [ms] - Chamando validadores de claims
2025-08-14 21:06:19 [http-nio-8080-exec-3] INFO  b.d.v.b.validators.NameValidator [82459d58-a8cd-429c-9970-945efe7136a7] [/api/validate] [480c8f6e38474f35] [e34462fc] [validateJwt] [ms] - Iniciando validacao da claim Name
2025-08-14 21:06:19 [http-nio-8080-exec-3] DEBUG b.d.v.b.validators.NameValidator [82459d58-a8cd-429c-9970-945efe7136a7] [/api/validate] [480c8f6e38474f35] [e34462fc] [validateJwt] [ms] - Nome valido: Toninho Araujo
2025-08-14 21:06:19 [http-nio-8080-exec-3] INFO  b.d.v.b.validators.RoleValidator [82459d58-a8cd-429c-9970-945efe7136a7] [/api/validate] [480c8f6e38474f35] [e34462fc] [validateJwt] [ms] - Iniciando validacao da claim Role
2025-08-14 21:06:19 [http-nio-8080-exec-3] DEBUG b.d.v.b.validators.RoleValidator [82459d58-a8cd-429c-9970-945efe7136a7] [/api/validate] [480c8f6e38474f35] [e34462fc] [validateJwt] [ms] - Role valida: Admin
2025-08-14 21:06:19 [http-nio-8080-exec-3] INFO  b.d.v.b.validators.SeedValidator [82459d58-a8cd-429c-9970-945efe7136a7] [/api/validate] [480c8f6e38474f35] [e34462fc] [validateJwt] [ms] - Iniciando validacao da claim Seed
2025-08-14 21:06:19 [http-nio-8080-exec-3] DEBUG b.d.v.b.validators.SeedValidator [82459d58-a8cd-429c-9970-945efe7136a7] [/api/validate] [480c8f6e38474f35] [e34462fc] [validateJwt] [ms] - Seed valida: 7841
2025-08-14 21:06:19 [http-nio-8080-exec-3] INFO  b.d.v.b.validators.JwtValidator [82459d58-a8cd-429c-9970-945efe7136a7] [/api/validate] [480c8f6e38474f35] [e34462fc] [validateJwt] [ms] - JWT passou nas validacoes
2025-08-14 21:06:19 [http-nio-8080-exec-3] INFO  b.d.v.b.validators.JwtValidator [82459d58-a8cd-429c-9970-945efe7136a7] [/api/validate] [480c8f6e38474f35] [e34462fc] [validateJwt] [ms] - Total de claims valido: 3
2025-08-14 21:06:19 [http-nio-8080-exec-3] DEBUG b.d.v.b.i.o.tracing.TraceContext [82459d58-a8cd-429c-9970-945efe7136a7] [/api/validate] [480c8f6e38474f35] [42da1a61] [parent] [ms] - Span finalizado, voltando para parent: spanId=42da1a61
2025-08-14 21:06:19 [http-nio-8080-exec-3] DEBUG b.d.v.b.i.o.tracing.TraceSpan [82459d58-a8cd-429c-9970-945efe7136a7] [/api/validate] [480c8f6e38474f35] [42da1a61] [parent] [ms] - Span finalizado: validateJwt [duracao: 36ms, spanId: 42da1a61, tags: {tokenLength=137, business.operation=jwt_validation}, metrics: {duration=36, startTime=1755216379134, endTime=1755216379170}]
2025-08-14 21:06:19 [http-nio-8080-exec-3] DEBUG b.d.v.b.i.o.tracing.TraceContext [82459d58-a8cd-429c-9970-945efe7136a7] [/api/validate] [480c8f6e38474f35] [42da1a61] [parent] [81ms] - getCurrentTraceId() retornou: 480c8f6e38474f35
2025-08-14 21:06:19 [http-nio-8080-exec-3] DEBUG b.d.v.b.i.o.l.LoggingInterceptor [82459d58-a8cd-429c-9970-945efe7136a7] [/api/validate] [480c8f6e38474f35] [42da1a61] [parent] [81ms] - Finalizando tracing para: GET /api/validate [traceId: 480c8f6e38474f35, duracao: 81ms]
2025-08-14 21:06:19 [http-nio-8080-exec-3] DEBUG b.d.v.b.i.o.tracing.TraceContext [82459d58-a8cd-429c-9970-945efe7136a7] [/api/validate] [480c8f6e38474f35] [42da1a61] [parent] [81ms] - Trace finalizado: traceId=480c8f6e38474f35
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

### Configuração Automática

O sistema de observabilidade é configurado automaticamente através de:

- **LoggingInterceptor**: Aplicado automaticamente a todas as rotas `/api/**`
- **MDC**: Contexto injetado automaticamente em cada requisição
- **Logging**: Formato configurado para exibir RequestID, Endpoint, TraceID, SpanID, Operação e Duração
- **Exception Handling**: Logging automático de todas as exceções
- **Tracing**: Geração automática de traces e spans
- **Monitoring**: Coleta automática de métricas
- **Health Checks**: Verificação automática de saúde da aplicação

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

---

## GitHub Actions CI/CD (Docker-Terraform-AWS)

O projeto utiliza GitHub Actions para automatizar o processo de integração, entrega contínua e provisionamento de infraestrutura.

### Pipeline CI (Continuous Integration)

O workflow `ci.yml` é executado em:
- Push para a branch `main`
- Pull requests para a branch `main`

**Etapas do CI:**
1. **Checkout do código** - Baixa o código fonte
2. **Configuração do JDK 21** - Configura o ambiente Java com Temurin
3. **Compilação e testes** - Executa `mvn clean verify`

### Pipeline CD (Continuous Deployment + Infraestrutura)

O workflow `cd.yml` é executado automaticamente após o sucesso do CI e:
- Constrói a imagem Docker
- Faz push para o Docker Hub
- Provisiona/atualiza a infraestrutura na AWS usando Terraform
- Utiliza secrets configurados para autenticação

**Etapas do CD:**
1. **Setup Docker Buildx** - Configura o ambiente Docker
2. **Login no Docker Hub** - Autentica usando secrets
3. **Build e Push** - Constrói e envia a imagem para o registry
4. **Deploy da Infraestrutura com Terraform** - Inicializa e aplica o Terraform para provisionar/atualizar recursos na AWS

### Pipeline de Destruição (Manual)

O workflow `destroy.yml` permite destruir manualmente toda a infraestrutura provisionada:

**Características:**
- **Execução manual** via `workflow_dispatch` (não automático)
- **Destruição completa** de todos os recursos AWS criados
- **Segurança** - Requer intervenção manual para execução
- **Limpeza** - Remove EC2, Security Groups, Key Pairs e outros recursos

**Etapas do Destroy:**
1. **Checkout do código** - Baixa o código fonte
2. **Setup Terraform** - Configura o ambiente Terraform
3. **Destruição da Infraestrutura** - Executa `terraform destroy -auto-approve`

**⚠️ Importante:** Este workflow destrói permanentemente todos os recursos da infraestrutura. Use apenas quando necessário para limpeza de custos ou reinicialização completa.

**Arquivos de configuração:**
- `.github/workflows/ci.yml` - Pipeline de integração contínua
- `.github/workflows/cd.yml` - Pipeline de entrega contínua
- `.github/workflows/destroy.yml` - Pipeline de destruição da infraestrutura

**Secrets necessários:**
- `DOCKERHUB_USERNAME` - Nome de usuário do Docker Hub
- `DOCKERHUB_TOKEN` - Token de acesso do Docker Hub
- `AWS_ACCESS_KEY_ID` - Chave de acesso da AWS
- `AWS_SECRET_ACCESS_KEY` - Chave secreta da AWS
- `AWS_REGION` - Região da AWS (ex: us-east-1)


---

## Infraestrutura como Código (Terraform-AWS)

O projeto inclui configuração completa de infraestrutura como código usando **Terraform** para provisionar e gerenciar recursos na AWS.

### Estrutura dos Arquivos Terraform

```
src/main/java/br/dev/viniciusleonel/backend_challenge/infra/terraform/
├── main.tf                   # Configuração principal dos recursos AWS
├── user_data.sh              # Script de inicialização da instância EC2
├── .terraform.lock.hcl       # Lock das versões dos providers
└── ssh/                      # Chaves SSH para acesso à instância
    └── id_rsa.pub            # Chave pública SSH
```

### Backend Remoto do Terraform

O projeto utiliza um **backend remoto** configurado para armazenar o estado do Terraform de forma segura e colaborativa:

- **Bucket S3**: `backend-challenge-terraform-state-bucket`
  - Armazena o arquivo de estado (`terraform.tfstate`)
  - Configurado com versionamento para backup automático
  - Acesso controlado via IAM policies

- **Tabela DynamoDB**: `terraform-locks`
  - Implementa **state locking** para evitar conflitos de concorrência
  - Garante que apenas uma execução do Terraform ocorra por vez
  - Previne corrupção do estado durante operações simultâneas

**Configuração do Backend:**
```hcl
terraform {
  backend "s3" {
    bucket         = "backend-challenge-terraform-state-bucket"
    key            = "backend-challenge/terraform.tfstate"
    region         = "us-east-1"
    dynamodb_table = "terraform-locks"
  }
}
```

### Recursos Provisionados

#### 1. Bucket S3 (`backend-challenge-terraform-state-bucket`)
- **Propósito**: Armazenamento remoto do estado do Terraform
- **Versionamento**: Habilitado para backup automático
- **Criptografia**: SSE-S3 para dados em repouso
- **Lifecycle**: Política de retenção configurada

#### 2. Tabela DynamoDB (`terraform-locks`)
- **Propósito**: Implementar state locking para o Terraform
- **Chave primária**: `LockID` (string)
- **TTL**: Configurado para limpeza automática de locks expirados
- **Capacidade**: On-demand para escalabilidade automática

#### 3. Security Group (`backend-challenge-security-group`)
- **Porta 80 (HTTP)**: Acesso público para a aplicação
- **Porta 22 (SSH)**: Acesso SSH para administração
- **Egress**: Acesso total à internet para downloads e atualizações

#### 4. Key Pair (`terraform-keypair`)
- Chave SSH para acesso à instância EC2
- Baseada no arquivo `ssh/id_rsa.pub`

#### 5. Instância EC2 (`backend-challenge-server`)
- **AMI**: Amazon Linux 2 (`ami-0de716d6197524dd9`)
- **Tipo**: `t3.micro` (1 vCPU, 1GB RAM)
- **Região**: `us-east-1`
- **User Data**: Script de inicialização automática

### Script de Inicialização (user_data.sh)

O script `user_data.sh` executa automaticamente na primeira inicialização da instância:

```bash

#!/bin/bash
sudo su
yum update -y                                                  # Atualiza o sistema
yum install -y docker                                          # Instala o Docker
service docker start                                           # Inicia o serviço Docker
usermod -a -G docker ec2-user                                  # Adiciona usuário ao grupo docker
docker run -p 80:8080 viniciusleonel/backend-challenge:latest  # Executa a aplicação
```

### Como Usar o Terraform

#### Pré-requisitos

1. **Terraform instalado** (versão 1.0+)
2. **AWS CLI configurado** com credenciais válidas
3. **Chave SSH** em `ssh/id_rsa.pub`

#### Comandos Básicos

```bash

# Navegar para o diretório Terraform
cd src/main/java/br/dev/viniciusleonel/backend_challenge/infra/terraform

# Inicializar o Terraform (configura o backend S3 automaticamente)
terraform init

# Verificar o plano de execução
terraform plan

# Aplicar a configuração
terraform apply

# Destruir a infraestrutura
terraform destroy
```

#### Configuração do Backend Remoto

O projeto já está configurado para usar o backend S3. Durante a inicialização (`terraform init`), o Terraform:

1. **Conecta ao bucket S3** `backend-challenge-terraform-state-bucket`
2. **Configura o DynamoDB** para state locking
3. **Baixa o estado atual** se existir
4. **Prepara o ambiente** para operações remotas

**Vantagens do Backend Remoto:**
- **Colaboração**: Múltiplos desenvolvedores podem trabalhar simultaneamente
- **Segurança**: Estado armazenado de forma segura na AWS
- **Backup**: Versionamento automático do estado
- **Locking**: Prevenção de conflitos de concorrência
- **Auditoria**: Histórico completo de mudanças

#### Configuração da AWS

Certifique-se de que suas credenciais AWS estão configuradas:

```bash

# Via AWS CLI
aws configure

# Ou via variáveis de ambiente
export AWS_ACCESS_KEY_ID="sua_access_key"
export AWS_SECRET_ACCESS_KEY="sua_secret_key"
export AWS_DEFAULT_REGION="us-east-1"
```

### Personalização da Infraestrutura

#### Alterar Região AWS
```hcl
provider "aws" {
  region = "us-west-2"  # Alterar para região desejada
}
```

#### Alterar Tipo de Instância
```hcl
resource "aws_instance" "backend-challenge-server" {
  ami = "ami-0de716d6197524dd9"
  instance_type = "t3.small"  # Alterar para tipo desejado
  # ... resto da configuração
}
```

#### Adicionar Tags
```hcl
resource "aws_instance" "backend-challenge-server" {
  # ... configuração existente
  
  tags = {
    Name        = "Backend-Challenge-Server"
    Environment = "Production"
    Project     = "Backend-Challenge"
    Owner       = "Vinicius Leonel"
  }
}
```

### Monitoramento e Acesso

#### Acesso SSH
```bash

# Conectar via SSH (substitua pelo IP público da instância)
ssh -i ssh/id_rsa ec2-user@<IP_PUBLICO_EC2>
```

#### Acesso à Aplicação
- **URL**: `http://<IP_PUBLICO_EC2>`
- **Swagger**: `http://<IP_PUBLICO_EC2>/swagger-ui.html`
- **Health Check**: `http://<IP_PUBLICO_EC2>/monitoring/health`

#### Logs da Aplicação
```bash

# Ver logs do container Docker
docker logs $(docker ps -q --filter ancestor=viniciusleonel/backend-challenge:latest)

# Ver logs do sistema
sudo journalctl -u docker
```

### Segurança e Boas Práticas

#### Recomendações de Segurança
1. **Restringir acesso SSH**: Limitar `cidr_blocks` para IPs específicos
2. **Usar VPC**: Configurar rede privada para isolamento
3. **IAM Roles**: Usar roles IAM ao invés de credenciais hardcoded
4. **Backup**: Manter backup do estado do Terraform
5. **Versionamento**: Versionar todas as mudanças de infraestrutura
6. **Bucket S3**: Configurar políticas de acesso restritivas
7. **DynamoDB**: Implementar TTL para limpeza automática de locks
8. **State Locking**: Sempre usar DynamoDB para evitar conflitos

#### Exemplo de Security Group Mais Restritivo
```hcl
resource "aws_security_group" "backend-challenge-group" {
  name = "backend-challenge-security-group"
  description = "Security group com acesso restrito"

  ingress {
    from_port = 80
    to_port = 80
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]  # HTTP público
  }

  ingress {
    from_port = 22
    to_port = 22
    protocol = "tcp"
    cidr_blocks = ["192.168.1.0/24"]  # Apenas rede local
  }

  egress {
    from_port = 0
    to_port = 65535
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}
```

### Troubleshooting

#### Problemas Comuns

1. **Erro de credenciais AWS**
   ```bash
   # Verificar configuração
   aws sts get-caller-identity
   ```

2. **Erro de chave SSH**
   ```bash
   # Verificar permissões
   chmod 400 ssh/id_rsa
   ```

3. **Instância não inicia**
   ```bash
   # Verificar logs de inicialização
   aws ec2 get-console-output --instance-id <INSTANCE_ID>
   ```

4. **Aplicação não responde**
   ```bash
   # Verificar se o Docker está rodando
   sudo systemctl status docker
   
   # Verificar containers ativos
   docker ps
   ```

### Limpeza e Destruição

Para evitar custos desnecessários, sempre destrua a infraestrutura quando não estiver usando:

#### Via Terraform Local
```bash

# Destruir todos os recursos
terraform destroy

# Confirmar destruição
yes
```

#### Via GitHub Actions (Recomendado)
O projeto inclui um workflow automatizado para destruição da infraestrutura que oferece execução consistente em ambiente controlado, logs centralizados para auditoria, e não requer configuração local do Terraform. Sua execução é manual e segura, exigindo intervenção humana para confirmação.

**⚠️ Atenção**: Ambos os métodos removem permanentemente todos os recursos criados pelo Terraform.


---

## Criado por:

### Vinicius Leonel

### Linkedin: https://www.linkedin.com/in/viniciuslps
