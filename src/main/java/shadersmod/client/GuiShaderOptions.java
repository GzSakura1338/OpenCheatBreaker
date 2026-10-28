package shadersmod.client;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MathHelper;
import net.optifine.Config;
import net.optifine.GuiScreenOF;
import net.optifine.Lang;
import net.optifine.TooltipManager;
import net.optifine.TooltipProviderShaderOptions;

public class GuiShaderOptions extends GuiScreenOF {
    public TooltipManager tooltipManager = new TooltipManager(this, new TooltipProviderShaderOptions());
    public String screenName = null;
    public String screenText = null;
    public boolean changed = false;
    public String title = "Shader Options";
    public GuiScreen prevScreen;
    public GameSettings settings;
    public static final String OPTION_PROFILE = "<profile>";
    public static final String OPTION_EMPTY = "<empty>";
    public static final String OPTION_REST = "*";

    public GuiShaderOptions(GuiScreen guiScreen, GameSettings gameSettings) {
        this.prevScreen = guiScreen;
        this.settings = gameSettings;
    }

    public GuiShaderOptions(GuiScreen guiScreen, GameSettings gameSettings, String str) {
        this(guiScreen, gameSettings);
        this.screenName = str;
        if (str != null) {
            this.screenText = Shaders.translate("screen." + str, str);
        }
    }

    @Override
    public void initGui() {
        this.title = I18n.format("of.options.shaderOptionsTitle");
        byte b = 100;
        boolean flag = false;
        byte b2 = 30;
        byte b3 = 20;
        byte b4 = 120;
        byte b5 = 20;
        int shaderPackColumns = Shaders.getShaderPackColumns(this.screenName, 2);
        ShaderOption[] shaderPackOptions = Shaders.getShaderPackOptions(this.screenName);
        if (shaderPackOptions != null) {
            int n = MathHelper.ceiling_double_int(shaderPackOptions.length / 9.0);
            if (shaderPackColumns < n) {
                shaderPackColumns = n;
            }

            for (int i = 0; i < shaderPackOptions.length; i++) {
                ShaderOption shaderOption = shaderPackOptions[i];
                if (shaderOption != null && shaderOption.isVisible()) {
                    int n2 = i % shaderPackColumns;
                    int n3 = i / shaderPackColumns;
                    int min = Math.min(this.width / shaderPackColumns, 200);
                    int n4 = (this.width - min * shaderPackColumns) / 2;
                    int n5 = n2 * min + 5 + n4;
                    int n6 = b2 + n3 * b3;
                    int n7 = min - 10;
                    String buttonText = getButtonText(shaderOption, n7);
                    GuiButtonShaderOption guiButtonShaderOption;
                    if (Shaders.isShaderPackOptionSlider(shaderOption.getName())) {
                        guiButtonShaderOption = new GuiSliderShaderOption(b + i, n5, n6, n7, b5, shaderOption, buttonText);
                    } else {
                        guiButtonShaderOption = new GuiButtonShaderOption(b + i, n5, n6, n7, b5, shaderOption, buttonText);
                    }

                    guiButtonShaderOption.enabled = shaderOption.isEnabled();
                    this.buttonList.add(guiButtonShaderOption);
                }
            }
        }

        this.buttonList.add(new GuiButton(201, this.width / 2 - b4 - 20, this.height / 6 + 168 + 11, b4, b5, I18n.format("controls.reset")));
        this.buttonList.add(new GuiButton(200, this.width / 2 + 20, this.height / 6 + 168 + 11, b4, b5, I18n.format("gui.done")));
    }

