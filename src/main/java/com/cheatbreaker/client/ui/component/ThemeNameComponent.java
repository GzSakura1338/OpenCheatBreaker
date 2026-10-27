package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.theme.Theme;
import com.cheatbreaker.client.util.ClientVersion;
import java.io.File;
import net.minecraft.client.Minecraft;

public class ThemeNameComponent extends SettingComponent {
   public TextField textField;
   public Theme theme2;

   @Override
   public void setBounds(int n, int n2, int n3, int n4) {
      super.setBounds(n, n2, n3, n4);
      this.textField.setBounds(this.x + 170, n2 + n4 - 17.0F, 150.0F, 15.0F);
   }

   public ThemeNameComponent(Setting setting, Theme theme, float f) {
      super(f);
      this.setting = setting;
      this.textField = new TextField(
         CheatBreaker.getInstance().getFontManager().playRegular16,
         theme.getThemeNameValue(),
         this.theme.getButtonBackgroundColorValue(),
         this.theme.getPrimaryTextColorValue()
      );
      this.textField.setMaxStringLength(256);
      this.height = 18;
      this.theme2 = theme;
   }

   public ThemeNameComponent(Setting setting, float f) {
      super(f);
      this.setting = setting;
      this.textField = new TextField(
         CheatBreaker.getInstance().getFontManager().playRegular16,
         this.setting.getValue().toString(),
         this.theme.getButtonBackgroundColorValue(),
         this.theme.getPrimaryTextColorValue()
      );
      this.textField.setMaxStringLength(256);
      this.height = 18;
   }

   @Override
   public void render(int n, int n2, float f) {
      CheatBreaker.getInstance()
         .getFontManager()
         .ubuntuM16
         .drawString(this.setting.getName().toUpperCase(), this.x + 10, this.y + 4, this.theme.getTextColor2Value());
      this.textField.renderAndTestHover(n, n2, true);
   }

   @Override
   public void tick() {
      this.textField.tick();
   }

   @Override
   public void mouseClicked(int n, int n2, int n3) {
      this.textField.setFocused(this.textField.isMouseOverScaled(n, n2));
   }

   @Override
   public void keyTyped(char c, int n) {
      if (this.textField.isFocused()) {
         this.textField.keyTyped(c, n);
         if (n == 28) {
            this.textField.setFocused(false);
            if (this.theme2 != null) {
               File file = new File(
                  Minecraft.getMinecraft().mcDataDir
                     + File.separator
                     + "config"
                     + File.separator
                     + "cheatbreaker-client-"
                     + ClientVersion.text
                     + "-"
                     + CheatBreaker.getInstance().getBranch()
                     + File.separator
                     + "themes"
               );
               File file2 = new File(file + File.separator + this.theme2.getThemeNameValue() + ".json");
               if (file2.delete()) {
                  System.out.println("deleted.");
               }

               this.theme2.setThemeNameValue(this.textField.getText());
               CheatBreaker.getInstance().getConfigFileManager().saveTheme(this.textField.getText());
            }
         } else if (n == 1) {
            this.textField.setFocused(false);
         }
      } else {
         super.keyTyped(c, n);
      }
   }
}
