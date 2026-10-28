package net.optifine;

import java.util.Map;

public class FileUploadThread extends Thread {
    public String urlString;
    public Map headers;
    public byte[] content;
    public IFileUploadListener listener;

    public FileUploadThread(String str, Map map, byte[] bytes, IFileUploadListener fileUploadListener) {
        this.urlString = str;
        this.headers = map;
        this.content = bytes;
        this.listener = fileUploadListener;
    }

    @Override
    public void run() {
        try {
            HttpUtils.post(this.urlString, this.headers, this.content);
            this.listener.fileUploadFinished(this.urlString, this.content, (Throwable)null);
        } catch (Exception e) {
            this.listener.fileUploadFinished(this.urlString, this.content, e);
        }
    }

    public String getUrlString() {
        return this.urlString;
    }

    public byte[] getContent() {
        return this.content;
    }

    public IFileUploadListener getListener() {
        return this.listener;
    }
}
