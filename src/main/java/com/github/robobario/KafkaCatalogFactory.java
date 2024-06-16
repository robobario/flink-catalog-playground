package com.github.robobario;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.table.catalog.Catalog;
import org.apache.flink.table.factories.CatalogFactory;
import org.apache.flink.table.factories.FactoryUtil;

import java.util.Map;
import java.util.Set;

public class KafkaCatalogFactory implements CatalogFactory {

    public static final ConfigOption<String> CATALOG_USER = ConfigOptions.key("catalog.user").stringType().noDefaultValue();

    @Override
    public String factoryIdentifier() {
        return "kafka-rob";
    }

    @Override
    public Catalog createCatalog(Context context) {
        return new KafkaCatalog(context);
    }

    @Override
    public Catalog createCatalog(String name, Map<String, String> properties) {
        return createCatalog(new FactoryUtil.DefaultCatalogContext(name, properties, new Configuration(), Thread.currentThread().getContextClassLoader()));
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        return Set.of(CATALOG_USER);
    }
}
