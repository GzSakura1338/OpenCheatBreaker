package net.optifine.util;

import net.minecraft.util.MathHelper;

public class MathUtilsTest {
   public static void main(String[] strs) throws Exception {
      MathUtilsTest.OPER[] values = MathUtilsTest.OPER.values();

      for (int i = 0; i < values.length; i++) {
         MathUtilsTest.OPER oper = values[i];
         dbg("******** " + oper + " ***********");
         test(oper, false);
      }
   }

   public static void test(MathUtilsTest.OPER oper, boolean flag) {
      MathHelper.fastMath = flag;
      double d;
      double d2;
      switch (oper) {
         case SIN:
         case COS:
            d = -MathHelper.floatValue;
            d2 = MathHelper.floatValue;
            break;
         case ASIN:
         case ACOS:
            d = -1.0;
            d2 = 1.0;
            break;
         default:
            return;
      }

      byte b = 10;

      for (int i = 0; i <= b; i++) {
         double d3 = d + i * (d2 - d) / b;
         float f;
         float f2;
         switch (oper) {
            case SIN:
               f = (float)Math.sin(d3);
               f2 = MathHelper.sin((float)d3);
               break;
            case COS:
               f = (float)Math.cos(d3);
               f2 = MathHelper.cos((float)d3);
               break;
            case ASIN:
               f = (float)Math.asin(d3);
               f2 = MathUtils.asin((float)d3);
               break;
            case ACOS:
               f = (float)Math.acos(d3);
               f2 = MathUtils.acos((float)d3);
               break;
            default:
               return;
         }

         dbg(String.format("%.2f, Math: %f, Helper: %f, diff: %f", d3, f, f2, Math.abs(f - f2)));
      }
   }

   public static void dbg(String str) {
      System.out.println(str);
   }

   private enum OPER {
      SIN,
      COS,
      ASIN,
      ACOS;
   }
}
