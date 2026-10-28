package shadersmod.client;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.optifine.Config;
import net.optifine.Lang;
import net.optifine.TooltipManager;
import net.optifine.TooltipProviderEnumShaderOptions;
import org.lwjgl.Sys;

public class GuiShaders extends GuiScreen {
    public String screenTitle = "Shaders";
    public TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderEnumShaderOptions());
    public int updateTimer = -1;
    public boolean saved = false;
    public GuiSlotShaders shaderList;
    public GuiScreen parentGui;
    public static float[] QUALITY_MULTIPLIERS = new float[]{0.5F, 0.70710677F, 1.0F, 1.4142135F, 2.0F};
    public static String[] QUALITY_MULTIPLIER_NAMES = new String[]{"0.5x", "0.7x", "1x", "1.5x", "2x"};
    public static float[] HAND_DEPTH_VALUES = new float[]{0.0625F, 0.125F, 0.25F};
    public static String[] HAND_DEPTH_NAMES = new String[]{"0.5x", "1x", "2x"};
    public static final int EnumOS_UNKNOWN = 0;
    public static final int EnumOS_WINDOWS = 1;
    public static final int EnumOS_OSX = 2;
    public static final int EnumOS_SOLARIS = 3;
    public static final int EnumOS_LINUX = 4;

    public GuiShaders(GuiScreen guiScreen, GameSettings gameSettings) {
        this.parentGui = guiScreen;
    }

    @Override
    public void initGui() {
        this.screenTitle = I18n.format("of.options.shadersTitle");
        if (Shaders.shadersConfig == null) {
            Shaders.loadConfig();
        }

        byte b = 120;
        byte b2 = 20;
        int n = this.width - b - 10;
        byte b3 = 30;
        byte b4 = 20;
        int n2 = this.width - b - 20;
        this.shaderList = new GuiSlotShaders(this, n2, this.height, b3, this.height - 50, 16);
        this.shaderList.registerScrollButtons(7, 8);
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.ANTIALIASING, n, 0 * b4 + b3, b, b2));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.NORMAL_MAP, n, 1 * b4 + b3, b, b2));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.SPECULAR_MAP, n, 2 * b4 + b3, b, b2));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.RENDER_RES_MUL, n, 3 * b4 + b3, b, b2));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.SHADOW_RES_MUL, n, 4 * b4 + b3, b, b2));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.HAND_DEPTH_MUL, n, 5 * b4 + b3, b, b2));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.OLD_HAND_LIGHT, n, 6 * b4 + b3, b, b2));
        this.buttonList.add(new GuiButtonEnumShaderOption(EnumShaderOption.OLD_LIGHTING, n, 7 * b4 + b3, b, b2));
        int min = Math.min(150, n2 / 2 - 10);
        this.buttonList.add(new GuiButton(201, n2 / 4 - min / 2, this.height - 25, min, b2, Lang.get("of.options.shaders.shadersFolder")));
        this.buttonList.add(new GuiButton(202, n2 / 4 * 3 - min / 2, this.height - 25, min, b2, I18n.format("gui.done")));
        this.buttonList.add(new GuiButton(203, n, this.height - 25, b, b2, Lang.get("of.options.shaders.shaderOptions")));
        this.updateButtons();
    }

    public void updateButtons() {
        boolean isShaders = Config.isShaders();

        for (GuiButton guiButton : this.buttonList) {
            if (guiButton.id != 201 && guiButton.id != 202 && guiButton.id != EnumShaderOption.ANTIALIASING.ordinal()) {
                guiButton.enabled = isShaders;
            }
        }
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
    }

    @Override
    public void actionPerformed(GuiButton guiButton) throws IOException {
        if (guiButton.enabled) {
            if (guiButton instanceof GuiButtonEnumShaderOption) {
                GuiButtonEnumShaderOption guiButtonEnumShaderOption = (GuiButtonEnumShaderOption)guiButton;
                switch (guiButtonEnumShaderOption.getEnumShaderOption()) {
                    case ANTIALIASING:
                        Shaders.nextAntialiasingLevel();
                        Shaders.uninit();
                        break;
                    case NORMAL_MAP:
                        Shaders.configNormalMap = !Shaders.configNormalMap;
                        Shaders.uninit();
                        this.mc.scheduleResourcesRefresh();
                        break;
                    case SPECULAR_MAP:
                        Shaders.configSpecularMap = !Shaders.configSpecularMap;
                        Shaders.uninit();
                        this.mc.scheduleResourcesRefresh();
                        break;
                    case RENDER_RES_MUL:
                        float configRenderResMul = Shaders.configRenderResMul;
                        float[] floats = QUALITY_MULTIPLIERS;
                        String[] strs = QUALITY_MULTIPLIER_NAMES;
                        int valueIndex = getValueIndex(configRenderResMul, floats);
                        if (isShiftKeyDown()) {
                            if (--valueIndex < 0) {
                                valueIndex = floats.length - 1;
                            }
                        } else if (++valueIndex >= floats.length) {
                            valueIndex = 0;
                        }

                        Shaders.configRenderResMul = floats[valueIndex];
                        Shaders.uninit();
                        Shaders.scheduleResize();
                        break;
                    case SHADOW_RES_MUL:
                        float configShadowResMul = Shaders.configShadowResMul;
                        float[] floats2 = QUALITY_MULTIPLIERS;
                        String[] strs2 = QUALITY_MULTIPLIER_NAMES;
                        int valueIndex2 = getValueIndex(configShadowResMul, floats2);
                        if (isShiftKeyDown()) {
                            if (--valueIndex2 < 0) {
                                valueIndex2 = floats2.length - 1;
                            }
                        } else if (++valueIndex2 >= floats2.length) {
                            valueIndex2 = 0;
                        }

                        Shaders.configShadowResMul = floats2[valueIndex2];
                        Shaders.uninit();
                        Shaders.scheduleResizeShadow();
                        break;
                    case HAND_DEPTH_MUL:
                        float configHandDepthMul = Shaders.configHandDepthMul;
                        float[] floats3 = HAND_DEPTH_VALUES;
                        String[] strs3 = HAND_DEPTH_NAMES;
                        int valueIndex3 = getValueIndex(configHandDepthMul, floats3);
                        if (isShiftKeyDown()) {
                            if (--valueIndex3 < 0) {
                                valueIndex3 = floats3.length - 1;
                            }
                        } else if (++valueIndex3 >= floats3.length) {
                            valueIndex3 = 0;
                        }

                        Shaders.configHandDepthMul = floats3[valueIndex3];
                        Shaders.uninit();
                        break;
                    case OLD_HAND_LIGHT:
                        Shaders.configOldHandLight.nextValue();
                        Shaders.uninit();
                        break;
                    case OLD_LIGHTING:
                        Shaders.configOldLighting.nextValue();
                        Shaders.updateBlockLightLevel();
                        Shaders.uninit();
                        this.mc.scheduleResourcesRefresh();
                        break;
                    case TWEAK_BLOCK_DAMAGE:
                        Shaders.configTweakBlockDamage = !Shaders.configTweakBlockDamage;
                        break;
                    case CLOUD_SHADOW:
                        Shaders.configCloudShadow = !Shaders.configCloudShadow;
                        break;
                    case TEX_MIN_FIL_B:
                        Shaders.configTexMinFilB = (Shaders.configTexMinFilB + 1) % 3;
                        Shaders.configTexMinFilN = Shaders.configTexMinFilS = Shaders.configTexMinFilB;
                        guiButton.displayString = "Tex Min: " + Shaders.texMinFilDesc[Shaders.configTexMinFilB];
                        ShadersTex.updateTextureMinMagFilter();
                        break;
                    case TEX_MAG_FIL_N:
                        Shaders.configTexMagFilN = (Shaders.configTexMagFilN + 1) % 2;
                        guiButton.displayString = "Tex_n Mag: " + Shaders.texMagFilDesc[Shaders.configTexMagFilN];
                        ShadersTex.updateTextureMinMagFilter();
                        break;
                    case TEX_MAG_FIL_S:
                        Shaders.configTexMagFilS = (Shaders.configTexMagFilS + 1) % 2;
                        guiButton.displayString = "Tex_s Mag: " + Shaders.texMagFilDesc[Shaders.configTexMagFilS];
                        ShadersTex.updateTextureMinMagFilter();
                        break;
                    case SHADOW_CLIP_FRUSTRUM:
                        Shaders.configShadowClipFrustrum = !Shaders.configShadowClipFrustrum;
                        guiButton.displayString = "ShadowClipFrustrum: " + toStringOnOff(Shaders.configShadowClipFrustrum);
                        ShadersTex.updateTextureMinMagFilter();
                }

                guiButtonEnumShaderOption.updateButtonText();
            } else {
                switch (guiButton.id) {
                    case 201:
                        switch (getOSType()) {
                            case 1:
                                String format = String.format("cmd.exe /C start \"Open file\" \"%s\"", Shaders.shaderpacksdir.getAbsolutePath());

                                try {
                                    Runtime.getRuntime().exec(format);
                                    return;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    break;
                                }
                            case 2:
                                try {
                                    Runtime.getRuntime().exec(new String[]{"/usr/bin/open", Shaders.shaderpacksdir.getAbsolutePath()});
                                    return;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                        }

                        boolean flag = false;

                        try {
                            Class forName = Class.forName("java.awt.Desktop");
                            Object invoke = forName.getMethod("getDesktop").invoke(null);
                            forName.getMethod("browse", URI.class).invoke(invoke, new File(this.mc.mcDataDir, Shaders.shaderpacksdirname).toURI());
                        } catch (Throwable e) {
                            e.printStackTrace();
                            flag = true;
                        }

                        if (flag) {
                            Config.dbg("Opening via system class!");
                            Sys.openURL("file://" + Shaders.shaderpacksdir.getAbsolutePath());
                        }
                        break;
                    case 202:
                        new File(Shaders.shadersdir, "current.cfg");
                        Shaders.storeConfig();
                        this.saved = true;
                        this.mc.displayGuiScreen(this.parentGui);
                        break;
                    case 203:
                        GuiShaderOptions guiShaderOptions = new GuiShaderOptions(this, Config.getGameSettings());
                        Config.getMinecraft().displayGuiScreen(guiShaderOptions);
                        break;
                    default:
                        this.shaderList.actionPerformed(guiButton);
                }
            }
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        if (!this.saved) {
            Shaders.storeConfig();
        }
    }

    @Override
    public void drawScreen(int n, int n2, float f) throws IOException {
        this.drawDefaultBackground();
        this.shaderList.drawScreen(n, n2, f);
        if (this.updateTimer <= 0) {
            this.shaderList.updateList();
            this.updateTimer += 20;
        }

        this.drawCenteredString(this.fontRendererObj, this.screenTitle + " ", this.width / 2, 15, 16777215);
        String str = "OpenGL: " + Shaders.glVersionString + ", " + Shaders.glVendorString + ", " + Shaders.glRendererString;
        int stringWidth = this.fontRendererObj.getStringWidth(str);
        if (stringWidth < this.width - 5) {
            this.drawCenteredString(this.fontRendererObj, str, this.width / 2, this.height - 40, 8421504);
        } else {
            this.drawString(this.fontRendererObj, str, 5, this.height - 40, 8421504);
        }

        super.drawScreen(n, n2, f);
        this.tooltipManager.drawTooltips(n, n2, this.buttonList);
    }

    @Override
    public void updateScreen() throws IOException {
        super.updateScreen();
        this.updateTimer--;
    }

    public Minecraft getMc() {
        return this.mc;
    }

    public void drawCenteredString(String str, int n, int n2, int n3) {
        this.drawCenteredString(this.fontRendererObj, str, n, n2, n3);
    }

    public static String toStringOnOff(boolean flag) {
        String on = Lang.getOn();
        String off = Lang.getOff();
        return flag ? on : off;
    }

    public static String toStringAa(int n) {
        return n == 2 ? "FXAA 2x" : (n == 4 ? "FXAA 4x" : Lang.getOff());
    }

    public static String toStringValue(float f, float[] floats, String[] strs) {
        int valueIndex = getValueIndex(f, floats);
        return strs[valueIndex];
    }

    public static int getValueIndex(float f, float[] floats) {
        for (int i = 0; i < floats.length; i++) {
            float f2 = floats[i];
            if (f2 >= f) {
                return i;
            }
        }

        return floats.length - 1;
    }

    public static String toStringQuality(float f) {
        return toStringValue(f, QUALITY_MULTIPLIERS, QUALITY_MULTIPLIER_NAMES);
    }

    public static String toStringHandDepth(float f) {
        return toStringValue(f, HAND_DEPTH_VALUES, HAND_DEPTH_NAMES);
    }

    public static int getOSType() {
        String toLowerCase = System.getProperty("os.name").toLowerCase();
        return toLowerCase.contains("win")
            ? 1
            : (
                toLowerCase.contains("mac")
                    ? 2
                    : (toLowerCase.contains("solaris") ? 3 : (toLowerCase.contains("sunos") ? 3 : (toLowerCase.contains("linux") ? 4 : (toLowerCase.contains("unix") ? 4 : 0))))
            );
    }
}
