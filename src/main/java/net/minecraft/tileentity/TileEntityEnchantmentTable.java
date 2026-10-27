package net.minecraft.tileentity;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IInteractionObject;

public class TileEntityEnchantmentTable extends TileEntity implements IInteractionObject, ITickable {
   public int tickCount;
   public float pageFlip;
   public float pageFlipPrev;
   public float nextPageTurningVelocity;
   public float pageTurningVelocity;
   public float bookSpread;
   public float bookSpreadPrev;
   public float bookRotation;
   public float bookRotationPrev;
   public float closedBookAngle;
   public static Random rand = new Random();
   public String customName;

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      if (this.hasCustomName()) {
         compound.setString("CustomName", this.customName);
      }
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      if (compound.hasKey("CustomName", 8)) {
         this.customName = compound.getString("CustomName");
      }
   }

   @Override
   public void update() {
      this.bookSpreadPrev = this.bookSpread;
      this.bookRotationPrev = this.bookRotation;
      EntityPlayer closestPlayer = this.worldObj.getClosestPlayer(this.pos.getX() + 0.5F, this.pos.getY() + 0.5F, this.pos.getZ() + 0.5F, 3.0);
      if (closestPlayer != null) {
         double d = closestPlayer.posX - (this.pos.getX() + 0.5F);
         double d2 = closestPlayer.posZ - (this.pos.getZ() + 0.5F);
         this.closedBookAngle = (float)MathHelper.atan2(d2, d);
         this.bookSpread += 0.1F;
         if (this.bookSpread < 0.5F || rand.nextInt(40) == 0) {
            float nextPageTurningVelocity = this.nextPageTurningVelocity;

            do {
               this.nextPageTurningVelocity = this.nextPageTurningVelocity + (rand.nextInt(4) - rand.nextInt(4));
            } while (nextPageTurningVelocity == this.nextPageTurningVelocity);
         }
      } else {
         this.closedBookAngle += 0.02F;
         this.bookSpread -= 0.1F;
      }

      while (this.bookRotation >= (float) Math.PI) {
         this.bookRotation -= (float) (Math.PI * 2);
      }

      while (this.bookRotation < (float) -Math.PI) {
         this.bookRotation += (float) (Math.PI * 2);
      }

      while (this.closedBookAngle >= (float) Math.PI) {
         this.closedBookAngle -= (float) (Math.PI * 2);
      }

      while (this.closedBookAngle < (float) -Math.PI) {
         this.closedBookAngle += (float) (Math.PI * 2);
      }

      float f = this.closedBookAngle - this.bookRotation;

      while (f >= (float) Math.PI) {
         f -= (float) (Math.PI * 2);
      }

      while (f < (float) -Math.PI) {
         f += (float) (Math.PI * 2);
      }

      this.bookRotation += f * 0.4F;
      this.bookSpread = MathHelper.clamp_float(this.bookSpread, 0.0F, 1.0F);
      this.tickCount++;
      this.pageFlipPrev = this.pageFlip;
      float f2 = (this.nextPageTurningVelocity - this.pageFlip) * 0.4F;
      float f3 = 0.2F;
      f2 = MathHelper.clamp_float(f2, -f3, f3);
      this.pageTurningVelocity = this.pageTurningVelocity + (f2 - this.pageTurningVelocity) * 0.9F;
      this.pageFlip = this.pageFlip + this.pageTurningVelocity;
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.customName : "container.enchant";
   }

   @Override
   public boolean hasCustomName() {
      return this.customName != null && this.customName.length() > 0;
   }

   public void setCustomName(String customNameIn) {
      this.customName = customNameIn;
   }

   @Override
   public IChatComponent getDisplayName() {
      return this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatComponentTranslation(this.getName());
   }

   @Override
   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return new ContainerEnchantment(playerInventory, this.worldObj, this.pos);
   }

   @Override
   public String getGuiID() {
      return "minecraft:enchanting_table";
   }
}
