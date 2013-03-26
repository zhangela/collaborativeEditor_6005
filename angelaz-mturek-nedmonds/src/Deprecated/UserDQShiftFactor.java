package Deprecated;
//package DoubleQueueExperiment;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import Server.*;
//
//public class UserDQShiftFactor {
//    private String userID;
//    private int requestCounter;
//    
//    private String syncCopy;
//    private int syncVersion;
//    
//    //private String viewCopy;
//    
//    private List<String> serverBuffer;
//    private List<ServerRequestDQ> localQueue;
//    
//    public UserDQShiftFactor(String userID, String document) {
//        this.userID = userID;
//        this.requestCounter = 0;
//        
//        this.syncCopy = document;
//        //this.viewCopy = document;
//        
//        this.localQueue = new ArrayList<ServerRequestDQ>();
//        this.serverBuffer = new ArrayList<String>();
//    }
//    
//    public String getView() {
//        // Apply local changes
//        String viewCopy = syncCopy;
//        for(ServerRequestDQ localChange : localQueue) {
//            viewCopy = applyChange(viewCopy, localChange);
//        }
//        
//        return viewCopy;
//    }
//    
//    public String getSyncCopy() {
//        return syncCopy;
//    }
//
//    // Request pushed from the server -> simulates message received
//    public void pushRequest(String requestText) {
//        ServerRequestDQ request = new ServerRequestDQ(requestText);
//        // Update the synchronized version
//        syncCopy = applyChange(syncCopy, request);
//        syncVersion = request.getVersionID();
//        
//        // If own request -> remove and continue
//        if(request.getUserID().equals(userID)) {
//            if(request.getRequestNumber() == localQueue.get(0).getRequestNumber()) {
//                localQueue.remove(0);
//                return;
//            } else {
//                userMsg("Request number sent from server: " + request.getRequestNumber());
//                userMsg("Request number in local queue: " + localQueue.get(0).getRequestNumber());
//                throw new RuntimeException("Request ordering error");
//            }
//        }
//        
//        userMsg("Someone else's request received: " + request);
//        userMsg("Current contents of localQueue: " + localQueue.toString());
// 
//        int shiftFactor = 0;
//        // If someone else's request, update local queue
//        for(ServerRequestDQ localChange : localQueue) {
//            shiftFactor = localChange.applyUpdate(request, shiftFactor);
//        }       
//        
//        userMsg("Final contents of localQueue: " + localQueue.toString());
//    }
//    
//    private void userMsg(String message) {
//        System.out.println("msg from " + userID + ": " + message);
//    }
//    
//    
//    // Eventually this will be in a listener as a response
//    // to a GUI event
//    // request: USER|REQ_NUM|DOC_ID|ACTION|BEG|END|CONTENT|VERSION_ID
//    // request body: DOC_ID|ACTION|BEG|END|CONTENT
//    // simulates message sent
//    public void createRequest(String requestBody) {
//        String fullRequestText = "" + userID + "|" + requestCounter + "|"
//                + requestBody + "|" + syncVersion;
//        requestCounter++;
//        
//        ServerRequestDQ request = new ServerRequestDQ(fullRequestText);
//        
//        localQueue.add(request);  
//        userMsg("After adding request: " + request.toString());
//        userMsg("The contents of localQueue are: " + localQueue.toString());
//        
//        serverBuffer.add(request.toString());
//    }
//    
//    // simulates message received by server
//    public String pullRequest() {
//        if(serverBuffer.size() > 0) {
//            return serverBuffer.remove(0);
//        } else {
//            return null;
//        }
//    }
//    /*
//    // NOT NECESSARY!!!
//    // Subtracts the local changes from a request to make
//    // indices match with the syncCopy
//    private ServerRequestDQ subtractLocal(ServerRequestDQ request) {
//        for(ServerRequestDQ localChange : localQueue) {
//            
//            if(localChange.getAction().equals("INSERT")) {
//               re
//            } else {
//                System.out.println("Operation not supported: " + request.getAction());
//            }
//            
//        }
//    }*/
//    
//    public String applyChange(String source, ServerRequestDQ request) {
//        StringBuilder result = new StringBuilder(source);
//        
//        if(request.getAction().equals("INSERT")) {
//            result.insert(request.getBeginning(), request.getContent());
//        } else if(request.getAction().equals("DELETE")) {
//            result.delete(request.getBeginning(), request.getEnd());
//        } else {
//            System.out.println("Operation not supported: " + request.getAction());
//        }
//        
//        return result.toString();
//    }
//}
