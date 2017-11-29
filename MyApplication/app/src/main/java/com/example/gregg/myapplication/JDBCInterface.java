package com.example.gregg.myapplication;

import java.util.ArrayList;
import java.sql.Connection;
import java.util.Properties;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import android.os.AsyncTask;

public class JDBCInterface{
	public static String lastUsername;
	public static void setupDBs(){
		//set up tables
		String create_pins = "CREATE TABLE IF NOT EXISTS `pins` ("
				+ "`pinID` INT NOT NULL AUTO_INCREMENT,"
				+ "`username` VARCHAR(255) NOT NULL,"
				+ "`position` VARCHAR(255) NOT NULL,"
				+ "`category` VARCHAR(255) NOT NULL,"
				+ "`description` VARCHAR(255) NOT NULL,"
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
				+ "`pinID` INT NOT NULL,"
				+ "`username` VARCHAR(255) NOT NULL,"
				+ "`vote` INT NOT NULL,"
				+ "PRIMARY KEY(`voteID`));";
		ExecuteUpdateTask eu = new ExecuteUpdateTask();
		eu.executeOnExecutor(ExecuteUpdateTask.THREAD_POOL_EXECUTOR,create_pins);
		eu.executeOnExecutor(ExecuteUpdateTask.THREAD_POOL_EXECUTOR,create_users); //hopefully using execute twice is okay
		eu.executeOnExecutor(ExecuteUpdateTask.THREAD_POOL_EXECUTOR,create_buildings);
		eu.executeOnExecutor(ExecuteUpdateTask.THREAD_POOL_EXECUTOR,create_votes);
	}

	public static void clearDBs(){
		String clear_pins = "truncate `pins`;";
		String clear_users = "truncate `users`;";
		String clear_buildings = "truncate `buildings`;";
		String clear_votes = "truncate `votes`;";
		ExecuteUpdateTask eu = new ExecuteUpdateTask();
		eu.executeOnExecutor(ExecuteUpdateTask.THREAD_POOL_EXECUTOR,clear_pins);
		eu.executeOnExecutor(ExecuteUpdateTask.THREAD_POOL_EXECUTOR,clear_users); //hopefully using execute twice is okay
		eu.executeOnExecutor(ExecuteUpdateTask.THREAD_POOL_EXECUTOR,clear_buildings);
		eu.executeOnExecutor(ExecuteUpdateTask.THREAD_POOL_EXECUTOR,clear_votes);
	}

	public static void addPin(String position, String description, String category, String username){
		String add_pin="INSERT INTO `pins` "
				+ "(`position`,`description`,`category`,`username`) VALUES "
				+ "('"+position+"','"+description+"','"+category+"','"+username+"');";
		ExecuteUpdateTask eu = new ExecuteUpdateTask();
		eu.executeOnExecutor(ExecuteQueryTask.THREAD_POOL_EXECUTOR,add_pin);
	}

	public static void addUser(String username, String password){
		String add_user="INSERT INTO `users` "
				+ "(`username`,`password`) VALUES "
				+ "('"+username+"','"+password+"');";
		ExecuteUpdateTask eu = new ExecuteUpdateTask();
		eu.executeOnExecutor(ExecuteQueryTask.THREAD_POOL_EXECUTOR,add_user);
	}

	public static void addBuilding(String name, String location){
		String add_building="INSERT INTO `buildings` "
				+ "(`name`,`location`) VALUES "
				+ "('"+name+"','"+location+"');";
		ExecuteUpdateTask eu = new ExecuteUpdateTask();
		eu.executeOnExecutor(ExecuteQueryTask.THREAD_POOL_EXECUTOR,add_building);
	}

	public static void changePassword(String username, String password){
		String change_pw="UPDATE `users` SET `password`='"+password
		+"' WHERE `username`='"+username+"';";
		ExecuteUpdateTask eu = new ExecuteUpdateTask();
		eu.executeOnExecutor(ExecuteQueryTask.THREAD_POOL_EXECUTOR,change_pw);
	}

	public static void deleteUser(String username){
		String del_user="DELETE FROM `users` WHERE `username`='"
		+username+"';";
		ExecuteUpdateTask eu = new ExecuteUpdateTask();
		eu.executeOnExecutor(ExecuteQueryTask.THREAD_POOL_EXECUTOR,del_user);
	}

