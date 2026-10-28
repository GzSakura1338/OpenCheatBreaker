package net.minecraft.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockPotato extends BlockCrops {
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
        return Items.potato;
    }

    @Override
    public Item getCrop() {
        return Items.potato;
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, int x, int y, int z, int meta, float chance, int fortune) {
        super.dropBlockAsItemWithChance(worldIn, x, y, z, meta, chance, fortune);
        if (!worldIn.isRemote && meta >= 7 && worldIn.rand.nextInt(50) == 0) {
            this.dropBlockAsItem(worldIn, x, y, z, new ItemStack(Items.poisonous_potato));
        }
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.iIconArray = new IIcon[4];

        for (int i = 0; i < this.iIconArray.length; i++) {
            this.iIconArray[i] = reg.registerIcon(this.getTextureName() + "_stage_" + i);
        }
    }
}
