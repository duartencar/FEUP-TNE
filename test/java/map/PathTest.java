package map;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import utils.Utils;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PathTest {

    private Graph getGraph() {
        Graph a = Graph.getInstance();

        final Document testFile = Utils.openAndParseXmlFile("test/resources/testGraph.xml");

        NodeList nodeList = testFile.getElementsByTagName("node");

        NodeList edgeList = testFile.getElementsByTagName("edge");

        a.init(nodeList, edgeList);

        return a;
    }

    private ArrayList<Integer> validPathExample() {
        ArrayList<Integer> rawPath = new ArrayList<Integer>(4);

        rawPath.add(1);
        rawPath.add(0);
        rawPath.add(7);
        rawPath.add(6);
        rawPath.add(3);

        return rawPath;
    }

    @Test
    void testPathCreation() {
        getGraph();
        ArrayList<Integer> rawPath = validPathExample();
        Path p = null;

        try {
            p = new Path(rawPath);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        assertNotNull(p, "Path variable shouldn't be null");

        assertEquals(5, p.getNumberOfSteps(), "This path should have 5 steps");

        assertEquals(26, p.getWeight(), "The path weight is wrong");
    }

    @Test
    void testCreationFail() {
        ArrayList<Integer> rawPath = validPathExample();
        rawPath.add(20);

        assertThrows(Exception.class, () -> {
            Path p = new Path(rawPath);
        });
    }
}
