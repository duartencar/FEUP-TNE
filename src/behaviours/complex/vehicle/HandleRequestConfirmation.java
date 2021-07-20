package behaviours.complex.vehicle;

import agents.ComplexVehicle;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import logic.Proposal;

public class HandleRequestConfirmation extends CyclicBehaviour {

    private final ComplexVehicle v;
    private final MessageTemplate mt;

    public HandleRequestConfirmation(ComplexVehicle v) {
        this.v = v;
        this.mt = MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF);
    }

    @Override
    public void action() {
        ACLMessage msg = v.receive(mt);
        ACLMessage reply = null;
        Proposal proposal = null;

        if(msg != null) {
            v.log("Received query if");
            int cfpId = Integer.parseInt(msg.getConversationId());
            reply = msg.createReply();
            reply.setPerformative(ACLMessage.CANCEL);

            if(v.doIHaveThisProposal(cfpId)) {
                if(v.doIHaveBetterProposals(cfpId)) {
                    v.removeProposal(cfpId);
                    v.log("I have better proposals than " + cfpId);
                }
                else {
                    proposal = v.removeProposal(cfpId);

                    if(v.addAcceptedProposalToSchedule(proposal)) {
                        reply.setPerformative(ACLMessage.AGREE);
                        v.log("Accepted new proposal, my schedule now is: " + v.getSchedule().toString());
                    }
                    else {
                        v.log("Couldn't add to " + cfpId);
                    }
                }
            }
            else {
                v.log("I don't have proposal to " + cfpId);
            }

            v.send(reply);
        }
    }
}
