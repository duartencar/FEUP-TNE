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

            public final class VehicleProperties {
                public static final String ELECTRIC_TYPE = "electric";
                public static final float ELECTRIC_CONSUMPTION = 18f; // 18 kW/100km
                public static final float ELECTRIC_CONSUMPTION_INCREASE_WITH_WEIGHT = 25f; // 0.16 €/kW
                public static final float ELECTRIC_COST = 0.16f; // 0.16 €/kW

                public static final String GAS_TYPE = "gas";
                public static final float GAS_CONSUMPTION = 8f; // 8 L/100km
                public static final float GAS_CONSUMPTION_INCREASE_WITH_WEIGHT = 4f;
                public static final float GAS_COST = 1.7f; // 1.7 €/L

                public static final String DIESEL_TYPE = "diesel";
                public static final float DIESEL_CONSUMPTION = 6.5f; // 6.5 L/100km
                public static final float DIESEL_CONSUMPTION_INCREASE_WITH_WEIGHT = 2.5f;
                public static final float DIESEL_COST = 1.45f; // 1.45 €/L
            }
        }
    }
    
    public final class GuiProperties {
    	
    	public final class Layout {
    		public static final int HEIGHT = 769;
        	public static final int WIDTH = 1366;
        	
        	public static final int BORDER = 10;
    	}
    	
    	public final class Text {
    		public static final String WINDOW_TITLE = "Distributed Logistics - DashBoard";
    	}

    }
}
