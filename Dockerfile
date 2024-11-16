FROM gradle:latest as builder
COPY . /workspace/app
WORKDIR /workspace/app
RUN gradle build -Dproduction=true --no-daemon

FROM openjdk:21-jdk
COPY --from=builder /workspace/app/build/libs/*.jar /app/backend.jar
CMD ["java", "-jar", "/app/backend.jar"]