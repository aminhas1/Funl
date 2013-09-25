package evaluator;
/**
 * Program creates a Tree<V> object that 
 * has the ability to set values of the nodes,
 * get number of children of the nodes, return a 
 * string of the tree, print out a tree of the string,
 * parse a proper string expression and turn it into a Tree<V> object.
 * 
 * @author Abeer Minhas
 */
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class Tree<V>{
    private V value;
    private ArrayList<Tree<V>> childrenArrayList;
    private Tree<V> parent = null;
    private boolean hasParent = false;
    

    /**
     * Constructs a Tree with the given value in the root node,
     * having the given children. 
     * @param value - value of root node
     * @param children - children of root
     */
    public Tree(V value, Tree<V>...children){
        try {
            this.value = value;
            this.childrenArrayList = new ArrayList<Tree<V>>();
            for (Tree<V> child : children){
                if (child.contains(this)){
                    throw new IllegalArgumentException("Creating circular tree.");
                }
                child.setHasParent();
                child.parent = this;
                this.childrenArrayList.add(child);
            }
        }
        catch (NullPointerException e){
            throw new IllegalArgumentException("Cannot add null as a node to Tree");


        }
    }
    /**
     * Sets the value in this node to the given value.
     * @param value is the value of this node.
     */
    public void setValue(V value){
        this.value = value;
    }
    
    /**
     * @returns value in this node. 
     */
    public V getValue(){
        return this.value;
    }

    /**
     * Adds the child as the new index'th child of this Tree; 
     * subsequent nodes are "moved over" as necessary to 
     * make room for the new child.
     * @param index - new index of child in ArrayList
     * @param child - child to be placed in tree
     */
    public void addChild(int index, Tree<V> child){
        try{
            child.setHasParent();
            if (index < 0 || index > numberOfChildren()){
                throw new IndexOutOfBoundsException("Cannot add at this index value");
            }
            if (child.contains(this)){
                throw new IllegalArgumentException("Creating circular tree.");
            }

            childrenArrayList.add(index, child);
            child.parent = this; 
        }
        catch (NullPointerException e){
            throw new IllegalArgumentException("Cannot add null");
        }

    }

    public void replace(int index, Tree<V> child){
    	try{
            child.setHasParent();
            if (index < 0 || index > numberOfChildren()){
                throw new IndexOutOfBoundsException("Cannot add at this index value");
            }
            if (child.contains(this)){
                throw new IllegalArgumentException("Creating circular tree.");
            }
            childrenArrayList.set(index, child);
            child.parent = this; 
        }
        catch (NullPointerException e){
            throw new IllegalArgumentException("Cannot add null");
        }
    }

    /**
     * If the child is not a root node
     * it will have a parent. 
     */
    protected void setHasParent(){
        this.hasParent = true;
    }
    
    /**
     * @returns true if the child has a parent.
     * returns false if it does not have a parent (is a root node).
     */
    protected boolean getHasParent(){
        return this.hasParent;
    }
    
    /**
     * @returns the parent node of the current node
     */
    protected Tree<V> getParent(){
        return this.parent;
    }
    
    /**
     * @returns an ArrayList of children of the current node; 
     */
    public ArrayList<Tree<V>> getChildren(){
        return childrenArrayList;
    }
    
    public Tree<V> child(int index){
        if(numberOfChildren() == 0) {
            throw new NullPointerException("This tree has no children");
        }
        return childrenArrayList.get(index);
    }
    
    public Tree<V> firstChild(){
        if(numberOfChildren() == 0) {
            throw new NullPointerException("This tree has no children");
        }
        return childrenArrayList.get(0);
    }
    
    /**
     * Adds the new children to this node after any current children.
     * @param children
     */
    @SuppressWarnings("unchecked") 
    public void addChildren(Tree<V>...children){
        for (Tree<V> child : children){
            try{
            if (child.contains(this)){
                throw new IllegalArgumentException("Creating circular tree.");
            }
            child.setHasParent();
            childrenArrayList.add(child);
            child.parent = this; 
            }
            catch (NullPointerException e){
                throw new IllegalArgumentException("Cannot add null as a child");
            }
        }
    }
    
    /**
     * 
     * @return the number of children that this node has.
     */
    public int numberOfChildren(){
        return childrenArrayList.size();
    }
    
    /**
     * @returns true if the node has at least one child.
     */
    boolean hasChildren(){
        return (this.numberOfChildren() > 0);
    }
    
    /**
     * Returns the index'th child of this node.
     */
    public Tree<V> getChild(int index){
        if (index < 0 || index > numberOfChildren()){
            throw new IndexOutOfBoundsException("Incorrect index value: " + index);
        }
        return childrenArrayList.get(index);
    }
    
    /**
     * Returns an iterator for the children of this node.
     */
    public Iterator<Tree<V>> iterator(){
        return childrenArrayList.iterator();
    }

    /**
     * Searches this Tree for a node that is == to node, and returns
     * true if found, false otherwise.
     * @param node is node we are looking for in the current tree
     * @returns true if @param node is found in current tree. 
     */
    boolean contains(Tree<V> node){
        if (this == node){
            return true;
        }
        ArrayList<Tree<V>> children = getChildren();
        for (Tree<V> child : children){
            //@param foundNode changes value each time to ensure iteration through entire tree
            boolean foundNode = child.contains(node);
            if(foundNode) {
                return true;
            }
        }
        return false;
    }

    
            
    /**
     * Returns a String representing this Tree. 
     * The returned String must be in the same format as the parse 
     * method expects as input.
     */
    @Override
    public String toString(){
        String str = "" + this.getValue();
        if (this.hasChildren()){
            str = str + "(";
            ArrayList<Tree<V>> children = getChildren();
            for (Tree<V> child : children){
                str = str + " " + child.toString();
            }
            str = str+")";
        }
        return str;
    }

    /**
     * Returns the top most node in the tree. Only works
     * @param child
     * @return
     */
    protected static Tree<String> getRoot(Tree<String> child){
        Tree<String> parent = child;
        while (parent.getHasParent()){
            parent= parent.getParent();
        }

        return parent;
    }
    
    /**
     * Finds and returns ancestor by @number generations above
     * the inputted child Node.  Called in parse function.
     * @param number of generations above the current node .
     * @param child - node who's ancestor to look for
     * @returns required ancestor node
     */
    protected static Tree<String> getGrandParent(int number, Tree<String> child){
        Tree<String> grandparent = child; 
        for (int i = 0; i < number + 1; i++){
            if(!(grandparent.getHasParent())){
                return null;
            }
            grandparent = grandparent.parent; 
        }
        return grandparent;
    }
    
    /**
     * Parses a string of the general form
     * <code>value(child child ... child)</code> and returns the
     * corresponding tree. Children are separated by spaces.
     * Node values are all Strings.
     * 
     * @param s The String to be parsed.
     * @return The resultant Tree&lt;String&lt;.
     * @throws IllegalArgumentException
     *             If problems are detected in the input string.
     */
    public static Tree<String> parse(String s) throws IllegalArgumentException {
        StringTokenizer tokenizer = new StringTokenizer(s, " ()", true);
        List<String> tokens = new LinkedList<String>();
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if (token.trim().length() == 0)
                continue;
            tokens.add(token);
        }
        Tree<String> result = parse(tokens);
        if (tokens.size() > 0) {
            throw new IllegalArgumentException("Leftover tokens: " + tokens);
        }
        return result;
    }
    
    /**
     * Parses and returns one tree, consisting of a value and possible children
     * (enclosed in parentheses), starting at the first element of tokens.
     * Returns null if this token is a close parenthesis, or if there are no
     * more tokens.
     * 
     * @param tokens
     *            The tokens that describe a Tree.
     * @return The Tree described by the tokens.
     * @throws IllegalArgumentException
     *             If problems are detected in the input list.
     */
    private static Tree<String> parse(List<String> tokens)
            throws IllegalArgumentException {
        // No tokens -- return null
        if (tokens.size() == 0) {
            return null;
        }
        // Get the next token and remove it from the list
        String token = tokens.remove(0);
        // If the token is an open parenthesis
        if (token.equals("(")) {
            throw new IllegalArgumentException(
                "Unexpected open parenthesis before " + tokens);
        }
        // If the token is a close parenthesis, we are at the end of a list of
        // children
        if (token.equals(")")) {
            return null;
        }
        // Make a tree with this token as its value
        Tree<String> tree = new Tree<String>(token);
        // Check for children
        if (tokens.size() > 0 && tokens.get(0).equals("(")) {
            tokens.remove(0);
            Tree<String> child;
            while ((child = parse(tokens)) != null) {
                tree.addChildren(child);
            }
        }
        return tree;
    }
    
