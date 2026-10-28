package optifine.xdelta;

import java.io.IOException;

public class DebugDiffWriter implements DiffWriter {
    public byte[] buf = new byte[256];
    public int buflen = 0;

    @Override
    public void addCopy(int n, int n2) throws IOException {
        if (this.buflen > 0) {
            this.writeBuf();
        }

        System.err.println("COPY off: " + n + ", len: " + n2);
    }

    @Override
    public void addData(byte b) throws IOException {
        if (this.buflen < 256) {
            this.buf[this.buflen++] = b;
        } else {
            this.writeBuf();
        }
    }

    public void writeBuf() {
        System.err.print("DATA: ");

        for (int i = 0; i < this.buflen; i++) {
            if (this.buf[i] == 10) {
                System.err.print("\\n");
            } else {
                System.err.print(String.valueOf((char)this.buf[i]));
            }
        }

        System.err.println("");
        this.buflen = 0;
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
    }
}
