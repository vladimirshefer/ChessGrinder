# Install maven dependencies to local repository so that they could be used in the pom.xml
mvn install:install-file -Dfile=./javafo-2.2-main.jar -DgroupId=javafo -DartifactId=javafo -Dversion=2.2 -Dpackaging=jar
