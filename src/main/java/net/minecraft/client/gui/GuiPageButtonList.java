package net.minecraft.client.gui;

import com.google.common.base.MoreObjects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.IntHashMap;

public class GuiPageButtonList extends GuiListExtended {
   public List<GuiPageButtonList.GuiEntry> widgetEntries = Lists.newArrayList();
   public IntHashMap<Gui> widgets = new IntHashMap<>();
   public List<GuiTextField> textFields = Lists.newArrayList();
   public GuiPageButtonList.GuiResponder listener;
   public int currentPageId;
   public GuiPageButtonList.GuiListEntry[][] pages;
   public Gui currentWidget;

   public GuiPageButtonList(
      Minecraft mcIn,
      int widthIn,
      int heightIn,
      int topIn,
      int bottomIn,
      int slotHeightIn,
      GuiPageButtonList.GuiResponder guiResponder,
      GuiPageButtonList.GuiListEntry[]... guiListEntrieses
   ) {
      super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
      this.listener = guiResponder;
      this.pages = guiListEntrieses;
      this.centerListVertically = false;
      this.initWidgets();
      this.initWidgetEntries();
   }

   public void initWidgets() {
      for (GuiPageButtonList.GuiListEntry[] guiListEntries : this.pages) {
         for (int i = 0; i < guiListEntries.length; i += 2) {
            GuiPageButtonList.GuiListEntry guiListEntry = guiListEntries[i];
            GuiPageButtonList.GuiListEntry guiListEntry2 = i < guiListEntries.length - 1 ? guiListEntries[i + 1] : null;
            Gui createWidget = this.createWidget(guiListEntry, 0, guiListEntry2 == null);
            Gui createWidget2 = this.createWidget(guiListEntry2, 160, guiListEntry == null);
            GuiPageButtonList.GuiEntry guiEntry = new GuiPageButtonList.GuiEntry(createWidget, createWidget2);
            this.widgetEntries.add(guiEntry);
            if (guiListEntry != null && createWidget != null) {
               this.widgets.addKey(guiListEntry.getId(), createWidget);
               if (createWidget instanceof GuiTextField) {
                  this.textFields.add((GuiTextField)createWidget);
               }
            }

            if (guiListEntry2 != null && createWidget2 != null) {
               this.widgets.addKey(guiListEntry2.getId(), createWidget2);
               if (createWidget2 instanceof GuiTextField) {
                  this.textFields.add((GuiTextField)createWidget2);
               }
            }
         }
      }
   }

   public void initWidgetEntries() {
      this.widgetEntries.clear();

      for (int i = 0; i < this.pages[this.currentPageId].length; i += 2) {
         GuiPageButtonList.GuiListEntry guiListEntry = this.pages[this.currentPageId][i];
         GuiPageButtonList.GuiListEntry guiListEntry2 = i < this.pages[this.currentPageId].length - 1 ? this.pages[this.currentPageId][i + 1] : null;
         Gui lookup = this.widgets.lookup(guiListEntry.getId());
         Gui gui = guiListEntry2 != null ? this.widgets.lookup(guiListEntry2.getId()) : null;
         GuiPageButtonList.GuiEntry guiEntry = new GuiPageButtonList.GuiEntry(lookup, gui);
         this.widgetEntries.add(guiEntry);
      }
   }

   public void setCurrentPage(int n) {
      if (n != this.currentPageId) {
         int currentPageId = this.currentPageId;
         this.currentPageId = n;
         this.initWidgetEntries();
         this.updateWidgetVisibility(currentPageId, n);
         this.amountScrolled = 0.0F;
      }
   }

   public int getCurrentPageId() {
      return this.currentPageId;
   }

   public int getMaxPages() {
      return this.pages.length;
   }

   public Gui getCurrentWidget() {
      return this.currentWidget;
   }

   public void previousPage() {
      if (this.currentPageId > 0) {
         this.setCurrentPage(this.currentPageId - 1);
      }
   }

   public void nextPage() {
      if (this.currentPageId < this.pages.length - 1) {
         this.setCurrentPage(this.currentPageId + 1);
      }
   }

   public Gui getWidget(int n) {
      return this.widgets.lookup(n);
   }

