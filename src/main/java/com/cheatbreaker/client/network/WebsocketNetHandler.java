package com.cheatbreaker.client.network;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.chat.ChatPrinter;
import com.cheatbreaker.client.config.Profile;
import com.cheatbreaker.client.cosmetic.Cosmetic;
import com.cheatbreaker.client.cosmetic.CosmeticType;
import com.cheatbreaker.client.cosmetic.CosmeticsManager;
import com.cheatbreaker.client.cosmetic.RecentCosmeticsManager;
import com.cheatbreaker.client.discord.DiscordLinkData;
import com.cheatbreaker.client.discord.DiscordLinkState;
import com.cheatbreaker.client.emote.AbstractEmote;
import com.cheatbreaker.client.event.ServerHandshakeEvent;
import com.cheatbreaker.client.event.StringBooleanIntEvent;
import com.cheatbreaker.client.friend.FriendOnlineStatus;
import com.cheatbreaker.client.friends.Friend;
import com.cheatbreaker.client.friends.FriendRequest;
import com.cheatbreaker.client.network.packet.BlockedServerPacket;
import com.cheatbreaker.client.network.packet.BooleanStringPacket;
import com.cheatbreaker.client.network.packet.ChallengeRequestPacket;
import com.cheatbreaker.client.network.packet.ChallengeResponsePacket;
import com.cheatbreaker.client.network.packet.ConnectionAcceptedPacket;
import com.cheatbreaker.client.network.packet.CosmeticData;
import com.cheatbreaker.client.network.packet.CosmeticSettingData;
import com.cheatbreaker.client.network.packet.CosmeticSettingsPacket;
import com.cheatbreaker.client.network.packet.EmoteWheelPacket;
import com.cheatbreaker.client.network.packet.EncryptionRequestPacket;
import com.cheatbreaker.client.network.packet.EncryptionResponsePacket;
import com.cheatbreaker.client.network.packet.FriendListPacket;
import com.cheatbreaker.client.network.packet.FriendMessagePacket;
import com.cheatbreaker.client.network.packet.FriendRemovedPacket;
import com.cheatbreaker.client.network.packet.FriendRequestReceivedPacket;
import com.cheatbreaker.client.network.packet.FriendRequestResponsePacket;
import com.cheatbreaker.client.network.packet.FriendRequestsPacket;
import com.cheatbreaker.client.network.packet.FriendServerPacket;
import com.cheatbreaker.client.network.packet.FriendStatusPacket;
import com.cheatbreaker.client.network.packet.IntegerListPacket;
import com.cheatbreaker.client.network.packet.KickPacket;
import com.cheatbreaker.client.network.packet.MessagePacket;
import com.cheatbreaker.client.network.packet.NotificationPacket;
import com.cheatbreaker.client.network.packet.OfflineFriendData;
import com.cheatbreaker.client.network.packet.OnlineFriendData;
import com.cheatbreaker.client.network.packet.OutgoingFriendRequestPacket;
import com.cheatbreaker.client.network.packet.OwnedEmotesPacket;
import com.cheatbreaker.client.network.packet.PlayEmotePacket;
import com.cheatbreaker.client.network.packet.PlayEmoteRequestPacket;
import com.cheatbreaker.client.network.packet.PlayerCosmeticsPacket;
import com.cheatbreaker.client.network.packet.PlayerStopEmotePacket;
import com.cheatbreaker.client.network.packet.ProcessListRequestPacket;
import com.cheatbreaker.client.network.packet.ProcessListResponsePacket;
import com.cheatbreaker.client.network.packet.RecentCosmeticsPacket;
import com.cheatbreaker.client.network.packet.RemoveBlockedServerPacket;
import com.cheatbreaker.client.network.packet.RequestPlayerCosmeticsPacket;
import com.cheatbreaker.client.network.packet.ServerChatMessagePacket;
import com.cheatbreaker.client.network.packet.ShutdownPacket;
import com.cheatbreaker.client.network.packet.StringTriplePacket;
import com.cheatbreaker.client.network.packet.SyncProfilesPacket;
import com.cheatbreaker.client.network.packet.ToggleEntryPacket;
import com.cheatbreaker.client.ui.component.FriendChatWindow;
import com.cheatbreaker.client.ui.component.FriendRequestElement;
import com.cheatbreaker.client.ui.component.UIElement;
import com.cheatbreaker.client.ui.notification.Notification;
import com.cheatbreaker.client.ui.screen.DiscordLinkScreen;
import com.cheatbreaker.client.ui.screen.FriendsScreen;
import com.cheatbreaker.client.ui.screen.PlayerViewer;
import com.cheatbreaker.client.util.ClientVersion;
import com.cheatbreaker.client.util.SneakyThrow;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import io.netty.buffer.Unpooled;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource.PSpecified;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.CryptManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Util;
import net.minecraft.util.Util.EnumOS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

