package net.minecraft.client.resources;

import java.util.UUID;
import net.minecraft.util.ResourceLocation;

public class DefaultPlayerSkin {
    public static ResourceLocation TEXTURE_STEVE = new ResourceLocation("textures/entity/steve.png");
    public static ResourceLocation TEXTURE_ALEX = new ResourceLocation("textures/entity/alex.png");

    public static ResourceLocation getDefaultSkin(UUID uuid) {
        return isSlimSkin(uuid) ? TEXTURE_ALEX : TEXTURE_STEVE;
    }

    public static boolean isSlimSkin(UUID uuid) {
        return (uuid.hashCode() & 1) == 1;
    }

    public static String getSkinType(UUID uuid) {
        return isSlimSkin(uuid) ? "slim" : "default";
    }

    public static ResourceLocation getDefaultSkinLegacy() {
        return TEXTURE_STEVE;
    }
}
