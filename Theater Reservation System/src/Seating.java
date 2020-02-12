
public interface Seating {
	
	/**
	 * @param seatNum - seat number
	 * @return integer representing the price of the ticket
	 */
	int getTicketPrice(int seatNum);
		
	/**
	 * @param num - seat number
	 * @return true is seat number is within defined constraints, false if otherwise 
	 */
	boolean isValidSeatNum(int num);
	
	/**
	 * reserves the seat of the given seat number for the movie at the given date and time
	 * @param seatNum - seat number
	 * @param dandt - date and time
	 * @return true if seat was successfully reserved, false if otherwise
	 */
	boolean reserveSeat(int seatNum, String dandt);
	
	/**
	 * makes sure the movie is showing at the given date and time
	 * @param dandt - date and time
	 * @return all the seats in the section
	 */
	boolean[] getSeats(String dandt);
	
	/**
	 * either reserves or free the seat depending on boolean input
	 * @param seatNum - seat number
	 * @param dandt - date and time
	 * @param b - true if trying to reserve, false if trying to free the seat
	 */
	void setSeat(int seatNum, String dandt, boolean b);
	
}
