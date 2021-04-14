package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Utils {

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
}
