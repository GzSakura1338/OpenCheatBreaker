package com.cheatbreaker.client.util.mumble;

import com.cheatbreaker.client.util.Logging;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MumbleTalkingListener {
   public DatagramSocket socket;
   public volatile long lastTalkingUpdate;
   public static final String TALKING_HEADER = "CBTALK2";
   public static final long TALKING_TIMEOUT_NANOS = 1500000000L;
   public volatile boolean pluginAlive;
   public volatile long lastPluginPing;
   public static final long PLUGIN_TIMEOUT_NANOS = 6000000000L;
   public static final int PLUGIN_PORT = 47812;
   public volatile List<MumbleIdentity> talking = Collections.emptyList();
   public static final String PLUGIN_HEADER = "CBPLUGIN1";
   public static final String SELF_HEADER = "CBSELF1";
   public static final int LISTEN_PORT = 47811;

   public List<MumbleIdentity> getTalking() {
      return System.nanoTime() - this.lastTalkingUpdate > 1500000000L ? Collections.emptyList() : this.talking;
   }

   public synchronized void sendSelfIdentity(String str, String str2) {
      if (this.socket != null) {
         byte[] bytes = ("CBSELF1\n" + str + "\n" + str2 + "\n").getBytes(StandardCharsets.UTF_8);

         try {
            this.socket.send(new DatagramPacket(bytes, bytes.length, new InetSocketAddress(getLoopbackAddress(), 47812)));
         } catch (IOException e) {
         }
      }
   }

   public static String emptyToNull(String str) {
      return !str.isEmpty() && !"-".equals(str) ? str : null;
   }

   public void receiveLoop(DatagramSocket datagramSocket) {
      byte[] bytes = new byte[2048];
      DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);

      while (!datagramSocket.isClosed()) {
         try {
            datagramPacket.setLength(bytes.length);
            datagramSocket.receive(datagramPacket);
         } catch (IOException e) {
            return;
         }

         String[] split = new String(datagramPacket.getData(), 0, datagramPacket.getLength(), StandardCharsets.UTF_8).split("\n");
         if (split.length > 0 && "CBPLUGIN1".equals(split[0])) {
            this.lastPluginPing = System.nanoTime();
            this.pluginAlive = true;
         } else if (split.length != 0 && "CBTALK2".equals(split[0])) {
            ArrayList list = new ArrayList();

            for (int i = 1; i < split.length; i++) {
               String[] split2 = split[i].split("\t", -1);
               if (split2.length == 3 && !split2[2].isEmpty()) {
                  list.add(new MumbleIdentity(emptyToNull(split2[0]), emptyToNull(split2[1]), split2[2]));
               }
            }

            this.talking = Collections.unmodifiableList(list);
            this.lastTalkingUpdate = System.nanoTime();
         }
      }
   }

   public synchronized void start() {
      if (this.socket == null) {
         DatagramSocket datagramSocket;
         try {
            datagramSocket = new DatagramSocket(new InetSocketAddress(getLoopbackAddress(), 47811));
         } catch (IOException e) {
            Logging.warnTagged("Mumble Link", "Could not listen for the Mumble talking plugin on port %s: %s", 47811, e.toString());
            return;
         }

         this.socket = datagramSocket;
         Thread thread = new Thread(() -> this.receiveLoop(datagramSocket), "Mumble Talking Listener");
         thread.setDaemon(true);
         thread.start();
      }
   }

   public static InetAddress getLoopbackAddress() throws java.net.UnknownHostException {
      return InetAddress.getByAddress(new byte[]{127, 0, 0, 1});
   }

   public synchronized void stop() {
      if (this.socket != null) {
         this.socket.close();
         this.socket = null;
      }

      this.talking = Collections.emptyList();
      this.pluginAlive = false;
   }

   public boolean isPluginRunning() {
      return this.pluginAlive && System.nanoTime() - this.lastPluginPing < 6000000000L;
   }
}
