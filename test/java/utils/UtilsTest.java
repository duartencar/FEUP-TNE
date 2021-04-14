package utils;

import org.junit.jupiter.api.Test;

import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    @Test
    void getFileReference() {
        Scanner file = Utils.getFileReference("test/resources/fileTest.txt");

        assertNotNull(file);

        int nLines = 0;

        while(file.hasNextLine()) {
            nLines++;
            System.out.println(file.nextLine());
        }

        assertEquals(2, nLines, "The number of lines should be 2");
    }
}