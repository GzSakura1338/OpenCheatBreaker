package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.minecraft.block.BlockTallGrass.EnumType;
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
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;
import org.lwjgl.input.Keyboard;

public class GuiFlatPresets extends GuiScreen {
   public static List<GuiFlatPresets.LayerItem> FLAT_WORLD_PRESETS = Lists.newArrayList();
   public GuiCreateFlatWorld parentScreen;
   public String presetsTitle;
   public String presetsShare;
   public String listText;
   public GuiFlatPresets.ListSlot listWidget;
   public GuiButton selectButton;
   public GuiTextField customPresetField;

   public GuiFlatPresets(GuiCreateFlatWorld guiCreateFlatWorld) {
      this.parentScreen = guiCreateFlatWorld;
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      Keyboard.enableRepeatEvents(true);
      this.presetsTitle = I18n.format("createWorld.customize.presets.title");
      this.presetsShare = I18n.format("createWorld.customize.presets.share");
      this.listText = I18n.format("createWorld.customize.presets.list");
      this.customPresetField = new GuiTextField(2, this.fontRendererObj, 50, 40, this.width - 100, 20);
      this.listWidget = new GuiFlatPresets.ListSlot();
      this.customPresetField.setMaxStringLength(1230);
      this.customPresetField.setText(this.parentScreen.getConfigString());
      this.buttonList
         .add(this.selectButton = new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("createWorld.customize.presets.select")));
      this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")));
      this.updateSelectButton();
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.listWidget.handleMouseInput();
   }

   @Override
   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      this.customPresetField.mouseClicked(mouseX, mouseY, mouseButton);
      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      if (!this.customPresetField.textboxKeyTyped(typedChar, keyCode)) {
         super.keyTyped(typedChar, keyCode);
      }
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 0 && this.isEntrySelected()) {
         this.parentScreen.setConfigHelper(this.customPresetField.getText());
         this.mc.displayGuiScreen(this.parentScreen);
      } else if (button.id == 1) {
         this.mc.displayGuiScreen(this.parentScreen);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.listWidget.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRendererObj, this.presetsTitle, this.width / 2, 8, 16777215);
      this.drawString(this.fontRendererObj, this.presetsShare, 50, 30, 10526880);
      this.drawString(this.fontRendererObj, this.listText, 50, 70, 10526880);
      this.customPresetField.drawTextBox();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void updateScreen() {
      this.customPresetField.updateCursorCounter();
      super.updateScreen();
   }

   public void updateSelectButton() {
      boolean isEntrySelected = this.isEntrySelected();
      this.selectButton.enabled = isEntrySelected;
   }

   public boolean isEntrySelected() {
      return this.listWidget.selectedEntryIndex > -1 && this.listWidget.selectedEntryIndex < FLAT_WORLD_PRESETS.size()
         || this.customPresetField.getText().length() > 1;
   }

   public static void addRedstonePreset(String str, Item item, BiomeGenBase biomeGenBase, FlatLayerInfo... flatLayerInfos) {
      addOverworldPreset(str, item, 0, biomeGenBase, (List<String>)null, flatLayerInfos);
   }

   public static void addPreset(String str, Item item, BiomeGenBase biomeGenBase, List<String> list, FlatLayerInfo... flatLayerInfos) {
      addOverworldPreset(str, item, 0, biomeGenBase, list, flatLayerInfos);
   }

   public static void addOverworldPreset(String str, Item item, int n, BiomeGenBase biomeGenBase, List<String> list, FlatLayerInfo... flatLayerInfos) {
      FlatGeneratorInfo flatGeneratorInfo = new FlatGeneratorInfo();

      for (int i = flatLayerInfos.length - 1; i >= 0; i--) {
         flatGeneratorInfo.getFlatLayers().add(flatLayerInfos[i]);
      }

      flatGeneratorInfo.setBiome(biomeGenBase.biomeID);
      flatGeneratorInfo.updateLayerLevel();
      if (list != null) {
         for (String str2 : list) {
            flatGeneratorInfo.getWorldFeatures().put(str2, Maps.newHashMap());
         }
      }

      FLAT_WORLD_PRESETS.add(new GuiFlatPresets.LayerItem(item, n, str, flatGeneratorInfo.toString()));
   }

   static {
      addPreset(
         "Classic Flat",
         Item.getItemFromBlock(Blocks.grass),
         BiomeGenBase.plains,
         Arrays.asList("village"),
         new FlatLayerInfo(1, Blocks.grass),
         new FlatLayerInfo(2, Blocks.dirt),
         new FlatLayerInfo(1, Blocks.bedrock)
      );
      addPreset(
         "Tunnelers' Dream",
         Item.getItemFromBlock(Blocks.stone),
         BiomeGenBase.extremeHills,
         Arrays.asList("biome_1", "dungeon", "decoration", "stronghold", "mineshaft"),
         new FlatLayerInfo(1, Blocks.grass),
         new FlatLayerInfo(5, Blocks.dirt),
         new FlatLayerInfo(230, Blocks.stone),
         new FlatLayerInfo(1, Blocks.bedrock)
      );
      addPreset(
         "Water World",
         Items.water_bucket,
         BiomeGenBase.deepOcean,
         Arrays.asList("biome_1", "oceanmonument"),
         new FlatLayerInfo(90, Blocks.water),
         new FlatLayerInfo(5, Blocks.sand),
         new FlatLayerInfo(5, Blocks.dirt),
         new FlatLayerInfo(5, Blocks.stone),
         new FlatLayerInfo(1, Blocks.bedrock)
      );
      addOverworldPreset(
         "Overworld",
         Item.getItemFromBlock(Blocks.tallgrass),
         EnumType.GRASS.getMeta(),
         BiomeGenBase.plains,
         Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon", "lake", "lava_lake"),
         new FlatLayerInfo(1, Blocks.grass),
         new FlatLayerInfo(3, Blocks.dirt),
         new FlatLayerInfo(59, Blocks.stone),
         new FlatLayerInfo(1, Blocks.bedrock)
      );
      addPreset(
         "Snowy Kingdom",
         Item.getItemFromBlock(Blocks.snow_layer),
         BiomeGenBase.icePlains,
         Arrays.asList("village", "biome_1"),
         new FlatLayerInfo(1, Blocks.snow_layer),
         new FlatLayerInfo(1, Blocks.grass),
         new FlatLayerInfo(3, Blocks.dirt),
         new FlatLayerInfo(59, Blocks.stone),
         new FlatLayerInfo(1, Blocks.bedrock)
      );
      addPreset(
         "Bottomless Pit",
         Items.feather,
         BiomeGenBase.plains,
         Arrays.asList("village", "biome_1"),
         new FlatLayerInfo(1, Blocks.grass),
         new FlatLayerInfo(3, Blocks.dirt),
         new FlatLayerInfo(2, Blocks.cobblestone)
      );
      addPreset(
         "Desert",
         Item.getItemFromBlock(Blocks.sand),
         BiomeGenBase.desert,
         Arrays.asList("village", "biome_1", "decoration", "stronghold", "mineshaft", "dungeon"),
         new FlatLayerInfo(8, Blocks.sand),
         new FlatLayerInfo(52, Blocks.sandstone),
         new FlatLayerInfo(3, Blocks.stone),
         new FlatLayerInfo(1, Blocks.bedrock)
      );
      addRedstonePreset(
         "Redstone Ready",
         Items.redstone,
         BiomeGenBase.desert,
         new FlatLayerInfo(52, Blocks.sandstone),
         new FlatLayerInfo(3, Blocks.stone),
         new FlatLayerInfo(1, Blocks.bedrock)
      );
   }

   static class LayerItem {
      public Item icon;
      public int iconDamage;
      public String name;
      public String config;

      public LayerItem(Item item, int n, String str, String str2) {
         this.icon = item;
         this.iconDamage = n;
         this.name = str;
         this.config = str2;
      }
   }

   class ListSlot extends GuiSlot {
      public int selectedEntryIndex = -1;

      public ListSlot() {
         super(GuiFlatPresets.this.mc, GuiFlatPresets.this.width, GuiFlatPresets.this.height, 80, GuiFlatPresets.this.height - 37, 24);
      }

      public void renderEntry(int n, int n2, Item item, int n3) {
         this.renderIconBackground(n + 1, n2 + 1);
         GlStateManager.enableRescaleNormal();
         RenderHelper.enableGUIStandardItemLighting();
         GuiFlatPresets.this.itemRender.renderItemIntoGUI(new ItemStack(item, 1, n3), n + 2, n2 + 2);
         RenderHelper.disableStandardItemLighting();
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
         worldRenderer.pos(n + 0, n2 + 18, GuiFlatPresets.zLevel).tex((n3 + 0) * 0.0078125F, (n4 + 18) * 0.0078125F).endVertex();
         worldRenderer.pos(n + 18, n2 + 18, GuiFlatPresets.zLevel).tex((n3 + 18) * 0.0078125F, (n4 + 18) * 0.0078125F).endVertex();
         worldRenderer.pos(n + 18, n2 + 0, GuiFlatPresets.zLevel).tex((n3 + 18) * 0.0078125F, (n4 + 0) * 0.0078125F).endVertex();
         worldRenderer.pos(n + 0, n2 + 0, GuiFlatPresets.zLevel).tex((n3 + 0) * 0.0078125F, (n4 + 0) * 0.0078125F).endVertex();
         instance.draw();
      }

      @Override
      public int getSize() {
         return GuiFlatPresets.FLAT_WORLD_PRESETS.size();
      }

      @Override
      public void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
         this.selectedEntryIndex = slotIndex;
         GuiFlatPresets.this.updateSelectButton();
         GuiFlatPresets.this.customPresetField.setText(GuiFlatPresets.FLAT_WORLD_PRESETS.get(GuiFlatPresets.this.listWidget.selectedEntryIndex).config);
      }

      @Override
      public boolean isSelected(int slotIndex) {
         return slotIndex == this.selectedEntryIndex;
      }

      @Override
      public void drawBackground() {
      }

      @Override
      public void drawSlot(int entryID, int n, int n2, int n3, int mouseXIn, int mouseYIn) {
         GuiFlatPresets.LayerItem layerItem = GuiFlatPresets.FLAT_WORLD_PRESETS.get(entryID);
         this.renderEntry(n, n2, layerItem.icon, layerItem.iconDamage);
         GuiFlatPresets.this.fontRendererObj.drawString(layerItem.name, n + 18 + 5, n2 + 6, 16777215);
      }
   }
}
