package net.minecraft.client.resources;

public class Language implements Comparable<Language> {
   public String languageCode;
   public String region;
   public String name;
   public boolean bidirectional;

   public Language(String languageCodeIn, String regionIn, String nameIn, boolean bidirectionalIn) {
      this.languageCode = languageCodeIn;
      this.region = regionIn;
      this.name = nameIn;
      this.bidirectional = bidirectionalIn;
   }

   public String getLanguageCode() {
      return this.languageCode;
   }

   public boolean isBidirectional() {
      return this.bidirectional;
   }

   @Override
   public String toString() {
      return String.format("%s (%s)", this.name, this.region);
   }

   @Override
   public boolean equals(Object obj) {
      return this == obj ? true : (!(obj instanceof Language) ? false : this.languageCode.equals(((Language)obj).languageCode));
   }

   @Override
   public int hashCode() {
      return this.languageCode.hashCode();
   }

   public int compareTo(Language language) {
      return this.languageCode.compareTo(language.languageCode);
   }
}
