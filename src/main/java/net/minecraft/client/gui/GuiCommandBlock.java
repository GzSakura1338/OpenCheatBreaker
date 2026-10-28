package net.minecraft.client.gui;

import com.cheatbreaker.client.CheatBreaker;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class GuiCommandBlock extends GuiScreen {
    public static Logger LOGGER = LogManager.getLogger();
    public GuiTextField commandTextField;
    public GuiTextField output;
    public CommandBlockLogic localCommandBlock;
    public GuiButton doneBtn;
    public GuiButton cancelBtn;

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
        this.commandTextField = new GuiTextField(this.fontRendererObj, this.width / 2 - 150, 50, 300, 20);
        this.commandTextField.setMaxStringLength(32767);
        this.commandTextField.setFocused(true);
        this.commandTextField.setText(this.localCommandBlock.getCustomName());
        this.output = new GuiTextField(this.fontRendererObj, this.width / 2 - 150, 135, 300, 20);
        this.output.setMaxStringLength(32767);
        this.output.setEnabled(false);
        this.output.setText(this.localCommandBlock.getCustomName());
        if (this.localCommandBlock.getLastOutput() != null) {
            this.output.setText(this.localCommandBlock.getLastOutput().getUnformattedText());
        }

        this.doneBtn.enabled = this.commandTextField.getText().trim().length() > 0;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void actionPerformed(GuiButton button) throws java.io.IOException {
        if (button.enabled) {
            if (button.id == 1) {
                if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().guiBlur.getValue()) {
                    Minecraft.getMinecraft().entityRenderer.stopUseShader();
                }

                this.mc.displayGuiScreen(null);
            } else if (button.id == 0) {
                PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());

                try {
                    packetBuffer.writeByte(this.localCommandBlock.constantInt());
                    this.localCommandBlock.write(packetBuffer);
                    packetBuffer.writeStringToBuffer(this.commandTextField.getText());
                    this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("MC|AdvCdm", packetBuffer));
                } catch (Exception e) {
                    LOGGER.error("Couldn't send command block info", e);
                } finally {
                    packetBuffer.release();
                }

                if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().guiBlur.getValue()) {
                    Minecraft.getMinecraft().entityRenderer.stopUseShader();
                }

                this.mc.displayGuiScreen(null);
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws java.io.IOException {
        this.commandTextField.textboxKeyTyped(typedChar, keyCode);
        this.output.textboxKeyTyped(typedChar, keyCode);
        this.doneBtn.enabled = this.commandTextField.getText().trim().length() > 0;
        if (keyCode == 28 || keyCode == 156) {
            this.actionPerformed(this.doneBtn);
        } else if (keyCode == 1) {
            this.actionPerformed(this.cancelBtn);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws java.io.IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.commandTextField.mouseClicked(mouseX, mouseY, mouseButton);
        this.output.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, I18n.format("advMode.setCommand"), this.width / 2, 20, 16777215);
        this.drawString(this.fontRendererObj, I18n.format("advMode.command"), this.width / 2 - 150, 37, 10526880);
        this.commandTextField.drawTextBox();
        byte b = 75;
        byte b2 = 0;
        FontRenderer fontRendererObj = this.fontRendererObj;
        String format = I18n.format("advMode.nearestPlayer");
        int n = this.width / 2 - 150;
        int n2 = b2 + 1;
        this.drawString(fontRendererObj, format, n, b + b2 * this.fontRendererObj.FONT_HEIGHT, 10526880);
        this.drawString(
            this.fontRendererObj, I18n.format("advMode.randomPlayer"), this.width / 2 - 150, b + n2++ * this.fontRendererObj.FONT_HEIGHT, 10526880
        );
        this.drawString(this.fontRendererObj, I18n.format("advMode.allPlayers"), this.width / 2 - 150, b + n2++ * this.fontRendererObj.FONT_HEIGHT, 10526880);
        if (this.output.getText().length() > 0) {
            int n3 = b + n2 * this.fontRendererObj.FONT_HEIGHT + 20;
            this.drawString(this.fontRendererObj, I18n.format("advMode.previousOutput"), this.width / 2 - 150, n3, 10526880);
            this.output.drawTextBox();
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
