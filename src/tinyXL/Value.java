package tinyXL;

/**
 * Sous-classe d'Argument pour les valeurs simples.
 * @see Argument
 * @author hero
 *
 */
public class Value extends Argument {

	public int val;
	public String strVal;
	
	Value(int value)
	{
		super();
		this.val = value;
		this.strVal = String.valueOf(value);
	}
	
	Value(String value)
	{
		super();
		try{
			this.val = Integer.valueOf(value);
		} catch (NumberFormatException e)
		{
			this.val = 0;
		}
		this.strVal = value;
	}
	
	@Override
	public String print()
	{
		return strVal;
	}
}
