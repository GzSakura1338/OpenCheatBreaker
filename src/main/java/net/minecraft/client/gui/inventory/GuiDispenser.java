package net.minecraft.client.gui.inventory;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerDispenser;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiDispenser extends GuiContainer {
   public static ResourceLocation dispenserGuiTextures = new ResourceLocation("textures/gui/container/dispenser.png");
   public InventoryPlayer playerInventory;
   public IInventory dispenserInventory;

   public GuiDispenser(InventoryPlayer playerInv, IInventory dispenserInv) {
      super(new ContainerDispenser(playerInv, dispenserInv));
      this.playerInventory = playerInv;
      this.dispenserInventory = dispenserInv;
   }

   @Override
   public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      String unformattedText = this.dispenserInventory.getDisplayName().getUnformattedText();
      this.fontRendererObj.drawString(unformattedText, this.xSize / 2 - this.fontRendererObj.getStringWidth(unformattedText) / 2, 6.0F, 4210752);
      this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8.0F, this.ySize - 96 + 2, 4210752);
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(dispenserGuiTextures);
      int n = (this.width - this.xSize) / 2;
      int n2 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(n, n2, 0, 0, this.xSize, this.ySize);
   }
}
