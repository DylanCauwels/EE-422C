package assignment7;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class cheater {
    private HashMap<Integer, List<String>> hits;
    static private HashMap<String, HashSet<String>> fileWords;

    /**
     * default constructor
     */
    public cheater() {}

    /**
     * runStats takes an array of Files along with the specified number of similarities to be considered note-worthy and
     * compares each file in the list with one another and puts the ones flagged into the hits hashmap
     * @param docs the array of Files to be checked for plagiarism
     * @param segmentSize the amount of similarities that will be considered plagiarism
     */
    public void runStats(File[] docs, int segmentSize) {
        hits = new HashMap<>();
        fileWords = new HashMap<>();
        for(int i = 0; i < docs.length; i++) {
            for(int j = i + 1; j < docs.length; j++) {
                FileComparator comparator = new FileComparator(segmentSize, docs[i], docs[j]);
                int conflicts = comparator.compare();
                if (hits.containsKey(conflicts)){
                    hits.get(conflicts).add(""+i+" "+j+" ");
                }else{
                    ArrayList list = new ArrayList();
                    list.add(""+docs[i].getName()+","+docs[j].getName()+" ");
                    hits.put(conflicts, list);
                }
            }
        }
    }

    /**
     *
     * @return the 2D matrix of registered 6-word similarities
     */
    public HashMap<Integer, List<String>> getDatabase() {
        return hits;
    }


    class FileComparator {
        HashSet<String> fileA_words;
        HashSet<String> fileB_words;
        int segmentSize;

        /**
         * FileComparator takes in two files, constructs hashmaps and checks each others hashmaps for similarities. If the amount
         * of similarities found is above the given segment size 'N', then the compare function when called will return the
         * number of collisions found
         * @param N the segment size to be checked against
         * @param fileA the first file
         * @param fileB the second file
         */
        FileComparator(int N, File fileA, File fileB){
            segmentSize = N;

            if(fileWords.containsKey(fileA.getName())){
                fileA_words = fileWords.get(fileA.getName());
            }else{
                //Construct hash set for this file
                fileA_words = parseFile(fileA);
                fileWords.put(fileA.getName(), fileA_words);
            }
            if(fileWords.containsKey(fileB.getName())){
                fileB_words = fileWords.get(fileB.getName());
            }else{
                //Construct hash set for this file
                fileB_words = parseFile(fileB);
                fileWords.put(fileB.getName(), fileB_words);
            }
        }

        /**
         * takes two files and runs them. The first run of parseFile puts the keys and values into the similarities
         * HashMap, and the second returns the collisions that occurred in the hashmap when the second file was put in
         * @return amount of collisions in the second run with the first run
         */
        public int compare() {
            int conflicts = 0;
            if(fileA_words.size()<fileB_words.size()){
                for(String s: fileA_words){
                    if(fileB_words.contains(s)){
                        conflicts++;
                    }
                }
            }else{
                for(String s: fileB_words){
                    if(fileA_words.contains(s)){
                        conflicts++;
                    }
                }
            }
            return conflicts;
        }

        /**
         * parseFile uses a string builder to construct all 6-word segments of a given file and puts them into the collisions
         * HashMap. If there are any collisions it represents them in the returned conflicts variable. If only one file is run
         * through the method then conflicts will always return 0.
         * @param file the file to be parsed and inserted into the hashmap
         * @return the number of conflicts in the hashmap
         */
        private HashSet<String> parseFile(File file) {
            try {
                HashSet<String> set = new HashSet<>();
                Scanner scanner = new Scanner(new BufferedReader(new FileReader(file)));
                ArrayList<String> segment = new ArrayList<>();

                while(scanner.hasNext()) {
                    segment.add(scanner.next());
                    if(segment.size()>=segmentSize) {
                        if(segment.size() > segmentSize){
                            segment.remove(0);
                        }
                        String parsed = String.join("", segment).toLowerCase().replaceAll("\\W", "");
                        set.add(parsed);
                    }
                }
                return set;
            } catch (NullPointerException | IOException a) {
                a.printStackTrace();
            }
            return null;
        }
    }
}
