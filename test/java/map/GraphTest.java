package map;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import utils.Utils;

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

    @Test
    void init() {
        final Document testFile = Utils.openAndParseXmlFile("test/resources/testGraph.xml");

        NodeList nodeList = testFile.getElementsByTagName("node");

        NodeList edgeList = testFile.getElementsByTagName("edge");

        Graph a = Graph.getInstance();

        assertFalse(a.isValid(), "Graph should be invalid because it hasn't been initialized");

        assertTrue(a.init(nodeList, edgeList), "Initialization should have returned true");

        assertTrue(a.isValid(), "Graph should be invalid because it hasn't been initialized");

        assertEquals(nodeList.getLength(), a.getNumberOfNodes(), "The number of nodes in graph should be the number of parsed nodes");

        assertEquals(edgeList.getLength(), a.getNumberOfEdges(), "The number of edges in graph should be the number of parsed nodes");
    }
}