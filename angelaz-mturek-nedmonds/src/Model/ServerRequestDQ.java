package Model;

/**
 * ServerRequestDQ represents an insert/delete request sent to or
 * received from the server. String->ServerRequestDQ constructor is provided
 * in addition to a symmetrical toString() method to ensure complete 
 * interchangeability of a String representation following the
 * specified grammar and an instance of ServerRequestDQ. 
 * 
 * Required request format: 
 * USER|REQ_NUM|DOC_ID|ACTION|BEG|END|CONTENT|VERSION_ID
 * 
 * This class also manages request-request interference and
 * versioning to ensure proper synchronization.
 */
public class ServerRequestDQ {
    private String userID;
    private final int documentID;
    private final int requestNumber;
    private final int versionID;
    
    private final String action;
    private int beginning;
    private int end;
    
    private String content;

    /** 
     * Parses a server request passed in in its String representation
     * and creates a new ServerRequestDQ object
     * 
     * grammar: USER|REQ_NUM|DOC_ID|ACTION|BEG|END|CONTENT|VERSION_ID
     * 
     * @param requestText - request to be parsed
     */
    public ServerRequestDQ (String requestText) {
        if(requestText == null || requestText.equals("")) {
            throw new RuntimeException("Invalid request: empty or null");
        }
        String[] elements = requestText.split("\\|");
        
        if(elements.length != 8) {
            throw new RuntimeException("Invalid request: wrong number of elements");
        }
        
        this.userID = elements[0];
        this.requestNumber = Integer.valueOf(elements[1]);
        this.documentID = Integer.valueOf(elements[2]);
        
        this.action = elements[3];
        this.beginning = Integer.valueOf(elements[4]);
        this.end = Integer.valueOf(elements[5]);
        this.content = elements[6];
        
        this.versionID = Integer.valueOf(elements[7]);    
        
        if(requestNumber < 0 || beginning < 0 || end < 0 || versionID < 0) {
            throw new RuntimeException("Invalid request: negative numeral");
        }
        
        if(!(action.equals("INSERT") || action.equals("DELETE"))) {
            throw new RuntimeException("Invalid request: unrecognized action");
        }
        
    }
    
    /**
     * Copies an existing request but changes the versionID
     * 
     * @param request - request to be copied
     * @param newVersionID - the new versionID
     */
    public ServerRequestDQ(ServerRequestDQ request, int newVersionID) {
        this.userID = request.userID;
        this.requestNumber = request.requestNumber;
        this.documentID = request.documentID;
        
        this.action = request.action;
        this.beginning = request.beginning;
        this.end = request.end;
        this.content = request.content;
        
        this.versionID = newVersionID;
        
        if(versionID < 0) {
            throw new RuntimeException("Invalid request: negative version number");
        }
    }
    
