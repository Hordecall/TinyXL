package tinyXL;

/**
 * La classe Ligne, qui contient un tableau de 7 cellules, avec les ID de tout le monde.
 * 
 * @author hero
 *
 */
public class Line{
	
	private Cell[] cells;
	private int lineID;
	private int cellID;
	private Line[] parent;
	
	Line(int id, Line[] parent){
		
		this.cellID = 0	;
		this.lineID = id;
		cells = new Cell[7];
		for (int i = 0; i < cells.length; i++)
		{
			cells[i] = new Cell(cellID, lineID, parent);
			cellID++;
		}
		this.parent = parent;
	}
	
	/**
	 * Retourne les Cellules comprises dans cette Ligne.
	 * @return un tableau de Cellules.
	 */
	public Cell[] getCells(){
		
		return cells;
	}
	
	/**
	 * Retourne une Cellule spécifique de cette Ligne.
	 * @param index
	 * @return la cellule a l'index spécifié.
	 */
	public Cell getCellAt(int index){
		
		return cells[index];
	}
	
	public Line[] getAllLines()
	{
		return this.parent;
	}
	
	public int getID()
	{
		return this.lineID;
	}
}