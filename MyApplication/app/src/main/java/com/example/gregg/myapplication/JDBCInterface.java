package com.example.gregg.myapplication;

import java.util.ArrayList;
import java.sql.Connection;
import java.util.Properties;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import android.os.AsyncTask;

class DBRequest{
	public DBRequest(String r){
		request=r;
		isUpdate=true;
	}
	public DBRequest(String r, ResultSetHolder h){
		request=r;
		isUpdate=false;
		holder=h;
	}
	public String request;
	boolean isUpdate; //otherwise is query
	ResultSetHolder holder; //only used for queries
}

class ResultSetHolder{
	public ResultSet rs;
}

public class JDBCInterface{
	public static String lastUsername;
	static Vector<DBRequest> requestQueue=new Vector<DBRequest>();
	static ExecuteQueueTasks queueTask=new ExecuteQueueTasks();
	public static void setupDBs(){
		//set up tables
		String create_pins = "CREATE TABLE IF NOT EXISTS `pins` ("
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
				+ "`pinID` INT NOT NULL,"
				+ "`username` VARCHAR(255) NOT NULL,"
				+ "`vote` INT NOT NULL,"
				+ "PRIMARY KEY(`voteID`));";
		String create_reports="CREATE TABLE IF NOT EXISTS `reports` ("
				+ "`reportID` INT NOT NULL AUTO_INCREMENT,"
				+ "`pinID` INT NOT NULL,"
				+ "`username` VARCHAR(255) NOT NULL,"
				+ "`text` VARCHAR(255) NOT NULL,"
				+ "PRIMARY KEY(`reportID`));";
		ExecuteUpdateTask eu = new ExecuteUpdateTask();
		eu.executeOnExecutor(ExecuteUpdateTask.THREAD_POOL_EXECUTOR,create_pins);
		eu.executeOnExecutor(ExecuteUpdateTask.THREAD_POOL_EXECUTOR,create_users); //hopefully using execute twice is okay
		eu.executeOnExecutor(ExecuteUpdateTask.THREAD_POOL_EXECUTOR,create_buildings);
		eu.executeOnExecutor(ExecuteUpdateTask.THREAD_POOL_EXECUTOR,create_votes);
		eu.executeOnExecutor(ExecuteUpdateTask.THREAD_POOL_EXECUTOR,create_reports);
	}

	public static void clearDBs(){
		String clear_pins = "truncate `pins`;";
		String clear_users = "truncate `users`;";
		String clear_buildings = "truncate `buildings`;";
		String clear_votes = "truncate `votes`;";
		String clear_reports = "truncate `reports`;";
		ExecuteUpdateTask eu = new ExecuteUpdateTask();
		eu.executeOnExecutor(ExecuteUpdateTask.THREAD_POOL_EXECUTOR,clear_pins);
		eu.executeOnExecutor(ExecuteUpdateTask.THREAD_POOL_EXECUTOR,clear_users); //hopefully using execute twice is okay
		eu.executeOnExecutor(ExecuteUpdateTask.THREAD_POOL_EXECUTOR,clear_buildings);
		eu.executeOnExecutor(ExecuteUpdateTask.THREAD_POOL_EXECUTOR,clear_votes);
		eu.executeOnExecutor(ExecuteUpdateTask.THREAD_POOL_EXECUTOR,clear_reports);
	}

	public static void addPin(String position, String description, String category, String username){
		String add_pin="INSERT INTO `pins` "
				+ "(`position`,`description`,`category`,`username`,`time`) VALUES "
				+ "('"+position+"','"+description+"','"+category+"','"+username+"',"+System.currentTimeMillis()+");";
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
		System.out.println("a");
		addRequestToQueue(clear_old_vote,null);
		addRequestToQueue(add_vote,null);
		//count new total for pin
		String get_votes_for_pin="SELECT * FROM `votes` WHERE"
				+" `pinID`="+pinID+";";
		ResultSetHolder holder=new ResultSetHolder();
		addRequestToQueue(get_votes_for_pin,holder);
		queueTask.get();
		ResultSet rs=holder.rs;
		int voteTotal=0;
		while(rs.next()){
			voteTotal+=Integer.parseInt(rs.getString("vote"));
		}
		//set pin votes to counted total if >-3, otherwise remove
		System.out.println("d");
		if (voteTotal>-3){
			String set_votes="UPDATE `pins` SET `votes`="+voteTotal
					+" WHERE `pinID`="+pinID+";";
			eu= new ExecuteUpdateTask();
			eu.executeOnExecutor(ExecuteUpdateTask.THREAD_POOL_EXECUTOR,set_votes);
			System.out.println("e");
		} else {
			String del_pin="DELETE FROM `pins` WHERE `pinID`="+pinID+";";
			String del_votes="DELETE FROM `votes` WHERE `pinID`="+pinID+";";
			addRequestToQueue(del_pin,null);
			addRequestToQueue(del_votes,null);
		}
	}

