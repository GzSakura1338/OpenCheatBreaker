package net.minecraft.entity.item;

import java.util.List;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerHopper;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.world.World;

public class EntityMinecartHopper extends EntityMinecartContainer implements IHopper {
   public boolean isBlocked = true;
   public int transferTicker = -1;
   public BlockPos currentBlockPos = BlockPos.ORIGIN;

   public EntityMinecartHopper(World worldIn) {
      super(worldIn);
   }

   public EntityMinecartHopper(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
   }

   @Override
   public EntityMinecart.EnumMinecartType getMinecartType() {
      return EntityMinecart.EnumMinecartType.HOPPER;
   }

   @Override
   public IBlockState getDefaultDisplayTile() {
      return Blocks.hopper.getDefaultState();
   }

   @Override
   public int getDefaultDisplayTileOffset() {
      return 1;
   }

   @Override
   public int getSizeInventory() {
      return 5;
   }

   @Override
   public boolean interactFirst(EntityPlayer playerIn) {
      if (!this.worldObj.isRemote) {
         playerIn.displayGUIChest(this);
      }

      return true;
   }

   @Override
   public void onActivatorRailPass(int x, int y, int z, boolean receivingPower) {
      boolean flag = !receivingPower;
      if (flag != this.getBlocked()) {
         this.setBlocked(flag);
      }
   }

   public boolean getBlocked() {
      return this.isBlocked;
   }

   public void setBlocked(boolean flag) {
      this.isBlocked = flag;
   }

   @Override
   public World getWorld() {
      return this.worldObj;
   }

   @Override
   public double getXPos() {
      return this.posX;
   }

   @Override
   public double getYPos() {
      return this.posY + 0.5;
   }

   @Override
   public double getZPos() {
      return this.posZ;
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (!this.worldObj.isRemote && this.isEntityAlive() && this.getBlocked()) {
         BlockPos blockPos = new BlockPos(this);
         if (blockPos.equals(this.currentBlockPos)) {
            this.transferTicker--;
         } else {
            this.setTransferTicker(0);
         }

         if (!this.canTransfer()) {
            this.setTransferTicker(0);
            if (this.canOperate()) {
               this.setTransferTicker(4);
               this.markDirty();
            }
         }
      }
   }

   public boolean canOperate() {
      if (TileEntityHopper.captureDroppedItems(this)) {
         return true;
      }

      List entitiesWithinAABB = this.worldObj.getEntitiesWithinAABB(EntityItem.class, this.getEntityBoundingBox().expand(0.25, 0.0, 0.25), EntitySelectors.selectAnything);
      if (entitiesWithinAABB.size() > 0) {
         TileEntityHopper.putDropInInventoryAllSlots(this, (EntityItem)entitiesWithinAABB.get(0));
      }

      return false;
   }

   @Override
   public void killMinecart(DamageSource source) {
      super.killMinecart(source);
      if (this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
         this.dropItemWithOffset(Item.getItemFromBlock(Blocks.hopper), 1, 0.0F);
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setInteger("TransferCooldown", this.transferTicker);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.transferTicker = tagCompund.getInteger("TransferCooldown");
   }

   public void setTransferTicker(int n) {
      this.transferTicker = n;
   }

   public boolean canTransfer() {
      return this.transferTicker > 0;
   }

   @Override
   public String getGuiID() {
      return "minecraft:hopper";
   }

   @Override
   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return new ContainerHopper(playerInventory, this, playerIn);
   }
}
