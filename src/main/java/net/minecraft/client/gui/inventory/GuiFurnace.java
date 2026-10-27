package net.minecraft.client.gui.inventory;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.ResourceLocation;

public class GuiFurnace extends GuiContainer {
   public static ResourceLocation furnaceGuiTextures = new ResourceLocation("textures/gui/container/furnace.png");
   public InventoryPlayer playerInventory;
   public IInventory tileFurnace;

   public GuiFurnace(InventoryPlayer playerInv, IInventory furnaceInv) {
      super(new ContainerFurnace(playerInv, furnaceInv));
      this.playerInventory = playerInv;
      this.tileFurnace = furnaceInv;
   }

   @Override
   public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      String unformattedText = this.tileFurnace.getDisplayName().getUnformattedText();
      this.fontRendererObj.drawString(unformattedText, this.xSize / 2 - this.fontRendererObj.getStringWidth(unformattedText) / 2, 6.0F, 4210752);
      this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8.0F, this.ySize - 96 + 2, 4210752);
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(furnaceGuiTextures);
      int n = (this.width - this.xSize) / 2;
      int n2 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(n, n2, 0, 0, this.xSize, this.ySize);
      if (TileEntityFurnace.isBurning(this.tileFurnace)) {
         int burnLeftScaled = this.getBurnLeftScaled(13);
         this.drawTexturedModalRect(n + 56, n2 + 36 + 12 - burnLeftScaled, 176, 12 - burnLeftScaled, 14, burnLeftScaled + 1);
      }

      int cookProgressScaled = this.getCookProgressScaled(24);
      this.drawTexturedModalRect(n + 79, n2 + 34, 176, 14, cookProgressScaled + 1, 16);
   }

   public int getCookProgressScaled(int pixels) {
      int field = this.tileFurnace.getField(2);
      int fieldRef2 = this.tileFurnace.getField(3);
      return fieldRef2 != 0 && field != 0 ? field * pixels / fieldRef2 : 0;
   }

   public int getBurnLeftScaled(int pixels) {
      int field = this.tileFurnace.getField(1);
      if (field == 0) {
         field = 200;
      }

      return this.tileFurnace.getField(0) * pixels / field;
   }
}
