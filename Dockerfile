FROM openjdk:17-jdk
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} slamtalk.jar
ENTRYPOINT ["java", "-jar", "/slamtalk.jar"]
