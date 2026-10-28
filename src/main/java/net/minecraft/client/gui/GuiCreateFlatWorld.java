package net.minecraft.client.gui;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.gen.FlatGeneratorInfo;
import net.minecraft.world.gen.FlatLayerInfo;
import org.lwjgl.opengl.GL11;

public class GuiCreateFlatWorld extends GuiScreen {
    public static RenderItem renderItem = new RenderItem();
    public FlatGeneratorInfo theFlatGeneratorInfo = FlatGeneratorInfo.getDefaultFlatGenerator();
    public GuiCreateWorld createWorldGui;
    public String title;
    public String tileText;
    public String heightText;
    public GuiCreateFlatWorld.Details createFlatWorldListSlotGui;
    public GuiButton addLayer;
    public GuiButton editLayer;
    public GuiButton removeLayer;

    public GuiCreateFlatWorld(GuiCreateWorld guiCreateWorld, String str) {
        this.createWorldGui = guiCreateWorld;
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
        this.title = I18n.format("createWorld.customize.flat.title");
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
    public void actionPerformed(GuiButton button) throws java.io.IOException {
        int n = this.theFlatGeneratorInfo.getFlatLayers().size() - this.createFlatWorldListSlotGui.intValue - 1;
        if (button.id == 1) {
            this.mc.displayGuiScreen(this.createWorldGui);
        } else if (button.id == 0) {
            this.createWorldGui.generatorOptions = this.getConfigString();
            this.mc.displayGuiScreen(this.createWorldGui);
        } else if (button.id == 5) {
            this.mc.displayGuiScreen(new GuiFlatPresets(this));
        } else if (button.id == 4 && this.hasLayerSelected()) {
            this.theFlatGeneratorInfo.getFlatLayers().remove(n);
            this.createFlatWorldListSlotGui.intValue = Math.min(this.createFlatWorldListSlotGui.intValue, this.theFlatGeneratorInfo.getFlatLayers().size() - 1);
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
        return this.createFlatWorldListSlotGui.intValue > -1 && this.createFlatWorldListSlotGui.intValue < this.theFlatGeneratorInfo.getFlatLayers().size();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        this.drawDefaultBackground();
        this.createFlatWorldListSlotGui.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 8, 16777215);
        int n = this.width / 2 - 92 - 16;
        this.drawString(this.fontRendererObj, this.tileText, n, 32, 16777215);
        this.drawString(this.fontRendererObj, this.heightText, n + 2 + 213 - this.fontRendererObj.getStringWidth(this.heightText), 32, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public class Details extends GuiSlot {
        public int intValue = -1;

        public Details() {
            super(GuiCreateFlatWorld.this.mc, GuiCreateFlatWorld.this.width, GuiCreateFlatWorld.this.height, 43, GuiCreateFlatWorld.this.height - 60, 24);
        }

        public void render(int n, int n2, ItemStack itemStack) {
            this.process3(n + 1, n2 + 1);
            GL11.glEnable(32826);
            if (itemStack != null) {
                RenderHelper.enableGUIStandardItemLighting();
                GuiCreateFlatWorld.renderItem
                    .renderItemIntoGUI(GuiCreateFlatWorld.this.fontRendererObj, GuiCreateFlatWorld.this.mc.getTextureManager(), itemStack, n + 2, n2 + 2);
                RenderHelper.disableStandardItemLighting();
            }

            GL11.glDisable(32826);
        }

        public void process3(int n, int n2) {
            this.process(n, n2, 0, 0);
        }

        public void process(int n, int n2, int n3, int n4) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GuiCreateFlatWorld.this.mc.getTextureManager().bindTexture(Gui.statIcons);
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
            return GuiCreateFlatWorld.this.theFlatGeneratorInfo.getFlatLayers().size();
        }

        @Override
        public void elementClicked(int n, boolean flag, int n2, int n3) {
            this.intValue = n;
            GuiCreateFlatWorld.this.updateButtons();
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
            FlatLayerInfo flatLayerInfo = (FlatLayerInfo)GuiCreateFlatWorld.this.theFlatGeneratorInfo
                .getFlatLayers()
                .get(GuiCreateFlatWorld.this.theFlatGeneratorInfo.getFlatLayers().size() - n - 1);
            Item itemFromBlock = Item.getItemFromBlock(flatLayerInfo.getBlock());
            ItemStack itemStack = flatLayerInfo.getBlock() == Blocks.air ? null : new ItemStack(itemFromBlock, 1, flatLayerInfo.getFillBlockMeta());
            String str = itemStack != null && itemFromBlock != null ? itemFromBlock.getItemStackDisplayName(itemStack) : "Air";
            this.render(n2, n3, itemStack);
            GuiCreateFlatWorld.this.fontRendererObj.drawString(str, n2 + 18 + 5, n3 + 3, 16777215);
            String str2;
            if (n == 0) {
                str2 = I18n.format("createWorld.customize.flat.layer.top", flatLayerInfo.getLayerCount());
            } else if (n == GuiCreateFlatWorld.this.theFlatGeneratorInfo.getFlatLayers().size() - 1) {
                str2 = I18n.format("createWorld.customize.flat.layer.bottom", flatLayerInfo.getLayerCount());
            } else {
                str2 = I18n.format("createWorld.customize.flat.layer", flatLayerInfo.getLayerCount());
            }

            GuiCreateFlatWorld.this.fontRendererObj
                .drawString(str2, n2 + 2 + 213 - GuiCreateFlatWorld.this.fontRendererObj.getStringWidth(str2), n3 + 3, 16777215);
        }

        @Override
        public int getScrollBarX() {
            return this.width - 70;
        }
    }
}
