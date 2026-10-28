package com.cheatbreaker.client.network;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.discord.DiscordRpcManager;
import com.cheatbreaker.client.event.S01PacketJoinGameEvent;
import com.cheatbreaker.client.event.SimpleEvent1;
import com.cheatbreaker.client.friend.FriendOnlineStatus;
import com.cheatbreaker.client.manager.AbstractManager;
import com.cheatbreaker.client.network.packet.HeartbeatPacket;
import com.cheatbreaker.client.util.ClientVersion;
import com.cheatbreaker.client.util.Logging;
import com.cheatbreaker.client.util.ParallelExecutor;
import com.cheatbreaker.client.util.SneakyThrow;
import com.google.common.base.MoreObjects;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WebsocketController extends AbstractManager {
    public DiscordRpcManager discordRpcManager = CheatBreaker.getInstance().getDiscordRpcManager();
    public boolean flag = false;
    public boolean flag2 = true;
    public FriendOnlineStatus friendOnlineStatus = FriendOnlineStatus.ONLINE;
    public Logger logger = LogManager.getLogger("WS Controller");

    public void onSimpleEvent1(SimpleEvent1 simpleEvent1) {
        this.cheatBreaker.playerassetsCheatbreakerNet.close();
    }

    public void setFlag2(boolean flag) {
        this.flag2 = flag;
    }

    public DiscordRpcManager getDiscordRpcManager() {
        return this.discordRpcManager;
    }

    public void onS01PacketJoinGameEvent(S01PacketJoinGameEvent s01PacketJoinGameEvent) {
        ServerData currentServerData = Minecraft.getMinecraft().getCurrentServerData();
        if (currentServerData != null) {
            this.onServerChange(currentServerData.serverIP, currentServerData.text, currentServerData.intValue);
        }
    }

    public void onServerChange(String str, String str2, int n) {
        try {
            this.cheatBreaker.getDiscordRpcManager().serverAddress = str;
            if (this.cheatBreaker.getKeybindSettings().showActiveAccount.getBooleanValue()) {
                this.discordRpcManager.updatePresence(this.mc.getSession().getUsername());
            } else {
                this.discordRpcManager.updatePresence(null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean flag = str.equals(str2 + ":" + n);
        if (!flag) {
            this.cheatBreaker.playerassetsCheatbreakerNet.sendServer(str);
            this.cheatBreaker.getDiscordRpcManager().serverAddress = str.isEmpty() ? "In-Menus" : str;
        } else {
            this.cheatBreaker.playerassetsCheatbreakerNet.sendServer("server");
            this.cheatBreaker.getDiscordRpcManager().serverAddress = "server";
        }
    }

    public Logger getLogger() {
        return this.logger;
    }

    @Override
    public void initialize() {
        try {
            this.connect();
        } catch (Throwable e) {
            throw SneakyThrow.sneakyThrow(e);
        }
    }

    public void startHeartbeat() {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                if (!CheatBreaker.isOfflineMode() && this.cheatBreaker.playerassetsCheatbreakerNet != null) {
                    this.cheatBreaker.playerassetsCheatbreakerNet.sendPacket(new HeartbeatPacket());
                }
            } catch (Exception e) {
                Logging.error("An error occurred while syncing with the server: " + e.getMessage());
            }
        }, 0L, 30L, TimeUnit.SECONDS);
    }

    public FriendOnlineStatus getFriendOnlineStatus() {
        return this.friendOnlineStatus;
    }

    public boolean isFlag2() {
        return this.flag2;
    }

    public WebsocketController() {
        this.cheatBreaker.getEventBus().checkClass2(S01PacketJoinGameEvent.class, this::onS01PacketJoinGameEvent);
        this.cheatBreaker.getEventBus().checkClass2(SimpleEvent1.class, this::onSimpleEvent1);
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean isFlag() {
        return this.flag;
    }

    public void connect() throws java.net.URISyntaxException {
        ParallelExecutor.execute(() -> {
            if (this.cheatBreaker.playerassetsCheatbreakerNet != null && this.cheatBreaker.playerassetsCheatbreakerNet.isOpen()) {
                this.cheatBreaker.playerassetsCheatbreakerNet.close();
            }

            this.logger.debug("Connecting to player assets server");
            Object2ObjectOpenHashMap object2ObjectOpenHashMap = new Object2ObjectOpenHashMap();
            object2ObjectOpenHashMap.put("username", this.mc.getSession().getUsername());
            object2ObjectOpenHashMap.put("playerId", this.cheatBreaker.getPlayerDataManager().formatUuid(this.mc.getSession().getPlayerID()));
            object2ObjectOpenHashMap.put("version", ClientVersion.version);
            object2ObjectOpenHashMap.put("client", CheatBreaker.getClientName());
            object2ObjectOpenHashMap.put("clientVersion", this.cheatBreaker.getClientVersion());
            object2ObjectOpenHashMap.put("clientBuildType", this.cheatBreaker.getClientBuildType());
            object2ObjectOpenHashMap.put("gitCommit", this.cheatBreaker.getGitCommit());
            object2ObjectOpenHashMap.put("branch", this.cheatBreaker.getBranch());
            object2ObjectOpenHashMap.put("os", System.getProperty("os.name"));
            object2ObjectOpenHashMap.put("arch", MoreObjects.firstNonNull(System.getenv("PROCESSOR_ARCHITECTURE"), System.getProperty("os.arch")));
            object2ObjectOpenHashMap.put("installationID", this.cheatBreaker.getPlayerDataManager().formatUuid(CheatBreaker.installationId));
            object2ObjectOpenHashMap.put("websocketToken", CheatBreaker.websocketToken);
            object2ObjectOpenHashMap.put("launcherVersion", CheatBreaker.launcherVersion);
            if (this.cheatBreaker.getDiscordRpcManager().serverAddress != null) {
                object2ObjectOpenHashMap.put("server", this.cheatBreaker.getDiscordRpcManager().serverAddress);
            }

            try {
                this.cheatBreaker.playerassetsCheatbreakerNet = new WebsocketNetHandler(new URI("wss://playerassets.cheatbreaker.net"), object2ObjectOpenHashMap);
                if (!CheatBreaker.isOfflineMode()) {
                    this.cheatBreaker.playerassetsCheatbreakerNet.connect();
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
                System.out.println("Failed to connect to the Player Assets Server");
                throw new RuntimeException(e);
            }
        });
    }

    public void setFriendOnlineStatus(FriendOnlineStatus friendOnlineStatus) {
        this.friendOnlineStatus = friendOnlineStatus;
    }
}
