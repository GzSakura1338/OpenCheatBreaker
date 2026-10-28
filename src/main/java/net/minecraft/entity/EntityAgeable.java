package net.minecraft.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public abstract class EntityAgeable extends EntityCreature {
    public float floatValue3 = -1.0F;
    public float floatValue2;

    public EntityAgeable(World world) {
        super(world);
    }

    public abstract EntityAgeable createChild(EntityAgeable entityAgeable);

    @Override
    public boolean interact(EntityPlayer entityPlayer) throws java.io.IOException {
        ItemStack currentItem = entityPlayer.inventory.getCurrentItem();
        if (currentItem != null && currentItem.getItem() == Items.spawn_egg) {
            if (!this.worldObj.isRemote) {
                Class classFromID = EntityList.getClassFromID(currentItem.getMetadata());
                if (classFromID != null && classFromID.isAssignableFrom(this.getClass())) {
                    EntityAgeable createChild = this.createChild(this);
                    if (createChild != null) {
                        createChild.setGrowingAge(-24000);
                        createChild.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
                        this.worldObj.spawnEntityInWorld(createChild);
                        if (currentItem.hasDisplayName()) {
                            createChild.setCustomNameTag(currentItem.getDisplayName());
                        }

                        if (!entityPlayer.capabilities.isCreativeMode) {
                            currentItem.stackSize--;
                            if (currentItem.stackSize <= 0) {
                                entityPlayer.inventory.setInventorySlotContents(entityPlayer.inventory.currentItem, null);
                            }
                        }
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(12, new Integer(0));
    }

    public int getGrowingAge() {
        return this.dataWatcher.getWatchableObjectInt(12);
    }

    public void addGrowth(int n) {
        int growingAge = this.getGrowingAge();
        growingAge += n * 20;
        if (growingAge > 0) {
            growingAge = 0;
        }

        this.setGrowingAge(growingAge);
    }

    public void setGrowingAge(int n) {
        this.dataWatcher.updateObject(12, n);
        this.setScaleForAge(this.isChild());
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("Age", this.getGrowingAge());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        this.setGrowingAge(tagCompund.getInteger("Age"));
    }

    @Override
    public void onLivingUpdate() throws java.io.IOException {
        super.onLivingUpdate();
        if (this.worldObj.isRemote) {
            this.setScaleForAge(this.isChild());
        } else {
            int growingAge = this.getGrowingAge();
            if (growingAge < 0) {
                this.setGrowingAge(++growingAge);
            } else if (growingAge > 0) {
                this.setGrowingAge(--growingAge);
            }
        }
    }

    @Override
    public boolean isChild() {
        return this.getGrowingAge() < 0;
    }

    public void setScaleForAge(boolean flag) {
        this.setScale(flag ? 0.5F : 1.0F);
    }

    @Override
    public void setSize(float width, float height) {
        boolean flag = this.floatValue3 > 0.0F;
        this.floatValue3 = width;
        this.floatValue2 = height;
        if (!flag) {
            this.setScale(1.0F);
        }
    }

    public void setScale(float f) {
        super.setSize(this.floatValue3 * f, this.floatValue2 * f);
    }
}
