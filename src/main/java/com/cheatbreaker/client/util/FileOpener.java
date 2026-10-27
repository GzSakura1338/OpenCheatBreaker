package com.cheatbreaker.client.util;

import com.cheatbreaker.client.CheatBreaker;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import net.minecraft.util.Util;
import org.lwjgl.Sys;

public class FileOpener {
   public static void handleFile(File file) throws IOException {
      if (Desktop.isDesktopSupported()) {
         Desktop.getDesktop().open(file);
      } else {
         String absolutePath = file.getAbsolutePath();

         try {
            switch (Util.getOSType()) {
               case OSX:
                  Runtime.getRuntime().exec(new String[]{"/usr/bin/open", absolutePath});
                  break;
               case WINDOWS:
                  Runtime.getRuntime().exec(String.format("cmd.exe /C start \"Open file\" \"%s\"", absolutePath));
                  break;
               default:
                  Sys.openURL("file://" + absolutePath);
            }
         } catch (IOException e) {
            CheatBreaker.getInstance().getLogger().error("Couldn't open file", e);
         }
      }
   }
}
