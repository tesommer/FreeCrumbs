package test.freecrumbs.finf;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Stream;

import freecrumbs.finf.Field;
import freecrumbs.finf.FieldReader;
import freecrumbs.finf.Info;

public final class MockInfo extends Info {
    
    public static final String PATH_FIELD_NAME = "path";
    public static final String FILENAME_FIELD_NAME = "filename";
    public static final String SIZE_FIELD_NAME = "size";
    public static final String MODIFIED_FIELD_NAME = "modified";
    public static final String MD5_FIELD_NAME = "md5";
    
    private final Field[] fields;

    private MockInfo(final Field[] fields) {
        super(FieldReader.getInstance(1024, fields), new File(""));
        this.fields = fields.clone();
    }
    
    public static MockInfo getInstance(
            final String path,
            final String filename,
            final String size,
            final String modified,
            final String md5) {
        
        return new MockInfo(
                getFields(path, filename, size, modified, md5));
    }
    
    private static Field[] getFields(
            final String path,
            final String filename,
            final String size,
            final String modified,
            final String md5) {
        
        return new Field[] {
                Field.getInstance(
                        PATH_FIELD_NAME, new MockFieldValue(path)),
                Field.getInstance(
                        FILENAME_FIELD_NAME, new MockFieldValue(filename)),
                Field.getInstance(
                        SIZE_FIELD_NAME, new MockFieldValue(size)),
                Field.getInstance(
                        MODIFIED_FIELD_NAME, new MockFieldValue(modified)),
                Field.getInstance(
                        MD5_FIELD_NAME, new MockFieldValue(md5)),
        };
    }
    
    public boolean isValueRead(final String fieldName) {
        return Stream.of(fields)
                .filter(field -> field.name().equals(fieldName))
                .map(MockFieldValue::isValueRead)
                .findAny()
                .orElse(false);
    }

    @Override
    protected Map<String, String> getValues(
            final FieldReader reader, final File file) throws IOException {
        
        return reader.readFieldValues(file);
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
