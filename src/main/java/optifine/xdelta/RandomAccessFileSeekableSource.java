package optifine.xdelta;

import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccessFileSeekableSource implements SeekableSource {
    public RandomAccessFile raf;

    public RandomAccessFileSeekableSource(RandomAccessFile randomAccessFile) {
        this.raf = randomAccessFile;
    }

    @Override
    public void seek(long l) throws IOException {
        this.raf.seek(l);
    }

    @Override
    public int read(byte[] bytes, int n, int n2) throws IOException {
        return this.raf.read(bytes, n, n2);
    }

    @Override
    public long length() throws IOException {
        return this.raf.length();
    }

    @Override
    public void close() throws IOException {
        this.raf.close();
    }
}
