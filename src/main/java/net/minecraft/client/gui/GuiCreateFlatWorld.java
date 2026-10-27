package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;

public class GuiCreateFlatWorld extends GuiScreen {
   public FlatGeneratorInfo theFlatGeneratorInfo = FlatGeneratorInfo.getDefaultFlatGenerator();
   public GuiCreateWorld createWorldGui;
   public String flatWorldTitle;
   public String tileText;
   public String heightText;
   public GuiCreateFlatWorld.Details createFlatWorldListSlotGui;
   public GuiButton addLayer;
   public GuiButton editLayer;
   public GuiButton removeLayer;

   public GuiCreateFlatWorld(GuiCreateWorld createWorldGuiIn, String str) {
      this.createWorldGui = createWorldGuiIn;
      this.setConfigHelper(str);
   }

   public String getConfigString() {
      return this.theFlatGeneratorInfo.toString();
   }

   public void setConfigHelper(String str) {
      this.theFlatGeneratorInfo = FlatGeneratorInfo.createFlatGeneratorFromString(str);
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      this.flatWorldTitle = I18n.format("createWorld.customize.flat.title");
      this.tileText = I18n.format("createWorld.customize.flat.tile");
      this.heightText = I18n.format("createWorld.customize.flat.height");
      this.createFlatWorldListSlotGui = new GuiCreateFlatWorld.Details();
      this.buttonList
         .add(this.addLayer = new GuiButton(2, this.width / 2 - 154, this.height - 52, 100, 20, I18n.format("createWorld.customize.flat.addLayer") + " (NYI)"));
      this.buttonList
         .add(this.editLayer = new GuiButton(3, this.width / 2 - 50, this.height - 52, 100, 20, I18n.format("createWorld.customize.flat.editLayer") + " (NYI)"));
      this.buttonList
         .add(this.removeLayer = new GuiButton(4, this.width / 2 - 155, this.height - 52, 150, 20, I18n.format("createWorld.customize.flat.removeLayer")));
      this.buttonList.add(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("gui.done")));
      this.buttonList.add(new GuiButton(5, this.width / 2 + 5, this.height - 52, 150, 20, I18n.format("createWorld.customize.presets")));
      this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")));
      this.addLayer.visible = this.editLayer.visible = false;
      this.theFlatGeneratorInfo.updateLayerLevel();
      this.updateButtons();
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.createFlatWorldListSlotGui.handleMouseInput();
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      int n = this.theFlatGeneratorInfo.getFlatLayers().size() - this.createFlatWorldListSlotGui.focusedEntry - 1;
      if (button.id == 1) {
         this.mc.displayGuiScreen(this.createWorldGui);
      } else if (button.id == 0) {
         this.createWorldGui.chunkProviderSettingsJson = this.getConfigString();
         this.mc.displayGuiScreen(this.createWorldGui);
      } else if (button.id == 5) {
         this.mc.displayGuiScreen(new GuiFlatPresets(this));
      } else if (button.id == 4 && this.hasLayerSelected()) {
         this.theFlatGeneratorInfo.getFlatLayers().remove(n);
         this.createFlatWorldListSlotGui.focusedEntry = Math.min(
            this.createFlatWorldListSlotGui.focusedEntry, this.theFlatGeneratorInfo.getFlatLayers().size() - 1
         );
      }

