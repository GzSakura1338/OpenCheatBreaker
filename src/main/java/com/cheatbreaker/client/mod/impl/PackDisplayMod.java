package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.IResourcePackEvent;
import com.cheatbreaker.client.mod.AbstractIconHudMod;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import com.cheatbreaker.client.util.PackNameUtils;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.IResourcePack;

public class PackDisplayMod extends AbstractIconHudMod {
    public Setting stripColorFormatting;
    public Setting removePrefixes;
    public String displayName;
    public Pattern allColorsPattern = Pattern.compile("(?i)§[0-9A-F]");
    public Setting rotationInterval;
    public Pattern notNeutralColorsPattern = Pattern.compile("(?i)§[1-69A-E]");
    public int ticksSinceRotation = 0;
    public int rotationIndex = 0;
    public Setting packOrder;
    public Setting removeFileExtensionString;
    public Setting rotationOrder;

    @Override
    public void onEnable() throws java.io.IOException {
        super.onEnable();
        this.updateIcon(PackNameUtils.getResourcePack(this.getPackIndex()));
        this.updateDisplayName();
    }

    @Override
    public String getDynamicText() {
        return this.displayName;
    }

    public void rotatePack() throws java.io.IOException {
        if (this.packOrder.getValue().equals("Rotate") && this.ticksSinceRotation > this.rotationInterval.getFloatValue() * 20.0F) {
            int n = PackNameUtils.getActivePackNames().split(",").length - 1;
            switch (this.rotationOrder.getStringValue()) {
                case "Top-Bottom":
                    this.rotationIndex--;
                    if (this.rotationIndex < 0) {
                        this.rotationIndex = n;
                    }
                    break;
                case "Bottom-Top":
                    this.rotationIndex++;
                    if (this.rotationIndex > n) {
                        this.rotationIndex = 0;
                    }
                    break;
                case "Randomized":
                    this.rotationIndex = (int)(Math.random() * (n + 1));
            }

            this.ticksSinceRotation = 0;
            this.updateIcon(PackNameUtils.getResourcePack(this.getPackIndex()));
            this.updateDisplayName();
        }
    }

    public void updateDisplayName() {
        String[] split = PackNameUtils.getActivePackNames().split(",");
        this.displayName = split[this.getPackIndex()];
        if (this.removeFileExtensionString.getBooleanValue()) {
            this.displayName = this.displayName.replace(".zip", "");
        }

        if (this.removePrefixes.getBooleanValue()) {
            this.displayName = this.displayName.substring(this.displayName.indexOf("!") + 1).substring(this.displayName.indexOf("#") + 1).trim();
            if (this.displayName.substring(0, 1).contains("§") && this.displayName.substring(2, 3).contains(" ")) {
                this.displayName = this.displayName.replaceFirst(" ", "");
            }
        }

        this.displayName = this.stripColors(this.displayName);
    }

    public void onClientTickEvent(ClientTickEvent clientTickEvent) { try {
        this.ticksSinceRotation++;
        this.rotatePack();
    } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } }

    public void updateIcon(IResourcePack resourcePack) throws java.io.IOException {
        this.setIcon(Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("texturepackicon", new DynamicTexture(resourcePack.getPackImage())));
    }

    public String stripColors(String str) {
        if (str == null) {
            return null;
        } else {
            return this.stripColorFormatting.getValue().equals("OFF")
                ? str
                : (this.stripColorFormatting.getValue().equals("ALL") ? this.allColorsPattern : this.notNeutralColorsPattern).matcher(str).replaceAll("§r");
        }
    }

    public PackDisplayMod() throws java.io.IOException {
        super("Pack Display");
        this.setDescription("Displays your active Resource Pack(s).");
        this.setAuthors("canelex");
        this.setLabel("Default");
        this.addEventHandler(ClientTickEvent.class, this::onClientTickEvent);
        this.addEventHandler(IResourcePackEvent.class, this::onIResourcePackEvent);
    }

    @Override
    public String getLabelText() {
        return "Pack";
    }

    public void onIResourcePackEvent(IResourcePackEvent resourcePackEvent) { try {
        this.updateIcon(PackNameUtils.getResourcePack(this.getPackIndex()));
        this.updateDisplayName();
    } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } }

    @Override
    public void createGeneralOptions() {
        super.createGeneralOptions();
        this.packOrder = new Setting(this, "Pack Order")
            .changeValue("Bottom")
            .setStringValues("Bottom", "Top", "Rotate")
            .setCondition(() -> PackNameUtils.getActivePackNames().split(",").length != 1)
            .setOnUpdate(obj -> { try {
                this.updateIcon(PackNameUtils.getResourcePack(this.getPackIndex()));
                this.updateDisplayName();
            } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } })
            .withParents(this.generalOptions);
        this.rotationOrder = new Setting(this, "Rotation Order")
            .changeValue("Top-Bottom")
            .setStringValues("Top-Bottom", "Bottom-Top", "Randomized")
            .setCondition(() -> this.packOrder.getValue().equals("Rotate") && PackNameUtils.getActivePackNames().split(",").length != 1)
            .setOnUpdate(obj -> { try {
                this.updateIcon(PackNameUtils.getResourcePack(this.getPackIndex()));
                this.updateDisplayName();
            } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } })
            .withParents(this.generalOptions);
        this.rotationInterval = new Setting(this, "Rotation Interval")
            .changeValue(10.0F)
            .setRange(1.0F, 60.0F)
            .setUnit("s")
            .setCondition(() -> this.packOrder.getValue().equals("Rotate") && PackNameUtils.getActivePackNames().split(",").length != 1)
            .withParents(this.generalOptions);
    }

    @Override
    public void createFormatOptions() {
        this.removeFileExtensionString = new Setting(this, "Remove file extension string")
            .setDescription("Removes the file extension suffix in an archive.")
            .changeValue(true)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .setOnUpdate(obj -> this.updateDisplayName())
            .withParents(this.formatOptions);
        this.removePrefixes = new Setting(this, "Remove prefixes")
            .setDescription("Removes ! and # prefixes from a pack.")
            .changeValue(true)
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .setOnUpdate(obj -> this.updateDisplayName())
            .withParents(this.formatOptions);
        this.stripColorFormatting = new Setting(this, "Strip Color Formatting")
            .setDescription("Remove all color formatting so you can set your own color.")
            .changeValue("OFF")
            .setStringValues("OFF", "Not Neutral", "ALL")
            .setCustomizationLevel(SettingsLevel.MEDIUM)
            .setOnUpdate(obj -> this.updateDisplayName())
            .withParents(this.formatOptions);
    }

    public int getPackIndex() {
        if (this.packOrder.getValue().equals("Bottom")) {
            return 0;
        }

        if (this.packOrder.getValue().equals("Rotate")) {
            return this.rotationIndex;
        }

        String[] split = PackNameUtils.getActivePackNames().split(",");
        return split.length - 1;
    }
}
