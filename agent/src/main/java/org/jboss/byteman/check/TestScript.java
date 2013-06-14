/*
* JBoss, Home of Professional Open Source
* Copyright 2008-10 Red Hat and individual contributors
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
package org.jboss.byteman.check;

/**
 * utility which parses and typechecks all rules in a rule script.
 *
 * usage : java org.jboss.byteman.TestScript [scriptfile]
 *
 * n.b. the byteman jar and any classes mentioned in the script rules need to be in the classpath
 */
public class TestScript
{

    public static void main(String[] args)
    {
        int length = args.length;
        RuleCheck check = new RuleCheck();
        check.setPrintStream(System.out);

        int start =  0;
        boolean verbose = false;

        while (start < length) {
            if (args[start].equals("-p"))  {
                start++;
                if (start == length) {
                    usage();
                    return;
                }
                String packageName = args[start++];
                check.addPackage(packageName);
            } else if (args[start].equals("-v")) {
                start++;
                verbose = true;
            } else if (args[start].equals("-h")) {
                usage();
                return;
            } else {
                break;
            }
        }

        // must have some  args
        if (start == length) {
            usage();
            return;
        }

        while (start < length) {
            check.addRuleFile(args[start++]);
        }
        check.checkRules();

        RuleCheckResult result= check.getResult();

        if(result.hasError()) {
            int parseErrorCount = result.getParseErrorCount();
            int typeErrorCount = result.getTypeErrorCount();
            int typeWarningCount = result.getTypeWarningCount();
            int warningCount = result.getWarningCount() + typeWarningCount;
            int errorCount = result.getErrorCount() + parseErrorCount + typeErrorCount + typeWarningCount;
            System.out.println("TestScript: " + errorCount + " total errors");
            System.err.println("            " + warningCount + " total warnings");
            System.err.println("            " + parseErrorCount + " parse errors");
            System.err.println("            " + typeErrorCount + " type errors");
            System.err.println("            " + typeWarningCount + " type warnings");
        } else if (result.hasWarning()) {
            int typeWarningCount = result.getTypeWarningCount();
            int warningCount = result.getWarningCount() + typeWarningCount;
            System.out.println("TestScript: " + warningCount + " total warnings");
            System.err.println("            " + typeWarningCount + " type warnings");
        } else {
            System.err.println("TestScript: no errors");
        }
    }

    public static void usage()
    {
        System.out.println("usage : java org.jboss.byteman.TestScript [-p <package>]* [-v] scriptfile1 ...");
        System.out.println("        -p specify package to lookup non-package qualified classnames");
        System.out.println("        -v display parsed rules");
        System.out.println("        n.b. place the byteman jar and classes mentioned in the ");
        System.out.println("        scripts in the classpath");
    }
}