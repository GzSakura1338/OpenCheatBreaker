package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.component.AccountButton;
import com.cheatbreaker.client.ui.component.MenuIconButton;
import com.cheatbreaker.client.ui.component.MenuTextButton;
import com.cheatbreaker.client.util.FastTrig;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.optifine.GlStateManager;
import org.lwjgl.opengl.GL11;

public class OldMainMenuScreen extends AccountMenuScreen {
    public ResourceLocation logoOuterPng = new ResourceLocation("client/logo_outer.png");
    public ResourceLocation logoInnerPng = new ResourceLocation("client/logo_inner.png");
    public MenuIconButton exit;
    public MenuTextButton options = new MenuTextButton("OPTIONS", false);
    public AccountButton accountButton;
    public boolean flag;
    public MenuTextButton language = new MenuTextButton("LANGUAGE", false);
    public boolean flag2;
    public double rotationPhase;
    public List<MenuTextButton> menuTextButtonList = ImmutableList.of(this.options, this.language);
    public MenuTextButton menuSingleplayer = new MenuTextButton(I18n.format("menu.singleplayer"), true);
    public MenuTextButton menuMultiplayer = new MenuTextButton(I18n.format("menu.multiplayer"), true);
    public List<AccountButton> accountButtonList = new ArrayList<>();
    public float logoRotation = 0.0F;

    @Override
    public void updateScreen() throws IOException {
        super.updateScreen();
        this.rotationPhase += 0.06283185307179587;
        this.logoRotation = (float)((FastTrig.sin(this.rotationPhase) / 2.0 + 0.5) * 180.0);
    }

    public void drawTitleBox() {
        GL11.glPushMatrix();
        GL11.glTranslatef(0.0F, 20.0F, 0.0F);
        Gui.drawRect(
            this.getScaledWidth() / 2.0F - 71.0F,
            this.getScaledHeight() / 4.0F - 40.0F,
            this.getScaledWidth() / 2.0F + 71.0F,
            this.getScaledHeight() / 4.0F + 110.0F,
            -1342177281
        );
        Gui.drawRect(
            this.getScaledWidth() / 2.0F - 73.0F,
            this.getScaledHeight() / 4.0F - 42.0F,
            this.getScaledWidth() / 2.0F - 71.0F,
            this.getScaledHeight() / 4.0F + 112.0F,
            1073741823
        );
        Gui.drawRect(
            this.getScaledWidth() / 2.0F + 71.0F,
            this.getScaledHeight() / 4.0F - 42.0F,
            this.getScaledWidth() / 2.0F + 73.0F,
            this.getScaledHeight() / 4.0F + 112.0F,
            1073741823
        );
        Gui.drawRect(
            this.getScaledWidth() / 2.0F - 71.0F,
            this.getScaledHeight() / 4.0F + 110.0F,
            this.getScaledWidth() / 2.0F + 71.0F,
            this.getScaledHeight() / 4.0F + 112.0F,
            1073741823
        );
        Gui.drawRect(
            this.getScaledWidth() / 2.0F - 71.0F,
            this.getScaledHeight() / 4.0F - 42.0F,
            this.getScaledWidth() / 2.0F + 71.0F,
            this.getScaledHeight() / 4.0F - 40.0F,
            1073741823
        );
        this.drawLogo();
        GL11.glPopMatrix();
    }

    @Override
    public void drawScreenScaled(float f, float f2) {
        drawVerticalGradient(0.0F, 0.0F, this.getScaledWidth(), this.getScaledHeight(), 1610612735, 805306367);
        drawRect(0.0F, 0.0F, this.getScaledWidth(), 25.0F, -819846622);
        this.render();
        this.drawString(this.fontRendererObj, watermark, 2, (int)(this.getScaledHeight() - 10.0F), -1);
        this.drawString(
            this.fontRendererObj,
            "Copyright Mojang AB. Do not distribute!",
            (int)(this.getScaledWidth() - this.fontRendererObj.getStringWidth("Copyright Mojang AB. Do not distribute!") - 2.0F),
            (int)(this.getScaledHeight() - 10.0F),
            -1
        );
        this.drawTitleBox();
        this.menuSingleplayer.setEnabled(CheatBreaker.getInstance().isWebsocketConnected() || CheatBreaker.isOfflineMode());
        this.menuMultiplayer.setEnabled(CheatBreaker.getInstance().isWebsocketConnected() || CheatBreaker.isOfflineMode());
        this.menuSingleplayer.renderAndTestHover(f, f2, true);
        this.menuMultiplayer.renderAndTestHover(f, f2, true);

        for (MenuTextButton menuTextButton : this.menuTextButtonList) {
            menuTextButton.renderAndTestHover(f, f2, true);
        }

        if (this.accountButton != null) {
            this.accountButton.renderAndTestHover(f, f2, true);
            if (this.accountButton.containsPoint(f, f2)) {
                this.flag = true;
            }

            this.flag2 = this.flag
                && f > this.accountButton.getX()
                && f < this.accountButton.getX() + this.accountButton.getWidth()
                && f2 > this.accountButton.getY()
                && f2 < this.accountButton.getY() + this.accountButton.getHeight() * this.accountButtonList.size();
            if (this.flag2) {
                float height = this.accountButton.getHeight();

                for (AccountButton accountButton : this.accountButtonList) {
                    if (accountButton != this.accountButton) {
                        accountButton.renderAndTestHover(f, f2, true);
                    }

                    height += accountButton.getHeight();
                }
            } else {
                this.flag = false;
            }
        }

        this.exit.renderAndTestHover(f, f2, true);
    }

