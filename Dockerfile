## Dockerfile for java application

FROM maven as build-app
WORKDIR /app
COPY .mvn .mvn
COPY lib lib
COPY pom.xml pom.xml
# Install maven dependencies to local repository so that they could be used in the pom.xml
RUN mvn install:install-file -Dfile=./javafo-2.2-main.jar -DgroupId=javafo -DartifactId=javafo -Dversion=2.2 -Dpackaging=jar
RUN mvn dependency:resolve
COPY src/main src/main
RUN mvn install -DskipTests

FROM openjdk:17-slim as serve-app
WORKDIR /app
COPY --from=build-app /app/target/ChessGrinder-*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar", "--spring.config.additional-location=optional:/app/config/"]
