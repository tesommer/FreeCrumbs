package freecrumbs.finf;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import freecrumbs.finf.field.MockField;

public class MockInfo extends Info {
    
    public static final String PATH_FIELD_NAME = "path";
    public static final String FILENAME_FIELD_NAME = "filename";
    public static final String SIZE_FIELD_NAME = "size";
    public static final String MODIFIED_FIELD_NAME = "modified";
    public static final String HASH_FIELD_NAME = "hash";
    
    private final MockField[] fields;

    public MockInfo(final MockField... fields) {
        super(new File(""), fields);
        this.fields = fields.clone();
    }
    
    public static MockInfo getInstance(
            final String path,
            final String filename,
            final String size,
            final String modified,
            final String hash) {
        
        return new MockInfo(
                getFields(path, filename, size, modified, hash));
    }
    
    private static MockField[] getFields(
            final String path,
            final String filename,
            final String size,
            final String modified,
            final String hash) {
        
        return new MockField[] {
                new MockField(PATH_FIELD_NAME, path),
                new MockField(FILENAME_FIELD_NAME, filename),
                new MockField(SIZE_FIELD_NAME, size),
                new MockField(MODIFIED_FIELD_NAME, modified),
                new MockField(HASH_FIELD_NAME, hash),
        };
    }
    
    public boolean isValueRead(final String fieldName) {
        return Stream.of(fields)
                .filter(field -> field.getName().equals(fieldName))
                .map(MockField::isValueRead)
                .findAny()
                .orElse(false);
    }

    @Override
    protected String getValue(final InfoField field, final File file)
            throws IOException {
        
        return field.getValue(file);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        try {
            for (final String fieldName : getFieldNames()) {
                builder.append(fieldName);
                builder.append('=');
                builder.append(getValue(fieldName));
                builder.append(' ');
            }
            return builder.toString().trim();
        } catch (final IOException ex) {
            return ex.toString();
        }
    }

}
