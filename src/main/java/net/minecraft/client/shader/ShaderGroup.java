package net.minecraft.client.shader;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.vecmath.Matrix4f;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL11;

public class ShaderGroup {
    public List<Shader> listShaders = Lists.newArrayList();
    public Map mapFramebuffers = Maps.newHashMap();
    public List listFramebuffers = Lists.newArrayList();
    public IResourceManager resourceManager;
    public Framebuffer mainFramebuffer;
    public float time;
    public Matrix4f projectionMatrix;
    public float lastTickDelta;
    public int mainFramebufferWidth;
    public int mainFramebufferHeight;
    public String shaderGroupName;

    public ShaderGroup(TextureManager textureManager, IResourceManager resourceManager, Framebuffer framebuffer, ResourceLocation resourceLocation) throws JsonException {
        this.resourceManager = resourceManager;
        this.mainFramebuffer = framebuffer;
        this.time = 0.0F;
        this.lastTickDelta = 0.0F;
        this.mainFramebufferWidth = framebuffer.framebufferWidth;
        this.mainFramebufferHeight = framebuffer.framebufferHeight;
        this.shaderGroupName = resourceLocation.toString();
        this.resetProjectionMatrix();
        this.parseGroup(textureManager, resourceLocation);
    }

    public void parseGroup(TextureManager textureManager, ResourceLocation resourceLocation) throws JsonException {
        JsonParser jsonParser = new JsonParser();
        InputStream inputStream = null;

        try {
            IResource resource = this.resourceManager.getResource(resourceLocation);
            inputStream = resource.getInputStream();
            JsonObject asJsonObject = jsonParser.parse(IOUtils.toString(inputStream, Charsets.UTF_8)).getAsJsonObject();
            if (JsonUtils.jsonObjectFieldTypeIsArray(asJsonObject, "targets")) {
                JsonArray asJsonArray = asJsonObject.getAsJsonArray("targets");
                int n = 0;

                for (JsonElement jsonElement : asJsonArray) {
                    try {
                        this.initTarget(jsonElement);
                    } catch (Exception e) {
                        JsonException wrap = JsonException.wrap(e);
                        wrap.addFaultyElement("targets[" + n + "]");
                        throw wrap;
                    }

                    n++;
                }
            }

            if (JsonUtils.jsonObjectFieldTypeIsArray(asJsonObject, "passes")) {
                JsonArray asJsonArray = asJsonObject.getAsJsonArray("passes");
                int n = 0;

                for (JsonElement jsonElement : asJsonArray) {
                    try {
                        this.parsePass(textureManager, jsonElement);
                    } catch (Exception e) {
                        JsonException wrap = JsonException.wrap(e);
                        wrap.addFaultyElement("passes[" + n + "]");
                        throw wrap;
                    }

                    n++;
                }
            }
        } catch (Exception e) {
            JsonException wrap = JsonException.wrap(e);
            wrap.addFaultyFile(resourceLocation.getResourcePath());
            throw wrap;
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    public void initTarget(JsonElement jsonElement) throws JsonException {
        if (JsonUtils.jsonElementTypeIsString(jsonElement)) {
            this.addFramebuffer(jsonElement.getAsString(), this.mainFramebufferWidth, this.mainFramebufferHeight);
        } else {
            JsonObject elementAsJsonObject = JsonUtils.getElementAsJsonObject(jsonElement, "target");
            String jsonObjectStringFieldValue = JsonUtils.getJsonObjectStringFieldValue(elementAsJsonObject, "name");
            int n = JsonUtils.getJsonObjectIntegerFieldValueOrDefault(elementAsJsonObject, "width", this.mainFramebufferWidth);
            int n2 = JsonUtils.getJsonObjectIntegerFieldValueOrDefault(elementAsJsonObject, "height", this.mainFramebufferHeight);
            if (this.mapFramebuffers.containsKey(jsonObjectStringFieldValue)) {
                throw new JsonException(jsonObjectStringFieldValue + " is already defined");
            }

            this.addFramebuffer(jsonObjectStringFieldValue, n, n2);
        }
    }

    public void parsePass(TextureManager textureManager, JsonElement jsonElement) throws JsonException {
        JsonObject elementAsJsonObject = JsonUtils.getElementAsJsonObject(jsonElement, "pass");
        String jsonObjectStringFieldValue = JsonUtils.getJsonObjectStringFieldValue(elementAsJsonObject, "name");
        String jsonObjectStringFieldValue2 = JsonUtils.getJsonObjectStringFieldValue(elementAsJsonObject, "intarget");
        String jsonObjectStringFieldValue3 = JsonUtils.getJsonObjectStringFieldValue(elementAsJsonObject, "outtarget");
        Framebuffer framebuffer = this.getFramebuffer(jsonObjectStringFieldValue2);
        Framebuffer framebuffer2 = this.getFramebuffer(jsonObjectStringFieldValue3);
        if (framebuffer == null) {
            throw new JsonException("Input target '" + jsonObjectStringFieldValue2 + "' does not exist");
        }

        if (framebuffer2 == null) {
            throw new JsonException("Output target '" + jsonObjectStringFieldValue3 + "' does not exist");
        }

        Shader addShader = this.addShader(jsonObjectStringFieldValue, framebuffer, framebuffer2);
        JsonArray jsonArray = JsonUtils.getJsonObjectJsonArrayFieldOrDefault(elementAsJsonObject, "auxtargets", null);
        if (jsonArray != null) {
            int n = 0;

            for (JsonElement jsonElement2 : jsonArray) {
                try {
                    JsonObject elementAsJsonObject2 = JsonUtils.getElementAsJsonObject(jsonElement2, "auxtarget");
                    String jsonObjectStringFieldValue4 = JsonUtils.getJsonObjectStringFieldValue(elementAsJsonObject2, "name");
                    String jsonObjectStringFieldValue5 = JsonUtils.getJsonObjectStringFieldValue(elementAsJsonObject2, "id");
                    Framebuffer framebuffer3 = this.getFramebuffer(jsonObjectStringFieldValue5);
                    if (framebuffer3 == null) {
                        ResourceLocation resourceLocation = new ResourceLocation("textures/effect/" + jsonObjectStringFieldValue5 + ".png");

                        try {
                            this.resourceManager.getResource(resourceLocation);
                        } catch (FileNotFoundException e) {
                            throw new JsonException("Render target or texture '" + jsonObjectStringFieldValue5 + "' does not exist");
                        }

                        textureManager.bindTexture(resourceLocation);
                        ITextureObject texture = textureManager.getTexture(resourceLocation);
                        int jsonObjectIntegerFieldValue = JsonUtils.getJsonObjectIntegerFieldValue(elementAsJsonObject2, "width");
                        int jsonObjectIntegerFieldValue2 = JsonUtils.getJsonObjectIntegerFieldValue(elementAsJsonObject2, "height");
                        boolean jsonObjectBooleanFieldValue = JsonUtils.getJsonObjectBooleanFieldValue(elementAsJsonObject2, "bilinear");
                        if (jsonObjectBooleanFieldValue) {
                            GL11.glTexParameteri(3553, 10241, 9729);
                            GL11.glTexParameteri(3553, 10240, 9729);
                        } else {
                            GL11.glTexParameteri(3553, 10241, 9728);
                            GL11.glTexParameteri(3553, 10240, 9728);
                        }

                        addShader.addAuxFramebuffer(jsonObjectStringFieldValue4, texture.getGlTextureId(), jsonObjectIntegerFieldValue, jsonObjectIntegerFieldValue2);
                    } else {
                        addShader.addAuxFramebuffer(jsonObjectStringFieldValue4, framebuffer3, framebuffer3.framebufferTextureWidth, framebuffer3.framebufferTextureHeight);
                    }
                } catch (Exception e) {
                    JsonException wrap = JsonException.wrap(e);
                    wrap.addFaultyElement("auxtargets[" + n + "]");
                    throw wrap;
                }

                n++;
            }
        }

        JsonArray jsonArray2 = JsonUtils.getJsonObjectJsonArrayFieldOrDefault(elementAsJsonObject, "uniforms", null);
        if (jsonArray2 != null) {
            int n = 0;

            for (JsonElement jsonElement2 : jsonArray2) {
                try {
                    this.initUniform(jsonElement2);
                } catch (Exception e) {
                    JsonException wrap = JsonException.wrap(e);
                    wrap.addFaultyElement("uniforms[" + n + "]");
                    throw wrap;
                }

                n++;
            }
        }
    }

    public void initUniform(JsonElement jsonElement) throws JsonException {
        JsonObject elementAsJsonObject = JsonUtils.getElementAsJsonObject(jsonElement, "uniform");
        String jsonObjectStringFieldValue = JsonUtils.getJsonObjectStringFieldValue(elementAsJsonObject, "name");
        ShaderUniform shaderUniform = this.listShaders.get(this.listShaders.size() - 1).getShaderManager().getShaderUniform(jsonObjectStringFieldValue);
        if (shaderUniform == null) {
            throw new JsonException("Uniform '" + jsonObjectStringFieldValue + "' does not exist");
        }

        float[] floats = new float[4];
        int n = 0;

        for (JsonElement jsonElement2 : JsonUtils.getJsonObjectJsonArrayField(elementAsJsonObject, "values")) {
            try {
                floats[n] = JsonUtils.getJsonElementFloatValue(jsonElement2, "value");
            } catch (Exception e) {
                JsonException wrap = JsonException.wrap(e);
                wrap.addFaultyElement("values[" + n + "]");
                throw wrap;
            }

            n++;
        }

        switch (n) {
            case 0:
            default:
                break;
            case 1:
                shaderUniform.set(floats[0]);
                break;
            case 2:
                shaderUniform.set(floats[0], floats[1]);
                break;
            case 3:
                shaderUniform.set(floats[0], floats[1], floats[2]);
                break;
            case 4:
                shaderUniform.set(floats[0], floats[1], floats[2], floats[3]);
        }
    }

    public void addFramebuffer(String str, int n, int n2) {
        Framebuffer framebuffer = new Framebuffer(n, n2, true);
        framebuffer.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        this.mapFramebuffers.put(str, framebuffer);
        if (n == this.mainFramebufferWidth && n2 == this.mainFramebufferHeight) {
            this.listFramebuffers.add(framebuffer);
        }
    }

    public void deleteShaderGroup() {
        for (Framebuffer framebuffer : (Iterable<Framebuffer>)this.mapFramebuffers.values()) {
            framebuffer.deleteFramebuffer();
        }

        for (Shader shader : this.listShaders) {
            shader.deleteShader();
        }

        this.listShaders.clear();
    }

    public Shader addShader(String str, Framebuffer framebuffer, Framebuffer framebuffer2) throws JsonException {
        Shader shader = new Shader(this.resourceManager, str, framebuffer, framebuffer2);
        this.listShaders.add(this.listShaders.size(), shader);
        return shader;
    }

    public void resetProjectionMatrix() {
        this.projectionMatrix = new Matrix4f();
        this.projectionMatrix.setIdentity();
        this.projectionMatrix.m00 = 2.0F / this.mainFramebuffer.framebufferTextureWidth;
        this.projectionMatrix.m11 = 2.0F / -this.mainFramebuffer.framebufferTextureHeight;
        this.projectionMatrix.m22 = -0.0020001999F;
        this.projectionMatrix.m33 = 1.0F;
        this.projectionMatrix.m03 = -1.0F;
        this.projectionMatrix.m13 = 1.0F;
        this.projectionMatrix.m23 = -1.0001999F;
    }

    public void createBindFramebuffers(int n, int n2) {
        this.mainFramebufferWidth = this.mainFramebuffer.framebufferTextureWidth;
        this.mainFramebufferHeight = this.mainFramebuffer.framebufferTextureHeight;
        this.resetProjectionMatrix();

        for (Shader shader : this.listShaders) {
            shader.setProjectionMatrix(this.projectionMatrix);
        }

        for (Framebuffer framebuffer : (Iterable<Framebuffer>)this.listFramebuffers) {
            framebuffer.createBindFramebuffer(n, n2);
        }
    }

    public void loadShaderGroup(float f) {
        if (f < this.lastTickDelta) {
            this.time = this.time + (1.0F - this.lastTickDelta);
            this.time += f;
        } else {
            this.time = this.time + (f - this.lastTickDelta);
        }

        this.lastTickDelta = f;

        while (this.time > 20.0F) {
            this.time -= 20.0F;
        }

        for (Shader shader : this.listShaders) {
            shader.loadShader(this.time / 20.0F);
        }
    }

    public String getShaderGroupName() {
        return this.shaderGroupName;
    }

    public Framebuffer getFramebuffer(String str) {
        return str == null ? null : (str.equals("minecraft:main") ? this.mainFramebuffer : (Framebuffer)this.mapFramebuffers.get(str));
    }

    public List<Shader> getListShaders() {
        return this.listShaders;
    }
}
