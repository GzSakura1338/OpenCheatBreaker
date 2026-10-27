package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumWorldBlockLayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkRenderWorker implements Runnable {
   public static Logger LOGGER = LogManager.getLogger();
   public ChunkRenderDispatcher chunkRenderDispatcher;
   public RegionRenderCacheBuilder regionRenderCacheBuilder;
   public static ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(
      Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
   );

   public ChunkRenderWorker(ChunkRenderDispatcher chunkRenderDispatcher) {
      this(chunkRenderDispatcher, (RegionRenderCacheBuilder)null);
   }

   public ChunkRenderWorker(ChunkRenderDispatcher chunkRenderDispatcherIn, RegionRenderCacheBuilder regionRenderCacheBuilderIn) {
      this.chunkRenderDispatcher = chunkRenderDispatcherIn;
      this.regionRenderCacheBuilder = regionRenderCacheBuilderIn;
   }

   @Override
   public void run() {
      while (true) {
         try {
            this.processTask(this.chunkRenderDispatcher.getNextChunkUpdate());
         } catch (InterruptedException e) {
            LOGGER.debug("Stopping due to interrupt");
            return;
         } catch (Throwable e) {
            CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Batching chunks");
            Minecraft.getMinecraft().crashed(Minecraft.getMinecraft().addGraphicsAndWorldToCrashReport(makeCrashReport));
            return;
         }
      }
   }

   public void processTask(final ChunkCompileTaskGenerator generator) throws InterruptedException {
      generator.getLock().lock();

      try {
         if (generator.getStatus() != ChunkCompileTaskGenerator.Status.PENDING) {
            if (!generator.isFinished()) {
               LOGGER.warn("Chunk render task was " + generator.getStatus() + " when I expected it to be pending; ignoring task");
            }

            return;
         }

         generator.setStatus(ChunkCompileTaskGenerator.Status.COMPILING);
      } finally {
         generator.getLock().unlock();
      }

      Entity renderViewEntity = Minecraft.getMinecraft().getRenderViewEntity();
      if (renderViewEntity == null) {
         generator.finish();
      } else {
         generator.setRegionRenderCacheBuilder(this.getRegionRenderCacheBuilder());
         float posX = (float)renderViewEntity.posX;
         float f = (float)renderViewEntity.posY + renderViewEntity.getEyeHeight();
         float posZ = (float)renderViewEntity.posZ;
         ChunkCompileTaskGenerator.Type type = generator.getType();
         if (type == ChunkCompileTaskGenerator.Type.REBUILD_CHUNK) {
            generator.getRenderChunk().rebuildChunk(posX, f, posZ, generator);
         } else if (type == ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY) {
            generator.getRenderChunk().resortTransparency(posX, f, posZ, generator);
         }

         generator.getLock().lock();

         try {
            if (generator.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING) {
               if (!generator.isFinished()) {
                  LOGGER.warn("Chunk render task was " + generator.getStatus() + " when I expected it to be compiling; aborting task");
               }

               this.freeRenderBuilder(generator);
               return;
            }

            generator.setStatus(ChunkCompileTaskGenerator.Status.UPLOADING);
         } finally {
            generator.getLock().unlock();
         }

         final CompiledChunk compiledChunk = generator.getCompiledChunk();
         ArrayList newArrayList = Lists.newArrayList();
         if (type == ChunkCompileTaskGenerator.Type.REBUILD_CHUNK) {
            for (EnumWorldBlockLayer enumWorldBlockLayer : EnumWorldBlockLayer.values()) {
               if (compiledChunk.isLayerStarted(enumWorldBlockLayer)) {
                  newArrayList.add(
                     this.chunkRenderDispatcher
                        .uploadChunk(enumWorldBlockLayer, generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(enumWorldBlockLayer), generator.getRenderChunk(), compiledChunk)
                  );
               }
            }
         } else if (type == ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY) {
            newArrayList.add(
               this.chunkRenderDispatcher
                  .uploadChunk(
                     EnumWorldBlockLayer.TRANSLUCENT,
                     generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(EnumWorldBlockLayer.TRANSLUCENT),
                     generator.getRenderChunk(),
                     compiledChunk
                  )
            );
         }

         final ListenableFuture allAsList = Futures.allAsList(newArrayList);
         generator.addFinishRunnable(new Runnable() {
            @Override
            public void run() {
               allAsList.cancel(false);
            }
         });
         Futures.addCallback(allAsList, new FutureCallback<List<Object>>() {
            public void onSuccess(List<Object> list) {
               ChunkRenderWorker.this.freeRenderBuilder(generator);
               generator.getLock().lock();

               try {
                  if (generator.getStatus() != ChunkCompileTaskGenerator.Status.UPLOADING) {
                     if (!generator.isFinished()) {
                        ChunkRenderWorker.LOGGER.warn("Chunk render task was " + generator.getStatus() + " when I expected it to be uploading; aborting task");
                     }

                     return;
                  }

                  generator.setStatus(ChunkCompileTaskGenerator.Status.DONE);
               } finally {
                  generator.getLock().unlock();
               }

               generator.getRenderChunk().setCompiledChunk(compiledChunk);
            }

            public void onFailure(Throwable throwable) {
               ChunkRenderWorker.this.freeRenderBuilder(generator);
               if (!(throwable instanceof CancellationException) && !(throwable instanceof InterruptedException)) {
                  Minecraft.getMinecraft().crashed(CrashReport.makeCrashReport(throwable, "Rendering chunk"));
               }
            }
         }, listeningExecutorService);
      }
   }

   public RegionRenderCacheBuilder getRegionRenderCacheBuilder() throws InterruptedException {
      return this.regionRenderCacheBuilder != null ? this.regionRenderCacheBuilder : this.chunkRenderDispatcher.allocateRenderBuilder();
   }

   public void freeRenderBuilder(ChunkCompileTaskGenerator taskGenerator) {
      if (this.regionRenderCacheBuilder == null) {
         this.chunkRenderDispatcher.freeRenderBuilder(taskGenerator.getRegionRenderCacheBuilder());
      }
   }
}
