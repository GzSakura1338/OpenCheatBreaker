package net.minecraft.client.multiplayer;

import java.net.IDN;
import java.util.Hashtable;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

public class ServerAddress {
   public String ipAddress;
   public int serverPort;

   public ServerAddress(String address, int port) {
      this.ipAddress = address;
      this.serverPort = port;
   }

   public String getIP() {
      return IDN.toASCII(this.ipAddress);
   }

   public int getPort() {
      return this.serverPort;
   }

   public static ServerAddress fromString(String str) {
      if (str == null) {
         return null;
      }

      String[] split = str.split(":");
      if (str.startsWith("[")) {
         int indexOf = str.indexOf("]");
         if (indexOf > 0) {
            String substring = str.substring(1, indexOf);
            String trim = str.substring(indexOf + 1).trim();
            if (trim.startsWith(":") && trim.length() > 0) {
               trim = trim.substring(1);
               split = new String[]{substring, trim};
            } else {
               split = new String[]{substring};
            }
         }
      }

      if (split.length > 2) {
         split = new String[]{str};
      }

      String str2 = split[0];
      int n = split.length > 1 ? parseIntWithDefault(split[1], 25565) : 25565;
      if (n == 25565) {
         String[] serverAddress = getServerAddress(str2);
         str2 = serverAddress[0];
         n = parseIntWithDefault(serverAddress[1], 25565);
      }

      return new ServerAddress(str2, n);
   }

   public static String[] getServerAddress(String str) {
      try {
         String str2 = "com.sun.jndi.dns.DnsContextFactory";
         Class.forName("com.sun.jndi.dns.DnsContextFactory");
         Hashtable hashtable = new Hashtable();
         hashtable.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
         hashtable.put("java.naming.provider.url", "dns:");
         hashtable.put("com.sun.jndi.dns.timeout.retries", "1");
         InitialDirContext initialDirContext = new InitialDirContext(hashtable);
         Attributes attributes = initialDirContext.getAttributes("_minecraft._tcp." + str, new String[]{"SRV"});
         String[] split = attributes.get("srv").get().toString().split(" ", 4);
         return new String[]{split[3], split[2]};
      } catch (Throwable e) {
         return new String[]{str, Integer.toString(25565)};
      }
   }

   public static int parseIntWithDefault(String str, int n) {
      try {
         return Integer.parseInt(str.trim());
      } catch (Exception e) {
         return n;
      }
   }
}
