package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.EntityS19PacketEntityStatusEvent;
import com.cheatbreaker.client.event.PacketEvent;
import com.cheatbreaker.client.mod.AbstractCombatValueHudMod;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class SprintResetCounterMod extends AbstractCombatValueHudMod {
    public int count;
    public boolean forwardWasDown;
    public List<Long> hitTimes = new ArrayList<>();
    public int counterKey;
    public long lastHitTime;
    public boolean forwardReleased;
    public Setting counter;
    public List<Long> wTaps = new ArrayList<>();
    public List<Long> sTaps = new ArrayList<>();
    public int lastHitEntityId;
    public int attackedEntityId = -1;
    public long lastTapTime;

    @Override
    public String getLabelText() {
        int counterKey = this.counterKey;
        if (this.counter.getValue().equals("Higher")) {
            if (this.sTaps.size() > this.wTaps.size()) {
                counterKey = this.mc.gameSettings.keyBindBack.getKeyCode();
            } else {
                counterKey = this.mc.gameSettings.keyBindForward.getKeyCode();
            }
        }

        return Keyboard.getKeyName(counterKey) + "Tap" + (this.count != 1 ? "s" : "");
    }

    @Override
    public Number getNumericValue() {
        this.count = this.counter.getValue().equals("Forward") ? this.wTaps.size() : this.sTaps.size();
        this.count = this.counter.getValue().equals("Higher") ? Math.max(this.wTaps.size(), this.sTaps.size()) : this.count;
        return this.count;
    }

    public SprintResetCounterMod() throws java.io.IOException {
        super("Sprint Reset Counter");
        this.setDescription("Displays the amount of times you sprint reset.");
        this.setAuthors("AgentManny");
        this.setLabel("[4 WTaps]");
        this.addEventHandler(ClientTickEvent.class, this::onClientTickEvent3);
        this.addEventHandler(EntityS19PacketEntityStatusEvent.class, this::onEntityS19PacketEntityStatusEvent);
        this.addEventHandler(PacketEvent.class, this::onPacketEvent);
    }

    public void onEntityS19PacketEntityStatusEvent(EntityS19PacketEntityStatusEvent entityS19PacketEntityStatusEvent) {
        if (entityS19PacketEntityStatusEvent.getS19PacketEntityStatus().getStatus() == 2) {
            if (this.attackedEntityId == entityS19PacketEntityStatusEvent.getEntity().getEntityId()) {
                this.attackedEntityId = -1;
                if (this.lastHitEntityId != entityS19PacketEntityStatusEvent.getEntity().getEntityId()) {
                    this.hitTimes.clear();
                }

                if (this.mc.gameSettings.keyBindForward.getIsKeyPressed() && this.mc.thePlayer.isSprinting()) {
                    this.hitTimes.add(System.currentTimeMillis());
                }

                this.lastHitTime = System.currentTimeMillis();
                this.lastHitEntityId = entityS19PacketEntityStatusEvent.getEntity().getEntityId();
            }
        }
    }

    @Override
    public void reset() {
        this.lastTapTime = 0L;
        this.wTaps.clear();
        this.sTaps.clear();
    }

    @Override
    public String getStaticText() {
        return "4";
    }

    public void onPacketEvent(PacketEvent packetEvent) {
        if (packetEvent.getPacket() instanceof C02PacketUseEntity) {
            Entity entityFromWorld = ((C02PacketUseEntity)packetEvent.getPacket()).getEntityFromWorld(this.mc.theWorld);
            if (entityFromWorld != null && ((C02PacketUseEntity)packetEvent.getPacket()).getAction() == Action.ATTACK) {
                this.attackedEntityId = entityFromWorld.getEntityId();
                this.lastAttackMillis = System.currentTimeMillis();
            }
        }
    }

    public void onClientTickEvent3(ClientTickEvent clientTickEvent) {
        int keyCode = this.mc.gameSettings.keyBindForward.getKeyCode();
        int keyCode2 = this.mc.gameSettings.keyBindBack.getKeyCode();
        this.counterKey = this.counter.getValue().equals("Forward") ? keyCode : keyCode2;
        int n = keyCode != -99 && keyCode != -100 ? -1 : (keyCode == -99 ? 0 : 1);
        int n2 = keyCode2 != -99 && keyCode2 != -100 ? -1 : (keyCode2 == -99 ? 0 : 1);
        boolean flag = (this.mc.currentScreen == null || this.mc.currentScreen instanceof GuiContainer || this.mc.currentScreen instanceof ModsScreen)
            && (n != -1 ? Mouse.isButtonDown(n) : Keyboard.isKeyDown(keyCode));
        boolean flag2 = (this.mc.currentScreen == null || this.mc.currentScreen instanceof GuiContainer || this.mc.currentScreen instanceof ModsScreen)
            && (n2 != -1 ? Mouse.isButtonDown(n2) : Keyboard.isKeyDown(keyCode2));
        if (this.mc.gameSettings.keyBindForward.getIsKeyPressed() && this.mc.thePlayer.isSprinting() && this.hitTimes.size() > 0) {
            if (flag2) {
                this.sTaps.add(System.currentTimeMillis());
                this.hitTimes.clear();
                return;
            }

            if (flag && !this.forwardWasDown) {
                this.forwardWasDown = true;
                if (this.forwardReleased) {
                    this.wTaps.add(System.currentTimeMillis());
                }

                this.forwardReleased = false;
                this.lastTapTime = System.currentTimeMillis();
                this.hitTimes.clear();
            } else if (this.forwardWasDown && !flag) {
                this.forwardWasDown = false;
                this.forwardReleased = true;
            }
        }

        if (System.currentTimeMillis() - this.lastAttackMillis > 2000L) {
            this.lastAttackMillis = 0L;
            this.reset();
        }
    }

    @Override
    public void createGeneralOptions() {
        super.createGeneralOptions();
        this.counter = new Setting(this, "Counter").changeValue("Forward").setStringValues("Forward", "Backwards", "Higher");
        this.hideWhenNotAttacking = new Setting(this, "Hide When Not Attacking").changeValue(false);
    }
}
