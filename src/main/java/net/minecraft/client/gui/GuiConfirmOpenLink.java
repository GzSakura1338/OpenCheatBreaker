package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;

public class GuiConfirmOpenLink extends GuiYesNo {
    public boolean showSecurityWarning = true;
    public String copyLinkButtonText;
    public String openLinkWarning;
    public String linkText;

    public GuiConfirmOpenLink(GuiYesNoCallback guiYesNoCallback, String str, int n, boolean flag) {
        super(guiYesNoCallback, I18n.format(flag ? "chat.link.confirmTrusted" : "chat.link.confirm"), str, n);
        this.confirmButtonText = I18n.format(flag ? "chat.link.open" : "gui.yes");
        this.cancelButtonText = I18n.format(flag ? "gui.cancel" : "gui.no");
        this.copyLinkButtonText = I18n.format("chat.copy");
        this.openLinkWarning = I18n.format("chat.link.warning");
        this.linkText = str;
    }

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(0, this.width / 2 - 155, this.height / 6 + 96, 100, 20, this.confirmButtonText));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 50, this.height / 6 + 96, 100, 20, this.copyLinkButtonText));
        this.buttonList.add(new GuiButton(1, this.width / 2 + 55, this.height / 6 + 96, 100, 20, this.cancelButtonText));
    }

    @Override
    public void actionPerformed(GuiButton button) throws java.io.IOException {
        if (button.id == 2) {
            this.copyLinkToClipboard();
        }

        this.parentScreen.confirmClicked(button.id == 0, this.parentButtonClickedId);
    }

    public void copyLinkToClipboard() {
        setClipboardString(this.linkText);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.showSecurityWarning) {
            this.drawCenteredString(this.fontRendererObj, this.openLinkWarning, this.width / 2, 110, 16764108);
        }
    }

    public void disableSecurityWarning() {
        this.showSecurityWarning = false;
    }
}
