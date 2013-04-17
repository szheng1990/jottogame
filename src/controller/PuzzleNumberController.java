package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ui.JottoGUI;

/**
 * PuzzleNumberController is the listener for client specification of puzzle number
 * 
 * It updates the puzzle number based on the client input (either by hitting the button
 * newPuzzleNumber or writing in the puzzle number text field). Also, it resets the 
 * guessTable in the View. 
 *
 */
public class PuzzleNumberController implements ActionListener
{
	public JottoGUI jottoGUI;
	public PuzzleNumberController(JottoGUI jottoGUI)
	{
		this.jottoGUI = jottoGUI;
	}
	public void actionPerformed(ActionEvent e)
	{
		int PuzzleNumber=jottoGUI.getPuzzleNumber();
		jottoGUI.setPuzzleNumber(PuzzleNumber); 
	}
}
