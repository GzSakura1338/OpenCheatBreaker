package net.minecraft.client.util;

import com.google.common.collect.Lists;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class JsonException extends IOException {
   public List<JsonException.Entry> traces = Lists.newArrayList();
   public String exceptionMessage;

   public JsonException(String message) {
      this.traces.add(new JsonException.Entry());
      this.exceptionMessage = message;
   }

   public JsonException(String message, Throwable cause) {
      super(cause);
      this.traces.add(new JsonException.Entry());
      this.exceptionMessage = message;
   }

   public void addFaultyElement(String str) {
      this.traces.get(0).add(str);
   }

   public void addFaultyFile(String str) {
      this.traces.get(0).fileName = str;
      this.traces.add(0, new JsonException.Entry());
   }

   @Override
   public String getMessage() {
      return "Invalid " + this.traces.get(this.traces.size() - 1).toString() + ": " + this.exceptionMessage;
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
      public String fileName = null;
      public List<String> faultyElements = Lists.newArrayList();

      public void add(String str) {
         this.faultyElements.add(0, str);
      }

      public String joinStackTrace() {
         return StringUtils.join(this.faultyElements, "->");
      }

      @Override
      public String toString() {
         return this.fileName != null
            ? (!this.faultyElements.isEmpty() ? this.fileName + " " + this.joinStackTrace() : this.fileName)
            : (!this.faultyElements.isEmpty() ? "(Unknown file) " + this.joinStackTrace() : "(Unknown file)");
      }
   }
}
