# Usar uma imagem oficial do OpenJDK
FROM eclipse-temurin:21-jdk

# Criar diretório para o app
WORKDIR /app

# Copiar o jar gerado para dentro do container
COPY target/*.jar backend-challenge-0.0.1-SNAPSHOT.jar

# Expor a porta padrão do Spring Boot
EXPOSE 8080

# Comando para rodar a aplicação
ENTRYPOINT ["java", "-jar", "backend-challenge-0.0.1-SNAPSHOT.jar"]
