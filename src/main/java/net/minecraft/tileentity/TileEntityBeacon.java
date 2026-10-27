package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.util.ITickable;

public class TileEntityBeacon extends TileEntityLockable implements IInventory, ITickable {
   public static Potion[][] effectsList = new Potion[][]{
      {Potion.moveSpeed, Potion.digSpeed}, {Potion.resistance, Potion.jump}, {Potion.damageBoost}, {Potion.regeneration}
   };
   public List<TileEntityBeacon.BeamSegment> beamSegments = Lists.newArrayList();
   public long beamRenderCounter;
   public float beamSpeed;
   public boolean isComplete;
   public int levels = -1;
   public int primaryEffect;
   public int secondaryEffect;
   public ItemStack payment;
   public String customName;

   @Override
   public void update() {
      if (this.worldObj.getTotalWorldTime() % 80L == 0L) {
         this.updateBeacon();
      }
   }

   public void updateBeacon() {
      this.updateSegmentColors();
      this.addEffectsToPlayers();
   }

   public void addEffectsToPlayers() {
      if (this.isComplete && this.levels > 0 && !this.worldObj.isRemote && this.primaryEffect > 0) {
         double d = this.levels * 10 + 10;
         byte b = 0;
         if (this.levels >= 4 && this.primaryEffect == this.secondaryEffect) {
            b = 1;
         }

         int n = this.pos.getX();
         int n2 = this.pos.getY();
         int n3 = this.pos.getZ();
         AxisAlignedBB addCoord = new AxisAlignedBB(n, n2, n3, n + 1, n2 + 1, n3 + 1)
            .expand(d, d, d)
            .addCoord(0.0, this.worldObj.getHeight(), 0.0);
         List entitiesWithinAABB = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, addCoord);

         for (EntityPlayer entityPlayer : (Iterable<EntityPlayer>)entitiesWithinAABB) {
            entityPlayer.addPotionEffect(new PotionEffect(this.primaryEffect, 180, b, true, true));
         }

         if (this.levels >= 4 && this.primaryEffect != this.secondaryEffect && this.secondaryEffect > 0) {
            for (EntityPlayer entityPlayer : (Iterable<EntityPlayer>)entitiesWithinAABB) {
               entityPlayer.addPotionEffect(new PotionEffect(this.secondaryEffect, 180, 0, true, true));
            }
         }
      }
   }

   public void updateSegmentColors() {
      int levels = this.levels;
      int n = this.pos.getX();
      int n2 = this.pos.getY();
      int n3 = this.pos.getZ();
      this.levels = 0;
      this.beamSegments.clear();
      this.isComplete = true;
      TileEntityBeacon.BeamSegment beamSegment = new TileEntityBeacon.BeamSegment(EntitySheep.getDyeRgb(EnumDyeColor.WHITE));
      this.beamSegments.add(beamSegment);
      boolean flag = true;
      MutableBlockPos mutableBlockPos = new MutableBlockPos();

      for (int i = n2 + 1; i < 256; i++) {
         IBlockState blockState = this.worldObj.getBlockState(mutableBlockPos.set(n, i, n3));
         float[] floats;
         if (blockState.getBlock() == Blocks.stained_glass) {
            floats = EntitySheep.getDyeRgb(blockState.getValue(BlockStainedGlass.COLOR));
         } else {
            if (blockState.getBlock() != Blocks.stained_glass_pane) {
               if (blockState.getBlock().getLightOpacity() >= 15 && blockState.getBlock() != Blocks.bedrock) {
                  this.isComplete = false;
                  this.beamSegments.clear();
                  break;
               }

               beamSegment.incrementHeight();
               continue;
            }

            floats = EntitySheep.getDyeRgb(blockState.getValue(BlockStainedGlassPane.COLOR));
         }

         if (!flag) {
            floats = new float[]{(beamSegment.getColors()[0] + floats[0]) / 2.0F, (beamSegment.getColors()[1] + floats[1]) / 2.0F, (beamSegment.getColors()[2] + floats[2]) / 2.0F};
         }

         if (Arrays.equals(floats, beamSegment.getColors())) {
            beamSegment.incrementHeight();
         } else {
            beamSegment = new TileEntityBeacon.BeamSegment(floats);
            this.beamSegments.add(beamSegment);
         }

         flag = false;
      }

      if (this.isComplete) {
         for (int i = 1; i <= 4; this.levels = i++) {
            int n4 = n2 - i;
            if (n4 < 0) {
               break;
            }

            boolean flag2 = true;

            for (int j = n - i; j <= n + i && flag2; j++) {
               for (int k = n3 - i; k <= n3 + i; k++) {
                  Block block = this.worldObj.getBlockState(new BlockPos(j, n4, k)).getBlock();
                  if (block != Blocks.emerald_block && block != Blocks.gold_block && block != Blocks.diamond_block && block != Blocks.iron_block) {
                     flag2 = false;
                     break;
                  }
               }
            }

            if (!flag2) {
               break;
            }
         }

         if (this.levels == 0) {
            this.isComplete = false;
         }
      }

      if (!this.worldObj.isRemote && this.levels == 4 && levels < this.levels) {
         for (EntityPlayer entityPlayer : this.worldObj
            .getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(n, n2, n3, n, n2 - 4, n3).expand(10.0, 5.0, 10.0))) {
            entityPlayer.triggerAchievement(AchievementList.fullBeacon);
         }
      }
   }

   public List<TileEntityBeacon.BeamSegment> getBeamSegments() {
      return this.beamSegments;
   }

   public float shouldBeamRender() {
      if (!this.isComplete) {
         return 0.0F;
      }

      int n = (int)(this.worldObj.getTotalWorldTime() - this.beamRenderCounter);
      this.beamRenderCounter = this.worldObj.getTotalWorldTime();
      if (n > 1) {
         this.beamSpeed -= n / 40.0F;
         if (this.beamSpeed < 0.0F) {
            this.beamSpeed = 0.0F;
         }
      }

      this.beamSpeed += 0.025F;
      if (this.beamSpeed > 1.0F) {
         this.beamSpeed = 1.0F;
      }

      return this.beamSpeed;
   }

   @Override
   public Packet getDescriptionPacket() {
      NBTTagCompound nbttagCompound = new NBTTagCompound();
      this.writeToNBT(nbttagCompound);
      return new S35PacketUpdateTileEntity(this.pos, 3, nbttagCompound);
   }

   @Override
   public double getMaxRenderDistanceSquared() {
      return 65536.0;
   }

   public int getFixedEffectId(int n) {
      if (n >= 0 && n < Potion.potionTypes.length && Potion.potionTypes[n] != null) {
         Potion potion = Potion.potionTypes[n];
         return potion != Potion.moveSpeed
               && potion != Potion.digSpeed
               && potion != Potion.resistance
               && potion != Potion.jump
               && potion != Potion.damageBoost
               && potion != Potion.regeneration
            ? 0
            : n;
      } else {
         return 0;
      }
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.primaryEffect = this.getFixedEffectId(compound.getInteger("Primary"));
      this.secondaryEffect = this.getFixedEffectId(compound.getInteger("Secondary"));
      this.levels = compound.getInteger("Levels");
   }

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      compound.setInteger("Primary", this.primaryEffect);
      compound.setInteger("Secondary", this.secondaryEffect);
      compound.setInteger("Levels", this.levels);
   }

   @Override
   public int getSizeInventory() {
      return 1;
   }

   @Override
   public ItemStack getStackInSlot(int index) {
      return index == 0 ? this.payment : null;
   }

   @Override
   public ItemStack decrStackSize(int index, int count) {
      if (index != 0 || this.payment == null) {
         return null;
      } else if (count >= this.payment.stackSize) {
         ItemStack payment = this.payment;
         this.payment = null;
         return payment;
      } else {
         this.payment.stackSize -= count;
         return new ItemStack(this.payment.getItem(), count, this.payment.getMetadata());
      }
   }

   @Override
   public ItemStack removeStackFromSlot(int index) {
      if (index == 0 && this.payment != null) {
         ItemStack payment = this.payment;
         this.payment = null;
         return payment;
      } else {
         return null;
      }
   }

   @Override
   public void setInventorySlotContents(int index, ItemStack stack) {
      if (index == 0) {
         this.payment = stack;
      }
   }

   @Override
   public String getName() {
      return this.hasCustomName() ? this.customName : "container.beacon";
   }

   @Override
   public boolean hasCustomName() {
      return this.customName != null && this.customName.length() > 0;
   }

   public void setName(String name) {
      this.customName = name;
   }

   @Override
   public int getInventoryStackLimit() {
      return 1;
   }

   @Override
   public boolean isUseableByPlayer(EntityPlayer player) {
      return this.worldObj.getTileEntity(this.pos) != this
         ? false
         : player.getDistanceSq(this.pos.getX() + 0.5, this.pos.getY() + 0.5, this.pos.getZ() + 0.5) <= 64.0;
   }

   @Override
   public void openInventory(EntityPlayer player) {
   }

   @Override
   public void closeInventory(EntityPlayer player) {
   }

   @Override
   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return stack.getItem() == Items.emerald || stack.getItem() == Items.diamond || stack.getItem() == Items.gold_ingot || stack.getItem() == Items.iron_ingot;
   }

   @Override
   public String getGuiID() {
      return "minecraft:beacon";
   }

   @Override
   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      return new ContainerBeacon(playerInventory, this);
   }

   @Override
   public int getField(int id) {
      switch (id) {
         case 0:
            return this.levels;
         case 1:
            return this.primaryEffect;
         case 2:
            return this.secondaryEffect;
         default:
            return 0;
      }
   }

   @Override
   public void setField(int id, int value) {
      switch (id) {
         case 0:
            this.levels = value;
            break;
         case 1:
            this.primaryEffect = this.getFixedEffectId(value);
            break;
         case 2:
            this.secondaryEffect = this.getFixedEffectId(value);
      }
   }

   @Override
   public int getFieldCount() {
      return 3;
   }

   @Override
   public void clear() {
      this.payment = null;
   }

   @Override
   public boolean receiveClientEvent(int id, int type) {
      if (id == 1) {
         this.updateBeacon();
         return true;
      } else {
         return super.receiveClientEvent(id, type);
      }
   }

   public static class BeamSegment {
      public float[] colors;
      public int height;

      public BeamSegment(float[] floats) {
         this.colors = floats;
         this.height = 1;
      }

      public void incrementHeight() {
         this.height++;
      }

      public float[] getColors() {
         return this.colors;
      }

      public int getHeight() {
         return this.height;
      }
   }
}
