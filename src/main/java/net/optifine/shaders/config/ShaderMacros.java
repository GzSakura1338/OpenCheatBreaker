package net.optifine.shaders.config;

import net.minecraft.util.Util;
import net.minecraft.util.Util.EnumOS;
import net.optifine.Config;
import net.optifine.shaders.Shaders;

public class ShaderMacros {
   public static String PREFIX_MACRO = "MC_";
   public static final String MC_VERSION = "MC_VERSION";
   public static final String MC_GL_VERSION = "MC_GL_VERSION";
   public static final String MC_GLSL_VERSION = "MC_GLSL_VERSION";
   public static final String MC_OS_WINDOWS = "MC_OS_WINDOWS";
   public static final String MC_OS_MAC = "MC_OS_MAC";
   public static final String MC_OS_LINUX = "MC_OS_LINUX";
   public static final String MC_OS_OTHER = "MC_OS_OTHER";
   public static final String MC_GL_VENDOR_ATI = "MC_GL_VENDOR_ATI";
   public static final String MC_GL_VENDOR_INTEL = "MC_GL_VENDOR_INTEL";
   public static final String MC_GL_VENDOR_NVIDIA = "MC_GL_VENDOR_NVIDIA";
   public static final String MC_GL_VENDOR_XORG = "MC_GL_VENDOR_XORG";
   public static final String MC_GL_VENDOR_OTHER = "MC_GL_VENDOR_OTHER";
   public static final String MC_GL_RENDERER_RADEON = "MC_GL_RENDERER_RADEON";
   public static final String MC_GL_RENDERER_GEFORCE = "MC_GL_RENDERER_GEFORCE";
   public static final String MC_GL_RENDERER_QUADRO = "MC_GL_RENDERER_QUADRO";
   public static final String MC_GL_RENDERER_INTEL = "MC_GL_RENDERER_INTEL";
   public static final String MC_GL_RENDERER_GALLIUM = "MC_GL_RENDERER_GALLIUM";
   public static final String MC_GL_RENDERER_MESA = "MC_GL_RENDERER_MESA";
   public static final String MC_GL_RENDERER_OTHER = "MC_GL_RENDERER_OTHER";
   public static final String MC_FXAA_LEVEL = "MC_FXAA_LEVEL";
   public static final String MC_NORMAL_MAP = "MC_NORMAL_MAP";
   public static final String MC_SPECULAR_MAP = "MC_SPECULAR_MAP";
   public static final String MC_RENDER_QUALITY = "MC_RENDER_QUALITY";
   public static final String MC_SHADOW_QUALITY = "MC_SHADOW_QUALITY";
   public static final String MC_HAND_DEPTH = "MC_HAND_DEPTH";
   public static final String MC_OLD_HAND_LIGHT = "MC_OLD_HAND_LIGHT";
   public static final String MC_OLD_LIGHTING = "MC_OLD_LIGHTING";
   public static ShaderMacro[] extensionMacros;

   public static String getOs() {
      EnumOS oSType = Util.getOSType();
      switch (oSType) {
         case WINDOWS:
            return "MC_OS_WINDOWS";
         case OSX:
            return "MC_OS_MAC";
         case LINUX:
            return "MC_OS_LINUX";
         default:
            return "MC_OS_OTHER";
      }
   }

   public static String getVendor() {
      String openGlVendor = Config.openGlVendor;
      if (openGlVendor == null) {
         return "MC_GL_VENDOR_OTHER";
      }

      openGlVendor = openGlVendor.toLowerCase();
      return openGlVendor.startsWith("ati")
         ? "MC_GL_VENDOR_ATI"
         : (
            openGlVendor.startsWith("intel")
               ? "MC_GL_VENDOR_INTEL"
               : (openGlVendor.startsWith("nvidia") ? "MC_GL_VENDOR_NVIDIA" : (openGlVendor.startsWith("x.org") ? "MC_GL_VENDOR_XORG" : "MC_GL_VENDOR_OTHER"))
         );
   }

