package behaviours;

import agents.Vehicle;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;
import logic.Cfp;
import logic.Proposal;
import logic.Request;
import map.Graph;
import map.GraphNode;

import java.io.IOException;
import java.util.Map;

import static utils.Utils.convertToInteger;

public class VehicleReceiveBehaviour extends ContractNetResponder {
    private final Vehicle parent;
    private Cfp requestToAnswer = null;

    public VehicleReceiveBehaviour(Vehicle a, MessageTemplate template) {
        super(a, template);
        parent = a;
    }

    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) {
        ACLMessage reply = null;

            try {
                reply = cfp.createReply();

                requestToAnswer = (Cfp) cfp.getContentObject();

                if(requestToAnswer == null) {
                    parent.log("Received null object.");
                    reply.setPerformative(ACLMessage.REFUSE);
                    return reply;
                }

                reply.setPerformative(ACLMessage.PROPOSE);
                final Proposal answer = parent.handleCallForProposal(requestToAnswer);
                reply.setContentObject(answer);
                parent.log("SENT: " + answer.toString());

                return reply;
            }catch (UnreadableException e) {
                parent.log("There was an error");
                parent.log(e.getMessage());
                reply.setPerformative(ACLMessage.REFUSE);
                return reply;
            } catch (IOException e) {
                parent.log("Couldn't bind proposal to message");
                parent.log(e.getMessage());
                reply.setPerformative(ACLMessage.REFUSE);
                return reply;
            }
    }

    protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) {
        //parent.log("My offer was rejected");
    }

    @Override
    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {

        //parent.log("My offer was accepted");
        ACLMessage inform = cfp.createReply();
        Proposal doneProposal = null;

        try {
            doneProposal = (Proposal)propose.getContentObject();

            if(parent.addAcceptedProposalToSchedule(doneProposal)) {
                inform.setPerformative(ACLMessage.INFORM);
            }
            else {
                inform.setPerformative(ACLMessage.INFORM);
            }
        } catch (UnreadableException unreadableException) {
            parent.log("Failed to get proposal from object: " + unreadableException.getMessage());
            throw new FailureException(unreadableException.getMessage());
        }

        parent.log("Accepted new proposal, my shcedule now is: " + parent.getSchedule().toString());


        //TODO: check if no new better offer
        /*if (true) {
            //Request(String id, String parentId, int numBoxes, GraphNode destination, int deliveryTime)
            //prop.request.getId() + "-" + prop.request.getDeliveryTime() + "-" + prop.request.getDestination() + "-" + prop.request.getNumBoxes() + "-" + this.parent.getName()
            String[] content = cfp.getContent().split("-");
            GraphNode node = null;
            Graph g = Graph.getInstance();
            for (Map.Entry n: g.nodes.entrySet()) {
                if (n.getValue().toString().equals(content[2])) {
                    node = ((GraphNode) n.getValue());
                    break;
                }
            }
            Request req = new Request(convertToInteger(content[0]), convertToInteger(content[4]),Integer.parseInt(content[3]), node, Integer.parseInt(content[1]));
            this.parent.addRequest(req);
            reply.setPerformative(ACLMessage.INFORM);
        }
        else
            reply.setPerformative(ACLMessage.FAILURE);*/
        return inform;
    }
}
