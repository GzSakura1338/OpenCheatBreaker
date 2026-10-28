package optifine.xdelta;

import com.cheatbreaker.client.util.SneakyThrow;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

public class GDiffPatcher {
    public GDiffPatcher(File file, File file2, File file3) throws IOException, PatchException {
        RandomAccessFileSeekableSource randomAccessFileSeekableSource = new RandomAccessFileSeekableSource(new RandomAccessFile(file, "r"));
        FileInputStream fileInputStream = new FileInputStream(file2);
        FileOutputStream fileOutputStream = new FileOutputStream(file3);

        try {
            runPatch(randomAccessFileSeekableSource, fileInputStream, fileOutputStream);
        } catch (IOException e) {
            throw SneakyThrow.sneakyThrow(e);
        } catch (PatchException e) {
            throw SneakyThrow.sneakyThrow(e);
        } finally {
            randomAccessFileSeekableSource.close();
            fileInputStream.close();
            fileOutputStream.close();
        }
    }

    public GDiffPatcher(byte[] bytes, InputStream inputStream, OutputStream outputStream) throws IOException, PatchException {
        this(new ByteArraySeekableSource(bytes), inputStream, outputStream);
    }

    public GDiffPatcher(SeekableSource seekableSource, InputStream inputStream, OutputStream outputStream) throws IOException, PatchException {
        runPatch(seekableSource, inputStream, outputStream);
    }

    public static void runPatch(SeekableSource seekableSource, InputStream inputStream, OutputStream outputStream) throws IOException, PatchException {
        DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
        DataInputStream dataInputStream = new DataInputStream(inputStream);

        try {
            byte[] bytes = new byte[256];
            byte b = 0;
            if (dataInputStream.readUnsignedByte() == 209
                && dataInputStream.readUnsignedByte() == 255
                && dataInputStream.readUnsignedByte() == 209
                && dataInputStream.readUnsignedByte() == 255
                && dataInputStream.readUnsignedByte() == 4) {
                int n = b + 5;

                while (dataInputStream.available() > 0) {
                    int readUnsignedByte = dataInputStream.readUnsignedByte();
                    boolean flag = false;
                    boolean flag2 = false;
                    switch (readUnsignedByte) {
                        case 0:
                            break;
                        case 1:
                            append(1, dataInputStream, dataOutputStream);
                            n += 2;
                            break;
                        case 2:
                            append(2, dataInputStream, dataOutputStream);
                            n += 3;
                            break;
                        case 246:
                            append(246, dataInputStream, dataOutputStream);
                            n += 247;
                            break;
                        case 247:
                            int readUnsignedShort = dataInputStream.readUnsignedShort();
                            append(readUnsignedShort, dataInputStream, dataOutputStream);
                            n += readUnsignedShort + 3;
                            break;
                        case 248:
                            int readInt = dataInputStream.readInt();
                            append(readInt, dataInputStream, dataOutputStream);
                            n += readInt + 5;
                            break;
                        case 249:
                            int readUnsignedShort2 = dataInputStream.readUnsignedShort();
                            int readUnsignedByte2 = dataInputStream.readUnsignedByte();
                            copy(readUnsignedShort2, readUnsignedByte2, seekableSource, dataOutputStream);
                            n += 4;
                            break;
                        case 250:
                            int readUnsignedShort3 = dataInputStream.readUnsignedShort();
                            int readUnsignedShort4 = dataInputStream.readUnsignedShort();
                            copy(readUnsignedShort3, readUnsignedShort4, seekableSource, dataOutputStream);
                            n += 5;
                            break;
                        case 251:
                            int readUnsignedShort5 = dataInputStream.readUnsignedShort();
                            int readInt2 = dataInputStream.readInt();
                            copy(readUnsignedShort5, readInt2, seekableSource, dataOutputStream);
                            n += 7;
                            break;
                        case 252:
                            int readInt3 = dataInputStream.readInt();
                            int readUnsignedByte3 = dataInputStream.readUnsignedByte();
                            copy(readInt3, readUnsignedByte3, seekableSource, dataOutputStream);
                            n += 8;
                            break;
                        case 253:
                            int readInt4 = dataInputStream.readInt();
                            int readUnsignedShort6 = dataInputStream.readUnsignedShort();
                            copy(readInt4, readUnsignedShort6, seekableSource, dataOutputStream);
                            n += 7;
                            break;
                        case 254:
                            int readInt5 = dataInputStream.readInt();
                            int readInt6 = dataInputStream.readInt();
                            copy(readInt5, readInt6, seekableSource, dataOutputStream);
                            n += 9;
                            break;
                        case 255:
                            long readLong = dataInputStream.readLong();
                            int readInt7 = dataInputStream.readInt();
                            copy(readLong, readInt7, seekableSource, dataOutputStream);
                            n += 13;
                            break;
                        default:
                            append(readUnsignedByte, dataInputStream, dataOutputStream);
                            n += readUnsignedByte + 1;
                    }
                }
            } else {
                System.err.println("magic string not found, aborting!");
            }
        } catch (PatchException e) {
            throw SneakyThrow.sneakyThrow(e);
        } finally {
            dataOutputStream.flush();
        }
    }

    public static void copy(long l, int n, SeekableSource seekableSource, OutputStream outputStream) throws IOException, PatchException {
        if (l + n > seekableSource.length()) {
            throw new PatchException("truncated source file, aborting");
        }

        byte[] bytes = new byte[256];
        seekableSource.seek(l);

        while (n > 0) {
            int n2 = n > 256 ? 256 : n;
            int read = seekableSource.read(bytes, 0, n2);
            outputStream.write(bytes, 0, read);
            n -= read;
        }
    }

    public static void append(int n, InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bytes = new byte[256];

        while (n > 0) {
            int n2 = n > 256 ? 256 : n;
            int read = inputStream.read(bytes, 0, n2);
            outputStream.write(bytes, 0, read);
            n -= read;
        }
    }

    public static void main(String[] strs) {
        if (strs.length != 3) {
            System.err.println("usage GDiffPatch source patch output");
            System.err.println("aborting..");
        } else {
            try {
                File file = new File(strs[0]);
                File file2 = new File(strs[1]);
                File file3 = new File(strs[2]);
                if (file.length() > 2147483647L || file2.length() > 2147483647L) {
                    System.err.println("source or patch is too large, max length is 2147483647");
                    System.err.println("aborting..");
                    return;
                }

                new GDiffPatcher(file, file2, file3);
                System.out.println("finished patching file");
            } catch (Exception e) {
                System.err.println("error while patching: " + e);
            }
        }
    }
}
