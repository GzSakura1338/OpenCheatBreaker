package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.EntityS19PacketEntityStatusEvent;
import com.cheatbreaker.client.event.PacketEvent;
import com.cheatbreaker.client.mod.AbstractCombatValueHudMod;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;

public class ComboCounterMod extends AbstractCombatValueHudMod {
    public List<Long> longValueList = new ArrayList<>();
    public long lastHitMillis;
    public int attackedEntityId = -1;
    public int lastHitEntityId;

    @Override
    public String getLabelText() {
        return "Combo";
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

    @Override
    public String getStaticText() {
        return "6";
    }

    public void onEntityS19PacketEntityStatusEvent(EntityS19PacketEntityStatusEvent entityS19PacketEntityStatusEvent) {
        if (entityS19PacketEntityStatusEvent.getS19PacketEntityStatus().getStatus() == 2) {
            if (this.attackedEntityId == entityS19PacketEntityStatusEvent.getEntity().getEntityId()) {
                this.attackedEntityId = -1;
                if (this.lastHitEntityId != entityS19PacketEntityStatusEvent.getEntity().getEntityId()) {
                    this.longValueList.clear();
                }

                this.longValueList.add(System.currentTimeMillis());
                this.lastHitMillis = System.currentTimeMillis();
                this.lastHitEntityId = entityS19PacketEntityStatusEvent.getEntity().getEntityId();
            } else if (entityS19PacketEntityStatusEvent.getEntity().getEntityId() == this.mc.thePlayer.getEntityId()) {
                this.longValueList.clear();
            }
        }
    }

    public ComboCounterMod() throws java.io.IOException {
        super("Combo Counter");
        this.setDescription("Displays how many times you hit someone in a row within 2 seconds.");
        this.setAuthors("Erouax");
        this.setLabel("[6 Combo]");
        this.addEventHandler(EntityS19PacketEntityStatusEvent.class, this::onEntityS19PacketEntityStatusEvent);
        this.addEventHandler(PacketEvent.class, this::onPacketEvent);
    }

    @Override
    public void reset() {
        this.longValueList.clear();
    }

    @Override
    public Number getNumericValue() {
        return this.longValueList.size();
    }

    @Override
    public String constantText() {
        return "No %LABEL%";
    }
}
