FROM eclipse-temurin:21-jre-alpine

COPY target/SaveMi-0.0.1-SNAPSHOT.jar SaveMi.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","SaveMi.jar"]
