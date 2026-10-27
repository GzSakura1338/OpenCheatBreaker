package com.cheatbreaker.client.network;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.SimpleEvent26;
import com.cheatbreaker.client.event.StringByteArrayEvent;
import com.cheatbreaker.client.ui.screen.ClassicMainMenuScreen;
import com.cheatbreaker.client.util.SneakyThrow;
import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;

public class ServerTransferHandler {
    public Minecraft mc = Minecraft.getMinecraft();
    public OldServerPinger oldServerPinger = new OldServerPinger();

    public void handleServerData(ServerData serverData) {
        if (this.mc.getCurrentServerData() != null && this.mc.theWorld != null) {
            this.mc.theWorld.sendQuittingDisconnectingPacket();
            this.mc.loadWorld(null);
        }
        this.mc.displayGuiScreen(new GuiConnecting(new ClassicMainMenuScreen(), this.mc, serverData));
    }

    public ServerTransferHandler() {
        CheatBreaker.getInstance().getEventBus().checkClass2(SimpleEvent26.class, this::onSimpleEvent26);
        CheatBreaker.getInstance().getEventBus().checkClass2(StringByteArrayEvent.class, this::onStringByteArrayEvent);
    }

    public void write(boolean bl) {
        ByteBuf byteBuf = Unpooled.buffer();
        byteBuf.writeByte(1);
        byteBuf.writeByte(bl ? 1 : 0);
        this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("transfer:channel", new PacketBuffer(byteBuf)));
    }

    public void onSimpleEvent26(SimpleEvent26 simpleEvent26) {
        PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
        packetBuffer.writeBytes("transfer:channel".getBytes(Charsets.UTF_8));
        if (this.mc.getNetHandler() != null && this.mc.getCurrentServerData() != null && this.mc.getCurrentServerData().flag3) {
            this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("REGISTER", packetBuffer));
        }
    }

    public String formatText(ByteBuffer byteBuffer) {
        int n = byteBuffer.getInt();
        if (n > 1024) {
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + n + " > 1024)");
        }
        if (n < 0) {
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        }
        byte[] byArray = new byte[n];
        byteBuffer.get(byArray);
        return new String(byArray, StandardCharsets.UTF_8);
    }

    public boolean checkString(String string) {
        try {
            try {
                InetAddress inetAddress = InetAddress.getByName(string);
                return inetAddress.isLoopbackAddress() || inetAddress.isSiteLocalAddress();
            }
            catch (Exception exception) {
                return !string.contains("regions.lunar.gg");
            }
        }
        catch (Throwable throwable) {
            throw SneakyThrow.sneakyThrow(throwable);
        }
    }

    public void onStringByteArrayEvent(StringByteArrayEvent stringByteArrayEvent) {
        try {
            if (stringByteArrayEvent.getText().equals("transfer:channel")) {
                ByteBuffer byteBuffer = ByteBuffer.wrap(stringByteArrayEvent.getBytes());
                byte by = byteBuffer.get();
                if (by == 0) {
                    String string = this.formatText(byteBuffer);
                    if (string.equals(this.mc.getCurrentServerData().serverIP)) {
                        return;
                    }
                    if (this.mc.getCurrentServerData().flag3) {
                        this.write(true);
                        System.out.println("Connecting to IP: " + string);
                        this.handleServerData(new ServerData(true, string, string, false));
                    } else {
                        this.write(false);
                    }
                } else if (by == 2) {
                    int n = byteBuffer.getInt();
                    if (n > 10) {
                        throw new DecoderException("Too many servers received.");
                    }
                    new Thread(() -> {
                        HashMap<String, Long> hashMap = new HashMap<String, Long>();
                        for (int i = 0; i < n; ++i) {
                            String string2 = this.formatText(byteBuffer);
                            if (this.checkString(string2)) continue;
                            ServerData serverData = new ServerData(true, string2, string2, false);
                            try {
                                this.oldServerPinger.ping(serverData);
                                continue;
                            }
                            catch (UnknownHostException unknownHostException) {
                                unknownHostException.printStackTrace();
                            }
                        }
                        try {
                            Thread.sleep(1000L);
                        }
                        catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                        ByteBuf byteBuf = Unpooled.buffer();
                        byteBuf.writeByte(3);
                        byteBuf.writeInt(hashMap.size());
                        hashMap.forEach((string, l) -> {
                            byteBuf.writeInt(string.length());
                            try {
                                byteBuf.writeBytes(string.getBytes(StandardCharsets.UTF_8));
                                byteBuf.writeLong(l.longValue());
                            }
                            catch (Exception exception) {
                                byteBuf.writeBytes("localhost".getBytes());
                                byteBuf.writeLong(Long.MIN_VALUE);
                            }
                        });
                        if (this.mc.getNetHandler() != null) {
                            this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("transfer:channel", new PacketBuffer(byteBuf)));
                        }
                    }).start();
                }
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}
