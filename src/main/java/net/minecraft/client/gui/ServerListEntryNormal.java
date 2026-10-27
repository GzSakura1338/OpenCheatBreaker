package net.minecraft.client.gui;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.manager.BlockedServer;
import com.cheatbreaker.client.manager.BlockedServerLevel;
import com.cheatbreaker.client.render.RenderUtils;
import com.google.common.base.Charsets;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class ServerListEntryNormal implements GuiListExtended.IGuiListEntry {
   public static Logger logger = LogManager.getLogger();
   public static ThreadPoolExecutor SERVER_PINGER_THREAD_POOL = new ScheduledThreadPoolExecutor(
      5, new ThreadFactoryBuilder().setNameFormat("Server Pinger #%d").setDaemon(true).build()
   );
   public static ResourceLocation UNKNOWN_SERVER = new ResourceLocation("textures/misc/unknown_server.png");
   public static ResourceLocation SERVER_SELECTION_BUTTONS = new ResourceLocation("textures/gui/server_selection.png");
   public ResourceLocation error64Png = new ResourceLocation("client/icons/error-64.png");
   public ResourceLocation delete64Png = new ResourceLocation("client/icons/delete-64.png");
   public ResourceLocation cbPng = new ResourceLocation("client/icons/cb.png");
   public GuiMultiplayer owner;
   public String iconUri;
   public ServerData server;
   public long time;
   public boolean flag;
   public Minecraft mc;
   public ResourceLocation serverIcon;
   public DynamicTexture icon;
   public boolean flag2;

   public ServerListEntryNormal(GuiMultiplayer guiMultiplayer, ServerData serverIn) {
      this.owner = guiMultiplayer;
      this.server = serverIn;
      this.mc = Minecraft.getMinecraft();
      this.serverIcon = new ResourceLocation("servers/" + serverIn.serverIP + "/icon");
      this.icon = (DynamicTexture)this.mc.getTextureManager().getTexture(this.serverIcon);
   }

   @Override
   public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
      if (!this.server.online) {
         this.server.online = true;
         this.server.pingToServer = -2L;
         this.server.serverMOTD = "";
         this.server.populationInfo = "";
         SERVER_PINGER_THREAD_POOL.submit(() -> {
            try {
               this.owner.getOldServerPinger().ping(this.server);
            } catch (UnknownHostException e) {
               this.server.pingToServer = -1L;
               this.server.serverMOTD = EnumChatFormatting.DARK_RED + "Can't resolve hostname";
            } catch (Exception e) {
               this.server.pingToServer = -1L;
               this.server.serverMOTD = EnumChatFormatting.DARK_RED + "Can't connect to server.";
            }
         });
      }

      GL11.glColor4f(0.85F, 0.85F, 0.85F, 1.0F);
      if (this.server.check()) {
         float f = 16.0F;
         float f2 = 8.0F;
         float f3 = 0.0F;
         float f4 = 0.0F;
         float f5 = x - 20;
         float f6 = y + 14;
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

      String toLowerCase = this.server.serverIP.toLowerCase();

      for (BlockedServer blockedServer : CheatBreaker.getInstance().getBlockedServersManager().getStringToBlockedServerMap().values()) {
         for (String str : blockedServer.getStringList()) {
            String toLowerCase2 = str.toLowerCase();
            if (toLowerCase.endsWith(toLowerCase2)) {
               this.flag = blockedServer.isFlag() || blockedServer.getBlockedServerLevel() == BlockedServerLevel.BLOCK;
               GL11.glColor4f(0.9F, this.flag ? 0.0F : 0.8F, 0.0F, 1.0F);
               RenderUtils.drawSquareTextureViaRenderEngine(this.flag ? this.delete64Png : this.error64Png, 5.0F, x - 17, y + (this.server.check() ? 4 : 12));
               break;
            }
         }
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      boolean flag = this.server.version > 47;
      boolean flag2 = this.server.version < 47;
      boolean flag3 = flag || flag2;
      this.mc
         .fontRendererObj
         .drawString(this.server.serverName, x + 32 + 3, y + 1, this.flag2 ? new Color(0.9F, this.flag ? 0.0F : 0.8F, 0.0F, 1.0F).getRGB() : 16777215);
      List listFormattedStringToWidth = this.mc.fontRendererObj.listFormattedStringToWidth(this.server.serverMOTD, listWidth - 32 - 2);

      for (int i = 0; i < Math.min(listFormattedStringToWidth.size(), 2); i++) {
         this.mc.fontRendererObj.drawString((String)listFormattedStringToWidth.get(i), x + 32 + 3, y + 12 + this.mc.fontRendererObj.FONT_HEIGHT * i, 8421504);
      }

      String str = flag3 ? EnumChatFormatting.DARK_RED + this.server.gameVersion : this.server.populationInfo;
      int stringWidth = this.mc.fontRendererObj.getStringWidth(str);
      this.mc.fontRendererObj.drawString(str, x + listWidth - stringWidth - 15 - 2, y + 1, 8421504);
      byte b = 0;
      String str2 = null;
      int n;
      String str3;
      if (flag3) {
         n = 5;
         str3 = flag ? "Client out of date!" : "Server out of date!";
         str2 = this.server.playerList;
      } else if (this.server.online && this.server.pingToServer != -2L) {
         if (this.server.pingToServer < 0L) {
            n = 5;
         } else if (this.server.pingToServer < 150L) {
            n = 0;
         } else if (this.server.pingToServer < 300L) {
            n = 1;
         } else if (this.server.pingToServer < 600L) {
            n = 2;
         } else if (this.server.pingToServer < 1000L) {
            n = 3;
         } else {
            n = 4;
         }

         if (this.server.pingToServer < 0L) {
            str3 = "(no connection)";
         } else {
            str3 = this.server.pingToServer + "ms";
            str2 = this.server.playerList;
         }
      } else {
         b = 1;
         n = (int)(Minecraft.getSystemTime() / 100L + slotIndex * 2 & 7L);
         if (n > 4) {
            n = 8 - n;
         }

         str3 = "Pinging...";
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(Gui.icons);
      Gui.drawModalRectWithCustomSizedTexture(x + listWidth - 15, y, b * 10, 176 + n * 8, 10, 8, 256.0F, 256.0F);
      if (this.server.getBase64EncodedIconData() != null && !this.server.getBase64EncodedIconData().equals(this.iconUri)) {
         this.iconUri = this.server.getBase64EncodedIconData();
         this.prepareServerIcon();
         this.owner.getServerList().saveServerList();
      }

      if (this.icon != null) {
         this.drawTextureAt(x, y, this.serverIcon);
      } else {
         this.drawTextureAt(x, y, UNKNOWN_SERVER);
      }

      int n2 = mouseX - x;
      int n3 = mouseY - y;
      if (n2 >= listWidth - 15 && n2 <= listWidth - 5 && n3 >= 0 && n3 <= 8) {
         this.owner.setHoveringText(str3);
      } else if (n2 >= listWidth - stringWidth - 15 - 2 && n2 <= listWidth - 15 - 2 && n3 >= 0 && n3 <= 8) {
         this.owner.setHoveringText(str2);
      }

      if (this.mc.gameSettings.touchscreen || isSelected) {
         this.mc.getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);
         Gui.drawRect(x, y, x + 32, y + 32, -1601138544);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         int n4 = mouseX - x;
         int n5 = mouseY - y;
         if (this.isVisible()) {
            if (n4 < 32 && n4 > 16) {
               Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         }

         if (this.owner.canSortUp(this, slotIndex)) {
            if (n4 < 16 && n5 < 16) {
               Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.drawModalRectWithCustomSizedTexture(x, y, 96.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         }

         if (this.owner.canSortDown(this, slotIndex)) {
            if (n4 < 16 && n5 > 16) {
               Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 32.0F, 32, 32, 256.0F, 256.0F);
            } else {
               Gui.drawModalRectWithCustomSizedTexture(x, y, 64.0F, 0.0F, 32, 32, 256.0F, 256.0F);
            }
         }
      }
   }

   public void drawTextureAt(int n, int n2, ResourceLocation resourceLocation) {
      this.mc.getTextureManager().bindTexture(resourceLocation);
      GlStateManager.enableBlend();
      Gui.drawModalRectWithCustomSizedTexture(n, n2, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
      GlStateManager.disableBlend();
   }

   public boolean isVisible() {
      return true;
   }

   public void prepareServerIcon() {
      if (this.server.getBase64EncodedIconData() == null) {
         this.mc.getTextureManager().deleteTexture(this.serverIcon);
         this.icon = null;
      } else {
         ByteBuf copiedBuffer = Unpooled.copiedBuffer(this.server.getBase64EncodedIconData(), Charsets.UTF_8);
         ByteBuf decode = Base64.decode(copiedBuffer);

         BufferedImage bufferedImage;
         label63: {
            try {
               bufferedImage = TextureUtil.readBufferedImage(new ByteBufInputStream(decode));
               Validate.validState(bufferedImage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
               Validate.validState(bufferedImage.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
               break label63;
            } catch (Throwable e) {
               logger.error("Invalid icon for server " + this.server.serverName + " (" + this.server.serverIP + ")", e);
               this.server.setBase64EncodedIconData((String)null);
            } finally {
               copiedBuffer.release();
               decode.release();
            }

            return;
         }

         if (this.icon == null) {
            this.icon = new DynamicTexture(bufferedImage.getWidth(), bufferedImage.getHeight());
            this.mc.getTextureManager().loadTexture(this.serverIcon, this.icon);
         }

         bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), this.icon.getTextureData(), 0, bufferedImage.getWidth());
         this.icon.updateDynamicTexture();
      }
   }

   @Override
   public boolean mousePressed(int slotIndex, int n, int n2, int n3, int n4, int n5) {
      if (n4 <= 32) {
         if (n4 < 32 && n4 > 16 && this.isVisible()) {
            this.owner.selectServer(slotIndex);
            this.owner.connectToSelected();
            return true;
         }

         if (n4 < 16 && n5 < 16 && this.owner.canSortUp(this, slotIndex)) {
            this.owner.sortUp(this, slotIndex, GuiScreen.isShiftKeyDown());
            return true;
         }

         if (n4 < 16 && n5 > 16 && this.owner.canSortDown(this, slotIndex)) {
            this.owner.sortDown(this, slotIndex, GuiScreen.isShiftKeyDown());
            return true;
         }
      }

      this.owner.selectServer(slotIndex);
      if (Minecraft.getSystemTime() - this.time < 250L) {
         this.owner.connectToSelected();
      }

      this.time = Minecraft.getSystemTime();
      return false;
   }

   @Override
   public void setSelected(int n, int n2, int n3) {
   }

   @Override
   public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
   }

   public ServerData getServerData() {
      return this.server;
   }
}
