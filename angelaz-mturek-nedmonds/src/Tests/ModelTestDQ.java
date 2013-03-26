package Tests;

/*
 * Since the actual delays and lags in server-client communication 
 * on local network / even the Internet are extremely unpredictable
 * and depend on many different inputs we cannot affect, it is not 
 * ideal to use the same interface for testing as we are in the actual 
 * implementation. For testing, we decided to circumvent all network
 * communication and implement a server simulator that simulates and drives
 * all the events that happen when one actually runs the program but in
 * a much more organized and controlled manner.
 * 
 * Events are matched to function calls in the following manner:
 * UserDQ.createRequest(...) -> simulates a user making a local change in his GUI
 * UserDQ.pullRequest() -> simulates request sent from user
 * UserDQ.pushRequest() -> simulates message from server received by user
 * 
 * Server processing is not done automatically since the server is
 * tested separately in SEditTest. Here, server processing of messages
 * is done manually.

 * In real use, the difference between pullRequest and the server processing
 * (and in turn between server processing and pushRequest), for example, 
 * is determined by the lag of the network. Here, we can time and order 
 * them as we desire to simulate concurrent changes and lagging updates.
 * 
 * We also made heavy use of the routers in Number 6 broken by last week's
 * power outage which allowed us to test our system in a real-world environment
 * with substantial lagging. All out tests still passed.
 *
 *
 * We have implemented the following test cases:
 * 
 * 1) simpleInsertion():
 * Description: Single user connected to ABCD, inserts X after A
 * 
 * Desired output: User 1: AXBCD
 * 
 * 2) concurrentInsertion() 
 * Description: 
 * Two users connected to ABCD
 * U1 inserts X after A, U2 inserts Y after C
 * Requests happen at the same time and 
 * only get processed afterwards
 * 
 * Desired output:
 * Both users: AXBCYD
 * 
 * 3) concurrentInsertionInTheSamePlace()
 * Description:
 * Two users connected to ABCD
 * U1 inserts X after A, U2 inserts Y after A
 * Requests happen at the same time and 
 * only get processed afterwards, request made by
 * user 1 gets processed first
 * 
 * Desired output:
 * Both users: AYXBCD
 * 
 * 4) interleavingInsertions() 
 * Description:
 * Two users connected to ABCD
 * U1 inserts X after A (1), then Y after X (2),
 * U2 inserts Z after C (3), then K after Z (4)
 *  
 * U1 places both requests 1 and 2, while U2 places 3
 * Time t=1: Requests get processed by server in order 1, 3, 2
 * Time t=2: U2 receives request 1 from server
 * Time t=3: U2 places request 4
 * Time t=4: Server processes request 4
 * Time t=5: All remaining requests received and processed in order
 * 
 * Ending serverHistory: r1, r3, r2, r4
 * 
 * Desired output:
 * Both users: AXYBCZKD
 * 
 * 5) singleUserDeletion()
 * Description:
 * Single user connected to ABCD
 * inserts X after B -> ABXCD (INSERT X at 2)
 * deletes B through C -> AD (DELETE 1 through 4)
 * inserts Y after A -> AYD (INSERT Y at 1)
 * gets responses for first 2 events
 * inserts Z after Y -> AYZD (INSERT Z at 2)
 * get the remaining responses
 * 
 * Desired output:
 * User: AYZD
 * 
 * 6) tripleUserDeletion() 
 * Three users connected to ABCDE
 * R1: user 1 deletes BC -> sees ADE (DELETE 1, 3)
 * R2: user 2 deletes CD -> sees ABE (DELETE 2, 4)
 * R3: user 3 deletes BCD -> sees AE (DELETE 1, 4)
 * 
 * requests get processed in order
 *  
 * User views:
 * U1: R1->ADE, R2->AE, R3->AE
 * U2: R1->AE, R2->AE, R3->AE
 * U3: R1->AE, R2->AE, R3->AE
 * 
 * 7) multipleDocsTest()
 * Two users connected to two documents,
 * the documents shouldn't affect each other.
 * 
 * 1) ABCD gets loaded into document 0
 * 2) User2 inserts Y into document 1
 * 3) User1 inserts X after A in document 0
 * 4) requests get processed and displayed and
 *    both users hold AXBCD in one and Y in the other document
 * 
 * 
 * ----------------------------------------------------------------
 * The point of the test cases described above is to push
 * the concurrency capabilities of the implementation to
 * the limits with many changes happening at the same time
 * without knowing about other changes made by other users.
 * As shown above, the current implementation can handle
 * extreme lags when one client only gets the information
 * about the document changing when other clients made many
 * mutually interfering changes.
 */

