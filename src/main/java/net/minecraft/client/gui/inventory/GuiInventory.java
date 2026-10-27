package net.minecraft.client.gui.inventory;

import com.cheatbreaker.client.CheatBreaker;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class GuiInventory extends InventoryEffectRenderer {
   public float oldMouseX;
   public float oldMouseY;

   public GuiInventory(EntityPlayer entityPlayer) {
      super(entityPlayer.inventoryContainer);
      this.allowUserInput = true;
   }

   @Override
   public void updateScreen() {
      if (this.mc.playerController.isInCreativeMode()) {
         this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.thePlayer));
      }

      this.updateActivePotionEffects();
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      if (this.mc.playerController.isInCreativeMode()) {
         this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.thePlayer));
      } else {
         super.initGui();
      }
   }

   @Override
   public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      this.fontRendererObj.drawString(I18n.format("container.crafting"), 86.0F, 16.0F, 4210752);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      this.oldMouseX = mouseX;
      this.oldMouseY = mouseY;
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(inventoryBackground);
      int guiLeft = this.guiLeft;
      int guiTop = this.guiTop;
      if (!(Boolean)CheatBreaker.getInstance().getModManager().packTweaksMod.transparentInventory.getValue()
         || !CheatBreaker.getInstance().getModManager().packTweaksMod.isEnabled()) {
         this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, this.xSize, this.ySize);
      }

      drawEntityOnScreen(guiLeft + 51, guiTop + 75, 30, guiLeft + 51 - this.oldMouseX, guiTop + 75 - 50 - this.oldMouseY, this.mc.thePlayer);
   }

   public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, EntityLivingBase ent) {
      GlStateManager.enableColorMaterial();
      GlStateManager.pushMatrix();
      GlStateManager.translate(posX, posY, 50.0F);
      GlStateManager.scale(-scale, scale, scale);
      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      float renderYawOffset = ent.renderYawOffset;
      float rotationYaw = ent.rotationYaw;
      float rotationPitch = ent.rotationPitch;
      float prevRotationYawHead = ent.prevRotationYawHead;
      float rotationYawHead = ent.rotationYawHead;
      GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
      RenderHelper.enableStandardItemLighting();
      GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-((float)Math.atan(mouseY / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
      ent.renderYawOffset = (float)Math.atan(mouseX / 40.0F) * 20.0F;
      ent.rotationYaw = (float)Math.atan(mouseX / 40.0F) * 40.0F;
      ent.rotationPitch = -((float)Math.atan(mouseY / 40.0F)) * 20.0F;
      ent.rotationYawHead = ent.rotationYaw;
      ent.prevRotationYawHead = ent.rotationYaw;
      GlStateManager.translate(0.0F, 0.0F, 0.0F);
      RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
      renderManager.setPlayerViewY(180.0F);
      renderManager.setRenderShadow(false);
      renderManager.renderEntityWithPosYaw(ent, 0.0, 0.0, 0.0, 0.0F, 1.0F);
      renderManager.setRenderShadow(true);
      ent.renderYawOffset = renderYawOffset;
      ent.rotationYaw = rotationYaw;
      ent.rotationPitch = rotationPitch;
      ent.prevRotationYawHead = prevRotationYawHead;
      ent.rotationYawHead = rotationYawHead;
      GlStateManager.popMatrix();
      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableRescaleNormal();
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.disableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 0) {
         this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.getStatFileWriter()));
      }

      if (button.id == 1) {
         this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
      }
   }
}
