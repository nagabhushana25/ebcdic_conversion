import java.nio.charset.Charset;
import java.util.Arrays;

public class CobolBinaryDecoder {

    private static String decodeEBCDIC(byte[] ebcdicBytes) {
        return new String(ebcdicBytes, Charset.forName("Cp037")).trim();
    }

    private static String decodeComp3(byte[] comp3Bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < comp3Bytes.length; i++) {
            int b = comp3Bytes[i] & 0xFF;
            if (i < comp3Bytes.length - 1) {
                sb.append(b >> 4);
                sb.append(b & 0x0F);
            } else {
                sb.append(b >> 4); // digit
                int sign = b & 0x0F;
                if (sign == 0x0D) sb.insert(0, '-');
                // 0x0C or 0x0F = positive
            }
        }
        return sb.toString();
    }

    private static int decodeComp(byte[] compBytes) {
        int value = ((compBytes[0] & 0xFF) << 8) | (compBytes[1] & 0xFF);
        // Check for signed
        if ((value & 0x8000) != 0) {
            value = value - 0x10000;
        }
        return value;
    }

    public static void decode(byte[] record) {
        String cusip = decodeEBCDIC(Arrays.copyOfRange(record, 0, 9));
        String posId = decodeComp3(Arrays.copyOfRange(record, 9, 17));
        int partId = decodeComp(Arrays.copyOfRange(record, 17, 19));
        String brokerageAcct = decodeEBCDIC(Arrays.copyOfRange(record, 19, 27));
        // FILLER ignored

        System.out.println("CUSIP:           " + cusip);
        System.out.println("POSITION ID:     " + posId);
        System.out.println("PARTITION ID:    " + partId);
        System.out.println("BROKERAGE ACCT:  " + brokerageAcct);
    }

    public static void main(String[] args) {
        // This should be filled with your binary record from the database (bytea)
        byte[] record = new byte[80]; // Replace with actual data
        decode(record);
    }
}
