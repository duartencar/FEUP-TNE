package agents;

import behaviours.VehicleReceiveBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import gui.DistributedLogistics;
import logic.*;

import map.Graph;
import map.GraphNode;
import map.search.DijkstraGraph;

import java.awt.*;
import java.util.ArrayList;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import static utils.Constants.AgentsProperties.VehicleAgent.SERVICE_NAME;
import static utils.Constants.AgentsProperties.VehicleAgent.SERVICE_TYPE;

import utils.Utils;

import static utils.Constants.AgentsProperties.VehicleAgent.VehicleProperties.*;
import static utils.Utils.generateFloat;

public class Vehicle extends Elementary {
    private final GraphNode startPosition;
    private final String type;
    private final String name;
    private final float tankSize;
    private final float maxCapacity;
    private float currentLoad;
    private float profit;
    private TreeSet<Integer> utilityNodes;
    private ArrayList<Request> requests;
    private DijkstraGraph searchGraph;
    private AlphaSchedule schedule;
    private DistributedLogistics gui;
    private ConcurrentHashMap<Integer, Proposal> proposals;
    public Color agentColor;

    public Vehicle(String n, String t, GraphNode sp, float ts, float mc) {
        name = n;
        type = t;
        startPosition = sp;
        tankSize = ts;
        maxCapacity = mc;
        profit = 0;
        currentLoad = 0;
        requests = new ArrayList<Request>();
        schedule = new AlphaSchedule();
        agentColor = new Color(generateFloat(), generateFloat(), generateFloat());
        searchGraph = Graph.getInstance().getGraphToSearch();
        utilityNodes = new TreeSet<Integer>();
        proposals = new ConcurrentHashMap<Integer, Proposal>();
    }

    public DistributedLogistics getGui() {
        return gui;
    }

    public void setUtilityNodes(ArrayList<Integer> ids) {
        for(Integer id: ids) {
            utilityNodes.add(id);
        }
    }

    public void setColor(Color c) {
        this.agentColor = c;
    }

    public Color getColor() {
        return agentColor;
    }

    public AlphaSchedule getSchedule() {
        return schedule;
    }

    public void setGui(DistributedLogistics gui) {
        this.gui = gui;
    }

    public void setup() {
        log("hello my name is " + getAID().getLocalName());

        if (!registerInYellowPages(SERVICE_TYPE, SERVICE_NAME))
            Utils.print(name,"Failed to register to yellow pages services");

        MessageTemplate template = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
                MessageTemplate.MatchPerformative(ACLMessage.CFP) );
            
