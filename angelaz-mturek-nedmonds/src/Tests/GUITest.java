package Tests;

public class GUITest {

    
/*
 * GUI TEST STRATEGY:
 * 
 * ===============================================
 * SERVER INPUT PORT NUMBER DIALOG
 * ===============================================
 * 
 * 
 * 1) Input a valid, free port number -> server starts
 * 2) Leave the port number blank -> server is not started. error dialog
 * 3) Input an invalid port number -> server is not started. error dialog.
 * 4) Input a port number that is already taken -> server is not started. error dialog.
 * 
 * 
 * 
 * ==================================================================
 * EDITOR INPUT SERVER ADDRESS AND PORT NUMBER DIALOG
 * ==================================================================
 * 
 * Don't start server.
 * 
 * 1) input server address and random port number -> error dialog
 * 
 * 
 * First start server on local computer.
 * 
 * 1) input "localhost" and correct port number -> connect to server. Editor window.
 * 2) input "localhost" and incorrect port number -> does not connect. error dialog.
 * 3) input blank server address (default to localhost) and correct port number -> connect to server. Editor window.
 * 4) input blank server address and blank port number -> does not connect. error dialog.
 * 
 * 
 * Start server on another computer.
 * 
 * 1) input correct server address and port number -> connects to server. Editor window.
 * 2) input incorrect server address and/or port number -> does not connect. error dialog.
 *     
 *     
 * ===============================================    
 * MAIN EDITOR GUI
 * ===============================================
 * 
 *
 * First start server and editor.
 * 
 * 1) switch to "Open/Create" tab, double click on "There are no documents on the server" -> nothing happens
 * 2) switch to "Open/Create" tab, click on "There are no documents on the server" and click "open" -> nothing happens
 * 3) switch to "Open/Create" tab, enter <new document name> in the "Enter Document Name" textfield and  click create -> creates the document
 * 4) switch to "Open/Create" tab, leave the textfield blank and click "create" -> creates new document with name "Document"
 * 5) switch to "Open/Create" tab, from the list below "Open Existing Documents", double click or click "open" on an unopened document with existing content-> opens the document in new tab and loads existing content
 * 6) switch to "Open/Create" tab, from the list below "Open Existing Documents", double click or click "open" on an already opened -> pop up window "That document is already open!"
 * 7) switch to one of the document tabs, click on the "x" on the tab title -> closes that document, switches back to the "Open/Create" tab
 * 8) switch to one of the document tabs, start typing in the textfield -> text updates instantaneously
 * 9) switch to one of the document tabs, click on "rename" -> rename dialog pops up, enter <new name>, clicks "Ok" -> dialog closes, document is renamed
 * 10) switch to one of the document tabs, click on "rename" -> rename dialog pops up, do not enter anything, clicks "Ok" ->  give notice, does not rename.
 * 11) switch to one of the document tabs, click on "rename" -> rename dialog pops up, enter the name of an existing document, clicks "Ok" -> error dialog, does not rename.
 * 12) Test typing functionality
 *      a) type regular characters ( a, b, etc)
 *      b) enter newlines
 *      c) move cursor with mouse and arrow keys
 *      d) delete characters
 *      e) delete sections of text
 *      f) copy paste in text
 *      g) type pipe (should warn about typing pipes and delete it, also moves cursor to the beginning of the document)
 *      h) type tilda, creates new line.
 *     
 */
    
    
    
    
    
    
    
    
    
    
}
