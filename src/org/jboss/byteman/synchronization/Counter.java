/*
* JBoss, Home of Professional Open Source
* Copyright 2009-10 Red Hat and individual contributors
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
package org.jboss.byteman.synchronization;

/**
 * class used to associate a counter value with a given object
 */
public class Counter
{
    private int count;

    public Counter()
    {
        this(0);
    }
    public Counter(int count)
    {
        this.count = count;
    }

    /**
     * for backwards compatibility
     * @return
     */
    public int count()
    {
        return count(false);
    }

    public synchronized int count(boolean zero)
    {
        int result = count;
        if (zero) {
            count = 0;
        }
        return result;
    }

    public int increment()
    {
        return increment(1);
    }

    public synchronized int increment(int amount)
    {
        count += amount;

        return count;
    }

    public int decrement()
    {
        return increment(-1);
    }
}
