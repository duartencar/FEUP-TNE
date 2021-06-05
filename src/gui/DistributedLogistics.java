package gui;

import javax.swing.*;

import agents.Vehicle;
import gui.layout.AgentsPanel;
import gui.layout.MapDisplay;

import java.awt.Color;
import java.util.ArrayList;

public class DistributedLogistics {
	
	public static final String WINDOW_TITLE = "Distributed Logistics - DashBoard";
	public static final int HEIGHT = 1000;
	public static final int WIDTH = 1920;
	public static final int BORDER = 10;
	
	JFrame mainFrame;
	AgentsPanel agentsScrollableDisplay;
	JScrollPane scrol;
	MapDisplay mapDisplay;

	final ArrayList<Vehicle> agents;
	
	public DistributedLogistics(ArrayList<Vehicle> agents) throws Exception {

		if(agents.size() == 0) {
			throw new Exception("No agents.");
		}

		this.agents = agents;

		createMainFrame();
		createScrollableDisplay();
		createCanvas();

		//mainFrame.setVisible(false);
	}

	public void update() {
		agentsScrollableDisplay.repaint();
		mapDisplay.repaint();
	}
	
    public void createMainFrame() {
    	mainFrame = new JFrame(WINDOW_TITLE);
    	mainFrame.getContentPane().setBackground(Color.LIGHT_GRAY);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(WIDTH,HEIGHT);
        mainFrame.getContentPane().setLayout(null);
    }
    
    public void createScrollableDisplay() {
    	agentsScrollableDisplay = new AgentsPanel(agents);
		agentsScrollableDisplay.setPanelFather(this);

		scrol = new JScrollPane(agentsScrollableDisplay, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrol.setBounds(BORDER, BORDER, (WIDTH - 2 * BORDER) / 5, HEIGHT - 6 * BORDER);
		scrol.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255)));

		mainFrame.getContentPane().add(scrol);
    }
    
    public void createCanvas() {
    	mapDisplay = new MapDisplay(agents);
    	mapDisplay.setBackground(Color.BLACK);
    	mapDisplay.setBounds(BORDER * 2 + WIDTH / 5, BORDER, WIDTH - (BORDER * 4 + WIDTH / 5), HEIGHT - 6 * BORDER);
        mainFrame.getContentPane().add(mapDisplay);
        mainFrame.setVisible(true);
    }
}
