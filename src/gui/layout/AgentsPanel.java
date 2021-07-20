package gui.layout;

import agents.Vehicle;
import gui.DistributedLogistics;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class AgentsPanel extends JPanel {
    private JScrollPane scrol;
    private ArrayList<Vehicle> vehicleAgents;
    private final int length;
    private final int AGENT_AREA_HEIGHT = 100;
    private final int AGENT_AREA_WIDTH = (DistributedLogistics.WIDTH - 2 * DistributedLogistics.BORDER) / 5 - 5;
    DistributedLogistics father;

    public AgentsPanel(ArrayList<Vehicle> agents) {
        vehicleAgents = agents;
        length = agents.size() * AGENT_AREA_HEIGHT;
        this.setPreferredSize(new Dimension(AGENT_AREA_WIDTH, length));
        System.out.println();
    }

    public void setPanelFather(DistributedLogistics f) {
        father = f;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Vehicle agent;

        for(int i = 0; i < vehicleAgents.size(); i++) {
            agent = vehicleAgents.get(i);

            agent.paint(g, 0, i == 0 ? i : i * AGENT_AREA_HEIGHT + 10, this.getPreferredSize().width, AGENT_AREA_HEIGHT);
        }
    }
}
