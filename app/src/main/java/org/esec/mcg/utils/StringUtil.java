package org.esec.mcg.utils;

import java.util.Map;

/**
 * Created by yz on 2015/10/8.
 */
public class StringUtil {

    /**
     * 将一个16进制字符串解析成一个byte数组
     * "FF001020FH" -> {(byte)0xff, 0x00, 0x10, 0x02, (byte)0xfh}
     * @param hex
     * @return
     */
    public static byte[] HexStringToByteArray(String hex) {
        String tmp = hex.replaceAll("[^[0-9][a-f]]", ""); // 不在括号中的任意字符
        byte[] bytes = new byte[tmp.length() / 2]; // 每两个字符为一个byte

        String part = "";

        for(int i = 0; i <bytes.length; i++) {
            part = "0x" + tmp.substring(i * 2, i * 2 + 2);
            bytes[i] = Integer.decode(part).byteValue();
        }

        return bytes;
    }
}
