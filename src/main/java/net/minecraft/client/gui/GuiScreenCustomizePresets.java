package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.gen.ChunkProviderSettings.Factory;
import org.lwjgl.input.Keyboard;

public class GuiScreenCustomizePresets extends GuiScreen {
   public static List<GuiScreenCustomizePresets.Info> entries = Lists.newArrayList();
   public GuiScreenCustomizePresets.ListPreset presetList;
   public GuiButton selectButton;
   public GuiTextField searchField;
   public String title = "Customize World Presets";
   public GuiCustomizeWorldScreen parent;
   public String shareText;
   public String listText;

   public GuiScreenCustomizePresets(GuiCustomizeWorldScreen guiCustomizeWorldScreen) {
      this.parent = guiCustomizeWorldScreen;
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      Keyboard.enableRepeatEvents(true);
      this.title = I18n.format("createWorld.customize.custom.presets.title");
      this.shareText = I18n.format("createWorld.customize.presets.share");
      this.listText = I18n.format("createWorld.customize.presets.list");
      this.searchField = new GuiTextField(2, this.fontRendererObj, 50, 40, this.width - 100, 20);
      this.presetList = new GuiScreenCustomizePresets.ListPreset();
      this.searchField.setMaxStringLength(2000);
      this.searchField.setText(this.parent.getPropsAsString());
      this.buttonList
         .add(this.selectButton = new GuiButton(0, this.width / 2 - 102, this.height - 27, 100, 20, I18n.format("createWorld.customize.presets.select")));
      this.buttonList.add(new GuiButton(1, this.width / 2 + 3, this.height - 27, 100, 20, I18n.format("gui.cancel")));
      this.updateSelectButton();
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.presetList.handleMouseInput();
   }

   @Override
   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      if (!this.searchField.textboxKeyTyped(typedChar, keyCode)) {
         super.keyTyped(typedChar, keyCode);
      }
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      switch (button.id) {
         case 0:
            this.parent.loadProps(this.searchField.getText());
            this.mc.displayGuiScreen(this.parent);
            break;
         case 1:
            this.mc.displayGuiScreen(this.parent);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.presetList.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 8, 16777215);
      this.drawString(this.fontRendererObj, this.shareText, 50, 30, 10526880);
      this.drawString(this.fontRendererObj, this.listText, 50, 70, 10526880);
      this.searchField.drawTextBox();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   @Override
   public void updateScreen() {
      this.searchField.updateCursorCounter();
      super.updateScreen();
   }

   public void updateSelectButton() {
      this.selectButton.enabled = this.isEntrySelected();
   }

   public boolean isEntrySelected() {
      return this.presetList.selectedEntryIndex > -1 && this.presetList.selectedEntryIndex < entries.size() || this.searchField.getText().length() > 1;
   }

