package net.optifine;

public interface IFileUploadListener {
    void fileUploadFinished(String str, byte[] bytes, Throwable throwable);
}
