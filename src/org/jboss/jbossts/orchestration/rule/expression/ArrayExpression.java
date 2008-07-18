package org.jboss.jbossts.orchestration.rule.expression;

import org.jboss.jbossts.orchestration.rule.type.Type;
import org.jboss.jbossts.orchestration.rule.binding.Bindings;
import org.antlr.runtime.Token;
import org.antlr.runtime.tree.CommonTree;

import java.util.List;
import java.util.Iterator;

/**
 * an expression which identifies an array reference.
 */

public class ArrayExpression extends Expression
{

    public ArrayExpression(Type type, Token token, List<Expression> idxList)
    {
        super(type, token);
        this.name = token.getText();
        this.idxList = idxList;
    }

    /**
     * verify that variables mentioned in this expression are actually available in the supplied
     * bindings list and infer/validate the type of this expression or its subexpressions
     * where possible
     *
     * @param bindings the set of bindings in place at the point of evaluation of this expression
     * @return true if all variables in this expression are bound and no type mismatches have
     *         been detected during inference/validation.
     */
    public boolean bind(Bindings bindings) {
        // we  have to make sure that the array name is bound and taht the index expressions
        // contain valid bindings
        boolean valid = true;

        if (bindings.lookup(name) == null) {
            System.err.println("ArrayExpression.bind : unbound symbol " + name + getPos());
            valid = false;
        }

        Iterator<Expression> iterator = idxList.iterator();

        while (iterator.hasNext()) {
            valid &= iterator.next().bind(bindings);
        }

        return valid;
    }

    String name;
    List<Expression> idxList;
}
