package assignment4;

/**
 * Critter 4 is the Snake Class. Snakes reproduce once every year or so in the wild, so this is simulated by the snake
 * reproducing once in every 9 turns. If they don't reproduce, they slither quickly onward in search of additional food.
 * When offered a fight, a snake never backs down no matter what. They are very vulnerable from behind so they refuse to
 * turn around and run away from any challenger.
 * @see Critter
 */
public class Critter4 extends Critter {

	/**
	 * doTimeStep for Critter4 Overrides the abstract doTimeStep of Critter, its parent. It decides whether or not
	 * the snake will reproduce based solely on a 1-in-10 chance. If the snake doesn't produce, it will try to run
	 * every turn.
	 */
	@Override
	public void doTimeStep() {
		if(getRandomInt(9) == 9 && getEnergy() > 150) {
			reproduce(new Critter4(), getRandomInt(7));
		} else {
			run(getRandomInt(7));
		}
	}

	/**
	 * fight for Critter4 Overrides the abstract fight of Critter, its parent. The snake will always fight no matter the
	 * situation
	 * @param critter the toString value of the critter it will be fighting
	 * @return the boolean value of whether it will fight or not
	 */
	@Override
	public boolean fight(String critter) {
		return true;
	}

	/**
	 * toString allows the Critter to be represented on a map in character form
	 * @return the String representing the Critter
	 */
	public String toString() {
		return "4";
	}
}