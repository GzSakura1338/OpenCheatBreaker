package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.SimpleEvent6;
import com.cheatbreaker.client.event.StringEvent3;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class HypixelCollectionMod extends AbstractMod {
    public Pattern friendRequestPattern = Pattern.compile(
        "§m----------------------------------------------------Friend request from (?<name>.+)\\[ACCEPT\\] - \\[DENY\\] - \\[IGNORE\\].*"
    );
    public Pattern bedDestructionPattern = Pattern.compile("BED DESTRUCTION > Your Bed .*");
    public String[] eliminationMessages = new String[]{"You have been eliminated!", "Reward Summary"};
    public Pattern guildJoinPattern = Pattern.compile("Guild > (?<name>.+) joined\\..*");
    public Pattern friendJoinPattern = Pattern.compile("Friend > (?<name>.+) joined\\..*");
    public List<String> winMessages = Arrays.asList(
        "1st Killer - ",
        "1st Place - ",
        "Winner: ",
        " - Damage Dealt - ",
        "Winning Team -",
        "1st - ",
        "Winners: ",
        "Winner: ",
        "Winning Team: ",
        " won the game!",
        "Top Seeker: ",
        "1st Place: ",
        "Last team standing!",
        "Winner #1 (",
        "Top Survivors",
        "Winners - ",
        "Sumo Duel - "
    );
    public Setting autoCommandOptions = new SettingGroup(this, "Auto Command Options");
    public Setting autoTip = new Setting(this, "Auto Tip").changeValue(false);
    public Setting autoFriend = new Setting(this, "Auto Friend").changeValue(false);
    public Setting autoFriendMode = new Setting(this, "Auto Friend Mode").changeValue("Accept").setStringValues("Accept", "Deny", "Ignore");
    public Setting setting = new Setting(this, "Auto Friend Delay")
        .setUnit("s")
        .changeValue(0.0F)
        .setRange(0.0F, 15.0F)
        .setCondition(this.autoFriend::getBooleanValue);
    public Setting autoChatOptions = new SettingGroup(this, "Auto Chat Options").changeValue(true);
    public Setting autoGg = new Setting(this, "Auto GG").changeValue(false);
    public Setting ggDelay = new Setting(this, "Send GG Message Delay")
        .setUnit("s")
        .changeValue(0.0F)
        .setRange(0.0F, 5.0F)
        .setCondition(this.autoGg::getBooleanValue);
    public Setting ggMessage = new Setting(this, "Auto GG String")
        .changeValue("gg")
        .setCondition(this.autoGg::getBooleanValue)
        .setCustomizationLevel(SettingsLevel.MEDIUM);
    public Setting autoGl = new Setting(this, "Auto GL").changeValue(false);
    public Setting glDelay = new Setting(this, "Send GL Message Delay")
        .setUnit("s")
        .changeValue(0.0F)
        .setRange(0.0F, 15.0F)
        .setCondition(this.autoGl::getBooleanValue);
    public Setting glMessage = new Setting(this, "Auto GL String")
        .changeValue("gl")
        .setCondition(this.autoGl::getBooleanValue)
        .setCustomizationLevel(SettingsLevel.MEDIUM);
    public Setting autoWb = new Setting(this, "Auto WB").changeValue(false);
    public Setting welcomeBackFriends = new Setting(this, "Welcome Back Friends").changeValue(true).setCondition(this.autoWb::getBooleanValue);
    public Setting welcomeBackGuild = new Setting(this, "Welcome Back Guild Members").changeValue(true).setCondition(this.autoWb::getBooleanValue);
    public Setting wbDelay = new Setting(this, "Send WB Message Delay")
        .setUnit("s")
        .changeValue(2.0F)
        .setRange(2.0F, 15.0F)
        .setCondition(this.autoWb::getBooleanValue);
    public Setting wbMessage = new Setting(this, "Auto WB String")
        .changeValue("Welcome back %USER%!")
        .setCondition(this.autoWb::getBooleanValue)
        .setCustomizationLevel(SettingsLevel.MEDIUM);
    public Setting chatFilterOptions = new SettingGroup(this, "Chat Filter Options").changeValue(true);
    public Setting hideGg = new Setting(this, "Hide GG").changeValue(false);
    public Setting hideGl = new Setting(this, "Hide GL").changeValue(false);
    public Setting bedWarsOptions = new SettingGroup(this, "Bed Wars Options").changeValue(true);
    public Setting hardcoreHearts = new Setting(this, "Hardcore Hearts")
        .setDescription("Changes your hearts to show hardcore hearts when you lose your own bed.")
        .changeValue(false)
        .setCustomizationLevel(SettingsLevel.SIMPLE);

    public void onSimpleEvent6(SimpleEvent6 simpleEvent6) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            if (Minecraft.getMinecraft().getCurrentServerData() != null) {
                if (this.autoTip.getBooleanValue() && Minecraft.getMinecraft().getCurrentServerData().serverIP.toLowerCase().contains("hypixel")) {
                    Minecraft.getMinecraft().thePlayer.sendChatMessage("/tip all");
                }
            }
        }
    }

    public HypixelCollectionMod() throws java.io.IOException {
        super("Hypixel Collection");
        this.addEventHandler(StringEvent3.class, this::onStringEvent3);
        this.addEventHandler(SimpleEvent6.class, this::onSimpleEvent6);
        this.setIcon(new ResourceLocation("client/icons/mods/hypixel.png"), 21, 40);
        this.setDescription("A collection of mods made for the Hypixel Network.");
        this.setAuthors("2Pi (Auto GG, Auto Friend, Auto Tip)", "Sk1er (Auto GL, Hardcore Hearts)", "Maximusbarcz (Auto WB)");
        this.setCategories(ModCategory.SERVER);
        this.setEnabledByDefault(true);
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent6());
            }
        }, TimeUnit.SECONDS.toMillis(15L), TimeUnit.MINUTES.toMillis(1L));
    }

    public void onStringEvent3(StringEvent3 stringEvent3) {
        String text = stringEvent3.getText();
        if (this.mc.getCurrentServerData() != null && this.mc.getCurrentServerData().serverIP.toLowerCase().contains("hypixel")) {
            if (this.shouldHideMessage(text)) {
                stringEvent3.setCancelled(true);
                return;
            }

            if (!text.contains(this.eliminationMessages[0]) && text.contains(this.eliminationMessages[1])) {
            }

            if (this.hardcoreHearts.getBooleanValue() && this.bedDestructionPattern.matcher(text).matches()) {
            }

            if (this.autoGg.getBooleanValue() && this.winMessages.stream().anyMatch(text::contains)) {
                this.sendChatDelayed("/achat " + this.ggMessage.getStringValue(), this.ggDelay.getFloatValue());
            }

            if (this.autoGl.getBooleanValue() && text.startsWith("The game starts in 5 seconds!")) {
                this.sendChatDelayed("/achat " + this.glMessage.getStringValue(), this.glDelay.getFloatValue());
            }

            Matcher matcher;
            if (this.autoWb.getBooleanValue() && this.welcomeBackGuild.getBooleanValue() && (matcher = this.guildJoinPattern.matcher(text)).matches()) {
                String group = matcher.group("name");
                this.sendChatDelayed("/gc " + this.wbMessage.getStringValue().replaceAll("%USER%", group), this.wbDelay.getFloatValue());
            }

            if (this.autoWb.getBooleanValue() && this.welcomeBackFriends.getBooleanValue() && (matcher = this.friendJoinPattern.matcher(text)).matches()) {
                String group = matcher.group("name");
                this.sendChatDelayed("/msg " + group + " " + this.wbMessage.getStringValue().replaceAll("%USER%", group), this.wbDelay.getFloatValue());
            }

            if (this.autoFriend.getBooleanValue() && (matcher = this.friendRequestPattern.matcher(text.replace("\n", ""))).matches()) {
                String group = matcher.group("name");
                if (group.startsWith("[")) {
                    group = group.substring(group.indexOf("] ") + 2);
                }

                this.sendChatDelayed("/friend " + this.autoFriendMode.getStringValue().toLowerCase() + " " + group, this.setting.getFloatValue());
            }
        }
    }

    public boolean shouldHideMessage(String str) {
        if (!this.autoTip.getBooleanValue()
            || !str.startsWith("You can't tip the same person")
                && !str.equals("Still processing your most recent request!")
                && !str.startsWith("You've already tipped that person")
                && !str.equals("You cannot tip yourself!")
                && !str.startsWith("You can only use the /tip command")
                && !str.equals("You are not allowed to use commands as a spectator!")
                && !str.equals("Slow down! You can only use /tip every few seconds.")
                && !str.startsWith("You've already tipped someone in the past hour in ")) {
            return this.hideGl.getBooleanValue() && Arrays.stream(str.split(" ")).anyMatch(str2 -> str2.equalsIgnoreCase("gl"))
                ? true
                : this.hideGg.getBooleanValue() && Arrays.stream(str.split(" ")).anyMatch(str2 -> str2.equalsIgnoreCase("gg"));
        } else {
            return true;
        }
    }

    public void sendChatDelayed(String str, float f) {
        new Thread(() -> {
            try {
                Thread.sleep((long)f * 1000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            this.mc.thePlayer.sendChatMessage(str);
        }).start();
    }
}
