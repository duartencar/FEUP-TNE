package agents;

import behaviours.complex.AnswerComplexCfps;
import map.GraphNode;
import utils.Utils;

import static utils.Constants.AgentsProperties.VehicleAgent.SERVICE_NAME;
import static utils.Constants.AgentsProperties.VehicleAgent.SERVICE_TYPE;

public class ComplexVehicle extends Vehicle {
    public ComplexVehicle(String n, String t, GraphNode sp, float ts, float mc) {
        super(n, t, sp, ts, mc);
    }

    public boolean hasReceivedCfpForId(int cfpId) {
        return calls.get(cfpId) != null;
    }

    @Override
    public void setup() {
        log("Hi I'm a complex vehicle");

        if (!registerInYellowPages(SERVICE_TYPE, SERVICE_NAME))
            Utils.print(name,"Failed to register to yellow pages services");

        addBehaviour(new AnswerComplexCfps(this));
    }
}
