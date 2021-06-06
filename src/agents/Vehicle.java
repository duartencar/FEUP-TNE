package agents;

import behaviours.VehicleReceiveBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import logic.*;
import map.Graph;
import map.GraphNode;
import map.search.DijkstraGraph;
import utils.Utils;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import static utils.Constants.AgentsProperties.VehicleAgent.SERVICE_NAME;
import static utils.Constants.AgentsProperties.VehicleAgent.SERVICE_TYPE;
import static utils.Constants.AgentsProperties.VehicleAgent.VehicleProperties.*;
import static utils.Utils.generateFloat;

public class Vehicle extends Elementary {
    private final GraphNode startPosition;
    private final String type;
    private final String name;
    private final float tankSize;
    private final float maxCapacity;
    private float currentLoad;
    private float distanceCovered = 0;
    private float fuelConsumed = 0;
    private float fuelExpenses = 0;
    private float profit;
    private TreeSet<Integer> gasStations;
    private int hq;
    private ArrayList<Request> requests;
    private DijkstraGraph searchGraph;
    private AlphaSchedule schedule;
    private ConcurrentHashMap<Integer, Proposal> proposals;
    private ConcurrentHashMap<Integer, Cfp> calls;
    public Color agentColor;
    private long simulationStartTime;

    public Vehicle(String n, String t, GraphNode sp, float ts, float mc) {
        name = n;
        type = t;
        startPosition = sp;
        tankSize = ts;
        maxCapacity = mc;
        profit = 0;
        currentLoad = 0;
        requests = new ArrayList<Request>();
        agentColor = new Color(generateFloat(), generateFloat(), generateFloat());
        searchGraph = Graph.getInstance().getGraphToSearch();
        gasStations = new TreeSet<Integer>();
        proposals = new ConcurrentHashMap<Integer, Proposal>();
        simulationStartTime = Calendar.getInstance().getTimeInMillis();
        schedule = new AlphaSchedule(sp.getId(), new Date(simulationStartTime));
        calls = new ConcurrentHashMap<Integer, Cfp>();

        if(startPosition.getId() != hq) {
            goToHeadQuarters();
        }
    }

    public void setGasStations(ArrayList<Integer> ids) {
        for(Integer id: ids) {
            gasStations.add(id);
        }
    }

