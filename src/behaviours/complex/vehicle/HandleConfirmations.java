package behaviours.complex.vehicle;

import agents.ComplexVehicle;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class HandleConfirmations extends CyclicBehaviour {

    ComplexVehicle p;

    public HandleConfirmations(ComplexVehicle v) {
        p = v;
    }

    @Override
    public void action() {
        ACLMessage msg = p.receive();
        int cfpId;

        if(msg != null) {
            cfpId = Integer.parseInt(msg.getConversationId());

            if(msg.getPerformative() == ACLMessage.REJECT_PROPOSAL) {
                p.log("My proposal wasn't accepted");
                p.removeProposal(cfpId);
            }
            else if(msg.getPerformative() == ACLMessage.INFORM) {
                p.log("My proposal was accepted");
            }
        }
    }
}
