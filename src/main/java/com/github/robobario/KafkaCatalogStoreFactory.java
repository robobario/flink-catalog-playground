package com.github.robobario;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.table.catalog.CatalogStore;
import org.apache.flink.table.catalog.exceptions.CatalogException;
import org.apache.flink.table.factories.CatalogStoreFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Set;

public class KafkaCatalogStoreFactory implements CatalogStoreFactory {
    private static Logger LOG = LoggerFactory.getLogger(KafkaCatalogStoreFactory.class);
    private String user;

    @Override
    public CatalogStore createCatalogStore() {
        LOG.info("creating catalog store");
        return new KafkaCatalogStore(user);
    }

    @Override
    public void open(Context context) throws CatalogException {
        LOG.info("open");
        user = Objects.requireNonNull(context.getConfiguration().get(KafkaCatalogFactory.CATALOG_USER));
    }

    @Override
    public void close() throws CatalogException {
        LOG.info("close");
    }

    @Override
    public String factoryIdentifier() {
        return "kafka-store-rob";
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        LOG.info("get required options");
        return Set.of(KafkaCatalogFactory.CATALOG_USER);
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        LOG.info("get optional options");
        return Set.of();
    }
}
