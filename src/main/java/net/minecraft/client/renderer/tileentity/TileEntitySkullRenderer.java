package net.minecraft.client.renderer.tileentity;

import com.cheatbreaker.client.CheatBreaker;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelHumanoidHead;
import net.minecraft.client.model.ModelSkeletonHead;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TileEntitySkullRenderer extends TileEntitySpecialRenderer<TileEntitySkull> {
   public static ResourceLocation SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/skeleton.png");
   public static ResourceLocation WITHER_SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/wither_skeleton.png");
   public static ResourceLocation ZOMBIE_TEXTURES = new ResourceLocation("textures/entity/zombie/zombie.png");
   public static ResourceLocation CREEPER_TEXTURES = new ResourceLocation("textures/entity/creeper/creeper.png");
   public static TileEntitySkullRenderer instance;
   public ModelSkeletonHead skeletonHead = new ModelSkeletonHead(0, 0, 64, 32);
   public ModelSkeletonHead humanoidHead = new ModelHumanoidHead();

   public void renderTileEntityAt(TileEntitySkull te, double x, double y, double z, float partialTicks, int destroyStage) {
      if (!CheatBreaker.getInstance().getKeybindSettings().hidePlacedSkulls.getBooleanValue()) {
         EnumFacing front = EnumFacing.getFront(te.getBlockMetadata() & 7);
         this.renderSkull((float)x, (float)y, (float)z, front, te.getSkullRotation() * 360 / 16.0F, te.getSkullType(), te.getPlayerProfile(), destroyStage);
      }
   }

   @Override
   public void setRendererDispatcher(TileEntityRendererDispatcher rendererDispatcherIn) {
      super.setRendererDispatcher(rendererDispatcherIn);
      instance = this;
   }

   public void renderSkull(float f, float f2, float f3, EnumFacing enumFacing, float f4, int n, GameProfile gameProfile, int n2) {
      ModelSkeletonHead skeletonHead = this.skeletonHead;
      if (n2 >= 0) {
         this.bindTexture(DESTROY_STAGES[n2]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.scale(4.0F, 2.0F, 1.0F);
         GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         switch (n) {
            case 0:
            default:
               this.bindTexture(SKELETON_TEXTURES);
               break;
            case 1:
               this.bindTexture(WITHER_SKELETON_TEXTURES);
               break;
            case 2:
               this.bindTexture(ZOMBIE_TEXTURES);
               skeletonHead = this.humanoidHead;
               break;
            case 3:
               skeletonHead = this.humanoidHead;
               ResourceLocation defaultSkinLegacy = DefaultPlayerSkin.getDefaultSkinLegacy();
               if (gameProfile != null) {
                  Minecraft minecraft = Minecraft.getMinecraft();
                  Map loadSkinFromCache = minecraft.getSkinManager().loadSkinFromCache(gameProfile);
                  if (loadSkinFromCache != null && loadSkinFromCache.containsKey(Type.SKIN)) {
                     defaultSkinLegacy = minecraft.getSkinManager().loadSkin((MinecraftProfileTexture)loadSkinFromCache.get(Type.SKIN), Type.SKIN);
                  } else {
                     UUID uUID = EntityPlayer.getUUID(gameProfile);
                     defaultSkinLegacy = DefaultPlayerSkin.getDefaultSkin(uUID);
                  }
               }

               this.bindTexture(defaultSkinLegacy);
               break;
            case 4:
               this.bindTexture(CREEPER_TEXTURES);
         }
      }

      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      if (enumFacing != EnumFacing.UP) {
         switch (enumFacing) {
            case NORTH:
               GlStateManager.translate(f + 0.5F, f2 + 0.25F, f3 + 0.74F);
               break;
            case SOUTH:
               GlStateManager.translate(f + 0.5F, f2 + 0.25F, f3 + 0.26F);
               f4 = 180.0F;
               break;
            case WEST:
               GlStateManager.translate(f + 0.74F, f2 + 0.25F, f3 + 0.5F);
               f4 = 270.0F;
               break;
            case EAST:
            default:
               GlStateManager.translate(f + 0.26F, f2 + 0.25F, f3 + 0.5F);
               f4 = 90.0F;
         }
      } else {
         GlStateManager.translate(f + 0.5F, f2, f3 + 0.5F);
      }

      float f5 = 0.0625F;
      GlStateManager.enableRescaleNormal();
      GlStateManager.scale(-1.0F, -1.0F, 1.0F);
      GlStateManager.enableAlpha();
      skeletonHead.render((Entity)null, 0.0F, 0.0F, 0.0F, f4, 0.0F, f5);
      GlStateManager.popMatrix();
      if (n2 >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }
   }
}
