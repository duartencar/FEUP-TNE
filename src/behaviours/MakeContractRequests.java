package behaviours;

import agents.RequestAgent;
import agents.Vehicle;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import logic.Cfp;
import logic.Request;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static utils.Utils.log;

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

            p.log("vehicles -> " + vehicles.size());

            for(AID v : vehicles) {
                msg.addReceiver(v);
            }

            msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);

            msg.setReplyByDate(new Date(System.currentTimeMillis() + 500));

            try {
                msg.setContentObject(cfp);
            } catch (IOException e) {
                p.log("couldn't attach object to message");
            }

            p.log("Starting CFP for: " + cfp.toString());

            p.addBehaviour(new RequestBehaviour(p, msg));

            p.currentRequest++;
        }
        else {
            p.log("No more requests to do.");
            this.stop();
        }
    }
}
