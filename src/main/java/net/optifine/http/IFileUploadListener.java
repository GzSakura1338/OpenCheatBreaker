package net.optifine.http;

public interface IFileUploadListener {
   void fileUploadFinished(String str, byte[] bytes, Throwable throwable);
}
