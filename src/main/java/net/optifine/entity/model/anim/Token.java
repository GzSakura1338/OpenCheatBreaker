package net.optifine.entity.model.anim;

public class Token {
    public TokenType type;
    public String text;

    public Token(TokenType tokenType, String str) {
        this.type = tokenType;
        this.text = str;
    }

    public TokenType getType() {
        return this.type;
    }

    public String getText() {
        return this.text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}
