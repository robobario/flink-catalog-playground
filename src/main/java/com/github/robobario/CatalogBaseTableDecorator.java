package com.github.robobario;

import org.apache.flink.table.api.Schema;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.catalog.CatalogBaseTable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CatalogBaseTableDecorator implements CatalogBaseTable {

    private final CatalogBaseTable inner;
    private final Map<String, String> options;


    public CatalogBaseTableDecorator(CatalogBaseTable inner, Map<String, String> optionOverrides) {
        this.inner = inner;
        HashMap<String, String> options = new HashMap<>(inner.getOptions());
        options.putAll(optionOverrides);
        this.options = options;
    }

    @Override
    public TableKind getTableKind() {
        return inner.getTableKind();
    }

    @Override
    public Map<String, String> getOptions() {
        return options;
    }

    @Override
    @Deprecated
    public TableSchema getSchema() {
        return inner.getSchema();
    }

    @Override
    public Schema getUnresolvedSchema() {
        return inner.getUnresolvedSchema();
    }

    @Override
    public String getComment() {
        return inner.getComment();
    }

    @Override
    public CatalogBaseTable copy() {
        return inner.copy();
    }

    @Override
    public Optional<String> getDescription() {
        return inner.getDescription();
    }

    @Override
    public Optional<String> getDetailedDescription() {
        return inner.getDetailedDescription();
    }

}
