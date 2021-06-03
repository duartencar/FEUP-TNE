package agents;

import behaviours.RequestBehaviour;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import logic.Request;

import java.util.ArrayList;

public class RequestAgent extends Agent {
    private ArrayList<Request> requests;
    private final String name;
    public RequestAgent(String n, String reqFile){
        this.name = n;
        requests = new ArrayList<>();
    }

    public RequestAgent(String n, int num) {
        this.name = n;
        requests = new ArrayList<>();
        /*for (int i = 0; i < num; i++) {

        }*/
    }

    public ArrayList<Request> getRequests() {
        return requests;
    }

    public void setRequests(ArrayList<Request> requests) {
        this.requests = requests;
    }

    public void addRequest(Request request) {
        this.requests.add(request);
    }

    public void generateRandomRequest() {

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
}
