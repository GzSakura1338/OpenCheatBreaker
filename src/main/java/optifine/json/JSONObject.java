package optifine.json;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class JSONObject extends LinkedHashMap implements JSONStreamAware, JSONAware, Map {
    public static final long serialVersionUID = -503443796854799292L;

    public static void writeJSONString(Map map, Writer writer) throws IOException {
        if (map == null) {
            writer.write("null");
        } else {
            boolean flag = true;
            Iterator iterator = map.entrySet().iterator();
            writer.write(123);

            while (iterator.hasNext()) {
                if (flag) {
                    flag = false;
                } else {
                    writer.write(44);
                }

                Entry entry = (Entry)iterator.next();
                writer.write(34);
                writer.write(escape(String.valueOf(entry.getKey())));
                writer.write(34);
                writer.write(58);
                JSONValue.writeJSONString(entry.getValue(), writer);
            }

            writer.write(125);
        }
    }

    @Override
    public void writeJSONString(Writer writer) throws IOException {
        writeJSONString(this, writer);
    }

    public static String toJSONString(Map map) {
        if (map == null) {
            return "null";
        }

        StringBuffer sb = new StringBuffer();
        boolean flag = true;
        Iterator iterator = map.entrySet().iterator();
        sb.append('{');

        while (iterator.hasNext()) {
            if (flag) {
                flag = false;
            } else {
                sb.append(',');
            }

            Entry entry = (Entry)iterator.next();
            toJSONString(String.valueOf(entry.getKey()), entry.getValue(), sb);
        }

        sb.append('}');
        return sb.toString();
    }

    @Override
    public String toJSONString() {
        return toJSONString(this);
    }

    public static String toJSONString(String str, Object obj, StringBuffer sb) {
        sb.append('"');
        if (str == null) {
            sb.append("null");
        } else {
            JSONValue.escape(str, sb);
        }

        sb.append('"').append(':');
        sb.append(JSONValue.toJSONString(obj));
        return sb.toString();
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }

    public static String toString(String str, Object obj) {
        StringBuffer sb = new StringBuffer();
        toJSONString(str, obj, sb);
        return sb.toString();
    }

    public static String escape(String str) {
        return JSONValue.escape(str);
    }
}
