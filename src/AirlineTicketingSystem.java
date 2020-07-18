

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
//import com.mysql.jdbc.Connection;
import java.sql.*;
import java.util.Scanner;

public class AirlineTicketingSystem {
	
	static int member;

	public static void main(String[] args) throws Exception{
		
		
		Connection conn = airlineDBconnection.getConnection();
		Scanner input = new Scanner(System.in);
		int IsAdmin=-1;
		
		System.out.print("Welcome to the airline booking system, Are you registered with us ? Y/N" );
		
		String membership = input.next();
		if(membership.equalsIgnoreCase("Y")){
			System.out.print("Please enter username: ");
			String username = input.next();
			System.out.print("Please enter password: ");
			String password = input.next();
		
			
			
			
			//member login process
			
			
			//ResultSet memberRs = getMemberIdByUsernameAndPassword(conn, username, password);
			boolean result = getMemberIdByUsernameAndPasswordHash(conn, username, password);
			//System.out.println("result"+result+" memberid:"+member);
			
			//found member in Db
			if(result){
				//member = memberRs.getInt("memberID");
				
				//show booking info
				List<Integer> FlightIdList = getFlightIdsByMemberId(member, conn);
				printFlightInfoByFlightId(conn, FlightIdList);
				//add or delete flight goes here
				//add flight need user input destination and departure...get a flight
				//user input Y or N to book
				//Y ---> save to booking table ()
				//start the loop for main menu
				
				String quit= "i";
				while(quit!="n"){
					  IsAdmin=IsMemberAdmin(conn,member);
				      if (IsAdmin==1)	{
					
											System.out.print("How can we help you?\n"+" Main menu \n" + 
											"1:check all flights information \n"+
											"2:booking flight \n"+
											"3:check booked flight informtation \n"+
											"4:add flight (Admin only) \n"+ 
											"5:delete flight (Admin only)\n"+
											"6:check all member information (Admin only) \n"+
											"7:cancel booked flight\n"+
											"8:add a new flight Admin  (Admin only) \n"+
											"9:log out \n"+"Enter a choice: ");
											
				                        }
				      else              {
				    	  
				    	                    System.out.print("How can we help you?\n"+" Main menu \n" + 
				    	                    "1:check all flights information \n"+
									        "2:booking flight \n"+
									        "3:check booked flight informtation \n"+
									        "7:cancel booked flight\n"+
									        "9:log out \n"+"Enter a choice: ");
				    	  
				                        }
				
				int selection = input.nextInt();
				input.nextLine();
				switch(selection){
				case 1:
					System.out.println("All flight informtation are here: ");
					printAllFlightInfo(conn);
					
					break;
					
					
				case 2:
					//input.nextLine();
					System.out.println("Enter the departure city: ");
					String departureCity = input.nextLine();
					input.nextLine();
					System.out.println("Enter the destination city: ");
					String arrivalCity = input.nextLine();
					System.out.println(departureCity+" "+arrivalCity);
					System.out.println("Here is the matching flight information: ");
					
					//call method show matching flight information
					printFlightByDepartureAndArrivalCity(conn, departureCity, arrivalCity);
					
					System.out.println("Enter a flight number to book: ");
					int flightId = input.nextInt();	
					String seat_capacity="",booked_seats="";					
					PreparedStatement myStat = conn.prepareStatement(Queries.GET_FLIGHT_BY_ID);
					myStat.setInt(1, flightId);
					ResultSet resultSet = myStat.executeQuery();
					while(resultSet.next()){
						seat_capacity= resultSet.getString("seatCapacity");
						System.out.println("*seatCapacity"+ seat_capacity);
					}
					
					myStat = conn.prepareStatement(Queries.GET_BOOKED_SEATS);
					myStat.setInt(1, flightId);
					resultSet = myStat.executeQuery();
					while(resultSet.next()){
						booked_seats= resultSet.getString("Count");
						System.out.println("*booked_seats"+ booked_seats);
					}
					
					if (Integer.valueOf(seat_capacity)==Integer.valueOf(booked_seats))
					{
						System.out.println("*"+"We are sorry this flight is already booked - Please try again in some time or check all other flights !"+"*");
					}
					
					else
					{
					//put into the order table in database
					bookFlightIDbyMemberID(conn, member,flightId);
					System.out.println("Your flight has booked successfully.");
					}
					
					break;
				case 3:
					System.out.println("Your booked flight information: ");
					//getMemberIdByUsernameAndPassword(conn, username, password);
					List<Integer> FlightIdList1 = getFlightIdsByMemberId(member, conn);
					printFlightInfoByFlightId(conn, FlightIdList1);
					
					break;
					
				case 4:
					System.out.print("Please enter the flight information for adding: \n");
					//create a flight
					createFlight(input, conn,IsAdmin,member);
					break;
				case 5:
					
					
					//call deleteFlight method
					deleteFlight(input, conn);
					
					//flight deleted
					break;
					
				case 6:					
					System.out.print("All member's information: \n");
					//call get all member information method
					printAllMemberInfo(conn);
					
					break;
					
				case 7:
					System.out.println("Your booked flight informtation is here: ");
					//show booking info
					FlightIdList = getFlightIdsByMemberId(member, conn);
					printFlightInfoByFlightId(conn, FlightIdList);
					System.out.println("Please choose the flightid of the flight you wish to cancel :");
					flightId = input.nextInt();	
					System.out.println(flightId);
					Cancelmemberflight(conn,flightId,member);
					break;
					
				case 8:
					System.out.println("****welcome to adding a new flight Admin**** \n");
					//member=1011;
					member=createUser(input, conn);
					updateAdminInfo(conn,member);
					System.out.println("All flight informtation are here: ");
					printAllFlightInfo(conn);
					System.out.println("Please enter the flight ID the new Admin wishes to be an Admin of: \n");
					flightId = input.nextInt();
					PreparedStatement updAFPS = conn.prepareStatement(Queries.UPDATE_ADMIN_FLIGHT);
					updAFPS.setInt(1, flightId);
					updAFPS.executeUpdate();
					System.out.println("The FlightID has been updated with the new Admin");
					
					break;
				case 9:
					System.out.println("Logged out - Thank You for using Airline ticketing system \n");
				    System.exit(0);
				   
					break;
					
				default:
						System.out.println("Invalid input.Enter an option from 1 to 9: ");
					
				}
				System.out.print("Ready to go back to the main menu? press any key");
				quit = input.next().toLowerCase();

			
			}
			}
		

			
			else {System.out.print("invalid entry !");
			
			System.out.println("Invalid credentials, let's check if we can find you !");
			System.out.print("Please enter username: ");
			 username = input.next();
			PreparedStatement getMemberByUsernamePS = conn.prepareStatement(Queries.GET_MEMBER_BY_USERNAME);
			getMemberByUsernamePS.setString(1, username);
			ResultSet memberRsByUsername = getMemberByUsernamePS.executeQuery();
			if(memberRsByUsername.next()){
				//pop security question
				String sq = memberRsByUsername.getString("securityQuestion");
				System.out.println(sq);
				input.nextLine();
				String sqAnswer = input.nextLine();
				int memberId = -1;
				if(memberRsByUsername.getString("securityAnswer").equalsIgnoreCase(sqAnswer)){
					System.out.println("login success");
					memberId = memberRsByUsername.getInt("memberID");
					//show booking info:
					//1 find all flight id in booking table by member id
					member=memberId;
					List<Integer> FlightIdList = getFlightIdsByMemberId(memberId, conn);
					//2 find all flight in flight table by flight id from step 1
					printFlightInfoByFlightId(conn, FlightIdList);
					//main menu loop goes here
					//start the loop for main menu
					String quit= "i";
					while(quit!="n"){
						IsAdmin=IsMemberAdmin(conn,member);
					    if (IsAdmin==1)	{
						
												System.out.print("How can we help you?\n"+" Main menu \n" + 
												"1:check all flights information \n"+
												"2:booking flight \n"+
												"3:check booked flight informtation \n"+
												"4:add flight (Admin only) \n"+ 
												"5:delete flight (Admin only) \n"+
												"6:check all member information (Admin only) \n"+ 
												"7:cancel booked flight\n"+
												"8:add a new flight Admin  (Admin only) \n"+
												"9:log out \n"+"Enter a choice: ");
												
					                        }
					    else              {
					    	  
					    	                    System.out.print("How can we help you?\n"+" Main menu \n" + 
					    	                    "1:check all flights information \n"+
										        "2:booking flight \n"+
										        "3:check booked flight informtation \n"+
										        "9:log out \n"+"Enter a choice: ");
					    	  
					                        }
					
					int selection = input.nextInt();
					switch(selection){
					case 1:
						System.out.println("All flight informtation are here: ");
						printAllFlightInfo(conn);
						
						break;
					case 2:
						System.out.println("Enter the departure ciry: ");
						String departureCity = input.next();
						System.out.println("Enter the destination city: ");
						String arrivalCity = input.next();
						System.out.println("Here is the matching flight information: ");
						
						//call method show matching flight information
						printFlightByDepartureAndArrivalCity(conn, departureCity, arrivalCity);
						
						System.out.println("Enter a flight number to book: ");
						int flightId = input.nextInt();					
						//put into the order table in database
						
						System.out.println("Your flight has booked successfully.");
						
						break;
					case 3:
						System.out.println("Your booked flight information: ");
						//TO DO Pranjal
						//getMemberIdByUsernameAndPassword(conn, username, password);
						printFlightInfoByFlightId(conn, FlightIdList);
						
						break;
					
					case 4:
						System.out.print("Please enter the flight information for adding: \n");
						//create a flight TO DO Pranjal
						createFlight(input, conn,IsAdmin,member);
						break;
					case 5:
						
						
						//call deleteFlight method TO DO Pranjal
						deleteFlight(input, conn);
						
						//flight deleted
						break;
						
					case 6:					
						System.out.print("All member's information: \n");
						//call get all member information method TO DO Pranjal
						printAllMemberInfo(conn);
						
						break;
						
					case 7:
						System.out.println("Your booked flight informtation is here: ");
						//show booking info
						FlightIdList = getFlightIdsByMemberId(member, conn);
						printFlightInfoByFlightId(conn, FlightIdList);
						System.out.println("Please choose the flightid of the flight you wish to cancel :");
						flightId = input.nextInt();	
						System.out.println(flightId);
						Cancelmemberflight(conn,flightId,member);
						break;
						
					case 8:
						System.out.println("****welcome to adding a new flight Admin**** \n");
						//member=1011;
						member=createUser(input, conn);
						updateAdminInfo(conn,member);
						System.out.println("All flight informtation are here: ");
						printAllFlightInfo(conn);
						System.out.println("Please enter the flight ID the new Admin wishes to be an Admin of: \n");
						flightId = input.nextInt();
						PreparedStatement updAFPS = conn.prepareStatement(Queries.UPDATE_ADMIN_FLIGHT);
						updAFPS.setInt(1, flightId);
						updAFPS.executeUpdate();
						System.out.println("The FlightID has been updated with the new Admin");
						
						break;
						
					case 9:
						System.out.println("Logged out - Thank You for using Airline ticketing system \n");
					    System.exit(0);
					   
						break;
						
					default:
							System.out.println("Invalid input.Enter an option from 1 to 9: ");
						
					}
					System.out.print("Ready to go back to the main menu? press any key");
					quit = input.next().toLowerCase();

					
					}		
				}else{
					//wrong security answer
					System.out.println("login failed please register");
					memberId = createUser(input, conn);
					member=memberId;
				}
			}else{
				
				//if wrong username and wrong password situation goes here
				//create user
				System.out.println("Username and password are not found. Please register first.");
				member=createUser(input, conn);
				
				
			
			}
			
			}
			
		
				
				
				
				
			}
		
		
		else{
				//member not found
			System.out.println("Entered user registration");
			member=createUser(input, conn);
				
			}
//TO DO PRANJAL
	
				
		}
			
		



/*			
else (membership.equalsIgnoreCase("N")){
	
}*/
			

