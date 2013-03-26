Collaborative Editor
===================

This is our final team project for 6.005 - Software Studio.

The collaborative editor contains 2 major components: the server and the client. 

The server is made up of SEditServer, SEditThread, and SEditDocument, the functionalities of each will be described in detail in the Server section of the Design Document. The server serves as a central unit for all the clients to connect to, listens for and processes client requests, and maintains the edit history for all the documents.

The client is made up of the Model, View, Controller components, each will be described in detail in the Client section of the Design Document. Each client can connect and disconnect to the server, open existing documents that the server contains, create new documents, rename these documents, and concurrently edit the documents.

The server and clients communicate with each other via a Server-Client communication protocol, whose grammar is defined in the Messages section.

In addition, all components of the editor are designed to be thread-safe and free of concurrency bugs. This is accomplished by employing techniques such as confinement, immutability, using threadsafe datatypes, and explicit synchronization, as described in the Thread Safety section.

Each component is extensively tested individually and collectively. Following the test-first programming principle, we wrote test cases for the units Server, GUI, Model, and finally, System. For more information, please see the Testing section.

The server can be started by running the SEditServer.java file, and the client can be started by running the Editor.java file.
