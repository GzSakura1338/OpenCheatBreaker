package net.optifine.shaders.config;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import net.optifine.Config;
import net.optifine.shaders.Shaders;

public class MacroProcessor {
   public static InputStream process(InputStream inputStream, String str) throws IOException {
      String readInputStream = Config.readInputStream(inputStream, "ASCII");
      String macroHeader = getMacroHeader(readInputStream);
      if (!macroHeader.isEmpty()) {
         readInputStream = macroHeader + readInputStream;
         if (Shaders.saveFinalShaders) {
            String str2 = str.replace(':', '/') + ".pre";
            Shaders.saveShader(str2, readInputStream);
         }

         readInputStream = process(readInputStream);
      }

      if (Shaders.saveFinalShaders) {
         String replace = str.replace(':', '/');
         Shaders.saveShader(replace, readInputStream);
      }

      byte[] bytes = readInputStream.getBytes("ASCII");
      return new ByteArrayInputStream(bytes);
   }

   public static String process(String str) throws IOException {
      StringReader stringReader = new StringReader(str);
      BufferedReader bufferedReader = new BufferedReader(stringReader);
      MacroState macroState = new MacroState();
      StringBuilder sb = new StringBuilder();

      while (true) {
         String readLine = bufferedReader.readLine();
         if (readLine == null) {
            return sb.toString();
         }

         if (macroState.processLine(readLine) && !MacroState.isMacroLine(readLine)) {
            sb.append(readLine);
            sb.append("\n");
         }
      }
   }

   public static String getMacroHeader(String str) throws IOException {
      StringBuilder sb = new StringBuilder();
      Object obj = null;
      ArrayList list = null;
      StringReader stringReader = new StringReader(str);
      BufferedReader bufferedReader = new BufferedReader(stringReader);

      while (true) {
         String readLine = bufferedReader.readLine();
         if (readLine == null) {
            return sb.toString();
         }

         if (MacroState.isMacroLine(readLine)) {
            if (sb.length() == 0) {
               sb.append(ShaderMacros.getFixedMacroLines());
            }

            if (list == null) {
               list = new ArrayList<>(Arrays.asList(ShaderMacros.getExtensions()));
            }

            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
               ShaderMacro shaderMacro = (ShaderMacro)iterator.next();
               if (readLine.contains(shaderMacro.getName())) {
                  sb.append(shaderMacro.getSourceLine());
                  sb.append("\n");
                  iterator.remove();
               }
            }
         }
      }
   }

   public static List<ShaderOption> getMacroOptions() {
      ArrayList list = new ArrayList();
      ShaderOption[] shaderPackOptions = Shaders.getShaderPackOptions();

      for (int i = 0; i < shaderPackOptions.length; i++) {
         ShaderOption shaderOption = shaderPackOptions[i];
         String sourceLine = shaderOption.getSourceLine();
         if (sourceLine != null && sourceLine.startsWith("#")) {
            list.add(shaderOption);
         }
      }

      return list;
   }
}
