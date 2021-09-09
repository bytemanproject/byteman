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

package org.jboss.byteman.tests.auxiliary;

import java.util.Iterator;
public class StringsLinked implements Strings {
    private class StringCell {
        StringCell next;
        String value;
    }

    private StringCell first;
    private StringCell last;

    public StringsLinked() {
        this.first = this.last = null;
    }

    @Override
    public void add(String s) {
        append(s);
    }

    public void append(String s) {
        StringCell link = new StringCell();
        link.value = s;
        if (first != null) {
            last = last.next = link;
        } else {
            first = last = link;
        }
    }
    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            StringCell cursor = first;
            @Override
            public boolean hasNext() {
                return cursor != null;
            }
            @Override
            public String next() {
                String result = cursor.value;
                cursor = cursor.next;
                return result;
            }
        };
    }
}