import static org.junit.Assert.*;

import org.junit.Test;

import Model.UserDQ;

public class ModelTestDQ {
    
    /** 
     * Single user gets synchronized at ABCD,
     * inserts X after A, and at a later point
     * receives his update back from the server 
     */
    @Test
    public void simpleInsertion() {
        UserDQ user1 = new UserDQ("User1");
        user1.addDocument(0);
        
        // After startup, user sees an empty String
        assertEquals("", user1.getView(0));
        
        // Load the initial state: ABCD
        user1.pushRequest("LoadUser|0|0|INSERT|0|3|ABCD|1");
        assertEquals("ABCD", user1.getView(0));
        
        // User inserts X after A in his GUI 
        // but his syncCopy remains unchanged
        user1.createRequest(0, "INSERT|1|0|X");
        assertEquals("AXBCD", user1.getView(0));
        assertEquals("ABCD", user1.getSyncCopy(0));
        
        // User sends his request to the server. 
        // It should read: "User1|0|0|INSERT|1|0|X|1"
        String request = user1.pullRequest();
        assertEquals("User1|0|0|INSERT|1|0|X|1", request);
        
        // Server is processing the request
        
        // User receives his change back:
        user1.pushRequest("User1|0|0|INSERT|1|0|X|2");
        
        // User should both see modified state AXBCD and hold it as syncCopy
        assertEquals("AXBCD", user1.getView(0));
        assertEquals("AXBCD", user1.getSyncCopy(0));
    }
    
   /** Two users connected and synchronized at ABCD
    *  U1 inserts X after A, U2 inserts Y after C
    *  Both requests happen exactly at the same time
    *  while neither of the user has heard back from 
    *  the server about any update
    * 
    *  The server then processes the requests in order
    *  (user 1, then user 2) and distributes the changes
    *  to the users.
    */
    @Test
    public void concurrentInsertion() {
        UserDQ user1 = new UserDQ("User1");
        UserDQ user2 = new UserDQ("User2");

        user1.addDocument(0);
        user2.addDocument(0);

        // After startup, both users sees an empty String
        assertEquals("", user1.getView(0));
        assertEquals("", user2.getView(0));

        // Both users load the initial state: ABCD
        user1.pushRequest("LoadUser|0|0|INSERT|0|3|ABCD|1");
        user2.pushRequest("LoadUser|0|0|INSERT|0|3|ABCD|1");
        assertEquals("ABCD", user1.getView(0));
        assertEquals("ABCD", user2.getView(0));

        // User1 inserts X after A in his GUI 
        // but his syncCopy remains unchanged
        user1.createRequest(0, "INSERT|1|0|X");
        assertEquals("AXBCD", user1.getView(0));
        assertEquals("ABCD", user1.getSyncCopy(0));

        // User2 inserts Y after C in her GUI 
        // but her syncCopy remains unchanged
        user2.createRequest(0, "INSERT|3|0|Y");
        assertEquals("ABCYD", user2.getView(0));
        assertEquals("ABCD", user2.getSyncCopy(0));

        // User1 sends his request to the server. 
        String request1 = user1.pullRequest();
        assertEquals("User1|0|0|INSERT|1|0|X|1", request1);

        // User1 receives his change back and should 
        // still see AXBCD but now it's also his syncCopy
        user1.pushRequest("User1|0|0|INSERT|1|0|X|2");
        assertEquals("AXBCD", user1.getView(0));
        assertEquals("AXBCD", user1.getSyncCopy(0));

        // User2 receives User1's change and should now 
        // see AXBCYD while holding AXBCD
        user2.pushRequest("User1|0|0|INSERT|1|0|X|2");
        assertEquals("AXBCYD", user2.getView(0));
        assertEquals("AXBCD", user2.getSyncCopy(0));

        // User2 sends his request to the server. 
        String request2 = user2.pullRequest();
        assertEquals("User2|0|0|INSERT|3|0|Y|1", request2);

        // User1 receives the change made by User2
        // and should see AXBCYD
        user1.pushRequest("User2|0|0|INSERT|4|0|Y|3");
        assertEquals("AXBCYD", user1.getView(0));
        assertEquals("AXBCYD", user1.getSyncCopy(0));

        // User2 receives his change back and should 
        // now both see and hold AXBCYD
        user2.pushRequest("User2|0|0|INSERT|4|0|Y|3");
        assertEquals("AXBCYD", user2.getView(0));
        assertEquals("AXBCYD", user2.getSyncCopy(0));
    }
    
