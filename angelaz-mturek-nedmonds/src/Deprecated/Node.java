package Deprecated;


/**
 * Immutable Node class.
 * @author angelaz
 *
 */
public class Node {
    private final int idNum;
    private final String content;
    
    public Node(int id, String character) {
        idNum = id;
        content = character;
    }
    
    public synchronized String getContent() {
        return content;
    }
    
    public synchronized int getID() {
        return idNum;
    }
    
    @Override
    public boolean equals(Object otherObject) {
        if (!(otherObject instanceof Node)) {
            return false;
        }
        
        Node otherNode = (Node) otherObject;
        if (idNum == otherNode.getID() && content.equals(otherNode.getContent())) {
            return true;
        }
        
        return false;
    }
    
    public String toString() {
        return "(" + content + ", " + idNum + ")";
    }
}
