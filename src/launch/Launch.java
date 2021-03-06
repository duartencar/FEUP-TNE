package launch;

import agents.ComplexRequestAgent;
import agents.ComplexVehicle;
import agents.RequestAgent;
import agents.Vehicle;
import gui.DistributedLogistics;
import jade.Boot;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import map.Graph;
import map.GraphNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utils.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Properties;

import static utils.Constants.Arguments.*;
import static utils.Constants.Directories.MAPS_PATH;
import static utils.Constants.Directories.SIMULATIONS_PATH;
import static utils.Utils.*;

public class Launch extends Boot {
    private static ProfileImpl simulationProfile;
    private static ContainerController simulationContainerController;
    private static Properties simulationProperties;

    private static ArrayList<Vehicle> vehicleAgents;
    private static ArrayList<RequestAgent> requestAgents;
    private static ArrayList<AgentController> agentsController;

    private static void createSimulationContainer(boolean gui) {
        simulationProperties = new ExtendedProperties();

        if(gui) {
            simulationProperties.setProperty(Profile.GUI, "true");
        }

        simulationProfile = new ProfileImpl((jade.util.leap.Properties) simulationProperties);

        Runtime.instance().setCloseVM(true);

        if(simulationProfile.getBooleanProperty(Profile.MAIN,true)){
            simulationContainerController = Runtime.instance().createMainContainer(simulationProfile);
        } else {
            simulationContainerController = Runtime.instance().createAgentContainer(simulationProfile);
        }

        agentsController = new ArrayList<AgentController>();
    }

    /**
     * Receives the map name and searches for the folder
     * ./data/maps/<mapName> and reads the nodes and edges
     * files inside.
     * @param mapName - name of the folder where the map files are
     */
    private static boolean loadMap(String mapName) {
        String mapPath = MAPS_PATH + "/" + mapName + "/" + mapName + ".xml";

        final Document mapFile = Utils.openAndParseXmlFile(mapPath);

        if(mapFile == null) {
            log("Couldn't open " + mapPath);
            return false;
        }

        NodeList nodeList = mapFile.getElementsByTagName("node");

        NodeList edgeList = mapFile.getElementsByTagName("edge");

        Graph a = Graph.getInstance();

        a.init(nodeList, edgeList);

        return a.isValid();
    }

    private static int parseVehicleAgents(Document agentsFile) {
        NodeList agentsList = agentsFile.getElementsByTagName("vehicle");
        boolean complex = false;
        if(agentsList.getLength() == 0) {
            agentsList = agentsFile.getElementsByTagName("complexvehicle");
            complex = true;
        }

        vehicleAgents = new ArrayList<Vehicle>(agentsList.getLength());

        int numberOfAgents = 0;
        int r, g, b;

        for(int i = 0; i < agentsList.getLength(); i++) {
            Node agent = agentsList.item(i);

            if(agent.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) agent;

                String name = element.getAttribute("name");
                String vehicleType = element.getAttribute("type");
                String tank = element.getAttribute("tank");
                String capacity = element.getAttribute("capacity");
                String pathFindingAlgorithm = element.getAttribute("pathFinding");
                String startNode = element.getAttribute("startNode");
                String agentColor = element.getAttribute("color");
                String[] rgbValues = agentColor.split(" ");

                if(rgbValues.length != 3) {
                    log("Element in line " + i + " has invalid color " + element.getBaseURI().toString());
                    continue;
                }

                r = convertToInteger(rgbValues[0]);
                g = convertToInteger(rgbValues[1]);
                b = convertToInteger(rgbValues[2]);

                if(r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
                    log("Element in line " + i + " has color values out of range " + element.getBaseURI().toString());
                    continue;
                }

                if(name.equals("") || vehicleType.equals("") || tank.equals("") || capacity.equals("") || pathFindingAlgorithm.equals("")) {
                    log("Element in line " + i + " is invalid " + element.getBaseURI().toString());
                    continue;
                }

                GraphNode startPos = Graph.getInstance().nodes.get(convertToInteger(startNode));

                if(startPos == null) {
                    log("Element in line " + i + " has an invalid starting position");
                    continue;
                }

                try {
                    Vehicle vehicle = complex ? new ComplexVehicle(name, vehicleType, startPos, convertToFloat(tank), convertToFloat(capacity)) : new Vehicle(name, vehicleType, startPos, convertToFloat(tank), convertToFloat(capacity));
                    vehicle.setColor(new Color(r, g, b));
                    vehicleAgents.add(vehicle);
                    agentsController.add(simulationContainerController.acceptNewAgent(name, vehicle));
                    numberOfAgents++;
                } catch (Exception e) {
                    log("ERROR PARSING VEHICLE: " + e.getMessage());
                    continue;
                }
            }
        }

