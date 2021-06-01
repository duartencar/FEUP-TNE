package logic;

import map.GraphNode;

import java.util.Date;

public class ScheduleComponent {
    private Path pathToTarget;
    private int requestId;
    private Date deliveryTime;

    public ScheduleComponent(Path path, int id, Date time) throws Exception {

        if(path.getNumberOfSteps() < 2) {
            throw new Exception("Path has to have at least two nodes.");
        }

        pathToTarget = path;
        requestId = id;
        deliveryTime = time;
    }

    public int getRequestId() {
        return requestId;
    }

    public GraphNode getStart() {
        return pathToTarget.getNodes().get(0);
    }

    public GraphNode getEnd() {
        return pathToTarget.getNodes().get(pathToTarget.getNodes().size() - 1);
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public int compareTo(ScheduleComponent sc) {
        return deliveryTime.compareTo(sc.getDeliveryTime());
    }
}
