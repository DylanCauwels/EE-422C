/* WORD LADDER Main.java
 * EE422C Project 3 submission by
 * Dylan Cauwels
 * dmc3692
 * U#: 15505
 * Slip days used: <0>
 * Spring 2018
 */

package assignment3;

import java.util.*;
import java.io.*;

public class Main {
    private static int overflowPrevention;


    public static void main(String[] args) throws Exception {
        Scanner kb;
        PrintStream ps;
        if (args.length != 0) {
            kb = new Scanner(new File(args[0]));
            ps = new PrintStream(new File(args[1]));
            System.setOut(ps);
        } else {
            kb = new Scanner(System.in);
            ps = System.out;
        }
        initialize();
        ArrayList<String> inputs = parse(kb);
        //printLadder(getWordLadderBFS(inputs.get(0), inputs.get(1)));
        printLadder(getWordLadderDFS(inputs.get(0), inputs.get(1)));

    }

    /**
     * Who uses an initialize function for recursion and searching tho
     */
    public static void initialize() {
    }

    /**
     * @param keyboard Scanner connected to System.in
     * @return ArrayList of Strings containing start word and end word.
     * If command is /quit, return empty ArrayList.
     */
    public static ArrayList<String> parse(Scanner keyboard) {
        ArrayList<String> inputs = new ArrayList<>();
        inputs.add(keyboard.next());
        inputs.add(keyboard.next());
        return inputs;
    }

    /**
     * getWordLadderDFS is a function that searches a wordLadder Graph for a matching ladder in a depth-first fashion. It
     * also uses recursion as opposed to the iterative function of getWordLadderBFS. It is slower and less accurate than BFS
     * and will often return ladders exponentially larger than BFS.
     * @param start word starting the wordLadder
     * @param end word ending the wordLadder
     * @return ArrayList of Strings that contains the lowercase wordLadder in order from start(0) to end(finalLadder.size()--)
     */
    public static ArrayList<String> getWordLadderDFS(String start, String end) {
        if (start.equals("/quit") || end.equals("/quit"))
            return null;
        Graph wordLadder = new Graph();
        wordLadder = makeWordLadder(wordLadder, start, end);
        Vertex current = wordLadder.giveVertex(start.toUpperCase());
        ArrayList<String> finalLadder;
        end = end.toUpperCase();
        HashMap<String, Vertex> visited = new HashMap<>();
        overflowPrevention = 0;
        ArrayList<String> soItWorks = new ArrayList<>();
        finalLadder = DFSRecursion(visited, current, soItWorks, end);
        if(finalLadder == null) {
            finalLadder = new ArrayList<>();
            finalLadder.add(0,end);
            finalLadder.add(0, start);
        }
        for (int i = 0; i < finalLadder.size(); i++) {
            String temp = finalLadder.get(i).toLowerCase();
            finalLadder.remove(i);
            finalLadder.add(i, temp);
        }
        return finalLadder;
    }

    /**
     * DFS Recursion is the recursive helper function for DFS
     * @param visited pre-initialized HashMap that allows the function to remember the nodes it has visited and increase it's efficiency
     * @param top the current node being evaluated for a match and its adjacent Vertex's
     * @param ladder the ArrayList of Strings that will contain the lowercase ladder when the function exits
     * @param end the String that is to end the wordLadder
     * @return ladder (see above)
     * @see Graph wordLadder
     * @see Vertex top, visited
     */
    private static ArrayList<String> DFSRecursion(HashMap<String, Vertex> visited, Vertex top, ArrayList<String> ladder, String end) {
        if(++overflowPrevention > 3000)
            return null;
        if (top.getName().equals(end)) {
            ladder.add(0, top.getName());
            return ladder;
        }
        top.sortAdjacent(end);
        for (int i = 0; i < top.adjacentLength(); i++) {
            if (!visited.containsKey(top.getAdjacent(i).getName())) {
                visited.put(top.getAdjacent(i).getName(), top.getAdjacent(i));
                ladder = DFSRecursion(visited, top.getAdjacent(i), ladder, end);
                if (ladder != null && ladder.size() > 0) {
                    ladder.add(0, top.getName());
                    return ladder;
                }
            }
        }
        return ladder;
    }

