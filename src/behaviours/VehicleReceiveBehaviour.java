package behaviours;

import agents.Vehicle;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;
import logic.Cfp;
import logic.Request;
import map.Graph;
import map.GraphNode;

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

        if(cfp != null) {
            try {
                reply = cfp.createReply();

                requestToAnswer = (Cfp) cfp.getContentObject();

                if(requestToAnswer == null) {
                    parent.log("Received null object.");
                    reply.setPerformative(ACLMessage.REFUSE);
                    return reply;
                }

                System.out.println("Agent "+parent.getLocalName()+": CFP received from "+cfp.getSender().getLocalName() + ". Action is "+ requestToAnswer.toString());

                if (parent.canHandleRequest(requestToAnswer.getNumBoxes())) {
                    reply.setPerformative(ACLMessage.PROPOSE);
                    //reply.setContent(parent.handleCallForProposal(Integer.parseInt(content[0]), Integer.parseInt(content[1]), content[2], content[3])+"-"+content[4]);
                } else {
                    reply.setPerformative(ACLMessage.REFUSE);
                }
                return reply;
            }catch (UnreadableException e) {
                parent.log("There was an error");
                parent.log(e.getMessage());
                reply.setPerformative(ACLMessage.REFUSE);
                return reply;
            }
        }
        else {
            block();
        }

        return null;
    }

    protected void handleRejectProposal(ACLMessage cfp) {}

    protected ACLMessage handleAcceptProposal(ACLMessage cfp) {
        ACLMessage reply = cfp.createReply();
        //TODO: check if no new better offer
        if (true) {
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
            reply.setPerformative(ACLMessage.FAILURE);
        return reply;
    }
}
