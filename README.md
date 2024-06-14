1. `./sql-client.sh`

Then in the flink client

```
SELECT * FROM KafkaTable;
```

and you should see:
```
                           name                   id
                     helloworld                    1
```

To demonstrate how we can augment the table metadata by supplying the bootstrap servers configuration:

```
CREATE TABLE KafkaTable2 (
  `name` STRING,
  `id` BIGINT
) WITH (
  'connector' = 'kafka',
  'topic' = 'mytopic',
  'properties.bootstrap.cluster' = 'my-kafka',
  'properties.group.id' = 'testGroup',
  'scan.startup.mode' = 'earliest-offset',
  'format' = 'csv'
);
```
Note that we set properties.bootstrap.cluster to my-cluster, then the custom KafkaCatalog augments
in the bootstrap servers

then
```
select * from KafkaTable2;
```

and you should see:

```
                           name                   id
                     helloworld                    1
```

## SQL Gateway

To query via the SQL Gateway:

```
./query.py "SELECT * FROM KafkaTable;"
```
