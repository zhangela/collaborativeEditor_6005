package Tests;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import org.junit.Test;

import Server.SEditServer;

public class ServerTest {
    
    /*
     * Server Test Strategy:
     * 
     * 1) one user connects to server
     * 2) two users connect to the server
     * 3) one user has no documents
     * 4) one user has one document
     * 5) one user has multiple documents
     * 6) one user has one document with multiple inserts
     * 7) one user has one document and does many edits
     * 8) one user has multiple documents and does many edits
     * 9) multiple users have one documents and do multiple interleaving edits
     * 10) multiple users have multiple documents and do multiple interleaving edits
     * 11) multiple users have multiple documents and rename multiple times
     * 12) multiple users have multiple documents and rename unsuccessfully
     * 13) multiple users have multiple documents and one user closes one document
     * 14) invalid control request -> server ignores it
     * 15) invalid edit request -> server ignores it
     */
    private String serverToConnectTo = "localhost";
    private int PORT_NUMBER = 48690;

    public void startServer(final SEditServer server) {
        Thread backgroundThread = new Thread(new Runnable() {
            public void run() {
                server.serve();
            }
        });
        backgroundThread.start();
    }

    public void stopServer(final SEditServer server, PrintWriter out) throws IOException {
        try {
            out.println("BYE");
            server.kill();
        } catch (RuntimeException e) {
            //do nothing b.c. it is intended
        }
    }
    
    @Test
    public void oneUserConnectToServer() throws UnknownHostException, IOException {
        
        SEditServer server = new SEditServer(PORT_NUMBER);
        startServer(server);
        
        Socket serverSocket = new Socket(serverToConnectTo, PORT_NUMBER);
        PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(
                serverSocket.getInputStream()));

        String helloMessage = in.readLine();
        assertEquals("HELLO|User0", helloMessage);

