package logic;

import java.io.Serializable;

public class Cfp implements Serializable {
    private int id;
    private int parentId;
    private int numBoxes;
    private int destination;
    private int deliveryTime;

    public Cfp(int id, int parentId, int numBoxes, int destination, int deliveryTime) {
        this.id = id;
        this.parentId = parentId;
        this.numBoxes = numBoxes;
        this.destination = destination;
        this.deliveryTime = deliveryTime;
    }

    public String toString() {
        return id + " - " + parentId + " - " + numBoxes + " - " + destination + " - " + deliveryTime;
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

    public int getDestination() {
        return destination;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(int deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
}
