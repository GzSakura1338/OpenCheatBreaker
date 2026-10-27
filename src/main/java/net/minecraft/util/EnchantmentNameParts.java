package net.minecraft.util;

import java.util.Random;

public class EnchantmentNameParts {
   public static EnchantmentNameParts instance = new EnchantmentNameParts();
   public Random rand = new Random();
   public String[] namePartsArray = "the elder scrolls klaatu berata niktu xyzzy bless curse light darkness fire air earth water hot dry cold wet ignite snuff embiggen twist shorten stretch fiddle destroy imbue galvanize enchant free limited range of towards inside sphere cube self other ball mental physical grow shrink demon elemental spirit animal creature beast humanoid undead fresh stale "
      .split(" ");

   public static EnchantmentNameParts getInstance() {
      return instance;
   }

   public String generateNewRandomName() {
      int n = this.rand.nextInt(2) + 3;
      String str = "";

      for (int i = 0; i < n; i++) {
         if (i > 0) {
            str = str + " ";
         }

         str = str + this.namePartsArray[this.rand.nextInt(this.namePartsArray.length)];
      }

      return str;
   }

   public void reseedRandomGenerator(long seed) {
      this.rand.setSeed(seed);
   }
}
