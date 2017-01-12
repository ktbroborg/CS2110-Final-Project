package gnomenwald;

public class NotEnoughMoneyException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
public final String fake ;
	
	public NotEnoughMoneyException () {
		super() ;
		this.fake = null ;
	}
}
