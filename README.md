# Backend Challenge

## Descrição

Este é um projeto Spring Boot que implementa um sistema de validação de JWT (JSON Web Tokens) com validações específicas para claims personalizados. O projeto demonstra a aplicação de princípios SOLID, através de uma arquitetura extensível de validadores e tratamento robusto de exceções.

## Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.5.4**
- **Maven 3.9.8+**
- **Auth0 JWT Library 4.4.0**
- **JUnit 4.13.2** (para testes)
- **SLF4J + Logback** (para logging)
- **Docker**

## Funcionalidades

### Validação de JWT

O sistema valida JWT tokens através de um endpoint REST que verifica:

1. **Estrutura do Token**: Decodifica o JWT e verifica se é válido
2. **Quantidade de Claims**: Confirma se o token contém exatamente 3 claims (Name, Role, Seed)
3. **Validação Individual**: Aplica validadores específicos para cada claim
4. **Tratamento de Exceções**: Gerencia diferentes tipos de erros com códigos de status HTTP apropriados

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

## API Endpoints

### GET /api/validate

Valida um JWT token.

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

## Arquitetura

### Princípios SOLID

O projeto implementa os princípios SOLID através de:

- **Interface `ClaimValidator`**: Define o contrato para validadores
- **Implementações específicas**: Cada tipo de claim tem seu próprio validador
- **Extensibilidade**: Novos validadores podem ser adicionados sem modificar código existente
- **`JwtValidationConfig`**: Centraliza a configuração de validadores ativos e roles válidos
- **Open/Closed Principle**: Sistema aberto para extensão, fechado para modificação

### Padrões de Design

- **Strategy Pattern**: Diferentes estratégias de validação implementam a mesma interface
- **Factory Pattern**: `JwtValidationConfig` atua como factory para validadores
- **Template Method**: Estrutura comum de validação definida em `JwtValidator`
- **Exception Handler Pattern**: Tratamento centralizado de exceções

### Estrutura de Pacotes

```
src/main/java/br/dev/viniciusleonel/backend_challenge/
├── controller/          # Controladores REST
├── infra/               # Infraestrutura da aplicação
│   └── exception/       # Tratamento de exceções
│       └── handler/     # Handlers globais de exceção
├── utils/               # Utilitários (geração de JWT, validação de números)
├── validators/          # Validadores de claims
└── BackendChallengeApplication.java
```

## Tratamento de Exceções

A aplicação possui um sistema robusto de tratamento de exceções:

### GlobalExceptionHandler

- **InvalidClaimException**: Retorna status 422 (Unprocessable Entity) para claims inválidos
- **JWTDecodeException**: Retorna status 400 (Bad Request) para tokens malformados

### Tipos de Erro

1. **Claims Inválidos**: Nome com números, role inválido, seed não primo
2. **Token Malformado**: Formato JWT inválido
3. **Claims Ausentes**: Número incorreto de claims (deve ser exatamente 3)

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

docker run -p 8080:8080 backend-challenge
```

## Testes

Execute os testes unitários:

```bash

  mvn test
  
```

### Cobertura de Testes

O projeto inclui testes abrangentes para:
- **Validadores**: NameValidator, RoleValidator, SeedValidator
- **JWT Validator**: Validação completa de tokens
- **Controller**: Endpoints da API

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

# Formato de saída dos logs
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
```

## Sistema de Logging

A aplicação implementa um sistema robusto de logging utilizando SLF4J com Logback, fornecendo rastreabilidade completa das operações de validação de JWT.

### Configuração de Logs

- **Nível Global**: INFO (configurado em `logging.level.root`)
- **Nível da Aplicação**: DEBUG (configurado para o pacote principal)
- **Formato**: Timestamp, Thread, Nível, Logger e Mensagem

### Logs Implementados

#### Controller (ApiController)
- **INFO**: Endpoint chamado, JWT válido/inválido
- **ERROR**: JWT inválido

#### JWT Validator
- **INFO**: Início da validação, total de claims válido, chamada de validadores, validação bem-sucedida
- **DEBUG**: Verificação de total de claims
- **ERROR**: Total de claims inválido

