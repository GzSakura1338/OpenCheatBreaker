package net.optifine;

import java.util.Comparator;

public class CustomItemsComparator implements Comparator {
    @Override
    public int compare(Object obj, Object obj2) {
        CustomItemProperties customItemProperties = (CustomItemProperties)obj;
        CustomItemProperties customItemProperties2 = (CustomItemProperties)obj2;
        return customItemProperties.weight != customItemProperties2.weight
            ? customItemProperties2.weight - customItemProperties.weight
            : (!Config.equals(customItemProperties.basePath, customItemProperties2.basePath) ? customItemProperties.basePath.compareTo(customItemProperties2.basePath) : customItemProperties.name.compareTo(customItemProperties2.name));
    }
}
