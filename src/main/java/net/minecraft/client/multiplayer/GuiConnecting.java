package net.minecraft.client.multiplayer;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.SimpleEvent26;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiConnecting extends GuiScreen {
    public static AtomicInteger CONNECTION_ID = new AtomicInteger(0);
    public static Logger logger = LogManager.getLogger();
    public NetworkManager networkManager;
    public boolean cancel;
    public GuiScreen previousGuiScreen;
    public static OldServerPinger oldServerPinger = new OldServerPinger();

    public GuiConnecting(GuiScreen guiScreen, Minecraft mc, ServerData serverData) throws java.io.IOException {
        this.mc = mc;
        this.previousGuiScreen = guiScreen;
        ServerAddress parse = ServerAddress.parse(serverData.serverIP);
        mc.loadWorld(null);
        mc.setServerData(serverData);
        this.connect(parse.getIP(), parse.getPort());
    }

    public GuiConnecting(GuiScreen guiScreen, Minecraft mc, String str, int n) throws java.io.IOException {
        this.mc = mc;
        this.previousGuiScreen = guiScreen;
        mc.loadWorld(null);
        this.connect(str, n);
    }

    public void connect(final String ip, final int port) {
        logger.info("Connecting to " + ip + ", " + port);
        (new Thread("Server Connector #" + CONNECTION_ID.incrementAndGet()) {
                @Override
                public void run() { try {
                    InetAddress inetAddress = null;

                    try {
                        if (GuiConnecting.this.cancel) {
                            return;
                        }

                        inetAddress = InetAddress.getByName(ip);
                        Minecraft.getMinecraft().currentServerData.text = inetAddress.getHostAddress();
                        Minecraft.getMinecraft().currentServerData.intValue = port;
                        GuiConnecting.oldServerPinger.ping(Minecraft.getMinecraft().currentServerData);

                        while (!Minecraft.getMinecraft().currentServerData.flag2) {
                            try {
                                Thread.sleep(100L);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent26());
                        GuiConnecting.this.networkManager = NetworkManager.provideLanClient(inetAddress, port);
                        GuiConnecting.this.networkManager
                            .setNetHandler(new NetHandlerLoginClient(GuiConnecting.this.networkManager, GuiConnecting.this.mc, GuiConnecting.this.previousGuiScreen));
                        GuiConnecting.this.networkManager
                            .scheduleOutboundPacket(
                                new C00Handshake(Minecraft.getMinecraft().currentServerData.version == -1332 ? -1332 : 5, ip, port, EnumConnectionState.LOGIN)
                            );
                        GuiConnecting.this.networkManager.scheduleOutboundPacket(new C00PacketLoginStart(GuiConnecting.this.mc.getSession().getProfile()));
                    } catch (UnknownHostException e) {
                        if (GuiConnecting.this.cancel) {
                            return;
                        }

                        GuiConnecting.logger.error("Couldn't connect to server", e);
                        GuiConnecting.this.mc
                            .displayGuiScreen(
                                new GuiDisconnected(
                                    GuiConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", "Unknown host")
                                )
                            );
                    } catch (Exception e) {
                        if (GuiConnecting.this.cancel) {
                            return;
                        }

                        GuiConnecting.logger.error("Couldn't connect to server", e);
                        String str = e.toString();
                        if (inetAddress != null) {
                            String str2 = inetAddress + ":" + port;
                            str = str.replaceAll(str2, "");
                        }

                        GuiConnecting.this.mc
                            .displayGuiScreen(
                                new GuiDisconnected(
                                    GuiConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", str)
                                )
                            );
                    }
                } catch (Exception sneakyEx) { throw com.cheatbreaker.client.util.SneakyThrow.sneakyThrow(sneakyEx); } }
            })
            .start();
    }

    @Override
    public void updateScreen() throws java.io.IOException {
        if (this.networkManager != null) {
            if (this.networkManager.isChannelOpen()) {
                this.networkManager.processReceivedPackets();
            } else if (this.networkManager.getExitMessage() != null) {
                this.networkManager.getNetHandler().onDisconnect(this.networkManager.getExitMessage());
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + 50, I18n.format("gui.cancel")));
    }

    @Override
    public void actionPerformed(GuiButton button) throws java.io.IOException {
        if (button.id == 0) {
            this.cancel = true;
            if (this.networkManager != null) {
                this.networkManager.closeChannel(new ChatComponentText("Aborted"));
            }

            this.mc.displayGuiScreen(this.previousGuiScreen);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        this.drawDefaultBackground();
        if (this.networkManager == null) {
            this.drawCenteredString(this.fontRendererObj, I18n.format("connect.connecting"), this.width / 2, this.height / 2 - 50, 16777215);
        } else {
            this.drawCenteredString(this.fontRendererObj, I18n.format("connect.authorizing"), this.width / 2, this.height / 2 - 50, 16777215);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
