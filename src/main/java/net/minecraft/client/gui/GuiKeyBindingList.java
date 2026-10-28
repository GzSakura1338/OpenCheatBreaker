package net.minecraft.client.gui;

import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.ArrayUtils;

public class GuiKeyBindingList extends GuiListExtended {
    public int maxListLabelWidth = 0;
    public GuiControls parent;
    public Minecraft mc;
    public IGuiListEntry[] listEntries;

    public GuiKeyBindingList(GuiControls guiControls, Minecraft mc) {
        super(mc, guiControls.width, guiControls.height, 63, guiControls.height - 32, 20);
        this.parent = guiControls;
        this.mc = mc;
        KeyBinding[] keyBindings = (KeyBinding[])ArrayUtils.clone(mc.gameSettings.keyBindings);
        this.listEntries = new IGuiListEntry[keyBindings.length + KeyBinding.getKeybinds().size()];
        Arrays.sort(keyBindings);
        int n = 0;
        String str = null;
        KeyBinding[] keyBindings2 = keyBindings;
        int length = keyBindings.length;

        for (int i = 0; i < length; i++) {
            KeyBinding keyBinding = keyBindings2[i];
            String keyCategory = keyBinding.getKeyCategory();
            if (!keyCategory.equals(str)) {
                str = keyCategory;
                this.listEntries[n++] = new GuiKeyBindingList.CategoryEntry(keyCategory);
            }

            int stringWidth = mc.fontRendererObj.getStringWidth(I18n.format(keyBinding.getKeyDescription()));
            if (stringWidth > this.maxListLabelWidth) {
                this.maxListLabelWidth = stringWidth;
            }

            this.listEntries[n++] = new GuiKeyBindingList.KeyEntry(keyBinding, null);
        }
    }

    @Override
    public int getSize() {
        return this.listEntries.length;
    }

    @Override
    public IGuiListEntry getListEntry(int n) {
        return this.listEntries[n];
    }

    @Override
    public int getScrollBarX() {
        return super.getScrollBarX() + 15;
    }

    @Override
    public int getListWidth() {
        return super.getListWidth() + 32;
    }

    public class CategoryEntry implements IGuiListEntry {
        public String labelText;
        public int labelWidth;

        public CategoryEntry(String str) {
            this.labelText = I18n.format(str);
            this.labelWidth = GuiKeyBindingList.this.mc.fontRendererObj.getStringWidth(this.labelText);
        }

        @Override
        public void drawEntry(int n, int n2, int n3, int n4, int n5, Tessellator tessellator, int n6, int n7, boolean flag) {
            GuiKeyBindingList.this.mc
                .fontRendererObj
                .drawString(
                    this.labelText,
                    GuiKeyBindingList.this.mc.currentScreen.width / 2 - this.labelWidth / 2,
                    n3 + n5 - GuiKeyBindingList.this.mc.fontRendererObj.FONT_HEIGHT - 1,
                    16777215
                );
        }

        @Override
        public boolean mousePressed(int n, int n2, int n3, int n4, int n5, int n6) {
            return false;
        }

        @Override
        public void mouseReleased(int n, int n2, int n3, int n4, int n5, int n6) {
        }
    }

    public class KeyEntry implements IGuiListEntry {
        public KeyBinding keyBinding;
        public String text;
        public GuiButton btnChangeKeyBinding;
        public GuiButton btnReset;

        public KeyEntry(KeyBinding keyBinding) {
            this.keyBinding = keyBinding;
            this.text = I18n.format(keyBinding.getKeyDescription());
            this.btnChangeKeyBinding = new GuiButton(0, 0, 0, 75, 18, I18n.format(keyBinding.getKeyDescription()));
            this.btnReset = new GuiButton(0, 0, 0, 50, 18, I18n.format("controls.reset"));
        }

        @Override
        public void drawEntry(int n, int n2, int n3, int n4, int n5, Tessellator tessellator, int n6, int n7, boolean flag) throws java.io.IOException {
            boolean flag2 = GuiKeyBindingList.this.parent.buttonId == this.keyBinding;
            GuiKeyBindingList.this.mc
                .fontRendererObj
                .drawString(
                    this.text,
                    n2 + 90 - GuiKeyBindingList.this.maxListLabelWidth,
                    n3 + n5 / 2 - GuiKeyBindingList.this.mc.fontRendererObj.FONT_HEIGHT / 2,
                    16777215
                );
            this.btnReset.xPosition = n2 + 190;
            this.btnReset.yPosition = n3;
            this.btnReset.enabled = this.keyBinding.getKeyCode() != this.keyBinding.getKeyCodeDefault() || !this.keyBinding.check();
            Gui.floatValue = 0.0F;
            this.btnReset.drawButton(GuiKeyBindingList.this.mc, n6, n7);
            this.btnChangeKeyBinding.xPosition = n2 + 105;
            this.btnChangeKeyBinding.yPosition = n3;
            String keyDisplayString = GameSettings.getKeyDisplayString(this.keyBinding.getKeyCode());
            if (this.keyBinding.getKeyCode() != 0) {
                if (this.keyBinding.flag5) {
                    keyDisplayString = "Ctrl+" + keyDisplayString;
                }

                if (this.keyBinding.flag6) {
                    keyDisplayString = "Shift+" + keyDisplayString;
                }

                if (this.keyBinding.flag7) {
                    keyDisplayString = "Alt+" + keyDisplayString;
                }
            }

            this.btnChangeKeyBinding.displayString = keyDisplayString.toUpperCase();
            boolean flag3 = false;
            if (this.keyBinding.getKeyCode() != 0) {
                for (KeyBinding keyBinding : GuiKeyBindingList.this.mc.gameSettings.keyBindings) {
                    if (keyBinding != this.keyBinding
                        && keyBinding.getKeyCode() == this.keyBinding.getKeyCode()
                        && keyBinding.flag6 == this.keyBinding.flag6
                        && keyBinding.flag5 == this.keyBinding.flag5
                        && keyBinding.flag7 == this.keyBinding.flag7) {
                        flag3 = true;
                        break;
                    }
                }
            }

            if (flag2) {
                this.btnChangeKeyBinding.displayString = EnumChatFormatting.WHITE
                    + "> "
                    + EnumChatFormatting.YELLOW
                    + this.btnChangeKeyBinding.displayString
                    + EnumChatFormatting.WHITE
                    + " <";
            } else if (flag3) {
                this.btnChangeKeyBinding.displayString = EnumChatFormatting.RED + this.btnChangeKeyBinding.displayString;
            }

            this.btnChangeKeyBinding.drawButton(GuiKeyBindingList.this.mc, n6, n7);
        }

        @Override
        public boolean mousePressed(int n, int n2, int n3, int n4, int n5, int n6) throws java.io.IOException {
            if (this.btnChangeKeyBinding.mousePressed(GuiKeyBindingList.this.mc, n2, n3)) {
                GuiKeyBindingList.this.parent.buttonId = this.keyBinding;
                return true;
            } else if (this.btnReset.mousePressed(GuiKeyBindingList.this.mc, n2, n3)) {
                GuiKeyBindingList.this.mc.gameSettings.setOptionKeyBinding(this.keyBinding, this.keyBinding.getKeyCodeDefault());
                this.keyBinding.run();
                KeyBinding.resetKeyBindingArrayAndHash();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void mouseReleased(int n, int n2, int n3, int n4, int n5, int n6) {
            this.btnChangeKeyBinding.mouseReleased(n2, n3);
            this.btnReset.mouseReleased(n2, n3);
        }

        public KeyEntry(KeyBinding keyBinding, Object obj) {
            this(keyBinding);
        }
    }
}
