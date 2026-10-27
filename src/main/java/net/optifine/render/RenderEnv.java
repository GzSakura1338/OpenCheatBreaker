package net.optifine.render;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelRenderer.AmbientOcclusionFace;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BreakingFour;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.optifine.BlockPosM;
import net.optifine.Config;
import net.optifine.model.ListQuadsOverlay;

public class RenderEnv {
   public int blockId = -1;
   public int metadata = -1;
   public int breakingAnimation = -1;
   public int smartLeaves = -1;
   public float[] quadBounds = new float[EnumFacing.VALUES.length * 2];
   public BitSet boundsFlags = new BitSet(3);
   public AmbientOcclusionFace aoFace = new AmbientOcclusionFace();
   public BlockPosM colorizerBlockPosM = null;
   public boolean[] borderFlags = null;
   public boolean[] borderFlags2 = null;
   public boolean[] borderFlags3 = null;
   public EnumFacing[] borderDirections = null;
   public List<BakedQuad> listQuadsCustomizer = new ArrayList<>();
   public List<BakedQuad> listQuadsCtmMultipass = new ArrayList<>();
   public BakedQuad[] arrayQuadsCtm1 = new BakedQuad[1];
   public BakedQuad[] arrayQuadsCtm2 = new BakedQuad[2];
   public BakedQuad[] arrayQuadsCtm3 = new BakedQuad[3];
   public BakedQuad[] arrayQuadsCtm4 = new BakedQuad[4];
   public RegionRenderCacheBuilder regionRenderCacheBuilder = null;
   public ListQuadsOverlay[] listsQuadsOverlay = new ListQuadsOverlay[EnumWorldBlockLayer.values().length];
   public boolean overlaysRendered = false;
   public IBlockState blockState;
   public BlockPos blockPos;
   public static final int UNKNOWN = -1;
   public static final int FALSE = 0;
   public static final int TRUE = 1;

   public RenderEnv(IBlockState blockState, BlockPos blockPos) {
      this.blockState = blockState;
      this.blockPos = blockPos;
   }

   public void reset(IBlockState blockState, BlockPos blockPos) {
      if (this.blockState != blockState || this.blockPos != blockPos) {
         this.blockState = blockState;
         this.blockPos = blockPos;
         this.blockId = -1;
         this.metadata = -1;
         this.breakingAnimation = -1;
         this.smartLeaves = -1;
         this.boundsFlags.clear();
      }
   }

   public int getBlockId() {
      if (this.blockId < 0) {
         if (this.blockState instanceof BlockStateBase) {
            BlockStateBase blockState = (BlockStateBase)this.blockState;
            this.blockId = blockState.getBlockId();
         } else {
            this.blockId = Block.getIdFromBlock(this.blockState.getBlock());
         }
      }

      return this.blockId;
   }

   public int getMetadata() {
      if (this.metadata < 0) {
         if (this.blockState instanceof BlockStateBase) {
            BlockStateBase blockState = (BlockStateBase)this.blockState;
            this.metadata = blockState.getMetadata();
         } else {
            this.metadata = this.blockState.getBlock().getMetaFromState(this.blockState);
         }
      }

      return this.metadata;
   }

   public float[] getQuadBounds() {
      return this.quadBounds;
   }

   public BitSet getBoundsFlags() {
      return this.boundsFlags;
   }

   public AmbientOcclusionFace getAoFace() {
      return this.aoFace;
   }

   public boolean isBreakingAnimation(List list) {
      if (this.breakingAnimation == -1 && list.size() > 0) {
         if (list.get(0) instanceof BreakingFour) {
            this.breakingAnimation = 1;
         } else {
            this.breakingAnimation = 0;
         }
      }

      return this.breakingAnimation == 1;
   }

   public boolean isBreakingAnimation(BakedQuad bakedQuad) {
      if (this.breakingAnimation < 0) {
         if (bakedQuad instanceof BreakingFour) {
            this.breakingAnimation = 1;
         } else {
            this.breakingAnimation = 0;
         }
      }

      return this.breakingAnimation == 1;
   }

   public boolean isBreakingAnimation() {
      return this.breakingAnimation == 1;
   }

   public IBlockState getBlockState() {
      return this.blockState;
   }

   public BlockPosM getColorizerBlockPosM() {
      if (this.colorizerBlockPosM == null) {
         this.colorizerBlockPosM = new BlockPosM(0, 0, 0);
      }

      return this.colorizerBlockPosM;
   }

