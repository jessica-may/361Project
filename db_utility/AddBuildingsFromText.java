import java.util.Scanner;
import java.io.File;
import java.util.ArrayList;

public class AddBuildingsFromText{
	public static void main(String[] args){
		Scanner r=null;
		try{
			r=new Scanner(new File(args[0]));
		} catch(Exception e){
			e.printStackTrace();
		}
		int lineNum=0;
		ArrayList<String[]> buildings=new ArrayList<String[]>();
		while(r.hasNextLine()){
			lineNum++;
			String line[]=r.nextLine().split(":");
			if(line.length<2){
				System.out.println("problem on line "+lineNum);
				return;
			}
			buildings.add(line);
		}
		for(int i=0; i<buildings.size(); i++){
			String[] b=buildings.get(i);
			try{
				JDBCInterface.addBuilding(b[0].trim(),b[1].trim());
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		System.out.println(buildings.size()+" buildings added to database");
	}
}
