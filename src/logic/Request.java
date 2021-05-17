package logic;

import map.GraphNode;

public class Request {
    private int id;
    private int parentId;
    private int numBoxes;
    private GraphNode destination;
    private String deliveryTime;

    public Request(int id, int parentId, int numBoxes, GraphNode destination, String deliveryTime) {
        this.id = id;
        this.parentId = parentId;
        this.numBoxes = numBoxes;
        this.destination = destination;
        this.deliveryTime = deliveryTime;
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

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
}
