package tinyXL;

/**
 * Sous-classe d'Argument pour les références à d'autres cellules.
 * Absolues ou non en fonction de la booleenne isAbsolute.
 * @author hero
 *
 */
public class Position extends Argument {
	
	public int x, y;
	public boolean isAbsolute;
	
	Position(int x, int y, boolean absolute)
	{
		this.x = x;
		this.y = y;
		this.isAbsolute = absolute;
	}

	@Override
	public String print() {
		String s = "";
		if (isAbsolute)
			s = "$("+y+","+x+")";
		else
			s = "!("+y+","+x+")";
		return s;
	}
	
	public boolean isEqualTo(Position p2)
	{
		return p2.x == this.x && p2.y == this.y;
	}

}
