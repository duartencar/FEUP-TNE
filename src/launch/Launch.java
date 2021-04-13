package launch;

import jade.Boot;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.util.ExtendedProperties;
import jade.wrapper.ContainerController;

import java.util.Properties;

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
    private static void loadMap(String mapName) {

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
        createSimulationContainer(args[0].equals("-" + Profile.GUI));
        loadMap(args[1]);
        loadAndStartAgents(args[2]);
    }
}
