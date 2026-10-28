package optifine.json;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;

public class JSONWriter {
    public Writer writer = null;
    public int indentStep = 2;
    public int indent = 0;

    public JSONWriter(Writer writer) {
        this.writer = writer;
    }

    public JSONWriter(Writer writer, int n) {
        this.writer = writer;
        this.indentStep = n;
    }

    public JSONWriter(Writer writer, int n, int n2) {
        this.writer = writer;
        this.indentStep = n;
        this.indent = n2;
    }

    public void writeObject(Object obj) throws IOException {
        if (obj instanceof JSONObject) {
            JSONObject jsonobject = (JSONObject)obj;
            this.writeJsonObject(jsonobject);
        } else if (obj instanceof JSONArray) {
            JSONArray jsonarray = (JSONArray)obj;
            this.writeJsonArray(jsonarray);
        } else {
            this.writer.write(JSONValue.toJSONString(obj));
        }
    }

    public void writeJsonArray(JSONArray jsonarray) throws IOException {
        this.writeLine("[");
        this.indentAdd();
        int size = jsonarray.size();

        for (int i = 0; i < size; i++) {
            Object obj = jsonarray.get(i);
            this.writeIndent();
            this.writeObject(obj);
            if (i < jsonarray.size() - 1) {
                this.write(",");
            }

            this.writeLine("");
        }

        this.indentRemove();
        this.writeIndent();
        this.writer.write("]");
    }

    public void writeJsonObject(JSONObject jsonobject) throws IOException {
        this.writeLine("{");
        this.indentAdd();
        Set keySet = jsonobject.keySet();
        int size = keySet.size();
        int n = 0;

        for (String str : (Iterable<String>)keySet) {
            Object obj = jsonobject.get(str);
            this.writeIndent();
            this.writer.write(JSONValue.toJSONString(str));
            this.writer.write(": ");
            this.writeObject(obj);
            if (++n < size) {
                this.writeLine(",");
            } else {
                this.writeLine("");
            }
        }

        this.indentRemove();
        this.writeIndent();
        this.writer.write("}");
    }

    public void writeLine(String str) throws IOException {
        this.writer.write(str);
        this.writer.write("\n");
    }

    public void write(String str) throws IOException {
        this.writer.write(str);
    }

    public void writeIndent() throws IOException {
        for (int i = 0; i < this.indent; i++) {
            this.writer.write(32);
        }
    }

    public void indentAdd() {
        this.indent = this.indent + this.indentStep;
    }

    public void indentRemove() {
        this.indent = this.indent - this.indentStep;
    }
}
