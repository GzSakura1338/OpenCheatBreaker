package net.minecraft.client.shader;

import java.nio.ByteBuffer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureUtil;
import org.lwjgl.opengl.GL11;

public class Framebuffer {
    public int framebufferTextureWidth;
    public int framebufferTextureHeight;
    public int framebufferWidth;
    public int framebufferHeight;
    public boolean useDepth;
    public int framebufferObject;
    public int framebufferTexture;
    public int depthBuffer;
    public float[] framebufferColor;
    public int framebufferFilter;

    public Framebuffer(int n, int n2, boolean flag) {
        this.useDepth = flag;
        this.framebufferObject = -1;
        this.framebufferTexture = -1;
        this.depthBuffer = -1;
        this.framebufferColor = new float[4];
        this.framebufferColor[0] = 1.0F;
        this.framebufferColor[1] = 1.0F;
        this.framebufferColor[2] = 1.0F;
        this.framebufferColor[3] = 0.0F;
        this.createBindFramebuffer(n, n2);
    }

    public void createBindFramebuffer(int n, int n2) {
        if (!OpenGlHelper.isFramebufferEnabled()) {
            this.framebufferWidth = n;
            this.framebufferHeight = n2;
        } else {
            GL11.glEnable(2929);
            if (this.framebufferObject >= 0) {
                this.deleteFramebuffer();
            }

            this.createFramebuffer(n, n2);
            this.checkFramebufferComplete();
            OpenGlHelper.advancedBindFramebuffer(OpenGlHelper.framebuffer, 0);
        }
    }

    public void deleteFramebuffer() {
        if (OpenGlHelper.isFramebufferEnabled()) {
            this.unbindFramebufferTexture();
            this.unbindFramebuffer();
            if (this.depthBuffer > -1) {
                OpenGlHelper.advancedDeleteRenderBuffers(this.depthBuffer);
                this.depthBuffer = -1;
            }

            if (this.framebufferTexture > -1) {
                TextureUtil.deleteTexture(this.framebufferTexture);
                this.framebufferTexture = -1;
            }

            if (this.framebufferObject > -1) {
                OpenGlHelper.advancedBindFramebuffer(OpenGlHelper.framebuffer, 0);
                OpenGlHelper.advancedDeleteFrameBuffers(this.framebufferObject);
                this.framebufferObject = -1;
            }
        }
    }

    public void createFramebuffer(int n, int n2) {
        this.framebufferWidth = n;
        this.framebufferHeight = n2;
        this.framebufferTextureWidth = n;
        this.framebufferTextureHeight = n2;
        if (!OpenGlHelper.isFramebufferEnabled()) {
            this.framebufferClear();
        } else {
            this.framebufferObject = OpenGlHelper.advancedGenFrameBuffers();
            this.framebufferTexture = TextureUtil.glGenTextures();
            if (this.useDepth) {
                this.depthBuffer = OpenGlHelper.advancedGenRenderBuffers();
            }

            this.setFramebufferFilter(9728);
            GL11.glBindTexture(3553, this.framebufferTexture);
            GL11.glTexImage2D(3553, 0, 32856, this.framebufferTextureWidth, this.framebufferTextureHeight, 0, 6408, 5121, (ByteBuffer)null);
            OpenGlHelper.advancedBindFramebuffer(OpenGlHelper.framebuffer, this.framebufferObject);
            OpenGlHelper.advancedFrameBufferTexture2D(OpenGlHelper.framebuffer, OpenGlHelper.colorAttachment, 3553, this.framebufferTexture, 0);
            if (this.useDepth) {
                OpenGlHelper.advancedBindRenderBuffer(OpenGlHelper.renderbuffer, this.depthBuffer);
                OpenGlHelper.advancedRenderBufferStorage(OpenGlHelper.renderbuffer, 33190, this.framebufferTextureWidth, this.framebufferTextureHeight);
                OpenGlHelper.advancedFramebufferRenderbuffer(OpenGlHelper.framebuffer, OpenGlHelper.depthAttachment, OpenGlHelper.renderbuffer, this.depthBuffer);
            }

            this.framebufferClear();
            this.unbindFramebufferTexture();
        }
    }

