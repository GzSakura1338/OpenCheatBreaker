package net.minecraft.realms;

import net.minecraft.client.multiplayer.ServerAddress;

public class RealmsServerAddress {
   public String host;
   public int port;

   public RealmsServerAddress(String hostIn, int portIn) {
      this.host = hostIn;
      this.port = portIn;
   }

   public String getHost() {
      return this.host;
   }

   public int getPort() {
      return this.port;
   }

   public static RealmsServerAddress parseString(String str) {
      ServerAddress fromString = ServerAddress.fromString(str);
      return new RealmsServerAddress(fromString.getIP(), fromString.getPort());
   }
}
