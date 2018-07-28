package assignment4;

/**
 * Critter 3 is the Stick-Bug Class. Stick-Bugs are a small bug that disguise themselves as sticks to hide from predators.
 * They will constantly reproduce as they find strength in numbers and it takes them very little energy to reproduce. When
 * confronted with an opponent, they will almost always use their advantageous size and speed to run away unless it is a
 * fellow Stick-Bug infringing on their territory.
 * @see Critter
 */
public class Critter3 extends Critter {
    /**
     * doTimeStep for Critter3 Overrides the abstract doTimeStep of Critter, its parent. It decides whether or not
     * the Stick-Bug will reproduce based solely on a 1-in-2 chance. If the Stick-bug doesn't produce, it will try to walk
     * every turn.
     */
    @Override
    public void doTimeStep() {
        if(getRandomInt(1) == 1)
            walk(getRandomInt(7));
        Critter child = new Critter3();
        if(getEnergy() > 0){
            reproduce(child, getRandomInt(8));
        }
    }

    /**
     * fight for Critter4 Overrides the abstract fight of Critter, its parent. The Stick_Bug will always other Stick_Bugs
     * but won't fight any other animal
     * @param critter the toString value of the critter it will be fighting
     * @return the boolean value of whether it will fight or not
     */
    @Override
    public boolean fight(String critter) {
        if(critter.equals("3")) {
            return true;
        } else {
            walk(getRandomInt(7));
            return false;
        }
    }

    /**
     * toString allows the Critter to be represented on a map in character form
     * @return the String representing the Critter
     */
    public String toString() {
        return "3";
    }
}