package net.minecraft.client.gui;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.ui.screen.ServerListEntry;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector.LanServer;

public class ServerSelectionList extends GuiListExtended {
    public List servers = Lists.newArrayList();
    public List lanServers = Lists.newArrayList();
    public IGuiListEntry lanScanEntry = new ServerListEntryLanScan();
    public int selectedEntry = -1;
    public GuiMultiplayer owner;

    public ServerSelectionList(GuiMultiplayer guiMultiplayer, Minecraft mc, int n, int n2, int n3, int n4, int n5) {
        super(mc, n, n2, n3, n4, n5);
        this.owner = guiMultiplayer;
    }

    @Override
    public IGuiListEntry getListEntry(int n) {
        if (n < this.servers.size()) {
            return (IGuiListEntry)this.servers.get(n);
        }

        n -= this.servers.size();
        return n == 0 ? this.lanScanEntry : (IGuiListEntry)this.lanServers.get(--n);
    }

    @Override
    public int getSize() {
        return this.servers.size() + 1 + this.lanServers.size();
    }

    public void setSelected(int n) {
        this.selectedEntry = n;
    }

    @Override
    public boolean isSelected(int n) {
        return n == this.selectedEntry;
    }

    public int getSelected() {
        return this.selectedEntry;
    }

    public void setServers(ServerList serverList) {
        this.servers.clear();

        for (int i = 0; i < serverList.countServers(); i++) {
            ServerData serverData = serverList.getServerData(i);
            if (serverData.flag) {
                this.servers.add(new ServerListEntry(this.owner, serverData));
            } else if (!CheatBreaker.getInstance().getKeybindSettings().getFeaturedServers().stream().anyMatch(strs -> strs[1].equalsIgnoreCase(serverData.serverIP))) {
                this.servers.add(new ServerListEntryNormal(this.owner, serverData));
            }
        }
    }

    public void addServers(List list) {
        this.lanServers.clear();

        for (LanServer lanServer : (Iterable<LanServer>)list) {
            this.lanServers.add(new ServerListEntryLanDetected(this.owner, lanServer));
        }
    }

    @Override
    public int getScrollBarX() {
        return super.getScrollBarX() + 30;
    }

    @Override
    public int getListWidth() {
        return super.getListWidth() + 85;
    }

    public int computeInt(ServerListEntryNormal serverListEntryNormal) {
        if (this.servers.contains(serverListEntryNormal)) {
            return this.servers.indexOf(serverListEntryNormal);
        } else {
            return this.lanServers.contains(serverListEntryNormal) ? this.lanServers.indexOf(serverListEntryNormal) : 0;
        }
    }
}
