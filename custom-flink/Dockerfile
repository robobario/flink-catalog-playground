FROM flink:1.19.0-java17
RUN wget -P /opt/flink/lib https://repo.maven.apache.org/maven2/org/apache/flink/flink-sql-connector-kafka/3.2.0-1.19/flink-sql-connector-kafka-3.2.0-1.19.jar
COPY flink-catalog-playground-1.0-SNAPSHOT.jar /opt/flink/lib/