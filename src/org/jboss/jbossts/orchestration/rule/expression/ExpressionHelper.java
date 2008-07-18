package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import static org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser.*;
import org.jboss.jbossts.orchestration.rule.type.Type;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.Token;

import java.util.List;
import java.util.ArrayList;

/**
 * helper class to transform parsed expression AST into an actual Expression instance
 */
public class ExpressionHelper
{
    public static Expression createExpression(Bindings bindings, CommonTree exprTree)
    {
        return createExpression(bindings, exprTree, Type.UNDEFINED);
    }

    public static Expression createExpression(Bindings bindings, CommonTree exprTree, Type type)
    {
        // we expect expr = simple_expr |
        //                  (UNARYOP unary_oper expr) |
        //                  (BINOP infix_oper simple_expr exp) |
        //                  (TERNOP simple_expr expr expr)
        //
        // where simple_expr = (DOLLARSYM) |
        //                     (SYMBOL)
        //                     (ARRAY SYMBOL idx_list)
        //                     (METH SYMBOL)
        //                     (METH SYMBOL expr_list)
        //                     (NUMBER)
        //                     (STRING)
        //                     expr

        Token token = exprTree.getToken();
        int tokenType = token.getType();
        Expression expr = null;
        switch (tokenType) {
            case DOLLARSYM:
            {
                expr = new DollarExpression(type, token);
            }
            break;
            case SYMBOL:
            {
                // check for embedded dots

                String text = token.getText();
                int dotIdx = text.lastIndexOf('.');
                if (dotIdx < 0) {
                    // direct variable reference
                    expr = new Variable(type, token);
                } else {
                    // field reference either to an instance named by a field or or a static

                    String[] parts = text.split("\\.");
                    if (parts.length < 2) {
                        // oops malformed symbol either "." ro "foo." or ".foo"
                        // shoudl nto happen but ...
                        System.err.println("ExpressionHelper.createExpression : unexpected symbol "  + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
                    } else {
                        String prefix = parts[0];

                        if (bindings.lookup(prefix) != null) {
                            // intitial segment of text identifies a bound variable so treat as
                            // instance field access
                            int l = parts.length - 1;
                            String[] fields = new String[l];
                            for (int i = 0; i < l; i++) {
                                fields[i] = parts[i + 1];
                            }
                            expr = new FieldExpression(type, token, prefix, fields);
                        } else {
                            String clazzName = text.substring(0, dotIdx);
                            String fieldName = text.substring(dotIdx);
                            expr = new StaticExpression(type, token, clazzName, fieldName);
                        }
                    }
                }
            }
            break;
            case ARRAY:
            {
                CommonTree child0 = (CommonTree) exprTree.getChild(0);
                CommonTree child1 = (CommonTree) exprTree.getChild(1);
                token = child0.getToken();
                if (token.getType() != SYMBOL) {
                    System.err.println("ExpressionHelper.createExpression : unexpected token Type in array expression tree " + tokenType + " for token " + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
                } else {
                    List<Expression> indices = createExpressionList(bindings, child1, Type.INTEGER);
                    if (indices != null) {
                        expr = new ArrayExpression(type, token, indices);
                    } else {
                        System.err.println("ExpressionHelper.createExpression : invalid array index expression @ " + token.getLine() + "." + token.getCharPositionInLine());
                    }
                }
            }
            break;
            case METH:
            {
                CommonTree child0 = (CommonTree) exprTree.getChild(0);
                CommonTree child1;
                if (exprTree.getChildCount() > 1) {
                    child1 = (CommonTree) exprTree.getChild(1);
                } else {
                    child1 = null;
                }
                token = child0.getToken();
                if (token.getType() != SYMBOL) {
                    System.err.println("ExpressionHelper.createExpression : unexpected token Type in method expression tree " + tokenType + " for token " + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
                } else if (child1 == null) {
                    expr = new MethodExpression(type, token, new ArrayList<Expression>());
                } else {
                    List<Expression> args = createExpressionList(bindings, child1);
                    if (args != null) {
                        // need to separate out builtins, instance methdo calls and
                        // static method calls
                        expr = new MethodExpression(type, token, args);
                    } else {
                        System.err.println("ExpressionHelper.createExpression : method argument @ " + token.getLine() + "." + token.getCharPositionInLine());
                    }
                }
            }
            break;
            case NUMBER:
            {
                expr = new NumericLiteral(token);
            }
            break;
            case STRING:
            {
                expr = new StringLiteral(token);
            }
            break;
            case UNOP:
            {
                expr = createUnaryExpression(bindings, exprTree, type);
            }
            break;
            case BINOP:
            {
                expr = createBinaryExpression(bindings, exprTree, type);
            }
            break;
            case TERNOP:
            {
                expr = createTernaryExpression(bindings, exprTree, type);
            }
            break;
            default:
            {
                System.err.println("ExpressionHelper.createExpression : unexpected token Type in expression tree " + tokenType + " for token " + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
            }
            break;
        }

        if (expr != null) {
            Type exprType = Type.dereference(expr.getType());
            Type targetType = Type.dereference(type);
            if (exprType.isDefined() && targetType.isDefined() && !targetType.isAssignableFrom(exprType)) {
                // we already know this is an invalid type so notify an error and return null
                System.err.println("ExpressionHelper.createExpression : invalid expression type " + exprType.getName() + " expecting " + targetType.getName() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
                return null;
            } else if (targetType == Type.NUMBER && !exprType.isNumeric()) {
                // we already know this is an invalid type so notify ane rror and return null
                System.err.println("ExpressionHelper.createExpression : invalid expression type " + exprType.getName() + " expecting " + targetType.getName() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
                return null;
            }
            if (expr.bind(bindings)) {
                return expr;
            } else {
                System.err.println("ExpressionHelper.createExpression : unknown reference in expression @ " + token.getLine() + "." + token.getCharPositionInLine());
                return null;
            }
        } else {
            return null;
        }
    }

    public static Expression createUnaryExpression(Bindings bindings, CommonTree exprTree, Type type)
    {
        // we expect ^(UNOP unary_oper expr)

        CommonTree child0 = (CommonTree) exprTree.getChild(0);
        CommonTree child1 = (CommonTree) exprTree.getChild(1);
        Expression expr;
        Token token = child0.getToken();

        switch (token.getType())
        {
            case TWIDDLE:
            {
                // the argument must be a numeric expression
                Expression operand = createExpression(bindings, child1, Type.NUMBER);
                if (operand != null) {
                    expr = new TwiddleExpression(token, operand);
                } else {
                    expr = null;
                }
            }
            break;
            case NOT:
            {
                // the argument must be a boolean expression
                Expression operand = createExpression(bindings, child1, Type.BOOLEAN);
                if (operand != null) {
                    expr = new NotExpression(token, operand);
                } else {
                    expr = null;
                }
            }
            break;
            default:
            {
                System.err.println("ExpressionHelper.createUnaryExpression : unexpected token Type in expression tree " + token.getType() + " for token " + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
                expr = null;
            }
            break;
        }

        return expr;
    }

    public static Expression createBinaryExpression(Bindings bindings, CommonTree exprTree, Type type)
    {
        // we expect ^(BINOP infix_oper simple_expr expr)

        CommonTree child0 = (CommonTree) exprTree.getChild(0);
        CommonTree child1 = (CommonTree) exprTree.getChild(1);
        CommonTree child2 = (CommonTree) exprTree.getChild(2);
        Expression expr;
        Token token = child0.getToken();

        switch (token.getType())
        {
            case PLUS:
            {
                // this is a special case since we may be doing String concatenation
                Expression operand1;
                Expression operand2;
                if (type == Type.STRING) {
                    // must be doing String concatenation
                    operand1 = createExpression(bindings, child1, Type.STRING);
                    operand2 = createExpression(bindings, child1, Type.STRING);
                    if (operand1 != null && operand2 != null) {
                        expr = new StringPlusExpression(token, operand1,  operand2);
                    } else {
                        expr = null;
                    }
                } else if (type.isNumeric()) {
                    // must be doing arithmetic
                    operand1 = createExpression(bindings, child1, Type.NUMBER);
                    operand2 = createExpression(bindings, child1, Type.NUMBER);
                    if (operand1 != null && operand2 != null) {
                        expr = new ArithmeticExpression(PLUS, token, operand1,  operand2);
                    } else {
                        expr = null;
                    }
                } else {
                    // see if the operand gives us any type info
                    operand1 = createExpression(bindings, child1, Type.UNDEFINED);
                    if (operand1 != null) {
                        if (operand1.getType().isNumeric()) {
                            operand2 = createExpression(bindings, child1, Type.NUMBER);
                            if (operand2 != null) {
                                expr = new ArithmeticExpression(PLUS, token, operand1, operand2);
                            } else {
                                expr = null;
                            }
                        } else if (operand1.getType() == Type.STRING) {
                            operand2 = createExpression(bindings, child1, Type.STRING);
                            if (operand2 != null) {
                                expr = new StringPlusExpression(token, operand1,  operand2);
                            } else {
                                expr = null;
                            }
                        } else {
                            operand2 = createExpression(bindings, child1, Type.UNDEFINED);
                            if (operand2 != null) {
                                // create as generic plus expression which we will replace later during type
                                // checking
                                expr = new PlusExpression(token, operand1,  operand2);
                            } else {
                                expr = null;
                            }
                        }
                    } else {
                        // generate more errors if we can even though we are giving up
                        operand2 = createExpression(bindings, child1, Type.UNDEFINED);
                        expr = null;
                    }
                }
            }
            break;
            case MINUS:
            case MUL:
            case DIV:
            case MOD:
            {
                Expression operand1 = createExpression(bindings, child1, Type.NUMBER);
                Expression operand2 = createExpression(bindings, child2, Type.NUMBER);

                if (operand1 != null & operand2 != null) {
                    return new ArithmeticExpression(token.getType(), token, operand1, operand2);
                } else {
                    expr = null;
                }
            }
            break;
            case BAND:
            case BOR:
            case BXOR:
            {
                Expression operand1 = createExpression(bindings, child1, Type.NUMBER);
                Expression operand2 = createExpression(bindings, child2, Type.NUMBER);

                if (operand1 != null & operand2 != null) {
                    return new BitExpression(token.getType(), token, operand1, operand2);
                } else {
                    expr = null;
                }
            }
            break;
            case AND:
            case OR:
            {
                Expression operand1 = createExpression(bindings, child1, Type.BOOLEAN);
                Expression operand2 = createExpression(bindings, child2, Type.BOOLEAN);

                if (operand1 != null & operand2 != null) {
                    expr = new LogicalExpression(token.getType(), token, operand1, operand2);
                } else {
                    expr = null;
                }
            }
            break;
            case EQ:
            case NEQ:
            case GT:
            case LT:
            case GEQ:
            case LEQ:
            {
                Expression operand1 = createExpression(bindings, child1, Type.NUMBER);
                Expression operand2 = createExpression(bindings, child2, Type.NUMBER);

                if (operand1 != null & operand2 != null) {
                    expr = new ComparisonExpression(token.getType(), token, operand1, operand2);
                } else {
                    expr = null;
                }
            }
            default:
            {
                System.err.println("ExpressionHelper.createBinaryExpression : unexpected token Type in expression tree " + token.getType() + " for token " + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
                expr = null;
            }
            break;
        }

        return expr;
    }

    public static Expression createTernaryExpression(Bindings bindings, CommonTree exprTree, Type type)
    {
        // we expect ^(TERNOP ternary_oper simple_expr expr expr)

        CommonTree child0 = (CommonTree) exprTree.getChild(0);
        CommonTree child1 = (CommonTree) exprTree.getChild(1);
        CommonTree child2 = (CommonTree) exprTree.getChild(2);
        CommonTree child3 = (CommonTree) exprTree.getChild(3);
        Expression expr;
        Token token = child0.getToken();

        switch (token.getType())
        {
            case TERN_IF:
            {
                // the argument must be a numeric expression
                Expression operand1 = createExpression(bindings, child1, Type.BOOLEAN);
                Expression operand2 = createExpression(bindings, child1, Type.UNDEFINED);
                Expression operand3 = createExpression(bindings, child1, Type.UNDEFINED);
                if (operand1 != null && operand2 != null && operand3 != null) {
                    Type type2 = Type.dereference(operand2.getType());
                    Type type3 = Type.dereference(operand3.getType());
                    if (type2.isNumeric() || type3.isNumeric()) {
                        expr = new TernaryOperExpression(TERN_IF, Type.promote(type2, type3),  token, operand1,  operand2, operand3);
                    } else if (type2.isDefined() && type3.isDefined()) {
                        // since they are not numeric we have to have the same type
                        if (type2 == type3) {
                            // use this type
                            expr = new TernaryOperExpression(TERN_IF, type2,  token, operand1,  operand2, operand3);
                        } else {
                            // mismatched types so don't generate a result
                            System.err.println("ExpressionHelper.createTernaryExpression : mismatched types " + type2.getName() + " and " + type3.getName()  + " in conditional expression " + token.getType() + " for token " + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
                            expr = null;
                        }
                    } else {
                        // have to wait for type check to resolve types
                        expr = new TernaryOperExpression(TERN_IF, Type.UNDEFINED,  token, operand1,  operand2, operand3);
                    }
                } else {
                    expr = null;
                }
            }
            break;
            default:
            {
                System.err.println("ExpressionHelper.createTernaryExpression : unexpected token Type in expression tree " + token.getType() + " for token " + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
                expr = null;
            }
            break;
        }

        return expr;
    }

    public static List<Expression> createExpressionList(Bindings bindings, CommonTree exprTree)
    {
        return createExpressionList(bindings, exprTree, Type.UNDEFINED);

    }
    public static List<Expression> createExpressionList(Bindings bindings, CommonTree exprTree, Type type)
    {
        // we expect expr_list = ^(EXPR) |
        //                       ^(SEPR expr expr_list)

        List<Expression> exprList = new ArrayList<Expression>();
        boolean success = true;

        while (exprTree != null)
        {
            switch (exprTree.getToken().getType())
            {
                case SEPR:
                {
                    CommonTree child0 = (CommonTree) exprTree.getChild(0);
                    CommonTree child1 = (CommonTree) exprTree.getChild(1);
                    Expression expr = createExpression(bindings, child0, type);
                    if (expr != null) {
                        exprList.add(expr);
                    } else {
                        success &= false;
                    }
                    exprTree = child1;
                }
                break;
                default:
                {
                    Expression expr = createExpression(bindings, exprTree, type);
                    if (expr != null) {
                        exprList.add(expr);
                    } else {
                        success &= false;
                    }
                    exprTree = null;
                }
                break;
            }
        }
        if (success) {
            return exprList;
        } else {
            return null;
        }
    }
}
