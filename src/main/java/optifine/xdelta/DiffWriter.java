package optifine.xdelta;

import java.io.IOException;

public interface DiffWriter {
    void addCopy(int n, int n2) throws IOException;

    void addData(byte b) throws IOException;

    void flush() throws IOException;

    void close() throws IOException;
}
