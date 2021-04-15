package map;

import static utils.Utils.convertToInteger;
import static utils.Utils.log;

public abstract class GraphElement {

    int id;

    public GraphElement(String id) {
        try {
            this.id = convertToInteger(id);
        } catch (NumberFormatException e) {
            log("Couldn't convert id: " + id);
            log(e.getMessage());
        }
    }
}
