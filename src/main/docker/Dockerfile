FROM openjdk:8-jdk-alpine
VOLUME /tmp
RUN apk add --update curl && rm -rf /var/cache/apk/*
ENV JAVA_OPTS="-Xms64m -Xmx64m -XX:MaxMetaspaceSize=128m"
COPY /maven/${project.build.finalName}.jar ${project.build.finalName}.jar
CMD java $JAVA_OPTS -jar ${project.build.finalName}.jar
