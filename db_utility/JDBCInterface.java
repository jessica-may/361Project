import java.util.ArrayList;
import java.sql.Connection;
import java.util.Properties;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JDBCInterface{
	public static void setupDBs() throws SQLException{
		//set up pins and users databases
		Connection con=getConnection();
		String create_pins="CREATE TABLE IF NOT EXISTS `pins` ("
		+ "`pinID` INT NOT NULL AUTO_INCREMENT,"
		+ "`username` VARCHAR(255) NOT NULL,"
		+ "`position` VARCHAR(255) NOT NULL,"
		+ "`category` VARCHAR(255) NOT NULL,"
		+ "`description` VARCHAR(255) NOT NULL,"
		+ "`time` BIGINT,"
		+ "`votes` INT DEFAULT 0,"
		+ "PRIMARY KEY(`pinID`));";
		String create_users="CREATE TABLE IF NOT EXISTS `users` ("
		+ "`userID` INT NOT NULL AUTO_INCREMENT,"
		+ "`username` VARCHAR(255) NOT NULL,"
		+ "`password` VARCHAR(255) NOT NULL,"
		+ "PRIMARY KEY(`userID`));";
 		String create_buildings="CREATE TABLE IF NOT EXISTS `buildings` ("
        	+ "`buildingID` INT NOT NULL AUTO_INCREMENT,"
        	+ "`name` VARCHAR(255) NOT NULL,"
        	+ "`location` VARCHAR(255) NOT NULL,"
        	+ "PRIMARY KEY(`buildingID`));";
		String create_votes="CREATE TABLE IF NOT EXISTS `votes` ("
                + "`voteID` INT NOT NULL AUTO_INCREMENT,"
                + "`position` VARCHAR(255) NOT NULL,"
                + "`username` VARCHAR(255) NOT NULL,"
                + "`vote` INT NOT NULL,"
                + "PRIMARY KEY(`voteID`));";
		String create_reports="CREATE TABLE IF NOT EXISTS `reports` ("
		+ "`reportID` INT NOT NULL AUTO_INCREMENT,"
		+ "`pinID` INT NOT NULL,"
		+ "`username` VARCHAR(255) NOT NULL,"
		+ "`text` VARCHAR(255) NOT NULL,"
		+ "PRIMARY KEY(`reportID`));";

		Statement st=con.createStatement();
		st.executeUpdate(create_pins);
		st.executeUpdate(create_users);
		st.executeUpdate(create_buildings);
		st.executeUpdate(create_votes);
		st.executeUpdate(create_reports);
	}

	public static void clearDBs() throws SQLException{
		Connection con=getConnection();
		String clear_pins="truncate `pins`;";
		String clear_users="truncate `users`;";
		String clear_buildings="truncate `buildings`;";
		String clear_votes="truncate `votes`;";
		String clear_reports="truncate `reports`;";
		Statement st=con.createStatement();
		st.executeUpdate(clear_pins);
		st.executeUpdate(clear_users);
		st.executeUpdate(clear_buildings);
		st.executeUpdate(clear_votes);
		st.executeUpdate(clear_reports);
	}

	public static void addPin(String position, String description, String category, String username) throws SQLException{
		Connection con=getConnection();
		String add_pin="INSERT INTO `pins` "
		+ "(`position`,`description`,`category`,`username`,`time`) VALUES "
		+ "('"+position+"','"+description+"','"+category+"','"+username+"',"+System.currentTimeMillis()+");";
		Statement st=con.createStatement();
		st.executeUpdate(add_pin);
	}

	public static void addUser(String username, String password) throws SQLException{
		Connection con=getConnection();
		String add_user="INSERT INTO `users` "
		+ "(`username`,`password`) VALUES "
		+ "('"+username+"','"+password+"');";
		Statement st=con.createStatement();
		st.executeUpdate(add_user);
	}

 	public static void addBuilding(String name, String location) throws Exception{
        	String add_building="INSERT INTO `buildings` "
        	+ "(`name`,`location`) VALUES "
        	+ "('"+name+"','"+location+"');";
		Statement st=getConnection().createStatement();
		st.executeUpdate(add_building);
    	}
	public static void changePassword(String username, String password) throws Exception{
        	String change_pw="UPDATE `users` SET `password`='"+password
        	+"' WHERE `username`='"+username+"';";
		Statement st=getConnection().createStatement();
		st.executeUpdate(change_pw);
	}

	public static void deleteUser(String username) throws Exception{
		String del_user="DELETE FROM `users` WHERE `username`='"
		+username+"';";
		Statement st=getConnection().createStatement();
		st.executeUpdate(del_user);
	}

	public static void addVote(String username, String position, int vote) throws Exception{
		//clear any old vote for same pin by same user, then add new vote
		String clear_old_vote="DELETE FROM `votes` WHERE `position`='"+position
			+"' AND `username`='"+username+"';";
		String add_vote="INSERT INTO `votes` (`username`,`position`,`vote`)"
			+" VALUES ('"+username+"','"+position+"',"+vote+");";
		Statement st=getConnection().createStatement();
		st.executeUpdate(clear_old_vote);
		st.executeUpdate(add_vote);
		//count new total for pin
		String get_votes_for_position="SELECT * FROM `votes` WHERE"
			+" `position`='"+position+"';";
		ResultSet rs=st.executeQuery(get_votes_for_position);
		int voteTotal=0;
		while(rs.next()){
			voteTotal+=Integer.parseInt(rs.getString("vote"));
		}
		//set pin votes to counted total if >-3, otherwise remove
		if (voteTotal>-3){
			String set_votes="UPDATE `pins` SET `votes`="+voteTotal
				+" WHERE `position`='"+position+"';";
			st.executeUpdate(set_votes);
		} else {
			String del_pins="DELETE FROM `pins` WHERE `position`='"+position+"';";
			String del_votes="DELETE FROM `votes` WHERE `pinID`='"+position+"';";
			st.executeUpdate(del_pins);
			st.executeUpdate(del_votes);
		}
	}

	public static void addReport(int pinID, String username, String text) throws Exception{
		String add_report="INSERT INTO `reports` (`pinID`,`username`,`text`)"
			+" VALUES ("+pinID+",'"+username+"','"+text+"');";
		Statement st=getConnection().createStatement();
		st.executeUpdate(add_report);
	}

	public static int getVotes(String position) throws SQLException{
		String get_votes="SELECT * FROM `pins` WHERE `position`='"+position+"';";
		Statement st=getConnection().createStatement();
		ResultSet rs=st.executeQuery(get_votes);
		rs.next();
		int votes=Integer.parseInt(rs.getString("votes"));
		return votes;
	}

	public static ArrayList<String[]> getPins() throws SQLException{
		Connection con=getConnection();
		String get_pins="SELECT * FROM `pins`";
		Statement st=con.createStatement();
		ResultSet rs=st.executeQuery(get_pins);
		ArrayList<String[]> pins=new ArrayList<String[]>(); //later user <Pin>
		while(rs.next()){
			String[] pin={rs.getString("position"),rs.getString("category")
				,rs.getString("description"),rs.getString("username")};
			pins.add(pin);
		}
		return pins;
	}

	public static String getPassword(String username) throws SQLException{
		Connection con=getConnection();
		String get_user="SELECT * FROM `users` WHERE `username`='"+username+"';";
		Statement st=con.createStatement();
		ResultSet rs=st.executeQuery(get_user);
		String password="";
		while(rs.next()){
			password=rs.getString("password");
		}
		return password;
	}

	public static String getBuildingLocation(String building) throws Exception{
        	String get_location="SELECT * FROM `buildings` WHERE `name`='"+building+"';";
		Statement st=getConnection().createStatement();
        	ResultSet rs=st.executeQuery(get_location);
        	String location="";
        	while(rs.next()){
        	    location=rs.getString("location");
        	}
        	return location;
    	}

    public static Connection getConnection(){
        Connection con=null;
        Properties connectionProps=new Properties();
        connectionProps.put("user","gregg");
        connectionProps.put("password","361projectpassword");
        try{
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            con=DriverManager.getConnection("jdbc:mysql://76.84.107.142:3306/test361",connectionProps);
        }catch(Exception e){
            e.printStackTrace();
        }
        return con;
    }
}
