package net.minecraft.client.renderer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.http.HttpPipeline;
import net.optifine.http.HttpRequest;
import net.optifine.http.HttpResponse;
import net.optifine.player.CapeImageBuffer;
import net.optifine.shaders.ShadersTex;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadDownloadImageData extends SimpleTexture {
   public static Logger logger = LogManager.getLogger();
   public static AtomicInteger threadDownloadCounter = new AtomicInteger(0);
   public Boolean imageFound = null;
   public boolean pipeline = false;
   public File cacheFile;
   public BufferedImage bufferedImage;
   public Thread imageThread;
   public boolean textureUploaded;
   public String imageUrl;
   public IImageBuffer imageBuffer;

   public ThreadDownloadImageData(File cacheFileIn, String imageUrlIn, ResourceLocation textureResourceLocation, IImageBuffer imageBufferIn) {
      super(textureResourceLocation);
      this.cacheFile = cacheFileIn;
      this.imageUrl = imageUrlIn;
      this.imageBuffer = imageBufferIn;
   }

   public void checkTextureUploaded() {
      if (!this.textureUploaded && this.bufferedImage != null) {
         this.textureUploaded = true;
         if (this.textureLocation != null) {
            this.deleteGlTexture();
         }

         if (Config.isShaders()) {
            ShadersTex.loadSimpleTexture(
               super.getGlTextureId(), this.bufferedImage, false, false, Config.getResourceManager(), this.textureLocation, this.getMultiTexID()
            );
         } else {
            TextureUtil.uploadTextureImage(super.getGlTextureId(), this.bufferedImage);
         }
      }
   }

   @Override
   public int getGlTextureId() {
      this.checkTextureUploaded();
      return super.getGlTextureId();
   }

   public void setBufferedImage(BufferedImage bufferedImageIn) {
      this.bufferedImage = bufferedImageIn;
      if (this.imageBuffer != null) {
         this.imageBuffer.skinAvailable();
      }

      this.imageFound = this.bufferedImage != null;
   }

   @Override
   public void loadTexture(IResourceManager resourceManager) throws IOException {
      if (this.bufferedImage == null && this.textureLocation != null) {
         super.loadTexture(resourceManager);
      }

      if (this.imageThread == null) {
         if (this.cacheFile != null && this.cacheFile.isFile()) {
            logger.debug("Loading http texture from local cache ({})", new Object[]{this.cacheFile});

            try {
               this.bufferedImage = ImageIO.read(this.cacheFile);
               if (this.imageBuffer != null) {
                  this.setBufferedImage(this.imageBuffer.parseUserSkin(this.bufferedImage));
               }

               this.loadingFinished();
            } catch (IOException e) {
               logger.error("Couldn't load skin " + this.cacheFile, e);
               this.loadTextureFromServer();
            }
         } else {
            this.loadTextureFromServer();
         }
      }
   }

   public void loadTextureFromServer() {
      this.imageThread = new Thread("Texture Downloader #" + threadDownloadCounter.incrementAndGet()) {
         @Override
         public void run() {
            HttpURLConnection httpURLConnection = null;
            ThreadDownloadImageData.logger
               .debug("Downloading http texture from {} to {}", new Object[]{ThreadDownloadImageData.this.imageUrl, ThreadDownloadImageData.this.cacheFile});
            if (ThreadDownloadImageData.this.shouldPipeline()) {
               ThreadDownloadImageData.this.loadPipelined();
            } else {
               try {
                  httpURLConnection = (HttpURLConnection)new URL(ThreadDownloadImageData.this.imageUrl).openConnection(Minecraft.getMinecraft().getProxy());
                  httpURLConnection.setDoInput(true);
                  httpURLConnection.setDoOutput(false);
                  httpURLConnection.connect();
                  if (httpURLConnection.getResponseCode() / 100 == 2) {
                     BufferedImage bufferedImage;
                     if (ThreadDownloadImageData.this.cacheFile != null) {
                        FileUtils.copyInputStreamToFile(httpURLConnection.getInputStream(), ThreadDownloadImageData.this.cacheFile);
                        bufferedImage = ImageIO.read(ThreadDownloadImageData.this.cacheFile);
                     } else {
                        bufferedImage = TextureUtil.readBufferedImage(httpURLConnection.getInputStream());
                     }

                     if (ThreadDownloadImageData.this.imageBuffer != null) {
                        bufferedImage = ThreadDownloadImageData.this.imageBuffer.parseUserSkin(bufferedImage);
                     }

                     ThreadDownloadImageData.this.setBufferedImage(bufferedImage);
                     return;
                  }

                  if (httpURLConnection.getErrorStream() != null) {
                     Config.readAll(httpURLConnection.getErrorStream());
                  }

                  return;
               } catch (Exception e) {
                  ThreadDownloadImageData.logger.error("Couldn't download http texture: " + e.getClass().getName() + ": " + e.getMessage());
               } finally {
                  if (httpURLConnection != null) {
                     httpURLConnection.disconnect();
                  }

                  ThreadDownloadImageData.this.loadingFinished();
               }
            }
         }
      };
      this.imageThread.setDaemon(true);
      this.imageThread.start();
   }

   public boolean shouldPipeline() {
      if (!this.pipeline) {
         return false;
      }

      Proxy proxy = Minecraft.getMinecraft().getProxy();
      return proxy.type() != Type.DIRECT && proxy.type() != Type.SOCKS ? false : this.imageUrl.startsWith("http://");
   }

   public void loadPipelined() {
      try {
         HttpRequest makeRequest = HttpPipeline.makeRequest(this.imageUrl, Minecraft.getMinecraft().getProxy());
         HttpResponse executeRequest = HttpPipeline.executeRequest(makeRequest);
         if (executeRequest.getStatus() / 100 == 2) {
            byte[] body = executeRequest.getBody();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
            BufferedImage bufferedImage;
            if (this.cacheFile != null) {
               FileUtils.copyInputStreamToFile(byteArrayInputStream, this.cacheFile);
               bufferedImage = ImageIO.read(this.cacheFile);
            } else {
               bufferedImage = TextureUtil.readBufferedImage(byteArrayInputStream);
            }

            if (this.imageBuffer != null) {
               bufferedImage = this.imageBuffer.parseUserSkin(bufferedImage);
            }

            this.setBufferedImage(bufferedImage);
            return;
         }
      } catch (Exception e) {
         logger.error("Couldn't download http texture: " + e.getClass().getName() + ": " + e.getMessage());
         return;
      } finally {
         this.loadingFinished();
      }
   }

   public void loadingFinished() {
      this.imageFound = this.bufferedImage != null;
      if (this.imageBuffer instanceof CapeImageBuffer) {
         CapeImageBuffer imageBuffer = (CapeImageBuffer)this.imageBuffer;
         imageBuffer.cleanup();
      }
   }

   public IImageBuffer getImageBuffer() {
      return this.imageBuffer;
   }
}
