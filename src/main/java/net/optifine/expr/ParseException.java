package net.optifine.expr;

public class ParseException extends Exception {
   public ParseException(String str, Throwable throwable) {
      super(str, throwable);
   }

   public ParseException(String str) {
      super(str);
   }
}
