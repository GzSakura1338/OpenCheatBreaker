package com.cheatbreaker.client.ui.screen;

import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;

public class DummyNetworkManager extends NetworkManager {
   public DummyNetworkManager() {
      super(EnumPacketDirection.SERVERBOUND);
   }
}
