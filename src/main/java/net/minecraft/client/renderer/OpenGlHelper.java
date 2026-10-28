package net.minecraft.client.renderer;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings.Options;
import net.optifine.Config;
import net.optifine.GlStateManager;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBMultitexture;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTBlendFuncSeparate;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;

public class OpenGlHelper {
    public static boolean openGL21;
    public static int defaultTexUnit;
    public static int lightmapTexUnit;
    public static boolean nvidia;
    public static int framebuffer;
    public static int renderbuffer;
    public static int colorAttachment;
    public static int depthAttachment;
    public static int completeFramebuffer;
    public static int incompleteFramebufferAttachment;
    public static int incompleteFramebufferAttachmentMiss;
    public static int incompleteFramebufferAttachmentDraw;
    public static int incompleteFramebufferAttachmentRead;
    public static int type;
    public static boolean framebufferSupported;
    public static boolean shaders;
    public static boolean arbShaderObjects;
    public static int GL_LINK_STATUS;
    public static int GL_COMPILE_STATUS;
    public static int GL_VERTEX_SHADER;
    public static int GL_FRAGMENT_SHADER;
    public static boolean anisotropicFilteringSupported;
    public static int anisotropicFilteringMax;
    public static boolean arbMultitexture;
    public static boolean openGL14;
    public static boolean blendFuncSeparateSupported;
    public static boolean shadersSupported;
    public static String contextDescription = "";
    public static float lastBrightnessX = 0.0F;
    public static float lastBrightnessY = 0.0F;
    public static boolean glBlendFuncZero = false;

