package net.minecraft.item;

import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

public class ItemBoat extends Item {
    public ItemBoat() {
        this.maxStackSize = 1;
        this.setCreativeTab(CreativeTabs.tabTransport);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer player) {
        float f = 1.0F;
        float f2 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * f;
        float f3 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * f;
        double d = player.prevPosX + (player.posX - player.prevPosX) * f;
        double d2 = player.prevPosY + (player.posY - player.prevPosY) * f + 1.62 - player.yOffset;
        double d3 = player.prevPosZ + (player.posZ - player.prevPosZ) * f;
        Vec3 createVectorHelper = Vec3.createVectorHelper(d, d2, d3);
        float cos = MathHelper.cos(-f3 * (float) (Math.PI / 180.0) - (float) Math.PI);
        float sin = MathHelper.sin(-f3 * (float) (Math.PI / 180.0) - (float) Math.PI);
        float f4 = -MathHelper.cos(-f2 * (float) (Math.PI / 180.0));
        float sin2 = MathHelper.sin(-f2 * (float) (Math.PI / 180.0));
        float f5 = sin * f4;
        float f6 = cos * f4;
        double d4 = 5.0;
        Vec3 addVector = createVectorHelper.addVector(f5 * d4, sin2 * d4, f6 * d4);
        MovingObjectPosition rayTraceBlocks = worldIn.rayTraceBlocks(createVectorHelper, addVector, true);
        if (rayTraceBlocks == null) {
            return itemStackIn;
        }

        Vec3 look = player.getLook(f);
        boolean flag = false;
        float f7 = 1.0F;
        List list = worldIn.getEntitiesWithinAABBExcludingEntity(
            player, player.boundingBox.addCoord(look.xCoord * d4, look.yCoord * d4, look.zCoord * d4).expand(f7, f7, f7)
        );

        for (int i = 0; i < list.size(); i++) {
            Entity entity = (Entity)list.get(i);
            if (entity.canBeCollidedWith()) {
                float collisionBorderSize = entity.getCollisionBorderSize();
                AxisAlignedBB expand = entity.boundingBox.expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
                if (expand.isVecInside(createVectorHelper)) {
                    flag = true;
                }
            }
        }

        if (flag) {
            return itemStackIn;
        }

        if (rayTraceBlocks.typeOfHit == MovingObjectType.BLOCK) {
            int blockX = rayTraceBlocks.blockX;
            int blockY = rayTraceBlocks.blockY;
            int blockZ = rayTraceBlocks.blockZ;
            if (worldIn.getBlock(blockX, blockY, blockZ) == Blocks.snow_layer) {
                blockY--;
            }

            EntityBoat entityBoat = new EntityBoat(worldIn, blockX + 0.5F, blockY + 1.0F, blockZ + 0.5F);
            entityBoat.rotationYaw = ((MathHelper.floor_double(player.rotationYaw * 4.0F / 360.0F + 0.5) & 3) - 1) * 90;
            if (!worldIn.getCollidingBoundingBoxes(entityBoat, entityBoat.boundingBox.expand(-0.1, -0.1, -0.1)).isEmpty()) {
                return itemStackIn;
            }

            if (!worldIn.isRemote) {
                worldIn.spawnEntityInWorld(entityBoat);
            }

            if (!player.capabilities.isCreativeMode) {
                itemStackIn.stackSize--;
            }
        }

        return itemStackIn;
    }
}
