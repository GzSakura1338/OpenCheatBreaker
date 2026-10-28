package net.minecraft.client.renderer.tileentity;

import com.cheatbreaker.client.CheatBreaker;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class TileEntitySkullRenderer extends TileEntitySpecialRenderer {
    public static ResourceLocation SKELETON_TEXTURE = new ResourceLocation("textures/entity/skeleton/skeleton.png");
    public static ResourceLocation WITHER_SKELETON_TEXTURE = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
    public static ResourceLocation ZOMBIE_TEXTURE = new ResourceLocation("textures/entity/zombie/zombie.png");
    public static ResourceLocation CREEPER_TEXTURE = new ResourceLocation("textures/entity/creeper/creeper.png");
    public static TileEntitySkullRenderer instance;
    public ModelSkeletonHead model = new ModelSkeletonHead(0, 0, 64, 32);
    public ModelSkeletonHead modelSkeletonHead = new ModelSkeletonHead(0, 0, 64, 64);

    public void renderTileEntityAt(TileEntitySkull tileEntitySkull, double d, double d2, double d3, float f) {
        if (!CheatBreaker.getInstance().getKeybindSettings().hidePlacedSkulls.getBooleanValue()) {
            this.render(
                (float)d, (float)d2, (float)d3, tileEntitySkull.getBlockMetadata() & 7, tileEntitySkull.getSkullRotation() * 360 / 16.0F, tileEntitySkull.getSkullType(), tileEntitySkull.getOwner()
            );
        }
    }

    @Override
    public void setDispatcher(TileEntityRendererDispatcher tileEntityRendererDispatcher) {
        super.setDispatcher(tileEntityRendererDispatcher);
        instance = this;
    }

    public void render(float f, float f2, float f3, int n, float f4, int n2, GameProfile gameProfile) {
        ModelSkeletonHead model = this.model;
        switch (n2) {
            case 0:
            default:
                this.bindTexture(SKELETON_TEXTURE);
                break;
            case 1:
                this.bindTexture(WITHER_SKELETON_TEXTURE);
                break;
            case 2:
                this.bindTexture(ZOMBIE_TEXTURE);
                model = this.modelSkeletonHead;
                break;
            case 3:
                ResourceLocation locationStevePng = AbstractClientPlayer.locationStevePng;
                if (gameProfile != null) {
                    Minecraft minecraft = Minecraft.getMinecraft();
                    Map loadSkinFromCache = minecraft.getSkinManager().loadSkinFromCache(gameProfile);
                    if (loadSkinFromCache != null && loadSkinFromCache.containsKey(Type.SKIN)) {
                        locationStevePng = minecraft.getSkinManager().loadSkin((MinecraftProfileTexture)loadSkinFromCache.get(Type.SKIN), Type.SKIN);
                    }
                }

                this.bindTexture(locationStevePng);
                break;
            case 4:
                this.bindTexture(CREEPER_TEXTURE);
        }

        GL11.glPushMatrix();
        GL11.glDisable(2884);
        if (n != 1) {
            switch (n) {
                case 2:
                    GL11.glTranslatef(f + 0.5F, f2 + 0.25F, f3 + 0.74F);
                    break;
                case 3:
                    GL11.glTranslatef(f + 0.5F, f2 + 0.25F, f3 + 0.26F);
                    f4 = 180.0F;
                    break;
                case 4:
                    GL11.glTranslatef(f + 0.74F, f2 + 0.25F, f3 + 0.5F);
                    f4 = 270.0F;
                    break;
                case 5:
                default:
                    GL11.glTranslatef(f + 0.26F, f2 + 0.25F, f3 + 0.5F);
                    f4 = 90.0F;
            }
        } else {
            GL11.glTranslatef(f + 0.5F, f2, f3 + 0.5F);
        }

        float f5 = 0.0625F;
        GL11.glEnable(32826);
        GL11.glScalef(-1.0F, -1.0F, 1.0F);
        GL11.glEnable(3008);
        model.render(null, 0.0F, 0.0F, 0.0F, f4, 0.0F, f5);
        GL11.glPopMatrix();
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double d, double d2, double d3, float f) {
        this.renderTileEntityAt((TileEntitySkull)tileEntity, d, d2, d3, f);
    }
}
