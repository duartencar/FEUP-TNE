package agents;

import behaviours.RequestBehaviour;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import logic.Request;

import map.Graph;
import map.GraphNode;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;

import static utils.Constants.AgentsProperties.RequestAgent.*;
import static utils.Constants.Directories.SIMULATIONS_DATA_PATH;
import static utils.Utils.*;
import static utils.Utils.convertToInteger;

public class RequestAgent extends Elementary {
    final private int id;
    final private String agentName;
    final private int mode;
    final private HashMap<Integer, Request> requests;
    final private ArrayList<Request> requests_old;

    public RequestAgent(int id, String name, String randomRequests, String fileOrNumberOfRequests) throws Exception {
        this.id=id;
        agentName = name;
        mode = convertToInteger(randomRequests);
        requests = new HashMap<Integer, Request>();
        requests_old = new ArrayList<Request>();

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

    public ArrayList<Request> getRequests() {
        return requests_old;
    }

    public ArrayList<AID> getVehicles() {
        ArrayList<AID> vehicles = new ArrayList<>();
        DFAgentDescription template = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType("truck");
        template.addServices(sd);

        try {
            DFAgentDescription[] results = DFService.search(this, template);
            for (DFAgentDescription result : results) {
                vehicles.add(result.getName());
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    protected void setup() {
        addBehaviour(new RequestBehaviour(this));
    }

    private boolean parseRequests(Document requestsDoc) {
        final NodeList requestList = requestsDoc.getElementsByTagName("request");
        int numberOfBoxes, destinationId;
        GraphNode destinationNode;

        for(int i = 0; i < requestList.getLength(); i++) {
            Node request = requestList.item(i);

            if(request.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) request;

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

                    requests.put(toAdd.getId(), toAdd);

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
            requests.put(newRequest.getId(), newRequest);
        }
    }
}
