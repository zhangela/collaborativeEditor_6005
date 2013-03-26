package Deprecated;
//package DoubleQueueExperiment;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class DoubleQueueServerSimulator {
//    public static List<ServerRequestDQ> serverHistory;
//    
//    public static void resetServer() {
//        serverHistory = new ArrayList<ServerRequestDQ>();
//
//        //empty event leading to version 0
//        serverHistory.add(new ServerRequestDQ("|0|0|INSERT|0|0||0"));
//    }
//
//    private static void receiveRequest(String input) {
//        ServerRequestDQ request = new ServerRequestDQ(input);
//        int userVersion = request.getVersionID();
//        
//        
//        
//        for(int i = userVersion + 1; i < serverHistory.size(); i++) {
//            ServerRequestDQ serverEvent = serverHistory.get(i);
//            
//            // Don't apply one's own updates, they're applied implicitly
//            if(!request.getUserID().equals(serverEvent.getUserID())) {
//                request.applyUpdate(serverEvent);
//            }
//        }
//        
//        // The versionID of an event on the server is its index in the server history
//        ServerRequestDQ inclusionCandidate = new ServerRequestDQ(request, serverHistory.size());
//        serverHistory.add(inclusionCandidate);
//        
//        // distribute request to users:
//    }
//    
//    public static void main(String[] args) {
//        /* These will eventually turn into JUnit tests: */
//        
//        //simpleInsertion();
//        //concurrentInsertion();
//        //concurrentInsertionInTheSamePlace();
//        //interleavingInsertions();
//        //singleUserDeletion();
//        //tripleUserDeletion();
//        interleavingInsertionDeletion();
//        
//    }
//    
//    
//    
//    /* Single user connected to ABCD, inserts X after A */
//    public static void simpleInsertion() {
//        resetServer();
//        
//        UserDQ user1 = new UserDQ("User 1", "ABCD");
//        
//        System.out.println("User sees initial state: " + user1.getView());
//        System.out.println("User inserts X after A");
//        user1.createRequest("0|INSERT|1|0|X");
//        System.out.println("User sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//        System.out.println("Request sent to server, user still sees: " + user1.getView());
//        ServerRequestDQ request = new ServerRequestDQ(user1.pullRequest());
//        System.out.println("Server processes the request");
//        receiveRequest(request.toString());
//        System.out.println("Server now sees history: " + serverHistory.toString());
//        System.out.println("User receives the change back");
//        user1.pushRequest(serverHistory.get(1).toString());
//        System.out.println("User sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//    }
//    
//    /* Two users connected to ABCD
//     * U1 inserts X after A, U2 inserts Y after C
//     * Requests happen at the same time and 
//     * only get processed afterwards*/
//    public static void concurrentInsertion() {
//        resetServer();
//        
//        UserDQ user1 = new UserDQ("User 1", "ABCD");
//        UserDQ user2 = new UserDQ("User 2", "ABCD");
//        
//        System.out.println("User 1 sees initial state: " + user1.getView());
//        System.out.println("User 2 sees initial state: " + user2.getView());
//
//        System.out.println("Prior to his action, User 1 sees: " + user1.getView());
//        System.out.println("User 1 inserts X after A");
//        user1.createRequest("0|INSERT|1|0|X");
//        System.out.println("User 1 sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//        
//        System.out.println("Prior to his action, User 2 sees: " + user2.getView());
//        System.out.println("User 2 inserts Y after C");
//        user2.createRequest("0|INSERT|3|0|Y");
//        System.out.println("User 2 sees modified state: " + user2.getView());
//        System.out.println("While holding the syncCopy: " + user2.getSyncCopy());
//        
//        
//        System.out.println("User 1's request sent to server, user still sees: " + user1.getView());
//        ServerRequestDQ request1 = new ServerRequestDQ(user1.pullRequest());
//        System.out.println("Server processes User 1's request");
//        receiveRequest(request1.toString());
//        System.out.println("Server now sees history: " + serverHistory.toString());
//        
//        System.out.println("User 1 receives the change back");
//        user1.pushRequest(serverHistory.get(1).toString());
//        System.out.println("User 1 sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//        
//        System.out.println("User 2 receives the change made by User 1");
//        user2.pushRequest(serverHistory.get(1).toString());
//        System.out.println("User 2 sees modified state: " + user2.getView());
//        System.out.println("While holding the syncCopy: " + user2.getSyncCopy());
//        
//        System.out.println("User 2's request sent to server, user still sees: " + user2.getView());
//        ServerRequestDQ request2 = new ServerRequestDQ(user2.pullRequest());
//        System.out.println("Server processes User 2's request");
//        receiveRequest(request2.toString());
//        System.out.println("Server now sees history: " + serverHistory.toString());
//        
//        System.out.println("User 2 receives the change made by User 2");
//        user2.pushRequest(serverHistory.get(2).toString());
//        System.out.println("User 2 sees modified state: " + user2.getView());
//        System.out.println("While holding the syncCopy: " + user2.getSyncCopy());
//        
//        System.out.println("User 1 receives the change made by User 2");
//        user1.pushRequest(serverHistory.get(2).toString());
//        System.out.println("User 1 sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//    }
//    
//    /* Two users connected to ABCD
//     * U1 inserts X after A, U2 inserts Y after A
//     * Requests happen at the same time and 
//     * only get processed afterwards, request made by
//     * user 1 gets processed first*/
//    public static void concurrentInsertionInTheSamePlace() {
//        resetServer();
//        UserDQ user1 = new UserDQ("User 1", "ABCD");
//        UserDQ user2 = new UserDQ("User 2", "ABCD");
//        
//        System.out.println("User 1 sees initial state: " + user1.getView());
//        System.out.println("User 2 sees initial state: " + user2.getView());
//
//        System.out.println("Prior to his action, User 1 sees: " + user1.getView());
//        System.out.println("User 1 inserts X after A");
//        user1.createRequest("0|INSERT|1|0|X");
//        System.out.println("User 1 sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//        
//        System.out.println("Prior to his action, User 2 sees: " + user2.getView());
//        System.out.println("User 2 inserts Y after A");
//        user2.createRequest("0|INSERT|1|0|Y");
//        System.out.println("User 2 sees modified state: " + user2.getView());
//        System.out.println("While holding the syncCopy: " + user2.getSyncCopy());
//        
//        
//        System.out.println("User 1's request sent to server, user still sees: " + user1.getView());
//        ServerRequestDQ request1 = new ServerRequestDQ(user1.pullRequest());
//        System.out.println("Server processes User 1's request");
//        receiveRequest(request1.toString());
//        System.out.println("Server now sees history: " + serverHistory.toString());
//        
//        System.out.println("User 1 receives the change back");
//        user1.pushRequest(serverHistory.get(1).toString());
//        System.out.println("User 1 sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//        
//        System.out.println("User 2 receives the change made by User 1");
//        user2.pushRequest(serverHistory.get(1).toString());
//        System.out.println("User 2 sees modified state: " + user2.getView());
//        System.out.println("While holding the syncCopy: " + user2.getSyncCopy());
//        
//        System.out.println("User 2's request sent to server, user still sees: " + user2.getView());
//        ServerRequestDQ request2 = new ServerRequestDQ(user2.pullRequest());
//        System.out.println("Server processes User 2's request");
//        receiveRequest(request2.toString());
//        System.out.println("Server now sees history: " + serverHistory.toString());
//        
//        System.out.println("User 2 receives the change made by User 2");
//        user2.pushRequest(serverHistory.get(2).toString());
//        System.out.println("User 2 sees modified state: " + user2.getView());
//        System.out.println("While holding the syncCopy: " + user2.getSyncCopy());
//        
//        System.out.println("User 1 receives the change made by User 2");
//        user1.pushRequest(serverHistory.get(2).toString());
//        System.out.println("User 1 sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//    }
//    
//    /* Two users connected to ABCD
//     * U1 inserts X after A (1), then Y after X (2),
//     * U2 inserts Z after C (3), then K after Z (4)
//     * 
//     * Time t=0: U1 places both requests 1 and 2, while U2 places 3
//     * Time t=1: Requests get processed by server in order 1, 3, 2
//     * Time t=2: U2 receives request 1 from server
//     * Time t=3: U2 places request 4
//     * Time t=4: Server processes request 4
//     * Time t=5: All remaining requests received and processed in order
//     * 
//     * 
//     * serverHistory: r1, r3, r2, r4
//     */
//    public static void interleavingInsertions() {
//        //serverHistory = new ArrayList<ServerRequestDQ>();
//        
//        resetServer();
//        UserDQ user1 = new UserDQ("User 1", "ABCD");
//        UserDQ user2 = new UserDQ("User 2", "ABCD");
//        
//        System.out.println("User 1 sees initial state: " + user1.getView());
//        System.out.println("User 2 sees initial state: " + user2.getView());
//
//        System.out.println();
//        
//        System.out.println("Time t=0:");
//        System.out.println("Prior to his action, User 1 sees: " + user1.getView());
//        System.out.println("User 1 inserts X after A");
//        user1.createRequest("0|INSERT|1|0|X");
//        System.out.println("User 1 sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//       
//        System.out.println("User 1 inserts Y after X");
//        user1.createRequest("0|INSERT|2|0|Y");
//        System.out.println("User 1 sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//        
//        System.out.println("Prior to his action, User 2 sees: " + user2.getView());
//        System.out.println("User 2 inserts Z after C");
//        user2.createRequest("0|INSERT|3|0|Z");
//        System.out.println("User 2 sees modified state: " + user2.getView());
//        System.out.println("While holding the syncCopy: " + user2.getSyncCopy());
//        
//        System.out.println();
//        
//        System.out.println("Time t=1:");
//        System.out.println("User 1's request 1 sent to server, user still sees: " + user1.getView());
//        ServerRequestDQ request1 = new ServerRequestDQ(user1.pullRequest());
//        System.out.println("Server processes User 1's request 1:"  + request1.toString());
//        receiveRequest(request1.toString());
//        System.out.println("Server now sees history: " + serverHistory.toString());
//        
//        System.out.println("User 2's request 3 sent to server, user still sees: " + user2.getView());
//        ServerRequestDQ request3 = new ServerRequestDQ(user2.pullRequest());
//        System.out.println("Server processes User 2's request 3: " + request3.toString());
//        receiveRequest(request3.toString());
//        System.out.println("Server now sees history: " + serverHistory.toString());
//        
//        System.out.println("User 1's request 2 sent to server, user still sees: " + user1.getView());
//        ServerRequestDQ request2 = new ServerRequestDQ(user1.pullRequest());
//        System.out.println("Server processes User 1's request 2:"  + request2.toString());
//        receiveRequest(request2.toString());
//        System.out.println("Server now sees history: " + serverHistory.toString());
//        
//        System.out.println();
//        
//        System.out.println("Time t=2:");        
//        System.out.println("User 2 receives the change made by User 1 - request 1");
//        user2.pushRequest(serverHistory.get(1).toString());
//        System.out.println("User 2 sees modified state: " + user2.getView());
//        System.out.println("While holding the syncCopy: " + user2.getSyncCopy());
//        
//        System.out.println();
//        
//        System.out.println("Time t=3:");    
//        System.out.println("Prior to his action, User 2 sees: " + user2.getView()); // Should see AXBCZD
//        System.out.println("User 2 inserts K after Z");
//        user2.createRequest("0|INSERT|5|0|K");
//        System.out.println("User 2 sees modified state: " + user2.getView());
//        System.out.println("While holding the syncCopy: " + user2.getSyncCopy());
//
//        System.out.println();
//        
//        System.out.println("Time t=4:");    
//        System.out.println("User 2's request 4 sent to server, user still sees: " + user1.getView());
//        ServerRequestDQ request4 = new ServerRequestDQ(user2.pullRequest());
//        System.out.println("Server processes User 2's request 4: " + request4.toString());
//        receiveRequest(request4.toString());
//        System.out.println("Server now sees history: " + serverHistory.toString());
//
//        System.out.println();
//        
//        System.out.println("Time t=5:");   
//        // U2 receives changes 2, 3, 4
//        System.out.println("User 2 receives the change made by User 2 - request 3");
//        user2.pushRequest(serverHistory.get(2).toString());
//        System.out.println("User 2 sees modified state: " + user2.getView());
//        System.out.println("While holding the syncCopy: " + user2.getSyncCopy());
//        
//        System.out.println("User 2 receives the change made by User 1 - request 2");
//        user2.pushRequest(serverHistory.get(3).toString());
//        System.out.println("User 2 sees modified state: " + user2.getView());
//        System.out.println("While holding the syncCopy: " + user2.getSyncCopy());
//        
//        System.out.println("User 2 receives the change made by User 2 - request 4");
//        user2.pushRequest(serverHistory.get(4).toString());
//        System.out.println("User 2 sees modified state: " + user2.getView());
//        System.out.println("While holding the syncCopy: " + user2.getSyncCopy());
//        
//        // U1 receives all the 4 changes in sequence
//        System.out.println("User 1 receives the change made by User 1 - request 1");
//        user1.pushRequest(serverHistory.get(1).toString());
//        System.out.println("User 1 sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//        
//        System.out.println("User 1 receives the change made by User 2 - request 3");
//        user1.pushRequest(serverHistory.get(2).toString());
//        System.out.println("User 1 sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//        
//        System.out.println("User 1 receives the change made by User 1 - request 2");
//        user1.pushRequest(serverHistory.get(3).toString());
//        System.out.println("User 1 sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//        
//        System.out.println("User 1 receives the change made by User 2 - request 4");
//        user1.pushRequest(serverHistory.get(4).toString());
//        System.out.println("User 1 sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//        
//        System.out.println();
//        System.out.println("Time t=6:");   
//        System.out.println("User 1's final version: " + user1.getView());
//        System.out.println("User 2's final version: " + user2.getView());
//    }
//    
//    /* Single user connected to ABCD
//     * inserts X after B -> ABXCD (INSERT X at 2)
//     * deletes B through C -> AD (DELETE 1 through 4)
//     * inserts Y after A -> AYD (INSERT Y at 1)
//     * gets responses for first 2 events
//     * inserts Z after Y -> AYZD (INSERT Z at 2)
//     * get the remaining responses
//     */
//    public static void singleUserDeletion() {
//        resetServer();
//        
//        UserDQ user1 = new UserDQ("User 1", "ABCD");
//        
//        System.out.println("User sees initial state: " + user1.getView());
//        
//        System.out.println();
//        System.out.println("User inserts X after B");
//        user1.createRequest("0|INSERT|2|0|X");
//        System.out.println("User sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//        
//        System.out.println();
//        System.out.println("User deletes B through C");
//        user1.createRequest("0|DELETE|1|4|");
//        System.out.println("User sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//        
//        System.out.println();
//        System.out.println("User inserts Y after A");
//        user1.createRequest("0|INSERT|1|0|Y");
//        System.out.println("User sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//        
//        System.out.println();
//        System.out.println("Request 1 sent to server, user still sees: " + user1.getView());
//        ServerRequestDQ request1 = new ServerRequestDQ(user1.pullRequest());
//        System.out.println("Server processes request 1: " + request1.toString());
//        receiveRequest(request1.toString());
//        System.out.println("Server now sees history: " + serverHistory.toString());
//        
//        System.out.println("Request 2 sent to server, user still sees: " + user1.getView());
//        ServerRequestDQ request2 = new ServerRequestDQ(user1.pullRequest());
//        System.out.println("Server processes request 2: " + request2.toString());
//        receiveRequest(request2.toString());
//        System.out.println("Server now sees history: " + serverHistory.toString());
//        
//        System.out.println("Request 3 sent to server, user still sees: " + user1.getView());
//        ServerRequestDQ request3 = new ServerRequestDQ(user1.pullRequest());
//        System.out.println("Server processes request 3: " + request3.toString());
//        receiveRequest(request3.toString());
//        System.out.println("Server now sees history: " + serverHistory.toString());
//        
//        System.out.println();
//        System.out.println("User receives request 1 back");
//        user1.pushRequest(serverHistory.get(1).toString());
//        System.out.println("User sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//        
//        System.out.println();
//        System.out.println("User receives request 2 back");
//        user1.pushRequest(serverHistory.get(2).toString());
//        System.out.println("User sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//     
//        System.out.println();
//        System.out.println("User inserts Z after Y");
//        user1.createRequest("0|INSERT|2|0|Z");
//        System.out.println("User sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//        
//        System.out.println("Request 4 sent to server, user still sees: " + user1.getView());
//        ServerRequestDQ request4 = new ServerRequestDQ(user1.pullRequest());
//        System.out.println("Server processes request 3: " + request4.toString());
//        receiveRequest(request4.toString());
//        System.out.println("Server now sees history: " + serverHistory.toString());
//        
//        System.out.println();
//        System.out.println("User receives request 3 back");
//        user1.pushRequest(serverHistory.get(3).toString());
//        System.out.println("User sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//        
//        System.out.println();
//        System.out.println("User receives request 4 back");
//        user1.pushRequest(serverHistory.get(4).toString());
//        System.out.println("User sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//    }
//    
//    /* Three users connected to ABCDE
//     * R1: user 1 deletes BC -> sees ADE (DELETE 1, 3)
//     * R2: user 2 deletes CD -> sees ABE (DELETE 2, 4)
//     * R3: user 3 deletes BCD -> sees AE (DELETE 1, 4)
//     * 
//     * requests get processed in order
//     * 
//     * User views:
//     * U1: R1->ADE, R2->AE, R3->AE
//     * U2: R1->AE, R2->AE, R3->AE
//     * U3: R1->AE, R2->AE, R3->AE
//     */
//    public static void tripleUserDeletion() {
//        resetServer();
//        
//        UserDQ user1 = new UserDQ("User 1", "ABCDE");
//        UserDQ user2 = new UserDQ("User 2", "ABCDE");
//        UserDQ user3 = new UserDQ("User 3", "ABCDE");
//        
//        System.out.println("User 1 sees initial state: " + user1.getView());
//        System.out.println("User 2 sees initial state: " + user2.getView());
//        System.out.println("User 3 sees initial state: " + user3.getView());
//        
//        System.out.println();
//        System.out.println("User 1 deletes BC");
//        user1.createRequest("0|DELETE|1|3|");
//        System.out.println("User 1 sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//        
//        System.out.println();
//        System.out.println("User 2 deletes CD");
//        user2.createRequest("0|DELETE|2|4|");
//        System.out.println("User 2 sees modified state: " + user2.getView());
//        System.out.println("While holding the syncCopy: " + user2.getSyncCopy());
//   
//        System.out.println();
//        System.out.println("User 3 deletes BCD");
//        user3.createRequest("0|DELETE|1|4|");
//        System.out.println("User 3 sees modified state: " + user3.getView());
//        System.out.println("While holding the syncCopy: " + user3.getSyncCopy());
//        
//        
//        System.out.println();
//        System.out.println("Request 1 sent to server, user 1 still sees: " + user1.getView());
//        ServerRequestDQ request1 = new ServerRequestDQ(user1.pullRequest());
//        System.out.println("Server processes request 1: " + request1.toString());
//        receiveRequest(request1.toString());
//        System.out.println("Server now sees history: " + serverHistory.toString());
//        
//        System.out.println("Request 2 sent to server, user 2 still sees: " + user2.getView());
//        ServerRequestDQ request2 = new ServerRequestDQ(user2.pullRequest());
//        System.out.println("Server processes request 2: " + request2.toString());
//        receiveRequest(request2.toString());
//        System.out.println("Server now sees history: " + serverHistory.toString());
//        
//        System.out.println("Request 3 sent to server, user 3 still sees: " + user3.getView());
//        ServerRequestDQ request3 = new ServerRequestDQ(user3.pullRequest());
//        System.out.println("Server processes request 3: " + request3.toString());
//        receiveRequest(request3.toString());
//        System.out.println("Server now sees history: " + serverHistory.toString());
//        
//        System.out.println();
//        System.out.println("User 1 receives request 1 back");
//        user1.pushRequest(serverHistory.get(1).toString());
//        System.out.println("User sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//        
//        System.out.println("User 1 receives request 2");
//        user1.pushRequest(serverHistory.get(2).toString());
//        System.out.println("User sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//        
//        System.out.println("User 1 receives request 3");
//        user1.pushRequest(serverHistory.get(3).toString());
//        System.out.println("User sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//        
//        System.out.println();
//        System.out.println("User 2 receives request 1");
//        user2.pushRequest(serverHistory.get(1).toString());
//        System.out.println("User sees modified state: " + user2.getView());
//        System.out.println("While holding the syncCopy: " + user2.getSyncCopy());
//        
//        System.out.println("User 2 receives request 2 back");
//        user2.pushRequest(serverHistory.get(2).toString());
//        System.out.println("User sees modified state: " + user2.getView());
//        System.out.println("While holding the syncCopy: " + user2.getSyncCopy());
//        
//        System.out.println("User 2 receives request 3");
//        user2.pushRequest(serverHistory.get(3).toString());
//        System.out.println("User sees modified state: " + user2.getView());
//        System.out.println("While holding the syncCopy: " + user2.getSyncCopy());
//        
//        System.out.println();
//        System.out.println("User 3 receives request 1");
//        user3.pushRequest(serverHistory.get(1).toString());
//        System.out.println("User sees modified state: " + user3.getView());
//        System.out.println("While holding the syncCopy: " + user3.getSyncCopy());
//        
//        System.out.println("User 3 receives request 2");
//        user3.pushRequest(serverHistory.get(2).toString());
//        System.out.println("User sees modified state: " + user3.getView());
//        System.out.println("While holding the syncCopy: " + user3.getSyncCopy());
//        
//        System.out.println("User 3 receives request 3 back");
//        user3.pushRequest(serverHistory.get(3).toString());
//        System.out.println("User sees modified state: " + user3.getView());
//        System.out.println("While holding the syncCopy: " + user3.getSyncCopy());
//        
//        
//        System.out.println();
//        System.out.println("User 1's final version: " + user1.getView());
//        System.out.println("User 2's final version: " + user2.getView());
//        System.out.println("User 3's final version: " + user3.getView());        
//    }
//    
//    /* Two users connected to ABCDEFG
//     * 
//     * R1: User 1 inserts X after B -> sees ABXCDEF (INSERT X at 2)
//     * R2: User 1 deleted C through E -> sees ABXF  (DELETE 3 through 6)
//     * R3: User 2 deletes B through C -> sees ADEF (DELETE 1 through 3) -> this should eventually delete user 1's insertion
//     * R4: User 2 inserts Z after D -> sees ADZEF (INSERT Z at 2) -> inserting in the middle of non-existent segment -> insert at beginning 
//     * (eventually add delete user 2 deletes F)
//     * 
//     * Requests processed in increasing order at the same time
//     * 
//     * User 1 gets all the updates
//     * User 2 gets all the updates
//     * 
//     * Desired outcome is AZFG for both with specified intermediate behavior
//     */
//    public static void interleavingInsertionDeletion() {
//        resetServer();
//        UserDQ user1 = new UserDQ("User 1", "ABCDEFG");
//        UserDQ user2 = new UserDQ("User 2", "ABCDEFG");
//        
//        System.out.println("User 1 sees initial state: " + user1.getView());
//        System.out.println("User 2 sees initial state: " + user2.getView());
//
//        System.out.println();
//        
//        System.out.println("Prior to his action, User 1 sees: " + user1.getView());
//        System.out.println("User 1 inserts X after B");
//        user1.createRequest("0|INSERT|2|0|X");
//        System.out.println("User 1 sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//       
//        System.out.println("User 1 deletes C through E");
//        user1.createRequest("0|DELETE|3|6|");
//        System.out.println("User 1 sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//        
//        System.out.println();
//        System.out.println("Prior to his action, User 2 sees: " + user2.getView());
//        System.out.println("User 2 deletes B through C");
//        user2.createRequest("0|DELETE|1|3|");
//        System.out.println("User 2 sees modified state: " + user2.getView());
//        System.out.println("While holding the syncCopy: " + user2.getSyncCopy());
//        
//        System.out.println("User 2 inserts Z after D");
//        user2.createRequest("0|INSERT|2|0|Z");
//        System.out.println("User 2 sees modified state: " + user2.getView());
//        System.out.println("While holding the syncCopy: " + user2.getSyncCopy());
//        
//        
//        System.out.println();
//        System.out.println("User 1's request 1 sent to server, user still sees: " + user1.getView());
//        ServerRequestDQ request1 = new ServerRequestDQ(user1.pullRequest());
//        System.out.println("Server processes User 1's request 1:"  + request1.toString());
//        receiveRequest(request1.toString());
//        System.out.println("Server now sees history: " + serverHistory.toString());
//        
//        System.out.println("User 1's request 2 sent to server, user still sees: " + user1.getView());
//        ServerRequestDQ request2 = new ServerRequestDQ(user1.pullRequest());
//        System.out.println("Server processes User 1's request 1:"  + request2.toString());
//        receiveRequest(request2.toString());
//        System.out.println("Server now sees history: " + serverHistory.toString());
//        
//        System.out.println("User 2's request 3 sent to server, user still sees: " + user2.getView());
//        ServerRequestDQ request3 = new ServerRequestDQ(user2.pullRequest());
//        System.out.println("Server processes User 2's request 3: " + request3.toString());
//        receiveRequest(request3.toString());
//        System.out.println("Server now sees history: " + serverHistory.toString());
//        
//        System.out.println("User 2's request 4 sent to server, user still sees: " + user2.getView());
//        ServerRequestDQ request4 = new ServerRequestDQ(user2.pullRequest());
//        System.out.println("Server processes User 2's request 3: " + request4.toString());
//        receiveRequest(request4.toString());
//        System.out.println("Server now sees history: " + serverHistory.toString());
//        
//        // U1 receives all the 4 changes in sequence
//        System.out.println();
//        System.out.println("User 1 receives the change made by User 1 - request 1");
//        user1.pushRequest(serverHistory.get(1).toString());
//        System.out.println("User 1 sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//        
//        System.out.println("User 1 receives the change made by User 1 - request 2");
//        user1.pushRequest(serverHistory.get(2).toString());
//        System.out.println("User 1 sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//        
//        System.out.println("User 1 receives the change made by User 2 - request 3");
//        user1.pushRequest(serverHistory.get(3).toString());
//        System.out.println("User 1 sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//        
//        System.out.println("User 1 receives the change made by User 2 - request 4");
//        user1.pushRequest(serverHistory.get(4).toString());
//        System.out.println("User 1 sees modified state: " + user1.getView());
//        System.out.println("While holding the syncCopy: " + user1.getSyncCopy());
//        
//        // U2 receives all the 4 changes in sequence
//        
//        System.out.println();
//        System.out.println("User 2 receives the change made by User 1 - request 1");
//        user2.pushRequest(serverHistory.get(1).toString());
//        System.out.println("User 2 sees modified state: " + user2.getView());
//        System.out.println("While holding the syncCopy: " + user2.getSyncCopy());
//        
//        System.out.println("User 2 receives the change made by User 1 - request 2");
//        user2.pushRequest(serverHistory.get(2).toString());
//        System.out.println("User 1 sees modified state: " + user2.getView());
//        System.out.println("While holding the syncCopy: " + user2.getSyncCopy());
//        
//        System.out.println("User 2 receives the change made by User 2 - request 3");
//        user2.pushRequest(serverHistory.get(3).toString());
//        System.out.println("User 1 sees modified state: " + user2.getView());
//        System.out.println("While holding the syncCopy: " + user2.getSyncCopy());
//        
//        System.out.println("User 2 receives the change made by User 2 - request 4");
//        user2.pushRequest(serverHistory.get(4).toString());
//        System.out.println("User 1 sees modified state: " + user2.getView());
//        System.out.println("While holding the syncCopy: " + user2.getSyncCopy());
//        
//        System.out.println();
//        System.out.println("User 1's final version: " + user1.getView());
//        System.out.println("User 2's final version: " + user2.getView());
//    }
//    
//}
