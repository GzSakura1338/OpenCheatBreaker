package net.minecraft.client.gui;

import net.minecraft.util.IProgressUpdate;
import net.optifine.CustomLoadingScreen;
import net.optifine.CustomLoadingScreens;

public class GuiScreenWorking extends GuiScreen implements IProgressUpdate {
   public String title = "";
   public String task = "";
   public int progress;
   public boolean doneWorking;
   public CustomLoadingScreen customLoadingScreen = CustomLoadingScreens.getCustomLoadingScreen();

   @Override
   public void displaySavingString(String message) {
      this.resetProgressAndMessage(message);
   }

   @Override
   public void resetProgressAndMessage(String message) {
      this.title = message;
      this.displayLoadingString("Working...");
   }

   @Override
   public void displayLoadingString(String message) {
      this.task = message;
      this.setLoadingProgress(0);
   }

   @Override
   public void setLoadingProgress(int progress) {
      this.progress = progress;
   }

   @Override
   public void setDoneWorking() {
      this.doneWorking = true;
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if (this.doneWorking) {
         if (!this.mc.isConnectedToRealms()) {
            this.mc.displayGuiScreen((GuiScreen)null);
         }
      } else {
         if (this.customLoadingScreen != null && this.mc.theWorld == null) {
            this.customLoadingScreen.drawBackground(this.width, this.height);
         } else {
            this.drawDefaultBackground();
         }

         if (this.progress > 0) {
            this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 70, 16777215);
            this.drawCenteredString(this.fontRendererObj, this.task + " " + this.progress + "%", this.width / 2, 90, 16777215);
         }

         super.drawScreen(mouseX, mouseY, partialTicks);
      }
   }
}
