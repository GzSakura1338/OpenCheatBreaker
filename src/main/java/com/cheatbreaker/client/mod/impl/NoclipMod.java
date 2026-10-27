package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.BlockPosIBlockStateListEvent;
import com.cheatbreaker.client.mod.AbstractNamedMod;

public class NoclipMod extends AbstractNamedMod {
   public NoclipMod(String str) {
      super(str);
      this.setStaffMod(true);
      this.addEventHandler(BlockPosIBlockStateListEvent.class, blockPosIBlockStateListEvent -> blockPosIBlockStateListEvent.setFlag(true));
   }
}
