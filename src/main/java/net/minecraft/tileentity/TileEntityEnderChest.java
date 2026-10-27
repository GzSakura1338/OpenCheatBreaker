package net.minecraft.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ITickable;

public class TileEntityEnderChest extends TileEntity implements ITickable {
   public float lidAngle;
   public float prevLidAngle;
   public int numPlayersUsing;
   public int ticksSinceSync;

   @Override
   public void update() {
      if (++this.ticksSinceSync % 20 * 4 == 0) {
         this.worldObj.addBlockEvent(this.pos, Blocks.ender_chest, 1, this.numPlayersUsing);
      }

      this.prevLidAngle = this.lidAngle;
      int n = this.pos.getX();
      int n2 = this.pos.getY();
      int n3 = this.pos.getZ();
      float f = 0.1F;
      if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F) {
         double d = n + 0.5;
         double d2 = n3 + 0.5;
         this.worldObj.playSoundEffect(d, n2 + 0.5, d2, "random.chestopen", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
      }

      if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) {
         float lidAngle = this.lidAngle;
         if (this.numPlayersUsing > 0) {
            this.lidAngle += f;
         } else {
            this.lidAngle -= f;
         }

         if (this.lidAngle > 1.0F) {
            this.lidAngle = 1.0F;
         }

         float f2 = 0.5F;
         if (this.lidAngle < f2 && lidAngle >= f2) {
            double d = n + 0.5;
            double d2 = n3 + 0.5;
            this.worldObj.playSoundEffect(d, n2 + 0.5, d2, "random.chestclosed", 0.5F, this.worldObj.rand.nextFloat() * 0.1F + 0.9F);
         }

         if (this.lidAngle < 0.0F) {
            this.lidAngle = 0.0F;
         }
      }
   }

   @Override
   public boolean receiveClientEvent(int id, int type) {
      if (id == 1) {
         this.numPlayersUsing = type;
         return true;
      } else {
         return super.receiveClientEvent(id, type);
      }
   }

   @Override
   public void invalidate() {
      this.updateContainingBlockInfo();
      super.invalidate();
   }

   public void openChest() {
      this.numPlayersUsing++;
      this.worldObj.addBlockEvent(this.pos, Blocks.ender_chest, 1, this.numPlayersUsing);
   }

   public void closeChest() {
      this.numPlayersUsing--;
      this.worldObj.addBlockEvent(this.pos, Blocks.ender_chest, 1, this.numPlayersUsing);
   }

   public boolean canBeUsed(EntityPlayer entityPlayer) {
      return this.worldObj.getTileEntity(this.pos) != this
         ? false
         : entityPlayer.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
   }
}
