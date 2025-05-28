# 베이스 이미지: Azul Zulu OpenJDK 17
FROM eclipse-temurin:17-jdk as builder

# JAR 빌드 산출물을 복사할 디렉토리
WORKDIR /app

# Gradle Wrapper를 포함한 빌드 스크립트 복사
COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle settings.gradle ./

# 의존성만 미리 내려받기
RUN ./gradlew dependencies --no-daemon

# 소스 전체 복사 & JAR 빌드
COPY src/ src/
RUN ./gradlew bootJar --no-daemon

# --- Runtime Stage ---
FROM azul/zulu-openjdk:17-jre
WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 프로덕션 프로파일 활성화
ENV SPRING_PROFILES_ACTIVE=prod

# 도커 실행 명령
ENTRYPOINT ["java","-jar","/app/app.jar"]