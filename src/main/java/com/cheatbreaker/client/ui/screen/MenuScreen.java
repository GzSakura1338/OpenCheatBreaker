package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.animation.ColorAnimation;
import com.cheatbreaker.client.network.ReconnectHandler;
import com.cheatbreaker.client.network.packet.MessagePacket;
import com.cheatbreaker.client.ui.component.AccountSwitcher;
import com.cheatbreaker.client.ui.component.IconButton;
import com.cheatbreaker.client.ui.component.StatusLabel;
import com.cheatbreaker.client.ui.component.TextLabel;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.awt.Color;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import org.lwjgl.opengl.GL11;

public class MenuScreen extends AccountMenuScreen {
    public Minecraft mc2 = Minecraft.getMinecraft();
    public List<LauncherAccount> accounts = new CopyOnWriteArrayList<>();
    public ResourceLocation logo42Png = new ResourceLocation("client/logo_42.png");
    public IconButton logoButton = new IconButton(10.0F, this.logo42Png);
    public IconButton quitButton = new IconButton(new ResourceLocation("client/icons/delete-64.png"));
    public IconButton languageButton = new IconButton(6.0F, new ResourceLocation("client/icons/globe-24.png"));
    public IconButton skinButton = new IconButton(6.0F, new ResourceLocation("client/emotes/dab.png"));
    public IconButton discordLinkButton = new IconButton(new ResourceLocation("client/icons/chain-64.png"));
    public AccountSwitcher accountSwitcher = new AccountSwitcher(
        this,
        Minecraft.getMinecraft().getSession().getUsername(),
        CheatBreaker.getInstance().getFontManager().getHeadTexture(Minecraft.getMinecraft().getSession().getUsername())
    );
    public StatusLabel options = new StatusLabel("OPTIONS");
    public StatusLabel cosmetics = new StatusLabel("COSMETICS");
    public List<StatusLabel> tabs = ImmutableList.of(this.options, this.cosmetics);
    public List<IconButton> iconButtons = ImmutableList.of(this.languageButton);
    public final String emptyText = "";
    public TextLabel versionLabel = new TextLabel(watermark);
    public TextLabel copyrightLabel = new TextLabel("Copyright Mojang AB. Do not distribute!");
    public TextLabel aboutLabel = new TextLabel("", 50);
    public ColorAnimation colorAnimation3 = new ColorAnimation(251658240, -16777216);
    public float usernameWidth = CheatBreaker.getInstance().getFontManager().robotoRegular13.getStringWidth(Minecraft.getMinecraft().getSession().getUsername());

    @Override
    public void mouseReleasedScaled(float f, float f2, int n) {
    }

