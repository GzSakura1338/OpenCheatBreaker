package optifine.xdelta;

import java.io.IOException;

public class ByteArraySeekableSource implements SeekableSource {
    public long lastPos = 0L;
    public byte[] source;

    public ByteArraySeekableSource(byte[] bytes) {
        this.source = bytes;
    }

    @Override
    public void seek(long l) throws IOException {
        this.lastPos = l;
    }

    @Override
    public int read(byte[] bytes, int n, int n2) throws IOException {
        int n3 = this.source.length - (int)this.lastPos;
        if (n3 <= 0) {
            return -1;
        }

        if (n3 < n2) {
            n2 = n3;
        }

        System.arraycopy(this.source, (int)this.lastPos, bytes, n, n2);
        this.lastPos += n2;
        return n2;
    }

    @Override
    public long length() throws IOException {
        return this.source.length;
    }

    @Override
    public void close() throws IOException {
        this.source = null;
    }
}
