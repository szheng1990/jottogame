package ui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import controller.GuessController;
import controller.PuzzleNumberController;

/**
 * The GUI for Jotto game. Makes uses of labels, buttons, text fields and tables
 * to make a complete GUI for the game.
 * 
 * In this GUI, a user can type in a text field and hit enter, or click a button
 * to trigger changes in the GUI itself.
 * 
 * Specifically, action listener is added to the text fields puzzleNumber and
 * guess, and to the button newPuzzleButton. So, action events are triggered,
 * and changes happen in GUI according to the user-inputs or interactions in
 * those components.
 * 
 * JottoGUI's layout is organized by groupLayout class.
 * 
 * (1) newPuzzleButton: press to generate new puzzle question
 * (2) newPuzzleNumber: textField where the player can specify puzzle number and
 * "listens" to action "enter" to update puzzle puzzleNumber
 * (3) guess: client input the guess string. It also listens to action "enter" 
 * and creates a new thread for each new action. 
 * (4) guessTable: records guess and feedback history
 * (5) currentPuzzleNumber: stores the current puzzle number. default to 1
 * (6) tableModel: tableModel used by guessTable
 * 
 */

/**
 * 
 * Thread-Safety Argument
 * The threads in this program are:
 * - one thread per new guess input
 * - a background thread for the GUI
 * 
 * Since the underlying data structures of Java Swing are inherently unsafe, we have
 * used SwingUtilities.invokeLater() to queue messages to run in the single 
 * designated GUI thread.
 * 
 * All fields in the GUI are private final. The only shared mutable datatype
 * is the guessTable, which is continuously updated through new guess inputs. 
 * The rep invariants (please see below) for the guessTable, however, are always 
 * preserved even in concurrent conditions. Specifically:
 * (1) GUI.updateHistory() is written such that the guess string and its associated
 *     server feedback always have the same row number in the table - rep invariant 
 *     1 is satisfied.
 * (2) Whenever a new guess is entered, a new row will be appended to the table 
 *     even if the server has not yet returned - rep invariant 2 is satisfied.
 *     
 * Therefore, in a multi-threaded setting, the rep invariants are still preserved
 * , cleared from race conditions. 
 *
 */

/**
 * rep invariant for the GUI
 * (1) the guess string and its corresponding feedback must be in the same
 *     row of guessTable, even if the feedback is delayed from the server
 * (2) the last row of guessTable corresponds to the most recent guess entered
 *     (note: entered, NOT returned)
 * (3) (trivial) the JTextField 'guess' is always cleared after a new input 
 *     has been submitted to the server
 */

@SuppressWarnings("serial")
public class JottoGUI extends JFrame {

	final private JLabel puzzleNumber; // required
	final private JButton newPuzzleButton; // required
	final private JTextField newPuzzleNumber; // required
	final private JTextField guess; // required
	final private JTable guessTable; // required
	final private JLabel guessCommand;
	final private PuzzleNumberController puzzleNumberController;
	final private GuessController guessController;
	
	
	private int currentPuzzleNumber = 1;
	final private DefaultTableModel tableModel = new DefaultTableModel();

