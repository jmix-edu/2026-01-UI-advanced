FROM bellsoft/liberica-openjre-alpine:21
COPY ./build/libs/timesheets-0.0.1-SNAPSHOT.jar app.jar
ENV SPRING_PROFILES_ACTIVE=prod
ENV DEFAULT_USERNAME=admin
ENV VAULT_TOKEN=myroot
ENTRYPOINT ["java", "-jar", "/app.jar"]
