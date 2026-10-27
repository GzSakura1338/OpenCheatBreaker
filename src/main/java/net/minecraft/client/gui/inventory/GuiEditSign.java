package net.minecraft.client.gui.inventory;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentText;
import org.lwjgl.input.Keyboard;

public class GuiEditSign extends GuiScreen {
   public TileEntitySign tileSign;
   public int updateCounter;
   public int editLine;
   public GuiButton doneBtn;

   public GuiEditSign(TileEntitySign teSign) {
      this.tileSign = teSign;
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      Keyboard.enableRepeatEvents(true);
      this.buttonList.add(this.doneBtn = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120, I18n.format("gui.done")));
      this.tileSign.setEditable(false);
   }

   @Override
   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
      NetHandlerPlayClient netHandler = this.mc.getNetHandler();
      if (netHandler != null) {
         netHandler.addToSendQueue(new C12PacketUpdateSign(this.tileSign.getPos(), this.tileSign.signText));
      }

      this.tileSign.setEditable(true);
   }

   @Override
   public void updateScreen() {
      this.updateCounter++;
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled && button.id == 0) {
         this.tileSign.markDirty();
         this.mc.displayGuiScreen((GuiScreen)null);
      }
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      if (keyCode == 200) {
         this.editLine = this.editLine - 1 & 3;
      }

      if (keyCode == 208 || keyCode == 28 || keyCode == 156) {
         this.editLine = this.editLine + 1 & 3;
      }

      String unformattedText = this.tileSign.signText[this.editLine].getUnformattedText();
      if (keyCode == 14 && unformattedText.length() > 0) {
         unformattedText = unformattedText.substring(0, unformattedText.length() - 1);
      }

      if (ChatAllowedCharacters.isAllowedCharacter(typedChar) && this.fontRendererObj.getStringWidth(unformattedText + typedChar) <= 90) {
         unformattedText = unformattedText + typedChar;
      }

      this.tileSign.signText[this.editLine] = new ChatComponentText(unformattedText);
      if (keyCode == 1) {
         this.actionPerformed(this.doneBtn);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, I18n.format("sign.edit"), this.width / 2, 40, 16777215);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.pushMatrix();
      GlStateManager.translate(this.width / 2, 0.0F, 50.0F);
      float f = 93.75F;
      GlStateManager.scale(-f, -f, -f);
      GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
      Block blockType = this.tileSign.getBlockType();
      if (blockType == Blocks.standing_sign) {
         float f2 = this.tileSign.getBlockMetadata() * 360 / 16.0F;
         GlStateManager.rotate(f2, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(0.0F, -1.0625F, 0.0F);
      } else {
         int blockMetadata = this.tileSign.getBlockMetadata();
         float f2 = 0.0F;
         if (blockMetadata == 2) {
            f2 = 180.0F;
         }

         if (blockMetadata == 4) {
            f2 = 90.0F;
         }

         if (blockMetadata == 5) {
            f2 = -90.0F;
         }

         GlStateManager.rotate(f2, 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(0.0F, -1.0625F, 0.0F);
      }

      if (this.updateCounter / 6 % 2 == 0) {
         this.tileSign.lineBeingEdited = this.editLine;
      }

      TileEntityRendererDispatcher.instance.renderTileEntityAt(this.tileSign, -0.5, -0.75, -0.5, 0.0F);
      this.tileSign.lineBeingEdited = -1;
      GlStateManager.popMatrix();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }
}
