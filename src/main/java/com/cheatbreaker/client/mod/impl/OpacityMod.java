package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.BlockIntEvent;
import com.cheatbreaker.client.mod.AbstractNamedMod;
import com.cheatbreaker.client.mod.setting.Setting;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OpacityMod extends AbstractNamedMod {
   public List<Integer> integerList = new ArrayList<>();
   public Setting opacity;

   public void onBlockIntEvent(BlockIntEvent blockIntEvent) {
   }

   @Override
   public void onDisable() {
      super.onDisable();
      this.mc.renderGlobal.loadRenderers();
   }

   public List<Integer> getIntegerList() {
      return this.integerList;
   }

   public OpacityMod(String str) {
      super(str);
      this.setStaffMod(true);
      Collections.addAll(this.integerList, 14, 15, 16, 15, 56, 129, 52);
      this.opacity = new Setting(this, "Opacity").changeValue(45).setRange(15, 255).setUnit(" alpha");
      this.addEventHandler(BlockIntEvent.class, this::onBlockIntEvent);
   }

   @Override
   public void onEnable() {
      super.onEnable();
      this.mc.renderGlobal.loadRenderers();
   }

   public boolean check(int n, boolean flag) {
      return !this.isEnabled() ? flag : this.integerList.contains(n);
   }
}
