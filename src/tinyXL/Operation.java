package tinyXL;

/**
 * Sous-classe d'argument, pour les opérations Somme et Moyenne.
 * @author hero
 *
 */
public class Operation extends Argument{
	
	public int sx, sy, ex, ey;
	public boolean isSomme; //interrupteur somme/moyenne
	
	Operation(int startX, int startY, int endX, int endY, boolean isSomme)
	{
		super();
		this.sx = startX;
		this.sy = startY;
		this.ex = endX;
		this.ey = endY;
		this.isSomme = isSomme;
	}

	@Override
	public String print() {
		String s = "";
		if (isSomme)
			s = "Som(("+sy+","+sx+"),("+ey+","+ex+"))";
		else
			s = "Moy(("+sy+","+sx+"),("+ey+","+ex+"))";
		return s;
	}

}
