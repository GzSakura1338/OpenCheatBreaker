package net.optifine.entity.model.anim;

import net.optifine.expr.IExpression;

public class RenderResolverEntity implements IRenderResolver {
   @Override
   public IExpression getParameter(String str) {
      RenderEntityParameterBool parse = RenderEntityParameterBool.parse(str);
      if (parse != null) {
         return parse;
      }

      RenderEntityParameterFloat parse2 = RenderEntityParameterFloat.parse(str);
      return parse2 != null ? parse2 : null;
   }
}
