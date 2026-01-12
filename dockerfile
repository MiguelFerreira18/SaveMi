FROM openjdk:21

COPY target/SaveMe.0.0.1-SNAPSHOT.jar SaveMe.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","SaveMe.jar"]
