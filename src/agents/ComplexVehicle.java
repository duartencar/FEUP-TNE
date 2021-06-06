package agents;

import map.GraphNode;

public class ComplexVehicle extends Vehicle {
    public ComplexVehicle(String n, String t, GraphNode sp, float ts, float mc) {
        super(n, t, sp, ts, mc);
    }

    @Override
    public void setup() {
        log("Hi I'm a complex vehicle");

    }
}
