package tinyXL;

import javax.swing.JOptionPane;

/**
 * Classe d'exception.
 * 
 * @author hero
 *
 */
public class TerribleMistake extends Exception {

	TerribleMistake(String message)
	{
		JOptionPane.showMessageDialog(null, message, "Uh oh !", JOptionPane.ERROR_MESSAGE);
		System.out.println(message);
	}
}
