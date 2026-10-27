package net.minecraft.client.renderer;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.optifine.Config;
import net.optifine.SmartAnimations;
import net.optifine.render.GlAlphaState;
import net.optifine.render.GlBlendState;
import net.optifine.shaders.Shaders;
import net.optifine.util.LockCounter;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

public class GlStateManager {
   public static GlStateManager.AlphaState alphaState = new GlStateManager.AlphaState();
   public static GlStateManager.BooleanState lightingState = new GlStateManager.BooleanState(2896);
   public static GlStateManager.BooleanState[] lightState = new GlStateManager.BooleanState[8];
   public static GlStateManager.ColorMaterialState colorMaterialState = new GlStateManager.ColorMaterialState();
   public static GlStateManager.BlendState blendState = new GlStateManager.BlendState();
   public static GlStateManager.DepthState depthState = new GlStateManager.DepthState();
   public static GlStateManager.FogState fogState = new GlStateManager.FogState();
   public static GlStateManager.CullState cullState = new GlStateManager.CullState();
   public static GlStateManager.PolygonOffsetState polygonOffsetState = new GlStateManager.PolygonOffsetState();
   public static GlStateManager.ColorLogicState colorLogicState = new GlStateManager.ColorLogicState();
   public static GlStateManager.TexGenState texGenState = new GlStateManager.TexGenState();
   public static GlStateManager.ClearState clearState = new GlStateManager.ClearState();
   public static GlStateManager.StencilState stencilState = new GlStateManager.StencilState();
   public static GlStateManager.BooleanState normalizeState = new GlStateManager.BooleanState(2977);
   public static int activeTextureUnit = 0;
   public static GlStateManager.TextureState[] textureState = new GlStateManager.TextureState[32];
   public static int activeShadeModel = 7425;
   public static GlStateManager.BooleanState rescaleNormalState = new GlStateManager.BooleanState(32826);
   public static GlStateManager.ColorMask colorMaskState = new GlStateManager.ColorMask();
   public static GlStateManager.Color colorState = new GlStateManager.Color();
   public static boolean clearEnabled = true;
   public static LockCounter alphaLock = new LockCounter();
   public static GlAlphaState alphaLockState = new GlAlphaState();
   public static LockCounter blendLock = new LockCounter();
   public static GlBlendState blendLockState = new GlBlendState();
   public static boolean creatingDisplayList = false;

   public static void pushAttrib() {
      GL11.glPushAttrib(8256);
   }

   public static void popAttrib() {
      GL11.glPopAttrib();
   }

   public static void disableAlpha() {
      if (alphaLock.isLocked()) {
         alphaLockState.setDisabled();
      } else {
         alphaState.alphaTest.setDisabled();
      }
   }

   public static void enableAlpha() {
      if (alphaLock.isLocked()) {
         alphaLockState.setEnabled();
      } else {
         alphaState.alphaTest.setEnabled();
      }
   }

   public static void alphaFunc(int func, float ref) {
      if (alphaLock.isLocked()) {
         alphaLockState.setFuncRef(func, ref);
      } else if (func != alphaState.func || ref != alphaState.ref) {
         alphaState.func = func;
         alphaState.ref = ref;
         GL11.glAlphaFunc(func, ref);
      }
   }

   public static void enableLighting() {
      lightingState.setEnabled();
   }

   public static void disableLighting() {
      lightingState.setDisabled();
   }

   public static void enableLight(int light) {
      lightState[light].setEnabled();
   }

   public static void disableLight(int light) {
      lightState[light].setDisabled();
   }

   public static void enableColorMaterial() {
      colorMaterialState.colorMaterial.setEnabled();
   }

   public static void disableColorMaterial() {
      colorMaterialState.colorMaterial.setDisabled();
   }

   public static void colorMaterial(int face, int mode) {
      if (face != colorMaterialState.face || mode != colorMaterialState.mode) {
         colorMaterialState.face = face;
         colorMaterialState.mode = mode;
         GL11.glColorMaterial(face, mode);
      }
   }

   public static void disableDepth() {
      depthState.depthTest.setDisabled();
   }

