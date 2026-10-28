package shadersmod.uniform;

public class CustomUniforms {
    public CustomUniform[] uniforms;

    public CustomUniforms(CustomUniform[] customUniforms) {
        this.uniforms = customUniforms;
    }

    public void setProgram(int n) {
        for (int i = 0; i < this.uniforms.length; i++) {
            CustomUniform customUniform = this.uniforms[i];
            customUniform.setProgram(n);
        }
    }

    public void update() {
        for (int i = 0; i < this.uniforms.length; i++) {
            CustomUniform customUniform = this.uniforms[i];
            customUniform.update();
        }
    }
}