    /** Two users connected and synchronized to ABCD
     *  U1 inserts X after A, U2 inserts Y after A
     *  Requests happen at the same time and 
     *  only get processed afterwards, request made by
     *  user 1 gets processed first which means that
     *  the final version should be: AYXBCD by the
     *  desired behavior. 
     */
    @Test
    public void concurrentInsertionInTheSamePlace() {
        UserDQ user1 = new UserDQ("User1");
        UserDQ user2 = new UserDQ("User2");

        user1.addDocument(0);
        user2.addDocument(0);

        // After startup, both users see an empty String
        assertEquals("", user1.getView(0));
        assertEquals("", user2.getView(0));

        // Both users load the initial state: ABCD
        user1.pushRequest("LoadUser|0|0|INSERT|0|3|ABCD|1");
        user2.pushRequest("LoadUser|0|0|INSERT|0|3|ABCD|1");
        assertEquals("ABCD", user1.getView(0));
        assertEquals("ABCD", user2.getView(0));

        // User1 inserts X after A in his GUI 
        // but his syncCopy remains unchanged
        user1.createRequest(0, "INSERT|1|0|X");
        assertEquals("AXBCD", user1.getView(0));
        assertEquals("ABCD", user1.getSyncCopy(0));

        // User2 inserts Y after A in her GUI 
        // but her syncCopy remains unchanged
        user2.createRequest(0, "INSERT|1|0|Y");
        assertEquals("AYBCD", user2.getView(0));
        assertEquals("ABCD", user2.getSyncCopy(0));

        // User1 sends his request to the server. 
        String request1 = user1.pullRequest();
        assertEquals("User1|0|0|INSERT|1|0|X|1", request1);

        // User1 receives his change back and should 
        // still see AXBCD but now it's also his syncCopy
        user1.pushRequest("User1|0|0|INSERT|1|0|X|2");
        assertEquals("AXBCD", user1.getView(0));
        assertEquals("AXBCD", user1.getSyncCopy(0));

        // User2 receives User1's change and should now 
        // see AYXBCD while holding AXBCD
        user2.pushRequest("User1|0|0|INSERT|1|0|X|2");
        assertEquals("AYXBCD", user2.getView(0));
        assertEquals("AXBCD", user2.getSyncCopy(0));

        // User2 sends his request to the server. 
        String request2 = user2.pullRequest();
        assertEquals("User2|0|0|INSERT|1|0|Y|1", request2);

        // User1 receives the change made by User2
        // and should see AXBCYD
        user1.pushRequest("User2|0|0|INSERT|1|0|Y|3");
        assertEquals("AYXBCD", user1.getView(0));
        assertEquals("AYXBCD", user1.getSyncCopy(0));

        // User2 receives his change back and should 
        // now both see and hold AXBCYD
        user2.pushRequest("User2|0|0|INSERT|1|0|Y|3");
        assertEquals("AYXBCD", user2.getView(0));
        assertEquals("AYXBCD", user2.getSyncCopy(0));
    }

