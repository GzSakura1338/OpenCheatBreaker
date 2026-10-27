package net.minecraft.client.model;

import net.minecraft.client.renderer.WorldRenderer;

public class ModelBox {
   public float posX1;
   public float posY1;
   public float posZ1;
   public float posX2;
   public float posY2;
   public float posZ2;
   public PositionTextureVertex[] vertexPositions;
   public TexturedQuad[] quadList;
   public String boxName;

   public ModelBox(ModelRenderer renderer, int n, int n2, float f, float f2, float f3, int n3, int n4, int n5, float f4) {
      this(renderer, n, n2, f, f2, f3, n3, n4, n5, f4, renderer.mirror);
   }

   public ModelBox(ModelRenderer modelRenderer, int[][] intses, float f, float f2, float f3, float f4, float f5, float f6, float f7, boolean flag) {
      this.posX1 = f;
      this.posY1 = f2;
      this.posZ1 = f3;
      this.posX2 = f + f4;
      this.posY2 = f2 + f5;
      this.posZ2 = f3 + f6;
      this.vertexPositions = new PositionTextureVertex[8];
      this.quadList = new TexturedQuad[6];
      float f8 = f + f4;
      float f9 = f2 + f5;
      float f10 = f3 + f6;
      f -= f7;
      f2 -= f7;
      f3 -= f7;
      f8 += f7;
      f9 += f7;
      f10 += f7;
      if (flag) {
         float f11 = f8;
         f8 = f;
         f = f11;
      }

      PositionTextureVertex positionTextureVertex = new PositionTextureVertex(f, f2, f3, 0.0F, 0.0F);
      PositionTextureVertex positionTextureVertex2 = new PositionTextureVertex(f8, f2, f3, 0.0F, 8.0F);
      PositionTextureVertex positionTextureVertex3 = new PositionTextureVertex(f8, f9, f3, 8.0F, 8.0F);
      PositionTextureVertex positionTextureVertex4 = new PositionTextureVertex(f, f9, f3, 8.0F, 0.0F);
      PositionTextureVertex positionTextureVertex5 = new PositionTextureVertex(f, f2, f10, 0.0F, 0.0F);
      PositionTextureVertex positionTextureVertex6 = new PositionTextureVertex(f8, f2, f10, 0.0F, 8.0F);
      PositionTextureVertex positionTextureVertex7 = new PositionTextureVertex(f8, f9, f10, 8.0F, 8.0F);
      PositionTextureVertex positionTextureVertex8 = new PositionTextureVertex(f, f9, f10, 8.0F, 0.0F);
      this.vertexPositions[0] = positionTextureVertex;
      this.vertexPositions[1] = positionTextureVertex2;
      this.vertexPositions[2] = positionTextureVertex3;
      this.vertexPositions[3] = positionTextureVertex4;
      this.vertexPositions[4] = positionTextureVertex5;
      this.vertexPositions[5] = positionTextureVertex6;
      this.vertexPositions[6] = positionTextureVertex7;
      this.vertexPositions[7] = positionTextureVertex8;
      this.quadList[0] = this.makeTexturedQuad(new PositionTextureVertex[]{positionTextureVertex6, positionTextureVertex2, positionTextureVertex3, positionTextureVertex7}, intses[4], false, modelRenderer.textureWidth, modelRenderer.textureHeight);
      this.quadList[1] = this.makeTexturedQuad(new PositionTextureVertex[]{positionTextureVertex, positionTextureVertex5, positionTextureVertex8, positionTextureVertex4}, intses[5], false, modelRenderer.textureWidth, modelRenderer.textureHeight);
      this.quadList[2] = this.makeTexturedQuad(new PositionTextureVertex[]{positionTextureVertex6, positionTextureVertex5, positionTextureVertex, positionTextureVertex2}, intses[1], true, modelRenderer.textureWidth, modelRenderer.textureHeight);
      this.quadList[3] = this.makeTexturedQuad(new PositionTextureVertex[]{positionTextureVertex3, positionTextureVertex4, positionTextureVertex8, positionTextureVertex7}, intses[0], true, modelRenderer.textureWidth, modelRenderer.textureHeight);
      this.quadList[4] = this.makeTexturedQuad(new PositionTextureVertex[]{positionTextureVertex2, positionTextureVertex, positionTextureVertex4, positionTextureVertex3}, intses[2], false, modelRenderer.textureWidth, modelRenderer.textureHeight);
      this.quadList[5] = this.makeTexturedQuad(new PositionTextureVertex[]{positionTextureVertex5, positionTextureVertex6, positionTextureVertex7, positionTextureVertex8}, intses[3], false, modelRenderer.textureWidth, modelRenderer.textureHeight);
      if (flag) {
         for (TexturedQuad texturedQuad : this.quadList) {
            texturedQuad.flipFace();
         }
      }
   }

   public TexturedQuad makeTexturedQuad(PositionTextureVertex[] positionTextureVertexes, int[] ints, boolean flag, float f, float f2) {
      return ints == null
         ? null
         : (
            flag
               ? new TexturedQuad(positionTextureVertexes, ints[2], ints[3], ints[0], ints[1], f, f2)
               : new TexturedQuad(positionTextureVertexes, ints[0], ints[1], ints[2], ints[3], f, f2)
         );
   }

