package shadersmod.uniform;

import org.lwjgl.opengl.ARBShaderObjects;
import shadersmod.client.Shaders;

public class ShaderUniformFloat4 extends ShaderUniformBase {
    public float[] values = new float[4];

    public ShaderUniformFloat4(String str) {
        super(str);
    }

    @Override
    public void onProgramChanged() {
        this.values[0] = 0.0F;
        this.values[1] = 0.0F;
        this.values[2] = 0.0F;
        this.values[3] = 0.0F;
    }

    public void setValue(float f, float f2, float f3, float f4) {
        if (this.getLocation() >= 0 && (this.values[0] != f || this.values[1] != f2 || this.values[2] != f3 || this.values[3] != f4)) {
            ARBShaderObjects.glUniform4fARB(this.getLocation(), f, f2, f3, f4);
            Shaders.checkGLError(this.getName());
            this.values[0] = f;
            this.values[1] = f2;
            this.values[2] = f3;
            this.values[3] = f4;
        }
    }

    public float[] getValues() {
        return this.values;
    }
}
