package net.minecraft.entity.monster;

import com.google.common.base.Predicate;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityEnderman extends EntityMob {
   public static UUID attackingSpeedBoostModifierUUID = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
   public static AttributeModifier attackingSpeedBoostModifier = new AttributeModifier(attackingSpeedBoostModifierUUID, "Attacking speed boost", 0.15F, 0)
      .setSaved(false);
   public static Set<Block> carriableBlocks = Sets.newIdentityHashSet();
   public boolean isAggressive;

   public EntityEnderman(World worldIn) {
      super(worldIn);
      this.setSize(0.6F, 2.9F);
      this.stepHeight = 1.0F;
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIAttackOnCollide(this, 1.0, false));
      this.tasks.addTask(7, new EntityAIWander(this, 1.0));
      this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.tasks.addTask(10, new EntityEnderman.AIPlaceBlock(this));
      this.tasks.addTask(11, new EntityEnderman.AITakeBlock(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
      this.targetTasks.addTask(2, new EntityEnderman.AIFindPlayer(this));
      this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityEndermite.class, 10, true, false, new Predicate<EntityEndermite>() {
         public boolean apply(EntityEndermite entityEndermite) {
            return entityEndermite.isSpawnedByPlayer();
         }
      }));
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(40.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3F);
      this.getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(7.0);
      this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(64.0);
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, new Short((short)0));
      this.dataWatcher.addObject(17, new Byte((byte)0));
      this.dataWatcher.addObject(18, new Byte((byte)0));
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      IBlockState heldBlockState = this.getHeldBlockState();
      tagCompound.setShort("carried", (short)Block.getIdFromBlock(heldBlockState.getBlock()));
      tagCompound.setShort("carriedData", (short)heldBlockState.getBlock().getMetaFromState(heldBlockState));
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      IBlockState blockState;
      if (tagCompund.hasKey("carried", 8)) {
         blockState = Block.getBlockFromName(tagCompund.getString("carried")).getStateFromMeta(tagCompund.getShort("carriedData") & 65535);
      } else {
         blockState = Block.getBlockById(tagCompund.getShort("carried")).getStateFromMeta(tagCompund.getShort("carriedData") & 65535);
      }

      this.setHeldBlockState(blockState);
   }

   public boolean shouldAttackPlayer(EntityPlayer player) {
      ItemStack itemStack = player.inventory.armorInventory[3];
      if (itemStack != null && itemStack.getItem() == Item.getItemFromBlock(Blocks.pumpkin)) {
         return false;
      }

      Vec3 normalize = player.getLook(1.0F).normalize();
      Vec3 vec3 = new Vec3(
         this.posX - player.posX, this.getEntityBoundingBox().minY + this.height / 2.0F - (player.posY + player.getEyeHeight()), this.posZ - player.posZ
      );
      double lengthVector = vec3.lengthVector();
      vec3 = vec3.normalize();
      double dotProduct = normalize.dotProduct(vec3);
      return dotProduct > 1.0 - 0.025 / lengthVector ? player.canEntityBeSeen(this) : false;
   }

   @Override
   public float getEyeHeight() {
      return 2.55F;
   }

   @Override
   public void onLivingUpdate() {
      if (this.worldObj.isRemote) {
         for (int i = 0; i < 2; i++) {
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.PORTAL,
                  this.posX + (this.rand.nextDouble() - 0.5) * this.width,
                  this.posY + this.rand.nextDouble() * this.height - 0.25,
                  this.posZ + (this.rand.nextDouble() - 0.5) * this.width,
                  (this.rand.nextDouble() - 0.5) * 2.0,
                  -this.rand.nextDouble(),
                  (this.rand.nextDouble() - 0.5) * 2.0
               );
         }
      }

      this.isJumping = false;
      super.onLivingUpdate();
   }

   @Override
   public void updateAITasks() {
      if (this.isWet()) {
         this.attackEntityFrom(DamageSource.drown, 1.0F);
      }

      if (this.isScreaming() && !this.isAggressive && this.rand.nextInt(100) == 0) {
         this.setScreaming(false);
      }

      if (this.worldObj.isDaytime()) {
         float brightness = this.getBrightness(1.0F);
         if (brightness > 0.5F && this.worldObj.canSeeSky(new BlockPos(this)) && this.rand.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F) {
            this.setAttackTarget((EntityLivingBase)null);
            this.setScreaming(false);
            this.isAggressive = false;
            this.teleportRandomly();
         }
      }

      super.updateAITasks();
   }

   public boolean teleportRandomly() {
      double d = this.posX + (this.rand.nextDouble() - 0.5) * 64.0;
      double d2 = this.posY + (this.rand.nextInt(64) - 32);
      double d3 = this.posZ + (this.rand.nextDouble() - 0.5) * 64.0;
      return this.teleportTo(d, d2, d3);
   }

   public boolean teleportToEntity(Entity entity) {
      Vec3 vec3 = new Vec3(
         this.posX - entity.posX, this.getEntityBoundingBox().minY + this.height / 2.0F - entity.posY + entity.getEyeHeight(), this.posZ - entity.posZ
      );
      vec3 = vec3.normalize();
      double d = 16.0;
      double d2 = this.posX + (this.rand.nextDouble() - 0.5) * 8.0 - vec3.xCoord * d;
      double d3 = this.posY + (this.rand.nextInt(16) - 8) - vec3.yCoord * d;
      double d4 = this.posZ + (this.rand.nextDouble() - 0.5) * 8.0 - vec3.zCoord * d;
      return this.teleportTo(d2, d3, d4);
   }

   public boolean teleportTo(double x, double y, double z) {
      double posX = this.posX;
      double posY = this.posY;
      double posZ = this.posZ;
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      boolean flag = false;
      BlockPos blockPos = new BlockPos(this.posX, this.posY, this.posZ);
      if (this.worldObj.isBlockLoaded(blockPos)) {
         boolean flag2 = false;

         while (!flag2 && blockPos.getY() > 0) {
            BlockPos down = blockPos.down();
            Block block = this.worldObj.getBlockState(down).getBlock();
            if (block.getMaterial().blocksMovement()) {
               flag2 = true;
            } else {
               this.posY--;
               blockPos = down;
            }
         }

         if (flag2) {
            super.setPositionAndUpdate(this.posX, this.posY, this.posZ);
            if (this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty() && !this.worldObj.isAnyLiquid(this.getEntityBoundingBox())
               )
             {
               flag = true;
            }
         }
      }

      if (!flag) {
         this.setPosition(posX, posY, posZ);
         return false;
      }

      short s = 128;

      for (int i = 0; i < s; i++) {
         double d = i / (s - 1.0);
         float f = (this.rand.nextFloat() - 0.5F) * 0.2F;
         float f2 = (this.rand.nextFloat() - 0.5F) * 0.2F;
         float f3 = (this.rand.nextFloat() - 0.5F) * 0.2F;
         double d2 = posX + (this.posX - posX) * d + (this.rand.nextDouble() - 0.5) * this.width * 2.0;
         double d3 = posY + (this.posY - posY) * d + this.rand.nextDouble() * this.height;
         double d4 = posZ + (this.posZ - posZ) * d + (this.rand.nextDouble() - 0.5) * this.width * 2.0;
         this.worldObj.spawnParticle(EnumParticleTypes.PORTAL, d2, d3, d4, f, f2, f3);
      }

      this.worldObj.playSoundEffect(posX, posY, posZ, "mob.endermen.portal", 1.0F, 1.0F);
      this.playSound("mob.endermen.portal", 1.0F, 1.0F);
      return true;
   }

   @Override
   public String getLivingSound() {
      return this.isScreaming() ? "mob.endermen.scream" : "mob.endermen.idle";
   }

   @Override
   public String getHurtSound() {
      return "mob.endermen.hit";
   }

   @Override
   public String getDeathSound() {
      return "mob.endermen.death";
   }

   @Override
   public Item getDropItem() {
      return Items.ender_pearl;
   }

   @Override
   public void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      Item dropItem = this.getDropItem();
      if (dropItem != null) {
         int nextInt = this.rand.nextInt(2 + lootingModifier);

         for (int i = 0; i < nextInt; i++) {
            this.dropItem(dropItem, 1);
         }
      }
   }

   public void setHeldBlockState(IBlockState state) {
      this.dataWatcher.updateObject(16, (short)(Block.getStateId(state) & 65535));
   }

   public IBlockState getHeldBlockState() {
      return Block.getStateById(this.dataWatcher.getWatchableObjectShort(16) & 65535);
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if (source.getEntity() == null || !(source.getEntity() instanceof EntityEndermite)) {
         if (!this.worldObj.isRemote) {
            this.setScreaming(true);
         }

         if (source instanceof EntityDamageSource && source.getEntity() instanceof EntityPlayer) {
            if (source.getEntity() instanceof EntityPlayerMP && ((EntityPlayerMP)source.getEntity()).theItemInWorldManager.isCreative()) {
               this.setScreaming(false);
            } else {
               this.isAggressive = true;
            }
         }

         if (source instanceof EntityDamageSourceIndirect) {
            this.isAggressive = false;

            for (int i = 0; i < 64; i++) {
               if (this.teleportRandomly()) {
                  return true;
               }
            }

            return false;
         }
      }

      boolean attackEntityFrom = super.attackEntityFrom(source, amount);
      if (source.isUnblockable() && this.rand.nextInt(10) != 0) {
         this.teleportRandomly();
      }

      return attackEntityFrom;
   }

   public boolean isScreaming() {
      return this.dataWatcher.getWatchableObjectByte(18) > 0;
   }

   public void setScreaming(boolean screaming) {
      this.dataWatcher.updateObject(18, (byte)(screaming ? 1 : 0));
   }

   static {
      carriableBlocks.add(Blocks.grass);
      carriableBlocks.add(Blocks.dirt);
      carriableBlocks.add(Blocks.sand);
      carriableBlocks.add(Blocks.gravel);
      carriableBlocks.add(Blocks.yellow_flower);
      carriableBlocks.add(Blocks.red_flower);
      carriableBlocks.add(Blocks.brown_mushroom);
      carriableBlocks.add(Blocks.red_mushroom);
      carriableBlocks.add(Blocks.tnt);
      carriableBlocks.add(Blocks.cactus);
      carriableBlocks.add(Blocks.clay);
      carriableBlocks.add(Blocks.pumpkin);
      carriableBlocks.add(Blocks.melon_block);
      carriableBlocks.add(Blocks.mycelium);
   }

   static class AIFindPlayer extends EntityAINearestAttackableTarget {
      public EntityPlayer player;
      public int lookAtPlayerWarmup;
      public int ticksSinceUnseenTeleport;
      public EntityEnderman enderman;

      public AIFindPlayer(EntityEnderman entityEnderman) {
         super(entityEnderman, EntityPlayer.class, true);
         this.enderman = entityEnderman;
      }

      @Override
      public boolean shouldExecute() {
         double targetDistance = this.getTargetDistance();
         List entitiesWithinAABB = this.taskOwner
            .worldObj
            .getEntitiesWithinAABB(EntityPlayer.class, this.taskOwner.getEntityBoundingBox().expand(targetDistance, 4.0, targetDistance), this.targetEntitySelector);
         Collections.sort(entitiesWithinAABB, this.theNearestAttackableTargetSorter);
         if (entitiesWithinAABB.isEmpty()) {
            return false;
         }

         this.player = (EntityPlayer)entitiesWithinAABB.get(0);
         return true;
      }

      @Override
      public void startExecuting() {
         this.lookAtPlayerWarmup = 5;
         this.ticksSinceUnseenTeleport = 0;
      }

      @Override
      public void resetTask() {
         this.player = null;
         this.enderman.setScreaming(false);
         IAttributeInstance entityAttribute = this.enderman.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
         entityAttribute.removeModifier(EntityEnderman.attackingSpeedBoostModifier);
         super.resetTask();
      }

      @Override
      public boolean continueExecuting() {
         if (this.player != null) {
            if (!this.enderman.shouldAttackPlayer(this.player)) {
               return false;
            }

            this.enderman.isAggressive = true;
            this.enderman.faceEntity(this.player, 10.0F, 10.0F);
            return true;
         } else {
            return super.continueExecuting();
         }
      }

      @Override
      public void updateTask() {
         if (this.player != null) {
            if (--this.lookAtPlayerWarmup <= 0) {
               this.targetEntity = this.player;
               this.player = null;
               super.startExecuting();
               this.enderman.playSound("mob.endermen.stare", 1.0F, 1.0F);
               this.enderman.setScreaming(true);
               IAttributeInstance entityAttribute = this.enderman.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
               entityAttribute.applyModifier(EntityEnderman.attackingSpeedBoostModifier);
            }
         } else {
            if (this.targetEntity != null) {
               if (this.targetEntity instanceof EntityPlayer && this.enderman.shouldAttackPlayer((EntityPlayer)this.targetEntity)) {
                  if (this.targetEntity.getDistanceSqToEntity(this.enderman) < 16.0) {
                     this.enderman.teleportRandomly();
                  }

                  this.ticksSinceUnseenTeleport = 0;
               } else if (this.targetEntity.getDistanceSqToEntity(this.enderman) > 256.0
                  && this.ticksSinceUnseenTeleport++ >= 30
                  && this.enderman.teleportToEntity(this.targetEntity)) {
                  this.ticksSinceUnseenTeleport = 0;
               }
            }

            super.updateTask();
         }
      }
   }

   static class AIPlaceBlock extends EntityAIBase {
      public EntityEnderman enderman;

      public AIPlaceBlock(EntityEnderman entityEnderman) {
         this.enderman = entityEnderman;
      }

      @Override
      public boolean shouldExecute() {
         return !this.enderman.worldObj.getGameRules().getBoolean("mobGriefing")
            ? false
            : (this.enderman.getHeldBlockState().getBlock().getMaterial() == Material.air ? false : this.enderman.getRNG().nextInt(2000) == 0);
      }

      @Override
      public void updateTask() {
         Random rNG = this.enderman.getRNG();
         World worldObj = this.enderman.worldObj;
         int n = MathHelper.floor_double(this.enderman.posX - 1.0 + rNG.nextDouble() * 2.0);
         int n2 = MathHelper.floor_double(this.enderman.posY + rNG.nextDouble() * 2.0);
         int n3 = MathHelper.floor_double(this.enderman.posZ - 1.0 + rNG.nextDouble() * 2.0);
         BlockPos blockPos = new BlockPos(n, n2, n3);
         Block block = worldObj.getBlockState(blockPos).getBlock();
         Block block2 = worldObj.getBlockState(blockPos.down()).getBlock();
         if (this.canPlaceOn(worldObj, blockPos, this.enderman.getHeldBlockState().getBlock(), block, block2)) {
            worldObj.setBlockState(blockPos, this.enderman.getHeldBlockState(), 3);
            this.enderman.setHeldBlockState(Blocks.air.getDefaultState());
         }
      }

      public boolean canPlaceOn(World worldIn, BlockPos blockPos, Block block, Block block2, Block block3) {
         return !block.canPlaceBlockAt(worldIn, blockPos)
            ? false
            : (block2.getMaterial() != Material.air ? false : (block3.getMaterial() == Material.air ? false : block3.isFullCube()));
      }
   }

   static class AITakeBlock extends EntityAIBase {
      public EntityEnderman enderman;

      public AITakeBlock(EntityEnderman entityEnderman) {
         this.enderman = entityEnderman;
      }

      @Override
      public boolean shouldExecute() {
         return !this.enderman.worldObj.getGameRules().getBoolean("mobGriefing")
            ? false
            : (this.enderman.getHeldBlockState().getBlock().getMaterial() != Material.air ? false : this.enderman.getRNG().nextInt(20) == 0);
      }

      @Override
      public void updateTask() {
         Random rNG = this.enderman.getRNG();
         World worldObj = this.enderman.worldObj;
         int n = MathHelper.floor_double(this.enderman.posX - 2.0 + rNG.nextDouble() * 4.0);
         int n2 = MathHelper.floor_double(this.enderman.posY + rNG.nextDouble() * 3.0);
         int n3 = MathHelper.floor_double(this.enderman.posZ - 2.0 + rNG.nextDouble() * 4.0);
         BlockPos blockPos = new BlockPos(n, n2, n3);
         IBlockState blockState = worldObj.getBlockState(blockPos);
         Block block = blockState.getBlock();
         if (EntityEnderman.carriableBlocks.contains(block)) {
            this.enderman.setHeldBlockState(blockState);
            worldObj.setBlockState(blockPos, Blocks.air.getDefaultState());
         }
      }
   }
}
