package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.StringBooleanIntEvent;
import com.cheatbreaker.client.friend.FriendOnlineStatus;
import com.cheatbreaker.client.friends.Friend;
import com.cheatbreaker.client.friends.FriendRequest;
import com.cheatbreaker.client.network.PlayerData;
import com.cheatbreaker.client.network.PlayerDataManager;
import com.cheatbreaker.client.radio.RadioApi;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.component.ConsoleWindow;
import com.cheatbreaker.client.ui.component.FriendChatWindow;
import com.cheatbreaker.client.ui.component.FriendListEntry;
import com.cheatbreaker.client.ui.component.FriendRequestElement;
import com.cheatbreaker.client.ui.component.FriendRequestsPanel;
import com.cheatbreaker.client.ui.component.FriendsListPanel;
import com.cheatbreaker.client.ui.component.ListPanel;
import com.cheatbreaker.client.ui.component.RadioPanel;
import com.cheatbreaker.client.ui.component.SectionHeader;
import com.cheatbreaker.client.ui.component.UIElement;
import com.cheatbreaker.client.ui.notification.Notification;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class FriendsScreen extends UIScreen {
   public Queue<Notification> pendingNotifications = new LinkedList<>();
   public List<Notification> activeNotifications = new ArrayList<>();
   public FriendsListPanel friendsListPanel;
   public FriendRequestsPanel friendRequestsPanel;
   public long closedAt;
   public SectionHeader requests;
   public long openedAt;
   public SectionHeader friends;
   public static FriendsScreen friendsScreen;
   public RadioPanel radioPanel;
   public GuiScreen guiScreen;
   public ListPanel listPanel;
   public FriendChatWindow friendChatWindow;

   @Override
   public void mouseReleasedScaled(float f, float f2, int n) {
      this.dispatchMouseReleased(f, f2, n);
   }

   public static FriendsScreen getFriendsScreen() {
      return friendsScreen;
   }

   @Override
   public void keyTyped(char c, int n) {
      if (n == CheatBreaker.getInstance().getKeybindSettings().friendMenuKeybind.getIntValue()
            && !CheatBreaker.getInstance().getKeybindSettings().friendMenuKeybind.hasMouseBind()
            && CheatBreaker.getInstance().getKeybindSettings().friendMenuKeybind.areModifiersDown()
            && System.currentTimeMillis() - this.openedAt > 200L
         || n == 1) {
         this.closedAt = System.currentTimeMillis();
         this.mc.displayGuiScreen(this.guiScreen);
      }

      this.dispatchKeyTyped(c, n);
      if ((n == 59 || n == 41) && CheatBreaker.getInstance().getWebsocketController().isFlag()) {
         boolean flag = false;
         ConsoleWindow consoleWindow = null;

         for (UIElement uielement : this.elements) {
            if (uielement instanceof ConsoleWindow) {
               flag = true;
               consoleWindow = (ConsoleWindow)uielement;
               break;
            }
         }

         if (!flag) {
            UIElement[] uielements = new UIElement[1];
            ConsoleWindow consoleWindow2 = new ConsoleWindow();
            uielements[0] = consoleWindow2;
            this.addElements(uielements);
            consoleWindow2.setBounds(60.0F, 30.0F, 300.0F, 145.0F);
         } else if (!consoleWindow.isFocused()) {
            this.removeElements(consoleWindow);
         }
      }
   }

   public void updateFriend(Friend friend, boolean flag) {
      if (flag) {
         this.friendsListPanel.getList().add(new FriendListEntry(friend));
      } else {
         this.friendsListPanel.getList().removeIf(friendListEntry -> friendListEntry.getFriend() == friend);
      }

      this.friendsListPanel.setBounds();
   }

   public void onStringBooleanIntEvent(StringBooleanIntEvent stringBooleanIntEvent) {
      this.friendRequestsPanel.getList().clear();
      this.friendsListPanel.getList().clear();
   }

   public GuiScreen getGuiScreen() {
      return this.guiScreen;
   }

   @Override
   public void drawScreenScaled(float f, float f2) {
      GL11.glClear(256);
      this.process(this.getScaledWidth(), this.getScaledHeight());
      drawRect((float)0.0F, (float)0.0F, (float)140.0F, (float)this.getScaledHeight(), -14671840);
      drawRect((float)140.0F, (float)0.0F, (float)141.0F, (float)this.getScaledHeight(), -15395563);
      drawRect((float)0.0F, (float)0.0F, (float)140.0F, (float)28.0F, -15395563);
      drawRect(6, 6, 22, 22, Friend.computeInt(CheatBreaker.getInstance().getWebsocketController().getFriendOnlineStatus()));
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      ResourceLocation headTexture = CheatBreaker.getInstance().getFontManager().getHeadTexture(this.mc.getSession().getUsername());
      RenderUtils.drawSquareTextureViaRenderEngine(headTexture, 7.0F, 7.0F, 7.0F);
      PlayerDataManager playerDataManager = CheatBreaker.getInstance().getPlayerDataManager();
      PlayerData findPlayerData = playerDataManager.findPlayerData(this.mc.getSession().getUsername());
      int n = playerDataManager.findPlayerData(this.mc.getSession().getUsername()) == null
         ? 0
         : playerDataManager.findPlayerData(this.mc.getSession().getUsername()).getIntegerList2().get(0);
      float f3 = (n >> 16 & 0xFF) / 255.0F;
      float f4 = (n >> 8 & 0xFF) / 255.0F;
      float f5 = (n & 0xFF) / 255.0F;
      if (findPlayerData != null) {
         GlStateManager.color(f3, f4, f5, 1.0F);
         ResourceLocation resourceLocation = new ResourceLocation("client/logo_26_0.png");
         RenderUtils.drawSquareTextureViaRenderEngine(resourceLocation, 6.5F, 28.0F, 7.5F);
         CheatBreaker.getInstance().getFontManager().playRegular16.drawString(findPlayerData.getText(), 45.0F, 6.0F, -1);
         CheatBreaker.getInstance().getFontManager().playRegular12.drawString(CheatBreaker.getInstance().getFriendsManager().getText(), 45.0F, 15.0F, -5460820);
      } else {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         RenderUtils.drawSquareTextureViaRenderEngine(headTexture, 7.0F, 7.0F, 7.0F);
         CheatBreaker.getInstance().getFontManager().playRegular16.drawString(this.mc.getSession().getUsername(), 28.0F, 6.0F, -1);
         CheatBreaker.getInstance().getFontManager().playRegular12.drawString(CheatBreaker.getInstance().getFriendsManager().getText(), 28.0F, 15.0F, -5460820);
      }

      boolean flag = f > 6.0F && f < 134.0F && f2 > 6.0F && f2 < 22.0F;
      if (this.isTopmostAt(this.friends, f, f2) && flag && CheatBreaker.getInstance().isWebsocketConnected()) {
         drawRect((float)22.0F, (float)0.0F, (float)140.0F, (float)28.0F, -15395563);
         drawRect(24, 6, 40, 22, Friend.computeInt(FriendOnlineStatus.ONLINE));
         drawRect(42, 6, 58, 22, Friend.computeInt(FriendOnlineStatus.AWAY));
         drawRect(60, 6, 76, 22, Friend.computeInt(FriendOnlineStatus.BUSY));
         drawRect(78, 6, 94, 22, Friend.computeInt(FriendOnlineStatus.OFFLINE));
         GlStateManager.color(0.15F, 0.15F, 0.15F, 1.0F);
         RenderUtils.drawSquareTextureViaRenderEngine(headTexture, 7.0F, 25.0F, 7.0F);
         RenderUtils.drawSquareTextureViaRenderEngine(headTexture, 7.0F, 43.0F, 7.0F);
         RenderUtils.drawSquareTextureViaRenderEngine(headTexture, 7.0F, 61.0F, 7.0F);
         RenderUtils.drawSquareTextureViaRenderEngine(headTexture, 7.0F, 79.0F, 7.0F);
      }

      this.listPanel.renderAndTestHover(f, f2, this.isTopmostAt(this.requests, f, f2));
      drawRect((float)69.5F, (float)28.0F, (float)70.5F, (float)(28.0F + this.friends.getHeight()), -14869219);
      drawRect((float)0.0F, (float)(28.0F + this.friends.getHeight()), (float)140.0F, (float)(28.0F + this.friends.getHeight() + 1.0F), -15395563);
      this.renderElements(f, f2, this.friendsListPanel, this.friendRequestsPanel);
   }

   @Override
   public void updateScreen() {
      if (this.guiScreen != null) {
         this.guiScreen.updateScreen();
      }

      if (CheatBreaker.getInstance().isWebsocketConnected()) {
         this.friends.setTitle("FRIENDS (" + this.friendsListPanel.getList().size() + ")");
         this.requests
            .setTitle(
               "REQUESTS ("
                  + this.friendRequestsPanel.getList().stream().filter(friendRequestElement -> !friendRequestElement.getFriendRequest().isFlag()).count()
                  + " | "
                  + this.friendRequestsPanel.getList().stream().filter(friendRequestElement -> friendRequestElement.getFriendRequest().isFlag()).count()
                  + ")"
            );
      } else {
         this.friends.setTitle("FRIENDS");
         this.requests.setTitle("REQUESTS");
      }

      this.tickElements();
   }

   public FriendsListPanel getFriendsListPanel() {
      return this.friendsListPanel;
   }

   public static void setInstance(FriendsScreen friendsScreen2) {
      friendsScreen = friendsScreen2;
   }

   public ListPanel getListPanel() {
      return this.listPanel;
   }

   public List<Notification> getActiveNotifications() {
      return this.activeNotifications;
   }

   public long getOpenedAt() {
      return this.openedAt;
   }

   public void updateNotifications() {
      this.activeNotifications.removeIf(Notification::isExpired);
      if (!this.pendingNotifications.isEmpty()) {
         boolean flag = true;

         for (Notification notification : this.activeNotifications) {
            if (!notification.isSettled()) {
               flag = false;
            }
         }

         if (flag) {
            Notification poll = this.pendingNotifications.poll();
            poll.moveTo(this.getScaledHeight() - Notification.getHeight());
            this.activeNotifications.forEach(notification -> notification.moveTo(notification.getTargetY() - Notification.getHeight()));
            this.activeNotifications.add(poll);
         }
      }
   }

   public SectionHeader getRequests() {
      return this.requests;
   }

   @Override
   public void setWorldAndResolution(Minecraft mc, int n, int n2) {
      float scaledWidth = this.getScaledWidth();
      float scaledHeight = this.getScaledHeight();
      if (this.guiScreen != null) {
         this.guiScreen.setWorldAndResolution(mc, n, n2);
      }

      float scaledHeight2 = this.getScaledHeight();
      super.setWorldAndResolution(mc, n, n2);
      float scaledWidth2 = this.getScaledWidth();
      float scaledHeight3 = this.getScaledHeight();
      if (scaledWidth > 0.0F && scaledHeight > 0.0F) {
         float f = scaledWidth2 / scaledWidth;
         float f2 = scaledHeight3 / scaledHeight;

         for (UIElement uielement : this.elements) {
            if (uielement instanceof ConsoleWindow) {
               float f3 = uielement.getX() * f;
               float f4 = uielement.getY() * f2;
               float max = Math.max(200.0F, uielement.getWidth() * f);
               float max2 = Math.max(100.0F, uielement.getHeight() * f2);
               f3 = Math.min(f3, scaledWidth2 - max);
               f4 = Math.min(f4, scaledHeight3 - max2);
               f3 = Math.max(0.0F, f3);
               f4 = Math.max(0.0F, f4);
               uielement.setBounds(f3, f4, max, max2);
            }
         }
      }

      this.activeNotifications.forEach(notification -> this.shiftNotification(notification, this.getScaledHeight() + scaledHeight2));
      this.pendingNotifications.forEach(notification -> this.shiftNotification(notification, this.getScaledHeight() + scaledHeight2));
   }

   public FriendChatWindow getFriendChatWindow() {
      return this.friendChatWindow;
   }

   @Override
   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
      this.guiScreen = null;
      this.closeElements();
      this.mc.entityRenderer.run();
   }

   public FriendsScreen() {
      ArrayList list = new ArrayList();
      CheatBreaker.getInstance().getFriendsManager().getObject2ObjectOpenHashMap().forEach((str, friend) -> list.add(new FriendListEntry(friend)));
      UIElement[] uielements = new UIElement[]{
         this.friendsListPanel = new FriendsListPanel(list),
         this.friendRequestsPanel = new FriendRequestsPanel(new ArrayList<>()),
         this.requests = new SectionHeader("REQUESTS"),
         this.friends = new SectionHeader("FRIENDS"),
         this.radioPanel = new RadioPanel()
      };
      this.setElements(uielements);
      this.setUnfocusableElements(this.friendsListPanel, this.friendRequestsPanel, this.requests, this.friends);
      this.listPanel = this.friendsListPanel;
      CheatBreaker.getInstance().getEventBus().checkClass2(StringBooleanIntEvent.class, this::onStringBooleanIntEvent);
   }

   public void renderNotifications() {
      this.activeNotifications.forEach(Notification::render);
      if (this.mc != null
         && this.mc.currentScreen == null
         && (Boolean)CheatBreaker.getInstance().getKeybindSettings().pinRadioPlayer.getValue()
         && RadioApi.isPlaying()) {
         this.radioPanel.renderAndTestHover(0.0F, 0.0F, false);
      }
   }

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.openedAt = System.currentTimeMillis();
      if (CheatBreaker.getInstance().getGuiScreen() != this) {
         this.run2();
      }

      this.friends.setBounds(0.0F, 28.0F, 69.5F, 20.0F);
      this.requests.setBounds(70.5F, 28.0F, 69.5F, 20.0F);
      float f = 28.0F + this.friends.getHeight() + 1.0F;
      this.friendsListPanel.setBounds(0.0F, f, 140.0F, this.getScaledHeight() - f);
      this.friendRequestsPanel.setBounds(0.0F, f, 140.0F, this.getScaledHeight() - f);
      float f2 = 190.0F;
      this.radioPanel.setBounds(this.getScaledWidth() - f2 - 20.0F, 20.0F, f2, 28.0F);
   }

   public void notify(String str, String str2) {
      int width = Notification.getWidth();
      str2 = CheatBreaker.getInstance().getFontManager().playRegular14.wrapText(str2, width - 10);
      Notification notification = new Notification(str, str2.split("\n"), this.getScaledHeight());
      notification.setNoTitle(str.equals(""));
      this.pendingNotifications.add(notification);
   }

   public long getClosedAt() {
      return this.closedAt;
   }

   public void updateFriendRequest(FriendRequest friendRequest, boolean flag) {
      if (flag) {
         this.friendRequestsPanel.getList().add(new FriendRequestElement(friendRequest));
      } else {
         this.friendRequestsPanel.getList().removeIf(friendRequestElement -> friendRequestElement.getFriendRequest() == friendRequest);
      }

      this.friendRequestsPanel.setBounds();
   }

   public RadioPanel getRadioPanel() {
      return this.radioPanel;
   }

   @Override
   public void mouseClickedScaled(float f, float f2, int n) {
      this.listPanel.mouseClicked(f, f2, n, this.isTopmostAt(this.requests, f, f2));
      this.dispatchMouseClicked(f, f2, n, this.friendsListPanel, this.friendRequestsPanel);
      boolean isTopmostAt = this.isTopmostAt(this.friends, f, f2);
      if (isTopmostAt && this.friends.containsPoint(f, f2) && this.listPanel != this.friendsListPanel) {
         this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         this.listPanel = this.friendsListPanel;
      } else if (isTopmostAt && this.requests.containsPoint(f, f2) && this.listPanel != this.friendRequestsPanel) {
         this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         this.listPanel = this.friendRequestsPanel;
      }

      boolean flag = f > 6.0F && f < 134.0F && f2 > 6.0F && f2 < 22.0F;
      if (isTopmostAt && flag && CheatBreaker.getInstance().isWebsocketConnected()) {
         boolean flag2 = f > 24.0F && f < 40.0F;
         boolean flag3 = f > 42.0F && f < 58.0F;
         boolean flag4 = f > 60.0F && f < 76.0F;
         boolean flag5 = f > 78.0F && f < 94.0F;
         if (flag2) {
            CheatBreaker.getInstance().getWebsocketController().setFriendOnlineStatus(FriendOnlineStatus.ONLINE);
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         } else if (flag3) {
            CheatBreaker.getInstance().getWebsocketController().setFriendOnlineStatus(FriendOnlineStatus.AWAY);
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         } else if (flag4) {
            CheatBreaker.getInstance().getWebsocketController().setFriendOnlineStatus(FriendOnlineStatus.BUSY);
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         } else if (flag5) {
            CheatBreaker.getInstance().getWebsocketController().setFriendOnlineStatus(FriendOnlineStatus.OFFLINE);
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
         }

         CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().sendFriendStatus();
      }
   }

   @Override
   public void handleMouseInput() throws java.io.IOException {
      super.handleMouseInput();
      this.dispatchMouseInput();
   }

   public FriendRequestsPanel getFriendRequestsPanel() {
      return this.friendRequestsPanel;
   }

   public void openChat(Friend friend) {
      try {
         FriendChatWindow friendChatWindow = null;

         for (UIElement uielement : this.elements) {
            if (uielement instanceof FriendChatWindow) {
               friendChatWindow = (FriendChatWindow)uielement;
            }
         }

         if (friendChatWindow == null) {
            this.friendChatWindow = new FriendChatWindow(friend);
            this.elements.add(this.friendChatWindow);
            this.friendChatWindow.setBounds(170.0F, 30.0F, 245.0F, 150.0F);
         } else {
            this.elements.add(this.elements.remove(this.elements.indexOf(friendChatWindow)));
            friendChatWindow.setFriend(friend);
         }
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public Queue<Notification> getPendingNotifications() {
      return this.pendingNotifications;
   }

   public void notify(String str) {
      this.notify("", str);
   }

   public static FriendsScreen open(GuiScreen guiScreen) {
      if (guiScreen != friendsScreen) {
         getFriendsScreen().guiScreen = guiScreen;
      }

      return getFriendsScreen();
   }

   public void shiftNotification(Notification notification, float f) {
      notification.setStartY(notification.getStartY() - f);
      notification.setTargetY(notification.getTargetY() - f);
   }

   @Override
   public void drawScreen(int n, int n2, float f) {
      if (this.guiScreen != null) {
         this.guiScreen.drawScreen(-1, -1, f);
      }

      super.drawScreen(n, n2, f);
   }

   public SectionHeader getFriends() {
      return this.friends;
   }
}
