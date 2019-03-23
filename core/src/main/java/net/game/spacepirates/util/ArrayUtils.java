package net.game.spacepirates.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ArrayUtils {

    public static byte[] intArrToGLArr(int[] arr) {
        byte[] bytes = new byte[arr.length * Integer.BYTES];

        int ptr = 0;

        for (int i : arr) {
            byte[] subBytes = intToGLArr(i);
            System.arraycopy(subBytes, 0, bytes, ptr, subBytes.length);
            ptr += subBytes.length;
        }

        return bytes;
    }

    public static int glArrToInt(byte[] arr) {
        if(arr.length != Integer.BYTES) {
            throw new IllegalArgumentException("GL integer array must consist of " + Integer.BYTES + " bytes");
        }


        ByteBuffer b = ByteBuffer.wrap(arr);
        b.order(ByteOrder.LITTLE_ENDIAN);
        return b.getInt();
    }

    public static byte[] intToGLArr(int value) {
        return new byte[]{
                (byte)  value,
                        (byte) (value >>> 8),
                (byte) (value >>> 16),
                (byte) (value >>> 24)
        };
    }


}
