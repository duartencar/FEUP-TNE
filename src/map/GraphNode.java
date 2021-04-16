package map;

import java.util.ArrayList;

import static utils.Utils.*;

public class GraphNode extends GraphElement {

    private short x;
    private short y;
    final private String name;
    ArrayList<GraphEdge> edges;

    public GraphNode(String id, String x, String y, String name) {
        super(id);

        try {
            this.x = (short)convertToFloat(x);
            this.y = (short)convertToFloat(y);
        } catch (NumberFormatException e) {
            log("Couldn't convert node coordinates");
            log("X = " + x);
            log("Y = " + y);
            log(e.getMessage());
        }

        this.name = name;

        edges = new ArrayList<GraphEdge>();
    }

    public boolean addEdge(GraphEdge newEdge) {
        if(this.getId() != newEdge.getOrigin().getId()) {
            return false;
        }

        edges.add(newEdge);

        return true;
    }

    @Override
    public String toString() {
        return "ID: " + getId() + " (" + x + "," + y + ") Name: " + name;
    }
}
