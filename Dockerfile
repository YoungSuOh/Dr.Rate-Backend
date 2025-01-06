FROM openjdk:17-jdk-slim

# 타임존 설정 (Asia/Seoul)
ENV TZ=Asia/Seoul
RUN apt-get update && apt-get install -y tzdata && \
    ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone && \
    apt-get clean

# JAR 파일 복사
ARG JAR_FILE=build/libs/Dr.Rate-Backend-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]
