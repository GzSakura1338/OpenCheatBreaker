package optifine.xdelta;

import com.cheatbreaker.client.util.SneakyThrow;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.RandomAccessFile;

public class Delta {
    public static final int CONSTANT_16 = 16;
    public static final boolean debug = false;
    public static final int buff_size = 1024;

    public static void computeDelta(SeekableSource seekableSource, InputStream inputStream, int n, DiffWriter diffWriter) throws IOException, DeltaException {
        int n2 = (int)seekableSource.length();
        Checksum checksum = new Checksum();
        checksum.generateChecksums(new SeekableSourceInputStream(seekableSource), n2);
        seekableSource.seek(0L);
        PushbackInputStream pushbackInputStream = new PushbackInputStream(new BufferedInputStream(inputStream), 1024);
        boolean flag = false;
        byte[] bytes = new byte[16];
        long l = 0L;
        byte[] bytes2 = new byte[1];
        byte[] bytes3 = new byte[16];
        int n3;
        if (n > 16 && n2 > 16) {
            n3 = pushbackInputStream.read(bytes, 0, 16);
            int n4 = n3;
            l = Checksum.queryChecksum(bytes, 16);
            long l2 = l;
            boolean flag2 = false;

            while (!flag) {
                int findChecksumIndex = checksum.findChecksumIndex(l);
                if (findChecksumIndex != -1) {
                    boolean flag3 = true;
                    int n5 = findChecksumIndex * 16;
                    int n6 = 15;
                    seekableSource.seek(n5);
                    if (!flag2 && seekableSource.read(bytes3, 0, 16) != -1) {
                        for (int i = 0; i < 16; i++) {
                            if (bytes3[i] != bytes[i]) {
                                flag3 = false;
                            }
                        }
                    } else {
                        flag2 = true;
                    }

                    if (flag3 & !flag2) {
                        long currentTimeMillis = System.currentTimeMillis();
                        boolean flag4 = true;
                        byte[] bytes4 = new byte[1024];
                        byte[] bytes5 = new byte[1024];
                        boolean flag5 = false;
                        boolean flag6 = false;
                        boolean flag7 = false;

                        do {
                            int read = seekableSource.read(bytes4, 0, 1024);
                            if (read == -1) {
                                flag2 = true;
                                break;
                            }

                            int read2 = pushbackInputStream.read(bytes5, 0, read);
                            if (read2 == -1) {
                                break;
                            }

                            int min = Math.min(read, read2);
                            int n7 = 0;

                            do {
                                n4++;
                                n6++;
                                flag4 = bytes4[n7] == bytes5[n7];
                                n7++;
                                if (!flag4) {
                                    bytes2[0] = bytes5[n7 - 1];
                                    if (read2 != -1) {
                                        pushbackInputStream.unread(bytes5, n7, read2 - n7);
                                    }
                                }
                            } while (n7 < min && flag4);

                            bytes2[0] = bytes5[n7 - 1];
                        } while (flag4 && n - n4 > 0);

                        diffWriter.addCopy(n5, n6);
                        if (n - n4 > 15) {
                            bytes[0] = bytes2[0];
                            pushbackInputStream.read(bytes, 1, 15);
                            n4 += 15;
                            l2 = l = Checksum.queryChecksum(bytes, 16);
                            continue;
                        }

                        bytes[0] = bytes2[0];
                        int n7 = n - n4;
                        pushbackInputStream.read(bytes, 1, n7);
                        n4 += n7;

                        for (int i = 0; i < n7 + 1; i++) {
                            diffWriter.addData(bytes[i]);
                        }

                        flag = true;
                        continue;
                    }
                }

                if (n - n4 > 0) {
                    pushbackInputStream.read(bytes2, 0, 1);
                    n4++;
                    diffWriter.addData(bytes[0]);
                    l2 = Checksum.incrementChecksum(l2, bytes[0], bytes2[0]);

                    for (int i = 0; i < 15; i++) {
                        bytes[i] = bytes[i + 1];
                    }

                    bytes[15] = bytes2[0];
                    l = Checksum.queryChecksum(bytes, 16);
                } else {
                    for (int i = 0; i < 16; i++) {
                        diffWriter.addData(bytes[i]);
                    }

                    flag = true;
                }
            }
        } else {
            while ((n3 = pushbackInputStream.read(bytes)) >= 0) {
                for (int i = 0; i < n3; i++) {
                    diffWriter.addData(bytes[i]);
                }
            }
        }
    }

    public static void computeDelta(byte[] bytes, InputStream inputStream, int n, DiffWriter diffWriter) throws IOException, DeltaException {
        computeDelta(new ByteArraySeekableSource(bytes), inputStream, n, diffWriter);
    }

    public static void computeDelta(File file, File file2, DiffWriter diffWriter) throws IOException, DeltaException {
        int n = (int)file2.length();
        RandomAccessFileSeekableSource randomAccessFileSeekableSource = new RandomAccessFileSeekableSource(new RandomAccessFile(file, "r"));
        FileInputStream fileInputStream = new FileInputStream(file2);

        try {
            computeDelta(randomAccessFileSeekableSource, fileInputStream, n, diffWriter);
        } catch (IOException e) {
            throw SneakyThrow.sneakyThrow(e);
        } catch (DeltaException e) {
            throw SneakyThrow.sneakyThrow(e);
        } finally {
            diffWriter.flush();
            randomAccessFileSeekableSource.close();
            fileInputStream.close();
            diffWriter.close();
        }
    }

    public static void main(String[] strs) {
        new Delta();
        if (strs.length != 3) {
            System.err.println("usage Delta [-d] source target [output]");
            System.err.println("either -d or an output filename must be specified.");
            System.err.println("aborting..");
        } else {
            try {
                DiffWriter diffWriter = null;
                File file = null;
                File file2 = null;
                if (strs[0].equals("-d")) {
                    file = new File(strs[1]);
                    file2 = new File(strs[2]);
                    diffWriter = new DebugDiffWriter();
                } else {
                    file = new File(strs[0]);
                    file2 = new File(strs[1]);
                    diffWriter = new GDiffWriter(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(new File(strs[2])))));
                }

                if (file.length() > 2147483647L || file2.length() > 2147483647L) {
                    System.err.println("source or target is too large, max length is 2147483647");
                    System.err.println("aborting..");
                    return;
                }

                computeDelta(file, file2, diffWriter);
                diffWriter.flush();
                diffWriter.close();
            } catch (Exception e) {
                System.err.println("error while generating delta: " + e);
            }
        }
    }
}
