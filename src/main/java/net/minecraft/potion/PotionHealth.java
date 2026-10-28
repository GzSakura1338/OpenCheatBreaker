package net.minecraft.potion;

public class PotionHealth extends Potion {
    public PotionHealth(int n, boolean flag, int n2) {
        super(n, flag, n2);
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public boolean isReady(int n, int n2) {
        return n >= 1;
    }
}
