package tinyXL;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

/**
 * La classe des Cellules.
 * @author hero
 *
 */
public class Cell extends CopyPaster implements KeyListener, FocusListener{
	
	private JTextArea cell;
	private String content;
	private String formula;
	private int id; // est aussi égal à position X
	private int lineid; // est aussi égal à position Y;
	private Line[] parent;
	private boolean canEdit, isFocused, isInterface;
	private LinkedList<Argument> allArgs;
	private boolean isValidated;
	
	Cell(int id, int lineid, Line[] parent){
		
		cell = new JTextArea();
		cell.setSize(100, 20);	
		cell.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
		cell.setBackground(Color.black);
		cell.setForeground(Color.white);
		cell.setCaretColor(Color.white);
		
		this.id = id;
		this.lineid = lineid;
		this.content = "";
		this.formula = "";
		this.parent = parent;
		this.canEdit = true;
		
		cell.setText(content);
		cell.setFocusable(true);
		cell.setEditable(true);
		cell.setVisible(true);
		cell.addKeyListener(this);
		cell.addFocusListener(this);
		
		this.isFocused = false;
		this.isValidated = false;
				
	}
	
	/**
	 * Rend la cellule éditable ou non.
	 * @param state (true pour éditable, false pour non-éditable).
	 */
	public void canEdit(boolean state) {
		this.canEdit = state;
		if (canEdit)
		{
			this.cell.setEditable(true);
		}
		else
			this.cell.setEditable(false);
	}
	
	/**
	 * Retourne le contenu de la cellule.
	 * @return
	 */
	public String getContent()
	{
		return this.content;
	}
	
	/**
	 * Retourne la partie JTextArea de la cellule.
	 * @return
	 */
	public JTextArea getCell()
	{
		return cell;
	}
	
	/**
	 * Retourne la formule de la cellule (si il y en a une).
	 * @return la formule
	 */
	public String getCellFormula()
	{
		if (!this.formula.isEmpty())
		{
			return this.formula;
		}
		else return null;
	}
	
