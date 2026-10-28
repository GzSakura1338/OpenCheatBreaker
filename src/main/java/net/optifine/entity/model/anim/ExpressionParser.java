package net.optifine.entity.model.anim;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import net.optifine.Config;

public class ExpressionParser {
    public IExpressionResolver expressionResolver;

    public ExpressionParser(IExpressionResolver expressionResolver) {
        this.expressionResolver = expressionResolver;
    }

    public IExpressionFloat parseFloat(String str) throws ParseException {
        IExpression parse = this.parse(str);
        if (!(parse instanceof IExpressionFloat)) {
            throw new ParseException("Not a float expression: " + parse.getExpressionType());
        } else {
            return (IExpressionFloat)parse;
        }
    }

    public IExpressionBool parseBool(String str) throws ParseException {
        IExpression parse = this.parse(str);
        if (!(parse instanceof IExpressionBool)) {
            throw new ParseException("Not a boolean expression: " + parse.getExpressionType());
        } else {
            return (IExpressionBool)parse;
        }
    }

    public IExpression parse(String str) throws ParseException {
        try {
            Token[] parse = TokenParser.parse(str);
            if (parse == null) {
                return null;
            }

            ArrayDeque arrayDeque = new ArrayDeque<>(Arrays.asList(parse));
            return this.parseInfix(arrayDeque);
        } catch (IOException e) {
            throw new ParseException(e.getMessage(), e);
        }
    }

    public IExpression parseInfix(Deque<Token> deque) throws ParseException {
        if (deque.isEmpty()) {
            return null;
        }

        LinkedList linkedList = new LinkedList();
        LinkedList linkedList2 = new LinkedList();
        IExpression parseExpression = this.parseExpression(deque);
        checkNull(parseExpression, "Missing expression");
        linkedList.add(parseExpression);

        while (true) {
            Token poll = (Token)deque.poll();
            if (poll == null) {
                return this.makeInfix(linkedList, linkedList2);
            }

            if (poll.getType() != TokenType.OPERATOR) {
                throw new ParseException("Invalid operator: " + poll);
            }

            IExpression parseExpression2 = this.parseExpression(deque);
            checkNull(parseExpression2, "Missing expression");
            linkedList2.add(poll);
            linkedList.add(parseExpression2);
        }
    }

    public IExpression makeInfix(List<IExpression> list, List<Token> list2) throws ParseException {
        LinkedList linkedList = new LinkedList();

        for (Token token : list2) {
            FunctionType parse = FunctionType.parse(token.getText());
            checkNull(parse, "Invalid operator: " + token);
            linkedList.add(parse);
        }

        return this.makeInfixFunc(list, linkedList);
    }

    public IExpression makeInfixFunc(List<IExpression> list, List<FunctionType> list2) throws ParseException {
        if (list.size() != list2.size() + 1) {
            throw new ParseException("Invalid infix expression, expressions: " + list.size() + ", operators: " + list2.size());
        }

        if (list.size() == 1) {
            return (IExpression)list.get(0);
        }

        int n = Integer.MAX_VALUE;
        int n2 = Integer.MIN_VALUE;

        for (FunctionType functionType : list2) {
            n = Math.min(functionType.getPrecedence(), n);
            n2 = Math.max(functionType.getPrecedence(), n2);
        }

        if (n2 >= n && n2 - n <= 10) {
            for (int i = n2; i >= n; i--) {
                this.mergeOperators(list, list2, i);
            }

            if (list.size() == 1 && list2.size() == 0) {
                return (IExpression)list.get(0);
            } else {
                throw new ParseException("Error merging operators, expressions: " + list.size() + ", operators: " + list2.size());
            }
        } else {
            throw new ParseException("Invalid infix precedence, min: " + n + ", max: " + n2);
        }
    }

    public void mergeOperators(List<IExpression> list, List<FunctionType> list2, int n) throws ParseException {
        for (int i = 0; i < list2.size(); i++) {
            FunctionType functionType = (FunctionType)list2.get(i);
            if (functionType.getPrecedence() == n) {
                list2.remove(i);
                IExpression expression = (IExpression)list.remove(i);
                IExpression expression2 = (IExpression)list.remove(i);
                IExpression makeFunction2 = makeFunction(functionType, new IExpression[]{expression, expression2});
                list.add(i, makeFunction2);
                i--;
            }
        }
    }

    public IExpression parseExpression(Deque<Token> deque) throws ParseException {
        Token poll = (Token)deque.poll();
        checkNull(poll, "Missing expression");
        switch (poll.getType()) {
            case NUMBER:
                return makeConstantFloat(poll);
            case IDENTIFIER:
                FunctionType functionType = this.getFunctionType(poll, deque);
                if (functionType != null) {
                    return this.makeFunction(functionType, deque);
                }

                return this.makeVariable(poll);
            case BRACKET_OPEN:
                return this.makeBracketed(poll, deque);
            case OPERATOR:
                FunctionType parse = FunctionType.parse(poll.getText());
                checkNull(parse, "Invalid operator: " + poll);
                if (parse == FunctionType.PLUS) {
                    return this.parseExpression(deque);
                } else if (parse == FunctionType.MINUS) {
                    IExpression parseExpression = this.parseExpression(deque);
                    return makeFunction(FunctionType.NEG, new IExpression[]{parseExpression});
                } else if (parse == FunctionType.NOT) {
                    IExpression parseExpression = this.parseExpression(deque);
                    return makeFunction(FunctionType.NOT, new IExpression[]{parseExpression});
                }
            default:
                throw new ParseException("Invalid expression: " + poll);
        }
    }

