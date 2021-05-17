package launch;

import agents.RequestAgent;
import jade.Boot;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.wrapper.ContainerController;
import map.Graph;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utils.Utils;

import java.util.Properties;

import static utils.Constants.Arguments.*;
import static utils.Constants.Directories.MAPS_PATH;
import static utils.Constants.Directories.SIMULATIONS_PATH;
import static utils.Utils.log;

public class Launch extends Boot {
    private static ProfileImpl simulationProfile;
    private static ContainerController simulationContainerController;
    private static Properties simulationProperties;

    private static void createSimulationContainer(boolean gui) {
        simulationProperties = new ExtendedProperties();

        simulationProfile = new ProfileImpl((jade.util.leap.Properties) simulationProperties);

        if(gui) {
            simulationProperties.setProperty(Profile.GUI, "true");
        }

        Runtime.instance().setCloseVM(true);

        if(simulationProfile.getBooleanProperty(Profile.MAIN,true)){
            simulationContainerController = Runtime.instance().createMainContainer(simulationProfile);
        } else {
            simulationContainerController = Runtime.instance().createAgentContainer(simulationProfile);
        }
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
        int numberOfAgents = 0;

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

                if(name.equals("") || vehicleType.equals("") || tank.equals("") || capacity.equals("") || pathFindingAlgorithm.equals("")) {
                    log("Element in line " + i + " is invalid " + element.getBaseURI().toString());
                    continue;
                }

                /*
                VehicleAgent vehicle = new VehicleAgent(name, vehicleType, tank, capacity, pathFindingAlgorithm, startNode);
                simulationContainerController.acceptNewAgent(name, vehicle);
                */
                numberOfAgents++;
            }
        }

        return numberOfAgents;
    }

    private static int parseRequesterAgents(Document agentsFile) {
        NodeList agentsList = agentsFile.getElementsByTagName("requester");
        int numberOfAgents = 0;

        for(int i = 0; i < agentsList.getLength(); i++) {
            Node agent = agentsList.item(i);

            if(agent.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) agent;

                String name = element.getAttribute("name");
                String random = element.getAttribute("random");
                String numberOfRequests = element.getAttribute("numberOfRequests");
                String requestFile = element.getAttribute("requestFile");


                RequestAgent requester = null;
                try {
                    requester = new RequestAgent(i, name, random, random.equals("1") ? numberOfRequests : requestFile);
                    simulationContainerController.acceptNewAgent(name, requester);
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

    public static void main(String[] args) {
        
        checkParameters(args);

        if(!loadMap(args[ARGUMENT_MAP_INDEX])) {
            log("Couldn't load Graph. Shutting down...");
            System.exit(0);
        }

        createSimulationContainer(args[ARGUMENT_GUI_INDEX].equals("-" + Profile.GUI));

        if(loadAndStartAgents(args[ARGUMENT_AGENTS_INDEX])) {
            log("Problem with agents file");
            System.exit(0);
        }

        return;
    }
}