package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Rotations;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityArmorStand extends EntityLivingBase {
   public static Rotations DEFAULT_HEAD_ROTATION = new Rotations(0.0F, 0.0F, 0.0F);
   public static Rotations DEFAULT_BODY_ROTATION = new Rotations(0.0F, 0.0F, 0.0F);
   public static Rotations DEFAULT_LEFTARM_ROTATION = new Rotations(-10.0F, 0.0F, -10.0F);
   public static Rotations DEFAULT_RIGHTARM_ROTATION = new Rotations(-15.0F, 0.0F, 10.0F);
   public static Rotations DEFAULT_LEFTLEG_ROTATION = new Rotations(-1.0F, 0.0F, -1.0F);
   public static Rotations DEFAULT_RIGHTLEG_ROTATION = new Rotations(1.0F, 0.0F, 1.0F);
   public ItemStack[] contents = new ItemStack[5];
   public boolean canInteract;
   public long punchCooldown;
   public int disabledSlots;
   public boolean marker;
   public Rotations headRotation = DEFAULT_HEAD_ROTATION;
   public Rotations bodyRotation = DEFAULT_BODY_ROTATION;
   public Rotations leftArmRotation = DEFAULT_LEFTARM_ROTATION;
   public Rotations rightArmRotation = DEFAULT_RIGHTARM_ROTATION;
   public Rotations leftLegRotation = DEFAULT_LEFTLEG_ROTATION;
   public Rotations rightLegRotation = DEFAULT_RIGHTLEG_ROTATION;

   public EntityArmorStand(World worldIn) {
      super(worldIn);
      this.setSilent(true);
      this.noClip = this.hasNoGravity();
      this.setSize(0.5F, 1.975F);
   }

   public EntityArmorStand(World worldIn, double posX, double posY, double posZ) {
      this(worldIn);
      this.setPosition(posX, posY, posZ);
   }

   @Override
   public boolean isServerWorld() {
      return super.isServerWorld() && !this.hasNoGravity();
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(10, (byte)0);
      this.dataWatcher.addObject(11, DEFAULT_HEAD_ROTATION);
      this.dataWatcher.addObject(12, DEFAULT_BODY_ROTATION);
      this.dataWatcher.addObject(13, DEFAULT_LEFTARM_ROTATION);
      this.dataWatcher.addObject(14, DEFAULT_RIGHTARM_ROTATION);
      this.dataWatcher.addObject(15, DEFAULT_LEFTLEG_ROTATION);
      this.dataWatcher.addObject(16, DEFAULT_RIGHTLEG_ROTATION);
   }

   @Override
   public ItemStack getHeldItem() {
      return this.contents[0];
   }

   @Override
   public ItemStack getEquipmentInSlot(int slotIn) {
      return this.contents[slotIn];
   }

   @Override
   public ItemStack getCurrentArmor(int slotIn) {
      return this.contents[slotIn + 1];
   }

   @Override
   public void setCurrentItemOrArmor(int slotIn, ItemStack stack) {
      this.contents[slotIn] = stack;
   }

   @Override
   public ItemStack[] getInventory() {
      return this.contents;
   }

   @Override
   public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
      int n;
      if (inventorySlot == 99) {
         n = 0;
      } else {
         n = inventorySlot - 100 + 1;
         if (n < 0 || n >= this.contents.length) {
            return false;
         }
      }

      if (itemStackIn == null || EntityLiving.getArmorPosition(itemStackIn) == n || n == 4 && itemStackIn.getItem() instanceof ItemBlock) {
         this.setCurrentItemOrArmor(n, itemStackIn);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      NBTTagList nbttagList = new NBTTagList();

      for (int i = 0; i < this.contents.length; i++) {
         NBTTagCompound nbttagCompound = new NBTTagCompound();
         if (this.contents[i] != null) {
            this.contents[i].writeToNBT(nbttagCompound);
         }

         nbttagList.appendTag(nbttagCompound);
      }

      tagCompound.setTag("Equipment", nbttagList);
      if (this.getAlwaysRenderNameTag() && (this.getCustomNameTag() == null || this.getCustomNameTag().length() == 0)) {
         tagCompound.setBoolean("CustomNameVisible", this.getAlwaysRenderNameTag());
      }

      tagCompound.setBoolean("Invisible", this.isInvisible());
      tagCompound.setBoolean("Small", this.isSmall());
      tagCompound.setBoolean("ShowArms", this.getShowArms());
      tagCompound.setInteger("DisabledSlots", this.disabledSlots);
      tagCompound.setBoolean("NoGravity", this.hasNoGravity());
      tagCompound.setBoolean("NoBasePlate", this.hasNoBasePlate());
      if (this.hasMarker()) {
         tagCompound.setBoolean("Marker", this.hasMarker());
      }

      tagCompound.setTag("Pose", this.readPoseFromNBT());
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      if (tagCompund.hasKey("Equipment", 9)) {
         NBTTagList tagList = tagCompund.getTagList("Equipment", 10);

         for (int i = 0; i < this.contents.length; i++) {
            this.contents[i] = ItemStack.loadItemStackFromNBT(tagList.getCompoundTagAt(i));
         }
      }

      this.setInvisible(tagCompund.getBoolean("Invisible"));
      this.setSmall(tagCompund.getBoolean("Small"));
      this.setShowArms(tagCompund.getBoolean("ShowArms"));
      this.disabledSlots = tagCompund.getInteger("DisabledSlots");
      this.setNoGravity(tagCompund.getBoolean("NoGravity"));
      this.setNoBasePlate(tagCompund.getBoolean("NoBasePlate"));
      this.setMarker(tagCompund.getBoolean("Marker"));
      this.marker = !this.hasMarker();
      this.noClip = this.hasNoGravity();
      NBTTagCompound compoundTag = tagCompund.getCompoundTag("Pose");
      this.writePoseToNBT(compoundTag);
   }

   public void writePoseToNBT(NBTTagCompound tagCompound) {
      NBTTagList tagList = tagCompound.getTagList("Head", 5);
      if (tagList.tagCount() > 0) {
         this.setHeadRotation(new Rotations(tagList));
      } else {
         this.setHeadRotation(DEFAULT_HEAD_ROTATION);
      }

      NBTTagList tagList2 = tagCompound.getTagList("Body", 5);
      if (tagList2.tagCount() > 0) {
         this.setBodyRotation(new Rotations(tagList2));
      } else {
         this.setBodyRotation(DEFAULT_BODY_ROTATION);
      }

      NBTTagList tagList3 = tagCompound.getTagList("LeftArm", 5);
      if (tagList3.tagCount() > 0) {
         this.setLeftArmRotation(new Rotations(tagList3));
      } else {
         this.setLeftArmRotation(DEFAULT_LEFTARM_ROTATION);
      }

      NBTTagList tagList4 = tagCompound.getTagList("RightArm", 5);
      if (tagList4.tagCount() > 0) {
         this.setRightArmRotation(new Rotations(tagList4));
      } else {
         this.setRightArmRotation(DEFAULT_RIGHTARM_ROTATION);
      }

      NBTTagList tagList5 = tagCompound.getTagList("LeftLeg", 5);
      if (tagList5.tagCount() > 0) {
         this.setLeftLegRotation(new Rotations(tagList5));
      } else {
         this.setLeftLegRotation(DEFAULT_LEFTLEG_ROTATION);
      }

      NBTTagList tagList6 = tagCompound.getTagList("RightLeg", 5);
      if (tagList6.tagCount() > 0) {
         this.setRightLegRotation(new Rotations(tagList6));
      } else {
         this.setRightLegRotation(DEFAULT_RIGHTLEG_ROTATION);
      }
   }

   public NBTTagCompound readPoseFromNBT() {
      NBTTagCompound nbttagCompound = new NBTTagCompound();
      if (!DEFAULT_HEAD_ROTATION.equals(this.headRotation)) {
         nbttagCompound.setTag("Head", this.headRotation.writeToNBT());
      }

      if (!DEFAULT_BODY_ROTATION.equals(this.bodyRotation)) {
         nbttagCompound.setTag("Body", this.bodyRotation.writeToNBT());
      }

      if (!DEFAULT_LEFTARM_ROTATION.equals(this.leftArmRotation)) {
         nbttagCompound.setTag("LeftArm", this.leftArmRotation.writeToNBT());
      }

      if (!DEFAULT_RIGHTARM_ROTATION.equals(this.rightArmRotation)) {
         nbttagCompound.setTag("RightArm", this.rightArmRotation.writeToNBT());
      }

      if (!DEFAULT_LEFTLEG_ROTATION.equals(this.leftLegRotation)) {
         nbttagCompound.setTag("LeftLeg", this.leftLegRotation.writeToNBT());
      }

      if (!DEFAULT_RIGHTLEG_ROTATION.equals(this.rightLegRotation)) {
         nbttagCompound.setTag("RightLeg", this.rightLegRotation.writeToNBT());
      }

      return nbttagCompound;
   }

   @Override
   public boolean canBePushed() {
      return false;
   }

   @Override
   public void collideWithEntity(Entity entityIn) {
   }

   @Override
   public void collideWithNearbyEntities() {
      List list = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox());
      if (list != null && !list.isEmpty()) {
         for (int i = 0; i < list.size(); i++) {
            Entity entity = (Entity)list.get(i);
            if (entity instanceof EntityMinecart
               && ((EntityMinecart)entity).getMinecartType() == EntityMinecart.EnumMinecartType.RIDEABLE
               && this.getDistanceSqToEntity(entity) <= 0.2) {
               entity.applyEntityCollision(this);
            }
         }
      }
   }

   @Override
   public boolean interactAt(EntityPlayer player, Vec3 targetVec3) {
      if (this.hasMarker()) {
         return false;
      }

      if (!this.worldObj.isRemote && !player.isSpectator()) {
         byte b = 0;
         ItemStack currentEquippedItem = player.getCurrentEquippedItem();
         boolean flag = currentEquippedItem != null;
         if (flag && currentEquippedItem.getItem() instanceof ItemArmor) {
            ItemArmor item = (ItemArmor)currentEquippedItem.getItem();
            if (item.armorType == 3) {
               b = 1;
            } else if (item.armorType == 2) {
               b = 2;
            } else if (item.armorType == 1) {
               b = 3;
            } else if (item.armorType == 0) {
               b = 4;
            }
         }

         if (flag && (currentEquippedItem.getItem() == Items.skull || currentEquippedItem.getItem() == Item.getItemFromBlock(Blocks.pumpkin))) {
            b = 4;
         }

         double d = 0.1;
         double d2 = 0.9;
         double d3 = 0.4;
         double d4 = 1.6;
         byte b2 = 0;
         boolean isSmall = this.isSmall();
         double d5 = isSmall ? targetVec3.yCoord * 2.0 : targetVec3.yCoord;
         if (d5 >= 0.1 && d5 < 0.1 + (isSmall ? 0.8 : 0.45) && this.contents[1] != null) {
            b2 = 1;
         } else if (d5 >= 0.9 + (isSmall ? 0.3 : 0.0) && d5 < 0.9 + (isSmall ? 1.0 : 0.7) && this.contents[3] != null) {
            b2 = 3;
         } else if (d5 >= 0.4 && d5 < 0.4 + (isSmall ? 1.0 : 0.8) && this.contents[2] != null) {
            b2 = 2;
         } else if (d5 >= 1.6 && this.contents[4] != null) {
            b2 = 4;
         }

         boolean flag2 = this.contents[b2] != null;
         if ((this.disabledSlots & 1 << b2) != 0 || (this.disabledSlots & 1 << b) != 0) {
            b2 = b;
            if ((this.disabledSlots & 1 << b) != 0) {
               if ((this.disabledSlots & 1) != 0) {
                  return true;
               }

               b2 = 0;
            }
         }

         if (flag && b == 0 && !this.getShowArms()) {
            return true;
         }

         if (flag) {
            this.handleEntityPlayer(player, b);
         } else if (flag2) {
            this.handleEntityPlayer(player, b2);
         }

         return true;
      } else {
         return true;
      }
   }

   public void handleEntityPlayer(EntityPlayer entityPlayer, int n) {
      ItemStack itemStack = this.contents[n];
      if ((itemStack == null || (this.disabledSlots & 1 << n + 8) == 0) && (itemStack != null || (this.disabledSlots & 1 << n + 16) == 0)) {
         int currentItem = entityPlayer.inventory.currentItem;
         ItemStack stackInSlot = entityPlayer.inventory.getStackInSlot(currentItem);
         if (entityPlayer.capabilities.isCreativeMode && (itemStack == null || itemStack.getItem() == Item.getItemFromBlock(Blocks.air)) && stackInSlot != null) {
            ItemStack itemStack2 = stackInSlot.copy();
            itemStack2.stackSize = 1;
            this.setCurrentItemOrArmor(n, itemStack2);
         } else if (stackInSlot == null || stackInSlot.stackSize <= 1) {
            this.setCurrentItemOrArmor(n, stackInSlot);
            entityPlayer.inventory.setInventorySlotContents(currentItem, itemStack);
         } else if (itemStack == null) {
            ItemStack itemStack2 = stackInSlot.copy();
            itemStack2.stackSize = 1;
            this.setCurrentItemOrArmor(n, itemStack2);
            stackInSlot.stackSize--;
         }
      }
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.worldObj.isRemote) {
         return false;
      }

      if (DamageSource.outOfWorld.equals(source)) {
         this.setDead();
         return false;
      }

      if (this.isEntityInvulnerable(source) || this.canInteract || this.hasMarker()) {
         return false;
      }

      if (source.isExplosion()) {
         this.dropContents();
         this.setDead();
         return false;
      }

      if (DamageSource.inFire.equals(source)) {
         if (!this.isBurning()) {
            this.setFire(5);
         } else {
            this.damageArmorStand(0.15F);
         }

         return false;
      } else {
         if (DamageSource.onFire.equals(source) && this.getHealth() > 0.5F) {
            this.damageArmorStand(4.0F);
            return false;
         }

         boolean flag = "arrow".equals(source.getDamageType());
         boolean flag2 = "player".equals(source.getDamageType());
         if (!flag2 && !flag) {
            return false;
         }

         if (source.getSourceOfDamage() instanceof EntityArrow) {
            source.getSourceOfDamage().setDead();
         }

         if (source.getEntity() instanceof EntityPlayer && !((EntityPlayer)source.getEntity()).capabilities.allowEdit) {
            return false;
         }

         if (source.isCreativePlayer()) {
            this.playParticles();
            this.setDead();
            return false;
         }

         long totalWorldTime = this.worldObj.getTotalWorldTime();
         if (totalWorldTime - this.punchCooldown > 5L && !flag) {
            this.punchCooldown = totalWorldTime;
         } else {
            this.dropBlock();
            this.playParticles();
            this.setDead();
         }

         return false;
      }
   }

   @Override
   public boolean isInRangeToRenderDist(double distance) {
      double d = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0;
      if (Double.isNaN(d) || d == 0.0) {
         d = 4.0;
      }

      d *= 64.0;
      return distance < d * d;
   }

   public void playParticles() {
      if (this.worldObj instanceof WorldServer) {
         ((WorldServer)this.worldObj)
            .spawnParticle(
               EnumParticleTypes.BLOCK_DUST,
               this.posX,
               this.posY + this.height / 1.5,
               this.posZ,
               10,
               this.width / 4.0F,
               this.height / 4.0F,
               this.width / 4.0F,
               0.05,
               Block.getStateId(Blocks.planks.getDefaultState())
            );
      }
   }

   public void damageArmorStand(float f) {
      float health = this.getHealth();
      health -= f;
      if (health <= 0.5F) {
         this.dropContents();
         this.setDead();
      } else {
         this.setHealth(health);
      }
   }

   public void dropBlock() {
      Block.spawnAsEntity(this.worldObj, new BlockPos(this), new ItemStack(Items.armor_stand));
      this.dropContents();
   }

   public void dropContents() {
      for (int i = 0; i < this.contents.length; i++) {
         if (this.contents[i] != null && this.contents[i].stackSize > 0) {
            if (this.contents[i] != null) {
               Block.spawnAsEntity(this.worldObj, new BlockPos(this).up(), this.contents[i]);
            }

            this.contents[i] = null;
         }
      }
   }

   @Override
   public float updateDistance(float f, float f2) {
      this.prevRenderYawOffset = this.prevRotationYaw;
      this.renderYawOffset = this.rotationYaw;
      return 0.0F;
   }

   @Override
   public float getEyeHeight() {
      return this.isChild() ? this.height * 0.5F : this.height * 0.9F;
   }

   @Override
   public void moveEntityWithHeading(float strafe, float forward) {
      if (!this.hasNoGravity()) {
         super.moveEntityWithHeading(strafe, forward);
      }
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      Rotations watchableObjectRotations = this.dataWatcher.getWatchableObjectRotations(11);
      if (!this.headRotation.equals(watchableObjectRotations)) {
         this.setHeadRotation(watchableObjectRotations);
      }

      Rotations watchableObjectRotations2 = this.dataWatcher.getWatchableObjectRotations(12);
      if (!this.bodyRotation.equals(watchableObjectRotations2)) {
         this.setBodyRotation(watchableObjectRotations2);
      }

      Rotations watchableObjectRotations3 = this.dataWatcher.getWatchableObjectRotations(13);
      if (!this.leftArmRotation.equals(watchableObjectRotations3)) {
         this.setLeftArmRotation(watchableObjectRotations3);
      }

      Rotations watchableObjectRotations4 = this.dataWatcher.getWatchableObjectRotations(14);
      if (!this.rightArmRotation.equals(watchableObjectRotations4)) {
         this.setRightArmRotation(watchableObjectRotations4);
      }

      Rotations watchableObjectRotations5 = this.dataWatcher.getWatchableObjectRotations(15);
      if (!this.leftLegRotation.equals(watchableObjectRotations5)) {
         this.setLeftLegRotation(watchableObjectRotations5);
      }

      Rotations watchableObjectRotations6 = this.dataWatcher.getWatchableObjectRotations(16);
      if (!this.rightLegRotation.equals(watchableObjectRotations6)) {
         this.setRightLegRotation(watchableObjectRotations6);
      }

      boolean hasMarker = this.hasMarker();
      if (!this.marker && hasMarker) {
         this.process(false);
      } else {
         if (!this.marker || hasMarker) {
            return;
         }

         this.process(true);
      }

      this.marker = hasMarker;
   }

   public void process(boolean flag) {
      double posX = this.posX;
      double posY = this.posY;
      double posZ = this.posZ;
      if (flag) {
         this.setSize(0.5F, 1.975F);
      } else {
         this.setSize(0.0F, 0.0F);
      }

      this.setPosition(posX, posY, posZ);
   }

   @Override
   public void updatePotionMetadata() {
      this.setInvisible(this.canInteract);
   }

   @Override
   public void setInvisible(boolean invisible) {
      this.canInteract = invisible;
      super.setInvisible(invisible);
   }

   @Override
   public boolean isChild() {
      return this.isSmall();
   }

   @Override
   public void onKillCommand() {
      this.setDead();
   }

   @Override
   public boolean isImmuneToExplosions() {
      return this.isInvisible();
   }

   public void setSmall(boolean flag) {
      byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(10);
      if (flag) {
         watchableObjectByte = (byte)(watchableObjectByte | 1);
      } else {
         watchableObjectByte = (byte)(watchableObjectByte & -2);
      }

      this.dataWatcher.updateObject(10, watchableObjectByte);
   }

   public boolean isSmall() {
      return (this.dataWatcher.getWatchableObjectByte(10) & 1) != 0;
   }

   public void setNoGravity(boolean flag) {
      byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(10);
      if (flag) {
         watchableObjectByte = (byte)(watchableObjectByte | 2);
      } else {
         watchableObjectByte = (byte)(watchableObjectByte & -3);
      }

      this.dataWatcher.updateObject(10, watchableObjectByte);
   }

   public boolean hasNoGravity() {
      return (this.dataWatcher.getWatchableObjectByte(10) & 2) != 0;
   }

   public void setShowArms(boolean flag) {
      byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(10);
      if (flag) {
         watchableObjectByte = (byte)(watchableObjectByte | 4);
      } else {
         watchableObjectByte = (byte)(watchableObjectByte & -5);
      }

      this.dataWatcher.updateObject(10, watchableObjectByte);
   }

   public boolean getShowArms() {
      return (this.dataWatcher.getWatchableObjectByte(10) & 4) != 0;
   }

   public void setNoBasePlate(boolean flag) {
      byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(10);
      if (flag) {
         watchableObjectByte = (byte)(watchableObjectByte | 8);
      } else {
         watchableObjectByte = (byte)(watchableObjectByte & -9);
      }

      this.dataWatcher.updateObject(10, watchableObjectByte);
   }

   public boolean hasNoBasePlate() {
      return (this.dataWatcher.getWatchableObjectByte(10) & 8) != 0;
   }

   public void setMarker(boolean flag) {
      byte watchableObjectByte = this.dataWatcher.getWatchableObjectByte(10);
      if (flag) {
         watchableObjectByte = (byte)(watchableObjectByte | 16);
      } else {
         watchableObjectByte = (byte)(watchableObjectByte & -17);
      }

      this.dataWatcher.updateObject(10, watchableObjectByte);
   }

   public boolean hasMarker() {
      return (this.dataWatcher.getWatchableObjectByte(10) & 16) != 0;
   }

   public void setHeadRotation(Rotations rotations) {
      this.headRotation = rotations;
      this.dataWatcher.updateObject(11, rotations);
   }

   public void setBodyRotation(Rotations rotations) {
      this.bodyRotation = rotations;
      this.dataWatcher.updateObject(12, rotations);
   }

   public void setLeftArmRotation(Rotations rotations) {
      this.leftArmRotation = rotations;
      this.dataWatcher.updateObject(13, rotations);
   }

   public void setRightArmRotation(Rotations rotations) {
      this.rightArmRotation = rotations;
      this.dataWatcher.updateObject(14, rotations);
   }

   public void setLeftLegRotation(Rotations rotations) {
      this.leftLegRotation = rotations;
      this.dataWatcher.updateObject(15, rotations);
   }

   public void setRightLegRotation(Rotations rotations) {
      this.rightLegRotation = rotations;
      this.dataWatcher.updateObject(16, rotations);
   }

   public Rotations getHeadRotation() {
      return this.headRotation;
   }

   public Rotations getBodyRotation() {
      return this.bodyRotation;
   }

   public Rotations getLeftArmRotation() {
      return this.leftArmRotation;
   }

   public Rotations getRightArmRotation() {
      return this.rightArmRotation;
   }

   public Rotations getLeftLegRotation() {
      return this.leftLegRotation;
   }

   public Rotations getRightLegRotation() {
      return this.rightLegRotation;
   }

   @Override
   public boolean canBeCollidedWith() {
      return super.canBeCollidedWith() && !this.hasMarker();
   }
}
