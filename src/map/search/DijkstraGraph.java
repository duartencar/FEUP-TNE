package map.search;

import logic.AlphaSchedule;
import map.GraphEdge;
import map.GraphNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import static utils.Utils.log;

public class DijkstraGraph {
    private HashMap<Integer, DijkstraNode> nodes;
    private ArrayList<DijkstraNode> queue;

    public DijkstraGraph(HashMap<Integer, GraphNode> staticNodes) {
        nodes = new HashMap<Integer, DijkstraNode>(staticNodes.size());
        queue = new ArrayList<DijkstraNode>(staticNodes.size());

        for(Integer id : staticNodes.keySet()) {
            DijkstraNode newNode = new DijkstraNode(id);
            nodes.put(id, newNode);
        }

        for(Integer id : staticNodes.keySet()) {
            GraphNode staticNode = staticNodes.get(id);
            DijkstraNode startNode = nodes.get(id);

            for(GraphEdge edge: staticNode.getEdges()) {
                DijkstraNode endNode = nodes.get(edge.getTarget().getId());
                startNode.addEdge(new DijkstraEdge(edge.getId(), startNode, endNode, edge.getWeight()));
            }
        }
    }

    public int getNumberOfNodes() {
        return nodes.size();
    }

    public void reset() {
        for(Integer id : nodes.keySet()) {
            nodes.get(id).reset();
        }

        queue.clear();

        queue.ensureCapacity(nodes.size() / 2);
    }

    private void addToQueue(DijkstraNode toAdd) {
        for(int i = 0; i < queue.size(); i++) {
            if(toAdd.distance < queue.get(i).distance) {
                queue.add(i ,toAdd);
                return;
            }
        }

        queue.add(toAdd);
    }

    private DijkstraNode getQueueMin() {
        return queue.remove(0);
    }

    public synchronized ArrayList<Integer> getMinimumPathFromSchedule(Integer start, AlphaSchedule sc, TreeSet<Integer> utilityNodes) {
        ArrayList<Integer> scheduleTasks = sc.getScheduleMainPoints();
        DijkstraNode aux = null;
        ArrayList<Integer> answer = new ArrayList<Integer>(2);
        int selectableMainPoints = 0;
        DijkstraNode begin = nodes.get(start);

        if(begin == null) {
            log(start + " doesn't exist");
            return null;
        }

        for(int i = scheduleTasks.size() - 1; i <= 0; i--) {
            if(nodes.get(scheduleTasks.get(i)) == null) {
                log(scheduleTasks.get(i) + " doesn't exist");
                return null;
            }

            if(selectableMainPoints == 0 && utilityNodes.contains(scheduleTasks.get(i))) {
                selectableMainPoints = i;
            }
        }

        reset();

        begin.distance = 0;

        addToQueue(begin);

        while(queue.size() != 0) {

            aux = getQueueMin();

            if(aux.isVisited()) {
                continue;
            }

            for(int i = scheduleTasks.size()-1; i >= selectableMainPoints; i--) {
                if(aux.getId() == scheduleTasks.get(i)) {
                    break;
                }
            }

            //log("Visiting " + aux.getId());

            for(DijkstraEdge edge : aux.getEdges()) {

                DijkstraNode auxDest = edge.getTarget();

                if(auxDest.isVisited()) {
                    //log(auxDest.getId() + " already visited.");
                    continue;
                }

                if(aux.getDistance() + edge.getWeight() < auxDest.getDistance()) {
                    auxDest.setDistance((short) (aux.getDistance() + edge.getWeight()));
                    auxDest.setPrevious(aux);

                    //log("updated " + auxDest.getId() + ": distance is " + auxDest.getDistance() + " coming from " + aux.getId());

                    addToQueue(auxDest);
                }
            }

            aux.setVisited();
        }

        while(aux.getPrevious() != null) {
            answer.add(aux.getId());
            aux = aux.getPrevious();
        }

        return answer;
    }

    public synchronized ArrayList<Integer> findPath(Integer start, Integer end) {
        reset();

        ArrayList<Integer> answer = new ArrayList<Integer>(2);
        DijkstraNode begin = nodes.get(start);
        DijkstraNode last = nodes.get(end);
        DijkstraNode aux = null;

        if(begin == null || last == null) {
            log(start + " or " + end + " doesn't exist");
            return answer;
        }

        begin.distance = 0;

        addToQueue(begin);

        while(queue.size() != 0) {

            aux = getQueueMin();

            if(aux.isVisited()) {
                continue;
            }

            if(aux.equals(last)) {
                break;
            }

            //log("Visiting " + aux.getId());

            for(DijkstraEdge edge : aux.getEdges()) {

                DijkstraNode auxDest = edge.getTarget();

                if(auxDest.isVisited()) {
                    //log(auxDest.getId() + " already visited.");
                    continue;
                }

                if(aux.getDistance() + edge.getWeight() < auxDest.getDistance()) {
                    auxDest.setDistance((short) (aux.getDistance() + edge.getWeight()));
                    auxDest.setPrevious(aux);

                    //log("updated " + auxDest.getId() + ": distance is " + auxDest.getDistance() + " coming from " + aux.getId());

                    addToQueue(auxDest);
                }
            }

            aux.setVisited();
        }

        aux = last;

        answer.add(0, start);

        while(aux.getPrevious() != null) {
            answer.add(1, aux.getId());
            aux = aux.getPrevious();
         }

        return answer;
    }
}
