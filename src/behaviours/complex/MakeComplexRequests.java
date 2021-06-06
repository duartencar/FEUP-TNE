package behaviours.complex;

import agents.ComplexRequestAgent;
import jade.core.behaviours.TickerBehaviour;

public class MakeComplexRequests extends TickerBehaviour {

    ComplexRequestAgent p;

    public MakeComplexRequests(ComplexRequestAgent parent, long time) {
        super(parent, time);
        p = parent;
    }

    @Override
    protected void onTick() {

    }
}
