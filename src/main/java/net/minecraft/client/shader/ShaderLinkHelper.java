package net.minecraft.client.shader;

import java.io.IOException;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.util.JsonException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ShaderLinkHelper {
   public static Logger logger = LogManager.getLogger();
   public static ShaderLinkHelper staticShaderLinkHelper;

   public static void setNewStaticShaderLinkHelper() {
      staticShaderLinkHelper = new ShaderLinkHelper();
   }

   public static ShaderLinkHelper getStaticShaderLinkHelper() {
      return staticShaderLinkHelper;
   }

   public void deleteShader(ShaderManager shaderManager) {
      shaderManager.getFragmentShaderLoader().deleteShader(shaderManager);
      shaderManager.getVertexShaderLoader().deleteShader(shaderManager);
      OpenGlHelper.glDeleteProgram(shaderManager.getProgram());
   }

   public int createProgram() throws JsonException {
      int glCreateProgram = OpenGlHelper.glCreateProgram();
      if (glCreateProgram <= 0) {
         throw new JsonException("Could not create shader program (returned program ID " + glCreateProgram + ")");
      } else {
         return glCreateProgram;
      }
   }

   public void linkProgram(ShaderManager manager) throws IOException {
      manager.getFragmentShaderLoader().attachShader(manager);
      manager.getVertexShaderLoader().attachShader(manager);
      OpenGlHelper.glLinkProgram(manager.getProgram());
      int glGetProgrami = OpenGlHelper.glGetProgrami(manager.getProgram(), OpenGlHelper.GL_LINK_STATUS);
      if (glGetProgrami == 0) {
         logger.warn(
            "Error encountered when linking program containing VS "
               + manager.getVertexShaderLoader().getShaderFilename()
               + " and FS "
               + manager.getFragmentShaderLoader().getShaderFilename()
               + ". Log output:"
         );
         logger.warn(OpenGlHelper.glGetProgramInfoLog(manager.getProgram(), 32768));
      }
   }
}
