package map;

import java.util.Objects;

import static utils.Utils.convertToInteger;
import static utils.Utils.log;

public abstract class GraphElement implements Comparable<GraphElement> {

    private int id;

    public GraphElement(String id) {
        try {
            this.id = convertToInteger(id);
        } catch (NumberFormatException e) {
            log("Couldn't convert id: " + id);
            log(e.getMessage());
        }
    }

    public GraphElement(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GraphElement that = (GraphElement) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public int compareTo(GraphElement other) {
        if(id > other.id) {
            return 1;
        }
        else if(id < other.id) {
            return -1;
        }
        else {
            return 0;
        }
    }
}
