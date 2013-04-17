package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import ui.JottoGUI;

/**
 * 
 * GuessController is the listener for client guess input.
 * It starts a new thread whenever a new guess has been entered.
 *
 */
public class GuessController implements ActionListener {

	private final JottoGUI jottoGUI;

	// constructor
	public GuessController(JottoGUI jottoGUI) {
		this.jottoGUI = jottoGUI;
	}

	public void actionPerformed(ActionEvent e) {
		GuessControllerThread thread = new GuessControllerThread(jottoGUI);
		thread.start();
	}
}
