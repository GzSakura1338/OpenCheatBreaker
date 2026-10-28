package net.minecraft.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;

public class BlockCarrot extends BlockCrops {
    public IIcon[] iIconArray;

    @Override
    public IIcon getIcon(int side, int meta) {
        if (meta < 7) {
            if (meta == 6) {
                meta = 5;
            }

            return this.iIconArray[meta >> 1];
        } else {
            return this.iIconArray[3];
        }
    }

    @Override
    public Item getSeed() {
        return Items.carrot;
    }

    @Override
    public Item getCrop() {
        return Items.carrot;
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.iIconArray = new IIcon[4];

        for (int i = 0; i < this.iIconArray.length; i++) {
            this.iIconArray[i] = reg.registerIcon(this.getTextureName() + "_stage_" + i);
        }
    }
}
