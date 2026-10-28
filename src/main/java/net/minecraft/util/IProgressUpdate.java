package net.minecraft.util;

public interface IProgressUpdate {
    void displaySavingString(String str);

    void resetProgressAndMessage(String str);

    void displayLoadingString(String str) throws java.io.IOException;

    void setLoadingProgress(int n) throws java.io.IOException;

    void setDoneWorking();
}
