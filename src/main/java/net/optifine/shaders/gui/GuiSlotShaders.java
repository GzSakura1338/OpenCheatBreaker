package net.optifine.shaders.gui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.resources.I18n;
import net.optifine.Config;
import net.optifine.Lang;
import net.optifine.shaders.IShaderPack;
import net.optifine.shaders.Shaders;
import net.optifine.util.ResUtils;

public class GuiSlotShaders extends GuiSlot {
   public ArrayList shaderslist;
   public int selectedIndex;
   public long lastClickedCached = 0L;
   public GuiShaders shadersGui;

   public GuiSlotShaders(GuiShaders guiShaders, int n, int n2, int n3, int n4, int n5) {
      super(guiShaders.getMc(), n, n2, n3, n4, n5);
      this.shadersGui = guiShaders;
      this.updateList();
      this.amountScrolled = 0.0F;
      int n6 = this.selectedIndex * n5;
      int n7 = (n4 - n3) / 2;
      if (n6 > n7) {
         this.scrollBy(n6 - n7);
      }
   }

   @Override
   public int getListWidth() {
      return this.width - 20;
   }

   public void updateList() {
      this.shaderslist = Shaders.listOfShaders();
      this.selectedIndex = 0;
      int n = 0;

      for (int size = this.shaderslist.size(); n < size; n++) {
         if (((String)this.shaderslist.get(n)).equals(Shaders.currentShaderName)) {
            this.selectedIndex = n;
            break;
         }
      }
   }

   @Override
   public int getSize() {
      return this.shaderslist.size();
   }

   @Override
   public void elementClicked(int n, boolean flag, int n2, int n3) {
      if (n != this.selectedIndex || this.lastClicked != this.lastClickedCached) {
         String str = (String)this.shaderslist.get(n);
         IShaderPack shaderPack = Shaders.getShaderPack(str);
         if (this.checkCompatible(shaderPack, n)) {
            this.selectIndex(n);
         }
      }
   }

   public void selectIndex(int n) {
      this.selectedIndex = n;
      this.lastClickedCached = this.lastClicked;
      Shaders.setShaderPack((String)this.shaderslist.get(n));
      Shaders.uninit();
      this.shadersGui.updateButtons();
   }

   public boolean checkCompatible(IShaderPack shaderPack, final int n) {
      if (shaderPack == null) {
         return true;
      }

      InputStream resourceAsStream = shaderPack.getResourceAsStream("/shaders/shaders.properties");
      Properties readProperties = ResUtils.readProperties(resourceAsStream, "Shaders");
      if (readProperties == null) {
         return true;
      }

      String str = "version.1.8.9";
      String property = readProperties.getProperty(str);
      if (property == null) {
         return true;
      }

      property = property.trim();
      String str2 = "M6_pre2";
      int compareRelease = Config.compareRelease(str2, property);
      if (compareRelease >= 0) {
         return true;
      }

      String replace = ("HD_U_" + property).replace('_', ' ');
      String format = I18n.format("of.message.shaders.nv1", replace);
      String format2 = I18n.format("of.message.shaders.nv2");
      GuiYesNoCallback guiYesNoCallback = new GuiYesNoCallback() {
         @Override
         public void confirmClicked(boolean flag, int n2) {
            if (flag) {
               GuiSlotShaders.this.selectIndex(n);
            }

            GuiSlotShaders.this.mc.displayGuiScreen(GuiSlotShaders.this.shadersGui);
         }
      };
      GuiYesNo guiYesNo = new GuiYesNo(guiYesNoCallback, format, format2, 0);
      this.mc.displayGuiScreen(guiYesNo);
      return false;
   }

   @Override
   public boolean isSelected(int n) {
      return n == this.selectedIndex;
   }

   @Override
   public int getScrollBarX() {
      return this.width - 6;
   }

   @Override
   public int getContentHeight() {
      return this.getSize() * 18;
   }

   @Override
   public void drawBackground() {
   }

   @Override
   public void drawSlot(int n, int n2, int n3, int n4, int n5, int n6) {
      String str = (String)this.shaderslist.get(n);
      if (str.equals("OFF")) {
         str = Lang.get("of.options.shaders.packNone");
      } else if (str.equals("(internal)")) {
         str = Lang.get("of.options.shaders.packDefault");
      }

      this.shadersGui.drawCenteredString(str, this.width / 2, n3 + 1, 14737632);
   }

   public int getSelectedIndex() {
      return this.selectedIndex;
   }
}
