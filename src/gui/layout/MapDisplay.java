package gui.layout;

import agents.Vehicle;
import logic.Task;
import map.Graph;
import map.ui.UiGraph;
import map.ui.UiNode;

import java.awt.*;
import java.util.ArrayList;

public class MapDisplay extends Canvas {

    UiGraph graph;
    final ArrayList<Vehicle> agents;

    public MapDisplay(ArrayList<Vehicle> agents) {
        this.agents = agents;
        graph = Graph.getInstance().getGraphToDisplay();

        if(graph == null) {
            System.out.println("graph invalid");
        }
    }

    public void paint(Graphics g) {
        setBackground(Color.WHITE);

        graph.reset();

        ArrayList<Integer> fullPath;

        UiNode previous, current, next;

        for(Vehicle v: agents) {
            graph.nodes.get(v.getStartPosition().getId()).setAgentPos(v.getColor());

            fullPath = v.getSchedule().getFullPath();
            for(int i = 0; i < fullPath.size() - 1; i++ ){
                current = graph.nodes.get(fullPath.get(i));
                next = graph.nodes.get(fullPath.get(i + 1));

                current.setColor(v.getColor());
                // if this node doesn't have the edge to the next one
                if(!current.setColorToEdgeThatGoesTo(fullPath.get(i + 1), v.getColor())) {
                    // check if the edge is in the next node and paint it
                    if(!next.setColorToEdgeThatGoesTo(fullPath.get(i), v.getColor())) {
                        // check if the edge is in the previous node and paint it
                        if(i != 0) {
                            previous = graph.nodes.get(fullPath.get(i + 1));
                            previous.setColorToEdgeThatGoesTo(fullPath.get(i), v.getColor());
                        }
                    }
                }
            }
        }
        graph.paint(g);
    }
}
