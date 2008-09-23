package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.binding.Binding;
import static org.jboss.jbossts.orchestration.rule.grammar.ECAGrammarParser.*;
import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
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
            throws TypeException
    {
        return createExpression(bindings, exprTree, Type.UNDEFINED);
    }

    public static Expression createExpression(Bindings bindings, CommonTree exprTree, Type type)
            throws TypeException
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
        Expression expr;
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
                int length = text.length();
                // strip off any surrounding single quotes
                if (length > 1 && text.charAt(0) == '\'' && text.charAt(length - 1) == '\'') {
                    text = text.substring(1, length - 1);
                }
                int dotIdx = text.lastIndexOf('.');
                if (dotIdx < 0) {
                    // direct variable reference
                    expr = new Variable(type, token);
                } else {
                    // field reference either to an instance named by a binding or or a static

                    String[] parts = text.split("\\.");
                    if (parts.length < 2) {
                        // oops malformed symbol either "." ro "foo." or ".foo"
                        // should not  happen but ...
                        throw new TypeException("ExpressionHelper.createExpression : unexpected symbol "  + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
                    } else {
                        String prefix = parts[0];

                        if (bindings.lookup(prefix) != null) {
                            // intitial segment of text identifies a bound variable so treat as
                            // instance field access
                            int l = parts.length - 1;
                            String[] fields = new String[l];
                            System.arraycopy(parts, 1, fields, 0, l);
                            expr = new FieldExpression(type, token, prefix, fields);
                        } else {
                            expr = new StaticExpression(type, token, parts);
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
                    throw new TypeException("ExpressionHelper.createExpression : unexpected token Type in array expression tree " + tokenType + " for token " + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
                }
                Expression arrayRef;
                // check for embedded dots

                String text = token.getText();
                int dotIdx = text.lastIndexOf('.');
                if (dotIdx < 0) {
                    // array name is direct variable reference
                    arrayRef = new Variable(type, token);
                } else {
                    // field reference either to an instance named by a binding or or a static

                    String[] parts = text.split("\\.");
                    if (parts.length < 2) {
                        // oops malformed symbol either "." ro "foo." or ".foo"
                        // should not  happen but ...
                        throw new TypeException("ExpressionHelper.createExpression : unexpected symbol "  + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
                    } else {
                        String prefix = parts[0];

                        if (bindings.lookup(prefix) != null) {
                            // intitial segment of text identifies a bound variable so treat as
                            // instance field access
                            int l = parts.length - 1;
                            String[] fields = new String[l];
                            System.arraycopy(parts, 1, fields, 0, l);
                            arrayRef = new FieldExpression(type, token, prefix, fields);
                        } else {
                            arrayRef = new StaticExpression(type, token, parts);
                        }
                    }
                }
                List<Expression> indices = createExpressionList(bindings, child1, Type.INTEGER);
                if (indices != null) {
                    expr = new ArrayExpression(type, token, arrayRef, indices);
                } else {
                    throw new TypeException("ExpressionHelper.createExpression : invalid array index expression @ " + token.getLine() + "." + token.getCharPositionInLine());
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
                    throw new TypeException("ExpressionHelper.createExpression : unexpected token Type in method expression tree " + tokenType + " for token " + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
                } else {
                    expr = createCallExpression(bindings, token, child1, type);
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
                throw new TypeException("ExpressionHelper.createExpression : unexpected token Type in expression tree " + tokenType + " for token " + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
            }
        }

        Type exprType = Type.dereference(expr.getType());
        Type targetType = Type.dereference(type);
        if (exprType.isDefined() && targetType.isDefined() && !targetType.isAssignableFrom(exprType)) {
            // we already know this is an invalid type so notify an error and return null
            throw new TypeException("ExpressionHelper.createExpression : invalid expression type " + exprType.getName() + " expecting " + targetType.getName() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
        } else if (targetType.isNumeric() && !exprType.isNumeric()) {
            // we already know this is an invalid type so notify an error and return null
            throw new TypeException("ExpressionHelper.createExpression : invalid expression type " + exprType.getName() + " expecting " + targetType.getName() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
        }
        if (!expr.bind(bindings)) {
            throw new TypeException("ExpressionHelper.createExpression : unknown reference in expression @ " + token.getLine() + "." + token.getCharPositionInLine());
        }

        return expr;
    }

     public static Expression createCallExpression(Bindings bindings, Token token, CommonTree argTree, Type type)
             throws TypeException
     {
         Expression expr;
         String callName;
         Expression recipient;
         List<Expression> args;

         // we need to factor off the path from the method/builtin name

         String text = token.getText();
         int dotIdx = text.lastIndexOf('.');
         if (dotIdx < 0) {
             // a builtin call

             recipient = null;
             callName = text;
         } else {
             // prefix must be either a bound varibale, a field reference via a bound
             // variable, or a static field reference

             String[] path = text.split("\\.");
             if (path.length < 2) {
                 // oops malformed symbol either "." ro "foo." or ".foo"
                 // should not happen but ...
                 throw new TypeException("ExpressionHelper.createCallExpression : unexpected symbol "  + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
             } else {
                 // save the method name and only consider path preceding it
                 int pathLength = path.length - 1;
                 callName = path[pathLength];

                 // see if the path starts with a bound variable
                 String prefix = path[0];
                 Binding binding = bindings.lookup(prefix);

                 if (binding != null) {
                     // intitial segment of text identifies a bound variable so treat as
                     // a variable access or an instance field access depending upon how
                     // many extra path elenments there are
                     if (pathLength == 1) {
                         // method call on bound variable
                         recipient = new Variable(binding.getType(), token, binding.getName());
                     } else {
                         // method call on field of bound variable
                         String[] fields = new String[pathLength - 1];
                         System.arraycopy(path, 1, fields, 0, pathLength - 1);
                         recipient = new FieldExpression(Type.UNDEFINED, token, prefix, fields);
                     }
                 } else {
                     // ok, we need a version of the path without the method name on the end
                     String[] realPath = new String[pathLength];
                     System.arraycopy(path, 0, realPath, 0, pathLength);
                     recipient = new StaticExpression(Type.UNDEFINED, token, realPath);
                 }
             }
         }

         if (argTree == null) {
             args = new ArrayList<Expression>();
         } else {
             args = createExpressionList(bindings, argTree);
         }

         expr = new MethodExpression(type, token, callName, recipient, args);

         return expr;
     }

    public static Expression createUnaryExpression(Bindings bindings, CommonTree exprTree, Type type)
            throws TypeException
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
                if (!type.isUndefined() && !type.isVoid() && !type.isNumeric()) {
                    throw new TypeException("ExpressionHelper.createUnaryExpression : invalid numeric expression @ " + token.getLine() + "." + token.getCharPositionInLine());
                }
                Expression operand = createExpression(bindings, child1, Type.NUMBER);
                expr = new TwiddleExpression(token, operand);
            }
            break;
            case NOT:
            {
                // the argument must be a boolean expression
                if (!type.isUndefined() && !type.isVoid() && !type.isBoolean()) {
                    throw new TypeException("ExpressionHelper.createUnaryExpression : invalid boolean expression @ " + token.getLine() + "." + token.getCharPositionInLine());
                }
                Expression operand = createExpression(bindings, child1, Type.BOOLEAN);
                expr = new NotExpression( token, operand);
            }
            break;
            default:
            {
                throw new TypeException("ExpressionHelper.createUnaryExpression : unexpected token Type in expression tree " + token.getType() + " for token " + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
            }
        }

        return expr;
    }

    public static Expression createBinaryExpression(Bindings bindings, CommonTree exprTree, Type type)
            throws TypeException
    {
        // we expect ^(BINOP infix_oper simple_expr expr)

        CommonTree child0 = (CommonTree) exprTree.getChild(0);
        CommonTree child1 = (CommonTree) exprTree.getChild(1);
        CommonTree child2 = (CommonTree) exprTree.getChild(2);
        Expression expr;
        Token token = child0.getToken();
        int oper = token.getType();

        switch (oper)
        {
            case PLUS:
            {
                // this is a special case since we may be doing String concatenation
                Expression operand1;
                Expression operand2;
                if (type == Type.STRING) {
                    // must be doing String concatenation
                    operand1 = createExpression(bindings, child1, Type.STRING);
                    operand2 = createExpression(bindings, child2, Type.UNDEFINED);
                    expr = new StringPlusExpression(token, operand1,  operand2);
                } else if (type.isNumeric()) {
                    // must be doing arithmetic
                    operand1 = createExpression(bindings, child1, Type.NUMBER);
                    operand2 = createExpression(bindings, child2, Type.NUMBER);
                    int convertedOper = OperExpression.convertOper(oper);
                    expr = new ArithmeticExpression(convertedOper, token, operand1,  operand2);
                } else {
                    // see if the operand gives us any type info
                    operand1 = createExpression(bindings, child1, Type.UNDEFINED);
                    if (operand1.getType().isNumeric()) {
                        operand2 = createExpression(bindings, child2, Type.NUMBER);
                        int convertedOper = OperExpression.convertOper(oper);
                        expr = new ArithmeticExpression(convertedOper, token, operand1, operand2);
                    } else if (operand1.getType() == Type.STRING) {
                        operand2 = createExpression(bindings, child2, Type.UNDEFINED);
                        expr = new StringPlusExpression(token, operand1,  operand2);
                    } else {
                        operand2 = createExpression(bindings, child2, Type.UNDEFINED);
                        // create as generic plus expression which we will replace later during type
                        // checking
                        expr = new PlusExpression(token, operand1,  operand2);
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

                int convertedOper = OperExpression.convertOper(oper);
                expr = new ArithmeticExpression(convertedOper, token, operand1, operand2);
            }
            break;
            case BAND:
            case BOR:
            case BXOR:
            {
                Expression operand1 = createExpression(bindings, child1, Type.NUMBER);
                Expression operand2 = createExpression(bindings, child2, Type.NUMBER);

                int convertedOper = OperExpression.convertOper(oper);
                expr = new BitExpression(convertedOper, token, operand1, operand2);
            }
            break;
            case AND:
            case OR:
            {
                Expression operand1 = createExpression(bindings, child1, Type.BOOLEAN);
                Expression operand2 = createExpression(bindings, child2, Type.BOOLEAN);

                int convertedOper = OperExpression.convertOper(oper);
                expr = new LogicalExpression(convertedOper, token, operand1, operand2);
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

                int convertedOper = OperExpression.convertOper(oper);
                expr = new ComparisonExpression(convertedOper, token, operand1, operand2);
            }
            break;
            default:
            {
                throw new TypeException("ExpressionHelper.createBinaryExpression : unexpected token Type in expression tree " + token.getType() + " for token " + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
            }
        }

        return expr;
    }

    public static Expression createTernaryExpression(Bindings bindings, CommonTree exprTree, Type type)
            throws TypeException
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
                Expression operand2 = createExpression(bindings, child2, type);
                Expression operand3 = createExpression(bindings, child3, type);
                Type type2 = Type.dereference(operand2.getType());
                Type type3 = Type.dereference(operand3.getType());
                if (type2.isNumeric() || type3.isNumeric()) {
                    if (!type.isUndefined() && !type.isVoid() && !type.isNumeric()) {
                        throw new TypeException("ExpressionHelper.createUnaryExpression : invalid numeric expression @ " + token.getLine() + "." + token.getCharPositionInLine());
                    }
                    expr = new ConditionalEvalExpression(Type.promote(type2, type3),  token, operand1,  operand2, operand3);
                } else if (type2.isDefined() && type3.isDefined()) {
                    // since they are not numeric we have to have the same type
                    if (type2 == type3) {
                        // use this type
                        expr = new ConditionalEvalExpression(type2,  token, operand1,  operand2, operand3);
                    } else {
                        // mismatched types so don't generate a result
                        throw new TypeException("ExpressionHelper.createTernaryExpression : mismatched types " + type2.getName() + " and " + type3.getName()  + " in conditional expression " + token.getType() + " for token " + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
                    }
                } else {
                    // have to wait for type check to resolve types
                    expr = new ConditionalEvalExpression(Type.UNDEFINED,  token, operand1,  operand2, operand3);
                }
            }
            break;
            default:
            {
                throw new TypeException("ExpressionHelper.createTernaryExpression : unexpected token Type in expression tree " + token.getType() + " for token " + token.getText() + " @ " + token.getLine() + "." + token.getCharPositionInLine());
            }
        }

        return expr;
    }

    public static List<Expression> createExpressionList(Bindings bindings, CommonTree exprTree)
            throws TypeException
    {
        return createExpressionList(bindings, exprTree, Type.UNDEFINED);

    }
    public static List<Expression> createExpressionList(Bindings bindings, CommonTree exprTree, Type type)
            throws TypeException
    {
        // we expect expr_list = ^(EXPR) |
        //                       ^(SEPR expr expr_list)

        List<Expression> exprList = new ArrayList<Expression>();
        List<TypeException> exceptions = new ArrayList<TypeException>();

        while (exprTree != null)
        {
            try {
                switch (exprTree.getToken().getType())
                {
                    case SEPR:
                    {
                        CommonTree child0 = (CommonTree) exprTree.getChild(0);
                        // assign tree before we risk an exception
                        exprTree = (CommonTree) exprTree.getChild(1);
                        Expression expr = createExpression(bindings, child0, type);
                        exprList.add(expr);
                    }
                    break;
                    default:
                    {
                        // assign tree before we risk an exception
                        CommonTree saveTree = exprTree;
                        exprTree = null;
                        Expression expr = createExpression(bindings, saveTree, type);
                        exprList.add(expr);
                    }
                    break;
                }
            } catch (TypeException te) {
                exceptions.add(te);
            }
        }

        if (!exceptions.isEmpty()) {
            if (exceptions.size() == 1) {
                throw exceptions.get(0);
            } else {
                StringBuffer buffer = new StringBuffer();
                buffer.append("ExpressionHelper.createExpressionList : errors checking expression sequence");
                for (TypeException typeException : exceptions) {
                    buffer.append("\n");
                    buffer.append(typeException.toString());
                }
                throw new TypeException(buffer.toString());
            }
        }

        return exprList;
    }
}