	/*
	private static void Addasflightadmin(Connection conn, int flightId, int member2) throws SQLException  {
		PreparedStatement AddasflightadminPS = conn.prepareStatement(Queries.GET_MEMBERINFO_BY_MEMBERID);
		AddasflightadminPS.setInt(1, member2);
		ResultSet resultSet = AddasflightadminPS.executeQuery();
		int AdminId=0;
		String flightCode="";
		while(resultSet.next()){
			//System.out.println("*"+resultSet.getInt("IsAdmin"));
			AdminId=resultSet.getInt("AdminId");
		}
		
		AddasflightadminPS = conn.prepareStatement(Queries.GET_FLIGHT_BY_ID);
		AddasflightadminPS.setInt(1, flightId);
		resultSet = AddasflightadminPS.executeQuery();
		while(resultSet.next()){
			//System.out.println("*"+resultSet.getInt("IsAdmin"));
			flightCode=resultSet.getString("flightCode");
		}
		try {
			AddasflightadminPS = conn.prepareStatement(Queries.ADD_ME_AS_FLIGHT_ADMIN);
			AddasflightadminPS.setInt(1, AdminId);
			AddasflightadminPS.setString(2, flightCode);
			AddasflightadminPS.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		System.out.println("You are added as Admin for the Flight!");
	}*/



