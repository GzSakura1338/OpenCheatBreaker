package net.minecraft.client.shader;

import com.google.common.collect.Lists;
import java.util.List;
import javax.vecmath.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.util.JsonException;
import org.lwjgl.opengl.GL11;

public class Shader {
    public List listAuxFramebuffers = Lists.newArrayList();
    public List listAuxNames = Lists.newArrayList();
    public List listAuxWidths = Lists.newArrayList();
    public List listAuxHeights = Lists.newArrayList();
    public ShaderManager manager;
    public Framebuffer framebufferIn;
    public Framebuffer framebufferOut;
    public Matrix4f projectionMatrix;

    public Shader(IResourceManager resourceManager, String str, Framebuffer framebuffer, Framebuffer framebuffer2) throws JsonException {
        this.manager = new ShaderManager(resourceManager, str);
        this.framebufferIn = framebuffer;
        this.framebufferOut = framebuffer2;
    }

    public void deleteShader() {
        this.manager.deleteShader();
    }

    public void addAuxFramebuffer(String str, Object obj, int n, int n2) {
        this.listAuxNames.add(this.listAuxNames.size(), str);
        this.listAuxFramebuffers.add(this.listAuxFramebuffers.size(), obj);
        this.listAuxWidths.add(this.listAuxWidths.size(), n);
        this.listAuxHeights.add(this.listAuxHeights.size(), n2);
    }

    public void preLoadShader() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(3042);
        GL11.glDisable(2929);
        GL11.glDisable(3008);
        GL11.glDisable(2912);
        GL11.glDisable(2896);
        GL11.glDisable(2903);
        GL11.glEnable(3553);
        GL11.glBindTexture(3553, 0);
    }

    public void setProjectionMatrix(Matrix4f matrix4f) {
        this.projectionMatrix = matrix4f;
    }

    public void loadShader(float f) {
        this.preLoadShader();
        this.framebufferIn.unbindFramebuffer();
        float framebufferTextureWidth = this.framebufferOut.framebufferTextureWidth;
        float framebufferTextureHeight = this.framebufferOut.framebufferTextureHeight;
        GL11.glViewport(0, 0, (int)framebufferTextureWidth, (int)framebufferTextureHeight);
        this.manager.addSamplerTexture("DiffuseSampler", this.framebufferIn);

        for (int i = 0; i < this.listAuxFramebuffers.size(); i++) {
            this.manager.addSamplerTexture((String)this.listAuxNames.get(i), this.listAuxFramebuffers.get(i));
            this.manager
                .getShaderUniformOrDefault("AuxSize" + i)
                .set(((Integer)this.listAuxWidths.get(i)).intValue(), ((Integer)this.listAuxHeights.get(i)).intValue());
        }

        this.manager.getShaderUniformOrDefault("ProjMat").set(this.projectionMatrix);
        this.manager.getShaderUniformOrDefault("InSize").set(this.framebufferIn.framebufferTextureWidth, this.framebufferIn.framebufferTextureHeight);
        this.manager.getShaderUniformOrDefault("OutSize").set(framebufferTextureWidth, framebufferTextureHeight);
        this.manager.getShaderUniformOrDefault("Time").set(f);
        Minecraft minecraft = Minecraft.getMinecraft();
        this.manager.getShaderUniformOrDefault("ScreenSize").set(minecraft.displayWidth, minecraft.displayHeight);
        this.manager.useShader();
        this.framebufferOut.framebufferClear();
        this.framebufferOut.bindFramebuffer(false);
        GL11.glDepthMask(false);
        GL11.glColorMask(true, true, true, false);
        Tessellator instance = Tessellator.instance;
        instance.startDrawingQuads();
        instance.setColorOpaque_I(-1);
        instance.addVertex(0.0, framebufferTextureHeight, 500.0);
        instance.addVertex(framebufferTextureWidth, framebufferTextureHeight, 500.0);
        instance.addVertex(framebufferTextureWidth, 0.0, 500.0);
        instance.addVertex(0.0, 0.0, 500.0);
        instance.draw();
        GL11.glDepthMask(true);
        GL11.glColorMask(true, true, true, true);
        this.manager.endShader();
        this.framebufferOut.unbindFramebuffer();
        this.framebufferIn.unbindFramebufferTexture();

        for (Object obj : this.listAuxFramebuffers) {
            if (obj instanceof Framebuffer) {
                ((Framebuffer)obj).unbindFramebufferTexture();
            }
        }
    }

    public ShaderManager getShaderManager() {
        return this.manager;
    }
}
