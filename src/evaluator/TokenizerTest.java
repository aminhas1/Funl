package evaluator;

import static org.junit.Assert.*;

import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;

public class TokenizerTest {
    Tokenizer test;
    @Before
    public void setUp() throws Exception {
        String stringToBeTokened = "<while loop> ::= \"while\" <condition> statement | while <condition> \\{ statement \\}.";
       String string = "foo()";
        StringReader read = new StringReader(stringToBeTokened);
        test = new Tokenizer(string);

    }

    @Test
    public void testTokenizerReader() {
        int i = 40;
        while (i >= 0) {
            Token current = test.next();
            System.out.println(current);
            i --;
        }
    }

    @Test
    public void testTokenizerString() {
        fail("Not yet implemented");
    }

    @Test
    public void testNext() {
        fail("Not yet implemented");
    }

    @Test
    public void testPushBack() {
        fail("Not yet implemented");
    }

}
