FROM fabric8/java-jboss-openjdk8-jdk:1.2.1

ENV JAVA_APP_JAR demo-swarm.jar
ENV AB_OFF true
ENV JAVA_OPTIONS -Xmx512m

EXPOSE 8080

ADD target/demo-swarm.jar /app/