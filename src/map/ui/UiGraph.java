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
                UiEdge newEdge = new UiEdge(dest.getId(), node.getX(), node.getY(), dest.getX(), dest.getY());
                newNode.addEdge(newEdge);
            }
        }
    }

    public void paint(Graphics g) {

    }
}
