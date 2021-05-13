package map.search;

import map.GraphElement;

public class DijkstraEdge extends GraphElement {
    private DijkstraNode origin;
    private DijkstraNode target;
    private short weight;

    public DijkstraEdge(int id, DijkstraNode origin, DijkstraNode target, short weigth) {
        super(id);

        this.origin = origin;
        this.target = target;
        this.weight = weigth;
    }

    public DijkstraNode getOrigin() {
        return origin;
    }

    public DijkstraNode getTarget() {
        return target;
    }

    public short getWeight() {
        return weight;
    }

}
