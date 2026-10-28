package net.minecraft.client.shader;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.shader.ShaderLoader.ShaderType;
import net.minecraft.client.util.JsonBlendingMode;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class ShaderManager {
    public static Logger logger = LogManager.getLogger();
    public static ShaderDefault defaultShaderUniform = new ShaderDefault();
    public static ShaderManager staticShaderManager = null;
    public static int currentProgram = -1;
    public static boolean active = true;
    public Map shaderSamplers = Maps.newHashMap();
    public List samplerNames = Lists.newArrayList();
    public List shaderSamplerLocations = Lists.newArrayList();
    public List shaderUniforms = Lists.newArrayList();
    public List shaderUniformLocations = Lists.newArrayList();
    public Map mappedShaderUniforms = Maps.newHashMap();
    public String programFilename;
    public List attribLocs;
    public List attribNames;
    public boolean isDirty;
    public JsonBlendingMode blendState;
    public boolean useFaceCulling;
    public ShaderLoader vertexShaderLoader;
    public ShaderLoader fragmentShaderLoader;
    public int program;

    public ShaderManager(IResourceManager resourceManager, String programName) throws JsonException {
        JsonParser jsonParser = new JsonParser();
        ResourceLocation resourceLocation = new ResourceLocation("shaders/program/" + programName + ".json");
        this.programFilename = programName;
        InputStream inputStream = null;

        try {
            inputStream = resourceManager.getResource(resourceLocation).getInputStream();
            JsonObject asJsonObject = jsonParser.parse(IOUtils.toString(inputStream, Charsets.UTF_8)).getAsJsonObject();
            String jsonObjectStringFieldValue = JsonUtils.getJsonObjectStringFieldValue(asJsonObject, "vertex");
            String jsonObjectStringFieldValue2 = JsonUtils.getJsonObjectStringFieldValue(asJsonObject, "fragment");
            JsonArray jsonArray = JsonUtils.getJsonObjectJsonArrayFieldOrDefault(asJsonObject, "samplers", null);
            if (jsonArray != null) {
                int n = 0;

                for (JsonElement jsonElement : jsonArray) {
                    try {
                        this.parseSampler(jsonElement);
                    } catch (Exception e) {
                        JsonException wrap = JsonException.wrap(e);
                        wrap.addFaultyElement("samplers[" + n + "]");
                        throw wrap;
                    }

                    n++;
                }
            }

            JsonArray jsonArray2 = JsonUtils.getJsonObjectJsonArrayFieldOrDefault(asJsonObject, "attributes", null);
            if (jsonArray2 != null) {
                int n = 0;
                this.attribLocs = Lists.newArrayListWithCapacity(jsonArray2.size());
                this.attribNames = Lists.newArrayListWithCapacity(jsonArray2.size());

                for (JsonElement jsonElement : jsonArray2) {
                    try {
                        this.attribNames.add(JsonUtils.getJsonElementStringValue(jsonElement, "attribute"));
                    } catch (Exception e) {
                        JsonException wrap = JsonException.wrap(e);
                        wrap.addFaultyElement("attributes[" + n + "]");
                        throw wrap;
                    }

                    n++;
                }
            } else {
                this.attribLocs = null;
                this.attribNames = null;
            }

            JsonArray jsonArray3 = JsonUtils.getJsonObjectJsonArrayFieldOrDefault(asJsonObject, "uniforms", null);
            if (jsonArray3 != null) {
                int n = 0;

                for (JsonElement jsonElement : jsonArray3) {
                    try {
                        this.parseUniform(jsonElement);
                    } catch (Exception e) {
                        JsonException wrap = JsonException.wrap(e);
                        wrap.addFaultyElement("uniforms[" + n + "]");
                        throw wrap;
                    }

                    n++;
                }
            }

            this.blendState = JsonBlendingMode.deserializeBlendState(JsonUtils.getJsonObjectFieldOrDefault(asJsonObject, "blend", null));
            this.useFaceCulling = JsonUtils.getJsonObjectBooleanFieldValueOrDefault(asJsonObject, "cull", true);
            this.vertexShaderLoader = ShaderLoader.loadShader(resourceManager, ShaderType.VERTEX, jsonObjectStringFieldValue);
            this.fragmentShaderLoader = ShaderLoader.loadShader(resourceManager, ShaderType.FRAGMENT, jsonObjectStringFieldValue2);
            this.program = ShaderLinkHelper.getStaticShaderLinkHelper().createProgram();
            ShaderLinkHelper.getStaticShaderLinkHelper().linkProgram(this);
            this.setupUniforms();
            if (this.attribNames != null) {
                for (String str : (Iterable<String>)this.attribNames) {
                    int glGetAttribLocation = OpenGlHelper.glGetAttribLocation(this.program, str);
                    this.attribLocs.add(glGetAttribLocation);
                }
            }
        } catch (Exception e) {
            JsonException wrap = JsonException.wrap(e);
            wrap.addFaultyFile(resourceLocation.getResourcePath());
            throw wrap;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }

        this.markDirty();
    }

    public void deleteShader() {
        ShaderLinkHelper.getStaticShaderLinkHelper().deleteShader(this);
    }

    public void endShader() {
        OpenGlHelper.glUseProgram(0);
        currentProgram = -1;
        staticShaderManager = null;
        active = true;

        for (int i = 0; i < this.shaderSamplerLocations.size(); i++) {
            if (this.shaderSamplers.get(this.samplerNames.get(i)) != null) {
                GL13.glActiveTexture(33984 + i);
                GL11.glBindTexture(3553, 0);
            }
        }
    }

    public void useShader() {
        this.isDirty = false;
        staticShaderManager = this;
        this.blendState.enable();
        if (this.program != currentProgram) {
            OpenGlHelper.glUseProgram(this.program);
            currentProgram = this.program;
        }

        if (active != this.useFaceCulling) {
            active = this.useFaceCulling;
            if (this.useFaceCulling) {
                GL11.glEnable(2884);
            } else {
                GL11.glDisable(2884);
            }
        }

        for (int i = 0; i < this.shaderSamplerLocations.size(); i++) {
            if (this.shaderSamplers.get(this.samplerNames.get(i)) != null) {
                GL13.glActiveTexture(33984 + i);
                GL11.glEnable(3553);
                Object obj = this.shaderSamplers.get(this.samplerNames.get(i));
                int n = -1;
                if (obj instanceof Framebuffer) {
                    n = ((Framebuffer)obj).framebufferTexture;
                } else if (obj instanceof ITextureObject) {
                    n = ((ITextureObject)obj).getGlTextureId();
                } else if (obj instanceof Integer) {
                    n = (Integer)obj;
                }

                if (n != -1) {
                    GL11.glBindTexture(3553, n);
                    OpenGlHelper.glUniform1i(OpenGlHelper.glGetUniformLocation(this.program, (CharSequence)this.samplerNames.get(i)), i);
                }
            }
        }

        for (ShaderUniform shaderUniform : (Iterable<ShaderUniform>)this.shaderUniforms) {
            shaderUniform.upload();
        }
    }

    public void markDirty() {
        this.isDirty = true;
    }

    public ShaderUniform getShaderUniform(String str) {
        return this.mappedShaderUniforms.containsKey(str) ? (ShaderUniform)this.mappedShaderUniforms.get(str) : null;
    }

    public ShaderUniform getShaderUniformOrDefault(String str) {
        return this.mappedShaderUniforms.containsKey(str) ? (ShaderUniform)this.mappedShaderUniforms.get(str) : defaultShaderUniform;
    }

    public void setupUniforms() {
        int n = 0;

        for (int i = 0; n < this.samplerNames.size(); i++) {
            String str = (String)this.samplerNames.get(n);
            int glGetUniformLocation = OpenGlHelper.glGetUniformLocation(this.program, str);
            if (glGetUniformLocation == -1) {
                logger.warn("Shader " + this.programFilename + "could not find sampler named " + str + " in the specified shader program.");
                this.shaderSamplers.remove(str);
                this.samplerNames.remove(i);
                i--;
            } else {
                this.shaderSamplerLocations.add(glGetUniformLocation);
            }

            n++;
        }

        for (ShaderUniform shaderUniform : (Iterable<ShaderUniform>)this.shaderUniforms) {
            String shaderName = shaderUniform.getShaderName();
            int glGetUniformLocation = OpenGlHelper.glGetUniformLocation(this.program, shaderName);
            if (glGetUniformLocation == -1) {
                logger.warn("Could not find uniform named " + shaderName + " in the specified shader program.");
            } else {
                this.shaderUniformLocations.add(glGetUniformLocation);
                shaderUniform.setUniformLocation(glGetUniformLocation);
                this.mappedShaderUniforms.put(shaderName, shaderUniform);
            }
        }
    }

    public void parseSampler(JsonElement jsonElement) {
        JsonObject elementAsJsonObject = JsonUtils.getElementAsJsonObject(jsonElement, "sampler");
        String jsonObjectStringFieldValue = JsonUtils.getJsonObjectStringFieldValue(elementAsJsonObject, "name");
        if (!JsonUtils.jsonObjectFieldTypeIsString(elementAsJsonObject, "file")) {
            this.shaderSamplers.put(jsonObjectStringFieldValue, null);
            this.samplerNames.add(jsonObjectStringFieldValue);
        } else {
            this.samplerNames.add(jsonObjectStringFieldValue);
        }
    }

    public void addSamplerTexture(String str, Object obj) {
        this.shaderSamplers.remove(str);
        this.shaderSamplers.put(str, obj);
        this.markDirty();
    }

    public void parseUniform(JsonElement jsonElement) throws JsonException {
        JsonObject elementAsJsonObject = JsonUtils.getElementAsJsonObject(jsonElement, "uniform");
        String jsonObjectStringFieldValue = JsonUtils.getJsonObjectStringFieldValue(elementAsJsonObject, "name");
        int parseType = ShaderUniform.parseType(JsonUtils.getJsonObjectStringFieldValue(elementAsJsonObject, "type"));
        int jsonObjectIntegerFieldValue = JsonUtils.getJsonObjectIntegerFieldValue(elementAsJsonObject, "count");
        float[] floats = new float[Math.max(jsonObjectIntegerFieldValue, 16)];
        JsonArray jsonObjectJsonArrayField = JsonUtils.getJsonObjectJsonArrayField(elementAsJsonObject, "values");
        if (jsonObjectJsonArrayField.size() != jsonObjectIntegerFieldValue && jsonObjectJsonArrayField.size() > 1) {
            throw new JsonException("Invalid amount of values specified (expected " + jsonObjectIntegerFieldValue + ", found " + jsonObjectJsonArrayField.size() + ")");
        }

        int n = 0;

        for (JsonElement jsonElement2 : jsonObjectJsonArrayField) {
            try {
                floats[n] = JsonUtils.getJsonElementFloatValue(jsonElement2, "value");
            } catch (Exception e) {
                JsonException wrap = JsonException.wrap(e);
                wrap.addFaultyElement("values[" + n + "]");
                throw wrap;
            }

            n++;
        }

        if (jsonObjectIntegerFieldValue > 1 && jsonObjectJsonArrayField.size() == 1) {
            while (n < jsonObjectIntegerFieldValue) {
                floats[n] = floats[0];
                n++;
            }
        }

        int n2 = jsonObjectIntegerFieldValue > 1 && jsonObjectIntegerFieldValue <= 4 && parseType < 8 ? jsonObjectIntegerFieldValue - 1 : 0;
        ShaderUniform shaderUniform = new ShaderUniform(jsonObjectStringFieldValue, parseType + n2, jsonObjectIntegerFieldValue, this);
        if (parseType <= 3) {
            shaderUniform.set((int)floats[0], (int)floats[1], (int)floats[2], (int)floats[3]);
        } else if (parseType <= 7) {
            shaderUniform.process(floats[0], floats[1], floats[2], floats[3]);
        } else {
            shaderUniform.set(floats);
        }

        this.shaderUniforms.add(shaderUniform);
    }

    public ShaderLoader getVertexShaderLoader() {
        return this.vertexShaderLoader;
    }

    public ShaderLoader getFragmentShaderLoader() {
        return this.fragmentShaderLoader;
    }

    public int getProgram() {
        return this.program;
    }
}
