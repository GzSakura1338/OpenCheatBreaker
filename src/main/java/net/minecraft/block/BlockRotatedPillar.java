package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public abstract class BlockRotatedPillar extends Block {
    public IIcon iIcon2;

    public BlockRotatedPillar(Material material) {
        super(material);
    }

    @Override
    public int getRenderType() {
        return 31;
    }

    @Override
    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ, int meta) {
        int n = meta & 3;
        byte b = 0;
        switch (side) {
            case 0:
            case 1:
                b = 0;
                break;
            case 2:
            case 3:
                b = 8;
                break;
            case 4:
            case 5:
                b = 4;
        }

        return n | b;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        int n = meta & 12;
        int n2 = meta & 3;
        return n != 0 || side != 1 && side != 0
            ? (
                n != 4 || side != 5 && side != 4
                    ? (n != 8 || side != 2 && side != 3 ? this.getSideIcon(n2) : this.getTopIcon(n2))
                    : this.getTopIcon(n2)
            )
            : this.getTopIcon(n2);
    }

    public abstract IIcon getSideIcon(int n);

    public IIcon getTopIcon(int n) {
        return this.iIcon2;
    }

    @Override
    public int damageDropped(int meta) {
        return meta & 3;
    }

    public int computeInt(int n) {
        return n & 3;
    }

    @Override
    public ItemStack createStackedBlock(int meta) {
        return new ItemStack(Item.getItemFromBlock(this), 1, this.computeInt(meta));
    }
}
