package net.optifine;

public interface IFileDownloadListener {
    void fileDownloadFinished(String str, byte[] bytes, Throwable throwable);
}
