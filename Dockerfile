FROM adoptopenjdk/openjdk11:latest
ENV oauth.google.token-url https://oauth2.googleapis.com/token
ENV oauth.google.user-info-url https://www.googleapis.com/oauth2/v1/userinfo
ENV oauth.google.id 522113449269-9tbdnt6fr4ic7p4f60fgm19p905tvanp.apps.googleusercontent.com
ENV oauth.google.secret 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
ENV oauth.google.redirect http://localhost
EXPOSE 8080
ARG JAR_FILE=out/artifacts/movieguide_jar/movieguide.jar
COPY ${JAR_FILE} movieguide.jar
ADD /images /images
ENTRYPOINT ["java","-jar","/movieguide.jar"]