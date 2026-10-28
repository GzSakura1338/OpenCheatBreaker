package com.cheatbreaker.client.discord;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.config.ServerIndex;
import com.cheatbreaker.client.manager.BlockedServer;
import com.cheatbreaker.client.manager.BlockedServerLevel;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.StringUtils;

public class DiscordPresenceUtils {
    public static boolean isIpAddress(String str) {
        if (str == null) {
            return false;
        }

        String[] split = str.split("\\.");
        int n = 0;

        for (String str2 : split) {
            try {
                int parseInt = Integer.parseInt(str2);
                if (parseInt < 0 || parseInt > 255) {
                    return false;
                }

                n++;
            } catch (Exception e) {
                return false;
            }
        }

        return n == 4;
    }

    public static String[] getPresence(String str) {
        String str2 = "Private Server";
        String str3 = null;
        String str4 = null;
        if (Minecraft.getMinecraft().isSingleplayer()) {
            str2 = "Singleplayer";
            str3 = "Playing in Singleplayer";
        } else if (StringUtils.isEmpty(str)) {
            return new String[]{"In Menus", "cb", null, null, "In the Main Menu", null};
        }

        String toLowerCase = str.toLowerCase();

        for (BlockedServer blockedServer : CheatBreaker.getInstance().getBlockedServersManager().getStringToBlockedServerMap().values()) {
            for (String str5 : blockedServer.getDomains()) {
                String toLowerCase2 = str5.toLowerCase();
                boolean flag = toLowerCase.endsWith(toLowerCase2) || toLowerCase.matches("([a-zA-Z0-9]+)" + toLowerCase2 + "([a-zA-Z0-9]+)");
                if (flag) {
                    if (blockedServer.getText3() != null && !blockedServer.getText3().isEmpty()) {
                        str2 = "Problematic Server";
                        str3 = "Playing on a problematic server";
                        str4 = blockedServer.getText3();
                    } else if (Objects.requireNonNull(blockedServer.getBlockedServerLevel()) == BlockedServerLevel.UNSAFE) {
                        str2 = "Unsafe Server";
                        str3 = "Playing on an unsafe server";
                        str4 = "Risking my security";
                    }

                    return new String[]{str2, "unsafeserver", "cheatbreaker.net", "cb_small", str3, str4};
                }
            }
        }

        if (str.contains("localhost") || str.equals("127.0.0.1") || str.startsWith("192.168") || str.equals("0") || str.equals("0.0.0.0")) {
            str2 = "Local Server";
        } else if (isIpAddress(str)) {
            str2 = "Numeric Server";
        }

        if (str3 == null) {
            str3 = "Playing on a " + str2.toLowerCase();
        }

        String lookupByAddress = ServerIndex.lookupByAddress(str.toLowerCase(), "name");
        if (lookupByAddress != null) {
            String lookupByAddress2 = ServerIndex.lookupByAddress(str.toLowerCase(), "id");
            String str5 = lookupByAddress.endsWith("Network") ? "the " : "";
            return new String[]{
                lookupByAddress + " (" + str + ")",
                "https://client-api.cheatbreaker.net/mappings/servers/" + lookupByAddress2 + "/discord.png",
                "cheatbreaker.net",
                "cb_small",
                "Playing on " + str5 + lookupByAddress,
                str4
            };
        } else {
            return new String[]{str2, str2.toLowerCase().replace(" ", ""), "cheatbreaker.net", "cb_small", str3, str4};
        }
    }
}
