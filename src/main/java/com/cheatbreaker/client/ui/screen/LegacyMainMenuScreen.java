package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.ui.component.AccountButton;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

public class LegacyMainMenuScreen extends GuiScreen implements GuiYesNoCallback {
    public int warning2Width;
    public static int panoramaTimer;
    public String splashText = "missingno";
    public static Logger logger = LogManager.getLogger();
    public Object threadLock = new Object();
    public int warningY2;
    public static Random RANDOM = new Random();
    public static ResourceLocation splashesTxt = new ResourceLocation("texts/splashes.txt");
    public String openGLWarning2;
    public int warningX1;
    public ResourceLocation backgroundTexture;
    public static ResourceLocation minecraftPng = new ResourceLocation("textures/gui/title/minecraft.png");
    public List unusedList;
    public boolean unusedFlag1;
    public int warningY1;
    public static ResourceLocation[] TITLE_PANORAMA_PATHS = new ResourceLocation[]{
        new ResourceLocation("textures/gui/title/background/panorama_0.png"),
        new ResourceLocation("textures/gui/title/background/panorama_1.png"),
        new ResourceLocation("textures/gui/title/background/panorama_2.png"),
        new ResourceLocation("textures/gui/title/background/panorama_3.png"),
        new ResourceLocation("textures/gui/title/background/panorama_4.png"),
        new ResourceLocation("textures/gui/title/background/panorama_5.png")
    };
    public static String OPENGL_WARNING_TEXT = "Please click " + EnumChatFormatting.UNDERLINE + "here" + EnumChatFormatting.RESET + " for more information.";
    public int warningX2;
    public int warning1Width;
    public String text;
    public AccountButton accountButton;
    public float updateCounter;
    public String openGLWarning1;
    public DynamicTexture dynamicTexture;
    public String openGLWarningLink;

    public void unusedHook(int n, int n2) {
    }

    @Override
    public void drawScreen(int n, int n2, float f) throws IOException {
        GL11.glDisable(3008);
        this.renderSkybox(n, n2, f);
        GL11.glEnable(3008);
        short s = 274;
        int n3 = this.width / 2 - s / 2;
        byte b = 30;
        drawVerticalGradient(0.0F, 0.0F, this.width, this.height, 1610612735, 805306367);
        drawRect(0.0F, 0.0F, this.width, 25.0F, -819846622);
        String str = "CheatBreaker (" + CheatBreaker.getInstance().getCommitId() + "/" + CheatBreaker.getInstance().getBranch() + ")";
        this.drawString(this.fontRendererObj, str, 2, this.height - 10, -1);
        String str2 = "Copyright Mojang AB. Do not distribute!";
        this.drawString(this.fontRendererObj, str2, this.width - this.fontRendererObj.getStringWidth(str2) - 2, this.height - 10, -1);
        if (this.openGLWarning1 != null && this.openGLWarning1.length() > 0) {
            drawRect(this.warningX1 - 2, this.warningY1 - 2, this.warningX2 + 2, this.warningY2 - 1, 1428160512);
            this.drawString(this.fontRendererObj, this.openGLWarning1, this.warningX1, this.warningY1, -1);
            this.drawString(this.fontRendererObj, this.openGLWarning2, (this.width - this.warning2Width) / 2, this.buttonList.get(0).yPosition - 12, -1);
        }

        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f2 = 30.0F;
        float f3 = 15.0F;
        float f4 = 0.0F;
        float f5 = 0.0F;
        float f6 = 10.0F;
        float f7 = 5.0F;
        GL11.glEnable(3042);
        this.mc.renderEngine.bindTexture(new ResourceLocation("client/icons/cb.png"));
        GL11.glBegin(7);
        GL11.glTexCoord2d(f4 / 5.0F, f5 / 5.0F);
        GL11.glVertex2d(f6, f7);
        GL11.glTexCoord2d(f4 / 5.0F, (f5 + 5.0F) / 5.0F);
        GL11.glVertex2d(f6, f7 + f3);
        GL11.glTexCoord2d((f4 + 5.0F) / 5.0F, (f5 + 5.0F) / 5.0F);
        GL11.glVertex2d(f6 + f2, f7 + f3);
        GL11.glTexCoord2d((f4 + 5.0F) / 5.0F, f5 / 5.0F);
        GL11.glVertex2d(f6 + f2, f7);
        GL11.glEnd();
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    public void rotateAndBlurSkybox(float f) {
        this.mc.getTextureManager().bindTexture(this.backgroundTexture);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, 256, 256);
        GL11.glEnable(3042);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        GL11.glColorMask(true, true, true, false);
        Tessellator instance = Tessellator.instance;
        instance.startDrawingQuads();
        GL11.glDisable(3008);
        byte b = 3;

        for (int i = 0; i < b; i++) {
            instance.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F / (i + 1));
            int width = this.width;
            int height = this.height;
            float f2 = (i - b / 2) / 256.0F;
            instance.addVertexWithUV(width, height, floatValue, 0.0F + f2, 1.0);
            instance.addVertexWithUV(width, 0.0, floatValue, 1.0F + f2, 1.0);
            instance.addVertexWithUV(0.0, 0.0, floatValue, 1.0F + f2, 0.0);
            instance.addVertexWithUV(0.0, height, floatValue, 0.0F + f2, 0.0);
        }

