package com.cheatbreaker.client.cosmetic;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.network.WebsocketNetHandler;
import com.cheatbreaker.client.network.packet.RecentCosmeticsPacket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class RecentCosmeticsManager {
    public static final int MAX_PER_TYPE = 10;

    public static void setRecentFromServer(List<String> list) {
        ArrayList list2 = new ArrayList();

        for (String str : list) {
            if (str != null && !str.isEmpty() && !str.contains(",") && !list2.contains(str)) {
                list2.add(str);
            }
        }

        if (list2.isEmpty()) {
            List recent = getRecent();
            if (!recent.isEmpty()) {
                syncRecent(recent);
            }
        } else {
            CheatBreaker.getInstance().getKeybindSettings().recentCosmetics.changeValue(String.join(",", list2));
        }
    }

    public static void markRecent(Cosmetic cosmetic) {
        List<String> recent = getRecent();
        String key = getKey(cosmetic);
        recent.remove(key);
        recent.add(0, key);
        HashMap<String, Integer> map = new HashMap<>();
        recent.removeIf(str -> map.merge(str.substring(0, Math.max(0, str.indexOf(58))), 1, Integer::sum) > 10);
        CheatBreaker.getInstance().getKeybindSettings().recentCosmetics.changeValue(String.join(",", recent));
        syncRecent(recent);
    }

    public static List<String> getRecent() {
        return parseList(CheatBreaker.getInstance().getKeybindSettings().recentCosmetics);
    }

    public static Set<String> getFavorites() {
        return new LinkedHashSet<>(parseList(CheatBreaker.getInstance().getKeybindSettings().favoriteCosmetics));
    }

    public static void registerEquipped(List<Cosmetic> list) {
        List recent = getRecent();

        for (Cosmetic cosmetic : list) {
            if (cosmetic.isEquipped()) {
                boolean anyMatch = list.stream().anyMatch(cosmetic2 -> cosmetic2.getCosmeticType() == cosmetic.getCosmeticType() && recent.contains(getKey(cosmetic2)));
                if (!anyMatch) {
                    markRecent(cosmetic);
                    recent.add(0, getKey(cosmetic));
                }
            }
        }
    }

    public static void toggleFavorite(Cosmetic cosmetic) {
        Set favorites = getFavorites();
        String key = getKey(cosmetic);
        if (!favorites.remove(key)) {
            favorites.add(key);
        }

        CheatBreaker.getInstance().getKeybindSettings().favoriteCosmetics.changeValue(String.join(",", favorites));
    }

    public static List<String> parseList(Setting setting) {
        ArrayList list = new ArrayList();
        String stringValue = setting.getStringValue();
        if (stringValue != null && !stringValue.isEmpty()) {
            for (String str : stringValue.split(",")) {
                if (!str.isEmpty() && !list.contains(str)) {
                    list.add(str);
                }
            }
        }

        return list;
    }

    public static void syncRecent(List<String> list) {
        WebsocketNetHandler playerassetsCheatbreakerNet = CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet();
        if (playerassetsCheatbreakerNet != null) {
            playerassetsCheatbreakerNet.sendPacket(new RecentCosmeticsPacket(new ArrayList<>(list)));
        }
    }

    public static String getKey(Cosmetic cosmetic) {
        return cosmetic.getCosmeticType().getId() + ":" + cosmetic.getName();
    }
}
