FROM bellsoft/liberica-openjdk-alpine:21.0.2

ARG UID=1001
RUN adduser -S sodata -u $UID

WORKDIR /work
RUN chown $UID:0 . && \
    chmod 0775 . && \
    ls -la
VOLUME ["/work"]

WORKDIR /datahub
RUN chown $UID:0 . && \
    chmod 0775 . && \
    ls -la
VOLUME ["/datahub"]

ENV HOME=/app
WORKDIR $HOME

COPY build/libs/datahub-*-exec.jar ./application.jar

RUN chown $UID:0 . && \
    chmod 0775 . && \
    ls -la

USER $UID
EXPOSE 8080
ENV LOG4J_FORMAT_MSG_NO_LOOKUPS=true
CMD java -XX:MaxRAMPercentage=80.0 -jar application.jar
