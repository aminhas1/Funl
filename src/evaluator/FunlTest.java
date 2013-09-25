package evaluator;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

public class FunlTest {
    private Parser parser;

	private Funl funl = new Funl();
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testIsOperator() {
		String plus = "+";
		String minus = "-";
		String divide = "/";
		String multiply = "*";
		assertTrue(funl.isOperator(plus));
		assertTrue(funl.isOperator(minus));
		assertTrue(funl.isOperator(divide));
		assertTrue(funl.isOperator(multiply));		
		assertFalse(funl.isOperator("Not"));
	}
	
	/*
	 * The following tests test both eval and define. 
	 */
	@Test
	public void testEQ(){
		String eq ="def eq x y = if x - y then 0 else if y - x then 0 else 1 end end end";
		funl.define(eq);
		use("eq(10,10)");
		Tree<Token> complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"1.0");
		use("eq(5,10)");
		complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"0.0");
		use("eq(5,10)");
		complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"0.0");
	}
	
	@Test
	public void testArithmetic(){
		use("5+5+5");
		Tree<Token> complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"15.0");
		
		use("10*5-20");
		complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"30.0");
		
		use("100/2*2");
		complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"100.0");
		
		use("100-10-10-10");
		complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"70.0");
	}
	
	@Test
	public void testGT(){
		String gt = "def gt x y = x - y end";
		funl.define(gt);
		use("gt(10,5)");
		Tree<Token> complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"5.0");
		use("gt(10,10)");
		complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"0.0");
	}
	
	@Test
	public void testLT(){
		String lt = "def lt x y = y - x end";
		funl.define(lt);
		use("lt(5,10)");
		Tree<Token> complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"5.0");
		use("lt(10,10)");
		complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"0.0");
	}
	
	@Test
	public void testGE(){
		String ge = "def eq x y = if x - y then 0 else if y - x then 0 else 1 end end end " +
				"def gt x y = x - y end def lt x y = y - x end def ge x y = if gt(x, y) " +
				"then 1 else if eq(x, y) then 1 else 0 end end end";
		funl.define(ge);
		use("ge(10,5)");
		Tree<Token> complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"1.0");
		use("ge(10,1)");
		complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"1.0");
		use("ge(1,10)");
		complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"0.0");
	}
	
	@Test
	public void testLE(){
		String le = "def lt x y = y - x end def eq x y = if x - y then 0 else if y - x then 0 else 1 end end end " +
				"def le x y = if lt(x, y) then 1 else if eq(x, y) then 1 else 0 end end end";
		funl.define(le);
		use("le(5,10)");
		Tree<Token> complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"1.0");
		use("le(52,10)");
		complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"0.0");
	}
	
	@Test
	public void testNE(){
		String ne = "def eq x y = if x - y then 0 else if y - x then 0 else 1 end end end" +
				" def ne x y = if eq(x, y) then 0 else 1 end end";
		funl.define(ne);
		use("ne(5,10)");
		Tree<Token> complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"1.0");
		use("ne(5,5)");
		complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"0.0");
	}
	
	@Test
	public void testFactorial(){
		String fact = "def factorial n = if n - 1 then n * factorial(n - 1) else 1 end end";
		funl.define(fact);
		use("factorial(4)");
		Tree<Token> complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"24.0");
		use("factorial(10)");
		complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"3628800.0");
	}
	
	@Test
	public void testOR(){
		String or = "def or x y = if x then x else y end end";
		funl.define(or);
		use("or(1,9)");
		Tree<Token> complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"1.0");
		use("or(0,9)");
		complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"9.0");
	}
	
	@Test
	public void testFib(){
		String fibonacci = "def or x y = if x then x else y end end" +
				" def fibonacci n = if or(eq(n, 1), eq(n, 2)) then 1 else" +
				" fibonacci(n - 1) + fibonacci(n - 2) end end " +
				"def eq x y = if x - y then 0 else if y - x then 0 else 1 end end end";
		funl.define(fibonacci);
		use("fibonacci(5)");
		Tree<Token> complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"5.0");
		use("fibonacci(10)");
		complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"55.0");
	}
	
	@Test
	public void testAbs(){
		String abs = "def abs x = if x then x else 0 - x end end";
		funl.define(abs);
		use("abs(5)");
		Tree<Token> complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"5.0");
		use("abs(0-5)");
		complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"5.0");
	}
	
	@Test
	public void testNearly(){
		String nearlyEqual = "def abs x = if x then x else 0 - x end end" +
				" def nearly_equal x y = lt(abs(x - y), 0.00001) end def lt x y = y - x end";
		funl.define(nearlyEqual);
		use("nearly_equal(5,4)");
		Tree<Token> complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"-0.99999");
	}
	
	@Test
	public void testSqrt(){
		String sqrt = "def sqrt x = sqrt_helper(x, 1) end def abs x = if x then x else 0 - x end end" +
				" def nearly_equal x y = lt(abs(x - y), 0.00001) end def lt x y = y - x end " +
				"def sqrt_helper x y = val y_squared = y * y, if nearly_equal(x, y_squared) then y " +
				"else val next_guess = (y + x / y) / 2, sqrt_helper(x, next_guess) end end";
		funl.define(sqrt);
		use("sqrt(100)");
		Tree<Token> complete = funl.eval(parser.stack.peek());
		double value = Double.parseDouble(complete.getValue().getValue());
		int intValue = (int) value;
		assertEquals(10,intValue);
		
		use("sqrt(81)");
		complete = funl.eval(parser.stack.peek());
		value = Double.parseDouble(complete.getValue().getValue());
		intValue = (int) value;
		assertEquals(9,intValue);
	}
	
	@Test
	public void testPower(){
		String power = "def power x n = if eq(n, 1) then x else x * power(x, n - 1) end end" +
				" def eq x y = if x - y then 0 else if y - x then 0 else 1 end end end";
		funl.define(power);
		use("power(2,10)");
		Tree<Token> complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"1024.0");
	}
	
	@Test
	public void testIdentity(){
		String iden = "def identity x = x end";
		funl.define(iden);
		use("identity(5)");
		Tree<Token> complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"5.0");

	}
	
	@Test
	public void testArithmeticFunctions(){
		String arith = "def add x y = x + y end def multiply x y = x * y end " +
				"def square x = multiply(x, x) end";
		funl.define(arith);
		use("add(5,3)");
		Tree<Token> complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"8.0");
		
		use("add(0-5,2)");
		complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"-3.0");
		
		use("multiply(5,4)");
		complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"20.0");
		
		use("square(0-5)");
		complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"25.0");
	}
	
	@Test
	public void testFor(){
		String forLoop = "def for first last apply combine = val init = apply(first), " +
				"if ge(first, last) then init else combine(init, for((first +1), " +
				"last, apply, combine)) end end def eq x y = if x - y then 0 else if y - x then 0 else 1 end end end " +
				"def gt x y = x - y end def lt x y = y - x end def ge x y = if gt(x, y) " +
				"then 1 else if eq(x, y) then 1 else 0 end end end def add x y = x + y end " +
				"def abs x = if x then x else 0 - x end end";
		funl.define(forLoop);
		use("for(0 - 10, 10, abs, add)");
		Tree<Token> complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"110.0");
	}
	
	@Test
	public void testFactorial2(){
		String fact = "def factorial2 n = for(1, n, identity, multiply) end def for " +
				"first last apply combine = val init = apply(first), " +
				"if ge(first, last) then init else combine(init, for((first +1), " +
				"last, apply, combine)) end end def eq x y = if x - y then 0 else if y - x then 0 else 1 end end end " +
				"def gt x y = x - y end def lt x y = y - x end def ge x y = if gt(x, y) " +
				"then 1 else if eq(x, y) then 1 else 0 end end end def add x y = x + y end " +
				"def multiply x y = x * y end def identity x = x end";
		funl.define(fact);
		use("factorial2(4)");
		Tree<Token> complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"24.0");
	}
	
	@Test
	public void testSum(){
		String sum = "def sum_of_1_to_n function n = if eq(n, 0) then 0 " +
				"else function(n) + sum_of_1_to_n(function, n - 1) end end def square x = x * x end " +
				"def eq x y = if x - y then 0 else if y - x then 0 else 1 end end end";
		funl.define(sum);
		use("sum_of_1_to_n(square, 10)");
		Tree<Token> complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"385.0");
		
	}
	
	@Test
	public void testSumSquares(){
		String sum = "def sum_of_squares first last = for(first, last, square, add) end " +
				"def for first last apply combine = val init = apply(first), " +
				"if ge(first, last) then init else combine(init, for((first +1), " +
				"last, apply, combine)) end end def eq x y = if x - y then 0 else if y - x then 0 else 1 end end end " +
				"def gt x y = x - y end def lt x y = y - x end def ge x y = if gt(x, y) " +
				"then 1 else if eq(x, y) then 1 else 0 end end end def add x y = x + y end " +
				"def abs x = if x then x else 0 - x end end def square x = x * x end";
		funl.define(sum);
		use("sum_of_squares(1, 10)");
		Tree<Token> complete = funl.eval(parser.stack.peek());
		assertEquals(complete.getValue().getValue(),"385.0");
	}
	

	@SuppressWarnings("static-access")
	@Test
	public void testDefine() {
        String sum = "def sum_of_squares first last = for(first, last, square, add) end " +
				"def for first last apply combine = val init = apply(first), " +
				"if ge(first, last) then init else combine(init, for((first +1), " +
				"last, apply, combine)) end end def eq x y = if x - y then 0 else if y - x then 0 else 1 end end end " +
				"def gt x y = x - y end def lt x y = y - x end def ge x y = if gt(x, y) " +
				"then 1 else if eq(x, y) then 1 else 0 end end end def add x y = x + y end " +
				"def abs x = if x then x else 0 - x end end def square x = x * x end";
		funl.define(sum);
		assertEquals(9,funl.functions.size());
	
	}


	
    private void use(String s) {
        parser = new Parser(s);
        parser.expression();
    }
    
}
