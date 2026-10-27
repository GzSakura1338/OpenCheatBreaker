package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.animation.CosineAnimation;
import com.cheatbreaker.client.friends.Friend;
import com.cheatbreaker.client.ui.screen.FriendsScreen;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public class FriendMessageWindow extends DraggableElement {
   public List<String> stringList = new ArrayList<>();
   public ScrollBar scrollBar = new ScrollBar(this);
   public Friend friend;
   public SectionHeader sectionHeader;
   public CosineAnimation cosineAnimation;

   public Friend getFriend() {
      return this.friend;
   }

   public FriendMessageWindow(Friend friend) {
      this.friend = friend;
      this.sectionHeader = new SectionHeader("X");
      this.cosineAnimation = new CosineAnimation(1500L);
      this.cosineAnimation.start();
      this.cosineAnimation.enableRepeat();
   }

   public float getFloatValue() {
      return this.cosineAnimation.getProgress() * 2.0F - 1.0F;
   }

   @Override
   public boolean mouseClicked(float f, float f2, int n, boolean flag) {
      if (!flag) {
         return false;
      }

      this.scrollBar.mouseClicked(f, f2, n, flag);
      this.sectionHeader.mouseClicked(f, f2, n, flag);
      if (this.sectionHeader.containsPoint(f, f2)) {
         this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         FriendsScreen.getFriendsScreen().removeElements(this);
         return true;
      }

      if (this.containsPoint(f, f2)) {
         this.startDrag(f, f2);
      }

      return false;
   }

   @Override
   public void setBounds(float f, float f2, float f3, float f4) {
      super.setBounds(f, f2, f3, f4);
      this.scrollBar.setBounds(f + f3 - 4.0F, f2, 4.0F, f4);
      this.scrollBar.setFloatValue4(f4);
      this.sectionHeader.setBounds(f + f3 - 12.0F, f2 + 2.0F, 10.0F, 10.0F);
   }

   public List<String> getStringList() {
      return this.stringList;
   }

   @Override
   public void render(float f, float f2, boolean flag) {
      this.updateDrag(f, f2);
      this.scrollBar.process3(f, f2, flag);
      Gui.drawRectWithOuterBorder(this.x, this.y, this.x + this.width, this.y + this.height, 0.5F, -16777216, -14869219);
      CheatBreaker.getInstance().getFontManager().playRegular14.drawString(this.friend.getName(), this.x + 4.0F, this.y + 4.0F, -1);
      Gui.drawRect(this.x + 3.0F, this.y + 15.0F, this.x + this.width - 3.0F, this.y + 15.5F, 805306367);
      if (this.stringList.isEmpty()) {
         Gui.drawRect(this.x + 4.0F, this.y + this.height - 9.0F, this.x + this.width - 4.0F, this.y + this.height - 5.0F, -13158601);
         float f3 = this.x + this.width / 2.0F - 10.0F + (this.width - 28.0F) * this.getFloatValue() / 2.0F;
         Gui.drawRect(f3, this.y + this.height - 9.0F, f3 + 20.0F, this.y + this.height - 5.0F, -4180940);
      }

      int n = 0;

      for (String str : this.stringList) {
         float f3 = this.x + 4.0F;
         float f4 = this.y + 18.0F;
         CheatBreaker.getInstance().getFontManager().playRegular14.drawString(str, f3, f4 + n * 10.0F, -1);
         n++;
      }

      this.scrollBar.render(f, f2, flag);
      this.sectionHeader.render(f, f2, flag);
   }
}
