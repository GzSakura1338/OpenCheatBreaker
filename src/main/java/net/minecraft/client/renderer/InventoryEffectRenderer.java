package net.minecraft.client.renderer;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.SimpleEvent13;
import com.cheatbreaker.client.event.SimpleEvent41;
import java.util.Collection;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.Container;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public abstract class InventoryEffectRenderer extends GuiContainer {
   public boolean hasActivePotionEffects;

   public InventoryEffectRenderer(Container inventorySlotsIn) {
      super(inventorySlotsIn);
   }

   @Override
   public void initGui() {
      super.initGui();
      this.updateActivePotionEffects();
   }

   public void updateActivePotionEffects() {
      if (!this.mc.thePlayer.getActivePotionEffects().isEmpty()) {
         SimpleEvent13 simpleEvent13 = new SimpleEvent13();
         CheatBreaker.getInstance().getEventBus().handleEvent(simpleEvent13);
         if (!simpleEvent13.isFlag()) {
            if (CheatBreaker.getInstance().getKeybindSettings().potionInfoShiftsInventory.getBooleanValue()) {
               this.guiLeft = 160 + (this.width - this.xSize - 200) / 2;
            }

            this.hasActivePotionEffects = true;
         }
      } else {
         this.guiLeft = (this.width - this.xSize) / 2;
         this.hasActivePotionEffects = false;
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      if (this.hasActivePotionEffects) {
         this.drawActivePotionEffects();
      }
   }

   public void drawActivePotionEffects() {
      int n = this.guiLeft - 124;
      int guiTop = this.guiTop;
      short s = 166;
      Collection activePotionEffects = this.mc.thePlayer.getActivePotionEffects();
      if (!activePotionEffects.isEmpty()) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableLighting();
         int n2 = 33;
         if (activePotionEffects.size() > 5) {
            n2 = 132 / (activePotionEffects.size() - 1);
         }

         for (PotionEffect potionEffect : this.mc.thePlayer.getActivePotionEffects()) {
            Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.mc.getTextureManager().bindTexture(inventoryBackground);
            SimpleEvent41 simpleEvent41 = new SimpleEvent41();
            CheatBreaker.getInstance().getEventBus().handleEvent(simpleEvent41);
            if (!simpleEvent41.isFlag()) {
               this.drawTexturedModalRect(n, guiTop, 0, 166, 140, 32);
            }

            if (potion.hasStatusIcon()) {
               int statusIconIndex = potion.getStatusIconIndex();
               this.drawTexturedModalRect(n + 6, guiTop + 7, 0 + statusIconIndex % 8 * 18, 198 + statusIconIndex / 8 * 18, 18, 18);
            }

            String format = I18n.format(potion.getName());
            if (potionEffect.getAmplifier() == 1) {
               format = format + " " + I18n.format("enchantment.level.2");
            } else if (potionEffect.getAmplifier() == 2) {
               format = format + " " + I18n.format("enchantment.level.3");
            } else if (potionEffect.getAmplifier() == 3) {
               format = format + " " + I18n.format("enchantment.level.4");
            }

            this.fontRendererObj.drawStringWithShadow(format, n + 10 + 18, guiTop + 6, 16777215);
            String durationString = Potion.getDurationString(potionEffect);
            this.fontRendererObj.drawStringWithShadow(durationString, n + 10 + 18, guiTop + 6 + 10, 8355711);
            guiTop += n2;
         }
      }
   }
}
