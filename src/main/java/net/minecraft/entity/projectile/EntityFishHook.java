package net.minecraft.entity.projectile;

import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemFishFood.FishType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomFishable;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityFishHook extends Entity {
   public static List<WeightedRandomFishable> JUNK = Arrays.asList(
      new WeightedRandomFishable(new ItemStack(Items.leather_boots), 10).setMaxDamagePercent(0.9F),
      new WeightedRandomFishable(new ItemStack(Items.leather), 10),
      new WeightedRandomFishable(new ItemStack(Items.bone), 10),
      new WeightedRandomFishable(new ItemStack(Items.potionitem), 10),
      new WeightedRandomFishable(new ItemStack(Items.string), 5),
      new WeightedRandomFishable(new ItemStack(Items.fishing_rod), 2).setMaxDamagePercent(0.9F),
      new WeightedRandomFishable(new ItemStack(Items.bowl), 10),
      new WeightedRandomFishable(new ItemStack(Items.stick), 5),
      new WeightedRandomFishable(new ItemStack(Items.dye, 10, EnumDyeColor.BLACK.getDyeDamage()), 1),
      new WeightedRandomFishable(new ItemStack(Blocks.tripwire_hook), 10),
      new WeightedRandomFishable(new ItemStack(Items.rotten_flesh), 10)
   );
   public static List<WeightedRandomFishable> TREASURE = Arrays.asList(
      new WeightedRandomFishable(new ItemStack(Blocks.waterlily), 1),
      new WeightedRandomFishable(new ItemStack(Items.name_tag), 1),
      new WeightedRandomFishable(new ItemStack(Items.saddle), 1),
      new WeightedRandomFishable(new ItemStack(Items.bow), 1).setMaxDamagePercent(0.25F).setEnchantable(),
      new WeightedRandomFishable(new ItemStack(Items.fishing_rod), 1).setMaxDamagePercent(0.25F).setEnchantable(),
      new WeightedRandomFishable(new ItemStack(Items.book), 1).setEnchantable()
   );
   public static List<WeightedRandomFishable> FISH = Arrays.asList(
      new WeightedRandomFishable(new ItemStack(Items.fish, 1, FishType.COD.getMetadata()), 60),
      new WeightedRandomFishable(new ItemStack(Items.fish, 1, FishType.SALMON.getMetadata()), 25),
      new WeightedRandomFishable(new ItemStack(Items.fish, 1, FishType.CLOWNFISH.getMetadata()), 2),
      new WeightedRandomFishable(new ItemStack(Items.fish, 1, FishType.PUFFERFISH.getMetadata()), 13)
   );
   public int xTile = -1;
   public int yTile = -1;
   public int zTile = -1;
   public Block inTile;
   public boolean inGround;
   public int shake;
   public EntityPlayer angler;
   public int ticksInGround;
   public int ticksInAir;
   public int ticksCatchable;
   public int ticksCaughtDelay;
   public int ticksCatchableDelay;
   public float fishApproachAngle;
   public Entity caughtEntity;
   public int fishPosRotationIncrements;
   public double fishX;
   public double fishY;
   public double fishZ;
   public double fishYaw;
   public double fishPitch;
   public double clientMotionX;
   public double clientMotionY;
   public double clientMotionZ;

   public static List<WeightedRandomFishable> getFishingLoot() {
      return FISH;
   }

   public EntityFishHook(World worldIn) {
      super(worldIn);
      this.setSize(0.25F, 0.25F);
      this.ignoreFrustumCheck = true;
   }

   public EntityFishHook(World worldIn, double x, double y, double z, EntityPlayer anglerIn) {
      this(worldIn);
      this.setPosition(x, y, z);
      this.ignoreFrustumCheck = true;
      this.angler = anglerIn;
      anglerIn.fishEntity = this;
   }

   public EntityFishHook(World worldIn, EntityPlayer fishingPlayer) {
      super(worldIn);
      this.ignoreFrustumCheck = true;
      this.angler = fishingPlayer;
      this.angler.fishEntity = this;
      this.setSize(0.25F, 0.25F);
      this.setLocationAndAngles(
         fishingPlayer.posX, fishingPlayer.posY + fishingPlayer.getEyeHeight(), fishingPlayer.posZ, fishingPlayer.rotationYaw, fishingPlayer.rotationPitch
      );
      this.posX = this.posX - MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
      this.posY -= 0.1F;
      this.posZ = this.posZ - MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * 0.16F;
      this.setPosition(this.posX, this.posY, this.posZ);
      float f = 0.4F;
      this.motionX = -MathHelper.sin(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f;
      this.motionZ = MathHelper.cos(this.rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float) Math.PI) * f;
      this.motionY = -MathHelper.sin(this.rotationPitch / 180.0F * (float) Math.PI) * f;
      this.handleHookCasting(this.motionX, this.motionY, this.motionZ, 1.5F, 1.0F);
   }

   @Override
   public void entityInit() {
   }

   @Override
   public boolean isInRangeToRenderDist(double distance) {
      double d = this.getEntityBoundingBox().getAverageEdgeLength() * 4.0;
      if (Double.isNaN(d)) {
         d = 4.0;
      }

      d *= 64.0;
      return distance < d * d;
   }

   public void handleHookCasting(double d, double d2, double d3, float f, float f2) {
      float f3 = MathHelper.sqrt_double(d * d + d2 * d2 + d3 * d3);
      d /= f3;
      d2 /= f3;
      d3 /= f3;
      d += this.rand.nextGaussian() * 0.0075F * f2;
      d2 += this.rand.nextGaussian() * 0.0075F * f2;
      d3 += this.rand.nextGaussian() * 0.0075F * f2;
      d *= f;
      d2 *= f;
      d3 *= f;
      this.motionX = d;
      this.motionY = d2;
      this.motionZ = d3;
      float f4 = MathHelper.sqrt_double(d * d + d3 * d3);
      this.prevRotationYaw = this.rotationYaw = (float)(MathHelper.atan2(d, d3) * 180.0 / Math.PI);
      this.prevRotationPitch = this.rotationPitch = (float)(MathHelper.atan2(d2, f4) * 180.0 / Math.PI);
      this.ticksInGround = 0;
   }

   @Override
   public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean flag) {
      this.fishX = x;
      this.fishY = y;
      this.fishZ = z;
      this.fishYaw = yaw;
      this.fishPitch = pitch;
      this.fishPosRotationIncrements = posRotationIncrements;
      this.motionX = this.clientMotionX;
      this.motionY = this.clientMotionY;
      this.motionZ = this.clientMotionZ;
   }

   @Override
   public void setVelocity(double x, double y, double z) {
      this.clientMotionX = this.motionX = x;
      this.clientMotionY = this.motionY = y;
      this.clientMotionZ = this.motionZ = z;
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (this.fishPosRotationIncrements > 0) {
         double d = this.posX + (this.fishX - this.posX) / this.fishPosRotationIncrements;
         double d2 = this.posY + (this.fishY - this.posY) / this.fishPosRotationIncrements;
         double d3 = this.posZ + (this.fishZ - this.posZ) / this.fishPosRotationIncrements;
         double d4 = MathHelper.wrapAngleTo180_double(this.fishYaw - this.rotationYaw);
         this.rotationYaw = (float)(this.rotationYaw + d4 / this.fishPosRotationIncrements);
         this.rotationPitch = (float)(this.rotationPitch + (this.fishPitch - this.rotationPitch) / this.fishPosRotationIncrements);
         this.fishPosRotationIncrements--;
         this.setPosition(d, d2, d3);
         this.setRotation(this.rotationYaw, this.rotationPitch);
      } else {
         if (!this.worldObj.isRemote) {
            ItemStack currentEquippedItem = this.angler.getCurrentEquippedItem();
            if (this.angler.isDead
               || !this.angler.isEntityAlive()
               || currentEquippedItem == null
               || currentEquippedItem.getItem() != Items.fishing_rod
               || this.getDistanceSqToEntity(this.angler) > 1024.0) {
               this.setDead();
               this.angler.fishEntity = null;
               return;
            }

            if (this.caughtEntity != null) {
               if (!this.caughtEntity.isDead) {
                  this.posX = this.caughtEntity.posX;
                  double height = this.caughtEntity.height;
                  this.posY = this.caughtEntity.getEntityBoundingBox().minY + height * 0.8;
                  this.posZ = this.caughtEntity.posZ;
                  return;
               }

               this.caughtEntity = null;
            }
         }

         if (this.shake > 0) {
            this.shake--;
         }

         if (this.inGround) {
            if (this.worldObj.getBlockState(new BlockPos(this.xTile, this.yTile, this.zTile)).getBlock() == this.inTile) {
               this.ticksInGround++;
               if (this.ticksInGround == 1200) {
                  this.setDead();
               }

               return;
            }

            this.inGround = false;
            this.motionX = this.motionX * (this.rand.nextFloat() * 0.2F);
            this.motionY = this.motionY * (this.rand.nextFloat() * 0.2F);
            this.motionZ = this.motionZ * (this.rand.nextFloat() * 0.2F);
            this.ticksInGround = 0;
            this.ticksInAir = 0;
         } else {
            this.ticksInAir++;
         }

         Vec3 vec3 = new Vec3(this.posX, this.posY, this.posZ);
         Vec3 vec32 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         MovingObjectPosition rayTraceBlocks = this.worldObj.rayTraceBlocks(vec3, vec32);
         vec3 = new Vec3(this.posX, this.posY, this.posZ);
         vec32 = new Vec3(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
         if (rayTraceBlocks != null) {
            vec32 = new Vec3(rayTraceBlocks.hitVec.xCoord, rayTraceBlocks.hitVec.yCoord, rayTraceBlocks.hitVec.zCoord);
         }

         Entity entity = null;
         List list = this.worldObj
            .getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
         double d = 0.0;

         for (int i = 0; i < list.size(); i++) {
            Entity entity2 = (Entity)list.get(i);
            if (entity2.canBeCollidedWith() && (entity2 != this.angler || this.ticksInAir >= 5)) {
               float f = 0.3F;
               AxisAlignedBB expand = entity2.getEntityBoundingBox().expand(f, f, f);
               MovingObjectPosition calculateIntercept = expand.calculateIntercept(vec3, vec32);
               if (calculateIntercept != null) {
                  double squareDistanceTo = vec3.squareDistanceTo(calculateIntercept.hitVec);
                  if (squareDistanceTo < d || d == 0.0) {
                     entity = entity2;
                     d = squareDistanceTo;
                  }
               }
            }
         }

         if (entity != null) {
            rayTraceBlocks = new MovingObjectPosition(entity);
         }

         if (rayTraceBlocks != null) {
            if (rayTraceBlocks.entityHit != null) {
               if (rayTraceBlocks.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.angler), 0.0F)) {
                  this.caughtEntity = rayTraceBlocks.entityHit;
               }
            } else {
               this.inGround = true;
            }
         }

         if (!this.inGround) {
            this.moveEntity(this.motionX, this.motionY, this.motionZ);
            float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
            this.rotationYaw = (float)(MathHelper.atan2(this.motionX, this.motionZ) * 180.0 / Math.PI);
            this.rotationPitch = (float)(MathHelper.atan2(this.motionY, f) * 180.0 / Math.PI);

            while (this.rotationPitch - this.prevRotationPitch < -180.0F) {
               this.prevRotationPitch -= 360.0F;
            }

            while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
               this.prevRotationPitch += 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
               this.prevRotationYaw -= 360.0F;
            }

            while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
               this.prevRotationYaw += 360.0F;
            }

            this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
            this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
            float f2 = 0.92F;
            if (this.onGround || this.isCollidedHorizontally) {
               f2 = 0.5F;
            }

            byte b = 5;
            double d2 = 0.0;

            for (int i = 0; i < b; i++) {
               AxisAlignedBB entityBoundingBox = this.getEntityBoundingBox();
               double d3 = entityBoundingBox.maxY - entityBoundingBox.minY;
               double d4 = entityBoundingBox.minY + d3 * i / b;
               double d5 = entityBoundingBox.minY + d3 * (i + 1) / b;
               AxisAlignedBB axisAlignedBB = new AxisAlignedBB(entityBoundingBox.minX, d4, entityBoundingBox.minZ, entityBoundingBox.maxX, d5, entityBoundingBox.maxZ);
               if (this.worldObj.isAABBInMaterial(axisAlignedBB, Material.water)) {
                  d2 += 1.0 / b;
               }
            }

            if (!this.worldObj.isRemote && d2 > 0.0) {
               WorldServer worldObj = (WorldServer)this.worldObj;
               int n = 1;
               BlockPos up = new BlockPos(this).up();
               if (this.rand.nextFloat() < 0.25F && this.worldObj.isRainingAt(up)) {
                  n = 2;
               }

               if (this.rand.nextFloat() < 0.5F && !this.worldObj.canSeeSky(up)) {
                  n--;
               }

               if (this.ticksCatchable > 0) {
                  this.ticksCatchable--;
                  if (this.ticksCatchable <= 0) {
                     this.ticksCaughtDelay = 0;
                     this.ticksCatchableDelay = 0;
                  }
               } else if (this.ticksCatchableDelay > 0) {
                  this.ticksCatchableDelay -= n;
                  if (this.ticksCatchableDelay <= 0) {
                     this.motionY -= 0.2F;
                     this.playSound("random.splash", 0.25F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F);
                     float f3 = MathHelper.floor_double(this.getEntityBoundingBox().minY);
                     worldObj.spawnParticle(
                        EnumParticleTypes.WATER_BUBBLE, this.posX, f3 + 1.0F, this.posZ, (int)(1.0F + this.width * 20.0F), this.width, 0.0, this.width, 0.2F
                     );
                     worldObj.spawnParticle(
                        EnumParticleTypes.WATER_WAKE, this.posX, f3 + 1.0F, this.posZ, (int)(1.0F + this.width * 20.0F), this.width, 0.0, this.width, 0.2F
                     );
                     this.ticksCatchable = MathHelper.getRandomIntegerInRange(this.rand, 10, 30);
                  } else {
                     this.fishApproachAngle = (float)(this.fishApproachAngle + this.rand.nextGaussian() * 4.0);
                     float f3 = this.fishApproachAngle * (float) (Math.PI / 180.0);
                     float sin = MathHelper.sin(f3);
                     float cos = MathHelper.cos(f3);
                     double d3 = this.posX + sin * this.ticksCatchableDelay * 0.1F;
                     double d4 = MathHelper.floor_double(this.getEntityBoundingBox().minY) + 1.0F;
                     double d5 = this.posZ + cos * this.ticksCatchableDelay * 0.1F;
                     Block block = worldObj.getBlockState(new BlockPos((int)d3, (int)d4 - 1, (int)d5)).getBlock();
                     if (block == Blocks.water || block == Blocks.flowing_water) {
                        if (this.rand.nextFloat() < 0.15F) {
                           worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, d3, d4 - 0.1F, d5, 1, sin, 0.1, cos, 0.0);
                        }

                        float f4 = sin * 0.04F;
                        float f5 = cos * 0.04F;
                        worldObj.spawnParticle(EnumParticleTypes.WATER_WAKE, d3, d4, d5, 0, f5, 0.01, -f4, 1.0);
                        worldObj.spawnParticle(EnumParticleTypes.WATER_WAKE, d3, d4, d5, 0, -f5, 0.01, f4, 1.0);
                     }
                  }
               } else if (this.ticksCaughtDelay > 0) {
                  this.ticksCaughtDelay -= n;
                  float f3 = 0.15F;
                  if (this.ticksCaughtDelay < 20) {
                     f3 = (float)(f3 + (20 - this.ticksCaughtDelay) * 0.05);
                  } else if (this.ticksCaughtDelay < 40) {
                     f3 = (float)(f3 + (40 - this.ticksCaughtDelay) * 0.02);
                  } else if (this.ticksCaughtDelay < 60) {
                     f3 = (float)(f3 + (60 - this.ticksCaughtDelay) * 0.01);
                  }

                  if (this.rand.nextFloat() < f3) {
                     float f4 = MathHelper.randomFloatClamp(this.rand, 0.0F, 360.0F) * (float) (Math.PI / 180.0);
                     float randomFloatClamp = MathHelper.randomFloatClamp(this.rand, 25.0F, 60.0F);
                     double d3 = this.posX + MathHelper.sin(f4) * randomFloatClamp * 0.1F;
                     double d4 = MathHelper.floor_double(this.getEntityBoundingBox().minY) + 1.0F;
                     double d5 = this.posZ + MathHelper.cos(f4) * randomFloatClamp * 0.1F;
                     Block block = worldObj.getBlockState(new BlockPos((int)d3, (int)d4 - 1, (int)d5)).getBlock();
                     if (block == Blocks.water || block == Blocks.flowing_water) {
                        worldObj.spawnParticle(EnumParticleTypes.WATER_SPLASH, d3, d4, d5, 2 + this.rand.nextInt(2), 0.1F, 0.0, 0.1F, 0.0);
                     }
                  }

                  if (this.ticksCaughtDelay <= 0) {
                     this.fishApproachAngle = MathHelper.randomFloatClamp(this.rand, 0.0F, 360.0F);
                     this.ticksCatchableDelay = MathHelper.getRandomIntegerInRange(this.rand, 20, 80);
                  }
               } else {
                  this.ticksCaughtDelay = MathHelper.getRandomIntegerInRange(this.rand, 100, 900);
                  this.ticksCaughtDelay = this.ticksCaughtDelay - EnchantmentHelper.getLureModifier(this.angler) * 20 * 5;
               }

               if (this.ticksCatchable > 0) {
                  this.motionY = this.motionY - this.rand.nextFloat() * this.rand.nextFloat() * this.rand.nextFloat() * 0.2;
               }
            }

            double d3 = d2 * 2.0 - 1.0;
            this.motionY += 0.04F * d3;
            if (d2 > 0.0) {
               f2 = (float)(f2 * 0.9);
               this.motionY *= 0.8;
            }

            this.motionX *= f2;
            this.motionY *= f2;
            this.motionZ *= f2;
            this.setPosition(this.posX, this.posY, this.posZ);
         }
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      tagCompound.setShort("xTile", (short)this.xTile);
      tagCompound.setShort("yTile", (short)this.yTile);
      tagCompound.setShort("zTile", (short)this.zTile);
      ResourceLocation nameForObject = Block.blockRegistry.getNameForObject(this.inTile);
      tagCompound.setString("inTile", nameForObject == null ? "" : nameForObject.toString());
      tagCompound.setByte("shake", (byte)this.shake);
      tagCompound.setByte("inGround", (byte)(this.inGround ? 1 : 0));
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      this.xTile = tagCompund.getShort("xTile");
      this.yTile = tagCompund.getShort("yTile");
      this.zTile = tagCompund.getShort("zTile");
      if (tagCompund.hasKey("inTile", 8)) {
         this.inTile = Block.getBlockFromName(tagCompund.getString("inTile"));
      } else {
         this.inTile = Block.getBlockById(tagCompund.getByte("inTile") & 255);
      }

      this.shake = tagCompund.getByte("shake") & 255;
      this.inGround = tagCompund.getByte("inGround") == 1;
   }

   public int handleHookRetraction() {
      if (this.worldObj.isRemote) {
         return 0;
      }

      byte b = 0;
      if (this.caughtEntity != null) {
         double d = this.angler.posX - this.posX;
         double d2 = this.angler.posY - this.posY;
         double d3 = this.angler.posZ - this.posZ;
         double d4 = MathHelper.sqrt_double(d * d + d2 * d2 + d3 * d3);
         double d5 = 0.1;
         this.caughtEntity.motionX += d * d5;
         this.caughtEntity.motionY = this.caughtEntity.motionY + (d2 * d5 + MathHelper.sqrt_double(d4) * 0.08);
         this.caughtEntity.motionZ += d3 * d5;
         b = 3;
      } else if (this.ticksCatchable > 0) {
         EntityItem entityItem = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, this.getFishingResult());
         double d = this.angler.posX - this.posX;
         double d2 = this.angler.posY - this.posY;
         double d3 = this.angler.posZ - this.posZ;
         double d4 = MathHelper.sqrt_double(d * d + d2 * d2 + d3 * d3);
         double d5 = 0.1;
         entityItem.motionX = d * d5;
         entityItem.motionY = d2 * d5 + MathHelper.sqrt_double(d4) * 0.08;
         entityItem.motionZ = d3 * d5;
         this.worldObj.spawnEntityInWorld(entityItem);
         this.angler
            .worldObj
            .spawnEntityInWorld(
               new EntityXPOrb(this.angler.worldObj, this.angler.posX, this.angler.posY + 0.5, this.angler.posZ + 0.5, this.rand.nextInt(6) + 1)
            );
         b = 1;
      }

      if (this.inGround) {
         b = 2;
      }

      this.setDead();
      this.angler.fishEntity = null;
      return b;
   }

   public ItemStack getFishingResult() {
      float nextFloat = this.worldObj.rand.nextFloat();
      int luckOfSeaModifier = EnchantmentHelper.getLuckOfSeaModifier(this.angler);
      int lureModifier = EnchantmentHelper.getLureModifier(this.angler);
      float f = 0.1F - luckOfSeaModifier * 0.025F - lureModifier * 0.01F;
      float f2 = 0.05F + luckOfSeaModifier * 0.01F - lureModifier * 0.01F;
      f = MathHelper.clamp_float(f, 0.0F, 1.0F);
      f2 = MathHelper.clamp_float(f2, 0.0F, 1.0F);
      if (nextFloat < f) {
         this.angler.triggerAchievement(StatList.junkFishedStat);
         return WeightedRandom.getRandomItem(this.rand, JUNK).getItemStack(this.rand);
      } else {
         nextFloat -= f;
         if (nextFloat < f2) {
            this.angler.triggerAchievement(StatList.treasureFishedStat);
            return WeightedRandom.getRandomItem(this.rand, TREASURE).getItemStack(this.rand);
         } else {
            float f3 = nextFloat - f2;
            this.angler.triggerAchievement(StatList.fishCaughtStat);
            return WeightedRandom.getRandomItem(this.rand, FISH).getItemStack(this.rand);
         }
      }
   }

   @Override
   public void setDead() {
      super.setDead();
      if (this.angler != null) {
         this.angler.fishEntity = null;
      }
   }
}
