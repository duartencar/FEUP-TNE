package behaviours.complex.requester;

import agents.ComplexRequestAgent;
import agents.ComplexVehicle;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import logic.Proposal;

public class HandleAllComplexResponses extends CyclicBehaviour {

    ComplexRequestAgent p;
    private final MessageTemplate mt;

    public HandleAllComplexResponses(ComplexRequestAgent a, Integer cfpId) {
        super(a);
        p = a;
        mt = MessageTemplate.MatchConversationId(cfpId.toString());
    }

    @Override
    public void action() {
        ACLMessage msg = p.receive(mt);

        if(msg != null) {
            int cfpId = Integer.parseInt(msg.getConversationId());
            if (msg.getPerformative() == ACLMessage.PROPOSE) {
                try {
                    Proposal content = (Proposal)msg.getContentObject();
                    p.log("received proposal from " + msg.getSender().getLocalName() + " related to cfp " + msg.getConversationId());
                    p.anotherAnswerToCfp(cfpId);
                    p.addProposalForCfp(cfpId, content);
                } catch (UnreadableException unreadableException) {
                    p.log("Failed to get proposal from object: " + unreadableException.getMessage());
                    return;
                }
            } else if (msg.getPerformative() == ACLMessage.REFUSE) {
                p.log("received refusal from " + msg.getSender().getLocalName() + " related to cfp " + msg.getConversationId());
                p.someAgentRefusedToAnswerCfp(cfpId);
            }

            if(p.cfpHasReceivedAllProposals(cfpId)) {
                p.log("Has received all answers");
            }
        }
        else {
            block();
        }
    }
}
