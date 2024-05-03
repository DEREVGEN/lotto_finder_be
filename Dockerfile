FROM openjdk:17-jdk-alpine

# 시간설정
RUN apk add tzdata && ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime

ADD ./build/libs/lottofinder-0.0.1-SNAPSHOT.jar lottofinder.jar

# deploy 프로퍼티 파일로 실행.
ENTRYPOINT ["java", "-jar", "lottofinder.jar", "--spring.profiles.active=deploy"]