    public void setHq(int hq) {
        this.hq = hq;
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

    public void getGas() {
        int lastTaskDestination = schedule.getLastTaskDestination();
        ArrayList<Integer> path = Graph.getInstance().getGraphToSearch().getPathToNearestGasStation(lastTaskDestination);
    }

    public void goToHeadQuarters() {
        int lastTaskDestination = schedule.getLastTaskDestination();
        ArrayList<Integer> path = Graph.getInstance().getGraphToSearch().findPathToHq(lastTaskDestination);

        try {
            Path p = new Path(path);
            int[] deliveryCosts = Graph.getInstance().getPathCosts(path);

            int deliveryMinutes = deliveryCosts[0];
            float deliveryDistance = deliveryCosts[1] / 100.0f; // divide pixel distance for 100 to get distance in km
            float deliveryConsumption = consumption(deliveryDistance);
            float deliveryBudget = budget(deliveryConsumption);
            Task t = new Task(p, -1, new Date(schedule.getLastTaskDeliveryTime().getTime() + (deliveryMinutes * 60 * 1000)), 0, deliveryDistance, deliveryConsumption, deliveryBudget);
            schedule.addTask(t);
        } catch (Exception e) {
            log("Couldn't create task.");
        }
    }

    public boolean addAcceptedProposalToSchedule(Proposal p) {

        Task delivery = null, gas = null;

        if(p.getProposedPath().size() == 0) {
            log("Path with no steps");
            return false;
        }

        ArrayList<Integer> hqPath = new ArrayList<Integer>();
        ArrayList<Integer> deliveryPath = new ArrayList<Integer>();
        boolean goesToHq = false, previous;

        for(Integer id : p.getProposedPath()) {
            previous = goesToHq;

            if(gasStations.contains(id)) {
                goesToHq = true;
            }
            if(!previous) {
                hqPath.add(id);
            }
            if(goesToHq) {
                deliveryPath.add(id);
            }
        }

        // if stop for gas is false, delivery is p.getProposedPath()

        int[] deliveryCosts = goesToHq ? Graph.getInstance().getPathCosts(deliveryPath) : Graph.getInstance().getPathCosts(p.getProposedPath());
        int[] tripToHqCosts = goesToHq ? Graph.getInstance().getPathCosts(hqPath) : null;


        int deliveryMinutes = deliveryCosts[0];
        float deliveryDistance = deliveryCosts[1] / 100.0f; // divide pixel distance for 100 to get distance in km
        float deliveryConsumption = consumption(deliveryDistance);
        float deliveryBudget = budget(deliveryConsumption);

        int hqMinutes = goesToHq ? tripToHqCosts[0] : 0;
        float hqDistance = goesToHq ? tripToHqCosts[1] / 100.0f : 0;
        float hqConsumption = goesToHq ? consumption(hqDistance) : 0;
        float hqBudget = goesToHq ? budget(hqConsumption) : 0;

        Date deliveryDate = new Date(schedule.getLastTaskDeliveryTime().getTime() + ((goesToHq ? hqMinutes + deliveryMinutes : deliveryMinutes)  * 60 * 1000));
        Date hqDate = new Date(schedule.getLastTaskDeliveryTime().getTime() + hqMinutes * 60 * 1000);
        //log("\ntrying to add " + p.getProposedPath().get(0) + " -> " + p.getProposedPath().get(p.getProposedPath().size() - 1));

        try {
            Path pathToDelivery = new Path(goesToHq ? deliveryPath : p.getProposedPath());
            Path pathToHq = goesToHq ? new Path(hqPath) : null;
            delivery = new Task(pathToDelivery,
                                p.getCfpId(),
                                deliveryDate,
                                calls.get(p.getCfpId()).getNumBoxes(),
                                deliveryDistance,
                                deliveryConsumption,
                                deliveryBudget);

            gas = goesToHq ? new Task(pathToHq,
                            p.getCfpId(),
                            hqDate,
                            0,
                            hqDistance,
                            hqConsumption,
                            hqBudget) : null;

            if(goesToHq) {
                schedule.addTask(gas);
            }
            schedule.addTask(delivery);
        } catch (Exception e) {
            log("Couldn't create task.");
            return false;
        }

        if(maxCapacity - schedule.getLoadSinceLastTripToHeadQuarters() < 6) {
            goToHeadQuarters();
        }

        return true;
    }

    public float consumption(float distance) {
        float consumptionRatio = 0;
        float consumptionIncreaseWithWeight = 0;

        if(type.equals(ELECTRIC_TYPE)) {
            consumptionRatio = ELECTRIC_CONSUMPTION;
            consumptionIncreaseWithWeight = ELECTRIC_CONSUMPTION_INCREASE_WITH_WEIGHT;
        }
        else if(type.equals(GAS_TYPE)) {
            consumptionRatio = GAS_CONSUMPTION;
            consumptionIncreaseWithWeight = GAS_CONSUMPTION_INCREASE_WITH_WEIGHT;
        }
        else if(type.equals(DIESEL_TYPE)) {
            consumptionRatio = DIESEL_CONSUMPTION;
            consumptionIncreaseWithWeight = DIESEL_CONSUMPTION_INCREASE_WITH_WEIGHT;
        }

        return distance * (consumptionRatio + schedule.getLoadSinceLastTripToHeadQuarters() / maxCapacity * consumptionIncreaseWithWeight) / 100f;
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

    public ArrayList<Integer> getPathTo(int destination) {

        ArrayList<Integer> path = schedule.numberOfTasks() == 0 ?
                Graph.getInstance().getGraphToSearch().findPath(startPosition.getId(), destination) :
                Graph.getInstance().getGraphToSearch().findPath(schedule.getLastTaskDestination(), destination);

        return path;
    }

    public ArrayList<Integer> mergePaths(ArrayList<Integer> pathToHq, ArrayList<Integer> pathToDelivery) {
        ArrayList<Integer> finalPath = new ArrayList<Integer>(pathToHq.size() + pathToDelivery.size() - 1);

        for(Integer id : pathToHq) {
            finalPath.add(id);
        }

        for(int i = 1; i < pathToDelivery.size(); i++) {
            finalPath.add(pathToDelivery.get(i));
        }

        return finalPath;
    }

    public Proposal handleCallForProposal(Cfp cfp) {
        //TODO: use a pathfinding algorithm to see how long it would take for it to distribute the request
        /*
        distance traveled: 25
        loadIfAccepts: 85%
	    costIfAccepts: 8€
         */
        DijkstraGraph g = Graph.getInstance().getGraphToSearch();
        ArrayList<Integer> pathToDelivery, pathToGasStation, pathToHeadQuarters = null;
        float remainingFuel = tankSize - schedule.getConsumedFuelSinceLastTripToGasStation();
        int remainingSpace = (int)maxCapacity - schedule.getLoadSinceLastTripToHeadQuarters();
        int headQuartersId = -1;

        if(remainingSpace < cfp.getNumBoxes()) {
            int lastTaskDestination = schedule.getLastTaskDestination();
            pathToHeadQuarters = g.findPathToHq(lastTaskDestination);
            log("I have to go to HQ to make delivery " + cfp.getId());
        }

        headQuartersId = pathToHeadQuarters != null ? pathToHeadQuarters.get(pathToHeadQuarters.size() - 1) : -1;

        pathToDelivery = pathToHeadQuarters != null ? g.findPath(headQuartersId, cfp.getDestination()) : getPathTo(cfp.getDestination());

        int load = pathToHeadQuarters != null ? (int)(cfp.getNumBoxes() * 100 / maxCapacity) : (int) ((schedule.getLoadSinceLastTripToHeadQuarters() + cfp.getNumBoxes()) * 100 / maxCapacity);

        // log("received call for " + cfp.getNumBoxes() + " my current load is " + schedule.getLoadSinceLastTripToHeadQuarters() + " and my capacity is " + maxCapacity);

        int[] deliveryCosts = Graph.getInstance().getPathCosts(pathToDelivery);
        int deliveryMinutes = deliveryCosts[0];
        float deliveryDistance = deliveryCosts[1] / 100.0f; // divide pixel distance for 100 to get distance in km
        float deliveryConsumption = consumption(deliveryDistance);
        float deliveryBudget = budget(deliveryConsumption);

        int[] tripToHqCosts = null;
        int hqMinutes = 0;
        float hqDistance = 0;
        float hqConsumption = 0;
        float hqBudget = 0;



        if(pathToHeadQuarters != null) {
            tripToHqCosts = Graph.getInstance().getPathCosts(pathToHeadQuarters);
            hqMinutes = tripToHqCosts[0];
            hqDistance = tripToHqCosts[1] / 100.0f;
            hqConsumption = consumption(hqDistance);
            hqBudget = budget(hqConsumption);
            log("added path to headquarters");
        }
        else if(schedule.getLastTaskDestination() == hq) {
            hqMinutes = schedule.getLastTask().getMinutes();
            hqDistance = schedule.getLastTask().getDistanceToComplete();
            hqConsumption = schedule.getLastTask().getNecessaryFuel();
            hqBudget = schedule.getLastTask().getExpense();
            log("added previous trip to HQ");
        }

        Date arrival = new Date(schedule.getLastTaskDeliveryTime().getTime() + ((hqMinutes + deliveryMinutes)  * 60 * 1000));


        Proposal toPropose = new Proposal(
                                            cfp.getId(),
                                            cfp.getParentId(),
                                            getAID(),
                                            pathToHeadQuarters != null ? mergePaths(pathToHeadQuarters, pathToDelivery) : pathToDelivery,
                                            arrival,
                                            load,
                                            hqMinutes + deliveryMinutes,
                                            hqDistance + deliveryDistance,
                                            hqConsumption + deliveryConsumption,
                                            hqBudget + deliveryBudget);

        proposals.put(cfp.getId(), toPropose);
        calls.put(cfp.getId(), cfp);

        return toPropose;
    }

    public void paint(Graphics g, int x, int y, int width, int height) {
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        g.setColor(new Color(255, 255, 255));
        g.fillRect(x, y, width, height);
        g.setColor(new Color(0, 0, 0));
        g.drawString("Name: " + name, x + 10, y + 30);
        g.drawString(schedule.toString(), x + 10, y + 50);
        g.drawString("Boxes: " + schedule.getTotalBoxesCarried(), x + 10, y + 70);
        g.drawString("Fuel: " + df.format(schedule.getTotalFuelConsumed()), x + 70, y + 70);
        g.drawString("km: " + df.format(schedule.getTotalDistance()), x + 160, y + 70);
        g.drawString(df.format(schedule.getTotalExpense()) + " €", x + 250, y + 70);
        g.drawString(df.format(schedule.getTotalScheduleDuration()) + " min", x + 300, y + 70);
        g.setColor(agentColor);
        g.fillRect(width - 20, y, 20, 20);
    }
}
