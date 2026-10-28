package shadersmod.client;

public class ShaderOptionScreen extends ShaderOption {
    public ShaderOptionScreen(String str) {
        super(str, (String)null, (String)null, new String[0], (String)null, (String)null);
    }

    @Override
    public String getNameText() {
        return Shaders.translate("screen." + this.getName(), this.getName());
    }

    @Override
    public String getDescriptionText() {
        return Shaders.translate("screen." + this.getName() + ".comment", (String)null);
    }
}
