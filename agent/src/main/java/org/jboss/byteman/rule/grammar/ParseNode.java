/*
* JBoss, Home of Professional Open Source
* Copyright 2009-10 Red Hat and individual contributors
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
package org.jboss.byteman.rule.grammar;

/**
 * Class used by the JavaCUP parser to construct a parse tree.
 */
public abstract class ParseNode
{
    /*
     * tags for different types of parse nodes
     */
    public final static int ARRAY = 0;
    public final static int ASSIGN = 1;
    public final static int BIND = 2;
    public final static int BINOP = 3;
    public final static int BOOLEAN_LITERAL = 4;
    public final static int COMMA = 5;
    public final static int COLON = 6;
    public final static int FIELD = 7;
    public final static int FLOAT_LITERAL = 8;
    public final static int IDENTIFIER = 9;
    public final static int INTEGER_LITERAL = 10;
    public final static int METH = 11;
    public final static int NOTHING = 12;
    public final static int PATH = 13;
    public final static int RETURN = 14;
    public final static int SEMI = 15;
    public final static int STRING_LITERAL = 16;
    public final static int TERNOP = 17;
    public final static int THROW = 18;
    public final static int UNOP = 19;
    public final static int NEW = 20;
    public final static int NULL_LITERAL = 21;
    /* tags for operators */
    public final static int AND = 30;
    public final static int BAND = 31;
    public final static int BOR = 32;
    public final static int BXOR = 33;
    public final static int DIV = 34;
    public final static int DOLLAR = 35;
    public final static int EQ = 36;
    public final static int GE = 37;
    public final static int GT = 38;
    public final static int LE = 39;
    public final static int LT = 40;
    public final static int MINUS = 41;
    public final static int MOD = 42;
    public final static int MUL = 43;
    public final static int NE = 44;
    public final static int NOT = 45;
    public final static int OR = 46;
    public final static int PLUS = 47;
    public final static int TWIDDLE = 48;
    public final static int UMINUS = 49;

    public final static int LSH = 50;
    public final static int RSH = 51;
    public final static int URSH = 52;

    /**
     * the type tag for this node
     */
    private int tag;

    /**
     * the script file containing the text form which this node was parsed
     */

    private String file;

    /**
     * the line position fo rthis node
     */

    private int line;

    /**
     * the column position for this node
     */
    private int column;

    /**
     * generic constructor
     * @param tag identifies the type of this node
     * @param line identifies the start line for this node's text
     * @param column identifies the start columen for this node's text
     */
    protected ParseNode(int tag, String file, int line, int column)
    {
        this.tag  = tag;
        this.file = file;
        this.line  = line;
        this.column  = column;
    }

    /**
     * get the tag for this node
     * @return the tag for this node
     */

    public int getTag() {
        return tag;
    }

    /**
     * get the line position for this node
     * @return the line position for this node
     */
    public int getLine() {
        return line;
    }

    /**
     * get the column position for this node
     * @return the column position for this node
     */
    public int getColumn() {
        return column;
    }

    /**
     * get the child count for this node
     * @return the child count for this node
     */
    public abstract int getChildCount();

    /**
     * get the nth child for this node or null if the index exceeds the child count
     * @return the nth child for this node
     */
    public abstract Object getChild(int idx);

    /**
     * get the display representation of this node
     * @return the display representation of this node
     */
    public abstract String getText();

    /**
     * get a string representing the position for this node
     * @return a string representing the position for this node
     */
    public String getPos() {
        return " " + file + " line " + line;
    }

    /**
     * create a simple node for a builtin token
     * @param tag identifies the type of this node
     * @param line identifies the start line for this node's text
     * @param column identifies the start columen for this node's text
     * @return a simple node for a builtin token
     */
    public static ParseNode node(int tag, String file, int line, int column)
    {
        return new NullaryNode(tag, file, line, column);
    }

    /**
     * create a simple node for a builtin token
     * @param tag identifies the type of this node
     * @param line identifies the start line for this node's text
     * @param column identifies the start columen for this node's text
     * @return a simple node for a builtin token
     */
    public static ParseNode node(int tag, String file, int line, int column, Object child0)
    {
        return new UnaryNode(tag, file, line, column, child0);
    }

