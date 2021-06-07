package behaviours.complex.vehicle;

import agents.ComplexVehicle;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import logic.Cfp;
import logic.Proposal;

import java.io.IOException;

public class AnswerComplexCfps extends CyclicBehaviour {

    ComplexVehicle v;
    private final MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);

    public AnswerComplexCfps(ComplexVehicle a) {
        super(a);
        v = a;
    }

    @Override
    public void action() {
        Cfp requestToAnswer = null;
        ACLMessage reply = null;
        ACLMessage message = v.receive(mt);
        String conversationId;

        if(message != null) {
            reply = message.createReply();
            reply.setPerformative(ACLMessage.REFUSE);
            conversationId = message.getConversationId();

            if(v.hasReceivedCfpForId(Integer.parseInt(conversationId))) {
                v.log("Repeated cfp");
            }
            else {
                try {
                    requestToAnswer = (Cfp)message.getContentObject();
                    reply.setPerformative(ACLMessage.PROPOSE);
                    final Proposal answer = v.handleCallForProposal(requestToAnswer);
                    reply.setContentObject(answer);
                    v.log("answered cfp " + requestToAnswer.getId());
                } catch (UnreadableException | IOException e) {
                    v.log("There was an error");
                    v.log(e.getMessage());
                    reply.setPerformative(ACLMessage.REFUSE);
                    v.send(reply);
                }
            }
            v.send(reply);
        }

    }
}
