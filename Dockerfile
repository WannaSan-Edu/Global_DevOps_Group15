FROM eclipse-temurin:25
COPY ./target/semApp.jar /tmp/
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "worldReport.jar"]