    /** Two users synchronized at ABCD
     * U1 inserts X after A (R1), then Y after X (R2),
     * U2 inserts Z after C (R3), then K after Z (R4)
     * 
     * Time t=0: U1 places both requests 1 and 2, while U2 places 3
     * Time t=1: Requests get processed by server in order 1, 3, 2
     * Time t=2: U2 receives request 1 from server
     * Time t=3: U2 places request 4
     * Time t=4: Server processes request 4
     * Time t=5: All remaining requests received and processed in order
     * 
     * 
     * serverHistory: r1, r3, r2, r4
     */
    @Test
    public void interleavingInsertions() {
        UserDQ user1 = new UserDQ("User1");
        UserDQ user2 = new UserDQ("User2");

        user1.addDocument(0);
        user2.addDocument(0);

        // After startup, both users see an empty String
        assertEquals("", user1.getView(0));
        assertEquals("", user2.getView(0));

        // Both users load the initial state: ABCD 
        // which brings them up to version 1
        user1.pushRequest("LoadUser|0|0|INSERT|0|3|ABCD|1");
        user2.pushRequest("LoadUser|0|0|INSERT|0|3|ABCD|1");
        assertEquals("ABCD", user1.getView(0));
        assertEquals("ABCD", user2.getView(0));

        // Time t=0:
        // User1 inserts X after A
        user1.createRequest(0, "INSERT|1|0|X");
        assertEquals("AXBCD", user1.getView(0));
        assertEquals("ABCD", user1.getSyncCopy(0));
        
        // User1 inserts Y after X
        user1.createRequest(0, "INSERT|2|0|Y");
        assertEquals("AXYBCD", user1.getView(0));
        assertEquals("ABCD", user1.getSyncCopy(0));
        
        // User2 inserts Z after C
        user2.createRequest(0, "INSERT|3|0|Z");
        assertEquals("ABCZD", user2.getView(0));
        assertEquals("ABCD", user2.getSyncCopy(0));

        // time t=1:
        // Requests 1, 3, 2 on the way:
        String request1 = user1.pullRequest();
        String request3 = user2.pullRequest();
        String request2 = user1.pullRequest();
        assertEquals("User1|0|0|INSERT|1|0|X|1", request1);
        assertEquals("User2|0|0|INSERT|3|0|Z|1", request3);
        assertEquals("User1|1|0|INSERT|2|0|Y|1", request2);
        
        // time t=2:
        // User2 receives request 1 from server
        // and should now see AXBCZD while holding AXBCD
        user2.pushRequest("User1|0|0|INSERT|1|0|X|2");
        assertEquals("AXBCZD", user2.getView(0));
        assertEquals("AXBCD", user2.getSyncCopy(0));

        // time t=3:
        // User2 places request 4 and should now see AXBCZKD
        // while holding AXBCD
        user2.createRequest(0, "INSERT|5|0|K");
        assertEquals("AXBCZKD", user2.getView(0));
        assertEquals("AXBCD", user2.getSyncCopy(0));
        
        // time t=4:
        // Request 4 on the way:
        String request4 = user2.pullRequest();
        assertEquals("User2|1|0|INSERT|5|0|K|2", request4);

        // time t=5:
        // User2:
        // User2 receives change 3
        // Should see: AXBCZKD and hold: AXBCZD
        user2.pushRequest("User2|0|0|INSERT|4|0|Z|3");
        assertEquals("AXBCZKD", user2.getView(0));
        assertEquals("AXBCZD", user2.getSyncCopy(0));
        
        // User2 receives change 2
        // Should see: AXYBCZKD and hold: AXYBCZD
        user2.pushRequest("User1|1|0|INSERT|2|0|Y|4");
        assertEquals("AXYBCZKD", user2.getView(0));
        assertEquals("AXYBCZD", user2.getSyncCopy(0));
        
        // User2 receives change 4
        // Should see: AXYBCZKD and hold: AXYBCZKD
        user2.pushRequest("User2|1|0|INSERT|6|0|K|5");
        assertEquals("AXYBCZKD", user2.getView(0));
        assertEquals("AXYBCZKD", user2.getSyncCopy(0));
        
        // User1:
        // User1 receives change 1
        // Should see: AXYBCD and hold: AXBCD
        user1.pushRequest("User1|0|0|INSERT|1|0|X|2");
        assertEquals("AXYBCD", user1.getView(0));
        assertEquals("AXBCD", user1.getSyncCopy(0));
        
        // User1 receives change 3
        // Should see: AXYBCZD and hold: AXBCZD
        user1.pushRequest("User2|0|0|INSERT|4|0|Z|3");
        assertEquals("AXYBCZD", user1.getView(0));
        assertEquals("AXBCZD", user1.getSyncCopy(0));
        
        // User1 receives change 2
        // Should see: AXYBCZD and hold: AXYBCZD
        user1.pushRequest("User1|1|0|INSERT|2|0|Y|4");
        assertEquals("AXYBCZD", user1.getView(0));
        assertEquals("AXYBCZD", user1.getSyncCopy(0));
        
        // User1 receives change 4
        // Should see: AXYBCZKD and hold: AXYBCZKD
        user1.pushRequest("User2|1|0|INSERT|6|0|K|5");
        assertEquals("AXYBCZKD", user1.getView(0));
        assertEquals("AXYBCZKD", user1.getSyncCopy(0));
    }
    