#### JWT Decoder
- **INFO**: Início da decodificação
- **DEBUG**: JWT decodificado com sucesso
- **ERROR**: Falha na decodificação

#### Validadores de Claims

##### NameValidator
- **INFO**: Início da validação da claim Name
- **DEBUG**: Nome válido
- **ERROR**: Nome nulo/vazio, tamanho excedido, contém números

##### RoleValidator
- **INFO**: Início da validação da claim Role
- **DEBUG**: Role válido
- **ERROR**: Role nulo/vazio, role inválido

##### SeedValidator
- **INFO**: Início da validação da claim Seed
- **DEBUG**: Seed válido
- **ERROR**: Seed nulo/vazio, seed inválido, seed não é primo

### Exemplo de Saída de Logs

```
2024-01-15 10:30:15 [http-nio-8080-exec-1] INFO  b.d.v.b.c.ApiController - Endpoint /api/validate chamado
2024-01-15 10:30:15 [http-nio-8080-exec-1] INFO  b.d.v.b.v.JwtValidator - Iniciando validacao do JWT
2024-01-15 10:30:15 [http-nio-8080-exec-1] INFO  b.d.v.b.u.JwtDecoder - Iniciando decodificacao do JWT
2024-01-15 10:30:15 [http-nio-8080-exec-1] DEBUG b.d.v.b.u.JwtDecoder - JWT decodificado com sucesso
2024-01-15 10:30:15 [http-nio-8080-exec-1] DEBUG b.d.v.b.v.JwtValidator - Verificando total de claims
2024-01-15 10:30:15 [http-nio-8080-exec-1] INFO  b.d.v.b.v.JwtValidator - Total de claims valido: 3
2024-01-15 10:30:15 [http-nio-8080-exec-1] INFO  b.d.v.b.v.JwtValidator - Chamando validadores de claims
2024-01-15 10:30:15 [http-nio-8080-exec-1] INFO  b.d.v.b.v.NameValidator - Iniciando validacao da claim Name
2024-01-15 10:30:15 [http-nio-8080-exec-1] DEBUG b.d.v.b.v.NameValidator - Nome valido: Toninho Araujo
2024-01-15 10:30:15 [http-nio-8080-exec-1] INFO  b.d.v.b.v.RoleValidator - Iniciando validacao da claim Role
2024-01-15 10:30:15 [http-nio-8080-exec-1] DEBUG b.d.v.b.v.RoleValidator - Role valido: Admin
2024-01-15 10:30:15 [http-nio-8080-exec-1] INFO  b.d.v.b.v.SeedValidator - Iniciando validacao da claim Seed
2024-01-15 10:30:15 [http-nio-8080-exec-1] DEBUG b.d.v.b.v.SeedValidator - Seed valido: 7841
2024-01-15 10:30:15 [http-nio-8080-exec-1] INFO  b.d.v.b.v.JwtValidator - JWT passou nas validacoes
2024-01-15 10:30:15 [http-nio-8080-exec-1] INFO  b.d.v.b.c.ApiController - JWT valido
```

### Benefícios do Sistema de Logging

1. **Rastreabilidade**: Logs detalhados de todas as operações de validação
2. **Debugging**: Nível DEBUG para desenvolvimento e troubleshooting
3. **Monitoramento**: Logs estruturados para análise de performance e erros
4. **Auditoria**: Histórico completo de validações de JWT
5. **Manutenção**: Facilita a identificação e correção de problemas

## Exemplo de JWT Válido

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

## Contribuição

Para adicionar novos validadores:

1. Implemente a interface `ClaimValidator`
2. Adicione o novo validador em `JwtValidationConfig.getValidators()`
3. Implemente os testes correspondentes
4. Atualize a documentação

Para adicionar novos roles:

1. Adicione o novo role na lista `VALID_ROLES` em `JwtValidationConfig.java`
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

## Licença

Este projeto é um desafio de desenvolvimento e não possui licença específica definida.
