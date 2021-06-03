package map.search;

import map.GraphElement;

import java.util.ArrayList;

public class DijkstraNode extends GraphElement {
    private ArrayList<DijkstraEdge> edges;
    DijkstraNode previous;
    short distance;
    boolean visited = false;

    public DijkstraNode(int id) {
        super(id);

        edges = new ArrayList<DijkstraEdge>();

        previous = null;

        distance = Short.MAX_VALUE;
    }

    public void reset() {
        previous = null;

        distance = Short.MAX_VALUE;

        visited = false;
    }

    public void addEdge(DijkstraEdge newEdge) {
        edges.add(newEdge);
    }

    public void setPrevious(DijkstraNode prev) {
        previous = prev;
    }

    public DijkstraNode getPrevious() {
        return previous;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited() {
        visited = true;
    }

    public short getDistance() {
        return distance;
    }

    public void setDistance(short d) {
        distance = d;
    }

    public ArrayList<DijkstraEdge> getEdges() {
        return edges;
    }
}
