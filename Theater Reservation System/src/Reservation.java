import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.*;
public class Reservation {
	//Title of movie
	private String movie;
	
	//dandt = date and time
	private String dandt;
	
	private LocalDate date;
	private LocalTime time;
	private String seatNum;
	private int price;
	
	/**
	 * Constructor 
	 * @param movie - movie title
	 * @param dandt - date and time
	 * @param seatNum -seat number
	 */
	public Reservation(String movie, String dandt, String seatNum) {
		this.movie = capitalizeWords(movie);
		this.dandt = dandt;
		String[] times = dandt.split(",");
		this.date = LocalDate.parse(times[0]);
		this.time = LocalTime.parse(times[1], DateTimeFormatter.ofPattern("HH:mm"));
		this.seatNum = seatNum;
	}
	
	/**
	 * @return the movie this reservation is for
	 */
	public String getMovie() {
		return this.movie;
	}
	
	/**
	 * @return Date of reservation
	 */
	public LocalDate getDate() {
		return this.date;
	}
	/**
	 * @return time of reservation
	 */
	public LocalTime getTime() {
		return this.time;
	}
	
	/**
	 * sets the movie title
	 * @param movie - movie title
	 */
	public void setMovie(String movie) {
		this.movie = movie;
	}
	
	/**
	 * changes the time of the reservation
	 * @param time - user inputed
	 */
	public void setTime(LocalTime time) {
		this.time = time;
	}
	
	/**
	 * changes the date of the reservation
	 * @param date - user inputed
	 */
	public void setDate(LocalDate date) {
		this.date = date;
	}
	
	/**
	 * @return seat number of the reservation
	 */
	public String getSeatNum() {
		return this.seatNum;
	}
	
	/**
	 * @return a combined string of date and time for easier identification purposes
	 */
	public String getDAndT() {
		return this.dandt;
	}
	
	/**
	 * @return ticket price of the reservation
	 */
	public int getPrice() {
		return this.price;
	}
	
	/**
	 * changes the price of the reservation
	 * @param p price
	 */
	public void setPrice(int p) {
		this.price = p;
	}
	
	/**
	 * Capitalizes beginning of each word for viewing purposes
	 * @param title
	 * @return a correctly formatted movie title
	 */
	private String capitalizeWords(String title) {
		String[] words = title.split(" ");
		String result = "";
		for(String word: words) {
			if(Character.isLetter(word.charAt(0))) {
				result += word.substring(0, 1).toUpperCase() + word.substring(1) + " ";
			}else {
				result += word + " ";
			}
		}
		return result;
	}
}
