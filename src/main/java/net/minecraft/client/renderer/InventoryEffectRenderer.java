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
import org.lwjgl.opengl.GL11;

public abstract class InventoryEffectRenderer extends GuiContainer {
    public boolean hasActivePotionEffects;

    public InventoryEffectRenderer(Container container) {
        super(container);
    }

    @Override
    public void initGui() throws java.io.IOException {
        super.initGui();
        SimpleEvent13 simpleEvent13 = new SimpleEvent13();
        CheatBreaker.getInstance().getEventBus().handleEvent(simpleEvent13);
        if (!this.mc.thePlayer.getActivePotionEffects().isEmpty() && !simpleEvent13.isCancelled()) {
            if (CheatBreaker.getInstance().getKeybindSettings().potionInfoShiftsInventory.getBooleanValue()) {
                this.guiLeft = 160 + (this.width - this.xSize - 200) / 2;
            }

            this.hasActivePotionEffects = true;
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        super.drawScreen(mouseX, mouseY, partialTicks);
        if (this.hasActivePotionEffects) {
            this.drawActivePotionEffects();
        }
    }

    public void drawActivePotionEffects() {
        int n = this.guiLeft - 124;
        int guiTop = this.guiTop;
        boolean flag = true;
        Collection activePotionEffects = this.mc.thePlayer.getActivePotionEffects();
        if (!activePotionEffects.isEmpty()) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(2896);
            int n2 = 33;
            if (activePotionEffects.size() > 5) {
                n2 = 132 / (activePotionEffects.size() - 1);
            }

            for (PotionEffect potionEffect : this.mc.thePlayer.getActivePotionEffects()) {
                Potion potion = Potion.potionTypes[potionEffect.getPotionID()];
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                this.mc.getTextureManager().bindTexture(inventoryBackground);
                SimpleEvent41 simpleEvent41 = new SimpleEvent41();
                CheatBreaker.getInstance().getEventBus().handleEvent(simpleEvent41);
                if (!simpleEvent41.isCancelled()) {
                    drawTexturedRect(n, guiTop, 0.0F, 166.0F, 140, 32);
                }

                if (potion.hasStatusIcon()) {
                    int statusIconIndex = potion.getStatusIconIndex();
                    drawTexturedRect(n + 6, guiTop + 7, 0 + statusIconIndex % 8 * 18, 198 + statusIconIndex / 8 * 18, 18, 18);
                }

                String format = I18n.format(potion.getName());
                if (potionEffect.getAmplifier() == 1) {
                    format = format + " " + I18n.format("enchantment.level.2");
                } else if (potionEffect.getAmplifier() == 2) {
                    format = format + " " + I18n.format("enchantment.level.3");
                } else if (potionEffect.getAmplifier() == 3) {
                    format = format + " " + I18n.format("enchantment.level.4");
                }

                this.fontRendererObj.drawShadowedString(format, n + 10 + 18, guiTop + 6, 16777215);
                String durationString = Potion.getDurationString(potionEffect);
                this.fontRendererObj.drawShadowedString(durationString, n + 10 + 18, guiTop + 6 + 10, 8355711);
                guiTop += n2;
            }
        }
    }
}
