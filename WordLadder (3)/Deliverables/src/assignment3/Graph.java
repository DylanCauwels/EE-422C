/* WORD LADDER Graph.java, Vertex.java
 * EE422C Project 3 submission by
 * Dylan Cauwels
 * dmc3692
 * U#: 15505
 * Slip days used: <0>
 * Spring 2018
 */
package assignment3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    private Map<String, Vertex> vertices = new HashMap<>();

    Graph(){}

    /**
     * addVertex adds a valid Vertex object into the vertices HashMap iff there is not one with the same key already listed
     * @param a the Vertex to be added
     * @see Vertex
     */
    public void addVertex(Vertex a) {
         if(!vertices.containsValue(a))
             vertices.put(a.getName(), a);
         else
             vertices.replace(a.getName(), a);
    }

    /**
     * contains tests to see if the HashMap contains a Vertex with a particular String
     * @param a the String key
     * @return true/false
     * @see Vertex
     */
    public boolean contains(Vertex a) {
        return vertices.containsKey(a.getName());
    }

    /**
     * giveVertex returns the Vertex with the given key
     * @param key the key to be looked for
     * @return Vertex
     * @see Vertex
     */
    public Vertex giveVertex(String key) {
        return vertices.get(key);
    }
}

class Vertex {
    private String name;
    private List<Vertex> adjacent = new ArrayList<>();
    private Vertex parent;
    private int similarCharacters;

    /**
     * Default constructor. Sets the name of the Vertex to n
     * @param n the name of the constructed Vertex
     */
    Vertex(String n) {
        this.name = n;
    }

    /**
     * Constructor that sets the parent to be the passed in Vertex and the name to be the passed in String
     * @param n the name of the constructed Vertex
     * @param origin the parent of the constructed Vertex
     */
    Vertex(String n, Vertex origin) {
        this.name = n;
        this.parent = origin;
    }

    /**
     * getName returns the private name of the Vertex
     * @return String
     */
    public String getName() {
        return this.name;
    }

    /**
     * getParent returns the private name of the Parent
     * @return Vertex
     */
    public Vertex getParent() {
        return this.parent;
    }

    /**
     * addAdjacent adds a Vertex to the adjacent list of the current Vertex iff that Vertex isn't already within adjacent
     * @param a the Vertex to be added to adjacent
     */
    public void addAdjacent(Vertex a) {
        if(!this.adjacent.contains(a))
            this.adjacent.add(a);
    }

    /**
     * adjacentLength returns the length of the adjacent List of the Vertex
     * @return int
     */
    public int adjacentLength() {
        if(adjacent != null)
            return this.adjacent.size();
        else
            return 0;
    }

    /**
     * getAdjacent returns the Vertex contained in the desired index of the adjacent list
     * @param index index of desired adjacent
     * @return Vertex
     */
    public Vertex getAdjacent(int index) {
        return adjacent.get(index);
    }

    /**
     * sortAdjacent sorts the Vertex's adjacent List in reference to how many letters each Vertex has in common with the
     * end word
     * @param end the word to end the ladder
     */
    public void sortAdjacent (String end) {
        for(int i = 0; i < adjacentLength(); i++) {
            String current = adjacent.get(i).getName();
            adjacent.get(i).similarCharacters = 0;
            for(int j = 0; j < current.length(); j++) {
                if(current.charAt(j) == end.charAt(j))
                    adjacent.get(i).similarCharacters++;
            }
        }
        for(int k = 0; k < adjacentLength(); k++) {
            for(int m = 0; m < adjacentLength() - 1; m++) {
                if(adjacent.get(m).similarCharacters < adjacent.get(m + 1).similarCharacters) {
                    Vertex temp = adjacent.get(m + 1);
                    adjacent.remove(m + 1);
                    adjacent.add(m + 1, adjacent.get(m));
                    adjacent.remove(m);
                    adjacent.add(m, temp);
                }
            }
        }
    }
}
