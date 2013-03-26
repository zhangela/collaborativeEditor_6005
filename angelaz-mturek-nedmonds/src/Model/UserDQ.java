package Model;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * UserDQ is the main class in the model package,
 * It manages the current versions of the documents
 * and their updating on the client side.
 * 
 * Thread safety argument:
 * Even though thread-safety should not be an issue since
 * the GUI is designed in a completely single-threaded
 * way in all its interaction with the model, it is implemented
 * to satisfy all the thread-safety requirements:
 * 
 * All the methods in the Model classes (UserDQ, DocumentDQ, 
 * ServerRequestDQ) are synchronized when they access shared
 * data (applyChange does not need to be synchronized since 
 * all its data is self-contained). This locking scheme is 
 * very inefficient since it locks on the entire object
 * for all interactions but it is the safest way to prevent
 * concurrency bugs.
 *  
 * 
 * @author Michael Turek
 *
 */
public class UserDQ {
    private String userID;
    private int requestCounter;
        
    private List<String> serverBuffer;
    private Map<Integer, DocumentDQ> documents;
    
    /**
     * Creates a new UserDQ object with the specified userID
     * @param userID - the userID assigned by the server to this client
     */
    public UserDQ(String userID) {
        this.userID = userID;
        this.requestCounter = 0;

        this.serverBuffer = new ArrayList<String>();
        this.documents = new HashMap<Integer, DocumentDQ>();
    }
    
    /**
     * Registers a document with the UserDQ object so that 
     * all incoming requests can be properly redirected
     * 
     * @param documentID - document to be added
     */
    public synchronized void addDocument(int documentID) {
        documents.put(documentID, new DocumentDQ(userID));
    }
    
    /**
     * Returns the current viewCopy of the specified document
     * (viewCopy is what the user is supposed to see in his GUI)
     * @param documentID - the requested document
     * @return String - a user's view
     */
    public synchronized String getView(int documentID) {
       return documents.get(documentID).getView();
    }
    
    /**
     * Returns the current syncCopy held by a user
     * This is mostly for debugging purposes as the syncCopy
     * represents internal data that is never actually displayed 
     * by the client
     * 
     * @param documentID - requested document
     * @return String - current syncCopy
     */
    public synchronized String getSyncCopy(int documentID) {
       return documents.get(documentID).getSyncCopy();
    }
    
    /**
     * Returns the userID of the client using this model
     * @return userID of the client
     */
    public synchronized String getUserID() {
        return userID; 
    }

    /**
     * Forwards a request coming in from the server to its target document
     * For testing, this simulates event: message received by client 
     *                                    after being sent by the server
     * 
     * @param requestText - the request to be forwarded in String representation
     */
    public synchronized void pushRequest(String requestText) {
        ServerRequestDQ request = new ServerRequestDQ(requestText);
        
        documents.get(request.getDocumentID()).pushRequest(requestText);
    }
    
    /**
     * Creates a request to be sent to the server based on user's 
     * interaction with the GUI
     * For testing, this simulates event: Message sent by the user
     * 
     * request: USER|REQ_NUM|DOC_ID|ACTION|BEG|END|CONTENT|VERSION_ID
     * request body: ACTION|BEG|END|CONTENT
     * 
     * @param documentID - the document being edited
     * @param requestBody - a request body satisfying the specified grammar
     */
    public synchronized void createRequest(int documentID, String requestBody) {
        String fullRequestText = "" + userID + "|" + requestCounter + "|" + documentID + "|" 
                + requestBody + "|" + documents.get(documentID).getCurrentVersion();
        requestCounter++;
                
        documents.get(documentID).addRequest(fullRequestText);   
        serverBuffer.add(fullRequestText);
    }
    
    /**
     * Pulls a message to be sent to the server next from the serverBuffer
     * For testing, this simulates event: Request received by server
     * 
     * @return a message for the server
     */
    public synchronized String pullRequest() {
        if(serverBuffer.size() > 0) {
            return serverBuffer.remove(0);
        } else {
            return null;
        }
    }
    
    /**
     * Forwards the updateSelection requests to the document they are related to
     * 
     * @param requestText to be forwarded
     * @param documentID of the document the request it to be forwarded to
     * @return String - updated selection
     */
    public synchronized String updateSelection(String requestText) {
        ServerRequestDQ request = new ServerRequestDQ(requestText);
        
        return documents.get(request.getDocumentID()).updateSelection(requestText);
    }
}