	/**
	 * Transforme la formule en un ArrayList d'arguments.
	 * @see Argument
	 * @param formula la formule a parser, le texte d'un JTextArea
	 * @throws TerribleMistake en cas de recursivite des references
	 * @throws NumberFormatException en général quand la formule a été mal écrite.
	 */
	public void registerFormula(String formula) throws TerribleMistake, NumberFormatException
	{
		String form = formula;
		System.out.println("Form : " + form);
		allArgs = new LinkedList<Argument>();

		
		if (form != null || !form.isEmpty())
		{
			String[] args = form.split("(?<=\\*)|(?=\\*)|(?<=\\+)|(?=\\+)|(?=-)|(?<=-)|(?=/)|(?<=/)"); //sépare la formule en gardant les opérateurs

			for (int i = 0; i < args.length; i++)
			{
				System.out.println("Args["+i+"] = "+ args[i]);
				args[i] = args[i].trim();
				
				if (args[i].startsWith("!")) // est une position relative
				{
					args[i] = args[i].replace("!", "");
					args[i] = args[i].replace("(", "");
					args[i] = args[i].replace(")", "");
					
					String[] xy = args[i].split(",");
					
					int y = Integer.valueOf(xy[0].trim());
					int x = Integer.valueOf(xy[1].trim());
					
					if (y==0 && x==0)
						throw new TerribleMistake ("Attention, cette cellule pointe vers elle-même !");
					else 
						allArgs.add(new Position(this.getCellID()-x, this.getLineID()-y, false));
				}
				
				else if (args[i].startsWith("$")) // est une position absolue
				{
					args[i] = args[i].replace("$", "");
					args[i] = args[i].replace("(", "");
					args[i] = args[i].replace(")", "");
					String[] xy = args[i].split(",");
					
					int y = Integer.valueOf(xy[0].trim());
					int x = Integer.valueOf(xy[1].trim());
					
					System.out.println("refx:" +x+" refy:"+ y);
					System.out.println("thisx:" + this.getCellID() +" thisy:"+ this.getLineID());
					
					if (x == this.getCellID() && y == this.getLineID())
						throw new TerribleMistake ("Cette cellule pointe vers elle-même !");
					else
						{
							allArgs.add(new Position(x, y, true));
						}
				}
				else if (args[i].contains("+") || args[i].contains("-") || args[i].contains("*") || args[i].contains("/")) //est un operateur
				{
					for (int j = 0; j < args[i].length(); j++)
					{
						if (args[i].charAt(j) == '+' || args[i].charAt(j) == '-' || args[i].charAt(j) == '*' || args[i].charAt(j) == '/')
						{
							allArgs.add(new Operator(args[i].charAt(j)));
							break;
						}
					}
				}
				else if (args[i].startsWith("Som") || args[i].startsWith("Moy")) //est une somme ou une moyenne
				{
					if (args[i].startsWith("Som"))
					{
						args[i] = args[i].replace("Som", "");
						args[i] = args[i].replace("(", "");
						args[i] = args[i].replace(")", "");
						String[] xy = args[i].split(",");
						int sy = Integer.valueOf(xy[0].trim()); System.out.println("sy:"+sy);
						int sx = Integer.valueOf(xy[1].trim());System.out.println("sx:"+sx);
						int ey = Integer.valueOf(xy[2].trim());System.out.println("ey:"+ey);
						int ex = Integer.valueOf(xy[3].trim());System.out.println("ex:"+ex);
						if ((sx <= ex && sy <= ey) && sx >= 1 && sx <= 6 && sy >= 1 && sy <= 10)
						{
							allArgs.add(new Operation(sx, sy, ex, ey, true)); //sx = startX, ex = endX
						}
						else 
							{
								throw new TerribleMistake ("La référence est mal écrite. \n Format : Som((startCellY, startCellX), (endCellY, endCellX))");
							}
					}
					else
					{
						args[i] = args[i].replace("Moy", "");
						args[i] = args[i].replace("(", "");
						args[i] = args[i].replace(")", "");
						String[] xy = args[i].split(",");
						int sy = Integer.valueOf(xy[0].trim());
						int sx = Integer.valueOf(xy[1].trim());
						int ey = Integer.valueOf(xy[2].trim());
						int ex = Integer.valueOf(xy[3].trim());
						if ((sx <= ex && sy <= ey) && sx >= 1 && sx <= 6 && sy >= 1 && sy <= 10)
							allArgs.add(new Operation(sx, sy, ex, ey, false)); //sx = startX, ex = endX
						else throw new TerribleMistake ("La référence est mal écrite. \n Format : Som((startLigne, endLigne), (startCellule, endCellule)) \n Les références doivent être comprises entre 1 et 6 (x) ou 1 et 10 (y)");
					}
				}
				else // est une valeur simple
				{
					allArgs.add(new Value(args[i].trim()));
				}
			}

		}
		
	}
	
