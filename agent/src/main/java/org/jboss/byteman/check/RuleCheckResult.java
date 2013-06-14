/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
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
 */

package org.jboss.byteman.check;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Amos Feng
 *
 */
public class RuleCheckResult {
    private int errorCount = 0;
    private int warningCount = 0;
    private int parseErrorCount = 0;
    private int typeWarningCount = 0;
    private int typeErrorCount = 0;
    private List<String> errorMessages;
    private List<String> warningMessages;
    private List<String> parseErrorMessages;
    private List<String> typeWarningMessages;
    private List<String> typeErrorMessages;
    private List<String> infoMessages;
    
    public RuleCheckResult () {
        errorMessages = new ArrayList<String>();
        warningMessages = new ArrayList<String>();
        parseErrorMessages = new ArrayList<String>();
        typeWarningMessages = new ArrayList<String>();
        typeErrorMessages = new ArrayList<String>();
        infoMessages = new ArrayList<String>();
    }
    
    public void addError(String msg) {
        errorCount ++;
        errorMessages.add(msg);
    }
    
    public void addWarning(String msg) {
        warningCount++;
        warningMessages.add(msg);
    }

    public void addParseError(String msg) {
        parseErrorCount ++;
        parseErrorMessages.add(msg);
    }
    public void addTypeWarning(String msg) {
        typeWarningCount ++;
        typeWarningMessages.add(msg);
    }
    public void addTypeError(String msg) {
        typeErrorCount ++;
        typeErrorMessages.add(msg);
    }
    
    public void addInfo(String msg) {
        infoMessages.add(msg);
    }

    public int getErrorCount() {
        return errorCount;
    }
    
    public int getWarningCount() {
        return warningCount;
    }
    
    public int getParseErrorCount() {
        return parseErrorCount;
    }
    
    public int getTypeWarningCount() {
        return typeWarningCount;
    }
    
    public int getTypeErrorCount() {
        return typeErrorCount;
    }
    
    public boolean hasError() {
        return errorCount != 0 || parseErrorCount !=  0 || typeErrorCount != 0;
    }
    
    public boolean hasWarning() {
        return !(warningCount == 0 && typeWarningCount == 0); 
    }
    
    public boolean hasInfo() {
        return !infoMessages.isEmpty();
    }

    public List<String> getErrorMessages() {
        return errorMessages;
    }
    
    public List<String> getTypeErrorMessages() {
        return typeErrorMessages;
    }
    
    public List<String> getParseErrorMessages() {
        return parseErrorMessages;
    }
    
    public List<String> getWarningMessages() {
        return warningMessages;
    }
    
    public List<String> getTypeWarningMessages() {
        return typeWarningMessages;
    }
    
    public List<String> getInfoMessages() {
        return infoMessages;
    }
}
