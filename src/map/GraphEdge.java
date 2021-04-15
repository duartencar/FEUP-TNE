package map;

public class GraphEdge extends GraphElement {

    GraphNode origin;
    GraphNode target;
    short weight;

    public GraphEdge(String id, GraphNode origin, GraphNode target, short weigth) {
        super(id);
    }
}
