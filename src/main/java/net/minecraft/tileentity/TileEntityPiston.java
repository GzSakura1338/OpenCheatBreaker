package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class TileEntityPiston extends TileEntity implements ITickable {
   public List<Entity> affectedEntities = Lists.newArrayList();
   public IBlockState pistonState;
   public EnumFacing pistonFacing;
   public boolean extending;
   public float progress;
   public float lastProgress;
   public boolean shouldHeadBeRendered;

   public TileEntityPiston() {
   }

   public TileEntityPiston(IBlockState pistonStateIn, EnumFacing pistonFacingIn, boolean extendingIn, boolean shouldHeadBeRenderedIn) {
      this.pistonState = pistonStateIn;
      this.pistonFacing = pistonFacingIn;
      this.extending = extendingIn;
      this.shouldHeadBeRendered = shouldHeadBeRenderedIn;
   }

   public IBlockState getPistonState() {
      return this.pistonState;
   }

   @Override
   public int getBlockMetadata() {
      return 0;
   }

   public boolean isExtending() {
      return this.extending;
   }

   public EnumFacing getFacing() {
      return this.pistonFacing;
   }

   public boolean shouldPistonHeadBeRendered() {
      return this.shouldHeadBeRendered;
   }

   public float getProgress(float ticks) {
      if (ticks > 1.0F) {
         ticks = 1.0F;
      }

      return this.lastProgress + (this.progress - this.lastProgress) * ticks;
   }

   public float getOffsetX(float ticks) {
      return this.extending
         ? (this.getProgress(ticks) - 1.0F) * this.pistonFacing.getFrontOffsetX()
         : (1.0F - this.getProgress(ticks)) * this.pistonFacing.getFrontOffsetX();
   }

   public float getOffsetY(float ticks) {
      return this.extending
         ? (this.getProgress(ticks) - 1.0F) * this.pistonFacing.getFrontOffsetY()
         : (1.0F - this.getProgress(ticks)) * this.pistonFacing.getFrontOffsetY();
   }

   public float getOffsetZ(float ticks) {
      return this.extending
         ? (this.getProgress(ticks) - 1.0F) * this.pistonFacing.getFrontOffsetZ()
         : (1.0F - this.getProgress(ticks)) * this.pistonFacing.getFrontOffsetZ();
   }

   public void launchWithSlimeBlock(float f, float f2) {
      if (this.extending) {
         f = 1.0F - f;
      } else {
         f--;
      }

      AxisAlignedBB boundingBox = Blocks.piston_extension.getBoundingBox(this.worldObj, this.pos, this.pistonState, f, this.pistonFacing);
      if (boundingBox != null) {
         List list = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)null, boundingBox);
         if (!list.isEmpty()) {
            this.affectedEntities.addAll(list);

            for (Entity entity : this.affectedEntities) {
               if (this.pistonState.getBlock() == Blocks.slime_block && this.extending) {
                  switch (this.pistonFacing.getAxis()) {
                     case X:
                        entity.motionX = this.pistonFacing.getFrontOffsetX();
                        break;
                     case Y:
                        entity.motionY = this.pistonFacing.getFrontOffsetY();
                        break;
                     case Z:
                        entity.motionZ = this.pistonFacing.getFrontOffsetZ();
                  }
               } else {
                  entity.moveEntity(
                     f2 * this.pistonFacing.getFrontOffsetX(), f2 * this.pistonFacing.getFrontOffsetY(), f2 * this.pistonFacing.getFrontOffsetZ()
                  );
               }
            }

            this.affectedEntities.clear();
         }
      }
   }

   public void clearPistonTileEntity() {
      if (this.lastProgress < 1.0F && this.worldObj != null) {
         this.lastProgress = this.progress = 1.0F;
         this.worldObj.removeTileEntity(this.pos);
         this.invalidate();
         if (this.worldObj.getBlockState(this.pos).getBlock() == Blocks.piston_extension) {
            this.worldObj.setBlockState(this.pos, this.pistonState, 3);
            this.worldObj.notifyBlockOfStateChange(this.pos, this.pistonState.getBlock());
         }
      }
   }

   @Override
   public void update() {
      this.lastProgress = this.progress;
      if (this.lastProgress >= 1.0F) {
         this.launchWithSlimeBlock(1.0F, 0.25F);
         this.worldObj.removeTileEntity(this.pos);
         this.invalidate();
         if (this.worldObj.getBlockState(this.pos).getBlock() == Blocks.piston_extension) {
            this.worldObj.setBlockState(this.pos, this.pistonState, 3);
            this.worldObj.notifyBlockOfStateChange(this.pos, this.pistonState.getBlock());
         }
      } else {
         this.progress += 0.5F;
         if (this.progress >= 1.0F) {
            this.progress = 1.0F;
         }

         if (this.extending) {
            this.launchWithSlimeBlock(this.progress, this.progress - this.lastProgress + 0.0625F);
         }
      }
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.pistonState = Block.getBlockById(compound.getInteger("blockId")).getStateFromMeta(compound.getInteger("blockData"));
      this.pistonFacing = EnumFacing.getFront(compound.getInteger("facing"));
      this.lastProgress = this.progress = compound.getFloat("progress");
      this.extending = compound.getBoolean("extending");
   }

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      compound.setInteger("blockId", Block.getIdFromBlock(this.pistonState.getBlock()));
      compound.setInteger("blockData", this.pistonState.getBlock().getMetaFromState(this.pistonState));
      compound.setInteger("facing", this.pistonFacing.getIndex());
      compound.setFloat("progress", this.lastProgress);
      compound.setBoolean("extending", this.extending);
   }
}
