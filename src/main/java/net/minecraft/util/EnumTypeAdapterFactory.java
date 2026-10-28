package net.minecraft.util;

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
    public TypeAdapter create(Gson gson, TypeToken typeToken) {
        Class rawType = typeToken.getRawType();
        if (!rawType.isEnum()) {
            return null;
        }

        final HashMap map = new HashMap();

        for (Object obj : rawType.getEnumConstants()) {
            map.put(this.getKey(obj), obj);
        }

        return new TypeAdapter() {
            public void write(JsonWriter jsonWriter, Object obj) throws IOException {
                if (obj == null) {
                    jsonWriter.nullValue();
                } else {
                    jsonWriter.value(EnumTypeAdapterFactory.this.getKey(obj));
                }
            }

            public Object read(JsonReader jsonReader) throws IOException {
                if (jsonReader.peek() == JsonToken.NULL) {
                    jsonReader.nextNull();
                    return null;
                } else {
                    return map.get(jsonReader.nextString());
                }
            }
        };
    }

    public String getKey(Object obj) {
        return obj instanceof Enum ? ((Enum)obj).name().toLowerCase(Locale.US) : obj.toString().toLowerCase(Locale.US);
    }
}
