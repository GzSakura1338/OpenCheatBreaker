package com.cheatbreaker.client.emote.bobj;

import com.cheatbreaker.client.emote.Bone;
import com.cheatbreaker.client.emote.MeshMaterial;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;

public class RenderableMesh {
    public int indexBufferId;
    public int vertexBufferId;
    public int normalBufferId;
    public FloatBuffer texCoordFloatBuffer;
    public MeshModel model;
    public int texCoordBufferId;
    public FloatBuffer normalFloatBuffer;
    public String name;
    public BobjCompiledData compiledData;
    public IntBuffer indexBuffer;
    public ResourceLocation minecraft;
    public FloatBuffer vertexFloatBuffer;
    public BobjArmature armature;

    public void setTextureFilter(int n) {
        GL11.glTexParameteri(3553, 10241, n);
        GL11.glTexParameteri(3553, 10240, n);
    }

    public void updateSkinning() {
        Vector4f vector4f = new Vector4f();
        Vector4f vector4f2 = new Vector4f(0.0F, 0.0F, 0.0F, 0.0F);
        Vector3f vector3f = new Vector3f();
        Vector3f vector3f2 = new Vector3f(0.0F, 0.0F, 0.0F);
        float[] vertices = this.compiledData.vertices;
        float[] floats = new float[vertices.length];
        float[] normals = this.compiledData.normals;
        float[] floats2 = new float[normals.length];
        Matrix4f[] boneMatrices = this.armature.boneMatrices;

        for (int i = 0; i < floats.length / 4; i++) {
            int n = 0;

            for (int j = 0; j < 4; j++) {
                float f = this.compiledData.boneWeights[i * 4 + j];
                if (f > 0.0F) {
                    int n2 = this.compiledData.boneIndices[i * 4 + j];
                    vector4f.set(vertices[i * 4], vertices[i * 4 + 1], vertices[i * 4 + 2], 1.0F);
                    boneMatrices[n2].transform(vector4f);
                    vector4f.scale(f);
                    vector4f2.add(vector4f);
                    vector3f.set(normals[i * 3], normals[i * 3 + 1], normals[i * 3 + 2]);
                    boneMatrices[n2].transform(vector3f);
                    vector3f.scale(f);
                    vector3f2.add(vector3f);
                    n++;
                }
            }

            if (n == 0) {
                vector3f2.set(normals[i * 3], normals[i * 3 + 1], normals[i * 3 + 2]);
                vector4f2.set(vertices[i * 4], vertices[i * 4 + 1], vertices[i * 4 + 2], 1.0F);
            }

            floats[i * 4] = vector4f2.x;
            floats[i * 4 + 1] = vector4f2.y;
            floats[i * 4 + 2] = vector4f2.z;
            floats[i * 4 + 3] = vector4f2.w;
            floats2[i * 3] = vector3f2.x;
            floats2[i * 3 + 1] = vector3f2.y;
            floats2[i * 3 + 2] = vector3f2.z;
            vector4f2.set(0.0F, 0.0F, 0.0F, 0.0F);
            vector3f2.set(0.0F, 0.0F, 0.0F);
        }

        this.uploadVertices(floats);
        this.uploadNormals(floats2);
    }

    public RenderableMesh(MeshModel meshModel, String str, BobjCompiledData bobjCompiledData) {
        this.model = meshModel;
        this.name = str;
        this.compiledData = bobjCompiledData;
        (this.armature = this.compiledData.mesh.armature).initialize();
        this.createBuffers();
    }

    public int getTextureMinFilter() {
        return GL11.glGetTexParameteri(3553, 10241);
    }

    public void uploadNormals(float[] floats) {
        ((Buffer)this.normalFloatBuffer).clear();
        ((Buffer)this.normalFloatBuffer.put(floats)).flip();
        GL15.glBindBuffer(34962, this.normalBufferId);
        GL15.glBufferData(34962, this.normalFloatBuffer, 35048);
    }

