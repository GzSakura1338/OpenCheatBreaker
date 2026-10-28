package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.IntEvent6;
import com.cheatbreaker.client.event.PacketEvent;
import com.cheatbreaker.client.mod.AbstractCombatValueHudMod;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.render.RenderUtils;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class SPSMod extends AbstractCombatValueHudMod {
    public Deque<Long> swingTimes = new ArrayDeque<>();
    public String counterLabel;
    public Setting counter;
    public Deque<Long> useTimes = new ArrayDeque<>();
    public Setting lineColor;

    @Override
    public void onEnable() throws java.io.IOException {
        super.onEnable();
        this.updateCounterLabel();
    }

    public void onPacketEvent(PacketEvent packetEvent) {
        if (packetEvent.getPacket() instanceof C02PacketUseEntity) {
            Entity entityFromWorld = ((C02PacketUseEntity)packetEvent.getPacket()).getEntityFromWorld(this.mc.theWorld);
            if (entityFromWorld != null && ((C02PacketUseEntity)packetEvent.getPacket()).getAction() == Action.ATTACK) {
                this.lastAttackMillis = System.currentTimeMillis();
            }
        }
    }

    public SPSMod() throws java.io.IOException {
        super("SPS");
        this.setDescription("Displays your registered swings per second.");
        this.setKeywords("Swings Per Second");
        this.setLabel("[9 SPS]");
        this.addEventHandler(IntEvent6.class, this::onIntEvent6);
        this.addEventHandler(ClientTickEvent.class, this::onClientTickEvent4);
        this.addEventHandler(PacketEvent.class, this::onPacketEvent);
    }

    @Override
    public String getLabelText() {
        return this.counterLabel;
    }

    public void onClientTickEvent4(ClientTickEvent clientTickEvent) {
        super.onClientTickEvent(clientTickEvent);
        this.swingTimes.removeIf(longValue -> longValue < System.currentTimeMillis() - 1000L);
        this.useTimes.removeIf(longValue -> longValue < System.currentTimeMillis() - 1000L);
    }

    @Override
    public boolean isIdle() {
        return !this.counter.getStringValue().endsWith("Both")
            ? this.getNumericValue().intValue() == 0
            : this.swingTimes.size() == 0 && this.useTimes.size() == 0;
    }

    public void drawCursorLine(String str, String str2) {
        float f = this.mc.fontRendererObj.getStringWidth(StringUtils.substringBefore(str, "%VALUE%"))
                + this.mc.fontRendererObj.getStringWidth(String.valueOf(this.swingTimes.size()))
            + (this.mc.fontRendererObj.getStringWidth("  ") / 2.0F - 0.4F);
        float f2 = !this.showBackground.getBooleanValue() && !this.alwaysCenter.getBooleanValue() ? -1.0F : this.backgroundHeight.getFloatValue() / 2.0F - 4.5F;
        if (this.showBackground.getBooleanValue() || this.alwaysCenter.getBooleanValue()) {
            float f3 = this.hudWidth / 2.0F
                - this.mc.fontRendererObj.getStringWidth(str.replaceAll("%LABEL%", this.getLabelText()).replaceAll("%VALUE%", str2)) / 2;
            f += f3;
        }

        RenderUtils.drawRectWithShadow(
            f, f2, f + 1.0F, f2 + this.mc.fontRendererObj.FONT_HEIGHT, this.lineColor.getColorValue(), this.shouldDrawShadow()
        );
    }

    @Override
    public String getDynamicText() {
        if (this.lastAttackMillis == 0L && this.hideWhenNotAttacking.getBooleanValue()) {
            return null;
        } else {
            return !this.counter.getStringValue().endsWith("Both") ? super.getDynamicText() : this.swingTimes.size() + "  " + this.useTimes.size();
        }
    }

    @Override
    public void renderText(String str) throws java.io.IOException {
        super.renderText(str);
        if (this.counter.getStringValue().endsWith("Both")) {
            this.drawCursorLine((this.showBackground.getBooleanValue() ? this.formatBackground : this.formatNoBackground).getStringValue(), str);
        }
    }

    public void updateCounterLabel() {
        StringBuilder sb = new StringBuilder();
        switch (this.counter.getStringValue()) {
            case "Use":
                sb.append("U");
                break;
            case "Higher":
                if (this.getNumericValue().intValue() != 0) {
                    if (this.getNumericValue().intValue() == this.useTimes.size()) {
                        sb.append("U");
                    } else {
                        sb.append("S");
                    }
                }
                break;
            default:
                sb.append("S");
        }

        sb.append("PS");
        this.counterLabel = sb.toString();
    }

    public void onIntEvent6(IntEvent6 intEvent6) {
        if (intEvent6.getIntValue() == 0) {
            this.swingTimes.add(System.currentTimeMillis());
        }

        if (intEvent6.getIntValue() == 1) {
            this.useTimes.add(System.currentTimeMillis());
        }

        if (this.counter.getStringValue().equals("Higher")) {
            this.updateCounterLabel();
        }
    }

    @Override
    public Number getNumericValue() {
        switch (this.counter.getStringValue()) {
            case "Swing":
                return this.swingTimes.size();
            case "Use":
                return this.useTimes.size();
            case "Higher":
                int[] ints = new int[]{this.swingTimes.size(), this.useTimes.size()};
                return Collections.max(Arrays.asList(ArrayUtils.toObject(ints)));
            default:
                return null;
        }
    }

    @Override
    public String getStaticText() {
        return this.counter.getStringValue().endsWith("Both") ? "9  2" : "9";
    }

    @Override
    public void createColorOptions() {
        this.lineColor = new Setting(this, "Line Color")
            .withHierarchyParents(this.colorOptions)
            .changeValue(-14671840)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .setCondition(() -> this.counter.getStringValue().endsWith("Both"));
    }

    public void lambda$createGeneralOptions$4(Object obj) {
        this.updateCounterLabel();
    }

    @Override
    public void createGeneralOptions() {
        super.createGeneralOptions();
        this.hideWhenValueIs.setCondition(() -> !this.counter.getStringValue().endsWith("Both"));
        this.hiddenValueS.setCondition(() -> !this.hideWhenValueIs.getValue().equals("OFF") && !this.counter.getStringValue().endsWith("Both"));
        this.counter = new Setting(this, "Counter")
            .withHierarchyParents(this.generalOptions)
            .changeValue("Higher")
            .setStringValues("Swing", "Use", "Both", "Higher")
            .setOnUpdate(this::lambda$createGeneralOptions$4);
    }
}
