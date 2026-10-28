package net.minecraft.client.util;

import com.google.common.collect.ComparisonChain;
import java.util.Comparator;
import net.minecraft.client.renderer.RenderList;

public class RenderDistanceSorter implements Comparator {
    public int intValue;
    public int intValue2;

    public RenderDistanceSorter(int n, int n2) {
        this.intValue = n;
        this.intValue2 = n2;
    }

    public int compare(RenderList renderList, RenderList renderList2) {
        int n = renderList.renderChunkX - this.intValue;
        int n2 = renderList.renderChunkZ - this.intValue2;
        int n3 = renderList2.renderChunkX - this.intValue;
        int n4 = renderList2.renderChunkZ - this.intValue2;
        int n5 = n * n + n2 * n2;
        int n6 = n3 * n3 + n4 * n4;
        return ComparisonChain.start().compare(n6, n5).result();
    }

    @Override
    public int compare(Object obj, Object obj2) {
        return this.compare((RenderList)obj, (RenderList)obj2);
    }
}
