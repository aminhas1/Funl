package evaluator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

import javax.swing.JFileChooser;

public class Funl {
	/**
	 * @author Abeer Minhas 
	 * 
	 * This program is an evaluator. It allows user to calculate various
	 * mathematical functions. 
	 */

    protected static HashMap<String,Tree<Token>> functions;
    protected Stack<HashMap<String, Tree<Token>>> stack;

    /**
     * Main Program that runs the REPL for user to utilize.
     */
    public static void main(String [ ] args){
        Funl funl = new Funl();
        try {
			funl.REPL();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Funl Constructor initialize the stack with global variables
     *  placed at the beginning
     */
    public Funl(){
        stack = new Stack<HashMap<String,Tree<Token>>>();
        HashMap<String, Tree<Token>> globalVars = new HashMap<String,Tree<Token>>();
        stack.push(globalVars);
    }

    /**
     * The REPL allows for the evaluation of various mathematical functions.
     * User loads a file of functions, and can then use them to calculate 
     * answers. 
     */
    public void REPL() throws IOException {
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.print("Funl>> ");
            String value = scanner.nextLine();
            if(value.equals("load")) {
                String toLoad = load();
                define(toLoad);

            }
            else if(value.equals("quit")) {
                System.out.println("Ok you have quit the program");
                break;
            }
            else {
                Parser parser = new Parser(value);
                if(parser.expression()) {
                    try {
                        Tree<Token> completed = eval(parser.stack.peek());
                        System.out.println(completed.getValue().getValue());
                    }
                    catch (NullPointerException e) {
                        System.out.println("I do not understand. Please try again");
                    }
                    catch (IllegalArgumentException e) {
                        System.out.println("I do not understand. Please try again");
                    }
                    catch(RuntimeException e){
                    	System.out.println("There is an error with the inputted expression please fix it");
                    }
                }
                else {
                    System.out.println("Error: Invalid input.");
                }
        }
        }
    }
    
    /**
     * Returns true if the string is a number
     */
    private boolean isNumber(String s) {
        try {
            Double.parseDouble(s);
        }
        catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    
    /**
     * Returns true if the string is an operator
     */
    protected boolean isOperator(String s) {
        if(s.equals("+") || s.equals("-")||s.equals("*") || s.equals("/")) {
            return true;
        }
        return false;
    }
    
    /**
     * Looks up @param childValue in the variable hashTable. If it is
     * found then it returns that value.
     */ 
    public String obtainVariableString(String childValue){
    	HashMap<String, Tree<Token>> localVars = stack.peek();
        if(localVars.containsKey(childValue)) {
            Tree<Token> expr = localVars.get(childValue);
            String root = expr.getValue().getValue();
            if(isNumber(root)){
            	return String.valueOf(root);
            }
            else return root;
        }
        return null;
    }
    
    /**
     * Looks up variable in variable hashtable and returns value
     */
    public double obtainVariable(String childValue) {
    	HashMap<String, Tree<Token>> localVars = stack.peek();
        if(localVars.containsKey(childValue)) {
            Tree<Token> expr = localVars.get(childValue);
            String root = expr.getValue().getValue();
            if(isNumber(root)){
            	return Double.parseDouble(root);
            }
            else return Double.MIN_VALUE;
        }
        
       return Double.MAX_VALUE;
    }
    
    /**
     * Recursively determines an operation based on given tree and returns a sum.
     */
    private double Operation(Tree<Token> expr) {
        expr= updateParameters(expr);
        String root = expr.getValue().getValue();
       
        double sum = 0;
        if (root.equals("+")) {
            ArrayList<Tree<Token>> children = expr.getChildren();
            for (Tree<Token> child : children){
                String childValue = child.getValue().getValue();
                	sum+= Double.parseDouble(childValue);
            }
        }
        else if (root.equals("-")) {
            ArrayList<Tree<Token>> children = expr.getChildren();
            for (Tree<Token> child : children){
            	
                String childValue = child.getValue().getValue();
                if(child == expr.getChild(0)){
                	sum = Double.parseDouble(childValue);
                }
                else{
                	sum-= Double.parseDouble(childValue);
                }
            }
        }
        else if (root.equals("*")) {
            ArrayList<Tree<Token>> children = expr.getChildren();
            for (Tree<Token> child : children){
            	
                String childValue = child.getValue().getValue();
                if(child == expr.getChild(0)){
                	sum = Double.parseDouble(childValue);
                }
                else{
                	sum*= Double.parseDouble(childValue);
                }
            }
        }
        else if (root.equals("/")) {
            ArrayList<Tree<Token>> children = expr.getChildren();
            for (Tree<Token> child : children){
            	
                String childValue = child.getValue().getValue();
                if(child == expr.getChild(0)){
                	sum = Double.parseDouble(childValue);
                }
                else{
                	sum/= Double.parseDouble(childValue);
                }
            }
        }                
        return sum;
    }

    /**
     * Takes a Funl expression, evaluates it, and returns another Funl expression. 
     */
    @SuppressWarnings("unchecked")
    protected Tree<Token> eval(Tree<Token> expr){
    	HashMap<String, Tree<Token>> localVars = fetch();
        String root = expr.getValue().getValue();
        if(isNumber(root)) {
            return expr;
        }
        else if(isOperator(root)) {
        	double value = Operation(expr);
        	String result = "" + value;
        	expr = new Tree<Token>(new Token(TokenType.NUMBER, result));
        }
        else if(root.equals("val")) {
            String key = expr.getChild(0).getValue().getValue();
            Tree<Token> value = eval(expr.getChild(1)); 
            localVars.put(key.trim(),value);
            expr = value;
        }
        else if(localVars.containsKey(root.trim())) {//Do I need this?
            expr = localVars.get(root.trim());
        }
        else if(root.equals("read")) {
            Scanner scanner = new Scanner(System.in);
            try {
            double num = scanner.nextDouble();
            expr = new Tree<Token>(new Token(TokenType.NUMBER, ("" + num)));
            }
            catch(InputMismatchException e) {
                System.out.println("This only accepts doubles!");
            }
        }
        else if(root.equals("if")) {
            Tree<Token> ifTree = expr.getChild(0);
            Tree<Token> thenTree = expr.getChild(1);
            Tree<Token> elseTree = expr.getChild(2);
            
            ifTree = updateParameters(ifTree);
            String ifRoot = ifTree.getChild(0).getValue().getValue();
            double num = Double.parseDouble(ifRoot);
            if(num > 0.0) {
            	thenTree = updateParameters(thenTree);

                ArrayList<Tree<Token>> children = thenTree.getChildren();
                for (Tree<Token> child : children){
                    expr = eval(child);//=========================CHECK THIS
                }
            }
            else {
            	elseTree = updateParameters(elseTree);
                ArrayList<Tree<Token>> children = elseTree.getChildren();
                for (Tree<Token> child : children){
                    expr = eval(child);//=========================CHECK THIS
                }
            }
        }
        else if(root.equals("$call")) {
            Tree<Token> passedParameters = expr.getChild(1);
            passedParameters = updateParameters(passedParameters);
            Tree<Token> calledFunction = functions.get(expr.getChild(0).getValue().getValue());
            if(calledFunction == null){
            	String tryString = expr.getChild(0).getValue().getValue();
            	if(fetch().containsKey(tryString)){
            		calledFunction = fetch().get(tryString);
            		tryString = calledFunction.getValue().getValue();
            		calledFunction = functions.get(tryString);
            	}
            	else{
            		System.out.println("ERROR: Function Name Does Not Exist.");
            		return null;
            	}
            }
            localVariables(passedParameters,calledFunction);
            expr = calledFunction.getChild(2);
        	ArrayList<Tree<Token>> children = expr.getChildren();
        	for(Tree<Token> child : children){
        		expr = eval(child);
        	}
            stack.pop();
        }
           
        return expr;
    }

    /**
     * Makes a new local Variable hashMap based on new function call
     * and pushes it onto the stack
     */
    private void localVariables(Tree<Token> passedParameters, Tree<Token> calledFunction){
    	HashMap<String,Tree<Token>> localVars = new HashMap<String,Tree<Token>>();
    	Tree<Token> calledFunctionParameters = calledFunction.getChild(1);
    	if(passedParameters.numberOfChildren() != calledFunctionParameters.numberOfChildren()){
    		System.out.println("ERROR: The function expects " + calledFunctionParameters.numberOfChildren() + 
    				" arguments and you have tried to pass " + passedParameters.numberOfChildren() + 
    				" arguments.");
    		throw new IllegalArgumentException();
    	}
        for(int i = 0; i <  passedParameters.numberOfChildren(); i++) {
            Tree<Token> childPassed = passedParameters.getChild(i);
            Tree<Token> childCalled = calledFunctionParameters.getChild(i);
            localVars.put(childCalled.getValue().getValue(), childPassed);
        }
    	stack.push(localVars);
    }
    
    
    /**
     * Updates parameters so that they are all numbers.
     */
    @SuppressWarnings("unchecked")
	private Tree<Token> updateParameters(Tree<Token> passedParameters){
    	String value = passedParameters.getValue().getValue();
    	TokenType type = passedParameters.getValue().getType();
    	Tree<Token> newTree = new Tree<Token>(new Token(type,value));
    	ArrayList<Tree<Token>> children = passedParameters.getChildren();
    	for(Tree<Token> child : children){
    		String childValue = child.getValue().getValue();
			HashMap<String, Tree<Token>> localVars = fetch();

			if(localVars.containsKey(childValue.trim())){
				String temp = childValue;
				childValue = ""+obtainVariable(childValue);
				if(childValue.equals(""+Double.MIN_VALUE)){
					childValue = obtainVariableString(temp);
				}
				child = new Tree<Token>(new Token(TokenType.NUMBER, childValue));
				newTree.addChildren(child);
				continue;
			}
    		if(functions.containsKey(childValue)){
    			newTree.addChildren(child);
    		}
    		else if(!(isNumber(childValue))){
    			while(!isNumber(childValue)){
    				child = eval(child);
    				newTree.addChildren(child);
    				childValue = child.getValue().getValue();
    			}
    		}
    		else{
    			newTree.addChildren(child);
    		}
    	}
    	return newTree;
    }

    /**
     * Fetches the top HashMap in the stack that contains variables
     * This would be the most recent scope
     */
    private HashMap<String,Tree<Token>> fetch(){
        return stack.peek();
    }
    
    
    /**
     * This method will take a "program" (one or more function definitions), parse it,
     * and save the functions in a HashMap<String, Tree<Token>>.
     * @param functionDefinitions
     */
    public void define(String functionDefinitions) {
        Parser parser = new Parser(functionDefinitions);
        parser.program();
        functions = parser.getFunctions();
        Set<String> keys = functions.keySet();
        Iterator<String> keyiterator = keys.iterator();
        System.out.print("Functions Loaded: [");
        while(keyiterator.hasNext()) {
            System.out.print(keyiterator.next());
            if(keyiterator.hasNext()) System.out.print(", ");
        }
        System.out.println("]");
    }
    
    /**
     * Brings up a JFileChooser and allows the user to pick a file to be read by REPL
     * All functions in file will be added to a global HashMap functions.
     */
    @SuppressWarnings("resource")
	public String load() throws IOException {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Load which file?");
        StringBuffer str = new StringBuffer();
        File file = null;
        int result = chooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
        }
        BufferedReader br;
        try {
            br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
               str.append(line + " ");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return str.toString();
    }
    
    
    
}
