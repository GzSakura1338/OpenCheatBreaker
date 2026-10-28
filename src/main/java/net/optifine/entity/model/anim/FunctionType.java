package net.optifine.entity.model.anim;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.MathHelper;
import net.optifine.Config;
import net.optifine.MathUtils;
import shadersmod.uniform.Smoother;

public enum FunctionType {
    PLUS(10, ExpressionType.FLOAT, "+", ExpressionType.FLOAT, ExpressionType.FLOAT),
    MINUS(10, ExpressionType.FLOAT, "-", ExpressionType.FLOAT, ExpressionType.FLOAT),
    MUL(11, ExpressionType.FLOAT, "*", ExpressionType.FLOAT, ExpressionType.FLOAT),
    DIV(11, ExpressionType.FLOAT, "/", ExpressionType.FLOAT, ExpressionType.FLOAT),
    MOD(11, ExpressionType.FLOAT, "%", ExpressionType.FLOAT, ExpressionType.FLOAT),
    NEG(12, ExpressionType.FLOAT, "neg", ExpressionType.FLOAT),
    PI(ExpressionType.FLOAT, "pi"),
    SIN(ExpressionType.FLOAT, "sin", ExpressionType.FLOAT),
    COS(ExpressionType.FLOAT, "cos", ExpressionType.FLOAT),
    ASIN(ExpressionType.FLOAT, "asin", ExpressionType.FLOAT),
    ACOS(ExpressionType.FLOAT, "acos", ExpressionType.FLOAT),
    TAN(ExpressionType.FLOAT, "tan", ExpressionType.FLOAT),
    ATAN(ExpressionType.FLOAT, "atan", ExpressionType.FLOAT),
    ATAN2(ExpressionType.FLOAT, "atan2", ExpressionType.FLOAT, ExpressionType.FLOAT),
    TORAD(ExpressionType.FLOAT, "torad", ExpressionType.FLOAT),
    TODEG(ExpressionType.FLOAT, "todeg", ExpressionType.FLOAT),
    MIN(ExpressionType.FLOAT, "min", new ParametersVariable().first(ExpressionType.FLOAT).repeat(ExpressionType.FLOAT)),
    MAX(ExpressionType.FLOAT, "max", new ParametersVariable().first(ExpressionType.FLOAT).repeat(ExpressionType.FLOAT)),
    CLAMP(ExpressionType.FLOAT, "clamp", ExpressionType.FLOAT, ExpressionType.FLOAT, ExpressionType.FLOAT),
    ABS(ExpressionType.FLOAT, "abs", ExpressionType.FLOAT),
    FLOOR(ExpressionType.FLOAT, "floor", ExpressionType.FLOAT),
    CEIL(ExpressionType.FLOAT, "ceil", ExpressionType.FLOAT),
    EXP(ExpressionType.FLOAT, "exp", ExpressionType.FLOAT),
    FRAC(ExpressionType.FLOAT, "frac", ExpressionType.FLOAT),
    LOG(ExpressionType.FLOAT, "log", ExpressionType.FLOAT),
    POW(ExpressionType.FLOAT, "pow", ExpressionType.FLOAT, ExpressionType.FLOAT),
    RANDOM(ExpressionType.FLOAT, "random"),
    ROUND(ExpressionType.FLOAT, "round", ExpressionType.FLOAT),
    SIGNUM(ExpressionType.FLOAT, "signum", ExpressionType.FLOAT),
    SQRT(ExpressionType.FLOAT, "sqrt", ExpressionType.FLOAT),
    FMOD(ExpressionType.FLOAT, "fmod", ExpressionType.FLOAT, ExpressionType.FLOAT),
    TIME(ExpressionType.FLOAT, "time"),
    IF(
        ExpressionType.FLOAT,
        "if",
        new ParametersVariable().first(ExpressionType.BOOL, ExpressionType.FLOAT).repeat(ExpressionType.BOOL, ExpressionType.FLOAT).last(ExpressionType.FLOAT)
    ),
    NOT(12, ExpressionType.BOOL, "!", ExpressionType.BOOL),
    AND(3, ExpressionType.BOOL, "&&", ExpressionType.BOOL, ExpressionType.BOOL),
    OR(2, ExpressionType.BOOL, "||", ExpressionType.BOOL, ExpressionType.BOOL),
    GREATER(8, ExpressionType.BOOL, ">", ExpressionType.FLOAT, ExpressionType.FLOAT),
    GREATER_OR_EQUAL(8, ExpressionType.BOOL, ">=", ExpressionType.FLOAT, ExpressionType.FLOAT),
    SMALLER(8, ExpressionType.BOOL, "<", ExpressionType.FLOAT, ExpressionType.FLOAT),
    SMALLER_OR_EQUAL(8, ExpressionType.BOOL, "<=", ExpressionType.FLOAT, ExpressionType.FLOAT),
    EQUAL(7, ExpressionType.BOOL, "==", ExpressionType.FLOAT, ExpressionType.FLOAT),
    NOT_EQUAL(7, ExpressionType.BOOL, "!=", ExpressionType.FLOAT, ExpressionType.FLOAT),
    BETWEEN(7, ExpressionType.BOOL, "between", ExpressionType.FLOAT, ExpressionType.FLOAT, ExpressionType.FLOAT),
    EQUALS(7, ExpressionType.BOOL, "equals", ExpressionType.FLOAT, ExpressionType.FLOAT, ExpressionType.FLOAT),
    IN(ExpressionType.BOOL, "in", new ParametersVariable().first(ExpressionType.FLOAT).repeat(ExpressionType.FLOAT).last(ExpressionType.FLOAT)),
    SMOOTH(ExpressionType.FLOAT, "smooth", new ParametersVariable().first(ExpressionType.FLOAT, ExpressionType.FLOAT).repeat(ExpressionType.FLOAT).maxCount(4)),
    TRUE(ExpressionType.BOOL, "true"),
    FALSE(ExpressionType.BOOL, "false");

