package net.minecraft.entity.item;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockRailBase.EnumRailDirection;
import net.minecraft.block.BlockRailPowered;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.IWorldNameable;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class EntityMinecart extends Entity implements IWorldNameable {
   public boolean isInReverse;
   public String entityName;
   public static int[][][] matrix = new int[][][]{
      {{0, 0, -1}, {0, 0, 1}},
      {{-1, 0, 0}, {1, 0, 0}},
      {{-1, -1, 0}, {1, 0, 0}},
      {{-1, 0, 0}, {1, -1, 0}},
      {{0, 0, -1}, {0, -1, 1}},
      {{0, -1, -1}, {0, 0, 1}},
      {{0, 0, 1}, {1, 0, 0}},
      {{0, 0, 1}, {-1, 0, 0}},
      {{0, 0, -1}, {-1, 0, 0}},
      {{0, 0, -1}, {1, 0, 0}}
   };
   public int turnProgress;
   public double minecartX;
   public double minecartY;
   public double minecartZ;
   public double minecartYaw;
   public double minecartPitch;
   public double velocityX;
   public double velocityY;
   public double velocityZ;

   public EntityMinecart(World worldIn) {
      super(worldIn);
      this.preventEntitySpawning = true;
      this.setSize(0.98F, 0.7F);
   }

   public static EntityMinecart getMinecart(World worldIn, double x, double y, double z, EntityMinecart.EnumMinecartType type) {
      switch (type) {
         case CHEST:
            return new EntityMinecartChest(worldIn, x, y, z);
         case FURNACE:
            return new EntityMinecartFurnace(worldIn, x, y, z);
         case TNT:
            return new EntityMinecartTNT(worldIn, x, y, z);
         case SPAWNER:
            return new EntityMinecartMobSpawner(worldIn, x, y, z);
         case HOPPER:
            return new EntityMinecartHopper(worldIn, x, y, z);
         case COMMAND_BLOCK:
            return new EntityMinecartCommandBlock(worldIn, x, y, z);
         default:
            return new EntityMinecartEmpty(worldIn, x, y, z);
      }
   }

   @Override
   public boolean canTriggerWalking() {
      return false;
   }

   @Override
   public void entityInit() {
      this.dataWatcher.addObject(17, new Integer(0));
      this.dataWatcher.addObject(18, new Integer(1));
      this.dataWatcher.addObject(19, new Float(0.0F));
      this.dataWatcher.addObject(20, new Integer(0));
      this.dataWatcher.addObject(21, new Integer(6));
      this.dataWatcher.addObject(22, (byte)0);
   }

   @Override
   public AxisAlignedBB getCollisionBox(Entity entityIn) {
      return entityIn.canBePushed() ? entityIn.getEntityBoundingBox() : null;
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox() {
      return null;
   }

   @Override
   public boolean canBePushed() {
      return true;
   }

   public EntityMinecart(World worldIn, double x, double y, double z) {
      this(worldIn);
      this.setPosition(x, y, z);
      this.motionX = 0.0;
      this.motionY = 0.0;
      this.motionZ = 0.0;
      this.prevPosX = x;
      this.prevPosY = y;
      this.prevPosZ = z;
   }

   @Override
   public double getMountedYOffset() {
      return 0.0;
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.worldObj.isRemote || this.isDead) {
         return true;
      }

      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      this.setRollingDirection(-this.getRollingDirection());
      this.setRollingAmplitude(10);
      this.setBeenAttacked();
      this.setDamage(this.getDamage() + amount * 10.0F);
      boolean flag = source.getEntity() instanceof EntityPlayer && ((EntityPlayer)source.getEntity()).capabilities.isCreativeMode;
      if (flag || this.getDamage() > 40.0F) {
         if (this.riddenByEntity != null) {
            this.riddenByEntity.mountEntity((Entity)null);
         }

         if (flag && !this.hasCustomName()) {
            this.setDead();
         } else {
            this.killMinecart(source);
         }
      }

      return true;
   }

   public void killMinecart(DamageSource source) {
      this.setDead();
      if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
         ItemStack itemStack = new ItemStack(Items.minecart, 1);
         if (this.entityName != null) {
            itemStack.setStackDisplayName(this.entityName);
         }

         this.entityDropItem(itemStack, 0.0F);
      }
   }

   @Override
   public void performHurtAnimation() {
      this.setRollingDirection(-this.getRollingDirection());
      this.setRollingAmplitude(10);
      this.setDamage(this.getDamage() + this.getDamage() * 10.0F);
   }

   @Override
   public boolean canBeCollidedWith() {
      return !this.isDead;
   }

   @Override
   public void setDead() {
      super.setDead();
   }

   @Override
   public void onUpdate() {
      if (this.getRollingAmplitude() > 0) {
         this.setRollingAmplitude(this.getRollingAmplitude() - 1);
      }

      if (this.getDamage() > 0.0F) {
         this.setDamage(this.getDamage() - 1.0F);
      }

      if (this.posY < -64.0) {
         this.kill();
      }

      if (!this.worldObj.isRemote && this.worldObj instanceof WorldServer) {
         this.worldObj.theProfiler.startSection("portal");
         MinecraftServer minecraftServer = ((WorldServer)this.worldObj).getMinecraftServer();
         int maxInPortalTime = this.getMaxInPortalTime();
         if (this.inPortal) {
            if (minecraftServer.getAllowNether()) {
               if (this.ridingEntity == null && this.portalCounter++ >= maxInPortalTime) {
                  this.portalCounter = maxInPortalTime;
                  this.timeUntilPortal = this.getPortalCooldown();
                  byte b;
                  if (this.worldObj.provider.getDimensionId() == -1) {
                     b = 0;
                  } else {
                     b = -1;
                  }

                  this.travelToDimension(b);
               }

               this.inPortal = false;
            }
         } else {
            if (this.portalCounter > 0) {
               this.portalCounter -= 4;
            }

            if (this.portalCounter < 0) {
               this.portalCounter = 0;
            }
         }

         if (this.timeUntilPortal > 0) {
            this.timeUntilPortal--;
         }

         this.worldObj.theProfiler.endSection();
      }

      if (this.worldObj.isRemote) {
         if (this.turnProgress > 0) {
            double d = this.posX + (this.minecartX - this.posX) / this.turnProgress;
            double d2 = this.posY + (this.minecartY - this.posY) / this.turnProgress;
            double d3 = this.posZ + (this.minecartZ - this.posZ) / this.turnProgress;
            double d4 = MathHelper.wrapAngleTo180_double(this.minecartYaw - this.rotationYaw);
            this.rotationYaw = (float)(this.rotationYaw + d4 / this.turnProgress);
            this.rotationPitch = (float)(this.rotationPitch + (this.minecartPitch - this.rotationPitch) / this.turnProgress);
            this.turnProgress--;
            this.setPosition(d, d2, d3);
            this.setRotation(this.rotationYaw, this.rotationPitch);
         } else {
            this.setPosition(this.posX, this.posY, this.posZ);
            this.setRotation(this.rotationYaw, this.rotationPitch);
         }
      } else {
         this.prevPosX = this.posX;
         this.prevPosY = this.posY;
         this.prevPosZ = this.posZ;
         this.motionY -= 0.04F;
         int n = MathHelper.floor_double(this.posX);
         int n2 = MathHelper.floor_double(this.posY);
         int n3 = MathHelper.floor_double(this.posZ);
         if (BlockRailBase.isRailBlock(this.worldObj, new BlockPos(n, n2 - 1, n3))) {
            n2--;
         }

         BlockPos blockPos = new BlockPos(n, n2, n3);
         IBlockState blockState = this.worldObj.getBlockState(blockPos);
         if (BlockRailBase.isRailBlock(blockState)) {
            this.moveOnRail(blockPos, blockState);
            if (blockState.getBlock() == Blocks.activator_rail) {
               this.onActivatorRailPass(n, n2, n3, blockState.getValue(BlockRailPowered.POWERED));
            }
         } else {
            this.moveDerailedMinecart();
         }

         this.doBlockCollisions();
         this.rotationPitch = 0.0F;
         double d = this.prevPosX - this.posX;
         double d2 = this.prevPosZ - this.posZ;
         if (d * d + d2 * d2 > 0.001) {
            this.rotationYaw = (float)(MathHelper.atan2(d2, d) * 180.0 / Math.PI);
            if (this.isInReverse) {
               this.rotationYaw += 180.0F;
            }
         }

         double d3 = MathHelper.wrapAngleTo180_float(this.rotationYaw - this.prevRotationYaw);
         if (d3 < -170.0 || d3 >= 170.0) {
            this.rotationYaw += 180.0F;
            this.isInReverse = !this.isInReverse;
         }

         this.setRotation(this.rotationYaw, this.rotationPitch);

         for (Entity entity : this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().expand(0.2F, 0.0, 0.2F))) {
            if (entity != this.riddenByEntity && entity.canBePushed() && entity instanceof EntityMinecart) {
               entity.applyEntityCollision(this);
            }
         }

         if (this.riddenByEntity != null && this.riddenByEntity.isDead) {
            if (this.riddenByEntity.ridingEntity == this) {
               this.riddenByEntity.ridingEntity = null;
            }

            this.riddenByEntity = null;
         }

         this.handleWaterMovement();
      }
   }

   public double getMaximumSpeed() {
      return 0.4;
   }

   public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
   }

   public void moveDerailedMinecart() {
      double maximumSpeed = this.getMaximumSpeed();
      this.motionX = MathHelper.clamp_double(this.motionX, -maximumSpeed, maximumSpeed);
      this.motionZ = MathHelper.clamp_double(this.motionZ, -maximumSpeed, maximumSpeed);
      if (this.onGround) {
         this.motionX *= 0.5;
         this.motionY *= 0.5;
         this.motionZ *= 0.5;
      }

      this.moveEntity(this.motionX, this.motionY, this.motionZ);
      if (!this.onGround) {
         this.motionX *= 0.95F;
         this.motionY *= 0.95F;
         this.motionZ *= 0.95F;
      }
   }

   public void moveOnRail(BlockPos blockPos, IBlockState blockState) {
      this.fallDistance = 0.0F;
      Vec3 snapPositionToRail = this.snapPositionToRail(this.posX, this.posY, this.posZ);
      this.posY = blockPos.getY();
      boolean flag = false;
      boolean flag2 = false;
      BlockRailBase block = (BlockRailBase)blockState.getBlock();
      if (block == Blocks.golden_rail) {
         flag = blockState.getValue(BlockRailPowered.POWERED);
         flag2 = !flag;
      }

      double d = 0.0078125;
      EnumRailDirection value = blockState.getValue(block.getShapeProperty());
      switch (value) {
         case ASCENDING_EAST:
            this.motionX -= 0.0078125;
            this.posY++;
            break;
         case ASCENDING_WEST:
            this.motionX += 0.0078125;
            this.posY++;
            break;
         case ASCENDING_NORTH:
            this.motionZ += 0.0078125;
            this.posY++;
            break;
         case ASCENDING_SOUTH:
            this.motionZ -= 0.0078125;
            this.posY++;
      }

      int[][] intses = matrix[value.getMetadata()];
      double d2 = intses[1][0] - intses[0][0];
      double d3 = intses[1][2] - intses[0][2];
      double sqrt = Math.sqrt(d2 * d2 + d3 * d3);
      double d4 = this.motionX * d2 + this.motionZ * d3;
      if (d4 < 0.0) {
         d2 = -d2;
         d3 = -d3;
      }

      double sqrt2 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
      if (sqrt2 > 2.0) {
         sqrt2 = 2.0;
      }

      this.motionX = sqrt2 * d2 / sqrt;
      this.motionZ = sqrt2 * d3 / sqrt;
      if (this.riddenByEntity instanceof EntityLivingBase) {
         double moveForward = ((EntityLivingBase)this.riddenByEntity).moveForward;
         if (moveForward > 0.0) {
            double d5 = -Math.sin(this.riddenByEntity.rotationYaw * (float) Math.PI / 180.0F);
            double cos = Math.cos(this.riddenByEntity.rotationYaw * (float) Math.PI / 180.0F);
            double d6 = this.motionX * this.motionX + this.motionZ * this.motionZ;
            if (d6 < 0.01) {
               this.motionX += d5 * 0.1;
               this.motionZ += cos * 0.1;
               flag2 = false;
            }
         }
      }

      if (flag2) {
         double sqrt3 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (sqrt3 < 0.03) {
            this.motionX *= 0.0;
            this.motionY *= 0.0;
            this.motionZ *= 0.0;
         } else {
            this.motionX *= 0.5;
            this.motionY *= 0.0;
            this.motionZ *= 0.5;
         }
      }

      double d5 = 0.0;
      double d6 = blockPos.getX() + 0.5 + intses[0][0] * 0.5;
      double d7 = blockPos.getZ() + 0.5 + intses[0][2] * 0.5;
      double d8 = blockPos.getX() + 0.5 + intses[1][0] * 0.5;
      double d9 = blockPos.getZ() + 0.5 + intses[1][2] * 0.5;
      d2 = d8 - d6;
      d3 = d9 - d7;
      if (d2 == 0.0) {
         this.posX = blockPos.getX() + 0.5;
         d5 = this.posZ - blockPos.getZ();
      } else if (d3 == 0.0) {
         this.posZ = blockPos.getZ() + 0.5;
         d5 = this.posX - blockPos.getX();
      } else {
         double d10 = this.posX - d6;
         double d11 = this.posZ - d7;
         d5 = (d10 * d2 + d11 * d3) * 2.0;
      }

      this.posX = d6 + d2 * d5;
      this.posZ = d7 + d3 * d5;
      this.setPosition(this.posX, this.posY, this.posZ);
      double motionX = this.motionX;
      double motionZ = this.motionZ;
      if (this.riddenByEntity != null) {
         motionX *= 0.75;
         motionZ *= 0.75;
      }

      double maximumSpeed = this.getMaximumSpeed();
      motionX = MathHelper.clamp_double(motionX, -maximumSpeed, maximumSpeed);
      motionZ = MathHelper.clamp_double(motionZ, -maximumSpeed, maximumSpeed);
      this.moveEntity(motionX, 0.0, motionZ);
      if (intses[0][1] != 0
         && MathHelper.floor_double(this.posX) - blockPos.getX() == intses[0][0]
         && MathHelper.floor_double(this.posZ) - blockPos.getZ() == intses[0][2]) {
         this.setPosition(this.posX, this.posY + intses[0][1], this.posZ);
      } else if (intses[1][1] != 0
         && MathHelper.floor_double(this.posX) - blockPos.getX() == intses[1][0]
         && MathHelper.floor_double(this.posZ) - blockPos.getZ() == intses[1][2]) {
         this.setPosition(this.posX, this.posY + intses[1][1], this.posZ);
      }

      this.applyDrag();
      Vec3 snapPositionToRail2 = this.snapPositionToRail(this.posX, this.posY, this.posZ);
      if (snapPositionToRail2 != null && snapPositionToRail != null) {
         double d10 = (snapPositionToRail.yCoord - snapPositionToRail2.yCoord) * 0.05;
         sqrt2 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (sqrt2 > 0.0) {
            this.motionX = this.motionX / sqrt2 * (sqrt2 + d10);
            this.motionZ = this.motionZ / sqrt2 * (sqrt2 + d10);
         }

         this.setPosition(this.posX, snapPositionToRail2.yCoord, this.posZ);
      }

      int n = MathHelper.floor_double(this.posX);
      int n2 = MathHelper.floor_double(this.posZ);
      if (n != blockPos.getX() || n2 != blockPos.getZ()) {
         sqrt2 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         this.motionX = sqrt2 * (n - blockPos.getX());
         this.motionZ = sqrt2 * (n2 - blockPos.getZ());
      }

      if (flag) {
         double sqrt3 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
         if (sqrt3 > 0.01) {
            double d10 = 0.06;
            this.motionX = this.motionX + this.motionX / sqrt3 * d10;
            this.motionZ = this.motionZ + this.motionZ / sqrt3 * d10;
         } else if (value == EnumRailDirection.EAST_WEST) {
            if (this.worldObj.getBlockState(blockPos.west()).getBlock().isNormalCube()) {
               this.motionX = 0.02;
            } else if (this.worldObj.getBlockState(blockPos.east()).getBlock().isNormalCube()) {
               this.motionX = -0.02;
            }
         } else if (value == EnumRailDirection.NORTH_SOUTH) {
            if (this.worldObj.getBlockState(blockPos.north()).getBlock().isNormalCube()) {
               this.motionZ = 0.02;
            } else if (this.worldObj.getBlockState(blockPos.south()).getBlock().isNormalCube()) {
               this.motionZ = -0.02;
            }
         }
      }
   }

   public void applyDrag() {
      if (this.riddenByEntity != null) {
         this.motionX *= 0.997F;
         this.motionY *= 0.0;
         this.motionZ *= 0.997F;
      } else {
         this.motionX *= 0.96F;
         this.motionY *= 0.0;
         this.motionZ *= 0.96F;
      }
   }

   @Override
   public void setPosition(double x, double y, double z) {
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      float f = this.width / 2.0F;
      float height = this.height;
      this.setEntityBoundingBox(new AxisAlignedBB(x - f, y, z - f, x + f, y + height, z + f));
   }

   public Vec3 snapPositionToRailWithOffset(double d, double d2, double d3, double d4) {
      int n = MathHelper.floor_double(d);
      int n2 = MathHelper.floor_double(d2);
      int n3 = MathHelper.floor_double(d3);
      if (BlockRailBase.isRailBlock(this.worldObj, new BlockPos(n, n2 - 1, n3))) {
         n2--;
      }

      IBlockState blockState = this.worldObj.getBlockState(new BlockPos(n, n2, n3));
      if (BlockRailBase.isRailBlock(blockState)) {
         EnumRailDirection value = blockState.getValue(((BlockRailBase)blockState.getBlock()).getShapeProperty());
         d2 = n2;
         if (value.isAscending()) {
            d2 = n2 + 1;
         }

         int[][] intses = matrix[value.getMetadata()];
         double d5 = intses[1][0] - intses[0][0];
         double d6 = intses[1][2] - intses[0][2];
         double sqrt = Math.sqrt(d5 * d5 + d6 * d6);
         d5 /= sqrt;
         d6 /= sqrt;
         d += d5 * d4;
         d3 += d6 * d4;
         if (intses[0][1] != 0 && MathHelper.floor_double(d) - n == intses[0][0] && MathHelper.floor_double(d3) - n3 == intses[0][2]) {
            d2 += intses[0][1];
         } else if (intses[1][1] != 0 && MathHelper.floor_double(d) - n == intses[1][0] && MathHelper.floor_double(d3) - n3 == intses[1][2]) {
            d2 += intses[1][1];
         }

         return this.snapPositionToRail(d, d2, d3);
      } else {
         return null;
      }
   }

   public Vec3 snapPositionToRail(double d, double d2, double d3) {
      int n = MathHelper.floor_double(d);
      int n2 = MathHelper.floor_double(d2);
      int n3 = MathHelper.floor_double(d3);
      if (BlockRailBase.isRailBlock(this.worldObj, new BlockPos(n, n2 - 1, n3))) {
         n2--;
      }

      IBlockState blockState = this.worldObj.getBlockState(new BlockPos(n, n2, n3));
      if (BlockRailBase.isRailBlock(blockState)) {
         EnumRailDirection value = blockState.getValue(((BlockRailBase)blockState.getBlock()).getShapeProperty());
         int[][] intses = matrix[value.getMetadata()];
         double d4 = 0.0;
         double d5 = n + 0.5 + intses[0][0] * 0.5;
         double d6 = n2 + 0.0625 + intses[0][1] * 0.5;
         double d7 = n3 + 0.5 + intses[0][2] * 0.5;
         double d8 = n + 0.5 + intses[1][0] * 0.5;
         double d9 = n2 + 0.0625 + intses[1][1] * 0.5;
         double d10 = n3 + 0.5 + intses[1][2] * 0.5;
         double d11 = d8 - d5;
         double d12 = (d9 - d6) * 2.0;
         double d13 = d10 - d7;
         if (d11 == 0.0) {
            d = n + 0.5;
            d4 = d3 - n3;
         } else if (d13 == 0.0) {
            d3 = n3 + 0.5;
            d4 = d - n;
         } else {
            double d14 = d - d5;
            double d15 = d3 - d7;
            d4 = (d14 * d11 + d15 * d13) * 2.0;
         }

         d = d5 + d11 * d4;
         d2 = d6 + d12 * d4;
         d3 = d7 + d13 * d4;
         if (d12 < 0.0) {
            d2++;
         }

         if (d12 > 0.0) {
            d2 += 0.5;
         }

         return new Vec3(d, d2, d3);
      } else {
         return null;
      }
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      if (tagCompund.getBoolean("CustomDisplayTile")) {
         int integer = tagCompund.getInteger("DisplayData");
         if (tagCompund.hasKey("DisplayTile", 8)) {
            Block blockFromName = Block.getBlockFromName(tagCompund.getString("DisplayTile"));
            if (blockFromName == null) {
               this.setCustomBlock(Blocks.air.getDefaultState());
            } else {
               this.setCustomBlock(blockFromName.getStateFromMeta(integer));
            }
         } else {
            Block blockById = Block.getBlockById(tagCompund.getInteger("DisplayTile"));
            if (blockById == null) {
               this.setCustomBlock(Blocks.air.getDefaultState());
            } else {
               this.setCustomBlock(blockById.getStateFromMeta(integer));
            }
         }

         this.setDisplayTileOffset(tagCompund.getInteger("DisplayOffset"));
      }

      if (tagCompund.hasKey("CustomName", 8) && tagCompund.getString("CustomName").length() > 0) {
         this.entityName = tagCompund.getString("CustomName");
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      if (this.hasDisplayTile()) {
         tagCompound.setBoolean("CustomDisplayTile", true);
         IBlockState displayTile = this.getDisplayTile();
         ResourceLocation nameForObject = Block.blockRegistry.getNameForObject(displayTile.getBlock());
         tagCompound.setString("DisplayTile", nameForObject == null ? "" : nameForObject.toString());
         tagCompound.setInteger("DisplayData", displayTile.getBlock().getMetaFromState(displayTile));
         tagCompound.setInteger("DisplayOffset", this.getDisplayTileOffset());
      }

      if (this.entityName != null && this.entityName.length() > 0) {
         tagCompound.setString("CustomName", this.entityName);
      }
   }

   @Override
   public void applyEntityCollision(Entity entityIn) {
      if (!this.worldObj.isRemote && !entityIn.noClip && !this.noClip && entityIn != this.riddenByEntity) {
         if (entityIn instanceof EntityLivingBase
            && !(entityIn instanceof EntityPlayer)
            && !(entityIn instanceof EntityIronGolem)
            && this.getMinecartType() == EntityMinecart.EnumMinecartType.RIDEABLE
            && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.01
            && this.riddenByEntity == null
            && entityIn.ridingEntity == null) {
            entityIn.mountEntity(this);
         }

         double d = entityIn.posX - this.posX;
         double d2 = entityIn.posZ - this.posZ;
         double d3 = d * d + d2 * d2;
         if (d3 >= 1.0E-4F) {
            d3 = MathHelper.sqrt_double(d3);
            d /= d3;
            d2 /= d3;
            double d4 = 1.0 / d3;
            if (d4 > 1.0) {
               d4 = 1.0;
            }

            d *= d4;
            d2 *= d4;
            d *= 0.1F;
            d2 *= 0.1F;
            d *= 1.0F - this.entityCollisionReduction;
            d2 *= 1.0F - this.entityCollisionReduction;
            d *= 0.5;
            d2 *= 0.5;
            if (entityIn instanceof EntityMinecart) {
               double d5 = entityIn.posX - this.posX;
               double d6 = entityIn.posZ - this.posZ;
               Vec3 normalize = new Vec3(d5, 0.0, d6).normalize();
               Vec3 normalize2 = new Vec3(
                     MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F), 0.0, MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F)
                  )
                  .normalize();
               double abs = Math.abs(normalize.dotProduct(normalize2));
               if (abs < 0.8F) {
                  return;
               }

               double d7 = entityIn.motionX + this.motionX;
               double d8 = entityIn.motionZ + this.motionZ;
               if (((EntityMinecart)entityIn).getMinecartType() == EntityMinecart.EnumMinecartType.FURNACE
                  && this.getMinecartType() != EntityMinecart.EnumMinecartType.FURNACE) {
                  this.motionX *= 0.2F;
                  this.motionZ *= 0.2F;
                  this.addVelocity(entityIn.motionX - d, 0.0, entityIn.motionZ - d2);
                  entityIn.motionX *= 0.95F;
                  entityIn.motionZ *= 0.95F;
               } else if (((EntityMinecart)entityIn).getMinecartType() != EntityMinecart.EnumMinecartType.FURNACE
                  && this.getMinecartType() == EntityMinecart.EnumMinecartType.FURNACE) {
                  entityIn.motionX *= 0.2F;
                  entityIn.motionZ *= 0.2F;
                  entityIn.addVelocity(this.motionX + d, 0.0, this.motionZ + d2);
                  this.motionX *= 0.95F;
                  this.motionZ *= 0.95F;
               } else {
                  d7 /= 2.0;
                  d8 /= 2.0;
                  this.motionX *= 0.2F;
                  this.motionZ *= 0.2F;
                  this.addVelocity(d7 - d, 0.0, d8 - d2);
                  entityIn.motionX *= 0.2F;
                  entityIn.motionZ *= 0.2F;
                  entityIn.addVelocity(d7 + d, 0.0, d8 + d2);
               }
            } else {
               this.addVelocity(-d, 0.0, -d2);
               entityIn.addVelocity(d / 4.0, 0.0, d2 / 4.0);
            }
         }
      }
   }

   @Override
   public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean flag) {
      this.minecartX = x;
      this.minecartY = y;
      this.minecartZ = z;
      this.minecartYaw = yaw;
      this.minecartPitch = pitch;
      this.turnProgress = posRotationIncrements + 2;
      this.motionX = this.velocityX;
      this.motionY = this.velocityY;
      this.motionZ = this.velocityZ;
   }

   @Override
   public void setVelocity(double x, double y, double z) {
      this.velocityX = this.motionX = x;
      this.velocityY = this.motionY = y;
      this.velocityZ = this.motionZ = z;
   }

   public void setDamage(float f) {
      this.dataWatcher.updateObject(19, f);
   }

   public float getDamage() {
      return this.dataWatcher.getWatchableObjectFloat(19);
   }

   public void setRollingAmplitude(int n) {
      this.dataWatcher.updateObject(17, n);
   }

   public int getRollingAmplitude() {
      return this.dataWatcher.getWatchableObjectInt(17);
   }

   public void setRollingDirection(int n) {
      this.dataWatcher.updateObject(18, n);
   }

   public int getRollingDirection() {
      return this.dataWatcher.getWatchableObjectInt(18);
   }

   public abstract EntityMinecart.EnumMinecartType getMinecartType();

   public IBlockState getDisplayTile() {
      return !this.hasDisplayTile() ? this.getDefaultDisplayTile() : Block.getStateById(this.getDataWatcher().getWatchableObjectInt(20));
   }

   public IBlockState getDefaultDisplayTile() {
      return Blocks.air.getDefaultState();
   }

   public int getDisplayTileOffset() {
      return !this.hasDisplayTile() ? this.getDefaultDisplayTileOffset() : this.getDataWatcher().getWatchableObjectInt(21);
   }

   public int getDefaultDisplayTileOffset() {
      return 6;
   }

   public void setCustomBlock(IBlockState blockState) {
      this.getDataWatcher().updateObject(20, Block.getStateId(blockState));
      this.setHasDisplayTile(true);
   }

   public void setDisplayTileOffset(int n) {
      this.getDataWatcher().updateObject(21, n);
      this.setHasDisplayTile(true);
   }

   public boolean hasDisplayTile() {
      return this.getDataWatcher().getWatchableObjectByte(22) == 1;
   }

   public void setHasDisplayTile(boolean flag) {
      this.getDataWatcher().updateObject(22, (byte)(flag ? 1 : 0));
   }

   @Override
   public void setCustomNameTag(String name) {
      this.entityName = name;
   }

   @Override
   public String getName() {
      return this.entityName != null ? this.entityName : super.getName();
   }

   @Override
   public boolean hasCustomName() {
      return this.entityName != null;
   }

   @Override
   public String getCustomNameTag() {
      return this.entityName;
   }

   @Override
   public IChatComponent getDisplayName() {
      if (this.hasCustomName()) {
         ChatComponentText chatComponentText = new ChatComponentText(this.entityName);
         chatComponentText.getChatStyle().setChatHoverEvent(this.getHoverEvent());
         chatComponentText.getChatStyle().setInsertion(this.getUniqueID().toString());
         return chatComponentText;
      } else {
         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation(this.getName());
         chatComponentTranslation.getChatStyle().setChatHoverEvent(this.getHoverEvent());
         chatComponentTranslation.getChatStyle().setInsertion(this.getUniqueID().toString());
         return chatComponentTranslation;
      }
   }

   public enum EnumMinecartType {
      RIDEABLE(0, "MinecartRideable"),
      CHEST(1, "MinecartChest"),
      FURNACE(2, "MinecartFurnace"),
      TNT(3, "MinecartTNT"),
      SPAWNER(4, "MinecartSpawner"),
      HOPPER(5, "MinecartHopper"),
      COMMAND_BLOCK(6, "MinecartCommandBlock");

      public int networkID;
      public String name;
      public static Map<Integer, EntityMinecart.EnumMinecartType> ID_LOOKUP = Maps.newHashMap();

      EnumMinecartType(int networkID, String name) {
         this.networkID = networkID;
         this.name = name;
      }

      public int getNetworkID() {
         return this.networkID;
      }

      public String getName() {
         return this.name;
      }

      public static EntityMinecart.EnumMinecartType byNetworkID(int id) {
         EntityMinecart.EnumMinecartType enumMinecartType = ID_LOOKUP.get(id);
         return enumMinecartType == null ? RIDEABLE : enumMinecartType;
      }

      static {
         for (EntityMinecart.EnumMinecartType enumMinecartType : values()) {
            ID_LOOKUP.put(enumMinecartType.getNetworkID(), enumMinecartType);
         }
      }
   }
}
