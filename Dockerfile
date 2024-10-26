#bouhenimohamed
FROM maven:3.8.4-openjdk-17 AS buildWORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=build /app/target/Foyer-0.0.1-SNAPSHOT.jar /app/Foyer.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/Foyer.jar"]