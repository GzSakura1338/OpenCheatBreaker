package optifine.json;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class JSONValue {
    public static Object parse(Reader reader) {
        try {
            JSONParser jsonparser = new JSONParser();
            return jsonparser.parse(reader);
        } catch (Exception e) {
            return null;
        }
    }

    public static Object parse(String str) {
        StringReader stringReader = new StringReader(str);
        return parse(stringReader);
    }

    public static Object parseWithException(Reader reader) throws IOException, ParseException {
        JSONParser jsonparser = new JSONParser();
        return jsonparser.parse(reader);
    }

    public static Object parseWithException(String str) throws ParseException {
        JSONParser jsonparser = new JSONParser();
        return jsonparser.parse(str);
    }

    public static void writeJSONString(Object obj, Writer writer) throws IOException {
        if (obj == null) {
            writer.write("null");
        } else if (obj instanceof String) {
            writer.write(34);
            writer.write(escape((String)obj));
            writer.write(34);
        } else if (obj instanceof Double) {
            if (!((Double)obj).isInfinite() && !((Double)obj).isNaN()) {
                writer.write(obj.toString());
            } else {
                writer.write("null");
            }
        } else if (!(obj instanceof Float)) {
            if (obj instanceof Number) {
                writer.write(obj.toString());
            } else if (obj instanceof Boolean) {
                writer.write(obj.toString());
            } else if (obj instanceof JSONStreamAware) {
                ((JSONStreamAware)obj).writeJSONString(writer);
            } else if (obj instanceof JSONAware) {
                writer.write(((JSONAware)obj).toJSONString());
            } else if (obj instanceof Map) {
                JSONObject.writeJSONString((Map)obj, writer);
            } else if (obj instanceof List) {
                JSONArray.writeJSONString((List)obj, writer);
            } else {
                writer.write(obj.toString());
            }
        } else if (!((Float)obj).isInfinite() && !((Float)obj).isNaN()) {
            writer.write(obj.toString());
        } else {
            writer.write("null");
        }
    }

    public static String toJSONString(Object obj) {
        return obj == null
            ? "null"
            : (
                obj instanceof String
                    ? "\"" + escape((String)obj) + "\""
                    : (
                        obj instanceof Double
                            ? (!((Double)obj).isInfinite() && !((Double)obj).isNaN() ? obj.toString() : "null")
                            : (
                                obj instanceof Float
                                    ? (!((Float)obj).isInfinite() && !((Float)obj).isNaN() ? obj.toString() : "null")
                                    : (
                                        obj instanceof Number
                                            ? obj.toString()
                                            : (
                                                obj instanceof Boolean
                                                    ? obj.toString()
                                                    : (
                                                        obj instanceof JSONAware
                                                            ? ((JSONAware)obj).toJSONString()
                                                            : (
                                                                obj instanceof Map
                                                                    ? JSONObject.toJSONString((Map)obj)
                                                                    : (obj instanceof List ? JSONArray.toJSONString((List)obj) : obj.toString())
                                                            )
                                                    )
                                            )
                                    )
                            )
                    )
            );
    }

    public static String escape(String str) {
        if (str == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        escape(str, sb);
        return sb.toString();
    }

    public static void escape(String str, StringBuffer sb) {
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            switch (charAt) {
                case '\b':
                    sb.append("\\b");
                    continue;
                case '\t':
                    sb.append("\\t");
                    continue;
                case '\n':
                    sb.append("\\n");
                    continue;
                case '\f':
                    sb.append("\\f");
                    continue;
                case '\r':
                    sb.append("\\r");
                    continue;
                case '"':
                    sb.append("\\\"");
                    continue;
                case '\\':
                    sb.append("\\\\");
                    continue;
            }

            if (charAt >= 0 && charAt <= 31 || charAt >= 127 && charAt <= 159 || charAt >= 8192 && charAt <= 8447) {
                String toHexString = Integer.toHexString(charAt);
                sb.append("\\u");

                for (int j = 0; j < 4 - toHexString.length(); j++) {
                    sb.append('0');
                }

                sb.append(toHexString.toUpperCase());
            } else {
                sb.append(charAt);
            }
        }
    }
}
