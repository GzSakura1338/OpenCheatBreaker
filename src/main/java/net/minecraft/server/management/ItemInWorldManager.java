package net.minecraft.server.management;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings.GameType;

public class ItemInWorldManager {
    public GameType gameType = GameType.NOT_SET;
    public EntityPlayerMP thisPlayerMP;
    public int durabilityRemainingOnBlock = -1;
    public boolean isDestroyingBlock;
    public int initialDamage;
    public int curBlockX;
    public int curBlockY;
    public int curBlockZ;
    public int curblockDamage;
    public boolean receivedFinishDiggingPacket;
    public int posX;
    public int posY;
    public int posZ;
    public int initialBlockDamage;
    public World theWorld;

    public ItemInWorldManager(World world) {
        this.theWorld = world;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
        gameType.configurePlayerCapabilities(this.thisPlayerMP.capabilities);
        this.thisPlayerMP.sendPlayerAbilities();
    }

    public GameType getGameType() {
        return this.gameType;
    }

    public boolean isCreative() {
        return this.gameType.isCreative();
    }

    public void initializeGameType(GameType gameType) {
        if (this.gameType == GameType.NOT_SET) {
            this.gameType = gameType;
        }

        this.setGameType(this.gameType);
    }

    public void updateBlockRemoving() {
        this.curblockDamage++;
        if (this.receivedFinishDiggingPacket) {
            int n = this.curblockDamage - this.initialBlockDamage;
            Block block = this.theWorld.getBlock(this.posX, this.posY, this.posZ);
            if (block.getMaterial() == Material.air) {
                this.receivedFinishDiggingPacket = false;
            } else {
                float f = block.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, this.posX, this.posY, this.posZ) * (n + 1);
                int n2 = (int)(f * 10.0F);
                if (n2 != this.durabilityRemainingOnBlock) {
                    this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.getEntityId(), this.posX, this.posY, this.posZ, n2);
                    this.durabilityRemainingOnBlock = n2;
                }

                if (f >= 1.0F) {
                    this.receivedFinishDiggingPacket = false;
                    this.tryHarvestBlock(this.posX, this.posY, this.posZ);
                }
            }
        } else if (this.isDestroyingBlock) {
            Block block = this.theWorld.getBlock(this.curBlockX, this.curBlockY, this.curBlockZ);
            if (block.getMaterial() == Material.air) {
                this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.getEntityId(), this.curBlockX, this.curBlockY, this.curBlockZ, -1);
                this.durabilityRemainingOnBlock = -1;
                this.isDestroyingBlock = false;
            } else {
                int n = this.curblockDamage - this.initialDamage;
                float f = block.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, this.curBlockX, this.curBlockY, this.curBlockZ)
                    * (n + 1);
                int n2 = (int)(f * 10.0F);
                if (n2 != this.durabilityRemainingOnBlock) {
                    this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.getEntityId(), this.curBlockX, this.curBlockY, this.curBlockZ, n2);
                    this.durabilityRemainingOnBlock = n2;
                }
            }
        }
    }

    public void onBlockClicked(int n, int n2, int n3, int n4) {
        if (!this.gameType.isAdventure() || this.thisPlayerMP.isCurrentToolAdventureModeExempt(n, n2, n3)) {
            if (this.isCreative()) {
                if (!this.theWorld.extinguishFire(null, n, n2, n3, n4)) {
                    this.tryHarvestBlock(n, n2, n3);
                }
            } else {
                this.theWorld.extinguishFire(null, n, n2, n3, n4);
                this.initialDamage = this.curblockDamage;
                float f = 1.0F;
                Block block = this.theWorld.getBlock(n, n2, n3);
                if (block.getMaterial() != Material.air) {
                    block.onBlockClicked(this.theWorld, n, n2, n3, this.thisPlayerMP);
                    f = block.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, n, n2, n3);
                }

                if (block.getMaterial() != Material.air && f >= 1.0F) {
                    this.tryHarvestBlock(n, n2, n3);
                } else {
                    this.isDestroyingBlock = true;
                    this.curBlockX = n;
                    this.curBlockY = n2;
                    this.curBlockZ = n3;
                    int n5 = (int)(f * 10.0F);
                    this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.getEntityId(), n, n2, n3, n5);
                    this.durabilityRemainingOnBlock = n5;
                }
            }
        }
    }

    public void blockRemoving(int n, int n2, int n3) {
        if (n == this.curBlockX && n2 == this.curBlockY && n3 == this.curBlockZ) {
            int n4 = this.curblockDamage - this.initialDamage;
            Block block = this.theWorld.getBlock(n, n2, n3);
            if (block.getMaterial() != Material.air) {
                float f = block.getPlayerRelativeBlockHardness(this.thisPlayerMP, this.thisPlayerMP.worldObj, n, n2, n3) * (n4 + 1);
                if (f >= 0.7F) {
                    this.isDestroyingBlock = false;
                    this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.getEntityId(), n, n2, n3, -1);
                    this.tryHarvestBlock(n, n2, n3);
                } else if (!this.receivedFinishDiggingPacket) {
                    this.isDestroyingBlock = false;
                    this.receivedFinishDiggingPacket = true;
                    this.posX = n;
                    this.posY = n2;
                    this.posZ = n3;
                    this.initialBlockDamage = this.initialDamage;
                }
            }
        }
    }

    public void cancelDestroyingBlock(int n, int n2, int n3) {
        this.isDestroyingBlock = false;
        this.theWorld.destroyBlockInWorldPartially(this.thisPlayerMP.getEntityId(), this.curBlockX, this.curBlockY, this.curBlockZ, -1);
    }

    public boolean removeBlock(int n, int n2, int n3) {
        Block block = this.theWorld.getBlock(n, n2, n3);
        int blockMetadata = this.theWorld.getBlockMetadata(n, n2, n3);
        block.onBlockHarvested(this.theWorld, n, n2, n3, blockMetadata, this.thisPlayerMP);
        boolean setBlockToAir = this.theWorld.setBlockToAir(n, n2, n3);
        if (setBlockToAir) {
            block.onBlockDestroyedByPlayer(this.theWorld, n, n2, n3, blockMetadata);
        }

        return setBlockToAir;
    }

    public boolean tryHarvestBlock(int n, int n2, int n3) {
        if (this.gameType.isAdventure() && !this.thisPlayerMP.isCurrentToolAdventureModeExempt(n, n2, n3)) {
            return false;
        }

        if (this.gameType.isCreative() && this.thisPlayerMP.getHeldItem() != null && this.thisPlayerMP.getHeldItem().getItem() instanceof ItemSword) {
            return false;
        }

        Block block = this.theWorld.getBlock(n, n2, n3);
        int blockMetadata = this.theWorld.getBlockMetadata(n, n2, n3);
        this.theWorld
            .playAuxSFXAtEntity(this.thisPlayerMP, 2001, n, n2, n3, Block.getIdFromBlock(block) + (this.theWorld.getBlockMetadata(n, n2, n3) << 12));
        boolean removeBlock = this.removeBlock(n, n2, n3);
        if (this.isCreative()) {
            this.thisPlayerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(n, n2, n3, this.theWorld));
        } else {
            ItemStack currentEquippedItem = this.thisPlayerMP.getCurrentEquippedItem();
            boolean canHarvestBlock = this.thisPlayerMP.canHarvestBlock(block);
            if (currentEquippedItem != null) {
                currentEquippedItem.onBlockDestroyed(this.theWorld, block, n, n2, n3, this.thisPlayerMP);
                if (currentEquippedItem.stackSize == 0) {
                    this.thisPlayerMP.destroyCurrentEquippedItem();
                }
            }

            if (removeBlock && canHarvestBlock) {
                block.harvestBlock(this.theWorld, this.thisPlayerMP, n, n2, n3, blockMetadata);
            }
        }

        return removeBlock;
    }

    public boolean tryUseItem(EntityPlayer entityPlayer, World world, ItemStack itemStack) throws java.io.IOException {
        int stackSize = itemStack.stackSize;
        int metadata = itemStack.getMetadata();
        ItemStack useItemRightClick = itemStack.useItemRightClick(world, entityPlayer);
        if (useItemRightClick != itemStack || useItemRightClick != null && (useItemRightClick.stackSize != stackSize || useItemRightClick.getMaxItemUseDuration() > 0 || useItemRightClick.getMetadata() != metadata)) {
            entityPlayer.inventory.mainInventory[entityPlayer.inventory.currentItem] = useItemRightClick;
            if (this.isCreative()) {
                useItemRightClick.stackSize = stackSize;
                if (useItemRightClick.isItemStackDamageable()) {
                    useItemRightClick.setMetadata(metadata);
                }
            }

            if (useItemRightClick.stackSize == 0) {
                entityPlayer.inventory.mainInventory[entityPlayer.inventory.currentItem] = null;
            }

            if (!entityPlayer.isUsingItem()) {
                ((EntityPlayerMP)entityPlayer).sendContainerToPlayer(entityPlayer.inventoryContainer);
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean activateBlockOrUseItem(
        EntityPlayer player, World worldIn, ItemStack stack, int n, int n2, int n3, int n4, float f, float f2, float f3
    ) throws java.io.IOException {
        if ((!player.isSneaking() || player.getHeldItem() == null)
            && worldIn.getBlock(n, n2, n3).onBlockActivated(worldIn, n, n2, n3, player, n4, f, f2, f3)) {
            return true;
        } else if (stack == null) {
            return false;
        } else if (this.isCreative()) {
            int metadata = stack.getMetadata();
            int stackSize = stack.stackSize;
            boolean tryPlaceItemIntoWorld = stack.tryPlaceItemIntoWorld(player, worldIn, n, n2, n3, n4, f, f2, f3);
            stack.setMetadata(metadata);
            stack.stackSize = stackSize;
            return tryPlaceItemIntoWorld;
        } else {
            return stack.tryPlaceItemIntoWorld(player, worldIn, n, n2, n3, n4, f, f2, f3);
        }
    }

    public void setWorld(WorldServer worldServer) {
        this.theWorld = worldServer;
    }
}
