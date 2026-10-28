package shadersmod.uniform;

import org.lwjgl.opengl.ARBShaderObjects;
import shadersmod.client.Shaders;

public class ShaderUniformFloat extends ShaderUniformBase {
    public float value = -1.0F;

    public ShaderUniformFloat(String str) {
        super(str);
    }

    @Override
    public void onProgramChanged() {
        this.value = -1.0F;
    }

    public void setValue(float f) {
        if (this.getLocation() >= 0 && this.value != f) {
            ARBShaderObjects.glUniform1fARB(this.getLocation(), f);
            Shaders.checkGLError(this.getName());
            this.value = f;
        }
    }

    public float getValue() {
        return this.value;
    }
}
