package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

public abstract class AccountMenuScreen extends UIScreen {
    public String text = "";
    public static int panoramaTimer = 4100;
    public static String buildTypeText = !CheatBreaker.getInstance().getClientBuildType().isEmpty()
            && !CheatBreaker.getInstance().getClientBuildType().equalsIgnoreCase("Production")
        ? CheatBreaker.getInstance().getClientBuildType() + " "
        : "";
    public ResourceLocation resourceLocation;
    public DynamicTexture dynamicTexture;
    public List<LauncherAccount> launcherAccountList = new ArrayList<>();
    public static String commitText = CheatBreaker.getInstance().getCommitId() + "/" + CheatBreaker.getInstance().getBranch();
    public File file = new File(Minecraft.getMinecraft().mcDataDir + File.separator + "cheatbreaker_accounts.json");
    public static String versionText = getVersionText();
    public ResourceLocation[] resourceLocationArray;
    public static String watermark = "CheatBreaker " + buildTypeText + "(" + versionText + ")";
    public static final String TEXT_COPYRIGHT_MOJANG_AB_DO_N = "Copyright Mojang AB. Do not distribute!";

    public void playClickSound() {
        this.mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
    }

    public static String getVersionText() {
        String str = !CheatBreaker.isAprilFoolsEnabled() ? CheatBreaker.getInstance().getClientVersion() : "2069.4.20";
        if (CheatBreaker.getInstance().getClientBuildType().equalsIgnoreCase("Beta")) {
            return str + "-" + commitText;
        } else {
            return !CheatBreaker.getInstance().getClientBuildType().equalsIgnoreCase("Production")
                    && !CheatBreaker.getInstance().getClientBuildType().equalsIgnoreCase("Preview")
                ? commitText
                : str;
        }
    }

    @Override
    public void updateScreen() throws java.io.IOException {
        super.updateScreen();
        panoramaTimer++;
    }

    public void renderSkybox(int n, int n2, float f) {
        this.mc.getFramebuffer().unbindFramebuffer();
        GL11.glViewport(0, 0, 256, 256);
        this.drawPanorama(n, n2, f);
        this.copyToSkyboxTexture();
        this.copyToSkyboxTexture();
        this.copyToSkyboxTexture();
        this.copyToSkyboxTexture();
        this.copyToSkyboxTexture();
        this.copyToSkyboxTexture();
        this.copyToSkyboxTexture();
        this.mc.getFramebuffer().bindFramebuffer(true);
        GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        Tessellator instance = Tessellator.instance;
        instance.startDrawingQuads();
        float f2 = this.width > this.height ? 120.0F / this.width : 120.0F / this.height;
        float f3 = this.height * f2 / 256.0F;
        float f4 = this.width * f2 / 256.0F;
        instance.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
        float width = this.width;
        float height = this.height;
        instance.addVertexWithUV(0.0, height, floatValue, 0.5F - f3, 0.5F + f4);
        instance.addVertexWithUV(width, height, floatValue, 0.5F - f3, 0.5F - f4);
        instance.addVertexWithUV(width, 0.0, floatValue, 0.5F + f3, 0.5F - f4);
        instance.addVertexWithUV(0.0, 0.0, floatValue, 0.5F + f3, 0.5F + f4);
        instance.draw();
    }

    public void copyToSkyboxTexture() {
        this.mc.getTextureManager().bindTexture(this.resourceLocation);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, 256, 256);
        GlStateManager.enableBlend();
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GlStateManager.colorMask(true, true, true, false);
        Tessellator instance = Tessellator.instance;
        instance.startDrawingQuads();
        GL11.glDisable(3008);
        byte b = 3;

