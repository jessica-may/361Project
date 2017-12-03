import java.sql.Connection;
import java.util.Properties;
import java.sql.DriverManager;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

class JDBCTest{
	public static void main(String[] args){
		try{
			JDBCInterface.deleteUser("a");
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public static Connection getConnection(){	
		Connection con=null;
		Properties connectionProps=new Properties();
		connectionProps.put("user","root");
		connectionProps.put("password","nenv5696");
		try{
			DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			con=DriverManager.getConnection("jdbc:mysql://localhost:3306/mysql",connectionProps);
		}catch(Exception e){
			e.printStackTrace();
		}
		return con;
	}

	public static void addPin(int x, int y) throws Exception{
		String add_pin_statement="INSERT INTO `Pins` (`x`,`y`) VALUES ("+x+","+y+");";
		Connection con=getConnection();	
		Statement st=con.createStatement();
		st.executeUpdate(add_pin_statement);
	}
}
