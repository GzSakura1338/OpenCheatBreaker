package net.minecraft.tileentity;

public class TileEntityDropper extends TileEntityDispenser {
    @Override
    public String getInventoryName() {
        return this.isCustomInventoryName() ? this.customName : "container.dropper";
    }
}
