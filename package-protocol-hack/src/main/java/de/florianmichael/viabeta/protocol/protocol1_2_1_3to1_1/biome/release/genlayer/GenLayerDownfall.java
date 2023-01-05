package de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.release.genlayer;

import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.release.IntCache;
import de.florianmichael.viabeta.protocol.protocol1_2_1_3to1_1.biome.release.NewBiomeGenBase;

public class GenLayerDownfall extends GenLayer {
    public GenLayerDownfall(GenLayer genlayer) {
        super(0L);
        parent = genlayer;
    }

    public int[] getInts(int i, int j, int k, int l) {
        int[] ai = parent.getInts(i, j, k, l);
        int[] ai1 = IntCache.getIntCache(k * l);
        for (int i1 = 0; i1 < k * l; i1++) {
            ai1[i1] = NewBiomeGenBase.BIOME_LIST[ai[i1]].getIntRainfall();
        }

        return ai1;
    }
}
