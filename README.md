# Uber Web Application Demo
A kafka project that demonstrates the uber application in web

### Pre-requisite
1. sbt (Scala build tool)
2. Scala

### 1. kafka-server
This repo contains scala code for starting a single node kafka server.
To start a single node Kafka server one can use the following command:
```bash
cd kafka-server
sbt run
```

### 2. uber-web-app
This repo contains the uber web application written using Playframework.
To start the uber web application, one first start a kafka server and then run the following command:
```bash
cd uber-web-app
sbt run
```
After running the uber-web-app, one can go to the following url and play with the application: <br />
http://localhost:9000/driver <br />
http://localhost:9000/rider


### Inspiration
This project was inspired from koobar application demo on kafka by [@James Ward](https://github.com/jamesward). <br />
* [Introduction to Apache Kafka by James Ward](https://www.youtube.com/watch?v=UEg40Te8pnE&t=891s)