   public ModelBox(
      ModelRenderer renderer, int textureX, int textureY, float f, float f2, float f3, int n, int n2, int n3, float f4, boolean flag
   ) {
      this.posX1 = f;
      this.posY1 = f2;
      this.posZ1 = f3;
      this.posX2 = f + n;
      this.posY2 = f2 + n2;
      this.posZ2 = f3 + n3;
      this.vertexPositions = new PositionTextureVertex[8];
      this.quadList = new TexturedQuad[6];
      float f5 = f + n;
      float f6 = f2 + n2;
      float f7 = f3 + n3;
      f -= f4;
      f2 -= f4;
      f3 -= f4;
      f5 += f4;
      f6 += f4;
      f7 += f4;
      if (flag) {
         float f8 = f5;
         f5 = f;
         f = f8;
      }

      PositionTextureVertex positionTextureVertex = new PositionTextureVertex(f, f2, f3, 0.0F, 0.0F);
      PositionTextureVertex positionTextureVertex2 = new PositionTextureVertex(f5, f2, f3, 0.0F, 8.0F);
      PositionTextureVertex positionTextureVertex3 = new PositionTextureVertex(f5, f6, f3, 8.0F, 8.0F);
      PositionTextureVertex positionTextureVertex4 = new PositionTextureVertex(f, f6, f3, 8.0F, 0.0F);
      PositionTextureVertex positionTextureVertex5 = new PositionTextureVertex(f, f2, f7, 0.0F, 0.0F);
      PositionTextureVertex positionTextureVertex6 = new PositionTextureVertex(f5, f2, f7, 0.0F, 8.0F);
      PositionTextureVertex positionTextureVertex7 = new PositionTextureVertex(f5, f6, f7, 8.0F, 8.0F);
      PositionTextureVertex positionTextureVertex8 = new PositionTextureVertex(f, f6, f7, 8.0F, 0.0F);
      this.vertexPositions[0] = positionTextureVertex;
      this.vertexPositions[1] = positionTextureVertex2;
      this.vertexPositions[2] = positionTextureVertex3;
      this.vertexPositions[3] = positionTextureVertex4;
      this.vertexPositions[4] = positionTextureVertex5;
      this.vertexPositions[5] = positionTextureVertex6;
      this.vertexPositions[6] = positionTextureVertex7;
      this.vertexPositions[7] = positionTextureVertex8;
      this.quadList[0] = new TexturedQuad(
         new PositionTextureVertex[]{positionTextureVertex6, positionTextureVertex2, positionTextureVertex3, positionTextureVertex7},
         textureX + n3 + n,
         textureY + n3,
         textureX + n3 + n + n3,
         textureY + n3 + n2,
         renderer.textureWidth,
         renderer.textureHeight
      );
      this.quadList[1] = new TexturedQuad(
         new PositionTextureVertex[]{positionTextureVertex, positionTextureVertex5, positionTextureVertex8, positionTextureVertex4},
         textureX,
         textureY + n3,
         textureX + n3,
         textureY + n3 + n2,
         renderer.textureWidth,
         renderer.textureHeight
      );
      this.quadList[2] = new TexturedQuad(
         new PositionTextureVertex[]{positionTextureVertex6, positionTextureVertex5, positionTextureVertex, positionTextureVertex2},
         textureX + n3,
         textureY,
         textureX + n3 + n,
         textureY + n3,
         renderer.textureWidth,
         renderer.textureHeight
      );
      this.quadList[3] = new TexturedQuad(
         new PositionTextureVertex[]{positionTextureVertex3, positionTextureVertex4, positionTextureVertex8, positionTextureVertex7},
         textureX + n3 + n,
         textureY + n3,
         textureX + n3 + n + n,
         textureY,
         renderer.textureWidth,
         renderer.textureHeight
      );
      this.quadList[4] = new TexturedQuad(
         new PositionTextureVertex[]{positionTextureVertex2, positionTextureVertex, positionTextureVertex4, positionTextureVertex3},
         textureX + n3,
         textureY + n3,
         textureX + n3 + n,
         textureY + n3 + n2,
         renderer.textureWidth,
         renderer.textureHeight
      );
      this.quadList[5] = new TexturedQuad(
         new PositionTextureVertex[]{positionTextureVertex5, positionTextureVertex6, positionTextureVertex7, positionTextureVertex8},
         textureX + n3 + n + n3,
         textureY + n3,
         textureX + n3 + n + n3 + n,
         textureY + n3 + n2,
         renderer.textureWidth,
         renderer.textureHeight
      );
      if (flag) {
         for (int i = 0; i < this.quadList.length; i++) {
            this.quadList[i].flipFace();
         }
      }
   }

   public void render(WorldRenderer renderer, float scale) {
      for (int i = 0; i < this.quadList.length; i++) {
         TexturedQuad texturedQuad = this.quadList[i];
         if (texturedQuad != null) {
            texturedQuad.draw(renderer, scale);
         }
      }
   }

   public ModelBox setBoxName(String name) {
      this.boxName = name;
      return this;
   }
}
