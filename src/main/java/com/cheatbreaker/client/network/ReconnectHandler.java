package com.cheatbreaker.client.network;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.ServerHandshakeEvent;
import com.cheatbreaker.client.event.StringBooleanIntEvent;
import com.cheatbreaker.client.network.packet.ViolationReportPacket;
import com.cheatbreaker.client.ui.component.FriendChatWindow;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraft.util.Util.EnumOS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReconnectHandler {
    public static long closeTime;
    public Random random = new Random();
    public static long reconnectDelay = 5000L;
    public static long connectTime;
    public static int secondsUntilReconnect = -1;
    public ScheduledFuture<?> unusedFuture;
    public ScheduledFuture<?> reconnectTask;
    public static List<String> watchedDomains = Arrays.asList(
        "cheatbreaker.net", "optifine.net", "minecraft.net", "mojang.com", "microsoft.com", "dash-api.com", "minecraftservices.com", "discord.gg", "discord.com"
    );
    public ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);
    public static boolean reconnectDisabled = false;
    public Logger logger = LogManager.getLogger("Conn Thread");

    public void onServerHandshakeEvent(ServerHandshakeEvent serverHandshakeEvent) {
        connectTime = System.currentTimeMillis();
    }

    public void onStringBooleanIntEvent(StringBooleanIntEvent stringBooleanIntEvent) {
        closeTime = System.currentTimeMillis();
        if (closeTime - connectTime < 600000L) {
            if (secondsUntilReconnect > 0) {
                return;
            }

            long l = 7200000L;
            reconnectDelay = Math.min((int)((float)reconnectDelay * 1.2F), l);
            reconnectDelay = reconnectDelay + (long)((this.random.nextFloat() * 10.0F + 10.0F) * 1000.0F);
        } else {
            reconnectDelay = 30000L;
        }

        secondsUntilReconnect = (int)(reconnectDelay / 1000L);
        if (this.reconnectTask != null) {
            this.reconnectTask.cancel(true);
        }

        this.reconnectTask = this.executor.scheduleAtFixedRate(() -> {
            if (secondsUntilReconnect == 0) {
                try {
                    CheatBreaker.getInstance().getWebsocketController().connect();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            } else if (!CheatBreaker.getInstance().playerassetsCheatbreakerNet.isOpen() && !reconnectDisabled) {
                secondsUntilReconnect--;
            } else {
                this.reconnectTask.cancel(true);
            }
        }, 0L, 1L, TimeUnit.SECONDS);
    }

    public static byte[] getHostsPathBytes() {
        return Util.getOSType() == EnumOS.WINDOWS
            ? new byte[]{
                -87,
                52,
                40,
                -40,
                107,
                -104,
                -84,
                -49,
                73,
                -128,
                113,
                -3,
                -63,
                122,
                -80,
                95,
                89,
                -52,
                -51,
                -120,
                41,
                19,
                15,
                113,
                77,
                86,
                -64,
                -68,
                56,
                62,
                -120,
                13,
                65,
                43,
                41,
                9,
                -4,
                55,
                9,
                -8,
                14,
                -78,
                -13,
                88,
                46,
                -19,
                80,
                98
            }
            : new byte[]{27, -60, -120, -33, 14, 69, 42, -120, 19, -15, -99, -70, -9, -36, -21, 69};
    }

    public ReconnectHandler() {
        CheatBreaker.getInstance().getEventBus().checkClass2(ServerHandshakeEvent.class, this::onServerHandshakeEvent);
        CheatBreaker.getInstance().getEventBus().checkClass2(StringBooleanIntEvent.class, this::onStringBooleanIntEvent);
    }

    public void checkHostsFile() {
        try {
            byte[] hostsPathBytes = getHostsPathBytes();
            ObjectOpenHashSet objectOpenHashSet = new ObjectOpenHashSet();

            String str;
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(FriendChatWindow.decrypt(hostsPathBytes)))) {
                while ((str = bufferedReader.readLine()) != null) {
                    for (String str2 : watchedDomains) {
                        if (str.contains(str2)) {
                            objectOpenHashSet.add(str2);
                        }
                    }
                }
            } catch (IOException e) {
                this.logger.error("Failed to read the HOSTS file", e);
            }

            if (!objectOpenHashSet.isEmpty()) {
                if (CheatBreaker.getInstance().playerassetsCheatbreakerNet != null) {
                    CheatBreaker.getInstance().playerassetsCheatbreakerNet.sendPacket(new ViolationReportPacket(98, "CBSB: " + objectOpenHashSet));
                }

                Minecraft.getMinecraft().shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
