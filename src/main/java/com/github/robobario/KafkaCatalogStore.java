package com.github.robobario;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.table.catalog.AbstractCatalogStore;
import org.apache.flink.table.catalog.CatalogDescriptor;
import org.apache.flink.table.catalog.CatalogStore;
import org.apache.flink.table.catalog.CommonCatalogOptions;
import org.apache.flink.table.catalog.exceptions.CatalogException;
import org.apache.flink.table.factories.CatalogStoreFactory;
import org.apache.flink.table.factories.FactoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;

public class KafkaCatalogStore extends AbstractCatalogStore {
    private static Logger LOG = LoggerFactory.getLogger(KafkaCatalogStore.class);

    CatalogStore delegate;

    public KafkaCatalogStore() {
        delegate = FactoryUtil.discoverFactory(Thread.currentThread().getContextClassLoader(), CatalogStoreFactory.class, "generic_in_memory").createCatalogStore();
    }

    @Override
    public void storeCatalog(String catalogName, CatalogDescriptor catalog) throws CatalogException {
        delegate.storeCatalog(catalogName, catalog);
    }

    @Override
    public void removeCatalog(String catalogName, boolean ignoreIfNotExists) throws CatalogException {
        delegate.removeCatalog(catalogName, ignoreIfNotExists);
    }

    @Override
    public Optional<CatalogDescriptor> getCatalog(String catalogName) throws CatalogException {
        LOG.info("get catalog: {}", catalogName);
        Optional<CatalogDescriptor> catalog = delegate.getCatalog(catalogName);
        LOG.info("get catalog result: {}, {}", catalogName, catalog);
        return catalog;
    }

    @Override
    public Set<String> listCatalogs() throws CatalogException {
        return delegate.listCatalogs();
    }

    @Override
    public boolean contains(String catalogName) throws CatalogException {
        return delegate.contains(catalogName);
    }

    @Override
    public void open() throws CatalogException {
        delegate.open();
        Configuration configuration = new Configuration();
        configuration.set(CommonCatalogOptions.CATALOG_TYPE, "kafka-rob");
        delegate.storeCatalog("default_catalog", CatalogDescriptor.of("default_catalog", configuration));
    }

    @Override
    public void close() throws CatalogException {
        delegate.close();
    }
}
