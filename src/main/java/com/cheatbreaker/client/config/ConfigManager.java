package com.cheatbreaker.client.config;

import com.cheatbreaker.client.manager.AbstractManager;
import com.cheatbreaker.client.theme.DefaultDarkTheme;
import com.cheatbreaker.client.theme.DefaultLightTheme;
import com.cheatbreaker.client.theme.Theme;
import com.cheatbreaker.client.ui.component.ProfilesPanel;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import com.cheatbreaker.client.util.ClientVersion;
import com.cheatbreaker.client.util.Logging;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Objects;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;

public class ConfigManager extends AbstractManager {
    public DefaultLightTheme defaultLightTheme = new DefaultLightTheme();
    public Profile profile;
    public DefaultDarkTheme defaultDarkTheme = new DefaultDarkTheme();
    public Theme theme = this.defaultDarkTheme;

    public void prepareConfigFiles() {
        File file = new File(
            this.mc.mcDataDir + File.separator + "config" + File.separator + "cheatbreaker-client-" + ClientVersion.version + "-" + this.cheatBreaker.getBranch()
        );
        File file2 = new File(file + File.separator + "profiles");
        if (!file.exists()) {
            file.mkdirs();
            File file3 = new File(this.mc.mcDataDir + File.separator + "config" + File.separator + "cheatbreaker-client-" + ClientVersion.version + "-master");
            File file4 = new File(
                this.mc.mcDataDir + File.separator + "config" + File.separator + "cheatbreaker-client-" + ClientVersion.version.replaceAll("\\.", "_")
            );
            File file5 = new File(this.mc.mcDataDir + File.separator + "config" + File.separator + "cheatbreaker-client");
            File file6 = new File(this.mc.mcDataDir + File.separator + "config" + File.separator + "client");
            File file7 = null;

            for (File file8 : ImmutableList.of(file6, file5, file4, file3)) {
                if (file8.exists()) {
                    file7 = file8;
                }
            }

            if (file7 != null && file7 != file) {
                try {
                    FileUtils.copyDirectory(file7, file);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                }
            }
        }

        if (file2.exists() || file2.mkdirs()) {
            for (ResourceLocation resourceLocation : this.cheatBreaker.getDefaultResources()) {
                String replaceAll = resourceLocation.getResourcePath().replaceAll("([a-zA-Z0-9/]+)/", "");
                File file3 = new File(file2, replaceAll);
                if (!file3.exists()) {
                    try {
                        InputStream inputStream = this.mc.getResourceManager().getResource(resourceLocation).getInputStream();
                        Files.copy(inputStream, file3.toPath());
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void loadThemes() throws IOException {
        Logging.debug("Started Loading Themes");
        DefaultDarkTheme defaultDarkTheme = this.cheatBreaker.getConfigManager().getDefaultDarkTheme();
        DefaultLightTheme defaultLightTheme = this.cheatBreaker.getConfigManager().getDefaultLightTheme();
        this.cheatBreaker.themes.put(defaultDarkTheme.getThemeNameValue(), defaultDarkTheme);
        this.cheatBreaker.themes.put(defaultLightTheme.getThemeNameValue(), defaultLightTheme);
        File file = new File(
            this.mc.mcDataDir
                + File.separator
                + "config"
                + File.separator
                + "cheatbreaker-client-"
                + ClientVersion.version
                + "-"
                + this.cheatBreaker.getBranch()
                + File.separator
                + "themes"
        );
        if (this.cheatBreaker.configFileManager == null) {
            this.cheatBreaker.configFileManager = new ConfigFileManager();
        }

        if (file.exists()) {
            for (File file2 : Objects.requireNonNull(file.listFiles())) {
                this.mc.initializationScreen.advance();
                if (file2.getName().endsWith(".json")) {
                    this.cheatBreaker.configFileManager.loadTheme(file2.getName().replaceAll(".json", ""));
                }
            }
        }

        for (Theme theme : this.cheatBreaker.themes.values()) {
            theme.createSettings();
        }
    }

    public DefaultLightTheme getDefaultLightTheme() {
        return this.defaultLightTheme;
    }

    public void ensureProfileExists() {
        if (this.profile == this.cheatBreaker.profiles.get(0)) {
            Profile profile;
            this.cheatBreaker.getConfigManager().profile = profile = new Profile(this.uniqueProfileName("Profile 1"), false);
            this.cheatBreaker.profiles.add(profile);
            this.cheatBreaker.configFileManager.saveAll();
            if (this.mc.currentScreen instanceof ModsScreen) {
                ProfilesPanel profilesPanel = ((ModsScreen)this.mc.currentScreen).profilesPanel;
                profilesPanel.loadProfiles();
            }
        }
    }

    public void loadProfiles() throws IOException {
        this.cheatBreaker.profiles.add(new Profile("default", true));
        File file = new File(
            this.mc.mcDataDir
                + File.separator
                + "config"
                + File.separator
                + "cheatbreaker-client-"
                + ClientVersion.version
                + "-"
                + this.cheatBreaker.getBranch()
                + File.separator
                + "profiles"
        );
        if (file.exists()) {
            for (File file2 : Objects.requireNonNull(file.listFiles())) {
                this.mc.initializationScreen.advance();
                if (file2.getName().endsWith(".json")) {
                    this.cheatBreaker.profiles.add(new Profile(file2.getName().replace(".json", ""), false));
                }
            }
        }

        this.loadThemes();
    }

    public String uniqueProfileName(String str) {
        File file = new File(
            this.mc.mcDataDir + File.separator + "config" + File.separator + "cheatbreaker-client-" + ClientVersion.version + "-" + this.cheatBreaker.getBranch()
        );
        File file2 = new File(file + File.separator + "profiles");
        return (file2.exists() || file2.mkdirs()) && new File(file2 + File.separator + str + ".json").exists() ? this.uniqueProfileName(str + "1") : str;
    }

    @Override
    public void initialize() throws IOException {
        this.prepareConfigFiles();
        this.loadProfiles();
    }

    public DefaultDarkTheme getDefaultDarkTheme() {
        return this.defaultDarkTheme;
    }
}
