package net.minecraft.realms;

import java.net.InetAddress;
import java.net.UnknownHostException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RealmsConnect {
    public static Logger LOGGER = LogManager.getLogger();
    public volatile boolean aborted = false;
    public RealmsScreen onlineScreen;
    public NetworkManager connection;

    public RealmsConnect(RealmsScreen realmsScreen) {
        this.onlineScreen = realmsScreen;
    }

    public void connect(final String str, final int n) {
        (new Thread("Realms-connect-task") {
                @Override
                public void run() { try {
                    InetAddress inetAddress = null;

                    try {
                        inetAddress = InetAddress.getByName(str);
                        if (RealmsConnect.this.aborted) {
                            return;
                        }

                        RealmsConnect.this.connection = NetworkManager.provideLanClient(inetAddress, n);
                        if (RealmsConnect.this.aborted) {
                            return;
                        }

                        RealmsConnect.this.connection
                            .setNetHandler(
                                new NetHandlerLoginClient(RealmsConnect.this.connection, Minecraft.getMinecraft(), RealmsConnect.this.onlineScreen.getProxy())
                            );
                        if (RealmsConnect.this.aborted) {
                            return;
                        }

                        RealmsConnect.this.connection.scheduleOutboundPacket(new C00Handshake(5, str, n, EnumConnectionState.LOGIN));
                        if (RealmsConnect.this.aborted) {
                            return;
                        }

                        RealmsConnect.this.connection.scheduleOutboundPacket(new C00PacketLoginStart(Minecraft.getMinecraft().getSession().getProfile()));
                    } catch (UnknownHostException e) {
                        if (RealmsConnect.this.aborted) {
                            return;
                        }

                        RealmsConnect.LOGGER.error("Couldn't connect to world", e);
                        Realms.setScreen(
                            new DisconnectedOnlineScreen(
                                RealmsConnect.this.onlineScreen,
                                "connect.failed",
                                new ChatComponentTranslation("disconnect.genericReason", "Unknown host '" + str + "'")
                            )
                        );
                    } catch (Exception e) {
                        if (RealmsConnect.this.aborted) {
                            return;
                        }

                        RealmsConnect.LOGGER.error("Couldn't connect to world", e);
                        String str2 = e.toString();
                        if (inetAddress != null) {
                            String str3 = inetAddress + ":" + n;
                            str2 = str2.replaceAll(str3, "");
                        }

                        Realms.setScreen(
                            new DisconnectedOnlineScreen(
                                RealmsConnect.this.onlineScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", str2)
                            )
                        );
                    }
                } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } }
            })
            .start();
    }

    public void abort() {
        this.aborted = true;
    }

    public void tick() throws java.io.IOException {
        if (this.connection != null) {
            if (this.connection.isChannelOpen()) {
                this.connection.processReceivedPackets();
            } else if (this.connection.getExitMessage() != null) {
                this.connection.getNetHandler().onDisconnect(this.connection.getExitMessage());
            }
        }
    }
}
