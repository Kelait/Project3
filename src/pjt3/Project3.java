package pjt3;
import java.io.*;
import java.util.*;
public class Project3 {


	static Scanner sysScnr = new Scanner(System.in);
	
	
	public static void main(String[] args) {
		Game gamey;
		try{
			while(true){//Repeat
				gamey = new Game();
				gamey.checkContinue();
			}
		}
		catch(QuitGameException e){
			//TODO Roses are red, violets are blue, I should find something for you to do.
		}
		catch(Exception e){	//Capt'n, we have a problem.
			try{
				System.out.println("Exception encountered, terminating program.");
				PrintWriter exceptionLogger = new PrintWriter("log.txt");
				e.printStackTrace(exceptionLogger);
				exceptionLogger.close();
				System.exit(0); 
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
