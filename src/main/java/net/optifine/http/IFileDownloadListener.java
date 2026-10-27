package net.optifine.http;

public interface IFileDownloadListener {
   void fileDownloadFinished(String str, byte[] bytes, Throwable throwable);
}
