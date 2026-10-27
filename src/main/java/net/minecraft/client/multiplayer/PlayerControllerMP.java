package net.minecraft.client.multiplayer;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.EntityEntityPlayerEvent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.C02PacketUseEntity;

import net.minecraft.network.play.client.C07PacketPlayerDigging;

import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings.GameType;

public class PlayerControllerMP {
   public BlockPos currentBlock = new BlockPos(-1, -1, -1);
   public GameType currentGameType = GameType.SURVIVAL;
   public Minecraft mc;
   public ItemStack currentItemHittingBlock;
   public float curBlockDamageMP;
   public float stepSoundTickCounter;
   public int blockHitDelay;
   public boolean isHittingBlock;
   public NetHandlerPlayClient netClientHandler;
   public int currentPlayerItem;

   public PlayerControllerMP(Minecraft mcIn, NetHandlerPlayClient netHandler) {
      this.mc = mcIn;
      this.netClientHandler = netHandler;
   }

   public static void clickBlockCreative(Minecraft mcIn, PlayerControllerMP playerController, BlockPos pos, EnumFacing facing) {
      if (!mcIn.theWorld.extinguishFire(mcIn.thePlayer, pos, facing)) {
         playerController.onPlayerDestroyBlock(pos, facing);
      }
   }

   public void setPlayerCapabilities(EntityPlayer player) {
      this.currentGameType.configurePlayerCapabilities(player.capabilities);
   }

   public boolean isSpectator() {
      return this.currentGameType == GameType.SPECTATOR;
   }

   public void setGameType(GameType type) {
      this.currentGameType = type;
      this.currentGameType.configurePlayerCapabilities(this.mc.thePlayer.capabilities);
   }

   public void flipPlayer(EntityPlayer playerIn) {
      playerIn.rotationYaw = -180.0F;
   }

   public boolean shouldDrawHUD() {
      return this.currentGameType.isSurvivalOrAdventure();
   }

   public boolean onPlayerDestroyBlock(BlockPos pos, EnumFacing side) {
      if (this.currentGameType.isAdventure()) {
         if (this.currentGameType == GameType.SPECTATOR) {
            return false;
         }

         if (!this.mc.thePlayer.isAllowEdit()) {
            Block block = this.mc.theWorld.getBlockState(pos).getBlock();
            ItemStack currentEquippedItem = this.mc.thePlayer.getCurrentEquippedItem();
            if (currentEquippedItem == null) {
               return false;
            }

            if (!currentEquippedItem.canDestroy(block)) {
               return false;
            }
         }
      }

      if (this.currentGameType.isCreative() && this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
         return false;
      }

      WorldClient theWorld = this.mc.theWorld;
      IBlockState blockState = theWorld.getBlockState(pos);
      Block block = blockState.getBlock();
      if (block.getMaterial() == Material.air) {
         return false;
      }

      theWorld.playAuxSFX(2001, pos, Block.getStateId(blockState));
      boolean setBlockToAir = theWorld.setBlockToAir(pos);
      if (setBlockToAir) {
         block.onBlockDestroyedByPlayer(theWorld, pos, blockState);
      }

      this.currentBlock = new BlockPos(this.currentBlock.getX(), -1, this.currentBlock.getZ());
      if (!this.currentGameType.isCreative()) {
         ItemStack currentEquippedItem = this.mc.thePlayer.getCurrentEquippedItem();
         if (currentEquippedItem != null) {
            currentEquippedItem.onBlockDestroyed(theWorld, block, pos, this.mc.thePlayer);
            if (currentEquippedItem.stackSize == 0) {
               this.mc.thePlayer.destroyCurrentEquippedItem();
            }
         }
      }

      return setBlockToAir;
   }

