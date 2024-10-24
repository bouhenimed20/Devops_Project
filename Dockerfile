# Étape 1 : Construction de l'image avec Maven
FROM maven:3.9.9-openjdk-17 AS build
WORKDIR /app

# Copier le pom.xml et télécharger les dépendances
COPY pom.xml .
RUN mvn dependency:go-offline

# Copier le code source et construire le projet
COPY src ./src
RUN mvn clean package -DskipTests

# Étape 2 : Exécution de l'application
FROM openjdk:17-jdk-slim
WORKDIR /app

# Copier le fichier JAR généré dans l'étape précédente
COPY --from=build /app/target/Foyer-0.0.1-SNAPSHOT.jar /app/Foyer.jar

# Exposer le port sur lequel l'application va tourner
EXPOSE 8081

# Commande pour démarrer l'application
ENTRYPOINT ["java", "-jar", "/app/Foyer.jar"]