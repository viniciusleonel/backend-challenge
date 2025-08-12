# Backend Challenge

## Descrição

Este é um projeto Spring Boot que implementa um sistema de validação de JWT (JSON Web Tokens) com validações específicas para claims personalizados. O projeto demonstra a aplicação de princípios SOLID, através de uma arquitetura extensível de validadores.

## Tecnologias Utilizadas

- **Java 21**
- **Spring Boot 3.5.4**
- **Maven**
- **Auth0 JWT Library 4.4.0**
- **Docker**

## Funcionalidades

### Validação de JWT

O sistema valida JWT tokens através de um endpoint REST que verifica:

1. **Estrutura do Token**: Decodifica o JWT e verifica se é válido
2. **Quantidade de Claims**: Confirma se o token contém exatamente o número esperado de claims
3. **Validação Individual**: Aplica validadores específicos para cada claim

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

**Resposta:**
- `true`: Token válido
- `false`: Token inválido

**Exemplo de uso:**
```bash
GET /api/validate?token={{token}}
```

## Arquitetura

### Princípios SOLID

O projeto busca implementar os princípios SOLID através de:

- **Interface `ClaimValidator`**: Define o contrato para validadores
- **Implementações específicas**: Cada tipo de claim tem seu próprio validador
- **Extensibilidade**: Novos validadores podem ser adicionados sem modificar código existente
- **`ClaimValidatorsList`**: Centraliza a configuração de validadores ativos

### Padrões de Design

- **Strategy Pattern**: Diferentes estratégias de validação implementam a mesma interface
- **Factory Pattern**: `ClaimValidatorsList` atua como factory para validadores
- **Template Method**: Estrutura comum de validação definida em `JwtValidator`

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

## CI/CD

O projeto utiliza GitHub Actions para automatizar o processo de integração e entrega contínua.

### Pipeline CI (Continuous Integration)

O workflow `ci.yml` é executado em:
- Push para a branch `main`
- Pull requests para a branch `main`

**Etapas do CI:**
1. **Checkout do código** - Baixa o código fonte
2. **Configuração do JDK 21** - Configura o ambiente Java
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
```

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

## Contribuição

Para adicionar novos validadores:

1. Implemente a interface `ClaimValidator`
2. Adicione o novo validador em `ClaimValidatorsList.getValidators()`
3. Implemente os testes correspondentes

## Licença

Este projeto é um desafio de desenvolvimento e não possui licença específica definida.
