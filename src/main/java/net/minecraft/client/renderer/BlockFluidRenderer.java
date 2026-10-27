package net.minecraft.client.renderer;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.optifine.Config;
import net.optifine.CustomColors;
import net.optifine.render.RenderEnv;
import net.optifine.shaders.SVertexBuilder;

public class BlockFluidRenderer {
   public TextureAtlasSprite[] atlasSpritesLava = new TextureAtlasSprite[2];
   public TextureAtlasSprite[] atlasSpritesWater = new TextureAtlasSprite[2];

   public BlockFluidRenderer() {
      this.initAtlasSprites();
   }

   public void initAtlasSprites() {
      TextureMap textureMapBlocks = Minecraft.getMinecraft().getTextureMapBlocks();
      this.atlasSpritesLava[0] = textureMapBlocks.getAtlasSprite("minecraft:blocks/lava_still");
      this.atlasSpritesLava[1] = textureMapBlocks.getAtlasSprite("minecraft:blocks/lava_flow");
      this.atlasSpritesWater[0] = textureMapBlocks.getAtlasSprite("minecraft:blocks/water_still");
      this.atlasSpritesWater[1] = textureMapBlocks.getAtlasSprite("minecraft:blocks/water_flow");
   }

   public boolean renderFluid(IBlockAccess blockAccess, IBlockState blockStateIn, BlockPos blockPosIn, WorldRenderer worldRendererIn) {
      boolean flag;
      try {
         if (Config.isShaders()) {
            SVertexBuilder.pushEntity(blockStateIn, blockPosIn, blockAccess, worldRendererIn);
         }

         BlockLiquid block = (BlockLiquid)blockStateIn.getBlock();
         block.setBlockBoundsBasedOnState(blockAccess, blockPosIn);
         TextureAtlasSprite[] textureAtlasSprites = block.getMaterial() == Material.lava ? this.atlasSpritesLava : this.atlasSpritesWater;
         RenderEnv renderEnv = worldRendererIn.getRenderEnv(blockStateIn, blockPosIn);
         int fluidColor = CustomColors.getFluidColor(blockAccess, blockStateIn, blockPosIn, renderEnv);
         float f = (fluidColor >> 16 & 0xFF) / 255.0F;
         float f2 = (fluidColor >> 8 & 0xFF) / 255.0F;
         float f3 = (fluidColor & 0xFF) / 255.0F;
         boolean shouldSideBeRendered = block.shouldSideBeRendered(blockAccess, blockPosIn.up(), EnumFacing.UP);
         boolean shouldSideBeRendered2 = block.shouldSideBeRendered(blockAccess, blockPosIn.down(), EnumFacing.DOWN);
         boolean[] borderFlags = renderEnv.getBorderFlags();
         borderFlags[0] = block.shouldSideBeRendered(blockAccess, blockPosIn.north(), EnumFacing.NORTH);
         borderFlags[1] = block.shouldSideBeRendered(blockAccess, blockPosIn.south(), EnumFacing.SOUTH);
         borderFlags[2] = block.shouldSideBeRendered(blockAccess, blockPosIn.west(), EnumFacing.WEST);
         borderFlags[3] = block.shouldSideBeRendered(blockAccess, blockPosIn.east(), EnumFacing.EAST);
         if (shouldSideBeRendered || shouldSideBeRendered2 || borderFlags[0] || borderFlags[1] || borderFlags[2] || borderFlags[3]) {
            flag = false;
            float f4 = 0.5F;
            float f5 = 1.0F;
            float f6 = 0.8F;
            float f7 = 0.6F;
            Material material = block.getMaterial();
            float fluidHeight = this.getFluidHeight(blockAccess, blockPosIn, material);
            float fluidHeight2 = this.getFluidHeight(blockAccess, blockPosIn.south(), material);
            float fluidHeight3 = this.getFluidHeight(blockAccess, blockPosIn.east().south(), material);
            float fluidHeight4 = this.getFluidHeight(blockAccess, blockPosIn.east(), material);
            double d = blockPosIn.getX();
            double d2 = blockPosIn.getY();
            double d3 = blockPosIn.getZ();
            float f8 = 0.001F;
            if (shouldSideBeRendered) {
               flag = true;
               TextureAtlasSprite textureAtlasSprite = textureAtlasSprites[0];
               float flowDirection = (float)BlockLiquid.getFlowDirection(blockAccess, blockPosIn, material);
               if (flowDirection > -999.0F) {
                  textureAtlasSprite = textureAtlasSprites[1];
               }

               worldRendererIn.setSprite(textureAtlasSprite);
               fluidHeight -= f8;
               fluidHeight2 -= f8;
               fluidHeight3 -= f8;
               fluidHeight4 -= f8;
               float f9;
               float f10;
               float f11;
               float f12;
               float f13;
               float f14;
               float f15;
               float f16;
               if (flowDirection < -999.0F) {
                  f9 = textureAtlasSprite.getInterpolatedU(0.0);
                  f13 = textureAtlasSprite.getInterpolatedV(0.0);
                  f10 = f9;
                  f14 = textureAtlasSprite.getInterpolatedV(16.0);
                  f11 = textureAtlasSprite.getInterpolatedU(16.0);
                  f15 = f14;
                  f12 = f11;
                  f16 = f13;
               } else {
                  float f17 = MathHelper.sin(flowDirection) * 0.25F;
                  float f18 = MathHelper.cos(flowDirection) * 0.25F;
                  float f19 = 8.0F;
                  f9 = textureAtlasSprite.getInterpolatedU(8.0F + (-f18 - f17) * 16.0F);
                  f13 = textureAtlasSprite.getInterpolatedV(8.0F + (-f18 + f17) * 16.0F);
                  f10 = textureAtlasSprite.getInterpolatedU(8.0F + (-f18 + f17) * 16.0F);
                  f14 = textureAtlasSprite.getInterpolatedV(8.0F + (f18 + f17) * 16.0F);
                  f11 = textureAtlasSprite.getInterpolatedU(8.0F + (f18 + f17) * 16.0F);
                  f15 = textureAtlasSprite.getInterpolatedV(8.0F + (f18 - f17) * 16.0F);
                  f12 = textureAtlasSprite.getInterpolatedU(8.0F + (f18 - f17) * 16.0F);
                  f16 = textureAtlasSprite.getInterpolatedV(8.0F + (-f18 - f17) * 16.0F);
               }

               int mixedBrightnessForBlock = block.getMixedBrightnessForBlock(blockAccess, blockPosIn);
               int n = mixedBrightnessForBlock >> 16 & 65535;
               int n2 = mixedBrightnessForBlock & 65535;
               float f17 = f5 * f;
               float f18 = f5 * f2;
               float f19 = f5 * f3;
               worldRendererIn.pos(d + 0.0, d2 + fluidHeight, d3 + 0.0)
                  .color(f17, f18, f19, 1.0F)
                  .tex(f9, f13)
                  .lightmap(n, n2)
                  .endVertex();
               worldRendererIn.pos(d + 0.0, d2 + fluidHeight2, d3 + 1.0)
                  .color(f17, f18, f19, 1.0F)
                  .tex(f10, f14)
                  .lightmap(n, n2)
                  .endVertex();
               worldRendererIn.pos(d + 1.0, d2 + fluidHeight3, d3 + 1.0)
                  .color(f17, f18, f19, 1.0F)
                  .tex(f11, f15)
                  .lightmap(n, n2)
                  .endVertex();
               worldRendererIn.pos(d + 1.0, d2 + fluidHeight4, d3 + 0.0)
                  .color(f17, f18, f19, 1.0F)
                  .tex(f12, f16)
                  .lightmap(n, n2)
                  .endVertex();
               if (block.shouldRenderSides(blockAccess, blockPosIn.up())) {
                  worldRendererIn.pos(d + 0.0, d2 + fluidHeight, d3 + 0.0)
                     .color(f17, f18, f19, 1.0F)
                     .tex(f9, f13)
                     .lightmap(n, n2)
                     .endVertex();
                  worldRendererIn.pos(d + 1.0, d2 + fluidHeight4, d3 + 0.0)
                     .color(f17, f18, f19, 1.0F)
                     .tex(f12, f16)
                     .lightmap(n, n2)
                     .endVertex();
                  worldRendererIn.pos(d + 1.0, d2 + fluidHeight3, d3 + 1.0)
                     .color(f17, f18, f19, 1.0F)
                     .tex(f11, f15)
                     .lightmap(n, n2)
                     .endVertex();
                  worldRendererIn.pos(d + 0.0, d2 + fluidHeight2, d3 + 1.0)
                     .color(f17, f18, f19, 1.0F)
                     .tex(f10, f14)
                     .lightmap(n, n2)
                     .endVertex();
               }
            }

            if (shouldSideBeRendered2) {
               worldRendererIn.setSprite(textureAtlasSprites[0]);
               float minU = textureAtlasSprites[0].getMinU();
               float maxU = textureAtlasSprites[0].getMaxU();
               float minV = textureAtlasSprites[0].getMinV();
               float maxV = textureAtlasSprites[0].getMaxV();
               int mixedBrightnessForBlock = block.getMixedBrightnessForBlock(blockAccess, blockPosIn.down());
               int n = mixedBrightnessForBlock >> 16 & 65535;
               int n2 = mixedBrightnessForBlock & 65535;
               float faceBrightness = FaceBakery.getFaceBrightness(EnumFacing.DOWN);
               worldRendererIn.pos(d, d2, d3 + 1.0)
                  .color(f * faceBrightness, f2 * faceBrightness, f3 * faceBrightness, 1.0F)
                  .tex(minU, maxV)
                  .lightmap(n, n2)
                  .endVertex();
               worldRendererIn.pos(d, d2, d3)
                  .color(f * faceBrightness, f2 * faceBrightness, f3 * faceBrightness, 1.0F)
                  .tex(minU, minV)
                  .lightmap(n, n2)
                  .endVertex();
               worldRendererIn.pos(d + 1.0, d2, d3)
                  .color(f * faceBrightness, f2 * faceBrightness, f3 * faceBrightness, 1.0F)
                  .tex(maxU, minV)
                  .lightmap(n, n2)
                  .endVertex();
               worldRendererIn.pos(d + 1.0, d2, d3 + 1.0)
                  .color(f * faceBrightness, f2 * faceBrightness, f3 * faceBrightness, 1.0F)
                  .tex(maxU, maxV)
                  .lightmap(n, n2)
                  .endVertex();
               flag = true;
            }

            for (int i = 0; i < 4; i++) {
               int n = 0;
               int n2 = 0;
               if (i == 0) {
                  n2--;
               }

               if (i == 1) {
                  n2++;
               }

               if (i == 2) {
                  n--;
               }

               if (i == 3) {
                  n++;
               }

               BlockPos blockPos = blockPosIn.add(n, 0, n2);
               TextureAtlasSprite textureAtlasSprite = textureAtlasSprites[1];
               worldRendererIn.setSprite(textureAtlasSprite);
               if (borderFlags[i]) {
                  float f9;
                  float f10;
                  double d4;
                  double d5;
                  double d6;
                  double d7;
                  if (i == 0) {
                     f9 = fluidHeight;
                     f10 = fluidHeight4;
                     d4 = d;
                     d6 = d + 1.0;
                     d5 = d3 + f8;
                     d7 = d3 + f8;
                  } else if (i == 1) {
                     f9 = fluidHeight3;
                     f10 = fluidHeight2;
                     d4 = d + 1.0;
                     d6 = d;
                     d5 = d3 + 1.0 - f8;
                     d7 = d3 + 1.0 - f8;
                  } else if (i == 2) {
                     f9 = fluidHeight2;
                     f10 = fluidHeight;
                     d4 = d + f8;
                     d6 = d + f8;
                     d5 = d3 + 1.0;
                     d7 = d3;
                  } else {
                     f9 = fluidHeight4;
                     f10 = fluidHeight3;
                     d4 = d + 1.0 - f8;
                     d6 = d + 1.0 - f8;
                     d5 = d3;
                     d7 = d3 + 1.0;
                  }

                  flag = true;
                  float interpolatedU = textureAtlasSprite.getInterpolatedU(0.0);
                  float interpolatedU2 = textureAtlasSprite.getInterpolatedU(8.0);
                  float interpolatedV = textureAtlasSprite.getInterpolatedV((1.0F - f9) * 16.0F * 0.5F);
                  float interpolatedV2 = textureAtlasSprite.getInterpolatedV((1.0F - f10) * 16.0F * 0.5F);
                  float interpolatedV3 = textureAtlasSprite.getInterpolatedV(8.0);
                  int mixedBrightnessForBlock = block.getMixedBrightnessForBlock(blockAccess, blockPos);
                  int n3 = mixedBrightnessForBlock >> 16 & 65535;
                  int n4 = mixedBrightnessForBlock & 65535;
                  float f11 = i < 2 ? FaceBakery.getFaceBrightness(EnumFacing.NORTH) : FaceBakery.getFaceBrightness(EnumFacing.WEST);
                  float f12 = f5 * f11 * f;
                  float f13 = f5 * f11 * f2;
                  float f14 = f5 * f11 * f3;
                  worldRendererIn.pos(d4, d2 + f9, d5).color(f12, f13, f14, 1.0F).tex(interpolatedU, interpolatedV).lightmap(n3, n4).endVertex();
                  worldRendererIn.pos(d6, d2 + f10, d7).color(f12, f13, f14, 1.0F).tex(interpolatedU2, interpolatedV2).lightmap(n3, n4).endVertex();
                  worldRendererIn.pos(d6, d2 + 0.0, d7).color(f12, f13, f14, 1.0F).tex(interpolatedU2, interpolatedV3).lightmap(n3, n4).endVertex();
                  worldRendererIn.pos(d4, d2 + 0.0, d5).color(f12, f13, f14, 1.0F).tex(interpolatedU, interpolatedV3).lightmap(n3, n4).endVertex();
                  worldRendererIn.pos(d4, d2 + 0.0, d5).color(f12, f13, f14, 1.0F).tex(interpolatedU, interpolatedV3).lightmap(n3, n4).endVertex();
                  worldRendererIn.pos(d6, d2 + 0.0, d7).color(f12, f13, f14, 1.0F).tex(interpolatedU2, interpolatedV3).lightmap(n3, n4).endVertex();
                  worldRendererIn.pos(d6, d2 + f10, d7).color(f12, f13, f14, 1.0F).tex(interpolatedU2, interpolatedV2).lightmap(n3, n4).endVertex();
                  worldRendererIn.pos(d4, d2 + f9, d5).color(f12, f13, f14, 1.0F).tex(interpolatedU, interpolatedV).lightmap(n3, n4).endVertex();
               }
            }

            worldRendererIn.setSprite((TextureAtlasSprite)null);
            return flag;
         }

         flag = false;
      } finally {
         if (Config.isShaders()) {
            SVertexBuilder.popEntity(worldRendererIn);
         }
      }

      return flag;
   }

   public float getFluidHeight(IBlockAccess blockAccess, BlockPos blockPosIn, Material blockMaterial) {
      int n = 0;
      float f = 0.0F;

      for (int i = 0; i < 4; i++) {
         BlockPos blockPos = blockPosIn.add(-(i & 1), 0, -(i >> 1 & 1));
         if (blockAccess.getBlockState(blockPos.up()).getBlock().getMaterial() == blockMaterial) {
            return 1.0F;
         }

         IBlockState blockState = blockAccess.getBlockState(blockPos);
         Material material = blockState.getBlock().getMaterial();
         if (material != blockMaterial) {
            if (!material.isSolid()) {
               f++;
               n++;
            }
         } else {
            int value = blockState.getValue(BlockLiquid.LEVEL);
            if (value >= 8 || value == 0) {
               f += BlockLiquid.getLiquidHeightPercent(value) * 10.0F;
               n += 10;
            }

            f += BlockLiquid.getLiquidHeightPercent(value);
            n++;
         }
      }

      return 1.0F - f / n;
   }
}
