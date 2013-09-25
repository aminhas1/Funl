package evaluator;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Stack;

/**
 * Parser for the CIT 594 FunL Project
 * @author Abeer Minhas
 *
 */
public class Parser {
    protected Stack<Tree<Token>> stack;
    protected HashMap<String, Tree<Token>> functions;
    protected Tokenizer tokenizer;
    protected String functionName;
    
    /**
     * Constructor for Parser.
     * Initializes the tokenizer, stack<Tree<Token>>, and HashMap<String,Tree<Token>> that
     * are needed in the class. 
     */
    public Parser(String string) {
        tokenizer = new Tokenizer(string);
        stack = new Stack<Tree<Token>>();
        functions = new HashMap<String,Tree<Token>>();
    }
    
    /**
     * Construct an AST for each <function definition>, then remove the AST from the stack and save it in
     * a HashMap<String, Tree<Token>>. The key will be the function name, and the value will be 
     * the entire function definition (including the name).
     */
    public boolean program() {
        if(functionDefinition()) {
        	Tree<Token> newTree = stack.pop();
        	functions.put(functionName, newTree);
            while(functionDefinition()) {
            	newTree = stack.pop();
            	functions.put(functionName, newTree);

            }
            return true;
        }      
        return false;
       
    }
    /**
     * <function definition> ::= "def" <name> { <parameter> } "=" <expressions> "end"
     * @returns true if it has successfully parsed and made a proper tree. 
     */
    public boolean functionDefinition() {
        if(nextTokenIs("def", TokenType.KEYWORD)) {
            if(nextTokenIs(TokenType.NAME)) {
            	functionName = stack.peek().getValue().getValue();
                makeTree(2,1);
                if(parameter()) {
                    Token sequence = new Token(TokenType.SEQUENCE,"$seq");
                    stack.push(new Tree<Token>(sequence));
                    makeTree(1,2);//======================================CHECK THIS
                    while(parameter()) {
                        makeTree(2,1);
                    } 
                }
                if(stack.size() == 2){
                    makeTree(2,1);
                }
                else{
                    Token sequence = new Token(TokenType.SEQUENCE,"$seq");
                    stack.push(new Tree<Token>(sequence));
                    makeTree(2,1);
                }
                if(nextTokenIs("=",TokenType.SYMBOL)) {
                	stack.pop();
                    if(expressions()){
                    	makeTree(2,1);
                    	if(nextTokenIs("end",TokenType.KEYWORD)){
                    		stack.pop();
                    		return true;
                    	}error("Error in Function Definition: Did not include end statement.");
                    }error("Error in Function Definition: Did not pass expressions()");
                }error("Error in Function Definition: Did not include equal sign.");
            }error("Error in Function Defintion: Check name or parameter");
        }
        
        
        return false;
    }
    /**
     * <expressions> ::= <expression> { "," <expression> }
     * Parsing <expressions> results in a tree whose root is $seq and whose
     * children are the expressions, in the order of occurrence. This is 
     * true even when there is only one expression.
     */
    @SuppressWarnings("unchecked")
	public boolean expressions(){
    	Token token = new Token(TokenType.SEQUENCE,"$seq");
    	stack.push(new Tree<Token>(token));
    	if(expression()){
    		makeTree(2,1);
    		while(nextTokenIs(",",TokenType.SYMBOL)){
    			stack.pop();
    			if(expression()){
    				makeTree(2,1);
    			}
    		}
    		return true;
    	}	
    	
    	return false;
    }
    
    /**
     * <expression> ::= <value definition> | <term> { <add_operator> <term> }
     * @return
     */
    public boolean expression(){
    	if(valueDefinition()){
    		return true;
    	}
    	if(term()){
    		while(addOperator()){
    			if(term()){
    				makeTree(2,3,1);//??????
    			}
    		}
    		return true;
    	}
    	
    	return false;
    }
    /**
     * A <value definition> is a tree with root val, first child is the 
     * (actual) name, and second child is a tree representing the term.
     * <value definition> ::= "val" <name> = <expression>
     * @return
     */
    public boolean valueDefinition(){
    	if(nextTokenIs("val",TokenType.KEYWORD)){
    		if(nextTokenIs(TokenType.NAME)){
    			if(nextTokenIs("=",TokenType.SYMBOL)){
    				stack.pop();
    				if(expression()){
    					makeTree(3,2,1);
    					return true;
    				}error("Error in Value Definition: Did not have an expression at end");
    			}error("Error in Value Definition: Did not include equal sign.");
    		}error("Error in Value Definition: Did not include Name.");
    	}
    	return false;
    }
    
