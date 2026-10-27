package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.chat.ChatPrinter;
import com.cheatbreaker.client.network.packet.MessagePacket;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.screen.FriendsScreen;
import com.sun.javafx.geom.Vec2d;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ConsoleWindow extends DraggableElement {
   public List<String> stringList = new ArrayList<>();
   public int intValue = 0;
   public AtomicBoolean atomicBoolean = new AtomicBoolean();
   public Vec2d vec2d2 = new Vec2d();
   public float floatValue;
   public int intValue2 = 0;
   public TextField textField = new TextField(CheatBreaker.getInstance().getFontManager().playRegular14, "", 805306367, 1879048191);
   public float floatValue2;
   public ScrollBar scrollBar;
   public SectionHeader send;
   public SectionHeader sectionHeader;
   public float floatValue3;
   public float floatValue4;

   @Override
   public void keyTyped(char c, int n) {
      if (this.textField.isFocused() && !this.textField.getText().equals("") && n == 28) {
         this.run();
      }

      try {
         if (this.textField.isFocused() && n == 200 && this.stringList.size() != 0) {
            if (this.intValue != 0) {
               this.intValue--;
            }

            this.textField.setText(this.stringList.get(this.intValue));
         } else if (this.textField.isFocused() && n == 208 && this.stringList.size() != 0) {
            if (this.intValue != this.stringList.size() - 1) {
               this.intValue++;
            }

            this.textField.setText(this.stringList.get(this.intValue));
         }
      } catch (IndexOutOfBoundsException e) {
         this.intValue = 0;
      }

      this.textField.keyTyped(c, n);
      this.send.keyTyped(c, n);
      this.scrollBar.keyTyped(c, n);
      this.sectionHeader.keyTyped(c, n);
   }

   public void process(float f, float f2) {
      if (this.atomicBoolean.get()) {
         if (!Mouse.isButtonDown(0)) {
            this.atomicBoolean.set(false);
            return;
         }

         float f3 = f - (float)this.vec2d2.x;
         float f4 = f2 - (float)this.vec2d2.y;
         float floatValue4 = this.floatValue4;
         float floatValue3 = this.floatValue3;
         float floatValue2 = this.floatValue2;
         float floatValue = this.floatValue;
         if ((this.intValue2 & 1) != 0) {
            float f5 = this.floatValue2 - 200.0F;
            f3 = Math.min(f3, f5);
            floatValue4 += f3;
            floatValue2 -= f3;
         } else if ((this.intValue2 & 2) != 0) {
            floatValue2 += f3;
         }

         if ((this.intValue2 & 4) != 0) {
            float f5 = this.floatValue - 100.0F;
            f4 = Math.min(f4, f5);
            floatValue3 += f4;
            floatValue -= f4;
         } else if ((this.intValue2 & 8) != 0) {
            floatValue += f4;
         }

         floatValue2 = Math.max(floatValue2, 200.0F);
         floatValue = Math.max(floatValue, 100.0F);
         this.setBounds(floatValue4, floatValue3, floatValue2, floatValue);
      }
   }

   public void run() {
      String text = this.textField.getText();
      if (text.equals("ban")) {
         ChatPrinter.print("Test has been CheatBreaker banned.", true, true);
      } else if (text.equals("clear") || text.equals("cls")) {
         CheatBreaker.getInstance().getServerMessages().clear();
         CheatBreaker.getInstance().getKeybindSettings().firstLaunch = false;
      } else if (text.equalsIgnoreCase("wsReconnect") && CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().isClosed()) {
         try {
            CheatBreaker.getInstance().getWebsocketController().run2();
         } catch (URISyntaxException e) {
            CheatBreaker.getInstance().getServerMessages().add("Invalid URL: " + e.getInput());
            e.printStackTrace();
         }
      } else {
         CheatBreaker.getInstance().getServerMessages().add(EnumChatFormatting.GRAY + "> " + text);
         CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().sendPacket(new MessagePacket(text));
         this.stringList.add(text);
         this.intValue = this.stringList.size();
      }

      this.textField.setText("");
      this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
   }

   @Override
   public boolean mouseClicked(float f, float f2, int n, boolean flag) {
      this.textField.mouseClicked(f, f2, n, flag);
      this.scrollBar.mouseClicked(f, f2, n, flag);
      if (!flag) {
         return false;
      }

      if (!this.textField.getText().equals("") && this.send.containsPoint(f, f2)) {
         this.run();
      }

      this.send.mouseClicked(f, f2, n, flag);
      byte b = 4;
      boolean flag2 = f >= this.x && f <= this.x + b;
      boolean flag3 = f >= this.x + this.width - b && f <= this.x + this.width;
      boolean flag4 = f2 >= this.y && f2 <= this.y + b;
      boolean flag5 = f2 >= this.y + this.height - b && f2 <= this.y + this.height;
      byte b2 = 0;
      if (flag2 && flag4) {
         b2 = 5;
      } else if (flag3 && flag4) {
         b2 = 6;
      } else if (flag2 && flag5) {
         b2 = 9;
      } else if (flag3 && flag5) {
         b2 = 10;
      }

      if (!this.textField.containsPoint(f, f2) && !this.send.containsPoint(f, f2) && !this.sectionHeader.containsPoint(f, f2)) {
         boolean flag6 = false;
      } else {
         boolean flag6 = true;
      }

      if (b2 != 0 && n == 0) {
         this.atomicBoolean.set(true);
         this.intValue2 = b2;
         this.vec2d2.set(f, f2);
         this.floatValue4 = this.x;
         this.floatValue3 = this.y;
         this.floatValue2 = this.width;
         this.floatValue = this.height;
         return true;
      }

      if (this.containsPoint(f, f2) && f2 < this.y + 12.0F) {
         this.startDrag(f, f2);
      }

      if (this.sectionHeader.containsPoint(f, f2)) {
         this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         FriendsScreen.getFriendsScreen().removeElements(this);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void setBounds(float f, float f2, float f3, float f4) {
      super.setBounds(f, f2, f3, f4);
      this.textField.setBounds(f + 2.0F, f2 + f4 - 15.0F, f3 - 40.0F, 13.0F);
      this.scrollBar.setBounds(f + f3 - 6.0F, f2 + 12.0F + 3.0F, 4.0F, f4 - 32.0F);
      this.send.setBounds(f + f3 - 37.0F, f2 + f4 - 15.0F, 35.0F, 13.0F);
      this.sectionHeader.setBounds(f + f3 - 12.0F, f2 + 2.0F, 10.0F, 10.0F);
   }

   public boolean isFocused() {
      return this.textField.isFocused();
   }

   @Override
   public void onClose() {
      this.textField.onClose();
      this.send.onClose();
      this.scrollBar.onClose();
      this.sectionHeader.onClose();
   }

   @Override
   public boolean mouseReleased(float f, float f2, int n, boolean flag) {
      if (!flag) {
         return false;
      }

      this.textField.mouseReleased(f, f2, n, flag);
      this.send.mouseReleased(f, f2, n, flag);
      this.scrollBar.mouseReleased(f, f2, n, flag);
      this.sectionHeader.mouseReleased(f, f2, n, flag);
      return false;
   }

   @Override
   public void render(float f, float f2, boolean flag) {
      this.updateDrag(f, f2);
      this.process(f, f2);
      Gui.drawRectWithOuterBorder(this.x, this.y, this.x + this.width, this.y + this.height, 0.5F, -16777216, -15395563);
      GlStateManager.pushMatrix();
      Gui.drawRect(this.x, this.y - 0.5F, this.x + this.width, this.y, -1357572843);
      Gui.drawRect(this.x, this.y + this.height, this.x + this.width, this.y + this.height + 0.5F, -1357572843);
      float f3 = this.x + 4.0F;
      float f4 = this.y + 3.0F;
      CheatBreaker.getInstance().getFontManager().playRegular14.drawString("Console", f3, f4, -1);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      Gui.drawRect(this.x + 2.0F, this.y + 12.0F + 3.0F, this.x + this.width - 2.0F, this.y + this.height - 17.0F, -1356783327);
      this.scrollBar.process(f, f2, flag);

      try {
         if (CheatBreaker.getInstance().getWebsocketController().isFlag()) {
            GlStateManager.pushMatrix();
            GL11.glEnable(3089);
            FriendsScreen friendsScreen = FriendsScreen.getFriendsScreen();
            RenderUtils.scissorScaled(
               (int)(this.x + 2.0F),
               (int)(this.y + 12.0F + 3.0F),
               (int)(this.x + this.width - 2.0F),
               (int)(this.y + this.height - 17.0F),
               (int)(friendsScreen.getScaledResolution().getScaleFactor() * friendsScreen.getUiScale()),
               (int)friendsScreen.getScaledHeight()
            );
            List serverMessages = CheatBreaker.getInstance().getServerMessages();
            int n = 0;

            for (int i = serverMessages.size() - 1; i >= 0; i--) {
               String str = (String)serverMessages.get(i);
               String[] split = CheatBreaker.getInstance().getFontManager().playRegular14.wrapText(str, this.width - 10.0F).split("\n");
               n += split.length * 10;
               int n2 = 0;

               for (String str2 : split) {
                  f3 = this.x + 6.0F;
                  CheatBreaker.getInstance().getFontManager().playRegular14.drawString(str2, f3, this.y + this.height - 19.0F - n + n2 * 10, -1);
                  n2++;
               }
            }

            this.scrollBar.setFloatValue4(n + 4);
            GL11.glDisable(3089);
            GlStateManager.popMatrix();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      this.scrollBar.process2(f, f2, flag);
      GlStateManager.popMatrix();
      this.textField.renderAndTestHover(f, f2, flag);
      this.send.renderAndTestHover(f, f2, flag);
      this.sectionHeader.renderAndTestHover(f, f2, flag);
   }

   @Override
   public void tick() {
      this.textField.tick();
      this.send.tick();
      this.scrollBar.tick();
      this.sectionHeader.tick();
   }

   @Override
   public void handleMouseInput() {
      this.scrollBar.handleMouseInput();
   }

   public ConsoleWindow() {
      this.textField.setMaxStringLength(256);
      this.scrollBar = new ScrollBar(this);
      this.send = new SectionHeader("SEND");
      this.sectionHeader = new SectionHeader("X");
   }

   @Override
   public boolean mouseClickedFallback(float f, float f2, int n) {
      if (!this.textField.containsPoint(f, f2) && this.textField.isFocused()) {
         this.textField.setFocused(false);
      }

      return false;
   }
}
