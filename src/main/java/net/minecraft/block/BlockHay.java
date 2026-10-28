package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;

public class BlockHay extends BlockRotatedPillar {
    public BlockHay() {
        super(Material.grass);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public IIcon getSideIcon(int n) {
        return this.blockIcon;
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.iIcon2 = reg.registerIcon(this.getTextureName() + "_top");
        this.blockIcon = reg.registerIcon(this.getTextureName() + "_side");
    }
}
