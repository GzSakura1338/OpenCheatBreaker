package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockAnvil extends BlockFalling {
    public static String[] anvilDamageNames = new String[]{"intact", "slightlyDamaged", "veryDamaged"};
    public static String[] anvilIconNames = new String[]{"anvil_top_damaged_0", "anvil_top_damaged_1", "anvil_top_damaged_2"};
    public int anvilRenderSide;
    public IIcon[] anvilIcons;

    public BlockAnvil() {
        super(Material.anvil);
        this.setLightOpacity(0);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        if (this.anvilRenderSide == 3 && side == 1) {
            int n = (meta >> 2) % this.anvilIcons.length;
            return this.anvilIcons[n];
        } else {
            return this.blockIcon;
        }
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon("anvil_base");
        this.anvilIcons = new IIcon[anvilIconNames.length];

        for (int i = 0; i < this.anvilIcons.length; i++) {
            this.anvilIcons[i] = reg.registerIcon(anvilIconNames[i]);
        }
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        int n = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5) & 3;
        int n2 = worldIn.getBlockMetadata(x, y, z) >> 2;
        n = ++n % 4;
        if (n == 0) {
            worldIn.setBlockMetadataWithNotify(x, y, z, 2 | n2 << 2, 2);
        }

        if (n == 1) {
            worldIn.setBlockMetadataWithNotify(x, y, z, 3 | n2 << 2, 2);
        }

        if (n == 2) {
            worldIn.setBlockMetadataWithNotify(x, y, z, 0 | n2 << 2, 2);
        }

        if (n == 3) {
            worldIn.setBlockMetadataWithNotify(x, y, z, 1 | n2 << 2, 2);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) throws java.io.IOException {
        if (worldIn.isRemote) {
            return true;
        }

        player.displayGUIAnvil(x, y, z);
        return true;
    }

    @Override
    public int getRenderType() {
        return 35;
    }

    @Override
    public int damageDropped(int meta) {
        return meta >> 2;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        int n = worldIn.getBlockMetadata(x, y, z) & 3;
        if (n != 3 && n != 1) {
            this.setBlockBounds(0.125F, 0.0F, 0.0F, 0.875F, 1.0F, 1.0F);
        } else {
            this.setBlockBounds(0.0F, 0.0F, 0.125F, 1.0F, 1.0F, 0.875F);
        }
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        list.add(new ItemStack(itemIn, 1, 0));
        list.add(new ItemStack(itemIn, 1, 1));
        list.add(new ItemStack(itemIn, 1, 2));
    }

    @Override
    public void onStartFalling(EntityFallingBlock entityFallingBlock) {
        entityFallingBlock.setHurtEntities(true);
    }

    @Override
    public void playSoundWhenFallen(World world, int n, int n2, int n3, int n4) {
        world.playAuxSFX(1022, n, n2, n3, 0);
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        return true;
    }
}
