FROM openjdk:17-jdk-alpine
ADD ./build/libs/lottofinder-0.0.1-SNAPSHOT.jar lottofinder.jar

ENTRYPOINT ["java", "-jar", "/lottofinder.jar"]