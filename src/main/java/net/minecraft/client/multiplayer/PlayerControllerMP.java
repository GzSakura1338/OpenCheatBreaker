package net.minecraft.client.multiplayer;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.EntityEntityPlayerEvent;
import com.cheatbreaker.client.event.StopUsingItemEvent;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityClientPlayerMP;
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
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings.GameType;

public class PlayerControllerMP {
    public int currentBlockX = -1;
    public int currentBlockY = -1;
    public int currentblockZ = -1;
    public GameType currentGameType = GameType.SURVIVAL;
    public Minecraft mc;
    public ItemStack currentItemHittingBlock;
    public float curBlockDamageMP;
    public float stepSoundTickCounter;
    public int blockHitDelay;
    public boolean isHittingBlock;
    public NetHandlerPlayClient netClientHandler;
    public int currentPlayerItem;

    public PlayerControllerMP(Minecraft mc, NetHandlerPlayClient netHandlerPlayClient) {
        this.mc = mc;
        this.netClientHandler = netHandlerPlayClient;
    }

    public static void clickBlockCreative(Minecraft minecraftIn, PlayerControllerMP playerController, int x, int y, int z, int side) {
        if (!minecraftIn.theWorld.extinguishFire(minecraftIn.thePlayer, x, y, z, side)) {
            playerController.onPlayerDestroyBlock(x, y, z, side);
        }
    }

    public void setPlayerCapabilities(EntityPlayer entityPlayer) {
        this.currentGameType.configurePlayerCapabilities(entityPlayer.capabilities);
    }

    public boolean enableEverythingIsScrewedUpMode() {
        return false;
    }

    public void setGameType(GameType gameType) {
        this.currentGameType = gameType;
        this.currentGameType.configurePlayerCapabilities(this.mc.thePlayer.capabilities);
    }

    public void flipPlayer(EntityPlayer player) {
        player.rotationYaw = -180.0F;
    }

    public boolean shouldDrawHUD() {
        return this.currentGameType.isSurvivalOrAdventure();
    }

    public boolean onPlayerDestroyBlock(int x, int y, int z, int side) {
        if (this.currentGameType.isAdventure() && !this.mc.thePlayer.isCurrentToolAdventureModeExempt(x, y, z)) {
            return false;
        }

        if (this.currentGameType.isCreative() && this.mc.thePlayer.getHeldItem() != null && this.mc.thePlayer.getHeldItem().getItem() instanceof ItemSword) {
            return false;
        }

        WorldClient theWorld = this.mc.theWorld;
        Block block = theWorld.getBlock(x, y, z);
        if (block.getMaterial() == Material.air) {
            return false;
        }

        theWorld.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (theWorld.getBlockMetadata(x, y, z) << 12));
        int blockMetadata = theWorld.getBlockMetadata(x, y, z);
        boolean setBlockToAir = theWorld.setBlockToAir(x, y, z);
        if (setBlockToAir) {
            block.onBlockDestroyedByPlayer(theWorld, x, y, z, blockMetadata);
        }

        this.currentBlockY = -1;
        if (!this.currentGameType.isCreative()) {
            ItemStack currentEquippedItem = this.mc.thePlayer.getCurrentEquippedItem();
            if (currentEquippedItem != null) {
                currentEquippedItem.onBlockDestroyed(theWorld, block, x, y, z, this.mc.thePlayer);
                if (currentEquippedItem.stackSize == 0) {
                    this.mc.thePlayer.destroyCurrentEquippedItem();
                }
            }
        }