   static {
      Factory jsonToFactory = Factory.jsonToFactory(
         "{ \"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":512.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":5000.0, \"mainNoiseScaleY\":1000.0, \"mainNoiseScaleZ\":5000.0, \"baseSize\":8.5, \"stretchY\":8.0, \"biomeDepthWeight\":2.0, \"biomeDepthOffset\":0.5, \"biomeScaleWeight\":2.0, \"biomeScaleOffset\":0.375, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":255 }"
      );
      ResourceLocation resourceLocation = new ResourceLocation("textures/gui/presets/water.png");
      entries.add(new GuiScreenCustomizePresets.Info(I18n.format("createWorld.customize.custom.preset.waterWorld"), resourceLocation, jsonToFactory));
      jsonToFactory = Factory.jsonToFactory(
         "{\"coordinateScale\":3000.0, \"heightScale\":6000.0, \"upperLimitScale\":250.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":80.0, \"mainNoiseScaleY\":160.0, \"mainNoiseScaleZ\":80.0, \"baseSize\":8.5, \"stretchY\":10.0, \"biomeDepthWeight\":1.0, \"biomeDepthOffset\":0.0, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":0.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":63 }"
      );
      resourceLocation = new ResourceLocation("textures/gui/presets/isles.png");
      entries.add(new GuiScreenCustomizePresets.Info(I18n.format("createWorld.customize.custom.preset.isleLand"), resourceLocation, jsonToFactory));
      jsonToFactory = Factory.jsonToFactory(
         "{\"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":512.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":5000.0, \"mainNoiseScaleY\":1000.0, \"mainNoiseScaleZ\":5000.0, \"baseSize\":8.5, \"stretchY\":5.0, \"biomeDepthWeight\":2.0, \"biomeDepthOffset\":1.0, \"biomeScaleWeight\":4.0, \"biomeScaleOffset\":1.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":63 }"
      );
      resourceLocation = new ResourceLocation("textures/gui/presets/delight.png");
      entries.add(new GuiScreenCustomizePresets.Info(I18n.format("createWorld.customize.custom.preset.caveDelight"), resourceLocation, jsonToFactory));
      jsonToFactory = Factory.jsonToFactory(
         "{\"coordinateScale\":738.41864, \"heightScale\":157.69133, \"upperLimitScale\":801.4267, \"lowerLimitScale\":1254.1643, \"depthNoiseScaleX\":374.93652, \"depthNoiseScaleZ\":288.65228, \"depthNoiseScaleExponent\":1.2092624, \"mainNoiseScaleX\":1355.9908, \"mainNoiseScaleY\":745.5343, \"mainNoiseScaleZ\":1183.464, \"baseSize\":1.8758626, \"stretchY\":1.7137525, \"biomeDepthWeight\":1.7553768, \"biomeDepthOffset\":3.4701107, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":2.535211, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":63 }"
      );
      resourceLocation = new ResourceLocation("textures/gui/presets/madness.png");
      entries.add(new GuiScreenCustomizePresets.Info(I18n.format("createWorld.customize.custom.preset.mountains"), resourceLocation, jsonToFactory));
      jsonToFactory = Factory.jsonToFactory(
         "{\"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":512.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":1000.0, \"mainNoiseScaleY\":3000.0, \"mainNoiseScaleZ\":1000.0, \"baseSize\":8.5, \"stretchY\":10.0, \"biomeDepthWeight\":1.0, \"biomeDepthOffset\":0.0, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":0.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":20 }"
      );
      resourceLocation = new ResourceLocation("textures/gui/presets/drought.png");
      entries.add(new GuiScreenCustomizePresets.Info(I18n.format("createWorld.customize.custom.preset.drought"), resourceLocation, jsonToFactory));
      jsonToFactory = Factory.jsonToFactory(
         "{\"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":2.0, \"lowerLimitScale\":64.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":80.0, \"mainNoiseScaleY\":160.0, \"mainNoiseScaleZ\":80.0, \"baseSize\":8.5, \"stretchY\":12.0, \"biomeDepthWeight\":1.0, \"biomeDepthOffset\":0.0, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":0.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":false, \"seaLevel\":6 }"
      );
      resourceLocation = new ResourceLocation("textures/gui/presets/chaos.png");
      entries.add(new GuiScreenCustomizePresets.Info(I18n.format("createWorld.customize.custom.preset.caveChaos"), resourceLocation, jsonToFactory));
      jsonToFactory = Factory.jsonToFactory(
         "{\"coordinateScale\":684.412, \"heightScale\":684.412, \"upperLimitScale\":512.0, \"lowerLimitScale\":512.0, \"depthNoiseScaleX\":200.0, \"depthNoiseScaleZ\":200.0, \"depthNoiseScaleExponent\":0.5, \"mainNoiseScaleX\":80.0, \"mainNoiseScaleY\":160.0, \"mainNoiseScaleZ\":80.0, \"baseSize\":8.5, \"stretchY\":12.0, \"biomeDepthWeight\":1.0, \"biomeDepthOffset\":0.0, \"biomeScaleWeight\":1.0, \"biomeScaleOffset\":0.0, \"useCaves\":true, \"useDungeons\":true, \"dungeonChance\":8, \"useStrongholds\":true, \"useVillages\":true, \"useMineShafts\":true, \"useTemples\":true, \"useRavines\":true, \"useWaterLakes\":true, \"waterLakeChance\":4, \"useLavaLakes\":true, \"lavaLakeChance\":80, \"useLavaOceans\":true, \"seaLevel\":40 }"
      );
      resourceLocation = new ResourceLocation("textures/gui/presets/luck.png");
      entries.add(new GuiScreenCustomizePresets.Info(I18n.format("createWorld.customize.custom.preset.goodLuck"), resourceLocation, jsonToFactory));
   }

   static class Info {
      public String name;
      public ResourceLocation texture;
      public Factory utilities;

      public Info(String str, ResourceLocation resourceLocation, Factory factory) {
         this.name = str;
         this.texture = resourceLocation;
         this.utilities = factory;
      }
   }

   class ListPreset extends GuiSlot {
      public int selectedEntryIndex = -1;

      public ListPreset() {
         super(
            GuiScreenCustomizePresets.this.mc,
            GuiScreenCustomizePresets.this.width,
            GuiScreenCustomizePresets.this.height,
            80,
            GuiScreenCustomizePresets.this.height - 32,
            38
         );
      }

      @Override
      public int getSize() {
         return GuiScreenCustomizePresets.entries.size();
      }

      @Override
      public void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
         this.selectedEntryIndex = slotIndex;
         GuiScreenCustomizePresets.this.updateSelectButton();
         GuiScreenCustomizePresets.this.searchField
            .setText(GuiScreenCustomizePresets.entries.get(GuiScreenCustomizePresets.this.presetList.selectedEntryIndex).utilities.toString());
      }

      @Override
      public boolean isSelected(int slotIndex) {
         return slotIndex == this.selectedEntryIndex;
      }

      @Override
      public void drawBackground() {
      }

      public void renderEntryBackground(int n, int n2, ResourceLocation resourceLocation) {
         int n3 = n + 5;
         GuiScreenCustomizePresets.this.drawHorizontalLine(n3 - 1, n3 + 32, n2 - 1, -2039584);
         GuiScreenCustomizePresets.this.drawHorizontalLine(n3 - 1, n3 + 32, n2 + 32, -6250336);
         GuiScreenCustomizePresets.this.drawVerticalLine(n3 - 1, n2 - 1, n2 + 32, -2039584);
         GuiScreenCustomizePresets.this.drawVerticalLine(n3 + 32, n2 - 1, n2 + 32, -6250336);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.mc.getTextureManager().bindTexture(resourceLocation);
         byte b = 32;
         byte b2 = 32;
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldRenderer.pos(n3 + 0, n2 + 32, 0.0).tex(0.0, 1.0).endVertex();
         worldRenderer.pos(n3 + 32, n2 + 32, 0.0).tex(1.0, 1.0).endVertex();
         worldRenderer.pos(n3 + 32, n2 + 0, 0.0).tex(1.0, 0.0).endVertex();
         worldRenderer.pos(n3 + 0, n2 + 0, 0.0).tex(0.0, 0.0).endVertex();
         instance.draw();
      }

      @Override
      public void drawSlot(int entryID, int n, int n2, int n3, int mouseXIn, int mouseYIn) {
         GuiScreenCustomizePresets.Info info = GuiScreenCustomizePresets.entries.get(entryID);
         this.renderEntryBackground(n, n2, info.texture);
         GuiScreenCustomizePresets.this.fontRendererObj.drawString(info.name, n + 32 + 10, n2 + 14, 16777215);
      }
   }
}
