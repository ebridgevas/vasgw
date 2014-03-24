//package com.ebridgecommerce.playground;
//
//import org.junit.Test;
//
//import java.util.Iterator;
//
//import static org.mockito.Mockito.*;
//import static org.junit.Assert.*;
//
///**
// * Created with IntelliJ IDEA.
// * User: David
// * Date: 6/28/12
// * Time: 6:15 PM
// * To change this template use File | Settings | File Templates.
// */
//public class MickitoTest {
//
//    @Test
//    public void testReturnHelloWorld(){
//
//        // arrange
//        Iterator i = mock(Iterator.class);
//        when(i.next()).thenReturn("Hello").thenReturn("World");
//
//        // act
//        String result = i.next() + " " + i.next();
//
//        // assert
//        assertEquals("Hello World", result);
//    }
//
//    @Test
//    public void testArguments(){
//        Comparable c = mock(Comparable.class);
//        when(c.compareTo("Test")).thenReturn(1);
//        assertEquals(1, c.compareTo("Test"));
//    }
//}
