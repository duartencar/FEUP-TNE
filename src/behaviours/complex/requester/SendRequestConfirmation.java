package behaviours.complex.requester;

import agents.ComplexRequestAgent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import logic.Proposal;

public class SendRequestConfirmation extends OneShotBehaviour {
    ComplexRequestAgent p;
    private final Integer cfpId;

    public SendRequestConfirmation(ComplexRequestAgent requestAgent, int cfpId) {
        p = requestAgent;
        this.cfpId = cfpId;
    }
    @Override
    public void action() {
        ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
        Proposal bestProposal = p.getCurrentBestProposal(cfpId);

        msg.setConversationId(cfpId.toString());
        msg.addReceiver(bestProposal.getProposalParentId());

        p.send(msg);
    }
}
