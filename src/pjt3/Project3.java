package pjt3;
import java.io.*;
import java.util.*;
public class Project3 {

	public static void main(String[] args) {
		
		final Scanner sysScnr = new Scanner(System.in);
		Game gamey;
		try{

			
			
			while(!Game.isQuit()){//Repeat
				gamey = new Game();
				gamey.reset();
			}
		}
		catch(Exception e){	//Capt'n, we have a problem.
			try{
				System.out.println("Exception encountered, terminating program.");
				PrintWriter exceptionLogger = new PrintWriter("log.txt");
				exceptionLogger.print(e.getStackTrace());
				System.exit(0); 
				exceptionLogger.close();
			}
			catch(FileNotFoundException e1){
				//you dun goofed.
			}
		}
		finally{
			try{
				sysScnr.close();
			}
			catch(IllegalStateException e){
				System.out.println("Critial Error encountered, terminating program.");
				System.exit(0); 
			}
		}
		
	}

}
