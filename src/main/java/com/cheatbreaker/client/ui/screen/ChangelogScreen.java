package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.component.ChangelogEntryElement;
import com.cheatbreaker.client.ui.component.ScrollBar;
import com.cheatbreaker.client.ui.component.TextButton;
import com.cheatbreaker.client.util.SneakyThrow;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;
import org.lwjgl.opengl.GL11;

public class ChangelogScreen extends MenuScreen {
    public List<ChangelogEntryElement> changelogEntryElementList = new ArrayList<>();
    public TextButton back = new TextButton("BACK");
    public ScrollBar scrollBar = new ScrollBar(null);
    public static JsonObject jsonObject;

    @Override
    public void mouseClickedScaled(float f, float f2, int n) throws java.io.IOException {
        super.mouseClickedScaled(f, f2, n);
        if (this.back.containsPoint(f, f2)) {
            Minecraft.getMinecraft()
                .getSoundHandler()
                .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            this.mc2.displayGuiScreen(new MainMenuScreen());
        } else if (this.scrollBar.containsPoint(f, f2)) {
            this.scrollBar.mouseClicked(f, f2, n, true);
        } else {
            float min = Math.min(240.0F, this.getScaledWidth() - 10.0F);
            float f3 = this.getScaledWidth() / 2.0F - min / 2.0F;
            float f4 = this.getScaledWidth() / 2.0F + min / 2.0F;
            float f5 = 40.0F;
            float f6 = this.getScaledHeight() - 40.0F;
            if (f > f3 && f < f4 && f2 > f5 && f2 < f6) {
                for (ChangelogEntryElement changelogEntryElement : this.changelogEntryElementList) {
                    changelogEntryElement.mouseClicked(
                        f,
                        f2 - this.scrollBar.getScrollOffset(),
                        n,
                        changelogEntryElement.containsPoint(f, f2 - this.scrollBar.getScrollOffset()) && !this.scrollBar.isDragging()
                    );
                }
            }
        }
    }

    public void loadChangelog() {
        try {
            String branch = CheatBreaker.getInstance().getBranch();
            if (jsonObject != null && jsonObject.toString().contains("\"" + CheatBreaker.getInstance().getBranch() + "\"")) {
                if (jsonObject.toString().contains("\"?\"") && CheatBreaker.getInstance().getBranch().equalsIgnoreCase("dev")) {
                    branch = "?";
                }

                for (Entry entry : jsonObject.getAsJsonObject(branch).entrySet()) {
                    JsonObject asJsonObject = ((JsonElement)entry.getValue()).getAsJsonObject();
                    String key = (String)entry.getKey();
                    String asString = asJsonObject.get("author").getAsString();
                    String asString2 = asJsonObject.get("description").getAsString();
                    this.changelogEntryElementList.add(new ChangelogEntryElement(key, asString, asString2));
                }
            }
        } catch (Throwable e) {
            throw SneakyThrow.sneakyThrow(e);
        }
    }

    @Override
    public void handleMouseInput() throws java.io.IOException {
        super.handleMouseInput();
        this.scrollBar.handleMouseInput();
    }

    @Override
    public void drawScreenScaled(float f, float f2) {
        super.drawScreenScaled(f, f2);
        float min = Math.min(240.0F, this.getScaledWidth() - 10.0F);
        float f3 = this.getScaledWidth() / 2.0F - min / 2.0F;
        float f4 = this.getScaledWidth() / 2.0F + min / 2.0F;
        float f5 = 40.0F;
        float f6 = this.getScaledHeight() - 40.0F;
        Gui.drawRect(f3, f5, f4, f6, 788529152);
        Gui.drawRect(f3 + 8.0F, f5 + 16.0F, f4 - 8.0F, f5 + 16.5F, 452984831);
        CheatBreaker.getInstance().getFontManager().playRegular16.drawString("CHANGELOG", f3 + 8.0F, f5 + 5.0F, -1);
        if (this.changelogEntryElementList.size() < 1) {
            CheatBreaker.getInstance()
                .getFontManager()
                .playRegular16
                .drawCenteredString("Nothing has changed in the client, that's odd!", this.getScaledWidth() / 2.0F, this.getScaledHeight() / 2.0F + 4.0F, -6381922);
        }

        this.back.setBounds(this.getScaledWidth() / 2.0F - 30.0F, this.getScaledHeight() - 35.0F, 60.0F, 12.0F);
        this.back.renderAndTestHover(f, f2, true);
        this.scrollBar.scrollDown(f, f2, true);
        GlStateManager.pushMatrix();
        GL11.glEnable(3089);
        RenderUtils.scissorScaled(
            (int)f3,
            (int)f5 + 18,
            (int)f4,
            (int)f6 - 8,
            (int)(this.getScaledResolution().getScaleFactor() * this.getUiScale()),
            (int)this.getScaledHeight()
        );

        for (ChangelogEntryElement changelogEntryElement : this.changelogEntryElementList) {
            changelogEntryElement.renderAndTestHover(f, f2 - this.scrollBar.getScrollOffset(), !this.scrollBar.isDragging());
        }

        GL11.glDisable(3089);
        GlStateManager.popMatrix();
        this.scrollBar.renderAndTestHover(f, f2, true);
    }

    public ChangelogScreen() {
        this.loadChangelog();
    }

    @Override
    public void initGui() throws java.io.IOException {
        super.initGui();
        float min = Math.min(240.0F, this.getScaledWidth() - 10.0F);
        float f = this.getScaledWidth() / 2.0F - min / 2.0F;
        float f2 = this.getScaledWidth() / 2.0F + min / 2.0F;
        float f3 = 40.0F;
        float f4 = this.getScaledHeight() - 40.0F;
        this.scrollBar.setBounds(f2 - 8.0F, f3 + 18.0F, 4.0F, f4 - f3 - 28.0F);
        int n = 0;
        float f5 = 22.0F;

        for (ChangelogEntryElement changelogEntryElement : this.changelogEntryElementList) {
            changelogEntryElement.setBounds(f + 8.0F, f3 + 20.0F + n * (f5 + 1.0F), min - 16.0F, f5);
            n++;
        }

        this.scrollBar.setContentHeight(this.changelogEntryElementList.size() * (f5 + 1.0F) + 2.0F);
    }
}
