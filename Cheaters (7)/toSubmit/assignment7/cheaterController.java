package assignment7;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.io.PrintWriter;
import java.io.FileOutputStream;

public class cheaterController {

    /**
     * the main function in cheater controller is used to insert the filepaths into the cheater class along with the specified
     * parameters for the class. If we were to implement a GUI, we would expand this class to include a variety of commands to
     * insert filepaths, segment sizes, etc.
     * @param args if you know you know
     */
    public static void main(String[] args) {
        File folder = new File(args[0]);
        int segmentSize = Integer.parseInt(args[1]);
        int plagiagrismThreshold = Integer.parseInt(args[2]);

        File[] listOfFiles = folder.listFiles();

        assert listOfFiles != null;

        PrintWriter writer = null;
        try{
            writer = new PrintWriter(new FileOutputStream("output.txt", false));
        }catch(Exception e){
            //die
        }

        cheater cheat = new cheater();
        cheat.runStats(listOfFiles, segmentSize);
        HashMap<Integer, List<String>> map = cheat.getDatabase();
        SortedSet<Integer> keys = new TreeSet<>(map.keySet()).descendingSet();
        for (int key : keys) {
            if(key >= plagiagrismThreshold){
                writer.println(""+ key + ": "+ map.get(key));
            }else{
                break;
            }
        }
        writer.close();
    }
}