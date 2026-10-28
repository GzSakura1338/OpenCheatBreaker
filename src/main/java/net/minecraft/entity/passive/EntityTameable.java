package net.minecraft.entity.passive;

import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.world.World;

public abstract class EntityTameable extends EntityAnimal implements IEntityOwnable {
    public EntityAISit aiSit = new EntityAISit(this);

    public EntityTameable(World world) {
        super(world);
    }

    @Override
    public void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, (byte)0);
        this.dataWatcher.addObject(17, "");
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        if (this.getWatchableObjectString() == null) {
            tagCompound.setString("OwnerUUID", "");
        } else {
            tagCompound.setString("OwnerUUID", this.getWatchableObjectString());
        }

        tagCompound.setBoolean("Sitting", this.isSitting());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        String str = "";
        if (tagCompund.hasKey("OwnerUUID", 8)) {
            str = tagCompund.getString("OwnerUUID");
        } else {
            String string = tagCompund.getString("Owner");
            str = PreYggdrasilConverter.getPlayerUuidByName(string);
        }

        if (str.length() > 0) {
            this.updateObject(str);
            this.setTamed(true);
        }

        this.aiSit.setSitting(tagCompund.getBoolean("Sitting"));
        this.setSitting(tagCompund.getBoolean("Sitting"));
    }

    public void playTameEffect(boolean flag) {
        String str = "heart";
        if (!flag) {
            str = "smoke";
        }

        for (int i = 0; i < 7; i++) {
            double d = this.rand.nextGaussian() * 0.02;
            double d2 = this.rand.nextGaussian() * 0.02;
            double d3 = this.rand.nextGaussian() * 0.02;
            this.worldObj
                .spawnParticle(
                    str,
                    this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                    this.posY + 0.5 + this.rand.nextFloat() * this.height,
                    this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                    d,
                    d2,
                    d3
                );
        }
    }

    @Override
    public void handleHealthUpdate(byte b) {
        if (b == 7) {
            this.playTameEffect(true);
        } else if (b == 6) {
            this.playTameEffect(false);
        } else {
            super.handleHealthUpdate(b);
        }
    }

    public boolean isTamed() {
        return (this.dataWatcher.getWatchableObjectByte(16) & 4) != 0;
    }

    public void setTamed(boolean flag) {
        byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(16);
        if (flag) {
            this.dataWatcher.updateObject(16, (byte)(watchableObjectByte | 4));
        } else {
            this.dataWatcher.updateObject(16, (byte)(watchableObjectByte & -5));
        }
    }

    public boolean isSitting() {
        return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    public void setSitting(boolean flag) {
        byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(16);
        if (flag) {
            this.dataWatcher.updateObject(16, (byte)(watchableObjectByte | 1));
        } else {
            this.dataWatcher.updateObject(16, (byte)(watchableObjectByte & -2));
        }
    }

    @Override
    public String getWatchableObjectString() {
        return this.dataWatcher.getWatchableObjectString(17);
    }

    public void updateObject(String str) {
        this.dataWatcher.updateObject(17, str);
    }

    public EntityLivingBase getOwner() {
        try {
            UUID fromString = UUID.fromString(this.getWatchableObjectString());
            return fromString == null ? null : this.worldObj.getPlayerEntityByUUID(fromString);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public boolean isOwner(EntityLivingBase entityLivingBase) {
        return entityLivingBase == this.getOwner();
    }

    public EntityAISit getAISit() {
        return this.aiSit;
    }

    public boolean canAttackWithOwner(EntityLivingBase entityLivingBase, EntityLivingBase entityLivingBase2) {
        return true;
    }

    @Override
    public Team getTeam() {
        if (this.isTamed()) {
            EntityLivingBase owner = this.getOwner();
            if (owner != null) {
                return owner.getTeam();
            }
        }

        return super.getTeam();
    }

    @Override
    public boolean isOnSameTeam(EntityLivingBase entityLivingBase) {
        if (this.isTamed()) {
            EntityLivingBase owner = this.getOwner();
            if (entityLivingBase == owner) {
                return true;
            }

            if (owner != null) {
                return owner.isOnSameTeam(entityLivingBase);
            }
        }

        return super.isOnSameTeam(entityLivingBase);
    }
}
