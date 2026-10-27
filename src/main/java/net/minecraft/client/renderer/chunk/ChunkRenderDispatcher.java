package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.VertexBufferUploader;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class ChunkRenderDispatcher {
   public static Logger logger = LogManager.getLogger();
   public static ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("Chunk Batcher %d").setDaemon(true).build();
   public List<ChunkRenderWorker> listThreadedWorkers = Lists.newArrayList();
   public BlockingQueue<ChunkCompileTaskGenerator> queueChunkUpdates = Queues.newArrayBlockingQueue(100);
   public WorldVertexBufferUploader worldVertexUploader = new WorldVertexBufferUploader();
   public VertexBufferUploader vertexUploader = new VertexBufferUploader();
   public Queue<ListenableFutureTask<?>> queueChunkUploads = Queues.newArrayDeque();
   public List<RegionRenderCacheBuilder> listPausedBuilders = new ArrayList<>();
   public int countRenderBuilders;
   public BlockingQueue<RegionRenderCacheBuilder> queueFreeRenderBuilders;
   public ChunkRenderWorker renderWorker;

   public ChunkRenderDispatcher() {
      this(-1);
   }

   public ChunkRenderDispatcher(int n) {
      int max = Math.max(1, (int)(Runtime.getRuntime().maxMemory() * 0.3) / 10485760);
      int max2 = Math.max(1, MathHelper.clamp_int(Runtime.getRuntime().availableProcessors() - 2, 1, max / 5));
      if (n < 0) {
         this.countRenderBuilders = MathHelper.clamp_int(max2 * 8, 1, max);
      } else {
         this.countRenderBuilders = n;
      }

      for (int i = 0; i < max2; i++) {
         ChunkRenderWorker chunkRenderWorker = new ChunkRenderWorker(this);
         Thread newThread = threadFactory.newThread(chunkRenderWorker);
         newThread.start();
         this.listThreadedWorkers.add(chunkRenderWorker);
      }

      this.queueFreeRenderBuilders = Queues.newArrayBlockingQueue(this.countRenderBuilders);

      for (int i = 0; i < this.countRenderBuilders; i++) {
         this.queueFreeRenderBuilders.add(new RegionRenderCacheBuilder());
      }

      this.renderWorker = new ChunkRenderWorker(this, new RegionRenderCacheBuilder());
   }

   public String getDebugInfo() {
      return String.format("pC: %03d, pU: %1d, aB: %1d", this.queueChunkUpdates.size(), this.queueChunkUploads.size(), this.queueFreeRenderBuilders.size());
   }

   public boolean runChunkUploads(long l) {
      boolean flag = false;

      long l2;
      do {
         boolean flag2 = false;
         ListenableFutureTask listenableFutureTask = null;
         synchronized (this.queueChunkUploads) {
            listenableFutureTask = this.queueChunkUploads.poll();
         }

         if (listenableFutureTask != null) {
            listenableFutureTask.run();
            flag2 = true;
            flag = true;
         }

         if (l == 0L || !flag2) {
            break;
         }

         l2 = l - System.nanoTime();
      } while (l2 >= 0L);

      return flag;
   }

   public boolean updateChunkLater(RenderChunk chunkRenderer) {
      chunkRenderer.getLockCompileTask().lock();

      boolean flag;
      try {
         final ChunkCompileTaskGenerator makeCompileTaskChunk = chunkRenderer.makeCompileTaskChunk();
         makeCompileTaskChunk.addFinishRunnable(new Runnable() {
            @Override
            public void run() {
               ChunkRenderDispatcher.this.queueChunkUpdates.remove(makeCompileTaskChunk);
            }
         });
         boolean offer = this.queueChunkUpdates.offer(makeCompileTaskChunk);
         if (!offer) {
            makeCompileTaskChunk.finish();
         }

         flag = offer;
      } finally {
         chunkRenderer.getLockCompileTask().unlock();
      }

      return flag;
   }

   public boolean updateChunkNow(RenderChunk chunkRenderer) {
      chunkRenderer.getLockCompileTask().lock();

      boolean flag;
      try {
         ChunkCompileTaskGenerator makeCompileTaskChunk = chunkRenderer.makeCompileTaskChunk();

         try {
            this.renderWorker.processTask(makeCompileTaskChunk);
         } catch (InterruptedException e) {
         }

         flag = true;
      } finally {
         chunkRenderer.getLockCompileTask().unlock();
      }

      return flag;
   }

   public void stopChunkUpdates() {
      this.clearChunkUpdates();

      while (this.runChunkUploads(0L)) {
      }

      ArrayList newArrayList = Lists.newArrayList();

      while (newArrayList.size() != this.countRenderBuilders) {
         try {
            newArrayList.add(this.allocateRenderBuilder());
         } catch (InterruptedException e) {
         }
      }

      this.queueFreeRenderBuilders.addAll(newArrayList);
   }

   public void freeRenderBuilder(RegionRenderCacheBuilder regionRenderCacheBuilder) {
      this.queueFreeRenderBuilders.add(regionRenderCacheBuilder);
   }

   public RegionRenderCacheBuilder allocateRenderBuilder() throws InterruptedException {
      return this.queueFreeRenderBuilders.take();
   }

   public ChunkCompileTaskGenerator getNextChunkUpdate() throws InterruptedException {
      return this.queueChunkUpdates.take();
   }

   public boolean updateTransparencyLater(RenderChunk chunkRenderer) {
      chunkRenderer.getLockCompileTask().lock();

      boolean flag;
      try {
         final ChunkCompileTaskGenerator makeCompileTaskTransparency = chunkRenderer.makeCompileTaskTransparency();
         if (makeCompileTaskTransparency != null) {
            makeCompileTaskTransparency.addFinishRunnable(new Runnable() {
               @Override
               public void run() {
                  ChunkRenderDispatcher.this.queueChunkUpdates.remove(makeCompileTaskTransparency);
               }
            });
            return this.queueChunkUpdates.offer(makeCompileTaskTransparency);
         }

         boolean flag2 = true;
         flag = flag2;
      } finally {
         chunkRenderer.getLockCompileTask().unlock();
      }

      return flag;
   }

   public ListenableFuture<Object> uploadChunk(
      final EnumWorldBlockLayer player, final WorldRenderer worldRenderer, final RenderChunk chunkRenderer, final CompiledChunk compiledChunkIn
   ) {
      if (Minecraft.getMinecraft().isCallingFromMinecraftThread()) {
         if (OpenGlHelper.useVbo()) {
            this.uploadVertexBuffer(worldRenderer, chunkRenderer.getVertexBufferByLayer(player.ordinal()));
         } else {
            this.uploadDisplayList(worldRenderer, ((ListedRenderChunk)chunkRenderer).getDisplayList(player, compiledChunkIn), chunkRenderer);
         }

         worldRenderer.setTranslation(0.0, 0.0, 0.0);
         return Futures.immediateFuture(null);
      } else {
         ListenableFutureTask listenableFutureTask = ListenableFutureTask.create(new Runnable() {
            @Override
            public void run() {
               ChunkRenderDispatcher.this.uploadChunk(player, worldRenderer, chunkRenderer, compiledChunkIn);
            }
         }, null);
         synchronized (this.queueChunkUploads) {
            this.queueChunkUploads.add(listenableFutureTask);
            return listenableFutureTask;
         }
      }
   }

   public void uploadDisplayList(WorldRenderer worldRenderer, int n, RenderChunk chunkRenderer) {
      GL11.glNewList(n, 4864);
      GlStateManager.pushMatrix();
      chunkRenderer.multModelviewMatrix();
      this.worldVertexUploader.draw(worldRenderer);
      GlStateManager.popMatrix();
      GL11.glEndList();
   }

   public void uploadVertexBuffer(WorldRenderer worldRenderer, VertexBuffer vertexBufferIn) {
      this.vertexUploader.setVertexBuffer(vertexBufferIn);
      this.vertexUploader.draw(worldRenderer);
   }

   public void clearChunkUpdates() {
      while (!this.queueChunkUpdates.isEmpty()) {
         ChunkCompileTaskGenerator poll = this.queueChunkUpdates.poll();
         if (poll != null) {
            poll.finish();
         }
      }
   }

   public boolean hasChunkUpdates() {
      return this.queueChunkUpdates.isEmpty() && this.queueChunkUploads.isEmpty();
   }

   public void pauseChunkUpdates() {
      while (this.listPausedBuilders.size() != this.countRenderBuilders) {
         try {
            this.runChunkUploads(Long.MAX_VALUE);
            RegionRenderCacheBuilder poll = this.queueFreeRenderBuilders.poll(100L, TimeUnit.MILLISECONDS);
            if (poll != null) {
               this.listPausedBuilders.add(poll);
            }
         } catch (InterruptedException e) {
         }
      }
   }

   public void resumeChunkUpdates() {
      this.queueFreeRenderBuilders.addAll(this.listPausedBuilders);
      this.listPausedBuilders.clear();
   }
}