    public void render(Minecraft mc, MeshMaterial meshMaterial) {
        if (meshMaterial == null || meshMaterial.visible) {
            ResourceLocation texture = this.getTexture(meshMaterial);
            boolean flag = meshMaterial != null && meshMaterial.smooth;
            boolean flag2 = meshMaterial != null && meshMaterial.normals;
            boolean flag3 = meshMaterial == null || meshMaterial.lighting;
            float lastBrightnessX = OpenGlHelper.lastBrightnessX;
            float lastBrightnessY = OpenGlHelper.lastBrightnessY;
            if (texture != null) {
                GL11.glEnable(3553);
                mc.getTextureManager().bindTexture(texture);
                if (meshMaterial != null) {
                    this.setTextureFilter(meshMaterial.textureFilter);
                }
            }

            if (flag && flag2) {
                GL11.glShadeModel(7425);
            }

            if (!flag2) {
                RenderHelper.disableStandardItemLighting();
            }

            if (!flag3) {
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
            }

            int n = meshMaterial != null ? meshMaterial.color : 16777215;
            GL11.glColor4f((n >> 16 & 0xFF) / 255.0F, (n >> 8 & 0xFF) / 255.0F, (n & 0xFF) / 255.0F, 1.0F);
            GL11.glEnable(32826);
            GL11.glEnable(2977);
            GL15.glBindBuffer(34962, this.vertexBufferId);
            GL11.glVertexPointer(4, 5126, 0, 0L);
            GL15.glBindBuffer(34962, this.normalBufferId);
            GL11.glNormalPointer(5126, 0, 0L);
            GL15.glBindBuffer(34962, this.texCoordBufferId);
            GL11.glTexCoordPointer(2, 5126, 0, 0L);
            GL11.glEnableClientState(32884);
            GL11.glEnableClientState(32885);
            GL11.glEnableClientState(32888);
            GL15.glBindBuffer(34963, this.indexBufferId);
            GL11.glDisable(3042);
            GL11.glEnable(3008);
            GL11.glAlphaFunc(516, 0.99F);
            GL11.glDrawElements(4, this.compiledData.indices.length, 5125, 0L);
            GL11.glEnable(3042);
            GL14.glBlendFuncSeparate(770, 771, 1, 0);
            GL11.glAlphaFunc(516, 0.3F);
            GL11.glDepthMask(false);
            GL11.glDrawElements(4, this.compiledData.indices.length, 5125, 0L);
            GL11.glDepthMask(true);
            GL11.glAlphaFunc(516, 0.1F);
            GL15.glBindBuffer(34963, 0);
            GL15.glBindBuffer(34962, 0);
            GL11.glDisableClientState(32884);
            GL11.glDisableClientState(32885);
            GL11.glDisableClientState(32888);
            if (flag && flag2) {
                GL11.glShadeModel(7424);
            }

            if (!flag2) {
                RenderHelper.enableStandardItemLighting();
            }

            if (!flag3) {
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lastBrightnessX, lastBrightnessY);
            }

            GL11.glDisable(32826);
            GL11.glDisable(2977);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            if (mc.gameSettings.showDebugInfo) {
                GL11.glDisable(2896);
                GL11.glDisable(2929);
                GL11.glDisable(3553);

                for (Bone bone : this.compiledData.mesh.armature.bones) {
                    Vector4f vector4f = new Vector4f(0.0F, 0.0F, 0.0F, 1.0F);
                    Vector4f vector4f2 = new Vector4f(0.0F, bone.length, 0.0F, 1.0F);
                    Vector4f vector4f3 = new Vector4f(0.1F, 0.0F, 0.0F, 1.0F);
                    Vector4f vector4f4 = new Vector4f(0.0F, 0.1F, 0.0F, 1.0F);
                    Vector4f vector4f5 = new Vector4f(0.0F, 0.0F, 0.1F, 1.0F);
                    Matrix4f worldMatrix = bone.worldMatrix;
                    worldMatrix.transform(vector4f);
                    worldMatrix.transform(vector4f2);
                    worldMatrix.transform(vector4f3);
                    worldMatrix.transform(vector4f4);
                    worldMatrix.transform(vector4f5);
                    GL11.glPointSize(5.0F);
                    GL11.glBegin(0);
                    GL11.glColor3f(1.0F, 1.0F, 1.0F);
                    GL11.glVertex3f(vector4f.x, vector4f.y, vector4f.z);
                    GL11.glEnd();
                    GL11.glLineWidth(1.0F);
                    GL11.glBegin(1);
                    GL11.glColor3f(0.9F, 0.9F, 0.9F);
                    GL11.glVertex3f(vector4f.x, vector4f.y, vector4f.z);
                    GL11.glVertex3f(vector4f2.x, vector4f2.y, vector4f2.z);
                    GL11.glEnd();
                    GL11.glLineWidth(2.0F);
                    GL11.glBegin(1);
                    GL11.glColor3f(1.0F, 0.0F, 0.0F);
                    GL11.glVertex3f(vector4f.x, vector4f.y, vector4f.z);
                    GL11.glVertex3f(vector4f3.x, vector4f3.y, vector4f3.z);
                    GL11.glEnd();
                    GL11.glBegin(1);
                    GL11.glColor3f(0.0F, 1.0F, 0.0F);
                    GL11.glVertex3f(vector4f.x, vector4f.y, vector4f.z);
                    GL11.glVertex3f(vector4f4.x, vector4f4.y, vector4f4.z);
                    GL11.glEnd();
                    GL11.glBegin(1);
                    GL11.glColor3f(0.0F, 0.0F, 1.0F);
                    GL11.glVertex3f(vector4f.x, vector4f.y, vector4f.z);
                    GL11.glVertex3f(vector4f5.x, vector4f5.y, vector4f5.z);
                    GL11.glEnd();
                }

                GL11.glColor3f(1.0F, 1.0F, 1.0F);
                GL11.glLineWidth(1.0F);
                GL11.glEnable(2929);
                GL11.glEnable(2896);
                GL11.glEnable(3553);
            }
        }
    }

    public ResourceLocation getTexture(MeshMaterial meshMaterial) {
        if (meshMaterial == null) {
            return this.minecraft;
        } else {
            return meshMaterial.texture == null ? this.minecraft : meshMaterial.texture;
        }
    }

    public void createBuffers() {
        this.vertexFloatBuffer = BufferUtils.createFloatBuffer(this.compiledData.vertices.length);
        ((Buffer)this.vertexFloatBuffer.put(this.compiledData.vertices)).flip();
        this.normalFloatBuffer = BufferUtils.createFloatBuffer(this.compiledData.normals.length);
        ((Buffer)this.normalFloatBuffer.put(this.compiledData.normals)).flip();
        this.texCoordFloatBuffer = BufferUtils.createFloatBuffer(this.compiledData.texCoords.length);
        ((Buffer)this.texCoordFloatBuffer.put(this.compiledData.texCoords)).flip();
        this.indexBuffer = BufferUtils.createIntBuffer(this.compiledData.indices.length);
        ((Buffer)this.indexBuffer.put(this.compiledData.indices)).flip();
        GL15.glBindBuffer(34962, this.vertexBufferId = GL15.glGenBuffers());
        GL15.glBufferData(34962, this.vertexFloatBuffer, 35048);
        GL15.glBindBuffer(34962, this.normalBufferId = GL15.glGenBuffers());
        GL15.glBufferData(34962, this.normalFloatBuffer, 35044);
        GL15.glBindBuffer(34962, this.texCoordBufferId = GL15.glGenBuffers());
        GL15.glBufferData(34962, this.texCoordFloatBuffer, 35044);
        GL15.glBindBuffer(34963, this.indexBufferId = GL15.glGenBuffers());
        GL15.glBufferData(34963, this.indexBuffer, 35044);
        GL15.glBindBuffer(34962, 0);
        GL15.glBindBuffer(34963, 0);
    }

    public void deleteBuffers() {
        GL15.glDeleteBuffers(this.vertexBufferId);
        GL15.glDeleteBuffers(this.normalBufferId);
        GL15.glDeleteBuffers(this.texCoordBufferId);
        GL15.glDeleteBuffers(this.indexBufferId);
        this.vertexFloatBuffer = null;
        this.normalFloatBuffer = null;
        this.texCoordFloatBuffer = null;
        this.indexBuffer = null;
    }

    public void uploadVertices(float[] floats) {
        ((Buffer)this.vertexFloatBuffer).clear();
        ((Buffer)this.vertexFloatBuffer.put(floats)).flip();
        GL15.glBindBuffer(34962, this.vertexBufferId);
        GL15.glBufferData(34962, this.vertexFloatBuffer, 35048);
    }
}
