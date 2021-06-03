package logic;

import map.GraphNode;

public class Request {
    private int id;
    private int parentId;
    private int numBoxes;
    private GraphNode destination;
    private int deliveryTime;

    public Request(int id, int parentId, int numBoxes, GraphNode destination, int deliveryTime) {
        this.id = id;
        this.parentId = parentId;
        this.numBoxes = numBoxes;
        this.destination = destination;
        this.deliveryTime = deliveryTime;
    }

    public String toString() {
        return numBoxes + "-" + deliveryTime + "-" + destination.getId() + "-time-" + id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getNumBoxes() {
        return numBoxes;
    }

    public void setNumBoxes(int numBoxes) {
        this.numBoxes = numBoxes;
    }

    public GraphNode getDestination() {
        return destination;
    }

    public void setDestination(GraphNode destination) {
        this.destination = destination;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(int deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
}
