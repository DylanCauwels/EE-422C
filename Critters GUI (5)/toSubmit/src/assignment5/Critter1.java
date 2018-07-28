package assignment5;

import javafx.scene.paint.Color;

/**
 * Critter 1 is the Rabbit Class. Rabbits are small, nimble animals that escape predators by running away and being very
 * hard to catch. They reproduce whenever possible and never fight.
 * @see Critter
 */
public class Critter1 extends Critter{
    /**
     * doTimeStep for Critter1 Overrides the abstract doTimeStep of Critter, its parent. It decides whether or not
     * the Rabbit will reproduce based on if it has enough energy to produce a child and survive the turn. If the Rabbit
     * doesn't produce, it will run in a random direction
     */
    @Override
    public void doTimeStep() {
        if(getEnergy() > Params.rest_energy_cost + Params.min_reproduce_energy)
            reproduce(new Critter1(), getRandomInt(7));
        else {
            run(getRandomInt(7));
        }
    }

    /**
     * fight for Critter1 Overrides the abstract fight of Critter, its parent. The Rabbit will never fight anything and
     * when confronted with another Critter will simply try to run away.
     * @param opponent the toString value of the critter it will be fighting
     * @return the boolean value of whether it will fight or not
     */
    @Override
    public boolean fight(String opponent) {
        int choice = getRandomInt(7);
        if(look(choice, true) == null) {
            run(choice);
            return false;
        } else {
            return true;
        }
    }

    /**
     * toString allows the Critter to be represented on a map in character form
     * @return the String representing the Critter
     */
    public String toString() {
        return "1";
    }

    /**
     * viewShape is a method for the critterGUI to display the critter as a specific shape of the CritterShape enumerated
     * type
     * @return the CritterShape to represent the Critter
     */
    @Override
    public CritterShape viewShape() { return CritterShape.CIRCLE;}

    /**
     * viewOutlinerColor is a method for the critterGUI to display the critter's shape as a specific color from the Java
     * FX.scene.paint color library
     * @return the Color to represent the Critter
     */
    @Override
    public javafx.scene.paint.Color viewOutlineColor() { return Color.ANTIQUEWHITE; }
}