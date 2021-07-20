package behaviours.complex.requester;

import agents.ComplexRequestAgent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import logic.Proposal;

import java.util.ArrayList;

public class HandleConfirmationResponses extends CyclicBehaviour {

    private final ComplexRequestAgent p;
    private final MessageTemplate mt;

    public HandleConfirmationResponses(ComplexRequestAgent requester, Integer cfpId) {
        p = requester;
        mt = MessageTemplate.MatchConversationId(cfpId.toString());
    }
    @Override
    public void action() {
        ACLMessage msg = p.receive(mt);
        ACLMessage reply, rejectMessage;
        int cfpId;

        if(msg != null) {
            reply = msg.createReply();
            cfpId = Integer.parseInt(msg.getConversationId());
            if(msg.getPerformative() == ACLMessage.AGREE) {
                ArrayList<Proposal> proposals = p.removeProposalsFrom(cfpId);

                /* FIST REJECT OTHERS */
                rejectMessage = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
                rejectMessage.setConversationId(msg.getConversationId());
                for(int i = 1; i < proposals.size(); i++) {
                    rejectMessage.addReceiver(proposals.get(i).getProposalParentId());
                }
                p.log("send " + (proposals.size() - 1) + " reject messages");
                p.send(rejectMessage);

                /* THEN INFORM THE BEST PROPOSER */
                reply.setPerformative(ACLMessage.INFORM);
                p.send(reply);
                p.log("SENT REJECTS AND INFORMS");
                p.removeBehaviour(this);
            }
            else if(msg.getPerformative() == ACLMessage.CANCEL) {
                p.removeProposalFrom(cfpId, msg.getSender());

                if(p.areProposalsLeftFor(cfpId)) {
                    p.sendQueryIf(cfpId);
                }
                else {
                    p.log("There are no proposals left");
                }
            }
        }
        else {
            block(10);
        }
    }
}
