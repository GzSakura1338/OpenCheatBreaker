package net.optifine;

import net.minecraft.client.Minecraft;

public class FileDownloadThread extends Thread {
    public String urlString = null;
    public IFileDownloadListener listener = null;

    public FileDownloadThread(String str, IFileDownloadListener fileDownloadListener) {
        this.urlString = str;
        this.listener = fileDownloadListener;
    }

    @Override
    public void run() {
        try {
            byte[] bytes = HttpPipeline.get(this.urlString, Minecraft.getMinecraft().getProxy());
            this.listener.fileDownloadFinished(this.urlString, bytes, (Throwable)null);
        } catch (Exception e) {
            this.listener.fileDownloadFinished(this.urlString, (byte[])null, e);
        }
    }

    public String getUrlString() {
        return this.urlString;
    }

    public IFileDownloadListener getListener() {
        return this.listener;
    }
}
