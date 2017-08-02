package pjt3;
import java.io.*;
import java.util.*;
public class WordGenerator{

	private ArrayList<String> iKnowWords_IHaveTheBestWords = new ArrayList<String>(); //He has such a way with words.
	private ArrayList<String> words = iKnowWords_IHaveTheBestWords;
	
	private static final String WORDLIST = "words.txt";

	WordGenerator(Random inRandom) throws IOException{
		try{
			seedWords();
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

	public ArrayList<String> getWords(){
		return words;
	}
	public String pickWord(Random randy) throws Exception{
		try{
			String send = words.get(randy.nextInt(words.size()));
			return send;
		}
		catch(Exception e){
			throw new Exception("Exception occured while picking word.", e);
		}
		
	}
	public String pickWord() throws Exception{
		try{
		String send = pickWord(new Random(Game.SEED));
		return send;
		}
		catch(Exception e){
			throw new Exception("Exception occured while picking word.", e);
		}
	}
}
