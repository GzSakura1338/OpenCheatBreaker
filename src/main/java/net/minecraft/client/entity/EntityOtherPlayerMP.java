package net.minecraft.client.entity;

import com.cheatbreaker.client.emote.PlayerEmoteState;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityOtherPlayerMP extends AbstractClientPlayer {
   public boolean isItemInUse;
   public int otherPlayerMPPosRotationIncrements;
   public double otherPlayerMPX;
   public double otherPlayerMPY;
   public double otherPlayerMPZ;
   public double otherPlayerMPYaw;
   public double otherPlayerMPPitch;

   public EntityOtherPlayerMP(World worldIn, GameProfile gameProfileIn) {
      super(worldIn, gameProfileIn);
      this.stepHeight = 0.0F;
      this.noClip = true;
      this.renderOffsetY = 0.25F;
      this.renderDistanceWeight = 10.0;
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      return true;
   }

   @Override
   public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean flag) {
      this.otherPlayerMPX = x;
      this.otherPlayerMPY = y;
      this.otherPlayerMPZ = z;
      this.otherPlayerMPYaw = yaw;
      this.otherPlayerMPPitch = pitch;
      this.otherPlayerMPPosRotationIncrements = posRotationIncrements;
   }

   @Override
   public void onUpdate() {
      this.renderOffsetY = 0.0F;
      super.onUpdate();
      this.prevLimbSwingAmount = this.limbSwingAmount;
      double d = this.posX - this.prevPosX;
      double d2 = this.posZ - this.prevPosZ;
      float f = MathHelper.sqrt_double(d * d + d2 * d2) * 4.0F;
      if (f > 1.0F) {
         f = 1.0F;
      }

      this.limbSwingAmount = this.limbSwingAmount + (f - this.limbSwingAmount) * 0.4F;
      this.limbSwing = this.limbSwing + this.limbSwingAmount;
      if (!this.isItemInUse && this.isEating() && this.inventory.mainInventory[this.inventory.currentItem] != null) {
         ItemStack itemStack = this.inventory.mainInventory[this.inventory.currentItem];
         this.setItemInUse(this.inventory.mainInventory[this.inventory.currentItem], itemStack.getItem().getMaxItemUseDuration(itemStack));
         this.isItemInUse = true;
      } else if (this.isItemInUse && !this.isEating()) {
         this.clearItemInUse();
         this.isItemInUse = false;
      }
   }

   @Override
   public void onLivingUpdate() {
      PlayerEmoteState.tickPlayer(this);
      if (this.otherPlayerMPPosRotationIncrements > 0) {
         double d = this.posX + (this.otherPlayerMPX - this.posX) / this.otherPlayerMPPosRotationIncrements;
         double d2 = this.posY + (this.otherPlayerMPY - this.posY) / this.otherPlayerMPPosRotationIncrements;
         double d3 = this.posZ + (this.otherPlayerMPZ - this.posZ) / this.otherPlayerMPPosRotationIncrements;
         double d4 = this.otherPlayerMPYaw - this.rotationYaw;

         while (d4 < -180.0) {
            d4 += 360.0;
         }

         while (d4 >= 180.0) {
            d4 -= 360.0;
         }

         this.rotationYaw = (float)(this.rotationYaw + d4 / this.otherPlayerMPPosRotationIncrements);
         this.rotationPitch = (float)(this.rotationPitch + (this.otherPlayerMPPitch - this.rotationPitch) / this.otherPlayerMPPosRotationIncrements);
         this.otherPlayerMPPosRotationIncrements--;
         this.setPosition(d, d2, d3);
         this.setRotation(this.rotationYaw, this.rotationPitch);
      }

      this.prevCameraYaw = this.cameraYaw;
      this.updateArmSwingProgress();
      float f = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
      float f2 = (float)Math.atan(-this.motionY * 0.2F) * 15.0F;
      if (f > 0.1F) {
         f = 0.1F;
      }

      if (!this.onGround || this.getHealth() <= 0.0F) {
         f = 0.0F;
      }

      if (this.onGround || this.getHealth() <= 0.0F) {
         f2 = 0.0F;
      }

      this.cameraYaw = this.cameraYaw + (f - this.cameraYaw) * 0.4F;
      this.cameraPitch = this.cameraPitch + (f2 - this.cameraPitch) * 0.8F;
   }

   @Override
   public void setCurrentItemOrArmor(int slotIn, ItemStack stack) {
      if (slotIn == 0) {
         this.inventory.mainInventory[this.inventory.currentItem] = stack;
      } else {
         this.inventory.armorInventory[slotIn - 1] = stack;
      }
   }

   @Override
   public void addChatMessage(IChatComponent component) {
      Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessage(component);
   }

   @Override
   public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
      return false;
   }

   @Override
   public BlockPos getPosition() {
      return new BlockPos(this.posX + 0.5, this.posY + 0.5, this.posZ + 0.5);
   }
}
