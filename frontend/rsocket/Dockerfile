FROM amazoncorretto:11
ARG DEPENDENCY=build/unpacked
WORKDIR /app
COPY ${DEPENDENCY}/BOOT-INF/lib lib
COPY ${DEPENDENCY}/META-INF META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes .
COPY ci/configuration.yml application.yml
ENTRYPOINT ["java", "-cp", ".:lib/*", "com.sword.signature.rsocket.RSocketApplicationKt"]
EXPOSE 8080