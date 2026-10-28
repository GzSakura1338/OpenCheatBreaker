package net.minecraft.block.material;

public class MaterialLogic extends Material {
    public MaterialLogic(MapColor mapColor) {
        super(mapColor);
        this.setAdventureModeExempt();
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean blocksLight() {
        return false;
    }

    @Override
    public boolean blocksMovement() {
        return false;
    }
}
