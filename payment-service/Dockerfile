# 1. Imagem base ultra-leve com Java 21
FROM eclipse-temurin:21-jre-alpine

# 2. Define a pasta de trabalho dentro do container
WORKDIR /app

# 3. Pega qualquer arquivo .jar que estiver dentro da pasta target
ARG JAR_FILE=target/*.jar

# 4. Copia o .jar da sua máquina para dentro do container chamando de "app.jar"
COPY ${JAR_FILE} app.jar

# 5. O comando que o container vai rodar quando ligar
ENTRYPOINT ["java", "-jar", "app.jar"]