	//Constructor
	public JottoGUI() {

		// puzzleNumber display
		puzzleNumber = new JLabel("Puzzle #"+currentPuzzleNumber); // defaults to 1
		puzzleNumber.setName("puzzleNumber");
		getContentPane().add(puzzleNumber);

		// action listener for newPuzzleButton
		puzzleNumberController = new PuzzleNumberController(this);
		
		// newPuzzleButton
		newPuzzleButton = new JButton("New Puzzle");
		newPuzzleButton.setName("newPuzzleButton");
		getContentPane().add(newPuzzleButton);
		newPuzzleButton.addActionListener(puzzleNumberController);

		// text field for inputing new puzzle number
		newPuzzleNumber = new JTextField("");
		newPuzzleNumber.setName("newPuzzleNumber");
		getContentPane().add(newPuzzleNumber);
		newPuzzleNumber.addActionListener(puzzleNumberController);

		// "type a guess here"
		guessCommand = new JLabel("Type a guess here:");
		getContentPane().add(guessCommand);

		// action listener for guess
		guessController = new GuessController(this);
		
		// text field for inputting guess string
		guess = new JTextField("guess");
		guess.setName("guess");
		getContentPane().add(guess);
		guess.addActionListener(guessController);

		guessTable = new JTable(tableModel);
		tableModel.addColumn("Your Guess");
		tableModel.addColumn("# Letters in Common");
		tableModel.addColumn("# of Letters in Position");
		guessTable.setName("guessTable");
		getContentPane().add(guessTable);
		
		// make guess history scrollable
		JScrollPane scrollPane = new JScrollPane(guessTable);
		guessTable.scrollRectToVisible(guessTable.getCellRect(guessTable.getRowCount()-1, 0, true));
		getContentPane().add(scrollPane);
		
		GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addGroup(layout.createSequentialGroup()
                        .addComponent(puzzleNumber)
                        .addComponent(newPuzzleButton)
                        .addComponent(newPuzzleNumber))
                .addGroup(layout.createSequentialGroup()
                        .addComponent(guessCommand)
                        .addComponent(guess))
                .addComponent(scrollPane));
                             
                       
                       
                        
        
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup()
                        .addComponent(puzzleNumber)
                        .addComponent(newPuzzleButton)
                        .addComponent(newPuzzleNumber))
                .addGroup(layout.createParallelGroup()
                        .addComponent(guessCommand)
                        .addComponent(guess))
                .addComponent(scrollPane));

		setSize(getPreferredSize());

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	/**
	 * set the GUI into a new puzzle ID, and reset the previous guess history
	 * @param n the new puzzle number
	 */
	public void setPuzzleNumber(int n) {
		currentPuzzleNumber = n;
		puzzleNumber.setText("Puzzle #" + n);
		resetHistory();
	}

	/**
	 * If no number is provided or the input is not a positive integer, pick a
	 * random positive integer. Make sure you can generate at least 10,000
	 * different numbers.
	 * 
	 * @return the puzzle number requested by the client; or a randomly generated
	 * number if the client request is invalid
	 */
	public int getPuzzleNumber() {
		final String userInput = newPuzzleNumber.getText();
		int newPuzzleNumber = 0;
		try {
			newPuzzleNumber = Integer.parseInt(userInput);
		} catch (Exception e) {// text field is empty
			final Random generator = new Random();
			newPuzzleNumber = generator.nextInt(100000);
		}
		if (newPuzzleNumber == 0) {
			throw new RuntimeException("puzzle number failure!");
		}
		return newPuzzleNumber;

	}

	/**
	 * 
	 * @return the puzzle number on which Jotto Game is running
	 */
	public int getCurrentPuzzleNumber() {
		return currentPuzzleNumber;
	}

	/**
	 * 
	 * @return the guess string inputed by the client
	 */
	public String getGuess() {
		String userGuess = guess.getText();
		return userGuess;
	}

	public void resetGuessField() {
		guess.setText("");
	}

	/**
	 * 
	 * @param s1 string for column 1
	 * @param s2 string for column 2
	 * @param s3 string for column 3
	 * @return the number of rows in the current table
	 */
	public int addHistory(String s1, String s2, String s3) {

		tableModel.addRow(new Object[] { s1, s2, s3 });
		System.out.println("addHistory " + (tableModel.getRowCount() - 1));
		return tableModel.getRowCount() - 1;
	}

	
	/**
	 * 
	 * @param row the row to insert the guess information into
	 * @param s1 string for column 1
	 * @param s2 string for column 2
	 * @param s3 string for column 3
	 */
	public void updateHistory(int row, String s1, String s2, String s3) {
		System.out.println("updateHistory");
		tableModel.removeRow(row);
		tableModel.insertRow(row, new Object[] { s1, s2, s3 });

	}

	/**
	 * reset table to zero
	 */
	public void resetHistory() {
		tableModel.setRowCount(0);
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				JottoGUI main = new JottoGUI();
				main.setTitle("Jotto Game!");
				main.pack();
				main.setVisible(true);
			}
		});
	}
}