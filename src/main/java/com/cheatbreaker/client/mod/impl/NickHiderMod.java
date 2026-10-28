package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.PlayerSkinLookupEvent;
import com.cheatbreaker.client.event.StringEvent2;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.screen.ResourcePacksScreen;
import com.cheatbreaker.client.ui.screen.StringSettingEditScreen;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;

public class NickHiderMod extends AbstractMod {
    public Setting skinOptions = new SettingGroup(this, "Skin Options").changeValue(true);
    public Setting hideOwnSkin = new Setting(this, "Hide Own Skin").withHierarchyParents(this.skinOptions).changeValue(false);
    public Setting hideOtherSkins = new Setting(this, "Hide Other Skins").withHierarchyParents(this.skinOptions).changeValue(false);
    public Setting nameOptions = new SettingGroup(this, "Name Options").changeValue(true);
    public Setting hideRealName = new Setting(this, "Hide Real Name").withHierarchyParents(this.nameOptions).changeValue(false);
    public Setting customNameString = new Setting(this, "Custom Name String")
        .changeValue(this.mc.getSession().getUsername())
        .withHierarchyParents(this.hideRealName);

    public void onPlayerSkinLookupEvent(PlayerSkinLookupEvent playerSkinLookupEvent) {
        if (!this.hideOtherSkins.getBooleanValue() || !this.hideOwnSkin.getBooleanValue() && this.mc.getSession().getUsername().equals(playerSkinLookupEvent.getPlayerName())) {
            if (this.hideOwnSkin.getBooleanValue() && this.mc.getSession().getUsername().equals(playerSkinLookupEvent.getPlayerName())) {
                playerSkinLookupEvent.setCancelled(true);
            }
        } else {
            playerSkinLookupEvent.setCancelled(true);
        }
    }

    public boolean getBooleanValue() {
        return this.hideRealName.getBooleanValue()
            && !(this.mc.currentScreen instanceof ResourcePacksScreen)
            && !(this.mc.currentScreen instanceof StringSettingEditScreen);
    }

    public void onStringEvent2(StringEvent2 stringEvent2) {
        stringEvent2.setText(this.replaceUsername(stringEvent2.getText()));
    }

    public String replaceUsername(String str) {
        if (this.getBooleanValue()) {
            String username = Minecraft.getMinecraft().getSession().getUsername();
            String str2 = this.customNameString.getStringValue().equals(username) ? "You" : this.customNameString.getStringValue();
            str = str.replaceAll(Pattern.quote(username), Matcher.quoteReplacement(str2));
        }

        return str;
    }

    public NickHiderMod() throws java.io.IOException {
        super("Nick Hider");
        this.setIcon(new ResourceLocation("client/icons/mods/nickhider.png"), 32, 32);
        this.setDescription("Hides your identity client-sided.");
        this.setAuthors("Sk1er");
        this.setCategories(ModCategory.SERVER);
        this.setEnabledByDefault(false);
        this.addEventHandler(StringEvent2.class, this::onStringEvent2);
        this.addEventHandler(PlayerSkinLookupEvent.class, this::onPlayerSkinLookupEvent);
    }

    @Override
    public void renderPreview(int n, int n2, int n3, int n4) {
        float iconWidth = this.getIconWidth();
        float iconHeight = this.getIconHeight();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderUtils.drawTexture(
            CheatBreaker.getInstance().getFontManager().getHeadTexture(this.mc.getSession().getUsername()),
            n + n3 / 2 - iconWidth / 2.0F,
            n2 + n4 / 2 - 26 - iconHeight / 2.0F,
            iconWidth,
            iconHeight
        );
    }
}