    public static String getButtonText(ShaderOption shaderOption, int n) {
        String nameText = shaderOption.getNameText();
        if (shaderOption instanceof ShaderOptionScreen) {
            ShaderOptionScreen shaderOptionScreen = (ShaderOptionScreen)shaderOption;
            return nameText + "...";
        }

        FontRenderer fontRendererObj = Config.getMinecraft().fontRendererObj;
        int n2 = fontRendererObj.getStringWidth(": " + Lang.getOff()) + 5;

        while (fontRendererObj.getStringWidth(nameText) + n2 >= n && nameText.length() > 0) {
            nameText = nameText.substring(0, nameText.length() - 1);
        }

        String str = shaderOption.isChanged() ? shaderOption.getValueColor(shaderOption.getValue()) : "";
        String valueText = shaderOption.getValueText(shaderOption.getValue());
        return nameText + ": " + str + valueText;
    }

    @Override
    public void actionPerformed(GuiButton guiButton) throws java.io.IOException {
        if (guiButton.enabled) {
            if (guiButton.id < 200 && guiButton instanceof GuiButtonShaderOption) {
                GuiButtonShaderOption guiButtonShaderOption = (GuiButtonShaderOption)guiButton;
                ShaderOption shaderOption = guiButtonShaderOption.getShaderOption();
                if (shaderOption instanceof ShaderOptionScreen) {
                    String name = shaderOption.getName();
                    GuiShaderOptions guiShaderOptions = new GuiShaderOptions(this, this.settings, name);
                    this.mc.displayGuiScreen(guiShaderOptions);
                    return;
                }

                if (isShiftKeyDown()) {
                    shaderOption.resetValue();
                } else {
                    shaderOption.nextValue();
                }

                this.updateAllButtons();
                this.changed = true;
            }

            if (guiButton.id == 201) {
                ShaderOption[] changedOptions = Shaders.getChangedOptions(Shaders.getShaderPackOptions());

                for (int i = 0; i < changedOptions.length; i++) {
                    ShaderOption shaderOption = changedOptions[i];
                    shaderOption.resetValue();
                    this.changed = true;
                }

                this.updateAllButtons();
            }

            if (guiButton.id == 200) {
                if (this.changed) {
                    Shaders.saveShaderPackOptions();
                    this.changed = false;
                    Shaders.uninit();
                }

                this.mc.displayGuiScreen(this.prevScreen);
            }
        }
    }

    @Override
    public void actionPerformedRightClick(GuiButton guiButton) {
        if (guiButton instanceof GuiButtonShaderOption) {
            GuiButtonShaderOption guiButtonShaderOption = (GuiButtonShaderOption)guiButton;
            ShaderOption shaderOption = guiButtonShaderOption.getShaderOption();
            if (isShiftKeyDown()) {
                shaderOption.resetValue();
            } else {
                shaderOption.prevValue();
            }

            this.updateAllButtons();
            this.changed = true;
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        if (this.changed) {
            Shaders.saveShaderPackOptions();
            this.changed = false;
            Shaders.uninit();
        }
    }

    public void updateAllButtons() {
        for (GuiButton guiButton : this.buttonList) {
            if (guiButton instanceof GuiButtonShaderOption) {
                GuiButtonShaderOption guiButtonShaderOption = (GuiButtonShaderOption)guiButton;
                ShaderOption shaderOption = guiButtonShaderOption.getShaderOption();
                if (shaderOption instanceof ShaderOptionProfile) {
                    ShaderOptionProfile shaderOptionProfile = (ShaderOptionProfile)shaderOption;
                    shaderOptionProfile.updateProfile();
                }

                guiButtonShaderOption.displayString = getButtonText(shaderOption, guiButtonShaderOption.getButtonWidth());
                guiButtonShaderOption.valueChanged();
            }
        }
    }

    @Override
    public void drawScreen(int n, int n2, float f) throws java.io.IOException {
        this.drawDefaultBackground();
        if (this.screenText != null) {
            this.drawCenteredString(this.fontRendererObj, this.screenText, this.width / 2, 15, 16777215);
        } else {
            this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 15, 16777215);
        }

        super.drawScreen(n, n2, f);
        this.tooltipManager.drawTooltips(n, n2, this.buttonList);
    }
}