   public void updateWidgetVisibility(int n, int n2) {
      for (GuiPageButtonList.GuiListEntry guiListEntry : this.pages[n]) {
         if (guiListEntry != null) {
            this.setVisible(this.widgets.lookup(guiListEntry.getId()), false);
         }
      }

      for (GuiPageButtonList.GuiListEntry guiListEntry : this.pages[n2]) {
         if (guiListEntry != null) {
            this.setVisible(this.widgets.lookup(guiListEntry.getId()), true);
         }
      }
   }

   public void setVisible(Gui gui, boolean flag) {
      if (gui instanceof GuiButton) {
         ((GuiButton)gui).visible = flag;
      } else if (gui instanceof GuiTextField) {
         ((GuiTextField)gui).setVisible(flag);
      } else if (gui instanceof GuiLabel) {
         ((GuiLabel)gui).visible = flag;
      }
   }

   public Gui createWidget(GuiPageButtonList.GuiListEntry guiListEntry, int n, boolean flag) {
      return guiListEntry instanceof GuiPageButtonList.GuiSlideEntry
         ? this.createSliderWidget(this.width / 2 - 155 + n, 0, (GuiPageButtonList.GuiSlideEntry)guiListEntry)
         : (
            guiListEntry instanceof GuiPageButtonList.GuiButtonEntry
               ? this.createSwitchWidget(this.width / 2 - 155 + n, 0, (GuiPageButtonList.GuiButtonEntry)guiListEntry)
               : (
                  guiListEntry instanceof GuiPageButtonList.EditBoxEntry
                     ? this.createTextWidget(this.width / 2 - 155 + n, 0, (GuiPageButtonList.EditBoxEntry)guiListEntry)
                     : (
                        guiListEntry instanceof GuiPageButtonList.GuiLabelEntry
                           ? this.createLabelWidget(this.width / 2 - 155 + n, 0, (GuiPageButtonList.GuiLabelEntry)guiListEntry, flag)
                           : null
                     )
               )
         );
   }

   public void setActive(boolean flag) {
      for (GuiPageButtonList.GuiEntry guiEntry : this.widgetEntries) {
         if (guiEntry.first instanceof GuiButton) {
            ((GuiButton)guiEntry.first).enabled = flag;
         }

         if (guiEntry.second instanceof GuiButton) {
            ((GuiButton)guiEntry.second).enabled = flag;
         }
      }
   }

   @Override
   public boolean mouseClicked(int mouseX, int mouseY, int mouseEvent) {
      boolean mouseClicked = super.mouseClicked(mouseX, mouseY, mouseEvent);
      int slotIndexFromScreenCoords = this.getSlotIndexFromScreenCoords(mouseX, mouseY);
      if (slotIndexFromScreenCoords >= 0) {
         GuiPageButtonList.GuiEntry listEntry = this.getListEntry(slotIndexFromScreenCoords);
         if (this.currentWidget != listEntry.prevClicked && this.currentWidget != null && this.currentWidget instanceof GuiTextField) {
            ((GuiTextField)this.currentWidget).setFocused(false);
         }

         this.currentWidget = listEntry.prevClicked;
      }

      return mouseClicked;
   }

   public GuiSlider createSliderWidget(int n, int n2, GuiPageButtonList.GuiSlideEntry guiSlideEntry) {
      GuiSlider guiSlider = new GuiSlider(
         this.listener, guiSlideEntry.getId(), n, n2, guiSlideEntry.getLabel(), guiSlideEntry.getMin(), guiSlideEntry.getMax(), guiSlideEntry.getCurrentValue(), guiSlideEntry.getLabelSupplier()
      );
      guiSlider.visible = guiSlideEntry.isVisible();
      return guiSlider;
   }

   public GuiListButton createSwitchWidget(int n, int n2, GuiPageButtonList.GuiButtonEntry guiButtonEntry) {
      GuiListButton guiListButton = new GuiListButton(this.listener, guiButtonEntry.getId(), n, n2, guiButtonEntry.getLabel(), guiButtonEntry.isValueEnabled());
      guiListButton.visible = guiButtonEntry.isVisible();
      return guiListButton;
   }

   public GuiTextField createTextWidget(int n, int n2, GuiPageButtonList.EditBoxEntry editBoxEntry) {
      GuiTextField guiTextField = new GuiTextField(editBoxEntry.getId(), this.mc.fontRendererObj, n, n2, 150, 20);
      guiTextField.setText(editBoxEntry.getLabel());
      guiTextField.setListener(this.listener);
      guiTextField.setVisible(editBoxEntry.isVisible());
      guiTextField.setValidator(editBoxEntry.getPredicate());
      return guiTextField;
   }

