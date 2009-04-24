package org.jboss.jbossts.orchestration.tests.auxiliary;

/**
 * Auxiliary class used to implement TestField test class
 */
public class TestFieldMethodAuxiliary
{
    public static TestFieldMethodAuxiliary theAuxiliary;
    
    public int value;
    public TestFieldMethodAuxiliary left;
    public TestFieldMethodAuxiliary right;

    public TestFieldMethodAuxiliary(int value)
    {
        this(null, value, null);
    }

    public TestFieldMethodAuxiliary(TestFieldMethodAuxiliary left, int value)
    {
        this(left, value, null);
    }

    public TestFieldMethodAuxiliary(int value, TestFieldMethodAuxiliary right)
    {
        this(null, value, right);
    }

    public TestFieldMethodAuxiliary(TestFieldMethodAuxiliary left, int value, TestFieldMethodAuxiliary right)
    {
        this.left = left;
        this.value = value;
        this.right = right;
    }

    public static TestFieldMethodAuxiliary getTheAuxiliary()
    {
        return theAuxiliary;
    }

    public TestFieldMethodAuxiliary getLeft()
    {
        return left;
    }

    public int getValue()
    {
        return value;
    }
    
    public TestFieldMethodAuxiliary getRight()
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
