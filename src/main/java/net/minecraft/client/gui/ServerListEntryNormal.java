package net.minecraft.client.gui;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.manager.BlockedServer;
import com.cheatbreaker.client.manager.BlockedServerLevel;
import com.cheatbreaker.client.render.GlStateCache;
import com.cheatbreaker.client.render.RenderUtils;
import com.google.common.base.Charsets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import java.awt.image.BufferedImage;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class ServerListEntryNormal implements IGuiListEntry {
    public static ResourceLocation resourcePacksPng = new ResourceLocation("textures/gui/resource_packs.png");
    public static ResourceLocation unknownPackPng = new ResourceLocation("textures/misc/unknown_pack.png");
    public ResourceLocation error64Png = new ResourceLocation("client/icons/error-64.png");
    public ResourceLocation delete64Png = new ResourceLocation("client/icons/delete-64.png");
    public ResourceLocation cbPng = new ResourceLocation("client/icons/cb.png");
    public long time;
    public String iconUri;
    public boolean flag = false;
    public GuiMultiplayer parent;
    public ServerData serverInfo;
    public static Logger logger = LogManager.getLogger();
    public Minecraft client;
    public ResourceLocation iconTextureId;
    public DynamicTexture icon;
    public static ThreadPoolExecutor SERVER_PINGER_THREAD_POOL = new ScheduledThreadPoolExecutor(
        5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).build()
    );

    public ServerListEntryNormal(GuiMultiplayer guiMultiplayer, ServerData serverData) {
        this.parent = guiMultiplayer;
        this.serverInfo = serverData;
        this.client = Minecraft.getMinecraft();
        this.iconTextureId = new ResourceLocation("servers/" + serverData.serverIP + "/icon");
        ITextureObject texture = this.client.getTextureManager().getTexture(this.iconTextureId);
        this.icon = texture instanceof DynamicTexture && texture != TextureUtil.missingTexture ? (DynamicTexture)texture : null;
    }

    @Override
    public void drawEntry(int n, int n2, int n3, int n4, int n5, Tessellator tessellator, int n6, int n7, boolean flag) {
        if (!this.serverInfo.online) {
            this.serverInfo.online = true;
            this.serverInfo.pingToServer = -2L;
            this.serverInfo.serverMOTD = "";
            this.serverInfo.populationInfo = "";
            SERVER_PINGER_THREAD_POOL.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        ServerListEntryNormal.this.parent.getOldServerPinger().ping(ServerListEntryNormal.this.serverInfo);
                    } catch (UnknownHostException e) {
                        ServerListEntryNormal.this.serverInfo.pingToServer = -1L;
                        ServerListEntryNormal.this.serverInfo.serverMOTD = EnumChatFormatting.DARK_RED + "Can't resolve hostname";
                    } catch (Exception e) {
                        ServerListEntryNormal.this.serverInfo.pingToServer = -1L;
                        ServerListEntryNormal.this.serverInfo.serverMOTD = EnumChatFormatting.DARK_RED + "Can't connect to server.";
                    }
                }
            });
        }

        GL11.glColor4f(0.85F, 0.85F, 0.85F, 1.0F);
        if (this.serverInfo.check()) {
            float f = 16.0F;
            float f2 = 8.0F;
            float f3 = 0.0F;
            float f4 = 0.0F;
            float f5 = n2 - 20;
            float f6 = n3 + 14;
            GL11.glEnable(3042);
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
            GL11.glDisable(3042);
        }

        String toLowerCase = this.serverInfo.serverIP.toLowerCase();

        for (BlockedServer blockedServer : CheatBreaker.getInstance().getBlockedServersManager().getStringToBlockedServerMap().values()) {
            for (String str : blockedServer.getDomains()) {
                String toLowerCase2 = str.toLowerCase();
                if (toLowerCase.endsWith(toLowerCase2)) {
                    this.flag = blockedServer.isFlag() || blockedServer.getBlockedServerLevel() == BlockedServerLevel.BLOCK;
                    GL11.glColor4f(0.9F, this.flag ? 0.0F : 0.8F, 0.0F, 1.0F);
                    RenderUtils.drawSquareTextureViaRenderEngine(
                        this.flag ? this.delete64Png : this.error64Png, 5.0F, n2 - 17, n3 + (this.serverInfo.check() ? 4 : 12)
                    );
                    break;
                }
            }
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        boolean flag2 = this.serverInfo.version > 5;
        boolean flag3 = this.serverInfo.version < 5;
        boolean flag4 = (flag2 || flag3) && this.serverInfo.version != -1332;
        this.client.fontRendererObj.drawString(this.serverInfo.serverName, n2 + 32 + 3, n3 + 1, 16777215);
        List listFormattedStringToWidth = this.client.fontRendererObj.listFormattedStringToWidth(this.serverInfo.serverMOTD, n4 - 32 - 2);

        for (int i = 0; i < Math.min(listFormattedStringToWidth.size(), 2); i++) {
            this.client.fontRendererObj.drawString((String)listFormattedStringToWidth.get(i), n2 + 32 + 3, n3 + 12 + this.client.fontRendererObj.FONT_HEIGHT * i, 8421504);
        }

        String str = flag4 ? EnumChatFormatting.DARK_RED + this.serverInfo.gameVersion : this.serverInfo.populationInfo;
        if (this.flag) {
            str = EnumChatFormatting.DARK_RED + "Blocked!";
        }

        int stringWidth = this.client.fontRendererObj.getStringWidth(str);
        this.client.fontRendererObj.drawString(str, n2 + n4 - stringWidth - 15 - 2, n3 + 1, 8421504);
        byte b = 0;
        String str2 = null;
        int n8;
        String str3;
        if (this.flag) {
            n8 = 5;
            str3 = "Blocked!";
            str2 = "Blocked!";
        } else if (flag4) {
            n8 = 5;
            str3 = flag2 ? "Client out of date!" : "Server out of date!";
            str2 = this.serverInfo.playerList;
        } else if (this.serverInfo.online && this.serverInfo.pingToServer != -2L) {
            if (this.serverInfo.pingToServer < 0L) {
                n8 = 5;
            } else if (this.serverInfo.pingToServer < 150L) {
                n8 = 0;
            } else if (this.serverInfo.pingToServer < 300L) {
                n8 = 1;
            } else if (this.serverInfo.pingToServer < 600L) {
                n8 = 2;
            } else if (this.serverInfo.pingToServer < 1000L) {
                n8 = 3;
            } else {
                n8 = 4;
            }

            if (this.serverInfo.pingToServer < 0L) {
                str3 = "(no connection)";
            } else {
                str3 = this.serverInfo.pingToServer + "ms";
                str2 = this.serverInfo.playerList;
            }
        } else {
            b = 1;
            n8 = (int)(Minecraft.getSystemTime() / 100L + n * 2 & 7L);
            if (n8 > 4) {
                n8 = 8 - n8;
            }

            str3 = "Pinging...";
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.client.getTextureManager().bindTexture(Gui.icons);
        Gui.drawModalRectWithCustomSizedTexture(n2 + n4 - 15, n3, b * 10, 176 + n8 * 8, 10, 8, 256.0F, 256.0F);
        if (this.serverInfo.getBase64EncodedIconData() != null && !this.serverInfo.getBase64EncodedIconData().equals(this.iconUri)) {
            this.iconUri = this.serverInfo.getBase64EncodedIconData();
            this.prepareServerIcon();
            this.parent.getServerList().saveServerList();
        }

        if (this.icon != null) {
            this.client.getTextureManager().bindTexture(this.iconTextureId);
        } else {
            this.client.getTextureManager().bindTexture(unknownPackPng);
        }

        GlStateCache.enableBlend();
        Gui.drawModalRectWithCustomSizedTexture(n2, n3, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
        GlStateCache.disableBlend();
        int n9 = n6 - n2;
        int n10 = n7 - n3;
        if (n9 >= n4 - 15 && n9 <= n4 - 5 && n10 >= 0 && n10 <= 8) {
            this.parent.setTooltip(str3);
        } else if (n9 >= n4 - stringWidth - 15 - 2 && n9 <= n4 - 15 - 2 && n10 >= 0 && n10 <= 8) {
            this.parent.setTooltip(str2);
        }

        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.gameSettings.touchscreen || flag) {
            minecraft.getTextureManager().bindTexture(resourcePacksPng);
            Gui.drawRect(n2, n3, n2 + 32, n3 + 32, -1601138544);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            int n11 = n6 - n2;
            int n12 = n7 - n3;
            boolean checkServerListEntryNormal2 = this.parent.checkServerListEntryNormal2(this);
            boolean checkServerListEntryNormal = this.parent.checkServerListEntryNormal(this);
            boolean flag5 = checkServerListEntryNormal2 || checkServerListEntryNormal;
            if (n11 < (flag5 ? 16 : 32)) {
                Gui.drawModalRectWithCustomSizedTexture(n2 - (flag5 ? 6 : 0), n3, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
                Gui.drawModalRectWithCustomSizedTexture(n2 - (flag5 ? 6 : 0), n3, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }

            if (checkServerListEntryNormal2) {
                if (n11 < 32 && n11 > 16 && n12 < 16) {
                    Gui.drawModalRectWithCustomSizedTexture(n2, n3, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                } else {
                    Gui.drawModalRectWithCustomSizedTexture(n2, n3, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            }

            if (checkServerListEntryNormal) {
                if (n11 < 32 && n11 > 16 && n12 > 16) {
                    Gui.drawModalRectWithCustomSizedTexture(n2, n3, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
                } else {
                    Gui.drawModalRectWithCustomSizedTexture(n2, n3, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
                }
            }
        }
    }

    public void prepareServerIcon() {
        if (this.serverInfo.getBase64EncodedIconData() == null) {
            this.client.getTextureManager().deleteTexture(this.iconTextureId);
            this.icon = null;
        } else {
            ByteBuf copiedBuffer = Unpooled.copiedBuffer(this.serverInfo.getBase64EncodedIconData(), Charsets.UTF_8);

            ByteBuf byteBuf;
            try {
                byteBuf = Base64.decode(copiedBuffer);
            } catch (IllegalArgumentException e) {
                logger.error("Invalid icon for server " + this.serverInfo.serverName + " (" + this.serverInfo.serverIP + ")", e);
                this.serverInfo.setBase64EncodedIconData(null);
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
                    logger.error("Invalid icon for server " + this.serverInfo.serverName + " (" + this.serverInfo.serverIP + ")", e);
                    this.serverInfo.setBase64EncodedIconData(null);
                } finally {
                    copiedBuffer.release();
                    byteBuf.release();
                }

                return;
            }

            if (this.icon == null) {
                this.icon = new DynamicTexture(bufferedImage.getWidth(), bufferedImage.getHeight());
                this.client.getTextureManager().loadTexture(this.iconTextureId, this.icon);
            }

            bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), this.icon.getTextureData(), 0, bufferedImage.getWidth());
            this.icon.updateDynamicTexture();
        }
    }

    @Override
    public boolean mousePressed(int n, int n2, int n3, int n4, int n5, int n6) throws java.io.IOException {
        this.parent.selectServer(n);
        if (Minecraft.getSystemTime() - this.time < 250L) {
            this.parent.connectToSelected();
        }

        this.time = Minecraft.getSystemTime();
        boolean checkServerListEntryNormal2 = this.parent.checkServerListEntryNormal2(this);
        boolean checkServerListEntryNormal = this.parent.checkServerListEntryNormal(this);
        boolean flag = checkServerListEntryNormal2 || checkServerListEntryNormal;
        if (n5 <= 32) {
            if (n5 < (flag ? 16 : 32)) {
                this.parent.handleServerListEntryNormal3(this);
                return true;
            }

            if (n5 > 16 && n6 < 16 && checkServerListEntryNormal2) {
                this.parent.handleServerListEntryNormal(this);
                return true;
            }

            if (n5 > 16 && n6 > 16 && checkServerListEntryNormal) {
                this.parent.handleServerListEntryNormal2(this);
                return true;
            }
        }

        return false;
    }

    @Override
    public void mouseReleased(int n, int n2, int n3, int n4, int n5, int n6) {
    }

    public ServerData getServerData() {
        return this.serverInfo;
    }
}