   public GuiLabel createLabelWidget(int n, int n2, GuiPageButtonList.GuiLabelEntry guiLabelEntry, boolean flag) {
      GuiLabel guiLabel;
      if (flag) {
         guiLabel = new GuiLabel(this.mc.fontRendererObj, guiLabelEntry.getId(), n, n2, this.width - n * 2, 20, -1);
      } else {
         guiLabel = new GuiLabel(this.mc.fontRendererObj, guiLabelEntry.getId(), n, n2, 150, 20, -1);
      }

      guiLabel.visible = guiLabelEntry.isVisible();
      guiLabel.addLine(guiLabelEntry.getLabel());
      guiLabel.setCentered();
      return guiLabel;
   }

   public void updateText(char c, int n) {
      if (this.currentWidget instanceof GuiTextField) {
         GuiTextField currentWidget = (GuiTextField)this.currentWidget;
         if (!GuiScreen.isKeyComboCtrlV(n)) {
            if (n == 15) {
               currentWidget.setFocused(false);
               int indexOf = this.textFields.indexOf(this.currentWidget);
               if (GuiScreen.isShiftKeyDown()) {
                  if (indexOf == 0) {
                     indexOf = this.textFields.size() - 1;
                  } else {
                     indexOf--;
                  }
               } else if (indexOf == this.textFields.size() - 1) {
                  indexOf = 0;
               } else {
                  indexOf++;
               }

               this.currentWidget = this.textFields.get(indexOf);
               currentWidget = (GuiTextField)this.currentWidget;
               currentWidget.setFocused(true);
               int n2 = currentWidget.yPosition + this.slotHeight;
               int yPosition = currentWidget.yPosition;
               if (n2 > this.bottom) {
                  this.amountScrolled = this.amountScrolled + (n2 - this.bottom);
               } else if (yPosition < this.top) {
                  this.amountScrolled = yPosition;
               }
            } else {
               currentWidget.textboxKeyTyped(c, n);
            }
         } else {
            String clipboardString = GuiScreen.getClipboardString();
            String[] split = clipboardString.split(";");
            int indexOf = this.textFields.indexOf(this.currentWidget);
            int n2 = indexOf;

            for (String str : split) {
               this.textFields.get(n2).setText(str);
               if (n2 == this.textFields.size() - 1) {
                  n2 = 0;
               } else {
                  n2++;
               }

               if (n2 == indexOf) {
                  break;
               }
            }
         }
      }
   }

   public GuiPageButtonList.GuiEntry getListEntry(int index) {
      return this.widgetEntries.get(index);
   }

   @Override
   public int getSize() {
      return this.widgetEntries.size();
   }

   @Override
   public int getListWidth() {
      return 400;
   }

   @Override
   public int getScrollBarX() {
      return super.getScrollBarX() + 32;
   }

   public static class EditBoxEntry extends GuiPageButtonList.GuiListEntry {
      public Predicate<String> predicate;

      public EditBoxEntry(int n, String str, boolean flag, Predicate<String> predicate) {
         super(n, str, flag);
         this.predicate = (Predicate<String>)MoreObjects.firstNonNull(predicate, Predicates.alwaysTrue());
      }

      public Predicate<String> getPredicate() {
         return this.predicate;
      }
   }

   public static class GuiButtonEntry extends GuiPageButtonList.GuiListEntry {
      public boolean valueEnabled;

      public GuiButtonEntry(int n, String str, boolean flag, boolean flag2) {
         super(n, str, flag);
         this.valueEnabled = flag2;
      }

      public boolean isValueEnabled() {
         return this.valueEnabled;
      }
   }

   public static class GuiEntry implements GuiListExtended.IGuiListEntry {
      public Minecraft client = Minecraft.getMinecraft();
      public Gui first;
      public Gui second;
      public Gui prevClicked;

      public GuiEntry(Gui gui, Gui gui2) {
         this.first = gui;
         this.second = gui2;
      }

      public Gui getFirst() {
         return this.first;
      }

      public Gui getSecond() {
         return this.second;
      }

      @Override
      public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
         this.renderDrawable(this.first, y, mouseX, mouseY, false);
         this.renderDrawable(this.second, y, mouseX, mouseY, false);
      }

