package net.minecraft.client.network;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ThreadLanServerPing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LanServerDetector {
   public static AtomicInteger THREAD_ID = new AtomicInteger(0);
   public static Logger logger = LogManager.getLogger();

   public static class LanServer {
      public String lanServerMotd;
      public String lanServerIpPort;
      public long timeLastSeen;

      public LanServer(String motd, String address) {
         this.lanServerMotd = motd;
         this.lanServerIpPort = address;
         this.timeLastSeen = Minecraft.getSystemTime();
      }

      public String getServerMotd() {
         return this.lanServerMotd;
      }

      public String getServerIpPort() {
         return this.lanServerIpPort;
      }

      public void updateLastSeen() {
         this.timeLastSeen = Minecraft.getSystemTime();
      }
   }

   public static class LanServerList {
      public List<LanServerDetector.LanServer> listOfLanServers = Lists.newArrayList();
      public boolean wasUpdated;

      public synchronized boolean getWasUpdated() {
         return this.wasUpdated;
      }

      public synchronized void setWasNotUpdated() {
         this.wasUpdated = false;
      }

      public synchronized List<LanServerDetector.LanServer> getLanServers() {
         return Collections.unmodifiableList(this.listOfLanServers);
      }

      public synchronized void addServer(String str, InetAddress inetAddress) {
         String motdFromPingResponse = ThreadLanServerPing.getMotdFromPingResponse(str);
         String adFromPingResponse = ThreadLanServerPing.getAdFromPingResponse(str);
         if (adFromPingResponse != null) {
            adFromPingResponse = inetAddress.getHostAddress() + ":" + adFromPingResponse;
            boolean flag = false;

            for (LanServerDetector.LanServer lanServer : this.listOfLanServers) {
               if (lanServer.getServerIpPort().equals(adFromPingResponse)) {
                  lanServer.updateLastSeen();
                  flag = true;
                  break;
               }
            }

            if (!flag) {
               this.listOfLanServers.add(new LanServerDetector.LanServer(motdFromPingResponse, adFromPingResponse));
               this.wasUpdated = true;
            }
         }
      }
   }

   public static class ThreadLanServerFind extends Thread {
      public LanServerDetector.LanServerList localServerList;
      public MulticastSocket socket;
      public InetAddress broadcastAddress;

      public ThreadLanServerFind(LanServerDetector.LanServerList lanServerList) throws IOException {
         super("LanServerDetector #" + LanServerDetector.THREAD_ID.incrementAndGet());
         this.localServerList = lanServerList;
         this.setDaemon(true);
         this.socket = new MulticastSocket(4445);
         this.broadcastAddress = InetAddress.getByName("224.0.2.60");
         this.socket.setSoTimeout(5000);
         this.socket.joinGroup(this.broadcastAddress);
      }

      @Override
      public void run() {
         byte[] bytes = new byte[1024];

         while (!this.isInterrupted()) {
            DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);

            try {
               this.socket.receive(datagramPacket);
            } catch (SocketTimeoutException e) {
               continue;
            } catch (IOException e) {
               LanServerDetector.logger.error("Couldn't ping server", e);
               break;
            }

            String str = new String(datagramPacket.getData(), datagramPacket.getOffset(), datagramPacket.getLength());
            LanServerDetector.logger.debug(datagramPacket.getAddress() + ": " + str);
            this.localServerList.addServer(str, datagramPacket.getAddress());
         }

         try {
            this.socket.leaveGroup(this.broadcastAddress);
         } catch (IOException e) {
         }

         this.socket.close();
      }
   }
}
