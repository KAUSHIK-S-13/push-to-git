FROM openjdk:11
ENV TZ="Asia/Kolkata"
ADD target/D2D-0.0.1-SNAPSHOT.jar D2D-0.0.1-SNAPSHOT.jar
EXPOSE 8050
CMD java -jar D2D-0.0.1-SNAPSHOT.jar
