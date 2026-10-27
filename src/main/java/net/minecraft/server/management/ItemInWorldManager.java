package net.minecraft.server.management;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S38PacketPlayerListItem.Action;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings.GameType;

public class ItemInWorldManager {
   public GameType gameType = GameType.NOT_SET;
   public EntityPlayerMP thisPlayerMP;
   public BlockPos blockPos = BlockPos.ORIGIN;
   public boolean isDestroyingBlock;
   public int initialDamage;
   public BlockPos miningPos = BlockPos.ORIGIN;
   public int curblockDamage;
   public boolean receivedFinishDiggingPacket;
   public int durabilityRemainingOnBlock = -1;
   public int initialBlockDamage;
   public World theWorld;

   public ItemInWorldManager(World worldIn) {
      this.theWorld = worldIn;
   }

   public void setGameType(GameType type) {
      this.gameType = type;
      type.configurePlayerCapabilities(this.thisPlayerMP.capabilities);
      this.thisPlayerMP.sendPlayerAbilities();
      this.thisPlayerMP
         .mcServer
         .getConfigurationManager()
         .sendPacketToAllPlayers(new S38PacketPlayerListItem(Action.UPDATE_GAME_MODE, this.thisPlayerMP));
   }

   public GameType getGameType() {
      return this.gameType;
   }

   public boolean survivalOrAdventure() {
      return this.gameType.isSurvivalOrAdventure();
   }

   public boolean isCreative() {
      return this.gameType.isCreative();
   }

   public void initializeGameType(GameType type) {
      if (this.gameType == GameType.NOT_SET) {
         this.gameType = type;
      }

      this.setGameType(this.gameType);
   }

   public void updateBlockRemoving() {
      this.curblockDamage++;
      if (this.receivedFinishDiggingPacket) {
         int n = this.curblockDamage - this.initialBlockDamage;
         Block block = this.theWorld.getBlockState(this.miningPos).getBlock();
         if (block.getMaterial() == Material.air) {
            this.receivedFinishDiggingPacket = false;
         } else {
            float f = block.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, this.miningPos) * (n + 1);
            int n2 = (int)(f * 10.0F);
            if (n2 != this.durabilityRemainingOnBlock) {
               this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), this.miningPos, n2);
               this.durabilityRemainingOnBlock = n2;
            }

