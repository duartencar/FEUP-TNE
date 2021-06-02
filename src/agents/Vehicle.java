package agents;

import logic.Request;

import map.GraphNode;
import logic.Path;

import utils.Utils;

import java.awt.*;
import java.util.ArrayList;

public class Vehicle extends Elementary {
    private final GraphNode startPos;
    private final String type;
    private final String name;
    private Path currentPath;
    private final float tankSize;
    private final float maxCapacity;
    private float currentLoad;
    private float profit;
    private ArrayList<Request> requests;

    public Vehicle(String n, String t, GraphNode sp, float ts, float mc) {
        name = n;
        type = t;
        startPos = sp;
        tankSize = ts;
        maxCapacity = mc;
        profit = 0;
        currentLoad = 0;
        currentPath = null;
        requests = new ArrayList<Request>();
    }

    public void setup() {
        log("hello my name is " + getAID().getLocalName());

        if (!registerInYellowPages(type, name))
            Utils.print(name,"Failed to register to yellow pages services");
    }

    public String getVehicleName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public GraphNode getStartPos() {
        return startPos;
    }

    public Path getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(Path currentPath) {
        this.currentPath = currentPath;
    }

    public double getTankSize() {
        return tankSize;
    }

    public double getMaxCapacity() {
        return maxCapacity;
    }

    public double getCurrentLoad() {
        return currentLoad;
    }

    public void setCurrentLoad(float currentLoad) {
        this.currentLoad = currentLoad;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(float profit) {
        this.profit = profit;
    }

    public ArrayList<Request> getRequests() {
        return requests;
    }

    public boolean addRequest(Request newRequest) {
        if (currentLoad + newRequest.getNumBoxes() > maxCapacity) {
            //TODO: check if new request exceeds gas tank by checking consumption (requires the pathfinding algorithm)
            return false;
        }
        String[] date1 = newRequest.getDeliveryTime().split("/");
        int day = Integer.parseInt(date1[0]);
        int month = Integer.parseInt(date1[1]);
        int year = Integer.parseInt(date1[2]);
        int i = 0;
        for (Request r : requests) {
            date1 = r.getDeliveryTime().split("/");
            int d = Integer.parseInt(date1[0]);
            int m = Integer.parseInt(date1[1]);
            int y = Integer.parseInt(date1[2]);

            if (year < y || year == y && month < m || year == y && month == m && day < d)
                break;
            i++;
        }
        requests.add(i,newRequest);
        currentLoad += newRequest.getNumBoxes();
        return true;
    }

    public void removeRequest(int index) {
        if (index < requests.size()) {
            currentLoad -= requests.get(index).getNumBoxes();
            requests.remove(index);
        }
    }

    public double consumption(int speed, double gasPrice) {
        //Let’s settle on an average of around 30 or 40 litres every 100 km. src: https://www.webfleet.com/en_gb/webfleet/blog/do-you-know-the-diesel-consumption-of-a-lorry-per-km/
        /*
        25.000kg - 35l/100km
        16.000Kg - 25l/100km
        (35-25)/(25.000-16.000) = 10/9.000 =  0.01/9 = 0.0011111L/100km.kg
        para simplificar, vou assumir que cada caixa pesa 1000Kg
        Consumo base (sem carga):
        15 L/100km (estou a assumir, can be completely wrong though)
        para a influencia da velocidade, vou multiplicar o resultado do resto pelo exponencial da velocidade a dividir por 100, com os valores truncados a 120 (porque os camioes nao andam mais que isso)
        isto faz com que o consumo parado seja o resultado da conta anterior (porque gasta enquanto parado), a 50km/h multiplicado por 1.649 e a 100km/h multiplicado por 2.718 (parece-me realista)
        caso queiramos que o consumo enquanto parado seja 0 (porque pode desligar o motor, idk) podemos usar em vez do exponencial (velocidade/50)². Assim a 0 fica a 0, a 50 fica miplicado por 1 e a 100
        fica multiplicado por 4
         */
        double consumption = 15 + currentLoad/9;
        //consumption *= Math.pow(speed/50,2);
        consumption *= Math.exp(speed/100);

        return consumption;
    }

    public void paint(Graphics g, int x, int y, int width, int height) {
        g.setColor(new Color(255, 255, 255));
        g.fillRect(x, y, width, height);
        g.setColor(new Color(0, 0, 0));
        g.drawString("Nome: " + name, x + 10, y + 30);
    }
}
