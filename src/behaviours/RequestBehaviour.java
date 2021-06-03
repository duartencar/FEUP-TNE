package behaviours;

import agents.RequestAgent;
import agents.Vehicle;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetInitiator;
import logic.Request;

import java.util.*;

public class RequestBehaviour extends ContractNetInitiator {
    private AID bestCompany;
    private int bestCost;
    private int counter;
    private MessageTemplate mt;
    private int step = 0;
    private RequestAgent parent;
    private Map<String,Proposal> proposals;

    private class Proposal {
        Request request;
        int bestOffer;
        AID offerer;
        ACLMessage msg;
        Proposal(Request req) {
            this.request = req;
            bestOffer = Integer.MAX_VALUE;
            offerer = null;
            msg = null;
        }
    }

    public RequestBehaviour(RequestAgent p) {
        super(p, new ACLMessage(ACLMessage.CFP));
        this.parent = p;
        proposals = new HashMap<>();
    }
/*
    private int numBoxes;
    private GraphNode destination;
    private int deliveryTime;
 */
    @Override
    protected Vector prepareCfps(ACLMessage cfp) {
        Vector<ACLMessage> cfps = new Vector<>();
        ArrayList<Request> requests = this.parent.getRequests();
        ArrayList<AID> logistics = this.parent.getVehicles();

        for (AID vehicle : logistics) {
            cfp.addReceiver(vehicle);
        }

        for (Request request : requests) {
            Proposal proposal = new Proposal(request);
            proposals.put(request.getId() + "", proposal);
            String createMessage = "";
            createMessage += request.getNumBoxes()+"-"+request.getDeliveryTime()+"-"+request.getDestination().getId()+"-time-"+request.getId();
            cfp.setContent(createMessage);
            cfps.add(cfp);
        }
        return cfps;
    }

    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {
        //super.handleAllResponses(responses, acceptances);
        Enumeration e = responses.elements();
        while (e.hasMoreElements()) {
            ACLMessage msg = (ACLMessage) e.nextElement();
            if (msg.getPerformative() == ACLMessage.PROPOSE) {
                String[] content = msg.getContent().split("-");
                Proposal proposal = proposals.get(content[1]);
                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                acceptances.addElement(reply);
                if (proposal.bestOffer > Integer.parseInt(content[0])) {
                    proposal.offerer = msg.getSender();
                    proposal.bestOffer = Integer.parseInt(content[0]);
                    proposal.msg = reply;
                }
            }
        }

        for (Map.Entry proposal : proposals.entrySet()) {
            Proposal prop = (Proposal) proposal.getValue();
            if (prop.offerer != null) {
                prop.msg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                prop.msg.setContent(prop.request.getId() + "-" + prop.request.getDeliveryTime() + "-" + prop.request.getDestination().toString() + "-" + prop.request.getNumBoxes() + "-" + this.parent.getName());
            }
            else {//TODO: repeat 15 minutes later
            }
        }
    }

    /*protected void handlePropose(ACLMessage propose) {

    }*/
}
