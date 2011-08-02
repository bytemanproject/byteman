/*
* JBoss, Home of Professional Open Source
* Copyright 2009, Red Hat and individual contributors
* by the @authors tag. See the copyright.txt in the distribution for a
* full listing of individual contributors.
*
* This is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as
* published by the Free Software Foundation; either version 2.1 of
* the License, or (at your option) any later version.
*
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this software; if not, write to the Free
* Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
* 02110-1301 USA, or see the FSF site: http://www.fsf.org.
*
* @authors Andrew Dinn
*/
package org.jboss.byteman.tests.auxiliary;

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

    protected static TestFieldMethodAuxiliary getTheAuxiliaryProtected()
    {
        return theAuxiliary;
    }

    private static TestFieldMethodAuxiliary getTheAuxiliaryPrivate()
    {
        return theAuxiliary;
    }

    public TestFieldMethodAuxiliary getLeft()
    {
        return left;
    }

    protected TestFieldMethodAuxiliary getLeftProtected()
    {
        return left;
    }

    private TestFieldMethodAuxiliary getLeftPrivate()
    {
        return left;
    }

    public int getValue()
    {
        return value;
    }

    protected int getValueProtected()
    {
        return value;
    }

    private int getValuePrivate()
    {
        return value;
    }

    public TestFieldMethodAuxiliary getRight()
    {
        return right;
    }

    protected TestFieldMethodAuxiliary getRightProtected()
    {
        return right;
    }

    private TestFieldMethodAuxiliary getRightPrivate()
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
