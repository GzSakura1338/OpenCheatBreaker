package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.emote.EmoteWheelEntry;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.component.EmoteWheelButton;
import com.google.common.base.Preconditions;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.optifine.GlStateManager;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class EmoteWheelScreen extends UIScreen {
    public EmoteWheelEntry emoteWheelEntry;
    public static final int CONSTANT_8 = 8;
    public Consumer<EmoteWheelEntry> consumer;
    public EmoteWheelEntry[] emoteWheelEntryArray = new EmoteWheelEntry[8];
    public EmoteWheelButton[] emoteWheelButtonArray = new EmoteWheelButton[8];
    public int intValue = 0;
    public int page = 0;
    public List<EmoteWheelEntry> emoteWheelEntryList;
    public int holdKey;
    public static Minecraft mc2 = Minecraft.getMinecraft();

    @Override
    public void drawScreenScaled(float f, float f2) throws java.io.IOException {
        float scaledWidth = this.getScaledWidth();
        float scaledHeight = this.getScaledHeight();
        float floatValue = this.getFloatValue();

        for (EmoteWheelButton emoteWheelButton : this.emoteWheelButtonArray) {
            if (emoteWheelButton != null) {
                emoteWheelButton.render(f, f2, true);
            }
        }

        float f3 = 10.0F;
        float f4 = this.intValue >= f3 ? 1.0F : this.intValue / f3;
        GlStateManager.pushMatrix();
        GlStateManager.color(0.0F, 0.0F, 0.0F, 0.5F * f4);
        RenderUtils.drawProgressRing(scaledWidth / 2.0F, scaledHeight / 2.0F, 126.0 * floatValue, 123.2 * floatValue, 100.0, 100, 100.0);
        RenderUtils.drawProgressRing(scaledWidth / 2.0F, scaledHeight / 2.0F, 28.0 * floatValue, 25.2 * floatValue, 100.0, 100, 100.0);
        GlStateManager.popMatrix();
        int intValue = this.getIntValue();
        if (intValue > 1) {
            String str = EnumChatFormatting.GRAY + "Scroll for more (" + (this.page + 1) + "/" + intValue + ")";
            this.fontRendererObj.drawShadowedString(str, scaledWidth / 2.0F - this.fontRendererObj.getStringWidth(str) / 2.0F, scaledHeight / 2.0F + 147.0F * floatValue, -1);
        }

        if (this.emoteWheelEntry != null) {
            String name = this.emoteWheelEntry.getName();
            float stringWidth = this.fontRendererObj.getStringWidth(name);
            float f5 = 108.0F * floatValue;
            float f6 = stringWidth > f5 ? f5 / stringWidth : 1.0F;
            GlStateManager.pushMatrix();
            GL11.glTranslatef(scaledWidth / 2.0F, scaledHeight / 2.0F - 4.0F * floatValue, 0.0F);
            GL11.glScalef(f6, f6, 1.0F);
            this.fontRendererObj.drawShadowedString(name, -stringWidth / 2.0F, 0.0F, -1);
            GlStateManager.popMatrix();
        }

        if (!Keyboard.isKeyDown(this.holdKey)) {
            this.confirmSelection();
            mc2.displayGuiScreen(null);
        }
    }

    public void confirmSelection() {
        if (this.consumer != null) {
            if (this.emoteWheelEntry == null) {
                return;
            }

            this.consumer.accept(this.emoteWheelEntry);
        }
    }

    public void selectPage(int n) {
        int intValue = this.getIntValue();
        this.page = (n % intValue + intValue) % intValue;
        this.emoteWheelEntry = null;
        int n2 = this.page * 8;

        for (int i = 0; i < this.emoteWheelButtonArray.length; i++) {
            int n3 = n2 + i;
            EmoteWheelEntry emoteWheelEntry = n3 < this.emoteWheelEntryList.size() ? this.emoteWheelEntryList.get(n3) : null;
            this.emoteWheelEntryArray[i] = emoteWheelEntry;
            this.emoteWheelButtonArray[i] = new EmoteWheelButton(this, i, emoteWheelEntry);
        }
    }

    public int getIntValue() {
        return Math.max(1, (this.emoteWheelEntryList.size() + 8 - 1) / 8);
    }

    public float getFloatValue() {
        return Math.min(this.getScaledWidth(), this.getScaledHeight()) / 480.0F;
    }

    @Override
    public void updateScreen() throws java.io.IOException {
        super.updateScreen();
        this.intValue++;
    }

    @Override
    public void handleMouseInput() throws java.io.IOException {
        super.handleMouseInput();
        int eventDWheel = Mouse.getEventDWheel();
        if (eventDWheel != 0 && this.getIntValue() > 1) {
            this.selectPage(this.page + (eventDWheel > 0 ? -1 : 1));
        }
    }

    public EmoteWheelScreen(int n, List<EmoteWheelEntry> list) {
        Preconditions.checkNotNull(list, "options");
        this.emoteWheelEntryList = list;
        this.holdKey = n;
        this.selectPage(0);
    }
}
