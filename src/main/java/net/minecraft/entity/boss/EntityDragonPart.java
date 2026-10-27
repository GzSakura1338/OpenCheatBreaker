package net.minecraft.entity.boss;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

public class EntityDragonPart extends Entity {
   public IEntityMultiPart entityDragonObj;
   public String partName;

   public EntityDragonPart(IEntityMultiPart parent, String partName, float base, float sizeHeight) {
      super(parent.getWorld());
      this.setSize(base, sizeHeight);
      this.entityDragonObj = parent;
      this.partName = partName;
   }

   @Override
   public void entityInit() {
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
   }

   @Override
   public boolean canBeCollidedWith() {
      return true;
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      return this.isEntityInvulnerable(source) ? false : this.entityDragonObj.attackEntityFromPart(this, source, amount);
   }

   @Override
   public boolean isEntityEqual(Entity entityIn) {
      return this == entityIn || this.entityDragonObj == entityIn;
   }
}
