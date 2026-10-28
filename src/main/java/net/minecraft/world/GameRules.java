package net.minecraft.world;

import java.util.TreeMap;
import net.minecraft.nbt.NBTTagCompound;

public class GameRules {
    public TreeMap theGameRules = new TreeMap();

    public GameRules() {
        this.addGameRule("doFireTick", "true");
        this.addGameRule("mobGriefing", "true");
        this.addGameRule("keepInventory", "false");
        this.addGameRule("doMobSpawning", "true");
        this.addGameRule("doMobLoot", "true");
        this.addGameRule("doTileDrops", "true");
        this.addGameRule("commandBlockOutput", "true");
        this.addGameRule("naturalRegeneration", "true");
        this.addGameRule("doDaylightCycle", "true");
    }

    public void addGameRule(String str, String str2) {
        this.theGameRules.put(str, new GameRules.Value(str2));
    }

    public void setOrCreateGameRule(String str, String str2) {
        GameRules.Value value = (GameRules.Value)this.theGameRules.get(str);
        if (value != null) {
            value.setValue(str2);
        } else {
            this.addGameRule(str, str2);
        }
    }

    public String getGameRuleStringValue(String str) {
        GameRules.Value value = (GameRules.Value)this.theGameRules.get(str);
        return value != null ? value.getGameRuleStringValue() : "";
    }

    public boolean getGameRuleBooleanValue(String str) {
        GameRules.Value value = (GameRules.Value)this.theGameRules.get(str);
        return value != null ? value.getGameRuleBooleanValue() : false;
    }

    public NBTTagCompound writeGameRulesToNBT() {
        NBTTagCompound nbttagCompound = new NBTTagCompound();

        for (String str : (Iterable<String>)this.theGameRules.keySet()) {
            GameRules.Value value = (GameRules.Value)this.theGameRules.get(str);
            nbttagCompound.setString(str, value.getGameRuleStringValue());
        }

        return nbttagCompound;
    }

    public void readGameRulesFromNBT(NBTTagCompound nbttagCompound) {
        for (String str : (Iterable<String>)nbttagCompound.getKeySet()) {
            String string = nbttagCompound.getString(str);
            this.setOrCreateGameRule(str, string);
        }
    }

    public String[] getRules() {
        return (String[])this.theGameRules.keySet().toArray(new String[0]);
    }

    public boolean hasRule(String str) {
        return this.theGameRules.containsKey(str);
    }

    static class Value {
        public String valueString;
        public boolean valueBoolean;
        public int valueInteger;
        public double valueDouble;

        public Value(String str) {
            this.setValue(str);
        }

        public void setValue(String str) {
            this.valueString = str;
            if (str != null) {
                if (str.equals("false")) {
                    this.valueBoolean = false;
                    return;
                }

                if (str.equals("true")) {
                    this.valueBoolean = true;
                    return;
                }
            }

            this.valueBoolean = Boolean.parseBoolean(str);

            try {
                this.valueInteger = Integer.parseInt(str);
            } catch (NumberFormatException e) {
            }

            try {
                this.valueDouble = Double.parseDouble(str);
            } catch (NumberFormatException e) {
            }
        }

        public String getGameRuleStringValue() {
            return this.valueString;
        }

        public boolean getGameRuleBooleanValue() {
            return this.valueBoolean;
        }
    }
}
