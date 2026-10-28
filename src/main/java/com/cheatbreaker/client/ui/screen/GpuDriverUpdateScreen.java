package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.ui.component.IconButton;
import com.cheatbreaker.client.ui.component.StatusLabel;
import com.cheatbreaker.client.ui.component.TextButton;
import com.cheatbreaker.client.util.GpuDriverChecker;
import com.cheatbreaker.client.util.GpuDriverStatus;
import java.net.URI;
import java.net.URISyntaxException;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;

public class GpuDriverUpdateScreen extends LogoMenuScreen {
    public TextButton updateGpuDriver = new TextButton("UPDATE GPU DRIVER");

    @Override
    public void mouseClickedScaled(float f, float f2, int n) throws java.io.IOException {
        super.mouseClickedScaled(f, f2, n);
        if (this.updateGpuDriver.containsPoint(f, f2)) {
            this.mc2.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));

            try {
                String openGlVendor = Config.openGlVendor;
                String str = "";
                if (openGlVendor.contains("Intel")) {
                    if (GpuDriverChecker.getGpuDriverStatus() == GpuDriverStatus.REQUIRES_DOWNGRADE) {
                        str = "http://web.archive.org/web/20211119065952/https://downloadmirror.intel.com/677976/igfx_win_100.9955.exe";
                    } else {
                        str = "https://www.intel.com/content/www/us/en/search.html#q=&sort=relevancy&f:@tabfilter=[Downloads]&f:@stm_10385_en=[Graphics]";
                    }
                } else if (openGlVendor.contains("NVIDIA")) {
                    str = "https://www.nvidia.com/Download/Find.aspx";
                } else if (openGlVendor.contains("AMD") || openGlVendor.contains("ATI")) {
                    str = "https://www.amd.com/en/support/download/drivers.html";
                }

                if (!str.isEmpty()) {
                    this.openUri(new URI(str));
                }
            } catch (URISyntaxException e) {
                CheatBreaker.getInstance().getLogger().error("Couldn't open link", e);
            }
        }
    }

    @Override
    public void initGui() throws java.io.IOException {
        super.initGui();

        for (StatusLabel statusLabel : this.tabs) {
            statusLabel.setEnabled(false);
        }

        this.discordLinkButton.setEnabled(false);
        this.versionLabel.setEnabled(false);
        this.copyrightLabel.setEnabled(false);
        this.logoButton.setEnabled(false);

        for (IconButton iconButton : this.iconButtons) {
            iconButton.setEnabled(false);
        }
    }

    public GpuDriverUpdateScreen() {
        this.textButtonList.add(this.updateGpuDriver);
        if (GpuDriverChecker.getGpuDriverStatus() == GpuDriverStatus.REQUIRES_DOWNGRADE) {
            this.updateGpuDriver.setLabel("DOWNGRADE GPU DRIVER");
            this.text3 = "You have a bad GPU driver.";
            this.text2 = "Please downgrade before playing.";
        } else {
            this.text3 = "Good things are coming your way.";
            this.text2 = "First, update your GPU driver.";
        }
    }
}
