FROM eclipse-temurin:25
COPY ./target/worldReport.jar /tmp/
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "worldReport.jar"]