	/**
	 * Fonction récursive qui résout les formules jusqu'à en obtenir un double.
	 * @param allArgs
	 * @return double le résultat de la formule.
	 * @throws TerribleMistake en cas de récursivité des références ou lorsque ça pointe vers une cellule qui n'existe pas
	 */
	public double resolveFormula(LinkedList<Argument> allArgs) throws TerribleMistake, StackOverflowError
	{
		double result = 0;
		LinkedList<Argument> args = allArgs;
		ArrayList<Double> operands = new ArrayList<Double>(2);
		ArrayList<Argument> refPositions = new ArrayList<Argument>(); //Un ArrayList qui ajoute les Cellules référencées, et regarde si elles existent déjà, pour repérer les références récursives.
		char operator = 'n';
		
		for (int i = 0; i < args.size(); i++)
		{		
			double tmp = 0;
			if (args.get(i) instanceof Position)
			{
				Position p = (Position) args.get(i);
				int x = this.getCellID(); System.out.println("thisX : "+x);
				int y = this.getLineID(); System.out.println("thisY : "+y);
				int refX = p.x; System.out.println("px : "+ refX);
				int refY = p.y;System.out.println("py : "+ refY);
				int gapX = Math.abs(x-refX); System.out.println("gapX :"+gapX);
				int gapY = Math.abs(y-refY);System.out.println("gapY : "+gapY);
			
				try {
					
					Cell refCell = parent[refY].getCellAt(refX);
					
					if (!refCell.isEmpty())
					{					
						if (p.isEqualTo(new Position(this.id, this.lineid, true)))
							throw new TerribleMistake ("Les références se réferencent elle-mêmes.");
						else
						{
							tmp = resolveFormula(refCell.getArguments());
							refPositions.add(p);
						}

					}
					else
						this.getCell().setText("vide");
					
				} catch (NullPointerException e)
				{
					e.printStackTrace();
					throw new TerribleMistake("La référence pointe vers une cellule qui n'existe pas !");
				}
				operands.add(tmp);
			}
			
			else if (args.get(i) instanceof Operator)
			{
				Operator op = (Operator) args.get(i);
				operator = op.op; 
			}
			else if (args.get(i) instanceof Value)
			{
				Value x = (Value) args.get(i);
				operands.add((double) x.val);
			}
			else if (args.get(i) instanceof Operation)
			{
				System.out.println("Resolving operation ...");
				Operation o = (Operation) args.get(i);
//				System.out.println("Operation isSomme : "+ o.isSomme + "Op sx : "+o.sx + " op ex : "+ o.ex +" op sy "+ o.sy + " op ey "+ o.ey);
				
				double res = 0;
				int counter = 0;
				
				for (int j = o.sy; j < o.ey+1; j++)
				{
//					System.out.println("j: "+j);
					
					for (int k = o.sx; k < o.ex+1; k++)
					{
//						System.out.println("k: "+k);
						counter++;
						res += resolveFormula(parent[j].getCellAt(k).getArguments());
//						System.out.println("Adding res "+ res + " from cell("+ parent[j].getCellAt(k).getLineID() + parent[j].getCellAt(k).getCellID());
					}
				}
				if (o.isSomme)
					operands.add(res);
				else
					operands.add(res/counter);
			}
		}
		
		try {
			if (operands.size() > 1)
				result = calculate(operands.get(0), operands.get(1), operator);
			else
				result = Double.valueOf(operands.get(0));
		} catch (TerribleMistake e)
		{
			result = 0;
		}
		
		return result;
	}
	
	
	/**
	 * Retourne le numéro de ligne (qui est la position Y de la cellule).
	 * Getter en visibilité package pour le test.
	 * @see PositionTest
	 * @return  y
	 */
	int getLineID() {
		return this.lineid;
	}
	/**
	 * Retourne le numéro de cellule (qui est la position X de la cellule).
	 * Getter en visibilité package pour le test.
	 * @see PositionTest
	 * @return  x
	 */
	int getCellID() {
		return this.id;
	}

	/**
	 * Simple méthode de calcul avec opérateur.
	 * @param double1
	 * @param double2
	 * @param operator
	 * @return
	 * @throws TerribleMistake 
	 */
	private double calculate (Double double1, Double double2, char operator) throws TerribleMistake
	{
		double res = 0;
		switch(operator)
		{
			case '+':
				res = double1+double2;
				break;
			
			case '-':
				res = double1-double2;
				break;
			
			case '*':
				res = double1*double2;
				break;
			
			case '/':	
				if (double2 !=0)
				{
					res = double1/double2;
				}
				else throw new TerribleMistake("On ne peut pas diviser par zero.");
				break;
		}
		return res;
	}
	

	/**
	 * Contient une formule ?
	 * @return true si oui, false si non
	 */
	public boolean containsFormula(){
		
		return !this.getCell().getText().isEmpty();
	}
	
	public boolean isEmpty() {
		return this.getCell().getText().isEmpty();
	}

	/**
	 * Ecrit le text de la cellule dans la formule.
	 * @throws TerribleMistake
	 */
	public void validateCell()
	{
		formula = cell.getText().trim();
		if (!this.isInterface() && !isValidated())
		{
			try {
				this.registerFormula(formula);
				isValidated = true;
			} catch (TerribleMistake e)
			{
				cell.setText("error");
				e.printStackTrace();
			} catch (NumberFormatException e)
			{
				cell.setText("mauvais format");
			}
		}
	}
	
	private boolean isValidated() {
		return this.isValidated;
	}

	public LinkedList<Argument> getArguments()
	{
		return this.allArgs;
	}
	public void setArguments(LinkedList<Argument> args)
	{
		this.allArgs = args;
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		if (e.getKeyCode() == 10 || e.getKeyCode() == 9) // entrée
		{
			validateCell();
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {


	}

	@Override
	public void focusGained(FocusEvent e) {
			
		System.out.println("Focus gained on cell : Line : " + this.lineid + ", Cell "+ this.id);
		this.cell.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 1));
		this.isFocused = true;
		this.isValidated = false;
		
		for (int i = 0; i < parent.length; i++)
		{
			for (int j = 0; j < parent[i].getCells().length; j++ )
			{
				if (parent[i].getCellAt(j).isFocused() && parent[i].getCellAt(j) != this)
				{
					System.out.println("Turning off focus for ... " + parent[i].getID() + ", "+ parent[i].getCellAt(j).getCellID());
					parent[i].getCellAt(j).isFocused(false);
					break;
				}
			}
		}
		
	}