public class WebsocketNetHandler extends WebSocketClient implements WebsocketPacketHandler {
   public Minecraft mc = Minecraft.getMinecraft();
   public List<String> requestedPlayers = new ArrayList<>();
   public String errorMessage;
   public String errorTitle;
   public Logger logger = LogManager.getLogger("Websocket NetHandler");

   @Override
   public void handleKick(KickPacket kickPacket) {
      String reason = kickPacket.getReason();
      System.out.println(reason);
      if (reason.contains("Your account did not authenticate in the alloted amount of time")
         && CheatBreaker.getInstance().getClientBuildType().equals("Production")) {
         ReconnectHandler.reconnectDisabled = true;
         if (this.isOpen()) {
            this.close();
         }
      } else {
         Minecraft.getMinecraft().shutdown();
      }
   }

   public void onMessage(String str) {
      if (this.isOpen()) {
         super.send(str);
      }
   }

   @Override
   public void handleBlockedServer(BlockedServerPacket blockedServerPacket) {
      CheatBreaker.getInstance().getBlockedServersManager().handleBlockedServerPacket(blockedServerPacket);
   }

   @Override
   public void handleFriendServer(FriendServerPacket friendServerPacket) {
      String playerId = friendServerPacket.getPlayerId();
      String server = friendServerPacket.getServer();
      Friend friend = (Friend)CheatBreaker.getInstance().getFriendsManager().getObject2ObjectOpenHashMap().get(playerId);
      if (friend != null) {
         friend.setServer(server);
      }
   }

   @Override
   public void handleBooleanString(BooleanStringPacket booleanStringPacket) {
   }

   @Override
   public void handleShutdown(ShutdownPacket shutdownPacket) {
      Minecraft.getMinecraft().hasCrashed = true;
      Minecraft.getMinecraft().running = false;
   }

   public WebsocketNetHandler(URI uri, Map<String, String> map) {
      super(uri, new Draft_6455(), map, 0);
   }

