
//import com.mysql.jdbc.Connection;
import com.microsoft.sqlserver.jdbc.SQLServerDriver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
public class airlineDBconnection {
		
	public static Connection getConnection() throws Exception{
		try{
			/*
			String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			String url = "jdbc:sqlserver://localhost/airlineReservationDB";
			String username = "sa";
			String password = "Myjobclosetorohan16";
			Class.forName(driver);*/
			String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
			//below works
			String url = "jdbc:sqlserver://localhost:1433";
			String username = "sa";
			String password = "Myjobclosetorohan16";
			Class.forName(driver);
			System.out.println("Start");
			
			//System.out.println("Connected");
			Connection conn = (Connection)DriverManager.getConnection (url,username, password);
			System.out.println("Connected");
			/*
			Statement s;
			ResultSet rs;
			String query = "select * from [airlineReservationDB].dbo.member";
			s=conn.createStatement();
			rs=s.executeQuery(query);
			String query1 = "update [airlineReservationDB].dbo.member set passwordhash=? where memberid=?";
			int fc;
			String fc1;
			while (rs.next())
			{
				System.out.println(rs.getRow());
				System.out.println((rs.getString("password")+rs.getString("passwordsalt")).hashCode());
				fc=(rs.getString("password")+rs.getString("passwordsalt")).hashCode();
				if (fc<0)
				{fc=fc*-1;}
				fc1=String.valueOf(fc);
				System.out.println("started update");
				PreparedStatement myStat = conn.prepareStatement(query1);
				myStat.setString(1, fc1);
				myStat.setString(2, rs.getString("memberid"));
				myStat.executeUpdate();
				
			}*/
			return conn;
			
//			//2. create a statement
//			PreparedStatement myStat = conn.prepareStatement(Queries.GET_USER_BY_USERNAME_AND_PASSWORD);
//			//3. execute SQL query
//			myStat.setString(1, "tian");
//			myStat.setString(2, "myPassword");
//			//4. process the result set
//			ResultSet resultSet = myStat.executeQuery(Queries.GET_USER_BY_USERNAME_AND_PASSWORD);
//		
//			while(resultSet.next()){
//				System.out.println(resultSet.getString("userId") + "   ,   "+ resultSet.getString("ssn")+ "    ,   "+ resultSet.getString("datebrith"));
//									
//			}            
																																																								
		}catch(Exception ex){
			System.out.println(ex);
						
		}
		return null;
	}
}