package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.mod.AbstractValueHudMod;
import com.cheatbreaker.client.mod.ValueRange;

public class MemoryUsageMod extends AbstractValueHudMod {
   public Runtime runtime = Runtime.getRuntime();

   @Override
   public ValueRange getHiddenValueRange() {
      return new ValueRange(0, 50, 100);
   }

   @Override
   public Number getNumericValue() {
      return (this.runtime.totalMemory() - this.runtime.freeMemory()) * 100L / this.runtime.maxMemory();
   }

   @Override
   public String getStaticText() {
      return this.getNumericValue() + "%";
   }

   @Override
   public String getDefaultFormat() {
      return "%LABEL%: %VALUE%";
   }

   @Override
   public String getDynamicText() {
      return super.getDynamicText() == null ? null : super.getDynamicText() + "%";
   }

   @Override
   public String getLabelText() {
      return "Mem";
   }

   public MemoryUsageMod() {
      super("Memory Usage");
      this.setDescription("Displays how much memory the game is using.");
      this.handleString("[Mem: 19%]");
   }
}
