package net.optifine.entity.model.anim;

public enum TokenType {
    IDENTIFIER("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_:."),
    NUMBER("0123456789", "0123456789."),
    OPERATOR("+-*/%!&|<>=", "&|="),
    COMMA(","),
    BRACKET_OPEN("("),
    BRACKET_CLOSE(")");

    public String charsFirst;
    public String charsNext;
    public static TokenType[] VALUES = values();

    TokenType(String str) {
        this(str, "");
    }

    TokenType(String str, String str2) {
        this.charsFirst = str;
        this.charsNext = str2;
    }

    public String getCharsFirst() {
        return this.charsFirst;
    }

    public String getCharsNext() {
        return this.charsNext;
    }

    public static TokenType getTypeByFirstChar(char c) {
        for (int i = 0; i < VALUES.length; i++) {
            TokenType tokenType = VALUES[i];
            if (tokenType.getCharsFirst().indexOf(c) >= 0) {
                return tokenType;
            }
        }

        return null;
    }

    public boolean hasCharNext(char c) {
        return this.charsNext.indexOf(c) >= 0;
    }

    private static class Const {
        public static final String ALPHAS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        public static final String DIGITS = "0123456789";

        public Const() {
        }
    }
}
