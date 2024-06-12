1. `./sql-client.sh`

Then in the flink client

```
CREATE TABLE KafkaTable (
  `name` STRING,
  `id` BIGINT
) WITH (
  'connector' = 'kafka',
  'topic' = 'mytopic',
  'properties.bootstrap.servers' = 'kafka:9092',
  'properties.group.id' = 'testGroup',
  'scan.startup.mode' = 'earliest-offset',
  'format' = 'csv'
);
```

then

```
SELECT * FROM KafkaTable;
```

and you should see:
```
                           name                   id
                     helloworld                    1
```
