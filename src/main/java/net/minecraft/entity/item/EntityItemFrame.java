package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityHanging;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class EntityItemFrame extends EntityHanging {
    public float itemDropChance = 1.0F;

    public EntityItemFrame(World world) {
        super(world);
    }

    public EntityItemFrame(World world, int n, int n2, int n3, int n4) {
        super(world, n, n2, n3, n4);
        this.setDirection(n4);
    }

    @Override
    public void entityInit() {
        this.getDataWatcher().addObjectByDataType(2, 5);
        this.getDataWatcher().addObject(3, (byte)0);
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isEntityInvulnerable()) {
            return false;
        }

        if (this.getDisplayedItem() != null) {
            if (!this.worldObj.isRemote) {
                this.dropHeldStack(source.getEntity(), false);
                this.setDisplayedItem(null);
            }

            return true;
        } else {
            return super.attackEntityFrom(source, amount);
        }
    }

    @Override
    public int getWidthPixels() {
        return 9;
    }

    @Override
    public int getHeightPixels() {
        return 9;
    }

    @Override
    public boolean isInRangeToRenderDist(double distance) {
        double d = 16.0;
        d *= 64.0 * this.renderDistanceWeight;
        return distance < d * d;
    }

    @Override
    public void onBroken(Entity entity) {
        this.dropHeldStack(entity, true);
    }

    public void dropHeldStack(Entity entity, boolean flag) {
        ItemStack displayedItem = this.getDisplayedItem();
        if (entity instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer)entity;
            if (entityPlayer.capabilities.isCreativeMode) {
                this.removeFrameFromMap(displayedItem);
                return;
            }
        }

        if (flag) {
            this.entityDropItem(new ItemStack(Items.item_frame), 0.0F);
        }

        if (displayedItem != null && this.rand.nextFloat() < this.itemDropChance) {
            displayedItem = displayedItem.copy();
            this.removeFrameFromMap(displayedItem);
            this.entityDropItem(displayedItem, 0.0F);
        }
    }

    public void removeFrameFromMap(ItemStack itemStack) {
        if (itemStack != null) {
            if (itemStack.getItem() == Items.filled_map) {
                MapData mapData = ((ItemMap)itemStack.getItem()).getMapData(itemStack, this.worldObj);
                mapData.playersVisibleOnMap.remove("frame-" + this.getEntityId());
            }

            itemStack.setItemFrame(null);
        }
    }

    public ItemStack getDisplayedItem() {
        return this.getDataWatcher().getWatchableObjectItemStack(2);
    }

    public void setDisplayedItem(ItemStack itemStack) {
        if (itemStack != null) {
            itemStack = itemStack.copy();
            itemStack.stackSize = 1;
            itemStack.setItemFrame(this);
        }

        this.getDataWatcher().updateObject(2, itemStack);
        this.getDataWatcher().setObjectWatched(2);
    }

    public int getRotation() {
        return this.getDataWatcher().getWatchableObjectByte(3);
    }

    public void setItemRotation(int n) {
        this.getDataWatcher().updateObject(3, (byte)(n % 4));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        if (this.getDisplayedItem() != null) {
            tagCompound.setTag("Item", this.getDisplayedItem().writeToNBT(new NBTTagCompound()));
            tagCompound.setByte("ItemRotation", (byte)this.getRotation());
            tagCompound.setFloat("ItemDropChance", this.itemDropChance);
        }

        super.writeEntityToNBT(tagCompound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        NBTTagCompound compoundTag = tagCompund.getCompoundTag("Item");
        if (compoundTag != null && !compoundTag.hasNoTags()) {
            this.setDisplayedItem(ItemStack.loadItemStackFromNBT(compoundTag));
            this.setItemRotation(tagCompund.getByte("ItemRotation"));
            if (tagCompund.hasKey("ItemDropChance", 99)) {
                this.itemDropChance = tagCompund.getFloat("ItemDropChance");
            }
        }

        super.readEntityFromNBT(tagCompund);
    }

    @Override
    public boolean interactFirst(EntityPlayer player) {
        if (this.getDisplayedItem() == null) {
            ItemStack heldItem = player.getHeldItem();
            if (heldItem != null && !this.worldObj.isRemote) {
                this.setDisplayedItem(heldItem);
                if (!player.capabilities.isCreativeMode && --heldItem.stackSize <= 0) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
                }
            }
        } else if (!this.worldObj.isRemote) {
            this.setItemRotation(this.getRotation() + 1);
        }

        return true;
    }
}
