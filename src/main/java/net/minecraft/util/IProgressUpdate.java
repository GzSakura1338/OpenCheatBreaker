package net.minecraft.util;

public interface IProgressUpdate {
   void displaySavingString(String str);

   void resetProgressAndMessage(String str);

   void displayLoadingString(String str);

   void setLoadingProgress(int n);

   void setDoneWorking();
}
