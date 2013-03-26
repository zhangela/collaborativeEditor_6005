//package Deprecated;
//
//public class ServerRequest {
//
//    
//    
//    private final Action action;
//    private final String user;
//    private char[] chars;
//    private final int doc;
//    private int loc;
//    private int locEnd;
//    private String newVal;
//    
//    public static enum Action{
//        INSERT,
//        BLOCK_INSERT,
//        DELETE,
//        BLOCK_DELETE,
//        ANCHOR_UPDATE,
//        NEW_DOC,
//        UPDATE_DOC_NAME,
//        BYE
//    }
//    
//    //for block-insert and insert and rename doc
//    public ServerRequest(String user, int doc,  Action action, String contents){
//        this.action = action;
//        this.user = user;
//        chars = contents.toCharArray();  
//        this.doc = doc;
//    }
//    
//    //for deletions and anchorUpdates
//    public ServerRequest(String user, int doc, Action action, int start){
//        this.action = action;
//        this.user = user;
//        this.loc = start;
//        this.doc = doc;
//    }
//    
//    //for block deletions
//    public ServerRequest(String user, int doc, Action action, int delstart, int delend){
//        this.action = action;
//        this.user = user;
//        this.loc = delstart;
//        this.locEnd = delend;
//        this.doc = doc;
//    }
//    
//    //for newDoc request
//    public ServerRequest(String user, Action action, String newName){
//        this.user = user;
//        this.action = action;
//        this.newVal = newName;
//        this.doc = -1;
//    }
//    
//    public ServerRequest(String user, Action action){
//        this.action = action;
//        this.user = user;
//        this.doc = -1;
//                
//    }
//    
//    public String toString(){
//        return this.user + " " + this.action;
//    }
//
//}
