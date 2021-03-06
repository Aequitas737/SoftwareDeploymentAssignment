package servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

//https://www.mkyong.com/jdbc/jdbc-statement-example-insert-a-record/

@WebServlet(
        name = "MyServlet", 
        urlPatterns = {"/status"}
    )
public class MyServlet extends HttpServlet {
/*
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        ServletOutputStream out = resp.getOutputStream();
        out.write("IF YOU ARE SEEING THIS THEN IT IS WORKING".getBytes());
        out.flush();
        out.close();
    }*/
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    		throws ServletException, IOException {

        ServletOutputStream out = response.getOutputStream();
        
        String userName = request.getParameter("name");
        String userAge = request.getParameter("age");
        
        PreparedStatement preparedStatement = null;
        
        try
        {
        	Connection connection = getConnection();
        	// connection = DatabaseUrl.extract().getConnection();
        	Statement stmt = connection.createStatement();
        	stmt.executeUpdate("CREATE TABLE IF NOT EXISTS user ("
        			+ "name varchar(40)"
        			+ "age char(9))");
        	String insertStatement = "INSERT INTO user(name, age) VALUES(?, ?)";
        	preparedStatement = connection.prepareStatement(insertStatement);
        	
        	preparedStatement.setString(1, userName);
        	preparedStatement.setString(2, userAge);
        	
        	preparedStatement.executeUpdate();        	
        }
        catch(Exception ex)
        {
        	//TODO handle specific exception
        }
       /* if(userAge == "1")
        {
        	get("/db", (request, response) ->  
        {
            Connection connection = null;
            Map<String, Object> attributes = new HashMap<>();
            try {
              connection = DatabaseUrl.extract().getConnection();
              Statement stmt = connection.createStatement();
              stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
              stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
              ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

              ArrayList<String> output = new ArrayList<String>();
              while (rs.next()) {
                output.add( "Read from DB: " + rs.getTimestamp("tick"));
              }

               attributes.put("results", output);
               return new ModelAndView(attributes, "db.ftl");
             } catch (Exception e) {
               attributes.put("message", "There was an error: " + e);
               return new ModelAndView(attributes, "error.ftl");
             } finally {
               if (connection != null) try{connection.close();} catch(SQLException e){}
             }
           } , new FreeMarkerEngine());
        }
        */
        
        String message = "";
        Calendar currentCalendar = Calendar.getInstance();
        try 
        {
			Calendar userCalendar = getDateFromString(request.getParameter("age"));
			long dayDifference = daysBetween(userCalendar, currentCalendar);
			message = "You are " + dayDifference + " days old."; 
		} 
        catch (ParseException e) 
        {
			message = "Invalid Age Format";
		}
        out.write(message.getBytes());
        out.flush();
        out.close();
    }
/**
 * postgres://qkzwskiqlsnorc:9IvEOeX1QUiVZPhAWWXelQz9s7@ec2-54-243-208-195.compute-1.amazonaws.com:5432/df1609d1j6jvn9
 * password, 9IvEOeX1QUiVZPhAWWXelQz9s7
 * user, qkzwskiqlsnorc
 * @return
 * @throws URISyntaxException
 * @throws SQLException
 */
    
    private static Connection getConnection() throws URISyntaxException, SQLException 
    {
        URI dbUri = new URI(System.getenv("DATABASE_URL"));

        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();

        return DriverManager.getConnection(dbUrl, username, password);
    }
    
    public static Calendar getDateFromString(String inputString) throws ParseException
    {
    	Calendar calendar = new GregorianCalendar();
    	Date date = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(inputString);
		calendar.setTime(date);
    	
    	return calendar;
    }
    
    public static long daysBetween(Calendar startDate, Calendar endDate) 
    {
        long end = endDate.getTimeInMillis();
        long start = startDate.getTimeInMillis();
        return TimeUnit.MILLISECONDS.toDays(Math.abs(end - start));
    }
    
    
}
