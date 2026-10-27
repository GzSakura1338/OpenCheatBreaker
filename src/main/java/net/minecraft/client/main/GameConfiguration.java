package net.minecraft.client.main;

import com.mojang.authlib.properties.PropertyMap;
import java.io.File;
import java.net.Proxy;
import net.minecraft.util.Session;

public class GameConfiguration {
   public GameConfiguration.UserInformation userInfo;
   public GameConfiguration.DisplayInformation displayInfo;
   public GameConfiguration.FolderInformation folderInfo;
   public GameConfiguration.GameInformation gameInfo;
   public GameConfiguration.ServerInformation serverInfo;

   public GameConfiguration(
      GameConfiguration.UserInformation userInfoIn,
      GameConfiguration.DisplayInformation displayInfoIn,
      GameConfiguration.FolderInformation folderInfoIn,
      GameConfiguration.GameInformation gameInfoIn,
      GameConfiguration.ServerInformation serverInfoIn
   ) {
      this.userInfo = userInfoIn;
      this.displayInfo = displayInfoIn;
      this.folderInfo = folderInfoIn;
      this.gameInfo = gameInfoIn;
      this.serverInfo = serverInfoIn;
   }

   public static class DisplayInformation {
      public int width;
      public int height;
      public boolean fullscreen;
      public boolean checkGlErrors;

      public DisplayInformation(int widthIn, int heightIn, boolean fullscreenIn, boolean checkGlErrorsIn) {
         this.width = widthIn;
         this.height = heightIn;
         this.fullscreen = fullscreenIn;
         this.checkGlErrors = checkGlErrorsIn;
      }
   }

   public static class FolderInformation {
      public File mcDataDir;
      public File resourcePacksDir;
      public File assetsDir;
      public String assetIndex;

      public FolderInformation(File mcDataDirIn, File resourcePacksDirIn, File assetsDirIn, String assetIndexIn) {
         this.mcDataDir = mcDataDirIn;
         this.resourcePacksDir = resourcePacksDirIn;
         this.assetsDir = assetsDirIn;
         this.assetIndex = assetIndexIn;
      }
   }

   public static class GameInformation {
      public boolean isDemo;
      public String version;

      public GameInformation(boolean isDemoIn, String versionIn) {
         this.isDemo = isDemoIn;
         this.version = versionIn;
      }
   }

   public static class ServerInformation {
      public String serverName;
      public int serverPort;

      public ServerInformation(String serverNameIn, int serverPortIn) {
         this.serverName = serverNameIn;
         this.serverPort = serverPortIn;
      }
   }

   public static class UserInformation {
      public Session session;
      public PropertyMap userProperties;
      public PropertyMap profileProperties;
      public Proxy proxy;

      public UserInformation(Session sessionIn, PropertyMap userPropertiesIn, PropertyMap profilePropertiesIn, Proxy proxyIn) {
         this.session = sessionIn;
         this.userProperties = userPropertiesIn;
         this.profileProperties = profilePropertiesIn;
         this.proxy = proxyIn;
      }
   }
}
