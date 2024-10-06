FROM openjdk:17-oracle
LABEL authors="chern"
# Créez un répertoire pour votre application
WORKDIR /app
# Copiez le fichier security_jar3 dans le répertoire de l'image test
COPY . .
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "security.jar"]