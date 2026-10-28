package net.minecraft.nbt;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;

public class CompressedStreamTools {
    public static NBTTagCompound readCompressed(InputStream inputStream) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(inputStream)));

        NBTTagCompound nbttagCompound;
        try {
            nbttagCompound = read(dataInputStream, NBTSizeTracker.INFINITE);
        } finally {
            dataInputStream.close();
        }

        return nbttagCompound;
    }

    public static void writeCompressed(NBTTagCompound nbttagCompound, OutputStream outputStream) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputStream)));

        try {
            write(nbttagCompound, dataOutputStream);
        } finally {
            dataOutputStream.close();
        }
    }

    public static NBTTagCompound decompress(byte[] bytes, NBTSizeTracker nbtsizeTracker) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes))));

        NBTTagCompound nbttagCompound;
        try {
            nbttagCompound = read(dataInputStream, nbtsizeTracker);
        } finally {
            dataInputStream.close();
        }

        return nbttagCompound;
    }

    public static byte[] compress(NBTTagCompound nbttagCompound) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(new GZIPOutputStream(byteArrayOutputStream));

        try {
            write(nbttagCompound, dataOutputStream);
        } finally {
            dataOutputStream.close();
        }

        return byteArrayOutputStream.toByteArray();
    }

    public static void safeWrite(NBTTagCompound nbttagCompound, File file) throws IOException {
        File file2 = new File(file.getAbsolutePath() + "_tmp");
        if (file2.exists()) {
            file2.delete();
        }

        write(nbttagCompound, file2);
        if (file.exists()) {
            file.delete();
        }

        if (file.exists()) {
            throw new IOException("Failed to delete " + file);
        }

        file2.renameTo(file);
    }

    public static void write(NBTTagCompound nbttagCompound, File file) throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));

        try {
            write(nbttagCompound, dataOutputStream);
        } finally {
            dataOutputStream.close();
        }
    }

    public static NBTTagCompound read(File file) throws IOException {
        return readFromFile(file, NBTSizeTracker.INFINITE);
    }

    public static NBTTagCompound readFromFile(File file, NBTSizeTracker nbtsizeTracker) throws IOException {
        if (!file.exists()) {
            return null;
        }

        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));

        NBTTagCompound nbttagCompound;
        try {
            nbttagCompound = read(dataInputStream, nbtsizeTracker);
        } finally {
            dataInputStream.close();
        }

        return nbttagCompound;
    }

    public static NBTTagCompound read(DataInputStream dataInputStream) throws IOException {
        return read(dataInputStream, NBTSizeTracker.INFINITE);
    }

    public static NBTTagCompound read(DataInput dataInput, NBTSizeTracker nbtsizeTracker) throws IOException {
        NBTBase read2 = read(dataInput, 0, nbtsizeTracker);
        if (read2 instanceof NBTTagCompound) {
            return (NBTTagCompound)read2;
        } else {
            throw new IOException("Root tag must be a named compound tag");
        }
    }

    public static void write(NBTTagCompound nbttagCompound, DataOutput dataOutput) throws IOException {
        writeTag(nbttagCompound, dataOutput);
    }

    public static void writeTag(NBTBase nbtbase, DataOutput dataOutput) throws IOException {
        dataOutput.writeByte(nbtbase.getId());
        if (nbtbase.getId() != 0) {
            dataOutput.writeUTF("");
            nbtbase.write(dataOutput);
        }
    }

    public static NBTBase read(DataInput dataInput, int n, NBTSizeTracker nbtsizeTracker) throws IOException {
        byte readByte = dataInput.readByte();
        if (readByte == 0) {
            return new NBTTagEnd();
        }

        dataInput.readUTF();
        NBTBase createNewByType = NBTBase.createNewByType(readByte);

        try {
            createNewByType.read(dataInput, n, nbtsizeTracker);
            return createNewByType;
        } catch (IOException e) {
            CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Loading NBT data");
            CrashReportCategory makeCategory = makeCrashReport.makeCategory("NBT Tag");
            makeCategory.addCrashSection("Tag name", "[UNNAMED TAG]");
            makeCategory.addCrashSection("Tag type", readByte);
            throw new ReportedException(makeCrashReport);
        }
    }
}
