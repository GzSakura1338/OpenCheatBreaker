package net.minecraft.network.play.server;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import java.io.IOException;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IChatComponent.Serializer;
import net.minecraft.world.WorldSettings.GameType;

public class S38PacketPlayerListItem implements Packet<INetHandlerPlayClient> {
   public List<S38PacketPlayerListItem.AddPlayerData> players = Lists.newArrayList();
   public S38PacketPlayerListItem.Action action;

   public S38PacketPlayerListItem() {
   }

   public S38PacketPlayerListItem(S38PacketPlayerListItem.Action actionIn, EntityPlayerMP... players) {
      this.action = actionIn;

      for (EntityPlayerMP entityPlayerMP : players) {
         this.players
            .add(
               new S38PacketPlayerListItem.AddPlayerData(
                  entityPlayerMP.getGameProfile(), entityPlayerMP.ping, entityPlayerMP.theItemInWorldManager.getGameType(), entityPlayerMP.getTabListDisplayName()
               )
            );
      }
   }

   public S38PacketPlayerListItem(S38PacketPlayerListItem.Action actionIn, Iterable<EntityPlayerMP> players) {
      this.action = actionIn;

      for (EntityPlayerMP entityPlayerMP : players) {
         this.players
            .add(
               new S38PacketPlayerListItem.AddPlayerData(
                  entityPlayerMP.getGameProfile(), entityPlayerMP.ping, entityPlayerMP.theItemInWorldManager.getGameType(), entityPlayerMP.getTabListDisplayName()
               )
            );
      }
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.action = buf.readEnumValue(S38PacketPlayerListItem.Action.class);
      int readVarIntFromBuffer = buf.readVarIntFromBuffer();

      for (int i = 0; i < readVarIntFromBuffer; i++) {
         GameProfile gameProfile = null;
         int n = 0;
         GameType gameType = null;
         IChatComponent chatComponent = null;
         switch (this.action) {
            case ADD_PLAYER:
               gameProfile = new GameProfile(buf.readUuid(), buf.readStringFromBuffer(16));
               int readVarIntFromBuffer2 = buf.readVarIntFromBuffer();
               int n2 = 0;

               for (; n2 < readVarIntFromBuffer2; n2++) {
                  String readStringFromBuffer = buf.readStringFromBuffer(32767);
                  String readStringFromBuffer2 = buf.readStringFromBuffer(32767);
                  if (buf.readBoolean()) {
                     gameProfile.getProperties().put(readStringFromBuffer, new Property(readStringFromBuffer, readStringFromBuffer2, buf.readStringFromBuffer(32767)));
                  } else {
                     gameProfile.getProperties().put(readStringFromBuffer, new Property(readStringFromBuffer, readStringFromBuffer2));
                  }
               }

               gameType = GameType.getByID(buf.readVarIntFromBuffer());
               n = buf.readVarIntFromBuffer();
               if (buf.readBoolean()) {
                  chatComponent = buf.readChatComponent();
               }
               break;
            case UPDATE_GAME_MODE:
               gameProfile = new GameProfile(buf.readUuid(), (String)null);
               gameType = GameType.getByID(buf.readVarIntFromBuffer());
               break;
            case UPDATE_LATENCY:
               gameProfile = new GameProfile(buf.readUuid(), (String)null);
               n = buf.readVarIntFromBuffer();
               break;
            case UPDATE_DISPLAY_NAME:
               gameProfile = new GameProfile(buf.readUuid(), (String)null);
               if (buf.readBoolean()) {
                  chatComponent = buf.readChatComponent();
               }
               break;
            case REMOVE_PLAYER:
               gameProfile = new GameProfile(buf.readUuid(), (String)null);
         }

         this.players.add(new S38PacketPlayerListItem.AddPlayerData(gameProfile, n, gameType, chatComponent));
      }
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeEnumValue(this.action);
      buf.writeVarIntToBuffer(this.players.size());

      for (S38PacketPlayerListItem.AddPlayerData addPlayerData : this.players) {
         switch (this.action) {
            case ADD_PLAYER:
               buf.writeUuid(addPlayerData.getProfile().getId());
               buf.writeString(addPlayerData.getProfile().getName());
               buf.writeVarIntToBuffer(addPlayerData.getProfile().getProperties().size());

               for (Property property : addPlayerData.getProfile().getProperties().values()) {
                  buf.writeString(property.getName());
                  buf.writeString(property.getValue());
                  if (property.hasSignature()) {
                     buf.writeBoolean(true);
                     buf.writeString(property.getSignature());
                  } else {
                     buf.writeBoolean(false);
                  }
               }

               buf.writeVarIntToBuffer(addPlayerData.getGameMode().getID());
               buf.writeVarIntToBuffer(addPlayerData.getPing());
               if (addPlayerData.getDisplayName() == null) {
                  buf.writeBoolean(false);
               } else {
                  buf.writeBoolean(true);
                  buf.writeChatComponent(addPlayerData.getDisplayName());
               }
               break;
            case UPDATE_GAME_MODE:
               buf.writeUuid(addPlayerData.getProfile().getId());
               buf.writeVarIntToBuffer(addPlayerData.getGameMode().getID());
               break;
            case UPDATE_LATENCY:
               buf.writeUuid(addPlayerData.getProfile().getId());
               buf.writeVarIntToBuffer(addPlayerData.getPing());
               break;
            case UPDATE_DISPLAY_NAME:
               buf.writeUuid(addPlayerData.getProfile().getId());
               if (addPlayerData.getDisplayName() == null) {
                  buf.writeBoolean(false);
               } else {
                  buf.writeBoolean(true);
                  buf.writeChatComponent(addPlayerData.getDisplayName());
               }
               break;
            case REMOVE_PLAYER:
               buf.writeUuid(addPlayerData.getProfile().getId());
         }
      }
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handlePlayerListItem(this);
   }

   public List<S38PacketPlayerListItem.AddPlayerData> getEntries() {
      return this.players;
   }

   public S38PacketPlayerListItem.Action getAction() {
      return this.action;
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this).add("action", this.action).add("entries", this.players).toString();
   }

   public enum Action {
      ADD_PLAYER,
      UPDATE_GAME_MODE,
      UPDATE_LATENCY,
      UPDATE_DISPLAY_NAME,
      REMOVE_PLAYER;
   }

   public class AddPlayerData {
      public GameProfile profile;
      public int ping;
      public GameType gamemode;
      public IChatComponent displayName;

      public AddPlayerData(GameProfile profile, int pingIn, GameType gamemodeIn, IChatComponent displayNameIn) {
         this.profile = profile;
         this.ping = pingIn;
         this.gamemode = gamemodeIn;
         this.displayName = displayNameIn;
      }

      public GameProfile getProfile() {
         return this.profile;
      }

      public int getPing() {
         return this.ping;
      }

      public GameType getGameMode() {
         return this.gamemode;
      }

      public IChatComponent getDisplayName() {
         return this.displayName;
      }

      @Override
      public String toString() {
         return MoreObjects.toStringHelper(this)
            .add("latency", this.ping)
            .add("gameMode", this.gamemode)
            .add("profile", this.profile)
            .add("displayName", this.displayName == null ? null : Serializer.componentToJson(this.displayName))
            .toString();
      }
   }
}
