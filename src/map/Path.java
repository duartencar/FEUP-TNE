package map;

import java.util.ArrayList;

public class Path {
    private ArrayList<GraphNode> nodes;
    private short weight;

    public Path(ArrayList<Integer> pathWithNodeIds) throws Exception {
        nodes = new ArrayList<GraphNode>(pathWithNodeIds.size());
        weight = 0;

        Graph map = Graph.getInstance();

        for(int i = 0; i < pathWithNodeIds.size(); i++) {
            GraphNode aux = map.nodes.get(pathWithNodeIds.get(i));

            if(aux == null) {
                throw new Exception(pathWithNodeIds.get(i) + " is an invalid node ID.");
            }

            nodes.add(aux);

            if(i != pathWithNodeIds.size() - 1) {
                for(GraphEdge edge : aux.getEdges()) {
                    if(edge.getTarget().equals(map.nodes.get(pathWithNodeIds.get(i + 1)))) {
                        weight +=  edge.getWeight();
                        break;
                    }
                }
            }
        }
    }

    public short getWeight() {
        return weight;
    }

    public int getNumberOfSteps() {
        return nodes.size();
    }

    public ArrayList<GraphNode> getNodes() {
        return nodes;
    }

    @Override
    public String toString() {
        String toReturn = "";

        for(int i = 0; i < nodes.size() - 1; i++) {
            toReturn += nodes.get(i).getId() + " -> ";
        }

        toReturn += nodes.get(nodes.size() - 1).getId();

        return toReturn;
    }
}