    /** 
     * Single user synchronized to ABCD
     * inserts X after B -> ABXCD (INSERT X at 2)
     * deletes B through C -> AD (DELETE 1 through 4)
     * inserts Y after A -> AYD (INSERT Y at 1)
     * gets responses for first 2 events
     * inserts Z after Y -> AYZD (INSERT Z at 2)
     * get the remaining responses
     */
    @Test
    public void singleUserDeletion() {
        UserDQ user1 = new UserDQ("User1");
        user1.addDocument(0);
        
        // After startup, user sees an empty String
        assertEquals("", user1.getView(0));
        
        // Load the initial state: ABCD
        user1.pushRequest("LoadUser|0|0|INSERT|0|3|ABCD|1");
        assertEquals("ABCD", user1.getView(0));
        
        // User1 inserts X after B in his GUI 
        // but his syncCopy remains unchanged
        user1.createRequest(0, "INSERT|2|0|X");
        assertEquals("ABXCD", user1.getView(0));
        assertEquals("ABCD", user1.getSyncCopy(0));
        
        // User1 deletes B through C in his GUI 
        // but his syncCopy remains unchanged
        user1.createRequest(0, "DELETE|1|4|");
        assertEquals("AD", user1.getView(0));
        assertEquals("ABCD", user1.getSyncCopy(0));

        // User1 inserts Y after A in his GUI 
        // but his syncCopy remains unchanged
        user1.createRequest(0, "INSERT|1|0|Y");
        assertEquals("AYD", user1.getView(0));
        assertEquals("ABCD", user1.getSyncCopy(0));
        
        // Requests 1, 2, 3 on the way:
        String request1 = user1.pullRequest();
        String request2 = user1.pullRequest();
        String request3 = user1.pullRequest();
        assertEquals("User1|0|0|INSERT|2|0|X|1", request1);
        assertEquals("User1|1|0|DELETE|1|4||1", request2);
        assertEquals("User1|2|0|INSERT|1|0|Y|1", request3);
        
        // User1 receives change 1
        // Should see: AYD and hold: ABXCD
        user1.pushRequest("User1|0|0|INSERT|2|0|X|2");
        assertEquals("AYD", user1.getView(0));
        assertEquals("ABXCD", user1.getSyncCopy(0));
        
        // User1 receives change 2
        // Should see: AYD and hold: AD
        user1.pushRequest("User1|1|0|DELETE|1|4||3");
        assertEquals("AYD", user1.getView(0));
        assertEquals("AD", user1.getSyncCopy(0));
        
        // User1 inserts Z after Y in his GUI 
        // but his syncCopy remains unchanged
        user1.createRequest(0, "INSERT|2|0|Z");
        assertEquals("AYZD", user1.getView(0));
        assertEquals("AD", user1.getSyncCopy(0));
        
        // Request 4 on the way:
        String request4 = user1.pullRequest();
        assertEquals("User1|3|0|INSERT|2|0|Z|3", request4);
        
        // User1 receives change 3
        // Should see: AYZD and hold: AYD
        user1.pushRequest("User1|2|0|INSERT|1|0|Y|4");
        assertEquals("AYZD", user1.getView(0));
        assertEquals("AYD", user1.getSyncCopy(0));
        
        // User1 receives change 4
        // Should see: AYZD and hold: AYZD
        user1.pushRequest("User1|3|0|INSERT|2|0|Z|5");
        assertEquals("AYZD", user1.getView(0));
        assertEquals("AYZD", user1.getSyncCopy(0));
    }
    
