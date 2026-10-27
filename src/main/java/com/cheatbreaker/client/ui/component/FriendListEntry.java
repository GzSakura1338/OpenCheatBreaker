package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.animation.CosineAnimation;
import com.cheatbreaker.client.animation.LinearAnimation;
import com.cheatbreaker.client.friends.Friend;
import com.cheatbreaker.client.friends.FriendsManager;
import com.cheatbreaker.client.network.PlayerDataManager;
import com.cheatbreaker.client.network.packet.FriendRemovedPacket;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.screen.FriendsScreen;
import java.awt.Color;
import java.util.List;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public class FriendListEntry extends UIElement {
   public Friend friend;
   public CosineAnimation cosineAnimation;
   public static ResourceLocation garbage26Png = new ResourceLocation("client/icons/garbage-26.png");
   public static ResourceLocation logo26Png = new ResourceLocation("client/logo_26.png");
   public LinearAnimation linearAnimation;
   public static ResourceLocation youtubeIconPng = new ResourceLocation("client/icons/friends/youtube-icon.png");

   public FriendListEntry(Friend friend) {
      this.friend = friend;
      this.cosineAnimation = new CosineAnimation(1500L);
      this.linearAnimation = new LinearAnimation(200L);
      this.cosineAnimation.enableRepeat();
   }

   @Override
   public void render(float f, float f2, boolean flag) {
      if (flag && this.containsPoint(f, f2)) {
         Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, -13750738);
      }

      GlStateManager.pushMatrix();
      FriendsManager friendsManager = CheatBreaker.getInstance().getFriendsManager();
      if (friendsManager.getObject2ObjectOpenHashMap4().containsKey(this.friend.getPlayerId())) {
         List list = (List)friendsManager.getObject2ObjectOpenHashMap4().get(this.friend.getPlayerId());
         if (list != null && list.size() > 0) {
            if (!this.cosineAnimation.hasStarted()) {
               this.cosineAnimation.start();
            }

            Gui.drawRect(
               this.x,
               this.y,
               this.x + this.width,
               this.y + this.height,
               new Color(0.89F, 0.54F, 0.05F, 0.65F * (0.154F + this.cosineAnimation.getProgress())).getRGB()
            );
            CheatBreaker.getInstance()
               .getFontManager()
               .playBold18
               .drawCenteredString(String.valueOf(list.size()), this.x + this.width - 15.0F, this.y + 6.0F, -1);
         } else if (this.cosineAnimation.hasStarted() && this.cosineAnimation.isFinished()) {
            this.cosineAnimation.reset();
         }
      }

      Gui.drawRect(this.x, this.y - 0.5F, this.x + this.width, this.y, -1357572843);
      Gui.drawRect(this.x, this.y + this.height, this.x + this.width, this.y + this.height + 0.5F, -1357572843);
      Gui.drawRect(
         this.x + 4.0F, this.y + 3.0F, this.x + 20.0F, this.y + 19.0F, this.friend.isOnline() ? Friend.computeInt(this.friend.getOnlineStatus()) : -13158601
      );
      PlayerDataManager playerDataManager = CheatBreaker.getInstance().getPlayerDataManager();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      ResourceLocation headTexture = CheatBreaker.getInstance()
         .getFontManager()
         .getHeadTexture(EnumChatFormatting.getTextWithoutFormattingCodes(this.friend.getName()));
      RenderUtils.drawSquareTextureViaRenderEngine(headTexture, 7.0F, this.x + 5.0F, this.y + 4.0F);
      boolean flag2 = flag && this.containsPoint(f, f2) && f > this.x + this.width - 20.0F;
      float updateToggle = this.linearAnimation.updateToggle(flag2);
      float f3 = this.x + this.width - 20.5F * updateToggle;
      if (flag) {
         Gui.drawRect(f3, this.y, this.x + this.width, this.y + this.height, -52429);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
         RenderUtils.drawTexture(garbage26Png, f3 + 4.0F, this.y + 5.0F, 12.0F, 12.0F);
      }

      GlStateManager.popMatrix();

      int n;
      try {
         n = playerDataManager.findPlayerData(this.friend.getName()) == null
            ? this.friend.getIntValue()
            : playerDataManager.findPlayerData(this.friend.getName()).getIntegerList2().get(0);
      } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
         n = -1;
      }

      float f4 = (n >> 16 & 0xFF) / 255.0F;
      float f5 = (n >> 8 & 0xFF) / 255.0F;
      float f6 = (n & 0xFF) / 255.0F;
      float f7 = 24.0F;
      if (this.friend.isOnline()) {
         if (this.friend.getName().startsWith(EnumChatFormatting.LIGHT_PURPLE.toString())) {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            RenderUtils.drawSquareTextureViaRenderEngine(youtubeIconPng, 6.5F, this.x + f7, this.y + 4.0F);
         } else if (n != 0 && n != -1) {
            GlStateManager.color(f4, f5, f6, 1.0F);
            RenderUtils.drawSquareTextureViaRenderEngine(logo26Png, 6.5F, this.x + f7, this.y + 4.0F);
         }

         f7 = 40.0F;
      }

      CheatBreaker.getInstance()
         .getFontManager()
         .playRegular16
         .drawString(EnumChatFormatting.getTextWithoutFormattingCodes(this.friend.getName()), this.x + f7, this.y + 2.0F, -1);
      String str = this.cheatBreaker.getFriendsTyping().stream().anyMatch(timedEntry -> timedEntry.getKey().equals(this.friend.getPlayerId()))
         ? "Typing..."
         : this.friend.getText();
      float f8 = this.x + f7;
      float f9 = this.y + 11.0F;
      CheatBreaker.getInstance().getFontManager().playRegular12.drawString(str, f8, f9, -5460820);
   }

   @Override
   public boolean mouseClicked(float f, float f2, int n, boolean flag) {
      boolean flag2 = this.containsPoint(f, f2) && f > this.x + this.width - 20.0F && flag;
      if (!flag) {
         return false;
      } else if (flag2 && this.linearAnimation.isFinished()) {
         this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().sendPacket(new FriendRemovedPacket(this.friend.getPlayerId()));
         FriendsScreen.getFriendsScreen().getFriendsListPanel().getFriendListEntryList().add(this);
         CheatBreaker.getInstance().getFriendsManager().getObject2ObjectOpenHashMap().remove(this.friend.getPlayerId());
         return true;
      } else if (!flag2 && this.containsPoint(f, f2)) {
         this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         FriendsScreen.getFriendsScreen().openChat(this.friend);
         CheatBreaker.getInstance().getFriendsManager().handleString(this.friend.getPlayerId());
         return true;
      } else {
         return super.mouseClicked(f, f2, n, flag);
      }
   }

   public Friend getFriend() {
      return this.friend;
   }
}
