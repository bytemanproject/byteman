/*
 * JBoss, Home of Professional Open Source
 * Copyright 2019, Red Hat and individual contributors
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

package org.jboss.byteman.tests.location;
import org.jboss.byteman.tests.Test;
import org.jboss.byteman.tests.auxiliary.Child;
import org.jboss.byteman.tests.auxiliary.Parent;

public class TestAtNew extends Test
{
    public TestAtNew()
    {
        super(TestAtNew.class.getCanonicalName());
    }

    public void test()
    {
        try {
            log("creating parent");
            Parent parent = new Parent();
            log("parent.getClass().getSimpleName() == " + parent.getClass().getSimpleName());
            log("creating childArray");
            Child[] childArray = new Child[10];
            log("childArray.getClass().getSimpleName() == " + childArray.getClass().getSimpleName());
            // n.b. this is created via ANEWARRAY even though DIMS is 2
            log("creating parentArray2d");
            Parent[][] parentArray2d = new Parent[2][];
            log("parentArray2d.getClass().getSimpleName() == " + parentArray2d.getClass().getSimpleName());
            // n.b. this one gets created via MULTIANEWARRAY
            log("creating childArray2d");
            Child[][] childArray2d = new Child[2][5];
            log("childArray2d.getClass().getSimpleName() == " + childArray2d.getClass().getSimpleName());
            // n.b. this one gets created via NEWARRAY
            log("creating intArray");
            int[] intArray = new int[10];
            log("intArray.getClass().getSimpleName() == " + intArray.getClass().getSimpleName());
            // n.b. this one gets created via MULTIANEWARRAY
            log("creating intArray2d");
            int[][] intArray2d = new int[2][5];
            log("intArray2d.getClass().getSimpleName() == " + intArray2d.getClass().getSimpleName());
        } catch (Exception e) {
            log(e);
        }

        checkOutput();
    }

    @Override
    public String getExpected() {
        logExpected("creating parent");
        logExpected("TestAtNew.test: triggered AT NEW NEWCLASS == " + Parent.class.getCanonicalName());
        logExpected("TestAtNew.test: triggered AT NEW Parent NEWCLASS == " + Parent.class.getCanonicalName());
        logExpected("TestAtNew.test: triggered AFTER NEW Parent NEWCLASS == " + Parent.class.getCanonicalName());
        logExpected("TestAtNew.test: $!.getClass().getSimpleName() == Parent");
        logExpected("TestAtNew.test: reassigning $! to a Child");
        logExpected("TestAtNew.test: triggered AFTER NEW NEWCLASS == " + Parent.class.getCanonicalName());
        logExpected("TestAtNew.test: $!.getClass().getSimpleName() == Child");
        logExpected("parent.getClass().getSimpleName() == Child");
        logExpected("creating childArray");
        logExpected("TestAtNew.test: triggered AT NEW [] NEWCLASS == " + Child[].class.getCanonicalName());
        logExpected("TestAtNew.test: triggered AT NEW Child [] NEWCLASS == " + Child[].class.getCanonicalName());
        logExpected("TestAtNew.test: triggered AFTER NEW Child [] NEWCLASS == " + Child[].class.getCanonicalName());
        logExpected("TestAtNew.test: $!.getClass().getSimpleName() == Child[]");
        logExpected("TestAtNew.test: $!.length == 10");
        logExpected("TestAtNew.test: triggered AFTER NEW [] NEWCLASS == " + Child[].class.getCanonicalName());
        logExpected("TestAtNew.test: $!.getClass().getSimpleName() == Child[]");
        logExpected("TestAtNew.test: $!.length == 10");
        logExpected("childArray.getClass().getSimpleName() == Child[]");
        logExpected("creating parentArray2d");
        logExpected("TestAtNew.test: triggered AT NEW [][] NEWCLASS == " + Parent[][].class.getCanonicalName());
        logExpected("TestAtNew.test: triggered AT NEW Parent [][] NEWCLASS == " + Parent[][].class.getCanonicalName());
        logExpected("TestAtNew.test: triggered AFTER NEW Parent [][] NEWCLASS == " + Parent[][].class.getCanonicalName());
        logExpected("TestAtNew.test: $!.getClass().getSimpleName() == Parent[][]");
        logExpected("TestAtNew.test: $!.length == 2");
        logExpected("TestAtNew.test: $![0] == null");
        logExpected("TestAtNew.test: triggered AFTER NEW [][] NEWCLASS == " + Parent[][].class.getCanonicalName());
        logExpected("TestAtNew.test: $!.getClass().getSimpleName() == Parent[][]");
        logExpected("TestAtNew.test: $!.length == 2");
        logExpected("TestAtNew.test: $![0] == null");
        logExpected("parentArray2d.getClass().getSimpleName() == Parent[][]");
        logExpected("creating childArray2d");
        logExpected("TestAtNew.test: triggered AT NEW [][] 2 NEWCLASS == " + Child[][].class.getCanonicalName());
        logExpected("TestAtNew.test: triggered AT NEW Child [][] NEWCLASS == " + Child[][].class.getCanonicalName());
        logExpected("TestAtNew.test: triggered AFTER NEW Child [][] NEWCLASS == " + Child[][].class.getCanonicalName());
        logExpected("TestAtNew.test: $!.getClass().getSimpleName() == Child[][]");
        logExpected("TestAtNew.test: $!.length == 2");
        logExpected("TestAtNew.test: $![0].length == 5");
        logExpected("TestAtNew.test: triggered AFTER NEW [][] 2 NEWCLASS == " + Child[][].class.getCanonicalName());
        logExpected("TestAtNew.test: $!.getClass().getSimpleName() == Child[][]");
        logExpected("TestAtNew.test: $!.length == 2");
        logExpected("TestAtNew.test: $![0].length == 5");
        logExpected("childArray2d.getClass().getSimpleName() == Child[][]");
        logExpected("creating intArray");
        logExpected("TestAtNew.test: triggered AT NEW [] 2 NEWCLASS == " + int[].class.getCanonicalName());
        logExpected("TestAtNew.test: triggered AT NEW int [] NEWCLASS == " + int[].class.getCanonicalName());
        logExpected("TestAtNew.test: triggered AFTER NEW int [] NEWCLASS == " + int[].class.getCanonicalName());
        logExpected("TestAtNew.test: $!.getClass().getSimpleName() == int[]");
        logExpected("TestAtNew.test: $!.length == 10");
        logExpected("TestAtNew.test: triggered AFTER NEW [] 2 NEWCLASS == " + int[].class.getCanonicalName());
        logExpected("TestAtNew.test: $!.getClass().getSimpleName() == int[]");
        logExpected("TestAtNew.test: $!.length == 10");
        logExpected("intArray.getClass().getSimpleName() == int[]");
        logExpected("creating intArray2d");
        logExpected("TestAtNew.test: triggered AT NEW [][] 3 NEWCLASS == " + int[][].class.getCanonicalName());
        logExpected("TestAtNew.test: triggered AT NEW int [][] NEWCLASS == " + int[][].class.getCanonicalName());
        logExpected("TestAtNew.test: triggered AFTER NEW int [][] NEWCLASS == " + int[][].class.getCanonicalName());
        logExpected("TestAtNew.test: $!.getClass().getSimpleName() == int[][]");
        logExpected("TestAtNew.test: $!.length == 2");
        logExpected("TestAtNew.test: $![0].length == 5");
        logExpected("TestAtNew.test: reassigning $! to new int[5][2]");
        logExpected("TestAtNew.test: triggered AFTER NEW [][] 3 NEWCLASS == " + int[][].class.getCanonicalName());
        logExpected("TestAtNew.test: $!.getClass().getSimpleName() == int[][]");
        logExpected("TestAtNew.test: $!.length == 5");
        logExpected("TestAtNew.test: $![0].length == 2");
        logExpected("intArray2d.getClass().getSimpleName() == int[][]");

        return super.getExpected();
    }

}
