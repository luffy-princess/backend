FROM openjdk:17-jdk-slim

WORKDIR /app
COPY ./ /app

# Maven을 사용하여 프로젝트를 빌드하고 JAR 파일을 생성
RUN ./mvnw clean package

# 빌드 후 소스 코드 및 불필요한 파일 삭제
RUN rm -rf /app/src /app/.mvn /app/mvnw /app/mvnw.cmd

# JAR 파일을 실행
ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar /app/target/$(ls /app/target | grep '.jar$')"]
