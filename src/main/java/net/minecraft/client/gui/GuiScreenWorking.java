package net.minecraft.client.gui;

import net.minecraft.util.IProgressUpdate;

public class GuiScreenWorking extends GuiScreen implements IProgressUpdate {
    public String title = "";
    public String task = "";
    public int progress;
    public boolean done;

    @Override
    public void displaySavingString(String str) {
        this.resetProgressAndMessage(str);
    }

    @Override
    public void resetProgressAndMessage(String str) {
        this.title = str;
        this.displayLoadingString("Working...");
    }

    @Override
    public void displayLoadingString(String str) {
        this.task = str;
        this.setLoadingProgress(0);
    }

    @Override
    public void setLoadingProgress(int n) {
        this.progress = n;
    }

    @Override
    public void setDoneWorking() {
        this.done = true;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        if (this.done) {
            this.mc.displayGuiScreen(null);
        } else {
            this.drawDefaultBackground();
            this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 70, 16777215);
            this.drawCenteredString(this.fontRendererObj, this.task + " " + this.progress + "%", this.width / 2, 90, 16777215);
            super.drawScreen(mouseX, mouseY, partialTicks);
        }
    }
}
