import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
public class Theater {
	//these are the seating sections that the theater has
	MainFloor mf;
	SouthBalcony sBalcony;
	Balcony eBalcony;
	Balcony wBalcony;
	
	//Predefined dates that are discount nights. dnights is only used to pass the information to discountNights
	LocalDate[] dnights = {LocalDate.parse("2020-12-26"),LocalDate.parse("2020-12-27")};
	HashSet<LocalDate> discountNights = new HashSet<LocalDate>(Arrays.asList(dnights));
	
	//movies contains a string with the movie title, mapped to a movie object with show times.
	HashMap<String,Movie> movies = new HashMap<String,Movie>();
	
	//File containing the previous reservation information. Initialized in constructor
	File reservations;
	
	/**
	 * Constructor
	 * initializes seating sections and movies that the theater is showing
	 * creates a new reservation information file if one does not already exist.
	 * @throws IOException catches file errors
	 */
	public Theater() throws IOException {
		reservations = new File("reservations.txt");
		reservations.createNewFile(); // if file already exists will do nothing 
		mf = new MainFloor();
		sBalcony = new SouthBalcony();
		eBalcony = new Balcony();
		wBalcony = new Balcony();
		initializeMovies();
	}	
	
	/**
	 * Method for setting the movies and dates and times in the respective field.
	 */
	private void initializeMovies() {
		Movie m = new Movie("Miracle on 34th Street");
		String[] datesShowing = {"2020-12-23", "2020-12-24", "2020-12-25", "2020-12-26", "2020-12-27", "2020-12-28", "2020-12-29", 
				"2020-12-30","2020-12-31", "2021-01-01", "2021-01-02"};
		for(String date:datesShowing) {
			m.addDate(date);
			m.addTime(date, "06:30");
			m.addTime(date, "08:30");
		}
		movies.put("Miracle on 34th Street".toLowerCase(),m);
	}
	
	/**
	 * Writes the reservation information to the reservation information file
	 * Adds the reservation to the list of the user's reservations and current reservations
	 * @param u - user making the reservation
	 * @param r - reservation to be added
	 * @throws IOException catches file errors
	 */
	public void addReservation(User u, Reservation r) throws IOException {
		FileWriter out = new FileWriter("reservations.txt", true);
		out.write(u.getUsername() + " " + formatTitle(r.getMovie().trim()) + " " + r.getDate() + " " + r.getTime() + " " + r.getSeatNum() +"\n");
		u.addReservation(r);
		u.addToCurrent(r);
		out.close();
	}
	
	/**
	 * @return Movies this theater is showing
	 */
	public HashMap<String, Movie> getMovies(){
		return this.movies;
	}
	
	/**
	 * 
	 * @return Titles of the movies this theater is showing
	 */
	public Set<String> getMovieTitles(){
		return this.movies.keySet();
	}
	
