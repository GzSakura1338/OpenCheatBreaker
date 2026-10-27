package net.minecraft.realms;

import java.util.List;
import net.minecraft.util.IChatComponent;

public class DisconnectedRealmsScreen extends RealmsScreen {
   public RealmsScreen parent;
   public String title;
   public List<String> lines;
   public IChatComponent reason;
   public int textHeight;

   public DisconnectedRealmsScreen(RealmsScreen parentIn, String unlocalizedTitle, IChatComponent reasonIn) {
      this.parent = parentIn;
      this.title = getLocalizedString(unlocalizedTitle);
      this.reason = reasonIn;
   }

   @Override
   public void init() {
      Realms.setConnectedToRealms(false);
      this.buttonsClear();
      this.lines = this.fontSplit(this.reason.getFormattedText(), this.width() - 50);
      this.textHeight = this.lines.size() * this.fontLineHeight();
      this.buttonsAdd(newButton(0, this.width() / 2 - 100, this.height() / 2 + this.textHeight / 2 + this.fontLineHeight(), getLocalizedString("gui.back")));
   }

   @Override
   public void keyPressed(char c, int n) {
      if (n == 1) {
         Realms.setScreen(this.parent);
      }
   }

   @Override
   public void buttonClicked(RealmsButton realmsButton) {
      if (realmsButton.id() == 0) {
         Realms.setScreen(this.parent);
      }
   }

   @Override
   public void render(int n, int n2, float f) {
      this.renderBackground();
      this.drawCenteredString(this.title, this.width() / 2, this.height() / 2 - this.textHeight / 2 - this.fontLineHeight() * 2, 11184810);
      int n3 = this.height() / 2 - this.textHeight / 2;
      if (this.lines != null) {
         for (String str : this.lines) {
            this.drawCenteredString(str, this.width() / 2, n3, 16777215);
            n3 += this.fontLineHeight();
         }
      }

      super.render(n, n2, f);
   }
}
