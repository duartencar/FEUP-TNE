package map;

import static utils.Utils.convertToShort;

public class GraphEdge extends GraphElement {

    private GraphNode origin;
    private GraphNode target;
    private short weight;

    public GraphEdge(String id, GraphNode origin, GraphNode target, String weigth) {
        super(id);

        this.origin = origin;
        this.target = target;
        this.weight = convertToShort(weigth);
    }

    public GraphNode getOrigin() {
        return origin;
    }

    public GraphNode getTarget() {
        return target;
    }

    public short getWeight() {
        return weight;
    }
}
