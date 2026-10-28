package optifine.json;

import java.io.IOException;

public interface ContentHandler {
    void startJSON() throws IOException, ParseException;

    void endJSON() throws IOException, ParseException;

    boolean endObject() throws IOException, ParseException;

    boolean startObjectEntry(String str) throws IOException, ParseException;

    boolean endObjectEntry() throws IOException, ParseException;

    boolean primitive(Object obj) throws IOException, ParseException;

    boolean check();

    boolean check2();

    boolean check3();
}
