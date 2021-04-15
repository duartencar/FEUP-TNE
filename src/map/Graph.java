package map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
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

    private boolean processEdges(NodeList edges) {
        return true;
    }

    private boolean processNodes(NodeList nodes) {
        if(nodes == null || nodes.getLength() == 0) {
            System.out.println("Node list empty");
            return false;
        }

        for(int i = 0; i < nodes.getLength(); i++) {
            Node vertex = nodes.item(i);

            if (vertex.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) vertex;

                String id = element.getAttribute("id");
                System.out.println(id);
            }
        }
        return true;
    }

    public boolean init(NodeList nodes, NodeList edges) {
        return processNodes(nodes) && processEdges(edges);
    }
}
