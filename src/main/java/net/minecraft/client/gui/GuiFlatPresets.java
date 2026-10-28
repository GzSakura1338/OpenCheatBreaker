package net.minecraft.client.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiFlatPresets extends GuiScreen {
    public static RenderItem renderItem = new RenderItem();
    public static List PRESETS = new ArrayList();
    public GuiCreateFlatWorld parent;
    public String title;
    public String shareText;
    public String listText;
    public GuiFlatPresets.ListSlot listSlot;
    public GuiButton selectButton;
    public GuiTextField customPresetField;

    public GuiFlatPresets(GuiCreateFlatWorld guiCreateFlatWorld) {
        this.parent = guiCreateFlatWorld;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        Keyboard.enableRepeatEvents(true);
        this.title = I18n.format("createWorld.customize.presets.title");
        this.shareText = I18n.format("createWorld.customize.presets.share");
        this.listText = I18n.format("createWorld.customize.presets.list");
        this.customPresetField = new GuiTextField(this.fontRendererObj, 50, 40, this.width - 100, 20);
        this.listSlot = new GuiFlatPresets.ListSlot();
        this.customPresetField.setMaxStringLength(1230);
        this.customPresetField.setText(this.parent.getConfigString());
        this.buttonList
            .add(this.selectButton = new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("createWorld.customize.presets.select")));
        this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")));
        this.updateSelectButton();
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws java.io.IOException {
        this.customPresetField.mouseClicked(mouseX, mouseY, mouseButton);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws java.io.IOException {
        if (!this.customPresetField.textboxKeyTyped(typedChar, keyCode)) {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public void actionPerformed(GuiButton button) throws java.io.IOException {
        if (button.id == 0 && this.isEntrySelected()) {
            this.parent.setConfigHelper(this.customPresetField.getText());
            this.mc.displayGuiScreen(this.parent);
        } else if (button.id == 1) {
            this.mc.displayGuiScreen(this.parent);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        this.drawDefaultBackground();
        this.listSlot.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 8, 16777215);
        this.drawString(this.fontRendererObj, this.shareText, 50, 30, 10526880);
        this.drawString(this.fontRendererObj, this.listText, 50, 70, 10526880);
        this.customPresetField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void updateScreen() throws java.io.IOException {
        this.customPresetField.updateCursorCounter();
        super.updateScreen();
    }

    public void updateSelectButton() {
        boolean isEntrySelected = this.isEntrySelected();
        this.selectButton.enabled = isEntrySelected;
    }

    public boolean isEntrySelected() {
        return this.listSlot.intValue > -1 && this.listSlot.intValue < PRESETS.size() || this.customPresetField.getText().length() > 1;
    }

    public static void addRedstonePreset(String str, Item item, BiomeGenBase biomeGenBase, FlatLayerInfo... flatLayerInfos) {
        addPreset(str, item, biomeGenBase, null, flatLayerInfos);
    }

    public static void addPreset(String str, Item item, BiomeGenBase biomeGenBase, List list, FlatLayerInfo... flatLayerInfos) {
        FlatGeneratorInfo flatGeneratorInfo = new FlatGeneratorInfo();

        for (int i = flatLayerInfos.length - 1; i >= 0; i--) {
            flatGeneratorInfo.getFlatLayers().add(flatLayerInfos[i]);
        }

        flatGeneratorInfo.setBiome(biomeGenBase.biomeID);
        flatGeneratorInfo.updateLayerLevel();
        if (list != null) {
            for (String str2 : (Iterable<String>)list) {
                flatGeneratorInfo.getWorldFeatures().put(str2, new HashMap());
            }
        }

        PRESETS.add(new GuiFlatPresets.LayerItem(item, str, flatGeneratorInfo.toString()));
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
            Item.getItemFromBlock(Blocks.flowing_water),
            BiomeGenBase.plains,
            Arrays.asList("village", "biome_1"),
            new FlatLayerInfo(90, Blocks.water),
            new FlatLayerInfo(5, Blocks.sand),
            new FlatLayerInfo(5, Blocks.dirt),
            new FlatLayerInfo(5, Blocks.stone),
            new FlatLayerInfo(1, Blocks.bedrock)
        );
        addPreset(
            "Overworld",
            Item.getItemFromBlock(Blocks.tallgrass),
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

    public static class LayerItem {
        public Item item;
        public String text;
        public String text2;

        public LayerItem(Item item, String str, String str2) {
            this.item = item;
            this.text = str;
            this.text2 = str2;
        }
    }

    public class ListSlot extends GuiSlot {
        public int intValue = -1;

        public ListSlot() {
            super(GuiFlatPresets.this.mc, GuiFlatPresets.this.width, GuiFlatPresets.this.height, 80, GuiFlatPresets.this.height - 37, 24);
        }

        public void render(int n, int n2, Item item) {
            this.process2(n + 1, n2 + 1);
            GL11.glEnable(32826);
            RenderHelper.enableGUIStandardItemLighting();
            GuiFlatPresets.renderItem
                .renderItemIntoGUI(GuiFlatPresets.this.fontRendererObj, GuiFlatPresets.this.mc.getTextureManager(), new ItemStack(item, 1, 0), n + 2, n2 + 2);
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(32826);
        }

        public void process2(int n, int n2) {
            this.process(n, n2, 0, 0);
        }

        public void process(int n, int n2, int n3, int n4) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GuiFlatPresets.this.mc.getTextureManager().bindTexture(Gui.statIcons);
            float f = 0.0078125F;
            float f2 = 0.0078125F;
            boolean flag = true;
            boolean flag2 = true;
            Tessellator instance = Tessellator.instance;
            instance.startDrawingQuads();
            instance.addVertexWithUV(n + 0, n2 + 18, Gui.floatValue, (n3 + 0) * 0.0078125F, (n4 + 18) * 0.0078125F);
            instance.addVertexWithUV(n + 18, n2 + 18, Gui.floatValue, (n3 + 18) * 0.0078125F, (n4 + 18) * 0.0078125F);
            instance.addVertexWithUV(n + 18, n2 + 0, Gui.floatValue, (n3 + 18) * 0.0078125F, (n4 + 0) * 0.0078125F);
            instance.addVertexWithUV(n + 0, n2 + 0, Gui.floatValue, (n3 + 0) * 0.0078125F, (n4 + 0) * 0.0078125F);
            instance.draw();
        }

        @Override
        public int getSize() {
            return GuiFlatPresets.PRESETS.size();
        }

        @Override
        public void elementClicked(int n, boolean flag, int n2, int n3) {
            this.intValue = n;
            GuiFlatPresets.this.updateSelectButton();
            GuiFlatPresets.this.customPresetField.setText(((GuiFlatPresets.LayerItem)GuiFlatPresets.PRESETS.get(GuiFlatPresets.this.listSlot.intValue)).text2);
        }

        @Override
        public boolean isSelected(int n) {
            return n == this.intValue;
        }

        @Override
        public void drawBackground() {
        }

        @Override
        public void drawSlot(int n, int n2, int n3, int n4, Tessellator tessellator, int n5, int n6) {
            GuiFlatPresets.LayerItem layerItem = (GuiFlatPresets.LayerItem)GuiFlatPresets.PRESETS.get(n);
            this.render(n2, n3, layerItem.item);
            GuiFlatPresets.this.fontRendererObj.drawString(layerItem.text, n2 + 18 + 5, n3 + 6, 16777215);
        }
    }
}
