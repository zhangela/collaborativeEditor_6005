package Deprecated;
//package DoubleQueueExperiment;
//
//import static org.junit.Assert.*;
//
//import org.junit.Test;
//
//public class DoubleQueueTest {
//    
//    /* Testing ServerRequestDQ */
// // USER|REQ_NUM|DOC_ID|ACTION|BEG|END|CONTENT|VERSION_ID    
//    @Test
//    public void simpleParseTest() {
//        String text = "User 1|0|0|INSERT|0|0|Hello|0";
//        ServerRequestDQ request 
//            = new ServerRequestDQ(text);
//        
//        assertEquals(request.getContent(), "Hello");
//        assertEquals(request.getAction(), "INSERT");
//        assertEquals(request.toString(), text);
//    }
//    
//    @Test
//    public void unaffectedUpdateTest() {
//        ServerRequestDQ request1 
//            = new ServerRequestDQ("User 1|0|0|INSERT|3|0|Hello|0");
//        
//        ServerRequestDQ request2 
//            = new ServerRequestDQ("User 2|0|0|INSERT|2|0|Hello|1");
//        
//        request2.applyUpdate(request1);
//        
//        assertEquals(2, request2.getBeginning());
//    }
//    
//    @Test
//    public void previousInsertUpdateTest() {
//        ServerRequestDQ request1 
//            = new ServerRequestDQ("User 1|0|0|INSERT|0|0|Hello|0");
//        
//        ServerRequestDQ request2 
//            = new ServerRequestDQ("User 2|0|0|INSERT|2|0|Hello|1");
//        
//        request2.applyUpdate(request1);
//        
//        assertEquals(7, request2.getBeginning());
//    }
//
//}
