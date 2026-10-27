package net.optifine.entity.model.anim;

import net.optifine.Config;
import net.optifine.expr.ExpressionParser;
import net.optifine.expr.IExpressionFloat;
import net.optifine.expr.ParseException;

public class ModelVariableUpdater {
   public String modelVariableName;
   public String expressionText;
   public ModelVariableFloat modelVariable;
   public IExpressionFloat expression;

   public boolean initialize(IModelResolver modelResolver) {
      this.modelVariable = modelResolver.getModelVariable(this.modelVariableName);
      if (this.modelVariable == null) {
         Config.warn("Model variable not found: " + this.modelVariableName);
         return false;
      }

      try {
         ExpressionParser expressionParser = new ExpressionParser(modelResolver);
         this.expression = expressionParser.parseFloat(this.expressionText);
         return true;
      } catch (ParseException e) {
         Config.warn("Error parsing expression: " + this.expressionText);
         Config.warn(e.getClass().getName() + ": " + e.getMessage());
         return false;
      }
   }

   public ModelVariableUpdater(String str, String str2) {
      this.modelVariableName = str;
      this.expressionText = str2;
   }

   public void update() {
      float eval = this.expression.eval();
      this.modelVariable.setValue(eval);
   }
}
