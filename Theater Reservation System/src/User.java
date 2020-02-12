import java.util.*;


public class User {
	private String username;
	private String pw;
	private HashSet<Reservation> reservations;
	private HashSet<Reservation> currentReservations = new HashSet<Reservation>();
	
	/**
	 * Constructor
	 * @param username - username
	 * @param pw - password (must be at least 6 characters long)
	 */
	public User(String username, String pw) {
		this.username = username;
		this.pw = pw;
		reservations = new HashSet<Reservation>();
	}
	
	/**
	 * @return this user's username
	 */
	public String getUsername() {
		return this.username;
	}
	
	/**
	 * @return this user's password.
	 */
	public String getPassword() {
		return this.pw;
	}
	
	/**
	 * @return all of this user's reservations
	 */
	public HashSet<Reservation> getReservations() {
		return this.reservations;
	}
	/**
	 * 
	 * @param date in form of a string
	 * @return all the user's reservations on a given date
	 */
	public HashSet<Reservation> getReservations(String date){
		HashSet<Reservation> results = new HashSet<Reservation>();
		for(Reservation r: reservations) {
			if(r.getDate().toString().equals(date)) {
				results.add(r);
			}
		}
		return results;
	}
	
	/**
	 * adds a reservation to the user's list of all their reservations
	 * @param r - Reservation
	 */
	public void addReservation(Reservation r) {
		this.reservations.add(r);
	}
	
	/**
	 * adds reservation to user's currents reservation list.
	 * @param r - reservation
	 */
	public void addToCurrent(Reservation r) {
		currentReservations.add(r);
	}
	/**
	 * 
	 * @return the current reservations that have been made during the user's session
	 */
	public HashSet<Reservation> getCurrentReservations(){
		return this.currentReservations;
	}
	
	/**
	 * resets the current reservation list
	 */
	public void clearCurrent() {
		this.currentReservations = new HashSet<Reservation>();
	}
}	
