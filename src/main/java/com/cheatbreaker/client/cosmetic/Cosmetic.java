package com.cheatbreaker.client.cosmetic;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class Cosmetic {
    public List<String> extras = new ArrayList<>();
    public long id;
    public int emoteId;
    public String playerId;
    public String name;
    public CosmeticType cosmeticType;
    public float scale;
    public boolean equipped;
    public ResourceLocation texture;
    public ResourceLocation thumbnail;

    public Cosmetic(String str, int n, CosmeticType cosmeticType) {
        this.playerId = str;
        this.emoteId = n;
        this.cosmeticType = cosmeticType;
    }

    public void setEquipped(boolean flag) {
        this.equipped = flag;
    }

    public void setScale(float f) {
        this.scale = f;
    }

    public long getId() {
        return this.id;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getPlayerId() {
        return this.playerId;
    }

    public static ResourceLocation getThumbnailLocation(CosmeticType cosmeticType, ResourceLocation resourceLocation) {
        if (cosmeticType == CosmeticType.HAT) {
            String resourcePath = resourceLocation.getResourcePath();
            String str = resourcePath.endsWith(".png") ? resourcePath.substring(0, resourcePath.length() - 4) : resourcePath;
            ResourceLocation resourceLocation2 = new ResourceLocation(resourceLocation.getResourceDomain(), str + "_thumbnail.png");
            if (resourceExists(resourceLocation2)) {
                return resourceLocation2;
            }

            String substring = resourcePath.substring(0, resourcePath.lastIndexOf(47) + 1);
            if (!substring.isEmpty()) {
                String substring2 = substring.substring(0, substring.length() - 1);
                substring2 = substring2.substring(substring2.lastIndexOf(47) + 1);
                ResourceLocation resourceLocation3 = new ResourceLocation(resourceLocation.getResourceDomain(), substring + substring2 + "_thumbnail.png");
                if (!resourceLocation3.equals(resourceLocation2) && resourceExists(resourceLocation3)) {
                    return resourceLocation3;
                }
            }

            return new ResourceLocation(resourceLocation.getResourceDomain(), substring + "thumbnail.png");
        } else if (cosmeticType == CosmeticType.WINGS) {
            String substring = resourceLocation.getResourcePath().substring("client/cosmetics/".length());
            return new ResourceLocation(resourceLocation.getResourceDomain(), "client/cosmetics/wings/thumbnail/" + substring);
        } else {
            return resourceLocation;
        }
    }

    public void setThumbnail(ResourceLocation resourceLocation) {
        this.thumbnail = resourceLocation;
    }

    public int getEmoteId() {
        return this.emoteId;
    }

    public void setPlayerId(String str) {
        this.playerId = str;
    }

    public ResourceLocation getThumbnail() {
        return this.thumbnail;
    }

    public void setTexture(ResourceLocation resourceLocation) {
        this.texture = resourceLocation;
    }

    public float getScale() {
        return this.scale;
    }

    public ResourceLocation getTexture() {
        return this.texture;
    }

    public static boolean resourceExists(ResourceLocation resourceLocation) {
        try (InputStream inputStream = Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream()) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public Cosmetic(long l, String str, String str2, CosmeticType cosmeticType, float f, boolean flag, String str3, String str4) {
        this.id = l;
        this.playerId = str;
        this.name = str2;
        this.cosmeticType = cosmeticType;
        this.scale = f;
        this.equipped = flag;
        this.texture = getTextureLocation(cosmeticType, str3);
        this.thumbnail = getThumbnailLocation(cosmeticType, this.texture);
        this.extras = str4 != null && !str4.isEmpty() ? new ArrayList<>(Arrays.asList(str4.split(","))) : new ArrayList<>();
    }

    public Cosmetic(String str, String str2, CosmeticType cosmeticType, float f, boolean flag, String str3) {
        this.playerId = str;
        this.name = str2;
        this.cosmeticType = cosmeticType;
        this.scale = f;
        this.equipped = flag;
        this.texture = getTextureLocation(cosmeticType, str3);
        this.thumbnail = getThumbnailLocation(cosmeticType, this.texture);
    }

    public void setId(long l) {
        this.id = l;
    }

    public String getName() {
        return this.name;
    }

    public static ResourceLocation getTextureLocation(CosmeticType cosmeticType, String str) {
        ResourceLocation resourceLocation = new ResourceLocation(str);
        String str2;
        switch (cosmeticType) {
            case CAPE:
                str2 = "capes/";
                break;
            case WINGS:
                str2 = "wings/";
                break;
            case HAT:
                str2 = "hats/";
                break;
            default:
                return resourceLocation;
        }

        String resourcePath = resourceLocation.getResourcePath();
        if (resourcePath.startsWith("client/")) {
            resourcePath = resourcePath.substring("client/".length());
        }

        if (resourcePath.startsWith("cosmetics/")) {
            resourcePath = resourcePath.substring("cosmetics/".length());
        }

        if (resourcePath.startsWith(str2)) {
            resourcePath = resourcePath.substring(str2.length());
        }

        return new ResourceLocation(resourceLocation.getResourceDomain(), "client/cosmetics/" + str2 + resourcePath);
    }

    public void setCosmeticType(CosmeticType cosmeticType) {
        this.cosmeticType = cosmeticType;
    }

    public CosmeticType getCosmeticType() {
        return this.cosmeticType;
    }

    public boolean isEquipped() {
        return this.equipped;
    }

    public List<String> getExtras() {
        return this.extras;
    }

    public void setExtras(List<String> list) {
        this.extras = list;
    }

    public void setEmoteId(int n) {
        this.emoteId = n;
    }
}
