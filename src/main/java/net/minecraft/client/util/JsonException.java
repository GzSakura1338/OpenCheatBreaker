package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class JsonException extends IOException {
    public List traces = Lists.newArrayList();
    public String message;

    public JsonException(String str) {
        this.traces.add(new JsonException.Entry(null));
        this.message = str;
    }

    public JsonException(String str, Throwable throwable) {
        super(throwable);
        this.traces.add(new JsonException.Entry(null));
        this.message = str;
    }

    public void addFaultyElement(String str) {
        ((JsonException.Entry)this.traces.get(0)).add(str);
    }

    public void addFaultyFile(String str) {
        ((JsonException.Entry)this.traces.get(0)).text = str;
        this.traces.add(0, new JsonException.Entry(null));
    }

    @Override
    public String getMessage() {
        return "Invalid " + this.traces.get(this.traces.size() - 1).toString() + ": " + this.message;
    }

    public static JsonException wrap(Exception exception) {
        if (exception instanceof JsonException) {
            return (JsonException)exception;
        }

        String message = exception.getMessage();
        if (exception instanceof FileNotFoundException) {
            message = "File not found";
        }

        return new JsonException(message, exception);
    }

    public static class Entry {
        public String text = null;
        public List list = Lists.newArrayList();

        public Entry() {
        }

        public void add(String str) {
            this.list.add(0, str);
        }

        public String join() {
            return StringUtils.join(this.list, "->");
        }

        @Override
        public String toString() {
            return this.text != null
                ? (!this.list.isEmpty() ? this.text + " " + this.join() : this.text)
                : (!this.list.isEmpty() ? "(Unknown file) " + this.join() : "(Unknown file)");
        }

        public Entry(Object obj) {
            this();
        }
    }
}