    /** 
     * Three users connected to ABCDE
     * R1: user 1 deletes BC -> sees ADE (DELETE 1, 3)
     * R2: user 2 deletes CD -> sees ABE (DELETE 2, 4)
     * R3: user 3 deletes BCD -> sees AE (DELETE 1, 4)
     * 
     * requests get processed in order
     * 
     * User views:
     * U1: R1->ADE, R2->AE, R3->AE
     * U2: R1->AE, R2->AE, R3->AE
     * U3: R1->AE, R2->AE, R3->AE
     */
    @Test
    public void tripleUserDeletion() {
        UserDQ user1 = new UserDQ("User1");
        UserDQ user2 = new UserDQ("User2");
        UserDQ user3 = new UserDQ("User3");

        user1.addDocument(0);
        user2.addDocument(0);
        user3.addDocument(0);

        // After startup, all users see an empty String
        assertEquals("", user1.getView(0));
        assertEquals("", user2.getView(0));
        assertEquals("", user3.getView(0));

        // All users load the initial state: ABCDE 
        // which brings them up to version 1
        user1.pushRequest("LoadUser|0|0|INSERT|0|0|ABCDE|1");
        user2.pushRequest("LoadUser|0|0|INSERT|0|0|ABCDE|1");
        user3.pushRequest("LoadUser|0|0|INSERT|0|0|ABCDE|1");
        assertEquals("ABCDE", user1.getView(0));
        assertEquals("ABCDE", user2.getView(0));
        assertEquals("ABCDE", user3.getView(0));

        // User1 deletes BC in his GUI 
        // but his syncCopy remains unchanged
        user1.createRequest(0, "DELETE|1|3|");
        assertEquals("ADE", user1.getView(0));
        assertEquals("ABCDE", user1.getSyncCopy(0));
        
        // User2 deletes CD in his GUI 
        // but his syncCopy remains unchanged
        user2.createRequest(0, "DELETE|2|4|");
        assertEquals("ABE", user2.getView(0));
        assertEquals("ABCDE", user2.getSyncCopy(0));
        
        // User3 deletes BCD in his GUI 
        // but his syncCopy remains unchanged
        user3.createRequest(0, "DELETE|1|4|");
        assertEquals("AE", user3.getView(0));
        assertEquals("ABCDE", user3.getSyncCopy(0));
        
        // Requests 1, 2, 3 on the way:
        String request1 = user1.pullRequest();
        String request2 = user2.pullRequest();
        String request3 = user3.pullRequest();
        assertEquals("User1|0|0|DELETE|1|3||1", request1);
        assertEquals("User2|0|0|DELETE|2|4||1", request2);
        assertEquals("User3|0|0|DELETE|1|4||1", request3);
        
        // User1:
        // User1 receives change 1
        // Should see: ADE and hold: ADE
        user1.pushRequest("User1|0|0|DELETE|1|3||2");
        assertEquals("ADE", user1.getView(0));
        assertEquals("ADE", user1.getSyncCopy(0));
        
        // User1 receives change 2
        // Should see: AE and hold: AE
        user1.pushRequest("User2|0|0|DELETE|1|2||3");
        assertEquals("AE", user1.getView(0));
        assertEquals("AE", user1.getSyncCopy(0));
        
        // User1 receives change 3
        // Should see: AE and hold: AE
        user1.pushRequest("User3|0|0|INSERT|1|1||4");
        assertEquals("AE", user1.getView(0));
        assertEquals("AE", user1.getSyncCopy(0));
        
        // User2:
        // User2 receives change 1
        // Should see: AE and hold: ADE
        user2.pushRequest("User1|0|0|DELETE|1|3||2");
        assertEquals("AE", user2.getView(0));
        assertEquals("ADE", user2.getSyncCopy(0));
        
        // User2 receives change 2
        // Should see: AE and hold: AE
        user2.pushRequest("User2|0|0|DELETE|1|2||3");
        assertEquals("AE", user2.getView(0));
        assertEquals("AE", user2.getSyncCopy(0));
        
        // User2 receives change 3
        // Should see: AE and hold: AE
        user2.pushRequest("User3|0|0|INSERT|1|1||4");
        assertEquals("AE", user2.getView(0));
        assertEquals("AE", user2.getSyncCopy(0));
        
        // User3:
        // User3 receives change 1
        // Should see: AE and hold: ADE
        user3.pushRequest("User1|0|0|DELETE|1|3||2");
        assertEquals("AE", user3.getView(0));
        assertEquals("ADE", user3.getSyncCopy(0));
        
        // User2 receives change 2
        // Should see: AE and hold: AE
        user3.pushRequest("User2|0|0|DELETE|1|2||3");
        assertEquals("AE", user3.getView(0));
        assertEquals("AE", user3.getSyncCopy(0));
        
        // User2 receives change 3
        // Should see: AE and hold: AE
        user3.pushRequest("User3|0|0|INSERT|1|1||4");
        assertEquals("AE", user3.getView(0));
        assertEquals("AE", user3.getSyncCopy(0));
    }
    
