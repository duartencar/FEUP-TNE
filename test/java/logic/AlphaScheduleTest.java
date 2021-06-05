package logic;

import map.Graph;
import map.search.DijkstraGraph;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import utils.Utils;

import java.util.ArrayList;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class AlphaScheduleTest {
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

        AlphaSchedule toTest = new AlphaSchedule(1);

        assertEquals("Empty", toTest.toString(), "Path should be: Empty");
        assertEquals((short)0, toTest.getTotalScheduleDuration(), "Total cost should be 0");
        assertEquals(0, toTest.numberOfTasks(), "Number of tasks should be 0");
    }

    @Test
    public void testSingleAdd() {
        DijkstraGraph toTest = getGraph().getGraphToSearch();

        ArrayList<Integer> path = toTest.findPath(1, 5);

        AlphaSchedule sc = new AlphaSchedule(1);

        Path p = null;

        try {
            p = new Path(path);
            sc.addTask(new Task(p, 1, new Date(), 0, 10, 1, 1));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }

        assertEquals("1 -> 5", sc.toString(), "Path should be: 1 -> 5");
        assertEquals(p.getWeight(), sc.getTotalScheduleDuration(), "Cost should be the same as path");
        assertEquals(1, sc.numberOfTasks(), "Number of tasks should be 1");
    }

    @Test
    public void testSequentialAdd() {
        DijkstraGraph sg = getGraph().getGraphToSearch();
        ArrayList<Integer> path1 = sg.findPath(1, 5);
        ArrayList<Integer> path2 = sg.findPath(5, 2);
        ArrayList<Integer> path3 = sg.findPath(2, 7);
        AlphaSchedule sc = new AlphaSchedule(1);

        Path p1 = null, p2 = null, p3 = null;

        try {
            p1 = new Path(path1);
            p2 = new Path(path2);
            p3 = new Path(path3);
            sc.addTask(new Task(p1, 1, new Date(), 0, 10, 1, 1));
            sc.addTask(new Task(p2, 2, new Date(), 0, 10, 1, 1));
            sc.addTask(new Task(p3, 3, new Date(), 0, 10, 1, 1));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }

        assertEquals("1 -> 5 -> 2 -> 7", sc.toString(), "Path should be: 1 -> 5 -> 2 -> 7");
        assertEquals(p1.getWeight() + p2.getWeight() + p3.getWeight(), sc.getTotalScheduleDuration(), "Cost should be the same as path");
        assertEquals(3, sc.numberOfTasks(), "Number of tasks should be 3");
    }

    @Test
    public void testRandomAdd() {
        DijkstraGraph sg = getGraph().getGraphToSearch();
        ArrayList<Integer> path1 = sg.findPath(1, 5);
        ArrayList<Integer> path2 = sg.findPath(5, 2);
        ArrayList<Integer> aux = sg.findPath(2, 7);
        ArrayList<Integer> path3 = sg.findPath(5, 7);
        AlphaSchedule sc = new AlphaSchedule(1);

        Path p1 = null, p2 = null, p3 = null, auxPath = null;

        try {
            p1 = new Path(path1);
            p3 = new Path(path3);
            sc.addTask(new Task(p1, 1, new Date(), 0, 10, 1, 1));
            sc.addTask(new Task(p3, 3, new Date(), 0, 10, 1, 1));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }

        assertEquals("1 -> 5 -> 7", sc.toString(), "Path should be: 1 -> 5");
        assertEquals(p1.getWeight() + p3.getWeight(), sc.getTotalScheduleDuration(), "Cost should be the same as path");
        assertEquals(2, sc.numberOfTasks(), "Number of tasks should be 3");

        try {
            p2 = new Path(path2);
            auxPath = new Path(aux);
            sc.addTask(new Task(p2, 2, new Date(), 0, 10, 1, 1));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }

        assertEquals("1 -> 5 -> 2 -> 7", sc.toString(), "Path should be: 1 -> 5 -> 2 -> 7");
        assertNotEquals(p1.getWeight() + p2.getWeight() + p3.getWeight(), sc.getTotalScheduleDuration(), "Cost shouldn't be the same as the sum of previous paths");
        assertEquals(p1.getWeight() + p2.getWeight() + auxPath.getWeight(), sc.getTotalScheduleDuration(), "Cost should be the same as the sum of previous paths from previous test");
        assertEquals(3, sc.numberOfTasks(), "Number of tasks should be 3");
    }

    @Test
    void testGetMainPoints() {
        DijkstraGraph sg = getGraph().getGraphToSearch();
        ArrayList<Integer> path1 = sg.findPath(1, 5);
        ArrayList<Integer> path2 = sg.findPath(5, 2);
        ArrayList<Integer> path3 = sg.findPath(2, 7);
        AlphaSchedule sc = new AlphaSchedule(1);

        Path p1 = null, p2 = null, p3 = null;

        try {
            p1 = new Path(path1);
            p2 = new Path(path2);
            p3 = new Path(path3);
            sc.addTask(new Task(p1, 1, new Date(), 1, 10, 1, 1));
            sc.addTask(new Task(p2, 2, new Date(), 2, 10, 1, 1));
            sc.addTask(new Task(p3, 3, new Date(), 3, 10, 1, 1));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            fail();
        }

        String toTest = "";

        for(Integer id : sc.getScheduleMainPoints()) {
            toTest += id + " ";
        }

        assertEquals("1 5 2 7 ", toTest, "string should be: 1 5 2 7 ");
        assertEquals(6, sc.getLoadSinceLastTripToHeadQuarters(), "Should be 6 ");
    }
}
