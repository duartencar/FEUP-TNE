package behaviours.complex;

import agents.ComplexRequestAgent;
import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import logic.Cfp;
import logic.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class MakeComplexCfp extends TickerBehaviour {

    ComplexRequestAgent p;

    public MakeComplexCfp(ComplexRequestAgent parent, long time) {
        super(parent, time);
        p = parent;
    }

    @Override
    protected void onTick() {
        int i = 0;
        Cfp cfp = null;

        for(Request r : p.getRequests()){
            if(i == p.currentRequest) {
                cfp = r.getRequestCfp();
                break;
            }
            i++;
        }

        if(cfp != null) {
            ACLMessage msg = new ACLMessage(ACLMessage.CFP);
            msg.setConversationId("" + cfp.getId());
            ArrayList<AID> vehicles = p.getVehicles();

            for(AID v : vehicles) {
                msg.addReceiver(v);
            }

            msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));

            try {
                msg.setContentObject(cfp);
            } catch (IOException e) {
                p.log("couldn't attach object to message");
            }

            p.send(msg);

            p.log("Starting CFP for " + p.getVehicles().size()+ " vehicles:" + cfp.toString());

            //p.addBehaviour(new RequestBehaviour(p, msg, vehicles.size()));

            p.currentRequest++;
        }
        else {
            p.log("No more requests to do.");
            this.stop();
        }
    }
}
