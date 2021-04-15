package map;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {

    @Test
    void getInstance() {
        Graph a, b;

        a = Graph.getInstance();
        b = Graph.getInstance();

        assertNotNull(a, "Object should be null.");
        assertNotNull(b, "Object should be null.");

        assertEquals(a, b, "objects should reference to the same object");
    }
}