package net.minecraft.tileentity;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityBeacon extends TileEntity implements IInventory {
    public static Potion[][] effectsList = new Potion[][]{
        {Potion.moveSpeed, Potion.digSpeed}, {Potion.resistance, Potion.jump}, {Potion.damageBoost}, {Potion.regeneration}
    };
    public long lastBeamRenderTime;
    public float beamSpeed;
    public boolean isComplete;
    public int levels = -1;
    public int primaryEffect;
    public int secondaryEffect;
    public ItemStack payment;
    public String customName;

    @Override
    public void updateEntity() {
        if (this.worldObj.getTotalWorldTime() % 80L == 0L) {
            this.updateBeam();
            this.addPlayerEffects();
        }
    }

    public void addPlayerEffects() {
        if (this.isComplete && this.levels > 0 && !this.worldObj.isRemote && this.primaryEffect > 0) {
            double d = this.levels * 10 + 10;
            byte b = 0;
            if (this.levels >= 4 && this.primaryEffect == this.secondaryEffect) {
                b = 1;
            }

            AxisAlignedBB expand = AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord, this.zCoord, this.xCoord + 1, this.yCoord + 1, this.zCoord + 1)
                .expand(d, d, d);
            expand.maxY = this.worldObj.getHeight();
            List entitiesWithinAABB = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, expand);

            for (EntityPlayer entityPlayer : (Iterable<EntityPlayer>)entitiesWithinAABB) {
                entityPlayer.addPotionEffect(new PotionEffect(this.primaryEffect, 180, b, true));
            }

            if (this.levels >= 4 && this.primaryEffect != this.secondaryEffect && this.secondaryEffect > 0) {
                for (EntityPlayer entityPlayer : (Iterable<EntityPlayer>)entitiesWithinAABB) {
                    entityPlayer.addPotionEffect(new PotionEffect(this.secondaryEffect, 180, 0, true));
                }
            }
        }
    }

    public void updateBeam() {
        int levels = this.levels;
        if (!this.worldObj.canBlockSeeTheSky(this.xCoord, this.yCoord + 1, this.zCoord)) {
            this.isComplete = false;
            this.levels = 0;
        } else {
            this.isComplete = true;
            this.levels = 0;

            for (int i = 1; i <= 4; this.levels = i++) {
                int n = this.yCoord - i;
                if (n < 0) {
                    break;
                }

                boolean flag = true;

                for (int j = this.xCoord - i; j <= this.xCoord + i && flag; j++) {
                    for (int k = this.zCoord - i; k <= this.zCoord + i; k++) {
                        Block block = this.worldObj.getBlock(j, n, k);
                        if (block != Blocks.emerald_block && block != Blocks.gold_block && block != Blocks.diamond_block && block != Blocks.iron_block) {
                            flag = false;
                            break;
                        }
                    }
                }

                if (!flag) {
                    break;
                }
            }

            if (this.levels == 0) {
                this.isComplete = false;
            }
        }

        if (!this.worldObj.isRemote && this.levels == 4 && levels < this.levels) {
            for (EntityPlayer entityPlayer : (Iterable<EntityPlayer>)(this.worldObj
                .getEntitiesWithinAABB(
                    EntityPlayer.class,
                    AxisAlignedBB.getBoundingBox(this.xCoord, this.yCoord, this.zCoord, this.xCoord, this.yCoord - 4, this.zCoord).expand(10.0, 5.0, 10.0)
                ))) {
                entityPlayer.triggerAchievement(AchievementList.fullBeacon);
            }
        }
    }

    public float shouldBeamRender() {
        if (!this.isComplete) {
            return 0.0F;
        }

        int n = (int)(this.worldObj.getTotalWorldTime() - this.lastBeamRenderTime);
        this.lastBeamRenderTime = this.worldObj.getTotalWorldTime();
        if (n > 1) {
            this.beamSpeed -= n / 40.0F;
            if (this.beamSpeed < 0.0F) {
                this.beamSpeed = 0.0F;
            }
        }

        this.beamSpeed += 0.025F;
        if (this.beamSpeed > 1.0F) {
            this.beamSpeed = 1.0F;
        }

        return this.beamSpeed;
    }

    public int getPrimaryEffect() {
        return this.primaryEffect;
    }

    public int getSecondaryEffect() {
        return this.secondaryEffect;
    }

    public int getLevels() {
        return this.levels;
    }

    public void setLevels(int n) {
        this.levels = n;
    }

    public void setPrimaryEffect(int n) {
        this.primaryEffect = 0;

        for (int i = 0; i < this.levels && i < 3; i++) {
            for (Potion potion : effectsList[i]) {
                if (potion.id == n) {
                    this.primaryEffect = n;
                    return;
                }
            }
        }
    }

    public void setSecondaryEffect(int n) {
        this.secondaryEffect = 0;
        if (this.levels >= 4) {
            for (int i = 0; i < 4; i++) {
                for (Potion potion : effectsList[i]) {
                    if (potion.id == n) {
                        this.secondaryEffect = n;
                        return;
                    }
                }
            }
        }
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbttagCompound = new NBTTagCompound();
        this.writeToNBT(nbttagCompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 3, nbttagCompound);
    }

    @Override
    public double getMaxRenderDistanceSquared() {
        return 65536.0;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.primaryEffect = compound.getInteger("Primary");
        this.secondaryEffect = compound.getInteger("Secondary");
        this.levels = compound.getInteger("Levels");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Primary", this.primaryEffect);
        compound.setInteger("Secondary", this.secondaryEffect);
        compound.setInteger("Levels", this.levels);
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slotIn) {
        return slotIn == 0 ? this.payment : null;
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        if (index != 0 || this.payment == null) {
            return null;
        } else if (count >= this.payment.stackSize) {
            ItemStack payment = this.payment;
            this.payment = null;
            return payment;
        } else {
            this.payment.stackSize -= count;
            return new ItemStack(this.payment.getItem(), count, this.payment.getMetadata());
        }
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index) {
        if (index == 0 && this.payment != null) {
            ItemStack payment = this.payment;
            this.payment = null;
            return payment;
        } else {
            return null;
        }
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        if (index == 0) {
            this.payment = stack;
        }
    }

    @Override
    public String getInventoryName() {
        return this.isCustomInventoryName() ? this.customName : "container.beacon";
    }

    @Override
    public boolean isCustomInventoryName() {
        return this.customName != null && this.customName.length() > 0;
    }

    public void setCustomName(String str) {
        this.customName = str;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) == this
            && player.getDistanceSq(this.xCoord + 0.5, this.yCoord + 0.5, this.zCoord + 0.5) <= 64.0;
    }

    @Override
    public void openChest() {
    }

    @Override
    public void closeChest() {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return stack.getItem() == Items.emerald || stack.getItem() == Items.diamond || stack.getItem() == Items.gold_ingot || stack.getItem() == Items.iron_ingot;
    }
}
