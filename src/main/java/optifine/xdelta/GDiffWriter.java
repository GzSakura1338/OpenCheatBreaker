package optifine.xdelta;

import java.io.DataOutputStream;
import java.io.IOException;

public class GDiffWriter implements DiffWriter {
    public byte[] buf = new byte[256];
    public int buflen = 0;
    public boolean debug = false;
    public DataOutputStream output = null;

    public GDiffWriter(DataOutputStream dataOutputStream) throws IOException {
        this.output = dataOutputStream;
        this.output.writeByte(209);
        this.output.writeByte(255);
        this.output.writeByte(209);
        this.output.writeByte(255);
        this.output.writeByte(4);
    }

    public void setDebug(boolean flag) {
        this.debug = flag;
    }

    @Override
    public void addCopy(int n, int n2) throws IOException {
        if (this.buflen > 0) {
            this.writeBuf();
        }

        if (this.debug) {
            System.err.println("COPY off: " + n + ", len: " + n2);
        }

        if (n > Integer.MAX_VALUE) {
            this.output.writeByte(255);
        } else if (n < 65536) {
            if (n2 < 256) {
                this.output.writeByte(249);
                this.output.writeShort(n);
                this.output.writeByte(n2);
            } else if (n2 > 65535) {
                this.output.writeByte(251);
                this.output.writeShort(n);
                this.output.writeInt(n2);
            } else {
                this.output.writeByte(250);
                this.output.writeShort(n);
                this.output.writeShort(n2);
            }
        } else if (n2 < 256) {
            this.output.writeByte(252);
            this.output.writeInt(n);
            this.output.writeByte(n2);
        } else if (n2 > 65535) {
            this.output.writeByte(254);
            this.output.writeInt(n);
            this.output.writeInt(n2);
        } else {
            this.output.writeByte(253);
            this.output.writeInt(n);
            this.output.writeShort(n2);
        }
    }

    @Override
    public void addData(byte b) throws IOException {
        if (this.buflen >= 246) {
            this.writeBuf();
        }

        this.buf[this.buflen] = b;
        this.buflen++;
    }

    public void writeBuf() throws IOException {
        if (this.debug) {
            System.err.print("DATA:");

            for (int i = 0; i < this.buflen; i++) {
                if (this.buf[i] == 10) {
                    System.err.print("\\n");
                } else {
                    System.err.print(String.valueOf((char)this.buf[i]));
                }
            }

            System.err.println("");
        }

        if (this.buflen > 0) {
            this.output.writeByte(this.buflen);
            this.output.write(this.buf, 0, this.buflen);
        }

        this.buflen = 0;
    }

    @Override
    public void flush() throws IOException {
        if (this.buflen > 0) {
            this.writeBuf();
        }

        this.buflen = 0;
        this.output.flush();
    }

    @Override
    public void close() throws IOException {
        this.flush();
    }
}
