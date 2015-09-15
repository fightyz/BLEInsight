package org.esec.mcg.utils;

import android.util.Pair;

import java.util.Objects;

/**
 * Created by yz on 2015/9/11.
 */
public final class ArrayUtil {

    public static int getArrayDimension(Object objects) {
        int dim = 0;
        for (int i = 0; i < objects.toString().length();++i) {
            if (objects.toString().charAt(i) == '[') {
                ++dim;
            } else {
                break;
            }
        }
        return dim;
    }

    public static Pair arrayToString(Object object) {
        StringBuilder builder = new StringBuilder("[");
        int length = 0;
        if (object instanceof int[]) {
            int[] ints = (int[]) object;
            length = ints.length;
            for (int i : ints) {
                builder.append(i + ",\t");
            }
        } else if (object instanceof byte[]) {
            byte[] bytes = (byte[]) object;
            length = bytes.length;
            for (byte item : bytes) {
                builder.append(item + ",\t");
            }
        } else if (object instanceof short[]) {
            short[] shorts = (short[]) object;
            length = shorts.length;
            for (short item : shorts) {
                builder.append(item + ",\t");
            }
        } else if (object instanceof long[]) {
            long[] longs = (long[]) object;
            length = longs.length;
            for (long item : longs) {
                builder.append(item + ",\t");
            }
        } else if (object instanceof float[]) {
            float[] floats = (float[]) object;
            length = floats.length;
            for (float item : floats) {
                builder.append(item + ",\t");
            }
        } else if (object instanceof double[]) {
            double[] doubles = (double[]) object;
            length = doubles.length;
            for (double item : doubles) {
                builder.append(item + ",\t");
            }
        } else if (object instanceof boolean[]) {
            boolean[] booleans = (boolean[]) object;
            length = booleans.length;
            for (boolean item : booleans) {
                builder.append(item + ",\t");
            }
        } else if (object instanceof char[]) {
            char[] chars = (char[]) object;
            length = chars.length;
            for (char item : chars) {
                builder.append(item + ",\t");
            }
        } else {
            Object[] objects = (Object[]) object;
            length = objects.length;
            for (Object item : objects) {
                builder.append(SystemUtil.objectToString(item) + ",\t");
            }
        }
        return Pair.create(length, builder.replace(builder.length() - 2, builder.length(), "]").toString());
    }

    public static Pair<Pair<Integer, Integer>, String> arrayToObject(Object object) {
        StringBuilder builder = new StringBuilder();
        int cross = 0, vertical = 0;
        if (object instanceof int[][]) {
            int[][] ints = (int[][]) object;
            cross = ints.length;
            vertical = cross == 0 ? 0 : ints[0].length;
            for (int[] ints1 : ints) {
                builder.append(arrayToString(ints1).second + "\n");
            }
        } else if (object instanceof byte[][]) {
            byte[][] bytes = (byte[][]) object;
            cross = bytes.length;
            vertical = cross == 0 ? 0 : bytes[0].length;
            for (byte[] bytes1 : bytes) {
                builder.append(arrayToString(bytes1).second + "\n");
            }
        } else if (object instanceof short[][]) {
            short[][] shorts = (short[][]) object;
            cross = shorts.length;
            vertical = cross == 0 ? 0 : shorts[0].length;
            for (short[] shorts1 : shorts) {
                builder.append(arrayToString(shorts1).second + "\n");
            }
        } else if (object instanceof long[][]) {
            long[][] longs = (long[][]) object;
            cross =longs.length;
            vertical = cross == 0 ? 0 : longs[0].length;
            for (long[] longs1 : longs) {
                builder.append(arrayToString(longs1).second + "\n");
            }
        } else if (object instanceof float[][]) {
            float[][] floats = (float[][]) object;
            cross = floats.length;
            vertical = cross == 0 ? 0 : floats[0].length;
            for (float[] floats1 : floats) {
                builder.append(arrayToString(floats1).second + "\n");
            }
        } else if (object instanceof double[][]) {
            double[][] doubles = (double[][]) object;
            cross = doubles.length;
            vertical = cross == 0 ? 0 : doubles[0].length;
            for (double[] doubles1 : doubles) {
                builder.append(arrayToString(doubles1).second + "\n");
            }
        } else if (object instanceof boolean[][]) {
            boolean[][] booleans = (boolean[][]) object;
            cross = booleans.length;
            vertical = cross == 0 ? 0 : booleans[0].length;
            for (boolean[] booleans1 : booleans) {
                builder.append(arrayToString(booleans1).second + "\n");
            }
        } else if (object instanceof char[][]) {
            char[][] chars = (char[][]) object;
            cross = chars.length;
            vertical = cross == 0 ? 0 : chars[0].length;
            for (char[] chars1 : chars) {
                builder.append(arrayToString(chars1).second + "\n");
            }
        } else {
            Object[][] objects = (Objects[][]) object;
            cross = objects.length;
            vertical = cross == 0 ? 0 : objects[0].length;
            for (Object[] objects1 : objects) {
                builder.append(arrayToString(objects1).second + "\n");
            }
        }
        return Pair.create(Pair.create(cross, vertical), builder.toString());
    }
}
