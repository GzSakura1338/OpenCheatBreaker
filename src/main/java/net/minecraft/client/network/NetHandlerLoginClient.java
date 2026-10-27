package net.minecraft.client.network;

import com.cheatbreaker.client.CheatBreaker;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.math.BigInteger;
import java.security.PublicKey;
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
import net.minecraft.network.login.server.S03PacketEnableCompression;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.CryptManager;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerLoginClient implements INetHandlerLoginClient {
   public static Logger logger = LogManager.getLogger();
   public NetworkManager networkManager;
   public Minecraft mc;
   public GuiScreen previousGuiScreen;
   public GameProfile gameProfile;

   public NetHandlerLoginClient(NetworkManager networkManagerIn, Minecraft mcIn, GuiScreen guiScreen) {
      this.networkManager = networkManagerIn;
      this.mc = mcIn;
      this.previousGuiScreen = guiScreen;
   }

   @Override
   public void handleEncryptionRequest(S01PacketEncryptionRequest packetIn) {
      final SecretKey createNewSharedKey = CryptManager.createNewSharedKey();
      String serverId = packetIn.getServerId();
      PublicKey publicKey = packetIn.getPublicKey();
      String str = new BigInteger(CryptManager.getServerIdHash(serverId, publicKey, createNewSharedKey)).toString(16);
      if (this.mc.getCurrentServerData() != null && this.mc.getCurrentServerData().isOnLAN()) {
         try {
            this.getSessionService().joinServer(this.mc.getSession().getProfile(), this.mc.getSession().getToken(), str);
         } catch (AuthenticationException e) {
            logger.warn("Couldn't connect to auth servers but will continue to join LAN");
         }
      } else {
         if (!CheatBreaker.getInstance().isWebsocketConnected() && !CheatBreaker.getInstance().getClientBuildType().equals("Production")) {
            this.networkManager.closeChannel(new ChatComponentTranslation("disconnect.loginFailedInfo", "Not connected to the player assets server!"));
            return;
         }

         try {
            this.getSessionService().joinServer(this.mc.getSession().getProfile(), this.mc.getSession().getToken(), str);
         } catch (AuthenticationUnavailableException e) {
            this.networkManager
               .closeChannel(
                  new ChatComponentTranslation("disconnect.loginFailedInfo", new ChatComponentTranslation("disconnect.loginFailedInfo.serversUnavailable"))
               );
            return;
         } catch (InvalidCredentialsException e) {
            this.networkManager
               .closeChannel(
                  new ChatComponentTranslation("disconnect.loginFailedInfo", new ChatComponentTranslation("disconnect.loginFailedInfo.invalidSession"))
               );
            return;
         } catch (AuthenticationException e) {
            this.networkManager.closeChannel(new ChatComponentTranslation("disconnect.loginFailedInfo", e.getMessage()));
            return;
         }
      }

      this.networkManager
         .sendPacket(new C01PacketEncryptionResponse(createNewSharedKey, publicKey, packetIn.getVerifyToken()), new GenericFutureListener<Future<? super Void>>() {
            public void operationComplete(Future<? super Void> future) throws Exception {
               NetHandlerLoginClient.this.networkManager.enableEncryption(createNewSharedKey);
            }
         });
   }

   public MinecraftSessionService getSessionService() {
      return this.mc.getSessionService();
   }

   @Override
   public void handleLoginSuccess(S02PacketLoginSuccess packetIn) {
      this.gameProfile = packetIn.getProfile();
      this.networkManager.setConnectionState(EnumConnectionState.PLAY);
      this.networkManager.setNetHandler(new NetHandlerPlayClient(this.mc, this.previousGuiScreen, this.networkManager, this.gameProfile));
   }

   @Override
   public void onDisconnect(IChatComponent reason) {
      this.mc.displayGuiScreen(new GuiDisconnected(this.previousGuiScreen, "connect.failed", reason));
   }

   @Override
   public void handleDisconnect(S00PacketDisconnect packetIn) {
      this.networkManager.closeChannel(packetIn.getReason());
   }

   @Override
   public void handleEnableCompression(S03PacketEnableCompression packetIn) {
      if (!this.networkManager.isLocalChannel()) {
         this.networkManager.setCompressionTreshold(packetIn.getCompressionTreshold());
      }
   }
}
