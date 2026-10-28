package net.minecraft.client.shader;

import com.google.common.collect.Maps;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Map;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.BufferUtils;

public class ShaderLoader {
    public int shaderAttachCount = 0;
    public ShaderLoader.ShaderType shaderType;
    public int shader;
    public String shaderFilename;

    public ShaderLoader(ShaderLoader.ShaderType type, int shaderId, String filename) {
        this.shaderType = type;
        this.shader = shaderId;
        this.shaderFilename = filename;
    }

    public void attachShader(ShaderManager manager) {
        this.shaderAttachCount++;
        OpenGlHelper.glAttachShader(manager.getProgram(), this.shader);
    }

    public void deleteShader(ShaderManager manager) {
        this.shaderAttachCount--;
        if (this.shaderAttachCount <= 0) {
            OpenGlHelper.glDeleteShader(this.shader);
            this.shaderType.getLoadedShaders().remove(this.shaderFilename);
        }
    }

    public String getShaderFilename() {
        return this.shaderFilename;
    }

    public static ShaderLoader loadShader(IResourceManager resourceManager, ShaderLoader.ShaderType type, String filename) throws IOException {
        ShaderLoader shaderLoader = (ShaderLoader)type.getLoadedShaders().get(filename);
        if (shaderLoader == null) {
            ResourceLocation resourceLocation = new ResourceLocation("shaders/program/" + filename + type.getShaderExtension());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(resourceManager.getResource(resourceLocation).getInputStream());
            byte[] toByteArray = IOUtils.toByteArray(bufferedInputStream);
            ByteBuffer createByteBuffer = BufferUtils.createByteBuffer(toByteArray.length);
            createByteBuffer.put(toByteArray);
            ((Buffer)createByteBuffer).position(0);
            int glCreateShader = OpenGlHelper.glCreateShader(type.getShaderMode());
            OpenGlHelper.glShaderSource(glCreateShader, createByteBuffer);
            OpenGlHelper.glCompileShader(glCreateShader);
            if (OpenGlHelper.glGetShaderi(glCreateShader, OpenGlHelper.GL_COMPILE_STATUS) == 0) {
                String trim = StringUtils.trim(OpenGlHelper.glGetShaderInfoLog(glCreateShader, 32768));
                JsonException jsonException = new JsonException("Couldn't compile " + type.getShaderName() + " program: " + trim);
                jsonException.addFaultyFile(resourceLocation.getResourcePath());
                throw jsonException;
            }

            shaderLoader = new ShaderLoader(type, glCreateShader, filename);
            type.getLoadedShaders().put(filename, shaderLoader);
        }

        return shaderLoader;
    }

    public enum ShaderType {
        VERTEX("VERTEX", 0, "vertex", ".vsh", OpenGlHelper.GL_VERTEX_SHADER),
        FRAGMENT("FRAGMENT", 1, "fragment", ".fsh", OpenGlHelper.GL_FRAGMENT_SHADER);

        public Map loadedShaders = Maps.newHashMap();
        public String shaderName;
        public String shaderExtension;
        public int shaderMode;

        public String getShaderName() {
            return this.shaderName;
        }

        public String getShaderExtension() {
            return this.shaderExtension;
        }

        public int getShaderMode() {
            return this.shaderMode;
        }

        public Map getLoadedShaders() {
            return this.loadedShaders;
        }

        ShaderType(String str, int n, String str2, String str3, int n2) {
            this.shaderName = str2;
            this.shaderExtension = str3;
            this.shaderMode = n2;
        }
    }
}
