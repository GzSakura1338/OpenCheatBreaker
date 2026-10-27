package net.optifine.gui;

import java.awt.Rectangle;
import java.util.ArrayList;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.GameSettings.Options;
import net.optifine.Lang;

public class TooltipProviderOptions implements TooltipProvider {
   @Override
   public Rectangle getTooltipBounds(GuiScreen guiScreen, int n, int n2) {
      int n3 = guiScreen.width / 2 - 150;
      int n4 = guiScreen.height / 6 - 7;
      if (n2 <= n4 + 98) {
         n4 += 105;
      }

      int n5 = n3 + 150 + 150;
      int n6 = n4 + 84 + 10;
      return new Rectangle(n3, n4, n5 - n3, n6 - n4);
   }

   @Override
   public boolean isRenderBorder() {
      return false;
   }

   @Override
   public String[] getTooltipLines(GuiButton guiButton, int n) {
      if (!(guiButton instanceof IOptionControl)) {
         return null;
      }

      IOptionControl optionControl = (IOptionControl)guiButton;
      Options option = optionControl.getOption();
      return getTooltipLines(option.getEnumString());
   }

   public static String[] getTooltipLines(String str) {
      ArrayList list = new ArrayList();

      for (int i = 0; i < 10; i++) {
         String str2 = str + ".tooltip." + (i + 1);
         String str3 = Lang.get(str2, (String)null);
         if (str3 == null) {
            break;
         }

         list.add(str3);
      }

      return list.size() <= 0 ? null : (String[])list.toArray(new String[list.size()]);
   }
}