//    /**
//     * Called in parse function. Used to parse the string based 
//     * on ')' and '(' values.
//     * @param tokens array of all values of given string, parsed by spaces
//     * @param helper is the current node who's children are to be added. 
//     * @param i - is the current index of the @param tokens array
//     */
//    @SuppressWarnings("unchecked")
//    private static void parseHelper(String[] tokens, Tree<String> helper, int i){
//        Tree<String> newTree;
//        if(!(tokens[i].equals(")"))){
//            if(tokens[i].equals("(")) i++;
//            while(!tokens[i].equals("(") && !tokens[i].equals(")") && i < tokens.length - 1){
//                newTree = new Tree<String>(tokens[i]);
//                helper.addChildren(newTree);
//                i++;
//                if(tokens[i].equals(")") || tokens[i].equals("(")){
//                    parseHelper(tokens, newTree, i);
//                }
//            }
//        }
//        else if(tokens[i].equals(")")){
//            int k = i;
//            int number = 0;
//            while (tokens[k].equals(")") && k < tokens.length-1){
//                k++;
//                number++;
//                if(k == tokens.length) break;
//            }
//            if(k < tokens.length -1){
//                i++;
//            if (helper.getHasParent()){
//                Tree<String> newHelper = getGrandParent(number, helper);
//                if(newHelper != null){
//                    parseHelper(tokens, newHelper, i);
//                }
//                else {
//                    newHelper = getRoot(helper);
//                    parseHelper(tokens, newHelper, i);
//                }
//            }
//            else {
//                Tree<String> newHelper = getRoot(helper);
//                parseHelper(tokens, newHelper, i);
//            }
//            }
//        }
//
//    }
//    
//
//    /**
//     * Translates a String description of a tree into a Tree<String> object. 
//     * The treeDescription has the form value(child child ... child), 
//     * where a value is any sequence of characters not containing parentheses or whitespace,
//     * and each child is either just a (String) value or is another treeDescription.
//     * @param treeDescription is the given string that is to be parsed
//     * @returns a Tree based upon the treeDescription string.
//     */
//    public static Tree<String> parse(String treeDescription){
//        treeDescription = addSpaces(treeDescription);
//        String delims = "[ ]+";
//        String[] tokens = treeDescription.split(delims);
//        Tree<String> newTree = new Tree<String>(tokens[0]);
//        if (tokens.length == 1) return newTree;
//
//        parseHelper(tokens, newTree, 1);
//        return newTree;
//    
//    }
//    
    /**
     * Used to help print() function.
     * Adds '|' based on how many generations it is away
     * from the root
     * @param countBraces is the number of braces found in the string. Used
     * to count how many generations the currentNode is away from the root
     */
    private void printHelper(int countBraces){
        System.out.print("\n");
        for(int k = 0; k < countBraces; k++){
            System.out.print("|");
        }
        System.out.print(" ");
    }
    
    /**
     * Prints a multiline version of the tree (see below).
     */
    public void print(){
        String str = toString();
        str = str.trim();
        int countBraces = 0;
        char[] string = str.toCharArray();
        int i =0;
        while (i < string.length){
            if (string[i] == '('){
                i+=2;
                countBraces++;
                printHelper(countBraces);
            }
            else if(string[i] == ')'){
                i+=2;
                if (i >= string.length){ 
                    System.out.print("\n");
                    break;
                }
                countBraces--;
                if ((string[i+1] > 41 && string[i+1] < 123)){
                    printHelper(countBraces);
                }
            }
            else if(string[i] == ' '){
                i++;
                printHelper(countBraces);           
            }
        System.out.print(string[i]);
        i++;            
        }
    }   
    
    
    /**
     * Called in parse. Used to add spaces whereever parentheses are found
     * in @param treeDescription so as to easily tokenize the string. 
     * @param treeDescription is the inputted string to be parsed
     * @returns the adjusted treeDescription
     */
    protected static String addSpaces(String treeDescription){
        String delims = "[(]";
        String replace = " ( ";

        treeDescription = treeDescription.replaceAll(delims, replace);
        delims = "[)]";
        replace = " ) ";
        treeDescription = treeDescription.replaceAll(delims, replace);


        return treeDescription;
    }

    
    /**
     * Returns true if obj is a Tree and has the same shape and contains 
     * the same values as this Tree.
     */
    @Override
    public boolean equals(Object obj){
        if (!(obj instanceof Tree<?>)){
            return false;
        }
        //System.out.println("Got here for " +  this.getValue());
        String string1 = this.toString();
        String string2 = obj.toString();
        
        if(string1.equals(string2)) return true;
        
        return false;
    }


    

    
    
}

