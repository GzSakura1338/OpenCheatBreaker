package net.optifine.entity.model.anim;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TestExpressions {
    public static void main(String[] strs) throws Exception {
        ExpressionParser expressionParser = new ExpressionParser((IExpressionResolver)null);

        while (true) {
            try {
                InputStreamReader inputStreamReader = new InputStreamReader(System.in);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String readLine = bufferedReader.readLine();
                if (readLine.length() <= 0) {
                    return;
                }

                IExpression parse = expressionParser.parse(readLine);
                if (parse instanceof IExpressionFloat) {
                    IExpressionFloat expressionFloat = (IExpressionFloat)parse;
                    float eval = expressionFloat.eval();
                    System.out.println("" + eval);
                }

                if (parse instanceof IExpressionBool) {
                    IExpressionBool expressionBool = (IExpressionBool)parse;
                    boolean eval = expressionBool.eval();
                    System.out.println("" + eval);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
