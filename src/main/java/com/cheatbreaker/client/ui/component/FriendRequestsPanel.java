package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.network.packet.FriendRequestReceivedPacket;
import com.cheatbreaker.client.network.packet.SetFriendRequestsEnabledPacket;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.notification.Notification;
import com.cheatbreaker.client.ui.screen.FriendsScreen;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class FriendRequestsPanel extends ListPanel<FriendRequestElement> {
   public List<FriendRequestElement> friendRequestElementList = new ArrayList<>();
   public TextField filter = new TextField(CheatBreaker.getInstance().getFontManager().playRegular14, "Filter", 805306367, 1879048191);
   public TextField username = new TextField(CheatBreaker.getInstance().getFontManager().playRegular14, "Username", 805306367, 1879048191);
   public SectionHeader add = new SectionHeader("ADD");
   public SectionHeader sectionHeader = new SectionHeader("");
   public ScrollBar scrollBar = new ScrollBar(this);

   @Override
   public void onClose() {
      this.filter.onClose();
      this.scrollBar.onClose();
   }

   public SectionHeader getAdd() {
      return this.add;
   }

   public boolean checkFriendRequestElement(FriendRequestElement friendRequestElement) {
      return this.filter.getText().equals("")
         || EnumChatFormatting.getTextWithoutFormattingCodes(friendRequestElement.getFriendRequest().getText()).toLowerCase().startsWith(this.filter.getText().toLowerCase());
   }

   @Override
   public void render(float f, float f2, boolean flag) {
      if (!this.friendRequestElementList.isEmpty()) {
         this.list.removeAll(this.friendRequestElementList);
         FriendsScreen.getFriendsScreen().getFriendRequestsPanel().setBounds();
         this.friendRequestElementList.clear();
      }

      if (!CheatBreaker.getInstance().isWebsocketConnected()) {
         float f3 = this.x + this.width / 2.0F;
         float f4 = this.y + 10.0F;
         CheatBreaker.getInstance().getFontManager().playBold18.drawCenteredString("Connection lost", f3, f4, -1);
         f3 = this.x + this.width / 2.0F;
         f4 = this.y + 22.0F;
         CheatBreaker.getInstance().getFontManager().playRegular14.drawCenteredString("Please try again later.", f3, f4, -1);
      } else {
         GlStateManager.pushMatrix();
         GL11.glEnable(3089);
         FriendsScreen friendsScreen = FriendsScreen.getFriendsScreen();
         this.scrollBar.process3(f, f2, flag);
         RenderUtils.scissorScaled(
            (int)this.x,
            (int)this.y,
            (int)(this.x + this.width),
            (int)(this.y + this.height),
            (int)(friendsScreen.getScaledResolution().getScaleFactor() * friendsScreen.getUiScale()),
            (int)friendsScreen.getScaledHeight()
         );
         ImmutableList copyOf = ImmutableList.copyOf(this.list);
         UnmodifiableIterator unmodifiableIterator = copyOf.iterator();

         while (unmodifiableIterator.hasNext()) {
            FriendRequestElement friendRequestElement = (FriendRequestElement)unmodifiableIterator.next();
            if (this.checkFriendRequestElement(friendRequestElement)) {
               friendRequestElement.renderAndTestHover(f, f2 - this.scrollBar.getFloatValue3(), flag);
            }
         }

         if (copyOf.isEmpty()) {
            float f3 = this.x + this.width / 2.0F;
            float f4 = this.y + 30.0F;
            CheatBreaker.getInstance().getFontManager().playBold18.drawCenteredString("No friend requests", f3, f4, -1);
         }

         this.filter.renderAndTestHover(f, f2 - this.scrollBar.getFloatValue3(), true);
         this.username.renderAndTestHover(f, f2, true);
         this.add.renderAndTestHover(f, f2, true);
         this.sectionHeader
            .handleString(
               (CheatBreaker.getInstance().getWebsocketController().isFlag2() ? "Disable" : "Enable") + " incoming friend requests", f, f2, true
            );
         GL11.glDisable(3089);
         GlStateManager.popMatrix();
         this.scrollBar.render(f, f2, flag);
      }
   }

   @Override
   public boolean mouseClicked(float f, float f2, int n, boolean flag) {
      this.filter.mouseClicked(f, f2 - this.scrollBar.getFloatValue3(), n, flag);
      this.username.mouseClicked(f, f2 - this.scrollBar.getFloatValue3(), n, flag);
      if (this.filter.isFocused() && n == 1 && this.filter.getText().equals("")) {
         this.setBounds();
      }

      if (!flag) {
         return false;
      }

      this.add.mouseClicked(f, f2 - this.scrollBar.getFloatValue3(), n, flag);
      this.sectionHeader.mouseClicked(f, f2 - this.scrollBar.getFloatValue3(), n, flag);
      this.scrollBar.mouseClicked(f, f2, n, flag);
      if (this.add.containsPoint(f, f2 - this.scrollBar.getFloatValue3())) {
         this.run();
      }

      if (this.sectionHeader.containsPoint(f, f2 - this.scrollBar.getFloatValue3())) {
         CheatBreaker.getInstance()
            .getPlayerassetsCheatbreakerNet()
            .sendPacket(new SetFriendRequestsEnabledPacket(!CheatBreaker.getInstance().getWebsocketController().isFlag2()));
         CheatBreaker.getInstance().getWebsocketController().setFlag2(!CheatBreaker.getInstance().getWebsocketController().isFlag2());
         return false;
      }

      boolean flag2 = false;

      for (FriendRequestElement friendRequestElement : this.list) {
         if (this.checkFriendRequestElement(friendRequestElement)) {
            if (flag2) {
               break;
            }

            flag2 = friendRequestElement.mouseClicked(f, f2 - this.scrollBar.getFloatValue3(), n, flag);
         }
      }

      return flag2;
   }

   @Override
   public void tick() {
      this.filter.tick();
      this.username.tick();
      this.sectionHeader.tick();
      this.add.tick();
      this.scrollBar.tick();
   }

   public FriendRequestsPanel(List<FriendRequestElement> list) {
      super(list);
   }

   public List<FriendRequestElement> getFriendRequestElementList() {
      return this.friendRequestElementList;
   }

   @Override
   public void handleMouseInput() {
      this.scrollBar.handleMouseInput();
   }

   @Override
   public void keyTyped(char c, int n) {
      super.keyTyped(c, n);
      this.filter.keyTyped(c, n);
      this.username.keyTyped(c, n);
      this.sectionHeader.keyTyped(c, n);
      this.add.keyTyped(c, n);
      this.scrollBar.keyTyped(c, n);
      if (this.username.isFocused() && n == 28) {
         this.run();
      }

      this.setBounds(this.x, this.y, this.width, this.height);
   }

   @Override
   public boolean mouseReleased(float f, float f2, int n, boolean flag) {
      if (!flag) {
         return false;
      }

      this.filter.mouseReleased(f, f2 - this.scrollBar.getFloatValue3(), n, flag);
      this.username.mouseReleased(f, f2 - this.scrollBar.getFloatValue3(), n, flag);
      this.add.mouseReleased(f, f2 - this.scrollBar.getFloatValue3(), n, flag);
      this.scrollBar.mouseReleased(f, f2 - this.scrollBar.getFloatValue3(), n, flag);
      boolean flag2 = false;

      for (FriendRequestElement friendRequestElement : this.list) {
         if (this.checkFriendRequestElement(friendRequestElement)) {
            if (flag2) {
               break;
            }

            flag2 = friendRequestElement.mouseReleased(f, f2 - this.scrollBar.getFloatValue3(), n, flag);
         }
      }

      return flag2;
   }

   public void run() {
      if (!this.username.getText().isEmpty()) {
         this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         String text = this.username.getText();
         if (text.matches("([a-zA-Z0-9_]+)") && text.length() <= 16) {
            CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().sendPacket(new FriendRequestReceivedPacket("", this.username.getText()));
            this.username.setText("");
         } else {
            Notification.post(EnumChatFormatting.RED + "Error!", "1");
         }
      }
   }

   @Override
   public void setBounds(float f, float f2, float f3, float f4) {
      super.setBounds(f, f2, f3, f4);
      this.scrollBar.setBounds(f + f3 - 4.0F, f2, 4.0F, f4);
      byte b = 22;
      int n = 0;

      for (FriendRequestElement friendRequestElement : this.list) {
         if (this.checkFriendRequestElement(friendRequestElement)) {
            friendRequestElement.setBounds(f, f2 + 14.0F + n * 22, f3, 22.0F);
            n++;
         }
      }

      float f5 = 14 + this.list.size() * 22 + 30;
      if (f5 < f4) {
         f5 = f4;
      }

      this.filter.setBounds(0.0F, f2, f3, 13.0F);
      this.username.setBounds(0.0F, f2 + f5 - 13.0F, f3 - 35.0F, 13.0F);
      this.add.setBounds(f3 - 35.0F, f2 + f5 - 13.0F, 35.0F, 13.0F);
      this.sectionHeader.setBounds(0.0F, f2 + f5 - 26.0F, f3, 13.0F);
      this.scrollBar.setFloatValue4(f5);
   }

   public void setBounds() {
      this.setBounds(this.x, this.y, this.width, this.height);
   }

   public SectionHeader getSectionHeader() {
      return this.sectionHeader;
   }
}
