package net.minecraft.client.gui;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.animation.ColorAnimation;
import com.cheatbreaker.client.ui.screen.FriendsScreen;
import com.cheatbreaker.client.util.DeveloperFlags;
import com.cheatbreaker.client.util.ScreenshotHelper;
import com.google.common.base.Splitter;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.awt.Toolkit;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.entity.EntityList;
import net.minecraft.event.ClickEvent;

import net.minecraft.event.HoverEvent;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class GuiScreen extends Gui implements GuiYesNoCallback {
   public static Logger LOGGER = LogManager.getLogger();
   public static Set<String> PROTOCOLS = Sets.newHashSet(new String[]{"http", "https"});
   public static Splitter NEWLINE_SPLITTER = Splitter.on('\n');
   public Minecraft mc;
   public RenderItem itemRender;
   public int width;
   public int height;
   public List<GuiButton> buttonList = Lists.newArrayList();
   public List<GuiLabel> labelList = Lists.newArrayList();
   public boolean allowUserInput;
   public FontRenderer fontRendererObj;
   public GuiButton selectedButton;
   public int eventButton;
   public long lastMouseEvent;
   public int touchValue;
   public URI clickedLinkURI;
   public static ColorAnimation colorAnimation = new ColorAnimation(0, -553648128);
   public static ColorAnimation colorAnimation2 = new ColorAnimation(0, 1243487774);

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      for (int i = 0; i < this.buttonList.size(); i++) {
         this.buttonList.get(i).drawButton(this.mc, mouseX, mouseY);
      }

      for (int i = 0; i < this.labelList.size(); i++) {
         this.labelList.get(i).drawLabel(this.mc, mouseX, mouseY);
      }
   }

   public void keyTyped(char typedChar, int keyCode) throws IOException {
      if (CheatBreaker.getInstance().getKeybindSettings().showFriendsMenu.getBooleanValue()
         && !CheatBreaker.getInstance().getKeybindSettings().friendMenuKeybind.hasMouseBind()
         && keyCode == CheatBreaker.getInstance().getKeybindSettings().friendMenuKeybind.getIntValue()
         && CheatBreaker.getInstance().getKeybindSettings().friendMenuKeybind.areModifiersDown()) {
         this.mc.displayGuiScreen(FriendsScreen.open(this.mc.currentScreen));
      }

      if (keyCode == 1) {
         if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().guiBlur.getValue()) {
            this.mc.entityRenderer.run();
         }

         this.mc.displayGuiScreen((GuiScreen)null);
         if (this.mc.currentScreen == null) {
            this.mc.setIngameFocus();
         }
      }
   }

   public static String getClipboardString() {
      try {
         Transferable contents = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
         if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            return (String)contents.getTransferData(DataFlavor.stringFlavor);
         }
      } catch (Exception e) {
      }

      return "";
   }

   public static void setClipboardString(String copyText) {
      if (!StringUtils.isEmpty(copyText)) {
         try {
            StringSelection stringSelection = new StringSelection(copyText);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, (ClipboardOwner)null);
         } catch (Exception e) {
         }
      }
   }

   public void renderToolTip(ItemStack stack, int x, int y) {
      List tooltip = stack.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

      for (int i = 0; i < tooltip.size(); i++) {
         if (i == 0) {
            tooltip.set(i, stack.getRarity().rarityColor + (String)tooltip.get(i));
         } else {
            tooltip.set(i, EnumChatFormatting.GRAY + (String)tooltip.get(i));
         }
      }

      this.drawHoveringText(tooltip, x, y);
   }

   public void drawCreativeTabHoveringText(String tabName, int mouseX, int mouseY) {
      this.drawHoveringText(Arrays.asList(tabName), mouseX, mouseY);
   }

   public void drawHoveringText(List<String> textLines, int x, int y) {
      if (!textLines.isEmpty()) {
         GlStateManager.disableRescaleNormal();
         RenderHelper.disableStandardItemLighting();
         GlStateManager.disableLighting();
         GlStateManager.disableDepth();
         int n = 0;

         for (String str : textLines) {
            int stringWidth = this.fontRendererObj.getStringWidth(str);
            if (stringWidth > n) {
               n = stringWidth;
            }
         }

         int n2 = x + 12;
         int n3 = y - 12;
         int n4 = 8;
         if (textLines.size() > 1) {
            n4 += 2 + (textLines.size() - 1) * 10;
         }

         if (n2 + n > this.width) {
            n2 -= 28 + n;
         }

         if (n3 + n4 + 6 > this.height) {
            n3 = this.height - n4 - 6;
         }

         zLevel = 300.0F;
         this.itemRender.zLevel = 300.0F;
         int n5 = -267386864;
         this.drawGradientRect(n2 - 3, n3 - 4, n2 + n + 3, n3 - 3, n5, n5);
         this.drawGradientRect(n2 - 3, n3 + n4 + 3, n2 + n + 3, n3 + n4 + 4, n5, n5);
         this.drawGradientRect(n2 - 3, n3 - 3, n2 + n + 3, n3 + n4 + 3, n5, n5);
         this.drawGradientRect(n2 - 4, n3 - 3, n2 - 3, n3 + n4 + 3, n5, n5);
         this.drawGradientRect(n2 + n + 3, n3 - 3, n2 + n + 4, n3 + n4 + 3, n5, n5);
         int n6 = 1347420415;
         int n7 = (n6 & 16711422) >> 1 | n6 & 0xFF000000;
         this.drawGradientRect(n2 - 3, n3 - 3 + 1, n2 - 3 + 1, n3 + n4 + 3 - 1, n6, n7);
         this.drawGradientRect(n2 + n + 2, n3 - 3 + 1, n2 + n + 3, n3 + n4 + 3 - 1, n6, n7);
         this.drawGradientRect(n2 - 3, n3 - 3, n2 + n + 3, n3 - 3 + 1, n6, n6);
         this.drawGradientRect(n2 - 3, n3 + n4 + 2, n2 + n + 3, n3 + n4 + 3, n7, n7);

         for (int i = 0; i < textLines.size(); i++) {
            String str = (String)textLines.get(i);
            this.fontRendererObj.drawStringWithShadow(str, n2, n3, -1);
            if (i == 0) {
               n3 += 2;
            }

            n3 += 10;
         }

         zLevel = 0.0F;
         this.itemRender.zLevel = 0.0F;
         GlStateManager.enableLighting();
         GlStateManager.enableDepth();
         RenderHelper.enableStandardItemLighting();
         GlStateManager.enableRescaleNormal();
      }
   }

   public void handleComponentHover(IChatComponent component, int x, int y) {
      if (component != null && component.getChatStyle().getChatHoverEvent() != null) {
         HoverEvent chatHoverEvent = component.getChatStyle().getChatHoverEvent();
         if (chatHoverEvent.getAction() == HoverEvent.Action.SHOW_ITEM) {
            ItemStack itemStack = null;

            try {
               NBTTagCompound tagFromJson = JsonToNBT.getTagFromJson(chatHoverEvent.getValue().getUnformattedText());
               if (tagFromJson instanceof NBTTagCompound) {
                  itemStack = ItemStack.loadItemStackFromNBT(tagFromJson);
               }
            } catch (NBTException e) {
            }

            if (itemStack != null) {
               this.renderToolTip(itemStack, x, y);
            } else {
               this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Item!", x, y);
            }
         } else if (chatHoverEvent.getAction() == HoverEvent.Action.SHOW_ENTITY) {
            if (this.mc.gameSettings.advancedItemTooltips) {
               try {
                  NBTTagCompound tagFromJson = JsonToNBT.getTagFromJson(chatHoverEvent.getValue().getUnformattedText());
                  if (tagFromJson instanceof NBTTagCompound) {
                     ArrayList newArrayList = Lists.newArrayList();
                     NBTTagCompound nbttagCompound = tagFromJson;
                     newArrayList.add(nbttagCompound.getString("name"));
                     if (nbttagCompound.hasKey("type", 8)) {
                        String string = nbttagCompound.getString("type");
                        newArrayList.add("Type: " + string + " (" + EntityList.getIDFromString(string) + ")");
                     }

                     newArrayList.add(nbttagCompound.getString("id"));
                     this.drawHoveringText(newArrayList, x, y);
                  } else {
                     this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", x, y);
                  }
               } catch (NBTException e) {
                  this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid Entity!", x, y);
               }
            }
         } else if (chatHoverEvent.getAction() == HoverEvent.Action.SHOW_TEXT) {
            this.drawHoveringText(NEWLINE_SPLITTER.splitToList(chatHoverEvent.getValue().getFormattedText()), x, y);
         } else if (chatHoverEvent.getAction() == HoverEvent.Action.SHOW_ACHIEVEMENT) {
            StatBase oneShotStat = StatList.getOneShotStat(chatHoverEvent.getValue().getUnformattedText());
            if (oneShotStat != null) {
               IChatComponent statName = oneShotStat.getStatName();
               ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("stats.tooltip.type." + (oneShotStat.isAchievement() ? "achievement" : "statistic"));
               chatComponentTranslation.getChatStyle().setItalic(true);
               String str = oneShotStat instanceof Achievement ? ((Achievement)oneShotStat).getDescription() : null;
               ArrayList newArrayList = Lists.newArrayList(new String[]{statName.getFormattedText(), chatComponentTranslation.getFormattedText()});
               if (str != null) {
                  newArrayList.addAll(this.fontRendererObj.listFormattedStringToWidth(str, 150));
               }

               this.drawHoveringText(newArrayList, x, y);
            } else {
               this.drawCreativeTabHoveringText(EnumChatFormatting.RED + "Invalid statistic/achievement!", x, y);
            }
         }

         GlStateManager.disableLighting();
      }
   }

   public void setText(String newChatText, boolean shouldOverwrite) {
   }

   public boolean handleComponentClick(IChatComponent component) {
      if (component == null) {
         return false;
      }

      ClickEvent chatClickEvent = component.getChatStyle().getChatClickEvent();
      if (isShiftKeyDown()) {
         if (component.getChatStyle().getInsertion() != null) {
            this.setText(component.getChatStyle().getInsertion(), false);
         }
      } else if (chatClickEvent != null) {
         if (chatClickEvent.getAction() == ClickEvent.Action.OPEN_URL) {
            if (!this.mc.gameSettings.chatLinks) {
               return false;
            }

            try {
               URI uri = new URI(chatClickEvent.getValue());
               String scheme = uri.getScheme();
               if (scheme == null) {
                  throw new URISyntaxException(chatClickEvent.getValue(), "Missing protocol");
               }

               if (!PROTOCOLS.contains(scheme.toLowerCase())) {
                  throw new URISyntaxException(chatClickEvent.getValue(), "Unsupported protocol: " + scheme.toLowerCase());
               }

               if (this.mc.gameSettings.chatLinksPrompt) {
                  this.clickedLinkURI = uri;
                  this.mc.displayGuiScreen(new GuiConfirmOpenLink(this, chatClickEvent.getValue(), 31102009, false));
               } else {
                  this.openWebLink(uri);
               }
            } catch (URISyntaxException e) {
               LOGGER.error("Can't open url for " + chatClickEvent, e);
            }
         } else if (chatClickEvent.getAction() == ClickEvent.Action.OPEN_FILE) {
            URI toURI = new File(chatClickEvent.getValue()).toURI();
            this.openWebLink(toURI);
         } else if (chatClickEvent.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
            this.setText(chatClickEvent.getValue(), true);
         } else if (chatClickEvent.getAction() == ClickEvent.Action.RUN_COMMAND) {
            this.sendChatMessage(chatClickEvent.getValue(), false);
         } else if (chatClickEvent.getAction() == ClickEvent.Action.UPLOAD_SCREENSHOT) {
            this.handleString(chatClickEvent.getValue());
         } else if (chatClickEvent.getAction() == ClickEvent.Action.COPY_SCREENSHOT) {
            ScreenshotHelper.handleString(chatClickEvent.getValue());
         } else {
            LOGGER.error("Don't know how to handle " + chatClickEvent);
         }

         return true;
      }

      return false;
   }

   public void sendChatMessage(String msg) {
      this.sendChatMessage(msg, true);
   }

   public void sendChatMessage(String msg, boolean addToChat) {
      if (addToChat) {
         this.mc.ingameGUI.getChatGUI().addToSentMessages(msg);
      }

      this.mc.thePlayer.sendChatMessage(msg);
   }

   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      if (mouseButton == 0) {
         for (int i = 0; i < this.buttonList.size(); i++) {
            GuiButton guiButton = this.buttonList.get(i);
            if (guiButton.mousePressed(this.mc, mouseX, mouseY)) {
               this.selectedButton = guiButton;
               guiButton.playPressSound(this.mc.getSoundHandler());
               this.actionPerformed(guiButton);
            }
         }
      }
   }

   public void mouseReleased(int mouseX, int mouseY, int state) {
      if (this.selectedButton != null && state == 0) {
         this.selectedButton.mouseReleased(mouseX, mouseY);
         this.selectedButton = null;
      }
   }

   public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
   }

   public void actionPerformed(GuiButton button) throws IOException {
   }

   public void setWorldAndResolution(Minecraft mc, int width, int height) {
      this.mc = mc;
      this.itemRender = mc.getRenderItem();
      this.fontRendererObj = mc.fontRendererObj;
      this.width = width;
      this.height = height;
      this.buttonList.clear();
      this.initGui();
      this.run2();
   }

   public void setGuiSize(int w, int h) {
      this.width = w;
      this.height = h;
   }

   public void initGui() {
   }

   public void handleInput() throws IOException {
      if (Mouse.isCreated()) {
         while (Mouse.next()) {
            if (this != this.mc.currentScreen) {
               return;
            }

            this.handleMouseInput();
         }
      }

      if (Keyboard.isCreated()) {
         while (Keyboard.next()) {
            if (this != this.mc.currentScreen) {
               return;
            }

            this.handleKeyboardInput();
         }
      }
   }

   public void handleMouseInput() throws IOException {
      int n = Mouse.getEventX() * this.width / this.mc.displayWidth;
      int n2 = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
      int eventButton = Mouse.getEventButton();
      if (Mouse.getEventButtonState()) {
         if (this.mc.gameSettings.touchscreen && this.touchValue++ > 0) {
            return;
         }

         this.eventButton = eventButton;
         this.lastMouseEvent = Minecraft.getSystemTime();
         this.mouseClicked(n, n2, this.eventButton);
      } else if (eventButton != -1) {
         if (this.mc.gameSettings.touchscreen && --this.touchValue > 0) {
            return;
         }

         this.eventButton = -1;
         this.mouseReleased(n, n2, eventButton);
      } else if (this.eventButton != -1 && this.lastMouseEvent > 0L) {
         long l = Minecraft.getSystemTime() - this.lastMouseEvent;
         this.mouseClickMove(n, n2, this.eventButton, l);
      }
   }

   public void handleKeyboardInput() throws IOException {
      char eventCharacter = Keyboard.getEventCharacter();
      int eventKey = Keyboard.getEventKey();
      if (Keyboard.getEventKeyState() || eventCharacter > ' ' && eventKey == 0) {
         this.keyTyped(eventCharacter, eventKey);
      }

      this.mc.dispatchKeypresses();
   }

   public void updateScreen() {
   }

   public void onGuiClosed() {
   }

   public void drawDefaultBackground() {
      if (this.mc.theWorld != null) {
         this.run();
         this.process2(this.width, this.height);
      } else {
         this.process(0, this.width, this.height);
      }
   }

   public void drawWorldBackground(int tint) {
      if (this.mc.theWorld != null) {
         this.mc.ingameGUI.drawGradientRect(0.0F, 0.0F, this.width, this.height, -1072689136, -804253680);
      } else {
         this.drawBackground(tint);
      }
   }

   public void drawBackground(int tint) {
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      this.mc.getTextureManager().bindTexture(optionsBackground);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      float f = 32.0F;
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      worldRenderer.pos(0.0, this.height, 0.0).tex(0.0, this.height / 32.0F + tint).color(64, 64, 64, 255).endVertex();
      worldRenderer.pos(this.width, this.height, 0.0).tex(this.width / 32.0F, this.height / 32.0F + tint).color(64, 64, 64, 255).endVertex();
      worldRenderer.pos(this.width, 0.0, 0.0).tex(this.width / 32.0F, tint).color(64, 64, 64, 255).endVertex();
      worldRenderer.pos(0.0, 0.0, 0.0).tex(0.0, tint).color(64, 64, 64, 255).endVertex();
      instance.draw();
   }

   public boolean doesGuiPauseGame() {
      return true;
   }

   @Override
   public void confirmClicked(boolean result, int id) {
      if (id == 31102009) {
         if (result) {
            this.openWebLink(this.clickedLinkURI);
         }

         this.clickedLinkURI = null;
         this.mc.displayGuiScreen(this);
      }
   }

   public void openWebLink(URI url) {
      try {
         Class forName = Class.forName("java.awt.Desktop");
         Object invoke = forName.getMethod("getDesktop").invoke(null);
         forName.getMethod("browse", URI.class).invoke(invoke, url);
      } catch (Throwable e) {
         LOGGER.error("Couldn't open link", e);
      }
   }

   public static boolean isCtrlKeyDown() {
      return Minecraft.isRunningOnMac ? Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220) : Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
   }

   public static boolean isShiftKeyDown() {
      return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
   }

   public static boolean isAltKeyDown() {
      return Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
   }

   public static boolean isKeyComboCtrlX(int keyID) {
      return keyID == 45 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
   }

   public static boolean isKeyComboCtrlV(int keyID) {
      return keyID == 47 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
   }

   public static boolean isKeyComboCtrlC(int keyID) {
      return keyID == 46 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
   }

   public static boolean isKeyComboCtrlA(int keyID) {
      return keyID == 30 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown();
   }

   public void onResize(Minecraft mcIn, int w, int h) {
      this.setWorldAndResolution(mcIn, w, h);
   }

   public void process(float f, float f2) {
      ShaderGroup shaderGroup2 = Minecraft.getMinecraft().entityRenderer.getShaderGroup2();

      try {
         if (this.mc.entityRenderer.check()) {
            for (Shader shader : shaderGroup2.getListShaders()) {
               ShaderUniform shaderUniform = shader.getShaderManager().getShaderUniform("Progress");
               if (shaderUniform != null) {
                  shaderUniform.set(colorAnimation.getProgress());
               }
            }
         }
      } catch (IllegalArgumentException e) {
         Throwables.propagate(e);
      }

      this.process2(f, f2);
   }

   public void process(int n, float f, float f2) {
      if (this.mc.theWorld != null) {
         this.mc.ingameGUI.drawGradientRect((float)0.0F, (float)0.0F, (float)f, (float)f2, -1072689136, -804253680);
      } else {
         this.drawBackground(n);
      }
   }

   public void handleString(String str) {
      File file = new File(this.mc.mcDataDir + File.separator + "screenshots" + File.separator + str);
      if (file.exists()) {
         GuiIngame.flag = true;
         new Thread(() -> {
            try {
               BufferedImage read = ImageIO.read(file);
               ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
               ImageIO.write(read, "png", byteArrayOutputStream);
               URL url = new URL("https://api.imgur.com/3/image");
               String str2 = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(Base64.encodeBase64String(byteArrayOutputStream.toByteArray()), "UTF-8");
               str2 = str2 + "&" + URLEncoder.encode("key", "UTF-8") + "=" + URLEncoder.encode("7fd132c453b5486", "UTF-8");
               URLConnection openConnection = url.openConnection();
               openConnection.setDoOutput(true);
               openConnection.setDoInput(true);
               openConnection.setRequestProperty("Authorization", "Client-ID 7fd132c453b5486");
               openConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
               OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openConnection.getOutputStream());
               outputStreamWriter.write(str2);
               outputStreamWriter.flush();
               BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(openConnection.getInputStream()));
               StringBuilder sb = new StringBuilder();

               String str3;
               while ((str3 = bufferedReader.readLine()) != null) {
                  sb.append(str3).append(System.lineSeparator());
               }

               bufferedReader.close();
               GuiIngame.flag = false;
               Gson gson = new GsonBuilder().create();
               JsonObject fromJson = (JsonObject)gson.fromJson(sb.toString(), JsonObject.class);
               String str4 = "https://i.imgur.com/" + fromJson.get("data").getAsJsonObject().get("id").getAsString() + ".png";
               this.clickedLinkURI = new URI(str4);
               GuiConfirmOpenLink guiConfirmOpenLink = new GuiConfirmOpenLink(this, str4, 31102009, true);
               guiConfirmOpenLink.disableSecurityWarning();
               this.mc.displayGuiScreen(guiConfirmOpenLink);
            } catch (Exception e) {
               GuiIngame.flag = false;
               e.printStackTrace();
            }
         }).start();
      }
   }

   public void run() {
      if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().guiBlur.getValue()) {
         try {
            if (this.mc.entityRenderer.check()) {
               ShaderGroup shaderGroup2 = Minecraft.getMinecraft().entityRenderer.getShaderGroup2();

               for (Shader shader : shaderGroup2.getListShaders()) {
                  ShaderUniform shaderUniform = shader.getShaderManager().getShaderUniform("Progress");
                  if (shaderUniform != null) {
                     shaderUniform.set(colorAnimation.getProgress());
                  }
               }
            }

            GL11.glEnable(2929);
         } catch (IllegalArgumentException e) {
            Throwables.propagate(e);
         }
      }
   }

   public void run2() {
      if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().guiBlur.getValue() && this.mc.theWorld != null && this.mc.thePlayer != null) {
         this.mc.entityRenderer.run3();
      }

      if (CheatBreaker.getInstance().getGuiScreen() == null) {
         colorAnimation.start();
         colorAnimation2.start();
      }
   }

   public void process2(float f, float f2) {
      if (CheatBreaker.getInstance().getKeybindSettings().containerBackground.getStringValue().equals(CheatBreaker.clientName)) {
         this.mc
            .ingameGUI
            .drawGradientRect(
               (float)0.0F, (float)0.0F, (float)f, (float)f2, colorAnimation.getColor(true).getRGB(), colorAnimation2.getColor(true).getRGB()
            );
      } else if (CheatBreaker.getInstance().getKeybindSettings().containerBackground.getStringValue().equals("Vanilla")) {
         this.process(0, f, f2);
      }

      if (this.mc.isFullScreen() && DeveloperFlags.showInternalMods) {
         String str = "1.8.9 (" + CheatBreaker.getInstance().getCommitId() + "/" + CheatBreaker.getInstance().getBranch() + ")";
         CheatBreaker.getInstance().getFontManager().playRegular18.drawStringWithOutline(str, 5.0, this.height - 14.0F, -1879048193);
      }
   }
}
