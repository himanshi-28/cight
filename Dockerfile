FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN sh ./mvnw -B -DskipTests dependency:go-offline
COPY src src
RUN sh ./mvnw -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --uid 10001 cight
COPY --from=build /workspace/target/cight-0.0.1-SNAPSHOT.jar app.jar
USER cight
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=70 -XX:+ExitOnOutOfMemoryError"
EXPOSE 8028
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
