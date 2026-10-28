package net.minecraft.client.model;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.Vec3;

public class TexturedQuad {
    public PositionTextureVertex[] vertexPositions;
    public int nVertices;
    public boolean invertNormal;

    public TexturedQuad(PositionTextureVertex[] positionTextureVertexes) {
        this.vertexPositions = positionTextureVertexes;
        this.nVertices = positionTextureVertexes.length;
    }

    public TexturedQuad(PositionTextureVertex[] positionTextureVertexes, int n, int n2, int n3, int n4, float f, float f2) {
        this(positionTextureVertexes);
        float f3 = 0.0F / f;
        float f4 = 0.0F / f2;
        positionTextureVertexes[0] = positionTextureVertexes[0].setTexturePosition(n3 / f - f3, n2 / f2 + f4);
        positionTextureVertexes[1] = positionTextureVertexes[1].setTexturePosition(n / f + f3, n2 / f2 + f4);
        positionTextureVertexes[2] = positionTextureVertexes[2].setTexturePosition(n / f + f3, n4 / f2 - f4);
        positionTextureVertexes[3] = positionTextureVertexes[3].setTexturePosition(n3 / f - f3, n4 / f2 - f4);
    }

    public void flipFace() {
        PositionTextureVertex[] positionTextureVertexes = new PositionTextureVertex[this.vertexPositions.length];

        for (int i = 0; i < this.vertexPositions.length; i++) {
            positionTextureVertexes[i] = this.vertexPositions[this.vertexPositions.length - i - 1];
        }

        this.vertexPositions = positionTextureVertexes;
    }

    public void draw(Tessellator tessellator, float f) {
        Vec3 subtract = this.vertexPositions[1].vector3D.subtract(this.vertexPositions[0].vector3D);
        Vec3 subtract2 = this.vertexPositions[1].vector3D.subtract(this.vertexPositions[2].vector3D);
        Vec3 normalize = subtract2.crossProduct(subtract).normalize();
        tessellator.startDrawingQuads();
        if (this.invertNormal) {
            tessellator.setNormal(-((float)normalize.xCoord), -((float)normalize.yCoord), -((float)normalize.zCoord));
        } else {
            tessellator.setNormal((float)normalize.xCoord, (float)normalize.yCoord, (float)normalize.zCoord);
        }

        for (int i = 0; i < 4; i++) {
            PositionTextureVertex positionTextureVertex = this.vertexPositions[i];
            tessellator.addVertexWithUV(
                (float)positionTextureVertex.vector3D.xCoord * f,
                (float)positionTextureVertex.vector3D.yCoord * f,
                (float)positionTextureVertex.vector3D.zCoord * f,
                positionTextureVertex.texturePositionX,
                positionTextureVertex.texturePositionY
            );
        }

        tessellator.draw();
    }
}
