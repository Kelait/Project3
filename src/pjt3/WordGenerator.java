package pjt3;
import java.io.*;
import java.util.*;

public class WordGenerator{



	public static final char[] LETTERSOFALPHABET = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
			'i', 'j','k','l','m','n','o','p','q',
			'r','s','t','u','v','w','x','y','z'};

	//	public static double[] letterProb = {	8.167, 1.492, 2.782, 4.253, 
	//			12.702, 2.228,2.015,6.094,6.966,0.153,
	//			0.770,4.025,2.406,6.749,7.507,1.929,0.095,
	//			5.987,6.327,9.056,2.758,0.978,2.630,0.150,
	//			1.974,0.074};
	public static double[] letterProb = new double[LETTERSOFALPHABET.length];
	public static double[] cumLetterProb = new double[LETTERSOFALPHABET.length];
	public static int[] letCount = new int[LETTERSOFALPHABET.length];
	private static int totLetters;

	private ArrayList<String> iKnowWords_IHaveTheBestWords = new ArrayList<String>(); //He has such a way with words.
	private ArrayList<String> words;

	public static final String WORDLIST = "words.txt";

	private Random wordRand = null;

	WordGenerator(Random inRandom) throws IOException{
		try{
			seedWords();
			wordRand = inRandom;
			this.updateWordVars();
		}
		catch(IOException e){
			throw new IOException("Failed to construct WordGenerator.", e);
		}

	}
	WordGenerator() throws IOException{
		this(new Random(Project3.SEED));
	}
	/**
	 * Generates list of words from file given by the file at directory defined by WORDLIST.
	 * @see String final WORDLIST
	 * @throws IOException
	 */
	private void seedWords() throws IOException{
		Scanner in = null;
		try{
			in = new Scanner(new FileReader(WORDLIST));
			while(in.hasNext()){
				iKnowWords_IHaveTheBestWords.add(in.next());
			}
			words = new ArrayList<String>(iKnowWords_IHaveTheBestWords);
			
		}
		catch(FileNotFoundException e){
			throw new IOException("Failed to seed words.",e);
		}
		finally{
			try{
				in.close();
			}
			catch(IllegalStateException e){
				throw new IOException("Exception occured while seeding words.",e);
			}

		}
	}

	public String pickWord(){
		try{
			String send = words.get(wordRand.nextInt(words.size()));
			return send;
		}
		catch(IndexOutOfBoundsException e){
			throw new IndexOutOfBoundsException("Exception occured while picking word.");
		}
		catch(IllegalArgumentException e){
			throw new IllegalArgumentException("Exception occured while picking word.", e);
		}
	}
	public ArrayList<String> getWords(){
		return words;
	}

	public void cullWordList(int wordLength){//for initial culling
		for(int s = 0; s<words.size();s++){
			if(words.get(s).length()!=wordLength){
//				System.out.println("Removing "+ words.get(s));
				words.remove(s);
				s--;
			}
		}
		this.updateWordVars();
	}
	public void cullWordList(char charGuess, ArrayList<Integer> positions){//for normal usage
		try{

			for(int s = 0; s<words.size();s++){
				if(words.get(s).indexOf(charGuess)>=0){
					for(int i = 0; i<positions.size();i++){
						if(words.get(s).charAt(positions.get(i)-1)!=charGuess){
//							System.out.println("Removing "+ words.get(s));
							words.remove(s);
							s--;
						}
					}
				}
				else{
//					System.out.println("Removing "+ words.get(s));
					words.remove(s);
					s--;
				}
			}

			this.updateWordVars();
		}
		catch(IndexOutOfBoundsException e){
			throw new IndexOutOfBoundsException("Exception thrown while culling word list.");
		}
	}
	public void cullWordList(char charGuess){//for strikes
		for(int s = 0; s<words.size();s++){
			if(words.get(s).indexOf(charGuess)>=0){
//				System.out.println("Removing "+ words.get(s));
				words.remove(s);
				s--;
			}
		}
		this.updateWordVars();
	}
	public String chooseWord() {
		return words.get(wordRand.nextInt(words.size())).toUpperCase();
	}

//	public static void seedCumLetterProb(){
//		double curSum = 0;
//		for(int i = 0; i< LETTERSOFALPHABET.length;i++){
//			cumLetterProb[i] = curSum;
//			curSum += letterProb[i];
//		}
//	}
	public void updateWordVars(){
//		System.out.println(words.size());
		totLetters = 0;
		letterProb = new double[LETTERSOFALPHABET.length];
		cumLetterProb = new double[LETTERSOFALPHABET.length];
		letCount = new int[LETTERSOFALPHABET.length];	
		for(String s:words){
			int curLet;
			for(int i = 0; i<s.length();i++){
				totLetters++;
				curLet = Arrays.binarySearch(LETTERSOFALPHABET, s.charAt(i));
				letCount[curLet]++;
			}
		}
		for(int i = 0; i<LETTERSOFALPHABET.length;i++){
			letterProb[i] = ((double)letCount[i]/(double)totLetters)*100;
			//System.out.println(LETTERSOFALPHABET[i]+ ": " +letterProb[i]);
		}
		double curSum = 0;
		for(int i = 0; i< LETTERSOFALPHABET.length;i++){
			cumLetterProb[i] = curSum;
			curSum += letterProb[i];
		}
	}
	public ArrayList<String> getiKnowWords_IHaveTheBestWords() {
		return iKnowWords_IHaveTheBestWords;
	}
}
