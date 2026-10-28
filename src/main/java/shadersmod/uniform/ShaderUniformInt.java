package shadersmod.uniform;

import org.lwjgl.opengl.ARBShaderObjects;
import shadersmod.client.Shaders;

public class ShaderUniformInt extends ShaderUniformBase {
    public int value = -1;

    public ShaderUniformInt(String str) {
        super(str);
    }

    @Override
    public void onProgramChanged() {
        this.value = -1;
    }

    public void setValue(int n) {
        if (this.getLocation() >= 0 && this.value != n) {
            ARBShaderObjects.glUniform1iARB(this.getLocation(), n);
            Shaders.checkGLError(this.getName());
            this.value = n;
        }
    }

    public int getValue() {
        return this.value;
    }
}
