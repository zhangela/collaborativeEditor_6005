//package Deprecated;
//
//
//import javax.swing.JFrame;
//
//public class EditorGUI extends JFrame {
//    
////    
////    private JButton newDocumentButton;
////<<<<<<< Updated upstream
////    private static JTabbedPane tabbedPane;
////    private JTextPane tp; //DELETE THIS SHIT
////=======
////    private JTabbedPane tabbedPane;
////    private Map<Integer, JTextPane> textpanes;
////>>>>>>> Stashed changes
////    private int documentCount = 0;
////    private UserDQ user;
////    private Socket serverSocket;
////    private PrintWriter out;
////    private BufferedReader in;
////    private DocumentContentListener listener;
////    private boolean ignoreNext = false;
////<<<<<<< Updated upstream
////    private int curDoc;
////=======
////    private Map<Integer, String> documents;
////>>>>>>> Stashed changes
////   
////    public EditorGUI() throws UnknownHostException, IOException {
////
////        //Server-related
////        String address = JOptionPane.showInputDialog("Insert the address of the server.");
////        serverSocket = new Socket(address, 12351);
////        
////        out = new PrintWriter(serverSocket.getOutputStream(), true);
////        in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
////        
////        handleServerGreeting(serverSocket);
////        handleConnectionInBackground(serverSocket);
////        out.println("GETDOCLIST");
////        //out.println("LOAD");
////        
////        
////        
////        //GUI-related
////        setTitle("Collaborative Editor");
////        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
////        
////        GroupLayout layout = new GroupLayout(getContentPane());
////        setLayout(layout);
////
////        // get some margins around components by default
////        layout.setAutoCreateContainerGaps(true);
////        layout.setAutoCreateGaps(true);
////        
////        
////        
////        tabbedPane = new JTabbedPane();
////        //JComponent newStuff = makeNewDocPanel(documentCount, tabbedPane);
////        //JComponent extraPane = makeNewDocPanel(documentCount, tabbedPane);
////        DocPanel freshdoc = new DocPanel(0, "hellloo");
////        addDocPanel(freshdoc, tabbedPane);
////        
////        tabbedPane.add("+", this.makeNewDocPanel(documentCount));
////        documentCount++;
////        
////
////        // place the components in the layout (which also adds them
////        // as children of this view)
////        layout.setHorizontalGroup(layout
////                .createParallelGroup(GroupLayout.Alignment.LEADING)
////                .addComponent(tabbedPane));
////
////        
////        layout.setVerticalGroup(layout
////                .createSequentialGroup()
////                .addComponent(tabbedPane));
////        
////        
////        
//////      newDocumentButton.addActionListener(new ActionListener() {
//////
//////            @Override
//////            public void actionPerformed(ActionEvent arg0) {
//////                JComponent newDocPane = makeTextPanel(documentCount, tabbedPane);
//////                tabbedPane.add("Document" + String.valueOf(documentCount), newDocPane);
//////                documentCount++;
//////            }
//////            
//////        });
////        
////        
////    }
////    
////    public void addDocPanel(DocPanel doc, JTabbedPane tabpane){
////        tabpane.add(doc.getName(), doc.getPanel());
////    }
////    
////    protected JComponent makeNewDocPanel(final int documentCount){
////        JPanel panel = new JPanel(false);
////        panel.setPreferredSize(new Dimension(100,200));
////        
////        
////        
////        
////        JLabel instructions = new JLabel();
////        instructions.setText("Select an already existing document from the list or create a new document");
////        instructions.setVerticalTextPosition(JLabel.CENTER);
////        
////        
////        
////        Object[] tempContent = {1,2,3,4};
////        final JList listOfDocs = new JList(tempContent);
////        
////        final JTextField newDocName = new JTextField();
////        newDocName.addActionListener(new ActionListener(){
////            public void actionPerformed(ActionEvent ae){
////                String newName = newDocName.getText();
////                newDocName.setText("");
////                DocPanel newpane =new DocPanel(documentCount, newName);
////                tabbedPane.add(newpane.getName(), newpane.getPanel());
////                tabbedPane.remove(tabbedPane.indexOfTab("+"));
////                
////            }
////        });
////        
////        JButton newButton = new JButton();
////        newButton.setText("Create");
////        newButton.addActionListener(new ActionListener(){
////            public void actionPerformed(ActionEvent ae){
////                String newName = newDocName.getText();
////                newDocName.setText("");
////                DocPanel newpane =new DocPanel(documentCount, newName);
////                tabbedPane.add(newpane.getName(), newpane.getPanel());
////                
////            }
////        });
////        
////        
//////        panel.add(instructions);
//////        panel.add(listOfDocs);
//////        panel.add(newDocName);
//////        
////        
////        
////        GroupLayout layout = new GroupLayout(panel);
////        panel.setLayout(layout);
////
////        // get some margins around components by default
////        layout.setAutoCreateContainerGaps(true);
////        layout.setAutoCreateGaps(true);
////        
////
////        layout.setHorizontalGroup(
////                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
////                .addComponent(instructions)
////                .addComponent(listOfDocs)
////                .addComponent(newDocName)
////                .addComponent(newButton))
////                ;
////
////        
////        layout.setVerticalGroup(layout
////                .createSequentialGroup()
////                .addComponent(instructions)
////                .addComponent(listOfDocs)
////                .addComponent(newDocName)
////                .addComponent(newButton)
////                );
////        
////        
////        return panel;
////    }
////
////<<<<<<< Updated upstream
////   /* protected JComponent makeTextPanel(final int documentCount, JTabbedPane pane) {
////        
////        final DocumentFile doc = new DocumentFile("Document " + String.valueOf(documentCount), documentCount);
////        
////=======
////    protected JComponent makeTextPanel(final int docID, JTabbedPane pane) {
////                
////>>>>>>> Stashed changes
////        JPanel panel = new JPanel(false);
////                
////        JLabel documentNamePrefixLabel = new JLabel();
////        documentNamePrefixLabel.setText("Document Name:");
////        documentNamePrefixLabel.setVerticalTextPosition(JLabel.CENTER);
////        
////        final JTextField documentNameTextField = new JTextField();
////        documentNameTextField.setText("New Document");
////        
////        JButton documentNameChangeButton = new JButton();
////        documentNameChangeButton.setText("Rename");
////        
////        JTextPane tp = textpanes.get(documentCount);
////        tp.setPreferredSize(new Dimension(300, 400));
////        listener = new DocumentContentListener(tp.getDocument(), docID);
////        tp.getDocument().addDocumentListener(listener);
////        JScrollPane documentBodyScrollPane = new JScrollPane(tp);
////        
////        GroupLayout layout = new GroupLayout(panel);
////        panel.setLayout(layout);
////
////        // get some margins around components by default
////        layout.setAutoCreateContainerGaps(true);
////        layout.setAutoCreateGaps(true);
////
////        // place the components in the layout (which also adds them
////        // as children of this view)
////        layout.setHorizontalGroup(layout
////                .createParallelGroup(GroupLayout.Alignment.LEADING)
////                .addGroup(
////                        layout.createSequentialGroup()
////                                .addComponent(documentNamePrefixLabel)
////                                .addComponent(documentNameTextField,  GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
////                                .addComponent(documentNameChangeButton))
////                .addComponent(documentBodyScrollPane));
////
////        
////        layout.setVerticalGroup(layout.createSequentialGroup()
////                .addGroup(
////                        layout.createParallelGroup(GroupLayout.Alignment.LEADING)
////                                .addComponent(documentNamePrefixLabel)
////                                .addComponent(documentNameTextField,  GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
////                                .addComponent(documentNameChangeButton))
////                .addComponent(documentBodyScrollPane)
////        );
////        
////  
////        documentNameChangeButton.addActionListener(new ActionListener(){
////
////            @Override
////            public void actionPerformed(ActionEvent arg0) {
////                String newDocumentName = documentNameTextField.getText();
////                tabbedPane.setTitleAt(documentCount, newDocumentName);
////                out.println("REQUESTNEW");
////            }
////            
////        });
////        
////        
////        tp.addKeyListener(new KeyListener() {
////
////            @Override
////            public void keyPressed(KeyEvent event) {
////            }
////
////            @Override
////            public void keyReleased(KeyEvent arg0) {                
////            }
////
////            @Override
////            public void keyTyped(KeyEvent arg0) {
////                
////            }
////            
////        });
////        
////        return panel;
////    }*/
////    
////    
////<<<<<<< Updated upstream
////    
/////*    protected JComponent makeTextPanel(final int documentCount, JTabbedPane pane) {
//////        
//////        final DocumentFile doc = new DocumentFile("Document " + String.valueOf(documentCount), documentCount);
//////        
//////        JPanel panel = new JPanel(false);
//////                
//////        JLabel documentNamePrefixLabel = new JLabel();
//////        documentNamePrefixLabel.setText("Document Name:");
//////        documentNamePrefixLabel.setVerticalTextPosition(JLabel.CENTER);
//////        
//////        final JTextField documentNameTextField = new JTextField();
//////        documentNameTextField.setText("New Document");
//////        
//////        JButton documentNameChangeButton = new JButton();
//////        documentNameChangeButton.setText("Rename");
//////        
//////
//////        JTextPane documentBodyTextPane = new JTextPane();
//////        documentBodyTextPane.setPreferredSize(new Dimension(300, 400));
//////        documentBodyTextPane.getDocument().addDocumentListener(new DocumentContentListener(doc));
//////        JScrollPane documentBodyScrollPane = new JScrollPane(documentBodyTextPane);
//////        
//////        GroupLayout layout = new GroupLayout(panel);
//////        panel.setLayout(layout);
//////
//////        // get some margins around components by default
//////        layout.setAutoCreateContainerGaps(true);
//////        layout.setAutoCreateGaps(true);
//////
//////        // place the components in the layout (which also adds them
//////        // as children of this view)
//////        layout.setHorizontalGroup(layout
//////                .createParallelGroup(GroupLayout.Alignment.LEADING)
//////                .addGroup(
//////                        layout.createSequentialGroup()
//////                                .addComponent(documentNamePrefixLabel)
//////                                .addComponent(documentNameTextField,  GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
//////                                .addComponent(documentNameChangeButton))
//////                .addComponent(documentBodyScrollPane));
//////
//////        
//////        layout.setVerticalGroup(layout.createSequentialGroup()
//////                .addGroup(
//////                        layout.createParallelGroup(GroupLayout.Alignment.LEADING)
//////                                .addComponent(documentNamePrefixLabel)
//////                                .addComponent(documentNameTextField,  GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
//////                                .addComponent(documentNameChangeButton))
//////                .addComponent(documentBodyScrollPane)
//////        );
//////        
//////  
//////        documentNameChangeButton.addActionListener(new ActionListener(){
//////
//////            @Override
//////            public void actionPerformed(ActionEvent arg0) {
//////                String newDocumentName = documentNameTextField.getText();
//////                tabbedPane.setTitleAt(documentCount, newDocumentName);
//////                doc.setDocumentName(newDocumentName);
//////            }
//////            
//////        });
//////        
//////        
//////        documentBodyTextPane.addKeyListener(new KeyListener() {
//////
//////            @Override
//////            public void keyPressed(KeyEvent event) {
//////            }
//////
//////            @Override
//////            public void keyReleased(KeyEvent arg0) {                
//////            }
//////
//////            @Override
//////            public void keyTyped(KeyEvent arg0) {
//////                
//////            }
//////            
//////        });
//////        
//////        return panel;
//////    }*/
////    
////    // QUESTION: How can we get the Node object from the listener?
////    class DocumentContentListener implements DocumentListener {
////
////        final int docNum;
////        public DocumentContentListener(int docNum){
////            this.docNum = docNum;
////=======
////    class DocumentContentListener implements DocumentListener {
////
////        final Document docFile;
////        final int docID;
////
////        public DocumentContentListener(Document df, int id) {
////            docFile = df;
////            docID = id;
////>>>>>>> Stashed changes
////        }
////
////        public void insertUpdate(DocumentEvent e) {
////            try {
////                update(e, EditType.INSERT);
////            } catch (BadLocationException e1) {
////                e1.printStackTrace();
////            }
////        }
////
////        public void removeUpdate(DocumentEvent e) {
////            try {
////                update(e, EditType.DELETE);
////            } catch (BadLocationException e1) {
////                e1.printStackTrace();
////            }
////        }
////
////        public void changedUpdate(DocumentEvent e) {
////            // Plain text components don't fire these events.
////        }
////
////        public void update(DocumentEvent e, EditType action)
////                throws BadLocationException {
////            synchronized (this) {
////                if (ignoreNext) {
////                    ignoreNext = false;
////                    return;
////                }
////                Document doc = (Document) e.getDocument();
////                int offset = e.getOffset();
////                int length = e.getLength();
////
////
////<<<<<<< Updated upstream
////                if (!doc.getText(0, doc.getLength()).equals(user.getView(docNum))) {
////
////                    if (action == EditType.INSERT) {
////                        String content = doc.getText(offset, length);
////                        user.createRequest(docNum, "" +  action.toString()
////=======
////                if (!doc.getText(0, doc.getLength()).equals(user.getView(docID))) {
////
////                    if (action == EditType.INSERT) {
////                        String content = doc.getText(offset, length);
////                        user.createRequest(docID, "" + 0 + "|" + action.toString()
////>>>>>>> Stashed changes
////                                + "|" + offset + "|" + (offset + length) + "|"
////                                + content);
////                    }
////
////                    else
////<<<<<<< Updated upstream
////                        user.createRequest(docNum, "" +  action.toString()
////=======
////                        user.createRequest(docID, "" + 0 + "|" + action.toString()
////>>>>>>> Stashed changes
////                                + "|" + offset + "|" + (offset + length) + "|"
////                                + "");
////
////                    sendMessage(serverSocket, user.pullRequest());
////                }
////            }
////        }
////        
////        public void printInfo(DocumentEvent documentEvent) {
////<<<<<<< Updated upstream
////            int offset = documentEvent.getOffset();
////            int length = documentEvent.getLength();
////            System.out.println("Offset: " + offset);
////            System.out.println("Length: " + length);
////            DocumentEvent.EventType type = documentEvent.getType();
////            String typeString = null;
////            if (type.equals(DocumentEvent.EventType.CHANGE)) {
////              typeString = "Change";
////            } else if (type.equals(DocumentEvent.EventType.INSERT)) {
//////              typeString = "Insert " + docFile.getNode(offset);
////            } else if (type.equals(DocumentEvent.EventType.REMOVE)) {
////              typeString = "Remove";
////            }
////            System.out.println("Type  : " + typeString);
////            Document documentSource = documentEvent.getDocument();
//////            System.out.println("Content: " + docFile.getContent());
////            System.out.println("\n");
////=======
//////            int offset = documentEvent.getOffset();
//////            int length = documentEvent.getLength();
//////            System.out.println("Offset: " + offset);
//////            System.out.println("Length: " + length);
//////            DocumentEvent.EventType type = documentEvent.getType();
//////            String typeString = null;
//////            if (type.equals(DocumentEvent.EventType.CHANGE)) {
//////              typeString = "Change";
//////            } else if (type.equals(DocumentEvent.EventType.INSERT)) {
//////              typeString = "Insert " + docFile.getNode(offset);
//////            } else if (type.equals(DocumentEvent.EventType.REMOVE)) {
//////              typeString = "Remove";
//////            }
//////            System.out.println("Type  : " + typeString);
//////            Document documentSource = documentEvent.getDocument();
//////            System.out.println("DocFile: " + docFile.getAllNodes());
//////            System.out.println("Content: " + docFile.getContent());
//////            System.out.println("\n");
////>>>>>>> Stashed changes
////          }
////    
////    }
////    
////    public static enum EditType {
////        INSERT,
////        DELETE,
////    }
////    
////    
////    private void handleServerGreeting(final Socket socket) throws IOException {
////        String helloMessage = in.readLine();
////        String[] elements = helloMessage.split("\\|");
////        if (!elements[0].equals("HELLO")) {
////            JOptionPane.showMessageDialog(null, "Sorry, the server has encountered unexpected error. Please try again.");
////            throw new RuntimeException();
////        } else {
////            user = new UserDQ(elements[1]);
////        }
////    }
////    /**
////     * Listens for server messages
////     * @param socket
////     */
////    private void handleConnectionInBackground(final Socket socket) {
////        Thread backgroundThread = new Thread(new Runnable() {
////            public void run() {
////                try {
////                    handleConnection(socket);
////                } catch (IOException e) {
////                }
////            }
////            private void handleConnection(Socket socket) throws IOException{
////                try {
////                    for (String line = in.readLine(); line != null; line = in.readLine()) {
////                        if(line != null && line.equals("BYE")) {
////                            break;
////                        }
////                        
////                        String[] elements = line.split("\\|");
////                        if (elements[0].equals("CONTROL") && elements[1].equals("REQNEWPROCESSED")) {
////                            int docID = Integer.parseInt(elements[2]);
////                            String docName = elements[3];
////                        } else if (elements[0].equals("CONTROL") && elements[1].equals("DOCLIST")) {
////                            int numberOfDocuments = elements.length - 2;
////                        }
////                        
////                        SwingUtilities.invokeLater(new Runnable() {
////                            public void run() {
////                                updateView();
////                            }
////                        });
////                        
////                        //TODO:remember to sync access to user between threads
////                        user.pushRequest(line);
////<<<<<<< Updated upstream
////                        System.out.println("SERVER SAYS: " + line);
////                       // System.out.println("SYNC COPY: " + user.getSyncCopy());
////                       // System.out.println("VIEW COPY: " + user.getView());
////=======
//////                        System.out.println("SERVER SAYS: " + line);
//////                        System.out.println("SYNC COPY: " + user.getSyncCopy());
//////                        System.out.println("VIEW COPY: " + user.getView());
////>>>>>>> Stashed changes
////                        
////                    }
////                } finally {      
////                    out.close();
////                    in.close();
////                }
////            }
////        });
////        backgroundThread.start();
////    }
////
////    
////    private void updateView(int docID) {
////        //tp.getDocument().removeDocumentListener(listener);
////        synchronized (this) {
////            ignoreNext = true;
////            textpanes.get(docID).setText(user.getView(docID));
////        }
////        //tp.getDocument().addDocumentListener(listener);
////        
////    }
////    public void sendMessage(Socket socket, String message){
////            out.println(message); 
////    }
////    
////    public static void main(final String[] args) {
////        SwingUtilities.invokeLater(new Runnable() {
////            public void run() {
////                try {
////                    EditorGUI main;
////                    main = new EditorGUI();
////                    main.setPreferredSize(new Dimension(800, 500));
////                    main.pack();
////                    main.setVisible(true);
////                } catch (UnknownHostException e) {
////                    JOptionPane.showMessageDialog(null, "Sorry, we couldn't connect to the server. Please try again.");
////                    e.printStackTrace();
////                } catch (IOException e) {
////                    JOptionPane.showMessageDialog(null, "Sorry, we were unable to communicate with the server. Please try again.");
////                    e.printStackTrace();
////                }
////            }
////        });
////        
////    }   
//}