    public static ParseNode node(int tag, String file, int line, int column, Object child0, Object child1)
    {
        return new BinaryNode(tag, file, line, column, child0, child1);
    }

    public static ParseNode node(int tag, String file, int line, int column, Object child0, Object child1, Object child2)
    {
        return new TernaryNode(tag, file, line, column, child0, child1, child2);
    }

    public static ParseNode node(int tag, String file, int line, int column, Object child0, Object child1, Object child2, Object child3)
    {
        return new QuaternaryNode(tag, file, line, column, child0, child1, child2, child3);
    }

    /**
     * a parse node with no children
     */
    private static class NullaryNode extends ParseNode
    {
        public NullaryNode(int tag, String file, int line, int column)
        {
            super(tag, file, line, column);
        }

        /**
         * get the child count for this node
         *
         * @return the child count for this node
         */
        public int getChildCount() {
            return 0;
        }

        /**
         * get the nth child for this node or null if the index exceeds the child count
         *
         * @return the nth child for this node
         */
        public Object getChild(int idx) {
            return null;
        }

        /**
         * get the display representation of this node
         *
         * @return athe display representation of this node
         */
        public String getText() {
            int tag = getTag();
            switch(tag) {
                case NOTHING:
                    return "NOTHING";
                case AND:
                    return "&&";
                case LSH:
                    return "<<";
                case RSH:
                    return ">>";
                case URSH:
                    return ">>>";
                case BAND:
                    return "&";
                case BOR:
                    return "|";
                case BXOR:
                    return "^";
                case DIV:
                    return "/";
                case DOLLAR:
                    return "$";
                case EQ:
                    return "==";
                case GE:
                    return ">=";
                case GT:
                    return ">";
                case LE:
                    return "<=";
                case LT:
                    return "<";
                case MINUS:
                    return "-";
                case MOD:
                    return "%";
                case MUL:
                    return "*";
                case NE:
                    return "!=";
                case NOT:
                    return "!";
                case OR:
                    return "||";
                case PLUS:
                    return "+";
                case TWIDDLE:
                    return "~";
                case UMINUS:
                    return "-";
                case NULL_LITERAL:
                    return "null";
                default:
                    System.out.println("NullaryNode.getText() : Unexpected tag " + tag);
                    return "???";
            }
        }
    }


    /**
     * a parse node with one child
     */
    private static class UnaryNode extends ParseNode
    {
        private Object child0;

        public UnaryNode(int tag, String file, int line, int column, Object child0)
        {
            super(tag, file, line, column);
            this.child0 = child0;
        }

        /**
         * get the child count for this node
         *
         * @return the child count for this node
         */
        public int getChildCount() {
            return 1;
        }

        /**
         * get the nth child for this node or null if the index exceeds the child count
         *
         * @return the nth child for this node
         */
        public Object getChild(int idx) {
            if (idx == 0) {
                return child0;
            }

            return null;
        }

        /**
         * get a string representing the display representation of this node
         *
         * @return a string representing the display representation of this node
         */
        public String getText() {
            int tag = getTag();
            switch(tag) {
                case ARRAY:
                {
                    // these occur when we have a type array declaration
                    return ((ParseNode)child0).getText() + "[]";
                }
                case BOOLEAN_LITERAL:
                    return child0.toString();
                case FLOAT_LITERAL:
                    return child0.toString();
                case INTEGER_LITERAL:
                    return child0.toString();
                case RETURN:
                    return "RETURN";
                case STRING_LITERAL:
                    return "\"" + ((String)child0) + "\"";
                case DOLLAR:
                    return ((String)child0);
                default:
                    System.out.println("UnaryNode.getText() : Unexpected tag " + tag);
                    return "???";
            }            
        }
    }

    /**
     * a parse node with two children
     */
    private static class BinaryNode extends ParseNode
    {
        private Object child0;
        private Object child1;

        public BinaryNode(int tag, String file, int line, int column, Object child0, Object child1)
        {
            super(tag, file, line, column);
            this.child0 = child0;
            this.child1 = child1;
        }

