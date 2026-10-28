package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockHugeMushroom extends Block {
    public static String[] stringArray = new String[]{"skin_brown", "skin_red"};
    public int intValue;
    public IIcon[] iIconArray;
    public IIcon iIcon3;
    public IIcon iIcon2;

    public BlockHugeMushroom(Material material, int n) {
        super(material);
        this.intValue = n;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return meta == 10 && side > 1
            ? this.iIcon3
            : (
                meta >= 1 && meta <= 9 && side == 1
                    ? this.iIconArray[this.intValue]
                    : (
                        meta >= 1 && meta <= 3 && side == 2
                            ? this.iIconArray[this.intValue]
                            : (
                                meta >= 7 && meta <= 9 && side == 3
                                    ? this.iIconArray[this.intValue]
                                    : (
                                        (meta == 1 || meta == 4 || meta == 7) && side == 4
                                            ? this.iIconArray[this.intValue]
                                            : (
                                                (meta == 3 || meta == 6 || meta == 9) && side == 5
                                                    ? this.iIconArray[this.intValue]
                                                    : (meta == 14 ? this.iIconArray[this.intValue] : (meta == 15 ? this.iIcon3 : this.iIcon2))
                                            )
                                    )
                            )
                    )
            );
    }

    @Override
    public int quantityDropped(Random random) {
        int n = random.nextInt(10) - 7;
        if (n < 0) {
            n = 0;
        }

        return n;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Item.getItemById(Block.getIdFromBlock(Blocks.brown_mushroom) + this.intValue);
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return Item.getItemById(Block.getIdFromBlock(Blocks.brown_mushroom) + this.intValue);
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.iIconArray = new IIcon[stringArray.length];

        for (int i = 0; i < this.iIconArray.length; i++) {
            this.iIconArray[i] = reg.registerIcon(this.getTextureName() + "_" + stringArray[i]);
        }

        this.iIcon2 = reg.registerIcon(this.getTextureName() + "_inside");
        this.iIcon3 = reg.registerIcon(this.getTextureName() + "_skin_stem");
    }
}
