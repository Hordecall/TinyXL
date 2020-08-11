package tinyXL;

/**
 * Sous-classe d'Argument pour les opérateurs (+, -, *, /)
 * @author hero
 *
 */
public class Operator extends Argument{
	
	public char op;
	
	Operator(char operator)
	{
		this.op = operator;
	}
	
	@Override
	public String print()
	{
		return String.valueOf(op);
	}

}
