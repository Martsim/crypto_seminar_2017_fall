import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.logging.Logger;

/**
 * Used for comparing rANS with Huffman coding
 * 
 *  rANS is the rangedANS example code
 * Huffman is the Huffman coding example which was based on
 * https://gist.github.com/mreid/fdf6353ec39d050e972b
 * Also given in the report
 */
public class CompareTwo {

	public static void main(String[] args) {
		Logger log = Logger.getLogger("Main");
		int inVal = 2;
		for (int r = 3; r < 11; r++) {
			log.info(String.format("Running with block size %s", Math.pow(inVal, r)));
			CompareTwo ct = new CompareTwo();
			ct.runTests((int)Math.pow(inVal, r), "dickens");
		}
		
	}

	/**
	 * Container for results of tests
	 */
	public class ComparisonValues {
		public int rANS_bigger = 0;
		public int HC_bigger = 0;
		public int equal = 0;
		public int nr_tests = 0;
		public int sum_diff = 0;
		public double avg_diff = 0.0;
		public double sum_HC = 0.0;
		public double sum_rANS = 0.0;
		public double avg_HC = 0.0;
		public double avg_rANS = 0.0;
	
		public ComparisonValues() {
			
		}
		public void logStatistics() {
			Logger log = Logger.getLogger("Stats");
			log.info("rANS coded text was_bigger than Huffman encoded text " + rANS_bigger + " number of times");
			log.info("Huffman Coding was bigger than rANS encoded text " + HC_bigger + " number of times");
			log.info("Encoded texts were equal " + equal + " number of times");
			log.info("The number of tests run: " + nr_tests);

			// Huffman encoded text size in bytes minus rANS encoded text size in bytes
			log.info("The average_difference in bytes between Huffman encoded");
			log.info(" and rANS encoded text: " + avg_diff);

			log.info("The average compression ratio for rANS " + avg_rANS);
			log.info("The average compression ratio for HC " + avg_HC);
		}
		
	}
	
	/** Runs compression tests on input text with given block size
	 * and calculates the average compression ratios for both algorithms.
	 */
	public void runTests(int blockSize, String fileName) {
		ComparisonValues cv = new ComparisonValues();
		String txt = "";
	    
	    try ( BufferedReader br =
				new BufferedReader( new InputStreamReader(new FileInputStream(new File(fileName)),"UTF-8"));)
		{
	    	boolean canTest = true;
	    	while (canTest) {
				StringBuilder sb = new StringBuilder();
				int TO_READ = blockSize;
				char[] buffer = new char[Math.min(blockSize, 64)];
				int read = 0;
				int read_before = 0;
				
				while(read_before < TO_READ ) {
					read = br.read(buffer);
					
					if (read == -1) {
						canTest = false;
						break;
					}
					
					read_before += read;
					
					for (int i = 0; i < read; i++) {
						sb.append(buffer[i]);
					}
				}
				
				txt = sb.toString();
				//Run both tests here
				try {
					compare_two(txt, cv);
				} catch (NullPointerException e) {
					continue;
				}
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    cv.avg_diff = ((double)cv.sum_diff)/(double)cv.nr_tests;
		cv.avg_rANS = Math.round((cv.sum_rANS/(double)cv.nr_tests) * 100000) / 1000;
		cv.avg_HC = Math.round((cv.sum_HC/(double)cv.nr_tests) * 100000) / 1000;
	    cv.logStatistics();
	}
	
	/** Compares Huffman coding with ranged ANS.
	* Prints results that show the compressed text size for both algorithms and their difference.
	*/
	static void compare_two(String text, ComparisonValues cv) throws NullPointerException {	    
	    // Runs both encoders
		BigInteger xx1 = RANSimpl.encode_text_with_dict_check(text);
	    String xx = new Huffman().hcEncode(text); // Possible NullPointer position
	    
	    // If both have passed, calculates results
	    cv.nr_tests += 1;
	    
	    double rans_baits = Math.ceil( (double)xx1.bitCount() / 8);	    
	    double HC_baits = Math.ceil(xx.length() / 8);
	    double diff = HC_baits - rans_baits;
	     
	    if (HC_baits > rans_baits) {
	        cv.HC_bigger += 1;
	    } else if (HC_baits < rans_baits) {
	        cv.rANS_bigger += 1;
	    } else {
	        cv.equal += 1;
	    }
	    cv.sum_diff += diff;
	    
	    double addHc = HC_baits / (double)text.length();
	    cv.sum_HC += addHc;
	    
	    double add = rans_baits / (double)text.length();
	    cv.sum_rANS += add;
	}
}

