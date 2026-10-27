package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class GuiControls extends GuiScreen {
   public static Options[] optionsArr = new Options[]{
      Options.INVERT_MOUSE, Options.SENSITIVITY, Options.TOUCHSCREEN
   };
   public String screenTitle = "Controls";
   public KeyBinding buttonId = null;
   public int intValue = 0;
   public GuiScreen parentScreen;
   public long time;
   public GuiKeyBindingList keyBindingList;
   public GuiButton buttonReset;
   public GameSettings options;

   public GuiControls(GuiScreen screen, GameSettings settings) {
      this.parentScreen = screen;
      this.options = settings;
   }

   @Override
   public void initGui() {
      this.keyBindingList = new GuiKeyBindingList(this, this.mc);
      this.buttonList.add(new GuiButton(200, this.width / 2 - 155, this.height - 29, 150, 20, I18n.format("gui.done")));
      this.buttonList.add(this.buttonReset = new GuiButton(201, this.width / 2 - 155 + 160, this.height - 29, 150, 20, I18n.format("controls.resetAll")));
      this.screenTitle = I18n.format("controls.title");
      int n = 0;

      for (Options options : optionsArr) {
         if (options.getEnumFloat()) {
            this.buttonList.add(new GuiOptionSlider(options.returnEnumOrdinal(), this.width / 2 - 155 + n % 2 * 160, 18 + 24 * (n >> 1), options));
         } else {
            this.buttonList
               .add(
                  new GuiOptionButton(
                     options.returnEnumOrdinal(), this.width / 2 - 155 + n % 2 * 160, 18 + 24 * (n >> 1), options, this.options.getKeyBinding(options)
                  )
               );
         }

         n++;
      }
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.keyBindingList.handleMouseInput();
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 200) {
         this.mc.displayGuiScreen(this.parentScreen);
      } else if (button.id == 201) {
         for (KeyBinding keyBinding : this.mc.gameSettings.keyBindings) {
            keyBinding.setKeyCode(keyBinding.getKeyCodeDefault());
            keyBinding.run2();
            keyBinding.run();
         }

         KeyBinding.resetKeyBindingArrayAndHash();
      } else if (button.id < 100 && button instanceof GuiOptionButton) {
         this.options.setOptionValue(((GuiOptionButton)button).returnEnumOptions(), 1);
         button.displayString = this.options.getKeyBinding(Options.getEnumOptions(button.id));
      }
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      if (this.buttonId != null) {
         this.options.setOptionKeyBinding(this.buttonId, -100 + mouseButton);
         boolean flag = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54);
         boolean flag2 = Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157);
         boolean flag3 = Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184);
         this.buttonId.process(flag, flag2, flag3);
         this.buttonId = null;
         KeyBinding.resetKeyBindingArrayAndHash();
      } else if (mouseButton != 0 || !this.keyBindingList.mouseClicked(mouseX, mouseY, mouseButton)) {
         super.mouseClicked(mouseX, mouseY, mouseButton);
      }
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY, int state) {
      if (state != 0 || !this.keyBindingList.mouseReleased(mouseX, mouseY, state)) {
         super.mouseReleased(mouseX, mouseY, state);
      }
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      if (this.buttonId != null) {
         if (check(keyCode)) {
            this.intValue = keyCode;
            return;
         }

         int n = 0;
         if (keyCode == 1) {
            this.options.setOptionKeyBinding(this.buttonId, 0);
         } else if (keyCode != 0) {
            n = keyCode;
            this.options.setOptionKeyBinding(this.buttonId, keyCode);
         } else if (typedChar > 0) {
            n = typedChar + 256;
            this.options.setOptionKeyBinding(this.buttonId, n);
         }

         boolean flag = n != 0 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
         boolean flag2 = n != 0 && (Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157));
         boolean flag3 = n != 0 && (Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184));
         this.buttonId.process(flag, flag2, flag3);
         this.intValue = 0;
         this.buttonId = null;
         this.time = Minecraft.getSystemTime();
         KeyBinding.resetKeyBindingArrayAndHash();
      } else {
         super.keyTyped(typedChar, keyCode);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.keyBindingList.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 8, 16777215);
      boolean flag = true;

      for (KeyBinding keyBinding : this.options.keyBindings) {
         if (keyBinding.getKeyCode() != keyBinding.getKeyCodeDefault() || !keyBinding.check()) {
            flag = false;
            break;
         }
      }

      this.buttonReset.enabled = !flag;
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      if (this.buttonId != null && this.intValue != 0 && !Keyboard.isKeyDown(this.intValue)) {
         int intValue = this.intValue;
         this.intValue = 0;
         this.options.setOptionKeyBinding(this.buttonId, intValue);
         this.buttonId.process(false, false, false);
         this.buttonId = null;
         this.time = Minecraft.getSystemTime();
         KeyBinding.resetKeyBindingArrayAndHash();
      }
   }

   public static boolean check(int n) {
      return n == 42 || n == 54 || n == 29 || n == 157 || n == 56 || n == 184;
   }
}
