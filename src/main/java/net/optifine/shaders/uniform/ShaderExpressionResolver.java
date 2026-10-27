package net.optifine.shaders.uniform;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.expr.ConstantFloat;
import net.optifine.expr.IExpression;
import net.optifine.expr.IExpressionResolver;
import net.optifine.shaders.SMCLog;

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
         this.addParameterFloat(this.mapExpressions, shaderParameterFloat);
      }

      ShaderParameterBool[] values2 = ShaderParameterBool.values();

      for (int i = 0; i < values2.length; i++) {
         ShaderParameterBool shaderParameterBool = values2[i];
         this.mapExpressions.put(shaderParameterBool.getName(), shaderParameterBool);
      }

      for (BiomeGenBase biomeGenBase : BiomeGenBase.BIOME_ID_MAP.values()) {
         String trim = biomeGenBase.biomeName.trim();
         trim = "BIOME_" + trim.toUpperCase().replace(' ', '_');
         int biomeID = biomeGenBase.biomeID;
         ConstantFloat constantFloat = new ConstantFloat(biomeID);
         this.registerExpression(trim, constantFloat);
      }
   }

   public void addParameterFloat(Map<String, IExpression> map, ShaderParameterFloat shaderParameterFloat) {
      String[] indexNames1 = shaderParameterFloat.getIndexNames1();
      if (indexNames1 == null) {
         map.put(shaderParameterFloat.getName(), new ShaderParameterIndexed(shaderParameterFloat));
      } else {
         for (int i = 0; i < indexNames1.length; i++) {
            String str = indexNames1[i];
            String[] indexNames2 = shaderParameterFloat.getIndexNames2();
            if (indexNames2 == null) {
               map.put(shaderParameterFloat.getName() + "." + str, new ShaderParameterIndexed(shaderParameterFloat, i));
            } else {
               for (int j = 0; j < indexNames2.length; j++) {
                  String str2 = indexNames2[j];
                  map.put(shaderParameterFloat.getName() + "." + str + "." + str2, new ShaderParameterIndexed(shaderParameterFloat, i, j));
               }
            }
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
