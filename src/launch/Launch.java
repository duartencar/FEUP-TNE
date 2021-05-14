package launch;

import jade.Boot;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.wrapper.ContainerController;
import map.Graph;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import utils.Utils;

import java.util.Properties;

import static utils.Constants.Arguments.*;
import static utils.Constants.Directories.MAPS_PATH;
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
            log("Couldn't open file");
            return false;
        }

        NodeList nodeList = mapFile.getElementsByTagName("node");

        NodeList edgeList = mapFile.getElementsByTagName("edge");

        Graph a = Graph.getInstance();

        a.init(nodeList, edgeList);

        return a.isValid();
    }

    /**
     * Receives the parameters file name and searches for it in
     * ./data/simulations/.
     * @param agentsFile - name of the files with agent parameters
     */
    private static void loadAndStartAgents(String agentsFile) {

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
        loadAndStartAgents(args[ARGUMENT_AGENTS_INDEX]);
    }
}
