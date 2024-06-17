package com.github.robobario;

import org.apache.flink.table.api.DataTypes;
import org.apache.flink.table.api.Schema;
import org.apache.flink.table.catalog.AbstractCatalog;
import org.apache.flink.table.catalog.Catalog;
import org.apache.flink.table.catalog.CatalogBaseTable;
import org.apache.flink.table.catalog.CatalogDatabase;
import org.apache.flink.table.catalog.CatalogFunction;
import org.apache.flink.table.catalog.CatalogPartition;
import org.apache.flink.table.catalog.CatalogPartitionSpec;
import org.apache.flink.table.catalog.CatalogTable;
import org.apache.flink.table.catalog.Column;
import org.apache.flink.table.catalog.DefaultCatalogTable;
import org.apache.flink.table.catalog.ObjectPath;
import org.apache.flink.table.catalog.ResolvedCatalogTable;
import org.apache.flink.table.catalog.ResolvedSchema;
import org.apache.flink.table.catalog.exceptions.CatalogException;
import org.apache.flink.table.catalog.exceptions.DatabaseAlreadyExistException;
import org.apache.flink.table.catalog.exceptions.DatabaseNotEmptyException;
import org.apache.flink.table.catalog.exceptions.DatabaseNotExistException;
import org.apache.flink.table.catalog.exceptions.FunctionAlreadyExistException;
import org.apache.flink.table.catalog.exceptions.FunctionNotExistException;
import org.apache.flink.table.catalog.exceptions.PartitionAlreadyExistsException;
import org.apache.flink.table.catalog.exceptions.PartitionNotExistException;
import org.apache.flink.table.catalog.exceptions.PartitionSpecInvalidException;
import org.apache.flink.table.catalog.exceptions.TableAlreadyExistException;
import org.apache.flink.table.catalog.exceptions.TableNotExistException;
import org.apache.flink.table.catalog.exceptions.TableNotPartitionedException;
import org.apache.flink.table.catalog.exceptions.TablePartitionedException;
import org.apache.flink.table.catalog.stats.CatalogColumnStatistics;
import org.apache.flink.table.catalog.stats.CatalogTableStatistics;
import org.apache.flink.table.expressions.Expression;
import org.apache.flink.table.factories.CatalogFactory;
import org.apache.flink.table.factories.FactoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class KafkaCatalog extends AbstractCatalog {

    private static Logger LOG = LoggerFactory.getLogger(KafkaCatalog.class);

    private static Map<String, Set<String>> userPermittedTables = Map.of("erin", Set.of("KafkaTable", "ErinTable"), "eric", Set.of("EricTable"), "samwise", Set.of("KafkaTable", "SamwiseTable", "KafkaTable2"));
    private final String user;

    private static Map<String, CatalogTable> tables = Map.of(
            "KafkaTable",CatalogTable.of(Schema.newBuilder().column("name", DataTypes.STRING()).column("id", DataTypes.INT()).build(), null, List.of(), Map.of(
                    "properties.bootstrap.servers", "kafka:9092",
                    "connector", "kafka",
                    "format", "csv",
                    "topic", "mytopic",
                    "scan.startup.mode", "earliest-offset",
                    "properties.group.id", "testGroup"
            )),
            "ErinTable", CatalogTable.of(Schema.newBuilder().column("name", DataTypes.STRING()).column("id", DataTypes.INT()).build(), null, List.of(), Map.of(
                    "properties.bootstrap.servers", "kafka:9092",
                    "connector", "kafka",
                    "format", "csv",
                    "topic", "erintopic",
                    "scan.startup.mode", "earliest-offset",
                    "properties.group.id", "testGroup"
            )),
            "EricTable", CatalogTable.of(Schema.newBuilder().column("name", DataTypes.STRING()).column("id", DataTypes.INT()).build(), null, List.of(), Map.of(
                    "properties.bootstrap.servers", "kafka:9092",
                    "connector", "kafka",
                    "format", "csv",
                    "topic", "erictopic",
                    "scan.startup.mode", "earliest-offset",
                    "properties.group.id", "testGroup"
            )),
            "SamwiseTable", CatalogTable.of(Schema.newBuilder().column("name", DataTypes.STRING()).column("id", DataTypes.INT()).build(), null, List.of(), Map.of(
                    "properties.bootstrap.servers", "kafka:9092",
                    "connector", "kafka",
                    "format", "csv",
                    "topic", "samwisetopic",
                    "scan.startup.mode", "earliest-offset",
                    "properties.group.id", "testGroup"
            ))
    );

    private Catalog delegate;

    public KafkaCatalog(CatalogFactory.Context context) {
        super(context.getName(), "default");
        delegate = FactoryUtil.discoverFactory(context.getClassLoader(), CatalogFactory.class, "generic_in_memory")
                // cannot pass through the context as it contains configuration specific to KafkaCatalog, the in memory catalog rejects it.
                .createCatalog(new FactoryUtil.DefaultCatalogContext(context.getName(), Map.of(), context.getConfiguration(), context.getClassLoader()));
        user = Objects.requireNonNull(context.getOptions().get(KafkaCatalogFactory.CATALOG_USER.key()));
    }

    @Override
    public void open() throws CatalogException {
        LOG.info("open catalog");
        delegate.open();
    }

    @Override
    public void close() throws CatalogException {
        LOG.info("close catalog");
        delegate.close();
    }

    @Override
    public List<String> listDatabases() throws CatalogException {
        LOG.info("list databases");
        return delegate.listDatabases();
    }

    @Override
    public CatalogDatabase getDatabase(String name) throws DatabaseNotExistException, CatalogException {
        LOG.info("get database {}", name);
        return delegate.getDatabase(name);
    }

    @Override
    public boolean databaseExists(String name) throws CatalogException {
        LOG.info("database exists {}", name);
        return delegate.databaseExists(name);
    }

    @Override
    public void createDatabase(String name, CatalogDatabase catalogDatabase, boolean ignoreIfExists) throws DatabaseAlreadyExistException, CatalogException {
        LOG.info("create database {}", name);
        delegate.createDatabase(name, catalogDatabase, ignoreIfExists);
    }

    @Override
    public void dropDatabase(String name, boolean ignoreIfNotExists, boolean cascade) throws DatabaseNotExistException, DatabaseNotEmptyException, CatalogException {
        LOG.info("drop database {}", name);
        delegate.dropDatabase(name, ignoreIfNotExists, cascade);
    }

    @Override
    public void alterDatabase(String name, CatalogDatabase catalogDatabase, boolean ignoreIfNotExists) throws DatabaseNotExistException, CatalogException {
        LOG.info("alter database {}", name);
        delegate.alterDatabase(name, catalogDatabase, ignoreIfNotExists);
    }

    @Override
    public List<String> listTables(String databaseName) throws DatabaseNotExistException, CatalogException {
        LOG.info("list tables {}", databaseName);
        List<String> allTables = delegate.listTables(databaseName);
        Stream<String> stream = Stream.concat(allTables.stream(), tables.keySet().stream());
        return stream.filter(table -> userPermittedTables.get(user).contains(table)).toList();
    }

    @Override
    public List<String> listViews(String databaseName) throws DatabaseNotExistException, CatalogException {
        LOG.info("list views {}", databaseName);
        return delegate.listViews(databaseName);
    }

    @Override
    public CatalogBaseTable getTable(ObjectPath objectPath) throws TableNotExistException, CatalogException {
        if(!userPermittedTables.get(user).contains(objectPath.getObjectName())) {
            throw new CatalogException("user " + user + " isn't permitted to get table metadata for " + objectPath);
        }
        LOG.info("get table {}", objectPath);
        if (objectPath.getDatabaseName().equals("default") && tables.containsKey(objectPath.getObjectName())) {
            LOG.info("returning the olde custom table!");
            return tables.get(objectPath.getObjectName());
        }
        CatalogBaseTable table = delegate.getTable(objectPath);
        if (table.getOptions().containsKey("properties.bootstrap.cluster")) {
            String cluster = table.getOptions().get("properties.bootstrap.cluster");
            String bootstrap;
            if(cluster.equals("my-kafka")) {
                bootstrap = "kafka:9092";
            } else {
                throw new RuntimeException("unknown kafka cluster: " + cluster);
            }
            if(table instanceof CatalogTable t) {
                HashMap<String, String> options = new HashMap<>(t.getOptions());
                options.put("properties.bootstrap.servers", bootstrap);
                return CatalogTable.of(t.getUnresolvedSchema(), t.getComment(), t.getPartitionKeys(), options);
            } else {
                throw new RuntimeException("table isn't a CatalogTable");
            }
        }
        LOG.info("get table result: {}, {}", objectPath, table);
        return table;
    }

    @Override
    public boolean tableExists(ObjectPath objectPath) throws CatalogException {
        if(!userPermittedTables.get(user).contains(objectPath.getObjectName())) {
            throw new CatalogException("user " + user + " isn't permitted to check existence of table metadata for " + objectPath);
        }
        if (objectPath.getDatabaseName().equals("default") && objectPath.getObjectName().equals("KafkaTable")) {
            return true;
        }
        LOG.info("table exists: {}", objectPath);
        return delegate.tableExists(objectPath);
    }

    @Override
    public void dropTable(ObjectPath objectPath, boolean ignoreIfNotExists) throws TableNotExistException, CatalogException {
        if(!userPermittedTables.get(user).contains(objectPath.getObjectName())) {
            throw new CatalogException("user " + user + " isn't permitted to drop table metadata for " + objectPath);
        }
        LOG.info("drop table: {}, {}", objectPath, ignoreIfNotExists);
        delegate.dropTable(objectPath, ignoreIfNotExists);
    }

    @Override
    public void renameTable(ObjectPath objectPath, String s, boolean ignoreIfNotExists) throws TableNotExistException, TableAlreadyExistException, CatalogException {
        if(!userPermittedTables.get(user).contains(objectPath.getObjectName())) {
            throw new CatalogException("user " + user + " isn't permitted to rename table metadata for " + objectPath);
        }
        LOG.info("rename table: {}, {}, {}", objectPath, s, ignoreIfNotExists);
        delegate.renameTable(objectPath, s, ignoreIfNotExists);
    }

    @Override
    public void createTable(ObjectPath objectPath, CatalogBaseTable catalogBaseTable, boolean ignoreIfExists) throws TableAlreadyExistException, DatabaseNotExistException, CatalogException {
        if(!userPermittedTables.get(user).contains(objectPath.getObjectName())) {
            throw new CatalogException("user " + user + " isn't permitted to create table metadata for " + objectPath);
        }
        LOG.info("create table: {}, {}, {}", objectPath, catalogBaseTable, ignoreIfExists);
        delegate.createTable(objectPath, catalogBaseTable, ignoreIfExists);
    }

    @Override
    public void alterTable(ObjectPath objectPath, CatalogBaseTable catalogBaseTable, boolean ignoreIfNotExists) throws TableNotExistException, CatalogException {
        if(!userPermittedTables.get(user).contains(objectPath.getObjectName())) {
            throw new CatalogException("user " + user + " isn't permitted to alter table metadata for " + objectPath);
        }
        LOG.info("alter table: {}, {}, {}", objectPath, catalogBaseTable, ignoreIfNotExists);
        delegate.alterTable(objectPath, catalogBaseTable, ignoreIfNotExists);
    }

    @Override
    public List<CatalogPartitionSpec> listPartitions(ObjectPath objectPath) throws TableNotExistException, TableNotPartitionedException, CatalogException {
        LOG.info("list partitions: {}", objectPath);
        return delegate.listPartitions(objectPath);
    }

    @Override
    public List<CatalogPartitionSpec> listPartitions(ObjectPath objectPath, CatalogPartitionSpec catalogPartitionSpec) throws TableNotExistException, TableNotPartitionedException, PartitionSpecInvalidException, CatalogException {
        LOG.info("list partitions: {}, {}", objectPath, catalogPartitionSpec);
        return delegate.listPartitions(objectPath, catalogPartitionSpec);
    }

    @Override
    public List<CatalogPartitionSpec> listPartitionsByFilter(ObjectPath objectPath, List<Expression> filters) throws TableNotExistException, TableNotPartitionedException, CatalogException {
        LOG.info("list partitions by filter: {}, {}", objectPath, filters);
        return delegate.listPartitionsByFilter(objectPath, filters);
    }

    @Override
    public CatalogPartition getPartition(ObjectPath objectPath, CatalogPartitionSpec catalogPartitionSpec) throws PartitionNotExistException, CatalogException {
        LOG.info("get partition: {}, {}", objectPath, catalogPartitionSpec);
        return delegate.getPartition(objectPath, catalogPartitionSpec);
    }

    @Override
    public boolean partitionExists(ObjectPath objectPath, CatalogPartitionSpec catalogPartitionSpec) throws CatalogException {
        LOG.info("partition exists: {}, {}", objectPath, catalogPartitionSpec);
        return delegate.partitionExists(objectPath, catalogPartitionSpec);
    }

    @Override
    public void createPartition(ObjectPath objectPath, CatalogPartitionSpec catalogPartitionSpec, CatalogPartition catalogPartition, boolean ignoreIfExists) throws TableNotExistException, TableNotPartitionedException, PartitionSpecInvalidException, PartitionAlreadyExistsException, CatalogException {
        LOG.info("create partition: {}, {}", objectPath, catalogPartitionSpec);
        delegate.createPartition(objectPath, catalogPartitionSpec, catalogPartition, ignoreIfExists);
    }

    @Override
    public void dropPartition(ObjectPath objectPath, CatalogPartitionSpec catalogPartitionSpec, boolean b) throws PartitionNotExistException, CatalogException {
        LOG.info("drop partition: {}, {}", objectPath, catalogPartitionSpec);
        delegate.dropPartition(objectPath, catalogPartitionSpec, b);
    }

    @Override
    public void alterPartition(ObjectPath objectPath, CatalogPartitionSpec catalogPartitionSpec, CatalogPartition catalogPartition, boolean ignoreIfNotExists) throws PartitionNotExistException, CatalogException {
        LOG.info("alter partition: {}, {}", objectPath, catalogPartitionSpec);
        delegate.alterPartition(objectPath, catalogPartitionSpec, catalogPartition, ignoreIfNotExists);
    }

    @Override
    public List<String> listFunctions(String s) throws DatabaseNotExistException, CatalogException {
        LOG.info("list functions: {}", s);
        return delegate.listFunctions(s);
    }

    @Override
    public CatalogFunction getFunction(ObjectPath objectPath) throws FunctionNotExistException, CatalogException {
        LOG.info("get function: {}", objectPath);
        return delegate.getFunction(objectPath);
    }

    @Override
    public boolean functionExists(ObjectPath objectPath) throws CatalogException {
        LOG.info("function exists: {}", objectPath);
        return delegate.functionExists(objectPath);
    }

    @Override
    public void createFunction(ObjectPath objectPath, CatalogFunction catalogFunction, boolean ignoreIfExists) throws FunctionAlreadyExistException, DatabaseNotExistException, CatalogException {
        LOG.info("create function: {}", objectPath);
        delegate.createFunction(objectPath, catalogFunction, ignoreIfExists);
    }

    @Override
    public void alterFunction(ObjectPath objectPath, CatalogFunction catalogFunction, boolean ignoreIfNotExists) throws FunctionNotExistException, CatalogException {
        LOG.info("alter function: {}", objectPath);
        delegate.alterFunction(objectPath, catalogFunction, ignoreIfNotExists);
    }

    @Override
    public void dropFunction(ObjectPath objectPath, boolean ignoreIfNotExists) throws FunctionNotExistException, CatalogException {
        LOG.info("drop function: {}", objectPath);
        delegate.dropFunction(objectPath, ignoreIfNotExists);
    }

    @Override
    public CatalogTableStatistics getTableStatistics(ObjectPath objectPath) throws TableNotExistException, CatalogException {
        LOG.info("get table statistics: {}", objectPath);
        return delegate.getTableStatistics(objectPath);
    }

    @Override
    public CatalogColumnStatistics getTableColumnStatistics(ObjectPath objectPath) throws TableNotExistException, CatalogException {
        LOG.info("get table column statistics: {}", objectPath);
        return delegate.getTableColumnStatistics(objectPath);
    }

    @Override
    public CatalogTableStatistics getPartitionStatistics(ObjectPath objectPath, CatalogPartitionSpec catalogPartitionSpec) throws PartitionNotExistException, CatalogException {
        LOG.info("get partition statistics: {}", objectPath);
        return delegate.getPartitionStatistics(objectPath, catalogPartitionSpec);
    }

    @Override
    public CatalogColumnStatistics getPartitionColumnStatistics(ObjectPath objectPath, CatalogPartitionSpec catalogPartitionSpec) throws PartitionNotExistException, CatalogException {
        LOG.info("get partition column statistics: {}", objectPath);
        return delegate.getPartitionColumnStatistics(objectPath, catalogPartitionSpec);
    }

    @Override
    public void alterTableStatistics(ObjectPath objectPath, CatalogTableStatistics catalogTableStatistics, boolean ignoreIfNotExists) throws TableNotExistException, CatalogException {
        LOG.info("alter table statistics: {}", objectPath);
        delegate.alterTableStatistics(objectPath, catalogTableStatistics, ignoreIfNotExists);
    }

    @Override
    public void alterTableColumnStatistics(ObjectPath objectPath, CatalogColumnStatistics catalogColumnStatistics, boolean ignoreIfNotExists) throws TableNotExistException, CatalogException, TablePartitionedException {
        LOG.info("alter table column statistics: {}", objectPath);
        delegate.alterTableColumnStatistics(objectPath, catalogColumnStatistics, ignoreIfNotExists);
    }

    @Override
    public void alterPartitionStatistics(ObjectPath objectPath, CatalogPartitionSpec catalogPartitionSpec, CatalogTableStatistics catalogTableStatistics, boolean ignoreIfNotExists) throws PartitionNotExistException, CatalogException {
        LOG.info("alter partition statistics: {}", objectPath);
        delegate.alterPartitionStatistics(objectPath, catalogPartitionSpec, catalogTableStatistics, ignoreIfNotExists);
    }

    @Override
    public void alterPartitionColumnStatistics(ObjectPath objectPath, CatalogPartitionSpec catalogPartitionSpec, CatalogColumnStatistics catalogColumnStatistics, boolean ignoreIfNotExists) throws PartitionNotExistException, CatalogException {
        LOG.info("alter partition column statistics: {}", objectPath);
        delegate.alterPartitionColumnStatistics(objectPath, catalogPartitionSpec, catalogColumnStatistics, ignoreIfNotExists);
    }
}
