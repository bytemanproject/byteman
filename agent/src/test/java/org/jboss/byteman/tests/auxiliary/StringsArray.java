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
public class StringsArray implements Strings {

    private String[] strings;
    private int size;
    private int count;
    private final static int INITIAL_SIZE = 5;

    private void ensureCapacity() {
        if (count == size) {
            size = (int)(size * 1.4);
            String[] newStrings = new String[size];
            for (int i = 0; i < count; i++) {
                newStrings[i] = strings[i];
            }
            strings = newStrings;
        }
    }

    public StringsArray() {
        this.strings = new String[INITIAL_SIZE];
        this.size = INITIAL_SIZE;
        this.count = 0;
    }

    @Override
    public void add(String s) {
        ensureCapacity();
        strings[count++] = s;
    }

    @Override
    public Iterator<String> iterator() {
        return new Iterator<String>() {
            int cursor = 0;
            @Override
            public boolean hasNext() {
                return cursor < count;
            }
            @Override
            public String next() {
                return strings[cursor++];
            }
        };
    }
}