   public boolean clickBlock(BlockPos loc, EnumFacing face) {
      if (this.currentGameType.isAdventure()) {
         if (this.currentGameType == GameType.SPECTATOR) {
            return false;
         }

         if (!this.mc.thePlayer.isAllowEdit()) {
            Block block = this.mc.theWorld.getBlockState(loc).getBlock();
            ItemStack currentEquippedItem = this.mc.thePlayer.getCurrentEquippedItem();
            if (currentEquippedItem == null) {
               return false;
            }

            if (!currentEquippedItem.canDestroy(block)) {
               return false;
            }
         }
      }

      if (!this.mc.theWorld.getWorldBorder().contains(loc)) {
         return false;
      }

      if (this.currentGameType.isCreative()) {
         this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, loc, face));
         clickBlockCreative(this.mc, this, loc, face);
         this.blockHitDelay = 5;
      } else if (!this.isHittingBlock || !this.isHittingPosition(loc)) {
         if (this.isHittingBlock) {
            this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.currentBlock, face));
         }

         this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, loc, face));
         Block block = this.mc.theWorld.getBlockState(loc).getBlock();
         boolean flag = block.getMaterial() != Material.air;
         if (flag && this.curBlockDamageMP == 0.0F) {
            block.onBlockClicked(this.mc.theWorld, loc, this.mc.thePlayer);
         }

         if (flag && block.getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.thePlayer.worldObj, loc) >= 1.0F) {
            this.onPlayerDestroyBlock(loc, face);
         } else {
            this.isHittingBlock = true;
            this.currentBlock = loc;
            this.currentItemHittingBlock = this.mc.thePlayer.getHeldItem();
            this.curBlockDamageMP = 0.0F;
            this.stepSoundTickCounter = 0.0F;
            this.mc.theWorld.sendBlockBreakProgress(this.mc.thePlayer.getEntityId(), this.currentBlock, (int)(this.curBlockDamageMP * 10.0F) - 1);
         }
      }

      return true;
   }

   public void resetBlockRemoving() {
      if (this.isHittingBlock) {
         this.netClientHandler
            .addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK, this.currentBlock, EnumFacing.DOWN));
         this.isHittingBlock = false;
         this.curBlockDamageMP = 0.0F;
         this.mc.theWorld.sendBlockBreakProgress(this.mc.thePlayer.getEntityId(), this.currentBlock, -1);
      }
   }

   public boolean onPlayerDamageBlock(BlockPos posBlock, EnumFacing directionFacing) {
      this.syncCurrentPlayItem();
      if (this.blockHitDelay > 0) {
         this.blockHitDelay--;
         return true;
      }

      if (this.currentGameType.isCreative() && this.mc.theWorld.getWorldBorder().contains(posBlock)) {
         this.blockHitDelay = 5;
         this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, posBlock, directionFacing));
         clickBlockCreative(this.mc, this, posBlock, directionFacing);
         return true;
      }

      if (this.isHittingPosition(posBlock)) {
         Block block = this.mc.theWorld.getBlockState(posBlock).getBlock();
         if (block.getMaterial() == Material.air) {
            this.isHittingBlock = false;
            return false;
         }

         this.curBlockDamageMP = this.curBlockDamageMP + block.getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.thePlayer.worldObj, posBlock);
         if (this.stepSoundTickCounter % 4.0F == 0.0F) {
            this.mc
               .getSoundHandler()
               .playSound(
                  new PositionedSoundRecord(
                     new ResourceLocation(block.stepSound.getStepSound()),
                     (block.stepSound.getVolume() + 1.0F) / 8.0F,
                     block.stepSound.getFrequency() * 0.5F,
                     posBlock.getX() + 0.5F,
                     posBlock.getY() + 0.5F,
                     posBlock.getZ() + 0.5F
                  )
               );
         }

         this.stepSoundTickCounter++;
         if (this.curBlockDamageMP >= 1.0F) {
            this.isHittingBlock = false;
            this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, posBlock, directionFacing));
            this.onPlayerDestroyBlock(posBlock, directionFacing);
            this.curBlockDamageMP = 0.0F;
            this.stepSoundTickCounter = 0.0F;
            this.blockHitDelay = 5;
         }

         this.mc.theWorld.sendBlockBreakProgress(this.mc.thePlayer.getEntityId(), this.currentBlock, (int)(this.curBlockDamageMP * 10.0F) - 1);
         return true;
      } else {
         return this.clickBlock(posBlock, directionFacing);
      }
   }

   public float getBlockReachDistance() {
      return this.currentGameType.isCreative() ? 5.0F : 4.5F;
   }

   public void updateController() {
      this.syncCurrentPlayItem();
      if (this.netClientHandler.getNetworkManager().isChannelOpen()) {
         this.netClientHandler.getNetworkManager().processReceivedPackets();
      } else {
         this.netClientHandler.getNetworkManager().checkDisconnected();
      }
   }

   public boolean isHittingPosition(BlockPos pos) {
      ItemStack heldItem = this.mc.thePlayer.getHeldItem();
      boolean flag = this.currentItemHittingBlock == null && heldItem == null;
      if (this.currentItemHittingBlock != null && heldItem != null) {
         flag = heldItem.getItem() == this.currentItemHittingBlock.getItem()
            && ItemStack.areItemStackTagsEqual(heldItem, this.currentItemHittingBlock)
            && (heldItem.isItemStackDamageable() || heldItem.getMetadata() == this.currentItemHittingBlock.getMetadata());
      }

      return pos.equals(this.currentBlock) && flag;
   }

   public void syncCurrentPlayItem() {
      int currentItem = this.mc.thePlayer.inventory.currentItem;
      if (currentItem != this.currentPlayerItem) {
         this.currentPlayerItem = currentItem;
         this.netClientHandler.addToSendQueue(new C09PacketHeldItemChange(this.currentPlayerItem));
      }
   }

   public boolean onPlayerRightClick(EntityPlayerSP player, WorldClient worldIn, ItemStack heldStack, BlockPos hitPos, EnumFacing side, Vec3 hitVec) {
      this.syncCurrentPlayItem();
      float f = (float)(hitVec.xCoord - hitPos.getX());
      float f2 = (float)(hitVec.yCoord - hitPos.getY());
      float f3 = (float)(hitVec.zCoord - hitPos.getZ());
      boolean flag = false;
      if (!this.mc.theWorld.getWorldBorder().contains(hitPos)) {
         return false;
      }

      if (this.currentGameType != GameType.SPECTATOR) {
         IBlockState blockState = worldIn.getBlockState(hitPos);
         if ((!player.isSneaking() || player.getHeldItem() == null)
            && blockState.getBlock().onBlockActivated(worldIn, hitPos, blockState, player, side, f, f2, f3)) {
            flag = true;
         }

         if (!flag && heldStack != null && heldStack.getItem() instanceof ItemBlock) {
            ItemBlock item = (ItemBlock)heldStack.getItem();
            if (!item.canPlaceBlockOnSide(worldIn, hitPos, side, player, heldStack)) {
               return false;
            }
         }
      }

      this.netClientHandler.addToSendQueue(new C08PacketPlayerBlockPlacement(hitPos, side.getIndex(), player.inventory.getCurrentItem(), f, f2, f3));
      if (flag || this.currentGameType == GameType.SPECTATOR) {
         return true;
      } else if (heldStack == null) {
         return false;
      } else if (this.currentGameType.isCreative()) {
         int metadata = heldStack.getMetadata();
         int stackSize = heldStack.stackSize;
         boolean onItemUse = heldStack.onItemUse(player, worldIn, hitPos, side, f, f2, f3);
         heldStack.setItemDamage(metadata);
         heldStack.stackSize = stackSize;
         return onItemUse;
      } else {
         return CheatBreaker.getInstance().getKeybindSettings().ghostLiquidFix.getBooleanValue() && heldStack.getItem() instanceof ItemBucket
            ? this.checkEntityPlayerSP(player, worldIn, heldStack, hitPos, side, hitVec)
            : heldStack.onItemUse(player, worldIn, hitPos, side, f, f2, f3);
      }
   }

   public boolean sendUseItem(EntityPlayer playerIn, World worldIn, ItemStack itemStackIn) {
      if (this.currentGameType == GameType.SPECTATOR) {
         return false;
      }

      this.syncCurrentPlayItem();
      this.netClientHandler.addToSendQueue(new C08PacketPlayerBlockPlacement(playerIn.inventory.getCurrentItem()));
      int stackSize = itemStackIn.stackSize;
      ItemStack useItemRightClick = itemStackIn.useItemRightClick(worldIn, playerIn);
      if (useItemRightClick != itemStackIn || useItemRightClick != null && useItemRightClick.stackSize != stackSize) {
         playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = useItemRightClick;
         if (useItemRightClick.stackSize == 0) {
            playerIn.inventory.mainInventory[playerIn.inventory.currentItem] = null;
         }

         return true;
      } else {
         return false;
      }
   }

   public EntityPlayerSP createPlayer(World worldIn, StatFileWriter statWriter) {
      return new EntityPlayerSP(this.mc, worldIn, this.netClientHandler, statWriter);
   }

   public void attackEntity(EntityPlayer playerIn, Entity targetEntity) {
      CheatBreaker.getInstance().getEventBus().handleEvent(new EntityEntityPlayerEvent(playerIn, targetEntity));
      this.syncCurrentPlayItem();
      this.netClientHandler.addToSendQueue(new C02PacketUseEntity(targetEntity, C02PacketUseEntity.Action.ATTACK));
      if (this.currentGameType != GameType.SPECTATOR) {
         playerIn.attackTargetEntityWithCurrentItem(targetEntity);
      }
   }

   public boolean interactWithEntitySendPacket(EntityPlayer playerIn, Entity targetEntity) {
      this.syncCurrentPlayItem();
      this.netClientHandler.addToSendQueue(new C02PacketUseEntity(targetEntity, C02PacketUseEntity.Action.INTERACT));
      return this.currentGameType != GameType.SPECTATOR && playerIn.interactWith(targetEntity);
   }

   public boolean isPlayerRightClickingOnEntity(EntityPlayer player, Entity entityIn, MovingObjectPosition movingObject) {
      this.syncCurrentPlayItem();
      Vec3 vec3 = new Vec3(movingObject.hitVec.xCoord - entityIn.posX, movingObject.hitVec.yCoord - entityIn.posY, movingObject.hitVec.zCoord - entityIn.posZ);
      this.netClientHandler.addToSendQueue(new C02PacketUseEntity(entityIn, vec3));
      return this.currentGameType != GameType.SPECTATOR && entityIn.interactAt(player, vec3);
   }

   public ItemStack windowClick(int windowId, int slotId, int mouseButtonClicked, int mode, EntityPlayer playerIn) {
      short nextTransactionID = playerIn.openContainer.getNextTransactionID(playerIn.inventory);
      ItemStack slotClick = playerIn.openContainer.slotClick(slotId, mouseButtonClicked, mode, playerIn);
      this.netClientHandler.addToSendQueue(new C0EPacketClickWindow(windowId, slotId, mouseButtonClicked, mode, slotClick, nextTransactionID));
      return slotClick;
   }

   public void sendEnchantPacket(int windowID, int button) {
      this.netClientHandler.addToSendQueue(new C11PacketEnchantItem(windowID, button));
   }

   public void sendSlotPacket(ItemStack itemStackIn, int slotId) {
      if (this.currentGameType.isCreative()) {
         this.netClientHandler.addToSendQueue(new C10PacketCreativeInventoryAction(slotId, itemStackIn));
      }
   }

   public void sendPacketDropItem(ItemStack itemStackIn) {
      if (this.currentGameType.isCreative() && itemStackIn != null) {
         this.netClientHandler.addToSendQueue(new C10PacketCreativeInventoryAction(-1, itemStackIn));
      }
   }

   public void onStoppedUsingItem(EntityPlayer playerIn) {
      this.syncCurrentPlayItem();
      this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, EnumFacing.DOWN));
      playerIn.stopUsingItem();
   }

   public boolean gameIsSurvivalOrAdventure() {
      return this.currentGameType.isSurvivalOrAdventure();
   }

   public boolean isNotCreative() {
      return !this.currentGameType.isCreative();
   }

   public boolean isInCreativeMode() {
      return this.currentGameType.isCreative();
   }

   public boolean extendedReach() {
      return this.currentGameType.isCreative();
   }

   public boolean isRidingHorse() {
      return this.mc.thePlayer.isRiding() && this.mc.thePlayer.ridingEntity instanceof EntityHorse;
   }

   public boolean isSpectatorMode() {
      return this.currentGameType == GameType.SPECTATOR;
   }

   public GameType getCurrentGameType() {
      return this.currentGameType;
   }

   public boolean getIsHittingBlock() {
      return this.isHittingBlock;
   }

   public boolean checkEntityPlayerSP(EntityPlayerSP entityPlayerSP, WorldClient worldClient, ItemStack itemStack, BlockPos blockPos, EnumFacing enumFacing, Vec3 vec3) {
      float f = (float)(vec3.xCoord - blockPos.getX());
      float f2 = (float)(vec3.yCoord - blockPos.getY());
      float f3 = (float)(vec3.zCoord - blockPos.getZ());
      C08PacketPlayerBlockPlacement c08PacketPlayerBlockPlacement = new C08PacketPlayerBlockPlacement(blockPos, enumFacing.getIndex(), entityPlayerSP.inventory.getCurrentItem(), f, f2, f3);
      if (itemStack.getUnlocalizedName().equals("item.bucketLava")) {
         worldClient.setBlockState(
            new BlockPos(c08PacketPlayerBlockPlacement.getPlacedBlockOffsetX(), c08PacketPlayerBlockPlacement.getPlacedBlockOffsetY(), c08PacketPlayerBlockPlacement.getPlacedBlockOffsetZ()), Blocks.flowing_lava.getDefaultState()
         );
      } else if (itemStack.getUnlocalizedName().equals("item.bucketWater")) {
         worldClient.setBlockState(
            new BlockPos(c08PacketPlayerBlockPlacement.getPlacedBlockOffsetX(), c08PacketPlayerBlockPlacement.getPlacedBlockOffsetY(), c08PacketPlayerBlockPlacement.getPlacedBlockOffsetZ()), Blocks.flowing_water.getDefaultState()
         );
      }

      return false;
   }

   public void setIsHittingBlock(boolean flag) {
      this.isHittingBlock = flag;
   }
}
