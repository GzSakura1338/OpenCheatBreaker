package net.minecraft.client.gui;

import com.google.common.base.Predicate;
import com.google.common.primitives.Floats;
import java.io.IOException;
import java.util.Random;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.MathHelper;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.ChunkProviderSettings.Factory;

public class GuiCustomizeWorldScreen extends GuiScreen implements GuiPageButtonList.GuiResponder, GuiSlider.FormatHelper {
   public String title = "Customize World Settings";
   public String page = "Page 1 of 3";
   public String subtitle = "Basic Settings";
   public String[] pageNames = new String[4];
   public boolean modified = false;
   public GuiPageButtonList settingsWidget;
   public GuiButton doneButton;
   public GuiButton randomizeButton;
   public GuiButton resetToDefaultsButton;
   public GuiButton previousButton;
   public GuiButton nextButton;
   public GuiButton yesButton;
   public GuiButton noButton;
   public GuiButton presetsButton;
   public int buttonLastPushed = 0;
   public boolean cancelled = false;
   public Predicate<String> floatVerifier = new Predicate<String>() {
      public boolean apply(String str) {
         Float tryParse = Floats.tryParse(str);
         return str.length() == 0 || tryParse != null && Floats.isFinite(tryParse) && tryParse >= 0.0F;
      }
   };
   public Factory defaultProps = new Factory();
   public Random random = new Random();
   public Factory props;
   public GuiCreateWorld parent;

   public GuiCustomizeWorldScreen(GuiScreen guiScreen, String str) {
      this.parent = (GuiCreateWorld)guiScreen;
      this.loadProps(str);
   }

   @Override
   public void initGui() {
      int n = 0;
      int n2 = 0;
      if (this.settingsWidget != null) {
         n = this.settingsWidget.getCurrentPageId();
         n2 = this.settingsWidget.getAmountScrolled();
      }

      this.title = I18n.format("options.customizeTitle");
      this.buttonList.clear();
      this.buttonList.add(this.previousButton = new GuiButton(302, 20, 5, 80, 20, I18n.format("createWorld.customize.custom.prev")));
      this.buttonList.add(this.nextButton = new GuiButton(303, this.width - 100, 5, 80, 20, I18n.format("createWorld.customize.custom.next")));
      this.buttonList
         .add(
            this.resetToDefaultsButton = new GuiButton(
               304, this.width / 2 - 187, this.height - 27, 90, 20, I18n.format("createWorld.customize.custom.defaults")
            )
         );
      this.buttonList
         .add(this.randomizeButton = new GuiButton(301, this.width / 2 - 92, this.height - 27, 90, 20, I18n.format("createWorld.customize.custom.randomize")));
      this.buttonList
         .add(this.presetsButton = new GuiButton(305, this.width / 2 + 3, this.height - 27, 90, 20, I18n.format("createWorld.customize.custom.presets")));
      this.buttonList.add(this.doneButton = new GuiButton(300, this.width / 2 + 98, this.height - 27, 90, 20, I18n.format("gui.done")));
      this.resetToDefaultsButton.enabled = this.modified;
      this.yesButton = new GuiButton(306, this.width / 2 - 55, 160, 50, 20, I18n.format("gui.yes"));
      this.yesButton.visible = false;
      this.buttonList.add(this.yesButton);
      this.noButton = new GuiButton(307, this.width / 2 + 5, 160, 50, 20, I18n.format("gui.no"));
      this.noButton.visible = false;
      this.buttonList.add(this.noButton);
      if (this.buttonLastPushed != 0) {
         this.yesButton.visible = true;
         this.noButton.visible = true;
      }

      this.initPages();
      if (n != 0) {
         this.settingsWidget.setCurrentPage(n);
         this.settingsWidget.scrollBy(n2);
         this.initButtons();
      }
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.settingsWidget.handleMouseInput();
   }

