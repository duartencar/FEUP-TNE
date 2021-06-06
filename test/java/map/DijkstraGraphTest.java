package map;

import map.search.DijkstraGraph;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import utils.Utils;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class DijkstraGraphTest {

    private Graph getGraph() {
        Graph a = Graph.getInstance();

        final Document testFile = Utils.openAndParseXmlFile("test/resources/testGraph.xml");

        NodeList nodeList = testFile.getElementsByTagName("node");

        NodeList edgeList = testFile.getElementsByTagName("edge");

        a.init(nodeList, edgeList);

        return a;
    }

    @Test
    void testCreate() {
        Graph a = getGraph();

        DijkstraGraph toTest = a.getGraphToSearch();

        assertNotNull(toTest, "This shouldn't be null");

        assertEquals(a.getNumberOfNodes(), toTest.getNumberOfNodes(), "Graph should have the same number of elements has the original");
    }

    @Test
    void testFindPath() {
        Graph a = getGraph();

        assertTrue(a.isValid(), "Graph should be valid");

        DijkstraGraph toTest = a.getGraphToSearch();

        ArrayList<Integer> path = toTest.findPath(1, 5);

        assertTrue(path.size() == 4, "Path should have 4 steps");

        assertEquals(1, path.get(0));
        assertEquals(2, path.get(1));
        assertEquals(4, path.get(2));
        assertEquals(5, path.get(3));
    }

    @Test
    void testFindPathWithInvalidNodes() {
        Graph a = getGraph();

        assertTrue(a.isValid(), "Graph should be valid");

        DijkstraGraph toTest = a.getGraphToSearch();

        ArrayList<Integer> path = toTest.findPath(1, 10);

        assertEquals(0, path.size(), "Path should be empty");
    }
}
