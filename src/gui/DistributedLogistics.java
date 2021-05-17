package gui;

import javax.swing.*;

public class DistributedLogistics {

    public DistributedLogistics() {
        JFrame frame = new JFrame("Distributed Logistics - DashBoard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1366,769);
        JButton button = new JButton("Press");
        frame.getContentPane().add(button); // Adds Button to content pane of frame
        frame.setVisible(true);
    }
}
