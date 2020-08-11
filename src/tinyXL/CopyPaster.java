package tinyXL;

/**
 * Classe abstraite pour gérer le copier/coller.
 * @author hero
 *
 */
public abstract class CopyPaster {

	public static Cell clipboard;
	
	public void copy(Cell copiedCell){
		CopyPaster.clipboard = copiedCell;
		System.out.println("Copied cell : " + clipboard);
	};
	public void paste(){};

}
