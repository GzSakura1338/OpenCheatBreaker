package net.minecraft.util;

import org.apache.commons.lang3.Validate;

public class ResourceLocation {
    public String resourceDomain;
    public String resourcePath;

    public ResourceLocation(String str, String str2) {
        Validate.notNull(str2);
        if (str != null && str.length() != 0) {
            this.resourceDomain = str;
        } else {
            this.resourceDomain = "minecraft";
        }

        this.resourcePath = str2;
    }

    public ResourceLocation(String str) {
        String str2 = "minecraft";
        String str3 = str;
        int indexOf = str.indexOf(58);
        if (indexOf >= 0) {
            str3 = str.substring(indexOf + 1);
            if (indexOf > 1) {
                str2 = str.substring(0, indexOf);
            }
        }

        this.resourceDomain = str2.toLowerCase();
        this.resourcePath = str3;
    }

    public String getResourcePath() {
        return this.resourcePath;
    }

    public String getResourceDomain() {
        return this.resourceDomain;
    }

    @Override
    public String toString() {
        return this.resourceDomain + ":" + this.resourcePath;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ResourceLocation)) {
            return false;
        }

        ResourceLocation resourceLocation = (ResourceLocation)obj;
        return this.resourceDomain.equals(resourceLocation.resourceDomain) && this.resourcePath.equals(resourceLocation.resourcePath);
    }

    @Override
    public int hashCode() {
        return 31 * this.resourceDomain.hashCode() + this.resourcePath.hashCode();
    }
}
