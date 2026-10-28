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
import net.optifine.CapeImageBuffer;
import net.optifine.Config;
import net.optifine.HttpPipeline;
import net.optifine.HttpRequest;
import net.optifine.HttpResponse;
import net.optifine.TextureUtils;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThreadDownloadImageData extends SimpleTexture {
    public static Logger logger = LogManager.getLogger();
    public static AtomicInteger threadDownloadCounter = new AtomicInteger(0);
    public Boolean imageFound = null;
    public boolean pipeline = false;
    public File file;
    public BufferedImage bufferedImage;
    public Thread imageThread;
    public boolean textureUploaded;
    public String imageUrl;
    public IImageBuffer imageBuffer;
    public static Runtime runtime = Runtime.getRuntime();

    public ThreadDownloadImageData(File file, String str, ResourceLocation resourceLocation, IImageBuffer imageBuffer) {
        super(resourceLocation);
        this.file = file;
        this.imageUrl = str;
        this.imageBuffer = imageBuffer;
    }

    public void checkTextureUploaded() {
        if (!this.textureUploaded && this.bufferedImage != null) {
            this.textureUploaded = true;
            if (this.textureLocation != null) {
                this.deleteGlTexture();
            }

            TextureUtil.uploadTextureImage(super.getGlTextureId(), this.bufferedImage);
        }
    }

    @Override
    public int getGlTextureId() {
        this.checkTextureUploaded();
        return super.getGlTextureId();
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
        if (this.imageBuffer != null) {
            this.imageBuffer.run();
        }

        this.imageFound = this.bufferedImage != null;
    }

    @Override
    public void loadTexture(IResourceManager resourceManager) throws IOException {
        if (this.bufferedImage == null && this.textureLocation != null) {
            super.loadTexture(resourceManager);
        }

        if (this.imageThread == null) {
            if (this.file != null && this.file.isFile()) {
                logger.debug("Loading http texture from local cache ({})", new Object[]{this.file});

                try {
                    this.bufferedImage = ImageIO.read(this.file);
                    if (this.imageBuffer != null) {
                        this.setBufferedImage(this.imageBuffer.parseUserSkin(this.bufferedImage));
                    }

                    this.loadingFinished();
                } catch (IOException e) {
                    logger.error("Couldn't load skin " + this.file, e);
                    this.run();
                }
            } else {
                this.run();
            }
        }
    }

    public void run() {
        this.imageThread = new Thread("Texture Downloader #" + threadDownloadCounter.incrementAndGet()) {
            @Override
            public void run() {
                HttpURLConnection httpURLConnection = null;
                ThreadDownloadImageData.logger
                    .debug("Downloading http texture from {} to {}", new Object[]{ThreadDownloadImageData.this.imageUrl, ThreadDownloadImageData.this.file});
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
                            if (ThreadDownloadImageData.this.file != null) {
                                FileUtils.copyInputStreamToFile(httpURLConnection.getInputStream(), ThreadDownloadImageData.this.file);
                                bufferedImage = ImageIO.read(ThreadDownloadImageData.this.file);
                            } else {
                                bufferedImage = ImageIO.read(httpURLConnection.getInputStream());
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
                    } catch (Exception e) {
                        ThreadDownloadImageData.logger.error("Couldn't download http texture: " + e.getClass().getName() + ": " + e.getMessage());
                        return;
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
                if (this.file != null) {
                    FileUtils.copyInputStreamToFile(byteArrayInputStream, this.file);
                    bufferedImage = ImageIO.read(this.file);
                } else {
                    bufferedImage = TextureUtils.readBufferedImage(byteArrayInputStream);
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
}
