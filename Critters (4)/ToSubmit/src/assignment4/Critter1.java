package assignment4;

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
        run(getRandomInt(7));
        return false;
    }

    /**
     * toString allows the Critter to be represented on a map in character form
     * @return the String representing the Critter
     */
    public String toString() {
        return "1";
    }
}