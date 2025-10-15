FROM maven:3.9.4-amazoncorretto-17 as build-backend
WORKDIR /app
COPY .mvn .mvn
COPY lib lib
COPY pom.xml pom.xml
ENV MAVEN_ARGS="--batch-mode --no-transfer-progress --show-version --errors"
# Install maven dependencies to local repository so that they could be used in the pom.xml
RUN mvn install:install-file -Dfile=./lib/javafo-2.2-main.jar -DgroupId=javafo -DartifactId=javafo -Dversion=2.2 -Dpackaging=jar
RUN mvn dependency:resolve
COPY src/main src/main
ARG VERSION="0.0.1"
RUN mvn versions:set -DnewVersion=${VERSION}
RUN mvn spring-boot:build-info
RUN mvn install -DskipTests

FROM node:22-slim as build-frontend
WORKDIR /app
COPY frontend/package.json package.json
COPY frontend/package-lock.json package-lock.json
RUN npm ci
COPY frontend/public ./public
COPY frontend/tsconfig.json ./
COPY frontend/vite.config.ts ./
COPY frontend/postcss.config.cjs ./
COPY frontend/eslint.config.mjs ./
COPY frontend/index.html ./
COPY frontend/src ./src
COPY frontend/.env* ./
RUN ls
RUN npm run build

# Use java + ubuntu docker image.
FROM eclipse-temurin:17-noble as serve-app
RUN apt update
RUN apt install -y nginx
COPY --from=build-frontend /app/build/ /usr/share/nginx/html/
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
WORKDIR /app
COPY --from=build-backend /app/target/ChessGrinder-*.jar app.jar
CMD service nginx restart && java -jar app.jar
