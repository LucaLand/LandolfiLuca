##FROM java:8
FROM openjdk:12.0.2

ADD ./app/build/distributions/app-2.0.tar  /

WORKDIR /app-2.0/bin

# COPY ./*.pl ./
RUN ls

CMD ["bash", "app"]

### gradlew distTar
### docker build -t boundarywalkeractor:2.0 .
### docker-compose -f docker-compose.yaml up



### docker run -p 8010:8010 -ti --rm steprobot:2.0 /bin/bash //to use the console
### docker run -ti -p 8090:8090  -p 8091:8091 --rm virtualrobotdisi:2.0 /bin/sh