   @Override
   public void handlePlayerCosmetics(PlayerCosmeticsPacket playerCosmeticsPacket) {
      String playerId = playerCosmeticsPacket.getPlayerId();
      CosmeticsManager cosmeticsManager = CheatBreaker.getInstance().getCosmeticsManager();
      PlayerDataManager playerDataManager = CheatBreaker.getInstance().getPlayerDataManager();
      if (!playerId.contains("-")) {
         playerId = playerDataManager.formatText(playerCosmeticsPacket.getPlayerId());
      } else {
         playerId = playerCosmeticsPacket.getPlayerId();
      }

      playerDataManager.getPlayers().put(UUID.fromString(playerId), new PlayerData(playerCosmeticsPacket.getPlayerName(), playerCosmeticsPacket.getText(), "", playerCosmeticsPacket.getIntegerList(), playerCosmeticsPacket.getIntegerList2()));
      CheatBreaker.getInstance().getCosmeticsManager().handleString(playerId);
      boolean equalsIgnoreCase = playerDataManager.formatText(playerId).equalsIgnoreCase(playerDataManager.formatText(this.mc.getSession().getPlayerID()));
      boolean flag = false;
      boolean flag2 = false;
      boolean flag3 = false;

      for (CosmeticData cosmeticData : playerCosmeticsPacket.getCosmetics()) {
         try {
            Cosmetic createCosmetic = this.createCosmetic(playerId, cosmeticData);
            if (createCosmetic != null) {
               switch (createCosmetic.getCosmeticType()) {
                  case CAPE:
                     cosmeticsManager.getCosmeticList().add(createCosmetic);
                     break;
                  case EMOTE:
                     if (equalsIgnoreCase && !CheatBreaker.getInstance().getEmoteManager().getOwnedEmotes().contains(createCosmetic.getEmoteId())) {
                        CheatBreaker.getInstance().getEmoteManager().getOwnedEmotes().add(createCosmetic.getEmoteId());
                     }
                     break;
                  case WINGS:
                     cosmeticsManager.getCosmeticList2().add(createCosmetic);
                     break;
                  case HAT:
                     cosmeticsManager.getCosmeticList3().add(createCosmetic);
               }

               if (createCosmetic.isEquipped()) {
                  if (createCosmetic.getCosmeticType() == CosmeticType.CAPE) {
                     flag = true;
                  } else if (createCosmetic.getCosmeticType() == CosmeticType.WINGS) {
                     flag2 = true;
                  } else if (createCosmetic.getCosmeticType() == CosmeticType.HAT) {
                     flag3 = true;
                  }

                  if (equalsIgnoreCase) {
                     if (createCosmetic.getCosmeticType() == CosmeticType.CAPE) {
                        PlayerViewer.guiButtonPress = createCosmetic.getTexture();
                     } else if (createCosmetic.getCosmeticType() == CosmeticType.WINGS) {
                        PlayerViewer.previewPlayer.setCosmetic2(createCosmetic);
                     } else if (createCosmetic.getCosmeticType() == CosmeticType.HAT && PlayerViewer.previewPlayer != null) {
                        PlayerViewer.previewPlayer.setCosmetic3(createCosmetic);
                     }
                  }

                  EntityPlayer entityPlayer = this.mc.theWorld == null ? null : this.mc.theWorld.getPlayerEntityByUUID(UUID.fromString(playerId));
                  if (entityPlayer instanceof AbstractClientPlayer) {
                     if (createCosmetic.getCosmeticType() == CosmeticType.CAPE) {
                        entityPlayer.setCosmetic(createCosmetic);
                     } else if (createCosmetic.getCosmeticType() == CosmeticType.HAT) {
                        entityPlayer.setCosmetic3(createCosmetic);
                     } else {
                        entityPlayer.setCosmetic2(createCosmetic);
                     }
                  }
               }
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      EntityPlayer entityPlayer = this.mc.theWorld == null ? null : this.mc.theWorld.getPlayerEntityByUUID(UUID.fromString(playerId));
      if (!flag) {
         if (entityPlayer != null) {
            entityPlayer.setCosmetic(null);
         }

         if (equalsIgnoreCase) {
            PlayerViewer.guiButtonPress = null;
            if (PlayerViewer.previewPlayer != null) {
               PlayerViewer.previewPlayer.setCosmetic(null);
            }
         }
      }

      if (!flag2) {
         if (entityPlayer != null) {
            entityPlayer.setCosmetic2(null);
         }

         if (equalsIgnoreCase && PlayerViewer.previewPlayer != null) {
            PlayerViewer.previewPlayer.setCosmetic2(null);
         }
      }

      if (!flag3) {
         if (entityPlayer != null) {
            entityPlayer.setCosmetic3(null);
         }

         if (equalsIgnoreCase && PlayerViewer.previewPlayer != null) {
            PlayerViewer.previewPlayer.setCosmetic3(null);
         }
      }
   }

   public void onClose(int n, String str, boolean flag) {
      try {
         this.logger.info("Close: {} ({}) - {}", str, n, flag);
         CheatBreaker.getInstance().getEventBus().handleEvent(new StringBooleanIntEvent(n, str, flag));
         CheatBreaker.getInstance().setWebsocketConnected(false);
      } catch (Throwable e) {
         throw SneakyThrow.sneakyThrow(e);
      }
   }

   @Override
   public void handlePlayEmote(PlayEmotePacket playEmotePacket) {
      Minecraft minecraft = Minecraft.getMinecraft();
      if (minecraft.theWorld != null) {
         EntityPlayer playerEntityByUUID = minecraft.theWorld.getPlayerEntityByUUID(playEmotePacket.getPlayerId());
         if (playerEntityByUUID != null) {
            AbstractEmote createEmote = CheatBreaker.getInstance().getEmoteManager().createEmote(playEmotePacket.getEmoteId());
            if (createEmote != null && playerEntityByUUID instanceof AbstractClientPlayer) {
               CheatBreaker.getInstance().getEmoteManager().startEmote((AbstractClientPlayer)playerEntityByUUID, createEmote);
            }
         }
      }
   }

   @Override
   public void handleOwnedEmotes(OwnedEmotesPacket ownedEmotesPacket) {
      List ownedEmotes = CheatBreaker.getInstance().getEmoteManager().getOwnedEmotes();
      ownedEmotes.retainAll(ownedEmotesPacket.getOwnedEmotes());

      for (Integer integer : ownedEmotesPacket.getOwnedEmotes()) {
         if (!ownedEmotes.contains(integer)) {
            ownedEmotes.add(integer);
         }
      }

      CheatBreaker.getInstance().getEmoteManager().refreshLocalEmote();
   }

   @Override
   public void handleServerChatMessage(ServerChatMessagePacket serverChatMessagePacket) {
      ChatPrinter.print(serverChatMessagePacket.getText(), serverChatMessagePacket.isFlag(), serverChatMessagePacket.isFlag2());
   }

   @Override
   public void handleRemoveBlockedServer(RemoveBlockedServerPacket removeBlockedServerPacket) {
      CheatBreaker.getInstance().getBlockedServersManager().remove(removeBlockedServerPacket.getServerId());
   }

   @Override
   public void handleNotification(NotificationPacket notificationPacket) {
      String title = notificationPacket.getTitle();
      String message = notificationPacket.getMessage();
      CheatBreaker.getInstance()
         .getServerMessages()
         .add(
            EnumChatFormatting.DARK_GRAY
               + "["
               + EnumChatFormatting.RESET
               + notificationPacket.getTitle()
               + EnumChatFormatting.DARK_GRAY
               + "] "
               + EnumChatFormatting.RESET
               + notificationPacket.getMessage()
         );
      Notification.post(title, message);
      if (title.matches("(Discord Link - (.*))")) {
         long currentTimeMillis = System.currentTimeMillis();
         String replace = title.replace("Discord Link - ", "");
         if (replace.isEmpty()) {
            replace = null;
         }

         DiscordLinkData.text = replace;
         DiscordLinkData.expiresAt = currentTimeMillis + 120000L;
         DiscordLinkData.discordLinkState = DiscordLinkState.STARTED;
         if (DiscordLinkData.text != null) {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(replace), null);
            System.out.println("[CB] Copied " + replace + " to clipboard.");
         }
      } else if (message.equals("You already have an existing link key!")) {
         DiscordLinkData.discordLinkState = DiscordLinkState.IN_PROGRESS;
      } else if (message.equals("Your account is already linked!")) {
         DiscordLinkData.discordLinkState = DiscordLinkState.ALREADY_LINKED;
      } else if (title.equals("Link Code Expired!")) {
         if (this.mc.currentScreen instanceof DiscordLinkScreen) {
            DiscordLinkData.text = "Regenerating...";
            CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().sendPacket(new MessagePacket("link"));
         } else {
            DiscordLinkData.discordLinkState = DiscordLinkState.EXPIRED;
            DiscordLinkData.text = null;
            DiscordLinkData.expiresAt = 0L;
         }
      } else if (title.equals("Account Linked!")) {
         DiscordLinkData.discordLinkState = DiscordLinkState.SUCCESS;
      }
   }

   @Override
   public void handleIntegerList(IntegerListPacket integerListPacket) {
   }

   @Override
   public void handleOutgoingFriendRequest(OutgoingFriendRequestPacket outgoingFriendRequestPacket) {
      FriendRequest friendRequest = new FriendRequest(outgoingFriendRequestPacket.getPlayerName(), outgoingFriendRequestPacket.getPlayerId());
      CheatBreaker.getInstance().getFriendsManager().getObject2ObjectOpenHashMap2().put(outgoingFriendRequestPacket.getPlayerId(), friendRequest);
      FriendsScreen.getFriendsScreen().updateFriendRequest(friendRequest, true);
      friendRequest.setFlag(outgoingFriendRequestPacket.isFlag());
      Notification.post("Friend Request", "Request has been sent.");
   }

   @Override
   public void handleFriendRequests(FriendRequestsPacket friendRequestsPacket) {
      CheatBreaker.getInstance().getFriendsManager().getObject2ObjectOpenHashMap2().clear();

      for (JsonElement jsonElement : new JsonParser().parse(friendRequestsPacket.getJson()).getAsJsonObject().getAsJsonArray("bulk")) {
         JsonObject asJsonObject = jsonElement.getAsJsonObject();
         String asString = asJsonObject.get("uuid").getAsString();
         String asString2 = asJsonObject.get("name").getAsString();
         FriendRequest friendRequest = new FriendRequest(asString2, asString);
         CheatBreaker.getInstance().getFriendsManager().getObject2ObjectOpenHashMap2().put(asString, friendRequest);
         FriendsScreen.getFriendsScreen().updateFriendRequest(friendRequest, true);
      }
   }

   public Cosmetic createCosmetic(String str, CosmeticData cosmeticData) {
      CosmeticType cosmeticType = CosmeticType.get(cosmeticData.getType());
      if (cosmeticType == null) {
         return null;
      }

      if (cosmeticType == CosmeticType.EMOTE) {
         return new Cosmetic(str, Integer.parseInt(cosmeticData.getName()), cosmeticType);
      }

      String str2 = cosmeticData.getExtras() == null ? null : String.join(",", cosmeticData.getExtras());
      return new Cosmetic(cosmeticData.getId(), str, cosmeticData.getName(), cosmeticType, cosmeticData.getScale(), cosmeticData.isEquipped(), cosmeticData.getTexturePath(), str2);
   }

   @Override
   public void handleFriendRequestReceived(FriendRequestReceivedPacket friendRequestReceivedPacket) {
      String playerId = friendRequestReceivedPacket.getPlayerId();
      String playerName = friendRequestReceivedPacket.getPlayerName();
      FriendRequest friendRequest = new FriendRequest(playerName, playerId);
      CheatBreaker.getInstance().getFriendsManager().getObject2ObjectOpenHashMap2().put(playerId, friendRequest);
      FriendsScreen.getFriendsScreen().updateFriendRequest(friendRequest, true);
      if (CheatBreaker.getInstance().getWebsocketController().getFriendOnlineStatus() != FriendOnlineStatus.BUSY) {
         CheatBreaker.getInstance().getAudioManager().handleString("message");
         Notification.post("Friend Request", friendRequest.getText() + " wants to be your friend.");
      }
   }

   public MinecraftSessionService createSessionService() {
      return new YggdrasilAuthenticationService(this.mc.getProxy(), UUID.randomUUID().toString()).createMinecraftSessionService();
   }

   public void handleAuthResult(JsonObject jsonObject) {
      String asString = jsonObject.get("result").getAsString();
      if (asString.equals("SUCCESS")) {
         Notification.post(EnumChatFormatting.GREEN + "Connected", "Welcome, " + this.mc.getSession().getUsername() + ".");
      }
   }

   public void sendServer(String str) {
      if (this.isOpen()) {
         this.sendPacket(new FriendServerPacket("", str));
      }
   }

   @Override
   public void handleFriendRemoved(FriendRemovedPacket friendRemovedPacket) {
      String playerId = friendRemovedPacket.getPlayerId();
      Friend friend = CheatBreaker.getInstance().getFriendsManager().get(playerId);
      if (friend != null) {
         CheatBreaker.getInstance().getFriendsManager().getObject2ObjectOpenHashMap().remove(playerId);
         FriendsScreen.getFriendsScreen().updateFriend(friend, false);
      }
   }

   public void sendCosmeticSettings() {
      List list = CheatBreaker.getInstance().getCosmeticsManager().getList().stream().map(this::toCosmeticSettingData).collect(Collectors.toList());
      this.sendPacket(new CosmeticSettingsPacket(list));
   }

   public void sendFriendStatus() {
      this.sendPacket(
         new FriendStatusPacket("", "", 14303827, CheatBreaker.getInstance().getWebsocketController().getFriendOnlineStatus().ordinal(), true, "1.7.10")
      );
   }

   public CosmeticSettingData toCosmeticSettingData(Cosmetic cosmetic) {
      String str = cosmetic.getCosmeticType() == CosmeticType.EMOTE ? String.valueOf(cosmetic.getEmoteId()) : cosmetic.getName();
      return new CosmeticSettingData(
         cosmetic.getId(),
         cosmetic.isEquipped(),
         cosmetic.getCosmeticType().getText(),
         str,
         cosmetic.getScale(),
         cosmetic.getTexture() != null ? cosmetic.getTexture().toString() : ""
      );
   }

   public void sendPacket(AbstractPacket abstractPacket) {
      if (this.isOpen()) {
         try {
            this.logger
               .info(
                  CheatBreaker.getInstance().getChatPrefix()
                     + "[WS NetHandler] (OUT) id: "
                     + AbstractPacket.biMap.get(abstractPacket.getClass())
                     + " Name: "
                     + abstractPacket.getClass().getSimpleName()
               );
            this.send(AbstractPacket.encode(abstractPacket));
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   @Override
   public void handleProcessListRequest(ProcessListRequestPacket processListRequestPacket) {
      try {
         ProcessBuilder processBuilder = Util.getOSType() == EnumOS.WINDOWS
            ? new ProcessBuilder(System.getenv("windir") + "\\system32\\tasklist.exe")
            : new ProcessBuilder("ps", "aux");
         Process start = processBuilder.start();
         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(start.getInputStream()));
         ArrayList list = new ArrayList();

         String str;
         while ((str = bufferedReader.readLine()) != null) {
            list.add(str);
         }

         CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().sendPacket(new ProcessListResponsePacket(list));
         bufferedReader.close();
      } catch (Exception e) {
      }
   }

   @Override
   public void handlePlayEmoteRequest(PlayEmoteRequestPacket playEmoteRequestPacket) {
   }

   public void failConnection(String str, Exception exception, String str2) {
      String str3 = exception.getMessage() == null ? str2 : exception.getMessage();
      Notification.post(str, str3);
      this.errorTitle = str;
      this.errorMessage = str3;
      ReconnectHandler.reconnectDisabled = true;
      this.close();
   }

   @Override
   public void handleFriendStatus(FriendStatusPacket friendStatusPacket) {
      String playerId = friendStatusPacket.getPlayerId();
      String name = friendStatusPacket.getName();
      int iconColor = friendStatusPacket.getIconColor();
      boolean isOnline = friendStatusPacket.isOnline();
      Friend friend = (Friend)CheatBreaker.getInstance().getFriendsManager().getObject2ObjectOpenHashMap().get(playerId);
      if (friend == null) {
         friend = Friend.createFriendBuilder()
            .setFlag(isOnline)
            .setText(name)
            .setIntValue(iconColor)
            .setText3(playerId)
            .setFlag(isOnline)
            .setFriendOnlineStatus(FriendOnlineStatus.ONLINE)
            .createFriend();
         CheatBreaker.getInstance().getFriendsManager().getObject2ObjectOpenHashMap().put(playerId, friend);
         FriendsScreen.getFriendsScreen().updateFriend(friend, true);
      }

      if (friendStatusPacket.getStatusOrLastSeen() < 10L) {
         int statusOrLastSeen = (int)friendStatusPacket.getStatusOrLastSeen();
         FriendOnlineStatus friendOnlineStatus = FriendOnlineStatus.ONLINE;

         for (FriendOnlineStatus friendOnlineStatus2 : FriendOnlineStatus.values()) {
            if (friendOnlineStatus2.ordinal() == statusOrLastSeen) {
               friendOnlineStatus = friendOnlineStatus2;
            }
         }

         friend.setOnlineStatus(friendOnlineStatus);
      }

      friend.setOnline(isOnline);
      friend.setName(name);
      friend.setIconColor(iconColor);
      FriendsScreen.getFriendsScreen().getFriendsListPanel().setBounds();
      if (!isOnline) {
         friend.setOfflineSince(friendStatusPacket.getStatusOrLastSeen());
      }
   }

   @Override
   public void handleFriendMessage(FriendMessagePacket friendMessagePacket) {
      String playerId = friendMessagePacket.getPlayerId();
      String message = friendMessagePacket.getMessage();
      Friend friend = (Friend)CheatBreaker.getInstance().getFriendsManager().getObject2ObjectOpenHashMap().get(playerId);
      if (friend != null) {
         CheatBreaker.getInstance().getFriendsManager().handleString2(friend.getPlayerId(), message);
         if (CheatBreaker.getInstance().getWebsocketController().getFriendOnlineStatus() != FriendOnlineStatus.BUSY) {
            CheatBreaker.getInstance().getAudioManager().handleString("message");
            Notification.post(EnumChatFormatting.GREEN + friend.getName() + EnumChatFormatting.RESET + " says:", message);
         }

         for (UIElement uielement : FriendsScreen.getFriendsScreen().getElements()) {
            if (uielement instanceof FriendChatWindow && ((FriendChatWindow)uielement).getFriend() == friend) {
               CheatBreaker.getInstance().getFriendsManager().handleString(friend.getPlayerId());
            }
         }
      }
   }

   @Override
   public void handleStringTriple(StringTriplePacket stringTriplePacket) {
   }

   @Override
   public void handleEncryptionRequest(EncryptionRequestPacket encryptionRequestPacket) {
      SecretKey createNewSharedKey = CryptManager.createNewSharedKey();
      PublicKey decodePublicKey = CryptManager.decodePublicKey(encryptionRequestPacket.getPublicKey());
      byte[] serverIdHash = CryptManager.getServerIdHash("", decodePublicKey, createNewSharedKey);
      if (serverIdHash == null) {
         Notification.post("Error", "Unable to generate server hash.");
      } else {
         String str = new BigInteger(serverIdHash).toString(16);

         try {
            this.createSessionService().joinServer(this.mc.getSession().getProfile(), this.mc.getSession().getToken(), str);
         } catch (AuthenticationUnavailableException e) {
            this.failConnection("Authentication Unavailable", e, "Mojang's authentication services are unavailable.");
            return;
         } catch (InvalidCredentialsException e) {
            this.failConnection("Invalid Credentials", e, "Please login to connect to the player assets server.");
            return;
         } catch (AuthenticationException e) {
            this.failConnection("Authentication Error", e, "There was an error authenticating with Minecraft.");
            return;
         } catch (NullPointerException e) {
            this.close();
            return;
         }

         try {
            PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
            EncryptionResponsePacket encryptionResponsePacket = new EncryptionResponsePacket(
               CryptManager.encryptData(decodePublicKey, createNewSharedKey.getEncoded()), CryptManager.encryptData(decodePublicKey, encryptionRequestPacket.getVerifyToken())
            );
            encryptionResponsePacket.write(packetBuffer);
            this.sendPacket(encryptionResponsePacket);
            Path path = Paths.get(
               Minecraft.getMinecraft().mcDataDir.getAbsolutePath(),
               "config",
               "cheatbreaker-client-" + ClientVersion.text + "-" + CheatBreaker.getInstance().getBranch().replace("?", "unknown"),
               "profiles.txt"
            );
            File toFile = path.toFile();
            if (toFile.exists()) {
               this.sendPacket(new SyncProfilesPacket());
            }
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   public void close() {
      super.close();
      CheatBreaker.getInstance().getEmoteManager().getOwnedEmotes().clear();
   }

   public boolean isOpen() {
      return super.isOpen();
   }

   public void onOpen(ServerHandshake serverHandshake) {
      this.logger.info(CheatBreaker.getInstance().getChatPrefix() + "Connection established");
      if (Objects.equals(Minecraft.getMinecraft().getSession().getUsername(), Minecraft.getMinecraft().getSession().getPlayerID())) {
         this.close();
      } else {
         CheatBreaker.getInstance().getEventBus().handleEvent(new ServerHandshakeEvent(serverHandshake));
         this.sendEmoteWheel();
      }
   }

   @Override
   public void handleConnectionAccepted(ConnectionAcceptedPacket connectionAcceptedPacket) {
      CheatBreaker.getInstance().setWebsocketConnected(true);
   }

   public void onError(Exception exception) {
      try {
         System.out.println("Error: " + exception.getMessage());
         exception.printStackTrace();
      } catch (Throwable e) {
         throw SneakyThrow.sneakyThrow(e);
      }
   }

   @Override
   public void handleSyncProfiles(SyncProfilesPacket syncProfilesPacket) {
      try {
         File file = new File(
            Minecraft.getMinecraft().mcDataDir
               + File.separator
               + "config"
               + File.separator
               + "cheatbreaker-client-"
               + ClientVersion.text
               + "-"
               + CheatBreaker.getInstance().getBranch().replace("?", "unknown")
               + File.separator
               + "profiles.txt"
         );
         if (!file.exists()) {
            file.createNewFile();
         }

         try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write("################################");
            bufferedWriter.newLine();
            bufferedWriter.write("# MC_Client: PROFILES");
            bufferedWriter.newLine();
            bufferedWriter.write("################################");
            bufferedWriter.newLine();

            for (Profile profile : CheatBreaker.getInstance().profiles) {
               bufferedWriter.write(profile.getText() + ":" + profile.intValue);
               bufferedWriter.newLine();
            }

            bufferedWriter.close();
         } catch (Exception e) {
         }
      } catch (Exception e) {
      }
   }

   @Override
   public void handleFriendRequestResponse(FriendRequestResponsePacket friendRequestResponsePacket) {
      if (!friendRequestResponsePacket.isAccepted()) {
         CheatBreaker.getInstance().getFriendsManager().getObject2ObjectOpenHashMap2().remove(friendRequestResponsePacket.getPlayerId());
         FriendRequestElement friendRequestElement = null;

         for (FriendRequestElement friendRequestElement2 : FriendsScreen.getFriendsScreen().getFriendRequestsPanel().getList()) {
            if (friendRequestElement2.getFriendRequest().getText2().equals(friendRequestResponsePacket.getPlayerId())) {
               friendRequestElement = friendRequestElement2;
            }
         }

         if (friendRequestElement != null) {
            FriendsScreen.getFriendsScreen().getFriendRequestsPanel().getFriendRequestElementList().add(friendRequestElement);
            FriendsScreen.getFriendsScreen().updateFriendRequest(friendRequestElement.getFriendRequest(), false);
         }
      }
   }

   public void requestPlayerCosmetics(AbstractClientPlayer abstractClientPlayer) {
      String str;
      if (abstractClientPlayer.getGameProfile() != null
         && this.mc.thePlayer != null
         && !this.requestedPlayers.contains(str = abstractClientPlayer.getUniqueID().toString())
         && !str.equals(this.mc.thePlayer.getUniqueID().toString())) {
         this.requestedPlayers.add(str);
         this.sendPacket(new RequestPlayerCosmeticsPacket(str));
      }
   }

   public void onMessage(ByteBuffer byteBuffer) {
      this.handleRawPacket(new PacketBuffer(Unpooled.wrappedBuffer(byteBuffer.array())));
   }

   @Override
   public void handleFriendList(FriendListPacket friendListPacket) {
      CheatBreaker.getInstance().getFriendsManager().getObject2ObjectOpenHashMap().clear();
      List onlineFriends = friendListPacket.getOnlineFriends();
      List offlineFriends = friendListPacket.getOfflineFriends();
      System.out.println(offlineFriends.toArray().length);
      CheatBreaker.getInstance().getWebsocketController().setFlag(friendListPacket.isFlag());
      CheatBreaker.getInstance().getWebsocketController().setFlag2(friendListPacket.isAcceptFriendRequests());

      for (OnlineFriendData onlineFriendData : (Iterable<OnlineFriendData>)onlineFriends) {
         String playerId = onlineFriendData.getPlayerId();
         String name = onlineFriendData.getName();
         int n = onlineFriendData.getOwnOnlineStatus().ordinal();
         String server = onlineFriendData.getServer();
         FriendOnlineStatus friendOnlineStatus = FriendOnlineStatus.ONLINE;

         for (FriendOnlineStatus friendOnlineStatus2 : FriendOnlineStatus.values()) {
            if (friendOnlineStatus2.ordinal() == n) {
               friendOnlineStatus = friendOnlineStatus2;
            }
         }

         Friend createFriend = Friend.createFriendBuilder()
            .setText(name)
            .setText3(playerId)
            .setText2(server)
            .setFriendOnlineStatus(friendOnlineStatus)
            .setFlag(true)
            .setText4("Online")
            .createFriend();
         CheatBreaker.getInstance().getFriendsManager().getObject2ObjectOpenHashMap().put(playerId, createFriend);
         FriendsScreen.getFriendsScreen().updateFriend(createFriend, true);
      }

      for (OfflineFriendData offlineFriendData : (Iterable<OfflineFriendData>)offlineFriends) {
         String playerId = offlineFriendData.getPlayerId();
         String name = offlineFriendData.getName();
         Friend createFriend = Friend.createFriendBuilder()
            .setText(name)
            .setText3(playerId)
            .setText2("")
            .setFriendOnlineStatus(FriendOnlineStatus.ONLINE)
            .setFlag(false)
            .setText4("Online")
            .setLongValue(offlineFriendData.getLastSeen())
            .createFriend();
         CheatBreaker.getInstance().getFriendsManager().getObject2ObjectOpenHashMap().put(playerId, createFriend);
         FriendsScreen.getFriendsScreen().updateFriend(createFriend, true);
      }
   }

   @Override
   public void handleRecentCosmetics(RecentCosmeticsPacket recentCosmeticsPacket) {
      RecentCosmeticsManager.setRecentFromServer(recentCosmeticsPacket.getValues());
   }

   @Override
   public void handlePlayerStopEmote(PlayerStopEmotePacket playerStopEmotePacket) {
      Minecraft minecraft = Minecraft.getMinecraft();
      if (minecraft.theWorld != null) {
         EntityPlayer playerEntityByUUID = minecraft.theWorld.getPlayerEntityByUUID(playerStopEmotePacket.getPlayerId());
         if (playerEntityByUUID != null) {
            if (playerEntityByUUID instanceof AbstractClientPlayer) {
               CheatBreaker.getInstance().getEmoteManager().stopEmote((AbstractClientPlayer)playerEntityByUUID);
            }
         }
      }
   }

   @Override
   public void handleToggleEntry(ToggleEntryPacket toggleEntryPacket) {
      if (toggleEntryPacket.isAdd()) {
         CheatBreaker.getInstance().getFriendsTyping().add(new TimedEntry(toggleEntryPacket.getKey(), true));
      } else {
         CheatBreaker.getInstance().getFriendsTyping().removeIf(timedEntry -> timedEntry.getKey().equals(toggleEntryPacket.getKey()));
      }
   }

   @Override
   public void handleMessage(MessagePacket messagePacket) {
      CheatBreaker.getInstance().getServerMessages().add(messagePacket.getMessage());
   }

   public void sendEmoteWheel() {
      ArrayList list = new ArrayList();

      for (Integer integer : (Iterable<Integer>)CheatBreaker.getInstance().getKeybindSettings().emoteWheelQuickActions.getListValue()) {
         if (integer != null && integer >= 0) {
            list.add(integer);
         }
      }

      this.sendPacket(new EmoteWheelPacket(list));
   }

   public static byte[] encryptWithRsa(byte[] bytes, byte[] bytes2) throws java.security.NoSuchAlgorithmException, java.security.spec.InvalidKeySpecException, java.security.InvalidKeyException, javax.crypto.IllegalBlockSizeException, javax.crypto.NoSuchPaddingException, java.security.InvalidAlgorithmParameterException, javax.crypto.BadPaddingException {
      PublicKey generatePublic = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(bytes));
      Cipher instance = Cipher.getInstance("RSA/ECB/OAEPPadding");
      instance.init(1, generatePublic, new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSpecified.DEFAULT));
      return instance.doFinal(bytes2);
   }

   @Override
   public void handleChallengeRequest(ChallengeRequestPacket challengeRequestPacket) {
   }

   public void handleRawPacket(PacketBuffer packetBuffer) {
      int readVarInt = packetBuffer.readVarInt();
      Class clazz = (Class)AbstractPacket.biMap.inverse().get(readVarInt);

      try {
         AbstractPacket abstractPacket = clazz == null ? null : (AbstractPacket)clazz.newInstance();
         if (abstractPacket == null) {
            return;
         }

         this.logger.info(CheatBreaker.getInstance().getChatPrefix() + "[WS NetHandler] (IN) id: " + readVarInt + " Name: " + abstractPacket.getClass().getSimpleName());
         abstractPacket.read(packetBuffer);
         abstractPacket.handle(this);
      } catch (Exception e) {
         this.logger.error("Error from: " + clazz);
         e.printStackTrace();
      }
   }
}
