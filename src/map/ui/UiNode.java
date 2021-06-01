package map.ui;

import map.GraphElement;

import java.awt.*;
import java.util.ArrayList;

public class UiNode extends GraphElement {

    private final static int RADIUS = 10;
    private final int x;
    private final int y;
    private final Color color;
    private final ArrayList<UiEdge> edges;

    public UiNode (int id, int x, int y, Color nodeColor) {
        super(id);
        this.x = x;
        this.y = y;
        this.color = nodeColor;
        edges = new ArrayList<UiEdge>();
    }

    public UiNode (int id, int x, int y) {
        super(id);
        this.x = x;
        this.y = y;
        this.color = Color.ORANGE;
        edges = new ArrayList<UiEdge>();
    }

    public void addEdge(UiEdge e) {
        edges.add(e);
    }

    public boolean hasEdgeTo(int id) {
        for(UiEdge edge : edges) {
            if(id == edge.getDestinationId()) {
                return true;
            }
        }

        return false;
    }

    public void paint(Graphics g) {

        for(UiEdge edge : edges) {
            edge.paint(g);
        }

        g.setColor(color);

        g.fillOval(x-RADIUS/2, y-RADIUS/2, RADIUS, RADIUS);
    }
}
