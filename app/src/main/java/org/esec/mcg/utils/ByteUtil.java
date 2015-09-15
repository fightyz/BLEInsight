package org.esec.mcg.utils;

public final class ByteUtil {
    /**
     * Convert a byte array to hex string
     * {0x00, 0x01, (byte)0xff}	-> "00 01 ff"
     */
    public static String ByteArrayToHexString(byte[] data) {
        final int len = 3*data.length;
        final StringBuilder stringBuilder = new StringBuilder(len);
        for (byte b : data) {
            stringBuilder.append(Integer.toString((b & 0x000000ff) + 0x100, 16).substring(1));
            stringBuilder.append(" ");
        }
        stringBuilder.delete(len-1, len);
        return stringBuilder.toString();
    }
}