import java.util.HashMap;

public class Balcony implements Seating{
	//variable representing the seats in the section
	//seat number will be offset by -1 to adjust for 0 indexing
	HashMap<String, boolean[]> seats;
	
	/**
	 * Constructor
	 * initializes seats
	 */
	public Balcony() {
		seats = new HashMap<String, boolean[]>();
	}
	
	/**
	 * @param seatNum - seat number
	 * @return integer representing the price of the ticket
	 */
	@Override
	public int getTicketPrice(int seatNum) {
		return 40;
	}
	
	/**
	 * reserves the seat of the given seat number for the movie at the given date and time
	 * @param seatNum - seat number
	 * @param dandt - date and time
	 * @return true if seat was successfully reserved, false if otherwise
	 */
	@Override
	public boolean reserveSeat(int seatNum, String dandt) {
		checkDateAndTime(dandt);
		if(!isValidSeatNum(seatNum)) {
			System.out.println("Seat Not Available");
			return false;
		}
		if(seats.get(dandt)[seatNum - 1] == true) {
			System.out.println("That Seat Has Already been Taken");
			return false;
		}else {
			seats.get(dandt)[seatNum - 1] = true;
			System.out.println("Seat " + (seatNum) + " has been reserved");
			return true;
		}
	}

	/**
	 * @param num - seat number
	 * @return true is seat number is within defined constraints, false if otherwise 
	 */
	public boolean isValidSeatNum(int num) {
		return (num >= 1 && num <= 100);
	}
	
	/**
	 * @param dandt - date and time of showing
	 * @return all the seats of the given date and time
	 */
	public boolean[] getSeats(String dandt) {
		checkDateAndTime(dandt);
		return seats.get(dandt);
	}
	
	/**
	 * makes sure the movie is showing at the given date and time
	 * @param dandt - date and time
	 */
	public void checkDateAndTime(String dandt) {
		if(!seats.keySet().contains(dandt)) {
			seats.put(dandt, new boolean[100]);
		}
	}
	
	/**
	 * either reserves or free the seat depending on boolean input
	 * @param seatNum - seat number
	 * @param dandt - date and time
	 * @param b - true if trying to reserve, false if trying to free the seat
	 */
	public void setSeat(int seatNum, String dandt, boolean b) {
		checkDateAndTime(dandt);
		seats.get(dandt)[seatNum - 1] = b;
	}

}