	public static void addVote(String username, int pinID, int vote) throws Exception{
		//clear any old vote for same pin by same user, then add new vote
		String clear_old_vote="DELETE FROM `votes` WHERE `pinID`="+pinID
			+" AND `username`='"+username+"';";
		String add_vote="INSERT INTO `votes` (`username`,`pinID`,`vote`)"
			+" VALUES ('"+username+"',"+pinID+","+vote+");";
		ExecuteUpdateTask eu = new ExecuteUpdateTask();
		eu.executeOnExecutor(ExecuteUpdateTask.THREAD_POOL_EXECUTOR,clear_old_vote);
		eu.executeOnExecutor(ExecuteUpdateTask.THREAD_POOL_EXECUTOR,add_vote);
		//count new total for pin
		String get_votes_for_pin="SELECT * FROM `votes` WHERE"
			+" `pinID`="+pinID+";";
		ExecuteQueryTask eq = new ExecuteQueryTask();
		ResultSet rs = eq.executeOnExecutor(ExecuteQueryTask.THREAD_POOL_EXECUTOR,get_votes_for_pin).get();
		int voteTotal=0;
		while(rs.next()){
			voteTotal+=Integer.parseInt(rs.getString("vote"));
		}
		//set pin votes to counted total if >-3, otherwise remove
		if (voteTotal>-3){
			String set_votes="UPDATE `pins` SET `votes`="+voteTotal
				+" WHERE `pinID`="+pinID+";";
			eu.executeOnExecutor(ExecuteUpdateTask.THREAD_POOL_EXECUTOR,set_votes);
		} else {
			String del_pin="DELETE FROM `pins` WHERE `pinID`="+pinID+";";
			String del_votes="DELETE FROM `votes` WHERE `pinID`="+pinID+";";
			eu.executeOnExecutor(ExecuteUpdateTask.THREAD_POOL_EXECUTOR,del_pin);
			eu.executeOnExecutor(ExecuteUpdateTask.THREAD_POOL_EXECUTOR,del_votes);
		}
	}

	public static ArrayList<String[]> getPins() throws Exception{
		String get_pins = "SELECT * FROM `pins`";
		ExecuteQueryTask eq = new ExecuteQueryTask();
		ResultSet rs = eq.executeOnExecutor(ExecuteQueryTask.THREAD_POOL_EXECUTOR,get_pins).get();
		ArrayList<String[]> pins = new ArrayList<String[]>(); //later user <Pin>
		while(rs.next()){
			String[] pin = {rs.getString("position"),rs.getString("category")
					,rs.getString("description"),rs.getString("username")
					,rs.getString("pinID"),rs.getString("votes")};
			pins.add(pin);
		}
		return pins;
	}

	public static ArrayList<Pin> getPinList() throws Exception{
		String get_pins = "SELECT * FROM `pins`";
		ExecuteQueryTask eq = new ExecuteQueryTask();
		ResultSet rs = eq.executeOnExecutor(ExecuteQueryTask.THREAD_POOL_EXECUTOR,get_pins).get();
		ArrayList<Pin> pins = new ArrayList<Pin>();
		while(rs.next()){
			Pin pin = new Pin(rs.getString("position"),rs.getString("category")
					,rs.getString("description"),rs.getString("username")
					,Integer.parseInt(rs.getString("pinID")),Integer.parseInt(rs.getString("votes")));
			pins.add(pin);
		}
		return pins;
	}

	public static String getPassword(String username) throws Exception{
		String get_user="SELECT * FROM `users` WHERE `username`='"+username+"';";
		ExecuteQueryTask eq=new ExecuteQueryTask();
		ResultSet rs=eq.executeOnExecutor(ExecuteQueryTask.THREAD_POOL_EXECUTOR,get_user).get();
		String password=null;
		while(rs.next()){
			password=rs.getString("password");
		}
		lastUsername = username;
		return password;
	}

	public static String getBuildingLocation(String building) throws Exception{
		String get_location="SELECT * FROM `buildings` WHERE `name`='"+building+"';";
		ExecuteQueryTask eq=new ExecuteQueryTask();
		ResultSet rs=eq.executeOnExecutor(ExecuteQueryTask.THREAD_POOL_EXECUTOR,get_location).get();
		String location="";
		while(rs.next()){
			location=rs.getString("location");
		}
		return location;
	}

	public static Connection getConnection() throws Exception{
		GetConnectionTask getCon=new GetConnectionTask();
		Connection con=getCon.executeOnExecutor(GetConnectionTask.THREAD_POOL_EXECUTOR,"jdbc:mysql://76.84.107.142:3306/test361","gregg","361projectpassword").get();
		return con;
	}

	private static class GetConnectionTask extends AsyncTask<String,Object,Connection>{
		protected Connection doInBackground(String... params){
			Connection con=null;
			Properties connectionProps=new Properties();
			connectionProps.put("user",params[1]);
			connectionProps.put("password",params[2]);
			try{
				DriverManager.registerDriver(new com.mysql.jdbc.Driver());
				con=DriverManager.getConnection(params[0],connectionProps);
			}catch(Exception e){
				e.printStackTrace();
			}
			return con;
		}
	}

	private static class ExecuteQueryTask extends AsyncTask<String,Object,ResultSet>{
		protected ResultSet doInBackground(String... params){
			try{
				Connection con=getConnection();
				Statement st=con.createStatement();
				ResultSet rs=st.executeQuery(params[0]);
				return rs;
			} catch(Exception e){
				e.printStackTrace();
				return null;
			}
		}
	}

	private static class ExecuteUpdateTask extends AsyncTask<String,Object,Boolean>{
		protected Boolean doInBackground(String... params){
			try{
				Connection con=getConnection();
				Statement st=con.createStatement();
				st.executeUpdate(params[0]);
				System.out.println("got here");
				return true;
			} catch(Exception e){
				System.out.println("broken");
				e.printStackTrace();
				return false;
			}
		}
	}
}
