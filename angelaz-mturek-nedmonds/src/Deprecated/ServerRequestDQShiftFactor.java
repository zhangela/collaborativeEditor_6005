package Deprecated;
//package DoubleQueueExperiment;
//
//import Server.ServerRequest.Action;
//
//public class ServerRequestDQShiftFactor {
//    private final String userID;
//    private final int documentID;
//    private final int requestNumber;
//    private final int versionID;
//    
//    private final String action;
//    private int beginning;
//    private int end;
//    
//    private String content;
//    
//    /*
//    public static enum Action{
//        INSERT,
//        BLOCK_INSERT,
//        DELETE,
//        BLOCK_DELETE,
//        ANCHOR_UPDATE,
//        NEW_DOC,
//        UPDATE_DOC_NAME,
//        BYE
//    }*/
//    
//    // Parses a server request text representation
//    // USER|REQ_NUM|DOC_ID|ACTION|BEG|END|CONTENT|VERSION_ID
//    public ServerRequestDQShiftFactor (String requestText) {
//        String[] elements = requestText.split("\\|");
//        
//        this.userID = elements[0];
//        this.requestNumber = Integer.valueOf(elements[1]);
//        this.documentID = Integer.valueOf(elements[2]); // Implement checking for invalid input
//        
//        this.action = elements[3]; // eventually make action use the enum
//        this.beginning = Integer.valueOf(elements[4]);
//        this.end = Integer.valueOf(elements[5]);
//        this.content = elements[6];
//        
//        this.versionID = Integer.valueOf(elements[7]);        
//    }
//    
//    public ServerRequestDQShiftFactor(ServerRequestDQShiftFactor request, int newVersionID) {
//        this.userID = request.userID;
//        this.requestNumber = request.requestNumber;
//        this.documentID = request.documentID;
//        
//        this.action = request.action;
//        this.beginning = request.beginning;
//        this.end = request.end;
//        this.content = request.content;
//        
//        this.versionID = newVersionID;
//    }
//    
//    
//    public int applyUpdate(ServerRequestDQShiftFactor previous, int shiftFactor) {
//        
//        // apply INSERT to INSERT
//        if(previous.getAction().equals("INSERT") && this.getAction().equals("INSERT")) {
//            // inserting in front of an insert
//            if(previous.getBeginning() < this.getBeginning()) {
//                this.beginning += previous.getContent().length();
//                //this.end += previous.getContent().length();
//            }
//            
//            this.beginning += shiftFactor;
//            return shiftFactor;
//        } 
//            
//        // apply INSERT to DELETE
//        else if(previous.getAction().equals("INSERT") && this.getAction().equals("DELETE")) {
//            // inserting in front of a delete
//            if(previous.getBeginning() <= this.getBeginning()) {
//                this.beginning += previous.getContent().length();
//                this.end += previous.getContent().length();
//            }
//            // inserting inside a delete
//            else if(previous.getBeginning() < this.getEnd()) {
//                this.end += previous.getContent().length();
//            }
//            
//            this.beginning += shiftFactor;
//            this.end += shiftFactor;
//            return shiftFactor;
//        } 
//        
//        // apply DELETE to INSERT
//        else if(previous.getAction().equals("DELETE") && this.getAction().equals("INSERT")) {
//            // delete completely precedes insert
//            if(previous.getEnd() <= this.getBeginning()) {
//                this.beginning -= (previous.getEnd()-previous.getBeginning());
//            }
//            // delete contains insert
//            else if(previous.getBeginning() < this.getBeginning()) {
//                this.beginning = previous.getBeginning(); // insert at the beginning of the deleted segment
//            }                
//            // previous.getBeginning() == this.getBeginning() -> not affected, do nothing
//        
//            this.beginning += shiftFactor;
//            return shiftFactor;
//        }
//        
//        // apply DELETE to DELETE
//        else if(previous.getAction().equals("DELETE") && this.getAction().equals("DELETE")) {
//            // apply shiftFactor before comparing with applied change:
//            this.beginning += shiftFactor;
//            this.end += shiftFactor;
//            
//            int beginningDeleteLength = (this.getEnd() - this.getBeginning());
//            
//            // previous delete completely precedes current delete
//            if(previous.getEnd() <= this.getBeginning()) {
//                this.beginning -= (previous.getEnd()-previous.getBeginning());
//                this.end -= (previous.getEnd()-previous.getBeginning());
//            }
//            
//            // previous delete partially overlaps current delete
//            else if(previous.getBeginning() < this.getEnd()){
//                // previous starts before current
//                if(previous.getBeginning() < this.getBeginning()) {
//                    this.beginning = previous.getBeginning();
//                }
//                // previous starts after current
//                else {
//                    // don't change beginning
//                }
//                
//                // previous ends before current or at the same place
//                if(previous.getEnd() <= this.getEnd()) {
//                    this.end -= (previous.getEnd()-previous.getBeginning());
//                }
//                // previous ends after current
//                else {
//                    this.end = previous.getBeginning();
//                }
//            }
//            // previous.getBeginning() == this.getBeginning() -> not affected, do nothing
//            
//            int finalDeleteLength = (this.getEnd() - this.getBeginning());
//            int deleteLengthChange = finalDeleteLength - beginningDeleteLength;
//            
//            return shiftFactor + deleteLengthChange;
//        }
//        
//        else {
//            System.out.println("Update not supported: " + previous.getAction());
//            return shiftFactor;
//        }
//    }
//    
//    /*
//    public void undoUpdate(ServerRequestDQ following) {
//        if(following.getAction().equals("INSERT")) {
//            if(following.getBeginning() < this.getBeginning()) {
//                this.beginning -= following.getContent().length();
//                // end is not used for inserts
//            }
//            
//        } else {
//            System.out.println("Update not supported: " + previous.getAction());
//        }
//    }*/
//    
//    public String getAction() { return action; }
//    public int getBeginning() { return beginning; }
//    public int getEnd() { return end; }
//    public String getContent() { return content; }
//    public int getVersionID() { return versionID; }
//    public String getUserID() { return userID; }
//    public int getRequestNumber() { return requestNumber; }
//    
//    
//    
//    @Override
//    public String toString() {
//        return "" + userID + "|" + requestNumber + "|" + documentID + "|"
//                + action + "|" + beginning + "|" + end + "|"
//                + content + "|" + versionID;
//    }
//    
//}
