package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.type.TypeGroup;
import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.jboss.jbossts.orchestration.rule.exception.TypeException;
import org.jboss.jbossts.orchestration.rule.exception.ExecuteException;
import org.jboss.jbossts.orchestration.rule.Rule;
import org.antlr.runtime.Token;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * an expression which identifies a method invocation
 */
public class MethodExpression extends Expression
{
    public MethodExpression(Rule rule, Type type, Token token, String name, Expression recipient, List<Expression> arguments) {
        super(rule, type, token);
        this.name = name;
        this.recipient = recipient;
        this.arguments = arguments;
        this.argumentTypes = null;
    }

    /**
     * verify that variables mentioned in this expression are actually available in the supplied
     * bindings list and infer/validate the type of this expression or its subexpressions
     * where possible
     *
     * @return true if all variables in this expression are bound and no type mismatches have
     *         been detected during inference/validation.
     */
    public boolean bind() {
        // check that the recipient and argument expressions have valid bindings

        boolean valid = true;
        if (recipient != null) {
            valid &= recipient.bind();
        }

        Iterator<Expression> iterator = arguments.iterator();

        while (valid && iterator.hasNext()) {
            valid &= iterator.next().bind();
        }

        return valid;
    }

    public Type typeCheck(Type expected) throws TypeException {
        // if we have no recipient then we use the rule's helper as a target via a binding
        // to $-1. this means  we can type check the call against methods of class Rule$Helper
        // without having to do any special case processing.

        TypeGroup typeGroup =  getTypeGroup();
        if (recipient == null) {
            Type ruleType = typeGroup.create("org.jboss.jbossts.orchestration.rule.Rule$Helper");
            recipient = new DollarExpression(rule, ruleType, token, "$-1");
        }
        // check the recipient type is defined and then look for a relevant method

        Type recipientType = recipient.typeCheck(Type.UNDEFINED);
        Class clazz = recipientType.getTargetClass();
        // if we can find a unique method then we can use it to type the parameters
        // otherwise we do it the hard way
        int arity = arguments.size();
        Method[] methods = clazz.getMethods();
        List<Method> candidates = new ArrayList<Method>();
        boolean duplicates = false;

        for (Method method : methods) {
            if (method.getName().equals(name) &&
                    method.getParameterTypes().length == arity) {
                candidates.add(method);
            }
        }

        argumentTypes = new ArrayList<Type>();
        
        // check each argument in turn -- if all candidates have the same argument type then
        // use that as the type to check against
        for (int i = 0; i < arguments.size() ; i++) {
            if (candidates.isEmpty()) {
                throw new TypeException("MethodExpression.typeCheck : invalid method " + name + " for target class " + recipientType.getName() + getPos());
            }

            // TODO get and prune operations do not allow for coercion but type check does!
            // e.g. the parameter type may be int and the arg type float
            // or the parameter type may be String and the arg type class Foo
            // reimplement this using type inter-assignability to do the pruning

            Class candidateClass = getCandidateArgClass(candidates, i);
            Type candidateType;
            if (candidateClass != null) {
                candidateType = typeGroup.ensureType(candidateClass);
            } else {
                candidateType = Type.UNDEFINED;
            }
            Type argType = arguments.get(i).typeCheck(candidateType);
            argumentTypes.add(argType);
            if (candidateType == Type.UNDEFINED) {
                // we had several methods to choose from
                candidates = pruneCandidates(candidates, i, argType.getTargetClass());
            }
        }

        if (candidates.isEmpty()) {
            throw new TypeException("MethodExpression.typeCheck : invalid method " + name + " for target class " + recipientType.getName() + getPos());
        }
        
        if (candidates.size() > 1) {
            throw new TypeException("MethodExpression.typeCheck : ambiguous method signature " + name + " for target class " + recipientType.getName() + getPos());
        }

        method = candidates.get(0);

        type = typeGroup.ensureType(method.getReturnType());

        if (Type.dereference(expected).isDefined() && !expected.isAssignableFrom(type)) {
            throw new TypeException("MethodExpression.typeCheck : invalid expected type " + expected.getName() + getPos());
        }

        return type;
    }

    public Object interpret(Rule.BasicHelper helper) throws ExecuteException {
        Object recipientValue = null;
        try {
            if (recipient != null) {
                recipientValue = recipient.interpret(helper);
                if (recipientValue == null) {
                    throw new ExecuteException("MethodExpression.interpret : null recipient for method " + token.getText() + getPos());
                }
            }
            int argCount = arguments.size();

            Object[] argValues = new Object[argCount];
            for (int i = 0; i < argCount; i++) {
                argValues[i] = arguments.get(i).interpret(helper);
            }

            return method.invoke(recipientValue, argValues);
        } catch (ExecuteException e) {
            throw e;
        } catch (Exception e) {
            throw new ExecuteException("MethodExpression.interpret : exception invoking method " + token.getText() + getPos(), e);
        }
    }

    public Class getCandidateArgClass(List<Method> candidates, int argIdx)
    {
        Class argClazz = null;

        for (Method m : candidates) {
            Class nextClazz = m.getParameterTypes()[argIdx];
            if (argClazz == null) {
                argClazz = nextClazz;
            } else if (argClazz != nextClazz) {
                return null;
            }
        }

        return argClazz;
    }

    public List<Method> pruneCandidates(List<Method> candidates, int argIdx, Class argClazz)
    {
        for (int i = 0; i < candidates.size();) {
            Method m = candidates.get(i);
            Class nextClazz = m.getParameterTypes()[argIdx];
            if (nextClazz != argClazz) {
                candidates.remove(i);
            } else {
                i++;
            }
        }
        return candidates;
    }

    public void writeTo(StringWriter stringWriter) {
        if (recipient != null) {
            recipient.writeTo(stringWriter);
            stringWriter.write(".");
        }
        stringWriter.write(name);
        stringWriter.write("(");
        String sepr = "";
        for (Expression arg : arguments) {
            stringWriter.write(sepr);
            arg.writeTo(stringWriter);
            sepr = ", ";
        }
        stringWriter.write(")");
    }

    private String name;
    private List<Expression> arguments;
    private List<Type> argumentTypes;
    private Expression recipient;
    private Method method;
}