      this.theFlatGeneratorInfo.updateLayerLevel();
      this.updateButtons();
   }

   public void updateButtons() {
      boolean hasLayerSelected = this.hasLayerSelected();
      this.removeLayer.enabled = hasLayerSelected;
      this.editLayer.enabled = hasLayerSelected;
      this.editLayer.enabled = false;
      this.addLayer.enabled = false;
   }

   public boolean hasLayerSelected() {
      return this.createFlatWorldListSlotGui.focusedEntry > -1
         && this.createFlatWorldListSlotGui.focusedEntry < this.theFlatGeneratorInfo.getFlatLayers().size();
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.createFlatWorldListSlotGui.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRendererObj, this.flatWorldTitle, this.width / 2, 8, 16777215);
      int n = this.width / 2 - 92 - 16;
      this.drawString(this.fontRendererObj, this.tileText, n, 32, 16777215);
      this.drawString(this.fontRendererObj, this.heightText, n + 2 + 213 - this.fontRendererObj.getStringWidth(this.heightText), 32, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   class Details extends GuiSlot {
      public int focusedEntry = -1;

      public Details() {
         super(GuiCreateFlatWorld.this.mc, GuiCreateFlatWorld.this.width, GuiCreateFlatWorld.this.height, 43, GuiCreateFlatWorld.this.height - 60, 24);
      }

      public void renderEntry(int n, int n2, ItemStack itemStack) {
         this.renderIconBackground(n + 1, n2 + 1);
         GlStateManager.enableRescaleNormal();
         if (itemStack != null && itemStack.getItem() != null) {
            RenderHelper.enableGUIStandardItemLighting();
            GuiCreateFlatWorld.this.itemRender.renderItemIntoGUI(itemStack, n + 2, n2 + 2);
            RenderHelper.disableStandardItemLighting();
         }

         GlStateManager.disableRescaleNormal();
      }

      public void renderIconBackground(int n, int n2) {
         this.renderIconBackground(n, n2, 0, 0);
      }

      public void renderIconBackground(int n, int n2, int n3, int n4) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(Gui.statIcons);
         float f = 0.0078125F;
         float f2 = 0.0078125F;
         byte b = 18;
         byte b2 = 18;
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldRenderer.pos(n + 0, n2 + 18, GuiCreateFlatWorld.zLevel).tex((n3 + 0) * 0.0078125F, (n4 + 18) * 0.0078125F).endVertex();
         worldRenderer.pos(n + 18, n2 + 18, GuiCreateFlatWorld.zLevel).tex((n3 + 18) * 0.0078125F, (n4 + 18) * 0.0078125F).endVertex();
         worldRenderer.pos(n + 18, n2 + 0, GuiCreateFlatWorld.zLevel).tex((n3 + 18) * 0.0078125F, (n4 + 0) * 0.0078125F).endVertex();
         worldRenderer.pos(n + 0, n2 + 0, GuiCreateFlatWorld.zLevel).tex((n3 + 0) * 0.0078125F, (n4 + 0) * 0.0078125F).endVertex();
         instance.draw();
      }

      @Override
      public int getSize() {
         return GuiCreateFlatWorld.this.theFlatGeneratorInfo.getFlatLayers().size();
      }

      @Override
      public void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
         this.focusedEntry = slotIndex;
         GuiCreateFlatWorld.this.updateButtons();
      }

      @Override
      public boolean isSelected(int slotIndex) {
         return slotIndex == this.focusedEntry;
      }

      @Override
      public void drawBackground() {
      }

      @Override
      public void drawSlot(int entryID, int n, int n2, int n3, int mouseXIn, int mouseYIn) {
         FlatLayerInfo flatLayerInfo = GuiCreateFlatWorld.this.theFlatGeneratorInfo
            .getFlatLayers()
            .get(GuiCreateFlatWorld.this.theFlatGeneratorInfo.getFlatLayers().size() - entryID - 1);
         IBlockState layerMaterial = flatLayerInfo.getLayerMaterial();
         Block block = layerMaterial.getBlock();
         Item itemFromBlock = Item.getItemFromBlock(block);
         ItemStack itemStack = block != Blocks.air && itemFromBlock != null ? new ItemStack(itemFromBlock, 1, block.getMetaFromState(layerMaterial)) : null;
         String str = itemStack == null ? "Air" : itemFromBlock.getItemStackDisplayName(itemStack);
         if (itemFromBlock == null) {
            if (block == Blocks.water || block == Blocks.flowing_water) {
               itemFromBlock = Items.water_bucket;
            } else if (block == Blocks.lava || block == Blocks.flowing_lava) {
               itemFromBlock = Items.lava_bucket;
            }

            if (itemFromBlock != null) {
               itemStack = new ItemStack(itemFromBlock, 1, block.getMetaFromState(layerMaterial));
               str = block.getLocalizedName();
            }
         }

         this.renderEntry(n, n2, itemStack);
         GuiCreateFlatWorld.this.fontRendererObj.drawString(str, n + 18 + 5, n2 + 3, 16777215);
         String str2;
         if (entryID == 0) {
            str2 = I18n.format("createWorld.customize.flat.layer.top", flatLayerInfo.getLayerCount());
         } else if (entryID == GuiCreateFlatWorld.this.theFlatGeneratorInfo.getFlatLayers().size() - 1) {
            str2 = I18n.format("createWorld.customize.flat.layer.bottom", flatLayerInfo.getLayerCount());
         } else {
            str2 = I18n.format("createWorld.customize.flat.layer", flatLayerInfo.getLayerCount());
         }

         GuiCreateFlatWorld.this.fontRendererObj
            .drawString(str2, n + 2 + 213 - GuiCreateFlatWorld.this.fontRendererObj.getStringWidth(str2), n2 + 3, 16777215);
      }

      @Override
      public int getScrollBarX() {
         return this.width - 70;
      }
   }
}