    /**
     * <term> ::= <factor> { <multiply_operator> <factor> }
     * If the term is just one factor, it is represented by the tree for 
     * that factor. Otherwise it is represented by a tree whose root is the
     * last multiply operator, the second child is the last factor,
     * and the right child is the tree representing the rest of the expression.

     * @return
     */
    public boolean term(){
    	if(factor()){
    		while(multiplyOperator()){
    			if(factor()){
    				makeTree(2,3,1);
    			}
    		}
    		return true;
    	}
    	
    	return false;
    }
    
    /**
     * <factor> ::= <name> [ "(" [ <expressions> ] ")" ]
           | "if" <expressions> "then" <expressions> "else" <expressions> "end"
           | <number>
           | "read" <quoted string>
           | "(" <expression> ")"
     */
    public boolean factor(){
    	if(factorName() || factorNumber() || factorIfThenElse()
    			|| factorParenthesizedExpression() || factorRead()){

    		return true;
    	}    	
    	return false;
    }
    
    /**
     * The root read with the quoted string as its single child.
     */
    public boolean factorRead() {
        if(nextTokenIs("read",TokenType.KEYWORD)) {
            if(nextTokenIs(TokenType.STRING)) {
                makeTree(2,1);
                return true;
            }
            error("No string after keyword read");
        }
        
        return false;
    }
    
    /**
     * Pushes onto the stack a tree representing the expression (the parentheses are discarded).
     */
    private boolean factorParenthesizedExpression(){
    	if(nextTokenIs("(",TokenType.SYMBOL)){
    		stack.pop();
    		if(expression()){
    			if (nextTokenIs(")",TokenType.SYMBOL)){
    				stack.pop();
    				return true;
    			}error("Error in Factor: Did not include closing parentheses in Parenthesized Expression");
    		}error("Error in Factor: Did not include expression in Parenthesized Expression");
    	}
    	return false;
    }
    
    /**
     * "if" <expressions> "then" <expressions> "else" <expressions> "end"
     * Pushes onto the stack The root 'if' with three children, each of which is a $seq.
     */
    private boolean factorIfThenElse(){
    	if(nextTokenIs("if",TokenType.KEYWORD)){
    		if(expressions()){
    			if (nextTokenIs("then",TokenType.KEYWORD)){
    				stack.pop();
    				if(expressions()){
    					if(nextTokenIs("else",TokenType.KEYWORD)){
    						stack.pop();
    						if(expressions()){
    							if(nextTokenIs("end",TokenType.KEYWORD)){
    								stack.pop();
    								makeTree(4,3,2,1);
    								return true;
    							}error("Error in Factor: Did not include 'end' at end of If statement");
    						}error("Error in Factor: Did not include expressions at end of If statement");
    					}error("Error in Factor: Did not include 'else' in If statement");
    				}error("Error in Factor: Did not include expressions after 'then' statement");
    			}error("Error in Factor: Did not include 'then' in If statement");
    		}error("Error in Factor: Did not include expressions after 'if' ");
    	}
    	
    	return false;
    }
    
    
    /**
     * Pushes onto the stack a single node containing the number.
     */
    @SuppressWarnings("unchecked")
	private boolean factorNumber(){
    	if(nextTokenIs(TokenType.NUMBER)){
    		Tree<Token> numberTree = stack.pop();
    		double num = Double.parseDouble(numberTree.getValue().getValue());
    		String value = Double.toString(num);
    		Tree<Token> number = new Tree<Token>(new Token(TokenType.NUMBER,value));
    		stack.push(number);
    		return true;
    	}
    	return false;
    }
    
