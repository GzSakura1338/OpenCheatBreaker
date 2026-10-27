package net.optifine.reflect;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import net.minecraft.client.Minecraft;

public class FieldLocatorActionKeyF3 implements IFieldLocator {
   @Override
   public Field getField() {
      Class<Minecraft> class2 = Minecraft.class;
      Field fieldRenderChunksMany = this.getFieldRenderChunksMany();
      if (fieldRenderChunksMany == null) {
         return null;
      }

      Field fieldAfter = ReflectorRaw.getFieldAfter(Minecraft.class, fieldRenderChunksMany, boolean.class, 0);
      return fieldAfter == null ? null : fieldAfter;
   }

   public Field getFieldRenderChunksMany() {
      Minecraft minecraft = Minecraft.getMinecraft();
      boolean renderChunksMany = minecraft.renderChunksMany;
      Field[] declaredFields = Minecraft.class.getDeclaredFields();
      minecraft.renderChunksMany = true;
      Field[] fields = ReflectorRaw.getFields(minecraft, declaredFields, boolean.class, Boolean.TRUE);
      minecraft.renderChunksMany = false;
      Field[] fields2 = ReflectorRaw.getFields(minecraft, declaredFields, boolean.class, Boolean.FALSE);
      minecraft.renderChunksMany = renderChunksMany;
      HashSet set = new HashSet<>(Arrays.asList(fields));
      HashSet set2 = new HashSet<>(Arrays.asList(fields2));
      HashSet set3 = new HashSet(set);
      set3.retainAll(set2);
      Field[] toArray = (Field[])set3.toArray(new Field[set3.size()]);
      return toArray.length != 1 ? null : toArray[0];
   }
}
