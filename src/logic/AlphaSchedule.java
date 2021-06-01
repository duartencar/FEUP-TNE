package logic;

import map.GraphNode;

import java.util.ArrayList;

public class AlphaSchedule {
    ArrayList<ScheduleComponent> tasks;

    public AlphaSchedule() {
        tasks = new ArrayList<ScheduleComponent>();
    }

    public void addTask(ScheduleComponent task) {
        GraphNode start = task.getStart(), end = task.getEnd();

        // if new task end is the shedulled starting point
        if(tasks.get(0).getStart().getId() == end.getId()) {
            tasks.add(0, task);
        }
        // if new task start is the schedulled last point
        else if(tasks.get(tasks.size()-1).getEnd().getId() == start.getId()) {
            tasks.add(task);
        }
        else {

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