   public static void enableDepth() {
      depthState.depthTest.setEnabled();
   }

   public static void depthFunc(int depthFunc) {
      if (depthFunc != depthState.depthFunc) {
         depthState.depthFunc = depthFunc;
         GL11.glDepthFunc(depthFunc);
      }
   }

   public static void depthMask(boolean flagIn) {
      if (flagIn != depthState.maskEnabled) {
         depthState.maskEnabled = flagIn;
         GL11.glDepthMask(flagIn);
      }
   }

   public static void disableBlend() {
      if (blendLock.isLocked()) {
         blendLockState.setDisabled();
      } else {
         blendState.blend.setDisabled();
      }
   }

   public static void enableBlend() {
      if (blendLock.isLocked()) {
         blendLockState.setEnabled();
      } else {
         blendState.blend.setEnabled();
      }
   }

   public static void blendFunc(int srcFactor, int dstFactor) {
      if (blendLock.isLocked()) {
         blendLockState.setFactors(srcFactor, dstFactor);
      } else if (srcFactor != blendState.srcFactor
         || dstFactor != blendState.dstFactor
         || srcFactor != blendState.srcFactorAlpha
         || dstFactor != blendState.dstFactorAlpha) {
         blendState.srcFactor = srcFactor;
         blendState.dstFactor = dstFactor;
         blendState.srcFactorAlpha = srcFactor;
         blendState.dstFactorAlpha = dstFactor;
         if (Config.isShaders()) {
            Shaders.uniform_blendFunc.setValue(srcFactor, dstFactor, srcFactor, dstFactor);
         }

         GL11.glBlendFunc(srcFactor, dstFactor);
      }
   }

