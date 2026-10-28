package com.cheatbreaker.client.cosmetic;

import java.util.HashMap;
import java.util.Map;

public enum CosmeticType {
    WINGS("dragon_wings"),
    CAPE("cape"),
    HAT("hat"),
    EMOTE("emote"),
    ICON("icon"),
    SUBICON("subicon");

    public static Map<String, CosmeticType> stringToCosmeticTypeMap = new HashMap<>();
    public String id;

    static {
        for (CosmeticType cosmeticType : values()) {
            stringToCosmeticTypeMap.put(cosmeticType.id, cosmeticType);
        }
    }

    CosmeticType(String str) {
        this.id = str;
    }

    public static CosmeticType get(String str) {
        return stringToCosmeticTypeMap.get(str);
    }

    public String getId() {
        return this.id;
    }
}