            if (f >= 1.0F) {
               this.receivedFinishDiggingPacket = false;
               this.tryHarvestBlock(this.miningPos);
            }
         }
      } else if (this.isDestroyingBlock) {
         Block block = this.theWorld.getBlockState(this.blockPos).getBlock();
         if (block.getMaterial() == Material.air) {
            this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), this.blockPos, -1);
            this.durabilityRemainingOnBlock = -1;
            this.isDestroyingBlock = false;
         } else {
            int n = this.curblockDamage - this.initialDamage;
            float f = block.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, this.miningPos) * (n + 1);
            int n2 = (int)(f * 10.0F);
            if (n2 != this.durabilityRemainingOnBlock) {
               this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), this.blockPos, n2);
               this.durabilityRemainingOnBlock = n2;
            }
         }
      }
   }

   public void onBlockClicked(BlockPos pos, EnumFacing side) {
      if (this.isCreative()) {
         if (!this.theWorld.extinguishFire((EntityPlayer)null, pos, side)) {
            this.tryHarvestBlock(pos);
         }
      } else {
         Block block = this.theWorld.getBlockState(pos).getBlock();
         if (this.gameType.isAdventure()) {
            if (this.gameType == GameType.SPECTATOR) {
               return;
            }

            if (!this.thisPlayerMP.isAllowEdit()) {
               ItemStack currentEquippedItem = this.thisPlayerMP.getCurrentEquippedItem();
               if (currentEquippedItem == null) {
                  return;
               }

               if (!currentEquippedItem.canDestroy(block)) {
                  return;
               }
            }
         }

         this.theWorld.extinguishFire((EntityPlayer)null, pos, side);
         this.initialDamage = this.curblockDamage;
         float f = 1.0F;
         if (block.getMaterial() != Material.air) {
            block.onBlockClicked(this.theWorld, pos, this.thisPlayerMP);
            f = block.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, pos);
         }

         if (block.getMaterial() != Material.air && f >= 1.0F) {
            this.tryHarvestBlock(pos);
         } else {
            this.isDestroyingBlock = true;
            this.blockPos = pos;
            int n = (int)(f * 10.0F);
            this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), pos, n);
            this.durabilityRemainingOnBlock = n;
         }
      }
   }

   public void blockRemoving(BlockPos pos) {
      if (pos.equals(this.blockPos)) {
         int n = this.curblockDamage - this.initialDamage;
         Block block = this.theWorld.getBlockState(pos).getBlock();
         if (block.getMaterial() != Material.air) {
            float f = block.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, pos) * (n + 1);
            if (f >= 0.7F) {
               this.isDestroyingBlock = false;
               this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), pos, -1);
               this.tryHarvestBlock(pos);
            } else if (!this.receivedFinishDiggingPacket) {
               this.isDestroyingBlock = false;
               this.receivedFinishDiggingPacket = true;
               this.miningPos = pos;
               this.initialBlockDamage = this.initialDamage;
            }
         }
      }
   }

   public void cancelDestroyingBlock() {
      this.isDestroyingBlock = false;
      this.theWorld.sendBlockBreakProgress(this.thisPlayerMP.getEntityId(), this.blockPos, -1);
   }

   public boolean removeBlock(BlockPos pos) {
      IBlockState blockState = this.theWorld.getBlockState(pos);
      blockState.getBlock().onBlockHarvested(this.theWorld, pos, blockState, this.thisPlayerMP);
      boolean setBlockToAir = this.theWorld.setBlockToAir(pos);
      if (setBlockToAir) {
         blockState.getBlock().onBlockDestroyedByPlayer(this.theWorld, pos, blockState);
      }

      return setBlockToAir;
   }

   public boolean tryHarvestBlock(BlockPos pos) {
      if (this.gameType.isCreative() && this.thisPlayerMP.getHeldItem() != null && this.thisPlayerMP.getHeldItem().getItem() instanceof ItemSword) {
         return false;
      }

      IBlockState blockState = this.theWorld.getBlockState(pos);
      TileEntity tileEntity = this.theWorld.getTileEntity(pos);
      if (this.gameType.isAdventure()) {
         if (this.gameType == GameType.SPECTATOR) {
            return false;
         }

         if (!this.thisPlayerMP.isAllowEdit()) {
            ItemStack currentEquippedItem = this.thisPlayerMP.getCurrentEquippedItem();
            if (currentEquippedItem == null) {
               return false;
            }

            if (!currentEquippedItem.canDestroy(blockState.getBlock())) {
               return false;
            }
         }
      }

      this.theWorld.playAuxSFXAtEntity(this.thisPlayerMP, 2001, pos, Block.getStateId(blockState));
      boolean removeBlock = this.removeBlock(pos);
      if (this.isCreative()) {
         this.thisPlayerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(this.theWorld, pos));
      } else {
         ItemStack currentEquippedItem = this.thisPlayerMP.getCurrentEquippedItem();
         boolean canHarvestBlock = this.thisPlayerMP.canHarvestBlock(blockState.getBlock());
         if (currentEquippedItem != null) {
            currentEquippedItem.onBlockDestroyed(this.theWorld, blockState.getBlock(), pos, this.thisPlayerMP);
            if (currentEquippedItem.stackSize == 0) {
               this.thisPlayerMP.destroyCurrentEquippedItem();
            }
         }

         if (removeBlock && canHarvestBlock) {
            blockState.getBlock().harvestBlock(this.theWorld, this.thisPlayerMP, pos, blockState, tileEntity);
         }
      }

      return removeBlock;
   }

   public boolean tryUseItem(EntityPlayer player, World worldIn, ItemStack stack) {
      if (this.gameType == GameType.SPECTATOR) {
         return false;
      }

      int stackSize = stack.stackSize;
      int metadata = stack.getMetadata();
      ItemStack useItemRightClick = stack.useItemRightClick(worldIn, player);
      if (useItemRightClick != stack || useItemRightClick != null && (useItemRightClick.stackSize != stackSize || useItemRightClick.getMaxItemUseDuration() > 0 || useItemRightClick.getMetadata() != metadata)) {
         player.inventory.mainInventory[player.inventory.currentItem] = useItemRightClick;
         if (this.isCreative()) {
            useItemRightClick.stackSize = stackSize;
            if (useItemRightClick.isItemStackDamageable()) {
               useItemRightClick.setItemDamage(metadata);
            }
         }

         if (useItemRightClick.stackSize == 0) {
            player.inventory.mainInventory[player.inventory.currentItem] = null;
         }

         if (!player.isUsingItem()) {
            ((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean activateBlockOrUseItem(
      EntityPlayer player, World worldIn, ItemStack stack, BlockPos pos, EnumFacing side, float offsetX, float offsetY, float offsetZ
   ) {
      if (this.gameType == GameType.SPECTATOR) {
         TileEntity tileEntity = worldIn.getTileEntity(pos);
         if (tileEntity instanceof ILockableContainer) {
            Block block = worldIn.getBlockState(pos).getBlock();
            ILockableContainer lockableContainer = (ILockableContainer)tileEntity;
            if (lockableContainer instanceof TileEntityChest && block instanceof BlockChest) {
               lockableContainer = ((BlockChest)block).getLockableContainer(worldIn, pos);
            }

            if (lockableContainer != null) {
               player.displayGUIChest(lockableContainer);
               return true;
            }
         } else if (tileEntity instanceof IInventory) {
            player.displayGUIChest((IInventory)tileEntity);
            return true;
         }

         return false;
      } else {
         if (!player.isSneaking() || player.getHeldItem() == null) {
            IBlockState blockState = worldIn.getBlockState(pos);
            if (blockState.getBlock().onBlockActivated(worldIn, pos, blockState, player, side, offsetX, offsetY, offsetZ)) {
               return true;
            }
         }

         if (stack == null) {
            return false;
         } else if (this.isCreative()) {
            int metadata = stack.getMetadata();
            int stackSize = stack.stackSize;
            boolean onItemUse = stack.onItemUse(player, worldIn, pos, side, offsetX, offsetY, offsetZ);
            stack.setItemDamage(metadata);
            stack.stackSize = stackSize;
            return onItemUse;
         } else {
            return stack.onItemUse(player, worldIn, pos, side, offsetX, offsetY, offsetZ);
         }
      }
   }

   public void setWorld(WorldServer serverWorld) {
      this.theWorld = serverWorld;
   }
}
