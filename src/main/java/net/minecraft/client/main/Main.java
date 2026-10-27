package net.minecraft.client.main;

import com.cheatbreaker.client.CheatBreaker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.authlib.properties.PropertyMap.Serializer;
import java.io.File;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.util.List;
import joptsimple.ArgumentAcceptingOptionSpec;
import joptsimple.NonOptionArgumentSpec;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class Main {
   public static void main(String[] strs) {
      System.setProperty("java.net.preferIPv4Stack", "true");
      OptionParser optionParser = new OptionParser();
      optionParser.allowsUnrecognizedOptions();
      optionParser.accepts("demo");
      optionParser.accepts("fullscreen");
      optionParser.accepts("checkGlErrors");
      ArgumentAcceptingOptionSpec withRequiredArg = optionParser.accepts("server").withRequiredArg();
      ArgumentAcceptingOptionSpec defaultsTo = optionParser.accepts("port").withRequiredArg().ofType(Integer.class).defaultsTo(25565, new Integer[0]);
      ArgumentAcceptingOptionSpec defaultsTo2 = optionParser.accepts("gameDir").withRequiredArg().ofType(File.class).defaultsTo(new File("."), new File[0]);
      ArgumentAcceptingOptionSpec ofType = optionParser.accepts("assetsDir").withRequiredArg().ofType(File.class);
      ArgumentAcceptingOptionSpec ofType2 = optionParser.accepts("resourcePackDir").withRequiredArg().ofType(File.class);
      ArgumentAcceptingOptionSpec withRequiredArg2 = optionParser.accepts("proxyHost").withRequiredArg();
      ArgumentAcceptingOptionSpec ofType3 = optionParser.accepts("proxyPort").withRequiredArg().defaultsTo("8080", new String[0]).ofType(Integer.class);
      ArgumentAcceptingOptionSpec withRequiredArg3 = optionParser.accepts("proxyUser").withRequiredArg();
      ArgumentAcceptingOptionSpec withRequiredArg4 = optionParser.accepts("proxyPass").withRequiredArg();
      ArgumentAcceptingOptionSpec defaultsTo3 = optionParser.accepts("username").withRequiredArg().defaultsTo("Player" + Minecraft.getSystemTime() % 1000L, new String[0]);
      ArgumentAcceptingOptionSpec withRequiredArg5 = optionParser.accepts("uuid").withRequiredArg();
      ArgumentAcceptingOptionSpec required = optionParser.accepts("accessToken").withRequiredArg().required();
      ArgumentAcceptingOptionSpec required2 = optionParser.accepts("version").withRequiredArg().required();
      ArgumentAcceptingOptionSpec defaultsTo4 = optionParser.accepts("width").withRequiredArg().ofType(Integer.class).defaultsTo(854, new Integer[0]);
      ArgumentAcceptingOptionSpec defaultsTo5 = optionParser.accepts("height").withRequiredArg().ofType(Integer.class).defaultsTo(480, new Integer[0]);
      ArgumentAcceptingOptionSpec defaultsTo6 = optionParser.accepts("userProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      ArgumentAcceptingOptionSpec defaultsTo7 = optionParser.accepts("profileProperties").withRequiredArg().defaultsTo("{}", new String[0]);
      ArgumentAcceptingOptionSpec withRequiredArg6 = optionParser.accepts("assetIndex").withRequiredArg();
      ArgumentAcceptingOptionSpec defaultsTo8 = optionParser.accepts("userType").withRequiredArg().defaultsTo("legacy", new String[0]);
      ArgumentAcceptingOptionSpec withRequiredArg7 = optionParser.accepts("installationID").withRequiredArg();
      ArgumentAcceptingOptionSpec withRequiredArg8 = optionParser.accepts("websocketToken").withRequiredArg();
      ArgumentAcceptingOptionSpec withRequiredArg9 = optionParser.accepts("launcherVersion").withRequiredArg();
      NonOptionArgumentSpec nonOptions = optionParser.nonOptions();
      OptionSet parse = optionParser.parse(strs);
      List valuesOf = parse.valuesOf(nonOptions);
      if (!valuesOf.isEmpty()) {
         System.out.println("Completely ignored arguments: " + valuesOf);
      }

      String str = (String)parse.valueOf(withRequiredArg2);
      Proxy proxy = Proxy.NO_PROXY;
      if (str != null) {
         try {
            proxy = new Proxy(Type.SOCKS, new InetSocketAddress(str, (Integer)parse.valueOf(ofType3)));
         } catch (Exception e) {
         }
      }

      final String str2 = (String)parse.valueOf(withRequiredArg3);
      final String str3 = (String)parse.valueOf(withRequiredArg4);
      if (!proxy.equals(Proxy.NO_PROXY) && isNullOrEmpty(str2) && isNullOrEmpty(str3)) {
         Authenticator.setDefault(new Authenticator() {
            @Override
            public PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(str2, str3.toCharArray());
            }
         });
      }

      int n = (Integer)parse.valueOf(defaultsTo4);
      int n2 = (Integer)parse.valueOf(defaultsTo5);
      boolean has = parse.has("fullscreen");
      boolean has2 = parse.has("checkGlErrors");
      boolean has3 = parse.has("demo");
      CheatBreaker.installationId = ((String)parse.valueOf(withRequiredArg7)).toString();
      CheatBreaker.websocketToken = ((String)parse.valueOf(withRequiredArg8)).toString();
      CheatBreaker.launcherVersion = ((String)parse.valueOf(withRequiredArg9)).toString();
      String str4 = (String)parse.valueOf(required2);
      Gson gson = new GsonBuilder().registerTypeAdapter(PropertyMap.class, new Serializer()).create();
      PropertyMap fromJson = (PropertyMap)gson.fromJson((String)parse.valueOf(defaultsTo6), PropertyMap.class);
      PropertyMap fromJson2 = (PropertyMap)gson.fromJson((String)parse.valueOf(defaultsTo7), PropertyMap.class);
      File file = (File)parse.valueOf(defaultsTo2);
      File file2 = parse.has(ofType) ? (File)parse.valueOf(ofType) : new File(file, "assets/");
      File file3 = parse.has(ofType2) ? (File)parse.valueOf(ofType2) : new File(file, "resourcepacks/");
      String str5 = parse.has(withRequiredArg5) ? (String)withRequiredArg5.value(parse) : (String)defaultsTo3.value(parse);
      String str6 = parse.has(withRequiredArg6) ? (String)withRequiredArg6.value(parse) : null;
      String str7 = (String)parse.valueOf(withRequiredArg);
      Integer integer = (Integer)parse.valueOf(defaultsTo);
      Session session = new Session((String)defaultsTo3.value(parse), str5, (String)required.value(parse), (String)defaultsTo8.value(parse));
      GameConfiguration gameConfiguration = new GameConfiguration(
         new GameConfiguration.UserInformation(session, fromJson, fromJson2, proxy),
         new GameConfiguration.DisplayInformation(n, n2, has, has2),
         new GameConfiguration.FolderInformation(file, file3, file2, str6),
         new GameConfiguration.GameInformation(has3, str4),
         new GameConfiguration.ServerInformation(str7, integer)
      );
      Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") {
         @Override
         public void run() {
            Minecraft.stopIntegratedServer();
         }
      });
      Thread.currentThread().setName("Client thread");
      new Minecraft(gameConfiguration).run();
   }

   public static boolean isNullOrEmpty(String str) {
      return str != null && !str.isEmpty();
   }
}
