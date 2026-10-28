package net.optifine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.settings.GameSettings;
import shadersmod.client.GuiButtonShaderOption;
import shadersmod.client.ShaderOption;
import shadersmod.client.ShaderOptionProfile;

public class TooltipProviderShaderOptions extends TooltipProviderOptions {
    @Override
    public String[] getTooltipLines(GuiButton guiButton, int n) {
        if (!(guiButton instanceof GuiButtonShaderOption)) {
            return null;
        }

        GuiButtonShaderOption guiButtonShaderOption = (GuiButtonShaderOption)guiButton;
        ShaderOption shaderOption = guiButtonShaderOption.getShaderOption();
        return this.makeTooltipLines(shaderOption, n);
    }

    public String[] makeTooltipLines(ShaderOption shaderOption, int n) {
        if (shaderOption instanceof ShaderOptionProfile) {
            return null;
        }

        String nameText = shaderOption.getNameText();
        String trim = Config.normalize(shaderOption.getDescriptionText()).trim();
        String[] splitDescription = this.splitDescription(trim);
        GameSettings gameSettings = Config.getGameSettings();
        String str = null;
        if (!nameText.equals(shaderOption.getName()) && gameSettings.advancedItemTooltips) {
            str = "§8" + Lang.get("of.general.id") + ": " + shaderOption.getName();
        }

        String str2 = null;
        if (shaderOption.getPaths() != null && gameSettings.advancedItemTooltips) {
            str2 = "§8" + Lang.get("of.general.from") + ": " + Config.arrayToString(shaderOption.getPaths());
        }

        String str3 = null;
        if (shaderOption.getValueDefault() != null && gameSettings.advancedItemTooltips) {
            String str4 = shaderOption.isEnabled() ? shaderOption.getValueText(shaderOption.getValueDefault()) : Lang.get("of.general.ambiguous");
            str3 = "§8" + Lang.getDefault() + ": " + str4;
        }

        ArrayList list = new ArrayList();
        list.add(nameText);
        list.addAll(Arrays.asList(splitDescription));
        if (str != null) {
            list.add(str);
        }

        if (str2 != null) {
            list.add(str2);
        }

        if (str3 != null) {
            list.add(str3);
        }

        return this.makeTooltipLines(n, list);
    }

    public String[] splitDescription(String str) {
        if (str.length() <= 0) {
            return new String[0];
        }

        str = StrUtils.removePrefix(str, "//");
        String[] split = str.split("\\. ");

        for (int i = 0; i < split.length; i++) {
            split[i] = "- " + split[i].trim();
            split[i] = StrUtils.removeSuffix(split[i], ".");
        }

        return split;
    }

    public String[] makeTooltipLines(int n, List<String> list) {
        FontRenderer fontRendererObj = Config.getMinecraft().fontRendererObj;
        ArrayList list2 = new ArrayList();

        for (int i = 0; i < list.size(); i++) {
            String str = (String)list.get(i);
            if (str != null && str.length() > 0) {
                for (String str2 : (Iterable<String>)fontRendererObj.listFormattedStringToWidth(str, n)) {
                    list2.add(str2);
                }
            }
        }

        return (String[])list2.toArray(new String[list2.size()]);
    }
}
