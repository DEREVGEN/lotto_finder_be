FROM openjdk:17-jdk-alpine
ADD ./build/libs/lottofinder-0.0.1-SNAPSHOT.jar lottofinder.jar

# deploy 프로퍼티 파일로 실행.
ENTRYPOINT ["java", "-jar", "lottofinder.jar", "--spring.profiles.active=deploy"]