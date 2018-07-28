package assignment4;

/**
 * Critter2 is the Elephant Class. Elephants are a large generally peaceful type of animal that finds strength in its
 * size and its numbers. They reproduce rarely and only fight when provoked. They almost never fight other Elephants.
 * @see Critter
 */
public class Critter2 extends Critter{
    /**
     * doTimeStep for Critter2 Overrides the abstract doTimeStep of Critter, its parent. It decides whether or not
     * the Elephant will reproduce based on if it has greater than 200 energy. If the Elephant doesn't produce, it will
     * walk in a random direction
     */
    @Override
    public void doTimeStep() {
        if(getEnergy() > 200)
            reproduce(new Critter2(), getRandomInt(7));
        else {
            walk(getRandomInt(7));
        }
    }

    /**
     * fight for Critter2 Overrides the abstract fight of Critter, its parent. The Elephant will only fight if it has a
     * sizeable amount of energy for an animal of it's size and if it's not fighting another Elephant. If it doesn't
     * choose to fight it will walk away slowly.
     * @param opponent the toString value of the critter it will be fighting
     * @return the boolean value of whether it will fight or not
     */
    @Override
    public boolean fight(String opponent) {
        if(getEnergy() < 50 && !opponent.equals("2"))
            return true;
        else {
            walk(getRandomInt(7));
            return false;
        }
    }

    /**
     * toString allows the Critter to be represented on a map in character form
     * @return the String representing the Critter
     */
    public String toString() {
        return "2";
    }
}