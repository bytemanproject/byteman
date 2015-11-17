package helper;

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.helper.Helper;

public abstract class ModularHelper extends Helper
{
    public ModularHelper(Rule rule)
    {
        super(rule);
    }

    public void logVia(Object logger) {
        try {
            logger.getClass().getMethod("log", String.class).invoke(logger, "ModularHelper.logVia");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