    /**
     * Applies an update that's received after the current one
     * but which actually precedes it in the logic of the server
     * -> updates the current update to match the version produced 
     * by the previous update.
     * 
     * E.g. Someone else submitted an update that inserts three characters
     * at index 3 and concurrently I made an update that inserts a character
     * at index 15. Since their update was processed first by the server, 
     * I want to "update" mine by increasing its index by 3 as if I made it
     * to the more recent version.
     * 
     * @param previous - update to be applied
     */
    public synchronized void applyUpdate(ServerRequestDQ previous) {
        
        // apply INSERT to INSERT
        if(previous.getAction().equals("INSERT") && this.getAction().equals("INSERT")) {
            // inserting in front of an insert
            if(previous.getBeginning() < this.getBeginning()) {
                this.beginning += previous.getContent().length();
                //this.end += previous.getContent().length();
            }
        } 
            
        // apply INSERT to DELETE
        else if(previous.getAction().equals("INSERT") && this.getAction().equals("DELETE")) {
            // inserting in front of a delete
            if(previous.getBeginning() <= this.getBeginning()) {
                this.beginning += previous.getContent().length();
                this.end += previous.getContent().length();
            }
            // inserting inside a delete
            else if(previous.getBeginning() < this.getEnd()) {
                this.end += previous.getContent().length();
            }
        } 
        
        // apply DELETE to INSERT
        else if(previous.getAction().equals("DELETE") && this.getAction().equals("INSERT")) {
            // delete completely precedes insert
            if(previous.getEnd() <= this.getBeginning()) {
                this.beginning -= (previous.getEnd()-previous.getBeginning());
            }
            // delete contains insert
            else if(previous.getBeginning() < this.getBeginning()) {
                this.beginning = previous.getBeginning(); // insert at the beginning of the deleted segment
            }                
            // previous.getBeginning() == this.getBeginning() -> not affected, do nothing
        }
        
        // apply DELETE to DELETE
        else if(previous.getAction().equals("DELETE") && this.getAction().equals("DELETE")) {
            // previous delete completely precedes current delete
            if(previous.getEnd() <= this.getBeginning()) {
                this.beginning -= (previous.getEnd()-previous.getBeginning());
                this.end -= (previous.getEnd()-previous.getBeginning());
            }
            
            // previous delete partially overlaps current delete
            else if(previous.getBeginning() < this.getEnd()){
                // previous starts before current
                if(previous.getBeginning() < this.getBeginning()) {
                    this.beginning = previous.getBeginning();
                }
                // previous starts after current
                else {
                    // don't change beginning
                }
                
                // previous ends before current or at the same place
                if(previous.getEnd() <= this.getEnd()) {
                    this.end -= (previous.getEnd()-previous.getBeginning());
                }
                // previous ends after current
                else {
                    this.end = previous.getBeginning();
                }
            }
            // previous.getBeginning() == this.getBeginning() -> not affected, do nothing
        }
        
        else {
            System.out.println("Update not supported, ignoring...");
        }
    }
    
    /**
     * Returns the type of the action described by this object
     * @return String - the action type
     */
    public synchronized String getAction() {
        return action; 
    }
    
    /**
     * Returns the beginning index of this request
     * @return int - the beginning index
     */
    public synchronized int getBeginning() { 
        return beginning; 
    }
    
    /**
     * Returns the ending index of this request
     * @return int - the ending index
     */
    public synchronized int getEnd() { 
        return end; 
    }
    
    /**
     * Returns the content this request which is 
     * the String to be inserted for INSERT requests 
     * and irrelevant for DELETE requests
     * 
     * @return String - the content of the request
     */
    public synchronized String getContent() { 
        return content; 
    }
    
    /**
     * Returns the ID of the document this request modifies
     * @return int - the documentID
     */
    public synchronized int getDocumentID() { 
        return documentID; 
    }
    
    /**
     * Returns the version ID field of this request
     * For Server->Client messages this is the version ID
     * this request produces
     * For Client->Server messages this is the version ID
     * of the currently held syncCopy
     * 
     * @return int - the version ID
     */
    public synchronized int getVersionID() { 
        return versionID; 
    }
    
    /**
     * Returns the userID of the user who created this request
     * @return String - the user ID of the creator
     */
    public synchronized String getUserID() { 
        return userID; 
    }
    
    /**
     * Returns the request number assigned to this request
     * by the user who created it
     * 
     * @return int - the request number
     */
    public synchronized int getRequestNumber() { 
        return requestNumber; 
    }
    
    /**
     * Sets the user ID the the required one to 
     * allow for anonymization of messages
     * @param newUserID - newUserID to be set
     */
    public synchronized void setUserID(String newUserID) {
        this.userID = newUserID;
    }
    
    /**
     * Creates a String representation of the request which is 
     * completely interchangeable with a ServerRequestDQ instance
     * through the use of the ServerRequestDQ(String requestText)
     * constructor
     * 
     * @return String - a String representation of the request
     */
    @Override
    public synchronized String toString() {
        return "" + userID + "|" + requestNumber + "|" + documentID + "|"
                + action + "|" + beginning + "|" + end + "|"
                + content + "|" + versionID;
    }
    
}
