package net.minecraft.realms;

import java.util.List;
import net.minecraft.util.IChatComponent;

public class DisconnectedOnlineScreen extends RealmsScreen {
    public RealmsScreen parent;
    public String title;
    public List lines;
    public IChatComponent reason;

    public DisconnectedOnlineScreen(RealmsScreen realmsScreen, String str, IChatComponent chatComponent) {
        this.parent = realmsScreen;
        this.title = getLocalizedString(str);
        this.reason = chatComponent;
    }

    @Override
    public void init() {
        this.buttonsClear();
        this.buttonsAdd(newButton(0, this.width() / 2 - 100, this.height() / 4 + 120 + 12, getLocalizedString("gui.back")));
        this.lines = this.fontSplit(this.reason.getFormattedText(), this.width() - 50);
    }

    @Override
    public void keyPressed(char c, int n) throws java.io.IOException {
        if (n == 1) {
            Realms.setScreen(this.parent);
        }
    }

    @Override
    public void buttonClicked(RealmsButton realmsButton) throws java.io.IOException {
        if (realmsButton.id() == 0) {
            Realms.setScreen(this.parent);
        }
    }

    @Override
    public void render(int n, int n2, float f) throws java.io.IOException {
        this.renderBackground();
        this.drawCenteredString(this.title, this.width() / 2, this.height() / 2 - 50, 11184810);
        int n3 = this.height() / 2 - 30;
        if (this.lines != null) {
            for (String str : (Iterable<String>)this.lines) {
                this.drawCenteredString(str, this.width() / 2, n3, 16777215);
                n3 += this.fontLineHeight();
            }
        }

        super.render(n, n2, f);
    }
}
