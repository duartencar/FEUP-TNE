package agents;

import behaviours.MakeContractRequests;

import gui.DistributedLogistics;
import jade.core.AID;
import logic.Proposal;
import logic.Request;

import map.Graph;
import map.GraphNode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import utils.Utils;

import java.util.ArrayList;

import static utils.Constants.AgentsProperties.RequestAgent.*;
import static utils.Constants.AgentsProperties.VehicleAgent.SERVICE_TYPE;
import static utils.Constants.Directories.SIMULATIONS_DATA_PATH;
import static utils.Utils.*;
import static utils.Utils.convertToInteger;

public class RequestAgent extends Elementary {
    final private int id;
    final private String agentName;
    final private int mode;
    final private char heuristic;
    final private ArrayList<Request> requests;
    public int currentRequest = 0;
    DistributedLogistics gui;

    public RequestAgent(int id, String name, String randomRequests, String fileOrNumberOfRequests, char heuristic) throws Exception {
        this.id=id;
        this.heuristic = heuristic;
        agentName = name;
        mode = convertToInteger(randomRequests);
        requests = new ArrayList<Request>();

        switch (mode) {
            case FILE_MODE:
                String requestsFilePath = SIMULATIONS_DATA_PATH + fileOrNumberOfRequests;
                final Document requestsFile = Utils.openAndParseXmlFile(requestsFilePath);
                if(requestsFile == null) {
                    throw new Exception("Couldn't open requests file -> " + requestsFilePath);
                }
                log("loading " + requestsFilePath);
                if(!parseRequests(requestsFile)) {
                    throw new Exception("Failed to parse " + requestsFilePath + " requests.");
                }
                break;
            case RANDOM_MODE:
                int numberOfRequestsToGenerate = convertToInteger(fileOrNumberOfRequests);
                if(numberOfRequestsToGenerate == 0) {
                    throw new Exception("Number of requests must be above 0");
                }
                generateRandomRequests(numberOfRequestsToGenerate);
                break;
            default:
                throw new Exception("Invalid agent mode.");
        }
    }

    public DistributedLogistics getGui() {
        return gui;
    }

    public void setGui(DistributedLogistics g) {
        this.gui = g;
    }

    public ArrayList<Request> getRequests() {
        return requests;
    }

    public ArrayList<AID> getVehicles() {
        ArrayList<AID> vehicles = new ArrayList<>();

        updateAgents(vehicles, SERVICE_TYPE);

        return vehicles;
    }

    protected void setup() {
        if(requests.size() > 0) {
            addBehaviour(new MakeContractRequests(this, 1000 + id * 10));
        }
        else {
            log("Didn't have requests.");
        }
    }

    private boolean parseRequests(Document requestsDoc) {
        final NodeList requestList = requestsDoc.getElementsByTagName("request");
        int numberOfBoxes, destinationId;
        GraphNode destinationNode;

        for(int i = 0; i < requestList.getLength(); i++) {
            Node request = requestList.item(i);

            if(request.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) request;

                String requesterId = element.getAttribute("requesterId");

                if(id != convertToInteger((requesterId))) {
                    continue;
                }

                String numBoxes = element.getAttribute("numBoxes");
                String destination = element.getAttribute("destination");
                String deliveryTime = element.getAttribute("deliveryTime");

                if(numBoxes.equals("") || destination.equals("") || deliveryTime.equals("")) {
                    log("One of the arguments doesn't exist on line " + (i+1));
                    return false;
                }

                try {
                    numberOfBoxes = convertToInteger(numBoxes);
                    destinationId = convertToInteger(destination);
                    destinationNode = Graph.getInstance().nodes.get(destinationId);

                    if(destinationNode == null) {
                        log("destination node doesn't exist in line " + (i+1));
                        return false;
                    }

                    Request toAdd = generateRequest(i, numberOfBoxes,destinationNode,deliveryTime);

                    requests.add(toAdd);

                } catch(NumberFormatException e) {
                    log("Couldn't parse one argument of the request in line " + (i + 1));
                    return false;
                }
            }
        }

        return requests.size() > 0;
    }

    private Request generateRequest(int requestId, int numBoxes, GraphNode location, String deliveryTime) {
        return new Request(id * AGENT_DOMAIN + requestId, id, numBoxes, location, 10);
    }

    private void generateRandomRequests(int numberOfRequestsToGenerate) {

        Graph g = Graph.getInstance();

        for (int i = 0; i < numberOfRequestsToGenerate; i++) {
            Request newRequest = generateRequest(i,1 + generateInt(8), g.getRandomLocation(), "1200");
            requests.add(newRequest);
        }
    }

    float absoluteDistanceHeuristic(Proposal p) {
        return p.getTotalDistance();
    }

    float absoluteCost(Proposal p) {
        return p.getPrice();
    }

    public float evaluateProposal(Proposal p) {
        float evaluation = Float.MAX_VALUE;

        switch(heuristic) {
            case 'D':
                evaluation = absoluteDistanceHeuristic(p);
                break;
            case 'C':
                evaluation = absoluteCost(p);
                break;
            default:
                evaluation = absoluteDistanceHeuristic(p);
                break;
        }

        return evaluation;
    }
}
