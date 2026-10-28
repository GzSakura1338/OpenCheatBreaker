package com.cheatbreaker.client.minimap;

import com.cheatbreaker.client.util.Translations;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import net.minecraft.client.Minecraft;

public class RadarSettings {
    public boolean showRadar = true;
    public boolean showHostiles = true;
    public boolean showPlayers = true;
    public boolean showNeutrals = false;
    public boolean showPlayerNames = true;
    public boolean outlineMobIcons = true;
    public boolean filterMobIcons = true;
    public boolean showPlayerHelmets = true;
    public boolean changed;
    public boolean showMobHelmets = true;
    public boolean randomMobs = true;
    public float fontScale = 1.0F;
    public Minecraft mc = Minecraft.getMinecraft();

    public boolean getBooleanOption(MinimapOption minimapOption) {
        switch (minimapOption) {
            case SHOWRADAR:
                return this.showRadar;
            case SHOWHOSTILES:
                return this.showHostiles;
            case SHOWPLAYERS:
                return this.showPlayers;
            case SHOWNEUTRALS:
                return this.showNeutrals;
            case SHOWPLAYERHELMETS:
                return this.showPlayerHelmets;
            case SHOWMOBHELMETS:
                return this.showMobHelmets;
            case SHOWPLAYERNAMES:
                return this.showPlayerNames;
            case RADAROUTLINES:
                return this.outlineMobIcons;
            case RADARFILTERING:
                return this.filterMobIcons;
            case RANDOMOBS:
                return this.randomMobs;
            default:
                throw new IllegalArgumentException("Add code to handle EnumOptionMinimap: " + minimapOption.getTranslationKey() + ". (possibly not a boolean)");
        }
    }

    public void save(PrintWriter printWriter) {
        printWriter.println("Show Radar:" + this.showRadar);
        printWriter.println("Show Hostiles:" + this.showHostiles);
        printWriter.println("Show Players:" + this.showPlayers);
        printWriter.println("Show Neutrals:" + this.showNeutrals);
        printWriter.println("Filter Mob Icons:" + this.filterMobIcons);
        printWriter.println("Outline Mob Icons:" + this.outlineMobIcons);
        printWriter.println("Show Player Helmets:" + this.showPlayerHelmets);
        printWriter.println("Show Mob Helmets:" + this.showMobHelmets);
        printWriter.println("Show Player Names:" + this.showPlayerNames);
        printWriter.println("Font Scale:" + this.fontScale);
        printWriter.println("Randomobs:" + this.randomMobs);
        printWriter.print("Hidden Mobs:");

        for (MobType mobType : MobType.values()) {
            if (mobType.flag && !mobType.selected) {
                printWriter.print(mobType.mobName + ",");
            }
        }

        for (CustomMob customMob : CustomMobs.customMobList) {
            if (!customMob.enabled) {
                printWriter.print(customMob.name + ",");
            }
        }

        printWriter.println();
    }

    public String getOptionLabel(MinimapOption minimapOption) {
        String str = Translations.translate(minimapOption.getTranslationKey()) + ": ";
        if (minimapOption.isBoolean()) {
            boolean booleanOption = this.getBooleanOption(minimapOption);
            return booleanOption ? str + Translations.translate("options.on") : str + Translations.translate("options.off");
        } else {
            return str;
        }
    }

    public void toggleOption(MinimapOption minimapOption, int n) {
        switch (minimapOption) {
            case SHOWRADAR:
                this.showRadar = !this.showRadar;
                break;
            case SHOWHOSTILES:
                this.showHostiles = !this.showHostiles;
                break;
            case SHOWPLAYERS:
                this.showPlayers = !this.showPlayers;
                break;
            case SHOWNEUTRALS:
                this.showNeutrals = !this.showNeutrals;
                break;
            case SHOWPLAYERHELMETS:
                this.showPlayerHelmets = !this.showPlayerHelmets;
                break;
            case SHOWMOBHELMETS:
                this.showMobHelmets = !this.showMobHelmets;
                break;
            case SHOWPLAYERNAMES:
                this.showPlayerNames = !this.showPlayerNames;
                break;
            case RADAROUTLINES:
                this.outlineMobIcons = !this.outlineMobIcons;
                break;
            case RADARFILTERING:
                this.filterMobIcons = !this.filterMobIcons;
                break;
            case RANDOMOBS:
                this.randomMobs = !this.randomMobs;
                break;
            default:
                throw new IllegalArgumentException("Add code to handle EnumOptionMinimap: " + minimapOption.getTranslationKey());
        }

        this.changed = true;
    }

    public void hideMobs(String str) {
        String[] split = str.split(",");

        for (int i = 0; i < split.length; i++) {
            MobType findMobType = MobType.findMobType(split[i]);
            if (findMobType != null) {
                findMobType.selected = false;
            } else {
                CustomMobs.setEnabled(split[i], false);
            }
        }
    }

    public boolean consumeChanged() {
        if (this.changed) {
            this.changed = false;
            return true;
        } else {
            return false;
        }
    }

    public void load(File file) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            String str;
            while ((str = bufferedReader.readLine()) != null) {
                String[] split = str.split(":");
                if (split[0].equals("Show Radar")) {
                    this.showRadar = Boolean.parseBoolean(split[1]);
                } else if (split[0].equals("Show Hostiles")) {
                    this.showHostiles = Boolean.parseBoolean(split[1]);
                } else if (split[0].equals("Show Players")) {
                    this.showPlayers = Boolean.parseBoolean(split[1]);
                } else if (split[0].equals("Show Neutrals")) {
                    this.showNeutrals = Boolean.parseBoolean(split[1]);
                } else if (split[0].equals("Filter Mob Icons")) {
                    this.filterMobIcons = Boolean.parseBoolean(split[1]);
                } else if (split[0].equals("Outline Mob Icons")) {
                    this.outlineMobIcons = Boolean.parseBoolean(split[1]);
                } else if (split[0].equals("Show Player Helmets")) {
                    this.showPlayerHelmets = Boolean.parseBoolean(split[1]);
                } else if (split[0].equals("Show Mob Helmets")) {
                    this.showMobHelmets = Boolean.parseBoolean(split[1]);
                } else if (split[0].equals("Show Player Names")) {
                    this.showPlayerNames = Boolean.parseBoolean(split[1]);
                } else if (split[0].equals("Font Scale")) {
                    this.fontScale = Float.parseFloat(split[1]);
                } else if (split[0].equals("Randomobs")) {
                    this.randomMobs = Boolean.parseBoolean(split[1]);
                } else if (split[0].equals("Hidden Mobs")) {
                    this.hideMobs(split[1]);
                }
            }

            bufferedReader.close();
        } catch (Exception e) {
        }
    }
}