    public void switchAccount(String str) {
        try {
            LauncherAccount launcherAccount = null;

            for (LauncherAccount launcherAccount2 : this.accounts) {
                if (launcherAccount2.getDisplayName().equals(str)) {
                    launcherAccount = launcherAccount2;
                }
            }

            if (launcherAccount != null) {
                if (launcherAccount.getUuid().equalsIgnoreCase(Minecraft.getMinecraft().getSession().getPlayerID())) {
                    return;
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

                                            this.accountSwitcher.setLabel(asString3);
                                            this.accountSwitcher.setResourceLocation(CheatBreaker.getInstance().getFontManager().getHeadTexture(asString3));
                                            this.loadAccounts();
                                            this.updateSwitcherBounds();
                                            ReconnectHandler.reconnectDisabled = false;
                                            CheatBreaker.getInstance().getWebsocketController().connect();
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
    }

    public List<LauncherAccount> getAccounts() {
        return this.accounts;
    }

    @Override
    public void updateScreen() throws IOException {
        super.updateScreen();
    }

    public static void cycleMenuStyle() throws IOException {
        int n = CheatBreaker.getInstance().getKeybindSettings().getMainMenuStyle().ordinal() + 1;
        MainMenuStyle mainMenuStyle;
        if (n == 2) {
            mainMenuStyle = MainMenuStyle.OLD;
        } else {
            mainMenuStyle = MainMenuStyle.values()[n];
        }

        CheatBreaker.getInstance().getKeybindSettings().getCurrentMenu().changeValue(mainMenuStyle.ordinal());
        Minecraft.getMinecraft().displayGuiScreen(MainMenuStyle.createScreen(mainMenuStyle));
    }

    public void updateSwitcherBounds() {
        this.accountSwitcher
            .setBounds(
                this.getScaledWidth() - 35.0F - this.accountSwitcher.getTotalHeight(this.usernameWidth),
                7.0F,
                this.accountSwitcher.getTotalHeight(this.usernameWidth),
                17.0F
            );
    }

    @Override
    public void mouseClickedScaled(float f, float f2, int n) throws IOException {
        this.quitButton.mouseClicked(f, f2, n, true);
        this.accountSwitcher.mouseClicked(f, f2, n, true);
        if (this.quitButton.containsPoint(f, f2)) {
            this.mc2.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            this.mc2.shutdown();
        } else if (this.options.containsPoint(f, f2)) {
            this.mc2.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            this.mc2.displayGuiScreen(new GuiOptions(this, this.mc2.gameSettings));
        } else if (this.languageButton.containsPoint(f, f2)) {
            this.mc2.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            this.mc2.displayGuiScreen(new GuiLanguage(this, this.mc2.gameSettings, this.mc2.getLanguageManager()));
        } else if (this.skinButton.containsPoint(f, f2)) {
            this.mc2.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            this.mc2.displayGuiScreen(new SkinSelectScreen());
        } else if (this.cosmetics.containsPoint(f, f2)) {
            this.mc2.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            this.mc2.displayGuiScreen(new CosmeticsScreen());
        } else if (this.versionLabel.containsPoint(f, f2)) {
            if (n == 1) {
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(CheatBreaker.getInstance().getCommitId()), null);
            } else {
                this.mc2.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
                this.mc2.displayGuiScreen(new BuildInfoScreen());
            }
        } else if (this.copyrightLabel.containsPoint(f, f2)) {
            this.mc2.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            this.mc2.displayGuiScreen(new CreditsScreen());
        } else if (this.aboutLabel.containsPoint(f, f2) && CheatBreaker.getInstance().isAboutScreenEnabled()) {
            this.mc2.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            this.mc2.displayGuiScreen(new AboutScreen());
        } else if (this.discordLinkButton.containsPoint(f, f2)) {
            this.mc2.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            System.out.println("[CB] Started Discord link process");
            CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().sendPacket(new MessagePacket("link"));
            this.mc2.displayGuiScreen(new DiscordLinkScreen());
        } else if (this.logoButton.containsPoint(f, f2)) {
            this.mc2.getSoundHandler().playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            cycleMenuStyle();
        }
    }

    public MenuScreen() {
        this.loadAccounts();
        this.logoButton.setFlag(false);
    }

    @Override
    public void keyTyped(char c, int n) throws IOException {
        super.keyTyped(c, n);
    }

    @Override
    public void drawScreenScaled(float f, float f2) {
        drawVerticalGradient(0.0F, 0.0F, this.getScaledWidth(), this.getScaledHeight(), 1610612735, 805306367);
        drawVerticalGradient(0.0F, 0.0F, this.getScaledWidth(), 160.0F, -553648128, 0);
        boolean flag = f < this.options.getX() && f2 < 30.0F;
        Color color = this.colorAnimation3.getColor(flag);
        CheatBreaker.getInstance()
            .getFontManager()
            .robotoRegular24
            .drawString(CheatBreaker.clientName, 37.0F, 9.0F, this.logoButton.isEnabled() ? color.getRGB() : 251658240);
        this.logoButton.renderAndTestHover(f, f2, true);
        this.logoButton.setAlpha(this.logoButton.isEnabled() ? 1.0F : 0.8F);
        CheatBreaker.getInstance()
            .getFontManager()
            .robotoRegular24
            .drawString(CheatBreaker.clientName, 36.0F, 8.0F, this.logoButton.isEnabled() ? -1 : -1342177281);
        this.quitButton.renderAndTestHover(f, f2, true);

        for (IconButton iconButton : this.iconButtons) {
            iconButton.renderAndTestHover(f, f2, true);
        }

        this.accountSwitcher.renderAndTestHover(f, f2, true);

        for (StatusLabel statusLabel : this.tabs) {
            statusLabel.renderAndTestHover(f, f2, true);
        }

        this.versionLabel.renderAndTestHover(f, f2, true);
        this.copyrightLabel.renderAndTestHover(f, f2, true);
        if (CheatBreaker.getInstance().isAboutScreenEnabled()) {
            this.aboutLabel.renderAndTestHover(f, f2, true);
        }

        this.discordLinkButton.renderAndTestHover(f, f2, true);
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

    public void openUri(URI uri) {
        try {
            Class forName = Class.forName("java.awt.Desktop");
            Object invoke = forName.getMethod("getDesktop").invoke(null);
            forName.getMethod("browse", URI.class).invoke(invoke, uri);
        } catch (Throwable e) {
            CheatBreaker.getInstance().getLogger().error("Couldn't open link", e);
        }
    }

    @Override
    public void initGui() throws IOException {
        super.initGui();
        float f = 0.0F;

        for (StatusLabel statusLabel : this.tabs) {
            statusLabel.setBounds(124.0F + f, 6.0F, CheatBreaker.getInstance().getFontManager().robotoBold14.getStringWidth(statusLabel.label) * 1.14F, 20.0F);
            f += CheatBreaker.getInstance().getFontManager().robotoBold14.getStringWidth(statusLabel.label) + 12.0F;
        }

        this.quitButton.setBounds(this.getScaledWidth() - 30.0F, 7.0F, 23.0F, 17.0F);
        this.discordLinkButton
            .setBounds(this.getScaledWidth() - 35.0F - this.accountSwitcher.getTotalHeight(this.usernameWidth) - 23.0F - 5.0F, 7.0F, 23.0F, 17.0F);
        float f2 = -this.iconButtons.size() * 31.0F / 2.0F + 2.5F;

        for (IconButton iconButton : this.iconButtons) {
            iconButton.setBounds(this.getScaledWidth() / 2.0F + f2, this.getScaledHeight() - 17.0F, 26.0F, 18.0F);
            f2 += 30.0F;
        }

        this.versionLabel
            .setBounds(5.0F, this.getScaledHeight() - 14.0F, CheatBreaker.getInstance().getFontManager().playRegular18.getStringWidth(watermark), 18.0F);
        this.copyrightLabel
            .setBounds(
                this.getScaledWidth() - CheatBreaker.getInstance().getFontManager().playRegular18.getStringWidth("Copyright Mojang AB. Do not distribute!") - 5.0F,
                this.getScaledHeight() - 14.0F,
                CheatBreaker.getInstance().getFontManager().playRegular18.getStringWidth("Copyright Mojang AB. Do not distribute!"),
                18.0F
            );
        if (CheatBreaker.getInstance().isAboutScreenEnabled()) {
            this.aboutLabel
                .setBounds(
                    this.getScaledWidth()
                        - CheatBreaker.getInstance().getFontManager().playRegular18.getStringWidth("Copyright Mojang AB. Do not distribute!")
                        - 5.0F,
                    this.getScaledHeight() - 30.0F,
                    CheatBreaker.getInstance().getFontManager().playRegular18.getStringWidth("Copyright Mojang AB. Do not distribute!"),
                    20.0F
                );
        }

        this.updateSwitcherBounds();
        this.logoButton.setBounds(8.0F, 6.0F, 20.0F, 20.0F);
    }

    public void loadAccounts() {
        Minecraft minecraft = Minecraft.getMinecraft();
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
        }

        this.accounts.clear();
        this.usernameWidth = CheatBreaker.getInstance().getFontManager().robotoRegular13.getStringWidth(Minecraft.getMinecraft().getSession().getUsername());

        for (Map map : (Iterable<Map>)list) {
            LauncherAccount launcherAccount = new LauncherAccount(
                (String)map.get("username"), this.text, (String)map.get("accessToken"), (String)map.get("displayName"), (String)map.get("uuid")
            );
            this.accounts.add(launcherAccount);
            float stringWidth = CheatBreaker.getInstance().getFontManager().robotoRegular13.getStringWidth(launcherAccount.getDisplayName());
            if (stringWidth > this.usernameWidth) {
                this.usernameWidth = stringWidth;
            }

            if (minecraft.getSession().getUsername().equals(launcherAccount.getDisplayName())) {
                this.accounts.remove(launcherAccount);
            }

            if (minecraft.getSession() != null && this.text.equalsIgnoreCase(minecraft.getSession().getUsername())) {
                this.accountSwitcher.setLabel(launcherAccount.getDisplayName());
                this.accountSwitcher.setResourceLocation(CheatBreaker.getInstance().getFontManager().getHeadTexture(launcherAccount.getDisplayName()));
                this.updateSwitcherBounds();
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        if (this.accountSwitcher != null) {
            this.accountSwitcher.handleMouseInput();
        }
    }
}
