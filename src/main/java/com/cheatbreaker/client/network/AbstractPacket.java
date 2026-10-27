package com.cheatbreaker.client.network;

import com.cheatbreaker.client.network.packet.BlockedServerPacket;
import com.cheatbreaker.client.network.packet.BooleanStringPacket;
import com.cheatbreaker.client.network.packet.ChallengeRequestPacket;
import com.cheatbreaker.client.network.packet.ChallengeResponsePacket;
import com.cheatbreaker.client.network.packet.ConnectionAcceptedPacket;
import com.cheatbreaker.client.network.packet.CosmeticSettingsPacket;
import com.cheatbreaker.client.network.packet.CrashReportPacket;
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
import com.cheatbreaker.client.network.packet.HeartbeatPacket;
import com.cheatbreaker.client.network.packet.IntegerListPacket;
import com.cheatbreaker.client.network.packet.KickPacket;
import com.cheatbreaker.client.network.packet.MessagePacket;
import com.cheatbreaker.client.network.packet.NotificationPacket;
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
import com.cheatbreaker.client.network.packet.SetFriendRequestsEnabledPacket;
import com.cheatbreaker.client.network.packet.ShutdownPacket;
import com.cheatbreaker.client.network.packet.StopEmotePacket;
import com.cheatbreaker.client.network.packet.StringTriplePacket;
import com.cheatbreaker.client.network.packet.SyncProfilesPacket;
import com.cheatbreaker.client.network.packet.ToggleEntryPacket;
import com.cheatbreaker.client.network.packet.UnusedEmptyPacket;
import com.cheatbreaker.client.network.packet.ViolationReportPacket;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import io.netty.buffer.Unpooled;
import java.io.IOException;

public abstract class AbstractPacket {
   public static BiMap<Class, Integer> biMap = HashBiMap.create();
   public Object attachment;

   public void writeByteArray(PacketBuffer packetBuffer, byte[] bytes) {
      packetBuffer.getByteBuf().writeShort(bytes.length);
      packetBuffer.getByteBuf().writeBytes(bytes);
   }

   public byte[] readByteArray(PacketBuffer packetBuffer) {
      short readShort = packetBuffer.getByteBuf().readShort();
      if (readShort < 0) {
         System.out.println("Key was smaller than nothing!  Weird key!");
         return null;
      } else {
         byte[] bytes = new byte[readShort];
         packetBuffer.getByteBuf().readBytes(bytes);
         return bytes;
      }
   }

   public abstract void read(PacketBuffer packetBuffer) throws IOException;

   public Object getAttachment() {
      return this.attachment;
   }

   public static AbstractPacket decode(PacketHandler packetHandler, byte[] bytes) {
      return decodeWithAttachment(packetHandler, bytes, null);
   }

   static {
      register(0, EncryptionRequestPacket.class);
      register(1, EncryptionResponsePacket.class);
      register(68, ConnectionAcceptedPacket.class);
      register(25, ChallengeResponsePacket.class);
      register(32, ChallengeRequestPacket.class);
      register(37, HeartbeatPacket.class);
      register(3, NotificationPacket.class);
      register(1056, ServerChatMessagePacket.class);
      register(2, MessagePacket.class);
      register(19, RequestPlayerCosmeticsPacket.class);
      register(34, SyncProfilesPacket.class);
      register(64, UnusedEmptyPacket.class);
      register(24, ViolationReportPacket.class);
      register(35, ProcessListRequestPacket.class);
      register(36, ProcessListResponsePacket.class);
      register(8, PlayerCosmeticsPacket.class);
      register(20, CosmeticSettingsPacket.class);
      register(60, RecentCosmeticsPacket.class);
      register(23, CrashReportPacket.class);
      register(69, KickPacket.class);
      register(33, ShutdownPacket.class);
      register(38, IntegerListPacket.class);
      register(39, PlayEmoteRequestPacket.class);
      register(51, PlayEmotePacket.class);
      register(56, EmoteWheelPacket.class);
      register(57, OwnedEmotesPacket.class);
      register(58, StopEmotePacket.class);
      register(59, PlayerStopEmotePacket.class);
      register(4, FriendListPacket.class);
      register(5, FriendMessagePacket.class);
      register(7, FriendRequestsPacket.class);
      register(9, FriendRequestReceivedPacket.class);
      register(16, OutgoingFriendRequestPacket.class);
      register(17, FriendRemovedPacket.class);
      register(18, FriendStatusPacket.class);
      register(21, FriendRequestResponsePacket.class);
      register(22, SetFriendRequestsEnabledPacket.class);
      register(101, ToggleEntryPacket.class);
      register(102, StringTriplePacket.class);
      register(6, FriendServerPacket.class);
      register(103, BooleanStringPacket.class);
      register(1100, BlockedServerPacket.class);
      register(1101, RemoveBlockedServerPacket.class);
   }

   public void setAttachment(Object obj) {
      this.attachment = obj;
   }

   public static AbstractPacket decodeWithAttachment(PacketHandler packetHandler, byte[] bytes, Object obj) {
      PacketBuffer packetBuffer = new PacketBuffer(Unpooled.wrappedBuffer(bytes));
      int readVarInt = packetBuffer.readVarInt();
      Class clazz = (Class)biMap.inverse().get(readVarInt);
      if (clazz != null) {
         try {
            AbstractPacket newInstance = (AbstractPacket)clazz.newInstance();
            if (obj != null) {
               newInstance.setAttachment(obj);
            }

            newInstance.read(packetBuffer);
            return newInstance;
         } catch (IOException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
         }
      }

      return null;
   }

   public abstract void write(PacketBuffer packetBuffer) throws IOException;

   public static byte[] encode(AbstractPacket abstractPacket) {
      PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
      packetBuffer.writeVarInt((Integer)biMap.get(abstractPacket.getClass()));

      try {
         abstractPacket.write(packetBuffer);
      } catch (IOException e) {
         e.printStackTrace();
      }

      return packetBuffer.getByteBuf().array();
   }

   public static void register(int n, Class clazz) {
      if (biMap.containsKey(clazz)) {
         throw new IllegalArgumentException("Duplicate packet class (" + clazz.getSimpleName() + "), already used by " + biMap.get(clazz));
      }

      if (biMap.containsValue(n)) {
         throw new IllegalArgumentException("Duplicate packet ID (" + n + "), already used by " + biMap.inverse().get(n));
      }

      biMap.put(clazz, n);
   }

   public abstract void handle(PacketHandler packetHandler);
}