	/**
	 * reserves the seat of the user's reservation and adds the reservation to the user's list
	 * @param u - user making reservation
	 * @param r - reservation being added
	 */
	public void makeReservation(User u, Reservation r){
		String seat = r.getSeatNum();
		seat = seat.toLowerCase();
		int seatNum = -1;
		Seating section = determineSection(seat);
		if(section == null) {
			System.out.println("Incorrect Format");
			return;
		}
		if(section.equals(mf)) {
			seatNum = Integer.parseInt(seat.substring(1));
		}else if(section !=null){
			seatNum = Integer.parseInt(seat.substring(2));
		}
		if(section.reserveSeat(seatNum,r.getDAndT())) {
			try {
				r.setPrice(section.getTicketPrice(seatNum));
				addReservation(u,r);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * method to check if the given movie is being shown on the given date
	 * @param title - movie title
	 * @param date - user inputed date
	 * @return true if movie is showing on date
	 */
	public boolean isValidDate(String title, String date) {
		if(!getMovieDates(title).contains(date)) {
			return false;
		}
		return true;
	}
	
	/**
	 * method to check if given movie is being shown on the given date and time
	 * @param title - movie title
	 * @param date - user inputed date
	 * @param time - user inputed time
	 * @return true if movie is being shown on given date and time
	 */
	public boolean isValidTime(String title, String date, String time) {
		if(!getMovieTimes(title,date).contains(time)) {
			return false;
		}
		return true;
	}
	
	/**
	 * counts number of available seats in the given section for the given time
	 * @param s - Seating section
	 * @param dandt = date and time string
	 * @return number of free seats in the given section
	 */
	private int countAvailableSeats(Seating s, String dandt) {
		int count = 0;
		for(boolean b : s.getSeats(dandt)) {
			if(!b) {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * prints each section and the number of available seats and calls method to print
	 * all seats
	 * @param dandt = date and time
	 */
	public void printAvailableSeats(String dandt) {
		System.out.println("Main Floor (m) : " + countAvailableSeats(mf, dandt) + " available seats");
		printSeats(mf, dandt);
		System.out.println("South Balcony (sb): " + countAvailableSeats(sBalcony, dandt) + " available seats");
		printSeats(sBalcony, dandt);
		System.out.println("East Balcony (eb) : " + countAvailableSeats(eBalcony, dandt) + " available seats");
		printSeats(eBalcony, dandt);
		System.out.println("West Balcony (wb) : " + countAvailableSeats(wBalcony, dandt) + " available seats");
		printSeats(wBalcony, dandt);
	}
	/**
	 * gets all the dates that the given movie is being shown on
	 * @param movie - movie title
	 * @return the dates that this movie is being shown
	 */
	public Set getMovieDates(String movie) {
		return movies.get(movie).getDates();
	}
	
	/**
	 * all the times that this movie is being shown on the given date
	 * @param movie - title of movie
	 * @param date - user inputed date
	 * @return times that this movie is being shown on the given date
	 */
	public Set getMovieTimes(String movie, String date) {
		return movies.get(movie).getTimes(date);
	}
	
	/**
	 * prints all seats for the given section, date, and time to visualize the section
	 * @param section - seating section
	 * @param dandt = date and time
	 */
	public void printSeats(Seating section, String dandt) {
		if(section == null) {
			System.out.println("Invalid Input");
			return;
		}
		boolean[] seats = section.getSeats(dandt);
		int count = 0;
		for(int i = 0; i < seats.length / 10; i++) {
			for(int j = 0; j < 10; j++) {
				if(!seats[count]) {
					System.out.printf("%3s ",count + 1 );
				}else {
					System.out.print("    ");
				}
				count++;
			}
			System.out.println();
		}
		System.out.println();
	}
	
	/**
	 * method gets the seating section of the given seat number
	 * @param s - seat number
	 * @return the seating section that the seat number corresponds to
	 */
	public Seating determineSection(String s) {
		boolean isnumeric = true;
		if(s.startsWith("m")) {
			try {
				Integer.parseInt(s.substring(1));
			}catch (NumberFormatException nfe){
				return null;
			}
			return mf;
		}else {
			try {
				Integer.parseInt(s.substring(2));
			}catch (NumberFormatException nfe){
				return null;
			}
			if(s.startsWith("sb")) {
				return sBalcony;
			}else if(s.startsWith("eb")) {
				return eBalcony;
			}else if(s.startsWith("wb")) {
				return wBalcony;
			}
		}
		return null;
	}
	
	/**
	 * transform movie title for easier parsing
	 * @param title to format
	 * @return title for parse purposes
	 */
	public String formatTitle(String title) {
		return title.replaceAll(" ", "-");
	}
	
	/**
	 * Reads reservations from text file and loads them into the corresponding user
	 * @param users - a map of usernames to their user objects
	 * @throws IOException catches file errors
	 */
	public void loadReservations(HashMap<String,User> users) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(reservations));  
		String line = null;
		while((line = br.readLine()) != null) {
			String[] elements = line.split(" ");
			String username = elements[0];
			User u = users.get(username);
			String movie = elements[1].replaceAll("-", " ");
			String date = elements[2];
			String time = elements[3];
			String seat = elements[4];
			Reservation r = new Reservation(movie, date+","+ time, seat);
			u.addReservation(r);
			
			Seating section = determineSection(r.getSeatNum().toLowerCase());
			int seatNum;
			if(section.equals(mf)) {
				seatNum = Integer.parseInt(seat.substring(1));
				section.setSeat(seatNum, r.getDAndT(), true);
			}else if(section !=null){
				seatNum = Integer.parseInt(seat.substring(2));
				section.setSeat(seatNum,r.getDAndT(), true);
			}
		}
		br.close();
	}
	
	/**
	 * prints all reservations made for a given movie, date, and time
	 * meant to display seats to be cancelled to the user
	 * @param u - user
	 * @param title - movie title
	 * @param dateString - contains date
	 * @param timeString - contains time
	 */
	public void printReservations(User u, String title, String dateString, String timeString) {
		LocalDate date = LocalDate.parse(dateString);
		LocalTime time = LocalTime.parse(timeString, DateTimeFormatter.ofPattern("HH:mm"));
		for(Reservation r: sortReservations(u.getReservations())) {
			if(r.getMovie().trim().toLowerCase().equals(title) && r.getDate().equals(date) && r.getTime().equals(time)) {
				System.out.println(r.getMovie() + ", Date :" +  r.getDate() +", Time: " + r.getTime() + " Seat Number : " + r.getSeatNum());
			}
		}
	}

	/**
	 * removes a reservation of the given arguments from the user's reservation info
	 * resets the reserved seat to unreserved
	 * @param u - user
	 * @param title - movie title
	 * @param date - user inputed date
	 * @param time -user inputed time
	 * @param seatNums - all seat numbers to be cancelled
	 */
	public void cancelReservation(User u, String title, String date, String time, String[] seatNums)  {
		for(String seat :seatNums) {
			Seating section = determineSection(seat);
			int seatNum;
			if(section.equals(mf)) {
				seatNum = Integer.parseInt(seat.substring(1));
				section.setSeat(seatNum, date+"," +time, false);
			}else if(section !=null){
				seatNum = Integer.parseInt(seat.substring(2));
				section.setSeat(seatNum, date+"," + time, false);
			}
			Reservation removeTarget = null;
			for(Reservation r: u.getReservations()) {
				if(r.getMovie().toLowerCase().trim().equals(title) && r.getDAndT().equals(date +"," +time) && r.getSeatNum().equals(seat)) {
					removeTarget = r;
				}
			}
			u.getReservations().remove(removeTarget);
			try {
				deleteResFromFile(u.getUsername() + " " + formatTitle(title.trim()) + " " + date + " " + time + " " + seat);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(seat + " has been cancelled!");
		}
	}
	
	/**
	 * method for checking if it is discount night
	 * @param date - date to check
	 * @return true if it is discount night, false otherwise
	 */
	public boolean isDiscountNight(LocalDate date) {
		return(this.discountNights.contains(date));
	}
	
	/**
	 * sorts reservations by their dates and times in ascending order
	 * @param list - set of unsorted reservations
	 * @return array list of reservations sorted by Date and time in ascending order
	 */
	public ArrayList<Reservation> sortReservations(HashSet<Reservation> list) {
		ArrayList<Reservation> sorted = new ArrayList<Reservation>();
		
		if(list.size() == 0) {
			return sorted;
		}
		for(Reservation r: list) {
			if(sorted.size() == 0) {
				sorted.add(r);
			}else {
				int count = 0;
				while(count != sorted.size() &&!(r.getDAndT().compareTo(sorted.get(count).getDAndT()) <= 0)) {
					count++;
				}
				sorted.add(count, r);
			}
		}
		return sorted;
	}
	
	/**
	 * deletes a reservation line from the reservations information file.
	 * @param s - line to delete from file
	 * @throws IOException catches file errors
	 */
	public void deleteResFromFile(String s) throws IOException {
		File initial = reservations;
		File newFile = new File("myTempFile.txt");

		BufferedReader reader = new BufferedReader(new FileReader(initial));
		BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));

		String currentLine;

		while((currentLine = reader.readLine()) != null) {
		    String line = currentLine.trim().toLowerCase();
		    if(line.equals(s.toLowerCase())) {
		    	continue;
		    }
		    writer.write(currentLine + "\n");
		}
		writer.close(); 
		reader.close(); 
		newFile.renameTo(initial);
	}
}
