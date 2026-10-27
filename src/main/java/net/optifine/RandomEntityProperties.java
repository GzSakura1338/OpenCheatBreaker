package net.optifine;

import java.util.ArrayList;
import java.util.Properties;
import net.minecraft.util.ResourceLocation;
import net.optifine.config.ConnectedParser;

public class RandomEntityProperties {
   public String name = null;
   public String basePath = null;
   public ResourceLocation[] resourceLocations = null;
   public RandomEntityRule[] rules = null;

   public RandomEntityProperties(String str, ResourceLocation[] resourceLocations) {
      ConnectedParser connectedParser = new ConnectedParser("RandomEntities");
      this.name = connectedParser.parseName(str);
      this.basePath = connectedParser.parseBasePath(str);
      this.resourceLocations = resourceLocations;
   }

   public RandomEntityProperties(Properties properties, String str, ResourceLocation resourceLocation) {
      ConnectedParser connectedParser = new ConnectedParser("RandomEntities");
      this.name = connectedParser.parseName(str);
      this.basePath = connectedParser.parseBasePath(str);
      this.rules = this.parseRules(properties, str, resourceLocation, connectedParser);
   }

   public ResourceLocation getTextureLocation(ResourceLocation resourceLocation, IRandomEntity randomEntity) {
      if (this.rules != null) {
         for (int i = 0; i < this.rules.length; i++) {
            RandomEntityRule randomEntityRule = this.rules[i];
            if (randomEntityRule.matches(randomEntity)) {
               return randomEntityRule.getTextureLocation(resourceLocation, randomEntity.getId());
            }
         }
      }

      if (this.resourceLocations != null) {
         int id = randomEntity.getId();
         int n = id % this.resourceLocations.length;
         return this.resourceLocations[n];
      } else {
         return resourceLocation;
      }
   }

   public RandomEntityRule[] parseRules(Properties properties, String str, ResourceLocation resourceLocation, ConnectedParser connectedParser) {
      ArrayList list = new ArrayList();
      int size = properties.size();

      for (int i = 0; i < size; i++) {
         int n = i + 1;
         String property = properties.getProperty("textures." + n);
         if (property == null) {
            property = properties.getProperty("skins." + n);
         }

         if (property != null) {
            RandomEntityRule randomEntityRule = new RandomEntityRule(properties, str, resourceLocation, n, property, connectedParser);
            if (randomEntityRule.isValid(str)) {
               list.add(randomEntityRule);
            }
         }
      }

      return (RandomEntityRule[])list.toArray(new RandomEntityRule[list.size()]);
   }

   public boolean isValid(String str) {
      if (this.resourceLocations == null && this.rules == null) {
         Config.warn("No skins specified: " + str);
         return false;
      }

      if (this.rules != null) {
         for (int i = 0; i < this.rules.length; i++) {
            RandomEntityRule randomEntityRule = this.rules[i];
            if (!randomEntityRule.isValid(str)) {
               return false;
            }
         }
      }

      if (this.resourceLocations != null) {
         for (int i = 0; i < this.resourceLocations.length; i++) {
            ResourceLocation resourceLocation = this.resourceLocations[i];
            if (!Config.hasResource(resourceLocation)) {
               Config.warn("Texture not found: " + resourceLocation.getResourcePath());
               return false;
            }
         }
      }

      return true;
   }

   public boolean isDefault() {
      return this.rules != null ? false : this.resourceLocations == null;
   }
}
