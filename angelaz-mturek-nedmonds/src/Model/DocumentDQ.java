package Model;

import java.util.ArrayList;
import java.util.List;

/**
 * DocumentDQ represents the client side of a document.
 * It stores the most current synchronized version and
 * a queue of local updates to be applied to it before
 * it is displayed to the user.
 * 
 * @author Michael Turek
 */
public class DocumentDQ {
    private String userID;
    
    private String syncCopy;
    private int syncVersion;
    
    private String lastMessageReceived;
        
    private List<ServerRequestDQ> localQueue;
    
    /**
     * Creates a new instance of DocumentDQ
     * and saves the owner ID into a private field
     * 
     * @param userID - the userID of the client
     */
    public DocumentDQ(String userID) {
        this.syncCopy = "";
        this.syncVersion = 0;
        
        this.userID = userID;
        this.localQueue = new ArrayList<ServerRequestDQ>();
        
        this.lastMessageReceived = "|0|0|INSERT|0|0||0";
    }
    
    /**
     * Applies the queue of local changes to the current 
     * synchronized copy and returns it for display
     * 
     * @return String - a viewCopy to be displayed by the GUI
     */
    public synchronized String getView() {
        String viewCopy = syncCopy;
        
        for(ServerRequestDQ localChange : localQueue) {
            viewCopy = applyChange(viewCopy, localChange);
        }
        
        return viewCopy;
    }
    
    /**
    * Returns the current syncCopy of this document
    * This is mostly for debugging purposes as the syncCopy
    * represents internal data that is never actually displayed 
    * by the client
    * 
    * @return String - current syncCopy
    */
    public synchronized String getSyncCopy() {
        return syncCopy;
    }
    
    /**
     * Returns the version ID of the most recent update
     * to the synchronized copy
     * 
     * @return int - current syncVersion
     */
    public synchronized int getCurrentVersion() { 
        return syncVersion; 
    }
    
    /**
     * Adds a request to the local queue to be displayed
     * to the user immediately after inputting it
     * 
     * @param request - the request to be added
     */
    public synchronized void addRequest(String request) {
        localQueue.add(new ServerRequestDQ(request));  
    }
    
    /**
     * Accepts a request that's targeted towards this document
     * and processes it
     * 
     * @param requestText - request to be processed
     */
    public synchronized void pushRequest(String requestText) {
        ServerRequestDQ request = new ServerRequestDQ(requestText);
        lastMessageReceived = requestText;
        
        // Update the synchronized version
        syncCopy = applyChange(syncCopy, request);
        syncVersion = request.getVersionID();
        
        // If own request -> remove and continue
        if(request.getUserID().equals(userID)) {
            if(request.getRequestNumber() == localQueue.get(0).getRequestNumber()) {
                localQueue.remove(0);
                return;
            } else {
                throw new RuntimeException("Request ordering error");
            }
        }
       
        // If someone else's request, update local queue
        for(ServerRequestDQ localChange : localQueue) {
            localChange.applyUpdate(request);
        }       
    }
    
    /**
     * Applies the specified edit request to a String and returns the
     * new version
     * 
     * @param source - the beginning String which we're applying the change to
     * @param request - the change to be applied
     * @return String - the final version of the String after the change is applied
     */
    public String applyChange(String source, ServerRequestDQ request) {
        StringBuilder result = new StringBuilder(source);
        
        if(request.getAction().equals("INSERT")) {
            result.insert(request.getBeginning(), request.getContent());
        } else if(request.getAction().equals("DELETE")) {
            result.delete(request.getBeginning(), request.getEnd());
        } else {
            System.out.println("Action not supported, ignoring...");
        }
        
        return result.toString();
    }
    
    /**
     * Takes in a dummy request produced by the GUI to represent
     * the current selection and applies the last request received
     * from the server to it to get a new selection
     * 
     * @param request to be updated
     * @return String - the updated request
     */
    public synchronized String updateSelection(String requestText) {
        ServerRequestDQ currentSelection = new ServerRequestDQ(requestText);
        ServerRequestDQ lastRequestReceived = new ServerRequestDQ(lastMessageReceived);
        
        if(lastRequestReceived.getUserID().equals(userID)) {
            return requestText;
        }
        
        currentSelection.applyUpdate(lastRequestReceived);
        return currentSelection.toString();
    }
}
