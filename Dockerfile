# Utiliser une image de Maven avec JDK 17
FROM maven:3.9.8-eclipse-temurin-17 AS build

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers de configuration Maven
COPY pom.xml .

# Copier le code source
COPY src ./src

# Construire le projet
RUN mvn clean package -DskipTests

# Étape de paquetage
FROM eclipse-temurin:17-jdk

# Copier le JAR construit à partir de l'étape de construction
COPY --from=build /app/target/security-0.0.1-SNAPSHOT.jar app.jar

# Exposer le port
EXPOSE 8080

# Définir le point d'entrée
ENTRYPOINT ["java", "-jar", "app.jar"]
