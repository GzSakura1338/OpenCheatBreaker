package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.SimpleEvent26;
import com.cheatbreaker.client.event.WorldClientEvent;
import com.cheatbreaker.client.mod.AbstractIconHudMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.OldServerPinger;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.util.ResourceLocation;

public class ServerAddressMod extends AbstractIconHudMod {
    public static ResourceLocation unknownServerPng = new ResourceLocation("textures/misc/unknown_server.png");
    public OldServerPinger oldServerPinger = new OldServerPinger();
    public static ExecutorService executorService = Executors.newSingleThreadExecutor(
        new ThreadFactoryBuilder().setNameFormat("Server Address Icon Pinger").setDaemon(true).build()
    );
    public volatile ServerData serverData;
    public String address = "";

    public String findServerIconData() {
        ServerData currentServerData = this.mc.getCurrentServerData();
        if (currentServerData != null && currentServerData.getBase64EncodedIconData() != null) {
            return currentServerData.getBase64EncodedIconData();
        }

        ServerList serverList = new ServerList(this.mc);

        for (int i = 0; i < serverList.countServers(); i++) {
            ServerData serverData = serverList.getServerData(i);
            if (serverData.serverIP.equalsIgnoreCase(this.address) && serverData.getBase64EncodedIconData() != null) {
                return serverData.getBase64EncodedIconData();
            }
        }

        return null;
    }

    public boolean checkResourceLocation(ResourceLocation resourceLocation) {
        ITextureObject texture = this.mc.getTextureManager().getTexture(resourceLocation);
        return texture instanceof DynamicTexture && texture != TextureUtil.missingTexture;
    }

    @Override
    public String getStaticText() {
        return this.address;
    }

    public void lambda$new$0(SimpleEvent26 simpleEvent26) {
        this.refresh();
    }

    public boolean loadIcon(ResourceLocation resourceLocation, String str) {
        if (str == null) {
            return false;
        }

        try {
            BufferedImage read = ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(str)));
            if (read != null && read.getWidth() == 64 && read.getHeight() == 64) {
                this.mc.getTextureManager().loadTexture(resourceLocation, new DynamicTexture(read));
                return true;
            } else {
                return false;
            }
        } catch (IOException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String getLabelText() {
        return "IP";
    }

    @Override
    public void onEnable() throws IOException {
        super.onEnable();
        if (this.mc.theWorld != null) {
            this.refresh();
        }
    }

    @Override
    public String getDynamicText() {
        return this.address;
    }

    public ServerAddressMod() throws IOException {
        super("Server Address");
        this.setDescription("Displays the address of the current connected server.");
        this.setCategories(ModCategory.HUD, ModCategory.SERVER);
        this.setLabel("[minehq.com]");
        this.addEventHandler(SimpleEvent26.class, this::lambda$new$0);
        this.addEventHandler(WorldClientEvent.class, this::lambda$new$2);
    }

    public void pingAddress(String str) {
        executorService.execute(() -> {
            ServerData serverData = new ServerData(str, str);

            try {
                this.oldServerPinger.ping(serverData);
                this.serverData = serverData;
            } catch (UnknownHostException e) {
            }
        });
    }

    public void loadServerIcon() {
        this.serverData = null;
        if (this.mc.isSingleplayer()) {
            this.setIcon(unknownServerPng);
        } else {
            ResourceLocation resourceLocation3 = this.getResourceLocation3();
            if (!this.checkResourceLocation(resourceLocation3) && !this.loadIcon(resourceLocation3, this.findServerIconData())) {
                this.setIcon(unknownServerPng);
                this.pingAddress(this.address);
            } else {
                this.setIcon(resourceLocation3);
            }
        }
    }

    public void refresh() {
        if (this.mc.isSingleplayer() || this.mc.getCurrentServerData() != null) {
            this.updateAddress();
            this.loadServerIcon();
        }
    }

    public void lambda$new$2(WorldClientEvent worldClientEvent) {
        this.refresh();
    }

    public ResourceLocation getResourceLocation3() {
        return new ResourceLocation("servers/" + this.address + "/icon");
    }

    public void updateAddress() {
        if (this.mc.isSingleplayer()) {
            this.address = "Singleplayer";
        } else {
            this.address = this.mc.getCurrentServerData().serverIP;
        }
    }

    public void pingServer() throws IOException {
        ServerData serverData = this.serverData;
        if (serverData != null) {
            this.oldServerPinger.pingPendingNetworks();
            if (!serverData.serverIP.equals(this.address)) {
                this.serverData = null;
            } else {
                if (serverData.getBase64EncodedIconData() != null) {
                    ResourceLocation resourceLocation3 = this.getResourceLocation3();
                    if (this.loadIcon(resourceLocation3, serverData.getBase64EncodedIconData())) {
                        this.setIcon(resourceLocation3);
                    }

                    this.serverData = null;
                } else if (!"Pinging...".equals(serverData.serverMOTD)) {
                    this.serverData = null;
                }
            }
        }
    }

    @Override
    public void renderText(String str) throws IOException {
        this.pingServer();
        super.renderText(str);
    }
}
