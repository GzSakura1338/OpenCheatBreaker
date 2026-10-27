package net.minecraft.client.gui;

import io.netty.buffer.Unpooled;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiMerchant extends GuiContainer {
   public static Logger logger = LogManager.getLogger();
   public static ResourceLocation MERCHANT_GUI_TEXTURE = new ResourceLocation("textures/gui/container/villager.png");
   public IMerchant merchant;
   public GuiMerchant.MerchantButton nextButton;
   public GuiMerchant.MerchantButton previousButton;
   public int selectedMerchantRecipe;
   public IChatComponent chatComponent;

   public GuiMerchant(InventoryPlayer inventoryPlayer, IMerchant merchant, World worldIn) {
      super(new ContainerMerchant(inventoryPlayer, merchant, worldIn));
      this.merchant = merchant;
      this.chatComponent = merchant.getDisplayName();
   }

   @Override
   public void initGui() {
      super.initGui();
      int n = (this.width - this.xSize) / 2;
      int n2 = (this.height - this.ySize) / 2;
      this.buttonList.add(this.nextButton = new GuiMerchant.MerchantButton(1, n + 120 + 27, n2 + 24 - 1, true));
      this.buttonList.add(this.previousButton = new GuiMerchant.MerchantButton(2, n + 36 - 19, n2 + 24 - 1, false));
      this.nextButton.enabled = false;
      this.previousButton.enabled = false;
   }

   @Override
   public void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
      String unformattedText = this.chatComponent.getUnformattedText();
      this.fontRendererObj.drawString(unformattedText, this.xSize / 2 - this.fontRendererObj.getStringWidth(unformattedText) / 2, 6.0F, 4210752);
      this.fontRendererObj.drawString(I18n.format("container.inventory"), 8.0F, this.ySize - 96 + 2, 4210752);
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      MerchantRecipeList recipes = this.merchant.getRecipes(this.mc.thePlayer);
      if (recipes != null) {
         this.nextButton.enabled = this.selectedMerchantRecipe < recipes.size() - 1;
         this.previousButton.enabled = this.selectedMerchantRecipe > 0;
      }
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      boolean flag = false;
      if (button == this.nextButton) {
         this.selectedMerchantRecipe++;
         MerchantRecipeList recipes = this.merchant.getRecipes(this.mc.thePlayer);
         if (recipes != null && this.selectedMerchantRecipe >= recipes.size()) {
            this.selectedMerchantRecipe = recipes.size() - 1;
         }

         flag = true;
      } else if (button == this.previousButton) {
         this.selectedMerchantRecipe--;
         if (this.selectedMerchantRecipe < 0) {
            this.selectedMerchantRecipe = 0;
         }

         flag = true;
      }

      if (flag) {
         ((ContainerMerchant)this.inventorySlots).setCurrentRecipeIndex(this.selectedMerchantRecipe);
         PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
         packetBuffer.writeInt(this.selectedMerchantRecipe);
         this.mc.getNetHandler().addToSendQueue(new C17PacketCustomPayload("MC|TrSel", packetBuffer));
      }
   }

   @Override
   public void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
      int n = (this.width - this.xSize) / 2;
      int n2 = (this.height - this.ySize) / 2;
      this.drawTexturedModalRect(n, n2, 0, 0, this.xSize, this.ySize);
      MerchantRecipeList recipes = this.merchant.getRecipes(this.mc.thePlayer);
      if (recipes != null && !recipes.isEmpty()) {
         int selectedMerchantRecipe = this.selectedMerchantRecipe;
         if (selectedMerchantRecipe < 0 || selectedMerchantRecipe >= recipes.size()) {
            return;
         }

         MerchantRecipe merchantRecipe = recipes.get(selectedMerchantRecipe);
         if (merchantRecipe.isRecipeDisabled()) {
            this.mc.getTextureManager().bindTexture(MERCHANT_GUI_TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableLighting();
            this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 21, 212, 0, 28, 21);
            this.drawTexturedModalRect(this.guiLeft + 83, this.guiTop + 51, 212, 0, 28, 21);
         }
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      super.drawScreen(mouseX, mouseY, partialTicks);
      MerchantRecipeList recipes = this.merchant.getRecipes(this.mc.thePlayer);
      if (recipes != null && !recipes.isEmpty()) {
         int n = (this.width - this.xSize) / 2;
         int n2 = (this.height - this.ySize) / 2;
         int selectedMerchantRecipe = this.selectedMerchantRecipe;
         MerchantRecipe merchantRecipe = recipes.get(selectedMerchantRecipe);
         ItemStack itemToBuy = merchantRecipe.getItemToBuy();
         ItemStack secondItemToBuy = merchantRecipe.getSecondItemToBuy();
         ItemStack itemToSell = merchantRecipe.getItemToSell();
         GlStateManager.pushMatrix();
         RenderHelper.enableGUIStandardItemLighting();
         GlStateManager.disableLighting();
         GlStateManager.enableRescaleNormal();
         GlStateManager.enableColorMaterial();
         GlStateManager.enableLighting();
         this.itemRender.zLevel = 100.0F;
         this.itemRender.renderItemAndEffectIntoGUI(itemToBuy, n + 36, n2 + 24);
         this.itemRender.renderItemOverlays(this.fontRendererObj, itemToBuy, n + 36, n2 + 24);
         if (secondItemToBuy != null) {
            this.itemRender.renderItemAndEffectIntoGUI(secondItemToBuy, n + 62, n2 + 24);
            this.itemRender.renderItemOverlays(this.fontRendererObj, secondItemToBuy, n + 62, n2 + 24);
         }

         this.itemRender.renderItemAndEffectIntoGUI(itemToSell, n + 120, n2 + 24);
         this.itemRender.renderItemOverlays(this.fontRendererObj, itemToSell, n + 120, n2 + 24);
         this.itemRender.zLevel = 0.0F;
         GlStateManager.disableLighting();
         if (this.isPointInRegion(36, 24, 16, 16, mouseX, mouseY) && itemToBuy != null) {
            this.renderToolTip(itemToBuy, mouseX, mouseY);
         } else if (secondItemToBuy != null && this.isPointInRegion(62, 24, 16, 16, mouseX, mouseY) && secondItemToBuy != null) {
            this.renderToolTip(secondItemToBuy, mouseX, mouseY);
         } else if (itemToSell != null && this.isPointInRegion(120, 24, 16, 16, mouseX, mouseY) && itemToSell != null) {
            this.renderToolTip(itemToSell, mouseX, mouseY);
         } else if (merchantRecipe.isRecipeDisabled() && (this.isPointInRegion(83, 21, 28, 21, mouseX, mouseY) || this.isPointInRegion(83, 51, 28, 21, mouseX, mouseY))) {
            this.drawCreativeTabHoveringText(I18n.format("merchant.deprecated"), mouseX, mouseY);
         }

         GlStateManager.popMatrix();
         GlStateManager.enableLighting();
         GlStateManager.enableDepth();
         RenderHelper.enableStandardItemLighting();
      }
   }

   public IMerchant getMerchant() {
      return this.merchant;
   }

   static class MerchantButton extends GuiButton {
      public boolean isRight;

      public MerchantButton(int buttonID, int x, int y, boolean flag) {
         super(buttonID, x, y, 12, 19, "");
         this.isRight = flag;
      }

      @Override
      public void drawButton(Minecraft mc, int mouseX, int mouseY) {
         if (this.visible) {
            mc.getTextureManager().bindTexture(GuiMerchant.MERCHANT_GUI_TEXTURE);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            boolean flag = mouseX >= this.xPosition
               && mouseY >= this.yPosition
               && mouseX < this.xPosition + this.width
               && mouseY < this.yPosition + this.height;
            int n = 0;
            int n2 = 176;
            if (!this.enabled) {
               n2 += this.width * 2;
            } else if (flag) {
               n2 += this.width;
            }

            if (!this.isRight) {
               n += this.height;
            }

            this.drawTexturedModalRect(this.xPosition, this.yPosition, n2, n, this.width, this.height);
         }
      }
   }
}
