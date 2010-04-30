package org.jboss.byteman.agent.submit;

/**
 * storage for a script file name and the corresponding script text
 */
public class ScriptText
{
    private String fileName;
    private String text;

    public ScriptText(String fileName, String text)
    {
        this.fileName = fileName;
        this.text = text;
    }

    public ScriptText(String text)
    {
        this.fileName = "";
        this.text = text;
    }

    public String getFileName() {
        return fileName;
    }

    public String getText() {
        return text;
    }
}
