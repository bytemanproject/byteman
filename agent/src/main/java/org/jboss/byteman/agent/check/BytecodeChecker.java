/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010 Red Hat and individual contributors
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
 * (C) 2010,
 * @authors Andrew Dinn
 */

package org.jboss.byteman.agent.check;

import org.jboss.byteman.rule.type.TypeHelper;
import org.objectweb.asm.ClassReader;

/**
 * a private class which can be used to derive the super and interfaces of a class from its defining bytecode
 */
public class BytecodeChecker implements ClassChecker {
    private boolean isInterface;
    private String[] interfaces;
    private String superName;
    private String outerClass;
    private int bytesize;

    public BytecodeChecker(byte[] buffer) {
        // run a pass over the bytecode to identify the interfaces
        ClassReader cr = new ClassReader(buffer);
        ClassStructureAdapter adapter = new ClassStructureAdapter();
        cr.accept(adapter, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
        isInterface = adapter.isInterface();
        interfaces = adapter.getInterfaces();
        superName = adapter.getSuper();
        outerClass = adapter.getOuterClass();
        bytesize = buffer.length;
    }

    public boolean isInterface() {
        return isInterface;
    }

    public String getSuper() {
        if (superName != null) {
            superName = TypeHelper.internalizeClass(superName);
        }
        return superName;
    }

    public boolean hasOuterClass() {
        return outerClass != null;
    }

    public int getInterfaceCount() {
        return interfaces.length;
    }

    public String getInterface(int idx) {
        return TypeHelper.internalizeClass(interfaces[idx]);
    }

    public int getBytesize() {
        return bytesize;
    }
}
