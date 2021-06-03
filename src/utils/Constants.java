package utils;

public final class Constants {

    public final class Arguments {
        public static final int ARGUMENT_GUI_INDEX = 0;
        public static final int ARGUMENT_MAP_INDEX = 1;
        public static final int ARGUMENT_AGENTS_INDEX = 2;
    }

    public final class Directories {
        public static final String MAPS_PATH = "data/maps/";
        public static final String SIMULATIONS_PATH = "data/simulations/";
        public static final String SIMULATIONS_DATA_PATH = "data/simulations/simulationFiles/";
    }

    public final class AgentsProperties {

        public final class RequestAgent {
            public static final int RANDOM_MODE = 1;
            public static final int FILE_MODE = 0;
            public static final int AGENT_DOMAIN = 100000;
        }

        public final class VehicleAgent {
            public static final String SERVICE_NAME = "request-delivery";
            public static final String SERVICE_TYPE = "transport";
        }
    }
}
