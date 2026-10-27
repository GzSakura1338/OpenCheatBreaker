package com.cheatbreaker.client.manager;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.GuiScreenServerDataEvent;
import com.cheatbreaker.client.network.packet.BlockedServerPacket;
import com.cheatbreaker.client.ui.screen.BlockedServerScreen;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;

public class BlockedServersManager extends AbstractManager {
   public Map<String, BlockedServer> stringToBlockedServerMap = new HashMap<>();

   public BlockedServersManager() {
      CheatBreaker.getInstance().getEventBus().checkClass2(GuiScreenServerDataEvent.class, this::onGuiScreenServerDataEvent);
   }

   public void remove(String str) {
      this.stringToBlockedServerMap.remove(str);
   }

   public void onGuiScreenServerDataEvent(GuiScreenServerDataEvent guiScreenServerDataEvent) {
      ServerData serverData = guiScreenServerDataEvent.getServerData();
      GuiScreen guiScreen = guiScreenServerDataEvent.getGuiScreen();
      String toLowerCase = serverData.serverIP.toLowerCase();

      for (BlockedServer blockedServer : this.stringToBlockedServerMap.values()) {
         for (String str : blockedServer.getStringList()) {
            String toLowerCase2 = str.toLowerCase();
            if (toLowerCase.endsWith(toLowerCase2) || toLowerCase.matches("([a-zA-Z0-9]+)" + toLowerCase2 + "([a-zA-Z0-9]+)")) {
               boolean flag = blockedServer.isFlag() || blockedServer.getBlockedServerLevel() == BlockedServerLevel.BLOCK;
               this.mc.displayGuiScreen(new BlockedServerScreen(guiScreen, serverData, blockedServer.getText2(), flag));
               guiScreenServerDataEvent.setFlag(true);
               return;
            }
         }
      }
   }

   public void handleBlockedServerPacket(BlockedServerPacket blockedServerPacket) {
      BlockedServer blockedServer = new BlockedServer(
         blockedServerPacket.getText(), blockedServerPacket.getDomains(), blockedServerPacket.getText2(), blockedServerPacket.getBlockedServerLevel(), blockedServerPacket.getIntValue(), blockedServerPacket.isFlag(), blockedServerPacket.getText3()
      );
      this.stringToBlockedServerMap.put(blockedServerPacket.getText(), blockedServer);
   }

   @Override
   public void run() {
   }

   public Map<String, BlockedServer> getStringToBlockedServerMap() {
      return this.stringToBlockedServerMap;
   }
}
