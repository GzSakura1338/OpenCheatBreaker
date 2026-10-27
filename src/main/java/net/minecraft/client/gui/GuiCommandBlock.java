package net.minecraft.client.gui;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class GuiCommandBlock extends GuiScreen {
   public static Logger LOGGER = LogManager.getLogger();
   public GuiTextField commandTextField;
   public GuiTextField previousOutputTextField;
   public CommandBlockLogic localCommandBlock;
   public GuiButton doneBtn;
   public GuiButton cancelBtn;
   public GuiButton trackingOutputToggleButton;
   public boolean trackingOutput;

   public GuiCommandBlock(CommandBlockLogic commandBlockLogic) {
      this.localCommandBlock = commandBlockLogic;
   }

   @Override
   public void updateScreen() {
      this.commandTextField.updateCursorCounter();
   }

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      this.buttonList.add(this.doneBtn = new GuiButton(0, this.width / 2 - 4 - 150, this.height / 4 + 120 + 12, 150, 20, I18n.format("gui.done")));
      this.buttonList.add(this.cancelBtn = new GuiButton(1, this.width / 2 + 4, this.height / 4 + 120 + 12, 150, 20, I18n.format("gui.cancel")));
      this.buttonList.add(this.trackingOutputToggleButton = new GuiButton(4, this.width / 2 + 150 - 20, 150, 20, 20, "O"));
      this.commandTextField = new GuiTextField(2, this.fontRendererObj, this.width / 2 - 150, 50, 300, 20);
      this.commandTextField.setMaxStringLength(32767);
      this.commandTextField.setFocused(true);
      this.commandTextField.setText(this.localCommandBlock.getCommand());
      this.previousOutputTextField = new GuiTextField(3, this.fontRendererObj, this.width / 2 - 150, 150, 276, 20);
      this.previousOutputTextField.setMaxStringLength(32767);
      this.previousOutputTextField.setEnabled(false);
      this.previousOutputTextField.setText("-");
      this.trackingOutput = this.localCommandBlock.shouldTrackOutput();
      this.updateButtonTexts();
      this.doneBtn.enabled = this.commandTextField.getText().trim().length() > 0;
   }

   @Override
   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         if (button.id == 1) {
            this.localCommandBlock.setTrackOutput(this.trackingOutput);
            this.mc.displayGuiScreen((GuiScreen)null);
         } else if (button.id == 0) {
            PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
            packetBuffer.writeByte(this.localCommandBlock.constantInt());
            this.localCommandBlock.write(packetBuffer);
            packetBuffer.writeString(this.commandTextField.getText());
            packetBuffer.writeBoolean(this.localCommandBlock.shouldTrackOutput());
            this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("MC|AdvCdm", packetBuffer));
            if (!this.localCommandBlock.shouldTrackOutput()) {
               this.localCommandBlock.setLastOutput((IChatComponent)null);
            }

            this.mc.displayGuiScreen((GuiScreen)null);
         } else if (button.id == 4) {
            this.localCommandBlock.setTrackOutput(!this.localCommandBlock.shouldTrackOutput());
            this.updateButtonTexts();
         }
      }
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      this.commandTextField.textboxKeyTyped(typedChar, keyCode);
      this.previousOutputTextField.textboxKeyTyped(typedChar, keyCode);
      this.doneBtn.enabled = this.commandTextField.getText().trim().length() > 0;
      if (keyCode == 28 || keyCode == 156) {
         this.actionPerformed(this.doneBtn);
      } else if (keyCode == 1) {
         this.actionPerformed(this.cancelBtn);
      }
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.commandTextField.mouseClicked(mouseX, mouseY, mouseButton);
      this.previousOutputTextField.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, I18n.format("advMode.setCommand"), this.width / 2, 20, 16777215);
      this.drawString(this.fontRendererObj, I18n.format("advMode.command"), this.width / 2 - 150, 37, 10526880);
      this.commandTextField.drawTextBox();
      int n = 75;
      int n2 = 0;
      this.drawString(
         this.fontRendererObj, I18n.format("advMode.nearestPlayer"), this.width / 2 - 150, n + n2++ * this.fontRendererObj.FONT_HEIGHT, 10526880
      );
      this.drawString(
         this.fontRendererObj, I18n.format("advMode.randomPlayer"), this.width / 2 - 150, n + n2++ * this.fontRendererObj.FONT_HEIGHT, 10526880
      );
      this.drawString(this.fontRendererObj, I18n.format("advMode.allPlayers"), this.width / 2 - 150, n + n2++ * this.fontRendererObj.FONT_HEIGHT, 10526880);
      this.drawString(
         this.fontRendererObj, I18n.format("advMode.allEntities"), this.width / 2 - 150, n + n2++ * this.fontRendererObj.FONT_HEIGHT, 10526880
      );
      this.drawString(this.fontRendererObj, "", this.width / 2 - 150, n + n2++ * this.fontRendererObj.FONT_HEIGHT, 10526880);
      if (this.previousOutputTextField.getText().length() > 0) {
         n = n + n2 * this.fontRendererObj.FONT_HEIGHT + 16;
         this.drawString(this.fontRendererObj, I18n.format("advMode.previousOutput"), this.width / 2 - 150, n, 10526880);
         this.previousOutputTextField.drawTextBox();
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public void updateButtonTexts() {
      if (this.localCommandBlock.shouldTrackOutput()) {
         this.trackingOutputToggleButton.displayString = "O";
         if (this.localCommandBlock.getLastOutput() != null) {
            this.previousOutputTextField.setText(this.localCommandBlock.getLastOutput().getUnformattedText());
         }
      } else {
         this.trackingOutputToggleButton.displayString = "X";
         this.previousOutputTextField.setText("-");
      }
   }
}
