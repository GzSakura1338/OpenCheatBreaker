package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.BlockIntEvent;
import com.cheatbreaker.client.event.IntEvent3;
import com.cheatbreaker.client.event.SimpleEvent31;
import com.cheatbreaker.client.event.SimpleEvent5;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.GlStateManager;
import org.lwjgl.opengl.GL11;

public class PackTweaksMod extends AbstractMod {
    public float floatValue = 1.0F;
    public Setting glassOptions;
    public Setting clearGlass;
    public Setting stringOptions;
    public Setting colorPlacedString;
    public Setting rodDashedLines;
    public Setting rodLineWidth;
    public Setting rodLineDistance;
    public Setting placedStringColor;
    public Setting rodStringColor;
    public Setting fireOptions;
    public Setting firstPersonFire;
    public Setting fireOpacity;
    public Setting fireHeight;
    public Setting fireOnEntities;
    public Setting vignetteOptions;
    public Setting vignetteType;
    public Setting vignetteOpacity;
    public Setting vignetteMinimumOpacity;
    public Setting vignetteMaximumOpacity;
    public Setting vignetteOpacityMultiplier;
    public Setting lowHealthOptions;
    public Setting lowHealthOverlay;
    public Setting lowHealthStart;
    public Setting lowHealthOpacity;
    public Setting pumpkinOverlayOptions;
    public Setting showPumpkinOverlay;
    public Setting pumpkinOverlayOpacity;
    public Setting inventoryOptions;
    public Setting transparentInventory;
    public Setting showInventoryTooltips;
    public Setting droppedItemOptions;
    public Setting facePlayer;
    public Setting rotateTowardsCamera;

