package net.minecraft.world.gen.layer;

public class GenLayerFuzzyZoom extends GenLayerZoom {
    public GenLayerFuzzyZoom(long l, GenLayer genLayer) {
        super(l, genLayer);
    }

    @Override
    public int selectModeOrRandom(int n, int n2, int n3, int n4) {
        return this.selectRandom(n, n2, n3, n4);
    }
}
