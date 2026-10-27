package net.minecraft.client.gui.inventory;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerBrewingStand;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiBrewingStand extends GuiContainer {
   public static ResourceLocation brewingStandGuiTextures = new ResourceLocation("textures/gui/container/brewing_stand.png");
   public InventoryPlayer playerInventory;
   public IInventory tileBrewingStand;

   public GuiBrewingStand(InventoryPlayer playerInv, IInventory inventory) {
      super(new ContainerBrewingStand(playerInv, inventory));
      this.playerInventory = playerInv;
      this.tileBrewingStand = inventory;
   }

   @Override
   public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      String unformattedText = this.tileBrewingStand.getDisplayName().getUnformattedText();
      this.fontRendererObj.drawString(unformattedText, this.xSize / 2 - this.fontRendererObj.getStringWidth(unformattedText) / 2, 6.0F, 4210752);
      this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8.0F, this.ySize - 96 + 2, 4210752);
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(brewingStandGuiTextures);
      int n = (this.width - this.xSize) / 2;
      int n2 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(n, n2, 0, 0, this.xSize, this.ySize);
      int field = this.tileBrewingStand.getField(0);
      if (field > 0) {
         int n3 = (int)(28.0F * (1.0F - field / 400.0F));
         if (n3 > 0) {
            this.drawTexturedModalRect(n + 97, n2 + 16, 176, 0, 9, n3);
         }

         int n4 = field / 2 % 7;
         switch (n4) {
            case 0:
               n3 = 29;
               break;
            case 1:
               n3 = 24;
               break;
            case 2:
               n3 = 20;
               break;
            case 3:
               n3 = 16;
               break;
            case 4:
               n3 = 11;
               break;
            case 5:
               n3 = 6;
               break;
            case 6:
               n3 = 0;
         }

         if (n3 > 0) {
            this.drawTexturedModalRect(n + 65, n2 + 14 + 29 - n3, 185, 29 - n3, 12, n3);
         }
      }
   }
}