    public void render() {
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderUtils.drawTexture(new ResourceLocation("client/icons/cb.png"), 10.0F, 5.0F, 30.0F, 15.0F);
        GL11.glPopMatrix();
    }

    @Override
    public void mouseClickedScaled(float f, float f2, int n) throws IOException {
        super.mouseClickedScaled(f, f2, n);
        if (this.menuSingleplayer.containsPoint(f, f2)) {
            this.playClickSound();
            this.mc.displayGuiScreen(new GuiSelectWorld(this));
        } else if (this.menuMultiplayer.containsPoint(f, f2)) {
            this.playClickSound();
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        } else if (this.options.containsPoint(f, f2)) {
            this.playClickSound();
            this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
        } else if (this.language.containsPoint(f, f2)) {
            this.playClickSound();
            this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
        } else if (this.exit.containsPoint(f, f2)) {
            this.playClickSound();
            this.mc.shutdown();
        }

        if (this.flag2) {
            for (AccountButton accountButton : this.accountButtonList) {
                if (accountButton != this.accountButton && f < accountButton.getX() + accountButton.getWidth() && f2 > accountButton.getY() && f2 < accountButton.getY() + accountButton.getHeight()) {
                    if (this.switchAccount(accountButton.getLabel()) && accountButton != this.accountButton) {
                        String label = this.accountButton.getLabel();
                        ResourceLocation resourceLocation = this.accountButton.getResourceLocation();
                        this.accountButton.setLabel(accountButton.getLabel());
                        this.accountButton.setResourceLocation(accountButton.getResourceLocation());
                        System.out.println(label);
                        accountButton.setLabel(label);
                        accountButton.setResourceLocation(resourceLocation);
                    }
                    break;
                }
            }
        }
    }

    @Override
    public void drawScreen(int n, int n2, float f) throws IOException {
        if (Minecraft.getMinecraft().theWorld == null) {
            GL11.glDisable(3008);
            this.renderSkybox(n, n2, 1.0F);
            GL11.glEnable(3008);
        }

        super.drawScreen(n, n2, f);
    }

    public void drawLogo() {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
        GlStateManager.pushMatrix();
        Tessellator instance = Tessellator.instance;
        GlStateManager.pushMatrix();
        float f = 0.65F;
        GL11.glScalef(f, f, f);
        GlStateManager.pushMatrix();
        GL11.glTranslatef((this.getScaledWidth() / 2.0F - 40.0F * f) / f, (this.getScaledHeight() / 4.0F - 40.0F * f) / f, 0.0F);
        byte b = 40;
        GL11.glTranslatef(b, b, b);
        GlStateManager.rotate(this.logoRotation, 0.0F, 0.0F, 1.0F);
        GL11.glTranslatef(-b, -b, -b);
        RenderUtils.drawSquareTextureViaRenderEngine(this.logoOuterPng, b, 0.0F, 0.0F);
        GlStateManager.popMatrix();
        RenderUtils.drawSquareTextureViaRenderEngine(
            this.logoInnerPng, 40.0F, (this.getScaledWidth() / 2.0F - 40.0F * f) / f, (this.getScaledHeight() / 4.0F - 39.0F * f) / f
        );
        GlStateManager.popMatrix();
        GlStateManager.popMatrix();
        instance.setColorOpaque_I(-1);
    }

