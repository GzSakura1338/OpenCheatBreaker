package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockCauldron extends Block {
    public IIcon iconInner;
    public IIcon iconTop;
    public IIcon iconBottom;

    public BlockCauldron() {
        super(Material.iron);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 1 ? this.iconTop : (side == 0 ? this.iconBottom : this.blockIcon);
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.iconInner = reg.registerIcon(this.getTextureName() + "_inner");
        this.iconTop = reg.registerIcon(this.getTextureName() + "_top");
        this.iconBottom = reg.registerIcon(this.getTextureName() + "_bottom");
        this.blockIcon = reg.registerIcon(this.getTextureName() + "_side");
    }

    public static IIcon getCauldronIcon(String iconName) {
        return iconName.equals("inner") ? Blocks.cauldron.iconInner : (iconName.equals("bottom") ? Blocks.cauldron.iconBottom : null);
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List list, Entity collider) {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.3125F, 1.0F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        float f = 0.125F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        this.setBlockBoundsForItemRender();
    }

    @Override
    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public int getRenderType() {
        return 24;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, int x, int y, int z, Entity entityIn) {
        int powerFromMeta = getPowerFromMeta(worldIn.getBlockMetadata(x, y, z));
        float f = y + (6.0F + 3 * powerFromMeta) / 16.0F;
        if (!worldIn.isRemote && entityIn.isBurning() && powerFromMeta > 0 && entityIn.boundingBox.minY <= f) {
            entityIn.extinguish();
            this.setWaterLevel(worldIn, x, y, z, powerFromMeta - 1);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        if (worldIn.isRemote) {
            return true;
        }

        ItemStack currentItem = player.inventory.getCurrentItem();
        if (currentItem == null) {
            return true;
        }

        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        int powerFromMeta = getPowerFromMeta(blockMetadata);
        if (currentItem.getItem() == Items.water_bucket) {
            if (powerFromMeta < 3) {
                if (!player.capabilities.isCreativeMode) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.bucket));
                }

                this.setWaterLevel(worldIn, x, y, z, 3);
            }

            return true;
        } else {
            if (currentItem.getItem() == Items.glass_bottle) {
                if (powerFromMeta > 0) {
                    if (!player.capabilities.isCreativeMode) {
                        ItemStack itemStack = new ItemStack(Items.potionitem, 1, 0);
                        if (!player.inventory.addItemStackToInventory(itemStack)) {
                            worldIn.spawnEntityInWorld(new EntityItem(worldIn, x + 0.5, y + 1.5, z + 0.5, itemStack));
                        } else if (player instanceof EntityPlayerMP) {
                            ((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
                        }

                        currentItem.stackSize--;
                        if (currentItem.stackSize <= 0) {
                            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                        }
                    }

                    this.setWaterLevel(worldIn, x, y, z, powerFromMeta - 1);
                }
            } else if (powerFromMeta > 0 && currentItem.getItem() instanceof ItemArmor && ((ItemArmor)currentItem.getItem()).getArmorMaterial() == ArmorMaterial.CLOTH) {
                ItemArmor item = (ItemArmor)currentItem.getItem();
                item.removeColor(currentItem);
                this.setWaterLevel(worldIn, x, y, z, powerFromMeta - 1);
                return true;
            }

            return false;
        }
    }

    public void setWaterLevel(World worldIn, int x, int y, int z, int level) {
        worldIn.setBlockMetadataWithNotify(x, y, z, MathHelper.clamp_int(level, 0, 3), 2);
        worldIn.updateNeighborsAboutBlockChange(x, y, z, this);
    }

    @Override
    public void fillWithRain(World worldIn, int x, int y, int z) {
        if (worldIn.rand.nextInt(20) == 1) {
            int blockMetadata = worldIn.getBlockMetadata(x, y, z);
            if (blockMetadata < 3) {
                worldIn.setBlockMetadataWithNotify(x, y, z, blockMetadata + 1, 2);
            }
        }
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Items.cauldron;
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return Items.cauldron;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World worldIn, int x, int y, int z, int side) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        return getPowerFromMeta(blockMetadata);
    }

    public static int getPowerFromMeta(int meta) {
        return meta;
    }

    public static float getRenderLiquidLevel(int meta) {
        int n = MathHelper.clamp_int(meta, 0, 3);
        return (6 + 3 * n) / 16.0F;
    }
}
