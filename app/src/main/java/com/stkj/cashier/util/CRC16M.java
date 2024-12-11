package com.stkj.cashier.util;

import androidx.core.internal.view.SupportMenu;
import androidx.core.view.MotionEventCompat;

import com.stkj.cashier.util.util.LogUtils;

import java.util.Objects;

/**
 * @description $
 * @author: Administrator
 * @date: 2024/4/22
 */
public class CRC16M {
    static final String HEXES = "0123456789ABCDEF";
    private static byte[] auchCRCHi = {0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64, 1, -64, Byte.MIN_VALUE, 65, 1, -64, Byte.MIN_VALUE, 65, 0, -63, -127, 64};
    private static byte[] auchCRCLo = {0, -64, -63, 1, -61, 3, 2, -62, -58, 6, 7, -57, 5, -59, -60, 4, -52, 12, 13, -51, 15, -49, -50, 14, 10, -54, -53, 11, -55, 9, 8, -56, -40, 24, 25, -39, 27, -37, -38, 26, 30, -34, -33, 31, -35, 29, 28, -36, 20, -44, -43, 21, -41, 23, 22, -42, -46, 18, 19, -45, 17, -47, -48, 16, -16, 48, 49, -15, 51, -13, -14, 50, 54, -10, -9, 55, -11, 53, 52, -12, 60, -4, -3, 61, -1, 63, 62, -2, -6, 58, 59, -5, 57, -7, -8, 56, 40, -24, -23, 41, -21, 43, 42, -22, -18, 46, 47, -17, 45, -19, -20, 44, -28, 36, 37, -27, 39, -25, -26, 38, 34, -30, -29, 35, -31, 33, 32, -32, -96, 96, 97, -95, 99, -93, -94, 98, 102, -90, -89, 103, -91, 101, 100, -92, 108, -84, -83, 109, -81, 111, 110, -82, -86, 106, 107, -85, 105, -87, -88, 104, 120, -72, -71, 121, -69, 123, 122, -70, -66, 126, Byte.MAX_VALUE, -65, 125, -67, -68, 124, -76, 116, 117, -75, 119, -73, -74, 118, 114, -78, -77, 115, -79, 113, 112, -80, 80, -112, -111, 81, -109, 83, 82, -110, -106, 86, 87, -105, 85, -107, -108, 84, -100, 92, 93, -99, 95, -97, -98, 94, 90, -102, -101, 91, -103, 89, 88, -104, -120, 72, 73, -119, 75, -117, -118, 74, 78, -114, -113, 79, -115, 77, 76, -116, 68, -124, -123, 69, -121, 71, 70, -122, -126, 66, 67, -125, 65, -127, Byte.MIN_VALUE, 64};
    byte uchCRCHi = -1;
    byte uchCRCLo = -1;
    public int value = 0;

    public void update(byte[] bArr, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            int i3 = (this.uchCRCHi ^ bArr[i2]) & 255;
            this.uchCRCHi = (byte) (this.uchCRCLo ^ auchCRCHi[i3]);
            this.uchCRCLo = auchCRCLo[i3];
        }
        this.value = ((this.uchCRCHi << 8) | (this.uchCRCLo & 255)) & SupportMenu.USER_MASK;
    }

    public void reset() {
        this.value = 0;
        this.uchCRCHi = (byte) -1;
        this.uchCRCLo = (byte) -1;
    }

    public int getValue() {
        return this.value;
    }

    private static byte uniteBytes(byte b, byte b2) {
        return (byte) (((byte) (Byte.decode("0x" + new String(new byte[]{b})).byteValue() << 4)) ^ Byte.decode("0x" + new String(new byte[]{b2})).byteValue());
    }

    private static byte[] HexString2Buf(String str) {
        int length = str.length();
        byte[] bArr = new byte[(length / 2) + 2];
        byte[] bytes = str.getBytes();
        for (int i = 0; i < length; i += 2) {
            bArr[i / 2] = uniteBytes(bytes[i], bytes[i + 1]);
        }
        return bArr;
    }

    public static byte[] getSendBuf(String str) {
        byte[] HexString2Buf = HexString2Buf(str);
        CRC16M crc16m = new CRC16M();
        crc16m.update(HexString2Buf, HexString2Buf.length - 2);
        int value = crc16m.getValue();
        HexString2Buf[HexString2Buf.length - 1] = (byte) (value & 255);
        HexString2Buf[HexString2Buf.length - 2] = (byte) ((value & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8);
        return HexString2Buf;
    }

    public static boolean checkBuf(byte[] bArr) {
        CRC16M crc16m = new CRC16M();
        crc16m.update(bArr, bArr.length - 2);
        int value = crc16m.getValue();
        return bArr[bArr.length - 1] == ((byte) (value & 255)) && bArr[bArr.length + (-2)] == ((byte) ((value & MotionEventCompat.ACTION_POINTER_INDEX_MASK) >> 8));
    }

    public static String getBufHexStr(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(bArr.length * 2);
        for (byte b : bArr) {
            sb.append(HEXES.charAt((b & 240) >> 4));
            sb.append(HEXES.charAt(b & 15));
        }
        return sb.toString();
    }

    public static boolean getCRCMCheckResult(String str) {
        String bufHexStr = getBufHexStr(getSendBuf(str.substring(0, str.length() - 4)));
        LogUtils.e(bufHexStr + "=========returnData2");
        String substring = bufHexStr.substring(bufHexStr.length() + (-4), bufHexStr.length());
        String HighLowHex = ParaseData.HighLowHex(ParaseData.spaceHex(substring));
        LogUtils.e(HighLowHex + "=========str2");
        LogUtils.e(substring + "=========returnData3");
        return Objects.equals(bufHexStr, str.toUpperCase());
    }

    public static void main(String[] strArr) {
        System.out.println(getBufHexStr(getSendBuf("010415000004")));
    }

}
