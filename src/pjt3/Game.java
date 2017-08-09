package pjt3;
import java.io.*;
import java.util.*;


public class Game {



	public static final double TOLERANCE = 0.00001;

	private static final int TOTLIVES = 10;



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
	private static String playerChosenWord = "";//only set after game over, check validity of player inputs.

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

	private static boolean isReed = false;
	private static char failChar = ' ';

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
					String[] curValues = curLine.split("\\^");
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
			//			for(int i = 0; i<cumLetterProb.length;i++){
			//				System.out.print(cumLetterProb[i] + " " + LETTERSOFALPHABET[i] + " ");
			//			}
			//			System.out.println();
			//			System.out.println(chooseAIMove(wordy.getWords()));
			wordy.updateWordVars();
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
	private static void run() throws RuntimeException{
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

			if(curLives == 0&&!aiChooser){
				if(endValidate()){
					//is valid, unknown word
					//System.out.println("check");
				}
				else{
					//endUser is tosser
					//System.out.println("no check");
				}
			}
			else if(curLives == 0&&aiChooser){
				displayLine(32);
				System.out.println();
			}

		}
		catch(InvalidUserInputException e){
			displayLine(30);
			if(endValidate()){
				//is valid, unknown word
				//System.out.println("check");
			}
			else{
				//endUser is tosser
				//System.out.println("no check");
			}


		}
		catch(RuntimeException e){
			throw new RuntimeException("Exception thrown in run().", e);
		}

	}
	private static boolean endValidate() {
		playerChosenWord = demandUserInput(29,Project3.sysScnr).toUpperCase().replaceAll("\\s", "");
//		System.out.println(playerChosenWord);
		if(playerChosenWord.length()!= curWordLength){

			displayLine(28);
			return false;
		}
		for(Character c:curGuessedChars){
			if(Character.isLowerCase(c)&&(Arrays.binarySearch(playerChosenWord.toCharArray(),Character.toUpperCase(c))>=0)){
				failChar = c;
				updateVarMap();
				displayLine(27);
				return false;
			}
			else if(Character.isUpperCase(c)&&(Arrays.binarySearch(playerChosenWord.toCharArray(),Character.toUpperCase(c))<0)){	
				failChar = c;
				updateVarMap();
				displayLine(27);
				return false;
			}
		}
		return true;
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
						String tempS = demandUserInput(18,Project3.sysScnr).replaceAll("\\s", "");
						curWordLength = Integer.parseInt(tempS);
						if(curWordLength>29||curWordLength<1){
							throw new InvalidUserInputException(localization.get(20));
						}
						wordy.cullWordList(curWordLength);
						isValid = true;
					}
					catch(NumberFormatException e){
						System.out.println(localization.get(19));
						isValid = false;
					}
					catch(InvalidUserInputException e){
						System.out.println(e.getMessage());
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
	private static void turn() throws RuntimeException, GameOverException, InvalidUserInputException{
		try{
			//updateVarMap();
			if(curLives == 0){
				throw new GameOverException("14");
			}
			//System.out.println(wordy.getWords());
			System.out.println();
			displayGameStatus();//Display past turn stats

			if(wordy.getWords().size()<=0){
				throw new InvalidUserInputException("Inconsistent input or unknown word.");
			}

			if(correctGuesses == curWordLength){
				throw new GameOverException("12");
			}
			if(!aiGuesser){
				for(boolean isValidInput = false;!isValidInput;){
					try{

						curCharInput = Character.toUpperCase(demandUserInput(5,Project3.sysScnr).charAt(0));//Request new player input
						if(Arrays.binarySearch(WordGenerator.LETTERSOFALPHABET,Character.toLowerCase(curCharInput))<0){
							throw new InvalidUserInputException(localization.get(31));
						}
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
						isValidInput = true;
					}
					catch(InvalidUserInputException e){
						System.out.println(e.getMessage());
					}
				}
			}
			else{

				for(boolean isValid = false; !isValid;){
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
				for(boolean isValidInput = false;!isValidInput;){
					try{
						isReed  = true;
						String inString = demandUserInput(11,Project3.sysScnr);
						isReed = false;
						int curPos = -1;
						Scanner tempScan = new Scanner(inString);
						String tempPosList = "";
						while(tempScan.hasNext()){
							tempPosList+=tempScan.next();
						}
						int tempMax = -1;
						for(int i = 0;i<tempPosList.length();i++){
							if((Integer.parseInt(Character.toString(tempPosList.charAt(i)))==0)
									&&(Integer.parseInt(Character.toString(tempPosList.charAt(i)))==tempPosList.length()-1)
									&&(tempPosList.length()!=1)){
								throw new InvalidUserInputException(localization.get(23));
							}
							if(Integer.parseInt(Character.toString(tempPosList.charAt(i)))<tempMax){
								throw new InvalidUserInputException(localization.get(22));
							}

							tempMax = Integer.parseInt(Character.toString(tempPosList.charAt(i)));
						}
						tempScan.close();
						for(int b = 0;b<tempPosList.length();b++){

							curPos = Integer.parseInt(Character.toString(tempPosList.charAt(b)));

							if(curPos<0||curPos>29){
								throw new InvalidUserInputException(localization.get(24));
							}

							for(Integer i:curGuessPositions){
								if(curPos<i){
									throw new InvalidUserInputException(localization.get(22));
								}
							}
							if(curPos != 0){

								correctGuesses++;
								if(!(curGuessedChars.contains(Character.toUpperCase(curCharInput)))){
									curGuessedChars.add(Character.toUpperCase(curCharInput));
								}
								curRevealedChars.remove(curPos-1);
								curRevealedChars.add(curPos-1, Character.toUpperCase(curCharInput));
								curGuessPositions.add(new Integer(curPos));

								wordy.cullWordList(curCharInput, curGuessPositions);

							}
							else{


								curGuessedChars.add(Character.toLowerCase(curCharInput));
								curGuessPositions.add(new Integer(curPos));
								strike();
								wordy.cullWordList(curCharInput);
							}
						}

						curPos = 0;
						isValidInput = true;
					}
					catch(NumberFormatException e){
						System.out.println(localization.get(26));
					}
					catch(InvalidUserInputException e){
						System.out.println(e.getMessage());
					}
				}
			}
			//Update base values based on input
			//Update derived values based on input

			numTurns++;
		}
		catch(RuntimeException e){
			throw new RuntimeException("Exception thrown in turn().",e);
		}
	}

	private static String chooseAIMove(ArrayList<String> words) {

		double probGuess = Project3.randy.nextDouble()*100;
		double former=0;
		double later=0;
		//		int curIndex=0;
		//		System.out.println(probGuess);
		for(int i = 0; i<WordGenerator.letterProb.length;i++){
			if(i!=WordGenerator.letterProb.length-1){
				later = WordGenerator.cumLetterProb[i+1];
			}
			else{
				later = 100;
			}
			former = WordGenerator.cumLetterProb[i];
			//			curIndex = i;
			if(probGuess>former&&probGuess<later){
				//				System.out.println("Returning " + LETTERSOFALPHABET[i]);//test
				return Character.toString(WordGenerator.LETTERSOFALPHABET[i]);
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
		aiChosenWord = "";
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
				//System.out.println(e.getMessage());
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
				//System.out.println(e.getMessage());
				if(isReed){
					System.out.println(localization.get(21));
				}
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
		String tempAIWord = "";
		tempAIWord+=aiChosenWord;
		localizationVarMap.put("$revealWord", tempRevealWord);
		localizationVarMap.put("$guessChars", tempGuessChars );
		localizationVarMap.put("$guessPositions", curGuessPositions );
		localizationVarMap.put("$wordLength", new Integer(curWordLength));
		localizationVarMap.put("$charInput", tempCharInput);
		localizationVarMap.put("$lives", new Integer(curLives));
		localizationVarMap.put("$strikes", new Integer(TOTLIVES-curLives));
		localizationVarMap.put("$totLives", new Integer(TOTLIVES));
		localizationVarMap.put("$finalWord", tempFinalWord);
		localizationVarMap.put("$inconsistGuess", new Character(failChar));
		localizationVarMap.put("$aiWord", new String(tempAIWord));

	}
	/**
	 * Displays a line taken from the file with the name equivalent to curLanguage+Local.txt
	 * @param inLocalizationIndex
	 */
	public static void displayLine(int inLocalizationIndex){
		if(!localization.get(inLocalizationIndex).contains("$")){
			System.out.print(localization.get(inLocalizationIndex) + " ");
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
		char temp = Character.toLowerCase(demandUserInput(3,posList,Project3.sysScnr));
		if(temp==possible[0]){
			aiGuesser = true;
			aiChooser = false;
			aiChosenWord = "";
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



}