    public void setFramebufferFilter(int n) {
        if (OpenGlHelper.isFramebufferEnabled()) {
            this.framebufferFilter = n;
            GL11.glBindTexture(3553, this.framebufferTexture);
            GL11.glTexParameterf(3553, 10241, n);
            GL11.glTexParameterf(3553, 10240, n);
            GL11.glTexParameterf(3553, 10242, 10496.0F);
            GL11.glTexParameterf(3553, 10243, 10496.0F);
            GL11.glBindTexture(3553, 0);
        }
    }

    public void checkFramebufferComplete() {
        int advancedCheckFrameBufferStatus = OpenGlHelper.advancedCheckFrameBufferStatus(OpenGlHelper.framebuffer);
        if (advancedCheckFrameBufferStatus != OpenGlHelper.completeFramebuffer) {
            if (advancedCheckFrameBufferStatus == OpenGlHelper.incompleteFramebufferAttachment) {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT");
            } else if (advancedCheckFrameBufferStatus == OpenGlHelper.incompleteFramebufferAttachmentMiss) {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT");
            } else if (advancedCheckFrameBufferStatus == OpenGlHelper.incompleteFramebufferAttachmentDraw) {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER");
            } else if (advancedCheckFrameBufferStatus == OpenGlHelper.incompleteFramebufferAttachmentRead) {
                throw new RuntimeException("GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER");
            } else {
                throw new RuntimeException("glCheckFramebufferStatus returned unknown status:" + advancedCheckFrameBufferStatus);
            }
        }
    }

    public void bindFramebufferTexture() {
        if (OpenGlHelper.isFramebufferEnabled()) {
            GL11.glBindTexture(3553, this.framebufferTexture);
        }
    }

    public void unbindFramebufferTexture() {
        if (OpenGlHelper.isFramebufferEnabled()) {
            GL11.glBindTexture(3553, 0);
        }
    }

    public void bindFramebuffer(boolean flag) {
        if (OpenGlHelper.isFramebufferEnabled()) {
            OpenGlHelper.advancedBindFramebuffer(OpenGlHelper.framebuffer, this.framebufferObject);
            if (flag) {
                GL11.glViewport(0, 0, this.framebufferWidth, this.framebufferHeight);
            }
        }
    }

    public void unbindFramebuffer() {
        if (OpenGlHelper.isFramebufferEnabled()) {
            OpenGlHelper.advancedBindFramebuffer(OpenGlHelper.framebuffer, 0);
        }
    }

    public void setFramebufferColor(float f, float f2, float f3, float f4) {
        this.framebufferColor[0] = f;
        this.framebufferColor[1] = f2;
        this.framebufferColor[2] = f3;
        this.framebufferColor[3] = f4;
    }

    public void framebufferRender(int n, int n2) {
        if (OpenGlHelper.isFramebufferEnabled()) {
            GL11.glColorMask(true, true, true, false);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0, n, n2, 0.0, 1000.0, 3000.0);
            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
            GL11.glViewport(0, 0, n, n2);
            GL11.glEnable(3553);
            GL11.glDisable(2896);
            GL11.glDisable(3008);
            GL11.glDisable(3042);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(2903);
            this.bindFramebufferTexture();
            float f = n;
            float f2 = n2;
            float f3 = (float)this.framebufferWidth / this.framebufferTextureWidth;
            float f4 = (float)this.framebufferHeight / this.framebufferTextureHeight;
            Tessellator instance = Tessellator.instance;
            instance.startDrawingQuads();
            instance.setColorOpaque_I(-1);
            instance.addVertexWithUV(0.0, f2, 0.0, 0.0, 0.0);
            instance.addVertexWithUV(f, f2, 0.0, f3, 0.0);
            instance.addVertexWithUV(f, 0.0, 0.0, f3, f4);
            instance.addVertexWithUV(0.0, 0.0, 0.0, 0.0, f4);
            instance.draw();
            this.unbindFramebufferTexture();
            GL11.glDepthMask(true);
            GL11.glColorMask(true, true, true, true);
        }
    }

    public void framebufferClear() {
        this.bindFramebuffer(true);
        GL11.glClearColor(this.framebufferColor[0], this.framebufferColor[1], this.framebufferColor[2], this.framebufferColor[3]);
        int n = 16384;
        if (this.useDepth) {
            GL11.glClearDepth(1.0);
            n |= 256;
        }

        GL11.glClear(n);
        this.unbindFramebuffer();
    }
}