        instance.draw();
        GL11.glEnable(3008);
        GL11.glColorMask(true, true, true, true);
    }

    public void drawPanorama(int n, int n2, float f) {
        Tessellator instance = Tessellator.instance;
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
        GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
        GL11.glEnable(3042);
        GL11.glDisable(3008);
        GL11.glDisable(2884);
        GL11.glDepthMask(false);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        byte b = 8;

        for (int i = 0; i < b * b; i++) {
            GL11.glPushMatrix();
            float f2 = ((float)(i % b) / b - 0.5F) / 64.0F;
            float f3 = ((float)(i / b) / b - 0.5F) / 64.0F;
            float f4 = 0.0F;
            GL11.glTranslatef(f2, f3, f4);
            GL11.glRotatef(MathHelper.sin((panoramaTimer + f) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(-(panoramaTimer + f) * 0.1F, 0.0F, 1.0F, 0.0F);

            for (int j = 0; j < 6; j++) {
                GL11.glPushMatrix();
                if (j == 1) {
                    GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (j == 2) {
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                }

                if (j == 3) {
                    GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
                }

                if (j == 4) {
                    GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (j == 5) {
                    GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                this.mc.getTextureManager().bindTexture(TITLE_PANORAMA_PATHS[j]);
                instance.startDrawingQuads();
                instance.setColorRGBA_I(16777215, 255 / (i + 1));
                float f5 = 0.0F;
                instance.addVertexWithUV(-1.0, -1.0, 1.0, 0.0F + f5, 0.0F + f5);
                instance.addVertexWithUV(1.0, -1.0, 1.0, 1.0F - f5, 0.0F + f5);
                instance.addVertexWithUV(1.0, 1.0, 1.0, 1.0F - f5, 1.0F - f5);
                instance.addVertexWithUV(-1.0, 1.0, 1.0, 0.0F + f5, 1.0F - f5);
                instance.draw();
                GL11.glPopMatrix();
            }

            GL11.glPopMatrix();
            GL11.glColorMask(true, true, true, false);
        }

        instance.setTranslation(0.0, 0.0, 0.0);
        GL11.glColorMask(true, true, true, true);
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glEnable(2884);
        GL11.glEnable(2929);
    }

    public LegacyMainMenuScreen() {
        this.openGLWarning2 = OPENGL_WARNING_TEXT;
        this.unusedList = new ArrayList();
        this.unusedFlag1 = false;
        this.text = "";
        BufferedReader bufferedReader = null;

        try {
            ArrayList list = new ArrayList();
            bufferedReader = new BufferedReader(
                new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(splashesTxt).getInputStream(), Charsets.UTF_8)
            );

            String str;
            while ((str = bufferedReader.readLine()) != null) {
                if (!(str = str.trim()).isEmpty()) {
                    list.add(str);
                }
            }

            if (!list.isEmpty()) {
                do {
                    this.splashText = (String)list.get(RANDOM.nextInt(list.size()));
                } while (this.splashText.hashCode() == 125780783);
            }
        } catch (IOException e) {
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                }
            }
        }

        this.updateCounter = RANDOM.nextFloat();
        this.openGLWarning1 = "";
        if (!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported()) {
            this.openGLWarning1 = I18n.format("title.oldgl1");
            this.openGLWarning2 = I18n.format("title.oldgl2");
            this.openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void confirmClicked(boolean flag, int n) throws IOException {
        if (flag && n == 12) {
            ISaveFormat saveLoader = this.mc.getSaveLoader();
            saveLoader.flushCache();
            saveLoader.deleteWorldDirectory("Demo_World");
            this.mc.displayGuiScreen(this);
        } else if (n == 13) {
            if (flag) {
                try {
                    Class forName = Class.forName("java.awt.Desktop");
                    Object invoke = forName.getMethod("getDesktop").invoke(null);
                    forName.getMethod("browse", URI.class).invoke(invoke, new URI(this.openGLWarningLink));
                } catch (Throwable e) {
                    logger.error("Couldn't open link", e);
                }
            }

            this.mc.displayGuiScreen(this);
        }
    }

    public void renderSkybox(int n, int n2, float f) {
        this.mc.getFramebuffer().unbindFramebuffer();
        GL11.glViewport(0, 0, 256, 256);
        this.drawPanorama(n, n2, f);
        this.rotateAndBlurSkybox(f);
        this.rotateAndBlurSkybox(f);
        this.rotateAndBlurSkybox(f);
        this.rotateAndBlurSkybox(f);
        this.rotateAndBlurSkybox(f);
        this.rotateAndBlurSkybox(f);
        this.rotateAndBlurSkybox(f);
        this.mc.getFramebuffer().bindFramebuffer(true);
        GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
        Tessellator instance = Tessellator.instance;
        instance.startDrawingQuads();
        float f2 = this.width > this.height ? 120.0F / this.width : 120.0F / this.height;
        float f3 = this.height * f2 / 256.0F;
        float f4 = this.width * f2 / 256.0F;
        instance.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
        int width = this.width;
        int height = this.height;
        instance.addVertexWithUV(0.0, height, floatValue, 0.5F - f3, 0.5F + f4);
        instance.addVertexWithUV(width, height, floatValue, 0.5F - f3, 0.5F - f4);
        instance.addVertexWithUV(width, 0.0, floatValue, 0.5F + f3, 0.5F - f4);
        instance.addVertexWithUV(0.0, 0.0, floatValue, 0.5F + f3, 0.5F + f4);
        instance.draw();
    }

    @Override
    public void mouseClicked(int n, int n2, int n3) throws IOException {
        super.mouseClicked(n, n2, n3);
        Object threadLock = this.threadLock;
        if (n <= 45 && n2 <= 25) {
            this.mc.displayGuiScreen(new ClassicMainMenuScreen());
        }

        synchronized (this.threadLock) {
            if (this.openGLWarning1.length() > 0 && n >= this.warningX1 && n <= this.warningX2 && n2 >= this.warningY1 && n2 <= this.warningY2) {
                GuiConfirmOpenLink guiConfirmOpenLink = new GuiConfirmOpenLink(this, this.openGLWarningLink, 13, true);
                guiConfirmOpenLink.disableSecurityWarning();
                this.mc.displayGuiScreen(guiConfirmOpenLink);
            }
        }
    }

    @Override
    public void keyTyped(char c, int n) throws IOException {
        this.mc.dispatchKeypresses();
        if (n == 1) {
            this.mc.displayGuiScreen(new ClassicMainMenuScreen());
        }
    }

    public void drawButtons(int n, int n2) throws IOException {
        for (int i = 0; i < this.buttonList.size(); i++) {
            this.buttonList.get(i).drawButton(this.mc, n, n2);
        }

        for (int i = 0; i < this.labelList.size(); i++) {
            ((GuiLabel)this.labelList.get(i)).drawLabel(this.mc, n, n2);
        }
    }

    @Override
    public void updateScreen() {
        panoramaTimer++;
    }

    @Override
    public void actionPerformed(GuiButton guiButton) throws IOException {
        if (guiButton.id == 0) {
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        }

        if (guiButton.id == 5) {
            this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
        }

        if (guiButton.id == 66) {
            this.mc.displayGuiScreen(new OldCosmeticsScreen());
        }

        if (guiButton.id == 67) {
            this.mc.displayGuiScreen(new QuickEmoteWheelScreen(8));
        }

        if (guiButton.id == 67) {
        }

        if (guiButton.id == 1) {
            this.mc.displayGuiScreen(new GuiSelectWorld(this));
        }

        if (guiButton.id == 2) {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        }

        if (guiButton.id == 14) {
            this.switchToRealms();
        }

        if (guiButton.id == 4) {
            this.mc.shutdown();
        }

        if (guiButton.id == 11) {
            this.mc.launchIntegratedServer("Demo_World", "Demo_World", DemoWorldServer.demoWorldSettings);
        }

        WorldInfo worldInfo;
        if (guiButton.id == 12 && (worldInfo = this.mc.getSaveLoader().getWorldInfo("Demo_World")) != null) {
            GuiYesNo createDeleteWarningScreen = GuiSelectWorld.createDeleteWarningScreen(this, worldInfo.getWorldName(), 12);
            this.mc.displayGuiScreen(createDeleteWarningScreen);
        }
    }

    public void switchToRealms() {
        RealmsBridge realmsBridge = new RealmsBridge();
        realmsBridge.switchToRealms(this);
    }

    @Override
    public void initGui() {
        this.dynamicTexture = new DynamicTexture(256, 256);
        this.backgroundTexture = this.mc.getTextureManager().getDynamicTextureLocation("background", this.dynamicTexture);
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        File file = new File(Minecraft.getMinecraft().mcDataDir + File.separator + "launcher_accounts.json");
        ArrayList list = new ArrayList();
        if (file.exists()) {
            try {
                FileReader fileReader = new FileReader(file);
                JsonParser jsonParser = new JsonParser();
                JsonElement parse = jsonParser.parse(fileReader);
                Iterator iterator = parse.getAsJsonObject().entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry entry = (Entry)iterator.next();

                    for (Entry entry2 : ((JsonElement)entry.getValue()).getAsJsonObject().entrySet()) {
                        HashMap map = new HashMap();

                        for (Entry entry3 : ((JsonElement)entry2.getValue()).getAsJsonObject().entrySet()) {
                            if (((String)entry3.getKey()).equalsIgnoreCase("minecraftProfile")) {
                                for (Entry entry4 : ((JsonElement)entry3.getValue()).getAsJsonObject().entrySet()) {
                                    if (((String)entry4.getKey()).equals("id")) {
                                        map.put("uuid", ((JsonElement)entry4.getValue()).getAsString());
                                    }

                                    if (((String)entry4.getKey()).equals("name")) {
                                        map.put("displayName", ((JsonElement)entry4.getValue()).getAsString());
                                    }
                                }
                            } else if (((String)entry3.getKey()).equalsIgnoreCase("username")
                                || ((String)entry3.getKey()).equalsIgnoreCase("name")
                                || ((String)entry3.getKey()).equalsIgnoreCase("id")
                                || ((String)entry3.getKey()).equalsIgnoreCase("accessToken")) {
                                map.put(entry3.getKey(), ((JsonElement)entry3.getValue()).getAsString());
                            }
                        }

                        list.add(map);
                    }

                    while (iterator.hasNext()) {
                        Object obj = iterator.next();
                        Entry entry2 = (Entry)obj;
                        if (entry2.getKey().equals("mojangClientToken")) {
                            JsonPrimitive value = (JsonPrimitive)entry2.getValue();
                            this.text = value.getAsString();
                        }
                    }
                }
            } catch (Exception e) {
            }
        }

        this.accountButton = null;
        this.unusedList.clear();
        Object threadLock = this.threadLock;
        synchronized (this.threadLock) {
            this.warning1Width = this.fontRendererObj.getStringWidth(this.openGLWarning1);
            this.warning2Width = this.fontRendererObj.getStringWidth(this.openGLWarning2);
            int max = Math.max(this.warning1Width, this.warning2Width);
            this.warningX1 = (this.width - max) / 2;
            this.warningY1 = this.buttonList.get(0).yPosition - 24;
            this.warningX2 = this.warningX1 + max;
            this.warningY2 = this.warningY1 + 24;
        }
    }
}
