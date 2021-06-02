package logic;

import map.Graph;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import utils.Utils;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AlphaSheduleTest {
    private Graph getGraph() {
        Graph a = Graph.getInstance();

        final Document testFile = Utils.openAndParseXmlFile("test/resources/testGraph.xml");

        NodeList nodeList = testFile.getElementsByTagName("node");

        NodeList edgeList = testFile.getElementsByTagName("edge");

        a.init(nodeList, edgeList);

        return a;
    }

    @Test
    public void testCreate() {
        getGraph();

        AlphaSchedule toTest = new AlphaSchedule();

        assertEquals("Empty", toTest.toString(), "Path should be: Empty");
        assertEquals((short)0, toTest.getTotalScheduleCost(), "Total cost should be 0");
    }
}
