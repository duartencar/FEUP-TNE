package agents;

import behaviours.simple.VehicleReceiveBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import logic.Proposal;
import map.GraphNode;
import utils.Utils;

import static utils.Constants.AgentsProperties.VehicleAgent.SERVICE_NAME;
import static utils.Constants.AgentsProperties.VehicleAgent.SERVICE_TYPE;

public class ComplexVehicle extends Vehicle {
    public ComplexVehicle(String n, String t, GraphNode sp, float ts, float mc) {
        super(n, t, sp, ts, mc);
    }

    public boolean hasReceivedCfpForId(int cfpId) {
        return calls.get(cfpId) != null;
    }

    public boolean doIHaveThisProposal(int cfpId) {
        return proposals.get(cfpId) != null;
    }

    public Proposal removeProposal(int cfpId) {
        return proposals.remove(cfpId);
    }

    public boolean doIHaveBetterProposals(int cfpId) {
        Proposal toCompare = proposals.get(cfpId);

        if(toCompare == null) {
            return true;
        }

        for(Proposal p : proposals.values()) {
            if(p.getMinutes() < toCompare.getMinutes()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void setup() {
        log("Hi I'm a complex vehicle");

        if (!registerInYellowPages(SERVICE_TYPE, SERVICE_NAME))
            Utils.print(name,"Failed to register to yellow pages services");

        MessageTemplate template = MessageTemplate.and(
                MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
                MessageTemplate.MatchPerformative(ACLMessage.CFP));

        addBehaviour(new VehicleReceiveBehaviour(this, template));
    }
}
