package pjt3;
import java.io.*;
import java.util.*;


public class Game {



	public static final int SEED = 777;

	private static final int TOTLIVES = 10;

	private static HashMap<Integer, String> localization = new HashMap<Integer,String>();

	private static HashMap<String, Object> localizationVarMap = new HashMap<String, Object>();

	private static Random randy;

	/**
	 * Dictionary words come from this.
	 * @see WordGenerator.java
	 */
	private static WordGenerator wordy;

	//Shouldn't change honestly, would need more language support if language were to change.
	private static String curLanguage = "eng";

	//Global game vars.
	private static boolean aiGuesser;
	private static boolean aiChooser;

	private static String aiChosenWord;
	private static int playerChosenWordLength;

	private static int numTurns;	

	//Vars for output display and gameplay.
	private static int curWordLength;
	private static int curLives;
	private static char curCharInput;

	//Arrays for output display and gameplay.
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
				if(curLine.isEmpty()||curLine.startsWith("#")||curLine.trim().isEmpty()){
					//ignore line
				}
				else{
					String[] curValues = curLine.split("\\s*,\\s*");
					int curIndex = Integer.parseInt(curValues[0]);
					localization.put(curIndex, curValues[1]);
					fileLineNum++;
				}
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
			reset();
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
	private static void initGame(){
		try{
			curLives = TOTLIVES;

			checkCG();
			if(aiChooser){
				aiChosenWord = wordy.chooseWord();
				for(int i = 0; i<aiChosenWord.length();i++){
					curRevealedChars.add('*');
				}
			}
			else{
				aiChosenWord = null;
				for(boolean isValid = false;!isValid;){
					try{
						playerChosenWordLength = Integer.parseInt(demandUserInput(18,Project3.sysScnr));
						isValid = true;
					}
					catch(NumberFormatException e){
						System.out.println(localization.get(16));
						isValid = false;
					}
				}
				for(int i = 0; i<playerChosenWordLength;i++){
					curRevealedChars.add('*');
				}
			}
			updateVarMap();
			//displayGameStatus();


		}
		catch(RuntimeException e){
			throw new RuntimeException("Failed to initialize game.",e);
		}

	}
	private static void turn() throws GameOverException{
		displayGameStatus();//Display past turn stats
		if(curLives == 0){
			throw new GameOverException("Loss by too many turns.");
		}
		String curGuess = new String();
		if(!aiGuesser){
			curGuess = demandUserInput(5,Project3.sysScnr);//Request new player input
		}
		else{
			//TODO ai guess logic.
		}
		
		//Update base values based on input
		//Update derived values based on input

		curGuess = null;
		updateVarMap();//ALWAYS DO AT END
	}