	private static void updateAdminInfo(Connection conn, int member2) throws SQLException {
		// TODO Auto-generated method stub
		PreparedStatement CancelmemberflightPS = conn.prepareStatement(Queries.ADD_NEW_ADMIN);
		CancelmemberflightPS.setInt(1, member2);
		//CancelmemberflightPS.setInt(2, flightId);
		CancelmemberflightPS.executeUpdate();
		System.out.println("SP execution success!");
		
	}





	private static void Cancelmemberflight(Connection conn, int flightId, int member2) throws SQLException {
		// TODO Auto-generated method stub
		
		PreparedStatement CancelmemberflightPS = conn.prepareStatement(Queries.DELETE_A_BOOKING);
		CancelmemberflightPS.setInt(1, member2);
		CancelmemberflightPS.setInt(2, flightId);
		CancelmemberflightPS.executeUpdate();
		System.out.println("Your booked flight ID:"+flightId+" has been successfuly canceled !");
		
		
	}



	private static int IsMemberAdmin(Connection conn, int member2) throws SQLException {
		// TODO Auto-generated method stub
		   
		PreparedStatement IsMemberAdminPS = conn.prepareStatement(Queries.GET_ADMIN_STATUS);
		IsMemberAdminPS.setInt(1, member2);
		int result=0;
		ResultSet resultSet = IsMemberAdminPS.executeQuery();
		while(resultSet.next()){
			//System.out.println("*"+resultSet.getInt("IsAdmin"));
			result=resultSet.getInt("IsAdmin");
		}
		return (result);
	}




