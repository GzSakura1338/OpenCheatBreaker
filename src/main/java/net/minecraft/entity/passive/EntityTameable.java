package net.minecraft.entity.passive;

import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public abstract class EntityTameable extends EntityAnimal implements IEntityOwnable {
   public EntityAISit aiSit = new EntityAISit(this);

   public EntityTameable(World worldIn) {
      super(worldIn);
      this.setupTamedAI();
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
      if (this.getOwnerId() == null) {
         tagCompound.setString("OwnerUUID", "");
      } else {
         tagCompound.setString("OwnerUUID", this.getOwnerId());
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
         str = PreYggdrasilConverter.getStringUUIDFromName(string);
      }

      if (str.length() > 0) {
         this.setOwnerId(str);
         this.setTamed(true);
      }

      this.aiSit.setSitting(tagCompund.getBoolean("Sitting"));
      this.setSitting(tagCompund.getBoolean("Sitting"));
   }

   public void playTameEffect(boolean play) {
      EnumParticleTypes enumParticleTypes = EnumParticleTypes.HEART;
      if (!play) {
         enumParticleTypes = EnumParticleTypes.SMOKE_NORMAL;
      }

      for (int i = 0; i < 7; i++) {
         double d = this.rand.nextGaussian() * 0.02;
         double d2 = this.rand.nextGaussian() * 0.02;
         double d3 = this.rand.nextGaussian() * 0.02;
         this.worldObj
            .spawnParticle(
               enumParticleTypes,
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
   public void handleStatusUpdate(byte id) {
      if (id == 7) {
         this.playTameEffect(true);
      } else if (id == 6) {
         this.playTameEffect(false);
      } else {
         super.handleStatusUpdate(id);
      }
   }

   public boolean isTamed() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 4) != 0;
   }

   public void setTamed(boolean tamed) {
      byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(16);
      if (tamed) {
         this.dataWatcher.updateObject(16, (byte)(watchableObjectByte | 4));
      } else {
         this.dataWatcher.updateObject(16, (byte)(watchableObjectByte & -5));
      }

      this.setupTamedAI();
   }

   public void setupTamedAI() {
   }

   public boolean isSitting() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
   }

   public void setSitting(boolean sitting) {
      byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(16);
      if (sitting) {
         this.dataWatcher.updateObject(16, (byte)(watchableObjectByte | 1));
      } else {
         this.dataWatcher.updateObject(16, (byte)(watchableObjectByte & -2));
      }
   }

   @Override
   public String getOwnerId() {
      return this.dataWatcher.getWatchableObjectString(17);
   }

   public void setOwnerId(String ownerUuid) {
      this.dataWatcher.updateObject(17, ownerUuid);
   }

   public EntityLivingBase getOwner() {
      try {
         UUID fromString = UUID.fromString(this.getOwnerId());
         return fromString == null ? null : this.worldObj.getPlayerEntityByUUID(fromString);
      } catch (IllegalArgumentException e) {
         return null;
      }
   }

   public boolean isOwner(EntityLivingBase entityIn) {
      return entityIn == this.getOwner();
   }

   public EntityAISit getAISit() {
      return this.aiSit;
   }

   public boolean shouldAttackEntity(EntityLivingBase entityLivingBase, EntityLivingBase entityLivingBase2) {
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
   public boolean isOnSameTeam(EntityLivingBase otherEntity) {
      if (this.isTamed()) {
         EntityLivingBase owner = this.getOwner();
         if (otherEntity == owner) {
            return true;
         }

         if (owner != null) {
            return owner.isOnSameTeam(otherEntity);
         }
      }

      return super.isOnSameTeam(otherEntity);
   }

   @Override
   public void onDeath(DamageSource cause) {
      if (!this.worldObj.isRemote
         && this.worldObj.getGameRules().getBoolean("showDeathMessages")
         && this.hasCustomName()
         && this.getOwner() instanceof EntityPlayerMP) {
         ((EntityPlayerMP)this.getOwner()).addChatMessage(this.getCombatTracker().getDeathMessage());
      }

      super.onDeath(cause);
   }
}