    public static IExpression makeConstantFloat(Token token) throws ParseException {
        float parseFloat = Config.parseFloat(token.getText(), Float.NaN);
        if (parseFloat == Float.NaN) {
            throw new ParseException("Invalid float value: " + token);
        } else {
            return new ConstantFloat(parseFloat);
        }
    }

    public FunctionType getFunctionType(Token token, Deque<Token> deque) throws ParseException {
        Token peek = (Token)deque.peek();
        if (peek != null && peek.getType() == TokenType.BRACKET_OPEN) {
            FunctionType parse = FunctionType.parse(token.getText());
            checkNull(parse, "Unknown function: " + token);
            return parse;
        } else {
            FunctionType parse = FunctionType.parse(token.getText());
            if (parse == null) {
                return null;
            } else if (parse.getParameterCount(new IExpression[0]) > 0) {
                throw new ParseException("Missing arguments: " + parse);
            } else {
                return parse;
            }
        }
    }

    public IExpression makeFunction(FunctionType functionType, Deque<Token> deque) throws ParseException {
        if (functionType.getParameterCount(new IExpression[0]) == 0) {
            return makeFunction(functionType, new IExpression[0]);
        }

        Token poll = (Token)deque.poll();
        Deque group = getGroup(deque, TokenType.BRACKET_CLOSE, true);
        IExpression[] parseExpressions = this.parseExpressions(group);
        return makeFunction(functionType, parseExpressions);
    }

    public IExpression[] parseExpressions(Deque<Token> deque) throws ParseException {
        ArrayList list = new ArrayList();

        while (true) {
            Deque group = getGroup(deque, TokenType.COMMA, false);
            IExpression parseInfix = this.parseInfix(group);
            if (parseInfix == null) {
                return (IExpression[])list.toArray(new IExpression[list.size()]);
            }

            list.add(parseInfix);
        }
    }

    public static IExpression makeFunction(FunctionType functionType, IExpression[] expressions) throws ParseException {
        ExpressionType[] parameterTypes = functionType.getParameterTypes(expressions);
        if (expressions.length != parameterTypes.length) {
            throw new ParseException(
                "Invalid number of arguments, function: \"" + functionType.getName() + "\", count arguments: " + expressions.length + ", should be: " + parameterTypes.length
            );
        }

        for (int i = 0; i < expressions.length; i++) {
            IExpression expression = expressions[i];
            ExpressionType expressionType = expression.getExpressionType();
            ExpressionType expressionType2 = parameterTypes[i];
            if (expressionType != expressionType2) {
                throw new ParseException("Invalid argument type, function: \"" + functionType.getName() + "\", index: " + i + ", type: " + expressionType + ", should be: " + expressionType2);
            }
        }

        if (functionType.getExpressionType() == ExpressionType.FLOAT) {
            return new FunctionFloat(functionType, expressions);
        } else if (functionType.getExpressionType() == ExpressionType.BOOL) {
            return new FunctionBool(functionType, expressions);
        } else {
            throw new ParseException("Unknown function type: " + functionType.getExpressionType() + ", function: " + functionType.getName());
        }
    }

    public IExpression makeVariable(Token token) throws ParseException {
        if (this.expressionResolver == null) {
            throw new ParseException("Model variable not found: " + token);
        } else {
            IExpression expression = this.expressionResolver.getExpression(token.getText());
            if (expression == null) {
                throw new ParseException("Model variable not found: " + token);
            } else {
                return expression;
            }
        }
    }

    public IExpression makeBracketed(Token token, Deque<Token> deque) throws ParseException {
        Deque group = getGroup(deque, TokenType.BRACKET_CLOSE, true);
        return this.parseInfix(group);
    }

    public static Deque<Token> getGroup(Deque<Token> deque, TokenType tokenType, boolean flag) throws ParseException {
        ArrayDeque arrayDeque = new ArrayDeque();
        int n = 0;
        Iterator iterator = deque.iterator();

        while (iterator.hasNext()) {
            Token token = (Token)iterator.next();
            iterator.remove();
            if (n == 0 && token.getType() == tokenType) {
                return arrayDeque;
            }

            arrayDeque.add(token);
            if (token.getType() == TokenType.BRACKET_OPEN) {
                n++;
            }

            if (token.getType() == TokenType.BRACKET_CLOSE) {
                n--;
            }
        }

        if (flag) {
            throw new ParseException("Missing end token: " + tokenType);
        } else {
            return arrayDeque;
        }
    }

    public static void checkNull(Object obj, String str) throws ParseException {
        if (obj == null) {
            throw new ParseException(str);
        }
    }
}
