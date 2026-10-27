package net.minecraft.client.resources;

import java.util.UUID;
import net.minecraft.util.ResourceLocation;

public class DefaultPlayerSkin {
   public static ResourceLocation TEXTURE_STEVE = new ResourceLocation("textures/entity/steve.png");
   public static ResourceLocation TEXTURE_ALEX = new ResourceLocation("textures/entity/alex.png");

   public static ResourceLocation getDefaultSkinLegacy() {
      return TEXTURE_STEVE;
   }

   public static ResourceLocation getDefaultSkin(UUID playerUUID) {
      return isSlimSkin(playerUUID) ? TEXTURE_ALEX : TEXTURE_STEVE;
   }

   public static String getSkinType(UUID playerUUID) {
      return isSlimSkin(playerUUID) ? "slim" : "default";
   }

   public static boolean isSlimSkin(UUID playerUUID) {
      return (playerUUID.hashCode() & 1) == 1;
   }
}