    public PackTweaksMod() throws java.io.IOException {
        super("Pack Tweaks");
        this.setDescription("Change how specific textures appear.");
        this.setCategories(ModCategory.MISC);
        this.setApplyDefaultColorOnEnable(false);
        this.glassOptions = new SettingGroup(this, "Glass Options").changeValue(true);
        this.clearGlass = new Setting(this, "Clear Glass")
            .setDescription(
                "Removes the glass texture.\n\n§bOFF:§r No glass textures are removed.\n§bREGULAR:§r Only normal glass texture is removed.\n§bALL:§r Normal and stained glass textures are removed."
            )
            .changeValue("OFF")
            .setStringValues("OFF", "REGULAR", "ALL")
            .setOnUpdate(obj -> this.mc.renderGlobal.loadRenderers())
            .withParents(this.glassOptions);
        this.stringOptions = new SettingGroup(this, "String Options").changeValue(true);
        this.colorPlacedString = new Setting(this, "Color Placed String")
            .setDescription("Show a solid color line instead of the texture pack's string texture.")
            .changeValue(false)
            .setOnUpdate(obj -> this.mc.renderGlobal.loadRenderers())
            .withParents(this.stringOptions);
        this.rodDashedLines = new Setting(this, "Rod Dashed Lines").changeValue(false).setUnit("px");
        this.rodLineWidth = new Setting(this, "Rod Line Width").changeValue(1).setRange(1, 10).setUnit("px");
        this.rodLineDistance = new Setting(this, "Rod Line Distance").changeValue(10).setRange(0, 100).setUnit("px").withParents(this.rodDashedLines);
        this.placedStringColor = new Setting(this, "Placed String Color")
            .changeValue(-65536)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .withHierarchyParents(this.colorPlacedString);
        this.rodStringColor = new Setting(this, "Rod String Color").changeValue(-16777216).setRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
        this.fireOptions = new SettingGroup(this, "Fire Options").changeValue(true);
        this.firstPersonFire = new Setting(this, "First Person Fire").changeValue(true).withParents(this.fireOptions);
        this.fireOpacity = new Setting(this, "Fire Opacity")
            .changeValue(90.0F)
            .setRange(0.0F, 100.0F)
            .setUnit("%")
            .setIconNames("circle-hollow-64", "circle-64")
            .setIconScales(7.5F, 7.5F)
            .setIconXOffsets(-0.25F, -2.75F)
            .setIconYOffsets(-0.75F, -0.75F)
            .withHierarchyParents(this.firstPersonFire);
        this.fireHeight = new Setting(this, "Fire Height").changeValue(1.0F).setRange(0.0F, 2.0F).withHierarchyParents(this.firstPersonFire);
        this.fireOnEntities = new Setting(this, "Fire on Entities").changeValue(true).withParents(this.fireOptions);
        this.vignetteOptions = new SettingGroup(this, "Vignette Options").changeValue(true);
        this.vignetteType = new Setting(this, "Vignette Type")
            .changeValue("Vanilla")
            .setStringValues("Vanilla", "Static", "Amplified")
            .withParents(this.vignetteOptions);
        this.vignetteOpacity = new Setting(this, "Vignette Opacity")
            .changeValue(75.0F)
            .setRange(0.0F, 100.0F)
            .setUnit("%")
            .setIconNames("circle-hollow-64", "circle-64")
            .setIconScales(7.5F, 7.5F)
            .setIconXOffsets(-0.25F, -2.75F)
            .setIconYOffsets(-0.75F, -0.75F)
            .setCondition(() -> this.vignetteType.getValue().equals("Static"))
            .withParents(this.vignetteOptions);
        this.vignetteMinimumOpacity = new Setting(this, "Vignette Minimum Opacity")
            .changeValue(0.0F)
            .setRange(0.0F, 100.0F)
            .setUnit("%")
            .setCondition(() -> this.vignetteType.getValue().equals("Amplified"))
            .withParents(this.vignetteOptions);
        this.vignetteMaximumOpacity = new Setting(this, "Vignette Maximum Opacity")
            .changeValue(100.0F)
            .setRange(0.0F, 100.0F)
            .setUnit("%")
            .setIconNames("circle-hollow-64", "circle-64")
            .setIconScales(7.5F, 7.5F)
            .setIconXOffsets(-0.25F, -2.75F)
            .setIconYOffsets(-0.75F, -0.75F)
            .setCondition(() -> this.vignetteType.getValue().equals("Amplified"))
            .withParents(this.vignetteOptions);
        this.vignetteOpacityMultiplier = new Setting(this, "Vignette Opacity Multiplier")
            .changeValue(1.0F)
            .setRange(0.1F, 10.0F)
            .setUnit("x")
            .setCondition(() -> this.vignetteType.getValue().equals("Amplified"))
            .withParents(this.vignetteOptions);
        this.lowHealthOptions = new SettingGroup(this, "Low Health Options").changeValue(true);
        this.lowHealthOverlay = new Setting(this, "Low Health Overlay").changeValue(false).withParents(this.lowHealthOptions);
        this.lowHealthStart = new Setting(this, "Low Health Start")
            .changeValue(3.5F)
            .setRange(1.0F, 10.0F)
            .setUnit(" hearts")
            .withHierarchyParents(this.lowHealthOverlay);
        this.lowHealthOpacity = new Setting(this, "Low Health Opacity")
            .changeValue(75.0F)
            .setRange(0.0F, 100.0F)
            .setUnit("%")
            .setIconNames("circle-hollow-64", "circle-64")
            .setIconScales(7.5F, 7.5F)
            .setIconXOffsets(-0.25F, -2.75F)
            .setIconYOffsets(-0.75F, -0.75F)
            .withHierarchyParents(this.lowHealthOverlay);
        this.pumpkinOverlayOptions = new SettingGroup(this, "Pumpkin Overlay Options").changeValue(true);
        this.showPumpkinOverlay = new Setting(this, "Show Pumpkin Overlay").changeValue(true).withParents(this.pumpkinOverlayOptions);
        this.pumpkinOverlayOpacity = new Setting(this, "Pumpkin Overlay Opacity")
            .changeValue(100.0F)
            .setRange(0.0F, 100.0F)
            .setUnit("%")
            .setIconNames("circle-hollow-64", "circle-64")
            .setIconScales(7.5F, 7.5F)
            .setIconXOffsets(-0.25F, -2.75F)
            .setIconYOffsets(-0.75F, -0.75F)
            .withHierarchyParents(this.showPumpkinOverlay);
        this.inventoryOptions = new SettingGroup(this, "Inventory Options").changeValue(true);
        this.transparentInventory = new Setting(this, "Transparent Inventory").changeValue(false).withParents(this.inventoryOptions);
        this.showInventoryTooltips = new Setting(this, "Show Inventory Tooltips").changeValue(true).withParents(this.inventoryOptions);
        this.droppedItemOptions = new SettingGroup(this, "Dropped Item Options").changeValue(true);
        this.facePlayer = new Setting(this, "Face player").changeValue(false).setCondition(Config::isDroppedItemsFancy).withParents(this.droppedItemOptions);
        this.rotateTowardsCamera = new Setting(this, "Rotate towards camera")
            .changeValue(false)
            .setCondition(() -> !Config.isDroppedItemsFancy() || this.facePlayer.getBooleanValue())
            .withParents(this.droppedItemOptions);
        this.setIcon(new ResourceLocation("client/icons/mods/packtweaks.png"), 32, 32);
        this.addEventHandler(BlockIntEvent.class, this::onBlockIntEvent);
        this.addEventHandler(SimpleEvent5.class, simpleEvent5 -> {
            if (!(Boolean)this.firstPersonFire.getValue()) {
                simpleEvent5.setCancelled(true);
            }
        });
        this.addEventHandler(IntEvent3.class, intEvent3 -> {
            intEvent3.setIntValue(this.rodStringColor.getColorValue());
            GL11.glLineWidth(this.rodLineWidth.getIntValue());
            if (this.rodDashedLines.getBooleanValue()) {
                GL11.glPushAttrib(8192);
                GL11.glLineStipple(this.rodLineDistance.getIntValue(), (short)-21846);
                GL11.glEnable(2852);
                GL11.glBegin(1);
                GL11.glEnd();
            }
        });
        this.addEventHandler(SimpleEvent31.class, simpleEvent31 -> {
            if (this.rodDashedLines.getBooleanValue()) {
                GL11.glPopAttrib();
            }
        });
        this.setEnabledByDefault(true);
    }

