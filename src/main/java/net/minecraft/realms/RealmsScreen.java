package net.minecraft.realms;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RealmsScreen {
    public static final int SKIN_TEX_WIDTH = 64;
    public static final int SKIN_TEX_HEIGHT = 32;
    public static final int SKIN_HAT_U = 40;
    public GuiScreenRealmsProxy proxy = new GuiScreenRealmsProxy(this);
    public static final int CONSTANT_8 = 8;
    public static final int CONSTANT_8_2 = 8;
    public static final int CONSTANT_8_3 = 8;
    public int intValue;
    public static final int CONSTANT_8_4 = 8;
    public static final int CONSTANT_8_5 = 8;
    public static final int CONSTANT_8_6 = 8;
    public int intValue2;
    public static final int CONSTANT_8_7 = 8;

    public GuiScreenRealmsProxy getProxy() {
        return this.proxy;
    }

    public void init() throws java.io.IOException {
    }

    public void init(Minecraft mc, int n, int n2) {
    }

    public void drawCenteredString(String str, int n, int n2, int n3) {
        this.proxy.drawCenteredString(str, n, n2, n3);
    }

    public void blit(int n, int n2, int n3, int n4, int n5, int n6) {
        this.proxy.drawTexturedModalRect(n, n2, n3, n4, n5, n6);
    }

    public static void blit(int n, int n2, float f, float f2, int n3, int n4, int n5, int n6, float f3, float f4) {
        Gui.drawScaledCustomSizeModalRect(n, n2, f, f2, n3, n4, n5, n6, f3, f4);
    }

    public static void blit(int n, int n2, float f, float f2, int n3, int n4, float f3, float f4) {
        Gui.drawModalRectWithCustomSizedTexture(n, n2, f, f2, n3, n4, f3, f4);
    }

    public void fillGradient(int n, int n2, int n3, int n4, int n5, int n6) {
        this.proxy.drawGradientRect(n, n2, n3, n4, n5, n6);
    }

    public void renderBackground() {
        this.proxy.drawDefaultBackground();
    }

    public boolean isPauseScreen() {
        return this.proxy.doesGuiPauseGame();
    }

    public void renderBackground(int n) {
        this.proxy.drawWorldBackground(n);
    }

    public void render(int n, int n2, float f) throws java.io.IOException {
        for (int i = 0; i < this.proxy.getButtons().size(); i++) {
            ((RealmsButton)this.proxy.getButtons().get(i)).render(n, n2);
        }
    }

    public void renderTooltip(ItemStack itemStack, int n, int n2) {
        this.proxy.renderToolTip(itemStack, n, n2);
    }

    public void renderTooltip(String str, int n, int n2) {
        this.proxy.drawCreativeTabHoveringText(str, n, n2);
    }

    public void renderTooltip(List list, int n, int n2) {
        this.proxy.drawHoveringText(list, n, n2);
    }

    public static void bindFace(String str) {
        ResourceLocation locationSkin = AbstractClientPlayer.getLocationSkin(str);
        if (locationSkin == null) {
            locationSkin = AbstractClientPlayer.getLocationSkin("default");
        }

        AbstractClientPlayer.getDownloadImageSkin(locationSkin, str);
        Minecraft.getMinecraft().getTextureManager().bindTexture(locationSkin);
    }

    public static void bind(String str) {
        ResourceLocation resourceLocation = new ResourceLocation(str);
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
    }

    public void tick() {
    }

    public int width() {
        return this.proxy.width;
    }

    public int height() {
        return this.proxy.height;
    }

    public int fontLineHeight() {
        return this.proxy.getFontHeight();
    }

    public int fontWidth(String str) {
        return this.proxy.getStringWidth(str);
    }

    public List fontSplit(String str, int n) {
        return this.proxy.wrapLines(str, n);
    }

    public void buttonClicked(RealmsButton realmsButton) throws java.io.IOException {
    }

    public static RealmsButton newButton(int n, int n2, int n3, String str) {
        return new RealmsButton(n, n2, n3, str);
    }

    public static RealmsButton newButton(int n, int n2, int n3, int n4, int n5, String str) {
        return new RealmsButton(n, n2, n3, n4, n5, str);
    }

    public void buttonsClear() {
        this.proxy.clear();
    }

    public void buttonsAdd(RealmsButton realmsButton) {
        this.proxy.addButton(realmsButton);
    }

    public List buttons() {
        return this.proxy.getButtons();
    }

    public void buttonsRemove(RealmsButton realmsButton) {
        this.proxy.removeButton(realmsButton);
    }

    public RealmsEditBox newEditBox(int n, int n2, int n3, int n4) {
        return new RealmsEditBox(n, n2, n3, n4);
    }

    public void mouseClicked(int n, int n2, int n3) {
    }

    public void mouseEvent() {
    }

    public void keyboardEvent() {
    }

    public void mouseReleased(int n, int n2, int n3) {
    }

    public void mouseDragged(int n, int n2, int n3, long l) {
    }

    public void keyPressed(char c, int n) throws java.io.IOException {
    }

    public void confirmResult(boolean flag, int n) {
    }

    public static String getLocalizedString(String str) {
        return I18n.format(str);
    }

    public static String getLocalizedString(String str, Object... objs) {
        return I18n.format(str, objs);
    }

    public RealmsAnvilLevelStorageSource getLevelStorageSource() {
        return new RealmsAnvilLevelStorageSource(Minecraft.getMinecraft().getSaveLoader());
    }

    public void removed() {
    }

    public void drawWithShadow(String str, int n, int n2, int n3) {
        this.proxy.drawWithShadow(str, n, n2, n3);
    }

    public void handleString(String str, int n, int n2, int n3) {
        this.proxy.handleString(str, n, n2, n3);
    }
}
