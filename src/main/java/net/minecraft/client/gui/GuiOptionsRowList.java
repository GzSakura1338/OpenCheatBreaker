package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings.Options;

public class GuiOptionsRowList extends GuiListExtended {
   public List<GuiOptionsRowList.Row> entries = Lists.newArrayList();

   public GuiOptionsRowList(Minecraft mcIn, int n, int n2, int n3, int n4, int n5, Options... optionses) {
      super(mcIn, n, n2, n3, n4, n5);
      this.centerListVertically = false;

      for (int i = 0; i < optionses.length; i += 2) {
         Options options = optionses[i];
         Options options2 = i < optionses.length - 1 ? optionses[i + 1] : null;
         GuiButton createWidget = this.createWidget(mcIn, n / 2 - 155, 0, options);
         GuiButton createWidget2 = this.createWidget(mcIn, n / 2 - 155 + 160, 0, options2);
         this.entries.add(new GuiOptionsRowList.Row(createWidget, createWidget2));
      }
   }

   public GuiButton createWidget(Minecraft mcIn, int n, int n2, Options options) {
      if (options == null) {
         return null;
      }

      int returnEnumOrdinal = options.returnEnumOrdinal();
      return options.getEnumFloat()
         ? new GuiOptionSlider(returnEnumOrdinal, n, n2, options)
         : new GuiOptionButton(returnEnumOrdinal, n, n2, options, mcIn.gameSettings.getKeyBinding(options));
   }

   public GuiOptionsRowList.Row getListEntry(int index) {
      return this.entries.get(index);
   }

   @Override
   public int getSize() {
      return this.entries.size();
   }

   @Override
   public int getListWidth() {
      return 400;
   }

   @Override
   public int getScrollBarX() {
      return super.getScrollBarX() + 32;
   }

   public static class Row implements GuiListExtended.IGuiListEntry {
      public Minecraft client = Minecraft.getMinecraft();
      public GuiButton left;
      public GuiButton right;

      public Row(GuiButton guiButton, GuiButton guiButton2) {
         this.left = guiButton;
         this.right = guiButton2;
      }

      @Override
      public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
         if (this.left != null) {
            this.left.yPosition = y;
            this.left.drawButton(this.client, mouseX, mouseY);
         }

         if (this.right != null) {
            this.right.yPosition = y;
            this.right.drawButton(this.client, mouseX, mouseY);
         }
      }

      @Override
      public boolean mousePressed(int slotIndex, int n, int n2, int n3, int n4, int n5) {
         if (this.left.mousePressed(this.client, n, n2)) {
            if (this.left instanceof GuiOptionButton) {
               this.client.gameSettings.setOptionValue(((GuiOptionButton)this.left).returnEnumOptions(), 1);
               this.left.displayString = this.client.gameSettings.getKeyBinding(Options.getEnumOptions(this.left.id));
            }

            return true;
         } else if (this.right != null && this.right.mousePressed(this.client, n, n2)) {
            if (this.right instanceof GuiOptionButton) {
               this.client.gameSettings.setOptionValue(((GuiOptionButton)this.right).returnEnumOptions(), 1);
               this.right.displayString = this.client.gameSettings.getKeyBinding(Options.getEnumOptions(this.right.id));
            }

            return true;
         } else {
            return false;
         }
      }

      @Override
      public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
         if (this.left != null) {
            this.left.mouseReleased(x, y);
         }

         if (this.right != null) {
            this.right.mouseReleased(x, y);
         }
      }

      @Override
      public void setSelected(int n, int n2, int n3) {
      }
   }
}