        addBehaviour(new VehicleReceiveBehaviour(this, template));
    }

    public GraphNode getStartPosition() {
        return startPosition;
    }

    public String getVehicleName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public double getTankSize() {
        return tankSize;
    }

    public double getMaxCapacity() {
        return maxCapacity;
    }

    public double getCurrentLoad() {
        return currentLoad;
    }

    public void setCurrentLoad(float currentLoad) {
        this.currentLoad = currentLoad;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(float profit) {
        this.profit = profit;
    }

    public ArrayList<Request> getRequests() {
        return requests;
    }

    public synchronized ArrayList<Integer> getPathTo(int destination) {
        ArrayList<Integer> path = schedule.numberOfTasks() == 0 ?
                searchGraph.findPath(startPosition.getId(), destination) :
                searchGraph.getMinimumPathFromSchedule(destination, schedule, utilityNodes);

        return path;
    }

    public boolean addRequest(Request newRequest) {
        if (currentLoad + newRequest.getNumBoxes() > maxCapacity) {
            //TODO: check if new request exceeds gas tank by checking consumption (requires the pathfinding algorithm)
            return false;
        }
        int i = 0;
        for (Request r : requests) {
            if (newRequest.getDeliveryTime() < r.getDeliveryTime())
                break;
            i++;
        }
        requests.add(i,newRequest);
        currentLoad += newRequest.getNumBoxes();
        return true;
    }

    public void removeRequest(int index) {
        if (index < requests.size()) {
            currentLoad -= requests.get(index).getNumBoxes();
            requests.remove(index);
        }
    }

    public float consumption(float distance) {
        float consumptionRatio = 0;

        if(type.equals(ELECTRIC_TYPE)) {
            consumptionRatio = ELECTRIC_CONSUMPTION;
        }
        else if(type.equals(GAS_TYPE)) {
            consumptionRatio = GAS_CONSUMPTION;
        }
        else if(type.equals(DIESEL_TYPE)) {
            consumptionRatio = DIESEL_CONSUMPTION;
        }

        return distance * consumptionRatio / 100f;
    }

    public float budget(float consumption) {
        float pricePerEnergy = 0;

        if(type.equals(ELECTRIC_TYPE)) {
            pricePerEnergy = ELECTRIC_COST;
        }
        else if(type.equals(GAS_TYPE)) {
            pricePerEnergy = GAS_COST;
        }
        else if(type.equals(DIESEL_TYPE)) {
            pricePerEnergy = DIESEL_COST;
        }

        return consumption * pricePerEnergy;
    }

    public boolean canHandleRequest(int numBoxes) {
        //TODO: check if enough gas
        return (currentLoad+numBoxes <= maxCapacity);
    }

    public Proposal handleCallForProposal(Cfp cfp) {
        //TODO: use a pathfinding algorithm to see how long it would take for it to distribute the request
        /*
        distance traveled: 25
        loadIfAccepts: 85%
	    costIfAccepts: 8€
         */
        ArrayList<Integer> bestPath = getPathTo(cfp.getDestination());

        int load = (int) ((currentLoad + cfp.getNumBoxes()) * 100 / maxCapacity);

        int[] costs = Graph.getInstance().getPathCosts(bestPath);

        int minutes = costs[0];
        float distance = costs[1] / 100.0f; // divide pixel distance for 100 to get distance in km
        float consumption = consumption(distance);
        float budget = budget(consumption);

        Proposal toPropose = new Proposal(
                                            cfp.getId(),
                                            cfp.getParentId(),
                                            getAID(),
                                            bestPath,
                                            load,
                                            minutes,
                                            distance,
                                            consumption,
                                            budget);

        proposals.put(cfp.getId(), toPropose);

        log(toPropose.toString());

        /*ArrayList<Request> auxRequests = new ArrayList<>();
        ArrayList<GraphNode> path = new ArrayList<>();
        GraphNode before = null, after = null;
        if (pathChoosing.contains("time")) {
            for (Request req : requests) {
                if (req.getDeliveryTime() < time) {
                    auxRequests.add(req);
                    path.add(req.getDestination());
                    before = req.getDestination();
                } else {
                    after = req.getDestination();
                    break;
                }
            }
        } else if (pathChoosing.contains("path")) {
            int min = 99999999;
            int pos = requests.size();
            for (Request req : requests) {
                //TODO: append here
                before = req.getDestination();
                after = req.getDestination();
            }
        }

        //TODO: calculate detour cost
        int prev = -1, curr = -1, next = -1;
        for (Map.Entry node : Graph.getInstance().nodes.entrySet()) {
            if (node.getValue().equals(destinationNode)) {
                curr = (int) node.getKey();
                break;
            }
        }
        if (before != null) {
            for (Map.Entry node : Graph.getInstance().nodes.entrySet()) {
                if (node.getValue().equals(before)) {
                    prev = (int) node.getKey();
                    break;
                }
            }
        }
        if (after != null) {
            for (Map.Entry node : Graph.getInstance().nodes.entrySet()) {
                if (node.getValue().equals(after)) {
                    next = (int) node.getKey();
                    break;
                }
            }
        }
        int cost = 0;
        if (prev != -1) {
            ArrayList<Integer> p = map.findPath(prev, curr);
            cost += p.size(); //TODO: use road weights, and not length of path
        }
        if (next != -1) {
            ArrayList<Integer> p = map.findPath(curr, next);
            cost += p.size();
        }*/

        return toPropose;
    }

    public void paint(Graphics g, int x, int y, int width, int height) {
        g.setColor(new Color(255, 255, 255));
        g.fillRect(x, y, width, height);
        g.setColor(new Color(0, 0, 0));
        g.drawString("Nome: " + name, x + 10, y + 30);
        g.drawString(schedule.toString(), x + 10, y + 50);
        g.setColor(agentColor);
        g.fillRect(width - 20, y, 20, 20);
    }
}
