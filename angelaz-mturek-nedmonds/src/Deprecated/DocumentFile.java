//package Deprecated;
//
//import java.util.LinkedList;
//
//
//public class DocumentFile {
//    private String mDocumentName;
//    private final int mDocumentID;
//    private LinkedList<Node> mContent = new LinkedList<Node>();
//    private int idCount = 0;
//    
//    
//    public DocumentFile(String name, int documentID) {
//        mDocumentID = documentID;
//        setDocumentName(name);
//    }
//    
//    public synchronized void setDocumentName(String name) {
//        mDocumentName = name;
//    }
//    
//    public synchronized void insert(int index, String character) {
//        Node newNode = new Node(idCount, character);
//        mContent.add(index, newNode);
//        idCount++;
//    }
//    
//    public synchronized void delete(int index) {
//        mContent.remove(index);
//    }
//    
//    public synchronized void blockDelete(int indexOfFirstNode,
//            int indexOfSecondNode) {
//
//        for (int i = indexOfFirstNode; i < indexOfSecondNode; i++) {
//            delete(indexOfFirstNode);
//        }
//    }
//    
//    
//    public synchronized void blockInsert(int index, String characters) {
//        char[] characterArray = characters.toCharArray();
//        for (int i = characterArray.length - 1; i >= 0; i--) {
//            insert(index, String.valueOf(characterArray[i]));
//        }
//    }
//    
//    public synchronized Node getNode(int index) {
//        if (index < mContent.size()) {
//            return mContent.get(index);
//        }
//        return null;
//    }
//    
//    public synchronized LinkedList<Node> getNodes(int indexOfFirstNode, int indexOfSecondNode) {
//        LinkedList<Node> nodes = new LinkedList<Node>();
//        for (int i = indexOfFirstNode; i < indexOfSecondNode; i++) {
//            nodes.add(mContent.get(i));
//        }
//        return nodes;
//    }
//    
//    public synchronized LinkedList<Node> getAllNodes() {
//        return getNodes(0, mContent.size());
//    }
//    
//    
//    public synchronized String getContent() {
//        StringBuffer sb = new StringBuffer();
//        LinkedList<Node> allNodes = getAllNodes();
//        for (Node n : allNodes) {
//            sb.append(n.getContent());
//        }
//        return sb.toString();
//    }
//}
