package net.optifine.entity.model.anim;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;

public class TokenParser {
    public static Token[] parse(String str) throws IOException, ParseException {
        StringReader stringReader = new StringReader(str);
        PushbackReader pushbackReader = new PushbackReader(stringReader);
        ArrayList list = new ArrayList();

        while (true) {
            int read = pushbackReader.read();
            if (read < 0) {
                return (Token[])list.toArray(new Token[list.size()]);
            }

            char c = (char)read;
            if (!Character.isWhitespace(c)) {
                TokenType typeByFirstChar = TokenType.getTypeByFirstChar(c);
                if (typeByFirstChar == null) {
                    throw new ParseException("Invalid character: '" + c + "', in: " + str);
                }

                Token readToken2 = readToken(c, typeByFirstChar, pushbackReader);
                list.add(readToken2);
            }
        }
    }

    public static Token readToken(char c, TokenType tokenType, PushbackReader pushbackReader) throws IOException {
        StringBuffer sb = new StringBuffer();
        sb.append(c);

        while (true) {
            int read = pushbackReader.read();
            if (read < 0) {
                break;
            }

            char c2 = (char)read;
            if (!tokenType.hasCharNext(c2)) {
                pushbackReader.unread(c2);
                break;
            }

            sb.append(c2);
        }

        return new Token(tokenType, sb.toString());
    }
}