   public static String getRenderer() {
      String openGlRenderer = Config.openGlRenderer;
      if (openGlRenderer == null) {
         return "MC_GL_RENDERER_OTHER";
      }

      openGlRenderer = openGlRenderer.toLowerCase();
      return openGlRenderer.startsWith("amd")
         ? "MC_GL_RENDERER_RADEON"
         : (
            openGlRenderer.startsWith("ati")
               ? "MC_GL_RENDERER_RADEON"
               : (
                  openGlRenderer.startsWith("radeon")
                     ? "MC_GL_RENDERER_RADEON"
                     : (
                        openGlRenderer.startsWith("gallium")
                           ? "MC_GL_RENDERER_GALLIUM"
                           : (
                              openGlRenderer.startsWith("intel")
                                 ? "MC_GL_RENDERER_INTEL"
                                 : (
                                    openGlRenderer.startsWith("geforce")
                                       ? "MC_GL_RENDERER_GEFORCE"
                                       : (
                                          openGlRenderer.startsWith("nvidia")
                                             ? "MC_GL_RENDERER_GEFORCE"
                                             : (
                                                openGlRenderer.startsWith("quadro")
                                                   ? "MC_GL_RENDERER_QUADRO"
                                                   : (
                                                      openGlRenderer.startsWith("nvs")
                                                         ? "MC_GL_RENDERER_QUADRO"
                                                         : (openGlRenderer.startsWith("mesa") ? "MC_GL_RENDERER_MESA" : "MC_GL_RENDERER_OTHER")
                                                   )
                                             )
                                       )
                                 )
                           )
                     )
               )
         );
   }

   public static String getPrefixMacro() {
      return PREFIX_MACRO;
   }

   public static ShaderMacro[] getExtensions() {
      if (extensionMacros == null) {
         String[] openGlExtensions = Config.getOpenGlExtensions();
         ShaderMacro[] shaderMacros = new ShaderMacro[openGlExtensions.length];

         for (int i = 0; i < openGlExtensions.length; i++) {
            shaderMacros[i] = new ShaderMacro(PREFIX_MACRO + openGlExtensions[i], "");
         }

         extensionMacros = shaderMacros;
      }

      return extensionMacros;
   }

   public static String getFixedMacroLines() {
      StringBuilder sb = new StringBuilder();
      addMacroLine(sb, "MC_VERSION", Config.getMinecraftVersionInt());
      addMacroLine(sb, "MC_GL_VERSION " + Config.getGlVersion().toInt());
      addMacroLine(sb, "MC_GLSL_VERSION " + Config.getGlslVersion().toInt());
      addMacroLine(sb, getOs());
      addMacroLine(sb, getVendor());
      addMacroLine(sb, getRenderer());
      return sb.toString();
   }

   public static String getOptionMacroLines() {
      StringBuilder sb = new StringBuilder();
      if (Shaders.configAntialiasingLevel > 0) {
         addMacroLine(sb, "MC_FXAA_LEVEL", Shaders.configAntialiasingLevel);
      }

      if (Shaders.configNormalMap) {
         addMacroLine(sb, "MC_NORMAL_MAP");
      }

      if (Shaders.configSpecularMap) {
         addMacroLine(sb, "MC_SPECULAR_MAP");
      }

      addMacroLine(sb, "MC_RENDER_QUALITY", Shaders.configRenderResMul);
      addMacroLine(sb, "MC_SHADOW_QUALITY", Shaders.configShadowResMul);
      addMacroLine(sb, "MC_HAND_DEPTH", Shaders.configHandDepthMul);
      if (Shaders.isOldHandLight()) {
         addMacroLine(sb, "MC_OLD_HAND_LIGHT");
      }

      if (Shaders.isOldLighting()) {
         addMacroLine(sb, "MC_OLD_LIGHTING");
      }

      return sb.toString();
   }

   public static void addMacroLine(StringBuilder sb, String str, int n) {
      sb.append("#define ");
      sb.append(str);
      sb.append(" ");
      sb.append(n);
      sb.append("\n");
   }

   public static void addMacroLine(StringBuilder sb, String str, float f) {
      sb.append("#define ");
      sb.append(str);
      sb.append(" ");
      sb.append(f);
      sb.append("\n");
   }

   public static void addMacroLine(StringBuilder sb, String str) {
      sb.append("#define ");
      sb.append(str);
      sb.append("\n");
   }
}
