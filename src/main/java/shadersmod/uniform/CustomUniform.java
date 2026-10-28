package shadersmod.uniform;

import net.optifine.entity.model.anim.IExpression;

public class CustomUniform {
    public String name;
    public UniformType type;
    public IExpression expression;
    public ShaderUniformBase shaderUniform;

    public CustomUniform(String str, UniformType uniformType, IExpression expression) {
        this.name = str;
        this.type = uniformType;
        this.expression = expression;
        this.shaderUniform = uniformType.makeShaderUniform(str);
    }

    public void setProgram(int n) {
        this.shaderUniform.setProgram(n);
    }

    public void update() {
        this.type.updateUniform(this.expression, this.shaderUniform);
    }

    public String getName() {
        return this.name;
    }

    public UniformType getType() {
        return this.type;
    }

    public IExpression getExpression() {
        return this.expression;
    }

    public ShaderUniformBase getShaderUniform() {
        return this.shaderUniform;
    }
}