   public static void tryBlendFuncSeparate(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha) {
      if (blendLock.isLocked()) {
         blendLockState.setFactors(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
      } else if (srcFactor != blendState.srcFactor
         || dstFactor != blendState.dstFactor
         || srcFactorAlpha != blendState.srcFactorAlpha
         || dstFactorAlpha != blendState.dstFactorAlpha) {
         blendState.srcFactor = srcFactor;
         blendState.dstFactor = dstFactor;
         blendState.srcFactorAlpha = srcFactorAlpha;
         blendState.dstFactorAlpha = dstFactorAlpha;
         if (Config.isShaders()) {
            Shaders.uniform_blendFunc.setValue(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
         }

         OpenGlHelper.glBlendFunc(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
      }
   }

   public static void enableFog() {
      fogState.fog.setEnabled();
   }

   public static void disableFog() {
      fogState.fog.setDisabled();
   }

   public static void setFog(int param) {
      if (param != fogState.mode) {
         fogState.mode = param;
         GL11.glFogi(2917, param);
         if (Config.isShaders()) {
            Shaders.setFogMode(param);
         }
      }
   }

   public static void setFogDensity(float param) {
      if (param < 0.0F) {
         param = 0.0F;
      }

      if (param != fogState.density) {
         fogState.density = param;
         GL11.glFogf(2914, param);
         if (Config.isShaders()) {
            Shaders.setFogDensity(param);
         }
      }
   }

   public static void setFogStart(float param) {
      if (param != fogState.start) {
         fogState.start = param;
         GL11.glFogf(2915, param);
      }
   }

   public static void setFogEnd(float param) {
      if (param != fogState.end) {
         fogState.end = param;
         GL11.glFogf(2916, param);
      }
   }

   public static void glFog(int n, FloatBuffer floatBuffer) {
      GL11.glFog(n, floatBuffer);
   }

   public static void glFogi(int n, int n2) {
      GL11.glFogi(n, n2);
   }

   public static void enableCull() {
      cullState.cullFace.setEnabled();
   }

   public static void disableCull() {
      cullState.cullFace.setDisabled();
   }

   public static void cullFace(int mode) {
      if (mode != cullState.mode) {
         cullState.mode = mode;
         GL11.glCullFace(mode);
      }
   }

   public static void enablePolygonOffset() {
      polygonOffsetState.polygonOffsetFill.setEnabled();
   }

   public static void disablePolygonOffset() {
      polygonOffsetState.polygonOffsetFill.setDisabled();
   }

   public static void doPolygonOffset(float factor, float units) {
      if (factor != polygonOffsetState.factor || units != polygonOffsetState.units) {
         polygonOffsetState.factor = factor;
         polygonOffsetState.units = units;
         GL11.glPolygonOffset(factor, units);
      }
   }

   public static void enableColorLogic() {
      colorLogicState.colorLogicOp.setEnabled();
   }

   public static void disableColorLogic() {
      colorLogicState.colorLogicOp.setDisabled();
   }

   public static void colorLogicOp(int opcode) {
      if (opcode != colorLogicState.opcode) {
         colorLogicState.opcode = opcode;
         GL11.glLogicOp(opcode);
      }
   }

   public static void enableTexGenCoord(GlStateManager.TexGen texGen) {
      texGenCoord(texGen).textureGen.setEnabled();
   }

   public static void disableTexGenCoord(GlStateManager.TexGen texGen) {
      texGenCoord(texGen).textureGen.setDisabled();
   }

   public static void texGen(GlStateManager.TexGen texGen, int param) {
      GlStateManager.TexGenCoord texGenCoord2 = texGenCoord(texGen);
      if (param != texGenCoord2.param) {
         texGenCoord2.param = param;
         GL11.glTexGeni(texGenCoord2.coord, 9472, param);
      }
   }

   public static void texGen(GlStateManager.TexGen texGen, int pname, FloatBuffer params) {
      GL11.glTexGen(texGenCoord(texGen).coord, pname, params);
   }

   public static GlStateManager.TexGenCoord texGenCoord(GlStateManager.TexGen texGen) {
      switch (texGen) {
         case S:
            return texGenState.s;
         case T:
            return texGenState.t;
         case R:
            return texGenState.r;
         case Q:
            return texGenState.q;
         default:
            return texGenState.s;
      }
   }

   public static void setActiveTexture(int texture) {
      if (activeTextureUnit != texture - OpenGlHelper.defaultTexUnit) {
         activeTextureUnit = texture - OpenGlHelper.defaultTexUnit;
         OpenGlHelper.setActiveTexture(texture);
      }
   }

   public static void enableTexture2D() {
      textureState[activeTextureUnit].texture2DState.setEnabled();
   }

   public static void disableTexture2D() {
      textureState[activeTextureUnit].texture2DState.setDisabled();
   }

   public static int generateTexture() {
      return GL11.glGenTextures();
   }

   public static void deleteTexture(int texture) {
      if (texture != 0) {
         GL11.glDeleteTextures(texture);

         for (GlStateManager.TextureState textureState2 : textureState) {
            if (textureState2.textureName == texture) {
               textureState2.textureName = 0;
            }
         }
      }
   }

   public static void bindTexture(int texture) {
      if (texture != textureState[activeTextureUnit].textureName) {
         textureState[activeTextureUnit].textureName = texture;
         GL11.glBindTexture(3553, texture);
         if (SmartAnimations.isActive()) {
            SmartAnimations.textureRendered(texture);
         }
      }
   }

   public static void enableNormalize() {
      normalizeState.setEnabled();
   }

   public static void disableNormalize() {
      normalizeState.setDisabled();
   }

   public static void shadeModel(int mode) {
      if (mode != activeShadeModel) {
         activeShadeModel = mode;
         GL11.glShadeModel(mode);
      }
   }

   public static void enableRescaleNormal() {
      rescaleNormalState.setEnabled();
   }

   public static void disableRescaleNormal() {
      rescaleNormalState.setDisabled();
   }

   public static void viewport(int x, int y, int width, int height) {
      GL11.glViewport(x, y, width, height);
   }

   public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
      if (red != colorMaskState.red || green != colorMaskState.green || blue != colorMaskState.blue || alpha != colorMaskState.alpha) {
         colorMaskState.red = red;
         colorMaskState.green = green;
         colorMaskState.blue = blue;
         colorMaskState.alpha = alpha;
         GL11.glColorMask(red, green, blue, alpha);
      }
   }

   public static void clearDepth(double depth) {
      if (depth != clearState.depth) {
         clearState.depth = depth;
         GL11.glClearDepth(depth);
      }
   }

   public static void clearColor(float red, float green, float blue, float alpha) {
      if (red != clearState.color.red || green != clearState.color.green || blue != clearState.color.blue || alpha != clearState.color.alpha) {
         clearState.color.red = red;
         clearState.color.green = green;
         clearState.color.blue = blue;
         clearState.color.alpha = alpha;
         GL11.glClearColor(red, green, blue, alpha);
      }
   }

   public static void clear(int mask) {
      if (clearEnabled) {
         GL11.glClear(mask);
      }
   }

   public static void matrixMode(int mode) {
      GL11.glMatrixMode(mode);
   }

   public static void loadIdentity() {
      GL11.glLoadIdentity();
   }

   public static void pushMatrix() {
      GL11.glPushMatrix();
   }

   public static void popMatrix() {
      GL11.glPopMatrix();
   }

   public static void getFloat(int pname, FloatBuffer params) {
      GL11.glGetFloat(pname, params);
   }

   public static void ortho(double left, double right, double bottom, double top, double zNear, double zFar) {
      GL11.glOrtho(left, right, bottom, top, zNear, zFar);
   }

   public static void rotate(float angle, float x, float y, float z) {
      GL11.glRotatef(angle, x, y, z);
   }

   public static void scale(float x, float y, float z) {
      GL11.glScalef(x, y, z);
   }

   public static void scale(double x, double y, double z) {
      GL11.glScaled(x, y, z);
   }

   public static void translate(float x, float y, float z) {
      GL11.glTranslatef(x, y, z);
   }

   public static void translate(double x, double y, double z) {
      GL11.glTranslated(x, y, z);
   }

   public static void multMatrix(FloatBuffer matrix) {
      GL11.glMultMatrix(matrix);
   }

   public static void color(float colorRed, float colorGreen, float colorBlue, float colorAlpha) {
      if (colorRed != colorState.red || colorGreen != colorState.green || colorBlue != colorState.blue || colorAlpha != colorState.alpha) {
         colorState.red = colorRed;
         colorState.green = colorGreen;
         colorState.blue = colorBlue;
         colorState.alpha = colorAlpha;
         GL11.glColor4f(colorRed, colorGreen, colorBlue, colorAlpha);
      }
   }

   public static void color(float colorRed, float colorGreen, float colorBlue) {
      color(colorRed, colorGreen, colorBlue, 1.0F);
   }

   public static void resetColor() {
      colorState.red = colorState.green = colorState.blue = colorState.alpha = -1.0F;
   }

   public static void glNormalPointer(int n, int n2, ByteBuffer byteBuffer) {
      GL11.glNormalPointer(n, n2, byteBuffer);
   }

   public static void glTexCoordPointer(int n, int n2, int n3, int n4) {
      GL11.glTexCoordPointer(n, n2, n3, n4);
   }

   public static void glTexCoordPointer(int n, int n2, int n3, ByteBuffer byteBuffer) {
      GL11.glTexCoordPointer(n, n2, n3, byteBuffer);
   }

   public static void glVertexPointer(int n, int n2, int n3, int n4) {
      GL11.glVertexPointer(n, n2, n3, n4);
   }

   public static void glVertexPointer(int n, int n2, int n3, ByteBuffer byteBuffer) {
      GL11.glVertexPointer(n, n2, n3, byteBuffer);
   }

   public static void glColorPointer(int n, int n2, int n3, int n4) {
      GL11.glColorPointer(n, n2, n3, n4);
   }

   public static void glColorPointer(int n, int n2, int n3, ByteBuffer byteBuffer) {
      GL11.glColorPointer(n, n2, n3, byteBuffer);
   }

   public static void glDisableClientState(int n) {
      GL11.glDisableClientState(n);
   }

   public static void glEnableClientState(int n) {
      GL11.glEnableClientState(n);
   }

   public static void glBegin(int n) {
      GL11.glBegin(n);
   }

   public static void glEnd() {
      GL11.glEnd();
   }

   public static void glDrawArrays(int n, int n2, int n3) {
      GL11.glDrawArrays(n, n2, n3);
      if (Config.isShaders() && !creatingDisplayList) {
         int countInstances = Shaders.activeProgram.getCountInstances();
         if (countInstances > 1) {
            for (int i = 1; i < countInstances; i++) {
               Shaders.uniform_instanceId.setValue(i);
               GL11.glDrawArrays(n, n2, n3);
            }

            Shaders.uniform_instanceId.setValue(0);
         }
      }
   }

   public static void callList(int list) {
      GL11.glCallList(list);
      if (Config.isShaders() && !creatingDisplayList) {
         int countInstances = Shaders.activeProgram.getCountInstances();
         if (countInstances > 1) {
            for (int i = 1; i < countInstances; i++) {
               Shaders.uniform_instanceId.setValue(i);
               GL11.glCallList(list);
            }

            Shaders.uniform_instanceId.setValue(0);
         }
      }
   }

   public static void callLists(IntBuffer intBuffer) {
      GL11.glCallLists(intBuffer);
      if (Config.isShaders() && !creatingDisplayList) {
         int countInstances = Shaders.activeProgram.getCountInstances();
         if (countInstances > 1) {
            for (int i = 1; i < countInstances; i++) {
               Shaders.uniform_instanceId.setValue(i);
               GL11.glCallLists(intBuffer);
            }

            Shaders.uniform_instanceId.setValue(0);
         }
      }
   }

   public static void glDeleteLists(int n, int n2) {
      GL11.glDeleteLists(n, n2);
   }

   public static void glNewList(int n, int n2) {
      GL11.glNewList(n, n2);
      creatingDisplayList = true;
   }

   public static void glEndList() {
      GL11.glEndList();
      creatingDisplayList = false;
   }

   public static int glGetError() {
      return GL11.glGetError();
   }

   public static void glTexImage2D(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, IntBuffer intBuffer) {
      GL11.glTexImage2D(n, n2, n3, n4, n5, n6, n7, n8, intBuffer);
   }

   public static void glTexSubImage2D(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8, IntBuffer intBuffer) {
      GL11.glTexSubImage2D(n, n2, n3, n4, n5, n6, n7, n8, intBuffer);
   }

   public static void glCopyTexSubImage2D(int n, int n2, int n3, int n4, int n5, int n6, int n7, int n8) {
      GL11.glCopyTexSubImage2D(n, n2, n3, n4, n5, n6, n7, n8);
   }

   public static void glGetTexImage(int n, int n2, int n3, int n4, IntBuffer intBuffer) {
      GL11.glGetTexImage(n, n2, n3, n4, intBuffer);
   }

   public static void glTexParameterf(int n, int n2, float f) {
      GL11.glTexParameterf(n, n2, f);
   }

   public static void glTexParameteri(int n, int n2, int n3) {
      GL11.glTexParameteri(n, n2, n3);
   }

   public static int glGetTexLevelParameteri(int n, int n2, int n3) {
      return GL11.glGetTexLevelParameteri(n, n2, n3);
   }

   public static int getActiveTextureUnit() {
      return OpenGlHelper.defaultTexUnit + activeTextureUnit;
   }

   public static void bindCurrentTexture() {
      GL11.glBindTexture(3553, textureState[activeTextureUnit].textureName);
   }

   public static int getBoundTexture() {
      return textureState[activeTextureUnit].textureName;
   }

   public static void checkBoundTexture() {
      if (Config.isMinecraftThread()) {
         int glGetInteger = GL11.glGetInteger(34016);
         int glGetInteger2 = GL11.glGetInteger(32873);
         int activeTextureUnit = getActiveTextureUnit();
         int boundTexture = getBoundTexture();
         if (boundTexture > 0 && (glGetInteger != activeTextureUnit || glGetInteger2 != boundTexture)) {
            Config.dbg("checkTexture: act: " + activeTextureUnit + ", glAct: " + glGetInteger + ", tex: " + boundTexture + ", glTex: " + glGetInteger2);
         }
      }
   }

   public static void deleteTextures(IntBuffer intBuffer) {
      ((Buffer)intBuffer).rewind();

      while (intBuffer.position() < intBuffer.limit()) {
         int n = intBuffer.get();
         deleteTexture(n);
      }

      ((Buffer)intBuffer).rewind();
   }

   public static boolean isFogEnabled() {
      return fogState.fog.currentState;
   }

   public static void setFogEnabled(boolean flag) {
      fogState.fog.setState(flag);
   }

   public static void lockAlpha(GlAlphaState glAlphaState) {
      if (!alphaLock.isLocked()) {
         getAlphaState(alphaLockState);
         setAlphaState(glAlphaState);
         alphaLock.lock();
      }
   }

   public static void unlockAlpha() {
      if (alphaLock.unlock()) {
         setAlphaState(alphaLockState);
      }
   }

   public static void getAlphaState(GlAlphaState glAlphaState) {
      if (alphaLock.isLocked()) {
         glAlphaState.setState(alphaLockState);
      } else {
         glAlphaState.setState(alphaState.alphaTest.currentState, alphaState.func, alphaState.ref);
      }
   }

   public static void setAlphaState(GlAlphaState glAlphaState) {
      if (alphaLock.isLocked()) {
         alphaLockState.setState(glAlphaState);
      } else {
         alphaState.alphaTest.setState(glAlphaState.isEnabled());
         alphaFunc(glAlphaState.getFunc(), glAlphaState.getRef());
      }
   }

   public static void lockBlend(GlBlendState glBlendState) {
      if (!blendLock.isLocked()) {
         getBlendState(blendLockState);
         setBlendState(glBlendState);
         blendLock.lock();
      }
   }

   public static void unlockBlend() {
      if (blendLock.unlock()) {
         setBlendState(blendLockState);
      }
   }

   public static void getBlendState(GlBlendState glBlendState) {
      if (blendLock.isLocked()) {
         glBlendState.setState(blendLockState);
      } else {
         glBlendState.setState(blendState.blend.currentState, blendState.srcFactor, blendState.dstFactor, blendState.srcFactorAlpha, blendState.dstFactorAlpha);
      }
   }

   public static void setBlendState(GlBlendState glBlendState) {
      if (blendLock.isLocked()) {
         blendLockState.setState(glBlendState);
      } else {
         blendState.blend.setState(glBlendState.isEnabled());
         if (!glBlendState.isSeparate()) {
            blendFunc(glBlendState.getSrcFactor(), glBlendState.getDstFactor());
         } else {
            tryBlendFuncSeparate(glBlendState.getSrcFactor(), glBlendState.getDstFactor(), glBlendState.getSrcFactorAlpha(), glBlendState.getDstFactorAlpha());
         }
      }
   }

   public static void glMultiDrawArrays(int n, IntBuffer intBuffer, IntBuffer intBuffer2) {
      GL14.glMultiDrawArrays(n, intBuffer, intBuffer2);
      if (Config.isShaders() && !creatingDisplayList) {
         int countInstances = Shaders.activeProgram.getCountInstances();
         if (countInstances > 1) {
            for (int i = 1; i < countInstances; i++) {
               Shaders.uniform_instanceId.setValue(i);
               GL14.glMultiDrawArrays(n, intBuffer, intBuffer2);
            }

            Shaders.uniform_instanceId.setValue(0);
         }
      }
   }

   static {
      for (int i = 0; i < 8; i++) {
         lightState[i] = new GlStateManager.BooleanState(16384 + i);
      }

      for (int i = 0; i < textureState.length; i++) {
         textureState[i] = new GlStateManager.TextureState();
      }
   }

   static class AlphaState {
      public GlStateManager.BooleanState alphaTest = new GlStateManager.BooleanState(3008);
      public int func = 519;
      public float ref = -1.0F;

      public AlphaState() {
      }
   }

   static class BlendState {
      public GlStateManager.BooleanState blend = new GlStateManager.BooleanState(3042);
      public int srcFactor = 1;
      public int dstFactor = 0;
      public int srcFactorAlpha = 1;
      public int dstFactorAlpha = 0;

      public BlendState() {
      }
   }

   static class BooleanState {
      public boolean currentState = false;
      public int capability;

      public BooleanState(int capabilityIn) {
         this.capability = capabilityIn;
      }

      public void setDisabled() {
         this.setState(false);
      }

      public void setEnabled() {
         this.setState(true);
      }

      public void setState(boolean state) {
         if (state != this.currentState) {
            this.currentState = state;
            if (state) {
               GL11.glEnable(this.capability);
            } else {
               GL11.glDisable(this.capability);
            }
         }
      }
   }

   static class ClearState {
      public double depth = 1.0;
      public GlStateManager.Color color = new GlStateManager.Color(0.0F, 0.0F, 0.0F, 0.0F);
      public int clearStencil = 0;

      public ClearState() {
      }
   }

   static class Color {
      public float red = 1.0F;
      public float green = 1.0F;
      public float blue = 1.0F;
      public float alpha = 1.0F;

      public Color() {
      }

      public Color(float redIn, float greenIn, float blueIn, float alphaIn) {
         this.red = redIn;
         this.green = greenIn;
         this.blue = blueIn;
         this.alpha = alphaIn;
      }
   }

   static class ColorLogicState {
      public GlStateManager.BooleanState colorLogicOp = new GlStateManager.BooleanState(3058);
      public int opcode = 5379;

      public ColorLogicState() {
      }
   }

   static class ColorMask {
      public boolean red = true;
      public boolean green = true;
      public boolean blue = true;
      public boolean alpha = true;

      public ColorMask() {
      }
   }

   static class ColorMaterialState {
      public GlStateManager.BooleanState colorMaterial = new GlStateManager.BooleanState(2903);
      public int face = 1032;
      public int mode = 5634;

      public ColorMaterialState() {
      }
   }

   static class CullState {
      public GlStateManager.BooleanState cullFace = new GlStateManager.BooleanState(2884);
      public int mode = 1029;

      public CullState() {
      }
   }

   static class DepthState {
      public GlStateManager.BooleanState depthTest = new GlStateManager.BooleanState(2929);
      public boolean maskEnabled = true;
      public int depthFunc = 513;

      public DepthState() {
      }
   }

   static class FogState {
      public GlStateManager.BooleanState fog = new GlStateManager.BooleanState(2912);
      public int mode = 2048;
      public float density = 1.0F;
      public float start = 0.0F;
      public float end = 1.0F;

      public FogState() {
      }
   }

   static class PolygonOffsetState {
      public GlStateManager.BooleanState polygonOffsetFill = new GlStateManager.BooleanState(32823);
      public GlStateManager.BooleanState polygonOffsetLine = new GlStateManager.BooleanState(10754);
      public float factor = 0.0F;
      public float units = 0.0F;

      public PolygonOffsetState() {
      }
   }

   static class StencilFunc {
      public int func = 519;
      public int ref = 0;
      public int mask = -1;

      public StencilFunc() {
      }
   }

   static class StencilState {
      public GlStateManager.StencilFunc capState = new GlStateManager.StencilFunc();
      public int mask = -1;
      public int sfail = 7680;
      public int dpfail = 7680;
      public int dppass = 7680;

      public StencilState() {
      }
   }

   public enum TexGen {
      S,
      T,
      R,
      Q;
   }

   static class TexGenCoord {
      public int param = -1;
      public int coord;
      public GlStateManager.BooleanState textureGen;

      public TexGenCoord(int n, int n2) {
         this.coord = n;
         this.textureGen = new GlStateManager.BooleanState(n2);
      }
   }

   static class TexGenState {
      public GlStateManager.TexGenCoord s = new GlStateManager.TexGenCoord(8192, 3168);
      public GlStateManager.TexGenCoord t = new GlStateManager.TexGenCoord(8193, 3169);
      public GlStateManager.TexGenCoord r = new GlStateManager.TexGenCoord(8194, 3170);
      public GlStateManager.TexGenCoord q = new GlStateManager.TexGenCoord(8195, 3171);

      public TexGenState() {
      }
   }

   static class TextureState {
      public GlStateManager.BooleanState texture2DState = new GlStateManager.BooleanState(3553);
      public int textureName = 0;

      public TextureState() {
      }
   }
}
