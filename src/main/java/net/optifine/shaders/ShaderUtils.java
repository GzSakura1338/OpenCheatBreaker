package net.optifine.shaders;

import net.optifine.Config;
import net.optifine.shaders.config.ShaderOption;
import net.optifine.shaders.config.ShaderProfile;

public class ShaderUtils {
   public static ShaderOption getShaderOption(String str, ShaderOption[] shaderOptions) {
      if (shaderOptions == null) {
         return null;
      }

      for (int i = 0; i < shaderOptions.length; i++) {
         ShaderOption shaderOption = shaderOptions[i];
         if (shaderOption.getName().equals(str)) {
            return shaderOption;
         }
      }

      return null;
   }

   public static ShaderProfile detectProfile(ShaderProfile[] shaderProfiles, ShaderOption[] shaderOptions, boolean flag) {
      if (shaderProfiles == null) {
         return null;
      }

      for (int i = 0; i < shaderProfiles.length; i++) {
         ShaderProfile shaderProfile = shaderProfiles[i];
         if (matchProfile(shaderProfile, shaderOptions, flag)) {
            return shaderProfile;
         }
      }

      return null;
   }

   public static boolean matchProfile(ShaderProfile shaderProfile, ShaderOption[] shaderOptions, boolean flag) {
      if (shaderProfile == null) {
         return false;
      }

      if (shaderOptions == null) {
         return false;
      }

      String[] options = shaderProfile.getOptions();

      for (int i = 0; i < options.length; i++) {
         String str = options[i];
         ShaderOption shaderOption = getShaderOption(str, shaderOptions);
         if (shaderOption != null) {
            String str2 = flag ? shaderOption.getValueDefault() : shaderOption.getValue();
            String value = shaderProfile.getValue(str);
            if (!Config.equals(str2, value)) {
               return false;
            }
         }
      }

      return true;
   }
}