	@Override
	public void focusLost(FocusEvent e) { //lorsqu'on clique en dehors de la cellule, cela la valide.
		if (canEdit && !isValidated)
			validateCell();
		this.cell.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1));
	}

	@Override
	public void copy(Cell cell) {
		super.copy(this);
	}

	@Override
	public void paste() {
		
		System.out.println("Pasting ...");
		Cell clipboard = CopyPaster.clipboard;
		LinkedList<Argument> args = clipboard.getArguments();
		System.out.println("Number of arguments in clipboard : "+ args.size());
		
		String newText = "";
		
		for (int i = 0; i < args.size(); i++)
		{
			String s ="";
			if (args.get(i) instanceof Position)
			{
				Position pos = (Position) args.get(i);
				int x = this.getCellID();
				int y = this.getLineID();
				int refX = pos.x;
				int refY = pos.y;
				if (!pos.isAbsolute)
				{
					int gapX = Math.abs(x-refX);
					int gapY = Math.abs(y-refY);
					s = "!("+gapY+","+gapX+")";
				}
				else
					s = "$("+refY+","+refX+")";
			}
			else
			{
				s = args.get(i).print();
			}
			newText+= s;
		}
		
		this.getCell().setText(newText);
		
			
	}

	public boolean isFocused() {
		return isFocused;
	}
	public void isFocused(boolean state) {
		this.isFocused = state;
	}

	public boolean isInterface() {
		return isInterface;
	}

	public void isInterface(boolean isInterface) {
		this.isInterface = isInterface;
	}

		/**
		 * L'ancienne version de resolveFormula().
		 * Retourne l'entier calculé par la formule, après sa résolution.
		 * Pour plus de clarté, le code regarde d'abord la composition de la formule, et édite des variables locales représentant sa composition
		 * @deprecated
		 * @param formula la formule à traiter
		 * @return int la valeur après calcul.
		 */
