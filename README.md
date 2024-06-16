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

To query via the SQL Gateway (from Erin's gateway):

```
./query.py 8083 "SELECT * FROM KafkaTable;"
./query.py 8083 "SELECT * FROM ErinTable;" # succeeds
./query.py 8083 "SELECT * FROM EricTable;" # fails
```

To query via the SQL Gateway (from Eric's gateway):

```
./query.py 8084 "SELECT * FROM KafkaTable;"
./query.py 8084 "SELECT * FROM ErinTable;" # fails
./query.py 8084 "SELECT * FROM EricTable;" # succeeds
```
