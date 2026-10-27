package net.minecraft.client.gui.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiScreenHorseInventory extends GuiContainer {
   public static ResourceLocation horseGuiTextures = new ResourceLocation("textures/gui/container/horse.png");
   public IInventory playerInventory;
   public IInventory horseInventory;
   public EntityHorse horseEntity;
   public float mousePosx;
   public float mousePosY;

   public GuiScreenHorseInventory(IInventory playerInv, IInventory horseInv, EntityHorse horse) {
      super(new ContainerHorseInventory(playerInv, horseInv, horse, Minecraft.getMinecraft().thePlayer));
      this.playerInventory = playerInv;
      this.horseInventory = horseInv;
      this.horseEntity = horse;
      this.allowUserInput = false;
   }

   @Override
   public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.fontRendererObj.drawString(this.horseInventory.getDisplayName().getUnformattedText(), 8.0F, 6.0F, 4210752);
      this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8.0F, this.ySize - 96 + 2, 4210752);
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(horseGuiTextures);
      int n = (this.width - this.xSize) / 2;
      int n2 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(n, n2, 0, 0, this.xSize, this.ySize);
      if (this.horseEntity.isChested()) {
         this.drawTexturedModalRect(n + 79, n2 + 17, 0, this.ySize, 90, 54);
      }

      if (this.horseEntity.canWearArmor()) {
         this.drawTexturedModalRect(n + 7, n2 + 35, 0, this.ySize + 54, 18, 18);
      }

      GuiInventory.drawEntityOnScreen(n + 51, n2 + 60, 17, n + 51 - this.mousePosx, n2 + 75 - 50 - this.mousePosY, this.horseEntity);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.mousePosx = mouseX;
      this.mousePosY = mouseY;
      super.drawScreen(mouseX, mouseY, partialTicks);
   }
}
