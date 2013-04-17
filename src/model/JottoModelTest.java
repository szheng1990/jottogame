package model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * 
 * Testing Strategy for JottoModel 
 * (1) Test whether a perfect guess returns 'guess 5 5'
 * (2) Test whether a legitimate guess gets valid feedback from
 *     the sever
 *     Case 1: without delay; Case 2: with delay (* included)
 * (3) Test whether the server returns Type 1 Error if the puzzle
 *     ID is invalid (Puzzle ID <= 0)
 * (4) Test whether the server still correctly returns even when 
 *     the puzzle ID is a very large number
 * (5) Test for Type 2 Error from the server  
 *     i) guess too long  
 *     ii) guess too short
 *     iii) guess contains illegal character(s)
 *     
 *  Note: Strategies (3) and (4) are revised based on the code review
 *  comment on Caesar. 
 */  

public class JottoModelTest {
	@Test
    public void testPerfectGuess() {
		assertEquals("guess 5 5",
				JottoModel.makeGuess("cargo", 16952));
    }	
	@Test
	public void testGoodGuess1(){
		String expected = "guess 3 1";
		String result = JottoModel.makeGuess("crazy", 16952);
		assertEquals(expected,result);
	}
	@Test
	public void testGoodGuess2(){
		// guess with a place-holder *
		String expected = "guess 2 1";
		String result = JottoModel.makeGuess("c*azy", 16952);
		assertEquals(expected,result);
	}
	@Test
	public void invalidGuessID1(){
		// puzzle ID = 0 
		String expected = "error 1";
		String result = JottoModel.makeGuess("crazy", 0);
		assertEquals(expected,result.substring(0, 7));
	}
	
	@Test
	public void invalidGuessID2(){
		// puzzle ID < 0 
		String expected = "error 1";
		String result = JottoModel.makeGuess("crazy", -1);
		assertEquals(expected,result.substring(0, 7));
	}
	
	@Test
	public void validButLargeGuessID(){
		// puzzle ID very large 
		String expected = "guess 1 1";
		String result = JottoModel.makeGuess("crazy", Integer.MAX_VALUE);
		assertEquals(expected,result);
	}
	
	@Test
	public void error2Test1(){
		// guess too long
		String expected = "error 2";
		String result = JottoModel.makeGuess("transformer", 16952);
		assertEquals(expected,result.substring(0, 7));	
	}	
	@Test
	public void error2Test2(){
		// guess too short
		String expected = "error 2";
		String result = JottoModel.makeGuess("*abc", 16952);
		assertEquals(expected,result.substring(0, 7));
	}
	
	@Test
	public void error2Test3(){
		// guess contains illegal characters
		String expected = "error 2";
		String result = JottoModel.makeGuess("!!!!", 16952);
		assertEquals(expected,result.substring(0, 7));
	}
}
