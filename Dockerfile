FROM openjdk
ADD target/eventos-0.0.1-SNAPSHOT.jar eventos-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar", "/eventos-0.0.1-SNAPSHOT.jar"]
EXPOSE 9400