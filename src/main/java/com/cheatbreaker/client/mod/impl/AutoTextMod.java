package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.IntEvent1;
import com.cheatbreaker.client.event.IntEvent4;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class AutoTextMod extends AbstractMod {
    public JsonObject autoTextOptions = new JsonObject();
    public String activeHotkey = "";
    public String[] allowedCommands = new String[]{"/skyblock", "/play", "/lobby", "/hub", "/spawn", "/main", "/leave", "/warp", "/rejoin"};
    public List<Long> recentUses = new ArrayList<>();
    public List<Setting> hotkeySettings = new ArrayList<>();
    public int nextHotkeyIndex = 1;

    @Override
    public void saveCustomSetting(Setting setting) {
        if (setting.getName().startsWith("Hot key")) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("keycode", setting.getKeyCode());
            jsonObject.addProperty("mouse", setting.hasMouseBind());
            jsonObject.addProperty("command", setting.getStringValue());
            this.autoTextOptions.add(setting.getName(), jsonObject);
        }
    }

    public void setActiveHotkey(String str) {
        this.activeHotkey = str;
    }

    public void addHotkey() {
        Setting setMouseBind = new Setting(this, "Hot key " + this.nextHotkeyIndex).changeValue("/Command").enableKeyCode(0).setMouseBind(false);
        this.hotkeySettings.add(setMouseBind);
        this.nextHotkeyIndex++;

        for (Setting setting : this.getSettings()) {
            if (setting.getName().equalsIgnoreCase(setMouseBind.getName())) {
                return;
            }
        }

        this.getSettings().add(setMouseBind);
        CheatBreaker.getInstance().getConfigFileManager().saveAll();
    }

    @Override
    public void renderPreview(int n, int n2, int n3, int n4) {
        float stringWidth = Minecraft.getMinecraft().fontRendererObj.getStringWidth(this.getLabel());
        Minecraft.getMinecraft().fontRendererObj.drawShadowedString(this.getLabel(), n - 1 + n3 / 2 - stringWidth / 2.0F, n2 + n4 / 2 - 43, -1);
        Minecraft.getMinecraft().fontRendererObj.drawShadowedString(this.getLabel(), n - 1 + n3 / 2 - stringWidth / 2.0F, n2 + n4 / 2 - 34, -1);
        Minecraft.getMinecraft().fontRendererObj.drawShadowedString(this.getLabel(), n - 1 + n3 / 2 - stringWidth / 2.0F, n2 + n4 / 2 - 25, -1);
        Minecraft.getMinecraft().fontRendererObj.drawShadowedString(this.getLabel(), n - 1 + n3 / 2 - stringWidth / 2.0F, n2 + n4 / 2 - 16, -1);
    }

    public boolean isAllowedCommand(String str) {
        for (String str2 : this.allowedCommands) {
            if (str.startsWith(str2)) {
                return true;
            }
        }

        return false;
    }

    public AutoTextMod() throws java.io.IOException {
        super("Auto Text");
        this.setEnabledByDefault(false);
        this.setCategories(ModCategory.MISC);
        this.setLabel("/command", 1.0F);
        this.setShowLabelInList(false);
        this.addEventHandler(IntEvent4.class, this::onIntEvent4);
        this.addEventHandler(IntEvent1.class, this::onIntEvent1);
    }

    @Override
    public void saveCustomConfig(JsonObject jsonObject) {
        jsonObject.add("autoTextOptions", this.autoTextOptions);
    }

    public void onIntEvent4(IntEvent4 intEvent4) {
        for (Setting setting : this.getSettings()) {
            if (setting.getName() != null && Keyboard.getKeyName(setting.getKeyCode()) != null && intEvent4.getIntValue() == setting.getKeyCode() && this.recentUses.size() < 5) {
                this.mc.thePlayer.sendChatMessage(setting.getStringValue());
                this.recentUses.add(System.currentTimeMillis());
            } else {
                this.recentUses.removeIf(longValue -> longValue < System.currentTimeMillis() - 2000L);
            }
        }
    }

    public void onIntEvent1(IntEvent1 intEvent1) {
        for (Setting setting : this.getSettings()) {
            if (setting.getName() != null && Mouse.getButtonName(setting.getKeyCode()) != null && Mouse.isButtonDown(setting.getKeyCode())) {
                if (setting.hasMouseBind() && setting.getKeyCode() != 0 && intEvent1.getEventButton() == setting.getKeyCode() && this.recentUses.size() < 5) {
                    this.mc.thePlayer.sendChatMessage(setting.getStringValue());
                    this.recentUses.add(System.currentTimeMillis());
                } else {
                    this.recentUses.removeIf(longValue -> longValue < System.currentTimeMillis() - 2000L);
                }
            }
        }
    }

    @Override
    public boolean loadCustomConfig(JsonObject jsonObject) {
        JsonElement jsonElement = jsonObject.getAsJsonObject().get("autoTextOptions");

        for (int i = 1; i < jsonElement.getAsJsonObject().size() + 1; i++) {
            JsonElement jsonElement2 = jsonObject.getAsJsonObject().get("autoTextOptions").getAsJsonObject().get("Hot key " + i);
            if (!this.hasSetting("Hot key " + i)) {
                int asInt = jsonElement2.getAsJsonObject().get("keycode").getAsInt();
                boolean asBoolean = jsonElement2.getAsJsonObject().get("mouse").getAsBoolean();
                String asString = jsonElement2.getAsJsonObject().get("command").getAsString();
                Setting setting = new Setting(this.getSettings(), "Hot key " + i);
                setting.enableKeyCode(asInt);
                setting.setMouseBind(asBoolean);
                setting.changeValue(asString);
                this.hotkeySettings.add(setting);
                this.nextHotkeyIndex++;
            }
        }

        return true;
    }

    @Override
    public void beforeSave() {
        this.autoTextOptions = new JsonObject();
    }

    public void sendMessage(String str) {
        if (this.mc.isIntegratedServerRunning()) {
            this.mc.thePlayer.sendChatMessage(str);
        } else {
            if (this.isRestricted()) {
                if (this.isAllowedCommand(str)) {
                    this.mc.thePlayer.sendChatMessage(str);
                } else {
                    CheatBreaker.getInstance()
                        .getModManager()
                        .notificationsMod
                        .addNotification("error", EnumChatFormatting.RED + "\"" + str + "\" is not allowed on this server.", 3000L);
                }
            } else {
                this.mc.thePlayer.sendChatMessage(str);
            }
        }
    }

    public String getActiveHotkey() {
        return this.activeHotkey;
    }
}
