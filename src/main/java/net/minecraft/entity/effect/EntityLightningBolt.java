package net.minecraft.entity.effect;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityLightningBolt extends EntityWeatherEffect {
   public int lightningState;
   public long boltVertex;
   public int boltLivingTime;

   public EntityLightningBolt(World worldIn, double posX, double posY, double posZ) {
      super(worldIn);
      this.setLocationAndAngles(posX, posY, posZ, 0.0F, 0.0F);
      this.lightningState = 2;
      this.boltVertex = this.rand.nextLong();
      this.boltLivingTime = this.rand.nextInt(3) + 1;
      BlockPos blockPos = new BlockPos(this);
      if (!worldIn.isRemote
         && worldIn.getGameRules().getBoolean("doFireTick")
         && (worldIn.getDifficulty() == EnumDifficulty.NORMAL || worldIn.getDifficulty() == EnumDifficulty.HARD)
         && worldIn.isAreaLoaded(blockPos, 10)) {
         if (worldIn.getBlockState(blockPos).getBlock().getMaterial() == Material.air && Blocks.fire.canPlaceBlockAt(worldIn, blockPos)) {
            worldIn.setBlockState(blockPos, Blocks.fire.getDefaultState());
         }

         for (int i = 0; i < 4; i++) {
            BlockPos blockPos2 = blockPos.add(this.rand.nextInt(3) - 1, this.rand.nextInt(3) - 1, this.rand.nextInt(3) - 1);
            if (worldIn.getBlockState(blockPos2).getBlock().getMaterial() == Material.air && Blocks.fire.canPlaceBlockAt(worldIn, blockPos2)) {
               worldIn.setBlockState(blockPos2, Blocks.fire.getDefaultState());
            }
         }
      }
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (this.lightningState == 2) {
         this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "ambient.weather.thunder", 10000.0F, 0.8F + this.rand.nextFloat() * 0.2F);
         this.worldObj.playSoundEffect(this.posX, this.posY, this.posZ, "random.explode", 2.0F, 0.5F + this.rand.nextFloat() * 0.2F);
      }

      this.lightningState--;
      if (this.lightningState < 0) {
         if (this.boltLivingTime == 0) {
            this.setDead();
         } else if (this.lightningState < -this.rand.nextInt(10)) {
            this.boltLivingTime--;
            this.lightningState = 1;
            this.boltVertex = this.rand.nextLong();
            BlockPos blockPos = new BlockPos(this);
            if (!this.worldObj.isRemote
               && this.worldObj.getGameRules().getBoolean("doFireTick")
               && this.worldObj.isAreaLoaded(blockPos, 10)
               && this.worldObj.getBlockState(blockPos).getBlock().getMaterial() == Material.air
               && Blocks.fire.canPlaceBlockAt(this.worldObj, blockPos)) {
               this.worldObj.setBlockState(blockPos, Blocks.fire.getDefaultState());
            }
         }
      }

      if (this.lightningState >= 0) {
         if (this.worldObj.isRemote) {
            this.worldObj.setLastLightningBolt(2);
         } else {
            double d = 3.0;
            List list = this.worldObj
               .getEntitiesWithinAABBExcludingEntity(
                  this, new AxisAlignedBB(this.posX - d, this.posY - d, this.posZ - d, this.posX + d, this.posY + 6.0 + d, this.posZ + d)
               );

            for (int i = 0; i < list.size(); i++) {
               Entity entity = (Entity)list.get(i);
               entity.onStruckByLightning(this);
            }
         }
      }
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
}
