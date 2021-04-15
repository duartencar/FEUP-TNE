package map;

import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class Graph {
    ArrayList<GraphNode> nodes;
    ArrayList<GraphEdge> edges;
    private boolean valid;

    private static Graph instance = null;

    private Graph() {
        nodes = new ArrayList<GraphNode>();
        edges = new ArrayList<GraphEdge>();
        valid = false;
    }

    public static Graph getInstance() {
        if(instance == null) {
            instance = new Graph();
        }

        return instance;
    }

    public boolean isValid() {
        return valid;
    }

    private boolean processEdges() {
        return true;
    }

    private boolean processNodes() {
        return true;
    }

    public boolean init(NodeList nodes, NodeList edges) {
        return true;
    }
}
