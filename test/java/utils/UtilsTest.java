package utils;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void getFileReference() {
        Scanner file = Utils.getFileReference("test/resources/fileTest.txt");

        assertNotNull(file);

        int nLines = 0;

        while(file.hasNextLine()) {
            file.nextLine();
            nLines++;
        }

        assertEquals(2, nLines, "The number of lines should be 2");
    }

    @Test
    void openAndParseXmlFile() {
        final Document testFile = Utils.openAndParseXmlFile("test/resources/testGraph.xml");

        assertNotNull(testFile, "Should return a Document class");

        NodeList nodeList = testFile.getElementsByTagName("node");

        NodeList edgeList = testFile.getElementsByTagName("edge");

        int numberOfNodes = nodeList.getLength();
        int numberOfEdges = edgeList.getLength();

        assertEquals(16, numberOfNodes + numberOfEdges, "The number of nodes + edges should be 16");
    }
}