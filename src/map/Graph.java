package map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

import static utils.Utils.log;

public class Graph {
    ArrayList<GraphNode> nodes;
    ArrayList<GraphElement> elements;
    private boolean valid;

    private static Graph instance = null;

    private Graph() {
        nodes = new ArrayList<GraphNode>();
        elements = new ArrayList<GraphElement>();
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
        if(edges == null || edges.getLength() == 0) {
            log("Node list empty");
            return false;
        }

        for(int i = 0; i < edges.getLength(); i++) {
            Node edge = edges.item(i);

            if (edge.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) edge;

                String id = element.getAttribute("id");
                String source = element.getAttribute("source");
                String target = element.getAttribute("target");
                String weight = element.getAttribute("weight");

                GraphEdge newEdge = new GraphEdge(id, source, target, weight);

                this.nodes.add(new GraphNode(id, x, y, name));
            }
        }

        return true;
    }

    private boolean processNodes(NodeList nodes) {
        if(nodes == null || nodes.getLength() == 0) {
            log("Node list empty");
            return false;
        }

        for(int i = 0; i < nodes.getLength(); i++) {
            Node vertex = nodes.item(i);

            if (vertex.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) vertex;

                String id = element.getAttribute("id");
                String x = element.getAttribute("positionX");
                String y = element.getAttribute("positionY");
                String name = element.getAttribute("mainText");

                GraphNode newNode = new GraphNode(id, x, y, name);

                this.nodes.add(newNode);
                this.elements.add(newNode);
            }
        }

        return true;
    }

    public boolean init(NodeList nodes, NodeList edges) {
        return processNodes(nodes) && processEdges(edges);
    }
}
