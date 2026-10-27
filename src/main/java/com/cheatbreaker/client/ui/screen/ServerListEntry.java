package com.cheatbreaker.client.ui.screen;

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
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class ServerListEntry implements IGuiListEntry {
   public ResourceLocation star64Png = new ResourceLocation("client/icons/star-64.png");
   public static ResourceLocation resourcePacksPng = new ResourceLocation("textures/gui/resource_packs.png");
   public ResourceLocation cbPng = new ResourceLocation("client/icons/cb.png");
   public GuiMultiplayer guiMultiplayer;
   public long longValue;
   public static ResourceLocation unknownPackPng = new ResourceLocation("textures/misc/unknown_pack.png");
   public static Logger logger = LogManager.getLogger();
   public ServerData serverData;
   public Minecraft mc;
   public String text;
   public ResourceLocation servers;
   public DynamicTexture dynamicTexture;
   public static ThreadPoolExecutor threadPoolExecutor = new ScheduledThreadPoolExecutor(
      5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).build()
   );

   public boolean alwaysTrue() {
      return true;
   }

   public static ServerData findServerData(ServerListEntry serverListEntry) {
      return serverListEntry.serverData;
   }

   public void run() {
      if (this.serverData.getBase64EncodedIconData() == null) {
         this.mc.getTextureManager().getTexture(this.servers);
         this.dynamicTexture = null;
      } else {
         ByteBuf copiedBuffer = Unpooled.copiedBuffer(this.serverData.getBase64EncodedIconData(), Charsets.UTF_8);
         ByteBuf decode = Base64.decode(copiedBuffer);

         BufferedImage bufferedImage;
         label63: {
            try {
               bufferedImage = ImageIO.read(new ByteBufInputStream(decode));
               Validate.validState(bufferedImage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
               Validate.validState(bufferedImage.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
               break label63;
            } catch (Exception e) {
               logger.error("Invalid icon for server " + this.serverData.serverName + " (" + this.serverData.serverIP + ")", e);
               this.serverData.setBase64EncodedIconData(null);
            } finally {
               copiedBuffer.release();
               decode.release();
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

   public ServerData getServerData() {
      return this.serverData;
   }

   @Override
   public void mouseReleased(int n, int n2, int n3, int n4, int n5, int n6) {
   }

   public ServerListEntry(GuiMultiplayer guiMultiplayer, ServerData serverData) {
      this.guiMultiplayer = guiMultiplayer;
      this.serverData = serverData;
      this.mc = Minecraft.getMinecraft();
      this.servers = new ResourceLocation("servers/" + serverData.serverIP + "/icon");
      this.dynamicTexture = (DynamicTexture)this.mc.getTextureManager().getTexture(this.servers);
   }

   @Override
   public void drawEntry(int n, int n2, int n3, int n4, int n5, int n6, int n7, boolean flag) {
      if (!this.serverData.online) {
         this.serverData.online = true;
         this.serverData.pingToServer = -2L;
         this.serverData.serverMOTD = "";
         this.serverData.populationInfo = "";
         threadPoolExecutor.submit(() -> {
            try {
               this.guiMultiplayer.getOldServerPinger().ping(this.serverData);
            } catch (UnknownHostException e) {
               this.serverData.pingToServer = -1L;
               this.serverData.serverMOTD = EnumChatFormatting.DARK_RED + "Can't resolve hostname";
            } catch (Exception e) {
               this.serverData.pingToServer = -1L;
               this.serverData.serverMOTD = EnumChatFormatting.DARK_RED + "Can't connect to server.";
            }
         });
      }

      GlStateManager.color(1.0F, 0.9F, 0.0F, 1.0F);
      RenderUtils.drawSquareTextureViaRenderEngine(this.star64Png, 5.0F, n2 - 17, n3 + (this.serverData.check() ? 4 : 12));
      if (this.serverData.check()) {
         GlStateManager.color(0.85F, 0.85F, 0.85F, 1.0F);
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
      boolean flag2 = this.serverData.version > 47;
      boolean flag3 = this.serverData.version < 47;
      boolean flag4 = flag2 || flag3;
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
      int n8;
      String str3;
      if (flag4) {
         n8 = 5;
         str3 = flag2 ? "Client out of date!" : "Server out of date!";
         str2 = this.serverData.playerList;
      } else if (this.serverData.online && this.serverData.pingToServer != -2L) {
         if (this.serverData.pingToServer < 0L) {
            n8 = 5;
         } else if (this.serverData.pingToServer < 150L) {
            n8 = 0;
         } else if (this.serverData.pingToServer < 300L) {
            n8 = 1;
         } else if (this.serverData.pingToServer < 600L) {
            n8 = 2;
         } else if (this.serverData.pingToServer < 1000L) {
            n8 = 3;
         } else {
            n8 = 4;
         }

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
      if (this.serverData.getBase64EncodedIconData() != null && !this.serverData.getBase64EncodedIconData().equals(this.text)) {
         this.text = this.serverData.getBase64EncodedIconData();
         this.run();
         this.guiMultiplayer.getServerList().saveServerList();
      }

      if (this.dynamicTexture != null) {
         this.process(n2, n3, this.servers);
      } else {
         this.process(n2, n3, unknownPackPng);
      }

      int n9 = n6 - n2;
      int n10 = n7 - n3;
      if (n9 >= n4 - 15 && n9 <= n4 - 5 && n10 >= 0 && n10 <= 8) {
         this.guiMultiplayer.setHoveringText(str3);
      } else if (n9 >= n4 - stringWidth - 15 - 2 && n9 <= n4 - 15 - 2 && n10 >= 0 && n10 <= 8) {
         this.guiMultiplayer.setHoveringText(str2);
      }

      if (this.mc.gameSettings.touchscreen || flag) {
         this.mc.getTextureManager().bindTexture(resourcePacksPng);
         Gui.drawRect(n2, n3, n2 + 32, n3 + 32, -1601138544);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         int n11 = n6 - n2;
         if (this.alwaysTrue()) {
            if (n11 < 32 && n11 > 16) {
               Gui.drawModalRectWithCustomSizedTexture(n2, n3, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.drawModalRectWithCustomSizedTexture(n2, n3, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         }
      }
   }

   @Override
   public void setSelected(int n, int n2, int n3) {
   }

   public static GuiMultiplayer findGuiMultiplayer(ServerListEntry serverListEntry) {
      return serverListEntry.guiMultiplayer;
   }

   @Override
   public boolean mousePressed(int n, int n2, int n3, int n4, int n5, int n6) {
      this.guiMultiplayer.selectServer(n);
      if (Minecraft.getSystemTime() - this.longValue < 250L) {
         this.guiMultiplayer.connectToSelected();
      }

      this.longValue = Minecraft.getSystemTime();
      if (n5 <= 32 && n5 < 32) {
         this.guiMultiplayer.connectToSelected();
         return true;
      } else {
         return false;
      }
   }

   public void process(int n, int n2, ResourceLocation resourceLocation) {
      this.mc.getTextureManager().bindTexture(resourceLocation);
      GlStateManager.enableBlend();
      Gui.drawModalRectWithCustomSizedTexture(n, n2, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
      GlStateManager.disableBlend();
   }
}
