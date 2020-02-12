import java.util.*;
import java.io.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
/**
 * 
 * @author marcoshung
 *@version 1.00
 */
//ok to have an empty txt file
//use a seperate file to store user info and reservations
public class ReservationSystem {
	//Scanner to read user input
	static Scanner console = new Scanner(System.in);
	
	//holds user information
	static HashMap<String, User> userList = new HashMap<String, User>();
	
	//theater that will be used during the user session
	static Theater t;
	
	//text file containing user information
	static File previousUsers = new File("users.txt");
	
	/**
	 * Main method
	 * initializes a new theater to use
	 * calls methods to load users and reservations from text files
	 * @param args main
	 * @throws IOException catches file errors
	 */
	public static void main(String[] args) throws IOException {
		
		t = new Theater();

		previousUsers.createNewFile(); // if file already exists will do nothing 
		loadUsers();
		t.loadReservations(userList);
		start();
	}
	
	/**
	 * Method for launching home page and getting user input.
	 */
	public static void start() {
		System.out.println("Enter I to Sign In or U to Sign up or X to Exit system");
		String input = console.next().toLowerCase();
		if(input.equals("i")) {
			login();
		}else if(input.equals("u")) {
			signUp();
		}else if(input.equals("x")) {
			System.out.println("Thank you for visiting!");
		}else {
			System.out.println("Invalid Input");
		}
	}
	
	/**
	 * Gets user input for username and password and checks validity
	 */
	public static void login() {
		System.out.println("Enter your username");
		String id = console.next();
		while(!userList.containsKey(id)) {
			System.out.println("Username not found");
			id = console.next();
		}
		System.out.println("Enter your password");
		String pswrd = console.next();
		while(!userList.get(id).getPassword().equals(pswrd)) {
			System.out.println("Incorrect Password");
			pswrd = console.next();
		}
		User user = userList.get(id);
		beginTransaction(user);
	}
	
	/**
	 * Gets user input to register username and password into the user credential registry.
	 */
	public static void signUp() {
		System.out.println("Choose a username");
		
		String name = console.next();
		while(userList.containsKey(name)) {
			System.out.println("Username already taken. Please choose another one.");
			name = console.next();
		}
		System.out.println("Choose a password. Must be at least 6 characters");
		String pswrd = console.next();
		while(pswrd.length() < 6) {
			System.out.println("Password too short. Please choose another one");
			pswrd = console.next();
		}
		try {
			addUser(name,pswrd);
		} catch (IOException e) {
			e.printStackTrace();
		}
		userList.put(name, new User(name,pswrd));
		System.out.println("You are all signed up!");
		login();
	}

	/**
	 * Takes user input to make the command they desire
	 * @param user to track and assign their reservations
	 */
	public static void beginTransaction(User user) {
		HashSet<String> commands = new HashSet<String>(Arrays.asList("r","v", "c", "o"));
		System.out.println("Enter Command: \n\n" + 
				"[R]eserve    [V]iew    [C]ancel    [O]ut");
		
		String command = console.next().toLowerCase();
		while(!commands.contains(command)) {
			System.out.println("Not valid input. Try again");
			command = console.next().toLowerCase();
		}

		console.nextLine(); //adjusts for the newline character
		String input = "";
		if(command.equals("r")) {
			while(!input.equals("n")){
				reserve(user);
				System.out.println("Would you like to reserve another seat?\n[Y]es or [N]o");
				input = console.nextLine().toLowerCase();
				while(!input.equals("y") && !input.equals("n")) {
					System.out.println("Not a valid command");
					input = console.nextLine();
				}
			}
		}else if(command.equals("v")) {
			view(user);
		}else if(command.equals("c")) {
			cancel(user);
		}else if(command.equals("o")) {
			System.out.println(user.getCurrentReservations().size());
			out(user);
			return;// ends method
		}
		beginTransaction(user);
	}
	
	/**
	 * Prints each individual seat reservation price and total price.
	 * Ends by closing the reservation page and returning to home page
	 * @param u user to access reservation information
	 */
	private static void out(User u) {
		int total = 0;
		int discount = 0;
		HashSet<Reservation> currentReservations = u.getCurrentReservations();
		if(currentReservations.size() >= 5 && currentReservations.size() <= 10) {
			discount = 2;
		}else if(currentReservations.size() >=11) {
			discount = 5;
		}
		for(Reservation r: currentReservations) {
			if(t.isDiscountNight(r.getDate())) {
				r.setPrice(20);
				System.out.println(r.getMovie() + ", Date :" +  r.getDate() +", Time: " + r.getTime() + " Seat Number : " + r.getSeatNum() + ", Price : $" + r.getPrice());

			}else {
				r.setPrice((r.getPrice() - discount));
			}
			System.out.println(r.getMovie() + ", Date :" +  r.getDate() +", Time: " + r.getTime() + " Seat Number : " + r.getSeatNum() + ", Price : $" + r.getPrice());
			total+=r.getPrice();
		}
		System.out.println("The total is $" + total);
		u.clearCurrent();
		start();
	}

	/**
	 * Cancels a reservation from the user's previously made reservations list
	 * @param u user
	 */
	private static void cancel(User u) {
		System.out.println("Enter movie name you wish to cancel");
		String title = console.nextLine();
		System.out.println("Enter Date and time you wish to cancel\nEnter in YYYY-MM-DD HH-MM Format");
		String[] elements = console.nextLine().split(" ");
		if(elements.length != 2) {
			System.out.println("Invalid format. Try again");
			elements = console.nextLine().split(" ");
		}
		String date = elements[0];
		String time = elements[1];
		t.printReservations(u, title, date, time);
		System.out.println("Enter all the seats you would like to cancel");
		String[] seatNums = console.nextLine().split(" ");
		t.cancelReservation(u, title, date, time, seatNums);
	}
	
