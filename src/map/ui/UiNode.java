package map.ui;

import map.GraphElement;

import java.awt.*;
import java.util.ArrayList;

public class UiNode extends GraphElement {

    private final static int RADIUS = 10;
    private final int x;
    private final int y;
    private Color color;
    private final ArrayList<UiEdge> edges;
    private Color agentPos = null;
    private boolean requestPos = false;

    public UiNode (int id, int x, int y, Color nodeColor) {
        super(id);
        this.x = x;
        this.y = y;
        this.color = nodeColor;
        edges = new ArrayList<UiEdge>();
    }

    public void setAgentPos(Color agentColor) {
        agentPos = agentColor;
    }

    public void setRequestPos() {
        requestPos = true;
    }

    public void setColor(Color c) {
        color = c;
    }

    public UiNode (int id, int x, int y) {
        super(id);
        this.x = x;
        this.y = y;
        this.color = Color.BLACK;
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

    public void reset() {
        agentPos = null;
        requestPos = false;

        for(UiEdge e : edges) {
            e.reset();
        }
    }

    public boolean setColorToEdgeThatGoesTo(int id, Color c) {
        for(UiEdge e : edges) {
            if(e.getDestinationId() == id) {
                e.setColor(c);
                return true;
            }
        }

        return false;
    }

    public void paint(Graphics g) {

        for(UiEdge edge : edges) {
            edge.paint(g);
        }

        if(agentPos != null) {
            g.setColor(agentPos);
            g.fillRect(x-RADIUS, y-RADIUS, RADIUS*2, RADIUS*2);
        }
        else {
            g.setColor(color);

            g.fillOval(x-RADIUS/2, y-RADIUS/2, RADIUS, RADIUS);
        }
    }
}
