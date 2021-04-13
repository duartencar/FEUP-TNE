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

    public static void main(String[] args) {

        createSimulationContainer(args[0].equals("-" + Profile.GUI));
    }
}
