package org.jboss.jbossts.orchestration.tests.auxiliary;

/**
 * Auxiliary class used to implement TestField test class
 */
public class TestFieldAuxiliary
{
    public static TestFieldAuxiliary theAuxiliary;
    
    public int value;
    public TestFieldAuxiliary left;
    public TestFieldAuxiliary right;

    public TestFieldAuxiliary(int value)
    {
        this(null, value, null);
    }

    public TestFieldAuxiliary(TestFieldAuxiliary left, int value)
    {
        this(left, value, null);
    }

    public TestFieldAuxiliary(int value, TestFieldAuxiliary right)
    {
        this(null, value, right);
    }

    public TestFieldAuxiliary(TestFieldAuxiliary left, int value, TestFieldAuxiliary right)
    {
        this.left = left;
        this.value = value;
        this.right = right;
    }

    public TestFieldAuxiliary getLeft()
    {
        return left;
    }

    public int getValue()
    {
        return value;
    }
    
    public TestFieldAuxiliary getRight()
    {
        return right;
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("aux[");
        buf.append(left == null ? "null" : left.value);
        buf.append("<--");
        buf.append(value);
        buf.append("-->");
        buf.append(right == null ? "null" : right.value);
        buf.append("]");
        return buf.toString();
    }
}
