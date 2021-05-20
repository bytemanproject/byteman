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
* @authors Andrew Dinn
*/
package org.jboss.byteman.rule.helper;

import org.jboss.byteman.rule.Rule;
import org.jboss.byteman.rule.helper.Helper;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Helper class used by ThreadPoolHelper script to create more thread in thread pool.
 */
public class ThreadPoolHelper extends Helper
{
    static int threadNum = 0;

    protected ThreadPoolHelper(Rule rule) {
        super(rule);
        this.threadNum = 0;
    }

    public void traceExecute(ThreadPoolExecutor threadPool, int num)
    {
        try {
                if (this.threadNum > num) {
                    return;
                }

                this.threadNum++;
                ThreadTask task = new ThreadTask();
                task.setInterval(9999999);
                threadPool.execute(task);
                
            } catch(Exception e) {
                System.out.println("traceExecute get exception when execute new thread:" + e);
                return;
            }
       
    }
}
