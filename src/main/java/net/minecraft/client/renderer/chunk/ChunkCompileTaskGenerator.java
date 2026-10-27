package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;

public class ChunkCompileTaskGenerator {
   public ReentrantLock lock = new ReentrantLock();
   public List<Runnable> listFinishRunnables = Lists.newArrayList();
   public ChunkCompileTaskGenerator.Status status = ChunkCompileTaskGenerator.Status.PENDING;
   public RenderChunk renderChunk;
   public RegionRenderCacheBuilder regionRenderCacheBuilder;
   public CompiledChunk compiledChunk;
   public ChunkCompileTaskGenerator.Type type;
   public boolean finished;

   public ChunkCompileTaskGenerator(RenderChunk renderChunkIn, ChunkCompileTaskGenerator.Type typeIn) {
      this.renderChunk = renderChunkIn;
      this.type = typeIn;
   }

   public ChunkCompileTaskGenerator.Status getStatus() {
      return this.status;
   }

   public RenderChunk getRenderChunk() {
      return this.renderChunk;
   }

   public CompiledChunk getCompiledChunk() {
      return this.compiledChunk;
   }

   public void setCompiledChunk(CompiledChunk compiledChunkIn) {
      this.compiledChunk = compiledChunkIn;
   }

   public RegionRenderCacheBuilder getRegionRenderCacheBuilder() {
      return this.regionRenderCacheBuilder;
   }

   public void setRegionRenderCacheBuilder(RegionRenderCacheBuilder regionRenderCacheBuilderIn) {
      this.regionRenderCacheBuilder = regionRenderCacheBuilderIn;
   }

   public void setStatus(ChunkCompileTaskGenerator.Status statusIn) {
      this.lock.lock();

      try {
         this.status = statusIn;
      } finally {
         this.lock.unlock();
      }
   }

   public void finish() {
      this.lock.lock();

      try {
         if (this.type == ChunkCompileTaskGenerator.Type.REBUILD_CHUNK && this.status != ChunkCompileTaskGenerator.Status.DONE) {
            this.renderChunk.setNeedsUpdate(true);
         }

         this.finished = true;
         this.status = ChunkCompileTaskGenerator.Status.DONE;

         for (Runnable runnable : this.listFinishRunnables) {
            runnable.run();
         }
      } finally {
         this.lock.unlock();
      }
   }

   public void addFinishRunnable(Runnable runnable) {
      this.lock.lock();

      try {
         this.listFinishRunnables.add(runnable);
         if (this.finished) {
            runnable.run();
         }
      } finally {
         this.lock.unlock();
      }
   }

   public ReentrantLock getLock() {
      return this.lock;
   }

   public ChunkCompileTaskGenerator.Type getType() {
      return this.type;
   }

   public boolean isFinished() {
      return this.finished;
   }

   public enum Status {
      PENDING,
      COMPILING,
      UPLOADING,
      DONE;
   }

   public enum Type {
      REBUILD_CHUNK,
      RESORT_TRANSPARENCY;
   }
}
