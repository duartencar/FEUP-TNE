package logic;

import map.Graph;
import map.GraphNode;

import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import static utils.Utils.log;

public class AlphaSchedule {
    // must be threadSafe
    Vector<Task> tasks;
    int startPosition;
    Date scheduleStart;

    public AlphaSchedule(int startPosition, Date start) {
        tasks = new Vector<Task>();
        this.startPosition = startPosition;
        scheduleStart = start;
    }

    public Task getLastTask() {
        return tasks.lastElement();
    }

    public int getTotalBoxesCarried() {
        int sum = 0;

        for(Task t : tasks) {
            sum += t.getLoad();
        }

        return sum;
    }

    public float getTotalFuelConsumed() {
        float sum = 0;

        for(Task t : tasks) {
            sum += t.getNecessaryFuel();
        }

        return sum;
    }

    public float getTotalDistance() {
        float sum = 0;

        for(Task t : tasks) {
            sum += t.getDistanceToComplete();
        }

        return sum;
    }

    public float getTotalExpense() {
        float sum = 0;

        for(Task t : tasks) {
            sum += t.getExpense();
        }

        return sum;
    }

    public short getTotalScheduleDuration() {
        short sum = 0;

        for(Task t : tasks) {
            sum += t.getMinutes();
        }

        return sum;
    }

    public int getLoadSinceLastTripToHeadQuarters() {
        int load = 0;

        if(tasks.isEmpty()) {
            return 0;
        }

        for(int i = tasks.size() - 1; i >= 0; i--) {
            if(tasks.get(i).getRequestId() == -1) {
                break;
            }

            load += tasks.get(i).getLoad();
        }

        return load;
    }

    public float getConsumedFuelSinceLastTripToGasStation() {
        float fuel = 0;

        if(tasks.isEmpty()) {
            return 0;
        }

        for(int i = tasks.size() - 1; i >= 0; i--) {
            if(tasks.get(i).getRequestId() == -2) {
                break;
            }

            fuel += tasks.get(i).getNecessaryFuel();
        }

        return fuel;
    }

    public int numberOfTasks() {
        return tasks.size();
    }

    public int getLastTaskDestination() {
        return tasks.size() == 0 ? startPosition : tasks.get(tasks.size() - 1).getEnd().getId();
    }

    public ArrayList<Integer> getFullPath() {
        ArrayList<Integer> fullPath = new ArrayList<Integer>(tasks.size() * 2);

        for(Task t : tasks) {
            for(GraphNode g : t.getPathToTarget().getNodes()) {
                fullPath.add(g.getId());
            }
        }

        return fullPath;
    }

    public ArrayList<Integer> getScheduleMainPoints() {
        ArrayList<Integer> mainPoints = new ArrayList<Integer>(tasks.size() * 2);

        mainPoints.add(tasks.get(0).getStart().getId());

        for(Task t : tasks) {
            mainPoints.add(t.getEnd().getId());
        }

        return mainPoints;
    }

    public synchronized void addTask(Task task) {
        GraphNode start = task.getStart(), end = task.getEnd();

        if(tasks.isEmpty()) {
            tasks.add(task);
        }
        // if new task end is the scheduled starting point
        else if(tasks.get(0).getStart().getId() == end.getId()) {
            tasks.add(0, task);
        }
        // if new task start is the scheduled last point
        else if(tasks.get(tasks.size()-1).getEnd().getId() == start.getId()) {
            tasks.add(task);
        }
        else {
            for(int i = 0; i < tasks.size(); i++) {
                Task t = tasks.get(i);
                if(t.getStart().getId() == start.getId()) {
                    // replace with new task
                    tasks.add(i, task);

                    try {
                        Path newPathForNextTask = new Path (Graph.getInstance().getGraphToSearch().findPath(task.getEnd().getId(), t.getEnd().getId()));
                        t.setNewPath(newPathForNextTask);
                    } catch (Exception e) {
                        log(e.getMessage());
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        if(tasks.size() == 0) {
            return "Empty";
        }

        String toReturn = tasks.get(0).getStart().getId() + " -> ";

        for(int i = 0; i < tasks.size(); i++) {
            toReturn += tasks.get(i).getEnd().getId();

            if(i != tasks.size() - 1) {
                toReturn += " -> ";
            }
        }

        return toReturn;
    }
}
