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

    public void paint(Graphics g) {
        g.setColor(color);
        g.drawOval(x-RADIUS, y-RADIUS, RADIUS, RADIUS);

        for(UiEdge edge : edges) {
            edge.paint(g);
        }
    }
}
