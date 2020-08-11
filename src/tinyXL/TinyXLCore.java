package tinyXL;

import java.awt.Color;
import java.awt.Dimension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import javax.swing.JFrame;


/**
 * Class principale du programme.
 * Le programme fonctionne de la manière suivante : il crée la fenêtre, "window", crée les Lignes (tableau de Cellules) qui contiennent les Cellules.
 * Il gère également via un "mode" le changement entre le mode Valeur (qui affiche les résultats) et le mode Edition (qui affiche les formules)
 * tous deux accessibles par un menu.
 * L'énoncé demandant un tableau de dix lignes par six, donc statique, je n'ai pas ressenti le besoin d'utiliser des collections dynamiques pour ce projet.
 * @see Cell
 * @see Line
 * @see TinyXLMenu
 * 
 * @author hero
 *
 */
public class TinyXLCore {


	private static int LineID = 0;
	private JFrame window; 
	private Line[] lines; 
	private TinyXLMenu menu;
	private String mode;
	
	
	TinyXLCore(){
		
		this.window = new JFrame("TinyXL"); 	
		window.setDefaultCloseOperation(3); 
		window.setSize(new Dimension(706,284));
		window.getContentPane().setBackground(Color.black); 
		window.setResizable(false);
		
		addLines(); // ajout des lignes de contenu
		addMenu(); // ajout des menus
			
		window.setVisible(true); // affichage final
		
		init(); // initialisation des cellules
				
	}

	/**
	 * Initialisation du menu.
	 * Dans sa propre méthode pour que le code soit plus clair.
	 */
	private void addMenu() {

		menu = new TinyXLMenu(this);
		window.setJMenuBar(menu.getMenuBar());
	}

	/**
	 * Numérotation des cellules de la 1ere ligne, et de la première colonne.
	 * Passage en mode "édition", mode de base pour éditer les cellules.
	 */
	private void init(){
		
		lines[0].getCellAt(0).getCell().setText("XL App");
		for (int i = 1; i < lines[0].getCells().length; i++)
		{
			lines[0].getCellAt(i).getCell().setText(String.valueOf(i));
			lines[0].getCellAt(i).isInterface(true);
		}
		for (int j = 1; j < lines.length; j++)
		{
			lines[j].getCellAt(0).getCell().setText(String.valueOf(j));
			lines[j].getCellAt(0).isInterface(true);
		}
		
		this.setMode("edit");
		
	}
	
	/**
	 * Ajoute les lignes de base au programme et s'occupe du positionnement des cellules dans la fenêtre.
	 * Réalisé avant les cours sur les interfaces graphiques, ma solution était d' "imprimer" les Cellules, leur position étant incrémentée à chaque tourne de la boucle. 
	 * Maintenant, il me paraitrait plus simple peut-être de le faire avec un GridLayout, mais le tableau étant statique, il n'y a pas besoin de redimmensionnement, je pense donc que cette solution convient.
	 */
	private void addLines() {
		
		int linesY = 30; //positionnement des cellules Y
		int cellsX = 0; // positionnement des cellules X
		lines = new Line[11]; // création du tableau de lignes
		
		for (int i =  0; i < lines.length; i++)
		{
			lines[i] = new Line(LineID, lines);
			Cell[] cells = lines[i].getCells();
			for (int j = 0; j < cells.length; j++)
			{
				window.getLayeredPane().add(cells[j].getCell()); // ajoute à la fenêtre d'affichage la partie JComponent de la cellule, JTextArea
				cells[j].getCell().setBounds(cellsX, linesY, 100, 20);
				cellsX += 100;
			}
			linesY +=20; //incrémentation Y
			cellsX = 0; // incrémentation X
			LineID ++; // incrémentation ID
		}
	}
	
