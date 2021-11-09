FROM adoptopenjdk/openjdk8
VOLUME /tmp
ADD register-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
EXPOSE 1111