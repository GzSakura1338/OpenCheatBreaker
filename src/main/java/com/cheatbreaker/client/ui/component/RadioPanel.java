package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.animation.EaseInOutAnimation;
import com.cheatbreaker.client.radio.RadioApi;
import com.cheatbreaker.client.radio.Station;
import com.cheatbreaker.client.radio.StationEntry;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.screen.FriendsScreen;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;
import org.lwjgl.opengl.GL11;

public class RadioPanel extends DraggableElement {
    public float floatValue;
    public ResourceLocation dashLogo54Png = new ResourceLocation("client/dash-logo-54.png");
    public boolean flag;
    public ResourceLocation play24Png = new ResourceLocation("client/icons/play-24.png");
    public EaseInOutAnimation easeInOutAnimation = new EaseInOutAnimation(300L);
    public SliderBar sliderBar = new SliderBar(CheatBreaker.getInstance().getKeybindSettings().radioVolume);
    public ScrollBar scrollBar = new ScrollBar(this);
    public TextField filter = new TextField(this.cheatBreaker.getFontManager().playRegular14, "Filter", -11842741, -11842741);
    public SectionHeader unpin = new SectionHeader((boolean)this.cheatBreaker.getKeybindSettings().pinRadioPlayer.getValue() ? "Unpin" : "Pin");
    public List<StationEntry> stationEntryList = new ArrayList<>();

    @Override
    public void setBounds(float f, float f2, float f3, float f4) {
        super.setBounds(f, f2, f3, f4);
        if (this.floatValue == 0.0F) {
            this.floatValue = f4;
        }

        this.stationEntryList.sort((stationEntry, stationEntry2) -> {
            if (stationEntry.getStation().isFavorite() && !stationEntry2.getStation().isFavorite()) {
                return -1;
            } else {
                return !stationEntry.getStation().isFavorite() && stationEntry2.getStation().isFavorite() ? 1 : 0;
            }
        });
        this.sliderBar.setBounds(f, f2 + this.floatValue, f3, 8.0F);
        this.filter.setBounds(f, f2 + this.floatValue + 8.0F, f3 - 30.0F, 13.0F);
        this.unpin.setBounds(f + f3 - 30.0F, f2 + this.floatValue + 8.0F, 30.0F, 13.0F);
        this.scrollBar.setBounds(f + f3 - 5.0F, f2 + this.floatValue + 21.0F, 5.0F, 99.0F);
        int n = 0;

        for (StationEntry stationEntry : this.stationEntryList) {
            if (this.checkStationEntry(stationEntry)) {
                float f5 = f2 + 21.0F + this.floatValue + n;
                stationEntry.setBounds(f, f5, f3 - 5.0F, 20.0F);
                n += 20;
            }
        }

        this.scrollBar.setContentHeight(n);
    }

    @Override
    public void handleMouseInput() {
        this.scrollBar.handleMouseInput();
    }

    @Override
    public boolean mouseClickedFallback(float f, float f2, int n) {
        if (!this.filter.containsPoint(f, f2) && this.filter.isFocused()) {
            this.filter.setFocused(false);
        }

        return false;
    }

    @Override
    public void tick() {
        this.filter.tick();
        this.unpin.tick();
    }

    @Override
    public boolean mouseClicked(float f, float f2, int n, boolean flag) throws java.io.IOException {
        this.filter.mouseClicked(f, f2, n, flag);
        if (this.filter.isFocused() && n == 1 && this.filter.getText().equals("")) {
            this.setBounds();
        }

        if (!flag) {
            return false;
        }

        boolean flag2 = this.containsPoint(f, f2) && f > this.x + 34.0F && f < this.x + 44.0F && f2 < this.y + this.floatValue;
        if (flag2) {
            if (!RadioApi.isPlaying()) {
                CheatBreaker.getInstance().getRadioManager().getStation().play();
            } else {
                RadioApi.stop();
            }
        }

        float updateToggle = this.easeInOutAnimation.updateToggle(this.containsPoint(f, f2) && flag);
        if (this.easeInOutAnimation.isTriggered()) {
            this.sliderBar.mouseClicked(f, f2, n, flag);
            this.scrollBar.mouseClicked(f, f2, n, flag);
            this.filter.mouseClicked(f, f2, n, flag);
            this.unpin.mouseClicked(f, f2, n, flag);
            boolean flag3 = f > (int)this.x
                && f < (int)(this.x + this.width)
                && f2 > (int)(this.y + this.floatValue + 21.0F)
                && f2 < (int)(this.y + this.floatValue + 21.0F + (this.height - this.floatValue - 21.0F) * updateToggle);
            if (flag3) {
                for (StationEntry stationEntry : this.stationEntryList) {
                    if (this.checkStationEntry(stationEntry) && stationEntry.mouseClicked(f, f2 - this.scrollBar.getScrollOffset(), n, flag)) {
                        break;
                    }
                }
            }

            if (this.unpin.containsPoint(f, f2)) {
                this.cheatBreaker.getKeybindSettings().pinRadioPlayer.changeValue(!(Boolean)this.cheatBreaker.getKeybindSettings().pinRadioPlayer.getValue());
                this.unpin.setTitle((boolean)this.cheatBreaker.getKeybindSettings().pinRadioPlayer.getValue() ? "Unpin" : "Pin");
            }
        }

        if (this.containsPoint(f, f2)
            && f2 < this.y + this.floatValue
            && !flag2
            && !this.sliderBar.containsPoint(f, f2)
            && !this.scrollBar.containsPoint(f, f2)) {
            this.startDrag(f, f2);
        }

        return super.mouseClicked(f, f2, n, flag);
    }

