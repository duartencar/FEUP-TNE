package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.*;
import jade.domain.FIPAException;
import utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public abstract class Elementary extends Agent {
    protected ArrayList<AID> logisticsAgents;

    protected boolean registerServices(ArrayList<ServiceDescription> serviceDescriptions){
        DFAgentDescription agentDescription = new DFAgentDescription();
        agentDescription.setName(getAID());

        for(ServiceDescription serviceDescription : serviceDescriptions){
            agentDescription.addServices(serviceDescription);
        }

        try {
            DFService.register(this, agentDescription );
        }
        catch (FIPAException fe) {
            Utils.print(getAID().getName(), "Failed to register.");
            Utils.print(getAID().getName(), fe.getMessage());
            return false;
        }


        return true;
    }

    public ArrayList<AID> getLogisticsAgents() {
        return logisticsAgents;
    }

    public void setLogisticsAgents(ArrayList<AID> logisticsAgents) {
        this.logisticsAgents = logisticsAgents;
    }

    protected void takeDown(){
        try {
            DFService.deregister(this);
        } catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    public AID getAgentByID(String id){
        for (AID agent : logisticsAgents){
            if(id.equals(agent.getLocalName())){
                return agent;
            }
        }
        return null;
    }

    protected void updateAgents(ArrayList<AID> agents, String type){
        DFAgentDescription agentDescription = new DFAgentDescription();
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(type);
        agentDescription.addServices(serviceDescription);

        try {
            DFAgentDescription[] result = DFService.search(this, agentDescription);
            agents.clear();
            for(DFAgentDescription agent : result){
                agents.add(agent.getName());
            }
        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

    protected boolean registerInYellowPages(String type, String name) {
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setType(type);
        serviceDescription.setName(name);

        return registerServices(new ArrayList<ServiceDescription>(Arrays.asList(serviceDescription)));
    }

    public synchronized void log(String message) {
        Date a = new Date();
        System.out.println(a.toString() + " -> " + getLocalName() + ": " + message);
    }
}
