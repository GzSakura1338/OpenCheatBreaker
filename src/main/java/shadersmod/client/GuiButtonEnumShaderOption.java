package shadersmod.client;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class GuiButtonEnumShaderOption extends GuiButton {
    public EnumShaderOption enumShaderOption = null;

    public GuiButtonEnumShaderOption(EnumShaderOption enumShaderOption, int n, int n2, int n3, int n4) {
        super(enumShaderOption.ordinal(), n, n2, n3, n4, getButtonText(enumShaderOption));
        this.enumShaderOption = enumShaderOption;
    }

    public EnumShaderOption getEnumShaderOption() {
        return this.enumShaderOption;
    }

    public static String getButtonText(EnumShaderOption enumShaderOption) {
        String str = I18n.format(enumShaderOption.getResourceKey()) + ": ";
        switch (enumShaderOption) {
            case ANTIALIASING:
                return str + GuiShaders.toStringAa(Shaders.configAntialiasingLevel);
            case NORMAL_MAP:
                return str + GuiShaders.toStringOnOff(Shaders.configNormalMap);
            case SPECULAR_MAP:
                return str + GuiShaders.toStringOnOff(Shaders.configSpecularMap);
            case RENDER_RES_MUL:
                return str + GuiShaders.toStringQuality(Shaders.configRenderResMul);
            case SHADOW_RES_MUL:
                return str + GuiShaders.toStringQuality(Shaders.configShadowResMul);
            case HAND_DEPTH_MUL:
                return str + GuiShaders.toStringHandDepth(Shaders.configHandDepthMul);
            case CLOUD_SHADOW:
                return str + GuiShaders.toStringOnOff(Shaders.configCloudShadow);
            case OLD_HAND_LIGHT:
                return str + Shaders.configOldHandLight.getUserValue();
            case OLD_LIGHTING:
                return str + Shaders.configOldLighting.getUserValue();
            case SHADOW_CLIP_FRUSTRUM:
                return str + GuiShaders.toStringOnOff(Shaders.configShadowClipFrustrum);
            case TWEAK_BLOCK_DAMAGE:
                return str + GuiShaders.toStringOnOff(Shaders.configTweakBlockDamage);
            default:
                return str + Shaders.getEnumShaderOption(enumShaderOption);
        }
    }

    public void updateButtonText() {
        this.displayString = getButtonText(this.enumShaderOption);
    }
}
