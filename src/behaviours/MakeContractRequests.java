package behaviours;

import agents.RequestAgent;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import logic.Cfp;
import logic.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class MakeContractRequests extends TickerBehaviour {
    RequestAgent p;

    public MakeContractRequests (RequestAgent parent, long time) {
        super(parent, time);
        p = parent;
    }

    @Override
    protected void onTick() {
        int i = 0;
        Cfp cfp = null;

        for(Request r : p.getRequestsToPerform().values()){
            if(i == p.currentRequest) {
                cfp = r.getRequestCfp();
                break;
            }
        }

        if(cfp != null) {
            ACLMessage msg = new ACLMessage(ACLMessage.CFP);
            ArrayList<AID> vehicles = p.getVehicles();

            for(AID v : vehicles) {
                msg.addReceiver(v);
            }

            msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);

            msg.setReplyByDate(new Date(System.currentTimeMillis() + 1000));

            try {
                msg.setContentObject(cfp);
            } catch (IOException e) {
                p.log("couldn't attach object to message");
            }

            p.log("Starting CFP for: " + cfp.toString());

            p.addBehaviour(new RequestBehaviour(p, msg, vehicles.size()));

            p.currentRequest++;
        }
        else {
            p.log("No more requests to do.");
            this.stop();
        }
    }
}
