package pjt3;
import java.io.*;
import java.util.*;
public class WordGenerator{

	private ArrayList<String> iKnowWords_IHaveTheBestWords = new ArrayList<String>(); //He has such a way with words.
	private ArrayList<String> words = iKnowWords_IHaveTheBestWords;

	private static final String WORDLIST = "words.txt";

	private Random wordRand = null;

	WordGenerator(Random inRandom) throws IOException{
		try{
			seedWords();
			wordRand = inRandom;
		}
		catch(IOException e){
			throw new IOException("Failed to construct WordGenerator.", e);
		}

	}
	WordGenerator() throws IOException{
		this(new Random(Game.SEED));
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
				words.add(in.next());
			}

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
	
	public void cullWordList(/*Probably some args here */){
		//TODO cull word list based on args.
	}
	public String chooseWord() {
		return words.get(wordRand.nextInt(words.size()));
	}
}