      public void renderDrawable(Gui gui, int n, int n2, int n3, boolean flag) {
         if (gui != null) {
            if (gui instanceof GuiButton) {
               this.renderButton((GuiButton)gui, n, n2, n3, flag);
            } else if (gui instanceof GuiTextField) {
               this.renderTextField((GuiTextField)gui, n, flag);
            } else if (gui instanceof GuiLabel) {
               this.renderLabel((GuiLabel)gui, n, n2, n3, flag);
            }
         }
      }

      public void renderButton(GuiButton guiButton, int n, int n2, int n3, boolean flag) {
         guiButton.yPosition = n;
         if (!flag) {
            guiButton.drawButton(this.client, n2, n3);
         }
      }

      public void renderTextField(GuiTextField guiTextField, int n, boolean flag) {
         guiTextField.yPosition = n;
         if (!flag) {
            guiTextField.drawTextBox();
         }
      }

      public void renderLabel(GuiLabel guiLabel, int n, int n2, int n3, boolean flag) {
         guiLabel.y = n;
         if (!flag) {
            guiLabel.drawLabel(this.client, n2, n3);
         }
      }

      @Override
      public void setSelected(int n, int n2, int n3) {
         this.renderDrawable(this.first, n3, 0, 0, true);
         this.renderDrawable(this.second, n3, 0, 0, true);
      }

      @Override
      public boolean mousePressed(int slotIndex, int n, int n2, int n3, int n4, int n5) {
         boolean drawableClicked = this.drawableClicked(this.first, n, n2, n3);
         boolean drawableClicked2 = this.drawableClicked(this.second, n, n2, n3);
         return drawableClicked || drawableClicked2;
      }

      public boolean drawableClicked(Gui gui, int n, int n2, int n3) {
         if (gui == null) {
            return false;
         }

         if (gui instanceof GuiButton) {
            return this.buttonClicked((GuiButton)gui, n, n2, n3);
         }

         if (gui instanceof GuiTextField) {
            this.textFieldClicked((GuiTextField)gui, n, n2, n3);
         }

         return false;
      }

      public boolean buttonClicked(GuiButton guiButton, int n, int n2, int n3) {
         boolean mousePressed = guiButton.mousePressed(this.client, n, n2);
         if (mousePressed) {
            this.prevClicked = guiButton;
         }

         return mousePressed;
      }

      public void textFieldClicked(GuiTextField guiTextField, int n, int n2, int n3) {
         guiTextField.mouseClicked(n, n2, n3);
         if (guiTextField.isFocused()) {
            this.prevClicked = guiTextField;
         }
      }

      @Override
      public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
         this.drawableReleased(this.first, x, y, mouseEvent);
         this.drawableReleased(this.second, x, y, mouseEvent);
      }

      public void drawableReleased(Gui gui, int n, int n2, int n3) {
         if (gui != null && gui instanceof GuiButton) {
            this.buttonReleased((GuiButton)gui, n, n2, n3);
         }
      }

      public void buttonReleased(GuiButton guiButton, int n, int n2, int n3) {
         guiButton.mouseReleased(n, n2);
      }
   }

   public static class GuiLabelEntry extends GuiPageButtonList.GuiListEntry {
      public GuiLabelEntry(int n, String str, boolean flag) {
         super(n, str, flag);
      }
   }

   public static class GuiListEntry {
      public int id;
      public String label;
      public boolean visible;

      public GuiListEntry(int n, String str, boolean flag) {
         this.id = n;
         this.label = str;
         this.visible = flag;
      }

      public int getId() {
         return this.id;
      }

      public String getLabel() {
         return this.label;
      }

      public boolean isVisible() {
         return this.visible;
      }
   }

   public interface GuiResponder {
      void setBooleanValue(int n, boolean flag);

      void onTick(int n, float f);

      void setStringValue(int n, String str);
   }

   public static class GuiSlideEntry extends GuiPageButtonList.GuiListEntry {
      public GuiSlider.FormatHelper labelSupplier;
      public float min;
      public float max;
      public float currentValue;

      public GuiSlideEntry(int n, String str, boolean flag, GuiSlider.FormatHelper formatHelper, float f, float f2, float f3) {
         super(n, str, flag);
         this.labelSupplier = formatHelper;
         this.min = f;
         this.max = f2;
         this.currentValue = f3;
      }

      public GuiSlider.FormatHelper getLabelSupplier() {
         return this.labelSupplier;
      }

      public float getMin() {
         return this.min;
      }

      public float getMax() {
         return this.max;
      }

      public float getCurrentValue() {
         return this.currentValue;
      }
   }
}
