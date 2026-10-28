package net.optifine.entity.model.anim;

import java.util.ArrayList;
import java.util.Arrays;

public class ParametersVariable implements IParameters {
    public int maxCount = Integer.MAX_VALUE;
    public ExpressionType[] first;
    public ExpressionType[] repeat;
    public ExpressionType[] last;
    public static ExpressionType[] EMPTY = new ExpressionType[0];

    public ParametersVariable() {
        this((ExpressionType[])null, (ExpressionType[])null, (ExpressionType[])null);
    }

    public ParametersVariable(ExpressionType[] expressionTypes, ExpressionType[] expressionTypes2, ExpressionType[] expressionTypes3) {
        this(expressionTypes, expressionTypes2, expressionTypes3, Integer.MAX_VALUE);
    }

    public ParametersVariable(ExpressionType[] expressionTypes, ExpressionType[] expressionTypes2, ExpressionType[] expressionTypes3, int n) {
        this.first = normalize(expressionTypes);
        this.repeat = normalize(expressionTypes2);
        this.last = normalize(expressionTypes3);
        this.maxCount = n;
    }

    public static ExpressionType[] normalize(ExpressionType[] expressionTypes) {
        return expressionTypes == null ? EMPTY : expressionTypes;
    }

    public int getCountRepeat() {
        return this.first == null ? 0 : this.first.length;
    }

    @Override
    public ExpressionType[] getParameterTypes(IExpression[] expressions) {
        int n = this.first.length + this.last.length;
        int n2 = expressions.length - n;
        int n3 = 0;

        for (int i = 0; i + this.repeat.length <= n2 && n + i + this.repeat.length <= this.maxCount; i += this.repeat.length) {
            n3++;
        }

        ArrayList list = new ArrayList();
        list.addAll(Arrays.asList(this.first));

        for (int i = 0; i < n3; i++) {
            list.addAll(Arrays.asList(this.repeat));
        }

        list.addAll(Arrays.asList(this.last));
        return (ExpressionType[])list.toArray(new ExpressionType[list.size()]);
    }

    public ParametersVariable first(ExpressionType... expressionTypes) {
        return new ParametersVariable(expressionTypes, this.repeat, this.last);
    }

    public ParametersVariable repeat(ExpressionType... expressionTypes) {
        return new ParametersVariable(this.first, expressionTypes, this.last);
    }

    public ParametersVariable last(ExpressionType... expressionTypes) {
        return new ParametersVariable(this.first, this.repeat, expressionTypes);
    }

    public ParametersVariable maxCount(int n) {
        return new ParametersVariable(this.first, this.repeat, this.last, n);
    }

    public ExpressionType[] getFirst() {
        return this.first;
    }

    public ExpressionType[] getLast() {
        return this.last;
    }

    public ExpressionType[] getRepeat() {
        return this.repeat;
    }
}
