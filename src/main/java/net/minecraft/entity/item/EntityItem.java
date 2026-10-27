package net.minecraft.entity.item;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityItem extends Entity {
   public static Logger logger = LogManager.getLogger();
   public int age;
   public int delayBeforeCanPickup;
   public int health = 5;
   public String thrower;
   public String owner;
   public float hoverStart = (float)(Math.random() * Math.PI * 2.0);

   public EntityItem(World worldIn, double x, double y, double z) {
      super(worldIn);
      this.setSize(0.25F, 0.25F);
      this.setPosition(x, y, z);
      this.rotationYaw = (float)(Math.random() * 360.0);
      this.motionX = (float)(Math.random() * 0.2F - 0.1F);
      this.motionY = 0.2F;
      this.motionZ = (float)(Math.random() * 0.2F - 0.1F);
   }

   public EntityItem(World worldIn, double x, double y, double z, ItemStack stack) {
      this(worldIn, x, y, z);
      this.setEntityItemStack(stack);
   }

   @Override
   public boolean canTriggerWalking() {
      return false;
   }

   public EntityItem(World worldIn) {
      super(worldIn);
      this.setSize(0.25F, 0.25F);
      this.setEntityItemStack(new ItemStack(Blocks.air, 0));
   }

   @Override
   public void entityInit() {
      this.getDataWatcher().addObjectByDataType(10, 5);
   }

   @Override
   public void onUpdate() {
      if (this.getEntityItem() == null) {
         this.setDead();
      } else {
         super.onUpdate();
         if (this.delayBeforeCanPickup > 0 && this.delayBeforeCanPickup != 32767) {
            this.delayBeforeCanPickup--;
         }

         this.prevPosX = this.posX;
         this.prevPosY = this.posY;
         this.prevPosZ = this.posZ;
         this.motionY -= 0.04F;
         this.noClip = this.pushOutOfBlocks(this.posX, (this.getEntityBoundingBox().minY + this.getEntityBoundingBox().maxY) / 2.0, this.posZ);
         this.moveEntity(this.motionX, this.motionY, this.motionZ);
         boolean flag = (int)this.prevPosX != (int)this.posX || (int)this.prevPosY != (int)this.posY || (int)this.prevPosZ != (int)this.posZ;
         if (flag || this.ticksExisted % 25 == 0) {
            if (this.worldObj.getBlockState(new BlockPos(this)).getBlock().getMaterial() == Material.lava) {
               this.motionY = 0.2F;
               this.motionX = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
               this.motionZ = (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F;
               this.playSound("random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
            }

            if (!this.worldObj.isRemote) {
               this.searchForOtherItemsNearby();
            }
         }

         float f = 0.98F;
         if (this.onGround) {
            f = this.worldObj
                  .getBlockState(
                     new BlockPos(
                        MathHelper.floor_double(this.posX), MathHelper.floor_double(this.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.posZ)
                     )
                  )
                  .getBlock()
                  .slipperiness
               * 0.98F;
         }

         this.motionX *= f;
         this.motionY *= 0.98F;
         this.motionZ *= f;
         if (this.onGround) {
            this.motionY *= -0.5;
         }

         if (this.age != -32768) {
            this.age++;
         }

         this.handleWaterMovement();
         if (!this.worldObj.isRemote && this.age >= 6000) {
            this.setDead();
         }
      }
   }

   public void searchForOtherItemsNearby() {
      for (EntityItem entityItem : this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().expand(0.5, 0.0, 0.5))) {
         this.combineItems(entityItem);
      }
   }

   public boolean combineItems(EntityItem other) {
      if (other == this) {
         return false;
      }

      if (other.isEntityAlive() && this.isEntityAlive()) {
         ItemStack entityItem = this.getEntityItem();
         ItemStack entityItem2 = other.getEntityItem();
         if (this.delayBeforeCanPickup == 32767 || other.delayBeforeCanPickup == 32767) {
            return false;
         }

         if (this.age != -32768 && other.age != -32768) {
            if (entityItem2.getItem() != entityItem.getItem()) {
               return false;
            }

            if (entityItem2.hasTagCompound() ^ entityItem.hasTagCompound()) {
               return false;
            }

            if (entityItem2.hasTagCompound() && !entityItem2.getTagCompound().equals(entityItem.getTagCompound())) {
               return false;
            }

            if (entityItem2.getItem() == null) {
               return false;
            }

            if (entityItem2.getItem().getHasSubtypes() && entityItem2.getMetadata() != entityItem.getMetadata()) {
               return false;
            }

            if (entityItem2.stackSize < entityItem.stackSize) {
               return other.combineItems(this);
            }

            if (entityItem2.stackSize + entityItem.stackSize > entityItem2.getMaxStackSize()) {
               return false;
            }

            entityItem2.stackSize = entityItem2.stackSize + entityItem.stackSize;
            other.delayBeforeCanPickup = Math.max(other.delayBeforeCanPickup, this.delayBeforeCanPickup);
            other.age = Math.min(other.age, this.age);
            other.setEntityItemStack(entityItem2);
            this.setDead();
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void setAgeToCreativeDespawnTime() {
      this.age = 4800;
   }

   @Override
   public boolean handleWaterMovement() {
      if (this.worldObj.handleMaterialAcceleration(this.getEntityBoundingBox(), Material.water, this)) {
         if (!this.inWater && !this.firstUpdate) {
            this.resetHeight();
         }

         this.inWater = true;
      } else {
         this.inWater = false;
      }

      return this.inWater;
   }

   @Override
   public void dealFireDamage(int amount) {
      this.attackEntityFrom(DamageSource.inFire, amount);
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if (this.getEntityItem() != null && this.getEntityItem().getItem() == Items.nether_star && source.isExplosion()) {
         return false;
      }

      this.setBeenAttacked();
      this.health = (int)(this.health - amount);
      if (this.health <= 0) {
         this.setDead();
      }

      return false;
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      tagCompound.setShort("Health", (byte)this.health);
      tagCompound.setShort("Age", (short)this.age);
      tagCompound.setShort("PickupDelay", (short)this.delayBeforeCanPickup);
      if (this.getThrower() != null) {
         tagCompound.setString("Thrower", this.thrower);
      }

      if (this.getOwner() != null) {
         tagCompound.setString("Owner", this.owner);
      }

      if (this.getEntityItem() != null) {
         tagCompound.setTag("Item", this.getEntityItem().writeToNBT(new NBTTagCompound()));
      }
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      this.health = tagCompund.getShort("Health") & 255;
      this.age = tagCompund.getShort("Age");
      if (tagCompund.hasKey("PickupDelay")) {
         this.delayBeforeCanPickup = tagCompund.getShort("PickupDelay");
      }

      if (tagCompund.hasKey("Owner")) {
         this.owner = tagCompund.getString("Owner");
      }

      if (tagCompund.hasKey("Thrower")) {
         this.thrower = tagCompund.getString("Thrower");
      }

      NBTTagCompound compoundTag = tagCompund.getCompoundTag("Item");
      this.setEntityItemStack(ItemStack.loadItemStackFromNBT(compoundTag));
      if (this.getEntityItem() == null) {
         this.setDead();
      }
   }

   @Override
   public void onCollideWithPlayer(EntityPlayer entityIn) {
      if (!this.worldObj.isRemote) {
         ItemStack entityItem = this.getEntityItem();
         int stackSize = entityItem.stackSize;
         if (this.delayBeforeCanPickup == 0
            && (this.owner == null || 6000 - this.age <= 200 || this.owner.equals(entityIn.getName()))
            && entityIn.inventory.addItemStackToInventory(entityItem)) {
            if (entityItem.getItem() == Item.getItemFromBlock(Blocks.log)) {
               entityIn.triggerAchievement(AchievementList.mineWood);
            }

            if (entityItem.getItem() == Item.getItemFromBlock(Blocks.log2)) {
               entityIn.triggerAchievement(AchievementList.mineWood);
            }

            if (entityItem.getItem() == Items.leather) {
               entityIn.triggerAchievement(AchievementList.killCow);
            }

            if (entityItem.getItem() == Items.diamond) {
               entityIn.triggerAchievement(AchievementList.diamonds);
            }

            if (entityItem.getItem() == Items.blaze_rod) {
               entityIn.triggerAchievement(AchievementList.blazeRod);
            }

            if (entityItem.getItem() == Items.diamond && this.getThrower() != null) {
               EntityPlayer playerEntityByName = this.worldObj.getPlayerEntityByName(this.getThrower());
               if (playerEntityByName != null && playerEntityByName != entityIn) {
                  playerEntityByName.triggerAchievement(AchievementList.diamondsToYou);
               }
            }

            if (!this.isSilent()) {
               this.worldObj.playSoundAtEntity(entityIn, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            }

            entityIn.onItemPickup(this, stackSize);
            if (entityItem.stackSize <= 0) {
               this.setDead();
            }
         }
      }
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.getCustomNameTag() : StatCollector.translateToLocal("item." + this.getEntityItem().getUnlocalizedName());
   }

   @Override
   public boolean canAttackWithItem() {
      return false;
   }

   @Override
   public void travelToDimension(int dimensionId) {
      super.travelToDimension(dimensionId);
      if (!this.worldObj.isRemote) {
         this.searchForOtherItemsNearby();
      }
   }

   public ItemStack getEntityItem() {
      ItemStack watchableObjectItemStack = this.getDataWatcher().getWatchableObjectItemStack(10);
      if (watchableObjectItemStack == null) {
         if (this.worldObj != null) {
            logger.error("Item entity " + this.getEntityId() + " has no item?!");
         }

         return new ItemStack(Blocks.stone);
      } else {
         return watchableObjectItemStack;
      }
   }

   public void setEntityItemStack(ItemStack stack) {
      this.getDataWatcher().updateObject(10, stack);
      this.getDataWatcher().setObjectWatched(10);
   }

   public String getOwner() {
      return this.owner;
   }

   public void setOwner(String owner) {
      this.owner = owner;
   }

   public String getThrower() {
      return this.thrower;
   }

   public void setThrower(String thrower) {
      this.thrower = thrower;
   }

   public int getAge() {
      return this.age;
   }

   public void setDefaultPickupDelay() {
      this.delayBeforeCanPickup = 10;
   }

   public void setNoPickupDelay() {
      this.delayBeforeCanPickup = 0;
   }

   public void setInfinitePickupDelay() {
      this.delayBeforeCanPickup = 32767;
   }

   public void setPickupDelay(int ticks) {
      this.delayBeforeCanPickup = ticks;
   }

   public boolean cannotPickup() {
      return this.delayBeforeCanPickup > 0;
   }

   public void setNoDespawn() {
      this.age = -6000;
   }

   public void setDespawnImmediately() {
      this.setInfinitePickupDelay();
      this.age = 5999;
   }
}
