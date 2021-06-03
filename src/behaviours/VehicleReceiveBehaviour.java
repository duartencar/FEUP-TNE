package behaviours;

import agents.Vehicle;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import logic.Request;
import map.Graph;
import map.GraphNode;

import java.util.Map;

import static utils.Utils.convertToInteger;

public class VehicleReceiveBehaviour extends ContractNetResponder {
    private final Vehicle parent;
    public VehicleReceiveBehaviour(Vehicle a) {
        super(a, MessageTemplate.MatchPerformative(ACLMessage.CFP));
        parent = a;
    }

    protected ACLMessage handleCfp(ACLMessage cfp) {
        ACLMessage reply = cfp.createReply();
        String[] content = cfp.getContent().split("-");
        if (parent.canHandleRequest(Integer.parseInt(content[0]))) {
            reply.setPerformative(ACLMessage.PROPOSE);
            reply.setContent(parent.handleCallForProposal(Integer.parseInt(content[0]), Integer.parseInt(content[1]), content[2], content[3])+"-"+content[4]);
        } else {
            reply.setPerformative(ACLMessage.REFUSE);
        }
        return reply;
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
