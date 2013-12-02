package org.cratedb.lucene.fields;


import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.MultiTermQueryWrapperFilter;
import org.apache.lucene.search.SortField;
import org.cratedb.DataType;
import org.cratedb.index.ColumnDefinition;
import org.elasticsearch.common.Booleans;

import java.io.IOException;

public class BooleanLuceneField extends LuceneField<Boolean> {

    public BooleanLuceneField(String name) {
        this(name, false);
    }

    public BooleanLuceneField(String name, boolean allowMultipleValues) {
        super(name, allowMultipleValues);
        type = SortField.Type.STRING;
    }

    @Override
    public Object getValue(IndexableField field) {
        if (field == null) {
            return Boolean.FALSE;
        }
        String sValue = field.toString();
        if (sValue.length() == 0) {
            return Boolean.FALSE;
        }
        if (sValue.length() == 1 && sValue.charAt(0) == 'F') {
            return Boolean.FALSE;
        }
        if (Booleans.parseBoolean(sValue, false)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    @Override
    public MultiTermQuery rangeQuery(Object from, Object to, boolean includeLower, boolean includeUpper) {
        throw new UnsupportedOperationException("Range query on boolean type is not possible");
    }

    @Override
    public MultiTermQueryWrapperFilter rangeFilter(Object from, Object to, boolean includeLower, boolean includeUpper) {
        throw new UnsupportedOperationException("Range filter on boolean type is not possible");
    }

    @Override
    public ColumnDefinition getColumnDefinition(String tableName, int ordinalPosition) {
        return new ColumnDefinition(tableName, name, DataType.BOOLEAN, null, ordinalPosition, false, true);
    }

    @Override
    public Object mappedValue(Object value) {
        return (Boolean)value;
    }

    @Override
    public Field field(Boolean value) {
        return new StringField(name, value.toString(), Field.Store.YES);
    }

    public TokenStream tokenStream(Boolean value) throws IOException {
        return field(value).tokenStream(analyzer);
    }

}