    public void render(float f, int n, int n2) {
        PackTweaksMod packTweaksMod = CheatBreaker.getInstance().getModManager().packTweaksMod;
        f = this.mc.thePlayer.getHealth() / 2.0F <= packTweaksMod.lowHealthStart.getFloatValue() ? f : 0.0F;
        f = MathHelper.clamp_float(f, 0.0F, 1.0F);
        float f2 = 0.0F;
        float f3 = 1.0F;
        if (f < f2) {
            f = f2;
        }

        if (f > f3) {
            f = f3;
        }

        this.floatValue = (float)(this.floatValue + (f - this.floatValue) * 0.01);
        GL11.glDisable(2929);
        GlStateManager.depthMask(false);
        OpenGlHelper.glBlendFunc(0, 769, 1, 0);
        GlStateManager.color(0.0F, this.floatValue, this.floatValue, 1.0F);
        this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/misc/vignette.png"));
        Tessellator instance = Tessellator.instance;
        instance.startDrawingQuads();
        instance.addVertexWithUV(0.0, n2, -90.0, 0.0, 1.0);
        instance.addVertexWithUV(n, n2, -90.0, 1.0, 1.0);
        instance.addVertexWithUV(n, 0.0, -90.0, 1.0, 0.0);
        instance.addVertexWithUV(0.0, 0.0, -90.0, 0.0, 0.0);
        instance.draw();
        GlStateManager.depthMask(true);
        GL11.glEnable(2929);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
    }

    public void onBlockIntEvent(BlockIntEvent blockIntEvent) {
        String stringValue = this.clearGlass.getStringValue();
        if (!"OFF".equals(stringValue)) {
            boolean flag = blockIntEvent.getBlock() instanceof BlockGlass;
            boolean flag2 = blockIntEvent.getBlock() instanceof BlockStainedGlass;
            if (flag || flag2 && "ALL".equals(stringValue)) {
                blockIntEvent.setCancelled(true);
            }
        }
    }
}
