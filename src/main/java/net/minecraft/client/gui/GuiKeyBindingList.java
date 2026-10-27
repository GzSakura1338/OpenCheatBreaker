package net.minecraft.client.gui;

import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.ArrayUtils;

public class GuiKeyBindingList extends GuiListExtended {
   public int maxListLabelWidth = 0;
   public GuiControls parent;
   public Minecraft mc;
   public GuiListExtended.IGuiListEntry[] listEntries;

   public GuiKeyBindingList(GuiControls controls, Minecraft mcIn) {
      super(mcIn, controls.width, controls.height, 63, controls.height - 32, 20);
      this.parent = controls;
      this.mc = mcIn;
      KeyBinding[] keyBindings = (KeyBinding[])ArrayUtils.clone(mcIn.gameSettings.keyBindings);
      this.listEntries = new GuiListExtended.IGuiListEntry[keyBindings.length + KeyBinding.getKeybinds().size()];
      Arrays.sort(keyBindings);
      int n = 0;
      String str = null;

      for (KeyBinding keyBinding : keyBindings) {
         String keyCategory = keyBinding.getKeyCategory();
         if (!keyCategory.equals(str)) {
            str = keyCategory;
            this.listEntries[n++] = new GuiKeyBindingList.CategoryEntry(keyCategory);
         }

         int stringWidth = mcIn.fontRendererObj.getStringWidth(I18n.format(keyBinding.getKeyDescription()));
         if (stringWidth > this.maxListLabelWidth) {
            this.maxListLabelWidth = stringWidth;
         }

         this.listEntries[n++] = new GuiKeyBindingList.KeyEntry(keyBinding);
      }
   }

   @Override
   public int getSize() {
      return this.listEntries.length;
   }

   @Override
   public GuiListExtended.IGuiListEntry getListEntry(int index) {
      return this.listEntries[index];
   }

   @Override
   public int getScrollBarX() {
      return super.getScrollBarX() + 15;
   }

   @Override
   public int getListWidth() {
      return super.getListWidth() + 32;
   }

   public class CategoryEntry implements GuiListExtended.IGuiListEntry {
      public String labelText;
      public int labelWidth;

      public CategoryEntry(String str) {
         this.labelText = I18n.format(str);
         this.labelWidth = GuiKeyBindingList.this.mc.fontRendererObj.getStringWidth(this.labelText);
      }

      @Override
      public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
         GuiKeyBindingList.this.mc
            .fontRendererObj
            .drawString(
               this.labelText,
               GuiKeyBindingList.this.mc.currentScreen.width / 2 - this.labelWidth / 2,
               y + slotHeight - GuiKeyBindingList.this.mc.fontRendererObj.FONT_HEIGHT - 1,
               16777215
            );
      }

      @Override
      public boolean mousePressed(int slotIndex, int n, int n2, int n3, int n4, int n5) {
         return false;
      }

      @Override
      public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
      }

      @Override
      public void setSelected(int n, int n2, int n3) {
      }
   }

   public class KeyEntry implements GuiListExtended.IGuiListEntry {
      public KeyBinding keybinding;
      public String keyDesc;
      public GuiButton btnChangeKeyBinding;
      public GuiButton btnReset;

      public KeyEntry(KeyBinding keyBinding) {
         this.keybinding = keyBinding;
         this.keyDesc = I18n.format(keyBinding.getKeyDescription());
         this.btnChangeKeyBinding = new GuiButton(0, 0, 0, 75, 20, I18n.format(keyBinding.getKeyDescription()));
         this.btnReset = new GuiButton(0, 0, 0, 50, 20, I18n.format("controls.reset"));
      }

      @Override
      public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
         boolean flag = GuiKeyBindingList.this.parent.buttonId == this.keybinding;
         GuiKeyBindingList.this.mc
            .fontRendererObj
            .drawString(
               this.keyDesc,
               x + 90 - GuiKeyBindingList.this.maxListLabelWidth,
               y + slotHeight / 2 - GuiKeyBindingList.this.mc.fontRendererObj.FONT_HEIGHT / 2,
               16777215
            );
         this.btnReset.xPosition = x + 190;
         this.btnReset.yPosition = y;
         this.btnReset.enabled = this.keybinding.getKeyCode() != this.keybinding.getKeyCodeDefault() || !this.keybinding.check();
         this.btnReset.drawButton(GuiKeyBindingList.this.mc, mouseX, mouseY);
         this.btnChangeKeyBinding.xPosition = x + 105;
         this.btnChangeKeyBinding.yPosition = y;
         String keyDisplayString = GameSettings.getKeyDisplayString(this.keybinding.getKeyCode());
         if (this.keybinding.getKeyCode() != 0) {
            if (this.keybinding.flag5) {
               keyDisplayString = "Ctrl+" + keyDisplayString;
            }

            if (this.keybinding.flag7) {
               keyDisplayString = "Shift+" + keyDisplayString;
            }

            if (this.keybinding.flag) {
               keyDisplayString = "Alt+" + keyDisplayString;
            }
         }

         this.btnChangeKeyBinding.displayString = keyDisplayString.toUpperCase();
         boolean flag2 = false;
         if (this.keybinding.getKeyCode() != 0) {
            for (KeyBinding keyBinding : GuiKeyBindingList.this.mc.gameSettings.keyBindings) {
               if (keyBinding != this.keybinding
                  && keyBinding.getKeyCode() == this.keybinding.getKeyCode()
                  && keyBinding.flag7 == this.keybinding.flag7
                  && keyBinding.flag5 == this.keybinding.flag5
                  && keyBinding.flag == this.keybinding.flag) {
                  flag2 = true;
                  break;
               }
            }
         }

         if (flag) {
            this.btnChangeKeyBinding.displayString = EnumChatFormatting.WHITE
               + "> "
               + EnumChatFormatting.YELLOW
               + this.btnChangeKeyBinding.displayString
               + EnumChatFormatting.WHITE
               + " <";
         } else if (flag2) {
            this.btnChangeKeyBinding.displayString = EnumChatFormatting.RED + this.btnChangeKeyBinding.displayString;
         }

         this.btnChangeKeyBinding.drawButton(GuiKeyBindingList.this.mc, mouseX, mouseY);
      }

      @Override
      public boolean mousePressed(int slotIndex, int n, int n2, int n3, int n4, int n5) {
         if (this.btnChangeKeyBinding.mousePressed(GuiKeyBindingList.this.mc, n, n2)) {
            GuiKeyBindingList.this.parent.buttonId = this.keybinding;
            return true;
         } else if (this.btnReset.mousePressed(GuiKeyBindingList.this.mc, n, n2)) {
            GuiKeyBindingList.this.mc.gameSettings.setOptionKeyBinding(this.keybinding, this.keybinding.getKeyCodeDefault());
            this.keybinding.run();
            KeyBinding.resetKeyBindingArrayAndHash();
            return true;
         } else {
            return false;
         }
      }

      @Override
      public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
         this.btnChangeKeyBinding.mouseReleased(x, y);
         this.btnReset.mouseReleased(x, y);
      }

      @Override
      public void setSelected(int n, int n2, int n3) {
      }
   }
}
