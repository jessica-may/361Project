import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;

public class CheckBuildings{
	public static void main(String[] args){
		//read in buildings_from_app.txt
		Scanner r = null;
		try{
			r = new Scanner(new File("buildings_from_app.txt"));
		} catch (Exception e){
			e.printStackTrace();
		}
		ArrayList<String> names = new ArrayList<String>();
		while (r.hasNextLine()){
			String line=r.nextLine().replace("<item>","").replace("</item>","").trim();
			names.add(line);
			//System.out.println(line);
		}
		//get coordinates from database
		System.out.println("Not in DB:");
		for (String n : names){
			String loc="not defined";
			try{
				loc=JDBCInterface.getBuildingLocation(n);
			}catch (Exception e) {loc="error!!!";}
			if(loc.equals(""))
				System.out.println("\t"+n);
		}
	}
}
