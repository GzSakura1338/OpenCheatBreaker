package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.IntEvent1;
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

public class CPSMod extends AbstractCombatValueHudMod {
    public String counterLabel;
    public Deque<Long> leftClickTimes = new ArrayDeque<>();
    public Deque<Long> rightClickTimes = new ArrayDeque<>();
    public Deque<Long> middleClickTimes = new ArrayDeque<>();
    public Deque<Long> backClickTimes = new ArrayDeque<>();
    public Setting counter;
    public Setting lineColor;
    public Deque<Long> forwardClickTimes = new ArrayDeque<>();

    @Override
    public String getLabelText() {
        return this.counterLabel;
    }

    public void drawCursorLine(String str, String str2) {
        float f = this.mc.fontRendererObj.getStringWidth(StringUtils.substringBefore(str, "%VALUE%"))
                + this.mc.fontRendererObj.getStringWidth(String.valueOf(this.leftClickTimes.size()))
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

    public void onPacketEvent(PacketEvent packetEvent) {
        if (packetEvent.getPacket() instanceof C02PacketUseEntity) {
            Entity entityFromWorld = ((C02PacketUseEntity)packetEvent.getPacket()).getEntityFromWorld(this.mc.theWorld);
            if (entityFromWorld != null && ((C02PacketUseEntity)packetEvent.getPacket()).getAction() == Action.ATTACK) {
                this.lastAttackMillis = System.currentTimeMillis();
            }
        }
    }

    @Override
    public boolean isIdle() {
        if (this.counter.getStringValue().endsWith("Primary Clicks")) {
            return this.leftClickTimes.size() == 0 && this.rightClickTimes.size() == 0;
        } else {
            return !this.counter.getStringValue().endsWith("Thumb Clicks")
                ? this.getNumericValue().intValue() == 0
                : this.backClickTimes.size() == 0 && this.forwardClickTimes.size() == 0;
        }
    }

    @Override
    public String getStaticText() {
        return this.counter.getStringValue().endsWith(" Clicks") ? "9  2" : "9";
    }

    public void onClientTickEvent2(ClientTickEvent clientTickEvent) {
        super.onClientTickEvent(clientTickEvent);
        this.leftClickTimes.removeIf(longValue -> longValue < System.currentTimeMillis() - 1000L);
        this.rightClickTimes.removeIf(longValue -> longValue < System.currentTimeMillis() - 1000L);
        this.middleClickTimes.removeIf(longValue -> longValue < System.currentTimeMillis() - 1000L);
        this.backClickTimes.removeIf(longValue -> longValue < System.currentTimeMillis() - 1000L);
        this.forwardClickTimes.removeIf(longValue -> longValue < System.currentTimeMillis() - 1000L);
        if (this.counter.getStringValue().equals("Higher")) {
            this.updateCounterLabel();
        }
    }

    @Override
    public String getDynamicText() {
        if (this.lastAttackMillis == 0L && this.hideWhenNotAttacking.getBooleanValue()) {
            return null;
        } else if (!this.counter.getStringValue().endsWith(" Clicks")) {
            return super.getDynamicText();
        } else {
            return this.counter.getStringValue().equals("Thumb Clicks")
                ? this.backClickTimes.size() + "  " + this.forwardClickTimes.size()
                : this.leftClickTimes.size() + "  " + this.rightClickTimes.size();
        }
    }

    @Override
    public void createColorOptions() {
        this.lineColor = new Setting(this, "Line Color")
            .withHierarchyParents(this.colorOptions)
            .changeValue(-14671840)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .setCondition(() -> this.counter.getStringValue().endsWith(" Clicks"));
    }

    public CPSMod() throws java.io.IOException {
        super("CPS");
        this.setDescription("Displays your input clicks per second.");
        this.setAuthors("Fyu");
        this.setLabel("[9 CPS]");
        this.addEventHandler(IntEvent1.class, this::onIntEvent1);
        this.addEventHandler(ClientTickEvent.class, this::onClientTickEvent2);
        this.addEventHandler(PacketEvent.class, this::onPacketEvent);
    }

    public void lambda$createGeneralOptions$6(Object obj) {
        this.updateCounterLabel();
    }

    public void onIntEvent1(IntEvent1 intEvent1) {
        if (intEvent1.getEventButton() == 0) {
            this.leftClickTimes.add(System.currentTimeMillis());
        }

        if (intEvent1.getEventButton() == 1) {
            this.rightClickTimes.add(System.currentTimeMillis());
        }

        if (intEvent1.getEventButton() == 2) {
            this.middleClickTimes.add(System.currentTimeMillis());
        }

        if (intEvent1.getEventButton() == 3) {
            this.backClickTimes.add(System.currentTimeMillis());
        }

        if (intEvent1.getEventButton() == 4) {
            this.forwardClickTimes.add(System.currentTimeMillis());
        }

        if (this.counter.getStringValue().equals("Higher")) {
            this.updateCounterLabel();
        }
    }

    @Override
    public Number getNumericValue() {
        switch (this.counter.getStringValue()) {
            case "Left":
                return this.leftClickTimes.size();
            case "Right":
                return this.rightClickTimes.size();
            case "Middle":
                return this.middleClickTimes.size();
            case "Backward":
                return this.backClickTimes.size();
            case "Forward":
                return this.forwardClickTimes.size();
            case "Higher":
                int[] ints = new int[]{
                    this.leftClickTimes.size(), this.rightClickTimes.size(), this.middleClickTimes.size(), this.backClickTimes.size(), this.forwardClickTimes.size()
                };
                return Collections.max(Arrays.asList(ArrayUtils.toObject(ints)));
            default:
                return null;
        }
    }

    @Override
    public void onEnable() throws java.io.IOException {
        super.onEnable();
        this.updateCounterLabel();
    }

    @Override
    public void renderText(String str) throws java.io.IOException {
        super.renderText(str);
        if (this.counter.getStringValue().endsWith(" Clicks")) {
            this.drawCursorLine((this.showBackground.getBooleanValue() ? this.formatBackground : this.formatNoBackground).getStringValue(), str);
        }
    }

    public void updateCounterLabel() {
        StringBuilder sb = new StringBuilder();
        switch (this.counter.getStringValue()) {
            case "Right":
                sb.append("R");
                break;
            case "Middle":
                sb.append("M");
                break;
            case "Backward":
                sb.append("TB");
                break;
            case "Forward":
                sb.append("TF");
                break;
            case "Thumb Clicks":
                sb.append("T");
                break;
            case "Higher":
                if (this.getNumericValue().intValue() != 0) {
                    if (this.getNumericValue().intValue() == this.rightClickTimes.size()) {
                        sb.append("R");
                    } else if (this.getNumericValue().intValue() == this.middleClickTimes.size()) {
                        sb.append("M");
                    } else if (this.getNumericValue().intValue() == this.backClickTimes.size()) {
                        sb.append("TB");
                    } else if (this.getNumericValue().intValue() == this.forwardClickTimes.size()) {
                        sb.append("TF");
                    }
                }
        }

        sb.append("CPS");
        this.counterLabel = sb.toString();
    }

    @Override
    public void createGeneralOptions() {
        super.createGeneralOptions();
        this.hideWhenValueIs.setCondition(() -> !this.counter.getStringValue().endsWith(" Clicks"));
        this.hiddenValueS.setCondition(() -> !this.hideWhenValueIs.getValue().equals("OFF") && !this.counter.getStringValue().endsWith(" Clicks"));
        this.counter = new Setting(this, "Counter")
            .setDescription(
                "Determines which and when clicks should be shown.\n\n§bLeft Clicks:§r Only show left clicks.\n§bRight Clicks:§r Only show right clicks.\n§bBoth:§r Show both left and right clicks.\n§bHigher:§r Shows either left or right clicks depending which has a higher amount."
            )
            .withHierarchyParents(this.generalOptions)
            .changeValue("Higher")
            .setStringValues("Left", "Right", "Middle", "Backward", "Forward", "Primary Clicks", "Thumb Clicks", "Higher")
            .setOnUpdate(this::lambda$createGeneralOptions$6);
    }
}
