package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.config.Profile;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import com.cheatbreaker.client.ui.screen.ProfileNameScreen;
import com.cheatbreaker.client.util.ClientVersion;
import com.cheatbreaker.client.util.FileOpener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class ProfilesPanel extends Panel {
    public List<ProfileComponent> profileComponentList = new ArrayList<>();
    public ResourceLocation plus64Png = new ResourceLocation("client/icons/plus-64.png");
    public int highlightColor = this.theme2.getNormalHighlightColorValue();
    public Button openProfilesFolder;
    public Button addNewProfile;

    public ProfilesPanel(float f, int n, int n2, int n3, int n4) {
        super(f, n, n2, n3, n4);
        int n5 = this.x + n3 - 120;
        this.openProfilesFolder = new Button(
            CheatBreaker.getInstance().getFontManager().playBold18,
            null,
            "Open Profiles Folder",
            n5,
            this.y + n4 + 4,
            110,
            28,
            this.highlightColor,
            f,
            false,
            true
        );
        n5 = this.x + 2;
        int n6 = this.y + 4;
        this.addNewProfile = new Button(
            CheatBreaker.getInstance().getFontManager().ubuntuM16, null, "Add New Profile", n5, n6, 14, 14, this.highlightColor, f, false, true
        );
        this.addNewProfile.setHoverLabel("(COPIES CURRENT) Add New Profile");
        this.loadProfiles();
    }

    @Override
    public boolean hasSettings(AbstractMod abstractMod) {
        return true;
    }

    public void loadProfiles() {
        new Thread(
                () -> {
                    this.profileComponentList.clear();
                    File file = new File(
                        Minecraft.getMinecraft().mcDataDir
                            + File.separator
                            + "config"
                            + File.separator
                            + "cheatbreaker-client-"
                            + ClientVersion.version
                            + "-"
                            + CheatBreaker.getInstance().getBranch()
                            + File.separator
                            + "profiles"
                    );
                    if (file.exists()) {
                        for (File file2 : file.listFiles()) {
                            if (file2.getName().endsWith(".json")) {
                                Profile profile = null;

                                for (Profile profile2 : CheatBreaker.getInstance().profiles) {
                                    if (file2.getName().equals(profile2.getName() + ".json")) {
                                        profile = profile2;
                                    }
                                }

                                if (profile == null) {
                                    CheatBreaker.getInstance().profiles.add(new Profile(file2.getName().replace(".json", ""), false));
                                }
                            }
                        }
                    }

                    for (Profile profile : CheatBreaker.getInstance().profiles) {
                        this.profileComponentList.add(new ProfileComponent(this, this.highlightColor, profile, this.scale));
                    }

                    this.profileComponentList.sort((profileComponent, profileComponent2) -> {
                        if (profileComponent.profile.getName().equalsIgnoreCase("default")) {
                            return 0;
                        } else {
                            return profileComponent.profile.intValue < profileComponent2.profile.intValue ? -1 : 1;
                        }
                    });
                }
            )
            .start();
    }

    @Override
    public void render(int n, int n2, float f) {
        RenderUtils.drawRoundedRectFilledPrecise(this.x, this.y, this.x + this.width, this.y + this.height + 2, 8.0, this.theme2.getMenuBackgroundColorValue());
        this.updateScroll(n, n2);
        this.contentHeight = 15;

        for (int i = 0; i < this.profileComponentList.size(); i++) {
            ProfileComponent profileComponent = this.profileComponentList.get(i);
            profileComponent.setBounds(this.x + 4, this.y + 4 + i * 18, this.width - 12, 18);
            profileComponent.scrollOffsetY = this.scrollOffset;
            profileComponent.render(n, n2, f);
            this.contentHeight = this.contentHeight + profileComponent.getHeight();
        }

        this.addNewProfile
            .setBounds(
                this.x + this.width - (this.addNewProfile.isMouseOver(n, n2) ? 162 : 87),
                this.y + this.contentHeight - 5,
                this.addNewProfile.isMouseOver(n, n2) ? 155 : 80,
                10
            );
        this.addNewProfile.scrollOffsetY = this.scrollOffset;
        this.addNewProfile.render(n, n2, f);
        this.contentHeight += 10;
        this.openProfilesFolder.scrollOffsetY = this.scrollOffset;
        this.openProfilesFolder.setBounds(this.x + this.width - 130, this.y + this.contentHeight, 125, 20);
        this.openProfilesFolder.render(n, n2, f);
        this.endScrollRegion(n, n2);
    }

    @Override
    public void selectMod(AbstractMod abstractMod) {
    }

    @Override
    public void mouseClicked(int n, int n2, int n3) throws IOException {
        if (this.openProfilesFolder.isMouseOver(n, n2)) {
            Minecraft.getMinecraft()
                .getSoundHandler()
                .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));

            try {
                FileOpener.openFile(CheatBreaker.getInstance().getConfigFileManager().profilesDirectory);
            } catch (IllegalArgumentException | IOException e) {
                e.printStackTrace();
            }
        } else {
            for (ProfileComponent profileComponent : this.profileComponentList) {
                if (profileComponent.isMouseOver(n, n2)) {
                    profileComponent.mouseClicked(n, n2, n3);
                    return;
                }
            }

            boolean flag = n > (this.x + this.width - 92) * this.scale
                && n < (this.x + this.width - 6) * this.scale
                && n2 > (this.y + this.contentHeight - 20 + this.scrollOffset) * this.scale
                && n2 < (this.y + this.contentHeight - 7 + this.scrollOffset) * this.scale;
            if (flag) {
                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                Minecraft.getMinecraft().displayGuiScreen(new ProfileNameScreen(ModsScreen.modsScreen, this, this.highlightColor, this.scale));
            }

            double d = this.height - 10;
            double contentHeight = this.contentHeight;
            double d2 = d / contentHeight * 100.0;
            double d3 = d / 100.0 * d2;
            double d4 = this.scrollOffset / 100.0 * d2;
            boolean flag2 = n > (this.x + this.width - 9) * this.scale
                && n < (this.x + this.width - 3) * this.scale
                && n2 > (this.y + 11 - d4) * this.scale
                && n2 < (this.y + 8 + d3 - d4) * this.scale;
            boolean flag3 = n > (this.x + this.width - 9) * this.scale
                && n < (this.x + this.width - 3) * this.scale
                && n2 > (this.y + 11) * this.scale
                && n2 < (this.y + 6 + d - 3.0) * this.scale;
            if (n3 == 0 && flag3 || flag2) {
                this.dragging = true;
            }
        }
    }
}