   public void initPages() {
      GuiPageButtonList.GuiListEntry[] guiListEntries = new GuiPageButtonList.GuiListEntry[]{
         new GuiPageButtonList.GuiSlideEntry(160, I18n.format("createWorld.customize.custom.seaLevel"), true, this, 1.0F, 255.0F, this.props.seaLevel),
         new GuiPageButtonList.GuiButtonEntry(148, I18n.format("createWorld.customize.custom.useCaves"), true, this.props.useCaves),
         new GuiPageButtonList.GuiButtonEntry(150, I18n.format("createWorld.customize.custom.useStrongholds"), true, this.props.useStrongholds),
         new GuiPageButtonList.GuiButtonEntry(151, I18n.format("createWorld.customize.custom.useVillages"), true, this.props.useVillages),
         new GuiPageButtonList.GuiButtonEntry(152, I18n.format("createWorld.customize.custom.useMineShafts"), true, this.props.useMineShafts),
         new GuiPageButtonList.GuiButtonEntry(153, I18n.format("createWorld.customize.custom.useTemples"), true, this.props.useTemples),
         new GuiPageButtonList.GuiButtonEntry(210, I18n.format("createWorld.customize.custom.useMonuments"), true, this.props.useMonuments),
         new GuiPageButtonList.GuiButtonEntry(154, I18n.format("createWorld.customize.custom.useRavines"), true, this.props.useRavines),
         new GuiPageButtonList.GuiButtonEntry(149, I18n.format("createWorld.customize.custom.useDungeons"), true, this.props.useDungeons),
         new GuiPageButtonList.GuiSlideEntry(157, I18n.format("createWorld.customize.custom.dungeonChance"), true, this, 1.0F, 100.0F, this.props.dungeonChance),
         new GuiPageButtonList.GuiButtonEntry(155, I18n.format("createWorld.customize.custom.useWaterLakes"), true, this.props.useWaterLakes),
         new GuiPageButtonList.GuiSlideEntry(
            158, I18n.format("createWorld.customize.custom.waterLakeChance"), true, this, 1.0F, 100.0F, this.props.waterLakeChance
         ),
         new GuiPageButtonList.GuiButtonEntry(156, I18n.format("createWorld.customize.custom.useLavaLakes"), true, this.props.useLavaLakes),
         new GuiPageButtonList.GuiSlideEntry(
            159, I18n.format("createWorld.customize.custom.lavaLakeChance"), true, this, 10.0F, 100.0F, this.props.lavaLakeChance
         ),
         new GuiPageButtonList.GuiButtonEntry(161, I18n.format("createWorld.customize.custom.useLavaOceans"), true, this.props.useLavaOceans),
         new GuiPageButtonList.GuiSlideEntry(162, I18n.format("createWorld.customize.custom.fixedBiome"), true, this, -1.0F, 37.0F, this.props.fixedBiome),
         new GuiPageButtonList.GuiSlideEntry(163, I18n.format("createWorld.customize.custom.biomeSize"), true, this, 1.0F, 8.0F, this.props.biomeSize),
         new GuiPageButtonList.GuiSlideEntry(164, I18n.format("createWorld.customize.custom.riverSize"), true, this, 1.0F, 5.0F, this.props.riverSize)
      };
      GuiPageButtonList.GuiListEntry[] guiListEntries2 = new GuiPageButtonList.GuiListEntry[]{
         new GuiPageButtonList.GuiLabelEntry(416, I18n.format("tile.dirt.name"), false),
         null,
         new GuiPageButtonList.GuiSlideEntry(165, I18n.format("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, this.props.dirtSize),
         new GuiPageButtonList.GuiSlideEntry(166, I18n.format("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, this.props.dirtCount),
         new GuiPageButtonList.GuiSlideEntry(167, I18n.format("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, this.props.dirtMinHeight),
         new GuiPageButtonList.GuiSlideEntry(168, I18n.format("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, this.props.dirtMaxHeight),
         new GuiPageButtonList.GuiLabelEntry(417, I18n.format("tile.gravel.name"), false),
         null,
         new GuiPageButtonList.GuiSlideEntry(169, I18n.format("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, this.props.gravelSize),
         new GuiPageButtonList.GuiSlideEntry(170, I18n.format("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, this.props.gravelCount),
         new GuiPageButtonList.GuiSlideEntry(171, I18n.format("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, this.props.gravelMinHeight),
         new GuiPageButtonList.GuiSlideEntry(172, I18n.format("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, this.props.gravelMaxHeight),
         new GuiPageButtonList.GuiLabelEntry(418, I18n.format("tile.stone.granite.name"), false),
         null,
         new GuiPageButtonList.GuiSlideEntry(173, I18n.format("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, this.props.graniteSize),
         new GuiPageButtonList.GuiSlideEntry(174, I18n.format("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, this.props.graniteCount),
         new GuiPageButtonList.GuiSlideEntry(175, I18n.format("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, this.props.graniteMinHeight),
         new GuiPageButtonList.GuiSlideEntry(176, I18n.format("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, this.props.graniteMaxHeight),
         new GuiPageButtonList.GuiLabelEntry(419, I18n.format("tile.stone.diorite.name"), false),
         null,
         new GuiPageButtonList.GuiSlideEntry(177, I18n.format("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, this.props.dioriteSize),
         new GuiPageButtonList.GuiSlideEntry(178, I18n.format("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, this.props.dioriteCount),
         new GuiPageButtonList.GuiSlideEntry(179, I18n.format("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, this.props.dioriteMinHeight),
         new GuiPageButtonList.GuiSlideEntry(180, I18n.format("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, this.props.dioriteMaxHeight),
         new GuiPageButtonList.GuiLabelEntry(420, I18n.format("tile.stone.andesite.name"), false),
         null,
         new GuiPageButtonList.GuiSlideEntry(181, I18n.format("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, this.props.andesiteSize),
         new GuiPageButtonList.GuiSlideEntry(182, I18n.format("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, this.props.andesiteCount),
         new GuiPageButtonList.GuiSlideEntry(
            183, I18n.format("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, this.props.andesiteMinHeight
         ),
         new GuiPageButtonList.GuiSlideEntry(
            184, I18n.format("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, this.props.andesiteMaxHeight
         ),
         new GuiPageButtonList.GuiLabelEntry(421, I18n.format("tile.oreCoal.name"), false),
         null,
         new GuiPageButtonList.GuiSlideEntry(185, I18n.format("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, this.props.coalSize),
         new GuiPageButtonList.GuiSlideEntry(186, I18n.format("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, this.props.coalCount),
         new GuiPageButtonList.GuiSlideEntry(187, I18n.format("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, this.props.coalMinHeight),
         new GuiPageButtonList.GuiSlideEntry(189, I18n.format("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, this.props.coalMaxHeight),
         new GuiPageButtonList.GuiLabelEntry(422, I18n.format("tile.oreIron.name"), false),
         null,
         new GuiPageButtonList.GuiSlideEntry(190, I18n.format("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, this.props.ironSize),
         new GuiPageButtonList.GuiSlideEntry(191, I18n.format("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, this.props.ironCount),
         new GuiPageButtonList.GuiSlideEntry(192, I18n.format("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, this.props.ironMinHeight),
         new GuiPageButtonList.GuiSlideEntry(193, I18n.format("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, this.props.ironMaxHeight),
         new GuiPageButtonList.GuiLabelEntry(423, I18n.format("tile.oreGold.name"), false),
         null,
         new GuiPageButtonList.GuiSlideEntry(194, I18n.format("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, this.props.goldSize),
         new GuiPageButtonList.GuiSlideEntry(195, I18n.format("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, this.props.goldCount),
         new GuiPageButtonList.GuiSlideEntry(196, I18n.format("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, this.props.goldMinHeight),
         new GuiPageButtonList.GuiSlideEntry(197, I18n.format("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, this.props.goldMaxHeight),
         new GuiPageButtonList.GuiLabelEntry(424, I18n.format("tile.oreRedstone.name"), false),
         null,
         new GuiPageButtonList.GuiSlideEntry(198, I18n.format("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, this.props.redstoneSize),
         new GuiPageButtonList.GuiSlideEntry(199, I18n.format("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, this.props.redstoneCount),
         new GuiPageButtonList.GuiSlideEntry(
            200, I18n.format("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, this.props.redstoneMinHeight
         ),
         new GuiPageButtonList.GuiSlideEntry(
            201, I18n.format("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, this.props.redstoneMaxHeight
         ),
         new GuiPageButtonList.GuiLabelEntry(425, I18n.format("tile.oreDiamond.name"), false),
         null,
         new GuiPageButtonList.GuiSlideEntry(202, I18n.format("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, this.props.diamondSize),
         new GuiPageButtonList.GuiSlideEntry(203, I18n.format("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, this.props.diamondCount),
         new GuiPageButtonList.GuiSlideEntry(204, I18n.format("createWorld.customize.custom.minHeight"), false, this, 0.0F, 255.0F, this.props.diamondMinHeight),
         new GuiPageButtonList.GuiSlideEntry(205, I18n.format("createWorld.customize.custom.maxHeight"), false, this, 0.0F, 255.0F, this.props.diamondMaxHeight),
         new GuiPageButtonList.GuiLabelEntry(426, I18n.format("tile.oreLapis.name"), false),
         null,
         new GuiPageButtonList.GuiSlideEntry(206, I18n.format("createWorld.customize.custom.size"), false, this, 1.0F, 50.0F, this.props.lapisSize),
         new GuiPageButtonList.GuiSlideEntry(207, I18n.format("createWorld.customize.custom.count"), false, this, 0.0F, 40.0F, this.props.lapisCount),
         new GuiPageButtonList.GuiSlideEntry(208, I18n.format("createWorld.customize.custom.center"), false, this, 0.0F, 255.0F, this.props.lapisCenterHeight),
         new GuiPageButtonList.GuiSlideEntry(209, I18n.format("createWorld.customize.custom.spread"), false, this, 0.0F, 255.0F, this.props.lapisSpread)
      };
      GuiPageButtonList.GuiListEntry[] guiListEntries3 = new GuiPageButtonList.GuiListEntry[]{
         new GuiPageButtonList.GuiSlideEntry(
            100, I18n.format("createWorld.customize.custom.mainNoiseScaleX"), false, this, 1.0F, 5000.0F, this.props.mainNoiseScaleX
         ),
         new GuiPageButtonList.GuiSlideEntry(
            101, I18n.format("createWorld.customize.custom.mainNoiseScaleY"), false, this, 1.0F, 5000.0F, this.props.mainNoiseScaleY
         ),
         new GuiPageButtonList.GuiSlideEntry(
            102, I18n.format("createWorld.customize.custom.mainNoiseScaleZ"), false, this, 1.0F, 5000.0F, this.props.mainNoiseScaleZ
         ),
         new GuiPageButtonList.GuiSlideEntry(
            103, I18n.format("createWorld.customize.custom.depthNoiseScaleX"), false, this, 1.0F, 2000.0F, this.props.depthNoiseScaleX
         ),
         new GuiPageButtonList.GuiSlideEntry(
            104, I18n.format("createWorld.customize.custom.depthNoiseScaleZ"), false, this, 1.0F, 2000.0F, this.props.depthNoiseScaleZ
         ),
         new GuiPageButtonList.GuiSlideEntry(
            105, I18n.format("createWorld.customize.custom.depthNoiseScaleExponent"), false, this, 0.01F, 20.0F, this.props.depthNoiseScaleExponent
         ),
         new GuiPageButtonList.GuiSlideEntry(106, I18n.format("createWorld.customize.custom.baseSize"), false, this, 1.0F, 25.0F, this.props.baseSize),
         new GuiPageButtonList.GuiSlideEntry(
            107, I18n.format("createWorld.customize.custom.coordinateScale"), false, this, 1.0F, 6000.0F, this.props.coordinateScale
         ),
         new GuiPageButtonList.GuiSlideEntry(108, I18n.format("createWorld.customize.custom.heightScale"), false, this, 1.0F, 6000.0F, this.props.heightScale),
         new GuiPageButtonList.GuiSlideEntry(109, I18n.format("createWorld.customize.custom.stretchY"), false, this, 0.01F, 50.0F, this.props.stretchY),
         new GuiPageButtonList.GuiSlideEntry(
            110, I18n.format("createWorld.customize.custom.upperLimitScale"), false, this, 1.0F, 5000.0F, this.props.upperLimitScale
         ),
         new GuiPageButtonList.GuiSlideEntry(
            111, I18n.format("createWorld.customize.custom.lowerLimitScale"), false, this, 1.0F, 5000.0F, this.props.lowerLimitScale
         ),
         new GuiPageButtonList.GuiSlideEntry(
            112, I18n.format("createWorld.customize.custom.biomeDepthWeight"), false, this, 1.0F, 20.0F, this.props.biomeDepthWeight
         ),
         new GuiPageButtonList.GuiSlideEntry(
            113, I18n.format("createWorld.customize.custom.biomeDepthOffset"), false, this, 0.0F, 20.0F, this.props.biomeDepthOffset
         ),
         new GuiPageButtonList.GuiSlideEntry(
            114, I18n.format("createWorld.customize.custom.biomeScaleWeight"), false, this, 1.0F, 20.0F, this.props.biomeScaleWeight
         ),
         new GuiPageButtonList.GuiSlideEntry(
            115, I18n.format("createWorld.customize.custom.biomeScaleOffset"), false, this, 0.0F, 20.0F, this.props.biomeScaleOffset
         )
      };
      GuiPageButtonList.GuiListEntry[] guiListEntries4 = new GuiPageButtonList.GuiListEntry[]{
         new GuiPageButtonList.GuiLabelEntry(400, I18n.format("createWorld.customize.custom.mainNoiseScaleX") + ":", false),
         new GuiPageButtonList.EditBoxEntry(132, String.format("%5.3f", this.props.mainNoiseScaleX), false, this.floatVerifier),
         new GuiPageButtonList.GuiLabelEntry(401, I18n.format("createWorld.customize.custom.mainNoiseScaleY") + ":", false),
         new GuiPageButtonList.EditBoxEntry(133, String.format("%5.3f", this.props.mainNoiseScaleY), false, this.floatVerifier),
         new GuiPageButtonList.GuiLabelEntry(402, I18n.format("createWorld.customize.custom.mainNoiseScaleZ") + ":", false),
         new GuiPageButtonList.EditBoxEntry(134, String.format("%5.3f", this.props.mainNoiseScaleZ), false, this.floatVerifier),
         new GuiPageButtonList.GuiLabelEntry(403, I18n.format("createWorld.customize.custom.depthNoiseScaleX") + ":", false),
         new GuiPageButtonList.EditBoxEntry(135, String.format("%5.3f", this.props.depthNoiseScaleX), false, this.floatVerifier),
         new GuiPageButtonList.GuiLabelEntry(404, I18n.format("createWorld.customize.custom.depthNoiseScaleZ") + ":", false),
         new GuiPageButtonList.EditBoxEntry(136, String.format("%5.3f", this.props.depthNoiseScaleZ), false, this.floatVerifier),
         new GuiPageButtonList.GuiLabelEntry(405, I18n.format("createWorld.customize.custom.depthNoiseScaleExponent") + ":", false),
         new GuiPageButtonList.EditBoxEntry(137, String.format("%2.3f", this.props.depthNoiseScaleExponent), false, this.floatVerifier),
         new GuiPageButtonList.GuiLabelEntry(406, I18n.format("createWorld.customize.custom.baseSize") + ":", false),
         new GuiPageButtonList.EditBoxEntry(138, String.format("%2.3f", this.props.baseSize), false, this.floatVerifier),
         new GuiPageButtonList.GuiLabelEntry(407, I18n.format("createWorld.customize.custom.coordinateScale") + ":", false),
         new GuiPageButtonList.EditBoxEntry(139, String.format("%5.3f", this.props.coordinateScale), false, this.floatVerifier),
         new GuiPageButtonList.GuiLabelEntry(408, I18n.format("createWorld.customize.custom.heightScale") + ":", false),
         new GuiPageButtonList.EditBoxEntry(140, String.format("%5.3f", this.props.heightScale), false, this.floatVerifier),
         new GuiPageButtonList.GuiLabelEntry(409, I18n.format("createWorld.customize.custom.stretchY") + ":", false),
         new GuiPageButtonList.EditBoxEntry(141, String.format("%2.3f", this.props.stretchY), false, this.floatVerifier),
         new GuiPageButtonList.GuiLabelEntry(410, I18n.format("createWorld.customize.custom.upperLimitScale") + ":", false),
         new GuiPageButtonList.EditBoxEntry(142, String.format("%5.3f", this.props.upperLimitScale), false, this.floatVerifier),
         new GuiPageButtonList.GuiLabelEntry(411, I18n.format("createWorld.customize.custom.lowerLimitScale") + ":", false),
         new GuiPageButtonList.EditBoxEntry(143, String.format("%5.3f", this.props.lowerLimitScale), false, this.floatVerifier),
         new GuiPageButtonList.GuiLabelEntry(412, I18n.format("createWorld.customize.custom.biomeDepthWeight") + ":", false),
         new GuiPageButtonList.EditBoxEntry(144, String.format("%2.3f", this.props.biomeDepthWeight), false, this.floatVerifier),
         new GuiPageButtonList.GuiLabelEntry(413, I18n.format("createWorld.customize.custom.biomeDepthOffset") + ":", false),
         new GuiPageButtonList.EditBoxEntry(145, String.format("%2.3f", this.props.biomeDepthOffset), false, this.floatVerifier),
         new GuiPageButtonList.GuiLabelEntry(414, I18n.format("createWorld.customize.custom.biomeScaleWeight") + ":", false),
         new GuiPageButtonList.EditBoxEntry(146, String.format("%2.3f", this.props.biomeScaleWeight), false, this.floatVerifier),
         new GuiPageButtonList.GuiLabelEntry(415, I18n.format("createWorld.customize.custom.biomeScaleOffset") + ":", false),
         new GuiPageButtonList.EditBoxEntry(147, String.format("%2.3f", this.props.biomeScaleOffset), false, this.floatVerifier)
      };
      this.settingsWidget = new GuiPageButtonList(this.mc, this.width, this.height, 32, this.height - 32, 25, this, guiListEntries, guiListEntries2, guiListEntries3, guiListEntries4);

      for (int i = 0; i < 4; i++) {
         this.pageNames[i] = I18n.format("createWorld.customize.custom.page" + i);
      }

      this.initButtons();
   }

   public String getPropsAsString() {
      return this.props.toString().replace("\n", "");
   }

   public void loadProps(String str) {
      if (str != null && str.length() != 0) {
         this.props = Factory.jsonToFactory(str);
      } else {
         this.props = new Factory();
      }
   }

   @Override
   public void setStringValue(int n, String str) {
      float f = 0.0F;

      try {
         f = Float.parseFloat(str);
      } catch (NumberFormatException e) {
      }

      float f2 = 0.0F;
      switch (n) {
         case 132:
            f2 = this.props.mainNoiseScaleX = MathHelper.clamp_float(f, 1.0F, 5000.0F);
            break;
         case 133:
            f2 = this.props.mainNoiseScaleY = MathHelper.clamp_float(f, 1.0F, 5000.0F);
            break;
         case 134:
            f2 = this.props.mainNoiseScaleZ = MathHelper.clamp_float(f, 1.0F, 5000.0F);
            break;
         case 135:
            f2 = this.props.depthNoiseScaleX = MathHelper.clamp_float(f, 1.0F, 2000.0F);
            break;
         case 136:
            f2 = this.props.depthNoiseScaleZ = MathHelper.clamp_float(f, 1.0F, 2000.0F);
            break;
         case 137:
            f2 = this.props.depthNoiseScaleExponent = MathHelper.clamp_float(f, 0.01F, 20.0F);
            break;
         case 138:
            f2 = this.props.baseSize = MathHelper.clamp_float(f, 1.0F, 25.0F);
            break;
         case 139:
            f2 = this.props.coordinateScale = MathHelper.clamp_float(f, 1.0F, 6000.0F);
            break;
         case 140:
            f2 = this.props.heightScale = MathHelper.clamp_float(f, 1.0F, 6000.0F);
            break;
         case 141:
            f2 = this.props.stretchY = MathHelper.clamp_float(f, 0.01F, 50.0F);
            break;
         case 142:
            f2 = this.props.upperLimitScale = MathHelper.clamp_float(f, 1.0F, 5000.0F);
            break;
         case 143:
            f2 = this.props.lowerLimitScale = MathHelper.clamp_float(f, 1.0F, 5000.0F);
            break;
         case 144:
            f2 = this.props.biomeDepthWeight = MathHelper.clamp_float(f, 1.0F, 20.0F);
            break;
         case 145:
            f2 = this.props.biomeDepthOffset = MathHelper.clamp_float(f, 0.0F, 20.0F);
            break;
         case 146:
            f2 = this.props.biomeScaleWeight = MathHelper.clamp_float(f, 1.0F, 20.0F);
            break;
         case 147:
            f2 = this.props.biomeScaleOffset = MathHelper.clamp_float(f, 0.0F, 20.0F);
      }

      if (f2 != f && f != 0.0F) {
         ((GuiTextField)this.settingsWidget.getWidget(n)).setText(this.getFormattedValue(n, f2));
      }

      ((GuiSlider)this.settingsWidget.getWidget(n - 132 + 100)).setSliderValue(f2, false);
      if (!this.props.equals(this.defaultProps)) {
         this.setModified(true);
      }
   }

   public void setModified(boolean flag) {
      this.modified = flag;
      this.resetToDefaultsButton.enabled = flag;
   }

   @Override
   public String getText(int id, String name, float value) {
      return name + ": " + this.getFormattedValue(id, value);
   }

   public String getFormattedValue(int n, float f) {
      switch (n) {
         case 100:
         case 101:
         case 102:
         case 103:
         case 104:
         case 107:
         case 108:
         case 110:
         case 111:
         case 132:
         case 133:
         case 134:
         case 135:
         case 136:
         case 139:
         case 140:
         case 142:
         case 143:
            return String.format("%5.3f", f);
         case 105:
         case 106:
         case 109:
         case 112:
         case 113:
         case 114:
         case 115:
         case 137:
         case 138:
         case 141:
         case 144:
         case 145:
         case 146:
         case 147:
            return String.format("%2.3f", f);
         case 116:
         case 117:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
         case 123:
         case 124:
         case 125:
         case 126:
         case 127:
         case 128:
         case 129:
         case 130:
         case 131:
         case 148:
         case 149:
         case 150:
         case 151:
         case 152:
         case 153:
         case 154:
         case 155:
         case 156:
         case 157:
         case 158:
         case 159:
         case 160:
         case 161:
         default:
            return String.format("%d", (int)f);
         case 162:
            if (f < 0.0F) {
               return I18n.format("gui.all");
            } else if ((int)f >= BiomeGenBase.hell.biomeID) {
               BiomeGenBase biomeGenBase = BiomeGenBase.getBiomeGenArray()[(int)f + 2];
               return biomeGenBase != null ? biomeGenBase.biomeName : "?";
            } else {
               BiomeGenBase biomeGenBase = BiomeGenBase.getBiomeGenArray()[(int)f];
               return biomeGenBase != null ? biomeGenBase.biomeName : "?";
            }
      }
   }

   @Override
   public void setBooleanValue(int n, boolean flag) {
      switch (n) {
         case 148:
            this.props.useCaves = flag;
            break;
         case 149:
            this.props.useDungeons = flag;
            break;
         case 150:
            this.props.useStrongholds = flag;
            break;
         case 151:
            this.props.useVillages = flag;
            break;
         case 152:
            this.props.useMineShafts = flag;
            break;
         case 153:
            this.props.useTemples = flag;
            break;
         case 154:
            this.props.useRavines = flag;
            break;
         case 155:
            this.props.useWaterLakes = flag;
            break;
         case 156:
            this.props.useLavaLakes = flag;
            break;
         case 161:
            this.props.useLavaOceans = flag;
            break;
         case 210:
            this.props.useMonuments = flag;
      }

      if (!this.props.equals(this.defaultProps)) {
         this.setModified(true);
      }
   }

   @Override
   public void onTick(int id, float value) {
      switch (id) {
         case 100:
            this.props.mainNoiseScaleX = value;
            break;
         case 101:
            this.props.mainNoiseScaleY = value;
            break;
         case 102:
            this.props.mainNoiseScaleZ = value;
            break;
         case 103:
            this.props.depthNoiseScaleX = value;
            break;
         case 104:
            this.props.depthNoiseScaleZ = value;
            break;
         case 105:
            this.props.depthNoiseScaleExponent = value;
            break;
         case 106:
            this.props.baseSize = value;
            break;
         case 107:
            this.props.coordinateScale = value;
            break;
         case 108:
            this.props.heightScale = value;
            break;
         case 109:
            this.props.stretchY = value;
            break;
         case 110:
            this.props.upperLimitScale = value;
            break;
         case 111:
            this.props.lowerLimitScale = value;
            break;
         case 112:
            this.props.biomeDepthWeight = value;
            break;
         case 113:
            this.props.biomeDepthOffset = value;
            break;
         case 114:
            this.props.biomeScaleWeight = value;
            break;
         case 115:
            this.props.biomeScaleOffset = value;
         case 116:
         case 117:
         case 118:
         case 119:
         case 120:
         case 121:
         case 122:
         case 123:
         case 124:
         case 125:
         case 126:
         case 127:
         case 128:
         case 129:
         case 130:
         case 131:
         case 132:
         case 133:
         case 134:
         case 135:
         case 136:
         case 137:
         case 138:
         case 139:
         case 140:
         case 141:
         case 142:
         case 143:
         case 144:
         case 145:
         case 146:
         case 147:
         case 148:
         case 149:
         case 150:
         case 151:
         case 152:
         case 153:
         case 154:
         case 155:
         case 156:
         case 161:
         case 188:
         default:
            break;
         case 157:
            this.props.dungeonChance = (int)value;
            break;
         case 158:
            this.props.waterLakeChance = (int)value;
            break;
         case 159:
            this.props.lavaLakeChance = (int)value;
            break;
         case 160:
            this.props.seaLevel = (int)value;
            break;
         case 162:
            this.props.fixedBiome = (int)value;
            break;
         case 163:
            this.props.biomeSize = (int)value;
            break;
         case 164:
            this.props.riverSize = (int)value;
            break;
         case 165:
            this.props.dirtSize = (int)value;
            break;
         case 166:
            this.props.dirtCount = (int)value;
            break;
         case 167:
            this.props.dirtMinHeight = (int)value;
            break;
         case 168:
            this.props.dirtMaxHeight = (int)value;
            break;
         case 169:
            this.props.gravelSize = (int)value;
            break;
         case 170:
            this.props.gravelCount = (int)value;
            break;
         case 171:
            this.props.gravelMinHeight = (int)value;
            break;
         case 172:
            this.props.gravelMaxHeight = (int)value;
            break;
         case 173:
            this.props.graniteSize = (int)value;
            break;
         case 174:
            this.props.graniteCount = (int)value;
            break;
         case 175:
            this.props.graniteMinHeight = (int)value;
            break;
         case 176:
            this.props.graniteMaxHeight = (int)value;
            break;
         case 177:
            this.props.dioriteSize = (int)value;
            break;
         case 178:
            this.props.dioriteCount = (int)value;
            break;
         case 179:
            this.props.dioriteMinHeight = (int)value;
            break;
         case 180:
            this.props.dioriteMaxHeight = (int)value;
            break;
         case 181:
            this.props.andesiteSize = (int)value;
            break;
         case 182:
            this.props.andesiteCount = (int)value;
            break;
         case 183:
            this.props.andesiteMinHeight = (int)value;
            break;
         case 184:
            this.props.andesiteMaxHeight = (int)value;
            break;
         case 185:
            this.props.coalSize = (int)value;
            break;
         case 186:
            this.props.coalCount = (int)value;
            break;
         case 187:
            this.props.coalMinHeight = (int)value;
            break;
         case 189:
            this.props.coalMaxHeight = (int)value;
            break;
         case 190:
            this.props.ironSize = (int)value;
            break;
         case 191:
            this.props.ironCount = (int)value;
            break;
         case 192:
            this.props.ironMinHeight = (int)value;
            break;
         case 193:
            this.props.ironMaxHeight = (int)value;
            break;
         case 194:
            this.props.goldSize = (int)value;
            break;
         case 195:
            this.props.goldCount = (int)value;
            break;
         case 196:
            this.props.goldMinHeight = (int)value;
            break;
         case 197:
            this.props.goldMaxHeight = (int)value;
            break;
         case 198:
            this.props.redstoneSize = (int)value;
            break;
         case 199:
            this.props.redstoneCount = (int)value;
            break;
         case 200:
            this.props.redstoneMinHeight = (int)value;
            break;
         case 201:
            this.props.redstoneMaxHeight = (int)value;
            break;
         case 202:
            this.props.diamondSize = (int)value;
            break;
         case 203:
            this.props.diamondCount = (int)value;
            break;
         case 204:
            this.props.diamondMinHeight = (int)value;
            break;
         case 205:
            this.props.diamondMaxHeight = (int)value;
            break;
         case 206:
            this.props.lapisSize = (int)value;
            break;
         case 207:
            this.props.lapisCount = (int)value;
            break;
         case 208:
            this.props.lapisCenterHeight = (int)value;
            break;
         case 209:
            this.props.lapisSpread = (int)value;
      }

      if (id >= 100 && id < 116) {
         Gui widget = this.settingsWidget.getWidget(id - 100 + 132);
         if (widget != null) {
            ((GuiTextField)widget).setText(this.getFormattedValue(id, value));
         }
      }

      if (!this.props.equals(this.defaultProps)) {
         this.setModified(true);
      }
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         switch (button.id) {
            case 300:
               this.parent.chunkProviderSettingsJson = this.props.toString();
               this.mc.displayGuiScreen(this.parent);
               break;
            case 301:
               for (int i = 0; i < this.settingsWidget.getSize(); i++) {
                  GuiPageButtonList.GuiEntry listEntry = this.settingsWidget.getListEntry(i);
                  Gui first = listEntry.getFirst();
                  if (first instanceof GuiButton) {
                     GuiButton guiButton = (GuiButton)first;
                     if (guiButton instanceof GuiSlider) {
                        float f = ((GuiSlider)guiButton).getProgress() * (0.75F + this.random.nextFloat() * 0.5F) + (this.random.nextFloat() * 0.1F - 0.05F);
                        ((GuiSlider)guiButton).setSliderProgress(MathHelper.clamp_float(f, 0.0F, 1.0F));
                     } else if (guiButton instanceof GuiListButton) {
                        ((GuiListButton)guiButton).setValue(this.random.nextBoolean());
                     }
                  }

                  Gui second = listEntry.getSecond();
                  if (second instanceof GuiButton) {
                     GuiButton guiButton = (GuiButton)second;
                     if (guiButton instanceof GuiSlider) {
                        float f = ((GuiSlider)guiButton).getProgress() * (0.75F + this.random.nextFloat() * 0.5F) + (this.random.nextFloat() * 0.1F - 0.05F);
                        ((GuiSlider)guiButton).setSliderProgress(MathHelper.clamp_float(f, 0.0F, 1.0F));
                     } else if (guiButton instanceof GuiListButton) {
                        ((GuiListButton)guiButton).setValue(this.random.nextBoolean());
                     }
                  }
               }

               return;
            case 302:
               this.settingsWidget.previousPage();
               this.initButtons();
               break;
            case 303:
               this.settingsWidget.nextPage();
               this.initButtons();
               break;
            case 304:
               if (this.modified) {
                  this.resetToDefaults(304);
               }
               break;
            case 305:
               this.mc.displayGuiScreen(new GuiScreenCustomizePresets(this));
               break;
            case 306:
               this.checkConfirmation();
               break;
            case 307:
               this.buttonLastPushed = 0;
               this.checkConfirmation();
         }
      }
   }

   public void resetToDefaults() {
      this.props.resetToDefault();
      this.initPages();
      this.setModified(false);
   }

   public void resetToDefaults(int n) {
      this.buttonLastPushed = n;
      this.setConfirmationButtons(true);
   }

   public void checkConfirmation() throws IOException {
      switch (this.buttonLastPushed) {
         case 300:
            this.actionPerformed((GuiListButton)this.settingsWidget.getWidget(300));
            break;
         case 304:
            this.resetToDefaults();
      }

      this.buttonLastPushed = 0;
      this.cancelled = true;
      this.setConfirmationButtons(false);
   }

   public void setConfirmationButtons(boolean flag) {
      this.yesButton.visible = flag;
      this.noButton.visible = flag;
      this.randomizeButton.enabled = !flag;
      this.doneButton.enabled = !flag;
      this.previousButton.enabled = !flag;
      this.nextButton.enabled = !flag;
      this.resetToDefaultsButton.enabled = this.modified && !flag;
      this.presetsButton.enabled = !flag;
      this.settingsWidget.setActive(!flag);
   }

   public void initButtons() {
      this.previousButton.enabled = this.settingsWidget.getCurrentPageId() != 0;
      this.nextButton.enabled = this.settingsWidget.getCurrentPageId() != this.settingsWidget.getMaxPages() - 1;
      this.page = I18n.format("book.pageIndicator", this.settingsWidget.getCurrentPageId() + 1, this.settingsWidget.getMaxPages());
      this.subtitle = this.pageNames[this.settingsWidget.getCurrentPageId()];
      this.randomizeButton.enabled = this.settingsWidget.getCurrentPageId() != this.settingsWidget.getMaxPages() - 1;
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      super.keyTyped(typedChar, keyCode);
      if (this.buttonLastPushed == 0) {
         switch (keyCode) {
            case 200:
               this.updateValue(1.0F);
               break;
            case 208:
               this.updateValue(-1.0F);
               break;
            default:
               this.settingsWidget.updateText(typedChar, keyCode);
         }
      }
   }

   public void updateValue(float f) {
      Gui currentWidget = this.settingsWidget.getCurrentWidget();
      if (currentWidget instanceof GuiTextField) {
         float f2 = f;
         if (GuiScreen.isShiftKeyDown()) {
            f2 = f * 0.1F;
            if (GuiScreen.isCtrlKeyDown()) {
               f2 *= 0.1F;
            }
         } else if (GuiScreen.isCtrlKeyDown()) {
            f2 = f * 10.0F;
            if (GuiScreen.isAltKeyDown()) {
               f2 *= 10.0F;
            }
         }

         GuiTextField guiTextField = (GuiTextField)currentWidget;
         Float tryParse = Floats.tryParse(guiTextField.getText());
         if (tryParse != null) {
            tryParse = tryParse + f2;
            int id = guiTextField.getId();
            String formattedValue = this.getFormattedValue(guiTextField.getId(), tryParse);
            guiTextField.setText(formattedValue);
            this.setStringValue(id, formattedValue);
         }
      }
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      if (this.buttonLastPushed == 0 && !this.cancelled) {
         this.settingsWidget.mouseClicked(mouseX, mouseY, mouseButton);
      }
   }

   @Override
   public void mouseReleased(int mouseX, int mouseY, int state) {
      super.mouseReleased(mouseX, mouseY, state);
      if (this.cancelled) {
         this.cancelled = false;
      } else if (this.buttonLastPushed == 0) {
         this.settingsWidget.mouseReleased(mouseX, mouseY, state);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.settingsWidget.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 2, 16777215);
      this.drawCenteredString(this.fontRendererObj, this.page, this.width / 2, 12, 16777215);
      this.drawCenteredString(this.fontRendererObj, this.subtitle, this.width / 2, 22, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
      if (this.buttonLastPushed != 0) {
         drawRect(0, 0, this.width, this.height, Integer.MIN_VALUE);
         this.drawHorizontalLine(this.width / 2 - 91, this.width / 2 + 90, 99, -2039584);
         this.drawHorizontalLine(this.width / 2 - 91, this.width / 2 + 90, 185, -6250336);
         this.drawVerticalLine(this.width / 2 - 91, 99, 185, -2039584);
         this.drawVerticalLine(this.width / 2 + 90, 99, 185, -6250336);
         float f = 85.0F;
         float f2 = 180.0F;
         GlStateManager.disableLighting();
         GlStateManager.disableFog();
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         this.mc.getTextureManager().bindTexture(optionsBackground);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         float f3 = 32.0F;
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         worldRenderer.pos(this.width / 2 - 90, 185.0, 0.0).tex(0.0, 2.65625).color(64, 64, 64, 64).endVertex();
         worldRenderer.pos(this.width / 2 + 90, 185.0, 0.0).tex(5.625, 2.65625).color(64, 64, 64, 64).endVertex();
         worldRenderer.pos(this.width / 2 + 90, 100.0, 0.0).tex(5.625, 0.0).color(64, 64, 64, 64).endVertex();
         worldRenderer.pos(this.width / 2 - 90, 100.0, 0.0).tex(0.0, 0.0).color(64, 64, 64, 64).endVertex();
         instance.draw();
         this.drawCenteredString(this.fontRendererObj, I18n.format("createWorld.customize.custom.confirmTitle"), this.width / 2, 105, 16777215);
         this.drawCenteredString(this.fontRendererObj, I18n.format("createWorld.customize.custom.confirm1"), this.width / 2, 125, 16777215);
         this.drawCenteredString(this.fontRendererObj, I18n.format("createWorld.customize.custom.confirm2"), this.width / 2, 135, 16777215);
         this.yesButton.drawButton(this.mc, mouseX, mouseY);
         this.noButton.drawButton(this.mc, mouseX, mouseY);
      }
   }
}
