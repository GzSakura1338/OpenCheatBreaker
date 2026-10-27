package net.optifine.shaders.config;

import java.util.ArrayList;
import net.optifine.Lang;
import net.optifine.shaders.ShaderUtils;
import net.optifine.shaders.Shaders;

public class ShaderOptionProfile extends ShaderOption {
   public ShaderProfile[] profiles = null;
   public ShaderOption[] options = null;
   public static final String NAME_PROFILE = "<profile>";
   public static final String VALUE_CUSTOM = "<custom>";

   public ShaderOptionProfile(ShaderProfile[] shaderProfiles, ShaderOption[] shaderOptions) {
      super("<profile>", "", detectProfileName(shaderProfiles, shaderOptions), getProfileNames(shaderProfiles), detectProfileName(shaderProfiles, shaderOptions, true), (String)null);
      this.profiles = shaderProfiles;
      this.options = shaderOptions;
   }

   @Override
   public void nextValue() {
      super.nextValue();
      if (this.getValue().equals("<custom>")) {
         super.nextValue();
      }

      this.applyProfileOptions();
   }

   public void updateProfile() {
      ShaderProfile profile = this.getProfile(this.getValue());
      if (profile == null || !ShaderUtils.matchProfile(profile, this.options, false)) {
         String detectProfileName2 = detectProfileName(this.profiles, this.options);
         this.setValue(detectProfileName2);
      }
   }

   public void applyProfileOptions() {
      ShaderProfile profile = this.getProfile(this.getValue());
      if (profile != null) {
         String[] options = profile.getOptions();

         for (int i = 0; i < options.length; i++) {
            String str = options[i];
            ShaderOption option = this.getOption(str);
            if (option != null) {
               String value = profile.getValue(str);
               option.setValue(value);
            }
         }
      }
   }

   public ShaderOption getOption(String str) {
      for (int i = 0; i < this.options.length; i++) {
         ShaderOption shaderOption = this.options[i];
         if (shaderOption.getName().equals(str)) {
            return shaderOption;
         }
      }

      return null;
   }

   public ShaderProfile getProfile(String str) {
      for (int i = 0; i < this.profiles.length; i++) {
         ShaderProfile shaderProfile = this.profiles[i];
         if (shaderProfile.getName().equals(str)) {
            return shaderProfile;
         }
      }

      return null;
   }

   @Override
   public String getNameText() {
      return Lang.get("of.shaders.profile");
   }

   @Override
   public String getValueText(String str) {
      return str.equals("<custom>") ? Lang.get("of.general.custom", "<custom>") : Shaders.translate("profile." + str, str);
   }

   @Override
   public String getValueColor(String str) {
      return str.equals("<custom>") ? "§c" : "§a";
   }

   @Override
   public String getDescriptionText() {
      String translate = Shaders.translate("profile.comment", (String)null);
      if (translate != null) {
         return translate;
      }

      StringBuffer sb = new StringBuffer();

      for (int i = 0; i < this.profiles.length; i++) {
         String name = this.profiles[i].getName();
         if (name != null) {
            String translate2 = Shaders.translate("profile." + name + ".comment", (String)null);
            if (translate2 != null) {
               sb.append(translate2);
               if (!translate2.endsWith(". ")) {
                  sb.append(". ");
               }
            }
         }
      }

      return sb.toString();
   }

   public static String detectProfileName(ShaderProfile[] shaderProfiles, ShaderOption[] shaderOptions) {
      return detectProfileName(shaderProfiles, shaderOptions, false);
   }

   public static String detectProfileName(ShaderProfile[] shaderProfiles, ShaderOption[] shaderOptions, boolean flag) {
      ShaderProfile detectProfile = ShaderUtils.detectProfile(shaderProfiles, shaderOptions, flag);
      return detectProfile == null ? "<custom>" : detectProfile.getName();
   }

   public static String[] getProfileNames(ShaderProfile[] shaderProfiles) {
      ArrayList list = new ArrayList();

      for (int i = 0; i < shaderProfiles.length; i++) {
         ShaderProfile shaderProfile = shaderProfiles[i];
         list.add(shaderProfile.getName());
      }

      list.add("<custom>");
      return (String[])list.toArray(new String[list.size()]);
   }
}