    /**
     * If the name is followed by a "(", then the resultant tree pushed onto the stack consists
     * of the root $call with the actual name as the first child, and a $seq of terms as the second
     * child. Otherwise the tree pushed onto the stack consists of a single node containing the name.
     */
    @SuppressWarnings("unchecked")
	private boolean factorName(){
    	if(nextTokenIs(TokenType.NAME)){

    		if(nextTokenIs("(",TokenType.SYMBOL)){

    			stack.pop();
    			stack.push(new Tree<Token>(new Token(TokenType.CALL,"$call")));
    			if(expressions()){
    				if(nextTokenIs(")",TokenType.SYMBOL)){
    					stack.pop();
    					makeTree(2,3,1);
    					return true;
    				}
    				error("Need to add a closing parentheses after an expression");
    			}
    			if(nextTokenIs(")",TokenType.SYMBOL)){
    				stack.pop();                 

    				makeTree(2,3,1);
    			}
    		}
    		return true;
    	}
    	return false;
    }
    
    /**
     * A single node whose value is "+" or "-".
     */
    public boolean addOperator(){
    	if(nextTokenIs("+",TokenType.SYMBOL) || nextTokenIs("-",TokenType.SYMBOL)){
    		return true;
    	}
    	return false;
    }
    
    /**
     * A single node whose value is "*" or "/".
     * @return
     */
    public boolean multiplyOperator(){
    	if(nextTokenIs("*",TokenType.SYMBOL) || nextTokenIs("/",TokenType.SYMBOL)){
    		return true;
    	}
    	return false;
    }
    
    /**
     *Pushes onto the stack a tree consisting of a single node whose value is the name found in the Funl program. 
     */
    public boolean parameter() {
        if(nextTokenIs(TokenType.NAME)) {
            return true;
        }
        return false;
        
    }
    
    /**
     * Obtains the next token and returns true if that token's value is 
     * equal to the inputted @param value as well as the TokenType
     * If true, it pushes that on the stack. Otherwise, it goes back one token and returns false.
     * @param value - string used to compare values
     * @return - true if it successfully pushed a token onto the stack. 
     */
    @SuppressWarnings("unchecked")
	public boolean nextTokenIs(String value, TokenType type) {
        Token current = tokenizer.next();

        if (current.getType() == TokenType.EOI) {
            return false;
        }
        if(current.getValue().equals(value) && current.getType() == type) {
            stack.push(new Tree<Token>(current));
            return true;
        }
        
        tokenizer.pushBack();
        return false;
          
    }
    
    /**
     * Obtains the next token and returns true if that token's type is 
     * equal to the inputted @param type. If true, it pushes that on the 
     * stack. Otherwise, it goes back one token and returns false.
     * @param type - TokenType being compared
     * @return - true if it successfully pushed a token onto the stack. 
     */
    @SuppressWarnings("unchecked")
	private boolean nextTokenIs(TokenType type) {
        Token current = tokenizer.next();
        if (current.getType() == TokenType.EOI) {
            return false;
        }
        if(current.getType() == type) {
            stack.push(new Tree<Token>(current));
            return true;
        }
        
        tokenizer.pushBack();
        return false;
          
    }
    
    /**
     * Helper method to throw an error message if it has incorrectly parsed a
     * string.
     */
    private void error(String message) {
        throw new RuntimeException(message);
    }
    
    /**
     * Makes a tree by obtaining a root node and then mak
     */
    @SuppressWarnings("unchecked")
	private void makeTree(int rootIndex, int... childIndices) {
        Tree<Token> root = getStackItem(rootIndex);
        for (int i = 0; i < childIndices.length; i++) {
            root.addChildren(getStackItem(childIndices[i]));
        }
        for(int i = 0; i <= childIndices.length; i++) {
            stack.pop();
        }
        stack.push(root);
    }
    
    private Tree<Token> getStackItem(int n){
        return stack.get(stack.size() - n);
    }
    
    public HashMap<String,Tree<Token>> getFunctions() {
        return functions;
    }

}
