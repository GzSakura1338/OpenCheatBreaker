package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.friends.FriendRequest;
import com.cheatbreaker.client.network.packet.FriendRequestResponsePacket;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.screen.FriendsScreen;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class FriendRequestElement extends UIElement {
   public FriendRequest friendRequest;
   public static byte[] byteArray = new byte[]{
      107, -20, -16, 107, 16, 12, 30, 82, -34, -44, -106, 14, 91, -126, 45, -85, -63, 42, 106, -17, 19, 94, -92, -48, 91, 77, 116, -15, -116, 20, 36, -123
   };

   public FriendRequest getFriendRequest() {
      return this.friendRequest;
   }

   public FriendRequestElement(FriendRequest friendRequest) {
      this.friendRequest = friendRequest;
   }

   @Override
   public boolean mouseClicked(float f, float f2, int n, boolean flag) {
      if (!flag) {
         return false;
      }

      if (this.friendRequest.isFlag()) {
         boolean flag2 = f > this.x + 24.0F && f < this.x + 84.0F && f2 < this.y + this.height && f2 > this.y + 10.0F && flag;
         if (flag2) {
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().sendPacket(new FriendRequestResponsePacket(false, this.friendRequest.getText2()));
            FriendsScreen.getFriendsScreen().getFriendRequestsPanel().getFriendRequestElementList().add(this);
         }
      } else {
         boolean flag2 = f > this.x + 24.0F && f < this.x + 52.0F && f2 < this.y + this.height && f2 > this.y + 10.0F;
         boolean flag3 = f > this.x + 52.0F && f < this.x + 84.0F && f2 < this.y + this.height && f2 > this.y + 10.0F;
         if (flag2) {
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().sendPacket(new FriendRequestResponsePacket(true, this.friendRequest.getText2()));
            FriendsScreen.getFriendsScreen().getFriendRequestsPanel().getFriendRequestElementList().add(this);
         } else if (flag3) {
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().sendPacket(new FriendRequestResponsePacket(false, this.friendRequest.getText2()));
            FriendsScreen.getFriendsScreen().getFriendRequestsPanel().getFriendRequestElementList().add(this);
         }
      }

      return super.mouseClicked(f, f2, n, flag);
   }

   @Override
   public void render(float f, float f2, boolean flag) {
      if (flag && this.containsPoint(f, f2)) {
         Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, -13750738);
      }

      GlStateManager.pushMatrix();
      Gui.drawRect(this.x, this.y - 0.5F, this.x + this.width, this.y, -1357572843);
      Gui.drawRect(this.x, this.y + this.height, this.x + this.width, this.y + this.height + 0.5F, -1357572843);
      Gui.drawRect(this.x + 4.0F, this.y + 3.0F, this.x + 20.0F, this.y + 19.0F, -16747106);
      CheatBreaker.getInstance().getFontManager().playRegular16.drawString(this.friendRequest.getText(), this.x + 24.0F, this.y + 2.0F, -1);
      if (this.friendRequest.isFlag()) {
         boolean flag2 = f > this.x + 24.0F && f < this.x + 84.0F && f2 < this.y + this.height && f2 > this.y + 10.0F && flag;
         CheatBreaker.getInstance()
            .getFontManager()
            .playRegular14
            .drawString(I18n.format("gui.cancel").toUpperCase(), this.x + 24.0F, this.y + 11.0F, flag2 ? -52429 : 2147431219);
      } else {
         boolean flag2 = f > this.x + 24.0F && f < this.x + 52.0F && f2 < this.y + this.height && f2 > this.y + 10.0F && flag;
         boolean flag3 = f > this.x + 52.0F && f < this.x + 84.0F && f2 < this.y + this.height && f2 > this.y + 10.0F && flag;
         float f3 = this.x + 24.0F;
         float f4 = this.y + 11.0F;
         CheatBreaker.getInstance().getFontManager().playRegular14.drawString("ACCEPT", f3, f4, flag2 ? -13369549 : 2134114099);
         f3 = this.x + 56.0F;
         f4 = this.y + 11.0F;
         CheatBreaker.getInstance().getFontManager().playRegular14.drawString("DENY", f3, f4, flag3 ? -52429 : 2147431219);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      ResourceLocation headTexture = CheatBreaker.getInstance()
         .getFontManager()
         .getHeadTexture(EnumChatFormatting.getTextWithoutFormattingCodes(this.friendRequest.getText()));
      RenderUtils.drawSquareTextureViaRenderEngine(headTexture, 7.0F, this.x + 5.0F, this.y + 4.0F);
      GlStateManager.popMatrix();
   }
}