    public int precedence;
    public ExpressionType expressionType;
    public String name;
    public IParameters parameters;
    public static FunctionType[] VALUES = values();
    public static Map<Integer, Float> mapSmooth = new HashMap<>();

    FunctionType(ExpressionType expressionType, String str, ExpressionType... expressionTypes) {
        this(0, expressionType, str, expressionTypes);
    }

    FunctionType(int n, ExpressionType expressionType, String str, ExpressionType... expressionTypes) {
        this(n, expressionType, str, new Parameters(expressionTypes));
    }

    FunctionType(ExpressionType expressionType, String str, IParameters parameters) {
        this(0, expressionType, str, parameters);
    }

    FunctionType(int n, ExpressionType expressionType, String str, IParameters parameters) {
        this.precedence = n;
        this.expressionType = expressionType;
        this.name = str;
        this.parameters = parameters;
    }

    public String getName() {
        return this.name;
    }

    public int getPrecedence() {
        return this.precedence;
    }

    public ExpressionType getExpressionType() {
        return this.expressionType;
    }

    public IParameters getParameters() {
        return this.parameters;
    }

    public int getParameterCount(IExpression[] expressions) {
        return this.parameters.getParameterTypes(expressions).length;
    }

    public ExpressionType[] getParameterTypes(IExpression[] expressions) {
        return this.parameters.getParameterTypes(expressions);
    }

    public float evalFloat(IExpression[] expressions) {
        switch (this) {
            case PLUS:
                return evalFloat(expressions, 0) + evalFloat(expressions, 1);
            case MINUS:
                return evalFloat(expressions, 0) - evalFloat(expressions, 1);
            case MUL:
                return evalFloat(expressions, 0) * evalFloat(expressions, 1);
            case DIV:
                return evalFloat(expressions, 0) / evalFloat(expressions, 1);
            case MOD:
                float evalFloat2 = evalFloat(expressions, 0);
                float evalFloat3 = evalFloat(expressions, 1);
                return evalFloat2 - evalFloat3 * (int)(evalFloat2 / evalFloat3);
            case NEG:
                return -evalFloat(expressions, 0);
            case PI:
                return (float) Math.PI;
            case SIN:
                return MathHelper.sin(evalFloat(expressions, 0));
            case COS:
                return MathHelper.cos(evalFloat(expressions, 0));
            case ASIN:
                return (float)Math.asin(evalFloat(expressions, 0));
            case ACOS:
                return (float)Math.acos(evalFloat(expressions, 0));
            case TAN:
                return (float)Math.tan(evalFloat(expressions, 0));
            case ATAN:
                return (float)Math.atan(evalFloat(expressions, 0));
            case ATAN2:
                return (float)Math.atan2(evalFloat(expressions, 0), evalFloat(expressions, 1));
            case TORAD:
                return MathUtils.toRad(evalFloat(expressions, 0));
            case TODEG:
                return MathUtils.toDeg(evalFloat(expressions, 0));
            case MIN:
                return this.getMin(expressions);
            case MAX:
                return this.getMax(expressions);
            case CLAMP:
                return MathHelper.clamp_float(evalFloat(expressions, 0), evalFloat(expressions, 1), evalFloat(expressions, 2));
            case ABS:
                return MathHelper.abs(evalFloat(expressions, 0));
            case EXP:
                return (float)Math.exp(evalFloat(expressions, 0));
            case FLOOR:
                return MathHelper.floor_float(evalFloat(expressions, 0));
            case CEIL:
                return MathHelper.ceiling_float_int(evalFloat(expressions, 0));
            case FRAC:
                float evalFloat4 = evalFloat(expressions, 0);
                return evalFloat4 - MathHelper.floor_float(evalFloat4);
            case LOG:
                return (float)Math.log(evalFloat(expressions, 0));
            case POW:
                return (float)Math.pow(evalFloat(expressions, 0), evalFloat(expressions, 1));
            case RANDOM:
                return (float)Math.random();
            case ROUND:
                return Math.round(evalFloat(expressions, 0));
            case SIGNUM:
                return Math.signum(evalFloat(expressions, 0));
            case SQRT:
                return MathHelper.sqrt_float(evalFloat(expressions, 0));
            case FMOD:
                float evalFloat5 = evalFloat(expressions, 0);
                float evalFloat6 = evalFloat(expressions, 1);
                return evalFloat5 - evalFloat6 * MathHelper.floor_float(evalFloat5 / evalFloat6);
            case TIME:
                Minecraft minecraft = Minecraft.getMinecraft();
                WorldClient theWorld = minecraft.theWorld;
                if (theWorld == null) {
                    return 0.0F;
                }

                return (float)(theWorld.getTotalWorldTime() % 24000L) + Config.renderPartialTicks;
            case IF:
                int n = (expressions.length - 1) / 2;

                for (int i = 0; i < n; i++) {
                    int n2 = i * 2;
                    if (evalBool(expressions, n2)) {
                        return evalFloat(expressions, n2 + 1);
                    }
                }

                return evalFloat(expressions, n * 2);
            case SMOOTH:
                int evalFloat7 = (int)evalFloat(expressions, 0);
                float evalFloat8 = evalFloat(expressions, 1);
                float f = expressions.length > 2 ? evalFloat(expressions, 2) : 1.0F;
                float f2 = expressions.length > 3 ? evalFloat(expressions, 3) : f;
                return Smoother.getSmoothValue(evalFloat7, evalFloat8, f, f2);
            default:
                Config.warn("Unknown function type: " + this);
                return 0.0F;
        }
    }

