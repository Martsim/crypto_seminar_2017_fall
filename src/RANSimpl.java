import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
	

public class RANSimpl {
	
	/*
	 * The following two map characters to ranges
	 */
	public static Map<String, Integer> letter_to_num = new HashMap<>();
	public static Map<Integer, String> num_to_letter = new HashMap<>();
	
	public static Map<Integer, Integer> ls = new HashMap<>(); /* Length of symbols range*/
	public static Map<Integer, Integer> bs = new HashMap<>(); /* Beginning of symbols range*/
	public static int NROFELEMENTS = 0; /*nr of unique elements in the text*/
	public static int M = 10; /* the base for our numeral system */	
	private static Logger log = Logger.getLogger("rANS");
	
	public static void main(String[] args) {
		demo_dickens(10000);
	}
	
	/**
	 *  Returns symbol from x
	 * @param x
	 * @return
	 */
	public static BigInteger s(BigInteger x) {
		BigInteger MM = new BigInteger(Integer.toString(M));
		return help_for_s(x.remainder(MM));
	}
	    
	/**
	 *  Helper function for cleaner code. Used by s(x)
	 */
	public static BigInteger help_for_s (BigInteger x) {
		int that_s = -1;
		for (int s = 0; s < NROFELEMENTS; s++) {
			int thissum = 0;
			for (int i = 0; i < s+1; i++) {
				thissum = thissum + ls.get(i).intValue(); 
			}   
			int comp = new BigInteger(Integer.toString(thissum)).compareTo(x);
			if (comp == 1) {
				that_s = s;
				break; // When iterating from the smallest element, there is no need to keep checking after finding first.
			}
		}
		return new BigInteger(Integer.toString(that_s));
	}
	
	
	/**Takes a symbol s and encodes it to the state x.
	 * 
	 * @return
	 */
	public static BigInteger encode(int s, BigInteger x) {
		BigInteger lsVal = new BigInteger(ls.get(s).toString());
		BigInteger bsVal = new BigInteger(bs.get(s).toString());
		BigInteger MM = new BigInteger(Integer.toString(M));
		
		return x.divide(lsVal).multiply(MM).add(bsVal).add(x.remainder(lsVal));
	}
	
	/**
	 * decodes symbol s and state x_s from given state x.
	 * @return
	 */
	public static BigInteger[] decode(BigInteger x) {
		BigInteger current_s = s(x);
		BigInteger[] resp = new BigInteger[2];
		resp[0] = current_s;
		BigInteger lsVal = new BigInteger(ls.get(current_s.intValue()).toString()); 
		BigInteger MM = new BigInteger(Integer.toString(M));
		BigInteger bsVal = new BigInteger(bs.get(current_s.intValue()).toString());
		BigInteger new_x = lsVal.multiply(x.divide(MM)).add(x.remainder(MM)).subtract(bsVal); 
		resp[1] = new_x;
		return resp;
	}
	
	/**Prepares the ranges based on the training text.
	 * 
	 * @return
	 */
	public static void build_dict(String text) {
	    letter_to_num = new HashMap<>();
	    num_to_letter = new HashMap<>();
	    NROFELEMENTS = 0;
	    ls = new HashMap<>();
	    bs = new HashMap<>();
	    
	    for (int ss = 0; ss < text.length(); ss++) {
	    	String symbol = Character.toString(text.charAt(ss));
	    
	    	int nr;
	        if (!letter_to_num.containsKey(symbol)) {
	            nr = NROFELEMENTS;
	            NROFELEMENTS = NROFELEMENTS + 1;
	            letter_to_num.put(symbol, nr);
	            num_to_letter.put(nr, symbol);
	            ls.put(nr, 1);
	        } else {
	            nr = letter_to_num.get(symbol);
	            ls.put(nr,ls.get(nr) + 1);
	        }
	    }
	    bs.put(0, 0);
	    M = 0;
	    for (int i = 1; i < NROFELEMENTS; i++) {//Check that all elements have beeb summed
	        bs.put(i,bs.get(i-1) + ls.get(i-1));
	        M += ls.get(i-1); 
	    }
	    
	    M += ls.get(NROFELEMENTS - 1);
	    assert(M == text.length());
	}
	
	public static BigInteger encode_text_with_dict(String text) {
		return encode_text_with_dict(text, new BigInteger("0"));
	}
	
	/**
	 * Initializes the range for this specific text and then encodes the text.
	 * @return
	 */
	public static BigInteger encode_text_with_dict(String text, BigInteger x) {
	    build_dict(text);

	    BigInteger working_x = x;
	    for (int ss = 0; ss < text.length(); ss++) {
	    	String symbol = Character.toString(text.charAt(ss));
	    
	    	BigInteger new_x = encode(letter_to_num.get(symbol), working_x);
	        working_x = new_x;
	    }
	    return working_x;
	}
	
	/**
	 * Encodes the text and checks if decoded text equals original text.
	 */
	public static BigInteger encode_text_with_dict_check(String text) {
		BigInteger encoded = encode_text_with_dict(text, new BigInteger("0"));
		String decodedText = decode_text(encoded, text.length());
		if (!text.equals(decodedText)) {
	    		log.warning("Decoding test failed");
	    		throw new RuntimeException("The decoded text does not equal the original text!!!");
	    	}
		return encoded;
	}
	/**
	 * # Decodes symbols until x==until
	 * @return
	 */
	public static String decode_text(BigInteger x, int nr_of_symbols) {
	    BigInteger working_x = x;
	    StringBuilder reverse_text = new StringBuilder();
	    int counter = 0;
	    while (counter < nr_of_symbols) {
	    	BigInteger[] new_pair = decode(working_x);
	    	BigInteger symbol = new_pair[0];
	    	BigInteger new_x = new_pair[1]; 
		    
		reverse_text.append(num_to_letter.get(symbol.intValue()));
	        working_x = new_x;
	        counter += 1;
	    }
	    
	    return reverse_text.reverse().toString();
	}
	
	
	/** Demo based on Dickens. Reads 200 characters, initializes range for this specific text and encodes it.
	 */
	public static void demo_dickens(int blockSize) {
		log.info(String.format("Running demo with block size %s on text by C. Dickens.", blockSize));
		String txt = "";    
		try ( BufferedReader br =
				new BufferedReader( new InputStreamReader(new FileInputStream(new File("dickens")),"UTF-8"));)
		{
			StringBuilder sb = new StringBuilder();
			int TO_READ = blockSize;
			char[] buffer = new char[100];
			int read = 0;
			int read_before = 0;
			while(read_before < TO_READ ) {
				read = br.read(buffer);
				if (read == -1) {
					break;
				}
				read_before += read;
				for (int i = 0; i < read; i++) {
					sb.append(buffer[i]);
				}
			}
			txt = sb.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    
	    BigInteger xx = encode_text_with_dict(txt, new BigInteger("0"));
	    log.info(String.format("Encoded text of size %s bytes to %s bytes. The compression ratio is: %s",txt.length(),Math.ceil(xx.bitCount() / 8), (double)Math.ceil(xx.bitCount() / 8) / (double)txt.length()));

	    System.out.println("Decoding");
	    
	    String dd = decode_text(xx, blockSize);
	    
	    assert(txt.equals(dd));//Checks that the decoded text is the same as the original
	    if (!txt.equals(dd)) {
	    	log.info("Decoding test failed");
	    	throw new RuntimeException("The decoded text does not equal the original text!!!");
	    }
	    log.info("Decoding test passed");
	    
	}
}