    public static void initializeTextures() {
        Config.initDisplay();
        ContextCapabilities capabilities = GLContext.getCapabilities();
        arbMultitexture = capabilities.GL_ARB_multitexture && !capabilities.OpenGL13;
        if (arbMultitexture) {
            contextDescription = contextDescription + "Using multitexturing ARB.\n";
            defaultTexUnit = 33984;
            lightmapTexUnit = 33985;
        } else {
            contextDescription = contextDescription + "Using GL 1.3 multitexturing.\n";
            defaultTexUnit = 33984;
            lightmapTexUnit = 33985;
        }

        blendFuncSeparateSupported = capabilities.GL_EXT_blend_func_separate && !capabilities.OpenGL14;
        openGL14 = capabilities.OpenGL14 || capabilities.GL_EXT_blend_func_separate;
        framebufferSupported = openGL14 && (capabilities.GL_ARB_framebuffer_object || capabilities.GL_EXT_framebuffer_object || capabilities.OpenGL30);
        if (framebufferSupported) {
            contextDescription = contextDescription + "Using framebuffer objects because ";
            if (capabilities.OpenGL30) {
                contextDescription = contextDescription + "OpenGL 3.0 is supported and separate blending is supported.\n";
                type = 0;
                framebuffer = 36160;
                renderbuffer = 36161;
                colorAttachment = 36064;
                depthAttachment = 36096;
                completeFramebuffer = 36053;
                incompleteFramebufferAttachment = 36054;
                incompleteFramebufferAttachmentMiss = 36055;
                incompleteFramebufferAttachmentDraw = 36059;
                incompleteFramebufferAttachmentRead = 36060;
            } else if (capabilities.GL_ARB_framebuffer_object) {
                contextDescription = contextDescription + "ARB_framebuffer_object is supported and separate blending is supported.\n";
                type = 1;
                framebuffer = 36160;
                renderbuffer = 36161;
                colorAttachment = 36064;
                depthAttachment = 36096;
                completeFramebuffer = 36053;
                incompleteFramebufferAttachmentMiss = 36055;
                incompleteFramebufferAttachment = 36054;
                incompleteFramebufferAttachmentDraw = 36059;
                incompleteFramebufferAttachmentRead = 36060;
            } else if (capabilities.GL_EXT_framebuffer_object) {
                contextDescription = contextDescription + "EXT_framebuffer_object is supported.\n";
                type = 2;
                framebuffer = 36160;
                renderbuffer = 36161;
                colorAttachment = 36064;
                depthAttachment = 36096;
                completeFramebuffer = 36053;
                incompleteFramebufferAttachmentMiss = 36055;
                incompleteFramebufferAttachment = 36054;
                incompleteFramebufferAttachmentDraw = 36059;
                incompleteFramebufferAttachmentRead = 36060;
            }
        } else {
            contextDescription = contextDescription + "Not using framebuffer objects because ";
            contextDescription = contextDescription + "OpenGL 1.4 is " + (capabilities.OpenGL14 ? "" : "not ") + "supported, ";
            contextDescription = contextDescription + "EXT_blend_func_separate is " + (capabilities.GL_EXT_blend_func_separate ? "" : "not ") + "supported, ";
            contextDescription = contextDescription + "OpenGL 3.0 is " + (capabilities.OpenGL30 ? "" : "not ") + "supported, ";
            contextDescription = contextDescription + "ARB_framebuffer_object is " + (capabilities.GL_ARB_framebuffer_object ? "" : "not ") + "supported, and ";
            contextDescription = contextDescription + "EXT_framebuffer_object is " + (capabilities.GL_EXT_framebuffer_object ? "" : "not ") + "supported.\n";
        }

        anisotropicFilteringSupported = capabilities.GL_EXT_texture_filter_anisotropic;
        anisotropicFilteringMax = (int)(anisotropicFilteringSupported ? GL11.glGetFloat(34047) : 0.0F);
        contextDescription = contextDescription + "Anisotropic filtering is " + (anisotropicFilteringSupported ? "" : "not ") + "supported";
        if (anisotropicFilteringSupported) {
            contextDescription = contextDescription + " and maximum anisotropy is " + anisotropicFilteringMax + ".\n";
        } else {
            contextDescription = contextDescription + ".\n";
        }

        Options.ANISOTROPIC_FILTERING.setValueMax(anisotropicFilteringMax);
        openGL21 = capabilities.OpenGL21;
        shaders = openGL21 || capabilities.GL_ARB_vertex_shader && capabilities.GL_ARB_fragment_shader && capabilities.GL_ARB_shader_objects;
        contextDescription = contextDescription + "Shaders are " + (shaders ? "" : "not ") + "available because ";
        if (shaders) {
            if (capabilities.OpenGL21) {
                contextDescription = contextDescription + "OpenGL 2.1 is supported.\n";
                arbShaderObjects = false;
                GL_LINK_STATUS = 35714;
                GL_COMPILE_STATUS = 35713;
                GL_VERTEX_SHADER = 35633;
                GL_FRAGMENT_SHADER = 35632;
            } else {
                contextDescription = contextDescription + "ARB_shader_objects, ARB_vertex_shader, and ARB_fragment_shader are supported.\n";
                arbShaderObjects = true;
                GL_LINK_STATUS = 35714;
                GL_COMPILE_STATUS = 35713;
                GL_VERTEX_SHADER = 35633;
                GL_FRAGMENT_SHADER = 35632;
            }
        } else {
            contextDescription = contextDescription + "OpenGL 2.1 is " + (capabilities.OpenGL21 ? "" : "not ") + "supported, ";
            contextDescription = contextDescription + "ARB_shader_objects is " + (capabilities.GL_ARB_shader_objects ? "" : "not ") + "supported, ";
            contextDescription = contextDescription + "ARB_vertex_shader is " + (capabilities.GL_ARB_vertex_shader ? "" : "not ") + "supported, and ";
            contextDescription = contextDescription + "ARB_fragment_shader is " + (capabilities.GL_ARB_fragment_shader ? "" : "not ") + "supported.\n";
        }

        shadersSupported = framebufferSupported && shaders;
        nvidia = GL11.glGetString(7936).toLowerCase().contains("nvidia");
    }

    public static boolean areShadersSupported() {
        return shadersSupported;
    }

