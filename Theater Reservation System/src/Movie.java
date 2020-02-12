import java.util.*;
public class Movie {
	String name;
	//Dates, Times
	HashMap<String, HashSet<String>> showtimes;
	
	/**
	 * Constructo
	 * @param name - movie title
	 */
	public Movie(String name) {
		this.name = name;
		showtimes = new HashMap<String, HashSet<String>>();
	}
	
	/**
	 * 
	 * @return this movies name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * 
	 * @return a map of the showtimes
	 */
	public HashMap<String, HashSet<String>> getShowtimes(){
		return this.showtimes;
	}
	
	/**
	 * 
	 * @return all the dates that this movie is being shown on 
	 */
	public Set getDates() {
		return showtimes.keySet();
	}
	
	/**
	 * 
	 * @param date - user inputed
	 * @return all times this movie is being shown on the given date
	 */
	public Set getTimes(String date) {
		return showtimes.get(date);
	}
	
	/**
	 * adds a showtime to the movie's showtimes
	 * @param date - user inputed
	 * @param time - user inputed
	 */
	public void addTime(String date, String time) {
		showtimes.get(date).add(time);
	}
	
	/**
	 * adds a date to the movies showtime
	 * @param date - user inputed
	 */
	public void addDate(String date) {
		showtimes.put(date, new HashSet<String>());
	}
}
