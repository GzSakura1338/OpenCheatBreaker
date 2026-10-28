package net.minecraft.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityList.EntityEggInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class ItemMonsterPlacer extends Item {
    public IIcon theIcon;

    public ItemMonsterPlacer() {
        this.setHasSubtypes(true);
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public String getItemStackDisplayName(ItemStack itemStack) {
        String trim = ("" + StatCollector.translateToLocal(this.getUnlocalizedName() + ".name")).trim();
        String stringFromID = EntityList.getStringFromID(itemStack.getMetadata());
        if (stringFromID != null) {
            trim = trim + " " + StatCollector.translateToLocal("entity." + stringFromID + ".name");
        }

        return trim;
    }

    @Override
    public int getColorFromItemStack(ItemStack itemStack, int n) {
        EntityEggInfo entityEggInfo = (EntityEggInfo)EntityList.entityEggs.get(itemStack.getMetadata());
        return entityEggInfo != null ? (n == 0 ? entityEggInfo.primaryColor : entityEggInfo.secondaryColor) : 16777215;
    }

    @Override
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    public IIcon getIconFromDamageForRenderPass(int n, int n2) {
        return n2 > 0 ? this.theIcon : super.getIconFromDamageForRenderPass(n, n2);
    }

    @Override
    public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int n, int n2, int n3, int n4, float f, float f2, float f3) {
        if (world.isRemote) {
            return true;
        }

        Block block = world.getBlock(n, n2, n3);
        n += Facing.offsetsXForSide[n4];
        n2 += Facing.offsetsYForSide[n4];
        n3 += Facing.offsetsZForSide[n4];
        double d = 0.0;
        if (n4 == 1 && block.getRenderType() == 11) {
            d = 0.5;
        }

        Entity spawnCreature2 = spawnCreature(world, itemStack.getMetadata(), n + 0.5, n2 + d, n3 + 0.5);
        if (spawnCreature2 != null) {
            if (spawnCreature2 instanceof EntityLivingBase && itemStack.hasDisplayName()) {
                ((EntityLiving)spawnCreature2).setCustomNameTag(itemStack.getDisplayName());
            }

            if (!entityPlayer.capabilities.isCreativeMode) {
                itemStack.stackSize--;
            }
        }

        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        if (worldIn.isRemote) {
            return itemStackIn;
        }

        MovingObjectPosition movingObjectPositionFromPlayer = this.getMovingObjectPositionFromPlayer(worldIn, player, true);
        if (movingObjectPositionFromPlayer == null) {
            return itemStackIn;
        }

        if (movingObjectPositionFromPlayer.typeOfHit == MovingObjectType.BLOCK) {
            int blockX = movingObjectPositionFromPlayer.blockX;
            int blockY = movingObjectPositionFromPlayer.blockY;
            int blockZ = movingObjectPositionFromPlayer.blockZ;
            if (!worldIn.canMineBlock(player, blockX, blockY, blockZ)) {
                return itemStackIn;
            }

            if (!player.canPlayerEdit(blockX, blockY, blockZ, movingObjectPositionFromPlayer.sideHit, itemStackIn)) {
                return itemStackIn;
            }

            if (worldIn.getBlock(blockX, blockY, blockZ) instanceof BlockLiquid) {
                Entity spawnCreature2 = spawnCreature(worldIn, itemStackIn.getMetadata(), blockX, blockY, blockZ);
                if (spawnCreature2 != null) {
                    if (spawnCreature2 instanceof EntityLivingBase && itemStackIn.hasDisplayName()) {
                        ((EntityLiving)spawnCreature2).setCustomNameTag(itemStackIn.getDisplayName());
                    }

                    if (!player.capabilities.isCreativeMode) {
                        itemStackIn.stackSize--;
                    }
                }
            }
        }

        return itemStackIn;
    }

    public static Entity spawnCreature(World world, int n, double d, double d2, double d3) {
        if (!EntityList.entityEggs.containsKey(n)) {
            return null;
        }

        Entity entity = null;

        for (int i = 0; i < 1; i++) {
            entity = EntityList.createEntityByID(n, world);
            if (entity != null && entity instanceof EntityLivingBase) {
                EntityLiving entityLiving = (EntityLiving)entity;
                entity.setLocationAndAngles(d, d2, d3, MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F), 0.0F);
                entityLiving.rotationYawHead = entityLiving.rotationYaw;
                entityLiving.renderYawOffset = entityLiving.rotationYaw;
                entityLiving.onSpawnWithEgg(null);
                world.spawnEntityInWorld(entity);
                entityLiving.playLivingSound();
            }
        }

        return entity;
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list) {
        for (EntityEggInfo entityEggInfo : (Iterable<EntityEggInfo>)EntityList.entityEggs.values()) {
            list.add(new ItemStack(item, 1, entityEggInfo.spawnedID));
        }
    }

    @Override
    public void registerIcons(IIconRegister register) {
        super.registerIcons(register);
        this.theIcon = register.registerIcon(this.getIconString() + "_overlay");
    }
}
