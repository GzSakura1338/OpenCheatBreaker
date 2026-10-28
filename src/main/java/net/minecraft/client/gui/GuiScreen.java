package net.minecraft.client.gui;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.animation.ColorAnimation;
import com.cheatbreaker.client.event.FloatEvent7;
import com.cheatbreaker.client.ui.screen.FriendsScreen;
import com.google.common.base.Throwables;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class GuiScreen extends Gui {
    public static RenderItem itemRender = new RenderItem();
    public Minecraft mc;
    public int width;
    public int height;
    public List<GuiButton> buttonList = new ArrayList<>();
    public List labelList = new ArrayList();
    public boolean allowUserInput;
    public FontRenderer fontRendererObj;
    public GuiButton selectedButton;
    public int eventButton;
    public long lastMouseEvent;
    public int touchValue;
    public static ColorAnimation colorAnimation = new ColorAnimation(0, -553648128);
    public static ColorAnimation colorAnimation2 = new ColorAnimation(0, 1243487774);

    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        for (int i = 0; i < this.buttonList.size(); i++) {
            this.buttonList.get(i).drawButton(this.mc, mouseX, mouseY);
        }

        for (int i = 0; i < this.labelList.size(); i++) {
            ((GuiLabel)this.labelList.get(i)).drawLabel(this.mc, mouseX, mouseY);
        }
    }

    public void keyTyped(char typedChar, int keyCode) throws java.io.IOException {
        if (CheatBreaker.getInstance().getKeybindSettings().showFriendsMenu.getBooleanValue()
            && !CheatBreaker.getInstance().getKeybindSettings().friendMenuKeybind.hasMouseBind()
            && keyCode == CheatBreaker.getInstance().getKeybindSettings().friendMenuKeybind.getIntValue()
            && CheatBreaker.getInstance().getKeybindSettings().friendMenuKeybind.areModifiersDown()) {
            this.mc.displayGuiScreen(FriendsScreen.open(this.mc.currentScreen));
        }

        if (keyCode == 1) {
            if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().guiBlur.getValue()) {
                this.mc.entityRenderer.stopUseShader();
            }

            this.mc.displayGuiScreen(null);
            this.mc.setIngameFocus();
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
        try {
            StringSelection stringSelection = new StringSelection(copyText);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, null);
        } catch (Exception e) {
        }
    }

    public void renderToolTip(ItemStack itemIn, int x, int y) {
        List tooltip = itemIn.getTooltip(this.mc.thePlayer, this.mc.gameSettings.advancedItemTooltips);

        for (int i = 0; i < tooltip.size(); i++) {
            if (i == 0) {
                tooltip.set(i, itemIn.getRarity().rarityColor + (String)tooltip.get(i));
            } else {
                tooltip.set(i, EnumChatFormatting.GRAY + (String)tooltip.get(i));
            }
        }

        this.drawHoveringText(tooltip, x, y);
    }

    public void drawCreativeTabHoveringText(String tabName, int mouseX, int mouseY) {
        this.drawHoveringText(Arrays.asList(tabName), mouseX, mouseY);
    }

    public void drawHoveringText(List textLines, int x, int y) {
        if (!textLines.isEmpty()) {
            GL11.glDisable(32826);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(2896);
            GL11.glDisable(2929);
            int n = 0;

            for (Object obj : textLines) {
                int stringWidth = this.fontRendererObj.getStringWidth((String)obj);
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

            floatValue = 300.0F;
            itemRender.zLevel = 300.0F;
            int n5 = -267386864;
            drawVerticalGradient(n2 - 3, n3 - 4, n2 + n + 3, n3 - 3, n5, n5);
            drawVerticalGradient(n2 - 3, n3 + n4 + 3, n2 + n + 3, n3 + n4 + 4, n5, n5);
            drawVerticalGradient(n2 - 3, n3 - 3, n2 + n + 3, n3 + n4 + 3, n5, n5);
            drawVerticalGradient(n2 - 4, n3 - 3, n2 - 3, n3 + n4 + 3, n5, n5);
            drawVerticalGradient(n2 + n + 3, n3 - 3, n2 + n + 4, n3 + n4 + 3, n5, n5);
            int n6 = 1347420415;
            int n7 = (n6 & 16711422) >> 1 | n6 & 0xFF000000;
            drawVerticalGradient(n2 - 3, n3 - 3 + 1, n2 - 3 + 1, n3 + n4 + 3 - 1, n6, n7);
            drawVerticalGradient(n2 + n + 2, n3 - 3 + 1, n2 + n + 3, n3 + n4 + 3 - 1, n6, n7);
            drawVerticalGradient(n2 - 3, n3 - 3, n2 + n + 3, n3 - 3 + 1, n6, n6);
            drawVerticalGradient(n2 - 3, n3 + n4 + 2, n2 + n + 3, n3 + n4 + 3, n7, n7);

            for (int i = 0; i < textLines.size(); i++) {
                String str = (String)textLines.get(i);
                this.fontRendererObj.drawShadowedString(str, n2, n3, -1);
                if (i == 0) {
                    n3 += 2;
                }

                n3 += 10;
            }

            floatValue = 0.0F;
            itemRender.zLevel = 0.0F;
            GL11.glEnable(2896);
            GL11.glEnable(2929);
            RenderHelper.enableStandardItemLighting();
            GL11.glEnable(32826);
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws java.io.IOException {
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

    public void actionPerformed(GuiButton button) throws java.io.IOException {
    }

    public void setWorldAndResolution(Minecraft mc, int width, int height) throws java.io.IOException {
        this.mc = mc;
        this.fontRendererObj = mc.fontRendererObj;
        this.width = width;
        this.height = height;
        this.buttonList.clear();
        this.initGui();
        this.startBlur();
    }

    public void initGui() throws java.io.IOException {
    }

    public void handleInput() throws java.io.IOException {
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

    public void handleMouseInput() throws java.io.IOException {
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

    public void handleKeyboardInput() throws java.io.IOException {
        char eventCharacter = Keyboard.getEventCharacter();
        int eventKey = Keyboard.getEventKey();
        if (Keyboard.getEventKeyState() || eventCharacter > ' ' && eventKey == 0) {
            this.keyTyped(eventCharacter, eventKey);
        }

        this.mc.dispatchKeypresses();
    }

    public void updateScreen() throws java.io.IOException {
    }

    public void onGuiClosed() {
    }

    public void drawDefaultBackground() {
        if (this.mc.theWorld != null) {
            this.updateBlurAnimation();
            this.drawContainerBackground(this.width, this.height);
        } else {
            this.drawBackdrop(0, this.width, this.height);
        }
    }

    public void drawWorldBackground(int tint) {
        if (this.mc.theWorld != null) {
            drawVerticalGradient(0.0F, 0.0F, this.width, this.height, -1072689136, -804253680);
        } else {
            this.drawBackground(tint);
        }
    }

    public void drawBackground(int tint) {
        GL11.glDisable(2896);
        GL11.glDisable(2912);
        Tessellator instance = Tessellator.instance;
        this.mc.getTextureManager().bindTexture(optionsBackground);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        float f = 32.0F;
        instance.startDrawingQuads();
        instance.setColorOpaque_I(4210752);
        instance.addVertexWithUV(0.0, this.height, 0.0, 0.0, this.height / f + tint);
        instance.addVertexWithUV(this.width, this.height, 0.0, this.width / f, this.height / f + tint);
        instance.addVertexWithUV(this.width, 0.0, 0.0, this.width / f, tint);
        instance.addVertexWithUV(0.0, 0.0, 0.0, 0.0, tint);
        instance.draw();
    }

    public boolean doesGuiPauseGame() {
        return true;
    }

    public void confirmClicked(boolean result, int id) throws java.io.IOException {
    }

    public static boolean isCtrlKeyDown() {
        return Minecraft.isRunningOnMac ? Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220) : Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
    }

    public static boolean isShiftKeyDown() {
        return Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
    }

    public void updateBlurAnimation() {
        if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().guiBlur.getValue()) {
            try {
                if (this.mc.entityRenderer.isBlurActive()) {
                    ShaderGroup blurShaderGroup = Minecraft.getMinecraft().entityRenderer.getBlurShaderGroup();

                    for (Shader shader : blurShaderGroup.getListShaders()) {
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

    public void drawBlurredBackground(float f, float f2) {
        ShaderGroup blurShaderGroup = Minecraft.getMinecraft().entityRenderer.getBlurShaderGroup();

        try {
            if (this.mc.entityRenderer.isBlurActive()) {
                for (Shader shader : blurShaderGroup.getListShaders()) {
                    ShaderUniform shaderUniform = shader.getShaderManager().getShaderUniform("Progress");
                    if (shaderUniform != null) {
                        shaderUniform.set(colorAnimation.getProgress());
                    }
                }
            }
        } catch (IllegalArgumentException e) {
            Throwables.propagate(e);
        }

        this.drawContainerBackground(f, f2);
    }

    public void drawBackdrop(int n, float f, float f2) {
        if (this.mc.theWorld != null) {
            drawVerticalGradient(0.0F, 0.0F, f, f2, -1072689136, -804253680);
        } else {
            this.drawBackground(n);
        }
    }

    public void startBlur() {
        if (CheatBreaker.getInstance().getKeybindSettings().guiBlur.getBooleanValue() && this.mc.theWorld != null && this.mc.thePlayer != null) {
            this.mc.entityRenderer.reloadBlurShader();
        }

        if (CheatBreaker.getInstance().getGuiScreen() == null) {
            colorAnimation.start();
            colorAnimation2.start();
        }
    }

    public void drawContainerBackground(float f, float f2) {
        if (CheatBreaker.getInstance().getKeybindSettings().containerBackground.getStringValue().equals(CheatBreaker.clientName)) {
            drawVerticalGradient(0.0F, 0.0F, f, f2, colorAnimation.getColor(true).getRGB(), colorAnimation2.getColor(true).getRGB());
        } else if (CheatBreaker.getInstance().getKeybindSettings().containerBackground.getStringValue().equals("Vanilla")) {
            this.drawBackdrop(0, f, f2);
        }

        CheatBreaker.getInstance().getEventBus().handleEvent(new FloatEvent7(f, f2));
    }
}
