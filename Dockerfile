FROM maven as build-backend
WORKDIR /app
COPY .mvn .mvn
COPY lib lib
COPY pom.xml pom.xml
# Install maven dependencies to local repository so that they could be used in the pom.xml
RUN mvn install:install-file -Dfile=./lib/javafo-2.2-main.jar -DgroupId=javafo -DartifactId=javafo -Dversion=2.2 -Dpackaging=jar
RUN mvn dependency:resolve
COPY src/main src/main
RUN mvn install -DskipTests

FROM node:16-slim as build-frontend
WORKDIR /app
COPY frontend/package.json package.json
RUN npm install
COPY frontend .
RUN npm run build

FROM nginx:stable-alpine as serve-app
RUN apk add openjdk17
COPY nginx.conf /etc/nginx/nginx.conf
EXPOSE 80
WORKDIR /app
COPY --from=build-frontend /app/build/ /usr/share/nginx/html/
COPY --from=build-backend /app/target/ChessGrinder-*.jar app.jar
RUN nginx
CMD ["java", "-jar", "app.jar"]
