package agents;

import behaviours.complex.MakeComplexCfp;
import logic.Proposal;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ComplexRequestAgent extends RequestAgent {

    private ConcurrentHashMap<Integer, Integer> numberOfAnswers;
    private ConcurrentHashMap<Integer, Integer> expectedNumberOfAnswersForCfp;
    private ConcurrentHashMap<Integer, ArrayList<Proposal>> cfpProposalsStorage;


    public ComplexRequestAgent(int id, String name, String randomRequests, String fileOrNumberOfRequests, char heuristic) throws Exception {
        super(id, name, randomRequests, fileOrNumberOfRequests, heuristic);
        numberOfAnswers = new ConcurrentHashMap<Integer, Integer>(requests.size());
        expectedNumberOfAnswersForCfp = new ConcurrentHashMap<Integer, Integer>(requests.size());
        cfpProposalsStorage = new ConcurrentHashMap<Integer, ArrayList<Proposal>>(requests.size());
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


    @Override
    protected void setup() {
        log("Hi I'm a complex requester");
        if(requests.size() > 0) {
            addBehaviour(new MakeComplexCfp(this, 1000 + id * 10));
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
    }

    public void initializeNumberOfAnswersCounterForCfp(Integer cfpId) {
        numberOfAnswers.put(cfpId, 0);
    }

    public boolean cfpHasReceivedAllProposals(Integer cfpId) {
        return expectedNumberOfAnswersForCfp.get(cfpId) == numberOfAnswers.get(cfpId);
    }

}
