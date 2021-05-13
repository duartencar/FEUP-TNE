package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.Scanner;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Utils {

    public static int convertToInteger (String number) throws NumberFormatException {
        return Integer.parseInt(number);
    }

    public static short convertToShort (String number) throws NumberFormatException {
        return Short.parseShort(number);
    }

    public static float convertToFloat (String number) throws NumberFormatException {
        return Float.parseFloat(number);
    }

    public static void log(String str) {
        System.out.println(str);
    }

    public static Scanner getFileReference(final String filePath) {

        File f;
        Scanner fileReference;

        try {
            f = new File(filePath);

            if(f.exists()) {
                System.out.println(filePath + " file opened.");
            }
            else {
                System.out.println(filePath + " doesn't exist.");
                return null;
            }
        } catch(NullPointerException | SecurityException e) {
            System.out.println(filePath + "couldn't be opened.");
            System.out.println(e.getMessage());
            return null;
        }

        try {
            fileReference= new Scanner(f);
        } catch(FileNotFoundException e) {
            System.out.println(filePath + "couldn't be found.");
            System.out.println(e.getMessage());
            return null;
        }

        return fileReference;
    }

    public static Document openAndParseXmlFile(String mapXmlFile) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            DocumentBuilder db = dbf.newDocumentBuilder();

            Document doc = db.parse(new File(mapXmlFile));

            doc.getDocumentElement().normalize();

            return doc;

        } catch (ParserConfigurationException | SAXException | IOException e) {
            System.out.println("ERROR parsing XML file:" + e.getMessage());
            return null;
        }
    }
}
