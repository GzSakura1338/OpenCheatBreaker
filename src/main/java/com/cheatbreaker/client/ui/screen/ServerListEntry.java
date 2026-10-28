package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.render.RenderUtils;
import com.google.common.base.Charsets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class ServerListEntry implements IGuiListEntry {
    public long lastClickMillis;
    public ResourceLocation star64Png = new ResourceLocation("client/icons/star-64.png");
    public ResourceLocation cbPng = new ResourceLocation("client/icons/cb.png");
    public static ResourceLocation resourcePacksPng = new ResourceLocation("textures/gui/resource_packs.png");
    public GuiMultiplayer guiMultiplayer;
    public static ResourceLocation unknownPackPng = new ResourceLocation("textures/misc/unknown_pack.png");
    public static Logger logger = LogManager.getLogger();
    public ServerData serverData;
    public Minecraft mc;
    public ResourceLocation servers;
    public DynamicTexture dynamicTexture;
    public static ThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(
        5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).build()
    );
    public String iconData;

    @Override
    public boolean mousePressed(int n, int n2, int n3, int n4, int n5, int n6) throws java.io.IOException {
        this.guiMultiplayer.selectServer(n);
        if (Minecraft.getSystemTime() - this.lastClickMillis < 250L) {
            this.guiMultiplayer.connectToSelected();
        }

        this.lastClickMillis = Minecraft.getSystemTime();
        if (n5 <= 32 && n5 < 32) {
            this.guiMultiplayer.connectToSelected();
            return true;
        } else {
            return false;
        }
    }

    public static ServerData findServerData(ServerListEntry serverListEntry) {
        return serverListEntry.serverData;
    }

    public ServerListEntry(GuiMultiplayer guiMultiplayer, ServerData serverData) {
        this.guiMultiplayer = guiMultiplayer;
        this.serverData = serverData;
        this.mc = Minecraft.getMinecraft();
        this.servers = new ResourceLocation("servers/" + serverData.serverIP + "/icon");
        ITextureObject texture = this.mc.getTextureManager().getTexture(this.servers);
        this.dynamicTexture = texture instanceof DynamicTexture && texture != TextureUtil.missingTexture ? (DynamicTexture)texture : null;
    }

    @Override
    public void mouseReleased(int n, int n2, int n3, int n4, int n5, int n6) {
    }

    public ServerData getServerData() {
        return this.serverData;
    }

    @Override
    public void drawEntry(int n, int n2, int n3, int n4, int n5, Tessellator tessellator, int n6, int n7, boolean flag) {
        if (!this.serverData.online) {
            this.serverData.online = true;
            this.serverData.pingToServer = -2L;
            this.serverData.serverMOTD = "";
            this.serverData.populationInfo = "";
            threadPoolExecutor.submit(() -> {
                try {
                    findGuiMultiplayer(this).getOldServerPinger().ping(findServerData(this));
                } catch (Exception e) {
                    findServerData(this).pingToServer = -1L;
                    findServerData(this).serverMOTD = EnumChatFormatting.DARK_RED + "Can't connect to server.";
                }
            });
        }

        GlStateManager.color(1.0F, 0.9F, 0.0F, 1.0F);
        RenderUtils.drawSquareTextureViaRenderEngine(this.star64Png, 5.0F, n2 - 17, n3 + (this.serverData.check() ? 4 : 12));
        if (this.serverData.check()) {
            GL11.glColor4f(0.85F, 0.85F, 0.85F, 1.0F);
            float f = 16.0F;
            float f2 = 8.0F;
            float f3 = 0.0F;
            float f4 = 0.0F;
            float f5 = n2 - 20;
            float f6 = n3 + 20;
            GlStateManager.enableBlend();
            Minecraft.getMinecraft().renderEngine.bindTexture(this.cbPng);
            GL11.glBegin(7);
            GL11.glTexCoord2d(f3 / 5.0F, f4 / 5.0F);
            GL11.glVertex2d(f5, f6);
            GL11.glTexCoord2d(f3 / 5.0F, (f4 + 5.0F) / 5.0F);
            GL11.glVertex2d(f5, f6 + f2);
            GL11.glTexCoord2d((f3 + 5.0F) / 5.0F, (f4 + 5.0F) / 5.0F);
            GL11.glVertex2d(f5 + f, f6 + f2);
            GL11.glTexCoord2d((f3 + 5.0F) / 5.0F, f4 / 5.0F);
            GL11.glVertex2d(f5 + f, f6);
            GL11.glEnd();
            GlStateManager.disableBlend();
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        boolean flag2 = this.serverData.version > 5;
        boolean flag3 = this.serverData.version < 5;
        boolean flag4 = (flag2 || flag3) && this.serverData.version != -1332;
        this.mc.fontRendererObj.drawString(this.serverData.serverName, n2 + 32 + 3, n3 + 1, 16777215);
        List listFormattedStringToWidth = this.mc.fontRendererObj.listFormattedStringToWidth(this.serverData.serverMOTD, n4 - 32 - 2);

        for (int i = 0; i < Math.min(listFormattedStringToWidth.size(), 2); i++) {
            this.mc.fontRendererObj.drawString((String)listFormattedStringToWidth.get(i), n2 + 32 + 3, n3 + 12 + this.mc.fontRendererObj.FONT_HEIGHT * i, 8421504);
        }

        String str = flag4 ? EnumChatFormatting.DARK_RED + this.serverData.gameVersion : this.serverData.populationInfo;
        int stringWidth = this.mc.fontRendererObj.getStringWidth(str);
        this.mc.fontRendererObj.drawString(str, n2 + n4 - stringWidth - 15 - 2, n3 + 1, 8421504);
        byte b = 0;
        String str2 = null;
        String str3;
        int n8;
        if (flag4) {
            n8 = 5;
            str3 = flag2 ? "Client out of date!" : "Server out of date!";
            str2 = this.serverData.playerList;
        } else if (this.serverData.online && this.serverData.pingToServer != -2L) {
            n8 = this.serverData.pingToServer < 0L
                ? 5
                : (
                    this.serverData.pingToServer < 150L
                        ? 0
                        : (this.serverData.pingToServer < 300L ? 1 : (this.serverData.pingToServer < 600L ? 2 : (this.serverData.pingToServer < 1000L ? 3 : 4)))
                );
            if (this.serverData.pingToServer < 0L) {
                str3 = "(no connection)";
            } else {
                str3 = this.serverData.pingToServer + "ms";
                str2 = this.serverData.playerList;
            }
        } else {
            b = 1;
            n8 = (int)(Minecraft.getSystemTime() / 100L + n * 2L & 7L);
            if (n8 > 4) {
                n8 = 8 - n8;
            }

            str3 = "Pinging...";
        }

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(Gui.icons);
        Gui.drawModalRectWithCustomSizedTexture(n2 + n4 - 15, n3, b * 10, 176 + n8 * 8, 10, 8, 256.0F, 256.0F);
        if (this.serverData.getBase64EncodedIconData() != null && !this.serverData.getBase64EncodedIconData().equals(this.iconData)) {
            this.iconData = this.serverData.getBase64EncodedIconData();
            this.updateIcon();
            this.guiMultiplayer.getServerList().saveServerList();
        }

        if (this.dynamicTexture != null) {
            this.mc.getTextureManager().bindTexture(this.servers);
        } else {
            this.mc.getTextureManager().bindTexture(unknownPackPng);
        }

        GlStateManager.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(n2, n3, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
        GlStateManager.disableBlend();
        int n9 = n6 - n2;
        int n10 = n7 - n3;
        if (n9 >= n4 - 15 && n9 <= n4 - 5 && n10 >= 0 && n10 <= 8) {
            this.guiMultiplayer.setTooltip(str3);
        } else if (n9 >= n4 - stringWidth - 15 - 2 && n9 <= n4 - 15 - 2 && n10 >= 0 && n10 <= 8) {
            this.guiMultiplayer.setTooltip(str2);
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.gameSettings.touchscreen || flag) {
            minecraft.getTextureManager().bindTexture(resourcePacksPng);
            Gui.drawRect(n2, n3, n2 + 32, n3 + 32, -1601138544);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int n11 = n6 - n2;
            if (n11 < 32) {
                Gui.drawModalRectWithCustomSizedTexture(n2, n3, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
                Gui.drawModalRectWithCustomSizedTexture(n2, n3, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
        }
    }

    public static GuiMultiplayer findGuiMultiplayer(ServerListEntry serverListEntry) {
        return serverListEntry.guiMultiplayer;
    }

    public void updateIcon() {
        if (this.serverData.getBase64EncodedIconData() == null) {
            this.mc.getTextureManager().deleteTexture(this.servers);
            this.dynamicTexture = null;
        } else {
            ByteBuf copiedBuffer = Unpooled.copiedBuffer(this.serverData.getBase64EncodedIconData(), Charsets.UTF_8);

            ByteBuf byteBuf;
            try {
                byteBuf = Base64.decode(copiedBuffer);
            } catch (IllegalArgumentException e) {
                logger.error("Invalid icon for server " + this.serverData.serverName + " (" + this.serverData.serverIP + ")", e);
                this.serverData.setBase64EncodedIconData(null);
                copiedBuffer.release();
                return;
            }

            BufferedImage bufferedImage;
            label71: {
                try {
                    bufferedImage = ImageIO.read(new ByteBufInputStream(byteBuf));
                    Validate.validState(bufferedImage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                    Validate.validState(bufferedImage.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                    break label71;
                } catch (Exception e) {
                    logger.error("Invalid icon for server " + this.serverData.serverName + " (" + this.serverData.serverIP + ")", e);
                    this.serverData.setBase64EncodedIconData(null);
                } finally {
                    copiedBuffer.release();
                    byteBuf.release();
                }

                return;
            }

            if (this.dynamicTexture == null) {
                this.dynamicTexture = new DynamicTexture(bufferedImage.getWidth(), bufferedImage.getHeight());
                this.mc.getTextureManager().loadTexture(this.servers, this.dynamicTexture);
            }

            bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), this.dynamicTexture.getTextureData(), 0, bufferedImage.getWidth());
            this.dynamicTexture.updateDynamicTexture();
        }
    }
}
