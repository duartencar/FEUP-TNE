package agents;

import logic.Request;
import map.Graph;

import java.util.ArrayList;

import static utils.Constants.AgentsProperties.RequestAgent.*;
import static utils.Utils.*;

public class RequestAgent extends Elementary {
    private int id;
    private String agentName;
    private int mode;
    private ArrayList<Request> requests;

    public RequestAgent(int id, String name, String randomRequests, String fileOrNumberOfRequests) throws Exception {
        agentName = name;
        mode = convertToInteger(randomRequests);
        requests = new ArrayList<Request>(1);

        switch (mode) {
            case FILE_MODE:
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

    private void generateRandomRequests(int numberOfRequestsToGenerate) {

        Graph g = Graph.getInstance();

        for (int i = 0; i < numberOfRequestsToGenerate; i++) {
            Request newRequest = new Request(id * AGENT_DOMAIN + i, id, 1 + generateInt(8), g.getRandomLocation(), "1200");
            requests.add(newRequest);
        }
    }
}
