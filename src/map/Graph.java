package map;

import map.search.DijkstraGraph;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

import static utils.Utils.*;

public class Graph {
    public HashMap<Integer, GraphNode> nodes;
    public LinkedList<GraphElement> elements;
    private boolean valid;

    private int headQuarter = -1;
    public TreeSet<Integer> gasStations;

    private static Graph instance = null;

    private Graph() {
        elements = new LinkedList<GraphElement>();
        valid = false;
        gasStations = new TreeSet<Integer>();
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

                GraphNode start = nodes.get(convertToInteger(source));

                GraphNode end = nodes.get(convertToInteger(target));

                if(start == null || end == null) {
                    log("Edge with nodes that don't exist");
                    return false;
                }

                GraphEdge outGoing = new GraphEdge(id, start, end, weight);
                GraphEdge inGoing = new GraphEdge(id, end, start, weight);

                if(!start.addEdge(outGoing)) {
                    log("Edge assign to wrong node");
                    return false;
                }
                if(!end.addEdge(inGoing)) {
                    log("Edge assign to wrong node");
                    return false;
                }

                this.elements.add(outGoing);
                this.elements.add(inGoing);
            }
        }

        // Now that edges are processed graph is fully loaded
        valid = true;

        return true;
    }

    private boolean processNodes(NodeList nodes) {
        if(nodes == null || nodes.getLength() == 0) {
            log("Node list empty");
            return false;
        }

        /* Initialize hashtable with the size of the number of nodes parsed*/
        this.nodes = new HashMap<Integer, GraphNode>((int)(nodes.getLength() / 0.75) + 1);

        for(int i = 0; i < nodes.getLength(); i++) {
            Node vertex = nodes.item(i);

            if (vertex.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) vertex;

                String id = element.getAttribute("id");
                String x = element.getAttribute("positionX");
                String y = element.getAttribute("positionY");
                String name = element.getAttribute("mainText");
                String special = element.getAttribute("special");


                GraphNode newNode = new GraphNode(id, x, y, name);

                if(special.equals("HQ")) {
                    headQuarter = convertToInteger(id);
                }
                else if(special.equals("GS")) {
                    gasStations.add(convertToInteger(id));
                }

                if(this.nodes.containsKey(newNode.getId())) {
                    log("Map has nodes with the same Id's");
                    return false;
                }

                this.nodes.put(newNode.getId(), newNode);
                this.elements.add(newNode);
            }
        }

        return headQuarter != - 1 && gasStations.size() != 0;
    }


    public DijkstraGraph getGraphToSearch() {
        if(valid) {
            return new DijkstraGraph(nodes);
        }

        return null;
    }

    public GraphNode getRandomLocation() {
        GraphNode toReturn = null;

        do {
            int random = generateInt(getNumberOfNodes());

            if(random != headQuarter && !gasStations.contains(random)) {
                toReturn = nodes.get(random);
            }
        } while(toReturn == null);

        return toReturn;
    }

    public int getNumberOfNodes() {
        return nodes.size();
    }

    public int getNumberOfElements() {
        return elements.size();
    }

    public int getNumberOfEdges() {
        return getNumberOfElements() - getNumberOfNodes();
    }

    public boolean init(NodeList nodes, NodeList edges) {
        return processNodes(nodes) && processEdges(edges);
    }
}
