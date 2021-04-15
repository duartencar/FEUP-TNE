package map;

import static utils.Utils.*;

public class GraphNode extends GraphElement {

    private short x;
    private short y;
    private String name;

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
    }

    @Override
    public String toString() {
        return "ID: " + id + " (" + x + "," + y + ") Name: " + name;
    }
}
