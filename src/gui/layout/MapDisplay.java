package gui.layout;

import map.Graph;
import map.ui.UiGraph;

import java.awt.*;

public class MapDisplay extends Canvas {

    UiGraph graph;

    public MapDisplay() {
        graph = Graph.getInstance().getGraphToDisplay();

        if(graph == null) {
            System.out.println("graph invalid");
        }
    }

    public void paint(Graphics g) {
        setBackground(Color.WHITE);
        graph.paint(g);
    }
}
