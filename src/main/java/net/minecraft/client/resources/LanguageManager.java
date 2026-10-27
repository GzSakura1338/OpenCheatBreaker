package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.util.StringTranslate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LanguageManager implements IResourceManagerReloadListener {
   public static Logger logger = LogManager.getLogger();
   public Map<String, Language> languageMap = Maps.newHashMap();
   public IMetadataSerializer theMetadataSerializer;
   public static Locale currentLocale = new Locale();
   public String currentLanguage;

   public LanguageManager(IMetadataSerializer theMetadataSerializerIn, String currentLanguageIn) {
      this.theMetadataSerializer = theMetadataSerializerIn;
      this.currentLanguage = currentLanguageIn;
      I18n.setLocale(currentLocale);
   }

   public void parseLanguageMetadata(List<IResourcePack> resourcesPacks) {
      this.languageMap.clear();

      for (IResourcePack resourcePack : resourcesPacks) {
         try {
            LanguageMetadataSection packMetadata = resourcePack.getPackMetadata(this.theMetadataSerializer, "language");
            if (packMetadata != null) {
               for (Language language : packMetadata.getLanguages()) {
                  if (!this.languageMap.containsKey(language.getLanguageCode())) {
                     this.languageMap.put(language.getLanguageCode(), language);
                  }
               }
            }
         } catch (RuntimeException e) {
            logger.warn("Unable to parse metadata section of resourcepack: " + resourcePack.getPackName(), e);
         } catch (IOException e) {
            logger.warn("Unable to parse metadata section of resourcepack: " + resourcePack.getPackName(), e);
         }
      }
   }

   @Override
   public void onResourceManagerReload(IResourceManager resourceManager) {
      ArrayList newArrayList = Lists.newArrayList(new String[]{"en_US"});
      if (!"en_US".equals(this.currentLanguage)) {
         newArrayList.add(this.currentLanguage);
      }

      currentLocale.loadLocaleDataFiles(resourceManager, newArrayList);
      StringTranslate.replaceWith(currentLocale.properties);
   }

   public boolean isCurrentLocaleUnicode() {
      return currentLocale.isUnicode();
   }

   public boolean isCurrentLanguageBidirectional() {
      return this.getCurrentLanguage() != null && this.getCurrentLanguage().isBidirectional();
   }

   public void setCurrentLanguage(Language currentLanguageIn) {
      this.currentLanguage = currentLanguageIn.getLanguageCode();
   }

   public Language getCurrentLanguage() {
      return this.languageMap.containsKey(this.currentLanguage) ? this.languageMap.get(this.currentLanguage) : this.languageMap.get("en_US");
   }

   public SortedSet<Language> getLanguages() {
      return Sets.newTreeSet(this.languageMap.values());
   }
}
