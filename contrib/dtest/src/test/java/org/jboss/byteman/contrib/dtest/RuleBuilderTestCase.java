package org.jboss.byteman.contrib.dtest;

import org.junit.Test;
import junit.framework.Assert;

public class RuleBuilderTestCase {

    @Test
    public void basic() {
        String testRule =
            "RULE test rule\n" +
            "CLASS javax.transaction.xa.XAResource\n" +
            "METHOD commit\n" +
            "AT ENTRY\n" +
            "IF NOT flagged(\"commitFlag\")\n" +
            "DO throw new javax.transaction.xa.XAResource(100)\n" +
            "ENDRULE\n";
        RuleBuilder builder = new RuleBuilder("test rule")
            .onClass("javax.transaction.xa.XAResource")
            .inMethod("commit")
            .atEntry()
            .when("NOT flagged(\"commitFlag\")")
            .doAction("throw new javax.transaction.xa.XAResource(100)");

        Assert.assertEquals("Defined rule does not match the one built by builder\n" + testRule + "\nvs.\n" + builder.toString(),
                testRule, builder.toString());
    }
    
}
