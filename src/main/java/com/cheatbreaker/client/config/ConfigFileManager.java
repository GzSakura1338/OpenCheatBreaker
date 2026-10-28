package com.cheatbreaker.client.config;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.SimpleEvent1;
import com.cheatbreaker.client.event.SimpleEvent14;
import com.cheatbreaker.client.event.SimpleEvent33;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingType;
import com.cheatbreaker.client.radio.Station;
import com.cheatbreaker.client.theme.Theme;
import com.cheatbreaker.client.ui.AnchorPoint;
import com.cheatbreaker.client.ui.component.ColorSwatch;
import com.cheatbreaker.client.util.ClientVersion;
import com.cheatbreaker.client.util.Logging;
import com.cheatbreaker.client.util.SneakyThrow;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class ConfigFileManager {
    public CheatBreaker cheatBreaker = CheatBreaker.getInstance();
    public File configDirectory = new File(
        Minecraft.getMinecraft().mcDataDir
            + File.separator
            + "config"
            + File.separator
            + "cheatbreaker-client-"
            + ClientVersion.version
            + "-"
            + this.cheatBreaker.getBranch()
    );
    public boolean globalConfigLoadFailed;
    public File themesDirectory = new File(this.configDirectory + File.separator + "themes");
    public File globalConfigFile = new File(this.configDirectory + File.separator + "global.json");
    public File mutesFile = new File(this.configDirectory + File.separator + "mutes.cfg");
    public File defaultProfileFile = new File(this.configDirectory + File.separator + "default.json");
    public File profilesDirectory = new File(this.configDirectory + File.separator + "profiles");

    public void saveMutes(File file) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));

            for (UUID uuid : this.cheatBreaker.getNetHandler().getAutoMutedPlayers()) {
                bufferedWriter.write(uuid.toString());
                bufferedWriter.newLine();
            }

            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onSimpleEvent1(SimpleEvent1 simpleEvent1) {
        this.saveAll();
    }

    public JsonObject newJsonObject() {
        return new JsonObject();
    }

    public void saveProfile(String str) {
        try {
            if (!str.equalsIgnoreCase("default")) {
                Logging.info("Saving Profile: " + str);
                File file = new File(this.configDirectory + File.separator + "profiles");
                File file2 = !file.exists() && !file.mkdirs() ? null : new File(file + File.separator + str + ".json");
                ArrayList list = new ArrayList<>(this.cheatBreaker.getModManager().abstractModList);
                JsonObject newJsonObject = this.newJsonObject();

                for (AbstractMod abstractMod : (Iterable<AbstractMod>)list) {
                    abstractMod.beforeSave();
                    JsonObject newJsonObject2 = this.newJsonObject();
                    JsonObject newJsonObject3 = this.newJsonObject();
                    JsonObject newJsonObject4 = this.newJsonObject();
                    newJsonObject3.addProperty("state", abstractMod.isEnabled());
                    newJsonObject3.addProperty("wasRenderHUD", abstractMod.wasRenderHud());
                    if (abstractMod.getAnchorPoint() != null) {
                        newJsonObject3.addProperty("position", abstractMod.getAnchorPoint().getDisplayName());
                    }

                    newJsonObject3.addProperty("xTranslation", abstractMod.getTranslationX());
                    newJsonObject3.addProperty("yTranslation", abstractMod.getTranslationY());
                    newJsonObject3.addProperty("renderHUD", abstractMod.isRenderHud());

                    for (Setting setting : abstractMod.getSettings()) {
                        if (abstractMod == this.cheatBreaker.getModManager().autoTextMod) {
                            abstractMod.saveCustomSetting(setting);
                        } else if (setting.getType() == SettingType.INTEGER && setting.getName().endsWith("Keybind")) {
                            JsonObject newJsonObject5 = this.newJsonObject();
                            newJsonObject5.addProperty("mouse", setting.hasMouseBind());
                            this.addProperty(newJsonObject5, "keybind", setting.getValue());
                            newJsonObject4.add(setting.getName(), newJsonObject5);
                        } else if (setting.getType().equals(SettingType.INTEGER) && setting.getName().toLowerCase().contains("color")) {
                            JsonObject newJsonObject5 = this.newJsonObject();
                            newJsonObject5.addProperty("chroma", setting.isChroma());
                            newJsonObject5.addProperty("color", setting.getColorValue());
                            newJsonObject4.add(setting.getName(), newJsonObject5);
                        } else {
                            this.addProperty(newJsonObject4, setting.getName(), setting.getValue());
                        }
                    }

                    newJsonObject2.add("info", newJsonObject3);
                    newJsonObject2.add("settings", newJsonObject4);
                    newJsonObject.add(abstractMod.getName(), newJsonObject2);
                    abstractMod.saveCustomConfig(newJsonObject);
                }

                FileWriter fileWriter = new FileWriter(file2);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                fileWriter.write(gson.toJson(newJsonObject));
                fileWriter.close();
            }
        } catch (Throwable e) {
            throw SneakyThrow.sneakyThrow(e);
        }
    }

    public void createEmptyThemeFile(File file) {
        try {
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("{}");
            fileWriter.close();
        } catch (Throwable e) {
            throw SneakyThrow.sneakyThrow(e);
        }
    }

    public List<Theme> getCustomThemes() {
        Collection<Theme> values = CheatBreaker.getInstance().getThemes().values();
        return (List<Theme>)(values.stream()
            .filter(theme -> !theme.getThemeNameValue().equalsIgnoreCase("default dark") && !theme.getThemeNameValue().equalsIgnoreCase("default light"))
            .collect(Collectors.toList()));
    }

    public void createEmptyConfigFile(File file) {
        try {
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("{}");
            fileWriter.close();
        } catch (Throwable e) {
            throw SneakyThrow.sneakyThrow(e);
        }
    }

    public Theme getTheme(String str) {
        return this.cheatBreaker.themes.getOrDefault(str, null);
    }

    public void saveGlobalConfig(File file) {
        if (this.globalConfigLoadFailed) {
            Logging.warnTagged("ConfigManager", "Not saving global config '%s' because loading failed.", file);
        } else {
            try {
                ArrayList list = new ArrayList<>(this.cheatBreaker.getKeybindSettings().allSettings);
                JsonObject newJsonObject = this.newJsonObject();
                JsonObject newJsonObject2 = this.newJsonObject();
                JsonObject newJsonObject3 = this.newJsonObject();
                if (this.cheatBreaker.getConfigManager().profile != null && !this.cheatBreaker.getConfigManager().profile.getName().equals("default")) {
                    newJsonObject.addProperty("activeProfile", this.cheatBreaker.getConfigManager().profile.getName());
                    newJsonObject.addProperty("activeTheme", this.cheatBreaker.getConfigManager().theme.getThemeNameValue());
                }

                for (Setting setting : (Iterable<Setting>)list) {
                    if (setting.getName().toLowerCase().endsWith("color")) {
                        JsonObject newJsonObject4 = this.newJsonObject();
                        newJsonObject4.addProperty("color", setting.getColorValue());
                        newJsonObject4.addProperty("chroma", setting.isChroma());
                        newJsonObject3.add(setting.getName(), newJsonObject4);
                    } else {
                        this.addProperty(newJsonObject3, setting.getName(), setting.getValue());
                    }
                }

                for (KeyBinding keyBinding : Minecraft.getMinecraft().gameSettings.keyBindings) {
                    if (keyBinding.flag3) {
                        newJsonObject2.addProperty(keyBinding.getKeyDescription(), keyBinding.getKeyCode());
                    }
                }

                newJsonObject3.add("keybinds", newJsonObject2);
                String str = "";

                for (ColorSwatch colorSwatch : this.cheatBreaker.getKeybindSettings().savedColors) {
                    str = str
                        + colorSwatch.color
                        + (
                            this.cheatBreaker.getKeybindSettings().savedColors.indexOf(colorSwatch) == this.cheatBreaker.getKeybindSettings().savedColors.size() - 1
                                ? ""
                                : ","
                        );
                }

                if (!str.equals("")) {
                    newJsonObject.addProperty("favoriteColors", str);
                }

                StringBuilder sb = new StringBuilder();

                for (Station station : this.cheatBreaker.getRadioManager().getStationList()) {
                    if (station.isFavorite()) {
                        if (sb.length() != 0) {
                            sb.append(",");
                        }

                        sb.append(station.getName());
                    }
                }

                if (!sb.toString().equals("")) {
                    newJsonObject.addProperty("favoriteStations", sb.toString());
                }

                StringBuilder sb2 = new StringBuilder();

                for (int n : this.cheatBreaker.getModManager().xray.getVisibleBlockIds()) {
                    if (sb2.length() != 0) {
                        sb2.append(",");
                    }

                    sb2.append(n);
                }

                newJsonObject.addProperty("xrayBlocks", sb2.toString());
                StringBuilder sb3 = new StringBuilder();

                for (Profile profile : this.cheatBreaker.profiles) {
                    sb3.append("[").append(profile.getName()).append(",").append(profile.intValue).append("]");
                }

                newJsonObject.addProperty("profileIndexes", sb3.toString());
                newJsonObject.add("settings", newJsonObject3);
                newJsonObject.addProperty("currentCategory", CheatBreaker.getInstance().getKeybindSettings().modCategory.name());
                FileWriter fileWriter = new FileWriter(file);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                fileWriter.write(gson.toJson(newJsonObject));
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean hasTheme(String str) {
        return this.cheatBreaker.themes.getOrDefault(str, null) != null;
    }

    public void loadGlobalConfig(File file) {
        this.globalConfigLoadFailed = false;
        if (file.exists() && file.length() != 0L) {
            JsonObject jsonObject;
            try (FileReader fileReader = new FileReader(file)) {
                jsonObject = JsonParser.parseReader(fileReader).getAsJsonObject();
            } catch (Exception e) {
                this.globalConfigLoadFailed = true;
                Logging.errorTagged("ConfigManager", "Could not read global config '%s': %s", file, e);
                return;
            }

            try {
                JsonElement jsonElement = jsonObject.get("settings");
                JsonObject jsonObject2 = jsonElement != null && !jsonElement.isJsonNull() ? jsonElement.getAsJsonObject() : null;
                if (jsonObject.get("activeProfile") != null) {
                    String asString = jsonObject.get("activeProfile").getAsString();
                    File file2 = null;
                    File file3 = new File(this.configDirectory + File.separator + "profiles");
                    if (file3.exists() || file3.mkdirs()) {
                        file2 = new File(file3 + File.separator + asString + ".json");
                    }

                    if (file2 == null || !file2.exists()) {
                        this.saveProfile(asString);
                        this.loadGlobalConfig(file);
                        return;
                    }

                    Profile profile = null;

                    for (Profile profile2 : this.cheatBreaker.profiles) {
                        if (asString.equalsIgnoreCase(profile2.getName())) {
                            profile = profile2;
                        }
                    }

                    if (profile != null && !profile.getName().equalsIgnoreCase("default")) {
                        this.cheatBreaker.getConfigManager().profile = profile;
                    }
                }

                if (jsonObject.get("activeTheme") != null) {
                    String asString = jsonObject.get("activeTheme").getAsString();
                    if (asString.equalsIgnoreCase("default light")) {
                        this.cheatBreaker.getConfigManager().theme = this.cheatBreaker.configManager.getDefaultLightTheme();
                        Logging.debug("Set Active Theme to Default Light");
                    } else if (asString.equalsIgnoreCase("default dark")) {
                        this.cheatBreaker.getConfigManager().theme = this.cheatBreaker.configManager.getDefaultDarkTheme();
                        Logging.debug("Set Active Theme to Default Dark");
                    } else {
                        File file2 = null;
                        File file3 = new File(this.configDirectory + File.separator + "themes");
                        if (file3.exists() || file3.mkdirs()) {
                            file2 = new File(file3 + File.separator + asString + ".json");
                        }

                        if (file2 == null || !file2.exists()) {
                            this.saveTheme(asString);
                            return;
                        }

                        Theme orDefault = this.cheatBreaker.themes.getOrDefault(asString.toLowerCase(), null);
                        if (orDefault != null && !this.getTheme(orDefault.getThemeNameValue()).isBuiltIn()) {
                            this.cheatBreaker.getConfigManager().theme = orDefault;
                            Logging.debug("Set Active Theme: " + orDefault.getThemeNameValue());
                        }
                    }
                }

                if (jsonObject.get("currentCategory") != null) {
                    String asString = jsonObject.get("currentCategory").getAsString();
                    switch (asString) {
                        case "MISC":
                            CheatBreaker.getInstance().getKeybindSettings().modCategory = ModCategory.MISC;
                            break;
                        case "COMBAT":
                            CheatBreaker.getInstance().getKeybindSettings().modCategory = ModCategory.COMBAT;
                            break;
                        case "SERVER":
                            CheatBreaker.getInstance().getKeybindSettings().modCategory = ModCategory.SERVER;
                            break;
                        case "HUD":
                            CheatBreaker.getInstance().getKeybindSettings().modCategory = ModCategory.HUD;
                            break;
                        default:
                            CheatBreaker.getInstance().getKeybindSettings().modCategory = ModCategory.ALL;
                    }
                }

                if (jsonObject.get("favoriteColors") != null) {
                    String asString = jsonObject.get("favoriteColors").getAsString();
                    String[] split = asString.split(",");

                    for (String str : split) {
                        try {
                            this.cheatBreaker.getKeybindSettings().savedColors.add(new ColorSwatch(1.0F, Integer.parseInt(str), 1.0F));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (jsonObject.get("favoriteStations") != null) {
                    String asString = jsonObject.get("favoriteStations").getAsString();
                    String[] split = asString.split(",");

                    for (String str : split) {
                        try {
                            for (Station station : this.cheatBreaker.getRadioManager().getStationList()) {
                                if (station.getName().equalsIgnoreCase(str)) {
                                    station.setFavorite(true);
                                }
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (jsonObject.get("xrayBlocks") != null) {
                    String asString = jsonObject.get("xrayBlocks").getAsString();
                    this.cheatBreaker.getModManager().xray.getVisibleBlockIds().clear();
                    String[] split = asString.split(",");

                    for (String str : split) {
                        try {
                            this.cheatBreaker.getModManager().xray.getVisibleBlockIds().add(Integer.parseInt(str));
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if (jsonObject2 != null && jsonObject2.get("keybinds") != null && !jsonObject2.get("keybinds").isJsonNull()) {
                    JsonObject asJsonObject = jsonObject2.getAsJsonObject("keybinds");

                    for (Entry entry : asJsonObject.entrySet()) {
                        for (KeyBinding keyBinding : Minecraft.getMinecraft().gameSettings.keyBindings) {
                            if (keyBinding.flag3 && ((String)entry.getKey()).equalsIgnoreCase(keyBinding.getKeyDescription())) {
                                try {
                                    keyBinding.setKeyCode(((JsonElement)entry.getValue()).getAsInt());
                                } catch (Exception e) {
                                    this.globalConfigLoadFailed = true;
                                    Logging.errorTagged("ConfigManager", "Could not read global keybind '%s': %s", entry.getKey(), e);
                                }
                            }
                        }
                    }

                    KeyBinding.resetKeyBindingArrayAndHash();
                }

                if (jsonObject.get("profileIndexes") != null) {
                    String asString = jsonObject.get("profileIndexes").getAsString();
                    String[] split = asString.split("]\\[");

                    for (String str : split) {
                        str = str.replaceFirst("\\[", "").replaceAll("\\]", "");
                        String[] split2 = str.split(",", 2);

                        try {
                            int parseInt = Integer.parseInt(split2[1]);

                            for (Profile profile : this.cheatBreaker.profiles) {
                                if (parseInt != 0 && profile.getName().equalsIgnoreCase(split2[0])) {
                                    profile.intValue = parseInt;
                                }
                            }
                        } catch (NumberFormatException e) {
                            Logging.errorTagged("Error in attempting to read the profile index", e);
                        }
                    }
                }

                if (jsonObject2 != null) {
                    for (Setting setting : this.cheatBreaker.getKeybindSettings().allSettings) {
                        if (setting != null) {
                            JsonElement jsonElement2 = jsonObject2.get(setting.getName());
                            if (jsonElement2 != null && !jsonElement2.isJsonNull()) {
                                try {
                                    switch (setting.getType()) {
                                        case BOOLEAN:
                                            setting.changeValue(jsonElement2.getAsBoolean());
                                            break;
                                        case INTEGER:
                                            if (setting.getName().toLowerCase().contains("color")) {
                                                JsonObject asJsonObject = jsonElement2.getAsJsonObject();
                                                boolean asBoolean = asJsonObject.get("chroma").getAsBoolean();
                                                int asInt = asJsonObject.get("color").getAsInt();
                                                if (asInt <= (Integer)setting.getMaximumValue() && asInt >= (Integer)setting.getMinimumValue()) {
                                                    setting.chroma = asBoolean;
                                                    setting.changeValue(asInt);
                                                }
                                            } else {
                                                int asInt = jsonElement2.getAsInt();
                                                setting.chroma = false;
                                                if (setting.getName().toLowerCase().endsWith("keybind")) {
                                                    setting.changeValue(jsonElement2.getAsInt());
                                                } else if (asInt <= (Integer)setting.getMaximumValue() && asInt >= (Integer)setting.getMinimumValue()) {
                                                    setting.changeValue(asInt);
                                                }
                                            }
                                            break;
                                        case FLOAT:
                                            float asFloat = jsonElement2.getAsFloat();
                                            if (asFloat <= (Float)setting.getMaximumValue() && asFloat >= (Float)setting.getMinimumValue()) {
                                                setting.changeValue(asFloat);
                                            }
                                            break;
                                        case DOUBLE:
                                            double asDouble = jsonElement2.getAsDouble();
                                            if (asDouble <= (Double)setting.getMaximumValue() && asDouble >= (Double)setting.getMinimumValue()) {
                                                setting.changeValue(asDouble);
                                            }
                                            break;
                                        case ARRAYLIST:
                                            JsonArray jsonArray;
                                            if (jsonElement2.isJsonArray()) {
                                                jsonArray = jsonElement2.getAsJsonArray();
                                            } else {
                                                if (!jsonElement2.isJsonPrimitive() || !jsonElement2.getAsJsonPrimitive().isString()) {
                                                    throw new IllegalStateException("Expected an integer array or a serialized integer array");
                                                }

                                                jsonArray = JsonParser.parseString(jsonElement2.getAsString()).getAsJsonArray();
                                            }

                                            ArrayList list = new ArrayList();

                                            for (JsonElement jsonElement3 : jsonArray) {
                                                list.add(jsonElement3.getAsInt());
                                            }

                                            setting.changeValue(list);
                                            break;
                                        case STRING_ARRAY:
                                            boolean flag = false;

                                            for (String str : setting.getStringValues()) {
                                                if (str.equalsIgnoreCase(jsonElement2.getAsString())) {
                                                    flag = true;
                                                }
                                            }

                                            if (flag) {
                                                setting.changeValue(jsonElement2.getAsString());
                                            }
                                            break;
                                        case STRING:
                                            setting.changeValue(jsonElement2.getAsString());
                                    }
                                } catch (Exception e) {
                                    this.globalConfigLoadFailed = true;
                                    Logging.errorTagged("ConfigManager", "Could not read global setting '%s': %s", setting.getName(), e);
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                this.globalConfigLoadFailed = true;
                Logging.errorTagged("ConfigManager", "Could not finish reading global config '%s': %s", file, e);
            }

            this.saveGlobalConfig(file);
        } else {
            this.saveGlobalConfig(file);
        }
    }

    public ConfigFileManager() {
        this.cheatBreaker.getEventBus().checkClass2(SimpleEvent1.class, this::onSimpleEvent1);
    }

    public void repairTheme(String str, File file, JsonObject jsonObject) {
        int n;
        if (jsonObject.get("primaryBackgroundColor") == null) {
            n = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getPrimaryBackgroundColorValue();
        } else {
            n = jsonObject.get("primaryBackgroundColor").getAsInt();
        }

        int n2;
        if (jsonObject.get("menuBackgroundColor") == null) {
            n2 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getMenuBackgroundColorValue();
        } else {
            n2 = jsonObject.get("menuBackgroundColor").getAsInt();
        }

        int n3;
        if (jsonObject.get("enabledBackgroundColor") == null) {
            n3 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getEnabledModColorValue();
        } else {
            n3 = jsonObject.get("enabledBackgroundColor").getAsInt();
        }

        int n4;
        if (jsonObject.get("inactiveBackgroundColor") == null) {
            n4 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getDisabledModColorValue();
        } else {
            n4 = jsonObject.get("inactiveBackgroundColor").getAsInt();
        }

        int n5;
        if (jsonObject.get("modNameTextColor") == null) {
            n5 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getModNameTextColorValue();
        } else {
            n5 = jsonObject.get("modNameTextColor").getAsInt();
        }

        float f;
        if (jsonObject.get("buttonBorderThickness") == null) {
            f = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getHoverBorderThicknessValue();
        } else {
            f = jsonObject.get("buttonBorderThickness").getAsInt();
        }

        float f2;
        if (jsonObject.get("buttonCornerRadius") == null) {
            f2 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getButtonCornerRadiusValue();
        } else {
            f2 = jsonObject.get("buttonCornerRadius").getAsInt();
        }

        int n6;
        if (jsonObject.get("buttonBackgroundColor") == null) {
            n6 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getButtonBackgroundColorValue();
        } else {
            n6 = jsonObject.get("buttonBackgroundColor").getAsInt();
        }

        int n7;
        if (jsonObject.get("selectedButtonColor") == null) {
            n7 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getSelectedButtonColorValue();
        } else {
            n7 = jsonObject.get("selectedButtonColor").getAsInt();
        }

        int n8;
        if (jsonObject.get("disabledButtonColor") == null) {
            n8 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getSelectedButtonColorValue();
        } else {
            n8 = jsonObject.get("disabledButtonColor").getAsInt();
        }

        int n9;
        if (jsonObject.get("sliderTrackColor") == null) {
            n9 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getTrackColorValue();
        } else {
            n9 = jsonObject.get("sliderTrackColor").getAsInt();
        }

        int n10;
        if (jsonObject.get("sliderThumbColor") == null) {
            n10 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getThumbColorValue();
        } else {
            n10 = jsonObject.get("sliderThumbColor").getAsInt();
        }

        int n11;
        if (jsonObject.get("sliderThumbOutlineColor") == null) {
            n11 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getThumbOutlineColorValue();
        } else {
            n11 = jsonObject.get("sliderThumbOutlineColor").getAsInt();
        }

        int n12;
        if (jsonObject.get("modsButtonHighlightColor") == null) {
            n12 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getModsHighlightColorValue();
        } else {
            n12 = jsonObject.get("modsButtonHighlightColor").getAsInt();
        }

        int n13;
        if (jsonObject.get("normalButtonHighlightColor") == null) {
            n13 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getNormalHighlightColorValue();
        } else {
            n13 = jsonObject.get("normalButtonHighlightColor").getAsInt();
        }

        int n14;
        if (jsonObject.get("labelButtonHighlightColor") == null) {
            n14 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getLabelHighlightColorValue();
        } else {
            n14 = jsonObject.get("labelButtonHighlightColor").getAsInt();
        }

        int n15;
        if (jsonObject.get("enableHighlightButtonColor") == null) {
            n15 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getEnableModHighlightColorValue();
        } else {
            n15 = jsonObject.get("enableHighlightButtonColor").getAsInt();
        }

        int n16;
        if (jsonObject.get("disableHighlightButtonColor") == null) {
            n16 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getDisableModHighlightColorValue();
        } else {
            n16 = jsonObject.get("disableHighlightButtonColor").getAsInt();
        }

        int n17;
        if (jsonObject.get("selectedModOutlineColor") == null) {
            n17 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getSelectedOutlineColorValue();
        } else {
            n17 = jsonObject.get("selectedModOutlineColor").getAsInt();
        }

        int n18;
        if (jsonObject.get("selectedModOverlayColor") == null) {
            n18 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getSelectedOverlayColorValue();
        } else {
            n18 = jsonObject.get("selectedModOverlayColor").getAsInt();
        }

        int n19;
        if (jsonObject.get("neighboringModOverlayColor") == null) {
            n19 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getNeighboringModOverlayColorValue();
        } else {
            n19 = jsonObject.get("neighboringModOverlayColor").getAsInt();
        }

        int n20;
        if (jsonObject.get("unselectedModOutlineColor") == null) {
            n20 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getUnselectedOutlineColorValue();
        } else {
            n20 = jsonObject.get("unselectedModOutlineColor").getAsInt();
        }

        int n21;
        if (jsonObject.get("unselectedModOverlayColor") == null) {
            n21 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getUnselectedOverlayColorValue();
        } else {
            n21 = jsonObject.get("unselectedModOverlayColor").getAsInt();
        }

        int n22;
        if (jsonObject.get("selectedModHoveringOutlineColor") == null) {
            n22 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getSelecHoveringOlColorValue();
        } else {
            n22 = jsonObject.get("selectedModHoveringOutlineColor").getAsInt();
        }

        int n23;
        if (jsonObject.get("selectedModHoveringOverlayColor") == null) {
            n23 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getSelecHoveringOvColorValue();
        } else {
            n23 = jsonObject.get("selectedModHoveringOverlayColor").getAsInt();
        }

        int n24;
        if (jsonObject.get("unselectedModHoveringOutlineColor") == null) {
            n24 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getUnselecHoveringOlColorValue();
        } else {
            n24 = jsonObject.get("unselectedModHoveringOutlineColor").getAsInt();
        }

        int n25;
        if (jsonObject.get("unselectedModHoveringOverlayColor") == null) {
            n25 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getUnselecHoveringOvColorValue();
        } else {
            n25 = jsonObject.get("unselectedModHoveringOverlayColor").getAsInt();
        }

        int n26;
        if (jsonObject.get("snapLineColor") == null) {
            n26 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getSnapLineColorValue();
        } else {
            n26 = jsonObject.get("snapLineColor").getAsInt();
        }

        int n27;
        if (jsonObject.get("hudBorderColor") == null) {
            n27 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getHudBorderColorValue();
        } else {
            n27 = jsonObject.get("hudBorderColor").getAsInt();
        }

        int n28;
        if (jsonObject.get("dragColor") == null) {
            n28 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getSelectionColorValue();
        } else {
            n28 = jsonObject.get("dragColor").getAsInt();
        }

        int n29;
        if (jsonObject.get("dragOutlineColor") == null) {
            n29 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getSelectionOutlineColorValue();
        } else {
            n29 = jsonObject.get("dragOutlineColor").getAsInt();
        }

        int n30;
        if (jsonObject.get("scrollbarThumbColor") == null) {
            n30 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getThumbColorValue2();
        } else {
            n30 = jsonObject.get("scrollbarThumbColor").getAsInt();
        }

        int n31;
        if (jsonObject.get("scrollbarThumbHoverColor") == null) {
            n31 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getThumbHoverColorValue();
        } else {
            n31 = jsonObject.get("scrollbarThumbHoverColor").getAsInt();
        }

        int n32;
        if (jsonObject.get("scrollbarTrackColor") == null) {
            n32 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getTrackColorValue2();
        } else {
            n32 = jsonObject.get("scrollbarTrackColor").getAsInt();
        }

        int n33;
        if (jsonObject.get("scrollbarTrackHoverColor") == null) {
            n33 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getTrackHoverColorValue();
        } else {
            n33 = jsonObject.get("scrollbarTrackHoverColor").getAsInt();
        }

        int n34;
        if (jsonObject.get("primaryTextColor") == null) {
            n34 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getPrimaryTextColorValue();
        } else {
            n34 = jsonObject.get("primaryTextColor").getAsInt();
        }

        int n35;
        if (jsonObject.get("textColor2") == null) {
            n35 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getTextColor2Value();
        } else {
            n35 = jsonObject.get("textColor2").getAsInt();
        }

        int n36;
        if (jsonObject.get("textColor3") == null) {
            n36 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getTextColor3Value();
        } else {
            n36 = jsonObject.get("textColor3").getAsInt();
        }

        int n37;
        if (jsonObject.get("textColor4") == null) {
            n37 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getTextColor4Value();
        } else {
            n37 = jsonObject.get("textColor4").getAsInt();
        }

        int n38;
        if (jsonObject.get("textColor5") == null) {
            n38 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getTextColor5Value();
        } else {
            n38 = jsonObject.get("textColor5").getAsInt();
        }

        int n39;
        if (jsonObject.get("textColor6") == null) {
            n39 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getIntValue2();
        } else {
            n39 = jsonObject.get("textColor6").getAsInt();
        }

        int n40;
        if (jsonObject.get("primaryDullTextColor") == null) {
            n40 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getPrimaryTextColorValue2();
        } else {
            n40 = jsonObject.get("primaryDullTextColor").getAsInt();
        }

        int n41;
        if (jsonObject.get("dullTextColor2") == null) {
            n41 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getTextColor2Value2();
        } else {
            n41 = jsonObject.get("dullTextColor2").getAsInt();
        }

        int n42;
        if (jsonObject.get("dullTextColor3") == null) {
            n42 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getTextColor3Value2();
        } else {
            n42 = jsonObject.get("dullTextColor3").getAsInt();
        }

        int n43;
        if (jsonObject.get("dullTextColor4") == null) {
            n43 = this.cheatBreaker.getConfigManager().getDefaultDarkTheme().getTextColor4Value2();
        } else {
            n43 = jsonObject.get("dullTextColor4").getAsInt();
        }

        String name = file.getName();
        Theme theme = new Theme(
            name,
            false,
            false,
            n,
            n2,
            n3,
            n4,
            n5,
            n6,
            f,
            f2,
            n7,
            n8,
            n12,
            n13,
            n14,
            n15,
            n16,
            n17,
            n20,
            n21,
            n18,
            n22,
            n24,
            n25,
            n23,
            n26,
            n19,
            n27,
            n29,
            n28,
            n9,
            n10,
            n11,
            n30,
            n31,
            n32,
            n33,
            n34,
            n35,
            n36,
            n37,
            n38,
            n39,
            n40,
            n41,
            n42,
            n43
        );
        if (!this.hasTheme(str)) {
            theme.intValue = this.cheatBreaker.getThemes().size() + 2;
            this.cheatBreaker.themes.put(theme.getThemeNameValue(), theme);
        }

        this.saveTheme(name);
        Logging.debug("Fixed & Loaded Theme: " + theme.getThemeNameValue());
    }

    public void loadMutes(File file) {
        try {
            if (!file.exists()) {
                this.saveMutes(file);
                return;
            }

            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            String str;
            while ((str = bufferedReader.readLine()) != null) {
                try {
                    UUID fromString = UUID.fromString(str);
                    this.cheatBreaker.getNetHandler().getAutoMutedPlayers().add(fromString);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean ensureFiles() {
        try {
            return (this.configDirectory.exists() || this.configDirectory.mkdirs())
                && (this.themesDirectory.exists() || this.themesDirectory.mkdirs())
                && (this.defaultProfileFile.exists() || this.defaultProfileFile.createNewFile())
                && (this.globalConfigFile.exists() || this.globalConfigFile.createNewFile());
        } catch (IOException e) {
            e.printStackTrace();
            return true;
        }
    }

    public void saveAll() {
        if (this.ensureFiles()) {
            this.cheatBreaker.getEventBus().handleEvent(new SimpleEvent14());
            this.saveGlobalConfig(this.globalConfigFile);
            this.saveMutes(this.mutesFile);
            this.saveProfile(this.cheatBreaker.getConfigManager().profile.getName());
            this.saveTheme(this.cheatBreaker.getConfigManager().theme.getThemeNameValue());
        }
    }

    public void loadTheme(String str) {
        try {
            str = str.replaceAll(".json", "");
            if (!str.equalsIgnoreCase("default light") && !str.equalsIgnoreCase("default dark")) {
                Logging.debug("Reading Theme: " + str);
                JsonParser jsonParser = new JsonParser();
                if (!this.themesDirectory.exists() && !this.themesDirectory.mkdirs()) {
                    Logging.errorTagged("ConfigManager", "Could not create themes directory, not reading theme: " + str);
                } else {
                    File file = new File(this.themesDirectory + File.separator + str + ".json");

                    try {
                        jsonParser.parse(new FileReader(file)).getAsJsonObject();
                    } catch (Exception e) {
                        this.createEmptyThemeFile(file);
                        this.saveTheme(str);
                        return;
                    }

                    JsonObject asJsonObject = jsonParser.parse(new FileReader(file)).getAsJsonObject();

                    Theme theme;
                    try {
                        theme = new Theme(
                            file.getName(),
                            false,
                            false,
                            asJsonObject.get("primaryBackgroundColor").getAsInt(),
                            asJsonObject.get("menuBackgroundColor").getAsInt(),
                            asJsonObject.get("enabledBackgroundColor").getAsInt(),
                            asJsonObject.get("inactiveBackgroundColor").getAsInt(),
                            asJsonObject.get("modNameTextColor").getAsInt(),
                            asJsonObject.get("buttonBackgroundColor").getAsInt(),
                            asJsonObject.get("buttonBorderThickness").getAsFloat(),
                            asJsonObject.get("buttonCornerRadius").getAsFloat(),
                            asJsonObject.get("selectedButtonColor").getAsInt(),
                            asJsonObject.get("disabledButtonColor").getAsInt(),
                            asJsonObject.get("modsButtonHighlightColor").getAsInt(),
                            asJsonObject.get("normalButtonHighlightColor").getAsInt(),
                            asJsonObject.get("labelButtonHighlightColor").getAsInt(),
                            asJsonObject.get("enableHighlightButtonColor").getAsInt(),
                            asJsonObject.get("disableHighlightButtonColor").getAsInt(),
                            asJsonObject.get("selectedModOutlineColor").getAsInt(),
                            asJsonObject.get("unselectedModOutlineColor").getAsInt(),
                            asJsonObject.get("unselectedModOverlayColor").getAsInt(),
                            asJsonObject.get("selectedModOverlayColor").getAsInt(),
                            asJsonObject.get("selectedModHoveringOutlineColor").getAsInt(),
                            asJsonObject.get("unselectedModHoveringOutlineColor").getAsInt(),
                            asJsonObject.get("unselectedModHoveringOverlayColor").getAsInt(),
                            asJsonObject.get("selectedModHoveringOverlayColor").getAsInt(),
                            asJsonObject.get("snapLineColor").getAsInt(),
                            asJsonObject.get("neighboringModOverlayColor").getAsInt(),
                            asJsonObject.get("hudBorderColor").getAsInt(),
                            asJsonObject.get("dragOutlineColor").getAsInt(),
                            asJsonObject.get("dragColor").getAsInt(),
                            asJsonObject.get("sliderTrackColor").getAsInt(),
                            asJsonObject.get("sliderThumbColor").getAsInt(),
                            asJsonObject.get("sliderThumbOutlineColor").getAsInt(),
                            asJsonObject.get("scrollbarThumbColor").getAsInt(),
                            asJsonObject.get("scrollbarThumbHoverColor").getAsInt(),
                            asJsonObject.get("scrollbarTrackColor").getAsInt(),
                            asJsonObject.get("scrollbarTrackHoverColor").getAsInt(),
                            asJsonObject.get("primaryTextColor").getAsInt(),
                            asJsonObject.get("textColor2").getAsInt(),
                            asJsonObject.get("textColor3").getAsInt(),
                            asJsonObject.get("textColor4").getAsInt(),
                            asJsonObject.get("textColor5").getAsInt(),
                            asJsonObject.get("textColor6").getAsInt(),
                            asJsonObject.get("primaryDullTextColor").getAsInt(),
                            asJsonObject.get("dullTextColor2").getAsInt(),
                            asJsonObject.get("dullTextColor3").getAsInt(),
                            asJsonObject.get("dullTextColor4").getAsInt()
                        );
                    } catch (Exception e) {
                        this.repairTheme(str, file, asJsonObject);
                        Logging.error("Failed to read theme, fixing it.");
                        return;
                    }

                    if (!this.hasTheme(str)) {
                        theme.intValue = this.cheatBreaker.getThemes().size() + 2;
                        this.cheatBreaker.themes.put(str, theme);
                    }

                    Logging.debug("Loaded New Theme: " + theme.getThemeNameValue());
                }
            }
        } catch (Throwable e) {
            throw SneakyThrow.sneakyThrow(e);
        }
    }

    public void createEmptyProfileFile(File file) {
        try {
            file.createNewFile();
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("{}");
            fileWriter.close();
        } catch (Throwable e) {
            throw SneakyThrow.sneakyThrow(e);
        }
    }

    public void loadAll() {
        if (this.ensureFiles()) {
            this.loadGlobalConfig(this.globalConfigFile);
            this.loadMutes(this.mutesFile);
            if (this.cheatBreaker.getConfigManager().profile == null) {
                this.cheatBreaker.getConfigManager().profile = this.cheatBreaker.profiles.get(0);
            } else {
                this.loadProfile(this.cheatBreaker.getConfigManager().profile.getName());
            }

            if (this.cheatBreaker.getConfigManager().theme == null) {
                this.cheatBreaker.getConfigManager().theme = this.cheatBreaker.getConfigManager().getDefaultLightTheme();
            } else {
                this.loadTheme(this.cheatBreaker.getConfigManager().theme.getThemeNameValue());
            }

            this.cheatBreaker.getEventBus().handleEvent(new SimpleEvent33());
        }
    }

    public void addProperty(JsonObject jsonObject, String str, Object obj) {
        if (obj instanceof String) {
            jsonObject.addProperty(str, (String)obj);
        } else if (obj instanceof Number) {
            jsonObject.addProperty(str, (Number)obj);
        } else if (obj instanceof Boolean) {
            jsonObject.addProperty(str, (Boolean)obj);
        } else if (obj instanceof Character) {
            jsonObject.addProperty(str, (Character)obj);
        } else if (obj == null) {
            jsonObject.add(str, JsonNull.INSTANCE);
        } else {
            jsonObject.addProperty(str, obj.toString());
        }
    }

    public Profile getProfile(String str) {
        for (Profile profile : this.cheatBreaker.getProfiles()) {
            if (profile.getName().equalsIgnoreCase(str)) {
                return profile;
            }
        }

        return null;
    }

    public void loadProfile(String str) {
        try {
            ArrayList list = new ArrayList<>(this.cheatBreaker.getModManager().abstractModList);
            if (str.equalsIgnoreCase("default")) {
                this.cheatBreaker.getConfigManager().profile = this.cheatBreaker.profiles.get(0);

                for (AbstractMod abstractMod : (Iterable<AbstractMod>)list) {
                    abstractMod.setEnabled(abstractMod.defaultEnabled);
                    abstractMod.setWasRenderHud(abstractMod.defaultWasRenderHud);
                    abstractMod.setAnchorPoint(abstractMod.defaultAnchorPoint);
                    abstractMod.setTranslation(abstractMod.defaultTranslationX, abstractMod.defaultTranslationY);
                    abstractMod.setRenderHud(abstractMod.defaultRenderHud);

                    for (int i = 0; i < abstractMod.getSettings().size(); i++) {
                        try {
                            abstractMod.getSettings().get(i).changeValueNotify(abstractMod.getDefaultSettingValues().get(i), false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                File file = new File(this.configDirectory + File.separator + "profiles");
                File file2 = !file.exists() && !file.mkdirs() ? null : new File(file + File.separator + str + ".json");
                JsonParser jsonParser = new JsonParser();

                try {
                    jsonParser.parse(new FileReader(file2)).getAsJsonObject();
                } catch (FileNotFoundException | JsonSyntaxException | IllegalStateException e) {
                    this.createEmptyProfileFile(file2);
                    this.createEmptyProfileFile(file2);
                    this.saveProfile(str);
                    return;
                }

                if (!file2.exists()) {
                    this.saveProfile(str);
                } else {
                    JsonObject asJsonObject = jsonParser.parse(new FileReader(file2)).getAsJsonObject();

                    for (AbstractMod abstractMod : (Iterable<AbstractMod>)list) {
                        JsonObject asJsonObject2 = asJsonObject.getAsJsonObject(abstractMod.getName());
                        if (asJsonObject2 != null) {
                            JsonObject asJsonObject3 = asJsonObject2.getAsJsonObject("info");
                            JsonObject asJsonObject4 = asJsonObject2.getAsJsonObject("settings");
                            String str2 = null;
                            boolean asBoolean = asJsonObject3.get("state").getAsBoolean();
                            boolean asBoolean2 = asJsonObject3.get("wasRenderHUD").getAsBoolean();

                            try {
                                str2 = asJsonObject3.get("position").getAsString();
                            } catch (NullPointerException e) {
                            }

                            float asFloat = asJsonObject3.get("xTranslation").getAsFloat();
                            float asFloat2 = asJsonObject3.get("yTranslation").getAsFloat();
                            boolean asBoolean3 = asJsonObject3.get("renderHUD").getAsBoolean();
                            abstractMod.setEnabled(asBoolean);
                            abstractMod.setWasRenderHud(asBoolean2);
                            if (str2 != null) {
                                abstractMod.setAnchorPoint(AnchorPoint.findAnchorPoint(str2));
                            }

                            abstractMod.setTranslationX(asFloat);
                            abstractMod.setTranslationY(asFloat2);
                            abstractMod.setRenderHud(asBoolean3);
                            if (!abstractMod.loadCustomConfig(asJsonObject)) {
                                for (Setting setting : abstractMod.getSettings()) {
                                    try {
                                        if (setting != null) {
                                            JsonElement jsonElement = asJsonObject4.get(setting.getName());
                                            if (jsonElement != null) {
                                                switch (setting.getType()) {
                                                    case BOOLEAN:
                                                        setting.changeValue(jsonElement.getAsBoolean());
                                                        break;
                                                    case INTEGER:
                                                        if (abstractMod.isStaffMod() && setting == abstractMod.toggleModKeybind) {
                                                            abstractMod.toggleModKeybind.changeValue(jsonElement.getAsInt());
                                                        } else if (setting.getName().toLowerCase().contains("color")) {
                                                            JsonObject asJsonObject5 = jsonElement.getAsJsonObject();
                                                            boolean asBoolean4 = asJsonObject5.get("chroma").getAsBoolean();
                                                            int asInt = asJsonObject5.get("color").getAsInt();
                                                            if (asInt <= (Integer)setting.getMaximumValue() && asInt >= (Integer)setting.getMinimumValue()) {
                                                                setting.chroma = asBoolean4;
                                                                setting.changeValue(asInt);
                                                            }
                                                        } else if (setting.getType() == SettingType.INTEGER && setting.getName().endsWith("Keybind")) {
                                                            boolean asBoolean4 = jsonElement.getAsJsonObject().get("mouse").getAsBoolean();
                                                            int asInt = jsonElement.getAsJsonObject().get("keybind").getAsInt();
                                                            setting.changeValue(asInt);
                                                            setting.setMouseBind(asBoolean4);
                                                        } else if (setting.getName().toLowerCase().endsWith("keybind")) {
                                                            int asInt = jsonElement.getAsJsonObject().get("keybind").getAsInt();
                                                            setting.changeValue(asInt);
                                                        } else {
                                                            setting.changeValue(jsonElement.getAsInt());
                                                        }
                                                        break;
                                                    case FLOAT:
                                                        if (abstractMod != this.cheatBreaker.getModManager().autoTextMod) {
                                                            float asFloat3 = jsonElement.getAsFloat();
                                                            Number minimumValue = (Number)setting.getMinimumValue();
                                                            Number maximumValue = (Number)setting.getMaximumValue();
                                                            if (asFloat3 <= maximumValue.floatValue() && asFloat3 >= minimumValue.floatValue()) {
                                                                setting.changeValue(asFloat3);
                                                            }
                                                        }
                                                        break;
                                                    case DOUBLE:
                                                        double asDouble = jsonElement.getAsDouble();
                                                        if (asDouble <= (Double)setting.getMaximumValue() && asDouble >= (Double)setting.getMinimumValue()) {
                                                            setting.changeValue(asDouble);
                                                        }
                                                        break;
                                                    case ARRAYLIST:
                                                        JsonArray jsonArray;
                                                        if (jsonElement.isJsonArray()) {
                                                            jsonArray = jsonElement.getAsJsonArray();
                                                        } else if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isString()) {
                                                            try {
                                                                jsonArray = JsonParser.parseString(jsonElement.getAsString()).getAsJsonArray();
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                                jsonArray = new JsonArray();
                                                            }
                                                        } else {
                                                            jsonArray = new JsonArray();
                                                        }

                                                        ArrayList list2 = new ArrayList();

                                                        for (JsonElement jsonElement2 : jsonArray) {
                                                            list2.add(jsonElement2.getAsInt());
                                                        }

                                                        setting.changeValue(list2);
                                                        break;
                                                    case STRING_ARRAY:
                                                        boolean flag = false;

                                                        for (String str3 : setting.getStringValues()) {
                                                            if (str3.equalsIgnoreCase(jsonElement.getAsString())) {
                                                                flag = true;
                                                            }
                                                        }

                                                        if (flag) {
                                                            setting.changeValue(jsonElement.getAsString());
                                                        }
                                                        break;
                                                    case STRING:
                                                        String asString = jsonElement.getAsString();
                                                        if (setting == this.cheatBreaker.getModManager().toggleSprintMod.flyBoostString) {
                                                            asString = asString.replaceAll("%FPS%", "%BOOST%");
                                                        }

                                                        setting.changeValue(asString.replaceAll("&([abcdefghijklmrABCDEFGHIJKLMNR0-9])|(&$)", "§$1"));
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable e) {
            throw SneakyThrow.sneakyThrow(e);
        }
    }

    public void saveTheme(String str) {
        try {
            str = str.replaceAll(".json", "");
            if (this.getTheme(str) != null) {
                if (!this.getTheme(str).isBuiltIn()) {
                    if (!this.themesDirectory.exists() && !this.themesDirectory.mkdirs()) {
                        Logging.errorTagged("ConfigManager", "Could not create themes directory, not saving theme: " + str);
                    } else {
                        File file = new File(this.themesDirectory + File.separator + str + ".json");
                        JsonObject newJsonObject = this.newJsonObject();
                        Theme theme = this.getTheme(str);
                        newJsonObject.addProperty("primaryBackgroundColor", theme.getPrimaryBackgroundColorValue());
                        newJsonObject.addProperty("menuBackgroundColor", theme.getMenuBackgroundColorValue());
                        newJsonObject.addProperty("enabledBackgroundColor", theme.getEnabledModColorValue());
                        newJsonObject.addProperty("inactiveBackgroundColor", theme.getDisabledModColorValue());
                        newJsonObject.addProperty("modNameTextColor", theme.getModNameTextColorValue());
                        newJsonObject.addProperty("buttonBackgroundColor", theme.getButtonBackgroundColorValue());
                        newJsonObject.addProperty("selectedButtonColor", theme.getSelectedButtonColorValue());
                        newJsonObject.addProperty("disabledButtonColor", theme.getDisabledButtonColorValue());
                        newJsonObject.addProperty("modsButtonHighlightColor", theme.getModsHighlightColorValue());
                        newJsonObject.addProperty("normalButtonHighlightColor", theme.getNormalHighlightColorValue());
                        newJsonObject.addProperty("labelButtonHighlightColor", theme.getLabelHighlightColorValue());
                        newJsonObject.addProperty("enableHighlightButtonColor", theme.getEnableModHighlightColorValue());
                        newJsonObject.addProperty("disableHighlightButtonColor", theme.getDisableModHighlightColorValue());
                        newJsonObject.addProperty("unselectedModOutlineColor", theme.getUnselectedOutlineColorValue());
                        newJsonObject.addProperty("unselectedModOverlayColor", theme.getUnselectedOverlayColorValue());
                        newJsonObject.addProperty("selectedModOutlineColor", theme.getSelectedOutlineColorValue());
                        newJsonObject.addProperty("selectedModOverlayColor", theme.getSelectedOverlayColorValue());
                        newJsonObject.addProperty("snapLineColor", theme.getSnapLineColorValue());
                        newJsonObject.addProperty("hudBorderColor", theme.getHudBorderColorValue());
                        newJsonObject.addProperty("dragOutlineColor", theme.getSelectionOutlineColorValue());
                        newJsonObject.addProperty("dragColor", theme.getSelectionColorValue());
                        newJsonObject.addProperty("sliderTrackColor", theme.getTrackColorValue());
                        newJsonObject.addProperty("sliderThumbColor", theme.getThumbColorValue());
                        newJsonObject.addProperty("sliderThumbOutlineColor", theme.getThumbOutlineColorValue());
                        newJsonObject.addProperty("scrollbarThumbColor", theme.getThumbColorValue2());
                        newJsonObject.addProperty("scrollbarThumbHoverColor", theme.getThumbHoverColorValue());
                        newJsonObject.addProperty("scrollbarTrackColor", theme.getTrackColorValue2());
                        newJsonObject.addProperty("scrollbarTrackHoverColor", theme.getTrackHoverColorValue());
                        newJsonObject.addProperty("primaryTextColor", theme.getPrimaryTextColorValue());
                        newJsonObject.addProperty("textColor2", theme.getTextColor2Value());
                        newJsonObject.addProperty("textColor3", theme.getTextColor3Value());
                        newJsonObject.addProperty("textColor4", theme.getTextColor4Value());
                        newJsonObject.addProperty("textColor5", theme.getTextColor5Value());
                        newJsonObject.addProperty("textColor6", theme.getIntValue2());
                        newJsonObject.addProperty("primaryDullTextColor", theme.getPrimaryTextColorValue2());
                        newJsonObject.addProperty("dullTextColor2", theme.getTextColor2Value2());
                        newJsonObject.addProperty("dullTextColor3", theme.getTextColor3Value2());
                        newJsonObject.addProperty("dullTextColor4", theme.getTextColor4Value2());
                        Logging.debug("Saving Theme: " + str);
                        FileWriter fileWriter = new FileWriter(file);
                        Gson gson = new GsonBuilder().setPrettyPrinting().create();
                        fileWriter.write(gson.toJson(newJsonObject));
                        fileWriter.close();
                    }
                }
            }
        } catch (Throwable e) {
            throw SneakyThrow.sneakyThrow(e);
        }
    }
}