    public static String getContextDescription() {
        return contextDescription;
    }

    public static int glGetProgrami(int n, int n2) {
        return arbShaderObjects ? ARBShaderObjects.glGetObjectParameteriARB(n, n2) : GL20.glGetProgrami(n, n2);
    }

    public static void glAttachShader(int n, int n2) {
        if (arbShaderObjects) {
            ARBShaderObjects.glAttachObjectARB(n, n2);
        } else {
            GL20.glAttachShader(n, n2);
        }
    }

    public static void glDeleteShader(int n) {
        if (arbShaderObjects) {
            ARBShaderObjects.glDeleteObjectARB(n);
        } else {
            GL20.glDeleteShader(n);
        }
    }

    public static int glCreateShader(int n) {
        return arbShaderObjects ? ARBShaderObjects.glCreateShaderObjectARB(n) : GL20.glCreateShader(n);
    }

    public static void glShaderSource(int n, ByteBuffer byteBuffer) {
        if (arbShaderObjects) {
            ARBShaderObjects.glShaderSourceARB(n, byteBuffer);
        } else {
            GL20.glShaderSource(n, byteBuffer);
        }
    }

    public static void glCompileShader(int n) {
        if (arbShaderObjects) {
            ARBShaderObjects.glCompileShaderARB(n);
        } else {
            GL20.glCompileShader(n);
        }
    }

    public static int glGetShaderi(int n, int n2) {
        return arbShaderObjects ? ARBShaderObjects.glGetObjectParameteriARB(n, n2) : GL20.glGetShaderi(n, n2);
    }

    public static String glGetShaderInfoLog(int n, int n2) {
        return arbShaderObjects ? ARBShaderObjects.glGetInfoLogARB(n, n2) : GL20.glGetShaderInfoLog(n, n2);
    }

    public static String glGetProgramInfoLog(int n, int n2) {
        return arbShaderObjects ? ARBShaderObjects.glGetInfoLogARB(n, n2) : GL20.glGetProgramInfoLog(n, n2);
    }

