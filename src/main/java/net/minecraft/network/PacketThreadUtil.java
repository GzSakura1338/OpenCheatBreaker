package net.minecraft.network;

import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.IThreadListener;
import net.optifine.Config;

public class PacketThreadUtil {
   public static int lastDimensionId = Integer.MIN_VALUE;

   public static <T extends INetHandler> void checkThreadAndEnqueue(final Packet<T> packet, final T value, IThreadListener threadListener) throws ThreadQuickExitException {
      if (!threadListener.isCallingFromMinecraftThread()) {
         threadListener.addScheduledTask(new Runnable() {
            @Override
            public void run() {
               PacketThreadUtil.clientPreProcessPacket(packet);
               packet.processPacket(value);
            }
         });
         throw ThreadQuickExitException.INSTANCE;
      }

      clientPreProcessPacket(packet);
   }

   public static void clientPreProcessPacket(Packet packet) {
      if (packet instanceof S08PacketPlayerPosLook) {
         Config.getRenderGlobal().onPlayerPositionSet();
      }

      if (packet instanceof S07PacketRespawn) {
         S07PacketRespawn s07PacketRespawn = (S07PacketRespawn)packet;
         lastDimensionId = s07PacketRespawn.getDimensionID();
      } else if (packet instanceof S01PacketJoinGame) {
         S01PacketJoinGame s01PacketJoinGame = (S01PacketJoinGame)packet;
         lastDimensionId = s01PacketJoinGame.getDimension();
      } else {
         lastDimensionId = Integer.MIN_VALUE;
      }
   }
}
