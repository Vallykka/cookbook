#FROM valerauko/clojure-graal:openjdk-11 as builder
FROM clojure:openjdk-16-lein-alpine as builder

WORKDIR /usr/src/cookbook/
COPY project.clj /usr/src/cookbook/
COPY resources/ /usr/src/cookbook/resources
COPY src/ /usr/src/cookbook/src

RUN lein with-profile dev uberjar
#RUN native-image --enable-url-protocols=http --report-unsupported-elements-at-runtime --allow-incomplete-classpath --no-server -J-Xmx3G -jar target/cookbook-0.1.0-SNAPSHOT-standalone.jar

FROM openjdk:16-jdk-alpine3.12
#COPY --from=builder /usr/src/cookbook/cookbook-0.1.0-SNAPSHOT-standalone /cookbook
#CMD ["./cookbook"]

COPY --from=builder /usr/src/cookbook/target/cookbook-0.1.0-SNAPSHOT-standalone.jar /cookbook-0.1.0-SNAPSHOT-standalone.jar
CMD ["java", "-jar", "/cookbook-0.1.0-SNAPSHOT-standalone.jar"]