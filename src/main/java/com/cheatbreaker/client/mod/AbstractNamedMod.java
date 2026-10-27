package com.cheatbreaker.client.mod;

public class AbstractNamedMod extends AbstractMod {
   public void run() {
      if (this.isEnabled()) {
         this.setEnabled(false);
      }

      this.setStaffModEnabled(false);
   }

   public AbstractNamedMod(String str) {
      super(str);
   }
}
