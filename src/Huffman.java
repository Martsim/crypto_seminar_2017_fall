import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;


/**
 * Example Huffman coding implementation
 * Distributions are represented as dictionaries of { 'symbol': probability }
 * Codes are dictionaries too: { 'symbol': 'codeword' }
 */

public class Huffman {
	
	public Map<String, String> huffmanTree;
	
	public static void main(String[] args) {
		System.out.println(new Huffman().hcEncode("aeaeouuiiiuiui"));
	}

	
	public Huffman() {
		
	}
	
	public String hcEncode(String text) {
	    Map<String, Double> prob_dist = createProbDist(text);
	    
	    huffmanTree = huffman(prob_dist);
	    
	    String code_to = "";
	    for (int i = 0; i < text.length(); i++) {
	    	String s = Character.toString(text.charAt(i));
	    	code_to += huffmanTree.get(s);
	    }
	    
	    if (!text.equals(decode(code_to))) {
	    	throw new RuntimeException("Decoded string mismatch!!");
	    }
	    
	    return code_to;
	}
	
	public static Map<String, Double> createProbDist(String text) {
	    Map<String, Double> prob_dist = new HashMap<>();
	    Map<String, Integer> counts = new HashMap<>();
	    int total = 0;
	    for (int i = 0; i < text.length(); i++) {
	    	String s = Character.toString(text.charAt(i));
	    
	        if (counts.containsKey(s)) {
	            counts.put(s, counts.get(s) + 1);
	        } else {
	            counts.put(s, 1);
	        }
	        total += 1;
	    }
	    
	    for (String k : counts.keySet()) {
	        prob_dist.put(k, (double)Math.round(((double)(counts.get(k)) / (double)(total))*100000) /100000);
	    }
	    
	    double summer = 0.0;
	    summer = sumVals(prob_dist.values());
	    if (summer != 1.0) {
	        double diff = 1.0 - summer;
	        String use_key = prob_dist.keySet().iterator().next();
	        prob_dist.put(use_key, ((double)Math.round((prob_dist.get(use_key) + diff) * 10000)/10000));
	    }
	
	    return prob_dist;
	}
	
	/*Return pair of symbols from distribution p with lowest probabilities.*/
	public static double sumVals(Collection<Double> values) {
		double summer = 0.0;
		for (Double val : values) {
			summer += val.doubleValue();
		}
		return summer;
	}

	public static String[] lowestProbPair(Map<String, Double> p) {
	    String[] pair = new String[2];
	    assert(p.keySet().size() >= 2); // Ensure there are at least 2 symbols in the dist.
	    
	    Map<String, Double> treeMap = sortByValue(p); 
	    Iterator<Entry<String, Double>> it = treeMap.entrySet().iterator();
	    if(it.hasNext()) {
	    	pair[0] = it.next().getKey();
	    }
	    if(it.hasNext()) {
	    	pair[1] = it.next().getKey();
	    }		
	    return pair;
	}

//pretty print a map
public static <K, V> void printMap(Map<K, V> map) {
    for (Map.Entry<K, V> entry : map.entrySet()) {
        System.out.println("Key : " + entry.getKey()
			+ " Value : " + entry.getValue());
    }
}
	
	/**
	 * Return a Huffman code for an ensemble with distribution p.
	 * */
	public static Map<String, String> huffman( Map<String, Double> p) {
	    // Base case of only two symbols, assign 0 or 1 arbitrarily
	    if (p.keySet().size() == 2) {
	    	HashMap<String, String> hm = new HashMap<>();
	    	String[] keys = new String[2];
	    	p.keySet().toArray(keys);
	    	hm.put(keys[0], "0");
	    	hm.put(keys[1], "1");
	    	return hm;
	    }
	    
	    // Create a new distribution by merging lowest prob. pair
	    Map<String, Double> p_prime = new HashMap<>(p);
	    String[] lowestPair = lowestProbPair(p);
	    Double p1 = p_prime.remove(lowestPair[0]);
	    Double p2 = p_prime.remove(lowestPair[1]);
	    
	    String united = lowestPair[0] + lowestPair[1];
	    
	    p_prime.put(united, p1 + p2);
	    

	    // Recurse and construct code on new distribution
	    Map<String, String> c = huffman(p_prime);
	    
	    String ca1a2 = c.remove(united);
	    c.put(lowestPair[0], ca1a2 + '0');
	    c.put(lowestPair[1], ca1a2 + '1');

	    return c;
	}
	
	/**
	 * Taken from https://www.mkyong.com/java/how-to-sort-a-map-in-java/
	 * and modified for <String, Double>
	 */
	private static Map<String, Double> sortByValue(Map<String, Double> p) {

        // 1. Convert Map to List of Map
        List<Map.Entry<String, Double>> list =
                new LinkedList<Map.Entry<String, Double>>(p.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
            public int compare(Map.Entry<String, Double> o1,
                               Map.Entry<String, Double> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        for (Map.Entry<String, Double> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
	
	public String decode(String encoded) {
		StringBuilder decoded = new StringBuilder("");
		
		int c = 0;
		StringBuilder reading = new StringBuilder();
		while (c < encoded.length()) {
			reading.append(encoded.charAt(c++));
			String currentRead = reading.toString();
			if (huffmanTree.containsValue(currentRead )) {
				// Find the key which gave the following bitstring
				List<Entry<String, String>> goodValues = huffmanTree
					    .entrySet()
					    .stream()
					    .filter(x -> x.getValue().equals(currentRead ))
					    .collect(Collectors.toCollection(ArrayList::new));
				if (goodValues.size() > 1 || goodValues.size() == 0) {
					throw new RuntimeException("Problem when decoding HC");
				}
				
				decoded.append(goodValues.get(0).getKey());
				reading = new StringBuilder();
			}
			
		}
		
		return decoded.toString();
	}
	
}
