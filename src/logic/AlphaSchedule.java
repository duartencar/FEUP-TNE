package logic;

import map.Graph;
import map.GraphNode;

import java.util.Vector;

import static utils.Utils.log;

public class AlphaSchedule {
    // must be threadSafe
    Vector<Task> tasks;

    public AlphaSchedule() {
        tasks = new Vector<Task>();
    }

    public short getTotalScheduleCost() {
        short sum = 0;

        for(Task t : tasks) {
            sum += t.getCost();
        }

        return sum;
    }

    public int numberOfTasks() {
        return tasks.size();
    }

    public void addTask(Task task) {
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
            for(int i = 1; i < tasks.size(); i++) {
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
