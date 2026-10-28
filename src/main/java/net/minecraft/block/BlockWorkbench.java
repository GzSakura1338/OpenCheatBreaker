package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockWorkbench extends Block {
    public IIcon top;
    public IIcon front;

    public BlockWorkbench() {
        super(Material.wood);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? this.top : (side == 0 ? Blocks.planks.getBlockTextureFromSide(side) : (side != 2 && side != 4 ? this.blockIcon : this.front));
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(this.getTextureName() + "_side");
        this.top = reg.registerIcon(this.getTextureName() + "_top");
        this.front = reg.registerIcon(this.getTextureName() + "_front");
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) throws java.io.IOException {
        if (worldIn.isRemote) {
            return true;
        }

        player.displayGUIWorkbench(x, y, z);
        return true;
    }
}
