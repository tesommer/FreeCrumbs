package freecrumbs.finf.field;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

import com.calclipse.lib.util.EncodingUtil;

import freecrumbs.finf.Field;
import freecrumbs.finf.FieldComputation;

/**
 * Fields for binary-to-text transformation.
 * These are the fields:
 * <ul>
 * <li>hex: lowercase hexadecimal</li>
 * <li>HEX: uppercase hexadecimal</li>
 * <li>base64: base64 without line separators</li>
 * <li>base64mime: base64 with max 76 chars per line and crlf</li>
 * <li>base64url: URL and filename safe base64</li>
 * </ul>
 * 
 * @author Tone Sommerland
 */
public final class BinaryToText {
    
    private static final String HEX_FIELD_NAME = "hex";
    private static final String HEX_UPPERCASE_FIELD_NAME = "HEX";
    private static final String BASE64_FIELD_NAME = "base64";
    private static final String BASE64_MIME_FIELD_NAME = "base64mime";
    private static final String BASE64_URL_FIELD_NAME = "base64url";

    private BinaryToText() {
    }
    
    /**
     * Returns binary-to-text transformation fields.
     */
    public static Field[] getFields() {
        return new Field[] {
                Field.getInstance(
                        HEX_FIELD_NAME,
                        new HexComputation(false)),
                Field.getInstance(
                        HEX_UPPERCASE_FIELD_NAME,
                        new HexComputation(true)),
                Field.getInstance(
                        BASE64_FIELD_NAME,
                        new Base64Computation(Base64.getEncoder())),
                Field.getInstance(
                        BASE64_MIME_FIELD_NAME,
                        new Base64Computation(Base64.getMimeEncoder())),
                Field.getInstance(
                        BASE64_URL_FIELD_NAME,
                        new Base64Computation(Base64.getUrlEncoder())),
        };
    }
    
    private static final class HexComputation implements FieldComputation {
        private final StringBuilder buffer = new StringBuilder();
        private final boolean uppercase;

        private HexComputation(final boolean uppercase) {
            this.uppercase = uppercase;
        }

        @Override
        public void reset(final File file) throws IOException {
            buffer.setLength(0);
        }

        @Override
        public boolean update(
                final byte[] input,
                final int offset,
                final int length) throws IOException {
            
            for (int i = offset; i < length; i++) {
                buffer.append(EncodingUtil.byteToHex(uppercase, input[i]));
            }
            return true;
        }

        @Override
        public String get() throws IOException {
            return buffer.toString();
        }
        
    }
    
    private static final class Base64Computation implements FieldComputation {
        
        private final ByteArrayOutputStream
        buffer = new ByteArrayOutputStream();
        
        private final Base64.Encoder encoder;

        private Base64Computation(final Base64.Encoder encoder) {
            assert encoder != null;
            this.encoder = encoder;
        }

        @Override
        public void reset(final File file) throws IOException {
            buffer.reset();
        }

        @Override
        public boolean update(
                final byte[] input,
                final int offset,
                final int length) throws IOException {
            
            buffer.write(input, offset, length);
            return true;
        }

        @Override
        public String get() throws IOException {
            return encoder.encodeToString(buffer.toByteArray());
        }
        
    }

}
