package net.minecraft.client.shader;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonBlendingMode;
import net.minecraft.client.util.JsonException;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShaderManager {
   public static Logger logger = LogManager.getLogger();
   public static ShaderDefault defaultShaderUniform = new ShaderDefault();
   public static ShaderManager staticShaderManager = null;
   public static int currentProgram = -1;
   public static boolean active = true;
   public Map<String, Object> shaderSamplers = Maps.newHashMap();
   public List<String> samplerNames = Lists.newArrayList();
   public List<Integer> shaderSamplerLocations = Lists.newArrayList();
   public List<ShaderUniform> shaderUniforms = Lists.newArrayList();
   public List<Integer> shaderUniformLocations = Lists.newArrayList();
   public Map<String, ShaderUniform> mappedShaderUniforms = Maps.newHashMap();
   public String programFilename;
   public List<Integer> attribLocations;
   public List<String> attributes;
   public boolean isDirty;
   public JsonBlendingMode blendState;
   public boolean useFaceCulling;
   public ShaderLoader vertexShaderLoader;
   public ShaderLoader fragmentShaderLoader;
   public int program;

   public ShaderManager(IResourceManager resourceManager, String programName) throws IOException, JsonException {
      JsonParser jsonParser = new JsonParser();
      ResourceLocation resourceLocation = new ResourceLocation("shaders/program/" + programName + ".json");
      this.programFilename = programName;
      InputStream inputStream = null;

      try {
         inputStream = resourceManager.getResource(resourceLocation).getInputStream();
         JsonObject asJsonObject = jsonParser.parse(IOUtils.toString(inputStream, Charsets.UTF_8)).getAsJsonObject();
         String string = JsonUtils.getString(asJsonObject, "vertex");
         String string2 = JsonUtils.getString(asJsonObject, "fragment");
         JsonArray jsonArray = JsonUtils.getJsonArray(asJsonObject, "samplers", (JsonArray)null);
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

         JsonArray jsonArray2 = JsonUtils.getJsonArray(asJsonObject, "attributes", (JsonArray)null);
         if (jsonArray2 != null) {
            int n = 0;
            this.attribLocations = Lists.newArrayListWithCapacity(jsonArray2.size());
            this.attributes = Lists.newArrayListWithCapacity(jsonArray2.size());

            for (JsonElement jsonElement : jsonArray2) {
               try {
                  this.attributes.add(JsonUtils.getString(jsonElement, "attribute"));
               } catch (Exception e) {
                  JsonException wrap = JsonException.wrap(e);
                  wrap.addFaultyElement("attributes[" + n + "]");
                  throw wrap;
               }

               n++;
            }
         } else {
            this.attribLocations = null;
            this.attributes = null;
         }

         JsonArray jsonArray3 = JsonUtils.getJsonArray(asJsonObject, "uniforms", (JsonArray)null);
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

         this.blendState = JsonBlendingMode.deserializeBlendState(JsonUtils.getJsonObject(asJsonObject, "blend", (JsonObject)null));
         this.useFaceCulling = JsonUtils.getBoolean(asJsonObject, "cull", true);
         this.vertexShaderLoader = ShaderLoader.loadShader(resourceManager, ShaderLoader.ShaderType.VERTEX, string);
         this.fragmentShaderLoader = ShaderLoader.loadShader(resourceManager, ShaderLoader.ShaderType.FRAGMENT, string2);
         this.program = ShaderLinkHelper.getStaticShaderLinkHelper().createProgram();
         ShaderLinkHelper.getStaticShaderLinkHelper().linkProgram(this);
         this.setupUniforms();
         if (this.attributes != null) {
            for (String str : this.attributes) {
               int glGetAttribLocation = OpenGlHelper.glGetAttribLocation(this.program, str);
               this.attribLocations.add(glGetAttribLocation);
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
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit + i);
            GlStateManager.bindTexture(0);
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

      if (this.useFaceCulling) {
         GlStateManager.enableCull();
      } else {
         GlStateManager.disableCull();
      }

      for (int i = 0; i < this.shaderSamplerLocations.size(); i++) {
         if (this.shaderSamplers.get(this.samplerNames.get(i)) != null) {
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit + i);
            GlStateManager.enableTexture2D();
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
               GlStateManager.bindTexture(n);
               OpenGlHelper.glUniform1i(OpenGlHelper.glGetUniformLocation(this.program, this.samplerNames.get(i)), i);
            }
         }
      }

      for (ShaderUniform shaderUniform : this.shaderUniforms) {
         shaderUniform.upload();
      }
   }

   public void markDirty() {
      this.isDirty = true;
   }

   public ShaderUniform getShaderUniform(String str) {
      return this.mappedShaderUniforms.containsKey(str) ? this.mappedShaderUniforms.get(str) : null;
   }

   public ShaderUniform getShaderUniformOrDefault(String str) {
      return this.mappedShaderUniforms.containsKey(str) ? this.mappedShaderUniforms.get(str) : defaultShaderUniform;
   }

   public void setupUniforms() {
      int n = 0;

      for (int i = 0; n < this.samplerNames.size(); i++) {
         String str = this.samplerNames.get(n);
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

      for (ShaderUniform shaderUniform : this.shaderUniforms) {
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

   public void parseSampler(JsonElement jsonElement) throws JsonException {
      JsonObject jsonObject = JsonUtils.getJsonObject(jsonElement, "sampler");
      String string = JsonUtils.getString(jsonObject, "name");
      if (!JsonUtils.isString(jsonObject, "file")) {
         this.shaderSamplers.put(string, null);
         this.samplerNames.add(string);
      } else {
         this.samplerNames.add(string);
      }
   }

   public void addSamplerTexture(String str, Object obj) {
      if (this.shaderSamplers.containsKey(str)) {
         this.shaderSamplers.remove(str);
      }

      this.shaderSamplers.put(str, obj);
      this.markDirty();
   }

   public void parseUniform(JsonElement jsonElement) throws JsonException {
      JsonObject jsonObject = JsonUtils.getJsonObject(jsonElement, "uniform");
      String string = JsonUtils.getString(jsonObject, "name");
      int parseType = ShaderUniform.parseType(JsonUtils.getString(jsonObject, "type"));
      int int2 = JsonUtils.getInt(jsonObject, "count");
      float[] floats = new float[Math.max(int2, 16)];
      JsonArray jsonArray = JsonUtils.getJsonArray(jsonObject, "values");
      if (jsonArray.size() != int2 && jsonArray.size() > 1) {
         throw new JsonException("Invalid amount of values specified (expected " + int2 + ", found " + jsonArray.size() + ")");
      }

      int n = 0;

      for (JsonElement jsonElement2 : jsonArray) {
         try {
            floats[n] = JsonUtils.getFloat(jsonElement2, "value");
         } catch (Exception e) {
            JsonException wrap = JsonException.wrap(e);
            wrap.addFaultyElement("values[" + n + "]");
            throw wrap;
         }

         n++;
      }

      if (int2 > 1 && jsonArray.size() == 1) {
         while (n < int2) {
            floats[n] = floats[0];
            n++;
         }
      }

      int n2 = int2 > 1 && int2 <= 4 && parseType < 8 ? int2 - 1 : 0;
      ShaderUniform shaderUniform = new ShaderUniform(string, parseType + n2, int2, this);
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
