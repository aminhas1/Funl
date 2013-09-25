package evaluator;
/**
 * @author Abeer Minhas
 */
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

public class TreeTest {
    Tree<String> firstTree;
    Tree<String> sTree;
    Tree<String> secondTree;
    
    @Before
    public void setUp() throws Exception {
        firstTree = new Tree<String>("first");
        secondTree = new Tree<String>("second");
        sTree = new Tree<String>("sRoot",firstTree, secondTree);
        

    }
    
    @Test
    public void testTreeConstructor(){
        Tree<Integer> numbers = new Tree<Integer>(1);
        assertTrue(numbers.getValue() == 1);
        numbers.setValue(5);
        assertTrue(numbers.getValue() == 5);
        Tree<Integer> numbers2 = new Tree<Integer>(2, numbers);
        assertTrue(numbers2.getValue() == 2);
        assertTrue(numbers2.getChild(0).getValue() == 5);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testTreeConstructorIllegal() {
        Tree<String> test = new Tree<String>("Fail",firstTree, null);
        assertFalse(test.getValue().equals("first"));

    }
    
    @Test(expected=IndexOutOfBoundsException.class)
    public void testTreeConstructor2(){
        Tree<Integer> numbers = new Tree<Integer>(1);
        numbers.getChild(0);
    }
    
    @Test 
    public void testSetValue(){
        firstTree.setValue("Yummy");
        assertFalse(firstTree.getValue().equals("first"));
        assertTrue(firstTree.getValue().equals("Yummy"));
    }
    
    @Test
    public void testGetValue(){
        assertTrue(firstTree.getValue().equals("first"));
        assertTrue(secondTree.getValue().equals("second"));
    }
    
    
    @Test
    public void testAddChild(){
        Tree<String> thirdTree = new Tree<String>("third");
        sTree.addChild(1, thirdTree);
        assertTrue(sTree.numberOfChildren() == 3);
        Tree<String> fourthTree = new Tree<String>("fourth");
        Tree<String> fifthTree = new Tree<String>("fifth");

        sTree.addChild(2, fourthTree);
        assertTrue(sTree.numberOfChildren() == 4);
        assertTrue(sTree.getChild(2) == fourthTree);
        assertTrue(sTree.getChild(1) == thirdTree);
        assertTrue(sTree.getChild(0) == firstTree);
        assertFalse(sTree.getChild(0) == fourthTree);

        assertTrue(sTree.getChild(3) == secondTree);
        sTree.addChild(2, fifthTree);
        assertTrue(sTree.getChild(1) == thirdTree);
        assertTrue(sTree.getChild(2) == fifthTree);     
    }
    
    @Test (expected=IllegalArgumentException.class)
    public void testAddChildIllegal(){
        secondTree.addChild(1,null);

    }
 
    @Test
    public void testParent(){
        Tree<String> Parent = new Tree<String>("Dad");
        assertFalse(Parent.getHasParent());
        Parent.setHasParent();
        assertTrue(Parent.getHasParent());
        Tree<String> Daughter = new Tree<String>("Abeer");
        Parent.addChild(0,Daughter);
        assertTrue(Daughter.getParent().equals(Parent));
        
    }

    @Test
    public void testGetChildren(){
        ArrayList<Tree<String>> children = sTree.getChildren();
        assertTrue(children.get(0).equals(firstTree));
        assertFalse(children.get(0).equals(secondTree));
        assertTrue(children.get(1).equals(secondTree));

    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testAddChildren(){
        Tree<String> child1 = new Tree<String>("Child1");
        Tree<String> child2 = new Tree<String>("Child2");
        Tree<String> child3 = new Tree<String>("Child3");
        Tree<String> child4 = new Tree<String>("Child4");
        child3.addChild(0,child4);
        Tree<String> Parent = new Tree<String>("Parent");
        Parent.addChildren(child1,child2,child3);

        assertTrue(Parent.getChild(0).equals(child1));

        assertTrue(Parent.getChild(1).equals(child2));
        assertFalse(Parent.getChild(1).equals(child3));
        assertTrue(Parent.getChild(2).equals(child3));
        assertTrue(Parent.contains(child4));
    }
    
    @SuppressWarnings("unchecked")
    @Test (expected=IllegalArgumentException.class)
    public void testAddChildrenIllegal(){
        Tree<String> child1 = new Tree<String>("Child1");
        Tree<String> child2 = new Tree<String>("Child2");
        Tree<String> child3 = new Tree<String>("Child3");
        Tree<String> Parent = new Tree<String>("Parent");
        Parent.addChildren(child1,child2,child3);
        child1.addChildren(Parent,child3);
    }
    
    @Test
    public void testGetNumberOfChildren(){
        assertTrue(sTree.numberOfChildren() == 2);
        Tree<String> newChild = new Tree<String>("Abeer");
        sTree.addChild(2, newChild);
        assertTrue(sTree.numberOfChildren() == 3);
    }
    
    @Test
    public void testHasChildren(){
        assertTrue(sTree.hasChildren());
        assertFalse(secondTree.hasChildren());
        Tree<String> newKid = new Tree<String>("new");
        secondTree.addChild(0, newKid);
        assertTrue(secondTree.hasChildren());
    }
    
    @Test
    public void testGetChild(){
        assertTrue(sTree.getChild(0).equals(firstTree));
        assertTrue(sTree.getChild(1).equals(secondTree));   
    }
    
    @Test
    public void testIterator(){
        Iterator<Tree<String>> iterate = sTree.iterator();
        assertTrue(iterate.hasNext());
        assertTrue(iterate.next().getValue().equals("first"));
        assertTrue(iterate.hasNext());
        assertTrue(iterate.next().getValue().equals("second"));
        assertFalse(iterate.hasNext());

    }
    @SuppressWarnings("unchecked")
    @Test
    public void testContains(){
//      assertTrue(sTree.contains(sTree));
        Tree<String> tTree = new Tree<String>("sRoot",firstTree, secondTree);

        Tree<String> fourthTree = new Tree<String>("fourth");
        Tree<String> fifthTree = new Tree<String>("fifth");
        

        secondTree.addChildren(fourthTree,fifthTree);

        assertTrue(sTree.contains(firstTree));
        assertTrue(sTree.contains(secondTree));
        assertTrue(sTree.contains(fifthTree));
        assertFalse(sTree.contains(tTree));
        assertFalse(secondTree.contains(firstTree));
        assertTrue(tTree.contains(tTree));
        assertTrue(tTree.contains(secondTree));
        assertTrue(tTree.contains(fourthTree));

    
}
    
    @SuppressWarnings("unchecked")
    @Test
    public void testToString(){
        String tree1 = sTree.toString();
        String parse = ("sRoot(first second)");
        Tree<String> parsed = Tree.parse(parse);
        String tree2 = parsed.toString();
        assertTrue(tree1.equals(tree2));
        
        Tree<String> tTree = new Tree<String>("sRoot",firstTree, secondTree);
        Tree<String> fourthTree = new Tree<String>("fourth");
        Tree<String> fifthTree = new Tree<String>("fifth");
        Tree<String> eTree = new Tree<String>("eight");
        Tree<String> fTree = new Tree<String>("f");
        Tree<String> n = new Tree<String>("n");
        Tree<String> b = new Tree<String>("b");
        Tree<String> c = new Tree<String>("c");
        Tree<String> sixthTree = new Tree<String>("sixth");
        Tree<String> seventhTree = new Tree<String>("seventh");     
        secondTree.addChildren(fourthTree,fifthTree);
        firstTree.addChildren(sixthTree, seventhTree);
        sixthTree.addChildren(n);
        fTree.addChildren(b,c);
        tTree.addChildren(eTree, fTree);
        
        String tree4 = tTree.toString();
        String parse2 = "sRoot( first( sixth( n) seventh) second( fourth fifth) eight f( b c))";
        Tree<String> parsed2 = Tree.parse(parse2);
        String tree3 = parsed2.toString();
        assertTrue(parsed2.equals(tTree));
        assertTrue(tree3.equals(tree4));
    }

    @Test
    public void testGetGrandParent(){
        Tree<String> gchild = new Tree<String>("Yes");
        Tree<String> ggchild = new Tree<String>("Yes 2");
        Tree<String> gggchild = new Tree<String>("Yes 3");
        secondTree.addChild(0,gchild);
        gchild.addChild(0, ggchild);
        ggchild.addChild(0,gggchild);
        assertTrue(Tree.getGrandParent(1,gchild).equals(sTree));
        assertTrue(Tree.getGrandParent(1,ggchild).equals(secondTree));
        assertTrue(Tree.getGrandParent(2,ggchild).equals(sTree));
        assertTrue(Tree.getGrandParent(2,gggchild).equals(secondTree));
        assertTrue(Tree.getGrandParent(3,gggchild).equals(sTree));
        assertFalse(Tree.getGrandParent(2, ggchild).equals(gchild));
    }
    

    
    @SuppressWarnings("unchecked")
    @Test
    public void testParse(){
        //Creating a nasty tree to ensure it matches with string version
        Tree<String> tTree = new Tree<String>("sRoot",firstTree, secondTree);
        Tree<String> fourthTree = new Tree<String>("fourth");
        Tree<String> fifthTree = new Tree<String>("fifth");
        Tree<String> eTree = new Tree<String>("eight");
        Tree<String> fTree = new Tree<String>("f");
        Tree<String> n = new Tree<String>("n");
        Tree<String> b = new Tree<String>("b");
        Tree<String> c = new Tree<String>("c");
        Tree<String> sixthTree = new Tree<String>("sixth");
        Tree<String> seventhTree = new Tree<String>("seventh");     
        secondTree.addChildren(fourthTree,fifthTree);
        firstTree.addChildren(sixthTree, seventhTree);
        sixthTree.addChildren(n);
        fTree.addChildren(b,c);
        tTree.addChildren(eTree, fTree);
    
        
        //String version of nasty tree: 
        String parse = "sRoot( first( sixth( n) seventh) second( fourth fifth) eight f( b c))";
        Tree<String> parsedTree = Tree.parse(parse);
        assertTrue(tTree.equals(parsedTree));       
        assertTrue(parsedTree.getValue().equals("sRoot"));
        assertTrue(parsedTree.getChild(0).getValue().equals("first"));
        assertTrue(parsedTree.getChild(1).getValue().equals("second"));
        assertTrue(parsedTree.getChild(0).getChild(0).getValue().equals("sixth"));
        assertFalse(parsedTree.getChild(0).getChild(1).getValue().equals("n"));
        assertTrue(parsedTree.getChild(0).getChild(1).getValue().equals("seventh"));
        
        String parse2 = "one(two three(four five(six seven eight))     )";
        Tree<String> parsedTree2 = Tree.parse(parse2);
        
        Tree<String> testTree = new Tree<String>("one");
        assertFalse(testTree.equals(parsedTree2));
        assertTrue(parsedTree2.getValue().equals("one"));
        assertTrue(parsedTree2.getChild(0).getValue().equals("two"));
        assertTrue(parsedTree2.getChild(1).getValue().equals("three"));
        assertTrue(parsedTree2.getChild(1).getChild(0).getValue().equals("four"));
        assertTrue(parsedTree2.getChild(1).getChild(1).getValue().equals("five"));
        assertTrue(parsedTree2.getChild(1).getChild(1).getChild(2).getValue().equals("eight"));


        
    }
    
    @Test
    public void testAddSpaces(){
        String str = "((()))";
        str = Tree.addSpaces(str);
        assertTrue(str.equals(" (  (  (  )  )  ) "));
        String str2 = "(A ( B (E(E)R))))";
        str2 = Tree.addSpaces(str2);
        assertTrue(str2.equals(" ( A  (  B  ( E ( E ) R )  )  )  ) "));

    }       

    @Test
    public void testEquals(){
        Tree<String> mTree = new Tree<String>("sRoot", firstTree, secondTree);
        assertTrue(mTree.equals(sTree));
        assertFalse(mTree.equals(secondTree));
        Tree<String> first = new Tree<String>("first");
        assertTrue(first.equals(firstTree));
        Tree<String> second = new Tree<String>("second");
        first.addChild(0, second);
        assertFalse(first.equals(firstTree));
        firstTree.addChild(0,secondTree);
        assertTrue(first.equals(firstTree));
    }


    
}