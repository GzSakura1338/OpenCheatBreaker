package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.IntEvent5;
import com.cheatbreaker.client.event.S02PacketChatEvent;
import com.cheatbreaker.client.event.ScaledResolutionEvent3;
import com.cheatbreaker.client.event.ScaledResolutionEvent6;
import com.cheatbreaker.client.event.ScaledResolutionEvent7;
import com.cheatbreaker.client.event.SimpleEvent18;
import com.cheatbreaker.client.event.SimpleEvent26;
import com.cheatbreaker.client.event.SimpleEvent4;
import com.cheatbreaker.client.event.SimpleEvent7;
import com.cheatbreaker.client.event.StringEvent1;
import com.cheatbreaker.client.event.WorldClientEvent;
import com.cheatbreaker.client.mod.AbstractHudMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import com.cheatbreaker.client.ui.AnchorPoint;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.optifine.GlStateManager;

public class ChatMod extends AbstractHudMod {
    public Pattern allColorsPattern = Pattern.compile("(?i)\u00a7[0-9A-F]");
    public Pattern notNeutralColorsPattern = Pattern.compile("(?i)\u00a7[1-69A-E]");
    public Map<String, EnumChatFormatting> colorsByName = new HashMap<String, EnumChatFormatting>();
    public String[][] welcomeLinks = new String[][]{{"Website", "www.cheatbreaker.net"}, {"Discord", "discord.cheatbreaker.net"}, {"GitHub", "www.github.com/CheatBreakerNet"}};
    public List<Long> mentionTimes = new ArrayList<Long>();
    public int visibleLineCount = 0;
    public GuiNewChat guiNewChat = new GuiNewChat(this.mc);
    public Setting generalOptions;
    public Setting masterOpacity;
    public Setting unlimitedChatHistory;
    public Setting clearChatOnRelog;
    public Setting backgroundOptions;
    public Setting showBackground;
    public Setting showInputFieldBackground;
    public Setting backgroundWidthType;
    public Setting backgroundColor;
    public Setting textOptions;
    public Setting textShadow;
    public Setting stripColorFormatting;
    public Setting textColor;
    public Setting animationOptions;
    public Setting smoothChat;
    public Setting smoothChatSpeed;
    public Setting highlightingOptions;
    public Setting highlightOwnName;
    public Setting highlightColour;
    public Setting playNotificationSound;
    public Setting positionOptions;
    public Setting chatHeightPosition;

