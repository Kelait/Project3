package pjt3;
import java.io.*;
import java.util.*;


public class Game {



	public static final double TOLERANCE = 0.00001;

	private static final int TOTLIVES = 10;

	private static final double[] LETTERPROB = {	8.167, 1.492, 2.782, 4.253, 
			12.702, 2.228,2.015,6.094,6.966,0.153,
			0.770,4.025,2.406,6.749,7.507,1.929,0.095,
			5.987,6.327,9.056,2.758,0.978,2.630,0.150,
			1.974,0.074};

	private static final char[] LETTERSOFALPHABET = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
			'i', 'j','k','l','m','n','o',
			'p','q','r','s','t','u','v',
			'w','x','y','z'};
	private static double[] cumLetterProb = new double[LETTERPROB.length];

	//private static HashMap<Double,Character> probabiltyMap = new HashMap<Double>


	private static HashMap<Integer, String> localization = new HashMap<Integer,String>();
	private static HashMap<String, Object> localizationVarMap = new HashMap<String, Object>();


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
	private static boolean isStrike;

	private static String aiChosenWord;

	private static int numTurns;	
	private static int correctGuesses;

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
				if(curLine.isEmpty()||curLine.startsWith("@")||curLine.trim().isEmpty()){
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
			wordy = new WordGenerator(Project3.randy);
			seedCumLetterProb();
			//			for(int i = 0; i<cumLetterProb.length;i++){
			//				System.out.print(cumLetterProb[i] + " " + LETTERSOFALPHABET[i] + " ");
			//			}
			//			System.out.println();
			//			System.out.println(chooseAIMove(wordy.getWords()));
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

				turn();
				updateVarMap();//ALWAYS DO 
				curCharInput = ' ';
			}
		}
		catch(GameOverException e){
			System.out.println();
			displayLine(Integer.parseInt(e.getMessage()));
			System.out.println();
		}

	}
	private static void initGame(){
		try{
			curLives = TOTLIVES;

			checkCG();
			if(aiChooser){
				aiChosenWord = wordy.chooseWord();
				curWordLength = aiChosenWord.length();
				for(int i = 0; i<curWordLength;i++){
					curRevealedChars.add('*');
				}
				//System.out.println(aiChosenWord);
			}
			else{
				aiChosenWord = null;
				for(boolean isValid = false;!isValid;){
					try{
						curWordLength = Integer.parseInt(demandUserInput(18,Project3.sysScnr));
						isValid = true;
					}
					catch(NumberFormatException e){
						System.out.println(localization.get(16));
						isValid = false;
					}
				}
				for(int i = 0; i<curWordLength;i++){
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
		if(curLives == 0){
			throw new GameOverException("14");
		}
		//System.out.println(correctGuesses);
		System.out.println();
		displayGameStatus();//Display past turn stats


		if(correctGuesses == curWordLength){
			throw new GameOverException("12");
		}
		if(!aiGuesser){
			curCharInput = Character.toUpperCase(demandUserInput(5,Project3.sysScnr).charAt(0));//Request new player input

			if(aiChosenWord.indexOf(curCharInput)==-1){
				curGuessedChars.add(Character.toLowerCase(curCharInput));
				strike();
			}
			else{
				for(int i = 0; i<curWordLength;i++){
					if(aiChosenWord.charAt(i)==curCharInput){
						if(!curGuessedChars.contains(Character.toUpperCase(curCharInput))){
							correctGuesses++;
						}
						curRevealedChars.remove(i);
						curRevealedChars.add(i, curCharInput);
						//curGuessedChars.add(curCharInput);
						curGuessPositions.add((Integer)i);

					}
				}
				curGuessedChars.add(Character.toUpperCase(curCharInput));
			}
		}
		else{
			for(boolean isValid = false; !isValid;){//TODO lots of input validation.
				curCharInput = chooseAIMove(wordy.getWords()).charAt(0);

				if(curGuessedChars.contains(Character.toUpperCase(curCharInput))||curGuessedChars.contains(Character.toLowerCase(curCharInput))){
					isValid = false;
				}
				else{
					isValid = true;
				}
			}
			updateVarMap();
			displayLine(10);
			System.out.println();
			
			String inString = demandUserInput(11,Project3.sysScnr);//TODO input valid try catch.
			int curPos;
			Scanner tempScan = new Scanner(inString);
			while(tempScan.hasNext()){
				curPos = Integer.parseInt(tempScan.next());
				if(curPos != 0){
					correctGuesses++;
					curGuessedChars.add(Character.toUpperCase(curCharInput));
					curRevealedChars.remove(curPos-1);
					curRevealedChars.add(curPos-1, Character.toUpperCase(curCharInput));
				}
				else{
					curGuessedChars.add(Character.toLowerCase(curCharInput));
					strike();
				}
			}
			curPos = 0;
		}
		//Update base values based on input
		//Update derived values based on input

		numTurns++;

	}

	private static String chooseAIMove(ArrayList<String> words) {

		double probGuess = Project3.randy.nextDouble()*100;
		double former=0;
		double later=0;
		//		int curIndex=0;
		//		System.out.println(probGuess);
		for(int i = 0; i<LETTERPROB.length;i++){
			if(i!=LETTERPROB.length-1){
				later = cumLetterProb[i+1];
			}
			else{
				later = 100;
			}
			former = cumLetterProb[i];
			//			curIndex = i;
			if(probGuess>former&&probGuess<later){
				//				System.out.println("Returning " + LETTERSOFALPHABET[i]);//test
				return Character.toString(LETTERSOFALPHABET[i]);
			}
		}
		return Character.toString('e');


	}
	private static void strike() {
		curLives--;
		isStrike = true;

	}
	public void reset() {

		curLives = TOTLIVES;
		correctGuesses = 0;
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
		localizationVarMap.put("$totLives", new Integer(TOTLIVES));
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
			return true;
		}
		else{
			aiGuesser = false;
			aiChooser = true;

			return true;
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
			if(!aiChooser){
				if(isStrike){
					displayLine(13);
					System.out.println();
					isStrike = false;
				}
				else{
					displayLine(10);
					System.out.println();
				}
			}
			else{
				if(isStrike){
					displayLine(13);
					System.out.println();
					isStrike = false;
				}
				else{
					displayLine(7);
					System.out.println();
				}

			}
			displayLine(6);
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
		System.out.println();

		curGuessPositions.clear();
	}

	private static void seedCumLetterProb(){
		double curSum = 0;
		for(int i = 0; i< LETTERPROB.length;i++){
			cumLetterProb[i] = curSum;
			curSum += LETTERPROB[i];
		}
	}


}

