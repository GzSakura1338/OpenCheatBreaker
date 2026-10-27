package net.optifine.gui;

import java.awt.Rectangle;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.optifine.shaders.config.EnumShaderOption;
import net.optifine.shaders.gui.GuiButtonDownloadShaders;
import net.optifine.shaders.gui.GuiButtonEnumShaderOption;

public class TooltipProviderEnumShaderOptions implements TooltipProvider {
   @Override
   public Rectangle getTooltipBounds(GuiScreen guiScreen, int n, int n2) {
      int n3 = guiScreen.width - 450;
      int n4 = 35;
      if (n3 < 10) {
         n3 = 10;
      }

      if (n2 <= n4 + 94) {
         n4 += 100;
      }

      int n5 = n3 + 150 + 150;
      int n6 = n4 + 84 + 10;
      return new Rectangle(n3, n4, n5 - n3, n6 - n4);
   }

   @Override
   public boolean isRenderBorder() {
      return true;
   }

   @Override
   public String[] getTooltipLines(GuiButton guiButton, int n) {
      if (guiButton instanceof GuiButtonDownloadShaders) {
         return TooltipProviderOptions.getTooltipLines("of.options.shaders.DOWNLOAD");
      }

      if (!(guiButton instanceof GuiButtonEnumShaderOption)) {
         return null;
      }

      GuiButtonEnumShaderOption guiButtonEnumShaderOption = (GuiButtonEnumShaderOption)guiButton;
      EnumShaderOption enumShaderOption = guiButtonEnumShaderOption.getEnumShaderOption();
      return this.getTooltipLines(enumShaderOption);
   }

   public String[] getTooltipLines(EnumShaderOption enumShaderOption) {
      return TooltipProviderOptions.getTooltipLines(enumShaderOption.getResourceKey());
   }
}
