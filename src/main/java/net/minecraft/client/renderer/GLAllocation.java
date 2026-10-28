package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.lwjgl.opengl.GL11;

public class GLAllocation {
    public static Map mapDisplayLists = new HashMap();
    public static List listDummy = new ArrayList();

    public static synchronized int generateDisplayLists(int n) {
        int glGenLists = GL11.glGenLists(n);
        mapDisplayLists.put(glGenLists, n);
        return glGenLists;
    }

    public static synchronized void deleteDisplayLists(int n) {
        GL11.glDeleteLists(n, (Integer)mapDisplayLists.remove(n));
    }

    public static synchronized void deleteTexturesAndDisplayLists() {
        for (Entry entry : (Iterable<Entry>)mapDisplayLists.entrySet()) {
            GL11.glDeleteLists((Integer)entry.getKey(), (Integer)entry.getValue());
        }

        mapDisplayLists.clear();
    }

    public static synchronized ByteBuffer createDirectByteBuffer(int n) {
        return ByteBuffer.allocateDirect(n).order(ByteOrder.nativeOrder());
    }

    public static IntBuffer createDirectIntBuffer(int n) {
        return createDirectByteBuffer(n << 2).asIntBuffer();
    }

    public static FloatBuffer createDirectFloatBuffer(int n) {
        return createDirectByteBuffer(n << 2).asFloatBuffer();
    }
}
