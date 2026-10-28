package net.minecraft.world.demo;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

public class DemoWorldManager extends ItemInWorldManager {
    public boolean sentHelp;
    public boolean demoTimeExpired;
    public int reminderTicks;
    public int tick;

    public DemoWorldManager(World world) {
        super(world);
    }

    @Override
    public void updateBlockRemoving() {
        super.updateBlockRemoving();
        this.tick++;
        long totalWorldTime = this.theWorld.getTotalWorldTime();
        long l = totalWorldTime / 24000L + 1L;
        if (!this.sentHelp && this.tick > 20) {
            this.sentHelp = true;
            this.thisPlayerMP.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(5, 0.0F));
        }

        this.demoTimeExpired = totalWorldTime > 120500L;
        if (this.demoTimeExpired) {
            this.reminderTicks++;
        }

        if (totalWorldTime % 24000L == 500L) {
            if (l <= 6L) {
                this.thisPlayerMP.addChatMessage(new ChatComponentTranslation("demo.day." + l));
            }
        } else if (l == 1L) {
            if (totalWorldTime == 100L) {
                this.thisPlayerMP.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(5, 101.0F));
            } else if (totalWorldTime == 175L) {
                this.thisPlayerMP.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(5, 102.0F));
            } else if (totalWorldTime == 250L) {
                this.thisPlayerMP.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(5, 103.0F));
            }
        } else if (l == 5L && totalWorldTime % 24000L == 22000L) {
            this.thisPlayerMP.addChatMessage(new ChatComponentTranslation("demo.day.warning"));
        }
    }

    public void sendDemoReminder() {
        if (this.reminderTicks > 100) {
            this.thisPlayerMP.addChatMessage(new ChatComponentTranslation("demo.reminder"));
            this.reminderTicks = 0;
        }
    }

    @Override
    public void onBlockClicked(int n, int n2, int n3, int n4) {
        if (this.demoTimeExpired) {
            this.sendDemoReminder();
        } else {
            super.onBlockClicked(n, n2, n3, n4);
        }
    }

    @Override
    public void blockRemoving(int n, int n2, int n3) {
        if (!this.demoTimeExpired) {
            super.blockRemoving(n, n2, n3);
        }
    }

    @Override
    public boolean tryHarvestBlock(int n, int n2, int n3) {
        return !this.demoTimeExpired && super.tryHarvestBlock(n, n2, n3);
    }

    @Override
    public boolean tryUseItem(EntityPlayer entityPlayer, World world, ItemStack itemStack) throws java.io.IOException {
        if (this.demoTimeExpired) {
            this.sendDemoReminder();
            return false;
        } else {
            return super.tryUseItem(entityPlayer, world, itemStack);
        }
    }

    @Override
    public boolean activateBlockOrUseItem(
        EntityPlayer player, World worldIn, ItemStack stack, int n, int n2, int n3, int n4, float f, float f2, float f3
    ) throws java.io.IOException {
        if (this.demoTimeExpired) {
            this.sendDemoReminder();
            return false;
        } else {
            return super.activateBlockOrUseItem(player, worldIn, stack, n, n2, n3, n4, f, f2, f3);
        }
    }
}
