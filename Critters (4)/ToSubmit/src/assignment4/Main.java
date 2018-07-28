package assignment4;
/* CRITTERS Main.java
 * EE422C Project 4 submission by
 * Replace <...> with your actual data.
 * Dylan Cauwels
 * dmc3692
 * 15505
 * Srinjoy Majumdar
 * sm64469
 * 15505
 * Slip days used: <0>
 * Fall 2016
 */

import java.util.Queue;
import java.util.Scanner;
import java.io.*;

import java.lang.reflect.Method;
import java.util.List;

/*
 * Usage: java <pkgname>.Main <input file> test
 * input file is optional.  If input file is specified, the word 'test' is optional.
 * May not use 'test' argument without specifying input file.
 */
public class Main {

    static Scanner kb;	// scanner connected to keyboard input, or input file
    private static String inputFile;	// input file, used instead of keyboard input if specified
    static ByteArrayOutputStream testOutputString;	// if test specified, holds all console output
    private static String myPackage;	// package of Critter file.  Critter cannot be in default pkg.
    private static boolean DEBUG = false; // Use it or not, as you wish!
    static PrintStream old = System.out;	// if you want to restore output to console


    // Gets the package name.  The usage assumes that Critter and its subclasses are all in the same package.
    static {
        myPackage = Critter.class.getPackage().toString().split(" ")[1];
    }

    /* Do not alter the code above for your submission. */

    /**
     * Process show command
     * @param ls scanner
     * @param line input line
     */
    private static void processShow(Scanner ls, String line){
        if (!ls.hasNext()) {
            Critter.displayWorld();
        } else {
            System.out.println("error processing: " + line);
        }
    }

    /**
     * Process step command
     * @param ls scanner
     * @param line input line
     */
    private static void processStep(Scanner ls, String line){
        int steps;
        if (ls.hasNextInt()) {
            steps = ls.nextInt();
        } else {
            steps = 1;
        }
        if (!ls.hasNext()) {
            for (int i = 0; i < steps; i++) {
                Critter.worldTimeStep();
            }
        } else {
            System.out.println("error processing: " + line );
        }
    }

    /**
     * Process seed command
     * @param ls scanner
     * @param line input line
     */
    private static void processSeed(Scanner ls, String line){
        int seedNum = ls.nextInt();
        if (!ls.hasNext()) {
            Critter.setSeed(seedNum);
        } else {
            System.out.println("error processing: " + line );
        }
    }

    /**
     * Process make command, throws error if class not found
     * @param ls scanner
     * @param line input line
     */
    private static void processMake(Scanner ls, String line){
        String critter_name = ls.next();
        int count = 1;
        if (ls.hasNextInt()) {
            count = ls.nextInt();
        }
        if (!ls.hasNext()) {
            for (int i = 0; i < count; i++) {
                try {
                    Critter.makeCritter(critter_name);
                } catch (InvalidCritterException a) {
                    System.out.println("error processing: " + line);
                    return;
                }
            }
        } else {
            System.out.println("error processing: " + line);
        }
    }

    /**
     * Process stats command, creates class and calls instance method using reflection, throws error
     * if class not found
     * @param ls scanner
     * @param line input line
     */
    private static void processStats(Scanner ls, String line){
        String critter_name = ls.next();
        if (!ls.hasNext()) {
            try {
                Class classTemp = Class.forName(myPackage + "." + critter_name);
                Method m = classTemp.getMethod("runStats", List.class); //Get method
                List<Critter> instances = Critter.getInstances(critter_name);
                m.invoke(null, instances); //Invoke with instances
            } catch (Exception e) {
                System.out.println("error processing: " + line);
            }
        } else {
            System.out.println("error processing: " + line);
        }
    }

    /**
     * Main method.
     * @param args args can be empty.  If not empty, provide two parameters -- the first is a file name, 
     * and the second is test (for test output, where all output to be directed to a String), or nothing.
     */
    public static void main(String[] args) {
        if (args.length != 0) {
            try {
                inputFile = args[0];
                kb = new Scanner(new File(inputFile));
            } catch (FileNotFoundException e) {
                System.out.println("USAGE: java Main OR java Main <input file> <test output>");
                e.printStackTrace();
            } catch (NullPointerException e) {
                System.out.println("USAGE: java Main OR java Main <input file>  <test output>");
            }
            if (args.length >= 2) {
                if (args[1].equals("test")) { // if the word "test" is the second argument to java
                    // Create a stream to hold the output
                    testOutputString = new ByteArrayOutputStream();
                    PrintStream ps = new PrintStream(testOutputString);
                    // Save the old System.out.
                    old = System.out;
                    // Tell Java to use the special stream; all console output will be redirected here from now
                    System.setOut(ps);
                }
            }
        } else { // if no arguments to main
            kb = new Scanner(System.in); // use keyboard and console
        }

        while (true) {
            String line = kb.nextLine(); //Gets whole line
            Scanner ls = new Scanner(line.trim()); //Creates individual scanner on each line for parsing
            String input;

            if(ls.hasNext()){
                input = ls.next();
            }else{ //Empty command
                System.out.println("invalid command");
                continue;
            }
            if (input.equals("quit")) {
                if (!ls.hasNext()) { //Check not invalid
                    break;
                } else {
                    System.out.println("error processing: " + line);
                }
            } else if (input.equals("show")) {
                processShow(ls, line);
            } else if (input.equals("step")) {
                processStep(ls, line);
            } else if (input.equals("seed")) {
                processSeed(ls, line);
            } else if (input.equals("make")) {
                processMake(ls, line);
            } else if (input.equals("stats")) {
                processStats(ls, line);
            } else {
                System.out.println("invalid command: " + line); //Returns invalid
            }
        }
    }
}
