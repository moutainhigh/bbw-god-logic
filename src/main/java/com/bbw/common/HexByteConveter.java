package com.bbw.common;

/**
 * hex 与 byte[] 互转
 *
 * @author: suhq
 * @date: 2021/12/29 5:17 下午
 */
public class HexByteConveter {

    /**
     * byte[] -> hex
     *
     * @param buff
     * @return
     */
    public static String byte2Hex(byte[] buff) {
        String hs = "";
        for (int i = 0; i < buff.length; i++) {
            String stmp = (Integer.toHexString(buff[i] & 0XFF));
            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }
        }
        return hs;
    }

    /**
     * str -> byte[]
     *
     * @param str
     * @return
     */
    public static byte[] hex2Byte(String str) {
        int len = str.length() / 2;
        byte[] buff = new byte[len];
        int index = 0;
        for (int i = 0; i < str.length(); i += 2) {
            buff[index++] = (byte) Integer.parseInt(str.substring(i, i + 2), 16);
        }
        return buff;
    }
}
