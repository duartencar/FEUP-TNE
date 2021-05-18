package gui;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import gui.layout.AgentsPanel;

import java.awt.Canvas;
import java.awt.Color;

public class DistributedLogistics {
	
	public static final String WINDOW_TITLE = "Distributed Logistics - DashBoard";
	public static final int HEIGHT = 769;
	public static final int WIDTH = 1366;
	public static final int BORDER = 10;
	
	JFrame mainFrame;
	AgentsPanel agentsScrollableDisplay;
	Canvas mapDisplay;
	
	public DistributedLogistics() {
		createMainFrame();
		createScrollableDisplay();
		createCanvas();
	}
	
    public void createMainFrame() {
    	mainFrame = new JFrame(WINDOW_TITLE);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(WIDTH,HEIGHT);
        mainFrame.getContentPane().setLayout(null);
    }
    
    public void createScrollableDisplay() {
    	agentsScrollableDisplay = new AgentsPanel();
        agentsScrollableDisplay.setBounds(BORDER, BORDER, (WIDTH - 2 * BORDER) / 5, HEIGHT - 6 * BORDER);
        mainFrame.getContentPane().add(agentsScrollableDisplay);
    }
    
    public void createCanvas() {
    	mapDisplay = new Canvas();
    	mapDisplay.setBackground(Color.BLACK);
    	mapDisplay.setBounds(BORDER * 2 + WIDTH / 5, BORDER, WIDTH - (BORDER * 4 + WIDTH / 5), HEIGHT - 6 * BORDER);
        mainFrame.getContentPane().add(mapDisplay);
        mainFrame.setVisible(true);
    }
}
