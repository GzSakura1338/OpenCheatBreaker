package net.optifine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class FieldLocatorTypes implements IFieldLocator {
    public Field field = null;

    public FieldLocatorTypes(Class clazz, Class[] clazzs, Class clazz2, Class[] clazzs2, String str) {
        Field[] declaredFields = clazz.getDeclaredFields();
        ArrayList list = new ArrayList();

        for (int i = 0; i < declaredFields.length; i++) {
            Field field = declaredFields[i];
            list.add(field.getType());
        }

        ArrayList list2 = new ArrayList();
        list2.addAll(Arrays.asList(clazzs));
        list2.add(clazz2);
        list2.addAll(Arrays.asList(clazzs2));
        int indexOfSubList = Collections.indexOfSubList(list, list2);
        if (indexOfSubList < 0) {
            Config.warn("Field not found: " + str);
        } else {
            int indexOfSubList2 = Collections.indexOfSubList(list.subList(indexOfSubList + 1, list.size()), list2);
            if (indexOfSubList2 >= 0) {
                Config.warn("More than one match found for field: " + str);
            } else {
                int n = indexOfSubList + clazzs.length;
                this.field = declaredFields[n];
            }
        }
    }

    @Override
    public Field getField() {
        return this.field;
    }
}