    public float getMin(IExpression[] expressions) {
        if (expressions.length == 2) {
            return Math.min(evalFloat(expressions, 0), evalFloat(expressions, 1));
        }

        float evalFloat2 = evalFloat(expressions, 0);

        for (int i = 1; i < expressions.length; i++) {
            float evalFloat3 = evalFloat(expressions, i);
            if (evalFloat3 < evalFloat2) {
                evalFloat2 = evalFloat3;
            }
        }

        return evalFloat2;
    }

    public float getMax(IExpression[] expressions) {
        if (expressions.length == 2) {
            return Math.max(evalFloat(expressions, 0), evalFloat(expressions, 1));
        }

        float evalFloat2 = evalFloat(expressions, 0);

        for (int i = 1; i < expressions.length; i++) {
            float evalFloat3 = evalFloat(expressions, i);
            if (evalFloat3 > evalFloat2) {
                evalFloat2 = evalFloat3;
            }
        }

        return evalFloat2;
    }

    public static float evalFloat(IExpression[] expressions, int n) {
        IExpressionFloat expressionFloat = (IExpressionFloat)expressions[n];
        return expressionFloat.eval();
    }

    public boolean evalBool(IExpression[] expressions) {
        switch (this) {
            case TRUE:
                return true;
            case FALSE:
                return false;
            case NOT:
                return !evalBool(expressions, 0);
            case AND:
                return evalBool(expressions, 0) && evalBool(expressions, 1);
            case OR:
                return evalBool(expressions, 0) || evalBool(expressions, 1);
            case GREATER:
                return evalFloat(expressions, 0) > evalFloat(expressions, 1);
            case GREATER_OR_EQUAL:
                return evalFloat(expressions, 0) >= evalFloat(expressions, 1);
            case SMALLER:
                return evalFloat(expressions, 0) < evalFloat(expressions, 1);
            case SMALLER_OR_EQUAL:
                return evalFloat(expressions, 0) <= evalFloat(expressions, 1);
            case EQUAL:
                return evalFloat(expressions, 0) == evalFloat(expressions, 1);
            case NOT_EQUAL:
                return evalFloat(expressions, 0) != evalFloat(expressions, 1);
            case BETWEEN:
                float evalFloat2 = evalFloat(expressions, 0);
                return evalFloat2 >= evalFloat(expressions, 1) && evalFloat2 <= evalFloat(expressions, 2);
            case EQUALS:
                float f = evalFloat(expressions, 0) - evalFloat(expressions, 1);
                float evalFloat3 = evalFloat(expressions, 2);
                return Math.abs(f) <= evalFloat3;
            case IN:
                float evalFloat4 = evalFloat(expressions, 0);

                for (int i = 1; i < expressions.length; i++) {
                    float evalFloat5 = evalFloat(expressions, i);
                    if (evalFloat4 == evalFloat5) {
                        return true;
                    }
                }

                return false;
            default:
                Config.warn("Unknown function type: " + this);
                return false;
        }
    }

    public static boolean evalBool(IExpression[] expressions, int n) {
        IExpressionBool expressionBool = (IExpressionBool)expressions[n];
        return expressionBool.eval();
    }

    public static FunctionType parse(String str) {
        for (int i = 0; i < VALUES.length; i++) {
            FunctionType functionType = VALUES[i];
            if (functionType.getName().equals(str)) {
                return functionType;
            }
        }

        return null;
    }
}
