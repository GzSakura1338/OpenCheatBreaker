package net.minecraft.client.renderer;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.List;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;
import net.optifine.Config;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.SVertexBuilder;
import org.lwjgl.opengl.GL11;

public class WorldVertexBufferUploader {
   public void draw(WorldRenderer worldRenderer) {
      if (worldRenderer.getVertexCount() > 0) {
         if (worldRenderer.getDrawMode() == 7 && Config.isQuadsToTriangles()) {
            worldRenderer.quadsToTriangles();
         }

         VertexFormat vertexFormat = worldRenderer.getVertexFormat();
         int nextOffset = vertexFormat.getNextOffset();
         ByteBuffer byteBuffer = worldRenderer.getByteBuffer();
         List elements = vertexFormat.getElements();
         boolean exists = Reflector.ForgeVertexFormatElementEnumUseage_preDraw.exists();
         boolean exists2 = Reflector.ForgeVertexFormatElementEnumUseage_postDraw.exists();

         for (int i = 0; i < elements.size(); i++) {
            VertexFormatElement vertexFormatElement = (VertexFormatElement)elements.get(i);
            EnumUsage usage = vertexFormatElement.getUsage();
            if (exists) {
               Reflector.callVoid(usage, Reflector.ForgeVertexFormatElementEnumUseage_preDraw, vertexFormat, i, nextOffset, byteBuffer);
            } else {
               int glConstant = vertexFormatElement.getType().getGlConstant();
               int index = vertexFormatElement.getIndex();
               ((Buffer)byteBuffer).position(vertexFormat.getOffset(i));
               switch (usage) {
                  case POSITION:
                     GL11.glVertexPointer(vertexFormatElement.getElementCount(), glConstant, nextOffset, byteBuffer);
                     GL11.glEnableClientState(32884);
                     break;
                  case UV:
                     OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + index);
                     GL11.glTexCoordPointer(vertexFormatElement.getElementCount(), glConstant, nextOffset, byteBuffer);
                     GL11.glEnableClientState(32888);
                     OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                     break;
                  case COLOR:
                     GL11.glColorPointer(vertexFormatElement.getElementCount(), glConstant, nextOffset, byteBuffer);
                     GL11.glEnableClientState(32886);
                     break;
                  case NORMAL:
                     GL11.glNormalPointer(glConstant, nextOffset, byteBuffer);
                     GL11.glEnableClientState(32885);
               }
            }
         }

         if (worldRenderer.isMultiTexture()) {
            worldRenderer.drawMultiTexture();
         } else if (Config.isShaders()) {
            SVertexBuilder.drawArrays(worldRenderer.getDrawMode(), 0, worldRenderer.getVertexCount(), worldRenderer);
         } else {
            GL11.glDrawArrays(worldRenderer.getDrawMode(), 0, worldRenderer.getVertexCount());
         }

         int n = 0;

         for (int size = elements.size(); n < size; n++) {
            VertexFormatElement vertexFormatElement = (VertexFormatElement)elements.get(n);
            EnumUsage usage = vertexFormatElement.getUsage();
            if (exists2) {
               Reflector.callVoid(usage, Reflector.ForgeVertexFormatElementEnumUseage_postDraw, vertexFormat, n, nextOffset, byteBuffer);
            } else {
               int index = vertexFormatElement.getIndex();
               switch (usage) {
                  case POSITION:
                     GL11.glDisableClientState(32884);
                     break;
                  case UV:
                     OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + index);
                     GL11.glDisableClientState(32888);
                     OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                     break;
                  case COLOR:
                     GL11.glDisableClientState(32886);
                     GlStateManager.resetColor();
                     break;
                  case NORMAL:
                     GL11.glDisableClientState(32885);
               }
            }
         }
      }

      worldRenderer.reset();
   }
}