    public void loadAccounts() {
        ArrayList list = new ArrayList();
        if (this.file.exists()) {
            try {
                FileReader fileReader = new FileReader(this.file);
                JsonElement parseReader = JsonParser.parseReader(fileReader);
                Iterator iterator = parseReader.getAsJsonObject().entrySet().iterator();

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

            this.launcherAccountList.clear();

            for (Map map : (Iterable<Map>)list) {
                LauncherAccount launcherAccount = new LauncherAccount(
                    (String)map.get("username"), this.text, (String)map.get("accessToken"), (String)map.get("displayName"), (String)map.get("uuid")
                );
                this.launcherAccountList.add(launcherAccount);
            }
        }
    }

    public boolean switchAccount(String str) {
        try {
            LauncherAccount launcherAccount = null;

            for (LauncherAccount launcherAccount2 : this.launcherAccountList) {
                if (launcherAccount2.getDisplayName().equals(str)) {
                    launcherAccount = launcherAccount2;
                }
            }

            if (launcherAccount != null) {
                if (launcherAccount.getUuid().equalsIgnoreCase(Minecraft.getMinecraft().getSession().getPlayerID())) {
                    return false;
                }

                Minecraft.getMinecraft()
                    .getSoundHandler()
                    .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                if (this.file.exists()) {
                    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(this.file))) {
                        JsonObject asJsonObject = JsonParser.parseReader(bufferedReader).getAsJsonObject();

                        for (Entry entry : asJsonObject.getAsJsonObject("accounts").entrySet()) {
                            JsonObject asJsonObject2 = ((JsonElement)entry.getValue()).getAsJsonObject();
                            if (asJsonObject2.has("accessToken") && asJsonObject2.has("localId") && asJsonObject2.has("minecraftProfile")) {
                                String asString = asJsonObject2.get("accessToken").getAsString();
                                JsonObject asJsonObject3 = asJsonObject2.getAsJsonObject("minecraftProfile");
                                if (asJsonObject3.has("id") && asJsonObject3.has("name")) {
                                    String asString2 = asJsonObject3.get("id").getAsString();
                                    String asString3 = asJsonObject3.get("name").getAsString();
                                    if (asJsonObject.has("activeAccountLocalId")) {
                                        this.text = asJsonObject.get("activeAccountLocalId").getAsString();
                                        if (asString3 != null && asString3.equals(str)) {
                                            Session session = new Session(asString3, asString2, asString, "mojang");
                                            CheatBreaker.getInstance().sessions.add(session);
                                            Minecraft.getMinecraft().setSession(session);
                                            if (Minecraft.getMinecraft().theWorld == null) {
                                                Minecraft.getMinecraft().thePlayer = null;
                                                PlayerViewer.createPreviewWorld();
                                            }

                                            CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().close();
                                            CheatBreaker.getInstance().getWebsocketController().connect();
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public void mouseReleasedScaled(float f, float f2, int n) {
    }

    @Override
    public void initGui() throws IOException {
        super.initGui();
        this.exit = new MenuIconButton(new ResourceLocation("client/icons/exit-64.png"), "EXIT", false);
        this.exit.setBounds(this.getScaledWidth() - 65.0F, 0.0F, 65.0F, 25.0F);
        this.loadAccounts();
        this.accountButton = null;
        this.accountButtonList.clear();
        int n = 0;

        for (LauncherAccount launcherAccount : this.launcherAccountList) {
            String displayName = launcherAccount.getDisplayName();
            AccountButton accountButton = new AccountButton(launcherAccount);
            accountButton.setBounds(this.getScaledWidth() - 200.0F, n * 25, 130.0F, 25.0F);
            this.accountButtonList.add(accountButton);
            if (this.mc.getSession() != null && displayName.equalsIgnoreCase(this.mc.getSession().getUsername())) {
                this.accountButton = accountButton;
                if (n != 0) {
                    AccountButton accountButton2 = this.accountButtonList.get(0);
                    accountButton2.scrollOffsetY = n * 25;
                    this.accountButton.scrollOffsetY = 0;
                }
            }

            n++;
        }

        if (this.accountButton == null && !this.accountButtonList.isEmpty()) {
            this.accountButton = this.accountButtonList.get(0);
        }

        float f = this.getScaledHeight() / 4.0F + 48.0F;
        this.menuSingleplayer.setBounds(this.getScaledWidth() / 2.0F - 65.0F, f + 24.0F, 130.0F, 24.0F);
        this.menuMultiplayer.setBounds(this.getScaledWidth() / 2.0F - 65.0F, f + 52.0F, 130.0F, 24.0F);
        float f2 = 44.0F;

        for (MenuTextButton menuTextButton : this.menuTextButtonList) {
            menuTextButton.setBounds(f2, 0.5F, 50.0F, 25.0F);
            f2 += 50.0F;
        }
    }
}
