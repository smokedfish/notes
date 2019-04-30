package org.rob.notes.responsebuilders.puml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.zip.DeflaterOutputStream;

public class PlantUmlEncoder {
    private static final char[] BASE64 = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
            'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
            'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
            '-', '_'
    };

    public static String encode(byte[] doc) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (DeflaterOutputStream dos = new DeflaterOutputStream(baos)) {
            dos.write(doc);
        }
        return encodeBytes(baos.toByteArray());
    }

    private static String encodeBytes(byte[] dataWithHeaders) {
        byte[] data = new byte[dataWithHeaders.length - 6];
        System.arraycopy(dataWithHeaders, 2, data, 0, dataWithHeaders.length - 6);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i += 3) {
            if (i + 2 == data.length) {
                encode3bytes(sb, data[i], data[i+1], (byte)0);
            } else if (i + 1 == data.length) {
                encode3bytes(sb, data[i], (byte)0, (byte)0);
            } else {
                encode3bytes(sb, data[i], data[i+1], data[i+2]);
            }
        }
        return sb.toString();
    }

    private static void encode3bytes(StringBuilder sb, byte b1, byte b2, byte b3) {
        int c1 = (b1 >> 2) & 0x3F;
        int c2 = (b1 & 0x3) << 4 | ((b2 >> 4) & 0xF);
        int c3 = (b2 & 0xF) << 2 | ((b3 >> 6) & 0x3);
        int c4 = (b3 & 0x3F);
        sb.append(BASE64[c1]).append(BASE64[c2]).append(BASE64[c3]).append(BASE64[c4]);
    }

}
