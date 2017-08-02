package pjt3;
import java.io.*;
import java.util.*;

public class Game {

	public static final int SEED = 777;

	private static Random randy;

	private static WordGenerator wordy;

	Game() throws Exception{
		try{
			randy = new Random(SEED);
				
			initGame();
			run();
		}
		catch(IOException e){
			throw new Exception("Exception encountered while constructing game.", e);
		}
	}
	private static void initGame() throws IOException{
		try{
			wordy = new WordGenerator(randy);
			initPlayers();
		}
		catch(IOException e){
			throw new IOException("IOException encountered while initalizing game.", e);
		}

	}
	private static void initPlayers(){

	}

	private static String demandUserInput(String displayString, List<String> possibleChoices, Scanner inScnr){
		for(boolean isInput = false; !isInput;){
			try{
				try{
					System.out.print(displayString + " ");
					String sendString = inScnr.nextLine();
					if(sendString.length()<1||sendString==null){
						throw new UserInputNotFoundException("No input found.");
					}
					if(!possibleChoices.contains(sendString)){
						throw new InvalidUserInputException("Invalid input detected.");
					}
					isInput = true;
					return sendString;
					
				}
				catch(NullPointerException e){
					throw new UserInputNotFoundException("No input found.", e);
				}
				catch(InvalidUserInputException e){
					throw e;
				}
				catch(Exception e){
					throw new Exception("Unhandled exception while demanding user input.", e );
				}
			}
			catch(InvalidUserInputException e){
				System.out.println(e.getMessage());
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return null;
	}


	private static void run(){
		while(!isOver()){
			turn();
		}

	}

	private static void turn(){

	}

	public static boolean isOver(){

		return false;
	}
	public static boolean isQuit() {

		return false;
	}

}
