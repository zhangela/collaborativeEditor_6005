package Tests;

public class SystemTests {

    
/*
 * SYSTEM TEST STRATEGY
 * 
 * System tests are slightly less formal than other tests. While some are  
 * quite specific, a portion are simple "stress tests" where the users
 * do everything in their power to break the system. Note that tests are 
 * broken into sequences, where the test relies on the actions that precede it
 * in the sequence. ==== indicates a test.
 *     
 *     
 * ===============================================    
 * OPENING THE CONNECTION
 * ===============================================
 * 
 *
 * Sequence 1: Renaming and Document List Updates
 * 1) Start the server, open User0.
 * 2) Create a document "doc1" from User0
 * 3) open User1. 
 * 4) ==== check User1 sees doc1 in loaded docs====
 * 5) open User2 first, then create new doc "doc2" from User1
 * 6) ==== check User2 sees doc2 in their doclist ====
 * 7) move User0 to Open/Create Panel, ==== check "doc2" is also visible from User0 (not initially in Open/Create) ====
 * 8) move User1 to Open/Create Panel.
 * 9) open doc1 in User2. 
 * 10) move User0 to doc1. Rename doc1 to "renamedDoc1"
 * 11) ==== check that User1 seed the name change in their docList====
 * 12) ==== check User2 sees the name change in their open doc1 ====
 * 
 * Sequence 2: Saving document changes
 * 1) Start the server, open User0
 * 2) Create a document "doc1" from User0
 * 3) add the message hello to doc1
 * 4) open a User1, open doc1 ====check that doc contains "hello"====
 * 5) add the line "what's up" to doc1 from User1. ==== check that it appears for User0 ====
 * 6) Close doc1 from both User0 and User1.
 * 7) Open doc1 from User0 === check that doc1 contains "hello what's up" ====
 * 
 * Sequence 3: Simultaneous Edits
 * 1) Start server, open 3 users, open to same document.
 * 2) All go to own line, begin typing ==== check that it displays properly ====
 * 3) All type on SAME line ====check that displays what is typed properly, same copy, and carat updates well ====
 * 4) One user highlights a section of the text, other users type, copy-paste, delete, block-delete INSIDE the selection
 *       ==== check that selection updates to include the new text ====
 * 5) Delete a selection which includes the carats of the other users ====check that carats are moved to beginning of deleted area ====
 * 6) One user highlights a selection, other user deletes the entirety of the selection ====check carat is moved to beginning of deleted area ====
 * 7) A user enters a pipe, ==== error message appears warning the user, pipe disappears, carat is moved to the beginning of the text pane ====
 * 
 * Stress Tests-- successful is nothing breaks
 * 1) Have a number of users (>3) connect to the same document, type as fast as humanly possible (multiple keyboards encouraged)
 * 2) Have many users connect to multiple documents, again type a bunch of things
 * 3) Open many many documents, load many many tabs.
 * 4) Type in Czech, or chinese. will display ascii equivalents.
 *   
 *
 * 
 *     
 */

}
