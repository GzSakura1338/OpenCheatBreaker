package net.minecraft.client.resources;

public class Language implements Comparable {
    public String languageCode;
    public String region;
    public String name;
    public boolean bidirectional;

    public Language(String str, String str2, String str3, boolean flag) {
        this.languageCode = str;
        this.region = str2;
        this.name = str3;
        this.bidirectional = flag;
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
        return this == obj || obj instanceof Language && this.languageCode.equals(((Language)obj).languageCode);
    }

    @Override
    public int hashCode() {
        return this.languageCode.hashCode();
    }

    public int compareTo(Language language) {
        return this.languageCode.compareTo(language.languageCode);
    }

    @Override
    public int compareTo(Object obj) {
        return this.compareTo((Language)obj);
    }
}
