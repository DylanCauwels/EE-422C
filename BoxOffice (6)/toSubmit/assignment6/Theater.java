/* MULTITHREADING <Theater.java>
 * EE422C Project 6 submission
 * Dylan Cauwels
 * dmc3692
 * 15505
 * Slip days used: <0>
 * Spring 2018
 */
package assignment6;

import java.util.ArrayList;
import java.util.List;

public class Theater {
	private int numRows;
	private int seatsPerRow;
	private String show;
	private Seat[][] assignedSeats;
	private List<Ticket> log = new ArrayList<>();
	/**
	* Represents a seat in the theater
	* A1, A2, A3, ... B1, B2, B3 ...
	*/
	static class Seat {
		private int rowNum;
		private int seatNum;

		/**
		 * Seat constructor that initializes the row and column of the seat
		 * @param rowNum the Seat's row
		 * @param seatNum the Seat's column
		 */
		public Seat(int rowNum, int seatNum) {
			this.rowNum = rowNum;
			this.seatNum = seatNum;
		}

		/**
		 * getter method for a Seat's column
		 * @return the seat number
		 */
		public int getSeatNum() {
			return seatNum;
		}

		/**
		 * getter method for a Seat's row
		 * @return the row number
		 */
		public int getRowNum() {
			return rowNum;
		}

		/**
		 * toString method of the Seat
		 * @return the String representation of the Seat
		 */
		@Override
		public String toString() {
			return rowToString(this.rowNum) + (this.seatNum + 1);
		}

		/**
		 * rowToString converts the row number to A, B, ... AA, AB, ... etc format
		 * @param rowNum the integer representing the row number
		 * @return the String representation of the row
		 */
		String rowToString(int rowNum) {
			//TODO test this method more intensely
			StringBuilder row = new StringBuilder();
			while(rowNum > 25) {
				row.append(findLetter(rowNum % 26));
				rowNum /= 26;
				rowNum -= 1;
			}
			row.append(findLetter(rowNum));
			row.reverse();
			return row.toString();
		}

		char findLetter(int value) {
			return (char)(value + 65);
		}
	}

	/**
	* Represents a ticket purchased by a client
	*/
	static class Ticket {
		private String show;
		private String boxOfficeId;
		private Seat seat;
	  	private int client;

	  	final private int SHOW_DASHES = 20;
	  	final private int BOXOFFICE_DASHES = 11;
	  	final private int SEAT_DASHES = 20;
	  	final private int CLIENT_DASHES = 18;

		/**
		 * advanced Ticket constructor that initializes all of the object's local variables. If you don't use this constructor
		 * the variables wont be initialized and the toString method will output empty
		 * @param show the name of the movie
		 * @param boxOfficeId the name of the box office that assigned the ticket
		 * @param seat the Seat assigned to the ticket
		 * @param client the client number who bought the ticket
		 * @see Seat
		 */
		public Ticket(String show, String boxOfficeId, Seat seat, int client) {
			this.show = show;
			this.boxOfficeId = boxOfficeId;
			this.seat = seat;
			this.client = client;
		}

		public Seat getSeat() {
			return seat;
		}

		public String getShow() {
			return show;
		}

		public String getBoxOfficeId() {
			return boxOfficeId;
		}

		public int getClient() {
			return client;
		}

		/**
		 * the Ticket toString method generates a ticket box with the show, box office id, seat, and client number all
		 * displayed. It auto-formats the spacing so you wont have to do it manually. However exceptionally long titles
		 * will cause the output to be wrongly formatted.
		 * @return the String representation of the Ticket
		 */
		@Override
		public String toString() {
			return ("\n------------------------------" +
					"\n|  Show: " + this.show + calcSpacing(SHOW_DASHES, show.length()) + "|" +
					"\n|  Box Office ID: " + this.boxOfficeId +  calcSpacing(BOXOFFICE_DASHES, boxOfficeId.length()) + "|" +
					"\n|  Seat: " + this.seat.toString() + calcSpacing(SEAT_DASHES, this.seat.toString().length()) + "|" +
					"\n|  Client: " + this.client + calcSpacing(CLIENT_DASHES, Integer.toString(client).length()) + "|" +
					"\n------------------------------");
		}

		/**
		 *
		 * @param defaultValue the constant number of dashes between the ": " and the end of the ticket box
		 * @param offsetValue the character size of the input to be displayed
		 * @return a String containing the proper spacing
		 */
		private String calcSpacing(int defaultValue, int offsetValue) {
			StringBuilder spacing  = new StringBuilder();
			for(int i = 0; i < (defaultValue - offsetValue); i++) {
				spacing.append(" ");
			}
			return spacing.toString();
		}
	}

	/**
	 *
	 * @param numRows the number of rows in the theater
	 * @param seatsPerRow the number of seats per row in the theater
	 * @param show the title of the movie being shown
	 */
	public Theater(int numRows, int seatsPerRow, String show) {
		 this.numRows = numRows;
		 this.seatsPerRow = seatsPerRow;
		 this.show = show;
		 this.assignedSeats = new Seat[numRows][seatsPerRow];
	}

	/** Calculates the best seat not yet reserved
	*
	* @return the best seat or null if theater is full
	*/
	public synchronized Seat bestAvailableSeat() {
		for(int i = 0; i < numRows; i++) {
			for(int j = 0; j < seatsPerRow; j++) {
				if(assignedSeats[i][j] == null) {
					Seat found = new Seat(i, j);
					assignedSeats[i][j] = found;
					return found;
				}
			}
		}
		return null;
	}

	/** Prints a ticket for the client after they reserve a seat
	* Also prints the ticket to the console
	* @param seat a particular seat in the theater
	* @return a ticket or null if a box office failed to reserve the seat
	*/
	public Ticket printTicket(String boxOfficeId, Seat seat, int client) {
		final Ticket given = new Ticket(show, boxOfficeId, seat, client);
		System.out.println(given.toString());
		System.out.flush();
		log.add(given);
		return given;
	}


	/**
	 * Lists all tickets sold for this theater in order of purchase
	 *
	 * @return list of tickets sold
	 */
	public List<Ticket> getTransactionLog() {
		return log;
	}
}