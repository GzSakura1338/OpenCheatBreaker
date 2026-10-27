package net.minecraft.client.gui;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.chat.ClientChatLine;
import com.cheatbreaker.client.event.SimpleEvent4;
import com.cheatbreaker.client.event.SimpleEvent7;
import com.cheatbreaker.client.event.StringEvent3;
import com.cheatbreaker.client.mod.impl.ChatMod;
import com.cheatbreaker.client.render.RenderUtils;
import com.google.common.collect.Lists;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer.EnumChatVisibility;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class GuiNewChat extends Gui {
   public static Logger logger = LogManager.getLogger();
   public ResourceLocation logoWhitePng = new ResourceLocation("client/logo_white.png");
   public List<String> sentMessages = Lists.newArrayList();
   public List<ChatLine> chatLines = Lists.newArrayList();
   public List<ChatLine> drawnChatLines = Lists.newArrayList();
   public int scrollPos;
   public boolean isScrolled;
   public List<ChatLine> iconLines = new ArrayList<>();
   public List<ChatLine> highlightedLines = new ArrayList<>();
   public boolean smoothChat = false;
   public float smoothAlpha;
   public static final int ICON_WIDTH = 17;
   public float smoothProgress;
   public long lastFrameTime = System.currentTimeMillis();
   public Minecraft mc;
   public int currentLineIndex;
   public int newestMessageEndIndex;

   public GuiNewChat(Minecraft mcIn) {
      this.mc = mcIn;
   }

   public void drawChat(int updateCounter) {
      if (this.mc.gameSettings.chatVisibility != EnumChatVisibility.HIDDEN) {
         int lineCount = this.getLineCount();
         boolean flag = false;
         int n = 0;
         int size = this.drawnChatLines.size();
         float f = this.mc.gameSettings.chatOpacity * 0.9F + 0.1F;
         if (size > 0) {
            if (this.getChatOpen()) {
               flag = true;
            }

            float chatScale = this.getChatScale();
            int n2 = MathHelper.ceiling_float_int(this.getChatWidth() / chatScale);
            GlStateManager.pushMatrix();
            GlStateManager.translate(2.0F, 20.0F, 0.0F);
            GlStateManager.scale(chatScale, chatScale, 1.0F);

            for (int i = 0; i + this.scrollPos < this.drawnChatLines.size() && i < lineCount; i++) {
               ChatLine chatLine = this.drawnChatLines.get(i + this.scrollPos);
               if (chatLine != null) {
                  int n3 = updateCounter - chatLine.getUpdatedCounter();
                  if (n3 < 200 || flag) {
                     double d = n3 / 200.0;
                     d = 1.0 - d;
                     d *= 10.0;
                     d = MathHelper.clamp_double(d, 0.0, 1.0);
                     d *= d;
                     int n4 = (int)(255.0 * d);
                     if (flag) {
                        n4 = 255;
                     }

                     n4 = (int)(n4 * f);
                     n++;
                     if (n4 > 3) {
                        byte b = 0;
                        int n5 = -i * 9;
                        drawRect(b, n5 - 9, b + n2 + 4, n5, n4 / 2 << 24);
                        String formattedText = chatLine.getChatComponent().getFormattedText();
                        GlStateManager.enableBlend();

                        for (int j = 0; j + this.scrollPos < this.drawnChatLines.size() && j < this.getLineCount(); j++) {
                           ChatLine chatLine2 = this.drawnChatLines.get(j + this.scrollPos);
                           if ((flag || 10.0 - (updateCounter - chatLine2.getUpdatedCounter()) / 20.0 > 1.0) && chatLine2 instanceof ClientChatLine) {
                              ClientChatLine clientChatLine = (ClientChatLine)chatLine2;
                              if (clientChatLine.hasIcon()) {
                                 this.iconLines.add(chatLine2);
                              }

                              if (clientChatLine.isHighlighted()) {
                                 this.highlightedLines.add(chatLine2);
                              }
                           }
                        }

                        this.mc.fontRendererObj.drawStringWithShadow(formattedText, b, n5 - 8, 16777215 + (n4 << 24));
                        GlStateManager.disableAlpha();
                        GlStateManager.disableBlend();
                     }
                  }
               }
            }

            if (flag) {
               int n3 = this.mc.fontRendererObj.FONT_HEIGHT;
               GlStateManager.translate(-3.0F, 0.0F, 0.0F);
               int n4 = size * n3 + size;
               int n5 = n * n3 + n;
               int n6 = this.scrollPos * n5 / size;
               int n7 = n5 * n5 / n4;
               if (n4 != n5) {
                  int n8 = n6 > 0 ? 170 : 96;
                  int n9 = this.isScrolled ? 13382451 : 3355562;
                  drawRect(0, -n6, 2, -n6 - n7, n9 + (n8 << 24));
                  drawRect(2, -n6, 1, -n6 - n7, 13421772 + (n8 << 24));
               }
            }

            GlStateManager.popMatrix();
         }
      }
   }

   public void clearChatMessages() {
      this.drawnChatLines.clear();
      this.chatLines.clear();
      this.sentMessages.clear();
   }

   public void printChatMessage(IChatComponent chatComponent) {
      this.printChatMessageWithOptionalDeletion(chatComponent, 0);
   }

   public void printChatMessageWithOptionalDeletion(IChatComponent chatComponent, int chatLineId) {
      StringEvent3 stringEvent3 = new StringEvent3(chatComponent.getUnformattedTextForChat());
      CheatBreaker.getInstance().getEventBus().handleEvent(new StringEvent3(chatComponent.getUnformattedText()));
      if (!stringEvent3.isFlag()) {
         this.smoothProgress = 0.0F;
         this.setChatLine(chatComponent, chatLineId, this.mc.ingameGUI.getUpdateCounter(), false);
         logger.info("[CHAT] " + chatComponent.getUnformattedText());
      }
   }

   public void setChatLine(IChatComponent chatComponent, int chatLineId, int updateCounter, boolean displayOnly) {
      if (chatLineId != 0) {
         this.deleteChatLine(chatLineId);
      }

      int n = MathHelper.floor_float(this.getChatWidth() / this.getChatScale());
      boolean flag = chatComponent instanceof ChatComponentText && ((ChatComponentText)chatComponent).hasIcon();
      boolean flag2 = chatComponent instanceof ChatComponentText && ((ChatComponentText)chatComponent).isHighlighted();
      int n2 = flag ? Math.max(0, n - 17) : n;
      List splitText = GuiUtilRenderComponents.splitText(chatComponent, n2, this.mc.fontRendererObj, false, false);
      boolean chatOpen = this.getChatOpen();
      boolean flag3 = true;

      for (IChatComponent chatComponent2 : (Iterable<IChatComponent>)splitText) {
         if (chatOpen && this.scrollPos > 0) {
            this.isScrolled = true;
            this.scroll(1);
         }

         boolean flag4 = flag3 && flag;
         if (!flag4 && !flag2) {
            this.drawnChatLines.add(0, new ChatLine(updateCounter, chatComponent2, chatLineId).setIChatComponent(chatComponent));
         } else {
            ClientChatLine from = ClientChatLine.from(new ChatLine(updateCounter, chatComponent2, chatLineId));
            from.setIChatComponent(chatComponent);
            from.setHasIcon(flag4);
            from.setHighlighted(flag2);
            this.drawnChatLines.add(0, from);
         }

         flag3 = false;
      }

      SimpleEvent7 simpleEvent7 = new SimpleEvent7();
      CheatBreaker.getInstance().getEventBus().handleEvent(simpleEvent7);

      while (this.drawnChatLines.size() > 100 && !simpleEvent7.isFlag()) {
         this.drawnChatLines.remove(this.drawnChatLines.size() - 1);
      }

      if (!displayOnly) {
         this.chatLines.add(0, new ChatLine(updateCounter, chatComponent, chatLineId));

         while (this.chatLines.size() > 100 && !simpleEvent7.isFlag()) {
            this.chatLines.remove(this.chatLines.size() - 1);
         }
      }

      this.markNewLines(splitText);
   }

   public void refreshChat() {
      this.drawnChatLines.clear();
      this.resetScroll();

      for (int i = this.chatLines.size() - 1; i >= 0; i--) {
         ChatLine chatLine = this.chatLines.get(i);
         this.setChatLine(chatLine.getChatComponent(), chatLine.getChatLineID(), chatLine.getUpdatedCounter(), true);
      }

      CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent4());
   }

   public List<String> getSentMessages() {
      return this.sentMessages;
   }

   public void addToSentMessages(String message) {
      if (this.sentMessages.isEmpty() || !this.sentMessages.get(this.sentMessages.size() - 1).equals(message)) {
         this.sentMessages.add(message);
      }
   }

   public void resetScroll() {
      this.scrollPos = 0;
      this.isScrolled = false;
   }

   public void scroll(int amount) {
      this.scrollPos += amount;
      int size = this.drawnChatLines.size();
      if (this.scrollPos > size - this.getLineCount()) {
         this.scrollPos = size - this.getLineCount();
      }

      if (this.scrollPos <= 0) {
         this.scrollPos = 0;
         this.isScrolled = false;
      }
   }

   public IChatComponent getChatComponent(int mouseX, int mouseY) {
      int[] lineAt = this.getLineAt(mouseX, mouseY);
      if (lineAt == null) {
         return null;
      }

      ChatLine chatLine = this.drawnChatLines.get(lineAt[0]);
      int n = 0;

      for (IChatComponent chatComponent : chatLine.getChatComponent()) {
         if (chatComponent instanceof ChatComponentText) {
            n += this.mc
               .fontRendererObj
               .getStringWidth(GuiUtilRenderComponents.getRenderChatMessage(((ChatComponentText)chatComponent).getChatComponentText_TextValue(), false));
            if (n > lineAt[1]) {
               return chatComponent;
            }
         }
      }

      return null;
   }

   public boolean getChatOpen() {
      return this.mc.currentScreen instanceof GuiChat;
   }

   public void deleteChatLine(int id) {
      Iterator iterator = this.drawnChatLines.iterator();

      while (iterator.hasNext()) {
         ChatLine chatLine = (ChatLine)iterator.next();
         if (chatLine.getChatLineID() == id) {
            iterator.remove();
         }
      }

      iterator = this.chatLines.iterator();

      while (iterator.hasNext()) {
         ChatLine chatLine = (ChatLine)iterator.next();
         if (chatLine.getChatLineID() == id) {
            iterator.remove();
            break;
         }
      }
   }

   public int getChatWidth() {
      return calculateChatboxWidth(this.mc.gameSettings.chatWidth);
   }

   public int getChatHeight() {
      return calculateChatboxHeight(this.getChatOpen() ? this.mc.gameSettings.chatHeightFocused : this.mc.gameSettings.chatHeightUnfocused);
   }

   public float getChatScale() {
      return this.mc.gameSettings.chatScale;
   }

   public static int calculateChatboxWidth(float scale) {
      short s = 320;
      byte b = 40;
      return MathHelper.floor_float(scale * (s - b) + b);
   }

   public static int calculateChatboxHeight(float scale) {
      short s = 180;
      byte b = 20;
      return MathHelper.floor_float(scale * (s - b) + b);
   }

   public int getLineCount() {
      return this.getChatHeight() / 9;
   }

   public String getHoveredText(int n, int n2) {
      int[] lineAt = this.getLineAt(n, n2);
      if (lineAt == null) {
         return null;
      }

      ChatLine chatLine = this.drawnChatLines.get(lineAt[0]);
      IChatComponent chatComponent = chatLine.getIChatComponent() != null ? chatLine.getIChatComponent() : chatLine.getChatComponent();
      return EnumChatFormatting.getTextWithoutFormattingCodes(chatComponent.getUnformattedText());
   }

   public void addChatMessage(IChatComponent chatComponent) {
      this.smoothProgress = 0.0F;
      this.setChatLine(chatComponent, 0, this.mc.ingameGUI.getUpdateCounter(), false);
   }

   public static float clamp(float f, float f2, float f3) {
      return f < f2 ? f2 : Math.min(f, f3);
   }

   public int fadeTextColor(int n) {
      if (this.smoothChat && this.currentLineIndex <= this.newestMessageEndIndex) {
         int n2 = n >> 24 & 0xFF;
         n2 = (int)(n2 * this.smoothAlpha);
         return n & 16777215 | n2 << 24;
      } else {
         int n2 = n >> 24 & 0xFF;
         return new Color(n >> 16 & 0xFF, n >> 8 & 0xFF, n & 0xFF, n2).getRGB();
      }
   }

   public int trackLineIndex(int n) {
      this.currentLineIndex = n;
      return n;
   }

   public int[] getLineAt(int n, int n2) {
      if (!this.getChatOpen()) {
         return null;
      }

      ScaledResolution scaledResolution = new ScaledResolution(this.mc);
      int scaleFactor = scaledResolution.getScaleFactor();
      float chatScale = this.getChatScale();
      int n3 = n / scaleFactor - 2;
      int n4 = n2 / scaleFactor - 28;
      ChatMod chatMod = CheatBreaker.getInstance().getModManager().chatMod;
      if (chatMod.isEnabled()) {
         float[] hudPosition = chatMod.getHudPosition(scaledResolution, true);
         n3 = (int)(n / scaleFactor / chatMod.getHudScale() - hudPosition[0]);
         n4 = (int)(n2 / scaleFactor / chatMod.getHudScale() - this.mc.displayHeight / scaleFactor / chatMod.getHudScale() + chatMod.hudHeight + hudPosition[1]);
      }

      n3 = MathHelper.floor_float(n3 / chatScale);
      n4 = MathHelper.floor_float(n4 / chatScale);
      if (n3 >= 0 && n4 >= 0) {
         int min = Math.min(this.getLineCount(), this.drawnChatLines.size());
         if (n3 <= MathHelper.floor_float(this.getChatWidth() / this.getChatScale()) && n4 < this.mc.fontRendererObj.FONT_HEIGHT * min + min) {
            int n5 = n4 / this.mc.fontRendererObj.FONT_HEIGHT + this.scrollPos;
            if (n5 >= 0 && n5 < this.drawnChatLines.size()) {
               return new int[]{n5, n3};
            }
         }
      }

      return null;
   }

   public int fadeBackgroundColor(int n) {
      if (this.smoothChat && this.currentLineIndex <= this.newestMessageEndIndex) {
         int n2 = n >> 24 & 0xFF;
         n2 = (int)(n2 * this.smoothAlpha);
         return n & 16777215 | n2 << 24;
      } else {
         return n;
      }
   }

   public void render(int n, boolean flag) {
      this.smoothChat = flag;
      ChatMod chatMod = CheatBreaker.getInstance().getModManager().chatMod;
      if (this.mc.gameSettings.chatVisibility != EnumChatVisibility.HIDDEN) {
         int lineCount = this.getLineCount();
         boolean flag2 = false;
         int n2 = 0;
         int size = this.drawnChatLines.size();
         float f = this.mc.gameSettings.chatOpacity * chatMod.masterOpacity.getFloatValue() / 100.0F * 0.9F + 0.1F;
         long currentTimeMillis = System.currentTimeMillis();
         long l = currentTimeMillis - this.lastFrameTime;
         this.lastFrameTime = currentTimeMillis;
         this.updateSmoothProgress(l);
         float smoothProgress = this.smoothProgress;
         this.smoothAlpha = clamp(1.0F - --smoothProgress * smoothProgress * smoothProgress * smoothProgress, 0.0F, 1.0F);
         if (size > 0) {
            if (this.getChatOpen()) {
               flag2 = true;
            }

            float chatScale = this.getChatScale();
            int n3 = MathHelper.ceiling_float_int(this.getChatWidth() / chatScale);
            GL11.glPushMatrix();
            float hudHeight = chatMod.hudHeight;
            if (this.smoothChat && !this.isScrolled) {
               hudHeight += (9.0F - 9.0F * this.smoothAlpha) * this.getChatScale();
            }

            GL11.glTranslatef(0.0F, hudHeight, 0.0F);
            GL11.glScalef(chatScale, chatScale, 1.0F);

            for (int i = 0; i + this.scrollPos < this.drawnChatLines.size() && i < lineCount; i++) {
               ChatLine chatLine = this.drawnChatLines.get(this.trackLineIndex(i + this.scrollPos));
               if (chatLine != null) {
                  int n4 = n - chatLine.getUpdatedCounter();
                  if (n4 < 200 || flag2) {
                     double d = n4 / 200.0;
                     d = 1.0 - d;
                     d *= 10.0;
                     if (d < 0.0) {
                        d = 0.0;
                     }

                     if (d > 1.0) {
                        d = 1.0;
                     }

                     d *= d;
                     int n5 = (int)(255.0 * d);
                     if (flag2) {
                        n5 = 255;
                     }

                     n5 = (int)(n5 * f);
                     n2++;
                     if (n5 > 3) {
                        byte b = 0;
                        int n6 = -i * 9;
                        String formattedText = chatLine.getChatComponent().getFormattedText();
                        boolean flag3 = chatLine instanceof ClientChatLine && ((ClientChatLine)chatLine).hasIcon();
                        float f2 = flag3 ? 17.0F : 0.0F;
                        if (this.mc.currentScreen instanceof GuiChat && chatMod.showBackground.getValue().equals("While Typing")
                           || chatMod.showBackground.getValue().equals("ON")) {
                           int n7 = this.highlightedLines.contains(chatLine) ? -1431699456 : chatMod.backgroundColor.getColorValue();
                           int n8 = (int)((chatMod.backgroundColor.getColorValue() >> 24 & 0xFF) * (n5 / 255.0F));
                           int n9 = n7 >> 16 & 0xFF;
                           int n10 = n7 >> 8 & 0xFF;
                           int n11 = n7 & 0xFF;
                           int n12 = chatMod.backgroundWidthType.getValue().equals("Full")
                              ? n3 + 4
                              : (int)(this.mc.fontRendererObj.getStringWidth(formattedText) + f2);
                           drawRect(b, n6 - 9, b + n12, n6, this.fadeBackgroundColor(new Color(n9, n10, n11, n8).getRGB()));
                        }

                        boolean flag4 = !chatMod.stripColorFormatting.getValue().equals("OFF");
                        if (formattedText.contains(Minecraft.getMinecraft().getSession().getUsername())) {
                           String username = Minecraft.getMinecraft().getSession().getUsername();
                           if (!chatMod.highlightColour.getStringValue().equalsIgnoreCase("None") && chatMod.highlightOwnName.getBooleanValue()) {
                              formattedText = formattedText.replaceAll(
                                    Minecraft.getMinecraft().getSession().getUsername(),
                                    chatMod.getHighlightFormatting() + Minecraft.getMinecraft().getSession().getUsername()
                                 )
                                 + chatMod.getLastFormatting(formattedText.split(username)[0]);
                           }
                        }

                        for (int j = 0; j + this.scrollPos < this.drawnChatLines.size() && j < this.getLineCount(); j++) {
                           if ((flag2 || 10.0 - (n - chatLine.getUpdatedCounter()) / 20.0 > 1.0) && chatLine instanceof ClientChatLine) {
                              ClientChatLine clientChatLine = (ClientChatLine)chatLine;
                              if (clientChatLine.hasIcon()) {
                                 this.iconLines.add(chatLine);
                              }

                              if (clientChatLine.isHighlighted()) {
                                 this.highlightedLines.add(chatLine);
                              }
                           }
                        }

                        GlStateManager.enableBlend();
                        this.mc
                           .fontRendererObj
                           .drawString(
                              chatMod.stripColors(formattedText),
                              b + f2,
                              n6 - 8,
                              this.fadeTextColor(flag4 ? chatMod.textColor.getColorValue() : 16777215 + (n5 << 24)),
                              chatMod.textShadow.getBooleanValue()
                           );
                        GlStateManager.disableAlpha();
                        GlStateManager.disableBlend();
                     }
                  }
               }
            }

            if (flag2) {
               int n4 = this.mc.fontRendererObj.FONT_HEIGHT;
               GL11.glTranslatef(-3.0F, 0.0F, 0.0F);
               int n5 = size * n4 + size;
               int n6 = n2 * n4 + n2;
               int n7 = this.scrollPos * n6 / size;
               int n8 = n6 * n6 / n5;
               if (n5 != n6) {
                  int n9 = n7 > 0 ? 170 : 96;
                  int n10 = this.isScrolled ? 13382451 : 3355562;
                  drawRect(0, -n7, 2, -n7 - n8, n10 + (n9 << 24));
                  drawRect(2, -n7, 1, -n7 - n8, 13421772 + (n9 << 24));
               }
            }

            int n4 = 0;

            for (int i = 0; i + this.scrollPos < this.drawnChatLines.size() && i < this.getLineCount(); i++) {
               ChatLine chatLine = this.drawnChatLines.get(i + this.scrollPos);
               int n5 = n - chatLine.getUpdatedCounter();
               if (n5 < 200 || flag2) {
                  n4 -= Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT;
                  if (chatLine instanceof ClientChatLine) {
                     ClientChatLine clientChatLine = (ClientChatLine)chatLine;
                     if (clientChatLine.hasIcon()) {
                        double d = n5 / 200.0;
                        d = 1.0 - d;
                        d *= 10.0;
                        if (d < 0.0) {
                           d = 0.0;
                        }

                        if (d > 1.0) {
                           d = 1.0;
                        }

                        d *= d;
                        int n6 = (int)(255.0 * d);
                        if (flag2) {
                           n6 = 255;
                        }

                        n6 = (int)(n6 * f);
                        float clamp2 = clamp(n6 / 255.0F, 0.0F, 1.0F);
                        if (!(clamp2 <= 0.02F)) {
                           if (chatMod.textShadow.getBooleanValue()) {
                              GL11.glColor4f(0.4F, 0.4F, 0.4F, clamp2);
                              RenderUtils.drawTexture(this.logoWhitePng, this.getChatOpen() ? 5.105F : 2.1F, n4 + 1.9F, 14.0F, 6.9F);
                           }

                           GL11.glColor4f(1.0F, 1.0F, 1.0F, clamp2);
                           RenderUtils.drawTexture(this.logoWhitePng, this.getChatOpen() ? 4.105F : 1.1F, n4 + 0.9F, 14.0F, 6.9F);
                        }
                     }
                  }
               }
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
         }
      }
   }

   public void updateSmoothProgress(long l) {
      if (this.smoothProgress < 1.0F) {
         this.smoothProgress = this.smoothProgress
            + 0.001F * CheatBreaker.getInstance().getModManager().chatMod.smoothChatSpeed.getFloatValue() / 2.0F * (float)l;
      }

      this.smoothProgress = clamp(this.smoothProgress, 0.0F, 1.0F);
   }

   public List<IChatComponent> markNewLines(List<IChatComponent> list) {
      this.newestMessageEndIndex = list.size() - 1;
      return list;
   }
}
