package net.minecraft.client.model;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.Vec3;
import net.optifine.Config;
import net.optifine.shaders.SVertexFormat;

public class TexturedQuad {
   public PositionTextureVertex[] vertexPositions;
   public int nVertices;
   public boolean invertNormal;

   public TexturedQuad(PositionTextureVertex[] vertices) {
      this.vertexPositions = vertices;
      this.nVertices = vertices.length;
   }

   public TexturedQuad(
      PositionTextureVertex[] vertices, int texcoordU1, int texcoordV1, int texcoordU2, int texcoordV2, float textureWidth, float textureHeight
   ) {
      this(vertices);
      float f = 0.0F / textureWidth;
      float f2 = 0.0F / textureHeight;
      vertices[0] = vertices[0].setTexturePosition(texcoordU2 / textureWidth - f, texcoordV1 / textureHeight + f2);
      vertices[1] = vertices[1].setTexturePosition(texcoordU1 / textureWidth + f, texcoordV1 / textureHeight + f2);
      vertices[2] = vertices[2].setTexturePosition(texcoordU1 / textureWidth + f, texcoordV2 / textureHeight - f2);
      vertices[3] = vertices[3].setTexturePosition(texcoordU2 / textureWidth - f, texcoordV2 / textureHeight - f2);
   }

   public void flipFace() {
      PositionTextureVertex[] positionTextureVertexes = new PositionTextureVertex[this.vertexPositions.length];

      for (int i = 0; i < this.vertexPositions.length; i++) {
         positionTextureVertexes[i] = this.vertexPositions[this.vertexPositions.length - i - 1];
      }

      this.vertexPositions = positionTextureVertexes;
   }

   public void draw(WorldRenderer renderer, float scale) {
      Vec3 subtractReverse = this.vertexPositions[1].vector3D.subtractReverse(this.vertexPositions[0].vector3D);
      Vec3 subtractReverse2 = this.vertexPositions[1].vector3D.subtractReverse(this.vertexPositions[2].vector3D);
      Vec3 normalize = subtractReverse2.crossProduct(subtractReverse).normalize();
      float xCoord = (float)normalize.xCoord;
      float yCoord = (float)normalize.yCoord;
      float zCoord = (float)normalize.zCoord;
      if (this.invertNormal) {
         xCoord = -xCoord;
         yCoord = -yCoord;
         zCoord = -zCoord;
      }

      if (Config.isShaders()) {
         renderer.begin(7, SVertexFormat.defVertexFormatTextured);
      } else {
         renderer.begin(7, DefaultVertexFormats.OLDMODEL_POSITION_TEX_NORMAL);
      }

      for (int i = 0; i < 4; i++) {
         PositionTextureVertex positionTextureVertex = this.vertexPositions[i];
         renderer.pos(positionTextureVertex.vector3D.xCoord * scale, positionTextureVertex.vector3D.yCoord * scale, positionTextureVertex.vector3D.zCoord * scale)
            .tex(positionTextureVertex.texturePositionX, positionTextureVertex.texturePositionY)
            .normal(xCoord, yCoord, zCoord)
            .endVertex();
      }

      Tessellator.getInstance().draw();
   }
}