        /**
         * get the child count for this node
         *
         * @return the child count for this node
         */
        public int getChildCount() {
            return 2;
        }

        /**
         * get the nth child for this node or null if the index exceeds the child count
         *
         * @return the nth child for this node
         */
        public Object getChild(int idx) {
            switch (idx) {
                case 0:
                return child0;
                case 1:
                return child1;
            }

            return null;
        }

        /**
         * get the display representation of this node
         *
         * @return the display representation of this node
         */
        public String getText() {
            int tag = getTag();
            switch(tag) {
                case ARRAY:
                    // these occur when we have an array expression
                    return ((ParseNode)child0).getText() + "[" + ((ParseNode)child1).getText() + "]";
                case ASSIGN:
                    return "=";
                case BIND:
                    return "BIND";
                case COLON:
                    return ((ParseNode)child0).getText();
                case FIELD:
                    return "." + ((ParseNode)child1).getText();                    
                case IDENTIFIER:
                {
                    String text = (String)child0;
                    // include path prefix
                    ParseNode next = (ParseNode)child1;
                    while (next != null) {
                        text = ((ParseNode)next).getText() + "." + text;
                        next = (ParseNode)next.getChild(1);
                    }
                    return text;
                }
                case PATH:
                    return (String)child0;
                case SEMI:
                    return ";";                    
                case THROW:
                    return "THROW";
                case NEW:
                    return "NEW";
                case UNOP:
                    return ((ParseNode)child0).getText();
                default:
                    System.out.println("BinaryNode.getText() : Unexpected tag " + tag);
                    return "???";
}
        }
    }

    /**
     * a parse node with three children
     */
    private static class TernaryNode extends ParseNode
    {
        private Object child0;
        private Object child1;
        private Object child2;

        public TernaryNode(int tag, String file, int line, int column, Object child0, Object child1, Object child2)
        {
            super(tag, file, line, column);
            this.child0 = child0;
            this.child1 = child1;
            this.child2 = child2;
        }

        /**
         * get the child count for this node
         *
         * @return the child count for this node
         */
        public int getChildCount() {
            return 3;
        }

        /**
         * get the nth child for this node or null if the index exceeds the child count
         *
         * @return the nth child for this node
         */
        public Object getChild(int idx) {
            switch (idx) {
                case 0:
                return child0;
                case 1:
                return child1;
                case 2:
                return child2;
            }

            return null;
        }

        /**
         * get the display representation of this node
         *
         * @return the display representation of this node
         */
        public String getText() {
            int tag = getTag();
            switch(tag) {
                case BINOP:
                    return ((ParseNode)child0).getText();
                case METH:
                    return ((ParseNode)child0).getText();
                case TERNOP:
                    return "?";
                default:
                    System.out.println("TernaryNode.getText() : Unexpected tag " + tag);
                    return "???";
            }
        }
    }

    /**
     * a parse node with four children
     */
    private static class QuaternaryNode extends ParseNode
    {
        private Object child0;
        private Object child1;
        private Object child2;
        private Object child3;

        public QuaternaryNode(int tag, String file, int line, int column, Object child0, Object child1, Object child2, Object child3)
        {
            super(tag, file, line, column);
            this.child0 = child0;
            this.child1 = child1;
            this.child2 = child2;
            this.child3 = child3;
        }

        /**
         * get the child count for this node
         *
         * @return the child count for this node
         */
        public int getChildCount() {
            return 2;
        }

        /**
         * get the nth child for this node or null if the index exceeds the child count
         *
         * @return the nth child for this node
         */
        public Object getChild(int idx) {
            switch (idx) {
                case 0:
                return child0;
                case 1:
                return child1;
                case 2:
                return child2;
                case 3:
                return child3;
            }

            return null;
        }

        /**
         * get the display representation of this node
         *
         * @return the display representation of this node
         */
        public String getText() {
            int tag = getTag();
            switch(tag) {
                default:
                    System.out.println("QuaternaryNode.getText() : Unexpected tag " + tag);
                    return "???";
            }
        }
    }
}