    public void onScaledResolutionEvent3(ScaledResolutionEvent3 scaledResolutionEvent3) {
        int n;
        if (!this.shouldRender()) {
            return;
        }
        int n2 = this.mc.ingameGUI.getChatGUI().getLineCount();
        int n3 = 0;
        for (n = 0; n + this.mc.ingameGUI.getChatGUI().scrollPos < this.mc.ingameGUI.getChatGUI().visibleMessages.size() && n < n2; ++n) {
            ChatLine chatLine = this.mc.ingameGUI.getChatGUI().visibleMessages.get(n);
            if (chatLine == null || this.mc.ingameGUI.getUpdateCounter() - chatLine.getUpdatedCounter() >= 200) continue;
            ++n3;
        }
        this.visibleLineCount = n3;
        if (this.visibleLineCount != 0 || !(this.mc.currentScreen instanceof ModsScreen)) {
            GlStateManager.pushMatrix();
            this.applyHudTransformForSize(scaledResolutionEvent3.getScaledResolution());
            n = this.mc.currentScreen instanceof GuiChat ? this.mc.ingameGUI.getChatGUI().visibleMessages.size() * 9 : -(-this.visibleLineCount * 9);
            this.mc.ingameGUI.getChatGUI().drawChat(this.mc.ingameGUI.getUpdateCounter(), this.smoothChat.getBooleanValue());
            this.setHudSize((float)this.mc.ingameGUI.getChatGUI().getChatWidth() + 4.0f * this.mc.ingameGUI.getChatGUI().getChatScale(), (float)Math.min(this.mc.ingameGUI.getChatGUI().getChatHeight(), n) * this.mc.ingameGUI.getChatGUI().getChatScale());
            GlStateManager.popMatrix();
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        Minecraft.getMinecraft().ingameGUI.getChatGUI().refreshChat();
    }

    public void onS02PacketChatEvent(S02PacketChatEvent s02PacketChatEvent) {
        if (s02PacketChatEvent.getS02PacketChat().getMessage().getUnformattedText().contains(this.mc.getSession().getUsername()) && this.highlightOwnName.getBooleanValue() && this.playNotificationSound.getBooleanValue() && !CheatBreaker.getInstance().getKeybindSettings().muteCheatbreakerSounds.getBooleanValue() && this.mentionTimes.size() < 1) {
            CheatBreaker.getInstance().getAudioManager().playSound("message");
            this.mentionTimes.add(System.currentTimeMillis());
        }
    }

    public void onStringEvent1(StringEvent1 stringEvent1) {
        if (!stringEvent1.isCancelled()) {
            this.mentionTimes.add(System.currentTimeMillis());
        }
    }

    public EnumChatFormatting getHighlightFormatting() {
        return this.colorsByName.get(this.highlightColour.getStringValue());
    }

    public EnumChatFormatting getLastFormatting(String string) {
        String string2 = "\u00a7";
        if (string.contains(string2)) {
            string = string.substring(string.lastIndexOf(string2));
        }
        return this.getFormattingByCode(string.replaceAll(string2, ""));
    }

    public void onScaledResolutionEvent7(ScaledResolutionEvent7 scaledResolutionEvent7) {
        if (!this.isRenderHud()) {
            return;
        }
        if (this.visibleLineCount == 0 && this.mc.currentScreen instanceof ModsScreen) {
            GlStateManager.pushMatrix();
            this.applyHudTransformForSize(scaledResolutionEvent7.getScaledResolution());
            this.guiNewChat.drawChat(this.mc.ingameGUI.getUpdateCounter(), false);
            this.setHudSize((float)this.mc.ingameGUI.getChatGUI().getChatWidth() + 4.0f * this.mc.ingameGUI.getChatGUI().getChatScale(), (float)Math.min(this.mc.ingameGUI.getChatGUI().getChatHeight(), this.guiNewChat.visibleMessages.size() * 9) * this.mc.ingameGUI.getChatGUI().getChatScale());
            GlStateManager.popMatrix();
        }
    }

    public void printWelcomeMessage() {
        this.guiNewChat.visibleMessages.clear();
        StringBuilder stringBuilder = new StringBuilder("\u00a77\u00a7m");
        while ((double)this.mc.ingameGUI.getChatGUI().getChatScale() >= 0.12 && (float)this.mc.fontRendererObj.getStringWidth(stringBuilder.toString()) * this.mc.ingameGUI.getChatGUI().getChatScale() < (float)(this.mc.ingameGUI.getChatGUI().getChatWidth() - 6)) {
            stringBuilder.append("-");
        }
        if ((double)this.mc.ingameGUI.getChatGUI().getChatScale() >= 0.12) {
            this.guiNewChat.printMessage(new ChatComponentText(stringBuilder.toString()));
            this.guiNewChat.printMessage(new ChatComponentText(""));
        }
        this.guiNewChat.printMessage(new ChatComponentText("Welcome to \u00a7cCheatBreaker\u00a7f!"));
        this.guiNewChat.printMessage(new ChatComponentText(""));
        for (String[] stringArray : this.welcomeLinks) {
            this.guiNewChat.printMessage(new ChatComponentText("- \u00a7c" + stringArray[0] + ": \u00a7f" + stringArray[1]));
        }
        if ((double)this.mc.ingameGUI.getChatGUI().getChatScale() >= 0.12) {
            this.guiNewChat.printMessage(new ChatComponentText(""));
            this.guiNewChat.printMessage(new ChatComponentText(stringBuilder.toString()));
        }
    }

    public ChatMod() throws java.io.IOException {
        super("Chat");
        this.setAnchorPointAndDefault(AnchorPoint.LEFT_BOTTOM);
        this.setTranslationAndDefault(0.0f, -38.0f);
        this.setUseDefaultTranslation(true);
        this.hudOnly = false;
        this.setRenderHudAndDefault(false);
        this.generalOptions = new SettingGroup(this, "General Options").changeValue(true);
        this.masterOpacity = new Setting(this, "Master Opacity").withHierarchyParents(this.generalOptions).changeValue(Float.valueOf(100.0f)).setRange(Float.valueOf(0.05f), Float.valueOf(100.0f)).setUnit("%").setIconNames("circle-hollow-64", "circle-64").setIconScales(7.5f, 7.5f).setIconXOffsets(-0.25f, -2.75f).setIconYOffsets(-0.75f, -0.75f);
        this.unlimitedChatHistory = new Setting(this, "Unlimited Chat History").withHierarchyParents(this.generalOptions).changeValue(false).setCustomizationLevel((Object)SettingsLevel.MEDIUM).setAliases("Infinite Chat History", "Infinite Chat Scrolling");
        this.clearChatOnRelog = new Setting(this, "Clear Chat on Relog").withHierarchyParents(this.generalOptions).changeValue(true).setCustomizationLevel((Object)SettingsLevel.MEDIUM);
        this.backgroundOptions = new SettingGroup(this, "Background Options").changeValue(false);
        this.showBackground = new Setting(this, "Show Background").setDescription("Draw a background behind chat.").withHierarchyParents(this.backgroundOptions).changeValue("ON").setStringValues("ON", "While Typing", "OFF").setAliases("Fastchat", "Fast Chat");
        this.showInputFieldBackground = new Setting(this, "Show Input Field Background").setDescription("Draw a background behind the chat text input field.").withHierarchyParents(this.backgroundOptions).changeValue(true);
        this.backgroundWidthType = new Setting(this, "Background Width Type").withHierarchyParents(this.backgroundOptions).changeValue("Full").setStringValues("Full", "Compact").setCondition(() -> !this.showBackground.getValue().equals("OFF"));
        this.backgroundColor = new Setting(this, "Background Color").withHierarchyParents(this.backgroundOptions).changeValue(Integer.MIN_VALUE).setRange(Integer.MIN_VALUE, Integer.MAX_VALUE).setCondition(() -> !this.showBackground.getValue().equals("OFF") || this.showInputFieldBackground.getBooleanValue());
        this.textOptions = new SettingGroup(this, "Text Options").changeValue(false);
        this.textShadow = new Setting(this, "Text Shadow").withHierarchyParents(this.textOptions).changeValue(true);
        this.stripColorFormatting = new Setting(this, "Strip Color Formatting").setDescription("Remove all color formatting so you can set your own color.").changeValue("OFF").setStringValues("OFF", "Not Neutral", "ALL").withHierarchyParents(this.textOptions).setCustomizationLevel((Object)SettingsLevel.MEDIUM);
        this.textColor = new Setting(this, "Text Color").withHierarchyParents(this.textOptions).changeValue(-1).setRange(Integer.MIN_VALUE, Integer.MAX_VALUE).setCondition(() -> !this.stripColorFormatting.getValue().equals("OFF"));
        this.animationOptions = new SettingGroup(this, "Animation Options").changeValue(false).setCustomizationLevel((Object)SettingsLevel.MEDIUM);
        this.smoothChat = new Setting(this, "Smooth Chat").withHierarchyParents(this.animationOptions).changeValue(false).setCustomizationLevel((Object)SettingsLevel.MEDIUM);
        this.smoothChatSpeed = new Setting(this, "Smooth Chat Speed").withHierarchyParents(this.animationOptions).changeValue(Float.valueOf(8.0f)).setRange(Float.valueOf(0.25f), Float.valueOf(10.0f)).withHierarchyParents(this.smoothChat);
        this.highlightingOptions = new SettingGroup(this, "Highlighting Options").changeValue(false);
        this.highlightOwnName = new Setting(this, "Highlight Own Name").withHierarchyParents(this.highlightingOptions).changeValue(false);
        this.fillColorsByName();
        ArrayList<String> arrayList = new ArrayList<String>(this.colorsByName.keySet());
        arrayList.add("None");
        this.highlightColour = new Setting(this, "Highlight Colour").withHierarchyParents(this.highlightingOptions).changeValue("None").setStringValues(arrayList.toArray(new String[0])).withHierarchyParents(this.highlightOwnName);
        this.playNotificationSound = new Setting(this, "Play Notification Sound").withHierarchyParents(this.highlightingOptions).changeValue(false).withHierarchyParents(this.highlightOwnName);
        this.positionOptions = new SettingGroup(this, "Position Options").changeValue(false).setCustomizationLevel((Object)SettingsLevel.MEDIUM).setCondition(() -> !this.isRenderHud());
        this.chatHeightPosition = new Setting(this, "Chat Height Position").withHierarchyParents(this.positionOptions).changeValue(Float.valueOf(0.0f)).setRange(Float.valueOf(-14.0f), Float.valueOf(32.0f)).setUnit("px").setCustomizationLevel((Object)SettingsLevel.MEDIUM).setCondition(() -> !this.isRenderHud());
        this.setDescription("Move and customize the Minecraft chat to your liking.");
        this.setAuthors("LlamaLad7 (Smooth Chat)");
        this.setCategories(ModCategory.HUD, ModCategory.SERVER);
        this.setShowLabelInList(false);
        this.addEventHandler(ScaledResolutionEvent7.class, this::onScaledResolutionEvent7);
        this.addEventHandler(ScaledResolutionEvent3.class, this::onScaledResolutionEvent3);
        this.addEventHandler(ScaledResolutionEvent6.class, this::onScaledResolutionEvent6);
        this.addEventHandler(StringEvent1.class, this::onStringEvent1);
        this.addEventHandler(S02PacketChatEvent.class, this::onS02PacketChatEvent);
        this.addEventHandler(SimpleEvent18.class, simpleEvent18 -> {
            this.printWelcomeMessage();
            if (!this.clearChatOnRelog.getBooleanValue()) {
                simpleEvent18.setCancelled(true);
            }
        });
        this.addEventHandler(IntEvent5.class, intEvent5 -> {
            if (!this.showInputFieldBackground.getBooleanValue()) {
                intEvent5.setCancelled(true);
            } else {
                intEvent5.setMIN_VALUE(this.backgroundColor.getColorValue());
            }
        });
        this.addEventHandler(ClientTickEvent.class, this::onClientTickEvent);
        this.addEventHandler(SimpleEvent4.class, simpleEvent4 -> this.printWelcomeMessage());
        this.addEventHandler(SimpleEvent7.class, simpleEvent7 -> {
            if (this.unlimitedChatHistory.getBooleanValue()) {
                simpleEvent7.setCancelled(true);
            }
        });
        this.addEventHandler(SimpleEvent26.class, simpleEvent26 -> this.printWelcomeMessage());
        this.addEventHandler(WorldClientEvent.class, worldClientEvent -> this.printWelcomeMessage());
        this.setEnabledByDefault(true);
    }

    public void fillColorsByName() {
        this.colorsByName.put("Black", EnumChatFormatting.BLACK);
        this.colorsByName.put("Dark Blue", EnumChatFormatting.DARK_BLUE);
        this.colorsByName.put("Dark Green", EnumChatFormatting.DARK_GREEN);
        this.colorsByName.put("Dark Aqua", EnumChatFormatting.DARK_AQUA);
        this.colorsByName.put("Dark Red", EnumChatFormatting.DARK_RED);
        this.colorsByName.put("Dark Purple", EnumChatFormatting.DARK_PURPLE);
        this.colorsByName.put("Gold", EnumChatFormatting.GOLD);
        this.colorsByName.put("Gray", EnumChatFormatting.GRAY);
        this.colorsByName.put("Dark Gray", EnumChatFormatting.DARK_GRAY);
        this.colorsByName.put("Blue", EnumChatFormatting.BLUE);
        this.colorsByName.put("Green", EnumChatFormatting.GREEN);
        this.colorsByName.put("Aqua", EnumChatFormatting.AQUA);
        this.colorsByName.put("Red", EnumChatFormatting.RED);
        this.colorsByName.put("Light Purple", EnumChatFormatting.LIGHT_PURPLE);
        this.colorsByName.put("Yellow", EnumChatFormatting.YELLOW);
        this.colorsByName.put("White", EnumChatFormatting.WHITE);
    }

    @Override
    public boolean shouldRender() {
        return this.mc.currentScreen instanceof GuiChat || super.shouldRender();
    }

    public String stripColors(String string) {
        if (string == null) {
            return null;
        }
        if (this.stripColorFormatting.getValue().equals("OFF")) {
            return string;
        }
        return (this.stripColorFormatting.getValue().equals("ALL") ? this.allColorsPattern : this.notNeutralColorsPattern).matcher(string).replaceAll("\u00a7r");
    }

    public void onScaledResolutionEvent6(ScaledResolutionEvent6 scaledResolutionEvent6) {
        if (this.hidden) {
            this.mc.fontRendererObj.drawShadowedString("Chat is hidden", 4.0f, (float)scaledResolutionEvent6.getScaledResolution().getScaledHeight() - 24.0f, -8947849);
        }
    }

    @Override
    public void renderPreview(int n, int n2, int n3, int n4) {
        Gui.drawRect(n + 10, n2 + n4 / 2 - 35, n + n3 - 10, n2 + n4 / 2 - 17, 0x6F000000);
        Minecraft.getMinecraft().fontRendererObj.drawShadowedString((Object)((Object)EnumChatFormatting.GREEN) + "Steve" + (Object)((Object)EnumChatFormatting.WHITE) + ": Hey.", n + 10, n2 + n4 / 2 - 34, -1);
        Minecraft.getMinecraft().fontRendererObj.drawShadowedString((Object)((Object)EnumChatFormatting.GREEN) + "Alex" + (Object)((Object)EnumChatFormatting.WHITE) + ": Hi!", n + 10, n2 + n4 / 2 - 25, -1);
    }

    @Override
    public void onEnable() throws java.io.IOException {
        super.onEnable();
        this.printWelcomeMessage();
        Minecraft.getMinecraft().ingameGUI.getChatGUI().refreshChat();
    }

    public void onClientTickEvent(ClientTickEvent clientTickEvent) {
        this.mentionTimes.removeIf(l -> l < System.currentTimeMillis() - 250L);
    }

    public EnumChatFormatting getFormattingByCode(String string) {
        for (EnumChatFormatting enumChatFormatting : EnumChatFormatting.values()) {
            if (enumChatFormatting.getFormattingCode() != string.charAt(0)) continue;
            return enumChatFormatting;
        }
        return EnumChatFormatting.RESET;
    }
}
