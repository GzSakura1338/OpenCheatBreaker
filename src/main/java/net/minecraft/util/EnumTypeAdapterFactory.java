package net.minecraft.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class EnumTypeAdapterFactory implements TypeAdapterFactory {
   public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
      Class rawType = typeToken.getRawType();
      if (!rawType.isEnum()) {
         return null;
      }

      final HashMap newHashMap = Maps.newHashMap();

      for (Object obj : rawType.getEnumConstants()) {
         newHashMap.put(this.getKey(obj), obj);
      }

      return new TypeAdapter<T>() {
         public void write(JsonWriter jsonWriter, T value) throws IOException {
            if (value == null) {
               jsonWriter.nullValue();
            } else {
               jsonWriter.value(EnumTypeAdapterFactory.this.getKey(value));
            }
         }

         public T read(JsonReader jsonReader) throws IOException {
            if (jsonReader.peek() == JsonToken.NULL) {
               jsonReader.nextNull();
               return null;
            } else {
               return (T)newHashMap.get(jsonReader.nextString());
            }
         }
      };
   }

   public String getKey(Object obj) {
      return obj instanceof Enum ? ((Enum)obj).name().toLowerCase(Locale.US) : obj.toString().toLowerCase(Locale.US);
   }
}
