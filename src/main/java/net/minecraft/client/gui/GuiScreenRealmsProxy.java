package net.minecraft.client.gui;

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
        super.buttonList = Collections.synchronizedList(new ArrayList<>());
    }

    public RealmsScreen getRealmsScreen() {
        return this.realmsScreen;
    }

    @Override
    public void initGui() throws java.io.IOException {
        this.realmsScreen.init();
        super.initGui();
    }

    public void drawCenteredString(String str, int n, int n2, int n3) {
        super.drawCenteredString(this.fontRendererObj, str, n, n2, n3);
    }

    public void handleString(String str, int n, int n2, int n3) {
        super.drawString(this.fontRendererObj, str, n, n2, n3);
    }

    public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
        this.realmsScreen.blit(x, y, textureX, textureY, width, height);
        Gui.drawTexturedRect(x, y, textureX, textureY, width, height);
    }

    public void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        Gui.drawVerticalGradient(left, top, right, bottom, startColor, endColor);
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
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        this.realmsScreen.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderToolTip(ItemStack itemIn, int x, int y) {
        super.renderToolTip(itemIn, x, y);
    }

    @Override
    public void drawCreativeTabHoveringText(String tabName, int mouseX, int mouseY) {
        super.drawCreativeTabHoveringText(tabName, mouseX, mouseY);
    }

    @Override
    public void drawHoveringText(List textLines, int x, int y) {
        super.drawHoveringText(textLines, x, y);
    }

    @Override
    public void updateScreen() throws java.io.IOException {
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
        this.fontRendererObj.drawShadowedString(str, n, n2, n3);
    }

    public List wrapLines(String str, int n) {
        return this.fontRendererObj.listFormattedStringToWidth(str, n);
    }

    @Override
    public void actionPerformed(GuiButton button) throws java.io.IOException {
        this.realmsScreen.buttonClicked(((GuiButtonRealmsProxy)button).getRealmsButton());
    }

    public void clear() {
        super.buttonList.clear();
    }

    public void addButton(RealmsButton realmsButton) {
        super.buttonList.add(realmsButton.getProxy());
    }

    public List getButtons() {
        ArrayList list = new ArrayList(super.buttonList.size());

        for (GuiButton guiButton : super.buttonList) {
            list.add(((GuiButtonRealmsProxy)guiButton).getRealmsButton());
        }

        return list;
    }

    public void removeButton(RealmsButton realmsButton) {
        super.buttonList.remove(realmsButton);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws java.io.IOException {
        this.realmsScreen.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void handleMouseInput() throws java.io.IOException {
        this.realmsScreen.mouseEvent();
        super.handleMouseInput();
    }

    @Override
    public void handleKeyboardInput() throws java.io.IOException {
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
    public void keyTyped(char typedChar, int keyCode) throws java.io.IOException {
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
