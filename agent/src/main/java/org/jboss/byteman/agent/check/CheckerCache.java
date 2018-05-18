/*
 * JBoss, Home of Professional Open Source
 * Copyright 2018, Red Hat and individual contributors
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

package org.jboss.byteman.agent.check;


import java.util.HashMap;
import java.util.WeakHashMap;

/**
 * A cache used to avoid repeatedly loading and determining the
 * structural details of classes during traversal of the super
 * and implemented interface chains.
 */
public class CheckerCache
{

    private static class CheckerStats
    {
        private long maps = 0;
        private long classes = 0;
        private long bytes = 0;
        private long hits = 0;
        private long hit_bytes = 0;

        public CheckerStats() { }

        public synchronized void record_hit(ClassLoader loader, String name, BytecodeChecker checker)
        {
            hits++;
            hit_bytes += checker.getBytesize();
            dump("Hit: ", loader, name);
        }
        public synchronized void record_map(ClassLoader loader)
        {
            maps++;
        }
        public synchronized void record_put(ClassLoader loader, String name, BytecodeChecker checker)
        {
            classes++;
            bytes +=  checker.getBytesize();
            dump("Put:", loader, name);
        }
        public void dump(String prefix, ClassLoader loader, String name)
        {
            System.out.format("\n  *** %s %s (%s)\n  ***maps %d classes %d bytes %d hits %d hit bytes %d\n",
                              prefix, name, (loader != null ? loader.toString() : "null"), maps, classes, bytes, hits, hit_bytes);
        }
    }

    public CheckerCache()
    {
    }

    // property which can be used to dump data used to compute checker cache stats
    private final String DUMPSTATS = "org.jboss.byteman.checker.dumpstats";
    private final boolean dumpstats = Boolean.valueOf(System.getProperty(DUMPSTATS));

    // hash table of hash tables associating indexes for previously created checkers with the loader which created them
    private WeakHashMap<ClassLoader, HashMap<String, BytecodeChecker>> loaderMaps = new WeakHashMap<ClassLoader, HashMap<String, BytecodeChecker>>();
    private final CheckerStats stats = (dumpstats ? new CheckerStats() : null);

    public BytecodeChecker lookup(ClassLoader loader, String name)
    {
        HashMap<String, BytecodeChecker> loaderMap = null;

        // see if we have cached details of this class in the map associated with this loader

        synchronized (loaderMaps) {
            loaderMap = loaderMaps.get(loader);
            if (loaderMap == null) {
                return null;
            }
        }

        synchronized (loaderMap) {
            BytecodeChecker checker = loaderMap.get(name);
            if(dumpstats && checker != null) {
                stats.record_hit(loader, name, checker);
            }
            return checker;
        }
    }

    public void put(ClassLoader loader, String name, BytecodeChecker checker) {

        HashMap<String, BytecodeChecker> loaderMap = null;

        // cache details of this class in the map associated with this loader

        synchronized (loaderMaps) {
            loaderMap = loaderMaps.get(loader);
            if(loaderMap == null) {
                loaderMap = new HashMap<String, BytecodeChecker>();
                loaderMaps.put(loader, loaderMap);
                if (dumpstats) {
                    stats.record_map(loader);
                }
            }
        }

        synchronized (loaderMap) {
            loaderMap.put(name, checker);
            if (dumpstats && checker != null) {
                stats.record_put(loader, name, checker);
            }
        }
    }
}

