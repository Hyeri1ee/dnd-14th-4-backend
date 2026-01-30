FROM eclipse-temurin:25-jdk-alpine AS build
WORKDIR /app

#필요한 패키지 설치
RUN apk add --no-cache bash curl unzip

#Gradle 설치
ENV GRADLE_VERSION=8.10.2
ENV GRADLE_HOME=/opt/gradle
RUN curl -L https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip -o gradle.zip && \
    unzip gradle.zip && \
    mv gradle-${GRADLE_VERSION} ${GRADLE_HOME} && \
    rm gradle.zip
ENV PATH="${GRADLE_HOME}/bin:${PATH}"

COPY build.gradle settings.gradle ./
COPY gradle ./gradle

RUN gradle dependencies --no-daemon || true

COPY src ./src

# 빌드
RUN gradle build -x test --no-daemon

FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
