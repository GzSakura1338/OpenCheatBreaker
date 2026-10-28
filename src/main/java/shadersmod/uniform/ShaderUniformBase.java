package shadersmod.uniform;

import org.lwjgl.opengl.ARBShaderObjects;

public abstract class ShaderUniformBase {
    public int program = -1;
    public int location = -1;
    public String name;

    public ShaderUniformBase(String str) {
        this.name = str;
    }

    public void setProgram(int n) {
        if (this.program != n) {
            this.program = n;
            this.location = ARBShaderObjects.glGetUniformLocationARB(n, this.name);
            this.onProgramChanged();
        }
    }

    public abstract void onProgramChanged();

    public String getName() {
        return this.name;
    }

    public int getProgram() {
        return this.program;
    }

    public int getLocation() {
        return this.location;
    }

    public boolean isDefined() {
        return this.location >= 0;
    }
}