	/**
	 * takes user input to decide whether to print all reservations or only from a 
	 * certain date
	 * @param u user
	 */
	private static void view(User u) {
		System.out.println("Enter 'A' to view all reservations or enter a date in YYYY-MM-DD format to view reservation for a given day");
		String input = console.nextLine().toLowerCase();
		if(input.equals("a")) {
			viewAll(u);
		}else {
			viewDate(u,input);
		}
	}
	
	/**
	 * prints all available reservations the user has, in ascending order.
	 * @param u user
	 */
	private static void viewAll(User u) {
		// TODO Auto-generated method stub
		HashSet<Reservation> reservations = u.getReservations();
		if(reservations == null) {
			System.out.println("No Reservations");
			return;
		}
		for(Reservation r: t.sortReservations(reservations)) {
			System.out.println(r.getMovie() + ", Date :" +  r.getDate() +", Time: " + r.getTime() + "PM Seat Number : " + r.getSeatNum());
		}
	}
	
	/**
	 * prints all available reservations the user has made on a given date, in ascending order
	 * @param u user
	 * @param input - date as a string
	 */
	private static void viewDate(User u, String input) {
		if(!isValidDate(input)) {
			System.out.println("Not a valid date");
			return;
		}
		
		HashSet<Reservation> reservations = u.getReservations(input);
		if(reservations == null) {
			System.out.println("No reservations on that day");
		}else {
			for(Reservation r : t.sortReservations(reservations)) {
				System.out.println(r.getMovie() + ", Date :" +  r.getDate() +", Time: " + r.getTime() + "PM Seat Number : " + r.getSeatNum());
			}
		}
		
	}

	/**
	 * takes in user input to reserve the seat at the given movie, date, and time
	 * @param u user
	 */
	public static void reserve(User u) {
		//Get the movie the user wants to reserve.
		System.out.println("Movies:\nMiracle on 34th Street");
		System.out.println("Enter the movie you want to see");
		String movie = console.nextLine().toLowerCase();
	
		while(!t.getMovieTitles().contains(movie)) {//if the movie entered is invalid
			System.out.println("That movie isn't showing in this theater. Enter another movie");
			movie = console.nextLine().toLowerCase();
		}
		
		//Gets date the the user wants to reserve
		System.out.println(movie + ":\n"); 						//prints movie name with
		System.out.println(t.getMovieDates(movie).toString());	//dates its showing
				System.out.println("Enter the date you want to see " + movie +"\nEnter in YYYY-MM-DD Format");
		String date = console.nextLine();
		while(!t.isValidDate(movie, date)) {//if date is invalid
			System.out.println("That movie isn't being shown on that day. Enter another date");
			date = console.nextLine();
		}
		
		//Gets the time the user wants to reserve
		System.out.println(movie + ":\n"); 								//prints movie name with
		System.out.println(t.getMovieTimes(movie, date).toString());	//all times its showing on the specified date
		System.out.println("Enter what time you would like to see" + movie +"\n(Please Enter in \"HH:MM\" form)");
		String time = console.nextLine();
		while(!t.isValidTime(movie, date, time)) {	//if time is invalid
			System.out.println("That movie isn't being shown at that time. Enter another time");
			time = console.nextLine();
		}
		String dandt = date+ "," +time;
		//Gets the section that the user wants to sit in
		System.out.println("Where would you like to sit?\n");
		t.printAvailableSeats(dandt);
		
		/*This code is for user wants to choose section first then choose a seat
		 * String section = console.nextLine().toLowerCase();
		String[] sections = {"m","sb","eb","wb"};
		HashSet<String> validSections = new HashSet<String>(Arrays.asList(sections));
		while(!validSections.contains(section)) {
			System.out.println("That section does not exist. Please enter another one");
			section = console.nextLine().toLowerCase();
		}
		Seating seating = t.determineSection(section);
		//Gets the seat number for the specified section.
		System.out.println("Which seat would you like?\n");
		int seatNum = console.nextInt();
		while(!seating.isValidSeatNum(seatNum)) {
			System.out.println("Not a valid seat. Please enter another");
			seatNum = console.nextInt();
		}
		*/
		String[] seats = console.nextLine().split(" ");
		for(String seat: seats) {
			Reservation r = new Reservation(movie.trim(),dandt, seat);
			t.makeReservation(u, r);
		}
;
	}
	
	/**
	 * loads a list of users from text file and stores in field for username and
	 * password validation
	 * @throws IOException catches file errors
	 */
	public static void loadUsers() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(previousUsers));  
		String line = null;
		while((line = br.readLine()) != null) {
			String[] userInfo = line.split(" ");
			userList.put(userInfo[0], new User(userInfo[0],userInfo[1]));
		}
		br.close();
	}
	
	/**
	 * Writes user information to the user information text file
	 * @param username username for user
	 * @param pswrd password for user
	 * @throws IOException catches File errors
	 */
	//writes user information to the user file
	public static void addUser(String username, String pswrd) throws IOException {
		FileWriter out = new FileWriter("users.txt", true);
		out.write(username + " " + pswrd +"\n"); 
		out.close();
	}
	
	/**
	 * Method to check if the date entered by the user is in the correct format.
	 * @param date to be tested
	 * @return True if the date is in correct date format. False if otherwise.
	 */
	public static boolean isValidDate(String date) {
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	        dateFormat.setLenient(false);
	        try {
	            dateFormat.parse(date.trim());
	        } catch (ParseException pe) {
	            return false;
	        }
	        return true;
	    }
	
}