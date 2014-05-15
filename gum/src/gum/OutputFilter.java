package gum;

import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Vector;

public class OutputFilter {

	private static String defaultANSIFooter;
	
	private static HashMap<String, String> utilReplaceMap = new HashMap<String, String>();
	private static Vector<HashMap<String,String>> descriptionVector = new Vector<HashMap<String, String>>();
	
	public OutputFilter(){
		initialize();
	}
	
	public static void initialize(){
		
		utilReplaceMap.put("SANE", "\u001B[0m");
		utilReplaceMap.put("BLACK","\u001B[30m");
		utilReplaceMap.put("RED", "\u001B[31m");
		utilReplaceMap.put("GREEN", "\u001B[32m");
		utilReplaceMap.put("YELLOW", "\u001B[33m");
		utilReplaceMap.put("BLUE", "\u001B[34m");
		utilReplaceMap.put("MAGENTA", "\u001B[35m");
		utilReplaceMap.put("CYAN", "\u001B[36m");
		utilReplaceMap.put("WHITE", "\u001B[37m");
		utilReplaceMap.put("B_BLACK", "\u001B[40m");
		utilReplaceMap.put("B_RED", "\u001B[41m");
		utilReplaceMap.put("B_GREEN", "\u001B[42m");
		utilReplaceMap.put("B_YELLOW", "\u001B[43m");
		utilReplaceMap.put("B_BLUE", "\u001B[44m");
		utilReplaceMap.put("B_MAGENTA", "\u001B[45m");
		utilReplaceMap.put("B_CYAN", "\u001B[46m");
		utilReplaceMap.put("B_WHITE", "\u001B[47m");
		utilReplaceMap.put("H_INTENS", "\u001B[1m");
		utilReplaceMap.put("L_INTENS", "\u001B[2m");
		utilReplaceMap.put("ITALIC", "\u001B[3m");
		utilReplaceMap.put("UNDERLINE", "\u001B[4m");
		utilReplaceMap.put("BLINK", "\u001b[5m");
		utilReplaceMap.put("RAPID_BLINK", "\u001B[6m");
		utilReplaceMap.put("REVERSE_VIDEO", "\u001B[7m");
		utilReplaceMap.put("INVISIBLE_TEXT", "\u001B[8m");
		utilReplaceMap.put("%", "%");
		
	}
	
	public static String filter(String input){
		String filteredOutput = "";
		
		filteredOutput = replaceString(input);
		
		return filteredOutput;
	}
	
	private static String replaceString(String original) {
		descriptionVector = buildReplacementVector();
	    StringBuilder result = new StringBuilder(original.length());
	    String delimiters = "% /r /n";
	    StringTokenizer st = new StringTokenizer(original, delimiters, true);
	    while (st.hasMoreTokens()) {
	        String w = st.nextToken();
	        if (w.equals("%")) {
	        	// String rep = utilReplaceMap.get(st.nextToken());
	        	String rep = findReplacement(st.nextToken());
	            result.append(rep);
	            if (st.hasMoreTokens()){
	            	st.nextToken(); // get the trailing space after the %command.
	            }
	        } else {
	            result.append(w);
	        }
	    }
	    result.append("\u001B[0m");
	    return result.toString();
	}
	
	private static Vector<HashMap<String,String>> buildReplacementVector(){
		Vector<HashMap<String,String>> result = new Vector<HashMap<String,String>>();
		result.add(utilReplaceMap);
		result.add(Room.WorldReplaceMap);
		return result;
	}
	
	private static String findReplacement(String descriptor){
		
		String result = null;
		int counter = 0;
		
		try{
		while ((result == null) && (counter < descriptionVector.size())){
			result = descriptionVector.get(counter).get(descriptor);
			counter++;
		}	
		if (result == null){
			result = descriptor;
		}
		} catch (Exception e){
			e.printStackTrace();
		}
		
		System.out.println("Replacing:"+descriptor+" with:"+result);
		
		return result;
	}

	public static String getDefaultANSIFooter() {
		return defaultANSIFooter;
	}

	public static void setDefaultANSIFooter(String defaultANSIFooter) {
		OutputFilter.defaultANSIFooter = defaultANSIFooter;
	}
	
}
