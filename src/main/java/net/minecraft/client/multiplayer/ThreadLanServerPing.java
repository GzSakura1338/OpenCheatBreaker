package net.minecraft.client.multiplayer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadLanServerPing extends Thread {
    public static AtomicInteger THREAD_ID = new AtomicInteger(0);
    public static Logger logger = LogManager.getLogger();
    public boolean isStopping = true;
    public String motd;
    public String address;
    public DatagramSocket socket;

    public ThreadLanServerPing(String str, String str2) throws IOException {
        super("LanServerPinger #" + THREAD_ID.incrementAndGet());
        this.motd = str;
        this.address = str2;
        this.setDaemon(true);
        this.socket = new DatagramSocket();
    }

    @Override
    public void run() {
        String pingResponse = getPingResponse(this.motd, this.address);
        byte[] bytes = pingResponse.getBytes();

        while (!this.isInterrupted() && this.isStopping) {
            try {
                InetAddress byName = InetAddress.getByName("224.0.2.60");
                DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length, byName, 4445);
                this.socket.send(datagramPacket);
            } catch (IOException e) {
                logger.warn("LanServerPinger: " + e.getMessage());
                break;
            }

            try {
                sleep(1500L);
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
        this.isStopping = false;
    }

    public static String getPingResponse(String str, String str2) {
        return "[MOTD]" + str + "[/MOTD][AD]" + str2 + "[/AD]";
    }

    public static String getMotdFromPingResponse(String str) {
        int indexOf = str.indexOf("[MOTD]");
        if (indexOf < 0) {
            return "missing no";
        }

        int indexOf2 = str.indexOf("[/MOTD]", indexOf + "[MOTD]".length());
        return indexOf2 < indexOf ? "missing no" : str.substring(indexOf + "[MOTD]".length(), indexOf2);
    }

    public static String getAdFromPingResponse(String str) {
        int indexOf = str.indexOf("[/MOTD]");
        if (indexOf < 0) {
            return null;
        }

        int indexOf2 = str.indexOf("[/MOTD]", indexOf + "[/MOTD]".length());
        if (indexOf2 >= 0) {
            return null;
        }

        int indexOf3 = str.indexOf("[AD]", indexOf + "[/MOTD]".length());
        if (indexOf3 < 0) {
            return null;
        }

        int indexOf4 = str.indexOf("[/AD]", indexOf3 + "[AD]".length());
        return indexOf4 < indexOf3 ? null : str.substring(indexOf3 + "[AD]".length(), indexOf4);
    }
}
