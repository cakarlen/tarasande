package de.florianmichael.rmath.mathtable.impl;

import de.florianmichael.rmath.mathtable.MathTable;

public class LibGDXMathTable implements MathTable {
    public static final float BF_PI = 3.1415927F;

    private static final int BF_SIN_BITS = 14; // 16KB. Adjust for accuracy.
    private static final int BF_SIN_MASK = ~(-1 << BF_SIN_BITS);
    private static final int BF_SIN_COUNT = BF_SIN_MASK + 1;

    private static final float BF_radFull = BF_PI * 2;
    private static final float BF_degFull = 360;
    private static final float BF_radToIndex = BF_SIN_COUNT / BF_radFull;
    private static final float BF_degToIndex = BF_SIN_COUNT / BF_degFull;

    public static final float BF_degreesToRadians = BF_PI / 180;

    private static final float[] BF_table = new float[BF_SIN_COUNT];

    static {
        for(int i = 0; i < BF_SIN_COUNT; i++) {
            BF_table[i] = (float) Math.sin((i + 0.5F) / BF_SIN_COUNT * BF_radFull);
        }
        for (int i = 0; i < 360; i += 90) {
            BF_table[(int)(i * BF_degToIndex) & BF_SIN_MASK] = (float) Math.sin(i * BF_degreesToRadians);
        }
    }

    @Override
    public float sin(float x) {
        return BF_table[(int)(x * BF_radToIndex) & BF_SIN_MASK];
    }

    @Override
    public float cos(float x) {
        return BF_table[(int)((x + BF_PI / 2) * BF_radToIndex) & BF_SIN_MASK];
    }
}
