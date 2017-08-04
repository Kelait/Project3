package pjt3;
import java.io.*;
import java.util.*;
public class Project3 {


	public static final int SEED = 777;
	
	public static Scanner sysScnr = new Scanner(System.in);
	public static Random randy = new Random(SEED);
	
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
				System.out.println("Unrecoverable error encountered, terminating program. Check log.txt for more information.");
				PrintWriter exceptionLogger = new PrintWriter("log.txt");
				e.printStackTrace(exceptionLogger);
				exceptionLogger.close();
				System.exit(0); 
			}
			catch(Exception e1){
				//Everything is now broken.
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
