package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;

public class GuiScreenRealmsProxy extends GuiScreen {
   public RealmsScreen realmsScreen;

   public GuiScreenRealmsProxy(RealmsScreen realmsScreen) {
      this.realmsScreen = realmsScreen;
      super.buttonList = Collections.synchronizedList(Lists.newArrayList());
   }

   public RealmsScreen getRealmsScreen() {
      return this.realmsScreen;
   }

   @Override
   public void initGui() {
      this.realmsScreen.init();
      super.initGui();
   }

   public void drawCenteredString(String str, int n, int n2, int n3) {
      super.drawCenteredString(this.fontRendererObj, str, n, n2, n3);
   }

   public void drawString(String str, int n, int n2, int n3, boolean flag) {
      if (flag) {
         super.drawString(this.fontRendererObj, str, n, n2, n3);
      } else {
         this.fontRendererObj.drawString(str, n, n2, n3);
      }
   }

   @Override
   public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
      this.realmsScreen.blit(x, y, textureX, textureY, width, height);
      super.drawTexturedModalRect(x, y, textureX, textureY, width, height);
   }

   @Override
   public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
      super.drawGradientRect(left, top, right, bottom, startColor, endColor);
   }

   @Override
   public void drawDefaultBackground() {
      super.drawDefaultBackground();
   }

   @Override
   public boolean doesGuiPauseGame() {
      return super.doesGuiPauseGame();
   }

   @Override
   public void drawWorldBackground(int tint) {
      super.drawWorldBackground(tint);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.realmsScreen.render(mouseX, mouseY, partialTicks);
   }

   @Override
   public void renderToolTip(ItemStack stack, int x, int y) {
      super.renderToolTip(stack, x, y);
   }

   @Override
   public void drawCreativeTabHoveringText(String tabName, int mouseX, int mouseY) {
      super.drawCreativeTabHoveringText(tabName, mouseX, mouseY);
   }

   @Override
   public void drawHoveringText(List<String> textLines, int x, int y) {
      super.drawHoveringText(textLines, x, y);
   }

   @Override
   public void updateScreen() {
      this.realmsScreen.tick();
      super.updateScreen();
   }

   public int getFontHeight() {
      return this.fontRendererObj.FONT_HEIGHT;
   }

   public int getStringWidth(String str) {
      return this.fontRendererObj.getStringWidth(str);
   }

   public void drawWithShadow(String str, int n, int n2, int n3) {
      this.fontRendererObj.drawStringWithShadow(str, n, n2, n3);
   }

   public List<String> wrapLines(String str, int n) {
      return this.fontRendererObj.listFormattedStringToWidth(str, n);
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      this.realmsScreen.buttonClicked(((GuiButtonRealmsProxy)button).getRealmsButton());
   }

   public void clear() {
      super.buttonList.clear();
   }

   public void addButton(RealmsButton realmsButton) {
      super.buttonList.add(realmsButton.getProxy());
   }

   public List<RealmsButton> getButtons() {
      ArrayList newArrayListWithExpectedSize = Lists.newArrayListWithExpectedSize(super.buttonList.size());

      for (GuiButton guiButton : super.buttonList) {
         newArrayListWithExpectedSize.add(((GuiButtonRealmsProxy)guiButton).getRealmsButton());
      }

      return newArrayListWithExpectedSize;
   }

   public void removeButton(RealmsButton realmsButton) {
      super.buttonList.remove(realmsButton.getProxy());
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      this.realmsScreen.mouseClicked(mouseX, mouseY, mouseButton);
      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void handleMouseInput() throws IOException {
      this.realmsScreen.mouseEvent();
      super.handleMouseInput();
   }

   @Override
   public void handleKeyboardInput() throws IOException {
      this.realmsScreen.keyboardEvent();
      super.handleKeyboardInput();
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY, int state) {
      this.realmsScreen.mouseReleased(mouseX, mouseY, state);
   }

   @Override
   public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      this.realmsScreen.mouseDragged(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      this.realmsScreen.keyPressed(typedChar, keyCode);
   }

   @Override
   public void confirmClicked(boolean result, int id) {
      this.realmsScreen.confirmResult(result, id);
   }

   @Override
   public void onGuiClosed() {
      this.realmsScreen.removed();
      super.onGuiClosed();
   }
}