        return setBlockToAir;
    }

    public void clickBlock(int x, int y, int z, int side) {
        if (!this.currentGameType.isAdventure() || this.mc.thePlayer.isCurrentToolAdventureModeExempt(x, y, z)) {
            if (this.currentGameType.isCreative()) {
                this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(0, x, y, z, side));
                clickBlockCreative(this.mc, this, x, y, z, side);
                this.blockHitDelay = 5;
            } else if (!this.isHittingBlock || !this.sameToolAndBlock(x, y, z)) {
                if (this.isHittingBlock) {
                    this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(1, this.currentBlockX, this.currentBlockY, this.currentblockZ, side));
                }

                this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(0, x, y, z, side));
                Block block = this.mc.theWorld.getBlock(x, y, z);
                boolean flag = block.getMaterial() != Material.air;
                if (flag && this.curBlockDamageMP == 0.0F) {
                    block.onBlockClicked(this.mc.theWorld, x, y, z, this.mc.thePlayer);
                }

                if (flag && block.getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.thePlayer.worldObj, x, y, z) >= 1.0F) {
                    this.onPlayerDestroyBlock(x, y, z, side);
                } else {
                    this.isHittingBlock = true;
                    this.currentBlockX = x;
                    this.currentBlockY = y;
                    this.currentblockZ = z;
                    this.currentItemHittingBlock = this.mc.thePlayer.getHeldItem();
                    this.curBlockDamageMP = 0.0F;
                    this.stepSoundTickCounter = 0.0F;
                    this.mc
                        .theWorld
                        .destroyBlockInWorldPartially(
                            this.mc.thePlayer.getEntityId(), this.currentBlockX, this.currentBlockY, this.currentblockZ, (int)(this.curBlockDamageMP * 10.0F) - 1
                        );
                }
            }
        }
    }

    public void resetBlockRemoving() {
        if (this.isHittingBlock) {
            this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(1, this.currentBlockX, this.currentBlockY, this.currentblockZ, -1));
        }

        this.isHittingBlock = false;
        this.curBlockDamageMP = 0.0F;
        this.mc.theWorld.destroyBlockInWorldPartially(this.mc.thePlayer.getEntityId(), this.currentBlockX, this.currentBlockY, this.currentblockZ, -1);
    }

    public void onPlayerDamageBlock(int x, int y, int z, int side) {
        this.syncCurrentPlayItem();
        if (this.blockHitDelay > 0) {
            this.blockHitDelay--;
        } else if (this.currentGameType.isCreative()) {
            this.blockHitDelay = 5;
            this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(0, x, y, z, side));
            clickBlockCreative(this.mc, this, x, y, z, side);
        } else if (this.sameToolAndBlock(x, y, z)) {
            Block block = this.mc.theWorld.getBlock(x, y, z);
            if (block.getMaterial() == Material.air) {
                this.isHittingBlock = false;
                return;
            }

            this.curBlockDamageMP = this.curBlockDamageMP + block.getPlayerRelativeBlockHardness(this.mc.thePlayer, this.mc.thePlayer.worldObj, x, y, z);
            if (this.stepSoundTickCounter % 4.0F == 0.0F) {
                this.mc
                    .getSoundHandler()
                    .playSound(
                        new PositionedSoundRecord(
                            new ResourceLocation(block.stepSound.getStepSound()),
                            (block.stepSound.getVolume() + 1.0F) / 8.0F,
                            block.stepSound.getFrequency() * 0.5F,
                            x + 0.5F,
                            y + 0.5F,
                            z + 0.5F
                        )
                    );
            }

            this.stepSoundTickCounter++;
            if (this.curBlockDamageMP >= 1.0F) {
                this.isHittingBlock = false;
                this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(2, x, y, z, side));
                this.onPlayerDestroyBlock(x, y, z, side);
                this.curBlockDamageMP = 0.0F;
                this.stepSoundTickCounter = 0.0F;
                this.blockHitDelay = 5;
            }

            this.mc
                .theWorld
                .destroyBlockInWorldPartially(
                    this.mc.thePlayer.getEntityId(), this.currentBlockX, this.currentBlockY, this.currentblockZ, (int)(this.curBlockDamageMP * 10.0F) - 1
                );
        } else {
            this.clickBlock(x, y, z, side);
        }
    }

    public float getBlockReachDistance() {
        return this.currentGameType.isCreative() ? 5.0F : 4.5F;
    }

    public void updateController() throws java.io.IOException {
        this.syncCurrentPlayItem();
        if (this.netClientHandler.getNetworkManager().isChannelOpen()) {
            this.netClientHandler.getNetworkManager().processReceivedPackets();
        } else if (this.netClientHandler.getNetworkManager().getExitMessage() != null) {
            this.netClientHandler.getNetworkManager().getNetHandler().onDisconnect(this.netClientHandler.getNetworkManager().getExitMessage());
        } else {
            this.netClientHandler.getNetworkManager().getNetHandler().onDisconnect(new ChatComponentText("Disconnected from server"));
        }
    }

    public boolean sameToolAndBlock(int x, int y, int z) {
        ItemStack heldItem = this.mc.thePlayer.getHeldItem();
        boolean flag = this.currentItemHittingBlock == null && heldItem == null;
        if (this.currentItemHittingBlock != null && heldItem != null) {
            flag = heldItem.getItem() == this.currentItemHittingBlock.getItem()
                && ItemStack.areItemStackTagsEqual(heldItem, this.currentItemHittingBlock)
                && (heldItem.isItemStackDamageable() || heldItem.getMetadata() == this.currentItemHittingBlock.getMetadata());
        }

        return x == this.currentBlockX && y == this.currentBlockY && z == this.currentblockZ && flag;
    }

    public void syncCurrentPlayItem() {
        int currentItem = this.mc.thePlayer.inventory.currentItem;
        if (currentItem != this.currentPlayerItem) {
            this.currentPlayerItem = currentItem;
            this.netClientHandler.addToSendQueue(new C09PacketHeldItemChange(this.currentPlayerItem));
        }
    }

    public boolean onPlayerRightClick(EntityPlayer player, World worldIn, ItemStack itemStackIn, int x, int y, int z, int side, Vec3 hitVector) throws java.io.IOException {
        this.syncCurrentPlayItem();
        float f = (float)hitVector.xCoord - x;
        float f2 = (float)hitVector.yCoord - y;
        float f3 = (float)hitVector.zCoord - z;
        boolean flag = (!player.isSneaking() || player.getHeldItem() == null)
            && worldIn.getBlock(x, y, z).onBlockActivated(worldIn, x, y, z, player, side, f, f2, f3);
        if (!flag && itemStackIn != null && itemStackIn.getItem() instanceof ItemBlock) {
            ItemBlock item = (ItemBlock)itemStackIn.getItem();
            if (!item.checkWorld(worldIn, x, y, z, side, player, itemStackIn)) {
                return false;
            }
        }

        this.netClientHandler.addToSendQueue(new C08PacketPlayerBlockPlacement(x, y, z, side, player.inventory.getCurrentItem(), f, f2, f3));
        if (flag) {
            return true;
        } else if (itemStackIn == null) {
            return false;
        } else if (this.currentGameType.isCreative()) {
            int metadata = itemStackIn.getMetadata();
            int stackSize = itemStackIn.stackSize;
            boolean tryPlaceItemIntoWorld = itemStackIn.tryPlaceItemIntoWorld(player, worldIn, x, y, z, side, f, f2, f3);
            itemStackIn.setMetadata(metadata);
            itemStackIn.stackSize = stackSize;
            return tryPlaceItemIntoWorld;
        } else {
            return CheatBreaker.getInstance().getKeybindSettings().ghostLiquidFix.getBooleanValue() && itemStackIn.getItem() instanceof ItemBucket
                ? !this.checkEntityPlayer(player, worldIn, itemStackIn, hitVector)
                : itemStackIn.tryPlaceItemIntoWorld(player, worldIn, x, y, z, side, f, f2, f3);
        }
    }

    public boolean sendUseItem(EntityPlayer player, World worldIn, ItemStack itemStackIn) throws java.io.IOException {
        this.syncCurrentPlayItem();
        this.netClientHandler.addToSendQueue(new C08PacketPlayerBlockPlacement(-1, -1, -1, 255, player.inventory.getCurrentItem(), 0.0F, 0.0F, 0.0F));
        int stackSize = itemStackIn.stackSize;
        ItemStack useItemRightClick = itemStackIn.useItemRightClick(worldIn, player);
        if (useItemRightClick != itemStackIn || useItemRightClick != null && useItemRightClick.stackSize != stackSize) {
            player.inventory.mainInventory[player.inventory.currentItem] = useItemRightClick;
            if (useItemRightClick.stackSize == 0) {
                player.inventory.mainInventory[player.inventory.currentItem] = null;
            }

            return true;
        } else {
            return false;
        }
    }

    public EntityClientPlayerMP createPlayer(World worldIn, StatFileWriter stats) {
        return new EntityClientPlayerMP(this.mc, worldIn, this.mc.getSession(), this.netClientHandler, stats);
    }

    public void attackEntity(EntityPlayer player, Entity targetEntity) {
        CheatBreaker.getInstance().getEventBus().handleEvent(new EntityEntityPlayerEvent(player, targetEntity));
        this.syncCurrentPlayItem();
        this.netClientHandler.addToSendQueue(new C02PacketUseEntity(targetEntity, Action.ATTACK));
        player.attackTargetEntityWithCurrentItem(targetEntity);
    }

    public boolean interactWithEntitySendPacket(EntityPlayer player, Entity targetEntity) throws java.io.IOException {
        this.syncCurrentPlayItem();
        this.netClientHandler.addToSendQueue(new C02PacketUseEntity(targetEntity, Action.INTERACT));
        return player.interactWith(targetEntity);
    }

    public ItemStack windowClick(int windowId, int slotId, int n, int n2, EntityPlayer player) {
        short nextTransactionID = player.openContainer.getNextTransactionID(player.inventory);
        ItemStack slotClick = player.openContainer.slotClick(slotId, n, n2, player);
        this.netClientHandler.addToSendQueue(new C0EPacketClickWindow(windowId, slotId, n, n2, slotClick, nextTransactionID));
        return slotClick;
    }

    public void sendEnchantPacket(int n, int n2) {
        this.netClientHandler.addToSendQueue(new C11PacketEnchantItem(n, n2));
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

    public void onStoppedUsingItem(EntityPlayer player) {
        this.syncCurrentPlayItem();
        StopUsingItemEvent stopUsingItemEvent = new StopUsingItemEvent();
        CheatBreaker.getInstance().getEventBus().handleEvent(stopUsingItemEvent);
        if (!stopUsingItemEvent.isCancelled()) {
            this.netClientHandler.addToSendQueue(new C07PacketPlayerDigging(5, 0, 0, 0, 255));
        }

        player.stopUsingItem();
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

    public boolean checkEntityPlayer(EntityPlayer entityPlayer, World world, ItemStack itemStack, Vec3 vec3) {
        Minecraft minecraft = Minecraft.getMinecraft();
        float f = (float)vec3.xCoord - minecraft.objectMouseOver.blockX;
        float f2 = (float)vec3.yCoord - minecraft.objectMouseOver.blockY;
        float f3 = (float)vec3.zCoord - minecraft.objectMouseOver.blockZ;
        C08PacketPlayerBlockPlacement c08PacketPlayerBlockPlacement = new C08PacketPlayerBlockPlacement(
            minecraft.objectMouseOver.blockX,
            minecraft.objectMouseOver.blockY,
            minecraft.objectMouseOver.blockZ,
            minecraft.objectMouseOver.sideHit,
            entityPlayer.inventory.getCurrentItem(),
            f,
            f2,
            f3
        );
        if (itemStack.getUnlocalizedName().equals("item.bucketLava")) {
            world.setBlock((int)c08PacketPlayerBlockPlacement.getFacingX(), (int)c08PacketPlayerBlockPlacement.getFacingY(), (int)c08PacketPlayerBlockPlacement.getFacingZ(), Blocks.flowing_lava, 0, 3);
            System.out.println("lava");
        } else if (itemStack.getUnlocalizedName().equals("item.bucketWater")) {
            world.setBlock((int)c08PacketPlayerBlockPlacement.getFacingX(), (int)c08PacketPlayerBlockPlacement.getFacingY(), (int)c08PacketPlayerBlockPlacement.getFacingZ(), Blocks.flowing_water, 0, 3);
            System.out.println("water");
        }

        return true;
    }

    public GameType getCurrentGameType() {
        return this.currentGameType;
    }
}
