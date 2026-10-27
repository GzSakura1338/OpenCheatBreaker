package net.minecraft.entity.ai;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;

public class EntityAIVillagerInteract extends EntityAIWatchClosest2 {
   public int interactionDelay;
   public EntityVillager villager;

   public EntityAIVillagerInteract(EntityVillager villagerIn) {
      super(villagerIn, EntityVillager.class, 3.0F, 0.02F);
      this.villager = villagerIn;
   }

   @Override
   public void startExecuting() {
      super.startExecuting();
      if (this.villager.canAbondonItems() && this.closestEntity instanceof EntityVillager && ((EntityVillager)this.closestEntity).check()) {
         this.interactionDelay = 10;
      } else {
         this.interactionDelay = 0;
      }
   }

   @Override
   public void updateTask() {
      super.updateTask();
      if (this.interactionDelay > 0) {
         this.interactionDelay--;
         if (this.interactionDelay == 0) {
            InventoryBasic villagerInventory = this.villager.getVillagerInventory();

            for (int i = 0; i < villagerInventory.getSizeInventory(); i++) {
               ItemStack stackInSlot = villagerInventory.getStackInSlot(i);
               ItemStack itemStack = null;
               if (stackInSlot != null) {
                  Item item = stackInSlot.getItem();
                  if ((item == Items.bread || item == Items.potato || item == Items.carrot) && stackInSlot.stackSize > 3) {
                     int n = stackInSlot.stackSize / 2;
                     stackInSlot.stackSize -= n;
                     itemStack = new ItemStack(item, n, stackInSlot.getMetadata());
                  } else if (item == Items.wheat && stackInSlot.stackSize > 5) {
                     int n = stackInSlot.stackSize / 2 / 3 * 3;
                     int n2 = n / 3;
                     stackInSlot.stackSize -= n;
                     itemStack = new ItemStack(Items.bread, n2, 0);
                  }

                  if (stackInSlot.stackSize <= 0) {
                     villagerInventory.setInventorySlotContents(i, (ItemStack)null);
                  }
               }

               if (itemStack != null) {
                  double d = this.villager.posY - 0.3F + this.villager.getEyeHeight();
                  EntityItem entityItem = new EntityItem(this.villager.worldObj, this.villager.posX, d, this.villager.posZ, itemStack);
                  float f = 0.3F;
                  float rotationYawHead = this.villager.rotationYawHead;
                  float rotationPitch = this.villager.rotationPitch;
                  entityItem.motionX = -MathHelper.sin(rotationYawHead / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI) * f;
                  entityItem.motionZ = MathHelper.cos(rotationYawHead / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI) * f;
                  entityItem.motionY = -MathHelper.sin(rotationPitch / 180.0F * (float) Math.PI) * f + 0.1F;
                  entityItem.setDefaultPickupDelay();
                  this.villager.worldObj.spawnEntityInWorld(entityItem);
                  break;
               }
            }
         }
      }
   }
}
