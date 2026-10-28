package net.minecraft.server.network;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import java.math.BigInteger;
import java.security.PrivateKey;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.SecretKey;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.CryptManager;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerLoginServer implements INetHandlerLoginServer {
    public static AtomicInteger AUTHENTICATOR_THREAD_ID = new AtomicInteger(0);
    public static Logger logger = LogManager.getLogger();
    public static Random RANDOM = new Random();
    public byte[] nonce = new byte[4];
    public NetHandlerLoginServer.LoginState currentLoginState = NetHandlerLoginServer.LoginState.HELLO;
    public String serverId = "";
    public MinecraftServer server;
    public int connectionTimer;
    public GameProfile loginGameProfile;
    public NetworkManager networkManager;
    public SecretKey secretKey;

    public NetHandlerLoginServer(MinecraftServer minecraftServer, NetworkManager networkManager) {
        this.server = minecraftServer;
        this.networkManager = networkManager;
        RANDOM.nextBytes(this.nonce);
    }

    @Override
    public void onNetworkTick() {
        if (this.currentLoginState == NetHandlerLoginServer.LoginState.READY_TO_ACCEPT) {
            this.acceptPlayer();
        }

        if (this.connectionTimer++ == 600) {
            this.closeConnection("Took too long to log in");
        }
    }

    public void closeConnection(String reason) {
        try {
            logger.info("Disconnecting " + this.getConnectionInfo() + ": " + reason);
            ChatComponentText chatComponentText = new ChatComponentText(reason);
            this.networkManager.scheduleOutboundPacket(new S00PacketDisconnect(chatComponentText));
            this.networkManager.closeChannel(chatComponentText);
        } catch (Exception e) {
            logger.error("Error whilst disconnecting player", e);
        }
    }

    public void acceptPlayer() {
        if (!this.loginGameProfile.isComplete()) {
            this.loginGameProfile = this.getOfflineProfile(this.loginGameProfile);
        }

        String allowUserToConnect = this.server.getConfigurationManager().allowUserToConnect(this.networkManager.getRemoteAddress(), this.loginGameProfile);
        if (allowUserToConnect != null) {
            this.closeConnection(allowUserToConnect);
        } else {
            this.currentLoginState = NetHandlerLoginServer.LoginState.ACCEPTED;
            this.networkManager.scheduleOutboundPacket(new S02PacketLoginSuccess(this.loginGameProfile));
            this.server
                .getConfigurationManager()
                .initializeConnectionToPlayer(this.networkManager, this.server.getConfigurationManager().createPlayerForUser(this.loginGameProfile));
        }
    }

    @Override
    public void onDisconnect(IChatComponent reason) {
        logger.info(this.getConnectionInfo() + " lost connection: " + reason.getUnformattedText());
    }

    public String getConnectionInfo() {
        return this.loginGameProfile != null
            ? this.loginGameProfile + " (" + this.networkManager.getRemoteAddress().toString() + ")"
            : String.valueOf(this.networkManager.getRemoteAddress());
    }

    @Override
    public void onConnectionStateTransition(EnumConnectionState oldState, EnumConnectionState newState) {
        Validate.validState(
            this.currentLoginState == NetHandlerLoginServer.LoginState.ACCEPTED || this.currentLoginState == NetHandlerLoginServer.LoginState.HELLO,
            "Unexpected change in protocol",
            new Object[0]
        );
        Validate.validState(newState == EnumConnectionState.PLAY || newState == EnumConnectionState.LOGIN, "Unexpected protocol " + newState, new Object[0]);
    }

    @Override
    public void processLoginStart(C00PacketLoginStart packetIn) {
        Validate.validState(this.currentLoginState == NetHandlerLoginServer.LoginState.HELLO, "Unexpected hello packet", new Object[0]);
        this.loginGameProfile = packetIn.getProfile();
        if (this.server.isServerInOnlineMode() && !this.networkManager.isLocalChannel()) {
            this.currentLoginState = NetHandlerLoginServer.LoginState.KEY;
            this.networkManager.scheduleOutboundPacket(new S01PacketEncryptionRequest(this.serverId, this.server.getKeyPair().getPublic(), this.nonce));
        } else {
            this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
        }
    }

    @Override
    public void processEncryptionResponse(C01PacketEncryptionResponse packetIn) {
        Validate.validState(this.currentLoginState == NetHandlerLoginServer.LoginState.KEY, "Unexpected key packet", new Object[0]);
        PrivateKey private2 = this.server.getKeyPair().getPrivate();
        if (!Arrays.equals(this.nonce, packetIn.decryptNonce(private2))) {
            throw new IllegalStateException("Invalid nonce!");
        }

        this.secretKey = packetIn.decryptSecretKey(private2);
        this.currentLoginState = NetHandlerLoginServer.LoginState.AUTHENTICATING;
        this.networkManager.enableEncryption(this.secretKey);
        (new Thread("User Authenticator #" + AUTHENTICATOR_THREAD_ID.incrementAndGet()) {
                @Override
                public void run() {
                    GameProfile loginGameProfile = NetHandlerLoginServer.this.loginGameProfile;

                    try {
                        String str = new BigInteger(
                                CryptManager.getServerIdHash(
                                    NetHandlerLoginServer.this.serverId,
                                    NetHandlerLoginServer.this.server.getKeyPair().getPublic(),
                                    NetHandlerLoginServer.this.secretKey
                                )
                            )
                            .toString(16);
                        NetHandlerLoginServer.this.loginGameProfile = NetHandlerLoginServer.this.server
                            .getMinecraftSessionService()
                            .hasJoinedServer(new GameProfile(null, loginGameProfile.getName()), str);
                        if (NetHandlerLoginServer.this.loginGameProfile != null) {
                            NetHandlerLoginServer.logger
                                .info(
                                    "UUID of player "
                                        + NetHandlerLoginServer.this.loginGameProfile.getName()
                                        + " is "
                                        + NetHandlerLoginServer.this.loginGameProfile.getId()
                                );
                            NetHandlerLoginServer.this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
                        } else if (NetHandlerLoginServer.this.server.isSinglePlayer()) {
                            NetHandlerLoginServer.logger.warn("Failed to verify username but will let them in anyway!");
                            NetHandlerLoginServer.this.loginGameProfile = NetHandlerLoginServer.this.getOfflineProfile(loginGameProfile);
                            NetHandlerLoginServer.this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
                        } else {
                            NetHandlerLoginServer.this.closeConnection("Failed to verify username!");
                            NetHandlerLoginServer.logger
                                .error("Username '" + NetHandlerLoginServer.this.loginGameProfile.getName() + "' tried to join with an invalid session");
                        }
                    } catch (AuthenticationUnavailableException e) {
                        if (NetHandlerLoginServer.this.server.isSinglePlayer()) {
                            NetHandlerLoginServer.logger.warn("Authentication servers are down but will let them in anyway!");
                            NetHandlerLoginServer.this.loginGameProfile = NetHandlerLoginServer.this.getOfflineProfile(loginGameProfile);
                            NetHandlerLoginServer.this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
                        } else {
                            NetHandlerLoginServer.this.closeConnection("Authentication servers are down. Please try again later, sorry!");
                            NetHandlerLoginServer.logger.error("Couldn't verify username because servers are unavailable");
                        }
                    }
                }
            })
            .start();
    }

    public GameProfile getOfflineProfile(GameProfile original) {
        UUID nameUUIDFromBytes = UUID.nameUUIDFromBytes(("OfflinePlayer:" + original.getName()).getBytes(Charsets.UTF_8));
        return new GameProfile(nameUUIDFromBytes, original.getName());
    }

    public enum LoginState {
        HELLO("HELLO", 0),
        KEY("KEY", 1),
        AUTHENTICATING("AUTHENTICATING", 2),
        READY_TO_ACCEPT("READY_TO_ACCEPT", 3),
        ACCEPTED("ACCEPTED", 4);

        LoginState(String str, int n) {
        }
    }
}
