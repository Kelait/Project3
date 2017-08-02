package pjt3;
import java.io.*;
import java.util.*;

public class Game {

	public static final int SEED = 777;

	public static HashMap<Integer, String> localization = new HashMap<Integer,String>();

	private static Random randy;

	private static WordGenerator wordy;
	
	private static String curLanguage = "eng";
	
	private static void readInLocalization(){
		Scanner in = null;
		int fileLineNum = 1;
		try{
			in = new Scanner(new FileReader(curLanguage + "Local.txt"));
			while(in.hasNextLine()){
				
				String curLine = in.nextLine();
				String[] curValues = curLine.split("\\s*,\\s*");
				int curIndex = Integer.parseInt(curValues[0]);
				localization.put(curIndex, curValues[1]);
				fileLineNum++;
			}
		}
		catch(NumberFormatException e){
			throw new RuntimeException("Error located in " + curLanguage + "Local.txt" + " at line " + fileLineNum,e);
		}
		catch(IllegalStateException e){
			throw new RuntimeException("Exception encountered while reading in localization.",e);
		}
		catch(FileNotFoundException e){
			throw new RuntimeException("Unable to find file: "+ curLanguage + "Local.txt");
		}
		finally{
			in.close();
		}
	}
	Game() throws RuntimeException, GameOverException{
		try{
			randy = new Random(SEED);
			wordy = new WordGenerator(randy);
			readInLocalization();
			initGame();
			run();
		}
		catch(IOException e){
			throw new RuntimeException("Failed to initialize word list.", e);
		}
		catch(RuntimeException e){
			throw new RuntimeException("Exception encountered while constructing game.", e);
		}
	}
	private static void run() throws GameOverException{
		try{
			while(true){
				//TODO Game logic.
				turn();
			}
		}
		catch(GameOverException e){
			//TODO something something end game.
		}
	
	}
	private static void initPlayers(){

	}

	private static void initGame(){
		try{
			initPlayers();
		}
		finally{
			//TODO do a thing, like dance.
		}
	
	}
	private static void turn() throws GameOverException{

	}

	public void reset() {
		// TODO Auto-generated method stub
		
	}
	private static String demandUserInput(String displayString, ArrayList<String> possibleChoices, Scanner inScnr){
		for(boolean isInput = false; !isInput;){
			try{
				try{
					System.out.print(displayString + " ");
					String sendString = inScnr.nextLine();
					if(sendString.length()<1||sendString==null){
						throw new UserInputNotFoundException("No input detected.");
					}
					if(!possibleChoices.contains(sendString)){
						throw new InvalidUserInputException("Invalid input detected.");
					}
					isInput = true;
					return sendString;
					
				}
				catch(NullPointerException e){
					throw new UserInputNotFoundException("No input detected.", e);
				}
				catch(InvalidUserInputException e){
					throw e;
				}
				catch(RuntimeException e){
					throw new RuntimeException("Unexpected exception while demanding user input.", e );
				}
			}
			catch(InvalidUserInputException e){
				System.out.println(e.getMessage());
			}
			catch(RuntimeException e){
				e.printStackTrace();
			}
		}
		return null;
	}
	public void checkContinue() throws QuitGameException {
		String[] aAvail = {"y","n","Y","N","Yes","No","yes","no"};
		ArrayList<String> avail = new ArrayList<String>(Arrays.asList(aAvail));
		if(demandUserInput(localization.get(0001),avail,Project3.sysScnr).charAt(0)=='y'){
			throw new QuitGameException("Exiting game by user input.");
		}
		else{
			//Do nothing.
		}
		
	}

}
