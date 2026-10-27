package com.cheatbreaker.client.util;

import java.util.HashMap;

public class CrashCauseHints extends HashMap<String, String> {
   public CrashCauseHints() {
      this.put("OutOfMemoryError", "Your client has ran out of memory.\nYou can increase memory allocation in the launcher.");
      this.put(
         "No OpenGL context found in the current thread",
         "Your operating system has an unstable component.\n\nPlease try:\n- Enabling Borderless Fullscreen in CheatBreaker Settings\n- Updating or reinstalling your drivers through a clean install (if on Windows, use Display Driver Uninstaller in Windows' Safe Mode)\n- Sticking to your monitor's native resolution (if on Windows, you can see the recommended resolution. If you own an NVIDIA graphics card, look for the resolution that displays (native) in the NVIDIA Control Panel)\n- Removing or down-clocking any overclocked monitors\n- Removing or down-clocking your graphics card"
      );
      this.put(
         "must be created before you can read events",
         "If you crashed while attempting to fullscreen, please enable Borderless Fullscreen in CheatBreaker Settings before attempting to fullscreen again. Otherwise, please update your graphics drivers."
      );
      this.put(
         "ZipException",
         "An error occurred while trying to read from or write to a ZIP file.\nPlease ensure:\n- Your Singleplayer world files are not corrupt\n- Your resource pack files are not corrupt\n- Your hard drive/SSD is not sending bad sectors (you will need to research third party tools for assistance)"
      );
      this.put(
         "FileNotFoundException",
         "Your client tried to access a file that does not exist.\nThis could be due to a typo in the filename or the file being deleted."
      );
   }
}
