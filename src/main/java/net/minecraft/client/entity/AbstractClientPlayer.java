package net.minecraft.client.entity;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.cosmetic.Cosmetic;
import com.cheatbreaker.client.event.PlayerSkinLookupEvent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.SkinManager.SkinAvailableCallback;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import net.optifine.CapeUtils;
import net.optifine.Config;

public abstract class AbstractClientPlayer extends EntityPlayer implements SkinAvailableCallback {
    public static ResourceLocation locationStevePng = new ResourceLocation("textures/entity/steve.png");
    public ResourceLocation locationSkin;
    public ResourceLocation locationCape;
    public ResourceLocation locationOfCape = null;
    public String nameClear;
    public static boolean flag2 = false;
    public String text;

    public AbstractClientPlayer(World world, GameProfile gameProfile) {
        super(world, gameProfile);
        String commandSenderName = this.getCommandSenderName();
        if (!commandSenderName.isEmpty()) {
            SkinManager skinManager = Minecraft.getMinecraft().getSkinManager();
            skinManager.handleGameProfile(gameProfile, this, true);
        }

        this.nameClear = gameProfile.getName();
        if (this.nameClear != null && !this.nameClear.isEmpty()) {
            this.nameClear = StringUtils.stripControlCodes(this.nameClear);
        }

        Cosmetic findCosmetic2 = CheatBreaker.getInstance().getCosmeticsManager().findCosmetic2(this.getUniqueID());
        Cosmetic findCosmetic3 = CheatBreaker.getInstance().getCosmeticsManager().findCosmetic3(this.getUniqueID());
        Cosmetic findCosmetic = CheatBreaker.getInstance().getCosmeticsManager().findCosmetic(this.getUniqueID());

        try {
            if (CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet() != null) {
                CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().requestPlayerCosmetics(this);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (findCosmetic2 != null && CheatBreaker.getInstance().getKeybindSettings().showCheatbreakerCapes.getBooleanValue()) {
            this.setLocationOfCape(findCosmetic2.getTexture());
            this.setCosmetic(findCosmetic2);
        } else if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().showOptifineCapes.getValue()) {
            CapeUtils.handleAbstractClientPlayer(this, this.getGameProfile().getId());
        }

        if (findCosmetic3 != null) {
            this.setCosmetic2(findCosmetic3);
        }

        if (findCosmetic != null) {
            this.setCosmetic3(findCosmetic);
        }
    }

    public boolean hasCape() {
        return Config.isShowCapes() && (this.locationOfCape != null || this.locationCape != null);
    }

    public boolean hasSkin() {
        return this.locationSkin != null;
    }

    public ResourceLocation getLocationSkin() {
        PlayerSkinLookupEvent playerSkinLookupEvent = new PlayerSkinLookupEvent(this.nameClear);
        CheatBreaker.getInstance().getEventBus().handleEvent(playerSkinLookupEvent);
        if (this.locationSkin != null && !playerSkinLookupEvent.isCancelled()) {
            return this.locationSkin;
        }

        run();
        return locationStevePng;
    }

    public ResourceLocation getLocationCape() {
        if (this.hasCosmetic()) {
            return this.getCosmetic().getTexture();
        } else {
            return !Config.isShowCapes() ? null : (this.locationOfCape != null ? this.locationOfCape : this.locationCape);
        }
    }

    public static ThreadDownloadImageData getDownloadImageSkin(ResourceLocation resourceLocationIn, String username) {
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        ITextureObject texture = textureManager.getTexture(resourceLocationIn);
        if (texture == null) {
            texture = new ThreadDownloadImageData(
                (File)null,
                String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtils.stripControlCodes(username)),
                locationStevePng,
                new ImageBufferDownload()
            );
            textureManager.loadTexture(resourceLocationIn, texture);
        }

        return (ThreadDownloadImageData)texture;
    }

    public static ResourceLocation getLocationSkin(String username) {
        return new ResourceLocation("skins/" + StringUtils.stripControlCodes(username));
    }

    public String getNameClear() {
        return this.nameClear;
    }

    public ResourceLocation getLocationOfCape() {
        return this.locationOfCape;
    }

    public void setLocationOfCape(ResourceLocation resourceLocation) {
        this.locationOfCape = resourceLocation;
    }

    public String getText() {
        return this.text == null ? DefaultPlayerSkin.getSkinType(this.getUniqueID()) : this.text;
    }

    public static void run() {
        if (!flag2) {
            flag2 = true;

            try {
                BufferedImage read = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(locationStevePng).getInputStream());
                if (read != null && read.getWidth() == 64 && read.getHeight() == 32) {
                    BufferedImage parseUserSkin = new ImageBufferDownload().parseUserSkin(read);
                    Minecraft.getMinecraft().getTextureManager().loadTexture(locationStevePng, new DynamicTexture(parseUserSkin));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void handleType(Type type, ResourceLocation resourceLocation, MinecraftProfileTexture minecraftProfileTexture) {
        switch (type) {
            case SKIN:
                this.locationSkin = resourceLocation;
                this.text = minecraftProfileTexture.getMetadata("model");
                if (this.text == null) {
                    this.text = "default";
                }
                break;
            case CAPE:
                this.locationCape = resourceLocation;
        }
    }
}
