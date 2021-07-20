package agents;

import behaviours.complex.requester.MakeComplexCfp;
import behaviours.simple.MakeContractRequests;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import logic.Proposal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ComplexRequestAgent extends RequestAgent {

    private HashMap<Integer, Integer> numberOfAnswers;
    private ConcurrentHashMap<Integer, Integer> expectedNumberOfAnswersForCfp;
    private ConcurrentHashMap<Integer, ArrayList<Proposal>> cfpProposalsStorage;


    public ComplexRequestAgent(int id, String name, String randomRequests, String fileOrNumberOfRequests, char heuristic) throws Exception {
        super(id, name, randomRequests, fileOrNumberOfRequests, heuristic);
        numberOfAnswers = new HashMap<Integer, Integer>(requests.size());
        expectedNumberOfAnswersForCfp = new ConcurrentHashMap<Integer, Integer>(requests.size());
        cfpProposalsStorage = new ConcurrentHashMap<Integer, ArrayList<Proposal>>(requests.size());
    }

    public void removeProposalFrom(int cfpId, AID proposer) {
        ArrayList<Proposal> proposalsForCfp = cfpProposalsStorage.get(cfpId);

        Proposal rejected = proposalsForCfp.remove(0);

        if(!rejected.getProposalParentId().equals(proposer)) {
            log("Removed the wrong proposal");
        }
    }

    public boolean doIAlreadyHaveAProposalFromThisVehicle(int cfpId, AID proposerId) {
        ArrayList<Proposal> proposalsForCfp = cfpProposalsStorage.get(cfpId);

        for(Proposal p : proposalsForCfp) {
            if(p.getProposalParentId().equals(proposerId)) {
                return true;
            }
        }

        return false;
    }

    public boolean areProposalsLeftFor(int cfpId) {
        ArrayList<Proposal> proposalsForCfp = cfpProposalsStorage.get(cfpId);

        return proposalsForCfp.size() > 0;
    }

    public ArrayList<Proposal> removeProposalsFrom(int cfpId) {
        return cfpProposalsStorage.remove(cfpId);
    }

    public void initializeProposalsStorage(int cfpId, int expectedNumberOfProposals) {
        ArrayList<Proposal> proposals = new ArrayList<Proposal>(expectedNumberOfProposals);

        cfpProposalsStorage.put(cfpId, proposals);
    }

    public Proposal getCurrentBestProposal(int cfpId) {
        return cfpProposalsStorage.get(cfpId).get(0);
    }

    public void addProposalForCfp(int cfpId, Proposal p) {
        ArrayList<Proposal> proposalsForCfp = cfpProposalsStorage.get(cfpId);
        Proposal toCompare;
        final float newProposalEvaluation = evaluateProposal(p);
        float evaluation;

        if(proposalsForCfp.size() == 0) {
            proposalsForCfp.add(p);
            return;
        }

        for(int i = 0; i < proposalsForCfp.size(); i++) {
            toCompare = proposalsForCfp.get(i);
            evaluation = evaluateProposal(toCompare);

            if(newProposalEvaluation <= evaluation) {
                proposalsForCfp.add(i, p);
                return;
            }
        }
    }

    public void sendQueryIf(Integer cfpId) {
        log("Sent query if");
        ACLMessage msg = new ACLMessage(ACLMessage.QUERY_IF);
        Proposal bestProposal = getCurrentBestProposal(cfpId);

        msg.setConversationId(cfpId.toString());
        msg.addReceiver(bestProposal.getProposalParentId());

        send(msg);
    }


    @Override
    protected void setup() {
        log("Hi I'm a complex requester");
        if(requests.size() > 0) {
            addBehaviour(new MakeContractRequests(this, 1000 + id * 10));
        }
        else {
            log("Didn't have requests.");
        }
    }

    public void setExpectedAnswersForCfpId(Integer cfpId, int numberOfExpectedAnswers) {
        expectedNumberOfAnswersForCfp.put(cfpId, numberOfExpectedAnswers);
    }

    public void someAgentRefusedToAnswerCfp(Integer cfpId) {
        expectedNumberOfAnswersForCfp.put(cfpId, expectedNumberOfAnswersForCfp.get(cfpId) - 1);
    }

    public void anotherAnswerToCfp(Integer cfpId) {
        numberOfAnswers.put(cfpId, numberOfAnswers.get(cfpId) + 1);
        log(cfpId + " now has " + numberOfAnswers.get(cfpId));
    }

    public void initializeNumberOfAnswersCounterForCfp(Integer cfpId) {
        numberOfAnswers.put(cfpId, 0);
    }

    public boolean cfpHasReceivedAllProposals(Integer cfpId) {
        return expectedNumberOfAnswersForCfp.get(cfpId) == numberOfAnswers.get(cfpId) && expectedNumberOfAnswersForCfp.get(cfpId) == cfpProposalsStorage.get(cfpId).size();
    }

}
