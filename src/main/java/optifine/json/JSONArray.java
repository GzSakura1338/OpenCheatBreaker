package optifine.json;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JSONArray extends ArrayList implements JSONStreamAware, JSONAware, List {
    public static final long serialVersionUID = 3957988303675231981L;

    public static void writeJSONString(List list, Writer writer) throws IOException {
        if (list == null) {
            writer.write("null");
        } else {
            boolean flag = true;
            Iterator iterator = list.iterator();
            writer.write(91);

            while (iterator.hasNext()) {
                if (flag) {
                    flag = false;
                } else {
                    writer.write(44);
                }

                Object obj = iterator.next();
                if (obj == null) {
                    writer.write("null");
                } else {
                    JSONValue.writeJSONString(obj, writer);
                }
            }

            writer.write(93);
        }
    }

    @Override
    public void writeJSONString(Writer writer) throws IOException {
        writeJSONString(this, writer);
    }

    public static String toJSONString(List list) {
        if (list == null) {
            return "null";
        }

        boolean flag = true;
        StringBuffer sb = new StringBuffer();
        Iterator iterator = list.iterator();
        sb.append('[');

        while (iterator.hasNext()) {
            if (flag) {
                flag = false;
            } else {
                sb.append(',');
            }

            Object obj = iterator.next();
            if (obj == null) {
                sb.append("null");
            } else {
                sb.append(JSONValue.toJSONString(obj));
            }
        }

        sb.append(']');
        return sb.toString();
    }

    @Override
    public String toJSONString() {
        return toJSONString(this);
    }

    @Override
    public String toString() {
        return this.toJSONString();
    }
}
