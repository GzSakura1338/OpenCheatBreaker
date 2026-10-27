package net.optifine.entity.model.anim;

import net.minecraft.client.model.ModelRenderer;
import net.optifine.expr.ExpressionType;
import net.optifine.expr.IExpressionFloat;

public class ModelVariableFloat implements IExpressionFloat {
   public String name;
   public ModelRenderer modelRenderer;
   public ModelVariableType enumModelVariable;

   public ModelVariableFloat(String str, ModelRenderer modelRenderer, ModelVariableType modelVariableType) {
      this.name = str;
      this.modelRenderer = modelRenderer;
      this.enumModelVariable = modelVariableType;
   }

   @Override
   public ExpressionType getExpressionType() {
      return ExpressionType.FLOAT;
   }

   @Override
   public float eval() {
      return this.getValue();
   }

   public float getValue() {
      return this.enumModelVariable.getFloat(this.modelRenderer);
   }

   public void setValue(float f) {
      this.enumModelVariable.setFloat(this.modelRenderer, f);
   }

   @Override
   public String toString() {
      return this.name;
   }
}