	public static void addReport(int pinID, String username, String text){
		String add_report="INSERT INTO `reports` (`pinID`,`username`,`text`)"
				+" VALUES ("+pinID+",'"+username+"','"+text+"');";
		ExecuteUpdateTask eu = new ExecuteUpdateTask();
		eu.executeOnExecutor(ExecuteUpdateTask.THREAD_POOL_EXECUTOR,add_report);
	}

	public static int findVoteTotal(int pinID) throws Exception{
		String get_votes_for_pin="SELECT * FROM `votes` WHERE"
				+" `pinID`="+pinID+";";
		ExecuteQueryTask eq = new ExecuteQueryTask();
		System.out.println("ready to get stuff");
		ResultSet rs = eq.executeOnExecutor(ExecuteQueryTask.THREAD_POOL_EXECUTOR,get_votes_for_pin).get();
		int voteTotal=0;
		System.out.println("got stuff into rs");
		while(rs.next()){
			voteTotal+=Integer.parseInt(rs.getString("vote"));
		}
		return voteTotal;
	}

	public static ArrayList<String[]> getPins() throws Exception{
		String get_pins = "SELECT * FROM `pins`";
		ExecuteQueryTask eq = new ExecuteQueryTask();
		ResultSet rs = eq.executeOnExecutor(ExecuteQueryTask.THREAD_POOL_EXECUTOR,get_pins).get();
		ArrayList<String[]> pins = new ArrayList<String[]>(); //later user <Pin>
		while(rs.next()){
			String[] pin = {rs.getString("position"),rs.getString("category")
					,rs.getString("description"),rs.getString("username")
					,rs.getString("pinID"),rs.getString("votes")
					,rs.getString("time")};
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
					,Integer.parseInt(rs.getString("pinID")),Integer.parseInt(rs.getString("votes"))
					,Long.parseLong(rs.getString("time")));
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
			System.out.println("start eq");
			try{
				Connection con=getConnection();
				Statement st=con.createStatement();
				ResultSet rs=st.executeQuery(params[0]);
				System.out.println("end eq");
				return rs;
			} catch(Exception e){
				System.out.println("!!!"+e.getStackTrace());
				//e.printStackTrace();
				System.out.println("end eq (ERR)");
				return null;
			}
		}
	}

	private static class ExecuteUpdateTask extends AsyncTask<String,Object,Boolean>{
		protected Boolean doInBackground(String... params){
			System.out.println("start eu");
			try{
				Connection con=getConnection();
				Statement st=con.createStatement();
				for (String p : params)
					st.executeUpdate(p);
				//st.executeUpdate(params[0]);
				System.out.println("end eu");
				return true;
			} catch(Exception e){
				System.out.println("!!!"+e.getStackTrace());
				System.out.println("end eu (ERR)");
				e.printStackTrace();
				return false;
			}
		}
	}

	public static void addRequestToQueue(String request, ResultSetHolder h){
		if(h==null)
			requestQueue.add(new DBRequest(request));
		else
			requestQueue.add(new DBRequest(request,h));
		if(queueTask.getStatus()!= AsyncTask.Status.RUNNING){
			queueTask.executeOnExecutor(ExecuteQueueTasks.THREAD_POOL_EXECUTOR);
		}
	}

	private static class ExecuteQueueTasks extends AsyncTask<Void,Object,Boolean>{
		protected Boolean doInBackground(Void... params){
			while(JDBCInterface.requestQueue.size()>0) {
				try {
					Connection con = getConnection();
					Statement st = con.createStatement();
					String request=JDBCInterface.requestQueue.get(0).request;
					boolean isUpdate=JDBCInterface.requestQueue.get(0).isUpdate;
					if(isUpdate)
						st.executeUpdate(request);
					else
						requestQueue.get(0).holder.rs=st.executeQuery(request);
					JDBCInterface.requestQueue.remove(0);
					System.out.println("completed request: "+request);
				} catch (Exception e) {
					System.out.println("!!!" + e.getStackTrace());
					e.printStackTrace();
				}
			}
			System.out.println("Queue now empty.");
			return true;
		}
	}
}
