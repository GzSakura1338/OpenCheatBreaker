package net.minecraft.entity.passive;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityMooshroom extends EntityCow {
    public EntityMooshroom(World world) {
        super(world);
        this.setSize(0.9F, 1.3F);
    }

    @Override
    public boolean interact(EntityPlayer entityPlayer) throws java.io.IOException {
        ItemStack currentItem = entityPlayer.inventory.getCurrentItem();
        if (currentItem != null && currentItem.getItem() == Items.bowl && this.getGrowingAge() >= 0) {
            if (currentItem.stackSize == 1) {
                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, new ItemStack(Items.mushroom_stew));
                return true;
            }

            if (entityPlayer.inventory.addItemStackToInventory(new ItemStack(Items.mushroom_stew)) && !entityPlayer.capabilities.isCreativeMode) {
                entityPlayer.inventory.decrStackSize(entityPlayer.inventory.currentItem, 1);
                return true;
            }
        }

        if (currentItem != null && currentItem.getItem() == Items.shears && this.getGrowingAge() >= 0) {
            this.setDead();
            this.worldObj.spawnParticle("largeexplode", this.posX, this.posY + this.height / 2.0F, this.posZ, 0.0, 0.0, 0.0);
            if (!this.worldObj.isRemote) {
                EntityCow entityCow = new EntityCow(this.worldObj);
                entityCow.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
                entityCow.setHealth(this.getHealth());
                entityCow.renderYawOffset = this.renderYawOffset;
                this.worldObj.spawnEntityInWorld(entityCow);

                for (int i = 0; i < 5; i++) {
                    this.worldObj
                        .spawnEntityInWorld(new EntityItem(this.worldObj, this.posX, this.posY + this.height, this.posZ, new ItemStack(Blocks.red_mushroom)));
                }

                currentItem.damageItem(1, entityPlayer);
                this.playSound("mob.sheep.shear", 1.0F, 1.0F);
            }

            return true;
        } else {
            return super.interact(entityPlayer);
        }
    }

    public EntityMooshroom createChild(EntityAgeable entityAgeable) {
        return new EntityMooshroom(this.worldObj);
    }
}
