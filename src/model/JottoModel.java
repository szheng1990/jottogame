package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Overall, JottoModel handles the inputs (puzzle number + guess) from the
 * client and converts the feedback from the server into a 2-column format.
 * 
 * 
 */
public class JottoModel {
	public final static String victoryString="guess 5 5";
	
	/**
	 * @param guess: a string to be submitted to the server
	 * 	
	 * @param puzzleNumber: the puzzle ID to be requested from the server
	 * 				
	 * @return (in String format) feedback from the server
	 * 			
	 * @throws RuntimeException("IOException") when failing server connection
	 * @throws RuntimeException("URLFailure") when URL is ill-formed
	 * @throws RuntimeException ("Null/Empty Response") 
	 * @throws RuntimeException ("invalid feedback")	
	 * 
	 *  
	 */
	public static String makeGuess(String guess, int puzzleNumber) {
		BufferedReader serverOut = null;
		URL gameLink = null;
		String feedBack;
		try {
			gameLink = new URL("http://courses.csail.mit.edu/"
					+ "6.005/jotto.py?puzzle=" + puzzleNumber + "&guess="
					+ guess);
			serverOut = new BufferedReader(
			        new InputStreamReader(gameLink.openStream()));
			
			while ((feedBack = serverOut.readLine()) != null && feedBack.trim() == "") {
				// read the input stream from the server until we get some concrete feedback message
			}
			serverOut.close();
		} catch (MalformedURLException e) {
			throw new RuntimeException("Invalid URL: " + gameLink);
		} catch (IOException e) {
			throw new RuntimeException("IOException");
		} finally {
			try {
				serverOut.close();
			} catch (Exception e) {
			}
		}
		/* Response to Caesar Code Review Comment 
		 * One of the code reviewers suggest that the method should not throw 
		 * RuntimeException; instead, it should print the error messages in
		 * the table. I chose not to heed his suggestion because it seems that 
		 * he confused the server errors with RuntimeException. 
		 * 
		 * Specifically, the sever errors (Ill-formatted request, Non-number 
		 * puzzle ID, and Invalid guess) occur because the client is not 
		 * supplying the correct input format. All the runtime exceptions below,
		 * however, occur only when the system encounters failures (e.g., connection 
		 * failure between server and client, unexpected server behavior, etc.).
		 * In this case, it is no longer sensible to leave the program running.
		 * 
		 * Therefore, I have kept my original design:
		 * (1) if an error matches one of the possible error messages from the 
		 * server, store the error message in the table
		 * (2) otherwise, throw RuntimeException to signify system error
	     */
		if (feedBack == null) {
			throw new RuntimeException("null/empty response!");
		}
		feedBack = feedBack.trim();
		if (feedBack == "") {
			throw new RuntimeException("null/empty response!");
		}
		
		// define regular expression for valid feedback format
		// i.e. either (guess number number) or an error ID
		String validfeedback = "(guess [0-5] [0-5])|(error [0-3].*)";
		if(!feedBack.matches(validfeedback)) {
			throw new RuntimeException("invalid feedback format from server!");
        }
		return feedBack;
       
	}
	
	
	/**
	 * 
	 * @param feedBack: feedback from the server
	 * @return a two-element list containing the number of correct characters
	 *          and the number of correct positions
	 * @throws RuntimeException ("invalid feedback format")
	 * 
	 * rep invariant: the output is a 2-column array list containing
	 *                the parsed feedback message from the server
	 */
	public static List<String> convertFeedback(String feedBack){
		String guessMessage = "(guess [0-5] [0-5])";
		String errorMessage = "(error [0-3].*)";	
		List<String> convertedOutput=new ArrayList<String>();
		if (feedBack.equals(victoryString)){
			convertedOutput.add("you win!");
			convertedOutput.add("");
		}
		else if (feedBack.matches(guessMessage)){
			convertedOutput.add(feedBack.substring(6, 7)); // # of correct guesses
			convertedOutput.add(feedBack.substring(8,9));  // # of correct positions
		}else if (feedBack.matches(errorMessage)){
			convertedOutput.add(feedBack.substring(8)); // full body of error message
			convertedOutput.add("");
		}else{
			/*revised to make the exception easier to follow (based on code review comment)*/
			throw new RuntimeException("invalid feedback from the server"); 
		}
		return convertedOutput;
	}
}