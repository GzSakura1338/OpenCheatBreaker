package optifine.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class Yylex {
    public static final int YYEOF = -1;
    public static final int ZZ_BUFFERSIZE = 16384;
    public static int[] ZZ_LEXSTATE = new int[]{0, 0, 1, 1};
    public static final String ZZ_CMAP_PACKED = "\t\u0000\u0001\u0007\u0001\u0007\u0002\u0000\u0001\u0007\u0012\u0000\u0001\u0007\u0001\u0000\u0001\t\b\u0000\u0001\u0006\u0001\u0019\u0001\u0002\u0001\u0004\u0001\n\n\u0003\u0001\u001a\u0006\u0000\u0004\u0001\u0001\u0005\u0001\u0001\u0014\u0000\u0001\u0017\u0001\b\u0001\u0018\u0003\u0000\u0001\u0012\u0001\u000b\u0002\u0001\u0001\u0011\u0001\f\u0005\u0000\u0001\u0013\u0001\u0000\u0001\r\u0003\u0000\u0001\u000e\u0001\u0014\u0001\u000f\u0001\u0010\u0005\u0000\u0001\u0015\u0001\u0000\u0001\u0016ﾂ\u0000";
    public static char[] ZZ_CMAP = zzUnpackCMap(
        "\t\u0000\u0001\u0007\u0001\u0007\u0002\u0000\u0001\u0007\u0012\u0000\u0001\u0007\u0001\u0000\u0001\t\b\u0000\u0001\u0006\u0001\u0019\u0001\u0002\u0001\u0004\u0001\n\n\u0003\u0001\u001a\u0006\u0000\u0004\u0001\u0001\u0005\u0001\u0001\u0014\u0000\u0001\u0017\u0001\b\u0001\u0018\u0003\u0000\u0001\u0012\u0001\u000b\u0002\u0001\u0001\u0011\u0001\f\u0005\u0000\u0001\u0013\u0001\u0000\u0001\r\u0003\u0000\u0001\u000e\u0001\u0014\u0001\u000f\u0001\u0010\u0005\u0000\u0001\u0015\u0001\u0000\u0001\u0016ﾂ\u0000"
    );
    public static int[] ZZ_ACTION = zzUnpackAction();
    public static final String ZZ_ACTION_PACKED_0 = "\u0002\u0000\u0002\u0001\u0001\u0002\u0001\u0003\u0001\u0004\u0003\u0001\u0001\u0005\u0001\u0006\u0001\u0007\u0001\b\u0001\t\u0001\n\u0001\u000b\u0001\f\u0001\r\u0005\u0000\u0001\f\u0001\u000e\u0001\u000f\u0001\u0010\u0001\u0011\u0001\u0012\u0001\u0013\u0001\u0014\u0001\u0000\u0001\u0015\u0001\u0000\u0001\u0015\u0004\u0000\u0001\u0016\u0001\u0017\u0002\u0000\u0001\u0018";
    public static int[] ZZ_ROWMAP = zzUnpackRowMap();
    public static final String ZZ_ROWMAP_PACKED_0 = "\u0000\u0000\u0000\u001b\u00006\u0000Q\u0000l\u0000\u0087\u00006\u0000¢\u0000½\u0000Ø\u00006\u00006\u00006\u00006\u00006\u00006\u0000ó\u0000Ď\u00006\u0000ĩ\u0000ń\u0000ş\u0000ź\u0000ƕ\u00006\u00006\u00006\u00006\u00006\u00006\u00006\u00006\u0000ư\u0000ǋ\u0000Ǧ\u0000Ǧ\u0000ȁ\u0000Ȝ\u0000ȷ\u0000ɒ\u00006\u00006\u0000ɭ\u0000ʈ\u00006";
    public static int[] ZZ_TRANS = new int[]{
        2,
        2,
        3,
        4,
        2,
        2,
        2,
        5,
        2,
        6,
        2,
        2,
        7,
        8,
        2,
        9,
        2,
        2,
        2,
        2,
        2,
        10,
        11,
        12,
        13,
        14,
        15,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        17,
        18,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        4,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        4,
        19,
        20,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        20,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        5,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        21,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        22,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        23,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        -1,
        -1,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        16,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        24,
        25,
        26,
        27,
        28,
        29,
        30,
        31,
        32,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        33,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        34,
        35,
        -1,
        -1,
        34,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        36,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        37,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        38,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        39,
        -1,
        39,
        -1,
        39,
        -1,
        -1,
        -1,
        -1,
        -1,
        39,
        39,
        -1,
        -1,
        -1,
        -1,
        39,
        39,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        33,
        -1,
        20,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        20,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        35,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        38,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        40,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        41,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        42,
        -1,
        42,
        -1,
        42,
        -1,
        -1,
        -1,
        -1,
        -1,
        42,
        42,
        -1,
        -1,
        -1,
        -1,
        42,
        42,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        43,
        -1,
        43,
        -1,
        43,
        -1,
        -1,
        -1,
        -1,
        -1,
        43,
        43,
        -1,
        -1,
        -1,
        -1,
        43,
        43,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        44,
        -1,
        44,
        -1,
        44,
        -1,
        -1,
        -1,
        -1,
        -1,
        44,
        44,
        -1,
        -1,
        -1,
        -1,
        44,
        44,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1,
        -1
    };
    public static final int ZZ_NO_MATCH = 1;
    public static String[] ZZ_ERROR_MSG = new String[]{"Unkown internal scanner error", "Error: could not match input", "Error: pushback value was too large"};
    public static int[] ZZ_ATTRIBUTE = zzUnpackAttribute();
    public static final String ZZ_ATTRIBUTE_PACKED_0 = "\u0002\u0000\u0001\t\u0003\u0001\u0001\t\u0003\u0001\u0006\t\u0002\u0001\u0001\t\u0005\u0000\b\t\u0001\u0000\u0001\u0001\u0001\u0000\u0001\u0001\u0004\u0000\u0002\t\u0002\u0000\u0001\t";
    public int zzLexicalState = 0;
    public int zzState;
    public char[] zzBuffer = new char[16384];
    public boolean zzAtBOL = true;
    public int zzMarkedPos;
    public int zzCurrentPos;
    public int zzStartRead;
    public int zzEndRead;
    public int yyline;
    public int yychar;
    public int yycolumn;
    public StringBuffer stringBuffer = new StringBuffer();
    public boolean zzAtEOF;
    public Reader zzReader;
    public static final int CONSTANT_2 = 2;
    public static final int CONSTANT_0 = 0;
    public static final int CONSTANT_0_2 = 0;
    public static final int CONSTANT_2_2 = 2;

    public static int[] zzUnpackAction() {
        int[] ints = new int[45];
        byte b = 0;
        zzUnpackAction(
            "\u0002\u0000\u0002\u0001\u0001\u0002\u0001\u0003\u0001\u0004\u0003\u0001\u0001\u0005\u0001\u0006\u0001\u0007\u0001\b\u0001\t\u0001\n\u0001\u000b\u0001\f\u0001\r\u0005\u0000\u0001\f\u0001\u000e\u0001\u000f\u0001\u0010\u0001\u0011\u0001\u0012\u0001\u0013\u0001\u0014\u0001\u0000\u0001\u0015\u0001\u0000\u0001\u0015\u0004\u0000\u0001\u0016\u0001\u0017\u0002\u0000\u0001\u0018",
            b,
            ints
        );
        return ints;
    }

    public static int zzUnpackAction(String str, int n, int[] ints) {
        int n2 = 0;
        int n3 = n;
        int n4 = str.length();

        while (n2 < n4) {
            int charAt = str.charAt(n2++);
            char charAt2 = str.charAt(n2++);

            do {
                ints[n3++] = charAt2;
            } while (--charAt > 0);
        }

        return n3;
    }

    public static int[] zzUnpackRowMap() {
        int[] ints = new int[45];
        byte b = 0;
        zzUnpackRowMap(
            "\u0000\u0000\u0000\u001b\u00006\u0000Q\u0000l\u0000\u0087\u00006\u0000¢\u0000½\u0000Ø\u00006\u00006\u00006\u00006\u00006\u00006\u0000ó\u0000Ď\u00006\u0000ĩ\u0000ń\u0000ş\u0000ź\u0000ƕ\u00006\u00006\u00006\u00006\u00006\u00006\u00006\u00006\u0000ư\u0000ǋ\u0000Ǧ\u0000Ǧ\u0000ȁ\u0000Ȝ\u0000ȷ\u0000ɒ\u00006\u00006\u0000ɭ\u0000ʈ\u00006",
            b,
            ints
        );
        return ints;
    }

    public static int zzUnpackRowMap(String str, int n, int[] ints) {
        int n2 = 0;
        int n3 = n;
        int n4 = str.length();

        while (n2 < n4) {
            int n5 = str.charAt(n2++) << 16;
            ints[n3++] = n5 | str.charAt(n2++);
        }

        return n3;
    }

    public static int[] zzUnpackAttribute() {
        int[] ints = new int[45];
        byte b = 0;
        zzUnpackAttribute(
            "\u0002\u0000\u0001\t\u0003\u0001\u0001\t\u0003\u0001\u0006\t\u0002\u0001\u0001\t\u0005\u0000\b\t\u0001\u0000\u0001\u0001\u0001\u0000\u0001\u0001\u0004\u0000\u0002\t\u0002\u0000\u0001\t",
            b,
            ints
        );
        return ints;
    }

    public static int zzUnpackAttribute(String str, int n, int[] ints) {
        int n2 = 0;
        int n3 = n;
        int n4 = str.length();

        while (n2 < n4) {
            int charAt = str.charAt(n2++);
            char charAt2 = str.charAt(n2++);

            do {
                ints[n3++] = charAt2;
            } while (--charAt > 0);
        }

        return n3;
    }

    public int getPosition() {
        return this.yychar;
    }

    public Yylex(Reader reader) {
        this.zzReader = reader;
    }

    public Yylex(InputStream inputStream) {
        this(new InputStreamReader(inputStream));
    }

    public static char[] zzUnpackCMap(String str) {
        char[] chars = new char[65536];
        int n = 0;
        int n2 = 0;

        while (n < 90) {
            int charAt = str.charAt(n++);
            char charAt2 = str.charAt(n++);

            do {
                chars[n2++] = charAt2;
            } while (--charAt > 0);
        }

        return chars;
    }

    public boolean zzRefill() throws IOException {
        if (this.zzStartRead > 0) {
            System.arraycopy(this.zzBuffer, this.zzStartRead, this.zzBuffer, 0, this.zzEndRead - this.zzStartRead);
            this.zzEndRead = this.zzEndRead - this.zzStartRead;
            this.zzCurrentPos = this.zzCurrentPos - this.zzStartRead;
            this.zzMarkedPos = this.zzMarkedPos - this.zzStartRead;
            this.zzStartRead = 0;
        }

        if (this.zzCurrentPos >= this.zzBuffer.length) {
            char[] chars = new char[this.zzCurrentPos * 2];
            System.arraycopy(this.zzBuffer, 0, chars, 0, this.zzBuffer.length);
            this.zzBuffer = chars;
        }

        int read = this.zzReader.read(this.zzBuffer, this.zzEndRead, this.zzBuffer.length - this.zzEndRead);
        if (read > 0) {
            this.zzEndRead += read;
            return false;
        }

        if (read == 0) {
            int read2 = this.zzReader.read();
            if (read2 == -1) {
                return true;
            }

            this.zzBuffer[this.zzEndRead++] = (char)read2;
            return false;
        } else {
            return true;
        }
    }

    public void yyclose() throws IOException {
        this.zzAtEOF = true;
        this.zzEndRead = this.zzStartRead;
        if (this.zzReader != null) {
            this.zzReader.close();
        }
    }

    public void yyreset(Reader reader) {
        this.zzReader = reader;
        this.zzAtBOL = true;
        this.zzAtEOF = false;
        this.zzEndRead = this.zzStartRead = 0;
        this.zzCurrentPos = this.zzMarkedPos = 0;
        this.yyline = this.yychar = this.yycolumn = 0;
        this.zzLexicalState = 0;
    }

    public int yystate() {
        return this.zzLexicalState;
    }

    public void yybegin(int n) {
        this.zzLexicalState = n;
    }

    public String yytext() {
        return new String(this.zzBuffer, this.zzStartRead, this.zzMarkedPos - this.zzStartRead);
    }

    public char yycharat(int n) {
        return this.zzBuffer[this.zzStartRead + n];
    }

    public int yylength() {
        return this.zzMarkedPos - this.zzStartRead;
    }

    public void zzScanError(int n) {
        String str;
        try {
            str = ZZ_ERROR_MSG[n];
        } catch (ArrayIndexOutOfBoundsException e) {
            str = ZZ_ERROR_MSG[0];
        }

        throw new Error(str);
    }

    public void yypushback(int n) {
        if (n > this.yylength()) {
            this.zzScanError(2);
        }

        this.zzMarkedPos -= n;
    }

    public Yytoken yylex() throws IOException, ParseException {
        int zzEndRead = this.zzEndRead;
        char[] zzBuffer = this.zzBuffer;
        char[] chars = ZZ_CMAP;
        int[] ints = ZZ_TRANS;
        int[] ints2 = ZZ_ROWMAP;
        int[] ints3 = ZZ_ATTRIBUTE;

        while (true) {
            int zzMarkedPos = this.zzMarkedPos;
            this.yychar = this.yychar + (zzMarkedPos - this.zzStartRead);
            int n = -1;
            int n2 = this.zzCurrentPos = this.zzStartRead = zzMarkedPos;
            this.zzState = ZZ_LEXSTATE[this.zzLexicalState];

            int n3;
            while (true) {
                if (n2 < zzEndRead) {
                    n3 = zzBuffer[n2++];
                } else {
                    if (this.zzAtEOF) {
                        n3 = -1;
                        break;
                    }

                    this.zzCurrentPos = n2;
                    this.zzMarkedPos = zzMarkedPos;
                    boolean zzRefill = this.zzRefill();
                    n2 = this.zzCurrentPos;
                    zzMarkedPos = this.zzMarkedPos;
                    zzBuffer = this.zzBuffer;
                    zzEndRead = this.zzEndRead;
                    if (zzRefill) {
                        n3 = -1;
                        break;
                    }

                    n3 = zzBuffer[n2++];
                }

                int n4 = ints[ints2[this.zzState] + chars[n3]];
                if (n4 == -1) {
                    break;
                }

                this.zzState = n4;
                int n5 = ints3[this.zzState];
                if ((n5 & 1) == 1) {
                    n = this.zzState;
                    zzMarkedPos = n2;
                    if ((n5 & 8) == 8) {
                        break;
                    }
                }
            }

            this.zzMarkedPos = zzMarkedPos;
            switch (n < 0 ? n : ZZ_ACTION[n]) {
                case 1:
                    throw new ParseException(this.yychar, 0, new Character(this.yycharat(0)));
                case 2:
                    Long longValue = Long.valueOf(this.yytext());
                    return new Yytoken(0, longValue);
                case 3:
                case 25:
                case 26:
                case 27:
                case 28:
                case 29:
                case 30:
                case 31:
                case 32:
                case 33:
                case 34:
                case 35:
                case 36:
                case 37:
                case 38:
                case 39:
                case 40:
                case 41:
                case 42:
                case 43:
                case 44:
                case 45:
                case 46:
                case 47:
                case 48:
                    break;
                case 4:
                    this.stringBuffer.delete(0, this.stringBuffer.length());
                    this.yybegin(2);
                    break;
                case 5:
                    return new Yytoken(1, null);
                case 6:
                    return new Yytoken(2, null);
                case 7:
                    return new Yytoken(3, null);
                case 8:
                    return new Yytoken(4, null);
                case 9:
                    return new Yytoken(5, null);
                case 10:
                    return new Yytoken(6, null);
                case 11:
                    this.stringBuffer.append(this.yytext());
                    break;
                case 12:
                    this.stringBuffer.append('\\');
                    break;
                case 13:
                    this.yybegin(0);
                    return new Yytoken(0, this.stringBuffer.toString());
                case 14:
                    this.stringBuffer.append('"');
                    break;
                case 15:
                    this.stringBuffer.append('/');
                    break;
                case 16:
                    this.stringBuffer.append('\b');
                    break;
                case 17:
                    this.stringBuffer.append('\f');
                    break;
                case 18:
                    this.stringBuffer.append('\n');
                    break;
                case 19:
                    this.stringBuffer.append('\r');
                    break;
                case 20:
                    this.stringBuffer.append('\t');
                    break;
                case 21:
                    Double doubleValue = Double.valueOf(this.yytext());
                    return new Yytoken(0, doubleValue);
                case 22:
                    return new Yytoken(0, null);
                case 23:
                    Boolean bool = Boolean.valueOf(this.yytext());
                    return new Yytoken(0, bool);
                case 24:
                    try {
                        int parseInt = Integer.parseInt(this.yytext().substring(2), 16);
                        this.stringBuffer.append((char)parseInt);
                        break;
                    } catch (Exception e) {
                        throw new ParseException(this.yychar, 2, e);
                    }
                default:
                    if (n3 == -1 && this.zzStartRead == this.zzCurrentPos) {
                        this.zzAtEOF = true;
                        return null;
                    }

                    this.zzScanError(1);
            }
        }
    }
}
