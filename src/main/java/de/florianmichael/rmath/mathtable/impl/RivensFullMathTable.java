package de.florianmichael.rmath.mathtable.impl;

import de.florianmichael.rmath.mathtable.MathTable;

public class RivensFullMathTable implements MathTable {

    private static final float BF_SIN_TO_COS;
    private static final int BF_SIN_BITS, BF_SIN_MASK, BF_SIN_COUNT;
    private static final float BF_radFull, BF_radToIndex;
    private static final float[] BF_sinFull;

    static {
        BF_SIN_TO_COS = (float) (Math.PI * 0.5f);

        BF_SIN_BITS = 12;
        BF_SIN_MASK = ~(-1 << BF_SIN_BITS);
        BF_SIN_COUNT = BF_SIN_MASK + 1;

        BF_radFull = (float) (Math.PI * 2.0);
        BF_radToIndex = BF_SIN_COUNT / BF_radFull;

        BF_sinFull = new float[BF_SIN_COUNT];
        for (int i = 0; i < BF_SIN_COUNT; i++) {
            BF_sinFull[i] = (float) Math.sin((i + Math.min(1, i % (BF_SIN_COUNT / 4)) * 0.5) / BF_SIN_COUNT * BF_radFull);
        }
    }

    @Override
    public float sin(float x) {
        return BF_sinFull[(int)(x * BF_radToIndex) & BF_SIN_MASK];
    }

    @Override
    public float cos(float x) {
        return sin(x + BF_SIN_TO_COS);
    }
}