package net.minecraft.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityAgeable extends EntityCreature {
   public int growingAge;
   public int forcedAge;
   public int intValue;
   public float ageWidth = -1.0F;
   public float ageHeight;

   public EntityAgeable(World worldIn) {
      super(worldIn);
   }

   public abstract EntityAgeable createChild(EntityAgeable entityAgeable);

   @Override
   public boolean interact(EntityPlayer player) {
      ItemStack currentItem = player.inventory.getCurrentItem();
      if (currentItem != null && currentItem.getItem() == Items.spawn_egg) {
         if (!this.worldObj.isRemote) {
            Class classFromID = EntityList.getClassFromID(currentItem.getMetadata());
            if (classFromID != null && this.getClass() == classFromID) {
               EntityAgeable createChild = this.createChild(this);
               if (createChild != null) {
                  createChild.setGrowingAge(-24000);
                  createChild.setLocationAndAngles(this.posX, this.posY, this.posZ, 0.0F, 0.0F);
                  this.worldObj.spawnEntityInWorld(createChild);
                  if (currentItem.hasDisplayName()) {
                     createChild.setCustomNameTag(currentItem.getDisplayName());
                  }

                  if (!player.capabilities.isCreativeMode) {
                     currentItem.stackSize--;
                     if (currentItem.stackSize <= 0) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
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
      this.dataWatcher.addObject(12, (byte)0);
   }

   public int getGrowingAge() {
      return this.worldObj.isRemote ? this.dataWatcher.getWatchableObjectByte(12) : this.growingAge;
   }

   public void process(int n, boolean flag) {
      int growingAge = this.getGrowingAge();
      int n2 = growingAge;
      growingAge += n * 20;
      if (growingAge > 0) {
         growingAge = 0;
         if (n2 < 0) {
            this.onGrowingAdult();
         }
      }

      int n3 = growingAge - n2;
      this.setGrowingAge(growingAge);
      if (flag) {
         this.forcedAge += n3;
         if (this.intValue == 0) {
            this.intValue = 40;
         }
      }

      if (this.getGrowingAge() == 0) {
         this.setGrowingAge(this.forcedAge);
      }
   }

   public void addGrowth(int growth) {
      this.process(growth, false);
   }

   public void setGrowingAge(int age) {
      this.dataWatcher.updateObject(12, (byte)MathHelper.clamp_int(age, -1, 1));
      this.growingAge = age;
      this.setScaleForAge(this.isChild());
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setInteger("Age", this.getGrowingAge());
      tagCompound.setInteger("ForcedAge", this.forcedAge);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.setGrowingAge(tagCompund.getInteger("Age"));
      this.forcedAge = tagCompund.getInteger("ForcedAge");
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.worldObj.isRemote) {
         if (this.intValue > 0) {
            if (this.intValue % 4 == 0) {
               this.worldObj
                  .spawnParticle(
                     EnumParticleTypes.VILLAGER_HAPPY,
                     this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                     this.posY + 0.5 + this.rand.nextFloat() * this.height,
                     this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                     0.0,
                     0.0,
                     0.0
                  );
            }

            this.intValue--;
         }

         this.setScaleForAge(this.isChild());
      } else {
         int growingAge = this.getGrowingAge();
         if (growingAge < 0) {
            this.setGrowingAge(++growingAge);
            if (growingAge == 0) {
               this.onGrowingAdult();
            }
         } else if (growingAge > 0) {
            this.setGrowingAge(--growingAge);
         }
      }
   }

   public void onGrowingAdult() {
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
      boolean flag = this.ageWidth > 0.0F;
      this.ageWidth = width;
      this.ageHeight = height;
      if (!flag) {
         this.setScale(1.0F);
      }
   }

   public void setScale(float scale) {
      super.setSize(this.ageWidth * scale, this.ageHeight * scale);
   }
}