	/**
	* Méthode appelée pour transformer un fichier texte en cellules. * 
	* @param File fileToImport, choisi par le JChooser
	* @throws IOException 
	* @throws TerribleMistake 
	*/
	public void parseFile(File fileToImport) throws IOException, TerribleMistake {
	
		System.out.println("Parsing file ...");
		this.setMode("edit"); // passage en mode édition, les fichiers contenant des formules
			
		List<String> input = Files.readAllLines(fileToImport.toPath());
		int lineCount = 0;
		int cellCount = 1;
			
		for (int i = 0; i < input.size(); i++)
		{
			System.out.println("input size : " + input.size());
			lineCount++;
			cellCount = 1;
				
			if (input.get(i).contains("&"))
			{
				String[] args = input.get(i).split("&"); //separation au caractère '&', découpage dans un tableau de Strings
				System.out.println("#ofArgs:" + args.length);
				for (int j = 0; j < args.length; j++)
				{
					args[j] = args[j].trim();
				}
				if (args.length > 6)
				{
					throw new TerribleMistake("Le fichier n'est pas valide, il y a trop de cellules par ligne.");
				}

				for (int j = 0; j < args.length; j++)
				{
//					System.out.println("arg :" + args[j]);
					lines[lineCount].getCellAt(cellCount).getCell().setText(args[j]);
					lines[lineCount].getCellAt(cellCount).validateCell();
					cellCount++;
				}
			}
			else
			{
				throw new TerribleMistake("Le fichier n'est pas valide.");
			}
			
			if (lineCount > 10)
			{
				throw new TerribleMistake("Le fichier n'est pas valide, il y a trop de lignes.");
			}
					
		}
			
	}

	/**
	 * Renvoie le "contexte", à savoir, le tableau de Lignes (toutes les lignes).
	 * @return tableau de Lignes
	 */
	public Line[] getLines()
	{
		return lines;
	}
	
	/**
	 * Renvoie la ligne demandée par le paramètre.
	 * @param int index, l'index de la ligne dans le tableau (0-11)
	 * @return Line la ligne demandée.
	 */
	public Line getLineAt(int index)
	{
		return lines[index];
	}
	
	/**
	 * Méthode utilisée pour passer entre le mode Edition pour éditer les formules, et le mode Valeurs qui donne les résultats.
	 * Il n'y a que deux entrées possibles : "edit" et "value" passées en String par le XLAppMenu.
	 * @param String mode, "edit" ou "value"
	 */
	public void setMode(String mode) {
	
		switch (mode)
		{
			case "edit":
				
				if (this.mode !="edit")
				{
					this.mode = "edit";
					for (int i = 1; i < lines.length; i++)
					{
						for (int j = 1; j < lines[i].getCells().length; j++)
						{
							if (!lines[i].getCellAt(j).isEmpty())
								lines[i].getCellAt(j).getCell().setText(lines[i].getCellAt(j).getCellFormula()); // si la cellule n'est pas vide, écrit la formule
							
							lines[i].getCellAt(j).canEdit(true); //rend la cellule éditable
	
						}
					}
				}
				
				break;
				
			case "value":
				
				if (this.mode != "value")					
				{
					this.mode = "value";
					for (int i = 1; i < lines.length; i++)
					{
						for (int j = 1; j < lines[i].getCells().length; j++)
						{
							if (!lines[i].getCellAt(j).isEmpty())
							{
								try {
									lines[i].getCellAt(j).getCell().setText(String.valueOf(lines[i].getCellAt(j).resolveFormula(lines[i].getCellAt(j).getArguments())));
//									lines[i].getCellAt(j).getCell().setText(String.valueOf(lines[i].getCellAt(j).resolveFormula(lines[i].getCellAt(j).getCellFormula())));
									} catch (TerribleMistake e)
									{
										System.out.println("Erreur dans la résolution de la formule.");
										lines[i].getCellAt(j).getCell().setText("error");
									} catch (StackOverflowError e)
									{
										System.out.println("Certaines cellules se référencent en boucle.");
										lines[i].getCellAt(j).getCell().setText("stackOverflow");
									}
							}

							else
								lines[i].getCellAt(j).getCell().setText("empty");
	
							lines[i].getCellAt(j).canEdit(false);
						}
					}
				}
				
				break;
				
			default:
				break;
		}
	}

	/**
	 * Permet a XLApp de changer le contenu des cellules si besoin est.
	 * @param lineid
	 * @param cellid
	 * @param content
	 */
	public void setCellContent(int lineid, int cellid, String content)
	{
		lines[lineid].getCellAt(cellid).getCell().setText(content);
	}
	
	/**
	 * Renvoie la fenêtre JFrame du programme.
	 * @return JFrame la fenêtre d'affichage.
	 */
	public JFrame getWindow()
	{
		return this.window;
	}
	
}










