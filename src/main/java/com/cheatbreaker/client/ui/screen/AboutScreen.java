package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.component.ScrollBar;
import com.cheatbreaker.client.ui.component.TextButton;
import com.cheatbreaker.client.util.TextUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;
import org.lwjgl.opengl.GL11;

public class AboutScreen extends MenuScreen {
    public final boolean flag = false;
    public TextButton back = new TextButton("BACK");
    public ScrollBar scrollBar = new ScrollBar(null);

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
        String str = "About this build";
        CheatBreaker.getInstance().getFontManager().playRegular16.drawString(str.toUpperCase(), f3 + 8.0F, f5 + 5.0F, -1);
        String str2 = "";
        String wrapToString = TextUtils.wrapSequence(str2).setMaxWidth(60).setHyphenate(false).wrapToString();
        String[] split = wrapToString.split("\n");
        int n = 0;
        boolean flag = false;
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

        for (String str3 : split) {
            this.getClass();
            CheatBreaker.getInstance()
                .getFontManager()
                .playRegular14
                .drawString(str3, f3 + 8.0F, f5 + 18.0F + n * (CheatBreaker.getInstance().getFontManager().playRegular14.getFont().getSize() / 2.0F), -1);
            n++;
        }

        this.scrollBar.setContentHeight(n * (CheatBreaker.getInstance().getFontManager().playRegular14.getFont().getSize() / 2.0F) + 2.0F);
        GL11.glDisable(3089);
        GlStateManager.popMatrix();
        this.scrollBar.renderAndTestHover(f, f2, true);
    }

    @Override
    public void initGui() throws java.io.IOException {
        super.initGui();
        float min = Math.min(240.0F, this.getScaledWidth() - 10.0F);
        float f = this.getScaledWidth() / 2.0F + min / 2.0F;
        float f2 = 40.0F;
        float f3 = this.getScaledHeight() - 40.0F;
        this.scrollBar.setBounds(f - 8.0F, f2 + 18.0F, 4.0F, f3 - f2 - 28.0F);
    }

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
        }
    }

    @Override
    public void handleMouseInput() throws java.io.IOException {
        super.handleMouseInput();
        this.scrollBar.handleMouseInput();
    }
}
