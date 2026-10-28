package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.mod.AbstractBackgroundHudMod;
import com.cheatbreaker.client.ui.AnchorPoint;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.StringUtils;

public class DemoTimerMod extends AbstractBackgroundHudMod {
    @Override
    public String getDefaultFormat() {
        return "%LABEL%: %VALUE%";
    }

    @Override
    public String getDynamicText() {
        return StringUtils.ticksToElapsedTime((int)(120500L - this.mc.theWorld.getTotalWorldTime()));
    }

    @Override
    public boolean wrapsTextWithoutBackground() {
        return false;
    }

    @Override
    public String getLabelText() {
        return "Remaining time";
    }

    @Override
    public String formatText(String str) {
        return this.mc.theWorld.getTotalWorldTime() >= 120500L ? I18n.format("demo.demoExpired") : super.formatText(str);
    }

    public DemoTimerMod() throws java.io.IOException {
        super("Demo Timer");
        this.setDescription("Displays the remaining time you have in your Demo World");
        this.setAnchorPointAndDefault(AnchorPoint.RIGHT_TOP);
        this.setTranslationAndDefault(-7.0F, 3.0F);
        this.setEnabledByDefault(true);
        this.setCanToggle(false);
        this.setLabel("Time's up!");
    }

    @Override
    public boolean showBackgroundByDefault() {
        return false;
    }
}
