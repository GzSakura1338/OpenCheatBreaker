package shadersmod.uniform;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.entity.model.anim.ConstantFloat;
import net.optifine.entity.model.anim.IExpression;
import net.optifine.entity.model.anim.IExpressionResolver;
import shadersmod.common.SMCLog;

public class ShaderExpressionResolver implements IExpressionResolver {
    public Map<String, IExpression> mapExpressions = new HashMap<>();

    public ShaderExpressionResolver(Map<String, IExpression> map) {
        this.registerExpressions();

        for (String str : map.keySet()) {
            IExpression expression = (IExpression)map.get(str);
            this.registerExpression(str, expression);
        }
    }

    public void registerExpressions() {
        ShaderParameterFloat[] values = ShaderParameterFloat.values();

        for (int i = 0; i < values.length; i++) {
            ShaderParameterFloat shaderParameterFloat = values[i];
            this.mapExpressions.put(shaderParameterFloat.getName(), shaderParameterFloat);
        }

        ShaderParameterBool[] values2 = ShaderParameterBool.values();

        for (int i = 0; i < values2.length; i++) {
            ShaderParameterBool shaderParameterBool = values2[i];
            this.mapExpressions.put(shaderParameterBool.getName(), shaderParameterBool);
        }

        BiomeGenBase[] biomeGenArray = BiomeGenBase.getBiomeGenArray();

        for (int i = 0; i < biomeGenArray.length; i++) {
            BiomeGenBase biomeGenBase = biomeGenArray[i];
            if (biomeGenBase != null) {
                String trim = biomeGenBase.biomeName.trim();
                trim = "BIOME_" + trim.toUpperCase().replace(' ', '_');
                int biomeID = biomeGenBase.biomeID;
                ConstantFloat constantFloat = new ConstantFloat(biomeID);
                this.registerExpression(trim, constantFloat);
            }
        }
    }

    public boolean registerExpression(String str, IExpression expression) {
        if (this.mapExpressions.containsKey(str)) {
            SMCLog.warning("Expression already defined: " + str);
            return false;
        } else {
            this.mapExpressions.put(str, expression);
            return true;
        }
    }

    @Override
    public IExpression getExpression(String str) {
        return this.mapExpressions.get(str);
    }

    public boolean hasExpression(String str) {
        return this.mapExpressions.containsKey(str);
    }
}
