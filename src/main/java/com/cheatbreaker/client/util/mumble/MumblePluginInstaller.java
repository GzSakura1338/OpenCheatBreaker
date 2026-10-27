package com.cheatbreaker.client.util.mumble;

import com.cheatbreaker.client.ui.notification.Notification;
import com.cheatbreaker.client.util.Logging;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

public class MumblePluginInstaller {
   public static final String TEXT_MUMBLE_LINK = "Mumble Link";
   public static ResourceLocation cheatbreakerTalkingMumblePlugin = new ResourceLocation("client/mumble/cheatbreaker_talking.mumble_plugin");

   public static Path getPath() {
      String toLowerCase = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
      Path path = Paths.get(System.getProperty("user.home", "."));
      Path path2;
      String str;
      if (toLowerCase.contains("win")) {
         String getenv = System.getenv("APPDATA");
         path2 = getenv != null && !getenv.isEmpty() ? Paths.get(getenv) : path.resolve("AppData").resolve("Roaming");
         str = "cheatbreaker_talking.dll";
      } else if (toLowerCase.contains("mac")) {
         path2 = path.resolve("Library").resolve("Application Support");
         str = "cheatbreaker_talking.dylib";
      } else {
         String getenv = System.getenv("XDG_DATA_HOME");
         path2 = getenv != null && !getenv.isEmpty() ? Paths.get(getenv) : path.resolve(".local").resolve("share");
         str = "cheatbreaker_talking.so";
      }

      return path2.resolve("Mumble").resolve("Mumble").resolve("Plugins").resolve(str);
   }

   public static void run() {
      byte[] bytes;
      try (InputStream inputStream = Minecraft.getMinecraft().getResourceManager().getResource(cheatbreakerTalkingMumblePlugin).getInputStream()) {
         bytes = IOUtils.toByteArray(inputStream);
      } catch (IOException e) {
         Logging.warnTagged("Mumble Link", "The bundled Mumble plugin could not be read: %s", e.toString());
         Notification.post("Mumble Link", "The Mumble plugin is missing from this build.");
         return;
      }

      Path path;
      try {
         Path createTempDirectory = Files.createTempDirectory("cheatbreaker-mumble");
         path = createTempDirectory.resolve("cheatbreaker_talking.mumble_plugin");
         Files.write(path, bytes);
         createTempDirectory.toFile().deleteOnExit();
         path.toFile().deleteOnExit();
      } catch (IOException e) {
         Logging.warnTagged("Mumble Link", "The Mumble plugin could not be unpacked: %s", e.toString());
         Notification.post("Mumble Link", "Could not unpack the Mumble plugin. See the log for details.");
         return;
      }

      boolean exists = Files.exists(getPath());
      Path path2 = exists ? path.getParent() : path;

      try {
         if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Action.OPEN)) {
            throw new UnsupportedOperationException("opening files is not supported on this system");
         }

         Desktop.getDesktop().open(path2.toFile());
         Notification.post(
            "Mumble Link",
            exists
               ? "Already installed. To update, use Install plugin in Mumble's Plugins settings."
               : "Confirm in Mumble, then tick it under Configure > Plugins."
         );
      } catch (IOException | RuntimeException e) {
         Logging.warnTagged(
            "Mumble Link",
            "Could not open %s (%s). %s",
            path2,
            e.toString(),
            exists
               ? "Choose the .mumble_plugin file in it with Install plugin in Mumble's Plugins settings."
               : "Open the .mumble_plugin file with Mumble to install it."
         );
         Notification.post("Mumble Link", "Could not open the plugin. See the log for its location.");
      }
   }
}
