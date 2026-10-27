package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCarrot;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityJumpHelper;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityRabbit extends EntityAnimal {
   public int jumpTicks = 0;
   public int jumpDuration = 0;
   public boolean flag = false;
   public boolean lastOnGround = false;
   public int currentMoveTypeDuration = 0;
   public EntityRabbit.EnumMoveType moveType = EntityRabbit.EnumMoveType.HOP;
   public int carrotTicks = 0;
   public EntityPlayer entityPlayer = null;
   public EntityRabbit.AIAvoidEntity<EntityWolf> aiAvoidWolves;

   public EntityRabbit(World worldIn) {
      super(worldIn);
      this.setSize(0.6F, 0.7F);
      this.jumpHelper = new EntityRabbit.RabbitJumpHelper(this);
      this.moveHelper = new EntityRabbit.RabbitMoveHelper(this);
      ((PathNavigateGround)this.getNavigator()).setAvoidsWater(true);
      this.navigator.setHeightRequirement(2.5F);
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityRabbit.AIPanic(this, 1.33));
      this.tasks.addTask(2, new EntityAITempt(this, 1.0, Items.carrot, false));
      this.tasks.addTask(2, new EntityAITempt(this, 1.0, Items.golden_carrot, false));
      this.tasks.addTask(2, new EntityAITempt(this, 1.0, Item.getItemFromBlock(Blocks.yellow_flower), false));
      this.tasks.addTask(3, new EntityAIMate(this, 0.8));
      this.tasks.addTask(5, new EntityRabbit.AIRaidFarm(this));
      this.tasks.addTask(5, new EntityAIWander(this, 0.6));
      this.tasks.addTask(11, new EntityAIWatchClosest(this, EntityPlayer.class, 10.0F));
      this.aiAvoidWolves = new EntityRabbit.AIAvoidEntity<>(this, EntityWolf.class, 16.0F, 1.33, 1.33);
      this.tasks.addTask(4, this.aiAvoidWolves);
      this.setMovementSpeed(0.0);
   }

   @Override
   public float getJumpUpwardsMotion() {
      return this.moveHelper.isUpdating() && this.moveHelper.getY() > this.posY + 0.5 ? 0.5F : this.moveType.getFloatValue();
   }

   public void setMoveType(EntityRabbit.EnumMoveType type) {
      this.moveType = type;
   }

   public float getJumpProgress(float f) {
      return this.jumpDuration == 0 ? 0.0F : (this.jumpTicks + f) / this.jumpDuration;
   }

   public void setMovementSpeed(double newSpeed) {
      this.getNavigator().setSpeed(newSpeed);
      this.moveHelper.setMoveTo(this.moveHelper.getX(), this.moveHelper.getY(), this.moveHelper.getZ(), newSpeed);
   }

   public void setJumping(boolean jump, EntityRabbit.EnumMoveType moveTypeIn) {
      super.setJumping(jump);
      if (!jump) {
         if (this.moveType == EntityRabbit.EnumMoveType.ATTACK) {
            this.moveType = EntityRabbit.EnumMoveType.HOP;
         }
      } else {
         this.setMovementSpeed(1.5 * moveTypeIn.getSpeed());
         this.playSound(this.getJumpingSound(), this.getSoundVolume(), ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F) * 0.8F);
      }

      this.flag = jump;
   }

   public void doMovementAction(EntityRabbit.EnumMoveType movetype) {
      this.setJumping(true, movetype);
      this.jumpDuration = movetype.getJumpDuration();
      this.jumpTicks = 0;
   }

   public boolean isFlag() {
      return this.flag;
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(18, (byte)0);
   }

   @Override
   public void updateAITasks() {
      if (this.moveHelper.getSpeed() > 0.8) {
         this.setMoveType(EntityRabbit.EnumMoveType.SPRINT);
      } else if (this.moveType != EntityRabbit.EnumMoveType.ATTACK) {
         this.setMoveType(EntityRabbit.EnumMoveType.HOP);
      }

      if (this.currentMoveTypeDuration > 0) {
         this.currentMoveTypeDuration--;
      }

      if (this.carrotTicks > 0) {
         this.carrotTicks = this.carrotTicks - this.rand.nextInt(3);
         if (this.carrotTicks < 0) {
            this.carrotTicks = 0;
         }
      }

      if (this.onGround) {
         if (!this.lastOnGround) {
            this.setJumping(false, EntityRabbit.EnumMoveType.NONE);
            this.run3();
         }

         if (this.getRabbitType() == 99 && this.currentMoveTypeDuration == 0) {
            EntityLivingBase attackTarget = this.getAttackTarget();
            if (attackTarget != null && this.getDistanceSqToEntity(attackTarget) < 16.0) {
               this.calculateRotationYaw(attackTarget.posX, attackTarget.posZ);
               this.moveHelper.setMoveTo(attackTarget.posX, attackTarget.posY, attackTarget.posZ, this.moveHelper.getSpeed());
               this.doMovementAction(EntityRabbit.EnumMoveType.ATTACK);
               this.lastOnGround = true;
            }
         }

         EntityRabbit.RabbitJumpHelper jumpHelper = (EntityRabbit.RabbitJumpHelper)this.jumpHelper;
         if (!jumpHelper.getIsJumping()) {
            if (this.moveHelper.isUpdating() && this.currentMoveTypeDuration == 0) {
               PathEntity path = this.navigator.getPath();
               Vec3 vec3 = new Vec3(this.moveHelper.getX(), this.moveHelper.getY(), this.moveHelper.getZ());
               if (path != null && path.getCurrentPathIndex() < path.getCurrentPathLength()) {
                  vec3 = path.getPosition(this);
               }

               this.calculateRotationYaw(vec3.xCoord, vec3.zCoord);
               this.doMovementAction(this.moveType);
            }
         } else if (!jumpHelper.isFlag()) {
            this.run();
         }
      }

      this.lastOnGround = this.onGround;
   }

   @Override
   public void spawnRunningParticles() {
   }

   public void calculateRotationYaw(double x, double z) {
      this.rotationYaw = (float)(MathHelper.atan2(z - this.posZ, x - this.posX) * 180.0 / Math.PI) - 90.0F;
   }

   public void run() {
      ((EntityRabbit.RabbitJumpHelper)this.jumpHelper).setFlag(true);
   }

   public void run2() {
      ((EntityRabbit.RabbitJumpHelper)this.jumpHelper).setFlag(false);
   }

   public void updateMoveTypeDuration() {
      this.currentMoveTypeDuration = this.getMoveTypeDuration();
   }

   public void run3() {
      this.updateMoveTypeDuration();
      this.run2();
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.jumpTicks != this.jumpDuration) {
         if (this.jumpTicks == 0 && !this.worldObj.isRemote) {
            this.worldObj.setEntityState(this, (byte)1);
         }

         this.jumpTicks++;
      } else if (this.jumpDuration != 0) {
         this.jumpTicks = 0;
         this.jumpDuration = 0;
      }
   }

   @Override
   public void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(10.0);
      this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.3F);
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setInteger("RabbitType", this.getRabbitType());
      tagCompound.setInteger("MoreCarrotTicks", this.carrotTicks);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.setRabbitType(tagCompund.getInteger("RabbitType"));
      this.carrotTicks = tagCompund.getInteger("MoreCarrotTicks");
   }

   public String getJumpingSound() {
      return "mob.rabbit.hop";
   }

   @Override
   public String getLivingSound() {
      return "mob.rabbit.idle";
   }

   @Override
   public String getHurtSound() {
      return "mob.rabbit.hurt";
   }

   @Override
   public String getDeathSound() {
      return "mob.rabbit.death";
   }

   @Override
   public boolean attackEntityAsMob(Entity entityIn) {
      if (this.getRabbitType() == 99) {
         this.playSound("mob.attack", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 8.0F);
      } else {
         return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), 3.0F);
      }
   }

   @Override
   public int getTotalArmorValue() {
      return this.getRabbitType() == 99 ? 8 : super.getTotalArmorValue();
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      return this.isEntityInvulnerable(source) ? false : super.attackEntityFrom(source, amount);
   }

   @Override
   public void addRandomDrop() {
      this.entityDropItem(new ItemStack(Items.rabbit_foot, 1), 0.0F);
   }

   @Override
   public void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
      int n = this.rand.nextInt(2) + this.rand.nextInt(1 + lootingModifier);

      for (int i = 0; i < n; i++) {
         this.dropItem(Items.rabbit_hide, 1);
      }

      n = this.rand.nextInt(2);

      for (int i = 0; i < n; i++) {
         if (this.isBurning()) {
            this.dropItem(Items.cooked_rabbit, 1);
         } else {
            this.dropItem(Items.rabbit, 1);
         }
      }
   }

   public boolean isRabbitBreedingItem(Item itemIn) {
      return itemIn == Items.carrot || itemIn == Items.golden_carrot || itemIn == Item.getItemFromBlock(Blocks.yellow_flower);
   }

   public EntityRabbit createChild(EntityAgeable ageable) {
      EntityRabbit entityRabbit = new EntityRabbit(this.worldObj);
      if (ageable instanceof EntityRabbit) {
         entityRabbit.setRabbitType(this.rand.nextBoolean() ? this.getRabbitType() : ((EntityRabbit)ageable).getRabbitType());
      }

      return entityRabbit;
   }

   @Override
   public boolean isBreedingItem(ItemStack stack) {
      return stack != null && this.isRabbitBreedingItem(stack.getItem());
   }

   public int getRabbitType() {
      return this.dataWatcher.getWatchableObjectByte(18);
   }

   public void setRabbitType(int rabbitTypeId) {
      if (rabbitTypeId == 99) {
         this.tasks.removeTask(this.aiAvoidWolves);
         this.tasks.addTask(4, new EntityRabbit.AIEvilAttack(this));
         this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
         this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
         this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityWolf.class, true));
         if (!this.hasCustomName()) {
            this.setCustomNameTag(StatCollector.translateToLocal("entity.KillerBunny.name"));
         }
      }

      this.dataWatcher.updateObject(18, (byte)rabbitTypeId);
   }

   @Override
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
      livingdata = super.onInitialSpawn(difficulty, livingdata);
      int nextInt = this.rand.nextInt(6);
      boolean flag = false;
      if (livingdata instanceof EntityRabbit.RabbitTypeData) {
         nextInt = ((EntityRabbit.RabbitTypeData)livingdata).typeData;
         flag = true;
      } else {
         livingdata = new EntityRabbit.RabbitTypeData(nextInt);
      }

      this.setRabbitType(nextInt);
      if (flag) {
         this.setGrowingAge(-24000);
      }

      return livingdata;
   }

   public boolean isCarrotEaten() {
      return this.carrotTicks == 0;
   }

   public int getMoveTypeDuration() {
      return this.moveType.getDuration();
   }

   public void createEatingParticles() {
      this.worldObj
         .spawnParticle(
            EnumParticleTypes.BLOCK_DUST,
            this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
            this.posY + 0.5 + this.rand.nextFloat() * this.height,
            this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
            0.0,
            0.0,
            0.0,
            Block.getStateId(Blocks.carrots.getStateFromMeta(7))
         );
      this.carrotTicks = 100;
   }

   @Override
   public void handleStatusUpdate(byte id) {
      if (id == 1) {
         this.createRunningParticles();
         this.jumpDuration = 10;
         this.jumpTicks = 0;
      } else {
         super.handleStatusUpdate(id);
      }
   }

   static class AIAvoidEntity<T extends Entity> extends EntityAIAvoidEntity<T> {
      public EntityRabbit entityInstance;

      public AIAvoidEntity(EntityRabbit rabbit, Class<T> clazz, float f, double d, double d2) {
         super(rabbit, clazz, f, d, d2);
         this.entityInstance = rabbit;
      }

      @Override
      public void updateTask() {
         super.updateTask();
      }
   }

   static class AIEvilAttack extends EntityAIAttackOnCollide {
      public AIEvilAttack(EntityRabbit rabbit) {
         super(rabbit, EntityLivingBase.class, 1.4, true);
      }

      @Override
      public double computeDouble(EntityLivingBase attackTarget) {
         return 4.0F + attackTarget.width;
      }
   }

   static class AIPanic extends EntityAIPanic {
      public EntityRabbit theEntity;

      public AIPanic(EntityRabbit rabbit, double speedIn) {
         super(rabbit, speedIn);
         this.theEntity = rabbit;
      }

      @Override
      public void updateTask() {
         super.updateTask();
         this.theEntity.setMovementSpeed(this.speed);
      }
   }

   static class AIRaidFarm extends EntityAIMoveToBlock {
      public boolean hasTarget = false;
      public boolean wantsCarrots;
      public EntityRabbit rabbit;

      public AIRaidFarm(EntityRabbit rabbitIn) {
         super(rabbitIn, 0.7F, 16);
         this.rabbit = rabbitIn;
      }

      @Override
      public boolean shouldExecute() {
         if (this.runDelay <= 0) {
            if (!this.rabbit.worldObj.getGameRules().getBoolean("mobGriefing")) {
               return false;
            }

            this.hasTarget = false;
            this.wantsCarrots = this.rabbit.isCarrotEaten();
         }

         return super.shouldExecute();
      }

      @Override
      public boolean continueExecuting() {
         return this.hasTarget && super.continueExecuting();
      }

      @Override
      public void startExecuting() {
         super.startExecuting();
      }

      @Override
      public void resetTask() {
         super.resetTask();
      }

      @Override
      public void updateTask() {
         super.updateTask();
         this.rabbit
            .getLookHelper()
            .setLookPosition(
               this.destinationBlock.getX() + 0.5,
               this.destinationBlock.getY() + 1,
               this.destinationBlock.getZ() + 0.5,
               10.0F,
               this.rabbit.getVerticalFaceSpeed()
            );
         if (this.getIsAboveDestination()) {
            World worldObj = this.rabbit.worldObj;
            BlockPos up = this.destinationBlock.up();
            IBlockState blockState = worldObj.getBlockState(up);
            Block block = blockState.getBlock();
            if (this.hasTarget && block instanceof BlockCarrot && blockState.getValue(BlockCarrot.AGE) == 7) {
               worldObj.setBlockState(up, Blocks.air.getDefaultState(), 2);
               worldObj.destroyBlock(up, true);
               this.rabbit.createEatingParticles();
            }

            this.hasTarget = false;
            this.runDelay = 10;
         }
      }

      @Override
      public boolean shouldMoveTo(World worldIn, BlockPos pos) {
         Block block = worldIn.getBlockState(pos).getBlock();
         if (block == Blocks.farmland) {
            pos = pos.up();
            IBlockState blockState = worldIn.getBlockState(pos);
            block = blockState.getBlock();
            if (block instanceof BlockCarrot && blockState.getValue(BlockCarrot.AGE) == 7 && this.wantsCarrots && !this.hasTarget) {
               this.hasTarget = true;
               return true;
            }
         }

         return false;
      }
   }

   enum EnumMoveType {
      NONE(0.0F, 0.0F, 30, 1),
      HOP(0.8F, 0.2F, 20, 10),
      STEP(1.0F, 0.45F, 14, 14),
      SPRINT(1.75F, 0.4F, 1, 8),
      ATTACK(2.0F, 0.7F, 7, 8);

      public float speed;
      public float floatValue;
      public int duration;
      public int intValue;

      EnumMoveType(float typeSpeed, float f, int typeDuration, int n) {
         this.speed = typeSpeed;
         this.floatValue = f;
         this.duration = typeDuration;
         this.intValue = n;
      }

      public float getSpeed() {
         return this.speed;
      }

      public float getFloatValue() {
         return this.floatValue;
      }

      public int getDuration() {
         return this.duration;
      }

      public int getJumpDuration() {
         return this.intValue;
      }
   }

   public class RabbitJumpHelper extends EntityJumpHelper {
      public boolean flag = false;
      public EntityRabbit theEntity;

      public RabbitJumpHelper(EntityRabbit rabbit) {
         super(rabbit);
         this.theEntity = rabbit;
      }

      public boolean getIsJumping() {
         return this.isJumping;
      }

      public boolean isFlag() {
         return this.flag;
      }

      public void setFlag(boolean flag) {
         this.flag = flag;
      }

      @Override
      public void doJump() {
         if (this.isJumping) {
            this.theEntity.doMovementAction(EntityRabbit.EnumMoveType.STEP);
            this.isJumping = false;
         }
      }
   }

   static class RabbitMoveHelper extends EntityMoveHelper {
      public EntityRabbit theEntity;

      public RabbitMoveHelper(EntityRabbit rabbit) {
         super(rabbit);
         this.theEntity = rabbit;
      }

      @Override
      public void onUpdateMoveHelper() {
         if (this.theEntity.onGround && !this.theEntity.isFlag()) {
            this.theEntity.setMovementSpeed(0.0);
         }

         super.onUpdateMoveHelper();
      }
   }

   public static class RabbitTypeData implements IEntityLivingData {
      public int typeData;

      public RabbitTypeData(int type) {
         this.typeData = type;
      }
   }
}