    /**
     * Two users connected to two documents,
     * the documents shouldn't affect each other.
     * 
     * 1) ABCD gets loaded into document 0
     * 2) User2 inserts Y into document 1
     * 3) User1 inserts X after A in document 0
     * 4) requests get processed and displayed and
     *    both users hold AXBCD in one and Y in the other document
     */
    @Test
    public void multipleDocsTest() {
        UserDQ user1 = new UserDQ("User1");
        UserDQ user2 = new UserDQ("User2");

        user1.addDocument(0);
        user1.addDocument(1);
        user2.addDocument(0);
        user2.addDocument(1);

        // After startup, both users sees an empty String
        assertEquals("", user1.getView(0));
        assertEquals("", user2.getView(0));
        assertEquals("", user1.getView(1));
        assertEquals("", user2.getView(1));

        // Both users load the initial state ABCD into document 0
        user1.pushRequest("LoadUser|0|0|INSERT|0|3|ABCD|1");
        user2.pushRequest("LoadUser|0|0|INSERT|0|3|ABCD|1");
        assertEquals("ABCD", user1.getView(0));
        assertEquals("ABCD", user2.getView(0));
        
        // Document 1 is still empty
        assertEquals("", user1.getView(1));
        assertEquals("", user2.getView(1));

        // User2 inserts Y in document 1 his GUI 
        // but his syncCopy remains unchanged
        user2.createRequest(1, "INSERT|0|0|Y");
        assertEquals("Y", user2.getView(1));
        assertEquals("", user2.getSyncCopy(1));
        
        // User2 sends his request to the server. 
        String request1 = user2.pullRequest();
        assertEquals("User2|0|1|INSERT|0|0|Y|0", request1);
        
        // User1 inserts X after A in document0 his GUI 
        // but his syncCopy remains unchanged
        user1.createRequest(0, "INSERT|1|0|X");
        assertEquals("AXBCD", user1.getView(0));
        assertEquals("ABCD", user1.getSyncCopy(0));
        
        // User1 sends his request to the server. 
        String request2 = user1.pullRequest();
        assertEquals("User1|0|0|INSERT|1|0|X|1", request2);

        // User1 receives his change back and should 
        // still see AXBCD but now it's also his syncCopy
        user1.pushRequest("User1|0|0|INSERT|1|0|X|2");
        assertEquals("AXBCD", user1.getView(0));
        assertEquals("AXBCD", user1.getSyncCopy(0));
        
        // User2 also receives the change and should 
        // now both see and hold AXBCD
        user2.pushRequest("User1|0|0|INSERT|1|0|X|2");
        assertEquals("AXBCD", user2.getView(0));
        assertEquals("AXBCD", user2.getSyncCopy(0));
        
        
        // Both users receive the change in document 1
        // and should both see and hold Y
        user1.pushRequest("User2|0|1|INSERT|0|0|Y|1");
        assertEquals("Y", user1.getView(1));
        assertEquals("Y", user1.getSyncCopy(1));
        user2.pushRequest("User2|0|1|INSERT|0|0|Y|1");
        assertEquals("Y", user2.getView(1));
        assertEquals("Y", user2.getSyncCopy(1));
    }
}