//		public int resolveFormula(String formula) throws TerribleMistake
//		{
//			
//			try
//			{
//				String form = "";
//				int result = 0;
//				if (formula != null)
//				{
//					form = formula.replace(" ", ""); //nettoyage de la formule
//				}
//				if (!form.isEmpty())
//				{
//					System.out.println("Form is empty ? "+ form.isEmpty() + " form : " + form);
//					form = form.trim();
//					
//					int nbrOfCellRefs = 0; //nombre de reférences aux autres cellules (0, 1 ou 2);
//					int nbrOfArgs = 1; //nombre d'arguments dans la cellule (1 ou 2);
//					
//					char operator = 'n'; // l'opérateur de la formule
//					boolean somme = false; // est une somme ou une moyenne
//				
//				
//					if (form.contains("Somme") || form.contains("Moy"))
//					{
//						System.out.println("Somme && Moyenne ...");
//						if (form.contains("Somme"))
//						{
//							form = form.replace("Somme", "");
//							somme = true;
//							System.out.println("Somme.");
//						}
//						else
//						{
//							System.out.println("Moyenne.");
//							form = form.replace("Moy", "");
//						}
//						
//						form = form.replace(")", ""); //nettoyage
//						form = form.replace("," ,"");
//						form = form.replace("(", "");
//						form = form.replace(" ", "");
//						form = form.trim();
//					
//						System.out.println("form : " + form);
//						
//						int startLine = Integer.valueOf(String.valueOf(form.charAt(0))); //calcul des coordonnées pour le calcul moyenne ou somme à venir
//						int startCell = Integer.valueOf(String.valueOf(form.charAt(1)));
//						int endLine = Integer.valueOf(String.valueOf(form.charAt(2)));
//						int endCell = Integer.valueOf(String.valueOf(form.charAt(3)));
//						
//			//			int[] debug = {startLine, startCell, endLine, endCell}; //pour le debuggage
//			//			for (int i = 0; i < debug.length; i++)
//			//			{
//			//				System.out.println(debug[i]);
//			//			}
//						
//						ArrayList<Cell> cells = new ArrayList<Cell>(); //ArrayList pour récupérer les cellules concernées et en additionner le contenu
//						
//						for (int i = startLine; i <= endLine; i++)
//						{
//							for (int j = startCell; j <= endCell; j++)
//							{
//								cells.add(parent[i].getCellAt(j));
//								System.out.println("Added cell :" + parent[i].getCellAt(j).id + " formula " + parent[i].getCellAt(j).getCellFormula());
//							}
//						}
//						
//						int counter = 0; //pour le calcul de la moyenne
//						
//						for (int i = 0; i < cells.size(); i++)
//						{
//							counter++;
//							result += cells.get(i).resolveFormula(cells.get(i).getCellFormula()); //résout toutes les formules de l'ArrayList temporaire et les ajoute au résultat
//						}
//						if (!somme)
//						{
//							System.out.println("Result : " + result);
//							System.out.println("Counter : " + counter);
//							result = result/counter; //les divise par le nombre de cellules si l'opération demandée n'est pas une somme
//						}
//					}
//					
//					else //n'est pas une opération somme ou moyenne
//					{
//						for (int i = 0; i < form.length(); i++)
//						{
//							if (form.charAt(i) == '$' || form.charAt(i) == '!') //contient une référence de cellule
//							{
//								nbrOfCellRefs++;
//							}
//							
//							if (form.charAt(i) == '+' || form.charAt(i) == '-' || form.charAt(i) == '*' || form.charAt(i) == '/') //contient un opérateur et donc plusieurs arguments
//							{
//								nbrOfArgs++;
//								operator = form.charAt(i);
//							}
//						}
//						
//						System.out.println("Number of cell refs in formula : " + nbrOfCellRefs);
//						System.out.println("Number of arguments in formula : " + nbrOfArgs);
//						
//						if(nbrOfArgs == 1 && nbrOfCellRefs == 0) // si il n'y qu'un argument, et pas de référence de cellule : c'est une valeur 'pure'
//						{
//							form = form.replace(" ", "");
//							form = form.trim();
//							System.out.println("Pure value detected.");
//							try {
//							result = Integer.valueOf(form);
//							} catch (NumberFormatException e) {
//								result = 0;
//							}
//						}
//						
//						else if (nbrOfArgs == 1 && nbrOfCellRefs == 1) // si il n'y a qu'un argument, et une référence de cellule : c'est une référence à une autre cellule
//						{
//							System.out.println("One cell reference detected, without operators.");
//							
//							if (form.contains("$"))
//							{
//								form = form.replace("$", "");
//								absolutePos = true;
//							}
//							else if (form.contains("!"))
//							{
//								form = form.replace("!", "");
//								absolutePos = false;
//							}
//							
//							form = form.replace("(", "");
//							form = form.replace(")","");
//							
//							String[] args = form.split(",");
//							
//							for (int i = 0; i<args.length; i++)
//							{
//								args[i] = args[i].trim();
//							}
//							
//							int lineRef = Integer.valueOf(args[0]);
//							int cellRef = Integer.valueOf(args[1]);
//							
//							Cell ref = parent[lineRef].getCellAt(cellRef);
//							
//							if (!absolutePos)
//							{
//								
//								ref = parent[this.lineid-lineRef].getCellAt(this.id-cellRef);
//	//							referredPos.add(new Position(ref.getLineID(), ref.getCellID(), false));
//							}
//							else
//							{
//	//							referredPos.add(new Position(ref.getLineID(), ref.getCellID(), true));
//							}
//			
//							result = ref.resolveFormula(ref.getCellFormula());
//							
//						}
//						
//						else if (nbrOfArgs == 2 && nbrOfCellRefs == 0) // si il y a deux arguments, et aucune référence de cellule : c'est une opération simple
//						{
//							System.out.println("Standard values operation detected.");
//							String[] args = form.split("\\*|\\+|-|/");
//							for (int i = 0; i<args.length; i++)
//							{
//								args[i] = args[i].trim();
//							}
//							
//						
//							result = (int) calculate(Double.valueOf(args[0]), Double.valueOf(args[1]), operator);
//	//						referredPos.clear();
//							
//						}
//						
//						else if (nbrOfArgs == 2 && nbrOfCellRefs >= 1) // si il y a deux arguments, et au moins une référence à une cellule
//						{
//							System.out.println("Two arguments with at least one cell reference detected.");
//							int value1 = 0;
//							int value2 = 0;
//							String[] args = form.split("\\*|\\+|-|/");
//							boolean doubleRefAbsolute = false; //indique si la cellule contient deux références à une autre cellule, avec une relative et une absolue
//							
//							if (args[0].contains("$") || args[0].contains("!")) //l'argument de gauche est une référence à une cellule
//							{
//								System.out.println("Left argument is a cell reference.");
//								if (args[0].contains("$"))
//								{
//									args[0] = args[0].replace("$", "");
//									absolutePos = true;
//								}
//								
//								else if (args[0].contains("!"))
//								{
//									args[0] = args[0].replace("!", "");
//									absolutePos = false;
//								}
//								
//								args[0] = args[0].replace("(", "");
//								args[0] = args[0].replace(")", "");
//								args[0] = args[0].trim();
//								
//								String[] leftArgs = args[0].split(",");
//								for (int i = 0; i < leftArgs.length; i++)
//								{
//									leftArgs[i] = leftArgs[i].trim();
//								}
//								int lineRef = Integer.valueOf(leftArgs[0]);
//								int cellRef = Integer.valueOf(leftArgs[1]);
//								Cell cell = parent[lineRef].getCellAt(cellRef);
//								if (!absolutePos)
//								{
//									cell = parent[this.lineid-lineRef].getCellAt(this.id-cellRef);
//	//								referredPos.add(new Position(cell.getLineID(), cell.getCellID(), false));
//								}
//								else
//								{
//	//								referredPos.add(new Position(cell.getLineID(), cell.getCellID(), true));
//								}
//								value1 = cell.resolveFormula(cell.getCellFormula());			
//							}
//							else //l'argument de gauche est une valeur
//							{
//								value1 = Integer.valueOf(args[0]);
//							}
//							
//							if (args[1].contains("$") || args[1].contains("!")) //l'argument de droite est une référence à une cellule
//							{
//								System.out.println("Right argument is a cell reference.");
//								if (args[1].contains("$"))
//								{
//									args[1] = args[1].replace("$", "");
//									absolutePos = true;
//								}
//								else if (args[1].contains("!"))
//								{
//									args[1] = args[1].replace("!", "");
//									absolutePos = false;
//								}
//								args[1] = args[1].replace("(", "");
//								args[1] = args[1].replace(")", "");
//								args[1] = args[1].trim();
//								String[] rightArgs = args[1].split(",");
//								for (int i = 0; i < rightArgs.length; i++)
//								{
//									rightArgs[i] = rightArgs[i].trim();
//								}
//								int lineRef = Integer.valueOf(rightArgs[0]);
//								int cellRef = Integer.valueOf(rightArgs[1]);
//								Cell cell = parent[lineRef].getCellAt(cellRef);
//								if (!absolutePos)
//								{
//									cell = parent[this.lineid-lineRef].getCellAt(this.id-cellRef);
//	//								referredPos.add(new Position(cell.getLineID(), cell.getCellID(), false));
//								}
//								else
//	//								referredPos.add(new Position(cell.getLineID(), cell.getCellID(), true));
//	
//								
//								value2 = cell.resolveFormula(cell.getCellFormula());	
//							}
//							else
//							{
//								value2 = Integer.valueOf(args[1]);
//							}
//							
//							result = (int) calculate((double)value1,(double) value2, operator);
//						}
//					}
//					
//				}
//				else
//				{
//					this.cell.setText("empty");
//				}
//				return result;
//				
//			} catch (NullPointerException e)
//			{
//				this.cell.setText("error");
//				throw new TerribleMistake("Une des références de cellule ne pointe pas vers une cellule existante.");
//			} catch (ArrayIndexOutOfBoundsException e)
//			{
//				this.cell.setText("error");
//				throw new TerribleMistake("Une des références de cellule a été mal écrite. \nFormat :\n $(y, x) (pour une référence absolue) ou !(y, x) (pour une référence relative)");
//			} catch (StringIndexOutOfBoundsException e)
//			{
//				this.cell.setText("error");
//				throw new TerribleMistake("Une des références de cellule ne pointe pas vers une cellule valide.");
//			}
//		}
//	
	
}
