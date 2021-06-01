package map.ui;

import map.GraphEdge;
import map.GraphNode;

import java.awt.*;
import java.util.HashMap;
import java.util.TreeSet;

public class UiGraph {
    public final HashMap<Integer, UiNode> nodes;

    public UiGraph(HashMap<Integer, GraphNode> staticNodes, int headQuarters, TreeSet<Integer> gasStations) {

        nodes = new HashMap<Integer, UiNode>(staticNodes.size());

        for(Integer id : staticNodes.keySet()) {
            GraphNode node = staticNodes.get(id);
            UiNode newNode;

            if(id == headQuarters) {
                newNode = new UiNode(node.getId(), node.getX(), node.getY(), Color.BLUE);
            }
            else if(gasStations.contains(id)) {
                newNode = new UiNode(node.getId(), node.getX(), node.getY(), Color.RED);
            }
            else {
                newNode = new UiNode(node.getId(), node.getX(), node.getY());
            }

            for(GraphEdge e : node.getEdges()) {
                GraphNode dest = e.getTarget();

                // if target node already exists in graph and it already has an edge to the source, it should not add.
                if(nodes.get(dest.getId()) != null && nodes.get(dest.getId()).hasEdgeTo(id)) {
                    continue;
                }

                UiEdge newEdge = new UiEdge(dest.getId(), node.getX(), node.getY(), dest.getX(), dest.getY());
                newNode.addEdge(newEdge);
            }

            nodes.put(id, newNode);
        }
    }

    public void paint(Graphics g) {
        for (UiNode value : nodes.values()) {

            value.paint(g);
        }
    }
}
