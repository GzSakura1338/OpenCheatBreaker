package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.animation.CosineAnimation;
import com.cheatbreaker.client.network.ReconnectHandler;
import com.cheatbreaker.client.ui.component.TextButton;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;
import javax.swing.JOptionPane;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.util.ResourceLocation;

public class MainMenuScreen extends LogoMenuScreen {
   public TextButton singleplayer = new TextButton("SINGLEPLAYER");
   public static String[] stringArray = new String[]{
      "https://youtu.be/dQw4w9WgXcQ",
      "https://youtu.be/qvSmM68xW5Q",
      "https://youtu.be/1cBSFFKXfr0",
      "https://youtu.be/Wav_1mqY5ZU",
      "https://youtu.be/vjrAaAD2iPw",
      "https://youtu.be/j8UJE7DoyJ8",
      "https://youtu.be/k8EPsykoGnY",
      "https://youtu.be/yA4E1WpxjFE",
      "https://youtu.be/IlCx5gjAmqI",
      "https://youtu.be/NXgmlDuTz7w",
      "https://youtu.be/6bSoeKMNBgI",
      "https://youtu.be/DhrK-PuJOgk",
      "https://youtu.be/Z9NQatne0xg",
      "https://youtu.be/pd9n8EfRk3w",
      "https://youtu.be/xiNrkWKIkd0",
      "https://youtu.be/XpImIRSDwmI",
      "https://youtu.be/G9PNubHnlOM"
   };
   public TextButton multiplayer = new TextButton("MULTIPLAYER");
   public TextButton unlockGrandCosmetic = new TextButton("UNLOCK GRAND COSMETIC");

   @Override
   public void initGui() {
      super.initGui();
      if (CheatBreaker.isAprilFoolsEnabled() && CheatBreaker.getInstance().getKeybindSettings().aprilFools.getBooleanValue()) {
         this.cosineAnimation = new CosineAnimation((long)Math.abs(Math.random() * 1000.0));
      }
   }

   @Override
   public void mouseClickedScaled(float f, float f2, int n) {
      super.mouseClickedScaled(f, f2, n);
      if (this.singleplayer.containsPoint(f, f2)) {
         this.mc2.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         this.mc2.displayGuiScreen(new GuiSelectWorld(this));
      } else if (this.multiplayer.containsPoint(f, f2)) {
         this.mc2.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         this.mc2.displayGuiScreen(new GuiMultiplayer(this));
      } else if (this.unlockGrandCosmetic.containsPoint(f, f2)) {
         this.mc2.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         new Thread(() -> JOptionPane.showMessageDialog(null, "Unlocking Grand Cosmetic....\n\nThis may take a moment.", "Please wait", 1)).start();
         new Thread(() -> {
            try {
               Thread.sleep(100000L);
            } catch (InterruptedException e) {
               e.printStackTrace();
            }

            for (int i = 0; i < 50; i++) {
               try {
                  Random random = new Random();
                  int nextInt = random.nextInt(stringArray.length);
                  this.openUri(new URI(stringArray[nextInt]));

                  try {
                     long l = 60000L * (random.nextInt(7) + 1);
                     Thread.sleep(l);
                  } catch (InterruptedException e) {
                     e.printStackTrace();
                  }
               } catch (URISyntaxException e) {
                  CheatBreaker.getInstance().getLogger().error("Couldn't open link", e);
               }
            }
         }).start();
      }
   }

   public MainMenuScreen() {
      this.textButtonList.add(this.singleplayer);
      this.textButtonList.add(this.multiplayer);
      if (CheatBreaker.isAprilFoolsEnabled()) {
         this.textButtonList.add(this.unlockGrandCosmetic);
      }
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      this.singleplayer.setEnabled(CheatBreaker.getInstance().isWebsocketConnected() || CheatBreaker.isOfflineMode());
      this.multiplayer.setEnabled(CheatBreaker.getInstance().isWebsocketConnected() || CheatBreaker.isOfflineMode());
      this.cosmetics.setEnabled(CheatBreaker.getInstance().isWebsocketConnected());
      this.discordLinkButton.setEnabled(CheatBreaker.getInstance().isWebsocketConnected());
      if (!CheatBreaker.getInstance().isWebsocketConnected() && !CheatBreaker.isOfflineMode()) {
         if (CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().isOpen()) {
            this.text6 = "Authenticating with the server.";
            if (System.currentTimeMillis() - ReconnectHandler.connectTime > 60000L) {
               CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().close();
            }

            if (System.currentTimeMillis() - ReconnectHandler.connectTime > 30000L) {
               this.text22 = "Something's not right...";
            } else if (System.currentTimeMillis() - ReconnectHandler.connectTime > 15000L) {
               this.text22 = "This is taking a bit longer...";
            } else {
               this.text22 = "This may take a few moments.";
            }
         } else {
            this.text6 = "Unable to connect to the server.";
            if (ReconnectHandler.secondsUntilReconnect >= 360000L) {
               this.text22 = "Please try again later.";
            } else if (ReconnectHandler.secondsUntilReconnect <= 0) {
               this.text6 = "Connecting to the server.";
               this.text22 = "This may take a few moments.";
            } else {
               this.text22 = "Trying again in " + ReconnectHandler.secondsUntilReconnect + " seconds.";
            }
         }

         if (CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().errorTitle != null) {
            this.text6 = CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().errorTitle;
            this.text22 = CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().errorMessage;
         }
      } else {
         this.text6 = this.text22 = "";
      }
   }
}
