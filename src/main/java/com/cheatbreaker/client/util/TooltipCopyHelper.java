package com.cheatbreaker.client.util;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.ui.notification.Notification;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;

public class TooltipCopyHelper {
    public static String getTooltipText(ItemStack itemStack) {
        List<String> list = itemStack.getTooltip(Minecraft.getMinecraft().thePlayer, false);
        return list.stream().map(EnumChatFormatting::getTextWithoutFormattingCodes).map(String::trim).filter(string -> !string.isEmpty()).collect(Collectors.joining(" | "));
    }

    public static boolean copyToClipboard(String string) {
        if (string == null || string.trim().isEmpty()) {
            return false;
        }
        GuiScreen.setClipboardString(string);
        Notification.post("Copied to clipboard", string.length() > 60 ? string.substring(0, 57) + "..." : string);
        return true;
    }

    public static boolean isCopyKeyPressed(int n) {
        Setting setting = CheatBreaker.getInstance().getKeybindSettings().copyHoveredTextKeybind;
        return !setting.hasMouseBind() && n != 0 && n == setting.getIntValue() && setting.areModifiersDown();
    }

    public static boolean isCopyMouseButtonPressed(int n) {
        Setting setting = CheatBreaker.getInstance().getKeybindSettings().copyHoveredTextKeybind;
        return setting.hasMouseBind() && n == setting.getIntValue() && setting.areModifiersDown();
    }
}