    /**
     * getWordLadderBFS is a function that finds a word ladder from the wordLadder graph. It searches by layers and therefore
     * will find the most efficient/smallest word ladder solution.
     * @param start the string starting the word ladder
     * @param end the string ending the word ladder
     * @return an ArrayList of Strings containing the in-order lowercase word ladder from start(0) to end(finalLadder.size())
     */
    public static ArrayList<String> getWordLadderBFS(String start, String end) {
        if (start.equals("/quit") || end.equals("/quit"))
            return null;
        ArrayList finalLadder = new ArrayList<String>();
        Graph wordLadder = new Graph();
        wordLadder = makeWordLadder(wordLadder, start, end);
        LinkedList<Vertex> queue = new LinkedList<>();
        end = end.toUpperCase();
        HashMap<String, Vertex> visited = new HashMap<>();
        queue.add(wordLadder.giveVertex(start.toUpperCase()));
        while (!queue.isEmpty()) {
            Vertex current = queue.remove();
            visited.put(current.getName(), current);
            if (current.getName().equals(end)) {
                while (current != null) {
                    finalLadder.add(0, current.getName().toLowerCase());
                    current = current.getParent();
                }
                break;
            }
            for (int i = 0; i < current.adjacentLength(); i++) {
                if (!visited.containsKey(current.getAdjacent(i).getName())) {
                    queue.add(current.getAdjacent(i));
                }
            }
        }
        if(finalLadder.size() == 0) {
            finalLadder.add(0, end.toLowerCase());
            finalLadder.add(0, start.toLowerCase());
        }
        return finalLadder;
    }

    /**
     * printLadder prints a valid ladder presented in String ArrayList form. It will not print an error message for an empty
     * ladder ArrayList. It will print an error message if the ArrayList only has two entries that are not separated by a
     * single letter difference. If it is a valid 2-word ladder the it will also print the ladder- even though the instructions
     * specified this was not necessary.
     * @param ladder the valid word ladder to be printed
     */
    public static void printLadder(ArrayList<String> ladder) {
        if(ladder == null) {
            return;
        }
        if (!(ladder == null) && ladder.size() > 2) {
            System.out.println("a " + (ladder.size() - 2) + "-rung word ladder exists between " + ladder.get(0) + " and " + ladder.get(ladder.size() - 1) + ".");
            for (String aLadder : ladder) {
                System.out.println(aLadder);
            }
        } else if(ladder.size() > 1 && withinOneLetter(ladder.get(0), ladder.get(1))) {
            System.out.println("a 0-rung word ladder exists between " + ladder.get(0) + " and " + ladder.get(1) + ".");
        } else if(ladder.size() > 1) {
            System.out.println("no word ladder can be found between " + ladder.get(0) + " and " + ladder.get(1));
        }
    }


    public static Set<String> makeDictionary() {
        Set<String> words = new HashSet<String>();
        Scanner infile = null;
        try {
            infile = new Scanner(new File("five_letter_words.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("Dictionary File not Found!");
            e.printStackTrace();
            System.exit(1);
        }
        while (infile.hasNext()) {
            words.add(infile.next().toUpperCase());
        }
        return words;
    }

    /**
     * withinOneLetter tests to see if two strings are within one letter of one another. This does not count spaces.
     * @param a the first String to be tested
     * @param b the second String to be tested
     * @return true/false
     */
    private static boolean withinOneLetter(String a, String b) {
        int notSame = 0;
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) != b.charAt(i))
                notSame++;
        }
        return notSame == 1;
    }

    /**
     * makeWordLadder makes the word ladder. That's it.
     * @param wordLadder the pre-allocated graph that will be returned as a full wordLadder
     * @param start the word String to start the ladder
     * @param end the word String to end the ladder
     * @return the Graph passed into the first parameter of the function
     */
    private static Graph makeWordLadder(Graph wordLadder, String start, String end) {
        Vertex top = new Vertex(start);
        Set<String> dictionary = makeDictionary();
        if (!dictionary.contains(start.toUpperCase()) || !dictionary.contains(end.toUpperCase())) {
            return null;
        }
        LinkedList<Vertex> queue = new LinkedList<>();
        dictionary.remove(top.getName().toUpperCase());
        queue.add(new Vertex(start.toUpperCase()));
        while (!queue.isEmpty()) {
            Vertex current = queue.remove();
            if (wordLadder.contains(current)) {
                current = wordLadder.giveVertex(current.getName());
            }
            String currentWord = current.getName();
            currentWord = currentWord.toUpperCase();
            if (!currentWord.equals(end.toUpperCase())) {
                char[] currentChar = currentWord.toCharArray();
                for (int i = 0; i < currentChar.length; i++) {
                    for (int j = 'A'; j <= 'Z'; j++) {
                        char store = currentChar[i];
                        currentChar[i] = (char) j;
                        String newWord = new String(currentChar);
                        if (dictionary.contains(newWord) && !newWord.equals(current.getName())) {

                            Vertex word = new Vertex(newWord, current);
                            if (wordLadder.contains(word)) {
                                wordLadder.giveVertex(newWord).addAdjacent(current);
                                current.addAdjacent(wordLadder.giveVertex(newWord));
                            } else {
                                queue.add(word);
                                current.addAdjacent(word);
                                wordLadder.addVertex(word);
                            }
                        }
                        currentChar[i] = store;
                    }
                }
                wordLadder.addVertex(current);
            }
        }
        return wordLadder;
    }
}