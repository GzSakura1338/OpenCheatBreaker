package net.minecraft.client.multiplayer;

import com.cheatbreaker.client.CheatBreaker;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerList {
    public static Logger logger = LogManager.getLogger();
    public List<ServerData> servers = new ArrayList<>();
    public Minecraft mc;

    public ServerList(Minecraft mc) {
        this.mc = mc;
        this.loadServerList();
    }

    public void loadServerList() {
        try {
            this.servers.clear();
            NBTTagCompound read = CompressedStreamTools.read(new File(this.mc.mcDataDir, "servers.dat"));
            if (read == null) {
                return;
            }

            NBTTagList tagList = read.getTagList("servers", 10);

            for (String[] strs : CheatBreaker.getInstance().getKeybindSettings().getFeaturedServers()) {
                this.servers.add(new ServerData(true, strs[0], strs[1]));
            }

            for (int i = 0; i < tagList.tagCount(); i++) {
                this.servers.add(ServerData.getServerDataFromNBTCompound(tagList.getCompoundTagAt(i)));
            }
        } catch (Exception e) {
            logger.error("Couldn't load server list", e);
        }
    }

    public void saveServerList() {
        try {
            NBTTagList nbttagList = new NBTTagList();

            for (ServerData serverData : this.servers) {
                if (!serverData.flag) {
                    nbttagList.appendTag(serverData.getNBTCompound());
                }
            }

            NBTTagCompound nbttagCompound = new NBTTagCompound();
            nbttagCompound.setTag("servers", nbttagList);
            CompressedStreamTools.safeWrite(nbttagCompound, new File(this.mc.mcDataDir, "servers.dat"));
        } catch (Exception e) {
            logger.error("Couldn't save server list", e);
        }
    }

    public ServerData getServerData(int n) {
        return this.servers.get(n);
    }

    public void removeServerData(int n) {
        this.servers.remove(n);
    }

    public void addServerData(ServerData serverData) {
        this.servers.add(serverData);
    }

    public int countServers() {
        return this.servers.size();
    }

    public void swapServers(int n, int n2) {
        ServerData serverData = this.getServerData(n2);
        ServerData serverData2 = this.getServerData(n);
        if (!serverData2.flag && !serverData.flag) {
            this.servers.set(n, serverData);
            this.servers.set(n2, serverData2);
            this.saveServerList();
        }
    }

    public void set(int n, ServerData serverData) {
        this.servers.set(n, serverData);
    }

    public static void updateServerListEntry(ServerData serverData) {
        ServerList serverList = new ServerList(Minecraft.getMinecraft());
        serverList.loadServerList();

        for (int i = 0; i < serverList.countServers(); i++) {
            ServerData serverData2 = serverList.getServerData(i);
            if (serverData2.serverName.equals(serverData.serverName) && serverData2.serverIP.equals(serverData.serverIP)) {
                serverList.set(i, serverData);
                break;
            }
        }

        serverList.saveServerList();
    }
}
