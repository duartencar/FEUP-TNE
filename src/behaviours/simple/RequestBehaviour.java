package behaviours.simple;

import agents.RequestAgent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import logic.Proposal;

import java.util.Enumeration;
import java.util.Vector;

public class RequestBehaviour extends ContractNetInitiator {
    private int nResponders;
    final private RequestAgent parent;

    public RequestBehaviour(RequestAgent p, ACLMessage msg, int numberOfResponders) {
        super(p, msg);
        this.parent = p;
        nResponders = numberOfResponders;
    }

    protected void handleFailure(ACLMessage failure) {
        if (failure.getSender().equals(myAgent.getAMS())) {
            // FAILURE notification from the JADE runtime: the receiver
            // does not exist
            System.out.println("Responder does not exist");
        }
        else {
            System.out.println("Agent "+failure.getSender().getName()+" failed");
        }
        // Immediate failure --> we will not receive a response from this agent
        nResponders--;
    }

    protected void handleRefuse(ACLMessage refuse) {
        System.out.println(refuse.getSender().getLocalName() + " refused to answer.");

    }


    protected void handleAllResponses(Vector responses, Vector acceptances) {

        if (responses.size() < nResponders) {
            // Some responder didn't reply within the specified timeout
            parent.log("Timeout expired: missing " + (nResponders - responses.size()) + " responses");
        }

        Enumeration e = responses.elements();

        float bestEvaluation = Float.MAX_VALUE;
        Proposal bestProposal = null;
        AID bestProposer = null;
        ACLMessage accept = null;

        while (e.hasMoreElements()) {
            ACLMessage msg = (ACLMessage) e.nextElement();

            if (msg.getPerformative() == ACLMessage.PROPOSE) {

                ACLMessage reply = msg.createReply();
                reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                acceptances.addElement(reply);

                Proposal content;
                try {
                    content = (Proposal)msg.getContentObject();
                } catch (UnreadableException unreadableException) {
                    parent.log("Failed to get proposal from object: " + unreadableException.getMessage());
                    return;
                }
                // parent.log("RECEIVED: " + content.toString());
                float evaluation = parent.evaluateProposal(content);
                if(evaluation < bestEvaluation) {
                    bestProposal = content;
                    bestEvaluation = evaluation;
                    bestProposer = msg.getSender();
                    accept = reply;
                }
            }
        }

        if(accept != null) {
            parent.log("Accepting proposal "+ bestProposal.toString() + " from vehicle " + bestProposer.getLocalName());
            accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
        }
    }

    protected void handleInform(ACLMessage inform) {
        parent.log("Agent scheduled task.");
        // parent.getGui().addNewRequest(bestProposal.getProposedPath().get(bestProposal.getProposedPath().size() - 1));
        parent.getGui().update();
    }

    /*protected void handlePropose(ACLMessage propose) {

    }*/
}