        for (int i = 0; i < b; i++) {
            instance.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F / (i + 1));
            float width = this.width;
            float height = this.height;
            float f = (i - b / 2) / 256.0F;
            instance.addVertexWithUV(width, height, floatValue, 0.0F + f, 1.0);
            instance.addVertexWithUV(width, 0.0, floatValue, 1.0F + f, 1.0);
            instance.addVertexWithUV(0.0, 0.0, floatValue, 1.0F + f, 0.0);
            instance.addVertexWithUV(0.0, height, floatValue, 0.0F + f, 0.0);
        }

        instance.draw();
        GL11.glEnable(3008);
        GlStateManager.colorMask(true, true, true, true);
    }

    @Override
    public void mouseClickedScaled(float f, float f2, int n) throws java.io.IOException {
        if (f >= 8.0F && f <= 26.0F && f2 >= 6.0F && f2 <= 26.0F) {
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            MenuScreen.cycleMenuStyle();
        }
    }

    public void drawPanorama(int n, int n2, float f) {
        Tessellator instance = Tessellator.instance;
        GlStateManager.matrixMode(5889);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
        GlStateManager.matrixMode(5888);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.enableBlend();
        GL11.glDisable(3008);
        GL11.glDisable(2884);
        GlStateManager.depthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        byte b = 8;

        for (int i = 0; i < b * b; i++) {
            GlStateManager.pushMatrix();
            float f2 = ((float)(i % b) / b - 0.5F) / 64.0F;
            float f3 = ((float)(i / b) / b - 0.5F) / 64.0F;
            float f4 = 0.0F;
            GL11.glTranslatef(f2, f3, f4);
            GlStateManager.rotate(MathHelper.sin((panoramaTimer + f) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(-(panoramaTimer + f) * 0.39240506F * 0.2548387F, 0.0F, 1.0F, 0.0F);

            for (int j = 0; j < 6; j++) {
                GlStateManager.pushMatrix();
                if (j == 1) {
                    GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (j == 2) {
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                }

                if (j == 3) {
                    GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (j == 4) {
                    GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (j == 5) {
                    GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                this.mc.getTextureManager().bindTexture(this.resourceLocationArray[j]);
                instance.startDrawingQuads();
                instance.setColorRGBA_I(16777215, 255 / (i + 1));
                float f5 = 0.0F;
                instance.addVertexWithUV(-1.0, -1.0, 1.0, 0.0F + f5, 0.0F + f5);
                instance.addVertexWithUV(1.0, -1.0, 1.0, 1.0F - f5, 0.0F + f5);
                instance.addVertexWithUV(1.0, 1.0, 1.0, 1.0F - f5, 1.0F - f5);
                instance.addVertexWithUV(-1.0, 1.0, 1.0, 0.0F + f5, 1.0F - f5);
                instance.draw();
                GlStateManager.popMatrix();
            }

            GlStateManager.popMatrix();
            GlStateManager.colorMask(true, true, true, false);
        }

        instance.setTranslation(0.0, 0.0, 0.0);
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.matrixMode(5889);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(5888);
        GlStateManager.popMatrix();
        GlStateManager.depthMask(true);
        GL11.glEnable(2884);
        GL11.glEnable(2929);
    }

    @Override
    public void initGui() throws java.io.IOException {
        super.initGui();
        int n = CheatBreaker.getInstance().getKeybindSettings().getMainMenuStyle().ordinal();
        MainMenuStyle mainMenuStyle = MainMenuStyle.values()[n];
        if (mainMenuStyle == MainMenuStyle.MAIN) {
            this.resourceLocationArray = PanoramaTextures.resourceLocationArray;
        } else {
            this.resourceLocationArray = PanoramaTextures.resourceLocationArray2;
        }

        this.dynamicTexture = new DynamicTexture(256, 256);
        this.resourceLocation = this.mc.getTextureManager().getDynamicTextureLocation("background", this.dynamicTexture);
    }

    @Override
    public void keyTyped(char c, int n) throws java.io.IOException {
        if (n != 1 || !(this.mc.currentScreen instanceof MainMenuScreen)) {
            super.keyTyped(c, n);
        }
    }
}