        stopServer(server, out);
    }
    
    
    @Test
    public void twoUsersConnectToServer() throws UnknownHostException, IOException {
                
        SEditServer server = new SEditServer(
                PORT_NUMBER);
        startServer(server);
        
        Socket serverSocket2 = new Socket(serverToConnectTo, PORT_NUMBER);
        PrintWriter out = new PrintWriter(serverSocket2.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket2.getInputStream()));
        
        String helloMessage = in.readLine();
        assertEquals("HELLO|User0",helloMessage);
        
        stopServer(server, out);
    }
    
    @Test
    public void oneUserNoDucment() throws UnknownHostException, IOException {
                
        SEditServer server = new SEditServer(PORT_NUMBER);
        startServer(server);
        Socket serverSocket = new Socket(serverToConnectTo, PORT_NUMBER);
        PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(
                serverSocket.getInputStream()));

        String helloMessage = in.readLine();
        String userID = helloMessage.split("\\|")[1];
        out.println("CONTROL|" + userID + "|GETDOCLIST");

        assertEquals("CONTROL|DOCLIST", in.readLine());
        stopServer(server, out);
        
    }
    
    
    @Test
    public void oneUserOneDocument() throws UnknownHostException, IOException {
                
        SEditServer server = new SEditServer(PORT_NUMBER);
        startServer(server);
        Socket serverSocket = new Socket(serverToConnectTo, PORT_NUMBER);
        PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(
                serverSocket.getInputStream()));

        String helloMessage = in.readLine();
        String userID = helloMessage.split("\\|")[1];
        out.println("CONTROL|" + userID + "|GETDOCLIST");
        in.readLine();
        out.println("CONTROL|" + userID + "|REQUESTNEW|first document");
        assertEquals("CONTROL|REQNEWPROCESSED|0~first document", in.readLine());
        out.println("CONTROL|" + userID + "|GETDOCLIST");
        System.out.println(in.readLine());
        stopServer(server, out);
        
    }
    
    
    @Test
    public void oneUserTwoDocuments() throws UnknownHostException, IOException {
                
        SEditServer server = new SEditServer(PORT_NUMBER);
        startServer(server);
        Socket serverSocket = new Socket(serverToConnectTo, PORT_NUMBER);
        PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(
                serverSocket.getInputStream()));

        String helloMessage = in.readLine();
        String userID = helloMessage.split("\\|")[1];
        out.println("CONTROL|" + userID + "|GETDOCLIST");
        in.readLine();
        out.println("CONTROL|" + userID + "|REQUESTNEW|first document");
        in.readLine();
        out.println("CONTROL|" + userID + "|REQUESTNEW|second document");
        assertEquals("CONTROL|DOCLIST|0~first document", in.readLine());
        out.println("CONTROL|" + userID + "|GETDOCLIST");
        System.out.println(in.readLine());
        stopServer(server, out);
        
    }
    
    @Test
    public void oneUserOneDocumentMultipleInserts() throws UnknownHostException, IOException {
                
        SEditServer server = new SEditServer(PORT_NUMBER);
        startServer(server);
        Socket serverSocket = new Socket(serverToConnectTo, PORT_NUMBER);
        PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(
                serverSocket.getInputStream()));

        String helloMessage = in.readLine();
        String userID = helloMessage.split("\\|")[1];
        out.println("CONTROL|" + userID + "|GETDOCLIST");
        in.readLine();
        out.println("CONTROL|" + userID + "|REQUESTNEW|first document");
        in.readLine();
        out.println("CONTROL|" + userID + "|REQUESTNEW|second document");
        in.readLine();
        out.println("CONTROL|" + userID + "|LOAD|0");
        out.println(userID + "|0|0|INSERT|0|0|my name is Angela|0");
        
        assertEquals("CONTROL|REQNEWPROCESSED|1~second document", in.readLine());
        
        out.println(userID + "|1|0|INSERT|3|0|first|0");
        assertEquals("CONTROL|DOCLIST|0~first document|1~second document", in.readLine());

        stopServer(server, out);
    }
    
    
    @Test
    public void oneUserOneDocumentOneInsertAndMultipleDeletes() throws UnknownHostException, IOException {
                
        SEditServer server = new SEditServer(PORT_NUMBER);
        startServer(server);
        Socket serverSocket = new Socket(serverToConnectTo, PORT_NUMBER);
        PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(
                serverSocket.getInputStream()));

        String helloMessage = in.readLine();
        String userID = helloMessage.split("\\|")[1];
        out.println("CONTROL|" + userID + "|GETDOCLIST");
        in.readLine();
        out.println("CONTROL|" + userID + "|REQUESTNEW|first document");
        in.readLine();
        out.println("CONTROL|" + userID + "|REQUESTNEW|second document");
        in.readLine();
        out.println("CONTROL|" + userID + "|LOAD|0");
        out.println(userID + "|0|0|INSERT|0|0|my name is Angela|0");
        
        assertEquals("CONTROL|REQNEWPROCESSED|1~second document", in.readLine());
       
        out.println(userID + "|1|0|DELETE|3|7|IRREVELANT|0");
        assertEquals("CONTROL|DOCLIST|0~first document|1~second document", in.readLine());

        out.println(userID + "|1|0|DELETE|0|2|IRREVELANT|0");
        assertEquals("User0|0|0|INSERT|0|0|my name is Angela|1", in.readLine());

        stopServer(server, out);
    }

    
    @Test
    public void oneUserMultipleDocumentsEdits() throws UnknownHostException, IOException {
                
        SEditServer server = new SEditServer(PORT_NUMBER);
        startServer(server);
        Socket serverSocket = new Socket(serverToConnectTo, PORT_NUMBER);
        PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(
                serverSocket.getInputStream()));

        String helloMessage = in.readLine();
        String userID = helloMessage.split("\\|")[1];
        out.println("CONTROL|" + userID + "|GETDOCLIST");
        in.readLine();
        out.println("CONTROL|" + userID + "|REQUESTNEW|first document");
        in.readLine();
        out.println("CONTROL|" + userID + "|REQUESTNEW|second document");
        in.readLine();
        out.println("CONTROL|" + userID + "|LOAD|0");
        out.println(userID + "|0|0|INSERT|0|0|my name is Angela|0");
        assertEquals("CONTROL|REQNEWPROCESSED|1~second document", in.readLine());
        
        out.println("CONTROL|" + userID + "|LOAD|1");
        out.println(userID + "|1|1|INSERT|3|3|yo dog|0");
        assertEquals("CONTROL|DOCLIST|0~first document|1~second document", in.readLine());

        out.println(userID + "|2|0|INSERT|3|3|wussup|0");
        assertEquals("User0|0|0|INSERT|0|0|my name is Angela|1", in.readLine());

        stopServer(server, out);
    }
    
    
    @Test
    public void multipleUsersOneDocumentEdits() throws UnknownHostException, IOException, InterruptedException {
                
        SEditServer server = new SEditServer(PORT_NUMBER);
        startServer(server);
        Socket serverSocket1 = new Socket(serverToConnectTo, PORT_NUMBER);
        Thread.sleep(100);
        Socket serverSocket2 = new Socket(serverToConnectTo, PORT_NUMBER);

        PrintWriter out1 = new PrintWriter(serverSocket1.getOutputStream(), true);
        BufferedReader in1 = new BufferedReader(new InputStreamReader(
                serverSocket1.getInputStream()));

        PrintWriter out2 = new PrintWriter(serverSocket2.getOutputStream(), true);
        BufferedReader in2 = new BufferedReader(new InputStreamReader(
                serverSocket2.getInputStream()));
        
        Thread.sleep(100);
        out1.println("CONTROL|User0|GETDOCLIST");
        out1.println("CONTROL|User0|REQUESTNEW|first document");
        Thread.sleep(100);
        out1.println("CONTROL|User0|LOAD|0");
        out1.println("User0|0|0|INSERT|0|0|my name is Angela|0");
        Thread.sleep(100);
        out2.println("CONTROL|User1|LOAD|0");
        out2.println("User1|1|0|DELETE|3|7|IRREVELANT|0");
        
        assertEquals("HELLO|User0", in1.readLine());
        assertEquals("CONTROL|DOCLIST", in1.readLine());
        assertEquals("CONTROL|REQNEWPROCESSED|0~first document", in1.readLine());
        assertEquals("CONTROL|DOCLIST|0~first document", in1.readLine());
        assertEquals("User0|0|0|INSERT|0|0|my name is Angela|1", in1.readLine());
        
        assertEquals("HELLO|User1", in2.readLine());
        assertEquals("CONTROL|DOCLIST|0~first document", in2.readLine());
        assertEquals("LoadUser|0|0|INSERT|0|0|my name is Angela|1", in2.readLine());
        assertEquals("User1|1|0|DELETE|20|24|IRREVELANT|2", in2.readLine());

        out2.println("BYE"); //the second client exits first
        stopServer(server, out1);
    }
    
    
    @Test
    public void multipleUsersMultipleDocumentEdits() throws UnknownHostException, IOException, InterruptedException {
                
        SEditServer server = new SEditServer(PORT_NUMBER);
        startServer(server);
        Socket serverSocket1 = new Socket(serverToConnectTo, PORT_NUMBER);
        Thread.sleep(100);
        Socket serverSocket2 = new Socket(serverToConnectTo, PORT_NUMBER);

        PrintWriter out1 = new PrintWriter(serverSocket1.getOutputStream(), true);
        BufferedReader in1 = new BufferedReader(new InputStreamReader(
                serverSocket1.getInputStream()));

        PrintWriter out2 = new PrintWriter(serverSocket2.getOutputStream(), true);
        BufferedReader in2 = new BufferedReader(new InputStreamReader(
                serverSocket2.getInputStream()));
        
        Thread.sleep(100);
        out1.println("CONTROL|User0|GETDOCLIST");
        out1.println("CONTROL|User0|REQUESTNEW|first document");
        out1.println("CONTROL|User0|LOAD|0");
        out1.println("User0|0|0|INSERT|0|0|my name is Angela|0");
        
        Thread.sleep(100);
        out2.println("CONTROL|User1|LOAD|0");
        out2.println("CONTROL|User1|REQUESTNEW|second document");
        Thread.sleep(100);
        out2.println("CONTROL|User1|LOAD|1");
        out2.println("User1|0|0|DELETE|3|7|IRREVELANT|0");
        out2.println("User1|1|1|INSERT|0|0|turek is a ginger.|0");
        
        Thread.sleep(100);
        out1.println("CONTROL|User0|GETDOCLIST");
        out1.println("CONTROL|User0|LOAD|1");
        out1.println("User0|1|1|INSERT|0|0|michael |0");
        
        Thread.sleep(100);
        out2.println("CONTROL|User1|GETDOCLIST");
        out2.println("User1|2|1|DELETE|0|8|IRRELEVANT|0");
        
        assertEquals("HELLO|User0", in1.readLine());
        assertEquals("CONTROL|DOCLIST", in1.readLine());
        assertEquals("CONTROL|REQNEWPROCESSED|0~first document", in1.readLine());
        assertEquals("CONTROL|DOCLIST|0~first document", in1.readLine());
        assertEquals("User0|0|0|INSERT|0|0|my name is Angela|1", in1.readLine());
        assertEquals("CONTROL|DOCLIST|0~first document|1~second document", in1.readLine());
        assertEquals("User1|0|0|DELETE|20|24|IRREVELANT|2", in1.readLine());
        assertEquals("CONTROL|DOCLIST|0~first document|1~second document", in1.readLine());
        assertEquals("LoadUser|1|1|INSERT|0|0|turek is a ginger.|1", in1.readLine());
        assertEquals("User0|1|1|INSERT|0|0|michael |2", in1.readLine());
        assertEquals("User1|2|1|DELETE|8|16|IRRELEVANT|3", in1.readLine());
        
        assertEquals("HELLO|User1", in2.readLine());
        assertEquals("CONTROL|DOCLIST|0~first document", in2.readLine());
        assertEquals("LoadUser|0|0|INSERT|0|0|my name is Angela|1", in2.readLine());
        assertEquals("CONTROL|REQNEWPROCESSED|1~second document", in2.readLine());
        assertEquals("CONTROL|DOCLIST|0~first document|1~second document", in2.readLine());
        assertEquals("User1|0|0|DELETE|20|24|IRREVELANT|2", in2.readLine());
        assertEquals("User1|1|1|INSERT|0|0|turek is a ginger.|1", in2.readLine());
        assertEquals("User0|1|1|INSERT|0|0|michael |2", in2.readLine());
        assertEquals("CONTROL|DOCLIST|0~first document|1~second document", in2.readLine());
        assertEquals("User1|2|1|DELETE|8|16|IRRELEVANT|3", in2.readLine());

        out2.println("BYE"); //the second client exits first
        stopServer(server, out1);
    }
    
    @Test
    public void multipleUsersMultipleDocumentsRename() throws UnknownHostException, IOException, InterruptedException {
                
        SEditServer server = new SEditServer(PORT_NUMBER);
        startServer(server);
        Socket serverSocket1 = new Socket(serverToConnectTo, PORT_NUMBER);
        Thread.sleep(100);
        Socket serverSocket2 = new Socket(serverToConnectTo, PORT_NUMBER);

        PrintWriter out1 = new PrintWriter(serverSocket1.getOutputStream(), true);
        BufferedReader in1 = new BufferedReader(new InputStreamReader(
                serverSocket1.getInputStream()));

        Thread.sleep(100);
        PrintWriter out2 = new PrintWriter(serverSocket2.getOutputStream(), true);
        BufferedReader in2 = new BufferedReader(new InputStreamReader(
                serverSocket2.getInputStream()));
        
        out1.println("CONTROL|User0|REQUESTNEW|first document");
        out1.println("CONTROL|User0|LOAD|0");
        
        Thread.sleep(100);
        out2.println("CONTROL|User1|LOAD|0");
        out2.println("CONTROL|User1|REQUESTNEW|second document");
        out2.println("CONTROL|User1|LOAD|1");
        out2.println("CONTROL|User1|RENAME|0~1st doc");
        
        Thread.sleep(100);
        out1.println("CONTROL|User0|LOAD|1");
        out1.println("CONTROL|User0|RENAME|1~2nd doc");
        out1.println("CONTROL|User0|RENAME|0~FIRST doc");
        Thread.sleep(100);
        
        assertEquals("HELLO|User0", in1.readLine());
        assertEquals("CONTROL|REQNEWPROCESSED|0~first document", in1.readLine());
        assertEquals("CONTROL|DOCLIST|0~first document", in1.readLine());
        assertEquals("CONTROL|DOCLIST|0~first document|1~second document", in1.readLine());
        assertEquals("CONTROL|DOCRENAMED|0~1st doc", in1.readLine());
        assertEquals("CONTROL|DOCLIST|0~1st doc|1~second document", in1.readLine());
        assertEquals("CONTROL|DOCRENAMED|1~2nd doc", in1.readLine());
        assertEquals("CONTROL|DOCLIST|0~1st doc|1~2nd doc", in1.readLine());
        assertEquals("CONTROL|DOCRENAMED|0~FIRST doc", in1.readLine());

        assertEquals("HELLO|User1", in2.readLine());
        assertEquals("CONTROL|DOCLIST|0~first document", in2.readLine());
        assertEquals("CONTROL|REQNEWPROCESSED|1~second document", in2.readLine());
        assertEquals("CONTROL|DOCLIST|0~first document|1~second document", in2.readLine());
        assertEquals("CONTROL|DOCRENAMED|0~1st doc", in2.readLine());
        assertEquals("CONTROL|DOCLIST|0~1st doc|1~second document", in2.readLine());
        assertEquals("CONTROL|DOCRENAMED|1~2nd doc", in2.readLine());
        assertEquals("CONTROL|DOCLIST|0~1st doc|1~2nd doc", in2.readLine());
        assertEquals("CONTROL|DOCRENAMED|0~FIRST doc", in2.readLine());

        out2.println("BYE"); //the second client exits first
        stopServer(server, out1);
    }
    
    @Test
    public void multipleUsersMultipleDocumentsRenameFail() throws UnknownHostException, IOException, InterruptedException {
                
        SEditServer server = new SEditServer(PORT_NUMBER);
        startServer(server);
        Socket serverSocket1 = new Socket(serverToConnectTo, PORT_NUMBER);
        Thread.sleep(100);
        Socket serverSocket2 = new Socket(serverToConnectTo, PORT_NUMBER);

        PrintWriter out1 = new PrintWriter(serverSocket1.getOutputStream(), true);
        BufferedReader in1 = new BufferedReader(new InputStreamReader(
                serverSocket1.getInputStream()));

        PrintWriter out2 = new PrintWriter(serverSocket2.getOutputStream(), true);
        BufferedReader in2 = new BufferedReader(new InputStreamReader(
                serverSocket2.getInputStream()));
        
        Thread.sleep(100);
        out1.println("CONTROL|User0|REQUESTNEW|first document");
        Thread.sleep(100);
        out1.println("CONTROL|User0|LOAD|0");
        
        Thread.sleep(100);
        out2.println("CONTROL|User1|LOAD|0");
        Thread.sleep(100);
        out2.println("CONTROL|User1|REQUESTNEW|second document");
        Thread.sleep(100);
        out2.println("CONTROL|User1|LOAD|1");
        out2.println("CONTROL|User1|RENAME|0~1st doc");
        
        Thread.sleep(100);
        out1.println("CONTROL|User0|LOAD|1");
        out1.println("CONTROL|User0|RENAME|1~2nd doc");
        out1.println("CONTROL|User0|RENAME|0~1st doc");
        Thread.sleep(100);
        
        assertEquals("HELLO|User0", in1.readLine());
        assertEquals("CONTROL|REQNEWPROCESSED|0~first document", in1.readLine());
        assertEquals("CONTROL|DOCLIST|0~first document", in1.readLine());
        assertEquals("CONTROL|DOCLIST|0~first document|1~second document", in1.readLine());
        assertEquals("CONTROL|DOCRENAMED|0~1st doc", in1.readLine());
        assertEquals("CONTROL|DOCLIST|0~1st doc|1~second document", in1.readLine());
        assertEquals("CONTROL|DOCRENAMED|1~2nd doc", in1.readLine());
        assertEquals("CONTROL|DOCLIST|0~1st doc|1~2nd doc", in1.readLine());
        assertEquals("CONTROL|ERROR|Name already in use", in1.readLine());
        
        assertEquals("HELLO|User1", in2.readLine());
        assertEquals("CONTROL|DOCLIST|0~first document", in2.readLine());
        assertEquals("CONTROL|REQNEWPROCESSED|1~second document", in2.readLine());
        assertEquals("CONTROL|DOCLIST|0~first document|1~second document", in2.readLine());
        assertEquals("CONTROL|DOCRENAMED|0~1st doc", in2.readLine());
        assertEquals("CONTROL|DOCLIST|0~1st doc|1~second document", in2.readLine());
        assertEquals("CONTROL|DOCRENAMED|1~2nd doc", in2.readLine());
        
        out2.println("BYE"); //the second client exits first
        stopServer(server, out1);
    }
    
    
    
    @Test
    public void multipleUsersMultipleDocumentOneUserCloses() throws UnknownHostException, IOException, InterruptedException {
                
        SEditServer server = new SEditServer(PORT_NUMBER);
        startServer(server);
        Socket serverSocket1 = new Socket(serverToConnectTo, PORT_NUMBER);
        Socket serverSocket2 = new Socket(serverToConnectTo, PORT_NUMBER);

        PrintWriter out1 = new PrintWriter(serverSocket1.getOutputStream(), true);
        BufferedReader in1 = new BufferedReader(new InputStreamReader(
                serverSocket1.getInputStream()));

        PrintWriter out2 = new PrintWriter(serverSocket2.getOutputStream(), true);
        BufferedReader in2 = new BufferedReader(new InputStreamReader(
                serverSocket2.getInputStream()));
        
        Thread.sleep(100);
        out1.println("CONTROL|User0|GETDOCLIST");
        out1.println("CONTROL|User0|REQUESTNEW|first document");
        out1.println("CONTROL|User0|LOAD|0");
        out1.println("User0|0|0|INSERT|0|0|my name is Angela|0");
        
        Thread.sleep(100);
        out2.println("CONTROL|User1|LOAD|0");
        out2.println("CONTROL|User1|REQUESTNEW|second document");
        out2.println("CONTROL|User1|LOAD|1");
        out2.println("User1|0|0|DELETE|3|7|IRREVELANT|0");
        out2.println("User1|1|1|INSERT|0|0|turek is a ginger.|0");
        
        Thread.sleep(100);
        out1.println("CONTROL|User0|GETDOCLIST");
        out1.println("CONTROL|User0|LOAD|1");
        Thread.sleep(100);
        out1.println("User0|1|1|INSERT|0|0|michael |0");
        out1.println("CONTROL|User0|CLOSE|1");
        
        Thread.sleep(100);
        out2.println("CONTROL|User1|GETDOCLIST");
        Thread.sleep(100);
        out2.println("User1|2|1|DELETE|0|8|IRRELEVANT|0");
        
        Thread.sleep(100);
        out1.println("CONTROL|User0|GETDOCLIST");
        
        assertEquals("HELLO|User0", in1.readLine());
        assertEquals("CONTROL|DOCLIST", in1.readLine());
        assertEquals("CONTROL|REQNEWPROCESSED|0~first document", in1.readLine());
        assertEquals("CONTROL|DOCLIST|0~first document", in1.readLine());
        assertEquals("User0|0|0|INSERT|0|0|my name is Angela|1", in1.readLine());
        assertEquals("CONTROL|DOCLIST|0~first document|1~second document", in1.readLine());
        assertEquals("User1|0|0|DELETE|20|24|IRREVELANT|2", in1.readLine());
        assertEquals("CONTROL|DOCLIST|0~first document|1~second document", in1.readLine());
        assertEquals("LoadUser|1|1|INSERT|0|0|turek is a ginger.|1", in1.readLine());
        assertEquals("User0|1|1|INSERT|0|0|michael |2", in1.readLine());
        assertEquals("CONTROL|DOCLIST|0~first document|1~second document", in1.readLine());

        assertEquals("HELLO|User1", in2.readLine());
        assertEquals("CONTROL|DOCLIST|0~first document", in2.readLine());
        assertEquals("LoadUser|0|0|INSERT|0|0|my name is Angela|1", in2.readLine());
        assertEquals("CONTROL|REQNEWPROCESSED|1~second document", in2.readLine());
        assertEquals("CONTROL|DOCLIST|0~first document|1~second document", in2.readLine());
        assertEquals("User1|0|0|DELETE|20|24|IRREVELANT|2", in2.readLine());
        assertEquals("User1|1|1|INSERT|0|0|turek is a ginger.|1", in2.readLine());
        assertEquals("User0|1|1|INSERT|0|0|michael |2", in2.readLine());
        assertEquals("CONTROL|DOCLIST|0~first document|1~second document", in2.readLine());
        assertEquals("User1|2|1|DELETE|8|16|IRRELEVANT|3", in2.readLine());

        out2.println("BYE"); //the second client exits first
        stopServer(server, out1);
    }
    
    @Test
    public void invalidControlRequest() throws UnknownHostException, IOException {
        
        SEditServer server = new SEditServer(PORT_NUMBER);
        startServer(server);
        
        Socket serverSocket = new Socket(serverToConnectTo, PORT_NUMBER);
        PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(
                serverSocket.getInputStream()));

        String helloMessage = in.readLine();
        assertEquals("HELLO|User0", helloMessage);
        out.println("CONTROL|User0|INVALID REQUEST");
        //This is ignored by the server
        stopServer(server, out);
    }    
    
    @Test
    public void invalidEditRequest() throws UnknownHostException, IOException {
        
        SEditServer server = new SEditServer(PORT_NUMBER);
        startServer(server);
        
        Socket serverSocket = new Socket(serverToConnectTo, PORT_NUMBER);
        PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(
                serverSocket.getInputStream()));

        String helloMessage = in.readLine();
        assertEquals("HELLO|User0", helloMessage);
        
        out.println("CONTROL|User0|REQUESTNEW|first document");
        out.println("CONTROL|User0|LOAD|0");
        in.readLine();
        out.println("User0|0|0|INSERT|10|0|my name is Angela|0");
        assertEquals("CONTROL|DOCLIST|0~first document", in.readLine());
        assertEquals("User0|0|0|INSERT|10|0|my name is Angela|1", in.readLine());
        stopServer(server, out);
    } 
    
}