	private static void bookFlightIDbyMemberID(Connection conn, int member2, int flightId) throws SQLException {
		// TODO Auto-generated method stub
		
		PreparedStatement bookFlightPS = conn.prepareStatement(Queries.INSERT_A_BOOKING);
		
		try {
			bookFlightPS.setInt(1, member2);
			bookFlightPS.setInt(2, flightId);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		bookFlightPS.executeUpdate();
		System.out.println("Record is inserted into Flight table!");
		
	}

	private static int createUser(Scanner input, Connection conn) throws SQLException{
		System.out.print("Please register your information below: \n");
		System.out.print("Enter your first name: ");
		String fname = input.next();
		System.out.println("Enter your last name: ");
		String lname = input.next();
		input.nextLine();
		System.out.print("Enter your address: ");
		String address = input.nextLine();
		System.out.print("Enter your zipcode: ");
		String zip = input.next();
		System.out.print("Enter your state: ");
		String state = input.next();
		System.out.print("Enter username: ");
		String username = input.next();
		System.out.print("Enter password: ");
		String password = input.next();
		System.out.print("Enter your email: ");
		String email = input.next();
		System.out.print("Enter your ssn: ");
		String ssn = input.next();			
		input.nextLine();
		System.out.print("Enter security question: ");
		String securityQuestion = input.nextLine();
		System.out.print("Enter security answer: ");
		String sqAnswer = input.nextLine();
		
		//create user in db
		PreparedStatement createMemberPS = conn.prepareStatement(Queries.REGISTER_USER_INFO);
		createMemberPS.setString(1, fname);
		createMemberPS.setString(2, lname);
		createMemberPS.setString(3, address);
		createMemberPS.setString(4, zip);
		createMemberPS.setString(5, state);
		createMemberPS.setString(6, username);
		createMemberPS.setString(7, password);
		createMemberPS.setString(8, email);
		createMemberPS.setString(9, ssn);
		createMemberPS.setString(10, securityQuestion);
		createMemberPS.setString(11, sqAnswer);
		createMemberPS.executeUpdate();
		System.out.println("Record is inserted into Member table!");
	
		
		PreparedStatement myStat = conn.prepareStatement(Queries.GET_MEMBER_BY_USERNAME);
		myStat.setString(1, username);
		ResultSet memberRs = myStat.executeQuery();
		String query1 = "update [airlineReservationDB].dbo.member set passwordhash=? where memberid=?";
		int fc;
		String fc1;
		while (memberRs.next())
		{
			System.out.println(memberRs.getRow());
			System.out.println((memberRs.getString("password")+memberRs.getString("passwordsalt")).hashCode());
			fc=(memberRs.getString("password")+memberRs.getString("passwordsalt")).hashCode();
			if (fc<0)
			{fc=fc*-1;}
			fc1=String.valueOf(fc);
			//System.out.println("started update");
			PreparedStatement myStat1 = conn.prepareStatement(query1);
			myStat1.setString(1, fc1);
			myStat1.setString(2, memberRs.getString("memberid"));
			myStat1.executeUpdate();
			
		}
		
		System.out.println("Password hash updated!");
		//get new user 
		int memberId = -1;
		boolean result = getMemberIdByUsernameAndPasswordHash(conn, username, password);
		if(result){
			memberId = member; 
		}
		
		return memberId;
	}
	
	/*
	private static ResultSet getMemberIdByUsernameAndPassword(Connection conn, String username, String password)  throws SQLException{
		PreparedStatement myStat = conn.prepareStatement(Queries.GET_USER_BY_USERNAME_AND_PASSWORD);
		myStat.setString(1, username);
		myStat.setString(2, password);
		ResultSet memberRs = myStat.executeQuery();
		return memberRs;
	}*/
	
	private static boolean getMemberIdByUsernameAndPasswordHash(Connection conn, String username, String password)  throws SQLException{
		PreparedStatement myStat = conn.prepareStatement(Queries.GET_MEMBER_BY_USERNAME);
		myStat.setString(1, username);
		ResultSet memberRs = myStat.executeQuery();
		int MyCalHashCode,FetchedHashCode;
while(memberRs.next())
{
			FetchedHashCode=Integer.valueOf(memberRs.getString("passwordhash"));
			//System.out.println("fetched hashcode"+String.valueOf(FetchedHashCode));
			MyCalHashCode=((password+memberRs.getString("passwordsalt")).hashCode());
			if (MyCalHashCode<0)
			{MyCalHashCode=MyCalHashCode*-1;}
			//System.out.println("calculated hashcode"+String.valueOf(MyCalHashCode));
		
		    if(FetchedHashCode==MyCalHashCode)
		    {
		    	member = memberRs.getInt("memberID");
		    	return true;
		    }
		
		    else
		    {
		    	return false;
		    }
		    
}
return false;
		
	}
	
	
	private static List<Integer> getFlightIdsByMemberId(int memberId, Connection conn) throws SQLException{
		List<Integer> flightIdList = new ArrayList<>();
		PreparedStatement myStat = conn.prepareStatement(Queries.GET_FLIGHT_BY_MemberID);
		myStat.setInt(1, memberId);
		ResultSet rs = myStat.executeQuery();
		while(rs.next()){
			flightIdList.add(rs.getInt("flightID"));
		}
		return flightIdList;
	}
	
	private static void printFlightInfoByFlightId(Connection conn, List<Integer> flightIds) throws SQLException{
		System.out.println("*****************************************************************************");
		for(int id : flightIds){
			PreparedStatement myStat = conn.prepareStatement(Queries.GET_FLIGHT_BY_ID);
			myStat.setInt(1, id);
			ResultSet resultSet = myStat.executeQuery();
			while(resultSet.next()){
				System.out.println("*"+ resultSet.getString("flightID") + " , "
						+ resultSet.getString("flightCode")+ " , "
						+ resultSet.getString("departureCity")+" , "
						+ resultSet.getString("destination")+" , "
						+ resultSet.getString("departureTime")+" , "
						+ resultSet.getString("arrivalTime")+" , "
						+ resultSet.getString("seatCapacity"));
			}
		}
		System.out.println("*****************************************************************************");
	}
	
	
	
	private static void printFlightByDepartureAndArrivalCity(Connection conn, String departureCity, String arrivalCity) throws SQLException{
		System.out.println("*****************************************************************************");
		PreparedStatement myStat = conn.prepareStatement(Queries.GET_FLIGHT_BY_DEPARTURE_DESTINATION);
		myStat.setString(1, departureCity);
		myStat.setString(2, arrivalCity);
		ResultSet resultSet = myStat.executeQuery();
		while(resultSet.next()){
			System.out.println("*"+ resultSet.getString("flightID") + " , "
					+ resultSet.getString("flightCode")+ " , "
					+ resultSet.getString("departureCity")+" , "
					+ resultSet.getString("destination")+" , "
					+ resultSet.getString("departureTime")+" , "
					+ resultSet.getString("arrivalTime")+" , "
					+ resultSet.getString("seatCapacity"));
		}
	
	System.out.println("*****************************************************************************");
		
	
	}
	
	
	
	private static void printAllFlightInfo(Connection conn) throws SQLException{
		System.out.println("*****************************************************************************");
		PreparedStatement myStat = conn.prepareStatement(Queries.GET_ALL_FLIGHTS);
		ResultSet resultSet = myStat.executeQuery();
		while(resultSet.next()){
			System.out.println("*"+ resultSet.getString("flightID") + " , "
					+ resultSet.getString("flightCode")+ " , "
					+ resultSet.getString("departureCity")+" , "
					+ resultSet.getString("destination")+" , "
					+ resultSet.getString("departureTime")+" , "
					+ resultSet.getString("arrivalTime")+" , "
					+ resultSet.getString("seatCapacity"));
		}
	
	System.out.println("*****************************************************************************");
		
	
	}
	
	
	
	
	
	
	
	
	private static void printAllMemberInfo(Connection conn) throws SQLException{
		System.out.println("*****************************************************************************");
		PreparedStatement myStat = conn.prepareStatement(Queries.GET_MEMBERINFO);
		ResultSet resultSet = myStat.executeQuery();
		while(resultSet.next()){
			System.out.println("*"+ resultSet.getString("memberID") + " , "
					+ resultSet.getString("lname")+ " , "
					+ resultSet.getString("fname")+" , "
					+ resultSet.getString("address")+" , "
					+ resultSet.getString("zip")+" , "
					+ resultSet.getString("state")+" , "
					+ resultSet.getString("username")+" , "
					+ resultSet.getString("password")+" , "
					+ resultSet.getString("email")+" , "
					+ resultSet.getString("ssn")+" , "
					+ resultSet.getString("securityQuestion")+" , "
					+ resultSet.getString("securityAnswer"));
		}
	
	System.out.println("*****************************************************************************");
		
	
	}
	
	
	

		
	private static void createFlight(Scanner input, Connection conn, int isAdmin, int member2) throws SQLException{
		System.out.print("Please enter the flight information below: \n");
		System.out.print("Enter the flight code: ");
		String flightCode = input.next();
		input.nextLine();
		System.out.println("Enter the destination: ");
		String destination = input.nextLine();
		System.out.println("Enter the flight's seat capacity: ");
		String seatCapacity = input.next();
		input.nextLine();
		System.out.print("Enter the departure city: ");
		String departureCity = input.nextLine();
		input.nextLine();
		System.out.println("Enter the departure time: ");
		String departureTime = input.nextLine();
		input.nextLine();
		System.out.print("Enter the arrival time: ");
		String arrivalTime = input.nextLine();
		
		int AdminID=0;
		if (isAdmin==1)
		{
		    
			 PreparedStatement myStat = conn.prepareStatement(Queries.GET_MEMBERINFO_BY_MEMBERID);
	    	 myStat.setInt(1,member2);
	    	 ResultSet resultSet = myStat.executeQuery();
	    	 while(resultSet.next()){
	    		 AdminID=resultSet.getInt("adminID");}
		}
		
		
			
		//create flight in db
		PreparedStatement createFlightPS = conn.prepareStatement(Queries.ADD_FLIGHT);
		createFlightPS.setString(1, flightCode);
		createFlightPS.setString(2, destination);
		createFlightPS.setString(3, seatCapacity);
		createFlightPS.setString(4, departureCity);
		createFlightPS.setString(5, departureTime);
		createFlightPS.setString(6, arrivalTime);
		createFlightPS.setInt(7, AdminID);
		
		createFlightPS.executeUpdate();
		System.out.println("Record is inserted into Flight table!");
		
		//get new flight 
	
	}
	
	private static void deleteFlight(Scanner input, Connection conn) throws SQLException{
		System.out.print("Please enter the flight id numer for delete: " );
		int deleteFlightId = input.nextInt();
		PreparedStatement deleteFlightPS = conn.prepareStatement(Queries.DELETE_FLIGHT);
		deleteFlightPS.setInt(1, deleteFlightId);
		deleteFlightPS.executeUpdate();
		System.out.println("Flight deleted!");
	
	
	
	}
	
		
		
	/*
     private static void printMemberProfByMemberId(Connection conn, List<Integer> memberId) throws SQLException{
    	 System.out.println("*****************************************************************************");
    	 for(int id: memberId){
    	 PreparedStatement myStat = conn.prepareStatement(Queries.GET_MEMBERINFO_BY_MEMBERID);
    	 myStat.setInt(1,id);
    	 ResultSet resultSet = myStat.executeQuery();
    	 while(resultSet.next()){
				System.out.println("*"+ resultSet.getString("memberID") + " , "
						+ resultSet.getString("lname")+ " , "
						+ resultSet.getString("fname")+" , "
						+ resultSet.getString("address")+" , "
						+ resultSet.getString("zip")+" , "
						+ resultSet.getString("state")+" , "
						+ resultSet.getString("email")+" , "
						+ resultSet.getString("ssn")+" , "
						+ resultSet.getString("userneme")+" , "
						+ resultSet.getString("email"));
			
		}
		System.out.println("*****************************************************************************");
    	 } 
    	 
    	 */
     }
			
			
			
		


