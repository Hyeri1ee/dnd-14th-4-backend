FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

# 필요한 패키지 설치
RUN apk add --no-cache bash

# Gradle wrapper 파일 복사
COPY gradlew ./
COPY gradle ./gradle
RUN chmod +x gradlew

# Gradle 설정 파일 복사
COPY build.gradle settings.gradle ./

# 의존성 다운로드 (캐시 최적화)
RUN ./gradlew dependencies --no-daemon || true

# 소스 코드 복사
COPY src ./src

# 빌드
RUN ./gradlew build -x test --no-daemon

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