   public boolean[] getBorderFlags() {
      if (this.borderFlags == null) {
         this.borderFlags = new boolean[4];
      }

      return this.borderFlags;
   }

   public boolean[] getBorderFlags2() {
      if (this.borderFlags2 == null) {
         this.borderFlags2 = new boolean[4];
      }

      return this.borderFlags2;
   }

   public boolean[] getBorderFlags3() {
      if (this.borderFlags3 == null) {
         this.borderFlags3 = new boolean[4];
      }

      return this.borderFlags3;
   }

   public EnumFacing[] getBorderDirections() {
      if (this.borderDirections == null) {
         this.borderDirections = new EnumFacing[4];
      }

      return this.borderDirections;
   }

   public EnumFacing[] getBorderDirections(EnumFacing enumFacing, EnumFacing enumFacing2, EnumFacing enumFacing3, EnumFacing enumFacing4) {
      EnumFacing[] borderDirections = this.getBorderDirections();
      borderDirections[0] = enumFacing;
      borderDirections[1] = enumFacing2;
      borderDirections[2] = enumFacing3;
      borderDirections[3] = enumFacing4;
      return borderDirections;
   }

   public boolean isSmartLeaves() {
      if (this.smartLeaves == -1) {
         if (Config.isTreesSmart() && this.blockState.getBlock() instanceof BlockLeaves) {
            this.smartLeaves = 1;
         } else {
            this.smartLeaves = 0;
         }
      }

      return this.smartLeaves == 1;
   }

   public List<BakedQuad> getListQuadsCustomizer() {
      return this.listQuadsCustomizer;
   }

   public BakedQuad[] getArrayQuadsCtm(BakedQuad bakedQuad) {
      this.arrayQuadsCtm1[0] = bakedQuad;
      return this.arrayQuadsCtm1;
   }

   public BakedQuad[] getArrayQuadsCtm(BakedQuad bakedQuad, BakedQuad bakedQuad2) {
      this.arrayQuadsCtm2[0] = bakedQuad;
      this.arrayQuadsCtm2[1] = bakedQuad2;
      return this.arrayQuadsCtm2;
   }

   public BakedQuad[] getArrayQuadsCtm(BakedQuad bakedQuad, BakedQuad bakedQuad2, BakedQuad bakedQuad3) {
      this.arrayQuadsCtm3[0] = bakedQuad;
      this.arrayQuadsCtm3[1] = bakedQuad2;
      this.arrayQuadsCtm3[2] = bakedQuad3;
      return this.arrayQuadsCtm3;
   }

   public BakedQuad[] getArrayQuadsCtm(BakedQuad bakedQuad, BakedQuad bakedQuad2, BakedQuad bakedQuad3, BakedQuad bakedQuad4) {
      this.arrayQuadsCtm4[0] = bakedQuad;
      this.arrayQuadsCtm4[1] = bakedQuad2;
      this.arrayQuadsCtm4[2] = bakedQuad3;
      this.arrayQuadsCtm4[3] = bakedQuad4;
      return this.arrayQuadsCtm4;
   }

   public List<BakedQuad> getListQuadsCtmMultipass(BakedQuad[] bakedQuads) {
      this.listQuadsCtmMultipass.clear();
      if (bakedQuads != null) {
         for (int i = 0; i < bakedQuads.length; i++) {
            BakedQuad bakedQuad = bakedQuads[i];
            this.listQuadsCtmMultipass.add(bakedQuad);
         }
      }

      return this.listQuadsCtmMultipass;
   }

   public RegionRenderCacheBuilder getRegionRenderCacheBuilder() {
      return this.regionRenderCacheBuilder;
   }

   public void setRegionRenderCacheBuilder(RegionRenderCacheBuilder regionRenderCacheBuilder) {
      this.regionRenderCacheBuilder = regionRenderCacheBuilder;
   }

   public ListQuadsOverlay getListQuadsOverlay(EnumWorldBlockLayer enumWorldBlockLayer) {
      ListQuadsOverlay listQuadsOverlay = this.listsQuadsOverlay[enumWorldBlockLayer.ordinal()];
      if (listQuadsOverlay == null) {
         listQuadsOverlay = new ListQuadsOverlay();
         this.listsQuadsOverlay[enumWorldBlockLayer.ordinal()] = listQuadsOverlay;
      }

      return listQuadsOverlay;
   }

   public boolean isOverlaysRendered() {
      return this.overlaysRendered;
   }

   public void setOverlaysRendered(boolean flag) {
      this.overlaysRendered = flag;
   }
}