    public static void glUseProgram(int n) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUseProgramObjectARB(n);
        } else {
            GL20.glUseProgram(n);
        }
    }

    public static int glCreateProgram() {
        return arbShaderObjects ? ARBShaderObjects.glCreateProgramObjectARB() : GL20.glCreateProgram();
    }

    public static void glDeleteProgram(int n) {
        if (arbShaderObjects) {
            ARBShaderObjects.glDeleteObjectARB(n);
        } else {
            GL20.glDeleteProgram(n);
        }
    }

    public static void glLinkProgram(int n) {
        if (arbShaderObjects) {
            ARBShaderObjects.glLinkProgramARB(n);
        } else {
            GL20.glLinkProgram(n);
        }
    }

    public static int glGetUniformLocation(int n, CharSequence charSequence) {
        return arbShaderObjects ? ARBShaderObjects.glGetUniformLocationARB(n, charSequence) : GL20.glGetUniformLocation(n, charSequence);
    }

    public static void glUniform1(int n, IntBuffer intBuffer) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUniform1ARB(n, intBuffer);
        } else {
            GL20.glUniform1(n, intBuffer);
        }
    }

    public static void glUniform1i(int n, int n2) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUniform1iARB(n, n2);
        } else {
            GL20.glUniform1i(n, n2);
        }
    }

    public static void glUniform1(int n, FloatBuffer floatBuffer) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUniform1ARB(n, floatBuffer);
        } else {
            GL20.glUniform1(n, floatBuffer);
        }
    }

    public static void glUniform2(int n, IntBuffer intBuffer) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUniform2ARB(n, intBuffer);
        } else {
            GL20.glUniform2(n, intBuffer);
        }
    }

    public static void glUniform2(int n, FloatBuffer floatBuffer) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUniform2ARB(n, floatBuffer);
        } else {
            GL20.glUniform2(n, floatBuffer);
        }
    }

    public static void glUniform3(int n, IntBuffer intBuffer) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUniform3ARB(n, intBuffer);
        } else {
            GL20.glUniform3(n, intBuffer);
        }
    }

    public static void glUniform3(int n, FloatBuffer floatBuffer) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUniform3ARB(n, floatBuffer);
        } else {
            GL20.glUniform3(n, floatBuffer);
        }
    }

    public static void glUniform4(int n, IntBuffer intBuffer) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUniform4ARB(n, intBuffer);
        } else {
            GL20.glUniform4(n, intBuffer);
        }
    }

    public static void glUniform4(int n, FloatBuffer floatBuffer) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUniform4ARB(n, floatBuffer);
        } else {
            GL20.glUniform4(n, floatBuffer);
        }
    }

    public static void glUniformMatrix2(int n, boolean flag, FloatBuffer floatBuffer) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUniformMatrix2ARB(n, flag, floatBuffer);
        } else {
            GL20.glUniformMatrix2(n, flag, floatBuffer);
        }
    }

    public static void glUniformMatrix3(int n, boolean flag, FloatBuffer floatBuffer) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUniformMatrix3ARB(n, flag, floatBuffer);
        } else {
            GL20.glUniformMatrix3(n, flag, floatBuffer);
        }
    }

    public static void glUniformMatrix4(int n, boolean flag, FloatBuffer floatBuffer) {
        if (arbShaderObjects) {
            ARBShaderObjects.glUniformMatrix4ARB(n, flag, floatBuffer);
        } else {
            GL20.glUniformMatrix4(n, flag, floatBuffer);
        }
    }

    public static int glGetAttribLocation(int n, CharSequence charSequence) {
        return arbShaderObjects ? ARBVertexShader.glGetAttribLocationARB(n, charSequence) : GL20.glGetAttribLocation(n, charSequence);
    }

    public static void advancedBindFramebuffer(int n, int n2) {
        if (framebufferSupported) {
            switch (type) {
                case 0:
                    GL30.glBindFramebuffer(n, n2);
                    break;
                case 1:
                    ARBFramebufferObject.glBindFramebuffer(n, n2);
                    break;
                case 2:
                    EXTFramebufferObject.glBindFramebufferEXT(n, n2);
            }
        }
    }

    public static void advancedBindRenderBuffer(int n, int n2) {
        if (framebufferSupported) {
            switch (type) {
                case 0:
                    GL30.glBindRenderbuffer(n, n2);
                    break;
                case 1:
                    ARBFramebufferObject.glBindRenderbuffer(n, n2);
                    break;
                case 2:
                    EXTFramebufferObject.glBindRenderbufferEXT(n, n2);
            }
        }
    }

    public static void advancedDeleteRenderBuffers(int n) {
        if (framebufferSupported) {
            switch (type) {
                case 0:
                    GL30.glDeleteRenderbuffers(n);
                    break;
                case 1:
                    ARBFramebufferObject.glDeleteRenderbuffers(n);
                    break;
                case 2:
                    EXTFramebufferObject.glDeleteRenderbuffersEXT(n);
            }
        }
    }

    public static void advancedDeleteFrameBuffers(int n) {
        if (framebufferSupported) {
            switch (type) {
                case 0:
                    GL30.glDeleteFramebuffers(n);
                    break;
                case 1:
                    ARBFramebufferObject.glDeleteFramebuffers(n);
                    break;
                case 2:
                    EXTFramebufferObject.glDeleteFramebuffersEXT(n);
            }
        }
    }

    public static int advancedGenFrameBuffers() {
        if (!framebufferSupported) {
            return -1;
        }

        switch (type) {
            case 0:
                return GL30.glGenFramebuffers();
            case 1:
                return ARBFramebufferObject.glGenFramebuffers();
            case 2:
                return EXTFramebufferObject.glGenFramebuffersEXT();
            default:
                return -1;
        }
    }

    public static int advancedGenRenderBuffers() {
        if (!framebufferSupported) {
            return -1;
        }

        switch (type) {
            case 0:
                return GL30.glGenRenderbuffers();
            case 1:
                return ARBFramebufferObject.glGenRenderbuffers();
            case 2:
                return EXTFramebufferObject.glGenRenderbuffersEXT();
            default:
                return -1;
        }
    }

    public static void advancedRenderBufferStorage(int n, int n2, int n3, int n4) {
        if (framebufferSupported) {
            switch (type) {
                case 0:
                    GL30.glRenderbufferStorage(n, n2, n3, n4);
                    break;
                case 1:
                    ARBFramebufferObject.glRenderbufferStorage(n, n2, n3, n4);
                    break;
                case 2:
                    EXTFramebufferObject.glRenderbufferStorageEXT(n, n2, n3, n4);
            }
        }
    }

    public static void advancedFramebufferRenderbuffer(int n, int n2, int n3, int n4) {
        if (framebufferSupported) {
            switch (type) {
                case 0:
                    GL30.glFramebufferRenderbuffer(n, n2, n3, n4);
                    break;
                case 1:
                    ARBFramebufferObject.glFramebufferRenderbuffer(n, n2, n3, n4);
                    break;
                case 2:
                    EXTFramebufferObject.glFramebufferRenderbufferEXT(n, n2, n3, n4);
            }
        }
    }

    public static int advancedCheckFrameBufferStatus(int n) {
        if (!framebufferSupported) {
            return -1;
        }

        switch (type) {
            case 0:
                return GL30.glCheckFramebufferStatus(n);
            case 1:
                return ARBFramebufferObject.glCheckFramebufferStatus(n);
            case 2:
                return EXTFramebufferObject.glCheckFramebufferStatusEXT(n);
            default:
                return -1;
        }
    }

    public static void advancedFrameBufferTexture2D(int n, int n2, int n3, int n4, int n5) {
        if (framebufferSupported) {
            switch (type) {
                case 0:
                    GL30.glFramebufferTexture2D(n, n2, n3, n4, n5);
                    break;
                case 1:
                    ARBFramebufferObject.glFramebufferTexture2D(n, n2, n3, n4, n5);
                    break;
                case 2:
                    EXTFramebufferObject.glFramebufferTexture2DEXT(n, n2, n3, n4, n5);
            }
        }
    }

    public static void setActiveTexture(int n) {
        GlStateManager.activeTextureUnit = n;
        if (arbMultitexture) {
            ARBMultitexture.glClientActiveTextureARB(n);
            ARBMultitexture.glActiveTextureARB(n);
        } else {
            GL13.glClientActiveTexture(n);
            GL13.glActiveTexture(n);
        }
    }

    public static void setClientActiveTexture(int n) {
        if (arbMultitexture) {
            ARBMultitexture.glClientActiveTextureARB(n);
        } else {
            GL13.glClientActiveTexture(n);
        }
    }

    public static void setLightmapTextureCoords(int n, float f, float f2) {
        if (arbMultitexture) {
            ARBMultitexture.glMultiTexCoord2fARB(n, f, f2);
        } else {
            GL13.glMultiTexCoord2f(n, f, f2);
        }

        if (n == lightmapTexUnit) {
            lastBrightnessX = f;
            lastBrightnessY = f2;
        }
    }

    public static void glBlendFunc(int n, int n2, int n3, int n4) {
        glBlendFuncZero = (n | n2 | n3 | n4) == 0;
        if (openGL14) {
            if (blendFuncSeparateSupported) {
                EXTBlendFuncSeparate.glBlendFuncSeparateEXT(n, n2, n3, n4);
            } else {
                GL14.glBlendFuncSeparate(n, n2, n3, n4);
            }
        } else {
            GL11.glBlendFunc(n, n2);
        }
    }

    public static boolean isFramebufferEnabled() {
        return framebufferSupported && Minecraft.getMinecraft().gameSettings.fboEnable;
    }
}
