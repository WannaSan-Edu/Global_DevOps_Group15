FROM eclipse-temurin:25
COPY ./target/woldReport.jar /tmp/
WORKDIR /tmp
ENTRYPOINT ["java", "-jar", "worldReport.jar"]
