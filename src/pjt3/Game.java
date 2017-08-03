package pjt3;
import java.io.*;
import java.util.*;

public class Game {

	public static final int SEED = 777;

	private static final int TOTLIVES = 10;
	
	private static HashMap<Integer, String> localization = new HashMap<Integer,String>();

	private static HashMap<String, Object> localizationVarMap = new HashMap<String, Object>();

	private static Random randy;

	private static WordGenerator wordy;

	private static String curLanguage = "eng";

	private static int curWordLength;
	private static int curLives;
	private static char curCharInput;


	private static ArrayList<Integer> curGuessPositions = new ArrayList<Integer>();
	private static ArrayList<Character> curRevealedChars = new ArrayList<Character>(curWordLength);
	private static ArrayList<Character> curGuessedChars = new ArrayList<Character>(curWordLength);

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
	Game() throws RuntimeException{
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
	private static void run(){
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
		//Display past turn stats
		//Request new char input
		//Update base values based on input
		//Update derived values based on input


		updateVarMap();//ALWAYS DO AT END
	}

	public void reset() {
		// TODO Auto-generated method stub

	}
	private static String demandUserInput(int displayStringIndex, ArrayList<String> possibleChoices, Scanner inScnr){
		for(boolean isInput = false; !isInput;){
			try{
				try{
					displayLine(displayStringIndex);
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

	private static void updateVarMap(){
		String tempRevealWord = "";
		for(Character c: curRevealedChars){
			tempRevealWord += c + " ";
		}
		String tempGuessChars = "";
		for(Character c: curGuessedChars){
			tempGuessChars += c + " ";
		}
		String tempGuessPos = "";
		for(Integer c: curGuessPositions){
			tempGuessPos += c + " ";
		}
		localizationVarMap.put("$revealWord", tempRevealWord);
		localizationVarMap.put("$guessChars", tempGuessChars );
		localizationVarMap.put("$guessPositions", tempGuessPos );
		localizationVarMap.put("$wordLength", new Integer(curWordLength));
		localizationVarMap.put("$charInput", new Character(curCharInput));
		localizationVarMap.put("$strikes", new Integer(curLives));
	}

	public static void displayLine(int inLocalizationIndex){
		if(!localization.get(inLocalizationIndex).contains("$")){
			System.out.println(localization.get(inLocalizationIndex) + " ");
		}
		else{
			Scanner varScnr = new Scanner(localization.get(inLocalizationIndex));
			while(varScnr.hasNext()){
				String curWord = varScnr.next();
				if(curWord.charAt(0)=='$'){
					try{
						System.out.print(localizationVarMap.get(curWord) + " ");
					}
					catch(Exception e){
						e.printStackTrace();//something bad dun gone wrong
					}
				}
				else{
					System.out.print(curWord + " ");
				}
			}
		}
	}
	public void checkContinue() throws QuitGameException {
		String[] aAvail = {"y","n","Y","N","Yes","No","yes","no"};
		ArrayList<String> avail = new ArrayList<String>(Arrays.asList(aAvail));
		if(demandUserInput(0001,avail,Project3.sysScnr).charAt(0)=='y'){
			throw new QuitGameException("Exiting game by user input.");
		}
		else{
			//Do nothing.
		}

	}

}
