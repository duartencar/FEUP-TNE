package agents;

import behaviours.complex.MakeComplexCfp;

public class ComplexRequestAgent extends RequestAgent {

    public ComplexRequestAgent(int id, String name, String randomRequests, String fileOrNumberOfRequests, char heuristic) throws Exception {
        super(id, name, randomRequests, fileOrNumberOfRequests, heuristic);
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
}
