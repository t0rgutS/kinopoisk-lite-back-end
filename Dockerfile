FROM adoptopenjdk/openjdk11:latest
EXPOSE 8080
ARG JAR_FILE=out/artifacts/movieguide_jar/movieguide.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/movieguide.jar"]