package shadersmod.client;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.optifine.GlStateManager;

public class GuiSliderShaderOption extends GuiButtonShaderOption {
    public float sliderValue = 1.0F;
    public boolean dragging;
    public ShaderOption shaderOption = null;

    public GuiSliderShaderOption(int n, int n2, int n3, int n4, int n5, ShaderOption shaderOption, String str) {
        super(n, n2, n3, n4, n5, shaderOption, str);
        this.shaderOption = shaderOption;
        this.sliderValue = shaderOption.getIndexNormalized();
        this.displayString = GuiShaderOptions.getButtonText(shaderOption, this.width);
    }

    @Override
    public int getHoverState(boolean flag) {
        return 0;
    }

    @Override
    public void mouseDragged(Minecraft mc, int n, int n2) {
        if (this.visible) {
            if (this.dragging) {
                this.sliderValue = (float)(n - (this.xPosition + 4)) / (this.width - 8);
                this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
                this.shaderOption.setIndexNormalized(this.sliderValue);
                this.sliderValue = this.shaderOption.getIndexNormalized();
                this.displayString = GuiShaderOptions.getButtonText(this.shaderOption, this.width);
            }

            mc.getTextureManager().bindTexture(buttonTextures);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            drawTexturedRect(this.xPosition + (int)(this.sliderValue * (this.width - 8)), this.yPosition, 0.0F, 66.0F, 4, 20);
            drawTexturedRect(this.xPosition + (int)(this.sliderValue * (this.width - 8)) + 4, this.yPosition, 196.0F, 66.0F, 4, 20);
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int n, int n2) throws java.io.IOException {
        if (super.mousePressed(mc, n, n2)) {
            this.sliderValue = (float)(n - (this.xPosition + 4)) / (this.width - 8);
            this.sliderValue = MathHelper.clamp_float(this.sliderValue, 0.0F, 1.0F);
            this.shaderOption.setIndexNormalized(this.sliderValue);
            this.displayString = GuiShaderOptions.getButtonText(this.shaderOption, this.width);
            this.dragging = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void mouseReleased(int n, int n2) {
        this.dragging = false;
    }

    @Override
    public void valueChanged() {
        this.sliderValue = this.shaderOption.getIndexNormalized();
    }
}
