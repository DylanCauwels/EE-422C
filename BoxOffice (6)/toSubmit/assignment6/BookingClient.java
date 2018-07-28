/* MULTITHREADING <BookingClient.java>
 * EE422C Project 6 submission
 * Dylan Cauwels
 * dmc3692
 * 15505
 * Slip days used: <0>
 * Spring 2018
 */
package assignment6;

import java.util.*;
import java.lang.Thread;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class BookingClient extends Thread{
    private List<Thread> threads = new ArrayList<>();
    private static Map<String, Integer> office;
    private Theater theater;
    private List<BoxOffice> offices = new ArrayList<>();
    private List<Theater.Ticket> customers = Collections.synchronizedList(new ArrayList<Theater.Ticket>());
    private int customerCount = 0;
    private boolean soldOut = false;
    private ReentrantLock reentrantLock = new ReentrantLock();

    public static void main(String[] args) {
        office = new HashMap<>();
        office.put("BX1", 2500);
        office.put("BX3", 3);
        office.put("BX2", 4);
        office.put("BX5", 3);
        office.put("BX4", 3);
        Theater theater = new Theater(1000, 3, "Ouija");
        BookingClient a = new BookingClient(office, theater);
        a.simulate();
    }

  /**
   * @param office maps box office id to number of customers in line
   * @param theater the theater where the show is playing
   */
  public BookingClient(Map<String, Integer> office, Theater theater) {
    BookingClient.office = office;
    this.theater = theater;
  }

  /**
   * Starts the box office simulation by creating (and starting) threads
   * for each box office to sell tickets for the given theater
   *
   * @return list of threads used in the simulation,
   *         should have as many threads as there are box offices
   */
	public List<Thread> simulate() {
        Set<Map.Entry<String, Integer>> list = office.entrySet();
        for(Map.Entry<String, Integer> a : list ) {
            BoxOffice nowOpen = new BoxOffice(a.getKey());
            offices.add(nowOpen);
        }
        for(BoxOffice office: offices) {
	        Thread newOffice = new Thread(office);
	        newOffice.start();
	        threads.add(newOffice);
        }
        for(Thread thread: threads) {
	        try {
	            thread.join();
            } catch (InterruptedException a) {
	            a.printStackTrace();
            }
        }
        return threads;
	}

    /**
     * the class used to implement multithreading
     * @see Thread
     */
    class BoxOffice implements Runnable {
        String name;

        /**
         * constructor that initializes the name of the box office for use later
         * @param name the name of the box office
         */
        BoxOffice(String name) {
            this.name = name;
        }

        /**
         * the BoxOffice run method implements a few different levels. The first level checks to see if there are customers
         * remaining in line and if the theater is sold out to prevent the sold out message from being printed multiple times.
         * The second level is for waiting to generate and print a ticket. Since the lab requires that we print the seats in order, a
         * reentrant lock must be used to prevent multiple threads from accessing the bestAvailableSeat and printTicket methods
         * at the same time. Once the thread has successfully acquired and printed a ticket, the thread will sleep to allow
         * other offices access to the system and allow the user to see the newly printed ticket.
         * @see ReentrantLock
         */
        @Override
        public void run() {
            Theater.Seat given;
            while (office.get(name) > 0 && !soldOut) {
                boolean lock = false;
                boolean ticketCycle = false;
                while(!ticketCycle) {
                    try {
                        lock = reentrantLock.tryLock(1, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (lock) {
                        given = theater.bestAvailableSeat();
                        if (given == null && !soldOut) {
                            soldOut = true;
                            try {
                                TimeUnit.SECONDS.sleep(1);
                            } catch (InterruptedException a) {
                                a.printStackTrace();
                            }
                            System.out.println("\nSorry, we are sold out!");
                            break;
                        } else if(given == null) {
                            break;
                        }
                        Theater.Ticket sale = theater.printTicket(name, given, ++customerCount);
                        office.replace(name, office.get(name), office.get(name) - 1);
                        customers.add(sale);
                        ticketCycle = true;
                        reentrantLock.unlock();
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}