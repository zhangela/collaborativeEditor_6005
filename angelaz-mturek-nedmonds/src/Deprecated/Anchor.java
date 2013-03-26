//package Deprecated;
//
//import java.util.ArrayList;
//
//
//public class Anchor {
//    private final String userID;
//    private ArrayList<Node> anchorNodes; //a list of length 2
//    
//    public Anchor(String id) {
//        userID = id;
//    }
//    
//    public synchronized void setAnchor(Node characterNode) {
//        anchorNodes.clear();
//        anchorNodes.add(characterNode);
//    }
//    
//    public synchronized void setAnchor(Node startNode, Node endNode) {
//        anchorNodes.clear();
//        anchorNodes.add(startNode);
//        anchorNodes.add(endNode);
//    }
//    
//    public synchronized void clearAnchors() {
//        anchorNodes.clear();
//    }
//    
//    public synchronized Node getFirstAnchor() {
//        if (anchorNodes.isEmpty()) {
//            return null;
//        }
//        return anchorNodes.get(0);
//    }
//    
//    
//    public synchronized Node getSecondAnchor() {
//        if (anchorNodes.isEmpty()) {
//            return null;
//        } else if (anchorNodes.size() == 1) { //in case if blockEdit/Delete is called when there is only one anchor, which shouldnt happen
//            return anchorNodes.get(0);
//        }
//        
//        return anchorNodes.get(1);
//    }
//    
//    public synchronized ArrayList<Node> getAnchors() {
//        if (anchorNodes.isEmpty()) {
//            return null;
//        }
//        ArrayList<Node> result = (ArrayList<Node>) anchorNodes.clone();
//        return result;
//    }
//        
//}