	public void reset() {

		curLives = TOTLIVES;
		numTurns = 0;
		aiGuesser = false;
		aiChooser = false;
		aiChosenWord = null;
		curCharInput = ' ';
		curGuessPositions.clear();
		curRevealedChars.clear();
		curGuessedChars.clear();

	}
	private static char demandUserInput(int displayStringIndex, List<Character> possibleChoices, Scanner inScnr){ //Overload for demanding set of inputs
		for(boolean isInput = false; !isInput;){
			try{
				try{
					displayLine(displayStringIndex);

					String sendString = inScnr.nextLine();
					if(sendString==null||sendString.length()<1){
						throw new UserInputNotFoundException(localization.get(16));
					}
					if(checkFirstChar(possibleChoices, sendString)){
						isInput = true;
						return sendString.charAt(0);
					}

				}
				catch(NullPointerException e){
					throw new UserInputNotFoundException(localization.get(16), e);
				}
				catch(UserInputNotFoundException e){
					throw e;
				}
				catch(InvalidUserInputException e){
					throw new InvalidUserInputException(localization.get(15),e);
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
		return ' ';
	}
	private static String demandUserInput(int displayStringIndex, Scanner inScnr){ //Overload for demanding any input.
		for(boolean isInput = false; !isInput;){
			try{
				try{
					displayLine(displayStringIndex);

					String sendString = inScnr.nextLine();
					if(sendString==null||sendString.length()<1){
						throw new UserInputNotFoundException(localization.get(16));
					}
					isInput = true;
					return sendString;

				}
				catch(NullPointerException e){
					throw new UserInputNotFoundException(localization.get(16), e);
				}
				catch(UserInputNotFoundException e){
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
				throw new RuntimeException("Error in demanding input.", e);
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
		String tempCharInput = "";
		tempCharInput += "'"+curCharInput+"'";

		String tempFinalWord = "";
		tempFinalWord += "'";
		for(Character c: curRevealedChars){
			tempFinalWord += c;
		}
		tempFinalWord += "'";
		//		String tempGuessPos = "";
		//		for(Integer c: curGuessPositions){
		//			tempGuessPos += c + " ";
		//		}
		localizationVarMap.put("$revealWord", tempRevealWord);
		localizationVarMap.put("$guessChars", tempGuessChars );
		localizationVarMap.put("$guessPositions", curGuessPositions );
		localizationVarMap.put("$wordLength", new Integer(curWordLength));
		localizationVarMap.put("$charInput", tempCharInput);
		localizationVarMap.put("$lives", new Integer(curLives));
		localizationVarMap.put("$strikes", new Integer(TOTLIVES-curLives));
		localizationVarMap.put("$TOTLIVES", new Integer(TOTLIVES));
		localizationVarMap.put("$finalWord", tempFinalWord);

	}
	/**
	 * Displays a line taken from the file with the name equivalent to curLanguage+Local.txt
	 * @param inLocalizationIndex
	 */
	public static void displayLine(int inLocalizationIndex){
		if(!localization.get(inLocalizationIndex).contains("$")){
			System.out.println(localization.get(inLocalizationIndex) + " ");
		}
		else{
			Scanner varScnr;
			try{
				varScnr = new Scanner(localization.get(inLocalizationIndex));
				while(varScnr.hasNext()){
					String curWord = varScnr.next();
					if(curWord.startsWith("$")){
						try{
							System.out.print(localizationVarMap.get(curWord) + " ");
						}
						catch(RuntimeException e){
							throw e;//something bad gone wrong
						}
					}
					else{
						System.out.print(curWord + " ");
					}
				}
				varScnr.close();
			}catch(RuntimeException e){
				throw new RuntimeException("Error in displaying a line.", e);
			}

		}

	}
	public static boolean checkFirstChar(List<Character> availChars, String inString) throws InvalidUserInputException {
		char temp = Character.toLowerCase(inString.charAt(0));
		if(availChars.contains(temp)){
			return true;
		}
		else{
			throw new InvalidUserInputException(localization.get(16));
		}

	}
	private boolean checkYN(int localIndex){
		char[] possible = {'y','n'};
		List<Character> posList = new ArrayList<Character>();
		for(char c:possible){
			posList.add(c);
		}
		if(demandUserInput(localIndex,posList,Project3.sysScnr)==possible[0]){
			return true;
		}
		else{
			return false;
		}
	}
	/**
	 * Sets the player and ai based on player input. 
	 * @see Localization code: 0003
	 * 
	 */
	private static boolean checkCG(){
		char[] possible = {'c','g'};
		List<Character> posList = new ArrayList<Character>();
		for(char c:possible){
			posList.add(c);
		}
		if(demandUserInput(3,posList,Project3.sysScnr)==possible[0]){
			aiGuesser = true;
			aiChooser = false;
			aiChosenWord = null;
			return true;//TODO make this set the player to chooser and AI to guesser.
		}
		else{
			aiGuesser = false;
			aiChooser = true;

			return true;//TODO make this set the player to guesser and the AI to chooser.
		}
	}
	public void checkContinue() throws QuitGameException{
		if(checkYN(1)){
			//nothing
		}
		else{
			throw new QuitGameException("User designated termination.");
		}
	}
	public static void displayGameStatus(){

		if(curGuessedChars.size() == 0){
			displayLine(17);
		}
		else{
			displayLine(10);
			System.out.println();
		}
		if(curLives == TOTLIVES){
			displayLine(8);
			System.out.println();
		}
		else{
			displayLine(9);
			System.out.println();
		}
		displayLine(2);
		System.out.println();

	}
}

