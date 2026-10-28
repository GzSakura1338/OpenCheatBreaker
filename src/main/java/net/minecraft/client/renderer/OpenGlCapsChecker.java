package net.minecraft.client.renderer;

import org.lwjgl.opengl.GLContext;

public class OpenGlCapsChecker {
    public static boolean checkARBOcclusion() {
        return GLContext.getCapabilities().GL_ARB_occlusion_query;
    }
}
