package map.ui;

import java.awt.*;

public class UiEdge {
    private int destinationId;
    private int startX;
    private int startY;
    private int endX;
    private int endY;
    Color color;

    public UiEdge(int dest, int x1, int y1, int x2, int y2) {
        destinationId = dest;

        startX = x1;
        startY = y1;

        endX = x2;
        endY = y2;

        color = Color.BLACK;
    }

    public void setColor(Color c) {
        color = c;
    }

    public void paint(Graphics g) {
        g.setColor(color);
        g.drawLine(startX, startY, endX, endY);
    }

    public int getDestinationId() {
        return destinationId;
    }
}
