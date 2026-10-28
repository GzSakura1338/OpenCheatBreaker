package optifine.xdelta;

import java.io.IOException;

public interface SeekableSource {
    void seek(long l) throws IOException;

    int read(byte[] bytes, int n, int n2) throws IOException;

    void close() throws IOException;

    long length() throws IOException;
}
