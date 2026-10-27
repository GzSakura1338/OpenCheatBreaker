package net.minecraft.client.multiplayer;

import com.cheatbreaker.client.CheatBreaker;
import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerList {
   public static Logger logger = LogManager.getLogger();
   public List<ServerData> servers = Lists.newArrayList();
   public Minecraft mc;

   public ServerList(Minecraft mcIn) {
      this.mc = mcIn;
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
            this.servers.add(new ServerData(true, strs[0], strs[1], false));
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

   public ServerData getServerData(int index) {
      return this.servers.get(index);
   }

   public void removeServerData(int index) {
      this.servers.remove(index);
   }

   public void addServerData(ServerData server) {
      this.servers.add(server);
   }

   public int countServers() {
      return this.servers.size();
   }

   public void swapServers(int n, int n2) {
      ServerData serverData = this.getServerData(n);
      ServerData serverData2 = this.getServerData(n2);
      if (!serverData.flag && !serverData2.flag) {
         this.servers.set(n, serverData2);
         this.servers.set(n2, serverData);
         this.saveServerList();
      }
   }

   public void set(int index, ServerData server) {
      this.servers.set(index, server);
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