        return numberOfAgents;
    }

    private static int parseRequesterAgents(Document agentsFile) {
        NodeList agentsList = agentsFile.getElementsByTagName("requester");
        boolean complex = false;

        if(agentsList.getLength() == 0) {
            agentsList = agentsFile.getElementsByTagName("complexrequester");
            complex = true;
        }

        int numberOfAgents = 0;
        requestAgents = new ArrayList<RequestAgent>(agentsList.getLength());

        for(int i = 0; i < agentsList.getLength(); i++) {
            Node agent = agentsList.item(i);

            if(agent.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) agent;

                String name = element.getAttribute("name");
                String random = element.getAttribute("random");
                String numberOfRequests = element.getAttribute("numberOfRequests");
                String requestFile = element.getAttribute("requestsFile");
                char heuristic = element.getAttribute("heuristic").charAt(0);
                String randomOrRequestFile = random.equals("1") ? numberOfRequests : requestFile;

                RequestAgent requester = null;
                try {
                    requester = complex ? new ComplexRequestAgent(i+1, name, random, randomOrRequestFile, heuristic) : new RequestAgent(i+1, name, random, randomOrRequestFile, heuristic);
                    agentsController.add(simulationContainerController.acceptNewAgent(name, requester));
                    requestAgents.add(requester);
                    numberOfAgents++;
                } catch (Exception e) {
                    log(e.getMessage());
                    continue;
                }
            }
        }

        return numberOfAgents;
    }

    /**
     * Receives the parameters file name and searches for it in
     * ./data/simulations/.
     * @param agentsFile - name of the files with agent parameters
     */
    private static boolean loadAndStartAgents(String agentsFile) {
        String agentsFilePath = SIMULATIONS_PATH + agentsFile + ".xml";

        final Document agentsDoc = Utils.openAndParseXmlFile(agentsFilePath);

        if(agentsDoc == null) {
            log("Couldn't open " + agentsFilePath);
            return false;
        }

        return parseVehicleAgents(agentsDoc) > 0 && parseRequesterAgents(agentsDoc) > 0;
    }

    private static void checkParameters(String[] args) {
        if(args.length != 3) {
            System.out.println("Expected 3 arguments: <-gui?> <mapFolderName> <simulationFile>");
            System.exit(0);
        }
    }

    private static void startAgents() {
        for(AgentController controller : agentsController) {
            try {
                controller.start();
            } catch (StaleProxyException e) {
                log("Couldn't start agent");
                log(e.getMessage());
            }
        }
    }

    private static void addGraphParametersToVehicle() {
        for(Vehicle v: vehicleAgents) {
            v.setGasStations(Graph.getInstance().getGasStations());
            v.setHq(Graph.getInstance().getHeadQuarter());
        }
    }

    private static void addGuiReferenceToRequesters(DistributedLogistics g) {
        for(RequestAgent ra : requestAgents) {
            ra.setGui(g);
        }
    }

    public static void main(String[] args) {
        
        checkParameters(args);

        if(!loadMap(args[ARGUMENT_MAP_INDEX])) {
            log("Couldn't load Graph. Shutting down...");
            System.exit(0);
        }

        createSimulationContainer(args[ARGUMENT_GUI_INDEX].equals("-" + Profile.GUI));

        if(!loadAndStartAgents(args[ARGUMENT_AGENTS_INDEX])) {
            log("Problem with agents file");
            System.exit(0);
        }

        addGraphParametersToVehicle();

        try {
            DistributedLogistics d = new DistributedLogistics(vehicleAgents);
            addGuiReferenceToRequesters(d);
        } catch (Exception e) {
            log("GUI exception: " + e.getMessage());
            System.exit(0);
        }

        startAgents();

        return;
    }
}
