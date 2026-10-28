package net.minecraft.block;

import net.minecraft.world.World;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class BlockRail extends BlockRailBase
{
    public IIcon turnedTexture;
    
    public BlockRail() {
        super(false);
    }
    
    @Override
    public IIcon getIcon(final int side, final int meta) {
        return (meta >= 6) ? this.turnedTexture : this.blockIcon;
    }
    
    @Override
    public void registerIcons(final IIconRegister reg) {
        super.registerIcons(reg);
        this.turnedTexture = reg.registerIcon(this.getTextureName() + "_turned");
    }
    
    @Override
    public void onRedstoneSignal(final World world, final int n, final int n2, final int n3, final int n4, final int n5, final Block block) {
        if (block.canProvidePower() && new Rail(world, n, n2, n3).countAdjacentRails() == 3) {
            this.refreshTrackShape(world, n, n2, n3, false);
        }
    }
}
