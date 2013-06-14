package org.jboss.byteman.contrib.rulecheck;

/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat and individual contributors
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
 * @authors Amos Feng
 */
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.jboss.byteman.check.RuleCheck;
import org.jboss.byteman.check.RuleCheckResult;

import java.io.File;
import java.net.MalformedURLException;
import java.util.List;

/**
 * Check the byteman script rule
 *
 */
@Mojo( name = "rulecheck", defaultPhase = LifecyclePhase.PROCESS_TEST_CLASSES, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class RuleCheckMojo extends AbstractMojo
{   
    @Component
    private MavenProject project;

    @Component
    private PluginDescriptor descriptor;

    /**
     * Location of the script.
     */
    @Parameter( defaultValue = "${project.build.testOutputDirectory}", property = "scriptDir", required = true )
    private File scriptDir;

    /**
     * Packages to lookup non-package qualified class names
     */
    @Parameter( property = "packages")
    private String[] packages;

    /**
     * Fail build when rule check returns error
     */
    @Parameter(defaultValue = "true", property = "failOnError")
    private boolean failOnError;
    
    /**
     * Fail build when rule check has warnings
     */
    @Parameter(defaultValue = "true", property = "failOnWarning")
    private boolean failOnWarning;
    
    /**
     * Expect count of warning messages
     */
    @Parameter(defaultValue = "0", property = "expectWarnings")
    private int expectWarnings;

    /*
     * Skip the checking
     */
    @Parameter(defaultValue = "false", property = "skip")
    private boolean skip;

    /**
     * include specified script files
     */
    @Parameter(property = "includes")
    private String[] includes;

    /**
     * exclude specified script files
     */
    @Parameter(property = "excludes")
    private String[] excludes;

    /**
     * additional class path 
     */
    @Parameter(property = "additionalClassPath")
    private String additionalClassPath;

    /** 
     * verbose 
     */
    @Parameter(defaultValue = "false" , property = "verbose")
    boolean verbose;

    public void execute() throws MojoExecutionException {
        List<File> scripts;

        if(skip) {
            getLog().info("Checking byteman scripts are skipped");
            return;
        }

        try {
            if(verbose) {
                getLog().info("find byteman script in " + scriptDir);
            }
            StringBuffer includebuf = new StringBuffer();
            for(int i = 0; i < includes.length; i++) {
                includebuf.append(includes[i]);
                if(i != includes.length - 1) includebuf.append(",");
            }
            
            StringBuffer excludebuf = new StringBuffer();
            for(int i = 0; i < excludes.length; i++) {
                excludebuf.append(excludes[i]);
                if(i != excludes.length - 1) excludebuf.append(",");
            }
            scripts = FileUtils.getFiles(scriptDir, includebuf.toString(), excludebuf.toString());
        } catch (Exception e) {
            getLog().debug("Can not find " + scriptDir);
            return;
        }

        if(scripts.size() == 0) {
            getLog().info("No byteman script in " + scriptDir);
            return;
        }

        List<String> classpathElements;
        try {
            classpathElements = project.getCompileClasspathElements();
            classpathElements.addAll(project.getRuntimeClasspathElements());
            classpathElements.add(project.getBuild().getOutputDirectory() );
            classpathElements.add(project.getBuild().getTestOutputDirectory() );
            if(additionalClassPath != null) {
                String[] cps = (additionalClassPath.split(";"));
                for(int i = 0; i < cps.length; i++) {
                    File file =  new File(cps[i]);
                    String path = null;
                    if(file.isAbsolute()) {
                        path = cps[i];
                    } else {
                        path = project.getBasedir() + File.separator + cps[i];
                    }
                    classpathElements.add(path);
                    if(verbose) {
                        getLog().info("add addional classpath " + path);
                    }
                }
            }
            ClassRealm realm = descriptor.getClassRealm();

            for (String element : classpathElements)
            {
                File elementFile = new File(element);
                if(verbose) {
                    getLog().info(element);
                } else {
                    getLog().debug(element);
                }
                realm.addURL(elementFile.toURI().toURL());
            }
        } catch (DependencyResolutionRequiredException e) {
            getLog().warn(e);
        } catch (MalformedURLException e) {
            getLog().warn(e);
        }

        RuleCheck checker = new RuleCheck();
        for(File script : scripts) {
            if(verbose) {
                getLog().info("add script " + script);
            }else {
                getLog().debug("add script " + script);
            }
            checker.addRuleFile(script.getAbsolutePath());
        }

        for(int i = 0; i < packages.length; i++) {
            checker.addPackage(packages[i]);
            if(verbose) {
                getLog().info("add package " + packages[i]);
            }else {
                getLog().debug("add package " + packages[i]);
            }
        }
        getLog().info("Checking " + scripts.size() + " byteman scripts in " + scriptDir);
        checker.checkRules();
        RuleCheckResult result= checker.getResult();
        if(result.hasWarning()) {
            List<String> warns = result.getWarningMessages();
            warns.addAll(result.getTypeWarningMessages());
            for(String warn : warns) {
                getLog().warn(warn);
            }
            int totalWarnCount = warns.size();
            if(failOnWarning && expectWarnings != totalWarnCount) {
                throw new MojoExecutionException("check byteman script rules failed with " + totalWarnCount + 
                        " warnings. You may config failOnWarning with false or expectWarnings with " + totalWarnCount);
            }
        }
        if(result.hasError()) {
            int totalErrorCount = result.getErrorCount() + result.getParseErrorCount() + result.getTypeErrorCount();
            getLog().error("Checking byteman script rules failed with " + totalErrorCount + " errors");
            List<String> errors = result.getErrorMessages();
            errors.addAll(result.getParseErrorMessages());
            errors.addAll(result.getTypeErrorMessages());
            for(String error : errors) {
                getLog().error(error);
            }
            if(failOnError) {
                throw new MojoExecutionException("check byteman script rules failed with " + totalErrorCount + " errors");
            } 
        }
    }
}
