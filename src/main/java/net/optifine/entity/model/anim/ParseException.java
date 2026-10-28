package net.optifine.entity.model.anim;

public class ParseException extends Exception {
    public ParseException(String str, Throwable throwable) {
        super(str, throwable);
    }

    public ParseException(String str) {
        super(str);
    }
}
