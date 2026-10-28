package optifine.xdelta;

import java.io.IOException;
import java.io.InputStream;

public class SeekableSourceInputStream extends InputStream {
    public SeekableSource seekableSource;

    public SeekableSourceInputStream(SeekableSource seekableSource) {
        this.seekableSource = seekableSource;
    }

    @Override
    public int read() throws IOException {
        byte[] bytes = new byte[1];
        this.seekableSource.read(bytes, 0, 1);
        return bytes[0];
    }

    @Override
    public int read(byte[] bytes, int n, int n2) throws IOException {
        return this.seekableSource.read(bytes, n, n2);
    }

    @Override
    public void close() throws IOException {
        this.seekableSource.close();
    }
}
