# Install maven dependencies to local repository so that they could be used in the pom.xml
./mvnw install:install-file -Dfile=./lib/javafo-2.2-main.jar -DgroupId=javafo -DartifactId=javafo -Dversion=2.2 -Dpackaging=jar
