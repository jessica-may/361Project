import java.util.ArrayList;

class JDBCRunner{
	//This class is used to run JDBCInterface methods without running the whole app
	public static void main(String[] args){
		try{
			JDBCInterface.setupDBs();
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
