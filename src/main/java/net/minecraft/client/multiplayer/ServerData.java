package net.minecraft.client.multiplayer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class ServerData {
    public int version = 5;
    public String gameVersion = "1.7.10";
    public String populationInfo;
    public String serverMOTD;
    public long pingToServer;
    public ServerData.ServerResourceMode resourceMode = ServerData.ServerResourceMode.PROMPT;
    public boolean flag = false;
    public boolean online;
    public String playerList;
    public boolean flag2 = false;
    public String serverIcon;
    public boolean lanServer;
    public boolean flag3 = false;
    public String serverName;
    public String serverIP;
    public String text;
    public int intValue;

    public ServerData(String str, String str2) {
        this.serverName = str;
        this.serverIP = str2;
    }

    public ServerData(String str, String str2, boolean flag) {
        this(str, str2);
        this.lanServer = flag;
    }

    public NBTTagCompound getNBTCompound() {
        NBTTagCompound nbttagCompound = new NBTTagCompound();
        nbttagCompound.setString("name", this.serverName);
        nbttagCompound.setString("ip", this.serverIP);
        if (this.serverIcon != null) {
            nbttagCompound.setString("icon", this.serverIcon);
        }

        if (this.resourceMode == ServerData.ServerResourceMode.ENABLED) {
            nbttagCompound.setBoolean("acceptTextures", true);
        } else if (this.resourceMode == ServerData.ServerResourceMode.DISABLED) {
            nbttagCompound.setBoolean("acceptTextures", false);
        }

        return nbttagCompound;
    }

    public ServerData.ServerResourceMode getResourceMode() {
        return this.resourceMode;
    }

    public void setResourceMode(ServerData.ServerResourceMode mode) {
        this.resourceMode = mode;
    }

    public static ServerData getServerDataFromNBTCompound(NBTTagCompound nbtCompound) {
        ServerData serverData = new ServerData(nbtCompound.getString("name"), nbtCompound.getString("ip"));
        if (nbtCompound.hasKey("icon", 8)) {
            serverData.setBase64EncodedIconData(nbtCompound.getString("icon"));
        }

        if (nbtCompound.hasKey("acceptTextures", 1)) {
            if (nbtCompound.getBoolean("acceptTextures")) {
                serverData.setResourceMode(ServerData.ServerResourceMode.ENABLED);
            } else {
                serverData.setResourceMode(ServerData.ServerResourceMode.DISABLED);
            }
        } else {
            serverData.setResourceMode(ServerData.ServerResourceMode.PROMPT);
        }

        return serverData;
    }

    public String getBase64EncodedIconData() {
        return this.serverIcon;
    }

    public void setBase64EncodedIconData(String icon) {
        this.serverIcon = icon;
    }

    public void copyFrom(ServerData serverDataIn) {
        this.serverIP = serverDataIn.serverIP;
        this.serverName = serverDataIn.serverName;
        this.setResourceMode(serverDataIn.getResourceMode());
        this.serverIcon = serverDataIn.serverIcon;
    }

    public boolean isLanServer() {
        return this.lanServer;
    }

    public ServerData(boolean flag, String str, String str2) {
        this(str, str2);
        this.flag = flag;
    }

    public boolean check() {
        return this.version == -1332;
    }

    public enum ServerResourceMode {
        ENABLED("ENABLED", 0, "enabled"),
        DISABLED("DISABLED", 1, "disabled"),
        PROMPT("PROMPT", 2, "prompt");

        public IChatComponent motd;
        public static ServerData.ServerResourceMode[] serverResourceModeArray = new ServerData.ServerResourceMode[]{ENABLED, DISABLED, PROMPT};

        public IChatComponent getMotd() {
            return this.motd;
        }

        ServerResourceMode(String str, int n, String str2) {
            this.motd = new ChatComponentTranslation("addServer.resourcePack." + str2);
        }
    }
}
