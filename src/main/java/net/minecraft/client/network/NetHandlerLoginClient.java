package net.minecraft.client.network;

import com.cheatbreaker.client.CheatBreaker;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.math.BigInteger;
import java.security.PublicKey;
import java.util.UUID;
import javax.crypto.SecretKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S01PacketEncryptionRequest;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.CryptManager;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerLoginClient implements INetHandlerLoginClient {
    public static Logger logger = LogManager.getLogger();
    public NetworkManager connection;
    public Minecraft client;
    public GuiScreen parent;

    public NetHandlerLoginClient(NetworkManager networkManager, Minecraft mc, GuiScreen guiScreen) {
        this.connection = networkManager;
        this.client = mc;
        this.parent = guiScreen;
    }

    @Override
    public void handleEncryptionRequest(S01PacketEncryptionRequest packetIn) {
        final SecretKey createNewSharedKey = CryptManager.createNewSharedKey();
        String serverId = packetIn.getServerId();
        PublicKey publicKey = packetIn.getPublicKey();
        String str = new BigInteger(CryptManager.getServerIdHash(serverId, publicKey, createNewSharedKey)).toString(16);
        boolean flag = this.client.getCurrentServerData() == null || !this.client.getCurrentServerData().isLanServer();

        try {
            if (!CheatBreaker.getInstance().isWebsocketConnected() && !CheatBreaker.getInstance().getClientBuildType().equals("Production") && flag) {
                this.connection.closeChannel(new ChatComponentTranslation("disconnect.loginFailedInfo", "Not connected to the player assets server!"));
                return;
            }

            this.getSessionService().joinServer(this.client.getSession().getProfile(), this.client.getSession().getToken(), str);
        } catch (AuthenticationUnavailableException e) {
            if (flag) {
                this.connection
                    .closeChannel(
                        new ChatComponentTranslation("disconnect.loginFailedInfo", new ChatComponentTranslation("disconnect.loginFailedInfo.serversUnavailable"))
                    );
                return;
            }
        } catch (InvalidCredentialsException e) {
            if (flag) {
                this.connection
                    .closeChannel(
                        new ChatComponentTranslation("disconnect.loginFailedInfo", new ChatComponentTranslation("disconnect.loginFailedInfo.invalidSession"))
                    );
                return;
            }
        } catch (AuthenticationException e) {
            if (flag) {
                this.connection.closeChannel(new ChatComponentTranslation("disconnect.loginFailedInfo", e.getMessage()));
                return;
            }
        }

        this.connection.scheduleOutboundPacket(new C01PacketEncryptionResponse(createNewSharedKey, publicKey, packetIn.getNonce()), new GenericFutureListener() {
            public void operationComplete(Future future) {
                NetHandlerLoginClient.this.connection.enableEncryption(createNewSharedKey);
            }
        });
    }

    public MinecraftSessionService getSessionService() {
        return new YggdrasilAuthenticationService(this.client.getProxy(), UUID.randomUUID().toString()).createMinecraftSessionService();
    }

    @Override
    public void handleLoginSuccess(S02PacketLoginSuccess packetIn) {
        this.connection.setConnectionState(EnumConnectionState.PLAY);
    }

    @Override
    public void onDisconnect(IChatComponent reason) throws java.io.IOException {
        this.client.displayGuiScreen(new GuiDisconnected(this.parent, "connect.failed", reason));
    }

    @Override
    public void onConnectionStateTransition(EnumConnectionState oldState, EnumConnectionState newState) {
        logger.debug("Switching protocol from " + oldState + " to " + newState);
        if (newState == EnumConnectionState.PLAY) {
            this.connection.setNetHandler(new NetHandlerPlayClient(this.client, this.parent, this.connection));
        }
    }

    @Override
    public void onNetworkTick() {
    }

    @Override
    public void handleDisconnect(S00PacketDisconnect packetIn) {
        this.connection.closeChannel(packetIn.getReason());
    }
}
