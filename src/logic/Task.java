package logic;

import map.GraphNode;

import java.util.Date;

public class Task {
    private Path pathToTarget;
    private int requestId;
    private Date deliveryTime;
    private int load;
    private int minutes;
    private float distanceToComplete;
    private float necessaryFuel;
    private float expense;
    

    public Task(Path path, int id, Date time, int load, float distance, float fuel, float expense) throws Exception {

        if(path.getNumberOfSteps() < 2) {
            throw new Exception("Path has to have at least two nodes.");
        }

        pathToTarget = path;
        requestId = id;
        deliveryTime = time;
        this.load = load;
        this.minutes = path.getWeight();
        this.distanceToComplete = distance;
        this.necessaryFuel = fuel;
        this.expense = expense;
    }

    public int getLoad() {
        return load;
    }

    public int getMinutes() {
        return minutes;
    }

    public float getDistanceToComplete() {
        return distanceToComplete;
    }

    public float getNecessaryFuel() {
        return necessaryFuel;
    }

    public float getExpense() {
        return expense;
    }

    public Path getPathToTarget() {
        return pathToTarget;
    }

    public int getRequestId() {
        return requestId;
    }

    public GraphNode getStart() {
        return pathToTarget.getNodes().get(0);
    }

    public void setNewPath(Path p) {
        pathToTarget = p;
    }

    public GraphNode getEnd() {
        return pathToTarget.getNodes().get(pathToTarget.getNodes().size() - 1);
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public String toString() {
        return getStart().getId() + " -> " + getEnd().getId();
    }
}
