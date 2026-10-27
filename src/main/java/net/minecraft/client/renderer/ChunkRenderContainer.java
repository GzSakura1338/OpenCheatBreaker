package net.minecraft.client.renderer;

import com.google.common.collect.Lists;
import java.util.BitSet;
import java.util.List;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.optifine.SmartAnimations;

public abstract class ChunkRenderContainer {
   public double viewEntityX;
   public double viewEntityY;
   public double viewEntityZ;
   public List<RenderChunk> renderChunks = Lists.newArrayListWithCapacity(17424);
   public boolean initialized;
   public BitSet animatedSpritesRendered;
   public BitSet animatedSpritesCached = new BitSet();

   public void initialize(double viewEntityXIn, double viewEntityYIn, double viewEntityZIn) {
      this.initialized = true;
      this.renderChunks.clear();
      this.viewEntityX = viewEntityXIn;
      this.viewEntityY = viewEntityYIn;
      this.viewEntityZ = viewEntityZIn;
      if (SmartAnimations.isActive()) {
         if (this.animatedSpritesRendered != null) {
            SmartAnimations.spritesRendered(this.animatedSpritesRendered);
         } else {
            this.animatedSpritesRendered = this.animatedSpritesCached;
         }

         this.animatedSpritesRendered.clear();
      } else if (this.animatedSpritesRendered != null) {
         SmartAnimations.spritesRendered(this.animatedSpritesRendered);
         this.animatedSpritesRendered = null;
      }
   }

   public void preRenderChunk(RenderChunk renderChunkIn) {
      BlockPos position = renderChunkIn.getPosition();
      GlStateManager.translate((float)(position.getX() - this.viewEntityX), (float)(position.getY() - this.viewEntityY), (float)(position.getZ() - this.viewEntityZ));
   }

   public void addRenderChunk(RenderChunk renderChunkIn, EnumWorldBlockLayer layer) {
      this.renderChunks.add(renderChunkIn);
      if (this.animatedSpritesRendered != null) {
         BitSet animatedSprites = renderChunkIn.compiledChunk.getAnimatedSprites(layer);
         if (animatedSprites != null) {
            this.animatedSpritesRendered.or(animatedSprites);
         }
      }
   }

   public abstract void renderChunkLayer(EnumWorldBlockLayer enumWorldBlockLayer);
}
