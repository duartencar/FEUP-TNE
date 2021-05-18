package gui.layout;

import agents.Vehicle;

import javax.swing.*;
import java.util.ArrayList;

public class AgentsPanel extends JPanel {
    private JScrollPane scrol;
    private ArrayList<Vehicle> vehicleAgents;

    public AgentsPanel() {
        vehicleAgents = new ArrayList<Vehicle>(1);
    }
}