    public void setBounds() {
        this.setBounds(this.x, this.y, this.width, this.height);
    }

    @Override
    public void keyTyped(char c, int n) {
        this.filter.keyTyped(c, n);
        this.unpin.keyTyped(c, n);
        this.scrollBar.keyTyped(c, n);
        if (this.filter.isFocused()) {
            this.setBounds();
        }
    }

    public boolean checkStationEntry(StationEntry stationEntry) {
        return this.filter.getText().equals("")
            || stationEntry.getStation().getName().toLowerCase().startsWith(this.filter.getText().toLowerCase())
            || stationEntry.getStation().getGenre().toLowerCase().startsWith(this.filter.getText().toLowerCase());
    }

    public boolean isHovered(float f, float f2) {
        return f > this.x && f < this.x + this.width && f2 > this.y && f2 < this.y + this.floatValue;
    }

    @Override
    public void render(float f, float f2, boolean flag) {
        this.updateDrag(f, f2);
        Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.floatValue, -14540254);
        Gui.drawRect(this.x, this.y, this.x + this.floatValue, this.y + this.floatValue, -1);
        Station station = CheatBreaker.getInstance().getRadioManager().getStation();
        if (station != null) {
            if (station.currentResource == null && !station.getCoverURL().equals("")) {
                if (station.previousResource != null) {
                    this.mc.getTextureManager().deleteTexture(station.previousResource);
                    station.previousResource = null;
                }

                station.currentResource = new ResourceLocation("client/songs/" + station.getTitle());
                ThreadDownloadImageData threadDownloadImageData = new ThreadDownloadImageData(null, station.getCoverURL(), this.dashLogo54Png, null);
                Minecraft.getMinecraft().renderEngine.loadTexture(station.currentResource, threadDownloadImageData);
            }

            ResourceLocation resourceLocation = station.currentResource == null ? this.dashLogo54Png : station.currentResource;
            RenderUtils.drawSquareTextureViaRenderEngine(resourceLocation, this.floatValue / 2.0F, this.x, this.y);
            float f3 = this.x + 50.0F;
            if (this.mc.currentScreen != FriendsScreen.getFriendsScreen()) {
                f3 = this.x + 34.0F;
            } else {
                boolean flag2 = this.containsPoint(f, f2) && f > this.x + 34.0F && f < this.x + 44.0F && f2 < this.y + this.floatValue;
                if (!RadioApi.isPlaying()) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, flag2 ? 1.0F : 0.8F);
                    RenderUtils.drawSquareTextureViaRenderEngine(this.play24Png, 6.0F, this.x + 34.0F, this.y + 7.5F);
                } else {
                    Gui.drawRect(this.x + 36.0F, this.y + 9.0F, this.x + 38.0F, this.y + this.floatValue - 11.0F, flag2 ? -1 : -1342177281);
                    Gui.drawRect(this.x + 40.0F, this.y + 9.0F, this.x + 42.0F, this.y + this.floatValue - 11.0F, flag2 ? -1 : -1342177281);
                }
            }

            String title = station.getTitle();
            if (CheatBreaker.getInstance().getFontManager().playRegular16.getStringWidth(title) > this.width - 52.0F) {
                float f4 = this.y + 4.0F;
                CheatBreaker.getInstance().getFontManager().playRegular12.drawString(title, f3, f4, -1);
            } else {
                float f4 = this.y + 4.0F;
                CheatBreaker.getInstance().getFontManager().playRegular16.drawString(title, f3, f4, -1);
            }

            CheatBreaker.getInstance().getFontManager().playRegular12.drawString(station.getArtist(), f3, this.y + 14.0F, -1342177281);
        }

        float updateToggle = this.easeInOutAnimation.updateToggle(this.containsPoint(f, f2) && flag);
        if (this.easeInOutAnimation.isRunning()) {
            this.setBounds(this.x, this.y, this.width, this.floatValue + 120.0F * updateToggle);
            this.flag = true;
        } else if (!this.easeInOutAnimation.isRunning() && !this.containsPoint(f, f2)) {
            this.flag = false;
        }

        if (this.flag) {
            GlStateManager.pushMatrix();
            GL11.glEnable(3089);
            FriendsScreen friendsScreen = FriendsScreen.getFriendsScreen();
            RenderUtils.scissorScaled(
                (int)this.x,
                (int)(this.y + this.floatValue),
                (int)(this.x + this.width),
                (int)(this.y + this.floatValue + (this.height - this.floatValue) * updateToggle),
                (int)(friendsScreen.getScaledResolution().getScaleFactor() * friendsScreen.getUiScale()),
                (int)friendsScreen.getScaledHeight()
            );
            Gui.drawRect(this.x, this.y + this.floatValue, this.x + this.width, this.y + this.height, -14540254);
            this.scrollBar.scrollDown(f, f2, flag);

            for (StationEntry stationEntry : this.stationEntryList) {
                if (this.checkStationEntry(stationEntry)) {
                    stationEntry.render(f, f2 - this.scrollBar.getScrollOffset(), flag && !this.scrollBar.isDragging() && !this.scrollBar.containsPoint(f, f2));
                }
            }

            this.scrollBar.render(f, f2, flag);
            this.sliderBar.renderAndTestHover(f, f2, flag);
            this.filter.render(f, f2, flag);
            this.unpin.render(f, f2, flag);
            GL11.glDisable(3089);
            GlStateManager.popMatrix();
        }
    }

    public RadioPanel() {
        for (Station station : CheatBreaker.getInstance().getRadioManager().getStationList()) {
            this.stationEntryList.add(new StationEntry(this, station));
        }
    }

    @Override
    public void onClose() {
        this.filter.onClose();
        this.unpin.onClose();
    }
}
