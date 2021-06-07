package behaviours.complex.requester;

import agents.ComplexRequestAgent;
import jade.core.behaviours.OneShotBehaviour;

public class SendRequestConfirmation extends OneShotBehaviour {
    ComplexRequestAgent p;
    private final Integer cfpId;

    public SendRequestConfirmation(ComplexRequestAgent requestAgent, int cfpId) {
        p = requestAgent;
        this.cfpId = cfpId;
        p.log("SendRequestConfirmation");
    }
    @Override
    public void action() {

    }
}
