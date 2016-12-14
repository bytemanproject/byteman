/*
 * JBoss, Home of Professional Open Source
 * Copyright 2016, Red Hat and individual contributors
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

package org.jboss.byteman.layer;

import java.io.IOException;
import java.lang.module.ModuleReader;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by adinn on 25/07/16.
 */
public class LayerModuleReader implements ModuleReader
{
    /**
     * the mapper to which this reader delegates under a read request
     * to locate class definitions
     */
    private final Function<String, byte[]> classMapper;

    public LayerModuleReader(Function<String, byte[]> classMapper)
    {
        this.classMapper = classMapper;
    }

    @Override
    public Optional<URI> find(String name) throws IOException
    {
        // this will get called if we ask the module loader to
        // find a resource but we won't do that will we
        return Optional.empty();
    }

    @Override
    public Optional<ByteBuffer> read(String name) throws IOException
    {
        // this gets called under the module loader's loadClass
        // method with name in format x/y/z/MyClass.class
        byte[] classBytes = classMapper.apply(name);
        if (classBytes != null) {
            ByteBuffer byteBuffer = ByteBuffer.wrap(classBytes) ;
            return Optional.of(byteBuffer);
        }
        return Optional.empty();
    }
    @Override
    public Stream<String> list() throws IOException
    {
        return Stream.empty();
    }

    @Override
    public void close() throws IOException
    {
        // nothing to do
    }
}
